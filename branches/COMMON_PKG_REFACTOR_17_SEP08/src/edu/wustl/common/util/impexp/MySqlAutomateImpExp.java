package edu.wustl.common.util.impexp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;

/**
 * This class is for import/export meta data for MySql.
 * @author ravi_kumar
 *
 */
public class MySqlAutomateImpExp extends AbstractAutomateImpExp
{
	/**
	 * Method to export meta data.
	 * @param args String array of parameters.
	 * @throws ApplicationException Application Exception
	 */
	public void executeExport(String[] args) throws ApplicationException
	{
		preImpExp(args);
		for (int i = 0; i < getSize(); i++)
		{
			String dumpFilePath = getFilePath() + getTableNamesList().get(i) + ".csv";
			try
			{
				exportForMySQLAndMsSql(dumpFilePath, getTableNamesList().get(i));
			}
			catch (SQLException exception)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("impexp.mysqlexport.error");
				throw new ApplicationException(errorKey,exception,"MySqlAutomateImpExp");
			}
			catch (ClassNotFoundException exception)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("impexp.mysqlexport.error");
				throw new ApplicationException(errorKey,exception,"MySqlAutomateImpExp");
			}
		}
	}
	/**
	 * Method to import meta data.
	 * @param args String array of parameters.
	 * @throws ApplicationException Application Exception
	 */
	public void executeImport(String[] args) throws ApplicationException
	{
		preImpExp(args);
		Connection conn=null;
		Statement stmt=null;
		try
		{
			conn=getConnection();
			stmt = conn.createStatement();
			stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
			String dumpFilePath;
			for (int i = 0; i < getSize(); i++)
			{
				dumpFilePath = getFilePath() + getTableNamesList().get(i) + ".csv";
				importDataMySQL(conn,dumpFilePath, getTableNamesList().get(i));
			}
			stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
		}
		catch(Exception exception)
		{
			ErrorKey errorKey=ErrorKey.getErrorKey("impexp.mysqlimport.error");
			throw new ApplicationException(errorKey,exception,"MySqlAutomateImpExp");
		}
		finally
		{
			try
			{
				conn.close();
				stmt.close();
			}
			catch (SQLException exception)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("impexp.mysqlimport.error");
				throw new ApplicationException(errorKey,exception,"MySqlAutomateImpExp");
			}
		}

	}
	/**
	 * Method to get database connection.
	 * @throws SQLException Generic SQL exception.
	 * @throws ClassNotFoundException throws this exception if Driver class not found in class path.
	 * @return MYSQL database connection
	 */
	public Connection getConnection() throws SQLException, ClassNotFoundException
	{
		DatabaseUtility dbUtil=getDbUtility();
		Class.forName(dbUtil.getDbDriver());
		String url = new StringBuffer("jdbc:mysql://").append(dbUtil.getDbServerNname())
						.append(":").append(dbUtil.getDbServerPortNumber())
						.append("/").append(dbUtil.getDbName()).toString();
		Connection connection= DriverManager.getConnection(url, dbUtil.getDbUserName()
						,dbUtil.getDbPassword());
		return connection;
	}

	/**
	 *  This method will insert the data to database.
	 * @param conn Connection object
	 * @param filename File Name
	 * @param tableName Table Name
	 * @throws SQLException generic SQL exception
	 */
	private void importDataMySQL(Connection conn, String filename, String tableName)
			throws SQLException
	{
		Statement stmt=null;
		try
		{
			stmt = conn.createStatement
				(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			StringBuffer query= new StringBuffer("LOAD DATA LOCAL INFILE '")
			.append(filename).append("' INTO TABLE ").append(tableName)
			.append(" FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\\n';");
			stmt.execute(query.toString());
		}
		finally
		{
			stmt.close();
		}
	}
}