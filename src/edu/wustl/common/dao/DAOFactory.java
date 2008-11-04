/**
 * <p>Title: DAOFactory Class>
 * <p>Description:	DAOFactory is a factory for JDBC DAO instances of various domain objects.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.dao;

import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.DaoProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * DAOFactory is a factory for JDBC DAO instances of various domain objects.
 * @author gautam_shetty
 */
public class DAOFactory
{

	/**
	 * Singleton instace.
	 */
	private static DAOFactory factory = null;
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOFactory.class);


	static
	{
		factory = new DAOFactory();
	}

	/**
	 * Constructor.
	 */
	protected DAOFactory()
	{
	}

	/**
	 * Setter method in singleton class is to setup mock unit testing.
	 * @param externalFactory DAOFactory object.
	 * */
	public static void setDAOFactory(DAOFactory externalFactory)
	{
		factory = externalFactory;
	}

	/**
	 * Getter method in singleton class is to setup mock unit testing.
	 * @return factory
	 */
	public static DAOFactory getInstance()
	{
		return factory;
	}

	/**
	 * Returns DAO instance according to the type.
	 * @param daoType The DAO type.
	 * @return An DAO object.
	 * @deprecated This method uses daoType argument which is not required anymore,
	 * please use getDAO() or getJDBCDAO(). 
	 */
	public DAO getDAO(int daoType)
	{
		DAO dao = null;
		switch (daoType)
		{
			case Constants.HIBERNATE_DAO :
				dao = new HibernateDAOImpl();
				break;

			case Constants.JDBC_DAO :
				dao = new JDBCDAOImpl();
				break;

			default :
				break;
		}
		return dao;
	}
	
	public DAO getDAO()
	{
		DAO dao = null;
		try {
		
		   dao = (DAO)Class.forName(DaoProperties.getValue("defaultDao")).newInstance();;
		   
		} catch (InstantiationException inExcp ) {
			
			logger.error(inExcp.getMessage() + "Class not be instantiated,it may be Interface or Abstract class " + inExcp);
			
		} catch (IllegalAccessException excp ) {
			
			logger.error(excp.getMessage() + "Class not Accessible " + excp);
			
		} catch (ClassNotFoundException excp ) {
			
			logger.error(excp.getMessage() + "Class not found " + excp);
			
		}
		return dao;
	}
	
	public DAO getJDBCDAO()
	{
		DAO dao = null;
		try {
		
			dao = (DAO)Class.forName(DaoProperties.getValue("jdbcDao")).newInstance();
	
		} catch (InstantiationException inExcp ) {
			
			logger.error(inExcp.getMessage() + "Class not be instantiated,it may be Interface or Abstract class " + inExcp);
			
		} catch (IllegalAccessException excp ) {
			
			logger.error(excp.getMessage() + "Class not Accessible " + excp);
			
		} catch (ClassNotFoundException excp ) {
			
			logger.error(excp.getMessage() + "Class not found " + excp);
			
		}
		return dao;
	}
	

}
