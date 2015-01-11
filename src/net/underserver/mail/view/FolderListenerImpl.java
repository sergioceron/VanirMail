package net.underserver.mail.view;

import net.underserver.mail.dao.Dao;
import net.underserver.mail.dao.HibernateDao;
import net.underserver.mail.dao.LocalMessageDao;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.service.FolderListener;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.util.List;

/**
 * User: sergio
 * Date: 12/08/12
 * Time: 12:30 AM
 */
public class FolderListenerImpl implements FolderListener {
	private static final Logger logger = Logger.getLogger("main");

	private UI mainUI;
	private Dao dao;
	private Dao messageDao;

	public FolderListenerImpl(UI mainUI) {
		this.mainUI = mainUI;
		this.dao = new HibernateDao();
		this.messageDao = new LocalMessageDao();
	}

	@Override
	public void onMessagesAdded(LocalFolder localFolder, List<LocalMessage> messages) {
		MailList mailList = mainUI.getMailList();
		for( LocalMessage message : messages ){
			mailList.add(new MailListItem(message));
			messageDao.persist(message);
			localFolder.addMessage(message);
		}
		try{
			localFolder.setMessageCount(localFolder.getRemoteFolder().getMessageCount());
			localFolder.setUnreadMessageCount(localFolder.getRemoteFolder().getUnreadMessageCount());
		} catch(MessagingException e) {
			logger.warn("Can't get folder count messages", e);
		}
		dao.persist(localFolder);
		dao.flush();
		mailList.sort();
		mailList.repaint();
		mainUI.getFolderList().updateFolder(localFolder);
	}

	@Override
	public void onMessagesRemoved(LocalFolder localFolder, List<LocalMessage> messages) {
		MailList mailList = mainUI.getMailList();
		for( LocalMessage message : messages ) {
			MailListItem toRemove = new MailListItem();
			if( message.getFolders().size() == 1 ){
				dao.delete(message);
			}
			toRemove.setMessage(message);
			mailList.remove(toRemove);
			localFolder.removeMessage(message);
		}
		try{
			localFolder.setMessageCount(localFolder.getRemoteFolder().getMessageCount());
			localFolder.setUnreadMessageCount(localFolder.getRemoteFolder().getUnreadMessageCount());
		} catch(MessagingException e) {
			logger.warn("Can't get folder count messages", e);
		}
		dao.persist(localFolder);
		dao.flush();
		mailList.sort();
		mailList.repaint();
		mainUI.getFolderList().updateFolder(localFolder);
	}
}
