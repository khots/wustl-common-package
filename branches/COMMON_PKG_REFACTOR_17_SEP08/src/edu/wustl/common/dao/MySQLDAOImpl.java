package edu.wustl.common.dao;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.logger.Logger;


public class MySQLDAOImpl extends JDBCDAOImpl
{
	
	private static org.apache.log4j.Logger logger = Logger.getLogger(MySQLDAOImpl.class);
		
	/**
	 * Deletes the specified table
	 * @param tableName
	 * @throws DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("DROP TABLE IF EXISTS " + tableName);
			executeUpdate(query.toString());
	}
			

	
	public String getDatePattern()
	{
		String datePattern = "%m-%d-%Y";
		return datePattern;
	}
	
	public String getTimePattern()
	{
		String timePattern = "%H:%i:%s";
		return timePattern;
	}
	public String getDateFormatFunction()
	{
		String dateFormatFunction = "DATE_FORMAT";
		return dateFormatFunction;
	}
	public String getTimeFormatFunction()
	{
		String timeFormatFunction = "TIME_FORMAT";
		return timeFormatFunction;
	}
	
	public String getDateTostrFunction()
	{
		String timeFormatFunction = "TO_CHAR";
		return timeFormatFunction;
	}
	
	public String getStrTodateFunction()
	{
		String timeFormatFunction = "STR_TO_DATE";
		return timeFormatFunction;
	}
	
	

	public String getActivityStatus(String sourceObjectName, Long indetifier) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean, boolean isAuditable) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}


	public void delete(Object obj) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}


	public void disableRelatedObjects(String tableName, String whereColumnName, Long[] whereColumnValues) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}


	public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureInsert) throws DAOException, UserNotAuthorizedException
	{
		// TODO Auto-generated method stub
		
	}


	public Object retrieveAttribute(String sourceObjectName, Long identifier, String attributeName) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException, UserNotAuthorizedException
	{
		// TODO Auto-generated method stub
		
	}



	
		
}
