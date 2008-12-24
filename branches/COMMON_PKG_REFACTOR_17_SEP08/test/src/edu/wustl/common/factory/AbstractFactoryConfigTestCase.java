package edu.wustl.common.factory;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;


public class AbstractFactoryConfigTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractFactoryConfigTestCase.class);

	public void testGetBizLogicFactory()
	{
		try
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory(
			"bizLogicFactory");
			assertEquals(true, factory instanceof MyBizLogicFactory);
		}
		catch (BizLogicException exception)
		{
			fail("Didn't get Factory classes.");
			logger.debug("Didn't get Factory classes.",exception);
		}
	}

	public void testFailGetBizLogicFactory()
	{
		try
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory(
			"xyz");
			fail("Negative test case: Must throw exception, with in valid name:xyz");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Throws exception.",true);
			logger.debug("Didn't get Factory classes.",exception);
		}
	}
	public void testGetForwToFactory()
	{
		try
		{
			IForwordToFactory factory = AbstractFactoryConfig.getInstance()
			.getForwToFactory("forwardToFactory");
			assertEquals(true, factory instanceof MyBizLogicFactory);
		}
		catch (BizLogicException exception)
		{
			fail("Didn't get Factory classes.");
			logger.debug("Didn't get Factory classes.",exception);
		}
	}

	public void testFailGetForwToFactory()
	{
		try
		{
			IForwordToFactory factory = AbstractFactoryConfig.getInstance().getForwToFactory("xyz");
			fail("Negative test case: Must throw exception, with in valid name:xyz");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Throws exception.",true);
			logger.debug("Didn't get Factory classes.",exception);
		}
	}
}
