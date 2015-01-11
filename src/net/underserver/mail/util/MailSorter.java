package net.underserver.mail.util;

import java.util.Comparator;

/**
 * User: sergio
 * Date: 23/07/12
 * Time: 04:49 PM
 */
public class MailSorter implements Comparator<net.underserver.mail.view.MailListItem> {

	public static enum SortCriteria {
		ARRIVAL,
		DATE,
		ATTACHMENT,
		SIZE,
		SUBJECT,
		PRIORITY
	}

	public static enum SortOrder {
		REVERSE,
		NORMAL
	}

	private final SortCriteria sortCriteria;
	private final SortOrder sortOrder;

	public MailSorter(SortCriteria sortCriteria, SortOrder sortOrder) {
		this.sortCriteria = sortCriteria;
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(net.underserver.mail.view.MailListItem mi1, net.underserver.mail.view.MailListItem mi2) {
		int comparative = 0;
		net.underserver.mail.model.LocalMessage m1 = mi1.getMessage();
		net.underserver.mail.model.LocalMessage m2 = mi2.getMessage();
		try {
			switch (sortCriteria) {
				case ARRIVAL:
					comparative = m1.getReceivedDate().compareTo(m2.getReceivedDate());
					break;
				case DATE:
					comparative = m1.getSentDate().compareTo(m2.getSentDate());
					break;
				case ATTACHMENT:
					comparative = m1.getFileName().compareTo(m2.getFileName());
					break;
				case SIZE:
					comparative = m1.getSize() - m2.getSize();
					break;
				case SUBJECT:
					comparative = m1.getSubject().compareTo(m2.getSubject());
					break;
				case PRIORITY:
					comparative = 0;
					break;
			}
		} catch (Exception ge) {
		}
		return sortOrder == SortOrder.REVERSE ? -comparative : comparative;
	}
}
