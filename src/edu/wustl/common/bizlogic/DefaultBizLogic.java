/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * <p>Title: DefaultBizLogic Class>
 * <p>Description:	DefaultBizLogic is a class which contains the default 
 * implementations required for all the biz logic classes.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 26, 2005
 */

package edu.wustl.common.bizlogic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.AbstractDAO;
import edu.wustl.common.dao.DAO;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.HibernateDAO;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * DefaultBizLogic is a class which contains the default 
 * implementations required for all the biz logic classes.
 * @author kapil_kaveeshwar
 */
public class DefaultBizLogic extends AbstractBizLogic
{
	/**
	 * This method gets called before insert method. Any logic before inserting into database can be included here.
	 * @param obj The object to be inserted.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 * */
	protected void preInsert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException
	{

	}

	/**
	 * Inserts an object into the database.
	 * @param obj The object to be inserted.
	 * @throws DAOException
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException
	{
		dao.insert(obj, sessionDataBean, true, true);
	}

	/**
	 * This method gets called after insert method. Any logic after insertnig object in database can be included here.
	 * @param obj The inserted object.
	 * @param dao the dao object
	 * @param sessionDataBean session specific data
	 * @throws DAOException
	 * @throws UserNotAuthorizedException 
	 * */
	protected void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException
	{

	}

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException
	 * @throws UserNotAuthorizedException TODO
	 */
	protected void delete(Object obj, DAO dao) throws DAOException, UserNotAuthorizedException
	{
		//dao.delete(obj);
	}

	/**
	 * This method gets called before update method. Any logic before updating into database can be included here.
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 * */
	protected void preUpdate(DAO dao, Object currentObj, Object oldObj, SessionDataBean sessionDataBean) throws BizLogicException,
			UserNotAuthorizedException
	{

	}

	/**
	 * Updates an objects into the database.
	 * @param obj The object to be updated into the database. 
	 * @throws DAOException
	 */
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException
	{
		dao.update(obj, sessionDataBean, true, true, false);
		dao.audit(obj, oldObj, sessionDataBean, true);
	}

	/**
	 * This method gets called after update method. Any logic after updating into database can be included here.
	 * @param dao the dao object
	 * @param currentObj The object to be updated.
	 * @param oldObj The old object.
	 * @param sessionDataBean session specific data
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 * */
	protected void postUpdate(DAO dao, Object currentObj, Object oldObj, SessionDataBean sessionDataBean) throws BizLogicException,
			UserNotAuthorizedException
	{

	}

	protected boolean validate(Object obj, DAO dao, String operation) throws DAOException
	{
		return true;
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param sourceObjectName	source object name
	 * @param selectColumnName	An array of field names to be selected
	 * @param whereColumnName An array of field names.
	 * @param whereColumnCondition The comparision condition for the field values. 
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @throws SMException
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName, String[] whereColumnName, String[] whereColumnCondition,
			Object[] whereColumnValue, String joinCondition) throws DAOException
	{
		AbstractDAO dao = DAOFactory.getInstance().getDAO(Constants.HIBERNATE_DAO);

		List list = null;

		try
		{
			dao.openSession(null);

			list = dao.retrieve(sourceObjectName, selectColumnName, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);
			dao.commit();
		}
		catch (DAOException daoExp)
		{
			daoExp.printStackTrace();
			Logger.out.error(daoExp.getMessage(), daoExp);
		}
		finally
		{
			dao.closeSession();
		}

		return list;
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param whereColumnName An array of field names.
	 * @param whereColumnCondition The comparision condition for the field values. 
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @throws SMException
	 */
	public List retrieve(String sourceObjectName, String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition) throws DAOException
	{

		AbstractDAO dao = DAOFactory.getInstance().getDAO(Constants.HIBERNATE_DAO);

		List list = null;

		try
		{
			dao.openSession(null);

			list = dao.retrieve(sourceObjectName, null, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);
			dao.commit();
		}
		catch (DAOException daoExp)
		{
			daoExp.printStackTrace();
			//Logger.out.error(daoExp.getMessage(),daoExp);
		}
		finally
		{
			dao.closeSession();
		}
		return list;
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed.
	 * @param colName Contains the field name.
	 * @param colValue Contains the field value.
	 */
	public List retrieve(String className, String colName, Object colValue) throws DAOException
	{
		String colNames[] = {colName};
		String colConditions[] = {"="};
		Object colValues[] = {colValue};

		return retrieve(className, colNames, colConditions, colValues, Constants.AND_JOIN_CONDITION);
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the classname whose records are to be retrieved.
	 */
	public List retrieve(String sourceObjectName) throws DAOException
	{
		return retrieve(sourceObjectName, null, null, null, null);
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the classname whose records are to be retrieved.
	 * @param selectColumnName An array of the fields that should be selected
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName) throws DAOException
	{
		return retrieve(sourceObjectName, selectColumnName, null, null, null, null);
	}

	public List getList(String sourceObjectName, String[] displayNameFields, String valueField, boolean isToExcludeDisabled) throws DAOException
	{
		String[] whereColumnName = null;
		String[] whereColumnCondition = null;
		Object[] whereColumnValue = null;
		String joinCondition = null;
		String separatorBetweenFields = ", ";

		if (isToExcludeDisabled)
		{
			whereColumnName = new String[]{"activityStatus"};
			whereColumnCondition = new String[]{"!="};
			whereColumnValue = new String[]{Constants.ACTIVITY_STATUS_DISABLED};
		}

		return getList(sourceObjectName, displayNameFields, valueField, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition,
				separatorBetweenFields);
	}

	/**
	 * Returns collection of name value pairs.
	 * @param sourceObjectName
	 * @param displayNameFields
	 * @param valueField
	 * @param whereColumnName
	 * @param whereColumnCondition
	 * @param whereColumnValue
	 * @param joinCondition
	 * @param separatorBetweenFields
	 * @return
	 * @throws DAOException
	 */
	public List getList(String sourceObjectName, String[] displayNameFields, String valueField, String[] whereColumnName,
			String[] whereColumnCondition, Object[] whereColumnValue, String joinCondition, String separatorBetweenFields, boolean isToExcludeDisabled)
			throws DAOException
	{
		if (isToExcludeDisabled)
		{
			whereColumnName = (String[]) Utility.addElement(whereColumnName, "activityStatus");
			whereColumnCondition = (String[]) Utility.addElement(whereColumnCondition, "!=");
			whereColumnValue = Utility.addElement(whereColumnValue, Constants.ACTIVITY_STATUS_DISABLED);
		}

		return getList(sourceObjectName, displayNameFields, valueField, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition,
				separatorBetweenFields);
	}

	//Mandar : 12-May-06 : bug id 863 : Sorting of ID columns
	private List getList(String sourceObjectName, String[] displayNameFields, String valueField, String[] whereColumnName,
			String[] whereColumnCondition, Object[] whereColumnValue, String joinCondition, String separatorBetweenFields) throws DAOException
	{
		//Logger.out.debug("in get list");
		Vector nameValuePairs = new Vector();

		nameValuePairs.add(new NameValueBean(Constants.SELECT_OPTION, "-1"));

		String[] selectColumnName = new String[displayNameFields.length + 1];
		for (int i = 0; i < displayNameFields.length; i++)
		{
			selectColumnName[i] = displayNameFields[i];
		}
		selectColumnName[displayNameFields.length] = valueField;

		List results = retrieve(sourceObjectName, selectColumnName, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);

		NameValueBean nameValueBean;
		Object[] columnArray = null;

		/**
		 * For each row in the result a vector will be created.Vector will contain all the columns
		 * other than the value column(last column). 
		 * If there is only one column in the result it will be set as the Name for the NameValueBean.
		 * When more than one columns are present, a string representation will be set. 
		 */
		if (results != null)
		{
			for (int i = 0; i < results.size(); i++)
			{
				columnArray = (Object[]) results.get(i);
				Object tmpObj = null;
				Vector tmpBuffer = new Vector();
				StringBuffer nameBuff = new StringBuffer();
				if (columnArray != null)
				{
					for (int j = 0; j < columnArray.length - 1; j++)
					{
						if (columnArray[j] != null && (!columnArray[j].toString().equals("")))
						{
							tmpBuffer.add(columnArray[j]);
						}
					}
					nameValueBean = new NameValueBean();

					//create name data
					if (tmpBuffer.size() == 1)//	only one column
					{
						tmpObj = tmpBuffer.get(0);

						Logger.out.debug("nameValueBean Name : : " + tmpObj);
						Logger.out.debug("NameClass : : " + tmpObj.getClass().getName());
						nameValueBean.setName(tmpObj);
					}
					else
					// multiple columns
					{
						for (int j = 0; j < tmpBuffer.size(); j++)
						{
							nameBuff.append(tmpBuffer.get(j).toString());
							if (j < tmpBuffer.size() - 1)
							{
								nameBuff.append(separatorBetweenFields);
							}
						}

						Logger.out.debug("nameValueBean Name : : " + nameBuff.toString());
						nameValueBean.setName(nameBuff.toString());
					}

					int valueID = columnArray.length - 1;
					nameValueBean.setValue(columnArray[valueID].toString());

					nameValuePairs.add(nameValueBean);
				}
			}
		}
		Collections.sort(nameValuePairs);
		return nameValuePairs;
	}

	protected List disableObjects(DAO dao, Class sourceClass, String classIdentifier, String tablename, String colName, Long objIDArr[])
			throws DAOException
	{
		dao.disableRelatedObjects(tablename, colName, objIDArr);
		List listOfSubElement = getRelatedObjects(dao, sourceClass, classIdentifier, objIDArr);
		auditDisabledObjects(dao, tablename, listOfSubElement);
		return listOfSubElement;
	}

	/**
	 * @param tablename
	 * @param listOfSubElement
	 */
	private void auditDisabledObjects(DAO dao, String tablename, List listOfSubElement)
	{
		Iterator iterator = listOfSubElement.iterator();
		Collection auditEventLogsCollection = new HashSet();

		while (iterator.hasNext())
		{
			Long objectIdentifier = (Long) iterator.next();
			AuditEventLog auditEventLog = new AuditEventLog();
			auditEventLog.setObjectIdentifier(objectIdentifier);
			auditEventLog.setObjectName(tablename);
			auditEventLog.setEventType(Constants.UPDATE_OPERATION);

			Collection auditEventDetailsCollection = new HashSet();
			AuditEventDetails auditEventDetails = new AuditEventDetails();
			auditEventDetails.setElementName(Constants.ACTIVITY_STATUS_COLUMN);
			auditEventDetails.setCurrentValue(Constants.ACTIVITY_STATUS_DISABLED);

			auditEventDetailsCollection.add(auditEventDetails);

			auditEventLog.setAuditEventDetailsCollcetion(auditEventDetailsCollection);
			auditEventLogsCollection.add(auditEventLog);
		}

		HibernateDAO hibDAO = (HibernateDAO) dao;
		hibDAO.addAuditEventLogs(auditEventLogsCollection);
	}

	public List getRelatedObjects(DAO dao, Class sourceClass, String classIdentifier, Long objIDArr[]) throws DAOException
	{
		String sourceObjectName = sourceClass.getName();
		String selectColumnName[] = {Constants.SYSTEM_IDENTIFIER};

		String[] whereColumnName = {classIdentifier + "." + Constants.SYSTEM_IDENTIFIER};
		String[] whereColumnCondition = {"in"};
		Object[] whereColumnValue = {objIDArr};
		String joinCondition = Constants.AND_JOIN_CONDITION;

		List list = dao.retrieve(sourceObjectName, selectColumnName, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);
		list = Utility.removeNull(list);
		Logger.out.debug(sourceClass.getName() + " Related objects to " + edu.wustl.common.util.Utility.getArrayString(objIDArr) + " are " + list);
		return list;
	}

	//Aarti: Overloaded to let selectColumnName and whereColumnName also be
	// parameters to method and are not hardcoded
	public List getRelatedObjects(DAO dao, Class sourceClass, String[] selectColumnName, String[] whereColumnName, Long objIDArr[])
			throws DAOException
	{
		String sourceObjectName = sourceClass.getName();
		String[] whereColumnCondition = {"in"};
		Object[] whereColumnValue = {objIDArr};
		String joinCondition = Constants.AND_JOIN_CONDITION;

		List list = dao.retrieve(sourceObjectName, selectColumnName, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);
		Logger.out.debug(sourceClass.getName() + " Related objects to " + edu.wustl.common.util.Utility.getArrayString(objIDArr) + " are " + list);
		list = Utility.removeNull(list);
		return list;
	}

	public List getRelatedObjects(DAO dao, Class sourceClass, String[] whereColumnName, String[] whereColumnValue, String[] whereColumnCondition)
			throws DAOException
	{
		String sourceObjectName = sourceClass.getName();
		String joinCondition = Constants.AND_JOIN_CONDITION;
		String selectColumnName[] = {Constants.SYSTEM_IDENTIFIER};
		List list = dao.retrieve(sourceObjectName, selectColumnName, whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);

		list = Utility.removeNull(list);
		return list;
	}

	/**
	 * @author aarti_sharma
	 * Method allows assigning of privilege privilegeName to user identified by userId 
	 * or role identified by roleId
	 * on objects ids objectIds of type objectType.
	 * Privilege is to be assigned to user or a role is identified by boolean assignToUser
	 * 
	 */
	protected void setPrivilege(DAO dao, String privilegeName, Class objectType, Long[] objectIds, Long userId, String roleId, boolean assignToUser,
			boolean assignOperation) throws SMException, DAOException
	{
		Logger.out.debug(" privilegeName:" + privilegeName + " objectType:" + objectType + " objectIds:"
				+ edu.wustl.common.util.Utility.getArrayString(objectIds) + " userId:" + userId + " roleId:" + roleId + " assignToUser:"
				+ assignToUser);
		if (assignToUser)
		{
			SecurityManager.getInstance(this.getClass()).assignPrivilegeToUser(privilegeName, objectType, objectIds, userId, assignOperation);
		}
		else
		{
			SecurityManager.getInstance(this.getClass()).assignPrivilegeToGroup(privilegeName, objectType, objectIds, roleId, assignOperation);
		}
	}

	protected Object getCorrespondingOldObject(Collection objectCollection, Long id)
	{
		Iterator iterator = objectCollection.iterator();
		while (iterator.hasNext())
		{
			AbstractDomainObject abstractDomainObject = (AbstractDomainObject) iterator.next();
			if (abstractDomainObject.getId().equals(id))
			{
				return abstractDomainObject;
			}
		}

		return null;
	}

	protected DAOException handleSMException(SMException e)
	{
		Logger.out.error("Exception in Authorization: " + e.getMessage(), e);
		String message = "Security Exception: " + e.getMessage();
		if (e.getCause() != null)
			message = message + " : " + e.getCause().getMessage();
		return new DAOException(message, e);
	}

	/**
	 *  Method to check the ActivityStatus of the given identifier
	 * @param dao
	 * @param identifier of the Element
	 * @param className of the Element
	 * @param errorName Dispaly Name of the Element
	 * @throws DAOException
	 */
	protected void checkStatus(DAO dao, AbstractDomainObject ado, String errorName) throws DAOException
	{
		if (ado != null)
		{
			Long identifier = ado.getId();
			if (identifier != null)
			{
				String className = ado.getClass().getName();
				String activityStatus = getActivityStatus(dao, className, identifier);
				if (activityStatus.equals(Constants.ACTIVITY_STATUS_CLOSED))
				{
					throw new DAOException(errorName + " " + ApplicationProperties.getValue("error.object.closed"));
				}
			}
		}
	}

	public String getActivityStatus(DAO dao, String sourceObjectName, Long indetifier) throws DAOException
	{
		String whereColumnNames[] = {Constants.SYSTEM_IDENTIFIER};
		String colConditions[] = {"="};
		Object whereColumnValues[] = {indetifier};
		String[] selectColumnName = {Constants.ACTIVITY_STATUS};
		List list = dao
				.retrieve(sourceObjectName, selectColumnName, whereColumnNames, colConditions, whereColumnValues, Constants.AND_JOIN_CONDITION);

		if (!list.isEmpty())
		{
			Object obj = list.get(0);
			Logger.out.debug("obj Class " + obj.getClass());
			//Object[] objArr = (String)obj
			return (String) obj;
		}
		return "";
	}

	protected void insert(Object obj, DAO dao) throws DAOException, UserNotAuthorizedException
	{
		dao.insert(obj,null,false,false);
	}

	protected void update(DAO dao, Object obj) throws DAOException, UserNotAuthorizedException
	{
		dao.update(obj,null,false,false,false);
	}
}