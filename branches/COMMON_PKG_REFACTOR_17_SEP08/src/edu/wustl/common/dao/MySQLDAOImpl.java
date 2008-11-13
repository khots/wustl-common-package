package edu.wustl.common.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatterFactory;
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
		List<Integer> dateColumns = new ArrayList<Integer>();
		List<Integer> numberColumns = new ArrayList<Integer>();
		List<Integer> tinyIntColumns = new ArrayList<Integer>();
		List <String>columnNames_t = getColumns(tableName,
				dateColumns,numberColumns,tinyIntColumns,columnNames);

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
		if (objExcp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
		{
			objExcp = (Exception) objExcp.getCause();
			logger.debug(objExcp);
		}
		String dispTableName = null;
		String tableName = null; // stores Table_Name for which column name to be found 
		String columnName = null; //stores Column_Name of table  
		String formattedErrMsg = null; // Formatted Error Message return by this method
		Connection connection = null;

		if (args[0] == null) {
			logger.debug("Table Name not specified");
			tableName = "Unknown Table";
			
		} else {
			tableName = (String) args[0];
		}
		logger.debug("Table Name:" + tableName);
		dispTableName = tableName;
		if (args.length > 2)
		{
			if (args[2] != null)
			{
				dispTableName = (String) args[2];
			}
			else
			{
				logger.debug("Table Name not specified");
				dispTableName = tableName;
			}
		}
		try
		{
			//get Class name from message "could not insert [classname]"
			tableName = ConstraintViolationFormatter.getTableNameFromMessage(tableName,objExcp);
			// Generate Error Message by appending all messages of previous cause Exceptions
			String sqlMessage = ConstraintViolationFormatter.generateErrorMessage(objExcp);

			// From the MySQL error msg and extract the key ID 
			// The unique key voilation message is "Duplicate entry %s for key %d"

			int key = -1;
			int indexofMsg = 0;
			indexofMsg = sqlMessage.indexOf(Constants.MYSQL_DUPL_KEY_MSG);
			indexofMsg += Constants.MYSQL_DUPL_KEY_MSG.length();

			// Get the %d part of the string
			String strKey = sqlMessage.substring(indexofMsg, sqlMessage.length() - 1);
			key = Integer.parseInt(strKey);
			logger.debug(String.valueOf(key));

			// For the key extracted frm the string, get the column name on which the 
			// costraint has failed
			boolean found = false;
			// get connection from arguments
			if (args[1] != null)
			{
				connection = (Connection) args[1];
			}
			else
			{
				logger.debug("Error Message: Connection object not given");
			}

			// Get database metadata object for the connection
			DatabaseMetaData dbmd = connection.getMetaData();

			//  Get a description of the given table's indices and statistics
			ResultSet rs = dbmd.getIndexInfo(connection.getCatalog(), null, tableName, true, false);
			StringBuffer columnNames = ConstraintViolationFormatter.getColumnInfo(rs,key);
			rs.close();

			// Create arrays of object containing data to insert in CONSTRAINT_VOILATION_ERROR
			Object[] arguments = new Object[2];
			dispTableName = ExceptionFormatterFactory.getDisplayName(tableName, connection);
			arguments[0] = dispTableName;
			columnName = columnNames.toString();
			columnName = columnName.substring(0, columnName.length());
			arguments[1] = columnName;
			logger.debug("Column Name: " + columnNames.toString());

			// Insert Table_Name and Column_Name in  CONSTRAINT_VOILATION_ERROR message   
			formattedErrMsg = MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR, arguments);
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


	public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException, UserNotAuthorizedException
	{
		//default imp	
	}



	
		
}
