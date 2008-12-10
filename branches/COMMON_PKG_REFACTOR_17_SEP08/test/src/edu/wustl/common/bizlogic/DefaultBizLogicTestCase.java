package edu.wustl.common.bizlogic;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;

/**
 * Test cases for DefaultBizLogic.
 * @author ravi_kumar
 *
 */
public class DefaultBizLogicTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DefaultBizLogicTestCase.class);


	public void testInsert()
	{
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("commonpackagetest");
		try
		{
			DAO myJdbcDao = daoFactory.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.insert(null,myJdbcDao);
			assertTrue("Data inserted successfully.",true);
		}
		catch (Exception exception)
		{
			fail("Not able to insert data.");
			logger.fatal(exception.getMessage());
		}
	}
	public void testInsertFail()
	{
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("commonpackagetest");

		try
		{
			DAO myJdbcDao = daoFactory.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			MyDAOImpl.isTestForFail=true;
			defaultBizLogic.insert(null,myJdbcDao);
			fail("Negative test case: should not inserted data.");
		}
		catch (Exception exception)
		{
			assertTrue("Error:exception should be thrown",true);
			logger.fatal(exception.getMessage());
		}
	}
}
