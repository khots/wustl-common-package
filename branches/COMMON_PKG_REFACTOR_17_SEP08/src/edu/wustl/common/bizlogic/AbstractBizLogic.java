/**
 * <p>Title: AbstractBizLogic Class>
 * <p>Description:	AbstractBizLogic is the base class of all the Biz Logic classes.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 26, 2005
 */

package edu.wustl.common.bizlogic;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import titli.controller.Name;
import titli.controller.RecordIdentifier;
import titli.controller.interfaces.IndexRefresherInterface;
import titli.controller.interfaces.ObjectMetadataInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.Titli;
import titli.model.TitliException;
import titli.model.util.TitliResultGroup;
import titli.model.util.TitliTableMapper;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatterFactory;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TitliSearchConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * AbstractBizLogic is the base class of all the Biz Logic classes.
 * @author gautam_shetty
 */
public abstract class AbstractBizLogic implements IBizLogic
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractBizLogic.class);

	/**
	 * This method gets called before insert method.
	 * Any logic before inserting into database can be included here.
	 * @param obj The object to be inserted.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException Generic BizLogic Exception
	 * */
	protected abstract void preInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException;

	/**
	 * Inserts an object into the database.
	 * @param obj The object to be inserted.
	 * @param dao the dao object.
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected abstract void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException;

	/**
	 * Inserts an object into the database.
	 * @param obj The object to be inserted.
	 * @param dao the dao object.
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected abstract void insert(Object obj, DAO dao) throws BizLogicException;

	/**
	 * This method gets called after insert method.
	 * Any logic after insertnig object in database can be included here.
	 * @param obj The inserted object.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * */
	protected abstract void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException;

	/**
	 * This method gets called after insert method.
	 * Any logic after insertnig object in database can be included here.
	 * @param objCollection Collection of object to be inserted
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException Generic BizLogic Exception
	 * */
	protected abstract void postInsert(Collection<AbstractDomainObject> objCollection, DAO dao,
			SessionDataBean sessionDataBean) throws BizLogicException;

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @param dao the dao object
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected abstract void delete(Object obj, DAO dao) throws BizLogicException;

	/**
	 * This method gets called before update method.
	 * Any logic before updating into database can be included here.
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException BizLogic Exception
	 * */
	protected abstract void preUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException;

	/**
	 * Updates an objects into the database.
	 * @param dao the dao object.
	 * @param obj The object to be updated into the database.
	 * @param oldObj The old object.
	 * @param sessionDataBean  session specific Data
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected abstract void update(DAO dao, Object obj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException;

	/**
	 * This method gets called after update method.
	 * Any logic after updating into database can be included here.
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException BizLogic Exception
	 */
	protected abstract void postUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException;

	/**
	 * Updates an objects into the database.
	 * @param dao the dao object
	 * @param obj The object to be updated.
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected abstract void update(DAO dao, Object obj) throws BizLogicException;

	/**
	 * Validates the domain object for enumerated values.
	 * @param obj The domain object to be validated.
	 * @param dao The DAO object
	 * @param operation The operation(Add/Edit) that is to be performed.
	 * @return True if all the enumerated value attributes contain valid values
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected abstract boolean validate(Object obj, DAO dao, String operation) throws BizLogicException;

	/**
	 * This method set privilege to the user.
	 * @param dao The DAO object
	 * @param privilegeName privilege Name
	 * @param objectType object Type
	 * @param objectIds object Ids
	 * @param userId user Id
	 * @param roleId role Id
	 * @param assignToUser assign To User
	 * @param assignOperation Operation
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	abstract protected void setPrivilege(DAO dao, String privilegeName, Class objectType,
			Long[] objectIds, Long userId, String roleId, boolean assignToUser,
			boolean assignOperation) throws BizLogicException;

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * delete(Object obj) throws UserNotAuthorizedException,BizLogicException
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	public void delete(Object obj, int daoType) throws
			BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(null);
			delete(obj, dao);
			dao.commit();
			//refresh the index for titli search
			if(TitliResultGroup.isTitliConfigured == true)
	        {
	        	refreshTitliSearchIndex(TitliSearchConstants.TITLI_DELETE_OPERATION, obj);
	        }
		}
		catch (DAOException ex)
		{			
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.delete.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			logger.debug("Error in delete");
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.delete.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.delete.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}
	}
	
	
	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @throws BizLogicException BizLogic Exception
	 **/
	public void delete(Object obj) throws BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(null);
			delete(obj, dao);
			dao.commit();
			//refresh the index for titli search
			//			refreshTitliSearchIndex(Constants.TITLI_DELETE_OPERATION, obj);
		}
		catch (DAOException ex)
		{
			
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.delete.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			logger.debug("Error in delete");
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.delete.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.delete.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}
	}
	
	

	/**
	 * This method gives the error message.
	 * This method should be overrided for customizing error message
	 * @param exception - Exception
	 * @param obj - Object
	 * @param operation operation
	 * @return - error message string
	 */
	/*public String getErrorMessage(DAOException exception, Object obj, String operation)
	{
		String errMsg;

		if (exception.getWrapException() == null)
		{
			errMsg = exception.getMessage();
		}
		else
		{
			errMsg = formatException(exception.getWrapException(), obj, operation);
		}

		return errMsg;
	}*/

	/**
	 * This method inserts object. If insert only is true then insert of Defaultbiz logic is called.
	 * @param obj The object to be inserted
	 * @param sessionDataBean  session specific data
	 * @param daoType Type of dao (Hibernate or JDBC)
	 * @param isInsertOnly If insert only is true then insert of Defaultbiz logic is called
	 * @deprecated : This method uses daoType argument which is not required anymore,please use method 
	 * insert(Object obj, SessionDataBean sessionDataBean,boolean isInsertOnly) 
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	private void insert(Object obj, SessionDataBean sessionDataBean, int daoType,
			boolean isInsertOnly) throws BizLogicException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(sessionDataBean);
			// Authorization to ADD object checked here
			if (isAuthorized(dao, obj, sessionDataBean))
			{
				validate(obj, dao, Constants.ADD);
				preInsert(obj, dao, sessionDataBean);
				insert(obj, sessionDataBean, isInsertOnly, dao);
				dao.commit();
				//refresh the index for titli search
		        if(TitliResultGroup.isTitliConfigured == true)
		        {
		        	refreshTitliSearchIndex(TitliSearchConstants.TITLI_INSERT_OPERATION, obj);
		        }
				postInsert(obj, dao, sessionDataBean);
			}
		}
		catch (DAOException exception)
		{			
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			logger.debug("Error in insert");
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
			throw new BizLogicException(errorKey,exception, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				//TODO ERROR Handling
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			long endTime = System.currentTimeMillis();
			logger.info("EXECUTE TIME FOR ACTION - " + this.getClass().getSimpleName() + " : "
					+ (endTime - startTime));
		}
	}
	
	
	
	/**
	 * This method inserts object. If insert only is true then insert of Defaultbiz logic is called.
	 * @param obj The object to be inserted
	 * @param sessionDataBean  session specific data
	 * @param isInsertOnly If insert only is true then insert of Defaultbiz logic is called
	 * @throws BizLogicException Wrapping DAO exception into Bizlogic exception
	 */
	private void insert(Object obj, SessionDataBean sessionDataBean,boolean isInsertOnly)
	throws BizLogicException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao= daofactory.getDAO();
			dao.openSession(sessionDataBean);
			// Authorization to ADD object checked here
			if (isAuthorized(dao, obj, sessionDataBean))
			{
				validate(obj, dao, Constants.ADD);
				preInsert(obj, dao, sessionDataBean);
				insert(obj, sessionDataBean, isInsertOnly, dao);
				dao.commit();
				postInsert(obj, dao, sessionDataBean);
			}
		}
		catch (DAOException exception)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			logger.debug("Error in insert");
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
			throw new BizLogicException(errorKey,exception, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			long endTime = System.currentTimeMillis();
			logger.info("EXECUTE TIME FOR ACTION - " + this.getClass().getSimpleName() + " : "
					+ (endTime - startTime));
		}
	}


	/**
	 * This method insert collection of objects.
	 * @param objCollection Collection of objects to be inserted
	 * @param sessionDataBean  session specific data
	 * @param daoType Type of dao (Hibernate or JDBC)
	 * @param isInsertOnly If insert only is true then insert of Defaultbiz logic is called
	 * @throws BizLogicException Wrapping DAO exception into Bizlogic exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * insert(Collection<AbstractDomainObject> objCollection,SessionDataBean sessionDataBean,
	 * boolean isInsertOnly)throws BizLogicException, UserNotAuthorizedException
	 */
	public final void insert(Collection<AbstractDomainObject> objCollection,
			SessionDataBean sessionDataBean, int daoType, boolean isInsertOnly)
			throws BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(sessionDataBean);

			preInsert(objCollection, dao, sessionDataBean);
			insertMultiple(objCollection, dao, sessionDataBean);
			dao.commit();
			if(TitliResultGroup.isTitliConfigured == true)
			{
				for(AbstractDomainObject obj : objCollection)
				{
				    refreshTitliSearchIndex(TitliSearchConstants.TITLI_INSERT_OPERATION, obj);
				}
			}
			postInsert(objCollection, dao, sessionDataBean);
		}
		catch (DAOException ex)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			logger.debug("Error in insert");
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}
	}

	/**
	 * This method insert collection of objects.
	 * @param objCollection Collection of objects to be inserted
	 * @param sessionDataBean  session specific data
	 * @param isInsertOnly If insert only is true then insert of Defaultbiz logic is called
	 * @throws BizLogicException Wrapping DAO exception into Bizlogic exception
	 */
	public final void insert(Collection<AbstractDomainObject> objCollection,
			SessionDataBean sessionDataBean, boolean isInsertOnly)
			throws BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(sessionDataBean);

			preInsert(objCollection, dao, sessionDataBean);
			insertMultiple(objCollection, dao, sessionDataBean);
			dao.commit();
			postInsert(objCollection, dao, sessionDataBean);
		}
		catch (DAOException ex)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			logger.debug("Error in insert");
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.insert.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}
	}

	/**
	 * This method insert collection of objects.
	 * @param objCollection Collection of objects to be inserted
	 * @param sessionDataBean  session specific data
	 * @param dao object
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	public final void insertMultiple(Collection<AbstractDomainObject> objCollection,
			DAO dao, SessionDataBean sessionDataBean) throws BizLogicException
	{
		//  Authorization to ADD multiple objects (e.g. Aliquots) checked here
		for (AbstractDomainObject obj : objCollection)
		{
			if (!isAuthorized(dao, obj, sessionDataBean))
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.multinsert.error");
				throw new BizLogicException(errorKey,null, "AbstractBizLogic-User Not Authorized");
			}
			else
			{
				validate(obj, dao, Constants.ADD);
			}
		}
		for (AbstractDomainObject obj : objCollection)
		{
			insert(obj, sessionDataBean, false, dao);
		}
	}

	/**
	 * @param obj object to be insert and validate
	 * @param sessionDataBean session specific Data
	 * @param isInsertOnly If insert only is true then insert of Defaultbiz logic is called
	 * @param dao The dao object
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	private void insert(Object obj, SessionDataBean sessionDataBean, boolean isInsertOnly,
			DAO dao) throws BizLogicException
	{
		if (isInsertOnly)
		{
			insert(obj, dao);
		}
		else
		{
			insert(obj, dao, sessionDataBean);
		}
	}

	/**
	 * This method insert object.
	 * @param obj object to be insert
	 * @param sessionDataBean session specific Data
	 * @param daoType dao Type
	 * @exception BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * insert(Object obj, SessionDataBean sessionDataBean)
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	public final void insert(Object obj, SessionDataBean sessionDataBean, int daoType)
			throws BizLogicException
	{
		insert(obj, sessionDataBean, daoType, false);
	}

	/**
	 * This method insert object.
	 * @param obj object to be insert
	 * @param sessionDataBean session specific Data
	 * @exception BizLogicException BizLogic Exception
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	public final void insert(Object obj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		insert(obj, sessionDataBean,false);
	}


	/**
	 * This method insert object.
	 * @param obj object to be insert
	 * @param daoType dao Type
	 * @throws BizLogicException Generic BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * insert(Object obj) throws BizLogicException,UserNotAuthorizedException
	 */
	public final void insert(Object obj, int daoType) throws BizLogicException
	{
		insert(obj, null, daoType, true);
	}


	/**
	 * This method insert object.
	 * @param obj object to be insert
	 * @throws BizLogicException BizLogic Exception
	 */
	public final void insert(Object obj) throws BizLogicException
	{
		insert(obj, null, true);
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @param oldObj old Object
	 * @param daoType daoType
	 * @param sessionDataBean session specific Data
	 * @param isUpdateOnly isUpdateOnly
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method 
	 * update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean, boolean isUpdateOnly)
	 * throws BizLogicException,UserNotAuthorizedException
	 */
	private void update(Object currentObj, Object oldObj, int daoType,
			SessionDataBean sessionDataBean, boolean isUpdateOnly) throws BizLogicException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(sessionDataBean);

			// Authorization to UPDATE object checked here
			if (!isAuthorized(dao, currentObj, sessionDataBean))
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
				throw new BizLogicException(errorKey,null, "AbstractBizLogic-User not authorized");
			}
			else
			{
				validate(currentObj, dao, Constants.EDIT);
				preUpdate(dao, currentObj, oldObj, sessionDataBean);
				if (isUpdateOnly)
				{
					update(dao, currentObj);
				}
				else
				{
					update(dao, currentObj, oldObj, sessionDataBean);
				}
				dao.commit();
				//refresh the index for titli search
		        if(TitliResultGroup.isTitliConfigured == true)
		        {
		        	refreshTitliSearchIndex(TitliSearchConstants.TITLI_UPDATE_OPERATION, currentObj);
		        }
				postUpdate(dao, currentObj, oldObj, sessionDataBean);
			}
		}
		catch (DAOException ex)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{

				ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			long endTime = System.currentTimeMillis();
			logger.info("EXECUTE TIME FOR ACTION - " + this.getClass().getSimpleName() + " : "
					+ (endTime - startTime));
		}
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @param oldObj old Object
	 * @param sessionDataBean session specific Data
	 * @param isUpdateOnly isUpdateOnly
	 * @throws BizLogicException BizLogic Exception
	 */
	private void update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean, boolean isUpdateOnly)
	throws BizLogicException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(sessionDataBean);

			// Authorization to UPDATE object checked here
			if (!isAuthorized(dao, currentObj, sessionDataBean))
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
				throw new BizLogicException(errorKey,null, "AbstractBizLogic:User Not Authorized");
			}
			else
			{
				validate(currentObj, dao, Constants.EDIT);
				preUpdate(dao, currentObj, oldObj, sessionDataBean);
				if (isUpdateOnly)
				{
					update(dao, currentObj);
				}
				else
				{
					update(dao, currentObj, oldObj, sessionDataBean);
				}
				dao.commit();
				postUpdate(dao, currentObj, oldObj, sessionDataBean);
			}
		}
		catch (DAOException ex)
		{			
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{

				ErrorKey errorKey=ErrorKey.getErrorKey("biz.update.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
			long endTime = System.currentTimeMillis();
			logger.info("EXECUTE TIME FOR ACTION - " + this.getClass().getSimpleName() + " : "
					+ (endTime - startTime));
		}
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @param oldObj old Object
	 * @param daoType dao Type
	 * @param sessionDataBean session specific Data
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean)
	 * throws BizLogicException, UserNotAuthorizedException
	 */
	public final void update(Object currentObj, Object oldObj, int daoType,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		update(currentObj, oldObj, daoType, sessionDataBean, false);
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @param oldObj old Object
	 * @param sessionDataBean session specific Data
	 * @throws BizLogicException BizLogic Exception
	 * */
	public final void update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean)
	throws BizLogicException
	{
		update(currentObj, oldObj, sessionDataBean, false);
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * update(Object currentObj) throws BizLogicException,UserNotAuthorizedException
	 */
	public final void update(Object currentObj, int daoType) throws BizLogicException
	{
		update(currentObj, null, daoType, null, true);
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	public final void update(Object currentObj) throws BizLogicException
	{
		update(currentObj, null,null, true);
	}

	/**
	 * This method set privilege to user.
	 * @param daoType dao Type
	 * @param privilegeName privilege Name
	 * @param objectType object Type
	 * @param objectIds object Ids
	 * @param userId user Id
	 * @param sessionDataBean session specific Data
	 * @param roleId role Id
	 * @param assignToUser assign To User
	 * @param assignOperation Operation
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * setPrivilege(String privilegeName, Class objectType,	Long[] objectIds, Long userId,
	 * SessionDataBean sessionDataBean, String roleId,boolean assignToUser, boolean assignOperation)
	 */
	public final void setPrivilege(int daoType, String privilegeName, Class objectType,
			Long[] objectIds, Long userId, SessionDataBean sessionDataBean, String roleId,
			boolean assignToUser, boolean assignOperation) throws BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao = daofactory.getDAO();
			logger.debug(" privilegeName:" + privilegeName + " objectType:" + objectType
					+ " objectIds:" + edu.wustl.common.util.Utility.getArrayString(objectIds)
					+ " userId:" + userId + " roleId:" + roleId
					+ " assignToUser:" + assignToUser);
			dao.openSession(sessionDataBean);
			setPrivilege(dao, privilegeName, objectType, objectIds, userId, roleId, assignToUser,
					assignOperation);
			dao.commit();
		}
		catch (DAOException ex)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.setpriv.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}

			ErrorKey errorKey=ErrorKey.getErrorKey("biz.setpriv.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.setpriv.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}
	}

	/**
	 * This method set privilege to user.
	 * @param privilegeName privilege Name
	 * @param objectType object Type
	 * @param objectIds object Ids
	 * @param userId user Id
	 * @param sessionDataBean session specific Data
	 * @param roleId role Id
	 * @param assignToUser assign To User
	 * @param assignOperation Operation
	 * @throws BizLogicException BizLogic Exception
	 */
	public final void setPrivilege(String privilegeName, Class objectType,
			Long[] objectIds, Long userId, SessionDataBean sessionDataBean, String roleId,
			boolean assignToUser, boolean assignOperation) throws BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao = daofactory.getDAO();
			logger.debug(" privilegeName:" + privilegeName + " objectType:" + objectType
					+ " objectIds:" + edu.wustl.common.util.Utility.getArrayString(objectIds)
					+ " userId:" + userId + " roleId:" + roleId
					+ " assignToUser:" + assignToUser);
			dao.openSession(sessionDataBean);
			setPrivilege(dao, privilegeName, objectType, objectIds, userId, roleId, assignToUser,
					assignOperation);
			dao.commit();
		}
		catch (DAOException ex)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.setpriv.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}

			ErrorKey errorKey=ErrorKey.getErrorKey("biz.setpriv.error");
			throw new BizLogicException(errorKey,ex, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.setpriv.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}
	}

	
	
	
	/**
	 * This method formats Exception.
	 * @param exception exception
	 * @param obj object
	 * @param operation operation
	 * @return error message.
	 */
	public String formatException(Exception exception, Object obj, String operation)
	{
		String errMsg;
		String tableName = null;
		try
		{
			if (exception == null)
			{
				errMsg = null;
			}
			String roottableName = null;
			// Get ExceptionFormatter
			ExceptionFormatter ef = ExceptionFormatterFactory.getFormatter(exception);
			
			IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
			DAO dao = daofactory.getDAO();
			IConnectionManager connectionManager = dao.getConnectionManager();
			
			// call for Formating Message
			if (ef != null)
			{
				roottableName = HibernateMetaData.getRootTableName(obj.getClass());
				tableName = HibernateMetaData.getTableName(obj.getClass());
				Object[] arguments = {roottableName, connectionManager.currentSession().connection(),
						tableName};
				errMsg = ef.formatMessage(exception, arguments);
			}
			else
			{
				errMsg = exception.getMessage();
			}
		}
		catch (Exception e)
		{
			logger.error(exception.getMessage(), exception);
			// if Error occured while formating message then get message
			// formatted through Default Formatter
			String [] arg = {operation, tableName};
			errMsg = new DefaultExceptionFormatter().getErrorMessage("Err.SMException.01", arg);
		}
		return errMsg;
	}

	/**
	 * refresh the titli search index to reflect the changes in the database.
	 * @param operation the operation to be performed : "insert", "update" or "delete"
	 * @param obj the object correspondig to the record to be refreshed
	 */
	private void refreshTitliSearchIndex(String operation, Object obj)
	{            
		try
		{ 
			TitliInterface titli = Titli.getInstance();
			Name dbName = (titli.getDatabases().keySet().toArray(new Name[0]))[0]; 
			
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("titli.properties"));
			String className=prop.getProperty("titliObjectMetadataImplementor");

			ObjectMetadataInterface objectMetadataInterface = (ObjectMetadataInterface)Class.forName(className).newInstance();
			String tableName = objectMetadataInterface.getTableName(obj);
			System.out.println("tableName: "+tableName);
			
			if(!tableName.equalsIgnoreCase(""))
			{
				String id = objectMetadataInterface.getUniqueIdentifier(obj);;
				System.out.println("id: "+id);
							
				Map<Name, String> uniqueKey = new HashMap<Name, String>();
				uniqueKey.put(new Name(Constants.IDENTIFIER), id);
				
				String mainTableName = TitliTableMapper.getInstance().returnMainTable(tableName);
				if(mainTableName != null)
				{
					tableName = mainTableName;
				}
					
				RecordIdentifier recordIdentifier = new RecordIdentifier(dbName,new Name(tableName),uniqueKey);
			
				IndexRefresherInterface indexRefresher = titli.getIndexRefresher();
				
				if (operation != null && operation.equalsIgnoreCase(TitliSearchConstants.TITLI_INSERT_OPERATION)) 
				{
					indexRefresher.insert(recordIdentifier);
				}
				else if (operation != null	&& operation.equalsIgnoreCase(TitliSearchConstants.TITLI_UPDATE_OPERATION)) 
				{  
					indexRefresher.update(recordIdentifier);
				}
				else if (operation != null	&& operation.equalsIgnoreCase(TitliSearchConstants.TITLI_DELETE_OPERATION)) 
				{
					indexRefresher.delete(recordIdentifier);
				}
			}
		} 
		catch (TitliException e) 
		{
			logger.error("Titli search index cound not be refreshed for opeartion "+operation, e);
		}
		catch(IOException e)
		{
			logger.error("Titli search index cound not be refreshed for opeartion "+operation, e);
		}
		catch(ClassNotFoundException e)
		{
			logger.error("Titli search index cound not be refreshed for opeartion "+operation, e);
		}
		catch(InstantiationException e)
		{
			logger.error("Titli search index cound not be refreshed for opeartion "+operation, e);
		}
		catch(IllegalAccessException e)
		{
			logger.error("Titli search index cound not be refreshed for opeartion "+operation, e);
		}
		
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param className Contains class Name.
	 * @param identifier Contains the identifier.
	 * @param uiForm object of the class which implements IValueObject
	 * @throws DAOException generic DAOException
	 * @throws BizLogicException BizLogic Exception
	 * @return isSuccess
	 */
	public boolean populateUIBean(String className, Long identifier, IValueObject uiForm)
			throws BizLogicException
	{
		long startTime = System.currentTimeMillis();
		boolean isSuccess = false;

		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao= daofactory.getDAO();
			dao.openSession(null);

			Object object = dao.retrieve(className, identifier);

			if (object != null)
			{
				/*
				  If the record searched is present in the database,
				  populate the formbean with the information retrieved.
				 */
				AbstractDomainObject abstractDomain = (AbstractDomainObject) object;

				prePopulateUIBean(abstractDomain, uiForm);
				uiForm.setAllValues(abstractDomain);
				postPopulateUIBean(abstractDomain, uiForm);
				isSuccess = true;
			}
		}
		catch (DAOException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.popbean.error");
			throw new BizLogicException(errorKey,daoExp, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.popbean.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}

		String simpleClassName = Utility.parseClassName(className);

		long endTime = System.currentTimeMillis();
		logger.info("EXECUTE TIME FOR RETRIEVE IN EDIT FOR UI - " + simpleClassName + " : "
				+ (endTime - startTime));

		return isSuccess;
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param className Contains the class Name.
	 * @param identifier identifier.
	 * @param uiForm object of the class which implements IValueObject
	 * @throws BizLogicException BizLogic Exception
	 * @return AbstractDomainObject
	 */
	public AbstractDomainObject populateDomainObject(String className, Long identifier,
			IValueObject uiForm) throws BizLogicException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao =null;
		AbstractDomainObject abstractDomain = null;

		try
		{
			dao = daofactory.getDAO();
			dao.openSession(null);

			Object object = dao.retrieve(className, identifier);

			if (object != null)
			{
				/*
				  If the record searched is present in the database,
				  populate the formbean with the information retrieved.
				 */
				abstractDomain = (AbstractDomainObject) object;
				if (abstractDomain != null)
				{
					abstractDomain.setAllValues(uiForm);
				}
			}
		}
		catch (DAOException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.popdomain.error");
			throw new BizLogicException(errorKey,daoExp, "AbstractBizLogic");
		}
		catch (AssignDataException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			ErrorKey errorKey=ErrorKey.getErrorKey("biz.popdomain.error");
			throw new BizLogicException(errorKey,daoExp, "AbstractBizLogic");
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.popdomain.error");
				throw new BizLogicException(errorKey,daoEx, "AbstractBizLogic");
			}
		}

		String simpleClassName = Utility.parseClassName(className);

		long endTime = System.currentTimeMillis();
		logger.info("EXECUTE TIME FOR RETRIEVE IN EDIT FOR DB - " + simpleClassName + " : "
				+ (endTime - startTime));

		return abstractDomain;
	}

	/**
	 * This method gets called before populateUIBean method.
	 * Any logic before updating uiForm can be included here.
	 * @param domainObj object of type AbstractDomainObject
	 * @param uiForm object of the class which implements IValueObject
	 * @throws BizLogicException BizLogic Exception
	 */
	protected abstract void prePopulateUIBean(AbstractDomainObject domainObj, IValueObject uiForm)
			throws BizLogicException;

	/**
	 * This method gets called after populateUIBean method.
	 * Any logic after populating  object uiForm can be included here.
	 * @param domainObj object of type AbstractDomainObject
	 * @param uiForm object of the class which implements IValueObject
	 * @throws BizLogicException BizLogic Exception
	 */
	protected abstract void postPopulateUIBean(AbstractDomainObject domainObj, IValueObject uiForm)
			throws BizLogicException;

	/**
	 * This method checks Read Denied.
	 * @return Read Denied or not.
	 */
	public abstract boolean isReadDeniedTobeChecked();

	/**
	 * This method gets Read Denied Privilege Name.
	 * @return Denied Privilege Name.
	 */
	public abstract String getReadDeniedPrivilegeName();
	/**
	 *
	 */
	public boolean hasPrivilegeToView(String objName, Long identifier,
			SessionDataBean sessionDataBean)
	{
		return false;
	}

	public boolean isAuthorized(DAO dao, Object domainObject,
			SessionDataBean sessionDataBean) throws BizLogicException
	{

		return false;
	}
}