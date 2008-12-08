package edu.wustl.common.bizlogic;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.MyJDBCDAOImpl;

/**
 * Test cases for DefaultBizLogic
 * @author ravi_kumar
 *
 */
public class DefaultBizLogicTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	//private static org.apache.log4j.Logger logger = Logger.getLogger(DefaultBizLogicTestCase.class);

	public void testInsert()
	{
		MyJDBCDAOImpl myJdbcDao = new MyJDBCDAOImpl();
		DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
		try
		{
			defaultBizLogic.insert(null,myJdbcDao);
		}
		catch (BizLogicException exception)
		{
			assertTrue("Error:exception should be thrown :",true);
		//	logger.fatal(exception.getLogMessage());
		}
	}
}
