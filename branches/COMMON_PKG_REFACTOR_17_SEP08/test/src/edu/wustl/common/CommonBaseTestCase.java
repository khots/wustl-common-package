package edu.wustl.common;

import org.apache.log4j.PropertyConfigurator;

import edu.wustl.common.test.BaseTestCase;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

public class CommonBaseTestCase extends BaseTestCase
{
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
		//Variables.applicationHome = System.getProperty("user.dir");
	//	System.out.println("proppath:"+Variables.propertiesDirPath);
		//System.out.println("logger Name:"+org.apache.log4j.Logger.getLogger("commonpkg.log").getName());
	//	Logger.out = org.apache.log4j.Logger.getLogger("");
		//PropertyConfigurator.configure(Variables.applicationHome + "/Logger.properties");
	}
}
