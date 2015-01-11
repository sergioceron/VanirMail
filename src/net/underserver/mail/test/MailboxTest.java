package net.underserver.mail.test;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * User: sergio
 * Date: 3/08/12
 * Time: 10:17 PM
 */
public class MailboxTest {

	private net.underserver.mail.dao.Dao dao;

	@Before
	public void init(){
		dao = new net.underserver.mail.dao.HibernateDao();
	}

	@Test
	public void testGetFolders() throws Exception {

		net.underserver.mail.model.Mailbox mailbox = new net.underserver.mail.model.Mailbox();
		mailbox.setType(net.underserver.mail.model.Mailbox.Type.IMAP);
		mailbox.setName("psychostrauss");
		net.underserver.mail.model.Account incoming = new net.underserver.mail.model.Account("imap.gmail.com", "psychostrauss@gmail.com", "LaciudadX.com");
		incoming.setSsl(true);
		net.underserver.mail.model.Account outgoing = new net.underserver.mail.model.Account("smtp.gmail.com", "psychostrauss@gmail.com", "LaciudadX.com");
		incoming.setSsl(true);
		mailbox.setIncoming(incoming);
		mailbox.setOutgoing(outgoing);

		net.underserver.mail.model.Mailbox mailbox1 = new net.underserver.mail.model.Mailbox();
		mailbox1.setType(net.underserver.mail.model.Mailbox.Type.IMAP);
		mailbox1.setName("sceronf");
		net.underserver.mail.model.Account incoming1 = new net.underserver.mail.model.Account("imap.gmail.com", "sceronf@gmail.com", "LaciudadX.com1");
		incoming1.setSsl(true);
		net.underserver.mail.model.Account outgoing1 = new net.underserver.mail.model.Account("smtp.gmail.com", "sceronf@gmail.com", "LaciudadX.com1");
		incoming1.setSsl(true);
		mailbox1.setIncoming(incoming1);
		mailbox1.setOutgoing(outgoing1);


		dao.persist(mailbox);
		dao.persist(mailbox1);
		dao.flush();
		//Mailbox d = dao.
		//dao.delete(Ma);

		List<net.underserver.mail.model.Mailbox> m = dao.find(net.underserver.mail.model.Mailbox.class);
		System.out.println(m.size());

	}
}
