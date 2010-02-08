/*
 *
 */

package edu.wustl.common.audit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;

/**
 * AuditManager is an algorithm to figure out the changes with respect to database due to
 * insert, update or delete data from/to database.
 * @author kapil_kaveeshwar
 */
public class AuditManager
{

	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(AuditManager.class);

	/**
	 *  Instance of Audit event.
	 * All the change under one database session are added under this event.
	 *
	 */
	private transient AuditEvent auditEvent;

	/**
	 * Instantiate a new instance of AuditManager.
	 * */
	public AuditManager()
	{
		auditEvent = new AuditEvent();
	}

	/**
	 * Set the id of the logged-in user who performed the changes.
	 * @param userId System identifier of logged-in user who performed the changes.
	 * */
	public void setUserId(Long userId)
	{
		auditEvent.setUserId(userId);
	}

	/**
	 * Set the ip address of the machine from which the event was performed.
	 * @param iPAddress ip address of the machine to set.
	 * */
	public void setIpAddress(String iPAddress)
	{
		auditEvent.setIpAddress(iPAddress);
	}

	/**
	 * Check whether the object type is a premitive data type or a user defined datatype.
	 * @param obj object.
	 * @return return true if object type is a premitive data type else false.
	 */
	private boolean isVariable(Object obj)
	{
		boolean objectType = obj instanceof Number || obj instanceof String
				|| obj instanceof Boolean || obj instanceof Character || obj instanceof Date;
		return objectType;
	}

	/**
	 * Compares the contents of two objects.
	 * @param currentObj Current state of object.
	 * @param previousObj Previous state of object.
	 * @param eventType Event for which the comparator will be called. e.g. Insert, update, delete etc.
	 * @throws AuditException Audit Exception.
	 */
	public void audit(Object currentObj, Object previousObj, String eventType)
	throws AuditException
	{
		if (currentObj instanceof Auditable)
		{
			compare((Auditable)currentObj,(Auditable)previousObj,eventType);
		}
	}

	/**
	 * Compares the contents of two objects.
	 * @param currentObj Current state of object.
	 * @param previousObj Previous state of object.
	 * @param eventType Event for which the comparator will be called. e.g. Insert, update, delete etc.
	 * @throws AuditException Audit Exception.
	 */
	private void compare(Auditable currentObj, Auditable previousObj, String eventType)
			throws AuditException
	{
		LOGGER.debug("Inside isObjectAuditable method.");

			Auditable auditableObject = (Auditable)currentObj;
			if (auditableObject == null)
			{
				return;
			}

			//An auidt event will contain many logs.
			AuditEventLog auditEventLog = new AuditEventLog();

			//Set System identifier if the current object.
			auditEventLog.setObjectIdentifier(auditableObject.getId());

			//Set the table name of the current class.
			Object obj = HibernateMetaData.getProxyObjectImpl(auditableObject);
			auditEventLog.setObjectName(HibernateMetaData.getTableName(obj.getClass()));
			auditEventLog.setEventType(eventType);

			//Class of the object being compared.
			compareClassOfObject((Auditable)obj, previousObj, auditEventLog);
	}

	/**
	 * Class of the object being compared.
	 * @param currentObj current Object
	 * @param previousObj previous Object
	 * @param auditEventLog audit Event Log
	 * @throws AuditException Audit Exception.
	 */
	private void compareClassOfObject(Auditable currentObj, Auditable previousObj,
			AuditEventLog auditEventLog) throws AuditException
	{
		//Class of the object being compared
		Class currentObjClass = currentObj.getClass();
		Class previousObjClass = currentObjClass;

		if (previousObj != null)
		{
			previousObjClass = previousObj.getClass();
		}

		//check the class for both objects are equals or not.
		if (previousObjClass.equals(currentObjClass))
		{
			Set<AuditEventDetails> auditEventDetailsCollection = processMethods(currentObj,
					previousObj, currentObjClass);

			if (!auditEventDetailsCollection.isEmpty())
			{
				auditEventLog.setAuditEventDetailsCollcetion(auditEventDetailsCollection);
				auditEvent.getAuditEventLogCollection().add(auditEventLog);
			}
		}
	}

	/**
	 * Process each getter Methods to find the change from previous value to current value.
	 * @param obj current Object
	 * @param previousObj previous Object
	 * @param currentObjClass current Object Class
	 * @return audit Event Details Collection.
	 * @throws AuditException Audit Exception.
	 */
	private Set<AuditEventDetails> processMethods(Auditable obj, Auditable previousObj,
			Class currentObjClass) throws AuditException
	{
		//Retrieve all the methods defined in the class.
		Object currentObj = HibernateMetaData.getProxyObjectImpl(obj);
		Method[] methods = currentObjClass.getMethods();
		Set<AuditEventDetails> auditEventDetailsCollection = new HashSet<AuditEventDetails>();
		for (int i = 0; i < methods.length; i++)
		{
			//filter only getter methods.
			if (methods[i].getName().startsWith("get")
					&& methods[i].getParameterTypes().length == 0)
			{
				AuditEventDetails auditEventDetails = processField(methods[i], currentObj,
						previousObj);
				if (auditEventDetails != null)
				{
					auditEventDetailsCollection.add(auditEventDetails);
				}
			}
		}
		return auditEventDetailsCollection;
	}

	/**
	 * Process each field to find the change from previous value to current value.
	 * @param method referance of getter method object access the current and previous value of the object.
	 *
	 * @param currentObj instance of current object
	 * @param previousObj instance of previous object.
	 * @return AuditEventDetails.
	 * @throws AuditException Audit Exception.
	 * */
	private AuditEventDetails processField(Method method, Object currentObj, Object previousObj)
			throws AuditException

	{
		//Get the old value of the attribute from previousObject
		Object prevVal = getValue(previousObj, method);

		//Get the current value of the attribute from currentObject
		Object currVal = getValue(currentObj, method);

		//Parse the attribute name from getter method.
		String attributeName = Utility.parseAttributeName(method.getName());

		//Find the currosponding column in the database
		String columnName = HibernateMetaData.getColumnName(currentObj.getClass(), attributeName);

		AuditEventDetails auditEventDetails = null;
		//Case of transient object
		if (!(TextConstants.EMPTY_STRING.equals(columnName.trim())))
		{
			//Compare the old and current value
			auditEventDetails = compareValue(prevVal, currVal);
			if (auditEventDetails != null)
			{
				auditEventDetails.setElementName(columnName);
				
			}
		}
		return auditEventDetails;
	}

	/**
	 * This function gets the value returned by the method invoked my given object.
	 * @param obj Object for which method should be invoked
	 * @param method This is the method for which we have to find out the return value
	 * @return Object return value
	 * @throws AuditException Audit Exception.
	 */
	private Object getValue(Object obj, Method method) throws AuditException
	{
		Object value = null;
		if (obj != null)
		{
			Object val;
				try
				{
					val = Utility.getValueFor(obj, method);
					value = getObjectValue(value, val);
				}
				catch (IllegalAccessException ex)
				{
					LOGGER.error(ex.getMessage(), ex);
					throw new AuditException(ex, "while comparing audit objects");
				}
				catch (InvocationTargetException iTException)
				{
					LOGGER.error(iTException.getMessage(), iTException);
					throw new AuditException(iTException, "while comparing audit objects");
				}
		}
		return value;
	}

	/**
	 * @param value object.
	 * @param val method object
	 * @return value
	 */
	private Object getObjectValue(Object value, Object val)
	{
		Object reqValue = value;
		if (val instanceof Auditable)
		{
			Auditable auditable = (Auditable) val;
			reqValue = auditable.getId();
		}
		else if (isVariable(val))
		{
			reqValue = val;
		}
		return reqValue;
	}

	/**
	 * This function compares the prevVal object and currval object
	 * and if there is any change in value then create the AuditEvenDetails object.
	 * @param prevVal previous value
	 * @param currVal current value
	 * @return AuditEventDetails
	 */
	private AuditEventDetails compareValue(Object prevVal, Object currVal)
	{
		AuditEventDetails auditEventDetails = null;
		if (prevVal != null || currVal != null)
		{
			auditEventDetails = compareValLogic(prevVal, currVal);
		}
		return auditEventDetails;
	}

	/**
	 * This function compares the prevVal object and currval object
	 * and if there is any change in value then create the AuditEvenDetails object.
	 * @param prevVal prev Value
	 * @param currVal curr Value
	 * @return auditEventDetails
	 */
	private AuditEventDetails compareValLogic(Object prevVal, Object currVal)
	{
		AuditEventDetails auditEventDetails;
		if (prevVal == null || currVal == null)
		{
			auditEventDetails = compareLogic(prevVal, currVal);
		}
		else
		{
			auditEventDetails = new AuditEventDetails();
			auditEventDetails.setPreviousValue(prevVal.toString());
			auditEventDetails.setCurrentValue(currVal.toString());
		}
		return auditEventDetails;
	}

	/**
	 * This function compares the prevVal object and currval object
	 * and if there is any change in value then create the AuditEvenDetails object.
	 * @param prevVal previos Value
	 * @param currVal current Value
	 * @return auditEventDetails
	 */
	private AuditEventDetails compareLogic(Object prevVal, Object currVal)
	{
		AuditEventDetails auditEventDetails = new AuditEventDetails();
		if (prevVal == null)
		{
			auditEventDetails.setCurrentValue(currVal.toString());
		}
		else
		{
			auditEventDetails.setPreviousValue(prevVal.toString());
		}
		return auditEventDetails;
	}

	/**
	 * This method inserts audit Event details in database.
	 * @param dao DAO object
	 * @throws DAOException generic DAOException
	 */
	public void insert(DAO dao) throws DAOException
	{
		if (auditEvent.getAuditEventLogCollection().isEmpty())
		{
			return;
		}

		dao.insert(auditEvent);
		Iterator<AuditEventLog> auditLogIterator = auditEvent.getAuditEventLogCollection()
				.iterator();
		while (auditLogIterator.hasNext())
		{
			AuditEventLog auditEventLog = (AuditEventLog) auditLogIterator.next();
			auditEventLog.setAuditEvent(auditEvent);
			dao.insert(auditEventLog);

			Iterator<AuditEventDetails> auditEventDetailsIterator = auditEventLog
					.getAuditEventDetailsCollcetion().iterator();
			while (auditEventDetailsIterator.hasNext())
			{
				AuditEventDetails auditEventDetails = (AuditEventDetails) auditEventDetailsIterator
						.next();
				auditEventDetails.setAuditEventLog(auditEventLog);
				dao.insert(auditEventDetails);
			}
		}
		//auditEvent = new AuditEvent();

	}

	/**
	 * This method adds Audit Event Logs.
	 * @param auditEventLogsCollection audit Event Logs Collection.
	 *//*
	public void addAuditEventLogs(Collection<AuditEventLog> auditEventLogsCollection)
	{
		auditEvent.getAuditEventLogCollection().addAll(auditEventLogsCollection);
	}*/
	
	
	public void insertAudit(DAO dao,Object currentObj)throws AuditException, DAOException
	{
		if (currentObj instanceof Auditable)
		{
			audit(currentObj, null, "INSERT");
			insert(dao);
		}
	}
	
	public void updateAudit(DAO dao,Object currentObj, Object previousObj)throws AuditException, DAOException
	{
		if (currentObj instanceof Auditable)
		{
			audit(currentObj, previousObj, "UPDATE");
			insert(dao);
		}
	}
	
	public void addAuditEventLogs(DAO dao,Collection<AuditEventLog> auditEventLogsCollection)throws AuditException, DAOException
	{
		auditEvent.getAuditEventLogCollection().addAll(auditEventLogsCollection);
		insert(dao);
	}


}