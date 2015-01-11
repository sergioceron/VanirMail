package net.underserver.mail.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.mail.Folder;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sergio
 * Date: 1/08/12
 * Time: 08:02 PM
 */
@Entity(name = "folder")
public class LocalFolder {

	private String URLName;
	private String fullName;
	private String name;
	private LocalFolder parent;
	private Mailbox mailbox;
	private int mode;
	private int type;
	private int messageCount;
	private int unreadMessageCount;
	private boolean exists;
	private boolean holdMessages;
	private List<LocalFolder> subFolders;
	private List<LocalMessage> messages;

	private Folder remoteFolder;
	private boolean open;

	public LocalFolder(){
		messages   = new ArrayList<LocalMessage>();
		subFolders = new ArrayList<LocalFolder>();
		open       = false;
	}

	public LocalFolder(Mailbox mailbox, String name) {
		this();
		this.name    = name;
		this.mailbox = mailbox;
	}

	@Id
	public String getURLName() {
		return URLName;
	}

	public void setURLName(String URLName) {
		this.URLName = URLName;
	}

	@ManyToOne
	public LocalFolder getParent() {
		return parent;
	}

	public void setParent(LocalFolder parent) {
		this.parent = parent;
	}

	@ManyToOne
	public Mailbox getMailbox() {
		return mailbox;
	}

	public void setMailbox(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public int getUnreadMessageCount() {
		return unreadMessageCount;
	}

	public void setUnreadMessageCount(int unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
	}

	@Column(name = "exist")
	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public boolean isHoldMessages() {
		return holdMessages;
	}

	public void setHoldMessages(boolean holdMessages) {
		this.holdMessages = holdMessages;
	}

	@OneToMany( mappedBy = "parent", cascade = CascadeType.ALL )
	public List<LocalFolder> getSubfolders() {
		return subFolders;
	}

	public void setSubfolders(List<LocalFolder> subFolders) {
		this.subFolders = subFolders;
	}

	@ManyToMany( cascade = {CascadeType.DETACH},
			      fetch = FetchType.LAZY)
	public List<LocalMessage> getMessages() {
		return messages;
	}

	public void addMessage(LocalMessage message){
		this.messages.add(message);
	}

	public void removeMessage(LocalMessage message){
		this.messages.remove(message);
	}

	public void setMessages(List<LocalMessage> messages) {
		this.messages = messages;
	}

	@Transient
	public Folder getRemoteFolder() {
		return remoteFolder;
	}

	public void setRemoteFolder(Folder remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

	@Transient
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public boolean equals(Object object){
		if( object instanceof LocalFolder){
			LocalFolder other = (LocalFolder) object;
			return other.getURLName().equals(this.getURLName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getURLName())
				.toHashCode();
	}
}
