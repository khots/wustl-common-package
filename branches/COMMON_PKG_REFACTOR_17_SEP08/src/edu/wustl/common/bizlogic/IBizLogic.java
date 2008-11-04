/*
 * Created on Jul 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.bizlogic;

import java.util.List;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.DAO;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;

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
	 * @param daoType dao Type.
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated : This method uses daoType argument which is not required anymore,please use method
	 * delete(Object obj) throws UserNotAuthorizedException,BizLogicException;
	 */
	void delete(Object obj, int daoType) throws UserNotAuthorizedException,
			BizLogicException;
	
	
	
	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @throws BizLogicException BizLogic Exception
	 */
	void delete(Object obj) throws UserNotAuthorizedException,
			BizLogicException;

	/**
	 * Inserts an object from database.
	 * @param obj The object to be Inserted.
	 * @param sessionDataBean session specific Data
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * insert(Object obj, SessionDataBean sessionDataBean)
	 * throws BizLogicException, UserNotAuthorizedException;
	 */
	void insert(Object obj, SessionDataBean sessionDataBean, int daoType)
			throws BizLogicException, UserNotAuthorizedException;

	
	
	/**
	 * Inserts an object from database.
	 * @param obj The object to be Inserted.
	 * @param sessionDataBean session specific Data
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	void insert(Object obj, SessionDataBean sessionDataBean)
			throws BizLogicException, UserNotAuthorizedException;

	
	
	/**
	 * Inserts an object from database.
	 * @param obj The object to be Inserted.
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * insert(Object obj) throws BizLogicException,UserNotAuthorizedException;
	 */
	void insert(Object obj, int daoType) throws BizLogicException,
		UserNotAuthorizedException;

	
	
	/**
	 * Inserts an object from database.
	 * @param obj The object to be Inserted.
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	void insert(Object obj) throws BizLogicException,
		UserNotAuthorizedException;

	
	
	/**
	 * Updates an object.
	 * @param currentObj current Object.
	 * @param oldObj old Object.
	 * @param daoType dao Type
	 * @param sessionDataBean session specific Data
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean)throws BizLogicException,
	 * UserNotAuthorizedException;
	 */
	void update(Object currentObj, Object oldObj, int daoType,
			SessionDataBean sessionDataBean) throws BizLogicException, UserNotAuthorizedException;

	
	/**
	 * Updates an object.
	 * @param currentObj current Object.
	 * @param oldObj old Object.
	 * @param sessionDataBean session specific Data
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	void update(Object currentObj, Object oldObj,SessionDataBean sessionDataBean) 
	throws BizLogicException, UserNotAuthorizedException;


	
	
	/**
	 * Updates an object.
	 * @param currentObj current Object.
	 * @param daoType dao Type
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * update(Object currentObj) throws BizLogicException,UserNotAuthorizedException;
	 */
	void update(Object currentObj, int daoType) throws BizLogicException,
			UserNotAuthorizedException;

	/**
	 * Updates an object.
	 * @param currentObj current Object.
	 * @throws BizLogicException BizLogic Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	void update(Object currentObj) throws BizLogicException,
			UserNotAuthorizedException;


	
	
	/**
	 * creates Protection Element.
	 * @param currentObj current Object.
	 * @throws BizLogicException BizLogic Exception.
	 */
	void createProtectionElement(Object currentObj) throws BizLogicException;

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param sourceObjectName source Object Name
	 * @param selectColumnName An array of field names.
	 * @param whereColumnName An array of field names.
	 * @param whereColumnCondition The comparision condition for the field values.
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @return List
	 * @throws DAOException generic DAOException
	 */
	List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition) throws DAOException;

	/**
	 * This method checks for a particular privilege on a particular Object_Id.
	 * Gets privilege name as well as Object_Id from appropriate BizLogic
	 * depending on the context of the operation.
	 * @param dao The dao object.
	 * @param domainObject domain Object.
	 * @param sessionDataBean session specific Data
	 * @return Authorized or not.
	 * @throws DAOException generic DAOException.
	 * @throws UserNotAuthorizedException User Not Authorized Exception.
	 * @see edu.wustl.common.bizlogic.IBizLogic#isAuthorized
	 * (edu.wustl.common.dao.DAO, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	 */
	boolean isAuthorized(DAO dao, Object domainObject,
			SessionDataBean sessionDataBean) throws UserNotAuthorizedException, DAOException;

	/**
	 * This method returns the protection element name which should be used to authorize.
	 * Default Implementation
	 * If call is through some bizLogic which does not require authorization,
	 * let that operation be allowed for ALL
	 * @param dao The dao object.
	 * @param domainObject domain Object.
	 * @return Object Id.
	 */
	String getObjectId(DAO dao, Object domainObject);

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param sourceObjectName source Object Name.
	 * @param whereColumnName An array of field names.
	 * @param whereColumnCondition The comparision condition for the field values.
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName, String[] whereColumnName,
			String[] whereColumnCondition, Object[] whereColumnValue, String joinCondition)
			throws DAOException;

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param className class Name.
	 * @param colName Contains the field name.
	 * @param colValue Contains the field value.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String className, String colName, Object colValue)
			throws DAOException;

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the classname whose records are to be retrieved.
	 * @return list.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName) throws DAOException;

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName source Object Name.
	 * @param identifier identifier.
	 * @return object.
	 * @throws DAOException generic DAOException.
	 */
	Object retrieve(String sourceObjectName, Long identifier) throws DAOException;

	/**
	 *
	 * @param sourceObjectName source Object Name
	 * @param displayNameFields display Name Fields
	 * @param valueField value Field
	 * @param whereColumnName An array of field names.
	 * @param whereColumnCondition The comparision condition for the field values.
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @param separatorBetweenFields separator Between Fields
	 * @param isToExcludeDisabled is To Exclude Disabled
	 * @return Returns collection
	 * @throws DAOException generic DAOException
	 */
	List getList(String sourceObjectName, String[] displayNameFields,
			String valueField, String[] whereColumnName, String[] whereColumnCondition,
			Object[] whereColumnValue, String joinCondition, String separatorBetweenFields,
			boolean isToExcludeDisabled) throws DAOException;

	/**
	 *
	 * @param sourceObjectName source Object Name
	 * @param displayNameFields display Name Fields
	 * @param valueField value Field
	 * @param isToExcludeDisabled -is To Exclude Disabled
	 * @return Returns collection.
	 * @throws DAOException generic DAOException.
	 */
	List getList(String sourceObjectName, String[] displayNameFields,
			String valueField, boolean isToExcludeDisabled) throws DAOException;

	/**
	 *
	 * @param dao The dao object.
	 * @param sourceClass source Class.
	 * @param classIdentifier class Identifier.
	 * @param objIDArr object ID Array.
	 * @return list of related objects.
	 * @throws DAOException generic DAOException
	 */
	List getRelatedObjects(DAO dao, Class sourceClass, String classIdentifier,
			Long [] objIDArr) throws DAOException;

	/**
	 * sets Privilege.
	 * @param daoType dao Type
	 * @param privilegeName privilege Name
	 * @param objectType objec tType
	 * @param objectIds object Ids
	 * @param userId user Id
	 * @param sessionDataBean session specific Data
	 * @param roleId role Id.
	 * @param assignToUser assign To User.
	 * @param assignOperation Operation
	 * @throws SMException SMException
	 * @throws BizLogicException BizLogic Exception
	 * @deprecated This method uses daoType argument which is not required anymore,please use method
	 * setPrivilege(String privilegeName, Class objectType, Long[] objectIds,Long userId, 
	 * SessionDataBean sessionDataBean, String roleId, boolean assignToUser,boolean assignOperation)
	 *  throws SMException, BizLogicException;
	 */
	void setPrivilege(int daoType, String privilegeName, Class objectType, Long[] objectIds,
			Long userId, SessionDataBean sessionDataBean, String roleId, boolean assignToUser,
			boolean assignOperation) throws SMException, BizLogicException;
	
	
	
	/**
	 * sets Privilege.
	 * @param privilegeName privilege Name
	 * @param objectType objec tType
	 * @param objectIds object Ids
	 * @param userId user Id
	 * @param sessionDataBean session specific Data
	 * @param roleId role Id.
	 * @param assignToUser assign To User.
	 * @param assignOperation Operation
	 * @throws SMException SMException
	 * @throws BizLogicException BizLogic Exception
	 */
	void setPrivilege(String privilegeName, Class objectType, Long[] objectIds,
			Long userId, SessionDataBean sessionDataBean, String roleId, boolean assignToUser,
			boolean assignOperation) throws SMException, BizLogicException;

	
	

	/**
	 * To retrieve the attribute value for the given source object name & Id.
	 * @param sourceObjectName Source object in the Database.
	 * @param identifier Id of the object.
	 * @param attributeName attribute name to be retrieved.
	 * @return The Attribute value corresponding to the SourceObjectName & id.
	 * @throws DAOException generic DAOException
	 */
	Object retrieveAttribute(String sourceObjectName, Long identifier, String attributeName)
			throws DAOException;

	/**
	 * This is a wrapper function to retrieves attribute  for given class
	 * name and identifier using dao.retrieveAttribute().
	 * @param objClass source Class object
	 * @param identifier identifer of the source object
	 * @param attributeName attribute to be retrieved
	 * @return object.
	 * @throws DAOException generic DAOException.
	 */
	Object retrieveAttribute(Class objClass, Long identifier, String attributeName)
			throws DAOException;

	/**
	 * populates UIBean.
	 * @param className class Name
	 * @param identifier identifier
	 * @param uiForm object of the class which implements IValueObject
	 * @return populated or not.
	 * @throws DAOException generic DAOException.
	 * @throws BizLogicException BizLogic Exception
	 */
	boolean populateUIBean(String className, Long identifier, IValueObject uiForm)
			throws DAOException, BizLogicException;

	/**
	 * populates Domain Object.
	 * @param className class Name
	 * @param identifier identifier
	 * @param uiForm object of the class which implements IValueObject
	 * @return AbstractDomainObject.
	 * @throws DAOException generic DAOException.
	 * @throws BizLogicException BizLogic Exception.
	 * @throws AssignDataException Assign Data Exception.
	 */
	AbstractDomainObject populateDomainObject(String className, Long identifier,
			IValueObject uiForm) throws DAOException, BizLogicException, AssignDataException;

	/**
	 * Checkes is ReadDenied.
	 * @return isReadDenied
	 */
	boolean isReadDeniedTobeChecked();

	/**
	 * Check Privilege To View.
	 * @param objName object Name.
	 * @param identifier identifier
	 * @param sessionDataBean session specific Data
	 * @return hasPrivilegeToView.
	 */
	boolean hasPrivilegeToView(String objName, Long identifier,
			SessionDataBean sessionDataBean);

	/**
	 * gets ReadDenied Privilege Name.
	 * @return Read Denied Privilege Name.
	 */
	String getReadDeniedPrivilegeName();
}
