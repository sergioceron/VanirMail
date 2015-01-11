package net.underserver.mail.model;

/**
 * User: sergio
 * Date: 1/08/12
 * Time: 07:57 PM
 */

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity(name = "message")
public class LocalMessage {

	private String uid;
	private String subject;
	private String content;
	private String fileName;
	private int size;
	private int priority;
	private int messageNumber;
	private boolean seen;
	private boolean attachment;
	private Calendar sentDate;
	private Calendar receivedDate;
	private List<LocalFolder> folders;
	private List<MailAddress> to;
	private List<MailAddress> cc;
	private List<MailAddress> bcc;
	private List<MailAddress> from;

	public LocalMessage(){
		priority = 0;
		seen = false;
		to = new ArrayList<MailAddress>();
		cc = new ArrayList<MailAddress>();
		bcc = new ArrayList<MailAddress>();
		from = new ArrayList<MailAddress>();
		folders = new ArrayList<LocalFolder>();
	}

	@Id
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getMessageNumber() {
		return messageNumber;
	}

	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@ManyToMany(cascade = { CascadeType.DETACH })
	@JoinTable(name = "MESSAGE_CC", joinColumns = @JoinColumn(name = "MESSAGE_UID"),
							 inverseJoinColumns = @JoinColumn(name = "MAIL_CC"))
	public List<MailAddress> getCc() {
		return cc;
	}

	public void setCc(List<MailAddress> cc) {
		this.cc = cc;
	}

	@ManyToMany(cascade = { CascadeType.DETACH })
	@JoinTable(name = "MESSAGE_BCC", joinColumns = @JoinColumn(name = "MESSAGE_UID"),
							  inverseJoinColumns = @JoinColumn(name = "MAIL_BCC"))
	public List<MailAddress> getBcc() {
		return bcc;
	}

	public void setBcc(List<MailAddress> bcc) {
		this.bcc = bcc;
	}

	@ManyToMany(cascade = { CascadeType.DETACH })
	@JoinTable(name = "MESSAGE_TO", joinColumns = @JoinColumn(name = "MESSAGE_UID"),
							 inverseJoinColumns = @JoinColumn(name = "MAIL_TO"))
	public List<MailAddress> getTo() {
		return to;
	}

	public void setTo(List<MailAddress> to) {
		this.to = to;
	}

	@ManyToMany(cascade = { CascadeType.DETACH })
	@JoinTable(name = "MESSAGE_FROM", joinColumns = @JoinColumn(name = "MESSAGE_UID"),
							   inverseJoinColumns = @JoinColumn(name = "MAIL_FROM"))
	public List<MailAddress> getFrom() {
		return from;
	}

	public void setFrom(List<MailAddress> from) {
		this.from = from;
	}

	@Lob
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Calendar receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isAttachment() {
		return attachment;
	}

	public void setAttachment(boolean attachment) {
		this.attachment = attachment;
	}

	@ManyToMany
	public List<LocalFolder> getFolders() {
		return folders;
	}

	public void setFolders(List<LocalFolder> folders) {
		this.folders = folders;
	}

	@Override
	public boolean equals(Object object){
		if( object instanceof LocalMessage ) {
			LocalMessage other = (LocalMessage) object;
			return other.getUid().equals(this.getUid());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getUid())
				.append(getSubject())
				.append(getPriority())
				.toHashCode();
	}
}