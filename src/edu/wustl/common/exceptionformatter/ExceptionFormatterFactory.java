
package edu.wustl.common.exceptionformatter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import edu.wustl.common.util.logger.Logger;

/**
 * @author sachin_lale
 * Description: The Factory class to instatiate ExceptionFormatter object of given Exception.
 */
public class ExceptionFormatterFactory
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(ExceptionFormatterFactory.class);

	/**
	 * Specify ResourceBundle property.
	 */
	private static ResourceBundle prop;
	static
	{
		try
		{
			/* Load ExceptionFormatter.properties file
			 * property file format is as follows:
			 * Exception_Class_Name = Exception_Formatter_Class_Name
			 */
			prop = ResourceBundle.getBundle("ExceptionFormatter");

			logger.debug("File Loaded");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage() + " " + e);
		}
	}

	/**
	 *  @param excp Exception excp : The fully qualified class name of excp
	 *  and the Exception_Formatter class name should be in ExceptionFormatter.properties file
	 *  @return Formatter.
	 */
	public static ExceptionFormatter getFormatter(Exception excp)
	{
		ExceptionFormatter expFormatter = null;
		try
		{
			//Get Excxeption Class name from given Object
			String excpClassName = excp.getClass().getName();

			//Get Exception Formatter Class name from Properties file
			String formatterClassName = prop.getString(excpClassName);
			if (formatterClassName == null)
			{
				logger.error("ExceptionFormatter Class not found for " + excpClassName);
			}
			else
			{
				//	Instantiate a Exception Formatter
				logger.debug("exceptionClass: " + excpClassName);
				logger.debug("formatterClass: " + formatterClassName);
				expFormatter =
					(ExceptionFormatter) Class.forName(formatterClassName).newInstance();
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage() + " " + e);
		}
		return expFormatter;
	}

	/**
	 * This method gets Display Name.
	 * @param tableName table Name
	 * @param conn Connection
	 * @return Display Name.
	 */
	public static String getDisplayName(String tableName, Connection conn)
	{
		String displayName = "";
		String sql = "select DISPLAY_NAME from CATISSUE_QUERY_TABLE_DATA where TABLE_NAME='"
				+ tableName + "'";
		try
		{
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next())
			{
				displayName = resultSet.getString("DISPLAY_NAME");
				break;
			}
			resultSet.close();
			statement.close();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
		}
		return displayName;
	}
}
