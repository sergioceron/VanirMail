package net.underserver.mail.service;

import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.LocalMessage;
import org.apache.commons.collections.BidiMap;

import java.util.List;
import java.util.Map;

/**
 * User: sergio
 * Date: 8/08/12
 * Time: 12:31 PM
 */
public interface SyncListener {
	public void onSyncReleased(LocalFolder localFolder, List<LocalMessage> messagesAdded, List<LocalMessage> messagedRemoved);
	public void onSyncStarted(LocalFolder localFolder);
	public void onSyncCompleted(LocalFolder localFolder, BidiMap messagesIds);
}
