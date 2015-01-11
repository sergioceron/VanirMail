package net.underserver.mail.view;

import jcurses.widgets.ListItem;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.util.ObjectFormat;

/**
 * User: sergio
 * Date: 24/07/12
 * Time: 02:03 PM
 */
public class FolderItem extends ListItem {
	private LocalFolder folder;
	private String format;
	private int depth;

	public FolderItem(LocalFolder folder, String format, int depth) {
		super();
		this.setFormat(format);
		this.setDepth(depth);
		this.setFolder(folder);
	}

	public FolderItem(String label, boolean selectable) {
		super.setLabel(label);
		super.setSelectable(selectable);
	}

	public LocalFolder getFolder() {
		return folder;
	}

	public void setFolder(LocalFolder folder) {
		boolean holdMessages = folder.isHoldMessages();
		boolean holdFolders = folder.getSubfolders().size() > 0;
		String name = holdFolders ? "+" + folder.getName() : folder.getName();
		String label = "";
		if( holdMessages ){
			label = ObjectFormat.format(format, folder);
			label = String.format("%-" + (26 - depth) + ".20s %10s", name, label);
		} else {
			label = folder.getName();
			super.setSelectable(false);
		}

		int offset = depth + label.length();
		super.setLabel(String.format("%" + offset + "s", label));
		this.folder = folder;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public boolean equals(Object other){
		if( other instanceof FolderItem){
			FolderItem otherFolder = (FolderItem) other;
			return otherFolder.getFolder().getURLName().equals(this.getFolder().getURLName());
		}
		return false;
	}
}
