/*
 *
 */

package edu.wustl.common.audit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.wustl.common.audit.util.AuditableMetadataUtil;
import edu.wustl.common.beans.LoginDetails;
import edu.wustl.common.domain.AbstractAuditEventLog;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.DataAuditEventLog;
import edu.wustl.common.domain.LoginEvent;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.dao.util.HibernateMetaDataFactory;


/**
 * AuditManager is an algorithm to figure out the changes with respect to database due to
 * insert, update or delete data from/to database.
 * @author kapil_kaveeshwar
 */
public class AuditManager // NOPMD
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
	private AuditEvent auditEvent;
	/**
	 * application name.
	 */
	private String applicationName;
	/**
	 * Instantiate a new instance of AuditManager.
	 * */
	public AuditManager()
	{
		auditEvent = new AuditEvent();
	}
	/**
	 * @return the applicationName.
	 */
	public String getApplicationName()
	{
		return applicationName;
	}
	/**
	 * @param applicationName the applicationName to set.
	 */
	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
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
	 * Set the IP address of the machine from which the event was performed.
	 * @param iPAddress IP address of the machine to set.
	 * */
	public void setIpAddress(String iPAddress)
	{
		auditEvent.setIpAddress(iPAddress);
	}
	/**
	 * Check whether the object type is a primitive data type or a user defined dataType.
	 * @param obj object.
	 * @return return true if object type is a primitive data type else false.
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
	 * @param eventType Event for which the comparator
	 * will be called. e.g. Insert, update, delete etc.
	 * @throws AuditException Audit Exception.
	 */
	protected void audit(Object currentObj, Object previousObj, String eventType)
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
		Auditable auditableObject = (Auditable) currentObj;
		if (auditableObject == null)
		{
			return;
		}
		// Set the table name of the current class.
		Object obj = HibernateMetaData.getProxyObjectImpl(auditableObject);
		// Class of the object being compared.
		compareClassOfObject((Auditable) obj, previousObj);

		// Set Event type for the audit Event
		auditEvent.setEventType(eventType);
	}
	/**
	 * Class of the object being compared.
	 *
	 * @param currentObj
	 *            current Object.
	 * @param previousObj
	 *            previous Object.
	 * @param auditableClass
	 * @throws AuditException
	 *             Audit Exception.
	 */
	private void compareClassOfObject(Auditable currentObj, Auditable previousObj)
			throws AuditException
	{
		// Class of the object being compared
		Class currentObjClass = currentObj.getClass();
		Class previousObjClass = currentObjClass;
		if (previousObj != null)
		{
			previousObjClass = previousObj.getClass();
		}
		// check the class for both objects are equals or not.
		if (previousObjClass.equals(currentObjClass))
		{
			DataAuditEventLog auditEventLog = processMethods(currentObj, previousObj);

			auditEvent.getAuditEventLogCollection().add(auditEventLog);
		}
	}
	/**
	 * Process each getter Methods to find the change from previous value to current value.
	 * @param obj current Object.
	 * @param previousObj previous Object.
	 * @return audit Event Details Collection.
	 * @throws AuditException Audit Exception.
	 */
	private DataAuditEventLog processMethods(Auditable obj, Auditable previousObj) throws AuditException
	{
		// An audit event will contain many logs.
		DataAuditEventLog auditEventLog = new DataAuditEventLog();

		//Get the instance of AuditableMetaData to read the auditable properties of the
		// domain objects
		AuditableMetadataUtil unMarshaller = new AuditableMetadataUtil();
		AuditableMetaData metadata = unMarshaller.getAuditableMetaData();
		Collection<AuditableClass> classList = metadata.getAuditableClass();
		/**
		 * In classList object all audiTable attributes of the domain classes.
		 * are present which are read from AuditableMetadata.xml file.
		 */
		if (classList != null)
		{
			Iterator<AuditableClass> classListIterator = classList.iterator();
			while (classListIterator.hasNext())
			{
				AuditableClass auditableClass = classListIterator.next();
				boolean flag=callAuditClassMethod(obj, previousObj, auditEventLog, auditableClass);
				if(flag)
				{
					break ;
				}
			}
		}
		return auditEventLog;
	}
	/**
	 * This method calls the auditClassInstance method for auditing.
	 * @param obj Current AuditableObject.
	 * @param previousObj Previous AuditableObject.
	 * @param auditEventLog AuditEvenLog object for storing AuditEventDetails.
	 * @param auditableClass Instance of the Castor class of the object being audited
	 * @return boolean result showing audit event details added successfully.
	 * @throws AuditException throws the Audit Exception.
	 */
	private boolean callAuditClassMethod(Auditable obj,Auditable previousObj,
			DataAuditEventLog auditEventLog,AuditableClass auditableClass) throws AuditException
	{
		boolean flag = false ;
		if (obj == null)
		{
			if (previousObj.getClass().getName().equals(auditableClass.getClassName()))
			{
				auditClassInstance(obj,previousObj,auditEventLog,auditableClass);
				flag = true ;
			}
		}
		else if(obj.getClass().getName().equals(auditableClass.getClassName()))
		{
			auditClassInstance(obj,previousObj,auditEventLog,auditableClass);
			flag = true;
		}
		return flag ;
	}
	/**
	 * Audits the given object according to the mappings specified by AuditableClass instance.
	 * @param obj AuditableObject.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditClassInstance(Auditable obj, Auditable previousObj,
			DataAuditEventLog auditEventLog, AuditableClass auditableClass) throws AuditException
	{
		// Set System identifier if the current object.
		if(obj == null)
		{
			auditEventLog.setObjectIdentifier(previousObj.getId());
		}
		else
		{
			auditEventLog.setObjectIdentifier(obj.getId());
		}
		HibernateMetaData hibernateMetaData = HibernateMetaDataFactory
				.getHibernateMetaData(this.applicationName); 
		
		LOGGER.info(" ::: Application name ::: " + applicationName) ;
		if (hibernateMetaData == null)
		{
			auditEventLog.setObjectName("");
		}
		else
		{
			if(obj == null)
			{
				auditEventLog.setObjectName
				(hibernateMetaData.getTableName(previousObj.getClass()));
			}
			else
			{
				auditEventLog.setObjectName(hibernateMetaData.getTableName(obj.getClass()));
			}
		}
		Object currentObj = null ;
		if(obj != null)
		{
			currentObj = HibernateMetaData.getProxyObjectImpl(obj);
		}

		//Audit simple attributes of the object
		auditSimpleAttributes(previousObj, auditEventLog, auditableClass, currentObj);

		//Audit reference attributes of the object
		auditReferences(previousObj, auditEventLog, auditableClass, currentObj);

		//Audit containment associations of the object
		auditContainments(previousObj, auditEventLog, auditableClass, currentObj);
	}
	/**
	 * Audits containment relations defined for the object,
	 * as mentioned in the auditableMetadata.xml.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditContainments(Auditable previousObj, DataAuditEventLog auditEventLog,
			AuditableClass auditableClass, Object currentObj) throws AuditException
	{
		if (auditableClass.getContainmentAssociationCollection() != null
				&& !auditableClass.getContainmentAssociationCollection().isEmpty())
		{
			processContainments(auditEventLog, currentObj, previousObj, auditableClass,
					auditableClass.getContainmentAssociationCollection());
		}
	}
	/**
	 * Audits reference relations defined for the object,
	 * as mentioned in the AuditableMetadata.xml.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditReferences(Auditable previousObj, DataAuditEventLog auditEventLog,
			AuditableClass auditableClass, Object currentObj) throws AuditException
	{
		Set<AuditEventDetails> auditEventDetailsCollection = null;
		if (auditableClass.getReferenceAssociationCollection() != null
				&& !auditableClass.getReferenceAssociationCollection().isEmpty())
		{
			auditEventDetailsCollection = processAssociations(currentObj, previousObj,
					auditableClass, auditableClass.getReferenceAssociationCollection());

			if (auditEventDetailsCollection != null
					&& !auditEventDetailsCollection.isEmpty())
			{
				auditEventLog.getAuditEventDetailsCollcetion().addAll(
						auditEventDetailsCollection);
			}
		}
	}
	/**
	 * Audits the simple attributes of the auditableObject.
	 * defined in the auditableMetadata.xml.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditSimpleAttributes(Auditable previousObj, DataAuditEventLog auditEventLog,
			AuditableClass auditableClass, Object currentObj) throws AuditException
	{
		Set<AuditEventDetails> auditEventDetailsCollection = null;
		if (auditableClass.getAttributeCollection() != null
				&& !auditableClass.getAttributeCollection().isEmpty())
		{
			auditEventDetailsCollection = processAttributes(previousObj, auditableClass, currentObj);
			if (auditEventDetailsCollection != null && !auditEventDetailsCollection.isEmpty())
			{
				auditEventLog.getAuditEventDetailsCollcetion().addAll(auditEventDetailsCollection);
			}
		}
	}
	/**
	 * This method adds a child AuditEventLog to the root.
	 * AuditEventLog object for each contained object association it has.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param currentObj Object.
	 * @param previousObj AuditableObject.
	 * @param auditableClass AuditableClass object.
	 * @param containmentAssoColl AuditableClass collection.
	 * @throws AuditException throw AuditException.
	 */
	private void processContainments(DataAuditEventLog auditEventLog, Object currentObj, // NOPMD
			Auditable previousObj, AuditableClass auditableClass,
			Collection<AuditableClass> containmentAssoColl) throws AuditException
	{
		Iterator<AuditableClass> containmentItert = containmentAssoColl.iterator();

		while (containmentItert.hasNext())
		{
			AuditableClass containmentClass = containmentItert.next();
			Object containedObj = auditableClass.invokeGetterMethod(containmentClass.getRoleName(),
					currentObj);

			if(previousObj == null)
			{
				auditEventLog.getContainedObjectLogs().addAll(
						addContainmentObjects(containedObj)) ;
			}
			else	// insert the containment object with the collection values.
			{
				Object prevObject = auditableClass.
				invokeGetterMethod(containmentClass.getRoleName(),previousObj);
				/**
				 * call this method to update the containment object with new collection.
				 * prevObject has previous collection values and
				 * containedObj has current collection values
				 */
				if ((prevObject instanceof Collection) && (containedObj instanceof Collection))
				{
					auditEventLog.getContainedObjectLogs().addAll(
					 getContainmentsForUpdate(containedObj, prevObject)) ;
				}
			}
		}
	}
	/**
	 * Adds the Containment object collection to auditEventDetails collection.
	 * @param containedObj Current object.
	 * @return Collection of AuditEventLog object.
	 * @throws AuditException throws an Audit Exception.
	 */
	private Collection<DataAuditEventLog> addContainmentObjects(Object containedObj)
							throws AuditException
	{
		DataAuditEventLog auditEventLog = new DataAuditEventLog() ;
		if (containedObj instanceof Collection)
		{
			for (Object object : (Collection) containedObj)
			{
				if (object instanceof Auditable)
				{
					auditEventLog.getContainedObjectLogs().addAll(
					getContainmentObject((Auditable) object,(Auditable) null));
				}
			}
		}
		return auditEventLog.getContainedObjectLogs();
	}
	/**
	 * This method creates AuditEventDetails collection.
	 * for the reference associations present in the object.
	 * @param currentObj Object.
	 * @param previousObj AuditableObject.
	 * @param auditableClass AuditableClass object.
	 * @param referenceAssociationCollection AuditableClass collection.
	 * @return AuditEventDetails set.
	 * @throws AuditException throw AuditException.
	 */
	private Set<AuditEventDetails> processAssociations(Object currentObj, Auditable previousObj,
			AuditableClass auditableClass, Collection<AuditableClass> referenceAssociationCollection)
			throws AuditException
	{
		Set<AuditEventDetails> auditEventDetailsCollection = new HashSet<AuditEventDetails>();
		Iterator<AuditableClass> associationItert = referenceAssociationCollection.iterator();
		while (associationItert.hasNext())
		{
			// the associated object to the main object
			AuditableClass associationClass = associationItert.next();

			Object currentReferencedObj = auditableClass.invokeGetterMethod(associationClass
					.getRoleName(), currentObj);

			Object previousReferencedObj = null;
			if (previousObj != null)
			{
				previousReferencedObj = auditableClass.invokeGetterMethod(associationClass
						.getRoleName(), previousObj);
			}
			if (currentReferencedObj instanceof Collection)
			{
				List<String> currentValue = getCollectionValueList(currentReferencedObj);
				List<String> previousValue = null;
				if (previousReferencedObj != null)
				{
					previousValue = getCollectionValueList(previousReferencedObj);
				}
				/**
				 * call this method for either insert or update with new
				 * collection values of object which has association relationship.
				 * currentValue holds the current collection values.
				 * previousValue holds the previous collection values.
				 */
				auditEventDetailsCollection.addAll(
						getRefObjectCollection(currentValue, previousValue,
							previousObj, currentObj, associationClass.getRoleName())) ;
			}
		}
		return auditEventDetailsCollection;
	}
	/**
	 * This method creates an AuditEventDetails instance for the given values.
	 * of the referenced association.
	 * @param attributeName String value.
	 * @param previousFieldValue String value holds previous value.
	 * @param currentFieldValue String value holds current value..
	 * @return AuditEventDetails collection.
	 */
	private AuditEventDetails processRefernceAttribute(String attributeName, String previousFieldValue,
			String currentFieldValue)
	{
		AuditEventDetails auditEventDetails = null;
		// Get the old value of the attribute from previousObject
		Object prevVal = previousFieldValue;

		// Get the current value of the attribute from currentObject
		Object currVal = currentFieldValue;

		//AuditEventDetails collectionAuditEventDetails = null;
		// Case of transient object
		if (!(TextConstants.EMPTY_STRING.equals(attributeName)))
		{
			// Compare the old and current value
			auditEventDetails = compareValue(prevVal, currVal);
			if (auditEventDetails != null)
			{
				auditEventDetails.setElementName(attributeName);
			}
		}
		return auditEventDetails;
	}
	/**
	 * This method prepares the collection of AuditEventDetails for the given AuditEventLog.
	 * @param previousObj AuditableObject.
	 * @param auditableClass AuditableClass Object.
	 * @param currentObj Object.
	 * @return AuditEventDetails set.
	 * @throws AuditException throw AuditException.
	 */
	private Set<AuditEventDetails> processAttributes(Auditable previousObj,
			AuditableClass auditableClass, Object currentObj) throws AuditException
	{
		Set<AuditEventDetails> auditEventDetailsCollection = new HashSet<AuditEventDetails>();
		for (Attribute attribute : auditableClass.getAttributeCollection())
		{
			AuditEventDetails auditEventDetails = processSingleAttirubte(auditableClass, attribute
					.getName(), currentObj, previousObj);
			if (auditEventDetails != null)
			{
				auditEventDetailsCollection.add(auditEventDetails);
			}
		}

		return auditEventDetailsCollection;
	}
	/**
	 * This method processes the single attribute of the AuditableObject.
	 * @param auditableClass AuditableClass Object
	 * @param attributeName String value.
	 * @param currentObj Object.
	 * @param previousObj AuditableObject.
	 * @return AuditEventDetails object.
	 * @throws AuditException throw AuditException.
	 */
	private AuditEventDetails processSingleAttirubte(AuditableClass auditableClass,
			String attributeName, Object currentObj, Auditable previousObj) throws AuditException
	{
		// Get the old value of the attribute from previousObject
		Object prevVal = auditableClass.invokeGetterMethod(attributeName, previousObj);
		prevVal = getObjectValue(null, prevVal);
		// Get the current value of the attribute from currentObject
		Object currVal = auditableClass.invokeGetterMethod(attributeName, currentObj);
		currVal = getObjectValue(null, currVal);

		// Find the corresponding column in the database
		String columnName = "";
		HibernateMetaData hibernateMetaData = HibernateMetaDataFactory
				.getHibernateMetaData(this.applicationName);

		if (hibernateMetaData == null)
		{
			columnName = "";
		}
		else
		{
			if(currentObj == null)
			{
				columnName = hibernateMetaData.getColumnName(previousObj.getClass(),attributeName);
			}
			else
			{
				columnName = hibernateMetaData.getColumnName(currentObj.getClass(), attributeName);
			}
		}

		AuditEventDetails auditEventDetails = null;
		// Case of transient object
		if (!(TextConstants.EMPTY_STRING.equals(columnName)))
		{
			// Compare the old and current value
			auditEventDetails = compareValue(prevVal, currVal);
			if (auditEventDetails != null)
			{
				auditEventDetails.setElementName(columnName);
			}
		}
		return auditEventDetails;
	}
	/**
	 * Returns the collection values of objects having association.
	 * relationship with AuditableObject.
	 * @param currentContainedObj Object.
	 * @return List of String values.
	 */
	private List<String> getCollectionValueList(Object currentContainedObj)
	{
		List<String> collectionValueList = new ArrayList<String>();

		if (currentContainedObj instanceof Collection)
		{
			for (Object object : (Collection) currentContainedObj)
			{
				if (object instanceof Auditable ) // AbstractDomainObject)
				{
					collectionValueList.add(((Auditable)
							object).getId().toString());
				}
				if (isVariable(object))
				{
					collectionValueList.add("" + object);
				}
			}
		}

		return collectionValueList;
	}
	/**
	 * Process each field to find the change from previous value to current value.
	 * @param method reference of getter method object access the current and previous value of the object.
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
		String columnName ;
		HibernateMetaData hibernateMetaData = HibernateMetaDataFactory
		.getHibernateMetaData(this.applicationName);
		if (hibernateMetaData == null)
		{
			columnName = "";
		}
		else
		{
			columnName = hibernateMetaData.getColumnName(currentObj.getClass(), attributeName);
		}

		AuditEventDetails auditEventDetails = null;
		//Case of transient object
		if (!(TextConstants.EMPTY_STRING.equals(columnName)))
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
	 * This function gets the value returned by the method invoked by given object.
	 * @param obj Object for which method should be invoked.
	 * @param method This is the method for which we have to find out the return value.
	 * @return Object return value.
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
	 * This method returns the object value.
	 * @param value object.
	 * @param val method object.
	 * @return value.
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
	 * This function compares the prevVal object and currVal object
	 * and if there is any change in value then create the AuditEvenDetails object.
	 * @param prevVal previous value.
	 * @param currVal current value.
	 * @return AuditEventDetails.
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
	 * This function compares the prevVal object and currVal object.
	 * and if there is any change in value then create the AuditEvenDetails object.
	 * @param prevVal prevValue.
	 * @param currVal currValue.
	 * @return auditEventDetails.
	 */
	private AuditEventDetails compareValLogic(Object prevVal, Object currVal)
	{
		AuditEventDetails auditEventDetails;
		if (currVal == null || prevVal == null)
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
	 * This function compares the prevVal object and currVal object.
	 * and if there is any change in value then create the AuditEvenDetails object.
	 * @param prevVal previous Value.
	 * @param currVal current Value.
	 * @return auditEventDetails.
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
	 * @param dao DAO object.
	 * @throws DAOException generic DAOException.
	 */
	private void insert(DAO dao) throws DAOException
	{
		if (auditEvent.getAuditEventLogCollection().isEmpty())
		{
			return;
		}

		dao.insert(auditEvent);
		Iterator<AbstractAuditEventLog> auditLogIterator = auditEvent.getAuditEventLogCollection()
		.iterator();
		while (auditLogIterator.hasNext())
		{
			AbstractAuditEventLog auditEventLog = (AbstractAuditEventLog) auditLogIterator.next();
			auditEventLog.setAuditEvent(auditEvent);
			//dao.insert(auditEventLog, false, "");
			dao.insert(auditEventLog);
			insertAuditEventDetails(dao, auditEventLog);
		}
		 auditEvent = new AuditEvent();
		//auditEvent = new AuditEvent();
	}
	/**
	 * This method inserts audit Event details in database.
	 * @param dao DAO object.
	 * @param auditEventLog AbstractAuditEventLog object.
	 * @throws DAOException throw DAOException.
	 */
	private void insertAuditEventDetails(DAO dao, AbstractAuditEventLog auditEventLog)
	throws DAOException
	{
		if (auditEventLog instanceof DataAuditEventLog)
		{
			insertAuditEventLogs(dao, (DataAuditEventLog) auditEventLog);
		}
	}
	/**
	 *
	 * @param dao DAO object.
	 * @param auditEventLog DataAuditEventLog object.
	 * @throws DAOException throw DAOException
	 */
	private void insertAuditEventLogs(DAO dao,DataAuditEventLog auditEventLog) throws DAOException
	{
		Iterator<AuditEventDetails> auditEventDetailsIterator = auditEventLog
		.getAuditEventDetailsCollcetion().iterator();
		while (auditEventDetailsIterator.hasNext())
		{
			AuditEventDetails auditEventDetails = (AuditEventDetails) auditEventDetailsIterator
			.next();
			auditEventDetails.setAuditEventLog(auditEventLog);
			dao.insert(auditEventDetails);
			//dao.insert(auditEventDetails,false,"");
		}
	}
	/**
	 * This method adds Audit Event Logs.
	 * @param auditEventLogsCollection audit Event Logs Collection.
	 *//*
	public void addAuditEventLogs(Collection<AuditEventLog> auditEventLogsCollection)
	{
		auditEvent.getAuditEventLogCollection().addAll(auditEventLogsCollection);
	}*/
	/**
	 * This method inserts the audit details of object which has passed as a parameter.
	 * @param dao DAO object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 * @throws DAOException throw DAOException.
	 */
	public void insertAudit(DAO dao,Object currentObj)throws AuditException, DAOException
	{
		if (currentObj instanceof Auditable)
		{
			audit(currentObj, null, "INSERT");
			insert(dao);
		}
	}
	/**
	 * This method updates the audit details of object which has passed as a parameter.
	 * @param dao DAO object.
	 * @param currentObj Current Object.
	 * @param previousObj Previous object.
	 * @throws AuditException throw AuditException.
	 * @throws DAOException throw DAOException.
	 */
	public void updateAudit(DAO dao,Object currentObj, Object previousObj)
					throws AuditException, DAOException
	{
		if (currentObj instanceof Auditable)
		{
			audit(currentObj, previousObj, "UPDATE");
			insert(dao);
		}
	}
	/**
	 * Adds the record of AuditableObject into the Database.
	 * @param dao DAO object.
	 * @param auditEventLogsCollection Collection object.
	 * @throws AuditException throw AuditException.o
	 * @throws DAOException throw DAOException.
	 */
	public void addAuditEventLogs(DAO dao,Collection<AbstractAuditEventLog> auditEventLogsCollection)
					throws AuditException, DAOException
	{
		auditEvent.getAuditEventLogCollection().addAll(auditEventLogsCollection);
		insert(dao);
	}
	/**
	 * This method returns the AuditEventDetails of contained object.
	 * with the previous and current values.
	 * @param currentObject Current object representing current values.
	 * @param previousObject Previous object representing previous values.
	 * @return DataAuditEventLog object.
	 * @throws AuditException throw AuditException.
	 */
	private Collection<DataAuditEventLog> getContainmentObject(Auditable currentObject,
			Auditable previousObject) throws AuditException
	{
		DataAuditEventLog auditEventLog = new DataAuditEventLog();

		DataAuditEventLog childAuditEventLog =	processMethods((Auditable) currentObject,
					(Auditable) previousObject);

		auditEventLog.getContainedObjectLogs().add(childAuditEventLog);

		return auditEventLog.getContainedObjectLogs() ;
	}
	/**
	 * This method returns the String representation of Collection values with.
	 * Previous Collection values and Current Collection values.
	 * @param currentCollectionIds Current Collection values.
	 * @param prevCollectionIds Previous Collection values.
	 * @return AuditEventDetails Object.
	 */
	private AuditEventDetails getAuditEventCollList
					(String currentCollectionIds,String prevCollectionIds)
	{
		AuditEventDetails auditEventDetails = new AuditEventDetails();
		auditEventDetails.setElementName("PREV_CURR_IDS_LIST");
		auditEventDetails.setCurrentValue(currentCollectionIds.substring(Constants.CONSTANT_ONE));
		auditEventDetails.setPreviousValue(prevCollectionIds.substring(Constants.CONSTANT_ONE));
		return auditEventDetails;
	}
	/**
	 * This method adds a child AuditEventLog object holding current Collection.
	 * values and its previous Collection values.
	 * @param containedObjColl Current Contained Object collection.
	 * @param prevObjColl Previous object Collection.
	 * @return DataAuditEventLog object which represents current values and previous values.
	 * @throws AuditException throw AuditException.
	 */
	private Collection<DataAuditEventLog> getContainmentsForUpdate(Object containedObjColl,
			Object prevObjColl) throws AuditException
	{
		DataAuditEventLog auditEventLog = new DataAuditEventLog();
		StringBuffer currentObjectIds= new StringBuffer("") ;
		StringBuffer previousObjectIds= new StringBuffer("") ;
		for (Object currObject : (Collection) containedObjColl)
		{
			String str = Constants.COLON_SEPARATOR + ((Auditable)currObject).getId();
			currentObjectIds.append(str) ;
			boolean flag = false ;
			for(Object prevObject : (Collection)prevObjColl)
			{
				flag = compareAuditableObject(currObject, prevObject) ;
				/**
				 * if previous and current values are same then add it
				 * to database with these values as previous and current value.
				 */
				if (flag)
				{
					str = Constants.COLON_SEPARATOR	+ ((Auditable)prevObject).getId() ;
					previousObjectIds.append(str) ;

					auditEventLog.getContainedObjectLogs().addAll(
					getContainmentObject((Auditable) currObject,(Auditable) prevObject)) ;
					((Collection)prevObjColl).remove(prevObject);
					break ;

				}
			}
			// If it is new collection then add it to DB with previous value as NULL.
			if(!flag)
			{
				auditEventLog.getContainedObjectLogs().
				addAll(getContainmentObject((Auditable) currObject,(Auditable) null));
			}
		}
		/**
		 * Adds the remaining collection values which are removed from
		 * the current object and replaced with new collection values for auditing.
		 * current values will be NULL for inserting into database.
		 */
		auditEventLog.getContainedObjectLogs().add(getRemainingContainements
				(prevObjColl, previousObjectIds.toString(), currentObjectIds.toString()));

		return auditEventLog.getContainedObjectLogs() ;
	}
	/**
	 * This method adds those collection values to child AuditEventLog which are.
	 * removed from the AuditableObject.
	 * @param prevObj Previous object representing previous collection values.
	 * @param previousIds Previous collection values.
	 * @param currentIds Current Collection values.
	 * @return DataAuditEventLog object showing current and previous collection value list.
	 * @throws AuditException throw AuditException.
	 */
	private DataAuditEventLog getRemainingContainements(Object prevObj,
			String previousIds, String currentIds) throws AuditException
	{
		DataAuditEventLog auditEventLog = new DataAuditEventLog() ;
		StringBuffer prevIds = new StringBuffer(previousIds) ;
		if(prevObj != null && ((Collection)prevObj).size() > 0)
		{
			for(Object prevObject :(Collection)prevObj)
			{
				String str = Constants.COLON_SEPARATOR + ((Auditable)prevObject).getId();
				prevIds.append(str)  ;
				auditEventLog = processMethods((Auditable) null,
						(Auditable) prevObject);
			}
			auditEventLog.getAuditEventDetailsCollcetion().
			add(getAuditEventCollList(currentIds, prevIds.toString())) ;
		}
		return auditEventLog ;
	}
	/**
	 * This method adds the remaining objects which are deleted.
	 * from the AuditableObject who have association relationship.
	 * @param previousValue Previous value.
	 * @param currentObj Current object representing current values.
	 * @param attributeName Role name of the collection.
	 * @param previousIds Previous values.
	 * @param currentIds Current values.
	 * @return Collection of auditEventDetails object.
	 */
	private Collection<AuditEventDetails> addRemAssoObjColl(List<String> previousValue, // NOPMD
			Object currentObj,String attributeName,String previousIds,String currentIds)
	{
		Collection<AuditEventDetails> auditEventDetailsCollection
							= new ArrayList<AuditEventDetails>();
		StringBuffer prevIds = new StringBuffer(previousIds);
		if (previousValue != null && (!previousValue.isEmpty()))
		{
			for (String previousFieldValue : previousValue)
			{
				String str = Constants.COLON_SEPARATOR + previousFieldValue;
				prevIds.append(str) ;
				AuditEventDetails eventDetails = processRefernceAttribute(attributeName,
						previousFieldValue, null);

				if (eventDetails != null)
				{
					auditEventDetailsCollection.add(eventDetails);
				}
				if(previousValue.isEmpty())
				{
					break;
				}
			}
			auditEventDetailsCollection.
			add(getAuditEventCollList(currentIds, prevIds.toString()));
		}
		return auditEventDetailsCollection ;
	}
	/**
	 * This method adds all associated object with their previous and current collection list.
	 * @param currentValue List representing current values.
	 * @param previousValue List representing previous values.
	 * @param previousObj Previous object holding previous values.
	 * @param currentObj Current object holding current values.
	 * @param attributeName Role name of the Collection.
	 * @return Collection of AuditEventDetails to add into the database.
	 */
	private Collection<AuditEventDetails> getRefObjectCollection(List<String> // NOPMD
			currentValue,List<String> previousValue,Object previousObj,
			Object currentObj,String attributeName)
	{
		StringBuffer currentIds= new StringBuffer("");
		StringBuffer previousIds=new StringBuffer("");
		Collection<AuditEventDetails> auditEventDetailsCollection =
							new ArrayList<AuditEventDetails>();
		for (String currentFieldValue : currentValue)
		{
			String str = Constants.COLON_SEPARATOR + currentFieldValue;
			currentIds.append(str);
			boolean flag = false ;
			if(previousObj == null) // if is is a new collection
			{ 					//  then insert with previous value as NULL.
				auditEventDetailsCollection.addAll(getAssoDetailsColl
						(currentObj, attributeName, null, currentFieldValue));
			}
			else	// update the object with previous and current collection values
			{	    // where collection objects have association relationship.
				for (String previousFieldValue : previousValue)
				{
					if(previousFieldValue.equals(currentFieldValue))
					{
						String prevStr = Constants.COLON_SEPARATOR + previousFieldValue ;
						previousIds.append(prevStr);
						auditEventDetailsCollection.addAll(getAssoDetailsColl
						(currentObj,attributeName,previousFieldValue,currentFieldValue));
						flag = true ;
						if ((previousValue != null) && (!previousValue.isEmpty()))
						{
							previousValue.remove(currentFieldValue);
						}
					}
					if(previousValue.isEmpty() || flag)
					{	break ;	}
				}
				if(!flag)
				{ // if is is a new collection then insert with previous collection value as NULL.
					auditEventDetailsCollection.addAll(getAssoDetailsColl
							(currentObj,attributeName, null, currentFieldValue)) ;
				}
			}
		}
		/**
		 *  Add the remaining collection values which are.
		 *  deleted with current collection values as NULL.
		 */
		auditEventDetailsCollection.addAll(addRemAssoObjColl(previousValue,
			currentObj,	attributeName , previousIds.toString(), currentIds.toString()));
		return auditEventDetailsCollection ;
	}
	/**
	 * This method adds auditEventDetails into collection where previous value is NULL.
	 * or current value is null or previous and current values are same.
	 * if previousValue is null then adds the NULL to previousValue.
	 * if currentValue is null then adds the NULL to currentValue.
	 * @param currentObj Represents Current collection value list.
	 * @param attributeName Role Name of the Collection.
	 * @param previousFieldValue Previous value.
	 * @param currentFieldValue Current value.
	 * @return Collection of AuditEventDetails.
	 */
	private Collection<AuditEventDetails> getAssoDetailsColl(Object currentObj,
			String attributeName,String previousFieldValue,String currentFieldValue)
	{
		Collection<AuditEventDetails> auditEventDetailsCollection =
			new ArrayList<AuditEventDetails>();

		AuditEventDetails eventDetails = processRefernceAttribute(attributeName,
				previousFieldValue, currentFieldValue);

		if(eventDetails != null)
		{
			auditEventDetailsCollection.add(eventDetails);
		}
		return auditEventDetailsCollection ;
	}
	/**
	 * This method compares the AuditableObject to check whether they are.
	 * same objects or not.
	 * @param currentObject Current AuditableObject.
	 * @param previousObject Previous AuditableObject.
	 * @return boolean result whether same object or not.
	 */
	private boolean compareAuditableObject(Object currentObject, Object previousObject)
	{
		boolean flag = false ;

		if((currentObject instanceof Auditable) && (previousObject instanceof Auditable) &&
				(((Auditable)currentObject).getId()).longValue()
				== (((Auditable)previousObject).getId()).longValue())
		{
			flag = true ;
		}
		return flag ;
	}
	/**
	 * Sets the LoginDetails.
	 * @param loginDetails LoginDetails object to set.
	 */
	private LoginEvent setLoginDetails(LoginDetails loginDetails)
	{
		LoginEvent loginEvent = new LoginEvent();
		loginEvent.setIpAddress(loginDetails.getIpAddress());
		loginEvent.setSourceId(loginDetails.getSourceId());
		loginEvent.setUserLoginId(loginDetails.getUserLoginId());
		return loginEvent ;
	}
	/**
	 * Sets the status of LoginAttempt to loginStatus provided as an argument.
	 * @param loginStatus LoginStatus boolean value.
	 * @param loginDetails LoginDetails object.
	 */
	public void loginAudit(boolean loginStatus,LoginDetails loginDetails)
	{
		LoginEvent loginEvent = setLoginDetails(loginDetails);
		HibernateDAO hibernateDao = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		try
		{
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
			.getDAO();
			hibernateDao.openSession(null);
			loginEvent.setIsLoginSuccessful(loginStatus);
			hibernateDao.insert(loginEvent);
			hibernateDao.commit();
		}
		catch (DAOException daoException)
		{
			Logger.out.debug("Exception while Auditing Login Attempt. " 
					+ daoException.getMessage(), daoException);
		}
		finally
		{
			try
			{
				hibernateDao.closeSession();
			}
			catch (DAOException daoException)
			{
				Logger.out.debug("Exception while Auditing Login Attempt. " 
						+ daoException.getMessage(),daoException);
			}
		}
	}
}