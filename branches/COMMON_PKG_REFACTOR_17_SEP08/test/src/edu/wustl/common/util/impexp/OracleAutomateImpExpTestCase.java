package edu.wustl.common.util.impexp;

import java.io.IOException;
import java.sql.SQLException;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.logger.Logger;


public class OracleAutomateImpExpTestCase extends CommonAutomateImpExpTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(OracleAutomateImpExpTestCase.class);
	private static final String ARGS[]={
			"localhost",
			"3306",
			"oracle",
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
			ARGS[9]=System.getProperty("user.dir")+"/SQL/Oracle/";
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
			runSQLFile(System.getProperty("user.dir")+"/SQL/Oracle/testAutomateImpExp.sql");
			runSQLFile(System.getProperty("user.dir")+"/SQL/Common/test/CDE_DummyData_Common.sql");
		}
		finally
		{
			getConnection().close();
		}
	}
}
