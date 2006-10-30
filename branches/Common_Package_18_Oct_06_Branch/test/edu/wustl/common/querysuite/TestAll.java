
package edu.wustl.common.querysuite;

/**
 * 
 */

import edu.wustl.common.querysuite.queryengine.impl.SqlGeneratorTestCase;
import edu.wustl.common.querysuite.queryobject.impl.ExpressionTestCases;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraphTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author prafull_kadam
 * Test Suite for testing all Query Interface related classes.
 */
public class TestAll
{

	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(TestAll.class);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for Query Interface Classes");
		suite.addTestSuite(ExpressionTestCases.class);
		suite.addTestSuite(JoinGraphTestCase.class);
		suite.addTestSuite(SqlGeneratorTestCase.class);
		return suite;
	}
}
