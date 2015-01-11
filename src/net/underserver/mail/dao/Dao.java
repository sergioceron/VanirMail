package net.underserver.mail.dao;

import java.io.Serializable;
import java.util.List;

/**
 * User: sergio
 * Date: 4/08/12
 * Time: 03:21 PM
 */
public interface Dao {
	public void persist(Object entity);
	public void persist(Object[] entities);
	public void delete(Object entity);
	public <T> List<T> find(String hql);
	public <T> List<T> find(Class<T> entityClass);
	public <T> T load(Class<T> entityClass, Serializable id);
	public <T> T get(Class<T> entityClass, Serializable id);
	public void flush();
}