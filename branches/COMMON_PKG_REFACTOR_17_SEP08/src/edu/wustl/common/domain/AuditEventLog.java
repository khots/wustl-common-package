
package edu.wustl.common.domain;

import java.util.Collection;
import java.util.HashSet;

/**
 * @hibernate.class table="CATISSUE_AUDIT_EVENT_LOG"
 **/
public class AuditEventLog implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
	
	/**
	 * System generated unique id.
	 */
	private Long id;
	/**
	 * identifier that identify the event object.
	 */
	private Long objectIdentifier;
	/**
	 * Name of the event object.
	 */
	private String objectName;
	/**
	 * Type of the event.
	 */
	private String eventType;
	/**
	 * object of audit event.
	 */
	private AuditEvent auditEvent;
	/**
	 * collection that contains details of event audit.
	 */
	private Collection<AuditEventDetails> auditEventDetailsCollection = new HashSet<AuditEventDetails>();

	/**
	 * get the id.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native" 
	 * @hibernate.generator-param name="sequence" value="CATISSUE_AUDIT_EVENT_LOG_SEQ"
	 */
	public Long getId()
	{
		return id;
	}
	/**
	 * set the id.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * get the identifier of the object.
	 * @hibernate.property name="objectIdentifier" type="long" 
	 * column="OBJECT_IDENTIFIER" length="50"
	 */
	public Long getObjectIdentifier()
	{
		return objectIdentifier;
	}
	/**
	 * set the object Identifier.
	 */
	public void setObjectIdentifier(Long objectIdentifier)
	{
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 * get the name of object.
	 * @hibernate.property name="ObjectName" type="string" 
	 * column="OBJECT_NAME" length="50"
	 */
	public String getObjectName()
	{
		return objectName;
	}
	/**
	 * set the name of the object.
	 */
	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	/**
	 * get the type of the event.
	 * @hibernate.property name="eventType" type="string" 
	 * column="EVENT_TYPE" length="50"
	 */
	public String getEventType()
	{
		return eventType;
	}
	/**
	 * set the type of the event.
	 */
	public void setEventType(String eventType)
	{
		this.eventType = eventType;
	}

	/**
	 * get the audit event object.
	 * @hibernate.many-to-one column="AUDIT_EVENT_ID"  class="edu.wustl.common.domain.AuditEvent" constrained="true"
	 * @see #setParticipant(Site)
	 */
	public AuditEvent getAuditEvent()
	{
		return auditEvent;
	}
	/**
	 * set the audit event object.
	 */
	public void setAuditEvent(AuditEvent auditEvent)
	{
		this.auditEvent = auditEvent;
	}

	/**
	 * get the collection.
	 * @hibernate.set name="auditEventDetailsCollcetion" table="CATISSUE_AUDIT_EVENT_DETAILS"
	 * @hibernate.collection-key column="AUDIT_EVENT_LOG_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.common.domain.AuditEventDetails"
	 */
	public Collection<AuditEventDetails> getAuditEventDetailsCollcetion()
	{
		return auditEventDetailsCollcetion;
	}
	/**
	 * set the collection with audit event details.
	 */
	public void setAuditEventDetailsCollcetion(
			Collection<AuditEventDetails> auditEventDetailsCollcetion)
	{
		this.auditEventDetailsCollcetion = auditEventDetailsCollcetion;
	}

	public String toString()
	{
		return id + " " + objectIdentifier + " " + objectName + " " + eventType + " \n "
				+ auditEventDetailsCollcetion;
	}
}