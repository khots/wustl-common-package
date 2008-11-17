package edu.wustl.common.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;


public class MySQLDAOImpl extends AbstractJDBCDAOImpl
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
		query = new StringBuffer("DROP TABLE IF EXISTS ").append(tableName);
			executeUpdate(query.toString());
	}
		
	public String getDatePattern()
	{
		return "%m-%d-%Y";
	}
	
	public String getTimePattern()
	{
		return "%H:%i:%s";
	}
	public String getDateFormatFunction()
	{
		return "DATE_FORMAT";
	}
	public String getTimeFormatFunction()
	{
		return "TIME_FORMAT";
	}
	
	public String getDateTostrFunction()
	{
		return "TO_CHAR";
	}
	
	public String getStrTodateFunction()
	{
		
		return "STR_TO_DATE";
	}
	
	
	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.JDBCDAO#insert(java.lang.String, java.util.List)
	 */
	public void insert(String tableName, List<Object> columnValues) throws DAOException, SQLException
	{
		insert(tableName, columnValues, null);
	}
	
	/**
	 * @param tableName
	 * @param columnValues
	 * @param columnNames
	 * @throws DAOException
	 * @throws SQLException
	 */
	public void insert(String tableName, List<Object> columnValues, List<String>... columnNames) throws DAOException, SQLException
	{
		List<Integer>dateColumns = new ArrayList<Integer>();
		List<Integer>numberColumns = new ArrayList<Integer>();
		List<Integer>tinyIntColumns = new ArrayList<Integer>();
		List<String>columnNames_t = new ArrayList<String>();
		
		ResultSetMetaData metaData = getMetadataAndUpdatedColumns(tableName,columnNames_t,columnNames);
		updateColumns(metaData, dateColumns,numberColumns, tinyIntColumns);
		String insertQuery = createInsertQuery(tableName,columnNames_t,columnValues);
		
		PreparedStatement stmt = null;
		try
		{
			stmt = getPreparedStatement(insertQuery);
			for (int i = 0; i < columnValues.size(); i++)
			{
				Object obj = columnValues.get(i);
				
				setDateColumns(stmt, i,obj, dateColumns);
				
				setTinyIntColumns(stmt, i, obj,tinyIntColumns);
				
				setTimeStampColumn(stmt, i, obj);
				
				setNumberColumns(numberColumns, stmt, i, obj);
					
				
			}
			stmt.executeUpdate();
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(),sqlExp);
			throw new DAOException(sqlExp.getMessage(), sqlExp);
		}
		finally
		{
			try
			{
				if (stmt != null) {
					stmt.close();
				}	
			}
			catch (SQLException ex)
			{
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	public String formatMessage(Exception excp, Object[] args)
	{
		logger.debug(excp.getClass().getName());
		Exception objExcp = excp;
		String tableName = null; // stores Table_Name for which column name to be found 
		String formattedErrMsg = null; // Formatted Error Message return by this method
		try
		{
			if (objExcp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
			{
				objExcp = (Exception) objExcp.getCause();
				logger.debug(objExcp);
			}
			tableName = ConstraintViolationFormatter.getTableName(args);
			formattedErrMsg = (String) ConstraintViolationFormatter.getFormattedErrorMessage(args, objExcp, tableName);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;
		}
		return formattedErrMsg;

	}

	

	

	public String getActivityStatus(String sourceObjectName, Long indetifier) throws DAOException
	{
			return null;
	}


	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean, boolean isAuditable) throws DAOException
	{
			//default imp
	}


	public void delete(Object obj) throws DAOException
	{
		//default imp
	}


	public void disableRelatedObjects(String tableName, String whereColumnName, Long[] whereColumnValues) throws DAOException
	{
		//default imp	
	}


	public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureInsert) throws DAOException, UserNotAuthorizedException
	{
		//default imp	
	}


	public Object retrieveAttribute(String sourceObjectName, Long identifier, String attributeName) throws DAOException
	{
			return null;
	}


	public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate) throws DAOException, UserNotAuthorizedException
	{
		//default imp	
	}
	
	



	
		
}
