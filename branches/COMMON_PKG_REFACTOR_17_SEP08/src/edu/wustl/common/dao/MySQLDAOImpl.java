package edu.wustl.common.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatterFactory;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
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
				/**
				 * For Number -1 is used as MarkUp data For Date 1-1-9999 is used as markUp data.
				 * Please refer bug 3576
				 */

				if (obj != null && dateColumns.contains(new Integer(i + 1)) && obj.toString().equals("##"))
				{
					java.util.Date date = null;
					try
					{
						date = Utility.parseDate("1-1-9999", "mm-dd-yyyy");
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
					Date sqlDate = new Date(date.getTime());
					stmt.setDate(i + 1, sqlDate);
				}
				else if (tinyIntColumns.contains(new Integer(i + 1)))
				{
					if (obj != null && (obj.equals("true") || obj.equals("TRUE") || obj.equals("1")))
					{
						stmt.setObject(i + 1, 1);
					}
					else
					{
						stmt.setObject(i + 1, 0);
					}
				}
				else
				{
					Timestamp date = isColumnValueDate(obj);
					if (date != null)
					{
						stmt.setObject(i + 1, date);
						//Logger.out.debug("t.toString(): " + "---" + date);
					}
					else
					{
						if (obj != null && numberColumns.contains(new Integer(i + 1)) && obj.toString().equals("##"))
						{
							stmt.setObject(i + 1, new Integer(-1));
						}
						else
						{
							stmt.setObject(i + 1, obj);
						}
					}
				}
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
	
	private List getColumns(String tableName, List columnValues, List dateColumns,
			List numberColumns,List tinyIntColumns,List<String>... columnNames) throws SQLException
	{
		StringBuffer sql = new StringBuffer("Select ");
		Statement statement = getConnectionStmt();
		ResultSet resultSet = null;
		ResultSetMetaData metaData = null;
		
		List<String> columnNames_t;
		if (columnNames != null && columnNames.length > 0)
		{
			columnNames_t = columnNames[0];
			for (int i = 0; i < columnNames_t.size(); i++)
			{
				sql.append(columnNames_t.get(i));
				if (i != columnNames_t.size() - 1)
				{
					sql.append(",");
				}
			}
			sql.append(" from " + tableName + " where 1!=1");
			resultSet = statement.executeQuery(sql.toString());
			metaData = resultSet.getMetaData();

		}
		else
		{
			sql.append("* from " + tableName + " where 1!=1");
			resultSet = statement.executeQuery(sql.toString());
			metaData = resultSet.getMetaData();

			columnNames_t = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++)
			{
				columnNames_t.add(metaData.getColumnName(i));
			}
		}

		for (int i = 1; i <= metaData.getColumnCount(); i++)
		{
			String type = metaData.getColumnTypeName(i);
			if (type.equals("DATE"))
				dateColumns.add(new Integer(i));
			if (type.equals("NUMBER"))
				numberColumns.add(new Integer(i));
			if (type.equals("TINYINT"))
				tinyIntColumns.add(new Integer(i));

		}

		resultSet.close();
		statement.close();
		return columnNames_t;
	}
	
	private String createInsertQuery(String tableName,List columnNames_t,List columnValues)
	{
		StringBuffer query = new StringBuffer("INSERT INTO " + tableName + "(");

		Iterator<String> columnIterator = columnNames_t.iterator();
		while (columnIterator.hasNext())
		{
			query.append(columnIterator.next());
			if (columnIterator.hasNext())
			{
				query.append(",");
			}
			else
			{
				query.append(") values(");
			}
		}

		//StringBuffer query = new StringBuffer("INSERT INTO " + tableName + " values(");
		//Changed implementation with column names


		Iterator it = columnValues.iterator();
		while (it.hasNext())
		{
			it.next();
			query.append("?");

			if (it.hasNext())
				query.append(",");
			else
				query.append(")");
		}
		return query.toString();
	}

	
	private Timestamp isColumnValueDate(Object value)
	{
		//Logger.out.debug("Column value: " + value);
		try
		{
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			formatter.setLenient(false);
			java.util.Date date = formatter.parse((String) value);
			Timestamp t = new Timestamp(date.getTime());
			// Date sqlDate = new Date(date.getTime());

			//Logger.out.debug("Column date value: " + date);
			if (value != null && value.toString().equals("") == false)
			{
				//Logger.out.debug("Return true: " + value);
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

		if (args[0] != null)
		{
			tableName = (String) args[0];
		}
		else
		{
			logger.debug("Table Name not specified");
			tableName = "Unknown Table";
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
