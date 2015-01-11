package net.underserver.mail.controller;

import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.Mailbox;
import org.apache.log4j.Logger;

import javax.mail.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: sergio
 * Date: 3/08/12
 * Time: 12:37 PM
 */
public class MailboxController {
	private static final Logger logger = Logger.getLogger("main");
	private static MailboxController instance;

	private MailboxController() { }

	public static MailboxController getInstance(){
		instance = instance != null ? instance : new MailboxController();
		return instance;
	}

	public boolean connect(Mailbox mailbox){
		if( !mailbox.isConnected() ){
			logger.info("Connecting to mailbox: " + mailbox.getName());
			try{
				Properties properties = new Properties();

				net.underserver.mail.model.Account outgoing = mailbox.getOutgoing();
				net.underserver.mail.model.Account incoming = mailbox.getIncoming();

				properties.put("mail.smtp.auth",            outgoing.isSsl());
				properties.put("mail.smtp.starttls.enable", outgoing.isTls());
				properties.put("mail.smtp.host",            outgoing.getServer());
				properties.put("mail.smtp.port",            outgoing.getPort());

				final String smtpUser = outgoing.getUsername();
				final String smtpPass = outgoing.getPassword();

				Session session = Session.getInstance(properties,
						new Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(smtpUser, smtpPass);
							}
						});

				Store store = null;
				if (mailbox.getType() == Mailbox.Type.IMAP) {
					store = session.getStore(incoming.isSsl() ? "imaps" : "imap");
				} else if (mailbox.getType() == Mailbox.Type.POP3) {
					store = session.getStore(incoming.isSsl() ? "pop3s" : "pop3");
				}

				String username = incoming.getUsername();
				String password = incoming.getPassword();

				if (incoming.getPort() != 0) {
					assert store != null;
					store.connect(incoming.getServer(), incoming.getPort(), username, password);
				} else {
					assert store != null;
					store.connect(incoming.getServer(), username, password);
				}

				mailbox.setSession(session);
				mailbox.setStore(store);
				mailbox.setConnected(true);
				logger.info("Connected to mailbox: " + mailbox.getName());
			} catch(MessagingException e){
				logger.error("Failed to connect mailbox: " + mailbox.getName(), e);
				return false;
			}
		}
		return true;
	}
	
	public List<LocalFolder> getFolders(Mailbox mailbox){
		return scanFolders(mailbox, null, null);
	}
	
	private List<LocalFolder> scanFolders(Mailbox mailbox, LocalFolder parent, Folder root){
		List<LocalFolder> folders = new ArrayList<LocalFolder>();
		try{
			root = root != null ? root : mailbox.getStore().getDefaultFolder();

			logger.info("Scanning folder: " + root.getFullName());

			for( Folder folder : root.list() ){
				boolean holdMessages = ((folder.getType() & Folder.HOLDS_MESSAGES) != 0);
				boolean holdFolders = ((folder.getType() & Folder.HOLDS_FOLDERS) != 0);
				LocalFolder localFolder = new LocalFolder();
				localFolder.setURLName(folder.getURLName().toString());
				localFolder.setParent(parent);
				localFolder.setFullName(folder.getFullName());
				localFolder.setHoldMessages(holdMessages);
				localFolder.setMailbox(mailbox);
				localFolder.setName(folder.getName());
				localFolder.setType(folder.getType());
				localFolder.setRemoteFolder(folder);
				if( holdFolders ) {
					List<LocalFolder> subfolders = scanFolders(mailbox, localFolder, folder);
					localFolder.setSubfolders(subfolders);
				}
				folders.add(localFolder);
			}
		} catch(MessagingException e) {
			assert root != null;
			logger.error("Failed to scan folder: " + root.getFullName(), e);
		}
		return folders;
	}

	public boolean disconnect(Mailbox mailbox){
		if( mailbox.isConnected() ){
			try{
				logger.info("Closing connection from mailbox: " + mailbox.getName());
				Store store = mailbox.getStore();
				store.close();
				mailbox.setSession(null);
				mailbox.setStore(null);
				mailbox.setConnected(false);
				logger.info("Closed connection from mailbox: " + mailbox.getName());
			} catch(MessagingException e){
				logger.warn("Can't close connection from mailbox: " + mailbox.getName());
				return false;
			}
		}
		return true;
	}

}
