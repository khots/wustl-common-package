package edu.wustl.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatterFactory;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;


public class OracleDAOImpl extends JDBCDAOImpl
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(OracleDAOImpl.class);

	/**
	 * Deletes the specified table
	 * @param tableName
	 * @throws DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("select tname from tab where tname='" + tableName + "'");
		
		try
		{
			Statement statement = getConnectionStmt();
			ResultSet rs = statement.executeQuery(query.toString());
			boolean isTableExists = rs.next();
			logger.debug("ORACLE****" + query.toString() + isTableExists);
			if (isTableExists)
			{
				logger.debug("Drop Table");
				executeUpdate("DROP TABLE " + tableName + " cascade constraints");
			}
			rs.close();
			statement.close();
		}
		catch (Exception sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
		}
		
	}
	
		
		
	public String getDatePattern()
	{
		String datePattern = "mm-dd-yyyy";
		return datePattern;
	}
	
	public String getTimePattern()
	{
		String timePattern = "hh-mi-ss";
		return timePattern;
	}
	public String getDateFormatFunction()
	{
		String dateFormatFunction = "TO_CHAR";
		return dateFormatFunction;
	}
	public String getTimeFormatFunction()
	{
		String timeFormatFunction = "TO_CHAR";
		return timeFormatFunction;
	}
	
	public String getDateTostrFunction()
	{
		String timeFormatFunction = "TO_CHAR";
		return timeFormatFunction;
	}
	
	public String getStrTodateFunction()
	{
		String timeFormatFunction = "TO_DATE";
		return timeFormatFunction;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.JDBCDAO#insert(java.lang.String, java.util.List)
	 */
	public void insert(String tableName, List columnValues) throws DAOException, SQLException
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
	public void insert(String tableName, List columnValues, List<String>... columnNames) throws DAOException, SQLException
	{
		List dateColumns = new ArrayList();
		List numberColumns = new ArrayList();
		List tinyIntColumns = new ArrayList();
		List columnNames_t = getColumns(tableName, columnValues,
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
			sqlExp.printStackTrace();
			throw new DAOException(sqlExp.getMessage(), sqlExp);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException ex)
			{
				throw new DAOException(ex.getMessage(), ex);
			}
		}
	}
		
	private Timestamp isColumnValueDate(Object value)
	{
	
		try
		{
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			formatter.setLenient(false);
			java.util.Date date = formatter.parse((String) value);
			Timestamp t = new Timestamp(date.getTime());
			if (value != null && value.toString().equals("") == false)
			{
				return t;
			}
		}
		catch (Exception e)
		{

		}
		return null;
	}
	
	
	public String formatMessage(Exception excp, Object[] args)
	{
		String tableName = "";
		String columnName = "";
		String formattedErrMsg = null; // Formatted Error Message return by this method
		Exception objExcp = excp;
		if (excp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
		{

			objExcp = (Exception) objExcp.getCause();
			logger.debug(objExcp);
		}
		try
		{
			//        	 Get database metadata object for the connection
			Connection connection = null;
			if (args[1] != null)
			{
				connection = (Connection) args[1];
			}
			else
			{
				logger.debug("Error Message: Connection object not given");
			}
			// Get Contraint Name from messages         		
			String sqlMessage = ConstraintViolationFormatter.generateErrorMessage(objExcp);
			int tempstartIndexofMsg = sqlMessage.indexOf("(");

			String temp = sqlMessage.substring(tempstartIndexofMsg);
			int startIndexofMsg = temp.indexOf(".");
			int endIndexofMsg = temp.indexOf(")");
			String strKey = temp.substring((startIndexofMsg + 1), endIndexofMsg);
			logger.debug("Contraint Name: " + strKey);

			String Query = "select COLUMN_NAME,TABLE_NAME from user_cons_columns where constraint_name = '"
					+ strKey + "'";
			logger.debug("ExceptionFormatter Query: " + Query);
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(Query);
			while (rs.next())
			{
				columnName += rs.getString("COLUMN_NAME") + ",";
				logger.debug("columnName: " + columnName);
				tableName = rs.getString("TABLE_NAME");
				logger.debug("tableName: " + tableName);
			}
			if (columnName.length() > 0 && tableName.length() > 0)
			{
				columnName = columnName.substring(0, columnName.length() - 1);
				logger.debug("columnName befor formatting: " + columnName);
				String displayName = ExceptionFormatterFactory
						.getDisplayName(tableName, connection);

				Object[] arguments = new Object[]{displayName, columnName};
				formattedErrMsg = MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR,
						arguments);
			}
			rs.close();
			statement.close();
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
