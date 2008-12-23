package edu.wustl.common.factory;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.logger.Logger;


public class AbstractFactoryConfigTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractFactoryConfigTestCase.class);

	public void testGetInstance()
	{
		try
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory(
			"bizLogicFactory");
			assertEquals(true, factory instanceof MyBizLogicFactory);
		}
		catch (ApplicationException exception)
		{
			fail("Didn't get Factory classes.");
			logger.debug("Didn't get Factory classes.",exception);
		}
	}
}
