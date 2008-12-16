package edu.wustl.common.bizlogic;

import java.util.List;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.QueryWhereClause;
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
	 *  Positive test case for update.
	 */
	public void testUpdateWithSessionDataBean()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.update(myJdbcDao,"","",null);
			assertTrue("Object updated successfuly.", true);
		}
		catch (Exception exception)
		{
			fail("Not able to update the object.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Negative test case for delete.
	 */
	public void testFailUpdateWithSessionDataBean()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.update(myJdbcDao,"","",null);
			fail("Negative test case: Should not update the object.");
		}
		catch (BizLogicException exception)
		{
			assertTrue(true);
			logger.fatal(exception.getMessage(),exception);
		}
		catch (Exception exception)
		{
			fail("Negative test case: Should not update the object.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 *  Positive test case for update.
	 */
	public void testUpdate()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.update(myJdbcDao,"");
			assertTrue("Object updated successfuly.", true);
		}
		catch (Exception exception)
		{
			fail("Not able to update the object.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Negative test case for delete.
	 */
	public void testFailUpdate()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.update(myJdbcDao,"");
			fail("Negative test case: Should not update the object.");
		}
		catch (BizLogicException exception)
		{
			assertTrue(true);
			logger.fatal(exception.getMessage(),exception);
		}
		catch (Exception exception)
		{
			fail("Negative test case: Should not update the object.");
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Positive test case for delete.
	 */
	public void testDelete()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.delete("");
			assertTrue("Object deleted successfuly.", true);
		}
		catch (BizLogicException exception)
		{
			fail("Not able to delete the object.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Negative test case for delete. 
	 */
	public void testFailDelete()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.delete("");
			fail("Negative test case: Should not delete the object.");
		}
		catch (BizLogicException exception)
		{
			assertTrue(true);
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Positive test for
	 * List getList(String sourceObjectName, String[] displayNameFields, String valueField,
			boolean isToExcludeDisabled).
	 */
	public void testGetListWithFourParam()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			String []displayNameFields={"displayNameFields"};
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List list=defaultBizLogic.getList("sourceObjectName",displayNameFields,"valueField",true);
			assertEquals(MyDAOImpl.list.size()+1,list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}


	/**
	 * Positive test for
	 * List getList(String sourceObjectName, String[] displayNameFields, String valueField,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition, String separatorBetweenFields, boolean isToExcludeDisabled).
	 */
	public void testGetListWithNineParam()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			String []displayNameFields={"displayNameFields"};
			String []selectColumnName={"selectColumnName"};
			String []whereColumnCondition={"="};
			String []whereColumnValue={"whereColumnValue"};
			String joinCondition="joinCondition";
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List list=defaultBizLogic.getList("sourceObjectName",displayNameFields,"valueField",
					selectColumnName,whereColumnCondition,whereColumnValue
					,joinCondition,",",true);
			assertEquals(MyDAOImpl.list.size()+1,list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Positive test for
	 * Object retrieveAttribute(Class objClass, Long identifier, String attributeName).
	 */
	public void testRetrieveAttribute()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			Object object=defaultBizLogic.retrieveAttribute("".getClass(),
					Long.valueOf(0),"attributeName");
			assertEquals("retObject",object.toString());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Negative test for
	 * Object retrieveAttribute(Class objClass, Long identifier, String attributeName).
	 */
	public void testFailRetrieveAttribute()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			Object object=defaultBizLogic.retrieveAttribute("".getClass(),
					Long.valueOf(0),"attributeName");
			fail("Negative test case: Should throw BizLogicException");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Thrown BizLogicException",true);
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Positive test for
	 * Object retrieveAttribute(Class objClass, Long identifier, String attributeName).
	 */
	public void testRetrieveAttributeByClassName()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			Object object=defaultBizLogic.retrieveAttribute("edu.wustl.common.beans.NameValueBean",
					Long.valueOf(0),"attributeName");
			assertEquals("retObject",object.toString());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Negative test for
	 * Object retrieveAttribute(Class objClass, Long identifier, String attributeName).
	 */
	public void testFailRetrieveAttributeByClassName()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			Object object=defaultBizLogic.retrieveAttribute("abc".getClass(),
					Long.valueOf(0),"attributeName");
			fail("Negative test case: Should throw BizLogicException");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Thrown BizLogicException",true);
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Positive test for
	 * List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition).
	 */
	public void testRetriveWithSixParam()
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
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Negative test for
	 * List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition).
	 */
	public void testFailRetriveWithSixParam()
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
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Positive test for
	 * public List retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause).
	 */
	public void testRetriveWithQueryWhereClause()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			String []selectColumnName={"selectColumnName"};
			QueryWhereClause queryWhereClause= new QueryWhereClause("SourceObjName");
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName",
						selectColumnName,queryWhereClause);
			assertEquals(MyDAOImpl.list.size(), list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Negative test for
	 * public List retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause).
	 */
	public void testFailRetriveWithQueryWhereClause()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			String []selectColumnName={"selectColumnName"};
			QueryWhereClause queryWhereClause= new QueryWhereClause("SourceObjName");
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName",
						selectColumnName,queryWhereClause);
			fail("Negative test case: Should throw BizLogicException");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case:Thrown BizLogicException",true);
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Test case for
	 * List retrieve(String sourceObjectName, String[] whereColumnName,
			String[] whereColumnCondition, Object[] whereColumnValue, String joinCondition).
	 */
	public void testRetriveWithFiveParam()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			String []whereColumnName={"whereColumnName"};
			String []whereColumnCondition={"="};
			String []whereColumnValue={"whereColumnValue"};
			String joinCondition="joinCondition";
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName",whereColumnName,whereColumnCondition,
					whereColumnValue,joinCondition);
			assertEquals(MyDAOImpl.list.size(), list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Test case for
	 * List retrieve(String className, String colName, Object colValue).
	 */
	public void testRetriveThreeParam()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName","colName","colValue");
			assertEquals(MyDAOImpl.list.size(), list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Test case for
	 * List retrieve(String sourceObjectName).
	 */
	public void testRetriveWithOneParam()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName");
			assertEquals(MyDAOImpl.list.size(), list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 * Test case for
	 * List retrieve(String sourceObjectName, String[] selectColumnName).
	 */
	public void testRetriveWithTwoParam()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			String []selectColumnName={"selectColumnName"};
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			List<Object> list=defaultBizLogic.retrieve("SourceObjName",selectColumnName);
			assertEquals(MyDAOImpl.list.size(), list.size());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Test case for
	 * Object retrieve(String sourceObjectName, Long identifier).
	 */
	public void testRetriveWithLongIdentifier()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			Object object=defaultBizLogic.retrieve("SourceObjName",Long.valueOf(1));
			assertEquals("retObject",object.toString());
		}
		catch (BizLogicException exception)
		{
			fail("Not able to retrieve data.");
			logger.fatal(exception.getMessage(),exception);
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
			logger.fatal(exception.getMessage(),exception);
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
			logger.fatal(exception.getMessage(),exception);
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

	/**
	 * Positive test case for disableObjects(DAO dao, Class sourceClass, String classIdentifier,
			String tablename, String colName, Long[] objIDArr).
	 */
	public void testDisableObjectsWithColumnName()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			MyDAOImpl.identifierList=true;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			Long []objIDArr={ Long.valueOf(1),Long.valueOf(2)};
			defaultBizLogic.disableObjects(myJdbcDao,"".getClass(),"classIdentifier","tablename","colName",objIDArr);
			assertTrue("objects disabled successfully.",true);
		}
		catch (BizLogicException exception)
		{
			fail("Not able to disable objects.");
			logger.fatal(exception.getMessage(),exception);
		}
		catch (DAOException exception)
		{
			fail("DAOException thrown");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * Positive test case for
	 * List disableObjects(DAO dao, String tablename, Class sourceClass,
			String classIdentifier, Long[] objIDArr).
	 */
	/*public void testDisableObjects()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			MyDAOImpl.identifierList=true;
			DAO myJdbcDao = DAO_FACTORY.getDAO();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			Long []objIDArr={ Long.valueOf(1),Long.valueOf(2)};
			defaultBizLogic.disableObjects(myJdbcDao,"tablename","".getClass(),"classIdentifier",objIDArr);
			assertTrue("objects disabled successfully.",true);
		}
		catch (BizLogicException exception)
		{
			fail("Not able to disable objects.");
			logger.fatal(exception.getMessage(),exception);
		}
		catch (DAOException exception)
		{
			fail("DAOException thrown");
			logger.fatal(exception.getMessage(),exception);
		}
	}*/
}