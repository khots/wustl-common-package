package edu.wustl.common;

import java.io.IOException;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.test.BaseTestCase;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;
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
		Logger.configureLogger(System.getProperty("user.dir"));
		try
		{
			XMLPropertyHandler.init(System.getProperty("app.propertiesFile"));
			ErrorKey.init("-");
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
