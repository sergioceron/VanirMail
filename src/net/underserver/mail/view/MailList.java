package net.underserver.mail.view;

import jcurses.system.CharColor;
import jcurses.widgets.List;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.service.ServiceManager;
import net.underserver.mail.service.SyncService;
import net.underserver.mail.util.Configuration;
import net.underserver.mail.util.MailSorter;

import java.util.Collections;

import static jcurses.system.CharColor.NORMAL;

/**
 * User: sergio
 * Date: 22/07/12
 * Time: 12:58 PM
 */
public class MailList extends List<MailListItem> {
	private final Configuration config = Configuration.getInstance();
	private final ServiceManager serviceManager = ServiceManager.getInstance();
	private LocalFolder localFolder;

	
	public MailList(int visible) {
		super(visible);
		this.setColors(new CharColor(NORMAL, NORMAL));
	}

	public void sort(){
		Collections.sort(getItems(), new MailSorter(config.getMailSort(), config.getMailSortOrder()));
	}

	public void repaint(){
		super.repaint();
	}

	public void setFolder(LocalFolder folder) {
		this.localFolder = folder;
		clear();
		for(LocalMessage message : folder.getMessages()){
			this.add(new MailListItem(message));
		}
		sort();
		SyncService sync = (SyncService) serviceManager.getService(SyncService.NAME);
		sync.synchronize(localFolder);
	}

	public LocalFolder getFolder() {
		return localFolder;
	}
}
