package net.underserver.mail.addons;

import net.underserver.mail.model.LocalMessage;

/**
 * User: sergio
 * Date: 17/08/12
 * Time: 03:36 PM
 */
public interface AddonListener {
	public void onOpenMail(LocalMessage mail);
}
