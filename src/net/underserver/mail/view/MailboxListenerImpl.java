package net.underserver.mail.view;

import net.underserver.mail.dao.Dao;
import net.underserver.mail.dao.HibernateDao;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.service.FolderListener;
import net.underserver.mail.service.MailboxListener;
import net.underserver.mail.service.SyncListener;

import java.util.List;

/**
 * User: sergio
 * Date: 28/07/12
 * Time: 03:30 PM
 */
public class MailboxListenerImpl implements MailboxListener{
	private UI mainUI;
	private Dao dao;

	public MailboxListenerImpl(UI mainUI) {
		this.mainUI = mainUI;
		this.dao = new HibernateDao();
	}

	@Override
	public void onFolderCountChange(LocalFolder localFolder) {
		mainUI.setStatus("Seleccionado folder: " + localFolder.getURLName());
		mainUI.getFolderList().updateFolder(localFolder);
		dao.persist(localFolder);
		dao.flush();
	}

}
