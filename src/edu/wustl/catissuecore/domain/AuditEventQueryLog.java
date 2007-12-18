package edu.wustl.catissuecore.domain;

import java.io.Serializable;


public class AuditEventQueryLog implements Serializable
{
	
	private static final long serialVersionUID = 1234567890L;
	
	/**
     * System generated unique id.
     */
	protected Long id;
	
	protected String queryDetails;
	
	protected AuditEvent auditEvent;

	
	public Long getId()
	{
		return id;
	}

	
	public void setId(Long id)
	{
		this.id = id;
	}

	
	public String getQueryDetails()
	{
		return queryDetails;
	}

	
	public void setQueryDetails(String queryDetails)
	{
		this.queryDetails = queryDetails;
	}

	
	public AuditEvent getAuditEvent()
	{
		return auditEvent;
	}

	
	public void setAuditEvent(AuditEvent auditEvent)
	{
		this.auditEvent = auditEvent;
	}

}
