/*
 * TODO
 */

package edu.wustl.common.exceptionformatter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.dao.util.HibernateMetaDataFactory;

/**
 * @author kalpana_thakur
 *
 */
public class MysqlExceptionFormatter implements IDBExceptionFormatter
{

	/**
	* LOGGER Logger - Generic LOGGER.
	*/
	private static final Logger LOGGER = Logger.getCommonLogger(MysqlExceptionFormatter.class);
	/**
	 * Index name.
	 */
	private static final String INDEX_NAME = "INDEX_NAME";

	/**
	 * This will generate the formatted error messages.
	 * @param objExp :Exception.
	 * @param jdbcDAO : jdbcDAO.
	 * @return the formated messages.
	 */
	public String getFormatedMessage(Exception objExp, JDBCDAO jdbcDAO)
	{

		Exception objExcp = objExp;
		if (objExcp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
		{
			objExcp = (Exception) objExcp.getCause();
		}

		String dispTableName = "";
		String tableName = ""; // stores Table_Name for which column name to be found
		String columnNames = ""; //stores Column_Name of table
		String formattedErrMsg = ""; // Formatted Error Message return by this method

		try
		{
			tableName = getTableName(objExcp);
			columnNames = getColumnNames(objExcp, tableName, jdbcDAO);

			// Create arrays of object containing data to insert in CONSTRAINT_VOILATION_ERROR
			Object[] arguments = new Object[2];
			dispTableName = Utility.getDisplayName(tableName, jdbcDAO);
			arguments[0] = dispTableName;
			columnNames = columnNames.substring(0, columnNames.length());
			arguments[1] = columnNames;

			// Insert Table_Name and Column_Name in  CONSTRAINT_VOILATION_ERROR message
			formattedErrMsg = MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR, arguments);
		}
		catch (Exception e)
		{
			LOGGER.debug(e.getMessage(), e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;
		}
		return formattedErrMsg;

	}

	/**
	 *For the key extracted from the string, get the column name on which the
	  constraint has failed.It get a description of the given table's indices and statistics.
	  In this loop, all the indexes are stored as key of the HashMap
	  and the column names are stored as value.
	 * @param objExcp :
	 * @param tableName :
	 * @param jdbcDAO :
	 * @return columnNames :
	 * @throws DAOException :
	 * @throws SQLException :
	 */
	private String getColumnNames(Exception objExcp, String tableName, JDBCDAO jdbcDAO)
			throws DAOException, SQLException
	{
		String columnNames = "";
		ResultSet resultSet = null;
		try
		{
			int key = getErrorKey(objExcp);
			resultSet = jdbcDAO.getDBMetaDataResultSet(tableName);
			columnNames = getColumnNames(resultSet, key);
		}
		catch (SQLException sqlExp)
		{
			LOGGER.debug(sqlExp.getMessage(), sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey, sqlExp, "MysqlFormattedErrorMessages.java :");
		}
		finally
		{
			if (resultSet != null)
			{
				resultSet.close();
			}

		}
		return columnNames;
	}

	/**
	 * @param resultSet :
	 * @param key :
	 * @return :
	 * @throws SQLException :
	 */
	private String getColumnNames(ResultSet resultSet, int key) throws SQLException
	{
		StringBuffer columnNames = new StringBuffer("");
		boolean found = false;

		HashMap<String, StringBuffer> indexDetails = new HashMap<String, StringBuffer>();

		int indexCount = 1;
		String constraintVoilated = "";
		while (resultSet.next())
		{

			if (key == indexCount)
			{
				constraintVoilated = resultSet.getString(INDEX_NAME);
				found = true; // column name for given key index found
				//break;
			}
			updateIndexDetailsMap(resultSet, indexDetails);
			indexCount++; // increment record count*/
		}

		if (found)
		{
			columnNames = (StringBuffer) indexDetails.get(constraintVoilated);
		}
		return columnNames.toString();
	}

	/**
	 * @param resultSet :Result set
	 * @param indexDetails : Map holding details of indexes.
	 * @throws SQLException :Exception.
	 */
	private void updateIndexDetailsMap(ResultSet resultSet, Map<String, StringBuffer> indexDetails)
			throws SQLException
	{
		StringBuffer temp = (StringBuffer) indexDetails.get(resultSet.getString(INDEX_NAME));
		if (temp == null)
		{
			temp = new StringBuffer(resultSet.getString("COLUMN_NAME"));
			temp.append(DAOConstants.SPLIT_OPERATOR);
			indexDetails.put(resultSet.getString(INDEX_NAME), temp);

		}
		else
		{
			temp.append(resultSet.getString("COLUMN_NAME"));
			temp.append(DAOConstants.SPLIT_OPERATOR);
			indexDetails.remove(resultSet.getString(INDEX_NAME));
			indexDetails.put(resultSet.getString(INDEX_NAME), temp);
		}
	}

	/**
	 * @param objExcp :
	 * @return :
	 */
	private int getErrorKey(Exception objExcp)
	{
		// Generate Error Message by appending all messages of previous cause Exceptions
		String sqlMessage = Utility.generateErrorMessage(objExcp);

		// From the MySQL error message and extract the key ID
		// The unique key Error message is "Duplicate entry %s for key %d"

		int key = -1;
		int indexofMsg = 0;
		indexofMsg = sqlMessage.indexOf(Constants.MYSQL_DUPL_KEY_MSG);
		indexofMsg += Constants.MYSQL_DUPL_KEY_MSG.length();

		// Get the %d part of the string
		String strKey = sqlMessage.substring(indexofMsg, sqlMessage.length() - 1);
		key = Integer.parseInt(strKey);
		return key;
	}

	/**
	 * @param objExcp :
	 * @return :
	 * @throws ClassNotFoundException :
	 */
	private String getTableName(Exception objExcp) throws ClassNotFoundException
	{
		String tableName;
		//get Class name from message "could not insert [class name]"
		ConstraintViolationException cEX = (ConstraintViolationException) objExcp;
		String message = objExcp.getMessage();

		int startIndex = message.indexOf('[');
		int endIndex = message.indexOf('#');
		if (endIndex == -1)
		{
			endIndex = message.indexOf(']');
		}
		String className = message.substring((startIndex + 1), endIndex);
		Class classObj = Class.forName(className);
		// get table name from class
		String appName = CommonServiceLocator.getInstance().getAppName();
		HibernateMetaData hibernateMetaData = HibernateMetaDataFactory
				.getHibernateMetaData(appName);
		if (hibernateMetaData != null)
		{
			tableName = hibernateMetaData.getRootTableName(classObj);
			if (!(cEX.getSQL().contains(tableName)))
			{
				tableName = hibernateMetaData.getTableName(classObj);
			}
		}
		else
		{
			tableName = "";
		}

		return tableName;
	}

}
