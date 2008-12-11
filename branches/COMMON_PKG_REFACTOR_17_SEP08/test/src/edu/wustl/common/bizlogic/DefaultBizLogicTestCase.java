package edu.wustl.common.bizlogic;

import java.util.List;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

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

	/**
	 * Application name for DAO configuration.This must be same as in file ApplicationDAOProperties.xml.
	 */
	private static final String APP_NAME="commonpackagetest";
	/**
	 * DAO factory.
	 */
	private static final  IDAOFactory DAO_FACTORY = DAOConfigFactory.getInstance().getDAOFactory(APP_NAME);

	/**
	 * Positive test for
	 * List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition).
	 */
	public void testRetrive1()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			String []selectColumnName={"selectColumnName"};
			String []whereColumnName={"whereColumnName"};
			String []whereColumnCondition={"="};
			String []whereColumnValue={"whereColumnValue"};
			String joinCondition="joinCondition";
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName",selectColumnName,
					whereColumnName,whereColumnCondition,whereColumnValue,joinCondition);
			assertEquals(MyDAOImpl.list.size(), list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage());
		}
	}
	/**
	 * Negative test for
	 * List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition).
	 */
	public void testFailRetrive1()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			String []selectColumnName={"selectColumnName"};
			String []whereColumnName={"whereColumnName"};
			String []whereColumnCondition={"="};
			String []whereColumnValue={"whereColumnValue"};
			String joinCondition="joinCondition";
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName",selectColumnName,
					whereColumnName,whereColumnCondition,whereColumnValue,joinCondition);
			fail("Negative test case: Should throw BizLogicException");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Thrown BizLogicException",true);
			logger.fatal(exception.getMessage());
		}
	}

	/**
	 * Positive test case for insert(Object,DAO,SessionDataBean).
	 */
	public void testInsertWithSessionDataBean()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.insert(null,myJdbcDao,null);
			assertTrue("Data inserted successfully.",true);
		}
		catch (Exception exception)
		{
			fail("Not able to insert data.");
			logger.fatal(exception.getMessage());
		}
	}

	/**
	 * Negative test case for insert(Object,DAO,SessionDataBean).
	 */
	public void testFailInsertWithSessionDataBean()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.insert(null,myJdbcDao,null);
			fail("Negative test case: should not inserted data.");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Thrown BizLogicException",true);
			logger.fatal(exception.getMessage(),exception);
		}
		catch (Exception exception)
		{
			fail("Negative test case: should throw only BizLogicException");
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Positive test case for insert(Object,DAO).
	 */
	public void testInsert()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
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
	/**
	 * Negative test case for insert(Object,DAO).
	 */
	public void testFailInsert()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.insert(null,myJdbcDao);
			fail("Negative test case: should not inserted data.");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Thrown BizLogicException",true);
			logger.fatal(exception.getMessage(),exception);
		}
		catch (Exception exception)
		{
			fail("Negative test case: should throw only BizLogicException");
			logger.fatal(exception.getMessage(),exception);
		}
	}
}
