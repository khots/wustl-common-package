package edu.wustl.common.util.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;

/**
 * This class contains some common functionality for import/export process.
 * @author ravi_kumar
 *
 */
public abstract class AbstractAutomateImpExp implements IAutomateImpExp
{

	/**
	 * Index for file which contains all table names.
	 */
	private static final int  INDX_FOR_TABLE_NAMES_FILE=8;

	/**
	 * Index for csv file name.
	 */
	private static final int  INDX_FOR_CSV_FILE_NAME=9;

	/**
	 * dbUtil object is used to store the argument values and getting database connection.
	 */
	private DatabaseUtility dbUtil;
	/**
	 * Number of tables.
	 */
	private int size;

	/**
	 * File path.
	 */
	private String filePath;

	/**
	 * list of table name.
	 */
	private List<String> tableNamesList;

	/**
	 * @param dbUtil :object of DatabaseUtility
	 * @throws ApplicationException Application Exception
	 */
	public void init(DatabaseUtility dbUtil) throws ApplicationException
	{
		this.dbUtil=dbUtil;
	}

	/**
	 * This method do some process before import/export operation.
	 * @param args String array of parameters.
	 * @throws ApplicationException Application Exception
	 */
	protected void preImpExp(String[] args)  throws ApplicationException
	{
		tableNamesList=getTableNamesList(args[INDX_FOR_TABLE_NAMES_FILE]);
		size = tableNamesList.size();
		filePath = args[INDX_FOR_CSV_FILE_NAME].replaceAll("\\\\", "//");
	}
	/**
	 * Common.
	 * This method will read the table list file.
	 * @param fileName file name
	 * @return table list
	 * @throws ApplicationException Generic IO exception
	 */
	private List<String> getTableNamesList(String fileName) throws ApplicationException
	{
		ArrayList<String> tableNamesList = new ArrayList<String>();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String linereader = reader.readLine();
			while (linereader != null)
			{
				tableNamesList.add(linereader);
				linereader = reader.readLine();
			}
		}
		catch(IOException exception)
		{
			ErrorKey errorKey=ErrorKey.getErrorKey("impexp.tablenamelist.error");
			throw new ApplicationException(errorKey,exception,"INot able to get import class");
		}
		return tableNamesList;
	}

	/**
	 *  This is common export method for mysql and mssql server database.
	 *  This method will export the data from database.
	 * @param fileName File Name
	 * @param tableName Table Name
	 * @throws SQLException Generic SQL exception.
	 * @throws ClassNotFoundException throws this exception if Driver class not found in class path.
	 */
	protected void exportForMySQLAndMsSql(String fileName, String tableName)
								throws SQLException,ClassNotFoundException
	{
		Connection conn=null;
		Statement stmt=null;
		try
		{
			conn= getConnection();
			File file = new File(fileName);
			if (file.exists())
			{
				file.delete();
			}
			stmt = conn.createStatement
			(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			StringBuffer query=new StringBuffer("SELECT * INTO OUTFILE '")
			.append(fileName)
			.append("' FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\\n' FROM ")
			.append(tableName).append(";");
			stmt.execute(query.toString());
		}
		finally
		{
			conn.close();
			stmt.close();
		}
	}

	/**
	 * @return the size
	 */
	public int getSize()
	{
		return size;
	}


	/**
	 * @param size the size to set
	 */
	public void setSize(int size)
	{
		this.size = size;
	}


	/**
	 * @return the filePath
	 */
	public String getFilePath()
	{
		return filePath;
	}


	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}


	/**
	 * @return the tableNamesList
	 */
	public List<String> getTableNamesList()
	{
		return tableNamesList;
	}


	/**
	 * @param tableNamesList the tableNamesList to set
	 */
	public void setTableNamesList(List<String> tableNamesList)
	{
		this.tableNamesList = tableNamesList;
	}

	/**
	 * @return the dbUtility
	 */
	public DatabaseUtility getDbUtility()
	{
		return dbUtil;
	}

	/**
	 * @param dbUtil the dbUtility to set
	 */
	public void setDbUtility(DatabaseUtility dbUtil)
	{
		this.dbUtil = dbUtil;
	}

}
