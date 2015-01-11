package net.underserver.mail.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

/**
 * User: sergio
 * Date: 22/07/12
 * Time: 09:15 PM
 */
@Entity
public class Account {

	private Long id;
	private int port;
	private boolean ssl;
	private boolean tls;
	private String username;
	private String password;
	private String server;

	public Account() {
		this.port = 0;
		this.ssl  = false;
		this.tls  = false;
	}

	public Account(String server, String username, String password) {
		this();
		this.server   = server;
		this.username = username;
		this.password = password;
	}

	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isTls() {
		return tls;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
	}

	@Override
	public boolean equals(Object object){
		if( object instanceof Account){
			Account other = (Account) object;
			return other.getServer().equals(this.getServer()) &&
				   other.getPort() == this.getPort() &&
				   other.getUsername().equals(this.getUsername()) &&
				   other.getPassword().equals(this.getPassword());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getServer())
				.append(getUsername())
				.append(getPassword())
				.append(getPort())
				.toHashCode();
	}
}
