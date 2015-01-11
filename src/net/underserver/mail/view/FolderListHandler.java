package net.underserver.mail.view;

import jcurses.event.ItemEvent;
import jcurses.event.ItemListener;
import net.underserver.mail.model.LocalFolder;

/**
 * User: sergio
 * Date: 24/07/12
 * Time: 12:28 PM
 */
public class FolderListHandler implements ItemListener{
	private final UI mainUI;

	public FolderListHandler(UI mainUI){
		this.mainUI = mainUI;
	}

	@Override
	public void stateChanged(ItemEvent itemEvent) {
		FolderItem folderItem = (FolderItem) itemEvent.getItem();
		LocalFolder folder = folderItem.getFolder();

		mainUI.getMailList().setFolder(folder);

		mainUI.getMailList().sort();
		mainUI.getMailList().repaint();

	}
}
