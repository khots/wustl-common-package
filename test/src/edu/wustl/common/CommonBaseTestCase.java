package edu.wustl.common;

import org.apache.log4j.PropertyConfigurator;

import edu.wustl.common.test.BaseTestCase;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

public class CommonBaseTestCase extends BaseTestCase
{
	static
	{
		System.setProperty("app.propertiesFile","D:/washu-workspace/CommonPackage_REFACTOR/ApplicationResource.properties");
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
		PropertyConfigurator.configure(System.getProperty("user.dir") + "/log4j.properties");
	}
}
