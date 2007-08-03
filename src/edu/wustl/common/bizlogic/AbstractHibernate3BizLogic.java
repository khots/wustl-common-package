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

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.AbstractDAO;
import edu.wustl.common.dao.DAO;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatter;
import edu.wustl.common.exceptionformatter.ExceptionFormatterFactory;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.dbManager.Hibernate3MetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * AbstractBizLogic is the base class of all the Biz Logic classes.
 * @author gautam_shetty
 */
public abstract class AbstractHibernate3BizLogic implements IBizLogic
{
	/**
     * This method gets called before insert method. Any logic before inserting into database can be included here.
     * @param obj The object to be inserted.
     * @param dao the dao object
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException
     * */
	protected abstract void preInsert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;
	
    /**
     * Inserts an object into the database.
     * @param obj The object to be inserted.
     * @param sessionDataBean TODO
     * @throws DAOException
     * @throws UserNotAuthorizedException TODO
     */
    protected abstract void insert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;
    
    protected abstract void insert(Object obj, DAO dao) throws DAOException, UserNotAuthorizedException;
    
    /**
     * This method gets called after insert method. Any logic after insertnig object in database can be included here.
     * @param obj The inserted object.
     * @param dao the dao object
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException 
     * */
    protected abstract void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;
    
    /**
     * Deletes an object from the database.
     * @param obj The object to be deleted.
     * @throws DAOException
     * @throws UserNotAuthorizedException TODO
     */
    protected abstract void delete(Object obj, DAO dao) throws DAOException, UserNotAuthorizedException;
    
    /**
     * This method gets called before update method. Any logic before updating into database can be included here.
     * @param dao the dao object
     * @param currentObj The object to be updated.
     * @param oldObj The old object.
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException
     * */
    protected abstract void preUpdate(DAO dao,Object currentObj,Object oldObj , SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;
    
    /**
     * Updates an objects into the database.
     * @param obj The object to be updated into the database. 
     * @param sessionDataBean TODO
     * @throws DAOException
     * @throws UserNotAuthorizedException TODO
     */
    protected abstract void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;
    
    /**
     * This method gets called after update method. Any logic after updating into database can be included here.
     * @param dao the dao object
     * @param currentObj The object to be updated.
     * @param oldObj The old object.
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException
     * */
    protected abstract void postUpdate(DAO dao,Object currentObj,Object oldObj , SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;
    
    protected abstract void update(DAO dao, Object obj) throws DAOException, UserNotAuthorizedException;
    
    /**
     * Validates the domain object for enumerated values.
     * @param obj The domain object to be validated. 
     * @param dao The DAO object
     * @param operation The operation(Add/Edit) that is to be performed.
     * @return True if all the enumerated value attributes contain valid values
     * @throws DAOException
     */
    protected abstract boolean validate(Object obj, DAO dao, String operation) throws DAOException;
    
    protected abstract void setPrivilege(DAO dao,String privilegeName, Class objectType, Long[] objectIds, Long userId, String roleId, boolean assignToUser, boolean assignOperation) throws SMException,DAOException;
    
    /**
     * Deletes an object from the database.
     * @param obj The object to be deleted.
     * @throws DAOException
     * @throws UserNotAuthorizedException TODO
     * @throws BizLogicException
     */
    public void delete(Object obj, int daoType) throws UserNotAuthorizedException, BizLogicException
    {
        AbstractDAO dao = DAOFactory.getInstance().getDAO(daoType);
		try
		{
	        dao.openSession(null);
	        delete(obj, dao);
	        dao.commit();
		}
		catch(DAOException ex)
		{
			String errMsg = getErrorMessage(ex,obj,"Deleting");
			if(errMsg==null)
			{
				errMsg=ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch(DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			Logger.out.debug("Error in delete");
			throw new BizLogicException(errMsg, ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch(DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException();
			}
		}
    }
    
  
    /**
     * This method gives the error message.
     * This method should be overrided for customizing error message
     * @param ex - Exception
     * @param obj - Object
     * @return - error message string
     */
    public String getErrorMessage(DAOException ex, Object obj, String operation)
    {
    	return formatException(ex.getWrapException(),obj,operation);
    }
    
    private void insert(Object obj, SessionDataBean sessionDataBean, int daoType, boolean isInsertOnly) throws UserNotAuthorizedException, BizLogicException
    {
    	AbstractDAO dao = DAOFactory.getInstance().getDAO(daoType);
		try
		{
	        dao.openSession(sessionDataBean);
	        validate(obj, dao, Constants.ADD);	        
	        preInsert(obj, dao, sessionDataBean);
	        if(isInsertOnly)
		    {
	        	insert(obj,dao);	        	
		    }else
		    {
		    	insert(obj, dao, sessionDataBean);
		    }
	        dao.commit();
	        postInsert(obj, dao, sessionDataBean);
		}
		catch(DAOException ex)
		{
			String errMsg = getErrorMessage(ex,obj,"Inserting");
			if(errMsg==null)
			{
				errMsg=ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch(DAOException daoEx)
			{
				throw new BizLogicException(daoEx.getMessage(), daoEx);
			}
			Logger.out.debug("Error in insert");
			throw new BizLogicException(errMsg, ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch(DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException();
			}
		}
    }
    
    public final void insert(Object obj,SessionDataBean sessionDataBean, int daoType) throws BizLogicException, UserNotAuthorizedException
	{
		insert(obj,sessionDataBean,daoType, false);
	}
    
    public final void insert(Object obj, int daoType) throws BizLogicException, UserNotAuthorizedException
    {
    	insert(obj,null,daoType,true);
    }
    
    private void update(Object currentObj,Object oldObj,int daoType, SessionDataBean sessionDataBean, boolean isUpdateOnly) throws BizLogicException, UserNotAuthorizedException
    {
    	AbstractDAO dao = DAOFactory.getInstance().getDAO(daoType);
		try
		{
	        dao.openSession(sessionDataBean);
	        validate(currentObj, dao, Constants.EDIT);
	        preUpdate(dao, currentObj, oldObj, sessionDataBean);
	        if(isUpdateOnly)
	        {
	        	update(dao,currentObj);
	        }
	        else
	        {
	        	update(dao, currentObj, oldObj, sessionDataBean);
	        }
	        dao.commit();
	        postUpdate(dao, currentObj, oldObj, sessionDataBean);
		}
		catch(DAOException ex)
		{
			//added to format constrainviolation message
			String errMsg = getErrorMessage(ex,currentObj,"Updating");
			if(errMsg==null)
			{
				errMsg=ex.getMessage();
			}
			try
			{
				dao.rollback();
			}
			catch(DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException(daoEx.getMessage(), daoEx);
				//throw new BizLogicException(ex.getMessage(), ex);
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
			catch(DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException();
			}
		}
    }
    
    /**
     * 
     */
    public final void update(Object currentObj,Object oldObj,int daoType, SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException
	{
		update(currentObj, oldObj, daoType, sessionDataBean, false);
	}
    
    /**
     * 
     */
    public final void update(Object currentObj, int daoType) throws BizLogicException, UserNotAuthorizedException
	{
    	update(currentObj, null, daoType, null, true);
	}
    
    public final void setPrivilege(int daoType,String privilegeName, Class objectType, Long[] objectIds, Long userId, SessionDataBean sessionDataBean, String roleId, boolean assignToUser, boolean assignOperation) throws SMException, BizLogicException
    {
        AbstractDAO dao = DAOFactory.getInstance().getDAO(daoType);
		try
		{
		    Logger.out.debug(" privilegeName:"+privilegeName+" objectType:"+objectType+" objectIds:"+edu.wustl.common.util.Utility.getArrayString(objectIds)+" userId:"+userId+" roleId:"+roleId+" assignToUser:"+assignToUser);
	        dao.openSession(sessionDataBean);
	        setPrivilege(dao, privilegeName,objectType,objectIds,userId, roleId, assignToUser, assignOperation);
	        dao.commit();
		}
		catch(DAOException ex)
		{
			try
			{
				dao.rollback();
			}
			catch(DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException(daoEx.getMessage(), daoEx);
				//throw new BizLogicException(ex.getMessage(), ex);
			}
			//TODO ERROR Handling
			throw new BizLogicException(ex.getMessage(), ex);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch(DAOException daoEx)
			{
				//TODO ERROR Handling
				throw new BizLogicException("Unknown Error");
			}
		}
    }
    
	public String formatException(Exception ex, Object obj, String operation)
	{
		String errMsg="";
		if(ex==null)
		{
			return null;
		}
		String roottableName=Hibernate3MetaData.getRootTableName(obj.getClass());
		String tableName=Hibernate3MetaData.getTableName(obj.getClass());
    	try
    	{   				
    	    // Get ExceptionFormatter
        	ExceptionFormatter ef = ExceptionFormatterFactory.getFormatter(ex);
			// call for Formating Message
			if(ef!=null)
			{
				Object[] arguments = {roottableName,DBUtil.currentSession().connection(),tableName};
				errMsg = ef.formatMessage(ex,arguments);
			}
			else
			{
				// if ExceptionFormatter not found Format message through Default Formatter 
				//String arg[]={operation,tableName};
	            //errMsg = new DefaultExceptionFormatter().getErrorMessage("Err.SMException.01",arg);
				errMsg=ex.getMessage();
			}
    	}
    	catch(Exception e)
    	{
    		Logger.out.error(ex.getMessage(),ex);
    		// if Error occured while formating message then get message
    		// formatted through Default Formatter
    		String arg[]={operation,tableName};
            errMsg = new DefaultExceptionFormatter().getErrorMessage("Err.SMException.01",arg);   
    	}
    	return errMsg;
	}
}