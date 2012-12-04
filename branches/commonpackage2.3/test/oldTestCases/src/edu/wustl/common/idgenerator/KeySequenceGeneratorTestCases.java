
package edu.wustl.common.idgenerator;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.util.KeySequenceGeneratorUtil;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.exception.DAOException;

public class KeySequenceGeneratorTestCases extends CommonBaseTestCase
{

	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(KeySequenceGeneratorTestCases.class);

	/**
	 * Test case for unique id generation
	 */
	public void testIdGenerationWithNoSeqId()
	{
		try
		{
			List<KeySequenceGenerator> dataList = new ArrayList();
			MyDAOImpl.setData(dataList);
			String uniqueId = KeySequenceGeneratorUtil.getNextUniqeId("100", "test_key_type");
			assertEquals(Long.valueOf(1), Long.valueOf(uniqueId));
		}
		catch (DAOException exception)
		{
			// TODO Auto-generated catch block
			fail("Error in unique ID generation");
			logger.fatal(exception.getMessage(), exception);
		}
		finally
		{
			MyDAOImpl.setData(new ArrayList());
		}
	}

	public void testIdGenerationForNextSeqId()
	{
		try
		{

			List<KeySequenceGenerator> dataList = new ArrayList();
			KeySequenceGenerator generator = new KeySequenceGenerator();
			generator.setId(Long.valueOf(1));
			generator.setKeyValue("100");
			generator.setKeyType("test_key_type");
			generator.setKeySequenceId("2");
			dataList.add(generator);

			MyDAOImpl.setData(dataList );
			String uniqueId = KeySequenceGeneratorUtil.getNextUniqeId("100", "test_key_type");
			assertEquals(Long.valueOf(2), Long.valueOf(uniqueId));
		}
		catch (DAOException exception)
		{
			// TODO Auto-generated catch block
			fail("Error in unique ID generation");
			logger.fatal(exception.getMessage(), exception);
		}
		finally
		{
			MyDAOImpl.setData(new ArrayList());
		}
	}
}
