package edu.wustl.common.util.impexp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.logger.Logger;

/**
 * This is for testing of Automate import/export function for MSSQL server database.
 * Please make sure that all the parameters passed into this are correct i.e Check the static array 'ARGS'.
 * Also database server must contains csv file because.   
 * @author ravi_kumar
 *
 */
public class MsSqlAutomateImpExpTestCase extends CommonAutomateImpExpTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(MsSqlAutomateImpExpTestCase.class);
	private static final String ARGS[]={
			"ps4057",
			"1433",
			"mssql",
			"testravi",
			"sa",
			"ps4057!@#",
			"com.microsoft.sqlserver.jdbc.SQLServerDriver",
			"import",
			System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/dumpFileColumnInfo.txt",
			System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/"
	};
	public void testMsSqlAutomateImport()
	{
		try
		{	preImport();
			AutomateImport.main(ARGS);
			assertTrue("Metadata imported successfully.", true);
		}
		catch (Exception exception)
		{
			fail("Fail to import metadata.");
			logger.debug("Fail to import metadata.", exception);
		}	
	}
	
	public void preImport() throws IOException, SQLException, ClassNotFoundException, ApplicationException
	{
		DatabaseUtility dbUtility= new DatabaseUtility();
		dbUtility.setDbParams(ARGS);
		setConnection(dbUtility.getConnection());
		try
		{
			runSQLFile(System.getProperty("user.dir")+"/SQL/MSSQL/testAutomateImpExp.sql");
			executeQuery(getConnection(),"SET IDENTITY_INSERT CATISSUE_PERMISSIBLE_VALUE ON");			
			runSQLFile(System.getProperty("user.dir")+"/SQL/Common/test/CDE_DummyData_Common.sql");
			executeQuery(getConnection(),"SET IDENTITY_INSERT CATISSUE_PERMISSIBLE_VALUE OFF");
		}
		finally
		{
			getConnection().close();
		}
	}
	
	public void executeQuery(Connection conn,String query) throws SQLException
	{
		Statement statement=conn.createStatement();
		statement.execute(query);
		
	}
}
