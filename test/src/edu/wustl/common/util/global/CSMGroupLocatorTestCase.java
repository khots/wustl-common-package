package edu.wustl.common.util.global;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.util.logger.Logger;


public class CSMGroupLocatorTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(CSMGroupLocatorTestCase.class);

	public void testGetPGName()
	{
		try
		{
			String className="edu.wustl.common.domain.CollectionProtocol";
			Class clazz= Class.forName(className);
			String pgName=CSMGroupLocator.getInstance().getPGName(Long.valueOf(1),clazz);
			assertEquals("COLLECTION_PROTOCOL_1",pgName);
		}
		catch (Exception exception)
		{
			fail("Not able to get correct pg name.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	public void testGetPIGroupName()
	{
		try
		{
			String className="edu.wustl.common.domain.CollectionProtocol";
			Class clazz= Class.forName(className);
			String pgName=CSMGroupLocator.getInstance().getPIGroupName(Long.valueOf(1),clazz);
			assertEquals("PI_COLLECTION_PROTOCOL_1",pgName);
		}
		catch (Exception exception)
		{
			fail("Not able to get correct PIGroup name.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	public void testGetCoordinatorGroupName()
	{
		try
		{
			String className="edu.wustl.common.domain.CollectionProtocol";
			Class clazz= Class.forName(className);
			String pgName=CSMGroupLocator.getInstance().getCoordinatorGroupName(Long.valueOf(1),clazz);
			assertEquals("COORDINATORS_COLLECTION_PROTOCOL_1",pgName);
		}
		catch (Exception exception)
		{
			fail("Not able to get correct coordinator name.");
			logger.fatal(exception.getMessage(),exception);
		}
	}
}
