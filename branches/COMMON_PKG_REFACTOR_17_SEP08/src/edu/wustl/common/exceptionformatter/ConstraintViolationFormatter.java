
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

import edu.wustl.common.dao.DAO;
import edu.wustl.common.dao.DAOConfigFactory;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.IDAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
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
}
