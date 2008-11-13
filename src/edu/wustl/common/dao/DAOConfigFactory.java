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
	private static DAOConfigFactory daoConfigurationFactory;
	private static IDAOFactory defaultDAOFactory;
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOConfigFactory.class);
	private static Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();

	static
	{
		daoConfigurationFactory = new DAOConfigFactory();
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
		return daoConfigurationFactory;
	}
	
	
	public IDAOFactory getDAOFactory(String applicationName)
	{
		return (IDAOFactory)daoFactoryMap.get(applicationName);
	}
	
	public IDAOFactory getDAOFactory()
	{
		return defaultDAOFactory;
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
			Iterator<String> mapKeySetIterator = daoFactoryMap.keySet().iterator();
			if(mapKeySetIterator.hasNext())	{
					defaultDAOFactory = (IDAOFactory)daoFactoryMap.get(mapKeySetIterator.next());
			}	
		}
		catch (Exception expc)
		{
			logger.error(expc.getMessage(), expc);
		}
	}
		

}
