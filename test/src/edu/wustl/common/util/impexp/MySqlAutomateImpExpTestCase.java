package edu.wustl.common.util.impexp;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.logger.Logger;


public class MySqlAutomateImpExpTestCase extends CommonAutomateImpExpTestCase
{

	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(MySqlAutomateImpExpTestCase.class);
	private static final String ARGS[]={
			"localhost",
			"3306",
			"mysql",
			"test",
			"root",
			"root",
			"com.mysql.jdbc.Driver",
			"import",
			System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/dumpFileColumnInfo.txt",
			System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/"
	};
	public void testMySqlAutomateImport()
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
	
	public void testMySqlAutomateExport()
	{
		
		try
		{
			ARGS[7]="export";
			ARGS[8]=System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/dumpFileColumnInfo.txt";
			ARGS[9]=System.getProperty("user.dir")+"/SQL/Common/test/";
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
			runSQL(System.getProperty("user.dir")+"/SQL/MySQL/testAutomateImpExpMysql.sql");
			runSQL(System.getProperty("user.dir")+"/SQL/Common/test/CDE_DummyData_Common.sql");
		}
		finally
		{
			getConnection().close();
		}
	}
	
	public void runSQL(String fileName) throws SQLException, IOException
	{
		setScript(new File(fileName));		
		setStatement(getConnection().createStatement());
		loadScript();
		execute();
	}

	
}
