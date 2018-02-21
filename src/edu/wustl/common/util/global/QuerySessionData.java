/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.util.global;

import java.io.Serializable;
import java.util.Map;

/**
 * @author prafull_kadam
 * This class will hold the information that will be saved in the session for the Pagenation of the query results.
 */
public class QuerySessionData implements Serializable
{

	/**
   * Generated serial id
   */
  private static final long serialVersionUID = 8418248296885892369L;
  /**
	 *  The SQL for the query for which user is navigating results.
	 */
	private String sql;
	/**
	 * Query Result Object Data Map.
	 */
	private Map queryResultObjectDataMap;
	/**
	 * specify Secure Execute.
	 */
	private boolean isSecureExecute;
	/**
	 * specify Condition On Identified Field.
	 */
	private boolean hasConditionOnIdentifiedField;
	/**
	 * specify records Per Page.
	 */
	private int recordsPerPage;
	/**
	 * specify total Number Of Records.
	 */
	private int totalNumberOfRecords;
	
	private Long queryId = 0l;
	
	private Long auditEventId = Long.valueOf(0L);
	
	
	public Long getAuditEventId() {
	    return this.auditEventId;
	  }
	
	public void setAuditEventId(Long auditEventId) {
	    this.auditEventId = auditEventId;
	}

	/**
	 * @return the hasConditionOnIdentifiedField
	 */
	public boolean isHasConditionOnIdentifiedField()
	{
		return hasConditionOnIdentifiedField;
	}

	/**
	 * @param hasConditionOnIdentifiedField the hasConditionOnIdentifiedField to set
	 */
	public void setHasConditionOnIdentifiedField(boolean hasConditionOnIdentifiedField)
	{
		this.hasConditionOnIdentifiedField = hasConditionOnIdentifiedField;
	}

	/**
	 * @return the isSecureExecute
	 */
	public boolean isSecureExecute()
	{
		return isSecureExecute;
	}

	/**
	 * @param isSecureExecute the isSecureExecute to set
	 */
	public void setSecureExecute(boolean isSecureExecute)
	{
		this.isSecureExecute = isSecureExecute;
	}

	/**
	 * @return the queryResultObjectDataMap
	 */
	public Map getQueryResultObjectDataMap()
	{
		return queryResultObjectDataMap;
	}

	/**
	 * @param queryResultObjectDataMap the queryResultObjectDataMap to set
	 */
	public void setQueryResultObjectDataMap(Map queryResultObjectDataMap)
	{
		this.queryResultObjectDataMap = queryResultObjectDataMap;
	}

	/**
	 * @return the recordsPerPage
	 */
	public int getRecordsPerPage()
	{
		return recordsPerPage;
	}

	/**
	 * @param recordsPerPage the recordsPerPage to set
	 */
	public void setRecordsPerPage(int recordsPerPage)
	{
		this.recordsPerPage = recordsPerPage;
	}

	/**
	 * @return the sql
	 */
	public String getSql()
	{
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql)
	{
		this.sql = sql;
	}

	/**
	 * @return the totalNumberOfRecords
	 */
	public int getTotalNumberOfRecords()
	{
		return totalNumberOfRecords;
	}

	/**
	 * @param totalNumberOfRecords the totalNumberOfRecords to set
	 */
	public void setTotalNumberOfRecords(int totalNumberOfRecords)
	{
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	/**
	 * @return queryId
	 */
	public Long getQueryId() {
		return queryId;
	}

	/**
	 * @param queryId
	 */
	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}
}
