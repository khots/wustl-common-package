package edu.wustl.common.util.global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database utility class.
 * @author ravi_kumar
 *
 */
public class DatabaseUtility
{
	/**
	 * The Name of the server for the database. For example : localhost
	 */
	private String dbServerName;

	/**
	 * The Port number of the server for the database.
	 */
	private String dbServerPortNumber;

	/**
	 * The Type of Database. Use one of the two values 'MySQL', 'Oracle'.
	 */
	private String dbType;

	/**
	 * Name of the Database.
	 */
	private String dbName;

	/**
	 * Database User name.
	 */
	private String dbUserName;

	/**
	 * Database Password.
	 */
	private String dbPassword;

	/**
	 * The database Driver.
	 */
	private String dbDriver;

	/**
	 * @return the dbServerName
	 */
	public String getDbServerNname()
	{
		return dbServerName;
	}

	/**
	 * @param dbServerName the dbServerName to set
	 */
	public void setDbServerNname(String dbServerName)
	{
		this.dbServerName = dbServerName;
	}

	/**
	 * @return the dbServerPortNumber
	 */
	public String getDbServerPortNumber()
	{
		return dbServerPortNumber;
	}

	/**
	 * @param dbServerPortNumber the dbServerPortNumber to set
	 */
	public void setDbServerPortNumber(String dbServerPortNumber)
	{
		this.dbServerPortNumber = dbServerPortNumber;
	}

	/**
	 * @return the dbType
	 */
	public String getDbType()
	{
		return dbType;
	}

	/**
	 * @param dbType the dbType to set
	 */
	public void setDbType(String dbType)
	{
		this.dbType = dbType;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName()
	{
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	/**
	 * @return the dbUserName
	 */
	public String getDbUserName()
	{
		return dbUserName;
	}

	/**
	 * @param dbUserName the dbUserName to set
	 */
	public void setDbUserName(String dbUserName)
	{
		this.dbUserName = dbUserName;
	}

	/**
	 * @return the dbPassword
	 */
	public String getDbPassword()
	{
		return dbPassword;
	}

	/**
	 * @param dbPassword the dbPassword to set
	 */
	public void setDbPassword(String dbPassword)
	{
		this.dbPassword = dbPassword;
	}

	/**
	 * @return the dbDriver
	 */
	public String getDbDriver()
	{
		return dbDriver;
	}

	/**
	 * @param dbDriver the dbDriver to set
	 */
	public void setDbDriver(String dbDriver)
	{
		this.dbDriver = dbDriver;
	}

	/**
	 * This method will create a database connection using configuration info.
	 * @return Connection : Database connection object
	 * @throws ClassNotFoundException if driver class not found
	 * @throws SQLException generic SQL exception
	 */
	public Connection getConnection() throws ClassNotFoundException, SQLException
	{
		Connection connection = null;
		Class.forName(dbDriver);
		String url = TextConstants.EMPTY_STRING;
		if (Constants.MYSQL_DATABASE.equalsIgnoreCase(dbType))
		{
			url = "jdbc:mysql://" + dbServerName + ":" + dbServerPortNumber + "/"
					+ dbName;
		}
		if (Constants.ORACLE_DATABASE.equalsIgnoreCase(dbType))
		{
			url = "jdbc:oracle:thin:@" + dbServerName + ":" + dbServerPortNumber
					+ ":" + dbName;
		}
		connection = DriverManager.getConnection(url, dbUserName, dbPassword);
		return connection;
	}

}
