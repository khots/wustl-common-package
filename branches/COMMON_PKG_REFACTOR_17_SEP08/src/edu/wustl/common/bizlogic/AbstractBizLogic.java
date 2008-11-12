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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import titli.controller.Name;
import titli.controller.RecordIdentifier;
import titli.controller.interfaces.IndexRefresherInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.Titli;
import titli.model.TitliException;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.DAO;
import edu.wustl.common.dao.DAOConfigFactory;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.IDAOFactory;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatterFactory;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.dbmanager.DBUtil;
import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

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
	 * @throws DAOException This will get thrown if application failed to
	 *  communicate with database objects
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * */
	protected abstract void preInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws DAOException, UserNotAuthorizedException;

	/**
	 * Inserts an object into the database.
	 * @param obj The object to be inserted.
	 * @param dao the dao object.
	 * @param sessionDataBean session specific data
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	protected abstract void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws DAOException, UserNotAuthorizedException;

	/**
	 * Inserts an object into the database.
	 * @param obj The object to be inserted.
	 * @param dao the dao object.
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	protected abstract void insert(Object obj, DAO dao) throws DAOException,
			UserNotAuthorizedException;

	/**
	 * This method gets called after insert method.
	 * Any logic after insertnig object in database can be included here.
	 * @param obj The inserted object.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * */
	protected abstract void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws DAOException, UserNotAuthorizedException;

	/**
	 * This method gets called after insert method.
	 * Any logic after insertnig object in database can be included here.
	 * @param objCollection Collection of object to be inserted
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * */
	protected abstract void postInsert(Collection<AbstractDomainObject> objCollection, DAO dao,
			SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @param dao the dao object
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	protected abstract void delete(Object obj, DAO dao) throws DAOException,
			UserNotAuthorizedException;

	/**
	 * This method gets called before update method.
	 * Any logic before updating into database can be included here.
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * */
	protected abstract void preUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;

	/**
	 * Updates an objects into the database.
	 * @param dao the dao object.
	 * @param obj The object to be updated into the database.
	 * @param oldObj The old object.
	 * @param sessionDataBean  session specific Data
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	protected abstract void update(DAO dao, Object obj, Object oldObj,
			SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;

	/**
	 * This method gets called after update method.
	 * Any logic after updating into database can be included here.
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	protected abstract void postUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;

	/**
	 * Updates an objects into the database.
	 * @param dao the dao object
	 * @param obj The object to be updated.
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	protected abstract void update(DAO dao, Object obj) throws DAOException,
			UserNotAuthorizedException;

	/**
	 * Validates the domain object for enumerated values.
	 * @param obj The domain object to be validated.
	 * @param dao The DAO object
	 * @param operation The operation(Add/Edit) that is to be performed.
	 * @return True if all the enumerated value attributes contain valid values
	 * @throws DAOException generic DAOException
	 */
	protected abstract boolean validate(Object obj, DAO dao, String operation) throws DAOException;

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
	 * @throws SMException generic SMException
	 * @throws DAOException generic DAOException
	 */
	protected void setPrivilege(DAO dao, String privilegeName, Class objectType,
			Long[] objectIds, Long userId, String roleId, boolean assignToUser,
			boolean assignOperation) throws SMException, DAOException{}

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @param daoType dao Type
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method 
	 * delete(Object obj) throws UserNotAuthorizedException,BizLogicException
	 */
	public void delete(Object obj, int daoType) throws UserNotAuthorizedException,
			BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
			dao.openSession(null);
			delete(obj, dao);
			dao.commit();
			//refresh the index for titli search
			//			refreshTitliSearchIndex(Constants.TITLI_DELETE_OPERATION, obj);
		}
		catch (DAOException ex)
		{
			String errMsg = getErrorMessage(ex, obj, "Deleting");
			if (errMsg == null)
			{
				errMsg = ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			logger.debug("Error in delete");
			throw new BizLogicException(errMsg, ex);
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
				throw new BizLogicException();
			}
		}
	}
	
	
	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException BizLogic Exception
	 **/
	public void delete(Object obj) throws UserNotAuthorizedException,
			BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
			dao.openSession(null);
			delete(obj, dao);
			dao.commit();
			//refresh the index for titli search
			//			refreshTitliSearchIndex(Constants.TITLI_DELETE_OPERATION, obj);
		}
		catch (DAOException ex)
		{
			String errMsg = getErrorMessage(ex, obj, "Deleting");
			if (errMsg == null)
			{
				errMsg = ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			logger.debug("Error in delete");
			throw new BizLogicException(errMsg, ex);
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
				throw new BizLogicException();
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
	public String getErrorMessage(DAOException exception, Object obj, String operation)
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
	}

	/**
	 * This method inserts object. If insert only is true then insert of Defaultbiz logic is called.
	 * @param obj The object to be inserted
	 * @param sessionDataBean  session specific data
	 * @param daoType Type of dao (Hibernate or JDBC)
	 * @param isInsertOnly If insert only is true then insert of Defaultbiz logic is called
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException Wrapping DAO exception into Bizlogic exception
	 * @deprecated : This method uses daoType argument which is not required anymore,please use method 
	 * insert(Object obj, SessionDataBean sessionDataBean,boolean isInsertOnly) 
	 * throws UserNotAuthorizedException, BizLogicException
	 */
	private void insert(Object obj, SessionDataBean sessionDataBean, int daoType,
			boolean isInsertOnly) throws UserNotAuthorizedException, BizLogicException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
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
			String errMsg = getErrorMessage(exception, obj, "Inserting");
			if (errMsg == null)
			{
				errMsg = exception.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			logger.debug("Error in insert");
			throw new BizLogicException(errMsg, exception);
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
				throw new BizLogicException();
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
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException Wrapping DAO exception into Bizlogic exception
	 */
	private void insert(Object obj, SessionDataBean sessionDataBean,boolean isInsertOnly)
	throws UserNotAuthorizedException, BizLogicException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
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
			String errMsg = getErrorMessage(exception, obj, "Inserting");
			if (errMsg == null)
			{
				errMsg = exception.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			logger.debug("Error in insert");
			throw new BizLogicException(errMsg, exception);
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
				throw new BizLogicException();
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
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException Wrapping DAO exception into Bizlogic exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method 
	 * insert(Collection<AbstractDomainObject> objCollection,SessionDataBean sessionDataBean, 
	 * boolean isInsertOnly)throws BizLogicException, UserNotAuthorizedException
	 */
	public final void insert(Collection<AbstractDomainObject> objCollection,
			SessionDataBean sessionDataBean, int daoType, boolean isInsertOnly)
			throws BizLogicException, UserNotAuthorizedException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
			dao.openSession(sessionDataBean);

			preInsert(objCollection, dao, sessionDataBean);
			insertMultiple(objCollection, dao, sessionDataBean);
			dao.commit();
			postInsert(objCollection, dao, sessionDataBean);
		}
		catch (DAOException ex)
		{
			String errMsg = getErrorMessage(ex, objCollection, "Inserting");
			if (errMsg == null)
			{
				errMsg = ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			logger.debug("Error in insert");
			throw new BizLogicException(errMsg, ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				String errMsg = getErrorMessage(daoEx, objCollection, "Session Close");
				errMsg = daoEx.getMessage();
				throw new BizLogicException(errMsg, daoEx);
			}
		}
	}
	
	
	/**
	 * This method insert collection of objects.
	 * @param objCollection Collection of objects to be inserted
	 * @param sessionDataBean  session specific data
	 * @param isInsertOnly If insert only is true then insert of Defaultbiz logic is called
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException Wrapping DAO exception into Bizlogic exception
	 */
	public final void insert(Collection<AbstractDomainObject> objCollection,
			SessionDataBean sessionDataBean, boolean isInsertOnly)
			throws BizLogicException, UserNotAuthorizedException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
			dao.openSession(sessionDataBean);

			preInsert(objCollection, dao, sessionDataBean);
			insertMultiple(objCollection, dao, sessionDataBean);
			dao.commit();
			postInsert(objCollection, dao, sessionDataBean);
		}
		catch (DAOException ex)
		{
			String errMsg = getErrorMessage(ex, objCollection, "Inserting");
			if (errMsg == null)
			{
				errMsg = ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			logger.debug("Error in insert");
			throw new BizLogicException(errMsg, ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				String errMsg = getErrorMessage(daoEx, objCollection, "Session Close");
				errMsg = daoEx.getMessage();
				throw new BizLogicException(errMsg, daoEx);
			}
		}
	}
	
	

	/**
	 * This method insert collection of objects.
	 * @param objCollection Collection of objects to be inserted
	 * @param sessionDataBean  session specific data
	 * @param dao object
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws DAOException Wrapping DAO exception into Bizlogic exception
	 */
	public final void insertMultiple(Collection<AbstractDomainObject> objCollection,
			DAO dao, SessionDataBean sessionDataBean) throws DAOException,
			UserNotAuthorizedException
	{
		//  Authorization to ADD multiple objects (e.g. Aliquots) checked here
		for (AbstractDomainObject obj : objCollection)
		{
			if (!isAuthorized(dao, obj, sessionDataBean))
			{
				throw new UserNotAuthorizedException();
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
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	private void insert(Object obj, SessionDataBean sessionDataBean, boolean isInsertOnly,
			DAO dao) throws DAOException, UserNotAuthorizedException
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
	 * @exception UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method 
	 * insert(Object obj, SessionDataBean sessionDataBean)
	 * throws BizLogicException, UserNotAuthorizedException.
	 */
	public final void insert(Object obj, SessionDataBean sessionDataBean, int daoType)
			throws BizLogicException, UserNotAuthorizedException
	{
		insert(obj, sessionDataBean, daoType, false);
	}
	
	
	/**
	 * This method insert object.
	 * @param obj object to be insert
	 * @param sessionDataBean session specific Data
	 * @exception BizLogicException BizLogic Exception
	 * @exception UserNotAuthorizedException User Not Authorized Exception
	 */
	public final void insert(Object obj, SessionDataBean sessionDataBean)
			throws BizLogicException, UserNotAuthorizedException
	{
		insert(obj, sessionDataBean,false);
	}
	
	

	/**
	 * This method insert object.
	 * @param obj object to be insert
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method 
	 * insert(Object obj) throws BizLogicException,UserNotAuthorizedException
	 */
	public final void insert(Object obj, int daoType) throws BizLogicException,
			UserNotAuthorizedException
	{
		insert(obj, null, daoType, true);
	}


	/**
	 * This method insert object.
	 * @param obj object to be insert
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	public final void insert(Object obj) throws BizLogicException,
			UserNotAuthorizedException
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
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method 
	 * update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean, boolean isUpdateOnly)
	 * throws BizLogicException,UserNotAuthorizedException
	 */
	private void update(Object currentObj, Object oldObj, int daoType,
			SessionDataBean sessionDataBean, boolean isUpdateOnly) throws BizLogicException,
			UserNotAuthorizedException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
			dao.openSession(sessionDataBean);

			// Authorization to UPDATE object checked here
			if (!isAuthorized(dao, currentObj, sessionDataBean))
			{
				throw new UserNotAuthorizedException();
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
			//added to format constrainviolation message
			String errMsg = getErrorMessage(ex, currentObj, "Updating");
			if (errMsg == null)
			{
				errMsg = ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			//TODO ERROR Handling
			throw new BizLogicException(errMsg, ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{

				throw new BizLogicException();
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
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	private void update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean, boolean isUpdateOnly)
	throws BizLogicException,UserNotAuthorizedException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
			dao.openSession(sessionDataBean);

			// Authorization to UPDATE object checked here
			if (!isAuthorized(dao, currentObj, sessionDataBean))
			{
				throw new UserNotAuthorizedException();
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
			//added to format constrainviolation message
			String errMsg = getErrorMessage(ex, currentObj, "Updating");
			if (errMsg == null)
			{
				errMsg = ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch (DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			//TODO ERROR Handling
			throw new BizLogicException(errMsg, ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{

				throw new BizLogicException();
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
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean)
	 * throws BizLogicException, UserNotAuthorizedException
	 */
	public final void update(Object currentObj, Object oldObj, int daoType,
			SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException
	{
		update(currentObj, oldObj, daoType, sessionDataBean, false);
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @param oldObj old Object
	 * @param sessionDataBean session specific Data
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * */
	public final void update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean)
	throws BizLogicException, UserNotAuthorizedException
	{
		update(currentObj, oldObj, sessionDataBean, false);
	}
	
	
	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * update(Object currentObj) throws BizLogicException,UserNotAuthorizedException
	 */
	public final void update(Object currentObj, int daoType) throws BizLogicException,
			UserNotAuthorizedException
	{
		update(currentObj, null, daoType, null, true);
	}

	
	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	public final void update(Object currentObj) throws BizLogicException,
			UserNotAuthorizedException
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
	 * @throws SMException generic SMException
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * setPrivilege(String privilegeName, Class objectType,	Long[] objectIds, Long userId, 
	 * SessionDataBean sessionDataBean, String roleId,boolean assignToUser, boolean assignOperation)
	 * throws SMException, BizLogicException
	 */
	public final void setPrivilege(int daoType, String privilegeName, Class objectType,
			Long[] objectIds, Long userId, SessionDataBean sessionDataBean, String roleId,
			boolean assignToUser, boolean assignOperation) throws SMException, BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
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
				//TODO ERROR Handling
				throw new BizLogicException(daoEx.getMessage(), daoEx);
				//throw new BizLogicException(ex.getMessage(), ex);
			}

			throw new BizLogicException(ex.getMessage(), ex);
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
				throw new BizLogicException("Unknown Error");
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
	 * @throws SMException generic SMException
	 * @throws BizLogicException BizLogic Exception
	 */
	public final void setPrivilege(String privilegeName, Class objectType,
			Long[] objectIds, Long userId, SessionDataBean sessionDataBean, String roleId,
			boolean assignToUser, boolean assignOperation) throws SMException, BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
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
				//TODO ERROR Handling
				throw new BizLogicException(daoEx.getMessage(), daoEx);
				//throw new BizLogicException(ex.getMessage(), ex);
			}

			throw new BizLogicException(ex.getMessage(), ex);
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
				throw new BizLogicException("Unknown Error");
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
		if (exception == null)
		{
			errMsg = null;
		}
		String roottableName = null;
		String tableName = null;
		try
		{
			// Get ExceptionFormatter
			ExceptionFormatter ef = ExceptionFormatterFactory.getFormatter(exception);
			// call for Formating Message
			if (ef != null)
			{
				roottableName = HibernateMetaData.getRootTableName(obj.getClass());
				tableName = HibernateMetaData.getTableName(obj.getClass());
				Object[] arguments = {roottableName, DBUtil.currentSession().connection(),
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
			String tableName = HibernateMetaData.getTableName(obj.getClass()).toLowerCase();
			String id = ((AbstractDomainObject) obj).getId().toString();

			Map<Name, String> uniqueKey = new HashMap<Name, String>();
			uniqueKey.put(new Name(Constants.IDENTIFIER), id);

			RecordIdentifier recordIdentifier = new RecordIdentifier(dbName, new Name(tableName),
					uniqueKey);

			IndexRefresherInterface indexRefresher = titli.getIndexRefresher();

			if (operation != null && operation.equalsIgnoreCase(Constants.TITLI_INSERT_OPERATION))
			{
				indexRefresher.insert(recordIdentifier);
			}
			else if (operation != null
					&& operation.equalsIgnoreCase(Constants.TITLI_UPDATE_OPERATION))
			{
				indexRefresher.update(recordIdentifier);
			}
			else if (operation != null
					&& operation.equalsIgnoreCase(Constants.TITLI_DELETE_OPERATION))
			{
				indexRefresher.delete(recordIdentifier);
			}
		}
		catch (TitliException e)
		{
			logger.error("Titli search index cound not be refreshed for opeartion " + operation, e);
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
			throws DAOException, BizLogicException
	{
		long startTime = System.currentTimeMillis();
		boolean isSuccess = false;

		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		try
		{
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
			String errMsg = daoExp.getMessage();
			throw new BizLogicException(errMsg, daoExp);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException();
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
	 * @throws DAOException generic DAOException
	 * @throws BizLogicException BizLogic Exception
	 * @throws AssignDataException Assign Data Exception
	 * @return AbstractDomainObject
	 */
	public AbstractDomainObject populateDomainObject(String className, Long identifier,
			IValueObject uiForm) throws DAOException, BizLogicException, AssignDataException
	{
		long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao = daofactory.getDAO();
		AbstractDomainObject abstractDomain = null;

		try
		{
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
			String errMsg = daoExp.getMessage();
			throw new BizLogicException(errMsg, daoExp);
		}
		catch (AssignDataException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			throw daoExp;
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException daoEx)
			{
				throw new BizLogicException();
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
			SessionDataBean sessionDataBean) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * 
	 */
	public boolean isAuthorized(DAO dao, Object domainObject,
			SessionDataBean sessionDataBean) throws UserNotAuthorizedException,
			DAOException {
		// TODO Auto-generated method stub
		return false;
	}
}