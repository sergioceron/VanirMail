package net.underserver.mail.service;

import net.underserver.mail.controller.FolderController;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.log4j.Logger;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: sergio
 * Date: 8/08/12
 * Time: 12:15 PM
 */
public class SyncService extends Service {
	public static final String NAME = "[Sync Service]";
	private static final Logger logger = Logger.getLogger("main");
	private FolderController folderController = FolderController.getInstance();
	private List<SyncListener> listeners;
	private LocalFolder folder;

	public SyncService(){     // TODO: set folder
		super(NAME);
		this.listeners = new ArrayList<SyncListener>();
	}

	@Override
	public void run(){

		List<LocalMessage> messageList = folder.getMessages();
		List<LocalMessage> removed = new ArrayList<LocalMessage>();

		if(folderController.open(folder, Folder.READ_ONLY)){
			logger.info("Synchronizing folder: " + folder.getURLName());

			for( SyncListener syncListener : listeners ){
				syncListener.onSyncStarted(folder);
			}

			BidiMap messageIds = folderController.fetchMessagesIds(folder);

			for( LocalMessage message : messageList ){
				if( !messageIds.containsKey(message.getUid()) ){
					removed.add(message);
				}
			}

			List<Integer> ids = new ArrayList<Integer>();
			MapIterator it = messageIds.mapIterator();
			while (it.hasNext()) {
				String uid = (String) it.next();
				Integer messageNumber = (Integer) it.getValue();
				LocalMessage dummyMessage = new LocalMessage();
				dummyMessage.setUid(uid);
				if( messageList.indexOf(dummyMessage) < 0 ){
					ids.add(messageNumber);
				}
			}

			List<LocalMessage> added = folderController.fetchMessagesById(folder, ids);
			
			logger.debug("Synchronized folder " + folder.getURLName() + " completed: [ Add: " + added.size() + ", Remove: " + removed.size() + " ]");

			if( added.size() > 0 || removed.size() > 0 ){
				try{
					folder.setMessageCount(folder.getRemoteFolder().getMessageCount());
					folder.setUnreadMessageCount(folder.getRemoteFolder().getUnreadMessageCount());
				} catch(MessagingException e) {
					logger.warn("Can't get folder count messages", e);
				}
				for( SyncListener syncListener : listeners ){
					syncListener.onSyncReleased(folder, added, removed);
				}
			}
			for( SyncListener syncListener : listeners ){
				syncListener.onSyncCompleted(folder, messageIds);
			}
		}
	}
	
	public void synchronize(LocalFolder folder){
		this.folder = folder;
		this.start(true);
	}

	public void addListener(SyncListener syncListener){
		listeners.add(syncListener);
	}
}