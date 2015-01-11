package net.underserver.mail.service;

/**
 * User: sergio
 * Date: 5/08/12
 * Time: 06:48 PM
 */
public interface MailboxListener {
	public void onFolderCountChange(net.underserver.mail.model.LocalFolder localFolder);
}
