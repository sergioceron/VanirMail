package net.underserver.mail.view;

import jcurses.widgets.ListItem;
import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.util.Configuration;
import net.underserver.mail.util.ObjectFormat;

/**
 * User: sergio
 * Date: 24/07/12
 * Time: 02:08 PM
 */
public class MailListItem extends ListItem {
	private final Configuration config;
	private LocalMessage message;

	public MailListItem() { // for reflection
		super();
		this.config = Configuration.getInstance();
	}

	public MailListItem(LocalMessage message) {
		this();
		this.message = message;
		this.setLabel(ObjectFormat.format(config.getListFormat(), message));

		if( !message.isSeen() ){
			setColor(config.getColors().get("message-unread"));
		} else {
			setColor(config.getColors().get("message-normal"));
		}
	}

	public LocalMessage getMessage() {
		return message;
	}

	public void setMessage(LocalMessage message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object other){
		if( other instanceof MailListItem){
			LocalMessage _this = this.getMessage();
			LocalMessage _other = ((MailListItem) other).getMessage();
			return _other.getMessageNumber() == _this.getMessageNumber();
		}
		return false;
	}
}
