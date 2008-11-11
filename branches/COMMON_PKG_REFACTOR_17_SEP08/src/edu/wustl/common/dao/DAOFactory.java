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
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.XMLHelper;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.hibernate.HibernateUtil;
import edu.wustl.common.util.global.DaoProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * DAOFactory is a factory for JDBC DAO instances of various domain objects.
 * @author gautam_shetty
 */
public class DAOFactory extends AbstractDAOFactory implements IConnectionManager,IDAOFactory
{
	private String connectionManagerName;
	private String defaultDAOClassName;
	private String jdbcDAOClassName;
	private String applicationName;
	private String configurationFile;
	private Map<String,Session> applicationSessionMap = new HashMap<String, Session>(); 
	
	
	private static final EntityResolver entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
	private Configuration cfg;
	private SessionFactory m_sessionFactory;
	 // ThreadLocal to hold the Session for the current executing thread.
    private static final ThreadLocal<Map> threadLocal = new ThreadLocal<Map>();
	

	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOFactory.class);
	
	public DAO getDAO()
	{
		DAO dao = null;
		try {
		
		   //dao = (DAO)Class.forName(DaoProperties.getValue("defaultDao")).newInstance();
		   Constructor daoConstructor = Class.forName(DaoProperties.getValue("defaultDao")).
		   										getConstructor(new Class[]{IConnectionManager.class});
		   dao = (DAO)daoConstructor.newInstance(this);
		   
		} catch (Exception inExcp ) {
			
			logger.error(inExcp.getMessage() + "Class not be instantiated,it may be Interface or Abstract class " + inExcp);
		}	
	
		return dao;
	}
	
	public JDBCDAO getJDBCDAO()
	{
		JDBCDAO dao = null;
		try {
		
			//dao = (JDBCDAO)Class.forName(DaoProperties.getValue("jdbcDao")).newInstance();
			  Constructor daoConstructor = Class.forName(DaoProperties.getValue("jdbcDao")).
			  										getConstructor(new Class[]{IConnectionManager.class});
              dao = (JDBCDAO)daoConstructor.newInstance(this);
			
	
		} catch (Exception inExcp ) {
			
			logger.error(inExcp.getMessage() + "Class not be instantiated,it may be Interface or Abstract class " + inExcp);
			
		} 
		return dao;
	}

	public void closeConnection() throws HibernateException
	{
		// TODO Auto-generated method stub
		
	}

	public void closeSession() throws HibernateException
	{
		// TODO Auto-generated method stub
		
	}

	public Session currentSession() throws HibernateException
	{

		applicationSessionMap = threadLocal.get();
		Session session = null;
           // Open a new Session, if this Thread has none yet
        if (applicationSessionMap == null || !(applicationSessionMap.containsKey(applicationName)) ) {
        	session = newSession();
        	applicationSessionMap.put(applicationName, session);
            threadLocal.set(applicationSessionMap);
        }
        return (Session)applicationSessionMap.get(applicationName);
    
	}
	
	public Session newSession() throws HibernateException
	{
		
		return null;
		
	}

	public Session getCleanSession() throws BizLogicException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Connection getConnection() throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object loadCleanObj(Class objectClass, Long identifier) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void buildSessionFactory()
	{
		 cfg = new Configuration(); 
		 addConfigurationFile(configurationFile, cfg);
		 m_sessionFactory = cfg.buildSessionFactory();
		
	}
	
	
	 /**
     * This method adds configuration file to Hibernate Configuration.
     * 
     * @param configurationfile name of the file that needs to be added
     * @param cfg Configuration to which this file is added.
     */
    private void addConfigurationFile(String configurationfile, Configuration cfg) {
        try {
            InputStream inputStream = DAOFactory.class.getClassLoader().getResourceAsStream(configurationfile);
            List errors = new ArrayList();
            // hibernate api to read configuration file and convert it to
            // Document(dom4j) object.
            XMLHelper xmlHelper = new XMLHelper();
            Document document = xmlHelper.createSAXReader(configurationfile, errors, entityResolver).read(
                    new InputSource(inputStream));
            // convert to w3c Document object.
            DOMWriter writer = new DOMWriter();
            org.w3c.dom.Document doc = writer.write(document);
            // configure
            cfg.configure(doc);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }
	

	public void setConnectionManager(String connectionManagerName)
	{
		this.connectionManagerName = connectionManagerName;
		
	}

	public void setDefaultDAOClassName(String defaultDAOClassName)
	{
		this.defaultDAOClassName = defaultDAOClassName;
		
	}

	public void setJDBCDAOClassName(String jdbcDAOClassName)
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

	public String getConnectionManager()
	{
		
		return connectionManagerName;
	}

	public String getDefaultDAOClassName()
	{
		
		return defaultDAOClassName;
	}

	public String getJDBCDAOClassName()
	{
		
		return jdbcDAOClassName;
	}

	public String getconfigurationFile()
	{
		return configurationFile;
	}

	public void setConfigurationFile(String configurationFile)
	{
		this.configurationFile = configurationFile;
		
	}

	

	

	
	

}
