
package edu.wustl.common.util.dbmanager;

import java.sql.Connection;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.hibernate.HibernateUtil;
import edu.wustl.common.util.global.Variables;

/**
 * <p>
 * Title: DBUtil Class>
 * <p>
 * Description: Utility class provides database specific utilities methods
 * </p>
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St.Louis.
 *
 * @author Kapil Kaveeshwar
 * @version 1.00
 */
public class DBUtil
{

	static
	{
		HibernateMetaData.initHibernateMetaData(HibernateUtil.getConfiguration());
		Variables.databaseName = HibernateMetaData.getDataBaseName();
	}

	/**
	 * @return Session only current opened session.
	 * @throws HibernateException exception about Hibernate.
	 */
	public static Session currentSession() throws HibernateException
	{
		return HibernateUtil.currentSession();
	}

	/**
	 * Close the currently opened session.
	 * @throws HibernateException exception of Hibernate.
	 */
	public static void closeSession() throws HibernateException
	{
		HibernateUtil.closeSession();
	}

	/**
	 * @return Connection get the connection.
	 * @throws HibernateException exception of Hibernate.
	 */
	public static Connection getConnection() throws HibernateException
	{
		return HibernateUtil.getConnection();
	}

	/**
	 * Close the connection.
	 * @throws HibernateException exception of Hibernate.
	 */
	public static void closeConnection() throws HibernateException
	{
		HibernateUtil.closeSession();
	}

	/**
	 * This method opens a new session, loads an object with given class and Id,
	 * and closes the session. This method should be used only when an object is
	 * to be opened in separate session.
	 *
	 * @param objectClass class of the object
	 * @param identifier id of the object
	 * @return object
	 * @throws HibernateException exception of Hibernate.
	 */
	public static Object loadCleanObj(Class objectClass, Long identifier) throws HibernateException
	{
		Session session = null;
		try
		{
			session = HibernateUtil.getSessionFactory().openSession();
			return session.load(objectClass, identifier);
		}
		finally
		{
			session.close();
		}
	}

	/**
	 * @return Session details of session.
	 * @throws BizLogicException exception of BizLogic.
	 */
	public static Session getCleanSession() throws BizLogicException
	{
		Session session = null;
		try
		{
			session = HibernateUtil.getSessionFactory().openSession();
			return session;
		}
		catch (HibernateException e)
		{
			throw new BizLogicException(e);
		}
	}
}