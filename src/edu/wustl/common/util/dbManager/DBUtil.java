/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.util.dbManager;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.util.XMLHelper;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.xml.sax.InputSource;

import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * <p>Title: DBUtil Class>
 * <p>Description:  Utility class provides database specific utilities methods </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Kapil Kaveeshwar
 * @version 1.00
 */
public class DBUtil
{

	//A factory for DB Session which provides the Connection for client. 
	private static SessionFactory m_sessionFactory;

	//ThreadLocal to hold the Session for the current executing thread. 
	private static final ThreadLocal threadLocal = new ThreadLocal();
	//Initialize the session Factory in the Static block.
	static
	{
		try
		{
			Configuration cfg = new Configuration();

			InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream(
					"dbutil.properties");
			Properties p = new Properties();
			p.load(inputStream);
			inputStream.close();

			String configurationFileNames = p.getProperty("hibernate.configuration.files");

			String[] fileNames = configurationFileNames.split(",");

			// if no configuraiton file found, get the default one.
			if (fileNames.length == 0) {
				fileNames = new String[] {"hibernate.cfg.xml"};
			}
			
            //get all configuration files 
			for (int i = 0; i < fileNames.length; i++)
			{
				String fileName = fileNames[i];
				fileName = fileName.trim();
				addConfigurationFile(fileName, cfg);
			}

			m_sessionFactory = cfg.buildSessionFactory();
			HibernateMetaData.initHibernateMetaData(cfg);

			Variables.databaseName = HibernateMetaData.getDataBaseName();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Logger.out.debug("Exception: " + ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * This method adds configuration file to Hibernate Configuration. 
	 * @param fileName name of the file that needs to be added
	 * @param cfg Configuration to which this file is added.
	 */
	private static void addConfigurationFile(String fileName, Configuration cfg)
	{

		try
		{
			InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream(fileName);
			List errors = new ArrayList();
			//hibernate api to read configuration file and convert it to Document(dom4j) object.
			Document document = XMLHelper.createSAXReader(fileName, errors).read(
					new InputSource(inputStream));
			//convert to w3c Document object.
			DOMWriter writer = new DOMWriter();
			org.w3c.dom.Document doc = writer.write(document);
			//configure
			cfg.configure(doc);
		}
		catch (DocumentException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		catch (HibernateException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Follows the singleton pattern and returns only current opened session.
	 * @return Returns the current db session.  
	 * */
	public static Session currentSession() throws HibernateException
	{
		Session s = (Session) threadLocal.get();

		//Open a new Session, if this Thread has none yet
		if (s == null)
		{
			s = m_sessionFactory.openSession();
			try
			{
				s.connection().setAutoCommit(false);
			}
			catch (SQLException ex)
			{
				throw new HibernateException(ex.getMessage(), ex);
			}
			threadLocal.set(s);
		}
		return s;
	}

	/**
	 * Close the currently opened session.
	 * */
	public static void closeSession() throws HibernateException
	{
		Session s = (Session) threadLocal.get();
		threadLocal.set(null);
		if (s != null)
			s.close();
	}

	public static Connection getConnection() throws HibernateException
	{
		return currentSession().connection();
	}

	public static void closeConnection() throws HibernateException
	{
		closeSession();
	}

	/**
	 * This method opens a new session, loads an object with given class and Id, and closes
	 * the session. This method should be used only when an object is to be opened in separate session.  
	 * 
	 * @param objectClass class of the object
	 * @param identifier id of the object
	 * @return object
	 * @throws HibernateException
	 */
	public static Object loadCleanObj(Class objectClass, Long identifier) throws HibernateException
	{
		Session session = null;
		try
		{
			session = m_sessionFactory.openSession();
			return session.load(objectClass, identifier);
		}
		catch (HibernateException e)
		{
			throw e;
		}
		finally
		{
			session.close();
		}
	}
}