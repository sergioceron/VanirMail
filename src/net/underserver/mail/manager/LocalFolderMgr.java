package net.underserver.mail.manager;

import net.underserver.mail.dao.Dao;
import net.underserver.mail.dao.HibernateDao;

/**
 * User: sergio
 * Date: 14/08/12
 * Time: 09:35 PM
 */
public class LocalFolderMgr {
	private final Dao dao = new HibernateDao();
}
