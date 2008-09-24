
package edu.wustl.common.domain;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * @hibernate.class table="CATISSUE_AUDIT_EVENT"
 **/
public class AuditEvent implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	/**
	 * System generated unique id.
	 */
	private Long id;

	/**
	 * Date and time of the event.
	 */
	private Date timestamp = Calendar.getInstance().getTime();

	/**
	 *  Id of the User who performs the event.
	 */
	private Long userId;

	/**
	 * Text comments on event.
	 */
	private String comments;

	/**
	 * IP address of the machine.
	 */
	private String ipAddress;

	private Collection<AuditEventLog> auditEventLogCollection = new HashSet<AuditEventLog>();

	/**
	 * Returns System generated unique id.
	 * @return System generated unique id.
	 * @see #setId(Integer)
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_AUDIT_EVENT_PARAM_SEQ" 
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets unique id.
	 * @param id Identifier to be set.
	 * @see #getId()
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Returns date and time of the event. 
	 * @return Date and time of the event.
	 * @see #setTimestamp(Date)
	 * @hibernate.property name="timestamp" type="timestamp" 
	 * column="EVENT_TIMESTAMP"
	 */
	public Date getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Sets date and time of the event.
	 * @param timestamp Date and time of the event.
	 * @see #getTimestamp()
	 */
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * @hibernate.property name="userId" type="long" column="USER_ID"
	 * @return Returns the Id of the user who performs the event.
	 */
	public Long getUserId()
	{
		return userId;
	}

	/**
	 * @param userId, set the Id of the user.
	 */
	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	/**
	 * Returns text comments on this event. 
	 * @return Text comments on this event.
	 * @see #setComments(String)
	 * @hibernate.property name="comments" type="string" 
	 * column="COMMENTS" length="500"
	 */
	public String getComments()
	{
		return comments;
	}

	/**
	 * Sets text comments on this event.
	 * @param comments text comments on this event.
	 * @see #getComments()
	 */
	public void setComments(String comments)
	{
		this.comments = comments;
	}

	/**
	 * get the Ip address.
	 * @hibernate.property name="ipAddress" type="string"
	 * column="IP_ADDRESS" length="20" 
	 **/
	public String getIpAddress()
	{
		return ipAddress;
	}
	/**
	 * set the Ip address.
	 * @hibernate.property name="ipAddress" type="string"
	 * column="IP_ADDRESS" length="20" 
	 **/
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	/**
	 * return the collection where audit event is added.
	 * @hibernate.set name="auditEventLogCollection" table="CATISSUE_AUDIT_EVENT_LOG"
	 * @hibernate.collection-key column="AUDIT_EVENT_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.common.domain.AuditEventLog"
	 */
	public Collection<AuditEventLog> getAuditEventLogCollection()
	{
		return auditEventLogCollection;
	}
	/**
	 * set the vlue in audit event collection.
	 * @hibernate.set name="auditEventLogCollection" table="CATISSUE_AUDIT_EVENT_LOG"
	 * @hibernate.collection-key column="AUDIT_EVENT_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.common.domain.AuditEventLog"
	 */
	public void setAuditEventLogCollection(Collection<AuditEventLog> auditEventLogCollection)
	{
		this.auditEventLogCollection = auditEventLogCollection;
	}
}