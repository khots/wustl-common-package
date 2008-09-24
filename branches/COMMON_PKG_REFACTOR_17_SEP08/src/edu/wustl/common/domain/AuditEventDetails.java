
package edu.wustl.common.domain;

/**
 * @hibernate.class table="CATISSUE_AUDIT_EVENT_DETAILS"
 **/
public class AuditEventDetails implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	private Long id;
	private String elementName;
	private String previousValue;
	private String currentValue;

	private AuditEventLog auditEventLog;

	/**
	 * @return Long System generated unique identifier.
	 * @see #setId(Long)
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native" 
	 * @hibernate.generator-param name="sequence" value="CATISSUE_AUDIT_EVENT_DET_SEQ"
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
	 * @hibernate.property name="elementName" type="string"
	 * column="ELEMENT_NAME" length="150" 
	 **/
	public String getElementName()
	{
		return elementName;
	}
	/**
	 * set the element name.
	 * @hibernate.property name="elementName" type="string"
	 * column="ELEMENT_NAME" length="150" 
	 **/
	public void setElementName(String elementName)
	{
		this.elementName = elementName;
	}

	/**
	 * return previous value.
	 * @hibernate.property name="previousValue" type="string"
	 * column="PREVIOUS_VALUE" length="150" 
	 **/
	public String getPreviousValue()
	{
		return previousValue;
	}
	/**
	 * set the previous value.
	 * @hibernate.property name="previousValue" type="string"
	 * column="PREVIOUS_VALUE" length="150" 
	 **/

	public void setPreviousValue(String previousValue)
	{
		this.previousValue = previousValue;
	}

	/* return current value
	 * @hibernate.property name="currentValue" type="string"
	 * column="CURRENT_VALUE" length="500" 
	 **/
	public String getCurrentValue()
	{
		return currentValue;
	}
	/* set the current value
	 * @hibernate.property name="currentValue" type="string"
	 * column="CURRENT_VALUE" length="500" 
	 **/

	public void setCurrentValue(String currentValue)
	{
		this.currentValue = currentValue;
	}

	/**
	 * get the audit event log object.
	 * @hibernate.many-to-one column="AUDIT_EVENT_LOG_ID"
	 * class="edu.wustl.common.domain.AuditEventLog" constrained="true"
	 * @see #setParticipant(Site)
	 */
	public AuditEventLog getAuditEventLog()
	{
		return auditEventLog;
	}

	/**
	 * set the value of audit event in audit event log.
	 * @param auditEventLog The auditEventLog to set.
	 */
	public void setAuditEventLog(AuditEventLog auditEventLog)
	{
		this.auditEventLog = auditEventLog;
	}

	public int hashCode()
	{
		int hashCode = 0;

		if (id != null)
		{
			hashCode += id.intValue();
		}
		if (elementName != null)
		{	
			hashCode += elementName.hashCode();
		}	
		if (previousValue != null)
		{	
			hashCode += previousValue.hashCode();
		}	
		if (currentValue != null)
		{	
			hashCode += currentValue.hashCode();
		}	
		return hashCode;
	}
	public String toString()
	{
		return "Id " + id + " " + "ElementName " + elementName + " " + "PreviousValue "
				+ previousValue + " " + "CurrentValue " + currentValue + "\n";
	}

	public boolean equals(Object obj)
	{
		//		if(obj instanceof AuditEventDetails)
		//		{
		//			AuditEventDetails auditEventDetails = (AuditEventDetails)obj;
		//           if(this.id.equals(auditEventDetails.id) && 
		//		       this.elementName.equals(auditEventDetails.elementName) &&
		//	             this.previousValue.equals(auditEventDetails.previousValue) &&
		//		           this.currentValue.equals(auditEventDetails.currentValue))
		//	     return true;
		//		}
		return false;
	}
}