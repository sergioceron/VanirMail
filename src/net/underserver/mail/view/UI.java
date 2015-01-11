package net.underserver.mail.view; /**
 * User: sergio
 * Date: 22/07/12
 * Time: 12:22 PM
 */

import jcurses.event.WindowEvent;
import jcurses.event.WindowListener;
import jcurses.system.CharColor;
import jcurses.system.Toolkit;
import jcurses.util.Protocol;
import jcurses.widgets.DefaultLayoutManager;
import jcurses.widgets.Label;
import jcurses.widgets.WidgetsConstants;
import jcurses.widgets.Window;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.Mailbox;
import net.underserver.mail.service.FolderService;
import net.underserver.mail.service.SyncService;
import net.underserver.mail.service.MailboxService;
import net.underserver.mail.service.ServiceManager;

import java.util.List;

public class UI extends Window implements WindowListener, WidgetsConstants {
	private List<Mailbox> mailboxes;
	
	private MailList mailList;
	private FolderList folderList;
	private MailArea mailArea;
	private Label statusBar;

	public UI( List<Mailbox> mailboxes ) {
		super(Toolkit.getScreenWidth(), Toolkit.getScreenHeight(), false, "JCurses Test");
		this.mailboxes = mailboxes;
	}

	public void init() {
		Toolkit.clearScreen(new CharColor(CharColor.NORMAL, CharColor.NORMAL));

		Protocol.activateChannel(Protocol.DEBUG);
		getRootPanel().setPanelColors(new CharColor(CharColor.BLACK, CharColor.WHITE));

		DefaultLayoutManager layoutManager = new DefaultLayoutManager();
		layoutManager.bindToContainer(this.getRootPanel());

		mailArea = new MailArea(Toolkit.getScreenWidth() - 2 - 40, 60 - 3);    // TODO: revisar width y height repetidos
		mailArea.setVisible(false);

		folderList = new FolderList(60 - 2, mailboxes);

		LocalFolder first = folderList.getItem(0).getFolder();

		mailList = new MailList(60 - 2);
		
		statusBar = new Label("VanirMail v-1", new CharColor(CharColor.YELLOW, CharColor.BLACK));
		statusBar.setWidth(Toolkit.getScreenWidth());

		layoutManager.addWidget(mailList, 40, 0, Toolkit.getScreenWidth() - 40, Toolkit.getScreenHeight() - 2,
				ALIGNMENT_CENTER,
				ALIGNMENT_CENTER);

		layoutManager.addWidget(folderList, 0, 0, 40 , Toolkit.getScreenHeight() - 2,
				ALIGNMENT_CENTER,
				ALIGNMENT_CENTER);

		layoutManager.addWidget(mailArea, 40, 0, Toolkit.getScreenWidth() - 40, Toolkit.getScreenHeight() - 2,
				ALIGNMENT_CENTER,
				ALIGNMENT_CENTER);

		layoutManager.addWidget(statusBar, 0, Toolkit.getScreenHeight() - 1, Toolkit.getScreenWidth(), Toolkit.getScreenHeight(),
				ALIGNMENT_LEFT,
				ALIGNMENT_LEFT);

		this.addListener(this);
		this.setShadow(false);

		MailListHandler mailListHandler = new MailListHandler(this);
		FolderListHandler folderListHandler = new FolderListHandler(this);

		mailList.addListener(mailListHandler);
		folderList.addListener(folderListHandler);
		/*mailArea.addListener(new MailListener(this));

		mailbox1.addMailboxListener(mailboxListenerImpl);
		mailbox2.addMailboxListener(mailboxListenerImpl);
		*/

		MailboxListenerImpl mailboxListener = new MailboxListenerImpl(this);
		FolderListenerImpl folderListener = new FolderListenerImpl(this);
		SyncListenerImpl syncListener = new SyncListenerImpl(this);

		MailboxService mailboxService = new MailboxService();
		mailboxService.addListener(mailboxListener);
		for( FolderItem folderItem : folderList.getItems()){
			LocalFolder localFolder = folderItem.getFolder();
			if( localFolder.isHoldMessages() ){
				mailboxService.addFolder(localFolder);
			}
		}
		FolderService folderService = new FolderService();
		folderService.setFolder(first); // First folder (of course, selected)
		folderService.addListener(folderListener);

		SyncService syncService = new SyncService();
		syncService.addListener(syncListener);

		ServiceManager serviceManager = ServiceManager.getInstance();
		serviceManager.addService(folderService);
		serviceManager.addService(mailboxService);
		serviceManager.addService(syncService);

		mailboxService.start();

		mailList.setFolder(first);

		this.show();
	}

	public void windowChanged(WindowEvent event) {
		if (event.getType() == WindowEvent.CLOSING) {
			event.getSourceWindow().close();
		}
	}

	public void refresh(){
		this.repaint();
	}

	public MailList getMailList() {
		return mailList;
	}

	public FolderList getFolderList() {
		return folderList;
	}

	public MailArea getMailArea() {
		return mailArea;
	}
	
	public void setStatus(final String text){
		statusBar.setLabel(text); // TODO: add queue support for messages
	}
}