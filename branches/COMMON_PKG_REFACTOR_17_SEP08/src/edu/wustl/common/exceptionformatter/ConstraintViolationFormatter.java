
package edu.wustl.common.exceptionformatter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;

import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;

/**
 @author sachin_lale
 * Description: Class implementing Exception_Formatter interface method for Constarint_Violation_Exception
 */
public class ConstraintViolationFormatter implements ExceptionFormatter
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(ConstraintViolationFormatter.class);

	/**
	 * @param objExcp - Exception object
	 * @param args where args[0] must be String 'Table_Name'
	 * and args[1] must be java.sql.Connection object get from session.getCOnnection().
	 * @return errMessage.
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

	/**
	 * This method generate Error Message.
	 * @param exception Exception.
	 * @return Error Message
	 */
	public static String generateErrorMessage(Exception exception)
	{
		String errMessage = TextConstants.EMPTY_STRING;
		if (exception instanceof HibernateException)
		{
			HibernateException hibernateException = (HibernateException) exception;
			StringBuffer message = new StringBuffer(TextConstants.EMPTY_STRING);
			String[] str = hibernateException.getMessages();
			for (int i = 0; i < str.length; i++)
			{
				logger.debug("str:" + str[i]);
				message.append(str[i]).append(' ');
			}
			errMessage = message.toString();

		}
		else
		{
			errMessage = exception.getMessage();
		}
		return errMessage;
	}

	/**
	 * This method get Table Name From Message.
	 * @param nameOfTable name Of Table.
	 * @param objExcp Exception
	 * @throws ClassNotFoundException Class Not Found Exception.
	 * @return Table Name From Message.
	 */
	public static String getTableNameFromMessage(String nameOfTable, Exception objExcp)
			throws ClassNotFoundException
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

	/**
	 * get Column Info.
	 * @param resultSet ResultSet
	 * @param key key.
	 * @return column Names.
	 * @throws SQLException SQL Exception.
	 */
	public static StringBuffer getColumnInfo(ResultSet resultSet, int key) throws SQLException
	{
		HashMap<String, StringBuffer> indexDetails = new HashMap<String, StringBuffer>();
		StringBuffer columnNames = new StringBuffer("");
		int indexCount = 1;
		String constraintVoilated = "";
		boolean found = false;

		while (resultSet.next())
		{
			// In this loop, all the indexes are stored as key of the HashMap
			// and the column names are stored as value.
			logger.debug("Key: " + indexCount);
			if (key == indexCount)
			{
				constraintVoilated = resultSet.getString("INDEX_NAME");
				logger.debug("Constraint: " + constraintVoilated);
				found = true; // column name for given key index found
				//break;
			}
			setIndexDetails(resultSet, indexDetails);
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

	/**
	 * set Index Details.
	 * @param resultSet ResultSet
	 * @param indexDetails HashMap.
	 * @throws SQLException SQL Exception.
	 */
	private static void setIndexDetails(ResultSet resultSet,
			HashMap<String, StringBuffer> indexDetails) throws SQLException
	{
		StringBuffer temp = (StringBuffer) indexDetails.get(resultSet.getString("INDEX_NAME"));
		if (temp == null)
		{
			temp = new StringBuffer(resultSet.getString("COLUMN_NAME"));
			temp.append(',');
			indexDetails.put(resultSet.getString("INDEX_NAME"), temp);
		}
		else
		{
			temp.append(resultSet.getString("COLUMN_NAME"));
			temp.append(',');
			indexDetails.remove(resultSet.getString("INDEX_NAME"));
			indexDetails.put(resultSet.getString("INDEX_NAME"), temp);
			logger.debug("Column :" + temp.toString());
		}
	}

	/**
	 * This method creates Message Format Arguments.
	 * @param tableName table Name
	 * @param connection Connection
	 * @param columnNames column Names.
	 * @return arguments.
	 */
	public static Object[] createMessageFormatArguments(String tableName, Connection connection,
			StringBuffer columnNames)
	{
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

	/**
	 * This method gets Colum Names.
	 * @param tableName table Name
	 * @param connection Connection
	 * @param key key
	 * @return column Names
	 * @throws SQLException SQL Exception.
	 */
	public static StringBuffer getColumNames(String tableName, Connection connection, int key)
			throws SQLException
	{
		// Get database metadata object for the connection
		DatabaseMetaData dbmd = connection.getMetaData();

		//  Get a description of the given table's indices and statistics
		ResultSet resultSet = dbmd.getIndexInfo(connection.getCatalog(), null, tableName, true,
				false);
		StringBuffer columnNames = getColumnInfo(resultSet, key);
		resultSet.close();
		return columnNames;
	}

	/**
	 * This method gets Connection.
	 * @param args arguments
	 * @param connection Connection.
	 * @return Connection
	 */
	public static Connection getConnection(Object[] args, Connection connection)
	{
		Connection conn = connection;
		if (args[1] == null)
		{
			logger.debug("Error Message: Connection object not given");
		}
		else
		{
			conn = (Connection) args[1];
		}
		return conn;
	}

	/**
	 * This method sets Display Table Name.
	 * @param args arguments
	 * @param tableName table Name
	 * @return display Table Name.
	 */
	public static String setDisplayTableName(Object[] args, String tableName)
	{
		String dispTableName;
		if (args[2] == null)
		{
			logger.debug("Table Name not specified");
			dispTableName = tableName;
		}
		else
		{
			dispTableName = (String) args[2];
		}
		return dispTableName;
	}

	/**
	 * This method gets Table Name.
	 * @param args arguments
	 * @return Table Name.
	 */
	public static String getTableName(Object[] args)
	{
		String tableName;
		if (args[0] == null)
		{
			logger.debug("Table Name not specified");
			tableName = "Unknown Table";

		}
		else
		{
			tableName = (String) args[0];
		}
		return tableName;
	}

	/**
	 * This method gets Formatted Error Message.
	 * @param args arguments
	 * @param objExcp Exception
	 * @param tableName table Name
	 * @return Formatted Error Message.
	 * @throws ClassNotFoundException Class Not Found Exception
	 * @throws SQLException SQL Exception.
	 */
	public static String getFormattedErrorMessage(Object[] args, Exception objExcp, String tableName)
			throws ClassNotFoundException, SQLException
	{
		String formattedErrMsg;
		Connection connection = null;
		//get Class name from message "could not insert [classname]"
		String tabName = getTableNameFromMessage(tableName, objExcp);
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
		StringBuffer columnNames = getColumNames(tabName, connection, key);
		Object[] arguments = createMessageFormatArguments(tabName, connection, columnNames);

		// Insert Table_Name and Column_Name in  CONSTRAINT_VOILATION_ERROR message.
		formattedErrMsg = MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR, arguments);
		return formattedErrMsg;
	}

	/**
	 * This method gets Formated Error Message For Oracle.
	 * @param args arguments
	 * @param objExcp Exception
	 * @return Formated Error Message For Oracle
	 * @throws SQLException SQL Exception
	 */
	public static String getFormatedErrorMessageForOracle(Object[] args, Exception objExcp)
			throws SQLException
	{
		StringBuffer formattedErrMsg = new StringBuffer();
		String tableName = "";
		StringBuffer columnName = new StringBuffer();
		//Get database metadata object for the connection
		Connection connection = getConnection(args);
		// Get Contraint Name from messages.
		String sqlMessage = generateErrorMessage(objExcp);
		int tempstartIndexofMsg = sqlMessage.indexOf("(");

		String temp = sqlMessage.substring(tempstartIndexofMsg);
		int startIndexofMsg = temp.indexOf(".");
		int endIndexofMsg = temp.indexOf(")");
		String strKey = temp.substring((startIndexofMsg + 1), endIndexofMsg);
		logger.debug("Contraint Name: " + strKey);

		String query = "select COLUMN_NAME,TABLE_NAME from user_cons_columns where constraint_name = '"
				+ strKey + "'";
		logger.debug("ExceptionFormatter Query: " + query);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next())
		{
			columnName.append(resultSet.getString("COLUMN_NAME")).append(',');
			logger.debug("columnName: " + columnName);
			tableName = resultSet.getString("TABLE_NAME");
			logger.debug("tableName: " + tableName);
		}
		formattedErrMsg = formatMessage(formattedErrMsg, tableName, columnName.toString(), connection);
		resultSet.close();
		statement.close();

		return formattedErrMsg.toString();
	}

	/**
	 * @param formattedErrMsg formatted Error Message.
	 * @param tableName table Name
	 * @param columnName column Name
	 * @param connection connection
	 * @return formatted Error Message
	 */
	private static StringBuffer formatMessage(StringBuffer formattedErrMsg, String tableName,
			String columnName, Connection connection)
	{
		String colName = columnName;
		if (colName.length() > 0 && tableName.length() > 0)
		{
			colName = colName.substring(0, colName.length() - 1);
			logger.debug("columnName befor formatting: " + colName);
			String displayName = ExceptionFormatterFactory.getDisplayName(tableName, connection);

			Object[] arguments = new Object[]{displayName, colName};
			formattedErrMsg.append(MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR,
					arguments));
		}
		return formattedErrMsg;
	}

	/**
	 * get Connection.
	 * @param args arguments
	 * @return Connection
	 */
	private static Connection getConnection(Object[] args)
	{
		Connection connection = null;
		if (args[1] == null)
		{
			logger.debug("Error Message: Connection object not given");
		}
		else
		{
			connection = (Connection) args[1];
		}
		return connection;
	}

}
