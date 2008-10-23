
package edu.wustl.common.util.global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is for import/export data to database.
 * @author abhishek_mehta
 *
 */
public class AutomateImport
{
	/**
	 * dbUtility object is used to store the argument values and getting database connection.
	 */
	private static DatabaseUtility dbUtility= new DatabaseUtility();

	/**
	 * Minimum number of arguments required.
	 */
	private static final int MIN_NO_OF_ARGS=10;

	/**
	 * oracle tns name.
	 */
	private static String oracleTnsName;

	/**
	 * Index for CA model operation like import or export.
	 */
	private static final int  INDX_FOR_OPERATION=7;
	/**
	 * Index for file which contains all table names.
	 */
	private static final int  INDX_FOR_TABLE_NAMES_FILE=8;
	/**
	 * Index for csv file name.
	 */
	private static final int  INDX_FOR_CSV_FILE_NAME=9;
	/**
	 * Index for CTL file name.
	 */
	private static final int  INDX_FOR_CTL_FILE_PATH=10;
	/**
	 * Index for oracle tns name.
	 */
	private static final int  INDX_FOR_TNS_NAME=11;

	/**
	 *
	 * @param args the arguments to be passed are:
	 * 		 	- database.host
	 * 			- database.port
	 * 			- database.type
	 * 			- database.name
	 * 			- database.username
	 * 			- database.password
	 * 			- database driver
	 * 			- import/export
	 * 			- path for dumpFileColumnInfo.txt which contains
	 * 				the table name list to be imported/exported.
	 * 			- folder path for CAModelCSVs files
	 * 			- folder path for CAModelCTLs files required in case of oracle
	 * 			- oracle.tns.name required in case of oracle
	 */
	public static void main(String[] args) throws Exception
	{
		Connection connection = null;
		try
		{
			AutomateImport automateImport = new AutomateImport();
			automateImport.configureDBConnection(args);
			connection = dbUtility.getConnection();
			List<String> tableNamesList = automateImport
							.getTableNamesList(args[INDX_FOR_TABLE_NAMES_FILE]);
			int size = tableNamesList.size();
			String filePath = args[INDX_FOR_CSV_FILE_NAME].replaceAll("\\\\", "//");
			if (Constants.ORACLE_DATABASE.equals(dbUtility.getDbType().toUpperCase()))
			{
				oracleTnsName = args[INDX_FOR_TNS_NAME];
				String filePathCTL = args[INDX_FOR_CTL_FILE_PATH].replaceAll("\\\\", "//");
				if (args[INDX_FOR_OPERATION].equalsIgnoreCase("import"))
				{
					for (int i = 0; i < size; i++)
					{
						String ctlFilePath = filePathCTL + tableNamesList.get(i) + ".ctl";
						if (!new File(ctlFilePath).exists())
						{
							String csvFilePath = filePath
								+ tableNamesList.get(i) + ".csv";
							automateImport.createCTLFiles(connection,
									csvFilePath,ctlFilePath,
									tableNamesList.get(i));
						}
						automateImport.importDataOracle(ctlFilePath);
					}
				}
				else
				{
					for (int i = 0; i < size; i++)
					{
						String ctlFilePath = filePathCTL + tableNamesList.get(i) + ".ctl";
						String csvFilePath = filePath + tableNamesList.get(i) + ".csv";
						automateImport.createCTLFiles(connection, csvFilePath, ctlFilePath,
								tableNamesList.get(i));
					}
				}
			}
			else
			{
				if (args[INDX_FOR_OPERATION].equalsIgnoreCase("import"))
				{
					Statement stmt = connection.createStatement();
					stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
					for (int i = 0; i < size; i++)
					{
						String dumpFilePath = filePath + tableNamesList.get(i) + ".csv";
						automateImport.importDataMySQL(connection,
								dumpFilePath, tableNamesList.get(i));
					}
					stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
				}
				else
				{
					for (int i = 0; i < size; i++)
					{
						String dumpFilePath = filePath + tableNamesList.get(i) + ".csv";
						automateImport.exportDataMySQL(connection,
								dumpFilePath, tableNamesList.get(i));
					}
				}
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * assigning database parameters.
	 * @param args String[] of configuration info
	 * @throws Exception  Generic exception
	 */
	private void configureDBConnection(String[] args) throws Exception
	{
		if (args.length < MIN_NO_OF_ARGS)
		{
			throw new Exception("In sufficient number of arguments");
		}
		dbUtility.setDbParams(args);
	}

	/**
	 *  This method will insert the data to database.
	 * @param conn Connection object
	 * @param filename File Name
	 * @param tableName Table Name
	 * @throws SQLException generic exception
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

	/**
	 *  This method will export the data to database.
	 * @param conn Connection object
	 * @param fileName File Name
	 * @param tableName Table Name
	 * @throws SQLException generic exception
	 */
	private void exportDataMySQL(Connection conn, String fileName, String tableName)
			throws SQLException
	{
		Statement stmt=null;
		try
		{
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
			stmt.close();
		}
	}

	/**
	 * This method will read the table list file.
	 * @param fileName file name
	 * @return table list
	 * @throws IOException Generic IO exception
	 */
	private List<String> getTableNamesList(String fileName) throws IOException
	{
		ArrayList<String> tableNamesList = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String linereader = reader.readLine();
		while (linereader != null)
		{
			tableNamesList.add(linereader);
			linereader = reader.readLine();
		}
		return tableNamesList;
	}

	/**
	 * This method will insert the data to database.
	 * @param fileName File Name
	 * @throws Exception generic exception
	 */
	private void importDataOracle(String fileName) throws Exception
	{
		StringBuffer cmd = new StringBuffer("sqlldr ").append(dbUtility.getDbUserName())
								.append('/').append(dbUtility.getDbPassword())
								.append('@').append(oracleTnsName)
								.append(" control=").append(fileName);
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(cmd.toString());
		// any error message?
		StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream());

		// any output?
		StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream());
		errorGobbler.start();
		outputGobbler.start();
		proc.waitFor();

	}

	/**
	 * This method will create control file for SQL loader.
	 * @param connection Connection Object.
	 * @param csvFileName
	 * @param ctlFileName
	 * @param tableName
	 * @throws IOException
	 * @throws SQLException
	 */
	private void createCTLFiles(Connection connection, String csvFileName, String ctlFileName,
			String tableName) throws IOException, SQLException
	{
		File file = new File(ctlFileName);
		if (file.exists())
		{
			file.delete();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(ctlFileName)));
		String value= new StringBuffer("LOAD DATA INFILE '")
			.append(csvFileName)
			.append("' \nBADFILE '/sample.bad'\nDISCARDFILE '/sample.dsc'\nAPPEND \nINTO TABLE ")
			.append(tableName)
			.append(" \nFIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"'\n").toString();

		String columnName = getColumnName(connection, tableName);
		bufferedWriter.write(value + columnName);
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	/**
	 * This method will retrieve the column name list for a given table.
	 * @param connection
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private String getColumnName(Connection connection, String tableName) throws SQLException
	{
		String query = "select * from " + tableName + " where 1=2";
		Statement stmt = null;
		ResultSet resultSet = null;
		try
		{
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
			StringBuffer columnNameList = getColumnNameList(resultSet);
			return columnNameList.toString();
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (resultSet != null)
			{
				resultSet.close();
			}

		}
	}

	/**
	 * @param resultSet ResultSet object.
	 * @return list of column name
	 * @throws SQLException Generic SQL Exception.
	 */
	private StringBuffer getColumnNameList(ResultSet resultSet) throws SQLException
	{
		StringBuffer columnNameList = new StringBuffer();
		columnNameList.append('(');
		ResultSetMetaData rsMetaData = resultSet.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();
		for (int i = 1; i < numberOfColumns + 1; i++)
		{
			columnNameList.append(rsMetaData.getColumnName(i));
			if (Types.DATE == rsMetaData.getColumnType(i)
					|| Types.TIMESTAMP == rsMetaData.getColumnType(i))
			{
				columnNameList.append(" DATE 'YYYY-MM-DD'");
			}
			if (!("HIDDEN".equals(rsMetaData.getColumnName(i)))
					&& !("FORMAT".equals(rsMetaData.getColumnName(i))))
			{
				columnNameList.append(" NULLIF ");
				columnNameList.append(rsMetaData.getColumnName(i));
				columnNameList.append("='\\\\N'");
			}
			if (i < numberOfColumns)
			{
				columnNameList.append(',');
			}
		}
		columnNameList.append(')');
		return columnNameList;
	}
}
/**
 *This class is for output of any  message(or error message) during import.
 *
 */
class StreamGobbler extends Thread
{
	/**
	 * InputStream object.
	 */
	private InputStream inputStream;

	/**
	 * One argument constructor.
	 * @param inputStream InputStream object.
	 */
	StreamGobbler(InputStream inputStream)
	{
		super();
		this.inputStream = inputStream;
	}
}
