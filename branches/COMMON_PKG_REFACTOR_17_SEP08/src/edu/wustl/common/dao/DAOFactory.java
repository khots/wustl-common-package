/**
 * <p>Title: DAOFactory Class>
 * <p>Description:	DAOFactory is a factory for JDBC DAO instances of various domain objects.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.XMLHelper;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import edu.wustl.common.hibernate.HibernateUtil;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.logger.Logger;

public class DAOFactory implements IConnectionManager,IDAOFactory
{
	private String connectionManagerName;
	private String defaultDAOClassName;
	private String jdbcDAOClassName;
	private String applicationName;
	private String configurationFile;
	private static final EntityResolver entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
	private Configuration configuration;
	private SessionFactory sessionFactory;
	private IConnectionManager connectionManager;
	
	// ThreadLocal to hold the Session for the current executing thread.
    private static final ThreadLocal<Map<String, Session>> threadLocal = new ThreadLocal<Map<String, Session>>();
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOFactory.class);
	
	static
	{
		
		Map<String, Session> applicationSessionMap = new HashMap<String, Session>();
		threadLocal.set(applicationSessionMap);
	}
	
	/**
	 * @return
	 */
	public DAO getDAO()
	{
		DAO dao = null;
		
		try 
		{
		   dao = (DAO)Class.forName(defaultDAOClassName).newInstance();
		   dao.setConnectionManager(getConnectionManager());			
		  		
		}
		catch (Exception inExcp )
		{
			logger.error(inExcp.getMessage() + "Class not be instantiated,it may be Interface or Abstract class " + inExcp);
		}	
	
		return dao;
	}
	
	/**
	 * @return
	 */
	public JDBCDAO getJDBCDAO()
	{
		JDBCDAO dao = null;
		
		try
		{
			   dao = (JDBCDAO) Class.forName(jdbcDAOClassName).newInstance();
			   dao.setConnectionManager(getConnectionManager());							
        
		}
		catch (Exception inExcp )
		{
			logger.error(inExcp.getMessage() + "Class not be instantiated,it may be Interface or Abstract class " + inExcp);
		} 
		return dao;
	}
	
	public Connection getConnection() throws HibernateException
	{
		return currentSession().connection();
	}


	public void closeConnection() throws HibernateException
	{
		closeSession();
		
	}

	public void closeSession() throws HibernateException
	{
		Map<String, Session> applicationSessionMap = (Map<String, Session>) threadLocal.get();
		if(applicationSessionMap.containsKey(applicationName))
		{
			Session session = (Session)applicationSessionMap.get(applicationName);
			if(session != null)
			{
				session.close();
			}
			applicationSessionMap.remove(applicationName);
		}
	}
	

	public Session currentSession() throws HibernateException
	{

		Map<String, Session> applicationSessionMap = (Map<String, Session>)threadLocal.get();
	    // Open a new Session, if this Thread has none yet
    
		if (!(applicationSessionMap.containsKey(applicationName)) )
		{
        	Session session = newSession();
        	applicationSessionMap.put(applicationName, session);
        }
        return (Session)applicationSessionMap.get(applicationName);
    
	}
	
	public Session newSession() throws HibernateException
	{
		Session session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.COMMIT);
        try
        {
            session.connection().setAutoCommit(false);
        }
        catch (SQLException ex)
        {
            throw new HibernateException(ex.getMessage(), ex);
        }
        return session;
			
	}

	public Session getCleanSession() throws DAOException
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			return session;
		}
		catch (HibernateException exp)
		{
			throw new DAOException("Problem in Clossing the session :"+exp);
		}
	
	}
	
	public void buildSessionFactory() throws DAOException
	{		
		try
		{
			Configuration configuration = setConfiguration(configurationFile);
			SessionFactory sessionFactory = configuration.buildSessionFactory();
			setConnectionManager(sessionFactory,configuration);
			 
		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(),exp);
			throw new DAOException("Problem in building Sessoin Factory :"+exp);
			
		}
		
		  
	}

	private void setConnectionManager(SessionFactory sessionFactory,Configuration configuration) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		/*
		 * Is writing this is valid here ...confirm !!!
		 */
		IConnectionManager connectionManager = (IConnectionManager)Class.forName(connectionManagerName).newInstance();
		connectionManager.setApplicationName(applicationName);
		connectionManager.setSessionFactory(sessionFactory);
		connectionManager.setConfiguration(configuration);
		setConnectionManager(connectionManager);
	}
	
	
	 /**
     * This method adds configuration file to Hibernate Configuration.
     * @param configurationfile name of the file that needs to be added
     * @param config Configuration to which this file is added.
	 * @throws DAOException 
     */
    private Configuration setConfiguration(String configurationfile) throws DAOException {
        try
        {
        	
        	Configuration configuration = new Configuration();
            //InputStream inputStream = DAOFactory.class.getClassLoader().getResourceAsStream(configurationfile);
        	InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationfile);
            List<Object> errors = new ArrayList<Object>();
            // hibernate api to read configuration file and convert it to
            // Document(dom4j) object.
            XMLHelper xmlHelper = new XMLHelper();
            Document document = xmlHelper.createSAXReader(configurationfile, errors, entityResolver).read(
                    new InputSource(inputStream));
            // convert to w3c Document object.
            DOMWriter writer = new DOMWriter();
            org.w3c.dom.Document doc = writer.write(document);
            // configure
            configuration.configure(doc);
            
            return configuration;
        }
        catch (DocumentException e)
        {
            throw new DAOException(e);
        }
        catch (HibernateException e)
        {
            throw new DAOException(e);
        }
        
       
    }
	

	public void setConnectionManagerName(String connectionManagerName)
	{
		this.connectionManagerName = connectionManagerName;
		
	}

	public void setDefaultDAOClassName(String defaultDAOClassName)
	{
		this.defaultDAOClassName = defaultDAOClassName;
	}

	public void setJdbcDAOClassName(String jdbcDAOClassName)
	{
		this.jdbcDAOClassName = jdbcDAOClassName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public String getApplicationName()
	{
		return applicationName;
	}

	public String getConnectionManagerName()
	{
		return connectionManagerName;
	}

	public String getDefaultDAOClassName()
	{
		return defaultDAOClassName;
	}

	public String getJdbcDAOClassName()
	{
		return jdbcDAOClassName;
	}

	public String getConfigurationFile()
	{
		return configurationFile;
	}

	public void setConfigurationFile(String configurationFile)
	{
		this.configurationFile = configurationFile;
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
	 * 
	 * Have to remove this method::::
	 */
	public Object loadCleanObj(Class objectClass, Long identifier) throws HibernateException
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

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(Configuration cfg)
	{
		this.configuration = cfg;
	}
	
	private IConnectionManager getConnectionManager() {
		return connectionManager;
	}

	private void setConnectionManager(IConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

}
