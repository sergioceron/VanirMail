package net.underserver.mail.view;

import jcurses.system.CharColor;
import jcurses.widgets.TextArea;
import net.underserver.mail.model.LocalMessage;

/**
 * User: sergio
 * Date: 24/07/12
 * Time: 12:43 PM
 */
public class MailArea extends TextArea {
	private LocalMessage mail;

	public MailArea(int width, int height) {
		super(width, height);
		super.setColors(new CharColor(CharColor.BLACK, CharColor.WHITE));
		super.setBorderColors(new CharColor(CharColor.BLACK, CharColor.WHITE));
	}

	public LocalMessage getMail() {
		return mail;
	}

	public void setMail(LocalMessage mail) {
		this.mail = mail;
		this.setText(mail.getContent());
	}
}
