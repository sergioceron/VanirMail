package net.underserver.mail.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sergio
 * Date: 6/08/12
 * Time: 06:55 PM
 */
@Entity
public class MailAddress {

	private String address;
	private String personal;
	private boolean group;
	private List<MailAddress> members;

	public MailAddress() {
		members = new ArrayList<MailAddress>();
	}

	public MailAddress(InternetAddress inetAddress) {
		this();
		this.address = inetAddress.getAddress();
		this.personal = inetAddress.getPersonal();
		this.group = inetAddress.isGroup();
		this.members = new ArrayList<MailAddress>();
		if( isGroup() ){
			try {
				for( InternetAddress ia : inetAddress.getGroup(true) ){
		            this.members.add(new MailAddress(ia));
				}
			} catch (AddressException e) {
				e.printStackTrace();
			}
		}
	}

	@Id
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	@Column(name = "isgroup")
	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	public List<MailAddress> getMembers() {
		return members;
	}

	public void setMembers(List<MailAddress> members) {
		this.members = members;
	}
	
	@Override
	public boolean equals(Object object){
		if( object instanceof MailAddress){
			MailAddress other = (MailAddress) object;
			return other.getAddress().equals(this.getAddress());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(address)
				.toHashCode();
	}
}
