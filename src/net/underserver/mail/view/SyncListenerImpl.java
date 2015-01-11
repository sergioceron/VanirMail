package net.underserver.mail.view;

import jcurses.widgets.Label;
import net.underserver.mail.dao.Dao;
import net.underserver.mail.dao.HibernateDao;
import net.underserver.mail.dao.LocalMessageDao;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.service.FolderService;
import net.underserver.mail.service.ServiceManager;
import net.underserver.mail.service.SyncListener;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * User: sergio
 * Date: 12/08/12
 * Time: 12:35 AM
 */
public class SyncListenerImpl implements SyncListener {
	private static final Logger logger = Logger.getLogger("main");
	private final ServiceManager serviceManager = ServiceManager.getInstance();

	private UI mainUI;
	private MailList mailList;
	private Dao messageDao;
	private Dao folderDao;
	
	public SyncListenerImpl(UI mainUI) {
		this.mailList = mainUI.getMailList();
		this.mainUI = mainUI;
		this.messageDao = new LocalMessageDao();
		this.folderDao = new HibernateDao();
	}

	@Override
	public void onSyncReleased(LocalFolder localFolder, List<LocalMessage> messagesAdded, List<LocalMessage> messagedRemoved) {

		if( messagesAdded.size() > 0 || messagedRemoved.size() > 0 ){
			for( LocalMessage remove : messagedRemoved ){
				if( remove.getFolders().size() == 1 ){
					messageDao.delete(remove);
				}
				mailList.remove(new MailListItem(remove));
			}

			for( LocalMessage add : messagesAdded ){
				messageDao.persist(add);
				//folderDao.flush();
				mailList.add(new MailListItem(add));
			}
			localFolder.getMessages().addAll(messagesAdded);
			localFolder.getMessages().removeAll(messagedRemoved);

			folderDao.persist(localFolder);
			folderDao.flush();

			mailList.sort();
			mailList.repaint();
			mainUI.getFolderList().updateFolder(localFolder);
		}

	}

	@Override
	public void onSyncStarted(LocalFolder localFolder) {
		mainUI.setStatus("Sincronizando folder: " + localFolder.getURLName());
	}

	@Override
	public void onSyncCompleted(LocalFolder localFolder, BidiMap messagesIds) {
		for( LocalMessage message : localFolder.getMessages() ){
			Object messageNumber = messagesIds.get(message.getUid());
			try{
				message.setMessageNumber((Integer)messageNumber);
				messageDao.persist(message);
			} catch (Exception e) {
				logger.trace("Message has not id: " + message.getUid());
			}
		}
		messageDao.flush();

		FolderService folderService = (FolderService) serviceManager.getService(FolderService.NAME);
		folderService.start(); // por si no ha iniciado o es la primera vez
		// This order is important
		folderService.changeFolder(localFolder); // este es el objetivo principal, cambiar de folder
		mainUI.setStatus("Sincronizacion completada folder: " + localFolder.getURLName());
	}
}
