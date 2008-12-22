/*
 * Created on Jul 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import edu.wustl.common.util.logger.Logger;

/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BaseTestCase extends TestCase
{

	/**
	 * No argument constructor.
	 */
	public BaseTestCase()
	{
		super();
	}
	/**
	 * Constructs a test case with the given name.
	 * @param name Name of Test Case.
	 */
	public BaseTestCase(String name)
	{
		super(name);
	}
	/**
	 * Set up method.
	 */
	protected void setUp()
	{
		Logger.out = org.apache.log4j.Logger.getLogger("");
		PropertyConfigurator.configure(System.getProperty("user.dir") + "/Logger.properties");
	}
}