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
import edu.wustl.common.domain.AbstractAuditEventLog;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.DataAuditEventLog;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;

/**
 * AuditManager is an algorithm to figure out the changes with respect to
 * database due to insert, update or delete data from/to database.
 * 
 * @author kapil_kaveeshwar
 */
public class AuditManager {

	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger
			.getCommonLogger(AuditManager.class);

	/**
	 * Instance of Audit event. All the change under one database session are
	 * added under this event.
	 * 
	 */
	private transient AuditEvent auditEvent;

	/**
	 * Instantiate a new instance of AuditManager.
	 * */
	public AuditManager() {
		auditEvent = new AuditEvent();
	}

	/**
	 * Set the id of the logged-in user who performed the changes.
	 * 
	 * @param userId
	 *            System identifier of logged-in user who performed the changes.
	 * */
	public void setUserId(Long userId) {
		auditEvent.setUserId(userId);
	}

	/**
	 * Set the ip address of the machine from which the event was performed.
	 * 
	 * @param iPAddress
	 *            ip address of the machine to set.
	 * */
	public void setIpAddress(String iPAddress) {
		auditEvent.setIpAddress(iPAddress);
	}

	/**
	 * Check whether the object type is a premitive data type or a user defined
	 * datatype.
	 * 
	 * @param obj
	 *            object.
	 * @return return true if object type is a premitive data type else false.
	 */
	private boolean isVariable(Object obj) {
		boolean objectType = obj instanceof Number || obj instanceof String
				|| obj instanceof Boolean || obj instanceof Character
				|| obj instanceof Date;
		return objectType;
	}

	/**
	 * Compares the contents of two objects.
	 * 
	 * @param currentObj
	 *            Current state of object.
	 * @param previousObj
	 *            Previous state of object.
	 * @param eventType
	 *            Event for which the comparator will be called. e.g. Insert,
	 *            update, delete etc.
	 * @throws AuditException
	 *             Audit Exception.
	 */
	public void audit(Auditable currentObj, Auditable previousObj,
			String eventType) throws AuditException {
		compare(currentObj, previousObj, eventType);
	}

	/**
	 * Compares the contents of two objects.
	 * 
	 * @param currentObj
	 *            Current state of object.
	 * @param previousObj
	 *            Previous state of object.
	 * @param eventType
	 *            Event for which the comparator will be called. e.g. Insert,
	 *            update, delete etc.
	 * @throws AuditException
	 *             Audit Exception.
	 */
	private void compare(Auditable currentObj, Auditable previousObj,
			String eventType) throws AuditException {
		LOGGER.debug("Inside isObjectAuditable method.");

		Auditable auditableObject = (Auditable) currentObj;
		if (auditableObject == null) {
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
	 *            current Object
	 * @param previousObj
	 *            previous Object
	 * @param auditableClass
	 * @param auditEventLog
	 *            audit Event Log
	 * @throws AuditException
	 *             Audit Exception.
	 */
	private void compareClassOfObject(Auditable currentObj,
			Auditable previousObj) throws AuditException {

		// Class of the object being compared
		Class currentObjClass = currentObj.getClass();
		Class previousObjClass = currentObjClass;

		if (previousObj != null) {
			previousObjClass = previousObj.getClass();
		}

		// check the class for both objects are equals or not.
		if (previousObjClass.equals(currentObjClass)) {
			DataAuditEventLog auditEventLog = processMethods(currentObj,
					previousObj, currentObjClass);

			auditEvent.getAuditEventLogCollection().add(auditEventLog);
		}
	}

	/**
	 * Process each auditable attribute of the object to find the change from previous value to
	 * current value.
	 * 
	 * @param auditEventLog
	 * @param obj
	 *            current Object
	 * @param previousObj
	 *            previous Object
	 * @param currentObjClass
	 *            current Object Class
	 * @param auditableClass
	 * @return audit Event Details Collection.
	 * @throws AuditException
	 *             Audit Exception.
	 */
	private DataAuditEventLog processMethods(Auditable obj,
			Auditable previousObj, Class currentObjClass) throws AuditException {

		// An audit event will contain many logs.
		DataAuditEventLog auditEventLog = new DataAuditEventLog();

		// Get instance of the Castor class of the object being audited
		AuditableClass auditableClass = null;

		//Get the instance of AuditableMetaData to read the auditable properties of the 
		// domain objects
		AuditableMetadataUtil unMarshaller = new AuditableMetadataUtil();
		AuditableMetaData metadata = unMarshaller.getAuditableMetaData();

		Collection<AuditableClass> classList = metadata.getAuditableClass();

		if (classList != null) {
			Iterator<AuditableClass> classListIterator = classList.iterator();
			while (classListIterator.hasNext()) {
				AuditableClass klass = classListIterator.next();
				if (obj.getClass().getName()
						.equals(klass.getClassName())) {
					auditableClass = klass;

					auditClassInstance(obj, previousObj, auditEventLog,
							auditableClass);
					break;
				}
			}
		}
		return auditEventLog;
	}

	/**
	 * Audits the given object according to the mappings specified by AuditableClass instance.
	 * @param obj
	 * @param previousObj
	 * @param auditEventLog
	 * @param auditableClass
	 * @throws AuditException
	 */
	private void auditClassInstance(Auditable obj, Auditable previousObj,
			DataAuditEventLog auditEventLog, AuditableClass auditableClass)
			throws AuditException {
		// Set System identifier if the current object.
		auditEventLog.setObjectIdentifier(obj.getId());

		auditEventLog.setObjectName(HibernateMetaData
				.getTableName(obj.getClass()));

		Object currentObj = HibernateMetaData
				.getProxyObjectImpl(obj);
		
		//Audit simple attributes of the object
		auditSimpleAttributes(previousObj, auditEventLog, auditableClass,
				currentObj);

		//Audit reference attributes of the object
		auditReferences(previousObj, auditEventLog, auditableClass, currentObj);

		//Audit containment associations of the object
		auditContainments(previousObj, auditEventLog, auditableClass,
				currentObj);
	}

	/**
	 * Audits containment relations defined for the object, as mentioned in the auditablemetadata.xml.
	 * @param previousObj
	 * @param auditEventLog
	 * @param auditableClass
	 * @param currentObj
	 * @throws AuditException
	 */
	private void auditContainments(Auditable previousObj,
			DataAuditEventLog auditEventLog, AuditableClass auditableClass,
			Object currentObj) throws AuditException {
		if (auditableClass.getContainmentAssociationCollection() != null
				&& !auditableClass
						.getContainmentAssociationCollection()
						.isEmpty()) {
			processContainments(auditEventLog, currentObj,
					previousObj, auditableClass, auditableClass
							.getContainmentAssociationCollection());
		}
	}

	/**
	 * Audits reference relations defined for the object, as mentioned in the auditablemetadata.xml.
	 * @param previousObj
	 * @param auditEventLog
	 * @param auditableClass
	 * @param currentObj
	 * @throws AuditException
	 */
	private void auditReferences(Auditable previousObj,
			DataAuditEventLog auditEventLog, AuditableClass auditableClass,
			Object currentObj) throws AuditException {
		Set<AuditEventDetails> collectionAuditEventDetailsCollection = null;
		if (auditableClass.getReferenceAssociationCollection() != null
				&& !auditableClass
						.getReferenceAssociationCollection()
						.isEmpty()) {
			collectionAuditEventDetailsCollection = processAssociations(
					currentObj, previousObj, auditableClass,
					auditableClass
							.getReferenceAssociationCollection());
			if (collectionAuditEventDetailsCollection != null
					&& !collectionAuditEventDetailsCollection
							.isEmpty()) {
				auditEventLog
						.getAuditEventDetailsCollcetion()
						.addAll(
								collectionAuditEventDetailsCollection);
			}
		}
	}

	/**
	 * Audits the simple attributes of the auditable object defined in the auditablemetadata.xml.
	 * @param previousObj
	 * @param auditEventLog
	 * @param auditableClass
	 * @param currentObj
	 * @throws AuditException
	 */
	private void auditSimpleAttributes(Auditable previousObj,
			DataAuditEventLog auditEventLog, AuditableClass auditableClass,
			Object currentObj) throws AuditException {
		Set<AuditEventDetails> auditEventDetailsCollection = null; 
		if (auditableClass.getAttributeCollection() != null
				&& !auditableClass.getAttributeCollection()
						.isEmpty()) {
			auditEventDetailsCollection = processAttributes(
					previousObj, auditableClass, currentObj);
			if (auditEventDetailsCollection != null
					&& !auditEventDetailsCollection.isEmpty()) {
				auditEventLog.getAuditEventDetailsCollcetion()
						.addAll(auditEventDetailsCollection);
			}
		}
	}

	/**
	 * This method adds a child AuditEventLog to the root AuditEventLog object for each contained object
	 * association it has.
	 * @param auditEventLog
	 * @param currentObj
	 * @param previousObj
	 * @param auditableClass
	 * @param containmentAssociationCollection
	 * @throws AuditException
	 */
	private void processContainments(DataAuditEventLog auditEventLog,
			Object currentObj, Auditable previousObj,
			AuditableClass auditableClass,
			Collection<AuditableClass> containmentAssociationCollection)
			throws AuditException {
		Iterator<AuditableClass> containmentItert = containmentAssociationCollection
				.iterator();

		while (containmentItert.hasNext()) {
			AuditableClass containmentClass = containmentItert.next();

			Object containedObj = auditableClass.invokeGetterMethod(
					containmentClass.getRoleName(), currentObj);

			if (containedObj instanceof Collection) {
				for (Object object : (Collection) containedObj) {
					if (object instanceof Auditable) {
						DataAuditEventLog childAuditEventLog = processMethods(
								(Auditable) object, (Auditable) null, object
										.getClass());
						auditEventLog.getContainedObjectLogs().add(
								childAuditEventLog);
					}
				}
			}

		}
	}

	/**
	 * This method creates AuditEventDetails collection for the reference associations present in the object. 
	 * @param currentObj
	 * @param previousObj
	 * @param auditableClass
	 * @param referenceAssociationCollection
	 * @return
	 * @throws AuditException
	 */
	private Set<AuditEventDetails> processAssociations(Object currentObj,
			Auditable previousObj, AuditableClass auditableClass,
			Collection<AuditableClass> referenceAssociationCollection)
			throws AuditException {
		Set<AuditEventDetails> collectionAuditEventDetailsCollection = new HashSet<AuditEventDetails>();
		Iterator<AuditableClass> associationItert = referenceAssociationCollection
				.iterator();
		while (associationItert.hasNext()) {
			// the associated object to the main object
			AuditableClass associationClass = associationItert.next();
			Object currentReferencedObj = auditableClass.invokeGetterMethod(
					associationClass.getRoleName(), currentObj);

			Object previousReferencedObj = null;
			if (previousObj != null) {
				previousReferencedObj = auditableClass.invokeGetterMethod(
						associationClass.getRoleName(), previousObj);
			}
			if (currentReferencedObj instanceof Collection) {
				List<String> currentValue = getCollectionValueList(currentReferencedObj);
				List<String> previousValue = null;
				if (previousReferencedObj != null) {
					previousValue = getCollectionValueList(previousReferencedObj);
				}
				for (String currentFieldValue : currentValue) {
					AuditEventDetails eventDetails = processRefernceAttribute(
							currentObj, associationClass.getRoleName(),
							previousValue, currentFieldValue);
					if (eventDetails != null) {
						collectionAuditEventDetailsCollection.add(eventDetails);
					}
				}

				if (previousValue != null && previousValue.size() > 0) {
					for (String previousFieldValue : previousValue) {
						AuditEventDetails eventDetails = processRefernceAttribute(
								currentObj, associationClass.getRoleName(),
								previousValue, previousFieldValue);
						if (eventDetails != null) {
							collectionAuditEventDetailsCollection
									.add(eventDetails);
						}
					}
				}

			}
		}

		return collectionAuditEventDetailsCollection;
	}

	/**
	 * This method prepares the collection of AuditEventDetails for the given AuditEventLog.
	 * @param previousObj
	 * @param auditableClass
	 * @param currentObj
	 * @return
	 * @throws AuditException
	 */
	private Set<AuditEventDetails> processAttributes(Auditable previousObj,
			AuditableClass auditableClass, Object currentObj)
			throws AuditException {
		Set<AuditEventDetails> auditEventDetailsCollection = new HashSet<AuditEventDetails>();
		for (Attribute attribute : auditableClass.getAttributeCollection()) {
			AuditEventDetails auditEventDetails = processSingleAttirubte(
					auditableClass, attribute.getName(), currentObj,
					previousObj);
			if (auditEventDetails != null) {
				auditEventDetailsCollection.add(auditEventDetails);
			}
		}

		return auditEventDetailsCollection;
	}

	/**
	 * 
	 * @param auditableClass
	 * @param attributeName
	 * @param currentObj
	 * @param previousObj
	 * @return
	 * @throws AuditException
	 */
	private AuditEventDetails processSingleAttirubte(AuditableClass auditableClass,
			String attributeName, Object currentObj, Auditable previousObj)
			throws AuditException {
		// Get the old value of the attribute from previousObject
		Object prevVal = auditableClass.invokeGetterMethod(attributeName,
				previousObj);
		prevVal = getObjectValue(null, prevVal);

		// Get the current value of the attribute from currentObject
		Object currVal = auditableClass.invokeGetterMethod(attributeName,
				currentObj);
		currVal = getObjectValue(null, currVal);

		// Find the currosponding column in the database
		String columnName = HibernateMetaData.getColumnName(currentObj
				.getClass(), attributeName);

		AuditEventDetails auditEventDetails = null;
		// Case of transient object
		if (!(TextConstants.EMPTY_STRING.equals(columnName))) {
			// Compare the old and current value
			auditEventDetails = compareValue(prevVal, currVal);
			if (auditEventDetails != null) {
				auditEventDetails.setElementName(columnName);
			}
		}
		return auditEventDetails;
	}

	private List<String> getCollectionValueList(Object currentContainedObj) {
		List<String> collectionValueList = new ArrayList<String>();

		if (currentContainedObj instanceof Collection) {
			for (Object object : (Collection) currentContainedObj) {
				if (object instanceof AbstractDomainObject) {
					collectionValueList.add(((AbstractDomainObject) object)
							.getId().toString());
				}
				if (isVariable(object)) {
					collectionValueList.add("" + object);
				}
			}
		}

		return collectionValueList;
	}

	/**
	 * This method creates an AuditEventDetails instance for the given values of the
	 * referenced association.
	 * @param currentObj
	 * @param attributeName
	 * @param previousValue
	 * @param currentFieldValue
	 * @return
	 */
	private AuditEventDetails processRefernceAttribute(Object currentObj,
			String attributeName, List<String> previousValue,
			String currentFieldValue) {

		AuditEventDetails auditEventDetails = null;
		// Get the old value of the attribute from previousObject
		Object prevVal = currentFieldValue;

		// Get the current value of the attribute from currentObject
		Object currVal = currentFieldValue;

		AuditEventDetails collectionAuditEventDetails = null;
		// Case of transient object
		if (!(TextConstants.EMPTY_STRING.equals(attributeName))) {
			// Compare the old and current value
			auditEventDetails = compareValue(prevVal, currVal);
			if (auditEventDetails != null) {
				auditEventDetails.setElementName(attributeName);
			}
		}
		if (previousValue != null && previousValue.size() > 0) {
			previousValue.remove(currentFieldValue);
		}

		return auditEventDetails;
	}

	/**
	 * Process each field to find the change from previous value to current
	 * value.
	 * 
	 * @param method
	 *            referance of getter method object access the current and
	 *            previous value of the object.
	 * 
	 * @param currentObj
	 *            instance of current object
	 * @param previousObj
	 *            instance of previous object.
	 * @return AuditEventDetails.
	 * @throws AuditException
	 *             Audit Exception.
	 * */
	private AuditEventDetails processField(Method method, Object currentObj,
			Object previousObj) throws AuditException

	{
		// Get the old value of the attribute from previousObject
		Object prevVal = getValue(previousObj, method);

		// Get the current value of the attribute from currentObject
		Object currVal = getValue(currentObj, method);

		// Parse the attribute name from getter method.
		String attributeName = Utility.parseAttributeName(method.getName());

		// Find the currosponding column in the database
		String columnName = HibernateMetaData.getColumnName(currentObj
				.getClass(), attributeName);

		AuditEventDetails auditEventDetails = null;
		// Case of transient object
		if (!(TextConstants.EMPTY_STRING.equals(columnName))) {
			// Compare the old and current value
			auditEventDetails = compareValue(prevVal, currVal);
			if (auditEventDetails != null) {
				auditEventDetails.setElementName(columnName);
			}
		}
		return auditEventDetails;
	}

	/**
	 * This function gets the value returned by the method invoked my given
	 * object.
	 * 
	 * @param obj
	 *            Object for which method should be invoked
	 * @param method
	 *            This is the method for which we have to find out the return
	 *            value
	 * @return Object return value
	 * @throws AuditException
	 *             Audit Exception.
	 */
	private Object getValue(Object obj, Method method) throws AuditException {
		Object value = null;
		if (obj != null) {
			Object val;
			try {
				val = Utility.getValueFor(obj, method);
				value = getObjectValue(value, val);
			} catch (IllegalAccessException ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new AuditException(ex, "while comparing audit objects");
			} catch (InvocationTargetException iTException) {
				LOGGER.error(iTException.getMessage(), iTException);
				throw new AuditException(iTException,
						"while comparing audit objects");
			}
		}
		return value;
	}

	/**
	 * @param value
	 *            object.
	 * @param val
	 *            method object
	 * @return value
	 */
	private Object getObjectValue(Object value, Object val) {
		Object reqValue = value;
		if (val instanceof Auditable) {
			Auditable auditable = (Auditable) val;
			reqValue = auditable.getId();
		} else if (isVariable(val)) {
			reqValue = val;
		}
		return reqValue;
	}

	/**
	 * This function compares the prevVal object and currval object and if there
	 * is any change in value then create the AuditEvenDetails object.
	 * 
	 * @param prevVal
	 *            previous value
	 * @param currVal
	 *            current value
	 * @return AuditEventDetails
	 */
	private AuditEventDetails compareValue(Object prevVal, Object currVal) {
		AuditEventDetails auditEventDetails = null;
		if (prevVal != null || currVal != null) {
			auditEventDetails = compareValLogic(prevVal, currVal);
		}
		return auditEventDetails;
	}

	/**
	 * This function compares the prevVal object and currval object and if there
	 * is any change in value then create the AuditEvenDetails object.
	 * 
	 * @param prevVal
	 *            prev Value
	 * @param currVal
	 *            curr Value
	 * @return auditEventDetails
	 */
	private AuditEventDetails compareValLogic(Object prevVal, Object currVal) {
		AuditEventDetails auditEventDetails;
		if (prevVal == null || currVal == null) {
			auditEventDetails = compareLogic(prevVal, currVal);
		} else {
			auditEventDetails = new AuditEventDetails();
			auditEventDetails.setPreviousValue(prevVal.toString());
			auditEventDetails.setCurrentValue(currVal.toString());
		}
		return auditEventDetails;
	}

	/**
	 * This function compares the prevVal object and currval object and if there
	 * is any change in value then create the AuditEvenDetails object.
	 * 
	 * @param prevVal
	 *            previos Value
	 * @param currVal
	 *            current Value
	 * @return auditEventDetails
	 */
	private AuditEventDetails compareLogic(Object prevVal, Object currVal) {
		AuditEventDetails auditEventDetails = new AuditEventDetails();
		if (prevVal == null) {
			auditEventDetails.setCurrentValue(currVal.toString());
		} else {
			auditEventDetails.setPreviousValue(prevVal.toString());
		}
		return auditEventDetails;
	}

	/**
	 * This method inserts audit Event details in database.
	 * 
	 * @param dao
	 *            DAO object
	 * @throws DAOException
	 *             generic DAOException
	 */
	public void insert(DAO dao) throws DAOException {
		if (auditEvent.getAuditEventLogCollection().isEmpty()) {
			return;
		}

		dao.insert(auditEvent, false, "");
		Iterator<AbstractAuditEventLog> auditLogIterator = auditEvent
				.getAuditEventLogCollection().iterator();
		while (auditLogIterator.hasNext()) {
			AbstractAuditEventLog auditEventLog = (AbstractAuditEventLog) auditLogIterator
					.next();
			auditEventLog.setAuditEvent(auditEvent);
			dao.insert(auditEventLog, false, "");

			insertAuditEventDetails(dao, auditEventLog);
		}
		auditEvent = new AuditEvent();

	}

	private void insertAuditEventDetails(DAO dao,
			AbstractAuditEventLog auditEventLog) throws DAOException {
		if (auditEventLog instanceof DataAuditEventLog) {
			DataAuditManager auditManager = new DataAuditManager();
			auditManager.insertAuditEventLogs(dao,
					(DataAuditEventLog) auditEventLog);
		}
	}

	/**
	 * This method adds Audit Event Logs.
	 * 
	 * @param auditEventLogsCollection
	 *            audit Event Logs Collection.
	 */
	public void addAuditEventLogs(
			Collection<AbstractAuditEventLog> auditEventLogsCollection) {
		auditEvent.getAuditEventLogCollection()
				.addAll(auditEventLogsCollection);
	}

}