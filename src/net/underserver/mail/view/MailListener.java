package net.underserver.mail.view;

import jcurses.event.ValueChangedEvent;
import jcurses.event.ValueChangedListener;


/**
 * User: sergio
 * Date: 24/07/12
 * Time: 10:58 PM
 */
public class MailListener implements ValueChangedListener{
	private final UI mainUI;

	public MailListener(UI mainUI) {
		this.mainUI = mainUI;
	}

	@Override
	public void valueChanged(ValueChangedEvent valueChangedEvent) {
		// TODO: implementar que hacer cuando el texo cambia, aunque de hecho tendria que ser vim
	}
}
