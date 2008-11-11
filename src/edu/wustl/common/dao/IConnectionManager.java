package edu.wustl.common.dao;

import java.sql.Connection;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.wustl.common.exception.BizLogicException;


public interface IConnectionManager
{

	Connection getConnection() throws HibernateException;
	
	void closeConnection() throws HibernateException;
	
	Session newSession() throws HibernateException;
	
	void closeSession() throws HibernateException;
	
	Session currentSession() throws HibernateException;
	
	Object loadCleanObj(Class objectClass, Long identifier) throws HibernateException;

	Session getCleanSession() throws BizLogicException;
	
	void setApplicationName(String applicationName);
	
	String getApplicationName();
	
	void setSessionFactory(SessionFactory sessionFactory);
	
	SessionFactory getSessionFactory();
	
	void setConfigurationFile(Configuration cfg);
	
	Configuration getConfigurationFile();
	
}
