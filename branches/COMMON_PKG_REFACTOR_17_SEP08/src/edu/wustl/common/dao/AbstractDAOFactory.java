package edu.wustl.common.dao;

import java.util.HashMap;
import java.util.Map;

import edu.wustl.common.util.logger.Logger;


public class AbstractDAOFactory
{
	/**
	 * Singleton instace.
	 */
	private static AbstractDAOFactory daoFactory = null;
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractDAOFactory.class);
	private static Map<String, DAOFactory> daoFactoryMap = new HashMap<String, DAOFactory>();

	static
	{
		daoFactory = new AbstractDAOFactory();
	}

	/**
	 * Constructor.
	 */
	protected AbstractDAOFactory()
	{
	}

	/**
	 * Getter method in singleton class is to setup mock unit testing.
	 * @return factory
	 */
	public static AbstractDAOFactory getInstance()
	{
		return daoFactory;
	}
	
	public static DAOFactory getDAOFactory(String applicationName)
	{
		DAOFactory daoFactory =  daoFactoryMap.get(applicationName);
		return daoFactory;
	}
	
	public static void populateDaoFactoryMap()
	{
		
	}

}
