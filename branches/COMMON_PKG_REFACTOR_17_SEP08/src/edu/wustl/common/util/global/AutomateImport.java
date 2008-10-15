
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
	private static final int MIN_NO_ARGS=10;

	/**
	 * oracle tns name.
	 */
	static String ORACLE_TNS_NAME;

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
			List<String> tableNamesList = automateImport.getTableNamesList(args[8]);
			int size = tableNamesList.size();
			String filePath = args[9].replaceAll("\\\\", "//");
			if (Constants.ORACLE_DATABASE.equals(dbUtility.getDbType().toUpperCase()))
			{
				ORACLE_TNS_NAME = args[11];
				String filePathCTL = args[10].replaceAll("\\\\", "//");
				if (args[7].toLowerCase().equals("import"))
				{
					for (int i = 0; i < size; i++)
					{
						String ctlFilePath = filePathCTL + tableNamesList.get(i) + ".ctl";
						if (!new File(ctlFilePath).exists())
						{
							String csvFilePath = filePath + tableNamesList.get(i) + ".csv";
							automateImport.createCTLFiles(connection, csvFilePath, ctlFilePath,
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
				if (args[7].toLowerCase().equals("import"))
				{
					Statement stmt = connection.createStatement();
					stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
					for (int i = 0; i < size; i++)
					{
						String dumpFilePath = filePath + tableNamesList.get(i) + ".csv";
						automateImport.importDataMySQL(connection, dumpFilePath, tableNamesList
								.get(i));
					}
					stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
				}
				else
				{
					for (int i = 0; i < size; i++)
					{
						String dumpFilePath = filePath + tableNamesList.get(i) + ".csv";
						automateImport.exportDataMySQL(connection, dumpFilePath, tableNamesList
								.get(i));
					}
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (connection != null)
			{
				try
				{
					connection.close();
					connection = null;
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * assigning database parameters.
	 * @param args String[] of configuration info
	 * @throws Exception  Generic exception
	 */
	private void configureDBConnection(String[] args) throws Exception
	{
		if (args.length < MIN_NO_ARGS)
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
			String query = "LOAD DATA LOCAL INFILE '" + filename + "' INTO TABLE " + tableName
					+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\\n';";
			stmt.execute(query);
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
	 * @param fileName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void importDataOracle(String fileName) throws Exception
	{
		StringBuffer cmd = new StringBuffer("sqlldr ").append(dbUtility.getDbUserName())
								.append('/').append(dbUtility.getDbPassword())
								.append('@').append(ORACLE_TNS_NAME)
								.append(" control=").append(fileName);
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(cmd.toString());
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
	 * @param connection
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
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(ctlFileName)));
		String value = "LOAD DATA INFILE '" + csvFileName + "' " + "\nBADFILE '/sample.bad'"
				+ "\nDISCARDFILE '/sample.dsc'" + "\nAPPEND " + "\nINTO TABLE " + tableName + " "
				+ "\nFIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"'\n";
		String columnName = getColumnName(connection, tableName);
		bw.write(value + columnName);
		bw.flush();
		bw.close();
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
		ResultSet rs = null;
		try
		{
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++)
			{
				sb.append(rsMetaData.getColumnName(i));
				if (Types.DATE == rsMetaData.getColumnType(i)
						|| Types.TIMESTAMP == rsMetaData.getColumnType(i))
				{
					sb.append(" DATE 'YYYY-MM-DD'");
				}
				if (!("HIDDEN".equals(rsMetaData.getColumnName(i)))
						&& !("FORMAT".equals(rsMetaData.getColumnName(i))))
				{
					sb.append(" NULLIF ");
					sb.append(rsMetaData.getColumnName(i));
					sb.append("='\\\\N'");
				}
				if (i < numberOfColumns)
				{
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (rs != null)
			{
				rs.close();
			}

		}
	}
}

class StreamGobbler extends Thread
{
	InputStream is;

	StreamGobbler(InputStream is)
	{
		this.is = is;
	}
}
