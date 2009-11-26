/*
 *
 */

package edu.wustl.common.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.wustl.common.audit.util.AuditableMetadataUtil;
import edu.wustl.common.beans.LoginDetails;
import edu.wustl.common.domain.AbstractAuditEventLog;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.DataAuditEventLog;
import edu.wustl.common.domain.LoginEvent;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.util.global.CommonServiceLocator;
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
	 * Collection of all the auditable classes.
	 */
	Collection<AuditableClass> auditableMetadata  = new ArrayList<AuditableClass>();
	
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
	protected void audit(Auditable currentObj, Auditable previousObj,
			String eventType)
	throws AuditException
	{
		if (currentObj instanceof Auditable)
		{

			LOGGER.debug("Inside isObjectAuditable method.");
			if (currentObj == null)
			{
				throw new AuditException(null,"Problem while performing the audit operation, Object not auditable." +
						"Check the instance before calling the audit events.");
			}
			// Set the table name of the current class.
			currentObj = (Auditable)HibernateMetaData.getProxyObjectImpl((Auditable) currentObj);
			
			if (previousObj != null && !currentObj.getClass().equals(previousObj.getClass()))
			{
				throw new AuditException(null,"Problem while performing the audit operation, previous and current instance should " +
						"be of same class type. Check the instance before calling the audit events.");
			}
			
			//Get the instance of AuditableMetaData to read the auditable properties of the 
			// domain objects
			AuditableMetadataUtil unMarshaller = new AuditableMetadataUtil();
			AuditableMetaData metadata = unMarshaller.getAuditableMetaData();

			auditableMetadata = metadata.getAuditableClass();
					
			DataAuditEventLog auditEventLog = obtainAuditableEventLog(currentObj, (Auditable)previousObj);
			auditEvent.getAuditEventLogCollection().add(auditEventLog);

			// Set Event type for the audit Event
			auditEvent.setEventType(eventType);
		
		}
	}
	
	
	/**
	 * Process each getter Methods to find the change from previous value to current value.
	 * @param obj current Object.
	 * @param previousObj previous Object.
	 * @return audit Event Details Collection.
	 * @throws AuditException Audit Exception.
	 */
	private DataAuditEventLog obtainAuditableEventLog(Auditable obj, Auditable previousObj) throws AuditException
	{
		// An audit event will contain many logs.
		DataAuditEventLog auditEventLog = new DataAuditEventLog();

		// Get instance of the Castor class of the object being audited
		//AuditableClass auditableClass = null;

		if (auditableMetadata != null) {
			Iterator<AuditableClass> classListIterator = auditableMetadata.iterator();
			while (classListIterator.hasNext())
			{
				AuditableClass klass = classListIterator.next();
				if (obj.getClass().getName()
						.equals(klass.getClassName())) {
						startAuditing(obj, previousObj, auditEventLog,
							klass);
					break;
				}
			}
		}
		return auditEventLog;
	}
	
	/**
	 * Audits the given object according to the mappings specified by AuditableClass instance.
	 * @param obj AuditableObject.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @throws AuditException throw AuditException.
	 */
	private void startAuditing(Auditable obj, Auditable previousObj,
			DataAuditEventLog auditEventLog, AuditableClass auditableClass) throws AuditException
	{
		// Set System identifier if the current object.
		auditEventLog.setObjectIdentifier(obj.getId());

		HibernateMetaData hibernateMetaData = HibernateMetaDataFactory.
		   getHibernateMetaData(CommonServiceLocator.getInstance().getAppName());
		
		auditEventLog.setObjectName(hibernateMetaData
				.getTableName(obj.getClass()));

		Object currentObj = HibernateMetaData
				.getProxyObjectImpl(obj);
		
		//Audit simple attributes of the object
		auditSimpleAttributes(previousObj, auditEventLog, auditableClass,
				currentObj);

		//Audit reference associations of the object
		auditReferenceAssociations(previousObj, auditEventLog, auditableClass, currentObj);

		//Audit containment associations of the object
		auditContainmentAssociation(previousObj, auditEventLog, auditableClass,
				currentObj);
	}
	/**
	 * Audits containment relations defined for the object,
	 * as mentioned in the auditableMetadata.xml.
	 * It audits the complete object within collection and also previous and current 
	 * Identifiers list.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditContainmentAssociation(Auditable previousObj, DataAuditEventLog auditEventLog,
			AuditableClass auditableClass, Object currentObj) throws AuditException
	{
		if (auditableClass.getContainmentAssociationCollection() != null
				&& !auditableClass.getContainmentAssociationCollection().isEmpty())
		{
			Iterator<AuditableClass> containmentItert = auditableClass.getContainmentAssociationCollection()
			.iterator();

			while (containmentItert.hasNext())
			{
				AuditableClass containmentClass = containmentItert.next();
				Object currentAuditableObject = auditableClass.invokeGetterMethod(
						containmentClass.getRoleName(), currentObj);

				//Case of Insert : when previous object is null. 
				if(previousObj == null)
				{
					//for one to many containment Associations.
					if (currentAuditableObject instanceof Collection)
					{
						for (Object object : (Collection) currentAuditableObject) {
							if (object instanceof Auditable) {
								//Call to obtainAuditableEventLog to audit the object of collection.
								DataAuditEventLog childAuditEventLog = obtainAuditableEventLog(
										(Auditable) object, null);
								auditEventLog.getContainedObjectLogs().add(
										childAuditEventLog);
							}
						}
					}//for one to one containment Associations.
					else if (currentAuditableObject instanceof Auditable)
					{

						//Call to obtainAuditableEventLog to audit the object of collection.
						DataAuditEventLog childAuditEventLog = obtainAuditableEventLog(
								(Auditable) currentAuditableObject, null);
						auditEventLog.getContainedObjectLogs().add(
								childAuditEventLog);

					}
				}
				else
				{
					Object previousAuditableObject = auditableClass.
					invokeGetterMethod(containmentClass.getRoleName(),previousObj);

					//for one to many containment Associations.
					if ((currentAuditableObject instanceof Collection) && (previousAuditableObject instanceof Collection))
					{

						String containmentCollectionObjectName = getAssociationCollectionObjectName(
								(Collection)currentAuditableObject,(Collection)previousAuditableObject);

						if(!(((Collection)currentAuditableObject).isEmpty() && ((Collection)previousAuditableObject).isEmpty()))
						{					
							//Audit identifiers of current and previous objects of collections.
							auditEventLog.getAuditEventDetailsCollcetion().add(
									auditRefrenceAssociationsIds(getColonSeparatedIds((Collection)currentAuditableObject),
											getColonSeparatedIds((Collection)previousAuditableObject),containmentCollectionObjectName));
						}

						//Audit collection entries.
						auditEventLog.getContainedObjectLogs().addAll(
								auditContainment(currentAuditableObject, previousAuditableObject)) ;

					}//for one to one containment Associations.
					else if (currentAuditableObject instanceof Auditable)
					{

						DataAuditEventLog childAuditEventLog = obtainAuditableEventLog(
								(Auditable) currentAuditableObject, (Auditable) previousAuditableObject);
						auditEventLog.getContainedObjectLogs().add(
								childAuditEventLog);

					}
				}

			}
		}
	}
	
	/**
	 * This method is called to obtain name of the object within the collection.
	 * @param currentObjectColl currentObjectColl
	 * @param prevObjectColl prevObjectColl
	 * @return Object Name.
	 */
	public String getAssociationCollectionObjectName(Collection currentObjectColl,Collection prevObjectColl)
	{
		String objectName = "";
		if(currentObjectColl != null && !((Collection)currentObjectColl).isEmpty())
		{
			objectName = (((Collection)currentObjectColl).iterator().next())
			.getClass().getName();
		}
		else if(prevObjectColl != null && !((Collection)prevObjectColl).isEmpty())
		{
			objectName = (((Collection)prevObjectColl).iterator().next())
			.getClass().getName();
		}
		return objectName;
	}
	
	/**
	 * Returns the collection values of objects having association.
	 * relationship with AuditableObject.
	 * @param currentContainedObj Object.
	 * @return List of String values.
	 */
	private String getColonSeparatedIds(Collection<Auditable> currentContainedObj)
	{
		StringBuffer colonSeparatedIds = new StringBuffer("");
		Iterator<Auditable> itr = currentContainedObj.iterator();
		while(itr.hasNext())
		{
			colonSeparatedIds.append((((Auditable)itr.next()).getId()));
			if(itr.hasNext())
			{
				colonSeparatedIds.append(":");
			}
			
		}
		return colonSeparatedIds.toString();	
	}
	
	
	
	/**
	 * Audits reference relations defined for the object,
	 * as mentioned in the AuditableMetadata.xml.
	 * Only the identifier get audited.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog DataAuditEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditReferenceAssociations(Auditable previousObj, DataAuditEventLog auditEventLog,
			AuditableClass auditableClass, Object currentObj) throws AuditException
	{
		if (auditableClass.getReferenceAssociationCollection() != null
				&& !auditableClass.getReferenceAssociationCollection().isEmpty())
		{
			
			Iterator<AuditableClass> associationItert = auditableClass.getReferenceAssociationCollection().iterator();
			while (associationItert.hasNext())
			{
				AuditableClass refrenceAssociation = associationItert.next();

				Object currentAuditableObject = auditableClass.invokeGetterMethod(
						refrenceAssociation.getRoleName(), currentObj);
	
	            //Case of Insert : when previous object is null. 
				if(previousObj == null)
				{
					
					//for one to many Reference Associations.
					if (currentAuditableObject instanceof Collection)
					{
						String associationObjectName = getAssociationCollectionObjectName(
								(Collection)currentAuditableObject,null);
						if(!(((Collection)currentAuditableObject).isEmpty()))
						{
							//colon separated identifier.
							auditEventLog.getAuditEventDetailsCollcetion().add(
								auditRefrenceAssociationsIds(getColonSeparatedIds((Collection)currentAuditableObject),
									null,associationObjectName));
						}
					}//for one to one Reference Associations.
					else if (currentAuditableObject instanceof Auditable)
					{
						//colon separated identifier.
						auditEventLog.getAuditEventDetailsCollcetion().add(
								auditRefrenceAssociationsIds(((Auditable) currentAuditableObject).getId().toString(),
									null,((Auditable) currentAuditableObject).getClass().getName()));

					}
				}
				else // Case of update : having both current and previous objects:
				{
					Object prevAuditableObject = auditableClass.
					invokeGetterMethod(refrenceAssociation.getRoleName(),previousObj);
				
					//for one to many reference association. 
					if ((currentAuditableObject instanceof Collection) &&(prevAuditableObject instanceof Collection))
					{
						String containmentCollectionObjectName = getAssociationCollectionObjectName(
								(Collection)currentAuditableObject,(Collection)prevAuditableObject);
							
						if(!(((Collection)currentAuditableObject).isEmpty() && ((Collection)prevAuditableObject).isEmpty()))
						{
							//colon separated identifier.
							auditEventLog.getAuditEventDetailsCollcetion().add(
								auditRefrenceAssociationsIds(getColonSeparatedIds((Collection)currentAuditableObject),
									getColonSeparatedIds((Collection)prevAuditableObject),containmentCollectionObjectName));
						}
		
					}//for one to one reference association.
					else if (currentAuditableObject instanceof Auditable)
					{
						//colon separated identifier.
						auditEventLog.getAuditEventDetailsCollcetion().add(
								auditRefrenceAssociationsIds(((Auditable) currentAuditableObject).getId().toString(),
										((Auditable)prevAuditableObject).getId().toString(),((Auditable) currentAuditableObject).getClass().getName()));

					}
				}
				
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
				.getHibernateMetaData(applicationName);

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
			audit((Auditable)currentObj, null, "INSERT");
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
			audit((Auditable)currentObj, (Auditable)previousObj, "UPDATE");
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
	 * This method returns the String representation of Collection values with.
	 * Previous Collection values and Current Collection values.
	 * @param currentCollectionIds Current Collection values.
	 * @param prevCollectionIds Previous Collection values.
	 * @return AuditEventDetails Object.
	 */
	private AuditEventDetails auditRefrenceAssociationsIds
					(String currentCollectionIds,String prevCollectionIds, String attributeName)
	{
		AuditEventDetails auditEventDetails = new AuditEventDetails();
		auditEventDetails.setElementName(attributeName+"_PREV_CURR_IDS_LIST");
		auditEventDetails.setCurrentValue(currentCollectionIds);
		auditEventDetails.setPreviousValue(prevCollectionIds);
		return auditEventDetails;
	}
	/**
	 * Audit entities within containment collection.
	 * @param currentObjColl Current Contained Object collection.
	 * @param prevObjColl Previous object Collection.
	 * @return DataAuditEventLog object which represents current values and previous values.
	 * @throws AuditException throw AuditException.
	 */
	private Collection<DataAuditEventLog> auditContainment(Object currentObjColl,
			Object prevObjColl) throws AuditException
	{
		DataAuditEventLog auditEventLog = new DataAuditEventLog();
		
		for (Object currentObject : (Collection) currentObjColl)
		{
			boolean isExists = false ;
			for(Object previousObject : (Collection)prevObjColl)
			{
				//Call to obtainAuditableEventLog to audit the object of collection.
				if((currentObject instanceof Auditable) && (previousObject instanceof Auditable) && 
						((Auditable)currentObject).getId().equals(((Auditable)currentObject).getId()))
				{
									
					auditEventLog.getContainedObjectLogs().add(obtainAuditableEventLog((Auditable) currentObject,
							(Auditable) previousObject));
					isExists = true;
					break;
							
				}
			}// If it is new entry in collection then add it to DB with previous value as NULL.
			if(!isExists)
			{
				//Call to obtainAuditableEventLog to audit the new object of collection.
				auditEventLog.getContainedObjectLogs().add(obtainAuditableEventLog((Auditable) currentObject,
						null));
			}
		}
		return auditEventLog.getContainedObjectLogs() ;
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