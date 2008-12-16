package edu.wustl.common;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.test.BaseTestCase;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

public class CommonBaseTestCase extends BaseTestCase
{
	static
	{
		System.setProperty("app.propertiesFile",System.getProperty("user.dir")+"/ApplicationResource.properties");
		PropertyConfigurator.configure(System.getProperty("user.dir") + "/log4j.properties");
		try
		{
			ErrorKey.init("-");
		}
		catch (IOException e)
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
