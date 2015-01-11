package net.underserver.mail.service;

import java.util.List;

/**
 * User: sergio
 * Date: 6/08/12
 * Time: 04:23 PM
 */
public interface FolderListener {
	public void onMessagesAdded(net.underserver.mail.model.LocalFolder localFolder, List<net.underserver.mail.model.LocalMessage> messages);
	public void onMessagesRemoved(net.underserver.mail.model.LocalFolder localFolder, List<net.underserver.mail.model.LocalMessage> messages);
}
