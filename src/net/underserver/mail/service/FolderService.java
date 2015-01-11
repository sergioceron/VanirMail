package net.underserver.mail.service;

import com.sun.mail.imap.IMAPFolder;
import net.underserver.mail.controller.FolderController;
import net.underserver.mail.dao.Dao;
import net.underserver.mail.dao.HibernateDao;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import org.apache.commons.collections.BidiMap;
import org.apache.log4j.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.mail.Folder.READ_ONLY;

/**
 * User: sergio
 * Date: 25/07/12
 * Time: 03:04 PM
 */
public class FolderService extends Service implements MessageCountListener {
	public static final String NAME = "[Folder Monitor]";
	private static final Logger logger = Logger.getLogger("main");

	private FolderController folderController = FolderController.getInstance();
	
	private Dao dao;

	private LocalFolder localFolder;
	private Folder remoteFolder;
	private List<FolderListener> listeners;
	private BidiMap messageIds;

	public FolderService() {
		super(NAME);
		listeners = new ArrayList<FolderListener>();
		dao = new HibernateDao();
	}

	@Override
	public void run(){
		// TODO: aqui se impelmenta el getnewmessages si es soportado por el servidor imapS
		while( isAlive() ){
			if( !isPaused() ){
				try {
					idle();
				} catch (Exception e) {
					logger.error("Error on IDLE folder: " + localFolder.getURLName(), e);
				}
			}
		}
	}

	private void abortIdle() throws MessagingException {
		logger.debug("Aborting IDLE folder: " + localFolder.getURLName());
		synchronized (remoteFolder){
			folderController.open(localFolder, READ_ONLY);
		}
	}

	private void idle() throws MessagingException {
		if( localFolder.isOpen() ){
			logger.debug("IDLE folder: " + localFolder.getURLName());
			remoteFolder = localFolder.getRemoteFolder(); // TODO: usar algo como el fopen arriba
			if( remoteFolder instanceof IMAPFolder){
				((IMAPFolder) remoteFolder).idle(true);
				logger.debug("End IDLE folder: " + localFolder.getURLName());
			}
		}

	}

	public boolean changeFolder(LocalFolder newFolder) {
		if( isAlive() ){
			logger.info("Changing idle folder: " + localFolder.getURLName() + " -> " + newFolder.getURLName());
			pause();
			remoteFolder.removeMessageCountListener(this);
			try {
				abortIdle();
				folderController.close(localFolder);
				setFolder(newFolder);
				if(!folderController.open(localFolder, READ_ONLY)){
					throw new MessagingException("Can't open folder");
				}
				messageIds = folderController.fetchMessagesIds(localFolder);
			} catch (MessagingException e) {
				logger.warn("Can't change idle folder: " + localFolder.getURLName() + " -> " + newFolder.getURLName());
				return false;
			}
			remoteFolder.addMessageCountListener(this);
			resume();
			return true;
		}
		return false;
	}

	@Override
	public void messagesAdded(MessageCountEvent messageCountEvent) {
		Message[] remoteMessages = messageCountEvent.getMessages();
		logger.info("New messages added [ Folder: " + localFolder.getURLName() + ", Count: " + remoteMessages.length + " ]");
		List<LocalMessage> messages = new ArrayList<LocalMessage>();

		for( Message message : remoteMessages ){
			LocalMessage localMessage = folderController.getMessage(message);
			// TODO: persist message with local folder
			messages.add(localMessage);
		}

		messageIds = folderController.fetchMessagesIds(localFolder);
		notifyListeners(1, messages);

	}

	@Override
	public void messagesRemoved(MessageCountEvent messageCountEvent) {
		Message[] remoteMessages = messageCountEvent.getMessages();
		logger.info("New messages removed [ Folder: " + localFolder.getURLName() + ", Count: " + remoteMessages.length + " ]");

		List<LocalMessage> messages = new ArrayList<LocalMessage>();
		for( Message message : remoteMessages ){
			String messageId = (String) messageIds.getKey(message.getMessageNumber());
			LocalMessage localMessage = dao.load(LocalMessage.class, messageId); // TODO: try with get
			messages.add(localMessage);
			logger.debug("To remove message: " + localMessage.getSubject());
		}

		messageIds = folderController.fetchMessagesIds(localFolder);
		notifyListeners(2, messages);
	}

	private void notifyListeners(int type, List<LocalMessage> messages) {
		for( FolderListener listener : listeners){
			if( type == 1 )
				listener.onMessagesAdded(localFolder, messages);
			else if( type == 2 )
				listener.onMessagesRemoved(localFolder, messages);
		}
	}

	public void setFolder(LocalFolder localFolder) {
		this.localFolder = localFolder;
		this.remoteFolder = folderController.getRemoteFolder(localFolder);
	}

	public void addListener(FolderListener listener){
		listeners.add(listener);
	}
}