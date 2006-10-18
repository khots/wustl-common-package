/*
 * Created on Jul 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.bizlogic;

import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.DAO;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbManager.DAOException;


/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IBizLogic
{
	/**
     * Deletes an object from the database.
     * @param obj The object to be deleted.
     * @throws DAOException
     * @throws UserNotAuthorizedException TODO
     * @throws BizLogicException
     */
	public abstract void delete(Object obj, int daoType) throws UserNotAuthorizedException, BizLogicException;
	
	/**
     * This method gets called before insert method. Any logic before inserting into database can be included here.
     * @param obj The object to be inserted.
     * @param dao the dao object
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException
     * */
	public void preInsert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;
    
	public void insert(Object obj,SessionDataBean sessionDataBean, int daoType) throws BizLogicException, UserNotAuthorizedException;
	
	/**
     * This method gets called after insert method. Any logic after insertnig object in database can be included here.
     * @param obj The inserted object.
     * @param dao the dao object
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException 
     * */
	public void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException;
	
    public void insert(Object obj, int daoType) throws BizLogicException, UserNotAuthorizedException;
    
    /**
     * This method gets called before update method. Any logic before updating into database can be included here.
     * @param dao the dao object
     * @param currentObj The object to be updated.
     * @param oldObj The old object.
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException
     * */
    public void preUpdate(DAO dao,Object currentObj,Object oldObj, SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;
    
    public void update(Object currentObj,Object oldObj,int daoType, SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;
	
    /**
     * This method gets called after update method. Any logic after updating into database can be included here.
     * @param dao the dao object
     * @param currentObj The object to be updated.
     * @param oldObj The old object.
     * @param sessionDataBean session specific data
     * @throws DAOException
     * @throws UserNotAuthorizedException
     * */
    public void postUpdate(DAO dao,Object currentObj,Object oldObj,SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;
    
	public void update(Object currentObj, int daoType) throws BizLogicException, UserNotAuthorizedException;
    
	public abstract List retrieve(String sourceObjectName, String[] selectColumnName, String[] whereColumnName,
            String[] whereColumnCondition, Object[] whereColumnValue,
            String joinCondition) throws DAOException;
    
    /**
     * Retrieves the records for class name in sourceObjectName according to field values passed.
     * @param whereColumnName An array of field names.
     * @param whereColumnCondition The comparision condition for the field values. 
     * @param whereColumnValue An array of field values.
     * @param joinCondition The join condition.
     */
    public abstract List retrieve(String sourceObjectName, String[] whereColumnName,
            String[] whereColumnCondition, Object[] whereColumnValue,
            String joinCondition) throws DAOException;    
    
    /**
     * Retrieves the records for class name in sourceObjectName according to field values passed.
     * @param colName Contains the field name.
     * @param colValue Contains the field value.
     */
    public abstract List retrieve(String className, String colName, Object colValue)
            throws DAOException;    
    
    /**
     * Retrieves all the records for class name in sourceObjectName.
     * @param sourceObjectName Contains the classname whose records are to be retrieved.
     */
    public abstract List retrieve(String sourceObjectName) throws DAOException;
    
    public abstract List getList(String sourceObjectName, String[] displayNameFields, String valueField, String[] whereColumnName,
            String[] whereColumnCondition, Object[] whereColumnValue,
            String joinCondition, String separatorBetweenFields,  boolean isToExcludeDisabled) throws DAOException;
    
    public abstract List getList(String sourceObjectName, String[] displayNameFields, String valueField, boolean isToExcludeDisabled) 
    			throws DAOException;
    
    public abstract List getRelatedObjects(DAO dao, Class sourceClass, String classIdentifier,Long objIDArr[])throws DAOException;

    public void setPrivilege(int daoType,String privilegeName, Class objectType, Long[] objectIds, Long userId, SessionDataBean sessionDataBean, String roleId, boolean assignToUser, boolean assignOperation) throws SMException, BizLogicException;
}
