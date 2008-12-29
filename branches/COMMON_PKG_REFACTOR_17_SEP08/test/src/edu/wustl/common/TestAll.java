
package edu.wustl.common;

/**
 * Main class to run junit test cases.
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.wustl.common.audit.AuditManagerTestCase;
import edu.wustl.common.bizlogic.AbstractBizLogicTestCase;
import edu.wustl.common.bizlogic.DefaultBizLogicTestCase;
import edu.wustl.common.datatypes.DataTypeTestCase;
import edu.wustl.common.factory.AbstractFactoryConfigTestCase;
import edu.wustl.common.util.UtilityTestCases;
import edu.wustl.common.util.XMLPropertyHandlerTestCase;
import edu.wustl.common.util.global.CSMGroupLocatorTestCase;
import edu.wustl.common.util.global.CommonServiceLocatorTestCase;
import edu.wustl.common.util.global.StatusTestCase;
import edu.wustl.common.util.impexp.MySqlAutomateImpExpTestCase;


/**
 * @author ravi kumar
 * Test Suite for testing all Common package test classes.
 */
public class TestAll
{
	/**
	 * Main method called by junit target in build.xml.
	 * @param args arguments to main method.
	 */
	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(TestAll.class);
	}
	/**
	 *
	 * @return Test object.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for Query Interface Classes");
		suite.addTestSuite(UtilityTestCases.class);
		suite.addTestSuite(DefaultBizLogicTestCase.class);
		suite.addTestSuite(AbstractBizLogicTestCase.class);
		suite.addTestSuite(StatusTestCase.class);
		suite.addTestSuite(DataTypeTestCase.class);
		suite.addTestSuite(AbstractFactoryConfigTestCase.class);
		suite.addTestSuite(CommonServiceLocatorTestCase.class);
		suite.addTestSuite(CSMGroupLocatorTestCase.class);
		suite.addTestSuite(AuditManagerTestCase.class);
		suite.addTestSuite(XMLPropertyHandlerTestCase.class);
		suite.addTestSuite(MySqlAutomateImpExpTestCase.class);
		return suite;
	}
}
