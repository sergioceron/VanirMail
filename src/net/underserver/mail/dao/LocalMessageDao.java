package net.underserver.mail.dao;

import net.underserver.mail.model.LocalMessage;
import net.underserver.mail.model.MailAddress;

/**
 * User: sergio
 * Date: 9/08/12
 * Time: 01:09 PM
 */
public class LocalMessageDao extends HibernateDao {

	@Override
	public void persist(Object entity) {
		LocalMessage localMessage = (LocalMessage) entity;

		for( MailAddress ma : localMessage.getBcc() ){
			if( super.get(ma.getClass(), ma.getAddress()) == null )
				super.persist(ma);
		}

		for( MailAddress ma : localMessage.getCc() ){
			if( super.get(ma.getClass(), ma.getAddress()) == null )
				super.persist(ma);
		}

		for( MailAddress ma : localMessage.getFrom() ){
			if( super.get(ma.getClass(), ma.getAddress()) == null )
				super.persist(ma);
		}

		for( MailAddress ma : localMessage.getTo() ){
			if( super.get(ma.getClass(), ma.getAddress()) == null )
				super.persist(ma);
		}

		super.persist(localMessage);
	}
}
