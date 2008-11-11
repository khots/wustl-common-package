package edu.wustl.common.dao;

import java.sql.Connection;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.wustl.common.exception.BizLogicException;


public interface IConnectionManager
{

	Connection getConnection() throws HibernateException;
	
	void closeConnection() throws HibernateException;
	
	Map newSession() throws HibernateException;
	
	void closeSession() throws HibernateException;
	
	Session currentSession() throws HibernateException;
	
	Object loadCleanObj(Class objectClass, Long identifier) throws HibernateException;

	Session getCleanSession() throws BizLogicException;
}
