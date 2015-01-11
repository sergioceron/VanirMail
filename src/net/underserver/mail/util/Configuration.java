package net.underserver.mail.util;

import jcurses.system.CharColor;

import java.util.HashMap;
import java.util.Map;

/**
 * User: sergio
 * Date: 22/07/12
 * Time: 09:51 PM
 */
public class Configuration {

	private static Configuration instance;

	private boolean keepAlive;
	private String folderFormat;
	private String listFormat;
	private MailSorter.SortCriteria mailSort;
	private MailSorter.SortOrder mailSortOrder;
	private Map<String, CharColor> colors;

	public Configuration() {
		this.colors = new HashMap<String, CharColor>();
		this.keepAlive = true;
		this.folderFormat = "(%d{messageCount}/%d{unreadMessageCount})";
		this.listFormat = "%-6s{messageNumber} %tD{receivedDate} %.50s{subject}";
		this.mailSort = MailSorter.SortCriteria.ARRIVAL;
		this.mailSortOrder = MailSorter.SortOrder.REVERSE;
		this.colors.put("message-normal", new CharColor(CharColor.BLACK, CharColor.WHITE));
		this.colors.put("message-unread", new CharColor(CharColor.CYAN, CharColor.BLACK));
	}

	public static Configuration getInstance(){
		instance = instance != null ? instance : new Configuration();
		return instance;
	}

	public String getFolderFormat() {
		return folderFormat;
	}

	public void setFolderFormat(String folderFormat) {
		this.folderFormat = folderFormat;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public String getListFormat() {
		return listFormat;
	}

	public void setListFormat(String listFormat) {
		this.listFormat = listFormat;
	}

	public MailSorter.SortCriteria getMailSort() {
		return mailSort;
	}

	public void setMailSort(MailSorter.SortCriteria mailSort) {
		this.mailSort = mailSort;
	}

	public MailSorter.SortOrder getMailSortOrder() {
		return mailSortOrder;
	}

	public void setMailSortOrder(MailSorter.SortOrder mailSortOrder) {
		this.mailSortOrder = mailSortOrder;
	}

	public Map<String, CharColor> getColors() {
		return colors;
	}

	public void setColors(Map<String, CharColor> colors) {
		this.colors = colors;
	}
}
