package edu.wustl.common.util.impexp;

import java.io.IOException;
import java.sql.SQLException;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.global.HibernateProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * This is for testing of Automate import/export function for MySQL database.
 * Please make sure that all the parameters configured in junitConf.properties are correct.
 * @author ravi_kumar
 *
 */
public class MySqlAutomateImpExpTestCase extends CommonAutomateImpExpTestCase
{

	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(MySqlAutomateImpExpTestCase.class);
	private static final String []ARGS= new String[10];
	static
	{
			ARGS[0]=HibernateProperties.getValue("mysql.db.host");
			ARGS[1]=HibernateProperties.getValue("mysql.db.port");
			ARGS[2]="mysql";
			ARGS[3]=HibernateProperties.getValue("mysql.db.name");
			ARGS[4]=HibernateProperties.getValue("mysql.db.username");
			ARGS[5]=HibernateProperties.getValue("mysql.db.password");
			ARGS[6]=HibernateProperties.getValue("mysql.db.driver");
	}
	public void testMySqlAutomateImport()
	{
		ARGS[7]="import";
		ARGS[8]=System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/dumpFileColumnInfo.txt";
		ARGS[9]=System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/";
		try
		{	preImport();
			AutomateImport.main(ARGS);
			assertTrue("Metadata imported successfully.", true);
		}
		catch (Exception exception)
		{
			logger.debug("Fail to import metadata.", exception);
			fail("Fail to import metadata.");
		}
	}

	public void testMySqlAutomateExport()
	{

		try
		{
			ARGS[7]="export";
			ARGS[8]=System.getProperty("user.dir")+"/SQL/Common/test/Permissible_values/dumpFileColumnInfo.txt";
			ARGS[9]=System.getProperty("user.dir")+"/SQL/MySQL/";
			AutomateImport.main(ARGS);
			assertTrue("Metadata imported successfully.", true);
		}
		catch (Exception exception)
		{
			logger.debug("Fail to import metadata.", exception);
			fail("Fail to import metadata.");
		}
	}

	public void preImport() throws IOException, SQLException, ClassNotFoundException, ApplicationException
	{
		DatabaseUtility dbUtility= new DatabaseUtility();
		dbUtility.setDbParams(ARGS);
		setConnection(dbUtility.getConnection());
		try
		{
			runSQLFile(System.getProperty("user.dir")+"/SQL/MySQL/testAutomateImpExp.sql");
			runSQLFile(System.getProperty("user.dir")+"/SQL/Common/test/CDE_DummyData_Common.sql");
		}
		finally
		{
			getConnection().close();
		}
	}
}
