/*
 * Created on Aug 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.domain;

import java.io.Serializable;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * @hibernate.class table="CATISSUE_QUERY_TABLE_DATA"
 * @author gautam_shetty
 */
public class QueryTableData extends AbstractDomainObject implements Serializable
{
	private static final long serialVersionUID = 731008569234191793L;
	/**
	 * identifier to identify the query.
	 */
	private long identifier;
	/**
	 * Name of the table.
	 */
	private String tableName;
	/**
	 * display name of the table.
	 */
	private String displayName;
	/**
	 * Name of the alias.
	 */
	private String aliasName;

	/**
	 * Display name of Table.
	 * @hibernate.property name="displayName" type="string" column="DISPLAY_NAME" length="50"
	 * @return Returns the displayName.
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * @hibernate.id name="identifier" column="TABLE_ID" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_QUERY_TABLE_DATA_SEQ"
	 * @return Returns the identifier.
	 */
	public long getIdentifier()
	{
		return identifier;
	}

	/**
	 * @param identifier The identifier to set.
	 */
	public void setIdentifier(long identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * Returns the name of the table.
	 * @hibernate.property name="tableName" type="string" column="TABLE_NAME" length="50"
	 * @return Returns the tableName.
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName The tableName to set.
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * @hibernate.property name="aliasName" type="string" column="ALIAS_NAME" length="50"
	 * @return Returns the name of alias.
	 */
	public String getAliasName()
	{
		return aliasName;
	}

	/**
	 * @param aliasName The aliasName to set.
	 */
	public void setAliasName(String aliasName)
	{
		this.aliasName = aliasName;
	}
	/**
	 * set the values from the ui form and set it into object.
	 * @param abstractForm from where the data has to be retrieved.
	 */	
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		// TODO Auto-generated method stub
	}
	/**
	 * @return Returns the Id.
	 */
	public Long getId()
	{
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * set the Id.
	 */
	public void setId(Long id)
	{
	}
}