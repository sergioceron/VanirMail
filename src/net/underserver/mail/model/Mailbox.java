package net.underserver.mail.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.mail.Session;
import javax.mail.Store;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sergio
 * Date: 2/08/12
 * Time: 01:51 PM
 */
@Entity
public class Mailbox {

	public enum Type{
		POP3,
		IMAP
	}

	private Long id;
	private String name;
	private Type type;
	private Account incoming;
	private Account outgoing;
	private Session session;
	private Store store;
	private List<LocalFolder> folders;
	private boolean connected;

	public Mailbox(){
		connected = false;
		folders = new ArrayList<LocalFolder>();
	}

	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Account getIncoming() {
		return incoming;
	}

	public void setIncoming(Account incoming) {
		this.incoming = incoming;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Account getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(Account outgoing) {
		this.outgoing = outgoing;
	}

	@Transient
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Transient
	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	@OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
	public List<LocalFolder> getFolders() {
		return folders;
	}

	public void setFolders(List<LocalFolder> folders) {
		this.folders = folders;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Transient
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public boolean equals(Object object){
		if( object instanceof Mailbox ){
			Mailbox other = (Mailbox) object;
			return other.getName().equals(this.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getName())
				.toHashCode();
	}
}
