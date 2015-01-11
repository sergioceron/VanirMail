package net.underserver.mail.service;

import net.underserver.mail.controller.FolderController;
import net.underserver.mail.controller.MailboxController;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.Mailbox;
import org.apache.log4j.Logger;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.mail.Folder.READ_ONLY;

/**
 * User: sergio
 * Date: 26/07/12
 * Time: 09:02 PM
 */
public class MailboxService extends Service {
	public static final String NAME = "[Message Count Monitor]";
	private static final Logger logger = Logger.getLogger("main");

	private MailboxController mailboxController = MailboxController.getInstance();
	private FolderController folderController = FolderController.getInstance();

	private Map<LocalFolder, Integer> folders;
	private Map<LocalFolder, Integer> unread;
	private List<MailboxListener> listeners;

	public MailboxService() {
		super(NAME);
		this.folders = new HashMap<LocalFolder, Integer>();
		this.unread = new HashMap<LocalFolder, Integer>();
		this.listeners = new ArrayList<MailboxListener>();
	}

	@Override
	public void run() {
		while( isAlive() ){
			if( !isPaused() ){
				for( LocalFolder folder : folders.keySet() ){
					try {
						countFolder(folder);
					}catch (MessagingException e) {
						logger.warn("Can't count messages from folder: " + folder.getURLName() );
					}
				}
				try {
					Thread.sleep(30 * 1000);
				} catch (InterruptedException e) {
					logger.warn("Can't sleep thread: " + Thread.activeCount());
				}
			}
		}
	}
	
	private void countFolder(LocalFolder localFolder) throws MessagingException {
		Mailbox mailbox = localFolder.getMailbox();
		if(mailboxController.connect(mailbox)){
			if( !localFolder.isOpen() ){
				if( folderController.open(localFolder, READ_ONLY) ){
					Folder remoteFolder = localFolder.getRemoteFolder();

					logger.debug("Counting messages folder: " + localFolder.getURLName());

					int newCount = remoteFolder.getMessageCount();
					int oldCount = folders.get(localFolder);

					int newUnread = remoteFolder.getUnreadMessageCount();
					int oldUnread = unread.get(localFolder);

					if( oldCount != newCount || oldUnread != newUnread ) {
						logger.info("Folder count change [ Folder: " + localFolder.getURLName() + ", count: " + (newCount - oldCount) + ", unread: " + (newUnread - oldUnread) + " ]");
						changeFolderCountEvent(localFolder, remoteFolder);
						folders.put(localFolder, newCount);
						unread.put(localFolder, newUnread);
					}
					folderController.close(localFolder);
				}

			}
		}
	}

	private void changeFolderCountEvent(LocalFolder localFolder, Folder remoteFolder) {
		try {
			localFolder.setMessageCount(remoteFolder.getMessageCount());
			localFolder.setUnreadMessageCount(remoteFolder.getUnreadMessageCount());
			notifyListeners(localFolder);
		} catch (MessagingException e) {
			logger.warn("Can't get message count for folder: " + localFolder.getURLName());
		}
	}

	private void notifyListeners(LocalFolder localFolder){
		for( MailboxListener mailboxListener : listeners ){
			mailboxListener.onFolderCountChange(localFolder);
		}
	}

	public void addFolder(LocalFolder localFolder) {
		if( localFolder.isHoldMessages() ){
			folders.put(localFolder, localFolder.getMessageCount());
			unread.put(localFolder, localFolder.getUnreadMessageCount());
		}
		for( LocalFolder subfolder : localFolder.getSubfolders() ) {
			addFolder(subfolder);
		}
	}

	public void addListener(MailboxListener listener){
		listeners.add(listener);
	}
}
