package edu.wustl.common;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.test.BaseTestCase;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.HibernateProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
/**
 * This is common test class.
 * All test classes in common package should extends this class.  
 * @author ravi_kumar
 *
 */
public class CommonBaseTestCase extends BaseTestCase
{
	static
	{
		System.setProperty("app.propertiesFile",System.getProperty("user.dir")+"/caTissueCore_Properties.xml");
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
		try
		{
			HibernateProperties.initBundle(System.getProperty("user.dir")+"/test/junitConf.properties");
			XMLPropertyHandler.init(System.getProperty("app.propertiesFile"));
			ErrorKey.init("~");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public CommonBaseTestCase()
	{
		super();
	}

	public CommonBaseTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
		
	}
}
