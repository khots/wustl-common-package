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
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatterFactory;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TitliSearchConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;

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
	 * Any logic after inserting object in database can be included here.
	 * @param obj The inserted object.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws BizLogicException Generic BizLogic Exception
	 * */
	protected abstract void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException;

	/**
	 * This method gets called after insert method.
	 * Any logic after inserting object in database can be included here.
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
	protected abstract void setPrivilege(DAO dao, String privilegeName, Class objectType,
			Long[] objectIds, Long userId, String roleId, boolean assignToUser,
			boolean assignOperation) throws BizLogicException;

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,
	 * please use method, delete(Object obj)
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	public void delete(Object obj, int daoType) throws
			BizLogicException
	{
		delete(obj);
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
			dao=daofactory.getDAO();
			dao.openSession(null);
			delete(obj, dao);
			dao.commit();
			//refresh the index for titli search
			if(TitliResultGroup.isTitliConfigured)
	        {
	        	refreshTitliSearchIndex(TitliSearchConstants.TITLI_DELETE_OPERATION, obj);
	        }
		}
		catch (ApplicationException ex)
		{
			rollback(dao);
			throw getBizLogicException(ex, "biz.delete.error","Exception in delete operation.");
		}
		finally
		{
			closeSession(dao);
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
		        if(TitliResultGroup.isTitliConfigured)
		        {
		        	refreshTitliSearchIndex(TitliSearchConstants.TITLI_INSERT_OPERATION, obj);
		        }
				postInsert(obj, dao, sessionDataBean);
			}
		}
		catch (ApplicationException exception)
		{
			rollback(dao);
			throw getBizLogicException(exception, "biz.insert.error"
					,"Exception in insert operation.");
		}
		finally
		{
			closeSession(dao);
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
	 * insert(Collection objCollection,SessionDataBean sessionDataBean,boolean isInsertOnly)
	 */
	public final void insert(Collection<AbstractDomainObject> objCollection,
			SessionDataBean sessionDataBean, int daoType, boolean isInsertOnly)
			throws BizLogicException
	{
		insert(objCollection,sessionDataBean,isInsertOnly);
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
			if(TitliResultGroup.isTitliConfigured)
			{
				for(AbstractDomainObject obj : objCollection)
				{
				    refreshTitliSearchIndex(TitliSearchConstants.TITLI_INSERT_OPERATION, obj);
				}
			}
			postInsert(objCollection, dao, sessionDataBean);
		}
		catch (ApplicationException exception)
		{
			rollback(dao);
			throw getBizLogicException(exception, "biz.insert.error"
					,"Exception in inserting multiple records operation.");
		}
		finally
		{
			closeSession(dao);
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
			if (isAuthorized(dao, obj, sessionDataBean))
			{
				validate(obj, dao, Constants.ADD);
			}
			else
			{
				ErrorKey errorKey=ErrorKey.getErrorKey("biz.multinsert.error");
				throw new BizLogicException(errorKey,null, "AbstractBizLogic-User Not Authorized");
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
		insert(obj,sessionDataBean,false);
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
		insert(obj,null,true);
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
	 * @param sessionDataBean session specific Data
	 * @param isUpdateOnly isUpdateOnly
	 * @throws BizLogicException BizLogic Exception
	 */
	private void update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean, boolean isUpdateOnly)
	throws BizLogicException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao=null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(sessionDataBean);

			// Authorization to UPDATE object checked here
			if (isAuthorized(dao, currentObj, sessionDataBean))
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
				if(TitliResultGroup.isTitliConfigured)
		        {
		        	refreshTitliSearchIndex(TitliSearchConstants.TITLI_UPDATE_OPERATION, currentObj);
		        }
				postUpdate(dao, currentObj, oldObj, sessionDataBean);
			}
			else
			{
				throw getBizLogicException(null, "biz.update.error"
						,"AbstractBizLogic:User Not Authorized.");
			}
		}
		catch (ApplicationException ex)
		{
			rollback(dao);
			throw getBizLogicException(ex, "biz.update.error"
					,"Exception in update method");
		}
		finally
		{
			closeSession(dao);
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
		update(currentObj,oldObj,sessionDataBean,false);
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
		update(currentObj,null,null,true);
	}

	/**
	 * Updates an object into the database.
	 * @param currentObj The object to be updated.
	 * @throws BizLogicException BizLogic Exception
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
		setPrivilege(privilegeName, objectType, objectIds, userId, sessionDataBean, roleId,
				assignToUser, assignOperation);
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
		catch (ApplicationException exception)
		{
			rollback(dao);
			throw getBizLogicException(exception, "biz.setpriv.error"
					,"Exception in setPrivilege method");
		}
		finally
		{
			closeSession(dao);
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
			ExceptionFormatter exFormatter = ExceptionFormatterFactory.getFormatter(exception);
			IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
			DAO dao = daofactory.getDAO();
			IConnectionManager connectionManager = dao.getConnectionManager();
			// call for Formating Message
			if (exFormatter == null)
			{
				errMsg = exception.getMessage();
			}
			else
			{
				roottableName = HibernateMetaData.getRootTableName(obj.getClass());
				tableName = HibernateMetaData.getTableName(obj.getClass());
				Object[] arguments = {roottableName,
						connectionManager.currentSession().connection(),tableName};
				errMsg = exFormatter.formatMessage(exception, arguments);
			}
		}
		catch (ApplicationException except)
		{
			logger.error(except.getMessage(), except);
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
	protected void refreshTitliSearchIndex(String operation, Object obj)
	{
		try
		{
			TitliInterface titli = Titli.getInstance();
			Name dbName = (titli.getDatabases().keySet().toArray(new Name[0]))[0];

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("titli.properties"));
			String className=prop.getProperty("titliObjectMetadataImplementor");

			ObjectMetadataInterface objectMetadataInterface =
				(ObjectMetadataInterface)Class.forName(className).newInstance();
			String tableName = objectMetadataInterface.getTableName(obj);
			if(!tableName.equalsIgnoreCase(""))
			{
				String objId = objectMetadataInterface.getUniqueIdentifier(obj);
				Map<Name, String> uniqueKey = new HashMap<Name, String>();
				uniqueKey.put(new Name(Constants.IDENTIFIER), objId);
				String mainTableName = TitliTableMapper.getInstance().returnMainTable(tableName);
				if(mainTableName != null)
				{
					tableName = mainTableName;
				}

				RecordIdentifier recordIdentifier =
						new RecordIdentifier(dbName,new Name(tableName),uniqueKey);

				IndexRefresherInterface indexRefresher = titli.getIndexRefresher();
				if(operation!=null)
				{
					performOperation(operation, recordIdentifier, indexRefresher);
				}
			}
		}
		catch (Exception excep)
		{
			logger.error("Titli search index cound not be refreshed for opeartion."+operation,excep);
		}

	}

	/**
	 * This method perform insert,update or delete operation.
	 * @param operation type of operation.
	 * @param recordIdentifier object of IndexRefresherInterface.
	 * @param indexRefresher object of IndexRefresherInterface.
	 * @throws TitliException throws this exception if operation unsuccessful.
	 */
	private void performOperation(String operation, RecordIdentifier recordIdentifier,
			IndexRefresherInterface indexRefresher) throws TitliException
	{
		if (operation.equalsIgnoreCase(TitliSearchConstants.TITLI_INSERT_OPERATION))
		{
			indexRefresher.insert(recordIdentifier);
		}
		else if(operation.equalsIgnoreCase(TitliSearchConstants.TITLI_UPDATE_OPERATION))
		{
			indexRefresher.update(recordIdentifier);
		}
		else if(operation.equalsIgnoreCase(TitliSearchConstants.TITLI_DELETE_OPERATION))
		{
			indexRefresher.delete(recordIdentifier);
		}
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param className Contains class Name.
	 * @param identifier Contains the identifier.
	 * @param uiForm object of the class which implements IValueObject
	 * @throws BizLogicException BizLogic Exception
	 * @return isSuccess
	 */
	public boolean populateUIBean(String className, Long identifier, IValueObject uiForm)
			throws BizLogicException
	{
		//long startTime = System.currentTimeMillis();
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
		catch (ApplicationException daoExp)
		{
			throw getBizLogicException(daoExp, "biz.popbean.error"
					,"Exception in bean population");
		}
		finally
		{
			closeSession(dao);
		}

		String simpleClassName = Utility.parseClassName(className);

		//long endTime = System.currentTimeMillis();
		//logger.info("EXECUTE TIME FOR RETRIEVE IN EDIT FOR UI - " + simpleClassName + " : "
			//	+ (endTime - startTime));

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
		//long startTime = System.currentTimeMillis();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		DAO dao =null;
		AbstractDomainObject abstractDomain = null;
		try
		{
			dao = daofactory.getDAO();
			dao.openSession(null);
			Object object = dao.retrieve(className, identifier);
			abstractDomain = populateFormBean(uiForm,object);
		}
		catch (Exception daoExp)
		{
			throw getBizLogicException(daoExp, "biz.popdomain.error"
					,"Exception in domain population");
		}
		finally
		{
			closeSession(dao);
		}

		String simpleClassName = Utility.parseClassName(className);

		//long endTime = System.currentTimeMillis();
		//logger.info("EXECUTE TIME FOR RETRIEVE IN EDIT FOR DB - " + simpleClassName + " : "
				//+ (endTime - startTime));

		return abstractDomain;
	}

	/**
	 * @param uiForm Form object.
	 * @param object object to populate.
	 * @return AbstractDomainObject
	 * @throws AssignDataException throws this exception if not able to set all values.
	 */
	private AbstractDomainObject populateFormBean(IValueObject uiForm,Object object) throws AssignDataException
	{
		AbstractDomainObject abstractDomain=null;
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
	 * boolean value true if Privilege to view else false.
	 * @param objName object.
	 * @param identifier Long Id
	 * @param sessionDataBean SessionDataBean object.
	 * @return true if Privilege to view else false.
	 */
	public boolean hasPrivilegeToView(String objName, Long identifier,
			SessionDataBean sessionDataBean)
	{
		return false;
	}
	/**
	 * this method return true if authorized user.
	 * @param dao DAO object.
	 * @param domainObject Domain object.
	 * @param sessionDataBean  SessionDataBean object.
	 * @throws BizLogicException generic BizLogic Exception
	 * @return true if authorized user.
	 */
	public boolean isAuthorized(DAO dao, Object domainObject,
			SessionDataBean sessionDataBean) throws BizLogicException
	{

		return false;
	}


	/**
	 * @param exception Exception object thrown in a catch block.
	 * @param key error-key in applicationResource file.
	 * @param logMessage message to log inlogger.
	 * @throws BizLogicException
	 * @return BizLogicException
	 */
	protected BizLogicException getBizLogicException(Exception exception, String key,String logMessage)
	{
		logger.debug(logMessage);
		ErrorKey errorKey=ErrorKey.getErrorKey(key);
		return  new BizLogicException(errorKey,exception, "AbstractBizLogic");
	}


	/**
	 * @param dao DAO object.
	 */
	protected void rollback(DAO dao)
	{
		try
		{
			dao.rollback();
		}
		catch (DAOException daoEx)
		{
			logger.fatal("Rollback unsuccessful in method.",daoEx);
		}
	}
	/**
	 * @param dao DAO object
	 * @throws BizLogicException :Generic BizLogic Exception- session not closed.
	 */
	protected void closeSession(DAO dao) throws BizLogicException
	{
		try
		{
			dao.closeSession();
		}
		catch (DAOException exception)
		{
			logger.error("Not able to close DAO session.", exception);
			throw new BizLogicException(exception);
		}
	}
}