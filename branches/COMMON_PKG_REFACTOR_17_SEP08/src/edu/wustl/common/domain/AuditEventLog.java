
package edu.wustl.common.domain;

import java.util.Collection;
import java.util.HashSet;

/**
 * @hibernate.class table="CATISSUE_AUDIT_EVENT_LOG"
 **/
public class AuditEventLog implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	private Long id;
	private Long objectIdentifier;
	private String objectName;
	private String eventType;
	private AuditEvent auditEvent;
	private Collection<AuditEventDetails> auditEventDetailsCollcetion = new HashSet<AuditEventDetails>();

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native" 
	 * @hibernate.generator-param name="sequence" value="CATISSUE_AUDIT_EVENT_LOG_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @hibernate.property name="objectIdentifier" type="long" 
	 * column="OBJECT_IDENTIFIER" length="50"
	 */
	public Long getObjectIdentifier()
	{
		return objectIdentifier;
	}

	public void setObjectIdentifier(Long objectIdentifier)
	{
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 * @hibernate.property name="ObjectName" type="string" 
	 * column="OBJECT_NAME" length="50"
	 */
	public String getObjectName()
	{
		return objectName;
	}

	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	/**
	 * @hibernate.property name="eventType" type="string" 
	 * column="EVENT_TYPE" length="50"
	 */
	public String getEventType()
	{
		return eventType;
	}

	public void setEventType(String eventType)
	{
		this.eventType = eventType;
	}

	/**
	 * @hibernate.many-to-one column="AUDIT_EVENT_ID"  class="edu.wustl.common.domain.AuditEvent" constrained="true"
	 * @see #setParticipant(Site)
	 */
	public AuditEvent getAuditEvent()
	{
		return auditEvent;
	}

	public void setAuditEvent(AuditEvent auditEvent)
	{
		this.auditEvent = auditEvent;
	}

	/**
	 * @hibernate.set name="auditEventDetailsCollcetion" table="CATISSUE_AUDIT_EVENT_DETAILS"
	 * @hibernate.collection-key column="AUDIT_EVENT_LOG_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.common.domain.AuditEventDetails"
	 */
	public Collection<AuditEventDetails> getAuditEventDetailsCollcetion()
	{
		return auditEventDetailsCollcetion;
	}

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