package edu.wustl.common.dao;

import java.util.HashMap;
import java.util.Map;

import edu.wustl.common.util.logger.Logger;


public class DAOConfigFactory
{
	/**
	 * Singleton instace.
	 */
	private static DAOConfigFactory daoFactory = null;
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOConfigFactory.class);
	private static Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();

	static
	{
		daoFactory = new DAOConfigFactory();
	}

	/**
	 * Constructor.
	 */
	public DAOConfigFactory()
	{
		populateDaoFactoryMap();
				
	}
	
	
	/**
	 * Getter method in singleton class is to setup mock unit testing.
	 * @return factory
	 */
	public static DAOConfigFactory getInstance()
	{
		return daoFactory;
	}
	
	
	public static IDAOFactory getDAOFactory(String applicationName)
	{
		IDAOFactory daoFactory = daoFactoryMap.get(applicationName);
		return daoFactory;
	}
	
	public static IDAOFactory getDAOFactory()
	{
		IDAOFactory daoFactory =  daoFactoryMap.get("catissuecore");
		return daoFactory;
	}
	
	
	public static void populateDaoFactoryMap()
	{
		try
		{
			ApplicationDAOPropertiesParser applicationPropertiesParser = new ApplicationDAOPropertiesParser();
			daoFactoryMap = applicationPropertiesParser.getDaoFactoryMap();

			/*
			IDAOFactory daoFactory = (IDAOFactory)Class.forName(DaoProperties.getValue("daoFactory.name")).newInstance();
			daoFactory.setConnectionManagerName(DaoProperties.getValue("connectionManager"));
			daoFactory.setDefaultDAOClassName(DaoProperties.getValue("defaultDao"));
			daoFactory.setJDBCDAOClassName(DaoProperties.getValue("jdbcDao"));
			daoFactory.setApplicationName(DaoProperties.getValue("application.name"));
			daoFactory.setConfigurationFile(DaoProperties.getValue("configuration.file").trim());		
			daoFactory.buildSessionFactory();
			
			daoFactoryMap.put(daoFactory.getApplicationName(),daoFactory);*/
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		

}
