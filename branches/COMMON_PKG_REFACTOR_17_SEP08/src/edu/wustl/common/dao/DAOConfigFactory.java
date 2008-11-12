package edu.wustl.common.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.wustl.common.util.logger.Logger;


public class DAOConfigFactory
{
	/**
	 * Singleton instace.
	 */
	private static DAOConfigFactory daoConfigFactory;
	private static IDAOFactory defaultDAOFactory;
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOConfigFactory.class);
	private static Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();

	static
	{
		daoConfigFactory = new DAOConfigFactory();
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
		return daoConfigFactory;
	}
	
	
	public IDAOFactory getDAOFactory(String applicationName)
	{
		IDAOFactory daoFactory = (IDAOFactory)daoFactoryMap.get(applicationName);
		return daoFactory;
	}
	
	public IDAOFactory getDAOFactory()
	{
		IDAOFactory daoFactory =  defaultDAOFactory;
		return daoFactory;
	}
	
	
	public static void populateDaoFactoryMap()
	{
		try
		{
			ApplicationDAOPropertiesParser applicationPropertiesParser = new ApplicationDAOPropertiesParser();
			daoFactoryMap = applicationPropertiesParser.getDaoFactoryMap();
			
			/*
			 * Is this right approach ...have to confirm with abhijit
			 * or we can keep default field in xml doc and save it in Map with key as defaultapplication 
			 */
			Iterator mapKeySetIterator = daoFactoryMap.keySet().iterator();
			if(mapKeySetIterator.hasNext())	{
					defaultDAOFactory = (IDAOFactory)daoFactoryMap.get(mapKeySetIterator.next());
			}	

			/*
			IDAOFactory daoConfigFactory = (IDAOFactory)Class.forName(DaoProperties.getValue("daoConfigFactory.name")).newInstance();
			daoConfigFactory.setConnectionManagerName(DaoProperties.getValue("connectionManager"));
			daoConfigFactory.setDefaultDAOClassName(DaoProperties.getValue("defaultDao"));
			daoConfigFactory.setJDBCDAOClassName(DaoProperties.getValue("jdbcDao"));
			daoConfigFactory.setApplicationName(DaoProperties.getValue("application.name"));
			daoConfigFactory.setConfigurationFile(DaoProperties.getValue("configuration.file").trim());		
			daoConfigFactory.buildSessionFactory();
			
			daoFactoryMap.put(daoConfigFactory.getApplicationName(),daoConfigFactory);*/
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		

}
