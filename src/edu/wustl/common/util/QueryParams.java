package edu.wustl.common.util;

import java.sql.Connection;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;

public class QueryParams
{
	
	private String query;
	private Connection connection;
	private	SessionDataBean sessionDataBean;
	private boolean secureToExecute;
	private boolean hasConditionOnIdentifiedField;
	private Map queryResultObjectDataMap;
	private int startIndex;
	private int noOfRecords;
	
	public String getQuery()
	{
		return query;
	}
	public void setQuery(String query)
	{
		this.query = query;
	}
	public Connection getConnection()
	{
		return connection;
	}
	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}
	public SessionDataBean getSessionDataBean()
	{
		return sessionDataBean;
	}
	public void setSessionDataBean(SessionDataBean sessionDataBean)
	{
		this.sessionDataBean = sessionDataBean;
	}
	public boolean isSecureToExecute()
	{
		return secureToExecute;
	}
	public void setSecureToExecute(boolean isSecureExecute)
	{
		this.secureToExecute = isSecureExecute;
	}
	public boolean isHasConditionOnIdentifiedField()
	{
		return hasConditionOnIdentifiedField;
	}
	public void setHasConditionOnIdentifiedField(
			boolean hasConditionOnIdentifiedField)
	{
		this.hasConditionOnIdentifiedField = hasConditionOnIdentifiedField;
 	}
	public Map getQueryResultObjectDataMap()
	{
		return queryResultObjectDataMap;
	}
	public void setQueryResultObjectDataMap(Map queryResultObjectDataMap)
	{
		this.queryResultObjectDataMap = queryResultObjectDataMap;
	}
	public int getStartIndex()
	{
		return startIndex;
	}
	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}
	public int getNoOfRecords()
	{
		return noOfRecords;
	}
	public void setNoOfRecords(int noOfRecords)
	{
		this.noOfRecords = noOfRecords;
	}
	

}
