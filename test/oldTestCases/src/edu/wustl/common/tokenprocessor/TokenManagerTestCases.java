/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.tokenprocessor;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

public class TokenManagerTestCases extends CommonBaseTestCase
{

	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(TokenManagerTestCases.class);

	/**
	 * Test case for unique id generation
	 */
	public void testLabelGeneratorCase1()
	{
		try
		{
			PropertyHandler.init("/test/src/LabelTokens.Properties");
			String format = "ABCD%SYS_UID%XYZ";
			String uniqueId = TokenManager.getLabelValue(null, format);
			assertEquals("ABCD1234XYZ", uniqueId);
		}
		catch (DAOException exception)
		{
			fail("Error in unique ID generation");
			logger.fatal(exception.getMessage(), exception);
		}
		catch (ApplicationException e)
		{
			fail("Error in unique ID generation");
			logger.fatal(e.getMessage(), e);
		}
		catch (Exception e)
		{
			fail("Error in unique ID generation");
			logger.fatal(e.getMessage(), e);
		}
	}

	/**
	 * Test case for unique id generation negative test case
	 */
	public void testLabelGeneratorCase2()
	{
		try
		{
			PropertyHandler.init("/test/src/LabelTokens.Properties");
			String format = "ABCD%%XYZ";
			TokenManager.getLabelValue(null, format);
			fail("Negative Test case");
		}
		catch (DAOException exception)
		{
			fail("Error in unique ID generation");
			logger.fatal(exception.getMessage(), exception);
		}
		catch (ApplicationException e)
		{
			logger.fatal(e.getMessage(), e);
		}
		catch (Exception e)
		{
			fail("Error in unique ID generation");
			logger.fatal(e.getMessage(), e);
		}
	}

}