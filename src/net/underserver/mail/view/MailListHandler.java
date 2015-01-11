package net.underserver.mail.view;

import jcurses.event.ItemEvent;
import jcurses.event.ItemListener;
import net.underserver.mail.addons.AddonListener;
import net.underserver.mail.addons.RemoteView.RemoteView;
import net.underserver.mail.controller.FolderController;
import net.underserver.mail.controller.MessageController;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.service.FolderService;
import net.underserver.mail.service.ServiceManager;
import org.apache.log4j.Logger;

/**
 * User: sergio
 * Date: 24/07/12
 * Time: 12:33 PM
 */
public class MailListHandler implements ItemListener {
	private static final Logger logger = Logger.getLogger("main");
	private MessageController messageController = MessageController.getInstance();
	private final UI mainUI;

	public MailListHandler(UI mainUI) {
		this.mainUI = mainUI;
	}

	@Override
	public void stateChanged(ItemEvent itemEvent) {
		MailListItem mailListItem = (MailListItem) itemEvent.getItem();
		MailList mailList = mainUI.getMailList();
		try {
			LocalFolder folder = mailList.getFolder();
			LocalMessage message = mailListItem.getMessage();
			String eml = messageController.getEML(folder, message);
			message.setContent(eml);
			/*mainUI.getMailArea().setMail(message);
			mainUI.getMailList().setVisible(false);
			mainUI.getMailArea().setVisible(true);
			mainUI.refresh();*/

			AddonListener addonListener = new RemoteView();
			addonListener.onOpenMail(message);
		} catch (Exception e) {
			logger.error("Error gathering message content", e);
		}
	}
}
