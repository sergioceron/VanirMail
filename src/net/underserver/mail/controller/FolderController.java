package net.underserver.mail.controller;

import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.model.MailAddress;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.event.ConnectionAdapter;
import javax.mail.event.ConnectionEvent;
import javax.mail.internet.InternetAddress;
import java.util.*;

import static javax.mail.Folder.READ_ONLY;

/**
 * User: sergio
 * Date: 3/08/12
 * Time: 12:54 PM
 */
public class FolderController {
	private static final Logger logger = Logger.getLogger("main");
	private static FolderController instance;
	private MailboxController mailboxController = MailboxController.getInstance();

	private FolderController(){ }

	public static FolderController getInstance() {
		instance = instance != null ? instance : new FolderController();
		return instance;
	}
	
	public BidiMap fetchMessagesIds(LocalFolder localFolder){
		BidiMap messageIds = new DualHashBidiMap();

		logger.debug("Fetching message's id's from folder: " + localFolder.getURLName());
		try {
			if( open(localFolder, READ_ONLY) ){
				Folder remoteFolder = localFolder.getRemoteFolder();
				Message[] messages = remoteFolder.getMessages();
				FetchProfile fp = new FetchProfile();
				fp.add("Message-Id");
				remoteFolder.fetch(messages, fp);
				for( Message message : messages ){
					if( !message.isExpunged() ){
						String[] uidHeader = message.getHeader("Message-Id");
						if( uidHeader == null ) continue;
						String uid = uidHeader[0];
						messageIds.put(uid, message.getMessageNumber());
					}
				}
			}
		} catch (MessagingException e) {
			logger.warn("Can't fetch message's id's from folder: " + localFolder.getURLName(), e);
		}

		return messageIds;
	}

	public List<LocalMessage> fetchMessagesById(LocalFolder localFolder, List<Integer> ids){

		logger.debug("Fetching messages: [ Folder: " + localFolder.getURLName() + ", Count: " + ids.size() + " ]");
		try {
			Folder remoteFolder = getRemoteFolder(localFolder);

			List<Message> messages = new ArrayList<Message>();
			for( Integer id : ids ){
				messages.add(remoteFolder.getMessage(id));
			}
			FetchProfile fp = new FetchProfile();
			fp.add("Message-Id");
			fp.add("From");
			fp.add("To");
			fp.add("Cc");
			fp.add("Bcc");
			fp.add("X-Priority");
			fp.add("Subject");
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);

			remoteFolder.fetch(messages.toArray(new Message[0]), fp);
			List<LocalMessage> localMessages = new ArrayList<LocalMessage>();
			for( Message message : messages ){
				LocalMessage localMessage = getMessage(message);
				localMessages.add(localMessage);
			}
			logger.debug("Fetched messages: [ Folder: " + localFolder.getURLName() + ", Count: " + ids.size() + " ]");
			return localMessages;
		} catch (MessagingException e) {
			logger.warn("Can't fetch messages: [ Folder: " + localFolder.getURLName() + ", Count: " + ids.size() + " ]", e);
		}
		return null;
	}
	
	public LocalMessage getMessage(Message remoteMessage){
		LocalMessage localMessage = new LocalMessage();
		// primary key unique identifier
		try {
			String[] res = remoteMessage.getHeader("Message-Id");
			String uid = res != null ? res[0] : String.valueOf(new Random().nextLong());
			localMessage.setUid(uid);
		} catch (Exception e) {
			logger.error("");
			return null;
		}

		// just for sort
		localMessage.setMessageNumber(remoteMessage.getMessageNumber());

		try {
			localMessage.setSubject(remoteMessage.getSubject());
		} catch (MessagingException e){
			localMessage.setSubject("(No Subject)");
			logger.trace("Message " + localMessage + " does not contains subject");
		}

		try {
			localMessage.setFrom(parseAddress(remoteMessage.getFrom()));
		} catch (MessagingException e){
			logger.trace("Message " + localMessage + " does not contains [from] recipients");
		}

		try {
			localMessage.setTo(parseAddress(remoteMessage.getRecipients(Message.RecipientType.TO)));
		} catch (MessagingException e){
			logger.trace("Message " + localMessage + " does not contains [to] recipients");
		}

		try {
			Address[] cc = remoteMessage.getRecipients(Message.RecipientType.CC);
			if( cc != null ){
				localMessage.setCc(parseAddress(cc));
			}
		} catch (MessagingException e){
			logger.trace("Message " + localMessage + " does not contains [cc] recipients");
		}

		try {
			Address[] bcc = remoteMessage.getRecipients(Message.RecipientType.BCC);
			if( bcc != null ){
				localMessage.setBcc(parseAddress(bcc));
			}
		} catch (MessagingException e){
			logger.trace("Message " + localMessage + " does not contains [bcc] recipients");
		}

		try{
			localMessage.setPriority(Integer.parseInt(remoteMessage.getHeader("X-Priority")[0]));
		} catch (Exception e){
			localMessage.setPriority(3); // normal priority
		}

		try{
			localMessage.setSeen(remoteMessage.isSet(Flags.Flag.SEEN));
		} catch (MessagingException e){
			localMessage.setSeen(true); // force user to mark as read
		}

		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(remoteMessage.getSentDate());
			localMessage.setSentDate(cal);
		} catch (Exception e){
			logger.trace("Message " + localMessage + " does not contains [send date]");
		}

		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(remoteMessage.getReceivedDate());
			localMessage.setReceivedDate(cal);
		} catch (Exception e){
			logger.trace("Message " + localMessage + " does not contains [send date]");
		}
		return localMessage;
	}
	
	public Message getRemoteMessage(LocalFolder localFolder, LocalMessage localMessage) throws MessagingException {
		Message remoteMessage = null;
		if( open(localFolder, READ_ONLY) ){
			Folder remoteFolder = localFolder.getRemoteFolder();
			remoteMessage = remoteFolder.getMessage(localMessage.getMessageNumber());
		}
		return remoteMessage;
	}
	
	private List<MailAddress> parseAddress(Address[] addresses){
		List<MailAddress> mailAddresses = new ArrayList<MailAddress>();
		if( addresses != null ) {
			for( Address address : addresses ){
				if( address instanceof InternetAddress){
					mailAddresses.add(new MailAddress((InternetAddress)address));
				}
			}
		}
		return mailAddresses;
	}

	public synchronized Folder getRemoteFolder(LocalFolder localFolder){
		Folder remoteFolder = localFolder.getRemoteFolder();
		if( remoteFolder == null ) {
			if( mailboxController.connect(localFolder.getMailbox())){
				try {
					remoteFolder = localFolder.getMailbox().
							getStore().getFolder(new URLName(localFolder.getURLName()));
					localFolder.setRemoteFolder(remoteFolder);
				} catch (MessagingException e) {
					logger.warn("Can't get remote folder: " + localFolder.getURLName(), e);
					return null;
				}
			}
		}
		return remoteFolder;
	}

	public synchronized boolean open(final LocalFolder localFolder, int mode){
		try {
			if( !localFolder.isOpen() ){ // TODO: verificar que este abierto en el modo indicado
				final Folder remoteFolder = getRemoteFolder(localFolder);
				remoteFolder.open(mode);
				localFolder.setOpen(true);
				remoteFolder.addConnectionListener(new ConnectionAdapter() {
					@Override
					public void closed(ConnectionEvent e) {
						localFolder.setOpen(false);
						remoteFolder.removeConnectionListener(this);
						logger.trace("Timeout closed folder: " + localFolder.getURLName());
					}
				});
				logger.trace("Open folder: " + localFolder.getURLName());
			}
			return true;
		} catch (MessagingException e) {
			logger.error("Failed open folder: " + localFolder.getURLName(), e);
		}
		return false;
	}

	public synchronized boolean close(LocalFolder localFolder){
		try {
			if( localFolder.isOpen() ){
				Folder remoteFolder = localFolder.getRemoteFolder();
				remoteFolder.close(false);
				localFolder.setOpen(false);
				logger.trace("Closed folder: " + localFolder.getURLName());
			}
			return true;
		} catch (MessagingException e) {
			logger.warn("Can't close folder: " + localFolder.getURLName(), e);
		}
		return false;
	}
}
