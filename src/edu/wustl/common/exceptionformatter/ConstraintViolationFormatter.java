
package edu.wustl.common.exceptionformatter;

/**
 * 
 * @author sachin_lale
 * Description: Class implementing Exception_Formatter interface method for Constarint_Violation_Exception  
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;

import edu.wustl.common.dao.DAOConfigFactory;
import edu.wustl.common.dao.IDAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

public class ConstraintViolationFormatter implements ExceptionFormatter
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(ConstraintViolationFormatter.class);
	/*  
	 * @param objExcp - Exception object 
	 * @param args[] where args[0] must be String 'Table_Name' 
	 * and args[1] must be java.sql.Connection object get from session.getCOnnection().   
	 */

	public String formatMessage(Exception objExcp, Object[] args)
	{
		String errMessage = null;
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO dao = daofactory.getJDBCDAO();
	
		errMessage = dao.formatMessage(objExcp, args);
		
		if (errMessage == null)
		{
			errMessage = " Database in used not specified " + Constants.GENERIC_DATABASE_ERROR;
		}
		
		return errMessage;
	}

	
	public static String generateErrorMessage(Exception ex)
	{
		String messageToAdd = "";
		if (ex instanceof HibernateException)
		{
			HibernateException hibernateException = (HibernateException) ex;
			StringBuffer message = new StringBuffer(messageToAdd);
			String str[] = hibernateException.getMessages();
			if (message != null)
			{
				for (int i = 0; i < str.length; i++)
				{
					logger.debug("str:" + str[i]);
					message.append(str[i] + " ");
				}
			}
			else
			{
				return "Unknown Error";
			}
			return message.toString();
		}
		else
		{
			return ex.getMessage();
		}
	}
	
	public static String getTableNameFromMessage(String nameOfTable,Exception objExcp) throws ClassNotFoundException
	{
		String tableName = nameOfTable;
		ConstraintViolationException cEX = (ConstraintViolationException) objExcp;
		String message = cEX.getMessage();
		logger.debug("message :" + message);
		int startIndex = message.indexOf("[");
		int endIndex = message.indexOf("#");
		if (endIndex == -1)
		{
			endIndex = message.indexOf("]");
		}
		String className = message.substring((startIndex + 1), endIndex);
		logger.debug("ClassName: " + className);
		Class classObj = Class.forName(className);
		// get table name from class 
		tableName = HibernateMetaData.getRootTableName(classObj);
		if (!(cEX.getSQL().contains(tableName)))
		{
			tableName = HibernateMetaData.getTableName(classObj);
		}
		return tableName;
	}
	
	public static StringBuffer getColumnInfo(ResultSet rs,int key) throws SQLException
	{
		HashMap indexDetails = new HashMap();
		StringBuffer columnNames = new StringBuffer("");
		int indexCount = 1;
		String constraintVoilated = "";
		boolean found = false;
		
		while (rs.next())
		{
			// In this loop, all the indexes are stored as key of the HashMap
			// and the column names are stored as value.
			logger.debug("Key: " + indexCount);
			if (key == indexCount)
			{
				constraintVoilated = rs.getString("INDEX_NAME");
				logger.debug("Constraint: " + constraintVoilated);
				found = true; // column name for given key index found
				//break;
			}
			StringBuffer temp = (StringBuffer) indexDetails.get(rs.getString("INDEX_NAME"));
			if (temp != null)
			{
				temp.append(rs.getString("COLUMN_NAME"));
				temp.append(",");
				indexDetails.remove(rs.getString("INDEX_NAME"));
				indexDetails.put(rs.getString("INDEX_NAME"), temp);
				logger.debug("Column :" + temp.toString());
			}
			else
			{
				temp = new StringBuffer(rs.getString("COLUMN_NAME"));
				//temp.append(rs.getString("COLUMN_NAME"));
				temp.append(",");
				indexDetails.put(rs.getString("INDEX_NAME"), temp);
			}

			indexCount++; // increment record count*/
		}
		logger.debug("out of loop");
		if (found)
		{
			columnNames = (StringBuffer) indexDetails.get(constraintVoilated);
			logger.debug("Column Name: " + columnNames.toString());
			logger.debug("Constraint: " + constraintVoilated);
		}
		return columnNames;
	}
	
	
	public static Object[] createMessageFormatArguments(String tableName,
			Connection connection, StringBuffer columnNames) {
		String dispTableName;
		String columnName;
		// Create arrays of object containing data to insert in CONSTRAINT_VOILATION_ERROR
		Object[] arguments = new Object[2];
		dispTableName = ExceptionFormatterFactory.getDisplayName(tableName, connection);
		arguments[0] = dispTableName;
		columnName = columnNames.toString();
		columnName = columnName.substring(0, columnName.length());
		arguments[1] = columnName;
		logger.debug("Column Name: " + columnNames.toString());
		return arguments;
	}

	public static StringBuffer getColumNames(String tableName, Connection connection,
			int key) throws SQLException {
		// Get database metadata object for the connection
		DatabaseMetaData dbmd = connection.getMetaData();

		//  Get a description of the given table's indices and statistics
		ResultSet resultSet = dbmd.getIndexInfo(connection.getCatalog(), null, tableName, true, false);
		StringBuffer columnNames = getColumnInfo(resultSet,key);
		resultSet.close();
		return columnNames;
	}

	public static Connection getConnection(Object[] args, Connection connection) {
		if (args[1] != null)
		{
			connection = (Connection) args[1];
		}
		else
		{
			logger.debug("Error Message: Connection object not given");
		}
		return connection;
	}

	public static String setDisplayTableName(Object[] args, String tableName) {
		String dispTableName;
		if (args[2] != null)
		{
			dispTableName = (String) args[2];
		}
		else
		{
			logger.debug("Table Name not specified");
			dispTableName = tableName;
		}
		return dispTableName;
	}

	public static String getTableName(Object[] args) {
		String tableName;
		if (args[0] == null) {
			logger.debug("Table Name not specified");
			tableName = "Unknown Table";
			
		} else {
			tableName = (String) args[0];
		}
		return tableName;
	}
	
	
	public static String getFormattedErrorMessage(Object[] args, Exception objExcp,
			String tableName)
			throws ClassNotFoundException, SQLException {
		String formattedErrMsg;
		Connection connection = null;
		//get Class name from message "could not insert [classname]"
		tableName = getTableNameFromMessage(tableName,objExcp);
		// Generate Error Message by appending all messages of previous cause Exceptions
		String sqlMessage = generateErrorMessage(objExcp);
		// From the MySQL error msg and extract the key ID 
		// The unique key voilation message is "Duplicate entry %s for key %d"

		int key = -1;
		int indexofMsg = 0;
		indexofMsg = sqlMessage.indexOf(Constants.MYSQL_DUPL_KEY_MSG);
		indexofMsg += Constants.MYSQL_DUPL_KEY_MSG.length();

		// Get the %d part of the string
		String strKey = sqlMessage.substring(indexofMsg, sqlMessage.length() - 1);
		key = Integer.parseInt(strKey);
		// For the key extracted frm the string, get the column name on which the 
		// costraint has failed get connection from arguments
		connection = getConnection(args, connection);
		StringBuffer columnNames = getColumNames(tableName, connection, key);
		Object[] arguments = createMessageFormatArguments(tableName,
				connection, columnNames);

		// Insert Table_Name and Column_Name in  CONSTRAINT_VOILATION_ERROR message   
		formattedErrMsg = MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR, arguments);
		return formattedErrMsg;
	}
	
	public static String getFormatedErrorMessageForOracle(Object[] args,Exception objExcp)
			throws SQLException {
		
		String formattedErrMsg = "" ;
		String tableName = "" ; 
		String columnName = "" ;
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
		String sqlMessage = generateErrorMessage(objExcp);
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
		
		return formattedErrMsg;
	}
	
	
	
	
}
