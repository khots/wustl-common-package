package edu.wustl.common.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.executor.OracleQueryExecutor;


public class OracleDAOImpl extends AbstractJDBCDAOImpl
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(OracleDAOImpl.class);

	/**
	 * Deletes the specified table
	 * @param tableName
	 * @throws DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		ResultSet resultSet = null;
		try
		{
			
			databaseConnectionParams.setConnection(getConnection());
						
			StringBuffer query = new StringBuffer("select tname from tab where tname='" + tableName + "'");
			resultSet = databaseConnectionParams.getResultSet(query.toString());
			boolean isTableExists = resultSet.next();
			
			logger.debug("ORACLE :" + query.toString() + isTableExists);
			
			if (isTableExists)
			{
				
				logger.debug("Drop Table");
				databaseConnectionParams.executeUpdate("DROP TABLE " + tableName + " cascade constraints");
			}
			
		} 
		catch (Exception sqlExp)
		{
			
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
			
		}
		finally
		{
			try
			{
				if (resultSet != null)
				{
					resultSet.close();
				}
				databaseConnectionParams.closeConnectionParams();
				
			}
			catch(SQLException sqlExp)
			{
				logger.fatal(DAOConstants.CONNECTIONS_CLOSING_ISSUE, sqlExp);
			}
		}
		
	}
	
		
		
	public String getDatePattern()
	{
		
		return "mm-dd-yyyy";
	}
	
	public String getTimePattern()
	{
		
		return "hh-mi-ss";
	}
	public String getDateFormatFunction()
	{
		
		return "TO_CHAR";
	}
	public String getTimeFormatFunction()
	{
		return "TO_CHAR";
	}
	
	public String getDateTostrFunction()
	{
		
		return "TO_CHAR";
	}
	
	public String getStrTodateFunction()
	{
		
		return "TO_DATE";
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
		List<Integer> dateColumns = new ArrayList<Integer>();
		List<Integer> numberColumns = new ArrayList<Integer>();
		List<Integer> tinyIntColumns = new ArrayList<Integer>();
		List<String> columnNames_t = new ArrayList<String>();
		
		ResultSetMetaData metaData;
		if(columnNames != null && columnNames.length > 0)
		{
			metaData = getMetaData(tableName, columnNames[0]);
		} 
		else
		{
			metaData = getMetaDataAndUpdateColumns(tableName,columnNames_t);
		}
		
		updateColumns(metaData, dateColumns,numberColumns, tinyIntColumns);
		String insertQuery = createInsertQuery(tableName,columnNames_t,columnValues);
		
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		databaseConnectionParams.setConnection(getConnection());
		
		PreparedStatement stmt = null;
		try
		{
			stmt = databaseConnectionParams.getPreparedStatement(insertQuery);
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
			databaseConnectionParams.closeConnectionParams();
		}
	}
	
	
	public String formatMessage(Exception excp, Object[] args)
	{
		
		String formattedErrMsg; // Formatted Error Message return by this method
		Exception objExcp = excp;
		
		try
		{
			if (excp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
			{
				objExcp = (Exception) objExcp.getCause();
				logger.debug(objExcp);
			}
			formattedErrMsg = ConstraintViolationFormatter.getFormatedErrorMessageForOracle(args,objExcp);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;
		}
		return formattedErrMsg;
	}
	
	public PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException
	{
		PagenatedResultData pagenatedResultData = null;
				
		queryParams.setConnection(getConnectionManager().getConnection());
		OracleQueryExecutor oracleQueryExecutor = new OracleQueryExecutor();
		pagenatedResultData = oracleQueryExecutor.getQueryResultList(queryParams);
		
		return pagenatedResultData;

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

	public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate) throws DAOException, UserNotAuthorizedException
	{
		// TODO Auto-generated method stub
		
	}



	public Object retrieve(String sourceObjectName, Long identifier)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
