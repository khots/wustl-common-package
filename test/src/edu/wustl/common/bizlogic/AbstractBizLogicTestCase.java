package edu.wustl.common.bizlogic;

import titli.model.util.TitliResultGroup;
import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.domain.MyDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;


public class AbstractBizLogicTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractBizLogicTestCase.class);

	/**
	 * Application name for DAO configuration.This must be same as in file ApplicationDAOProperties.xml.
	 */
	private static final String APP_NAME="commonpackagetest";
	/**
	 * DAO factory.
	 */
	private static final  IDAOFactory DAO_FACTORY = DAOConfigFactory.getInstance().getDAOFactory(APP_NAME);

	/**
	 *  Positive test case for public void delete(Object).
	 */
	public void testDelete()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			MyDomainObject ado = getAbstractDomainObject();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.delete(ado);
			assertTrue("Object deleted successfuly.", true);
		}
		catch (Exception exception)
		{
			fail("Not able to delete the object.");
			logger.fatal(exception.getMessage(),exception);
		}
	}
	/**
	 *  Negative test case for public void delete(Object).
	 */
	public void testFailDelete()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			MyDomainObject ado = getAbstractDomainObject();
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			defaultBizLogic.delete(ado);
			fail("Negative test case:must throw BizLogicException.");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case: Thrown BizLogicException during delete operation.", true);
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 *  Positive test case for public void delete(Object).
	 */
	public void testInsertWithSessionDataBean()
	{
		try
		{
			MyDAOImpl.isTestForFail=false;
			MyDomainObject ado = getAbstractDomainObject();
			MyDefaultBizLogic defaultBizLogic = new MyDefaultBizLogic();
			defaultBizLogic.insert(ado);
			assertTrue("Object inserted successfuly.", true);
		}
		catch (BizLogicException exception)
		{
			fail("Not able to insert the object.");
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 *  Positive test case for public void delete(Object).
	 */
	public void testFailInsertWithSessionDataBean()
	{
		try
		{
			MyDAOImpl.isTestForFail=true;
			MyDomainObject ado = getAbstractDomainObject();
			MyDefaultBizLogic defaultBizLogic = new MyDefaultBizLogic();
			defaultBizLogic.insert(ado);
			fail("Negative test case:must throw BizLogicException.");
		}
		catch (BizLogicException exception)
		{
			assertTrue("Negative test case: Thrown BizLogicException during insert operation.", true);
			logger.fatal(exception.getMessage(),exception);
		}
	}

	/**
	 * @return AbstractDomainObject
	 */
	private MyDomainObject getAbstractDomainObject()
	{
		MyDomainObject ado= new MyDomainObject();
		ado.setActivityStatus("Active");
		ado.setId(Long.valueOf(1));
		return ado;
	}
}
