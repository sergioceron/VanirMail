package net.underserver.mail.view;

import net.underserver.mail.controller.MailboxController;
import net.underserver.mail.dao.Dao;
import net.underserver.mail.dao.HibernateDao;
import net.underserver.mail.model.LocalFolder;
import net.underserver.mail.model.Mailbox;
import org.apache.log4j.*;
import org.apache.log4j.net.TelnetAppender;

import java.io.IOException;
import java.util.List;

/**
 * User: sergio
 * Date: 10/08/12
 * Time: 12:00 PM
 */
public class Boot {
	private MailboxController mailboxController = MailboxController.getInstance();
	private Dao dao;

	public Boot() {
		dao = new HibernateDao();
		List<Mailbox> mailboxes = dao.find(Mailbox.class);

		setupLogger();
		setupMailboxes(mailboxes);

		UI window = new UI(mailboxes);
		window.init();
	}

	public static void main(String[] args) {
		new Boot();
	}

	private void setupMailboxes(List<Mailbox> mailboxes){
		for( Mailbox mailbox : mailboxes ) {
			if( mailbox.getFolders().size() == 0 ){
				if( mailboxController.connect(mailbox) ){
					List<LocalFolder> folders = mailboxController.getFolders(mailbox);
					for( LocalFolder folder : folders ){
						dao.persist(folder);
					}
					mailbox.setFolders( folders );
					dao.persist(mailbox);
				}
			}
		}
		dao.flush();
	}

	private void setupLogger() {
		BasicConfigurator.resetConfiguration();
		PatternLayout fileLayout = new PatternLayout("%d %-5p [%c{1}] %m%n");
		PatternLayout telnetLayout = new PatternLayout("%d \033[40m\033[32m%-5p\033[0m [%c{1}] %m%n");
		String file = "vanirmail.log";
		FileAppender fileAppender = null;
		try {
			fileAppender = new FileAppender(fileLayout, file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		TelnetAppender telnetAppender = new TelnetAppender();
		telnetAppender.setName("ta");
		telnetAppender.setPort(8789);
		telnetAppender.setLayout(telnetLayout);
		telnetAppender.activateOptions();

		BasicConfigurator.configure(fileAppender);
		BasicConfigurator.configure(telnetAppender);

		Logger.getLogger("org.hibernate").setLevel(Level.WARN);
		Logger.getLogger("org.apache.commons.beanutils.converters").setLevel(Level.WARN);
		Logger log = Logger.getLogger("main");
		log.setLevel(Level.DEBUG);
		log.debug("Log4j init");
	}

}
