package net.underserver.mail.view;

import jcurses.system.CharColor;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.Mailbox;
import net.underserver.mail.util.Configuration;

import java.util.List;

import static jcurses.system.CharColor.*;

/**
 * User: sergio
 * Date: 23/07/12
 * Time: 04:04 PM
 */
public class FolderList extends jcurses.widgets.List<FolderItem> {
	private final Configuration config = Configuration.getInstance();

	private List<Mailbox> mailboxes;

	public FolderList(int visible, List<Mailbox> mailboxes) {
		super(visible);
		this.mailboxes = mailboxes;
		this.setColors(new CharColor(NORMAL, NORMAL));
		//this.set

		for( Mailbox mailbox : mailboxes ){
			List<LocalFolder> mailboxFolders = mailbox.getFolders();
			//this.add(new FolderItem(mailbox.getName(), true));
			for( LocalFolder folder : mailboxFolders ){
				addFolderItem(folder, 1);
			}
			//this.add(new FolderItem("", true));
		}
	}

	public void addFolderItem(LocalFolder root, int depth){
		FolderItem folderItem = new FolderItem(root, config.getFolderFormat(), depth);
		this.add(folderItem);
		if( root.getSubfolders().size() > 0 ){
			depth += 3;
			for( LocalFolder child : root.getSubfolders() ){
				addFolderItem(child, depth);
			}
		}
	}
	
	public void updateFolder(LocalFolder folder){
		int index = -1;
		for(FolderItem folderItem : getItems()){
			LocalFolder current = folderItem.getFolder();
			index++;
			if( current.equals(folder) ){
				folderItem.setFolder( current );
				this.repaintItem(index);
				break;
			}
		}
	}

	public void addMailbox(Mailbox mailbox) {
		this.mailboxes.add(mailbox);
	}

}
