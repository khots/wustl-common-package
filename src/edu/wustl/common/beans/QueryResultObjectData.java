/*
 * Created on Nov 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.beans;

import java.util.Vector;

/**
 * @author aarti_sharma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QueryResultObjectData
{

	/**
	 * Specify aliasName.
	 */
	private String aliasName;
	/**
	 * Specify identifierColumnId.
	 */
	private int identifierColumnId;
	/**
	 * Specify dependentColumnIds.
	 */
	private Vector dependentColumnIds = new Vector();
	/**
	 * Specify dependentObjectAliases.
	 */
	private Vector dependentObjectAliases = new Vector();
	/**
	 * Specify relatedQueryResultObjects.
	 */
	private Vector relatedQueryResultObjects = new Vector();
	/**
	 * Specify IdentifiedDataColumnIds.
	 */
	private Vector identifiedDataColumnIds = new Vector();

	/**
	 * Constructor.
	 */
	public QueryResultObjectData()
	{
		super();
	}

	/**
	 * Constructor.
	 * @param aliasName alias Name.
	 */
	public QueryResultObjectData(String aliasName)
	{
		this.aliasName = aliasName;
	}

	/**
	 * @return Returns the aliasName.
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
	 * @return Returns the dependentObjectAliases.
	 */
	public Vector getDependentObjectAliases()
	{
		return dependentObjectAliases;
	}

	/**
	 * @param dependentObjectAliases The dependentObjectAliases to set.
	 */
	public void setDependentObjectAliases(Vector dependentObjectAliases)
	{
		this.dependentObjectAliases = dependentObjectAliases;
	}

	/**
	 * @return Returns the identifierColumnId.
	 */
	public int getIdentifierColumnId()
	{
		return identifierColumnId;
	}

	/**
	 * @param identifierColumnId The identifierColumnId to set.
	 */
	public void setIdentifierColumnId(int identifierColumnId)
	{
		this.identifierColumnId = identifierColumnId;
	}

	/**
	 * @return Returns the relatedColumnIds.
	 */
	public Vector getDependentColumnIds()
	{
		return dependentColumnIds;
	}

	/**
	 * @param dependentColumnIds The dependentColumnIds to set.
	 */
	public void setDependentColumnIds(Vector dependentColumnIds)
	{
		this.dependentColumnIds = dependentColumnIds;
	}

	/**
	 * This method add Related Query Result Object.
	 * @param queryResultObjectData query Result Object Data.
	 */
	public void addRelatedQueryResultObject(QueryResultObjectData queryResultObjectData)
	{
		this.relatedQueryResultObjects.add(queryResultObjectData);
	}

	/**
	 * This method gets Number Of Independent Objects.
	 * @return Number Of Independent Objects.
	 */
	public int getNumberOfIndependentObjects()
	{
		return relatedQueryResultObjects.size() + 1;
	}

	/**
	 * get Independent Object Aliases.
	 * @return independent Object Aliases.
	 */
	public Vector getIndependentObjectAliases()
	{
		Vector independentObjectAliases = new Vector();
		independentObjectAliases.add(this.aliasName);
		for (int i = 0; i < relatedQueryResultObjects.size(); i++)
		{
			independentObjectAliases.add(((QueryResultObjectData) relatedQueryResultObjects.get(i))
					.getAliasName());
		}
		return independentObjectAliases;
	}

	/**
	 * This method gets Independent Query Objects.
	 * @return independent Query Objects.
	 */
	public Vector getIndependentQueryObjects()
	{
		Vector independentQueryObjects = new Vector();
		independentQueryObjects.add(this);
		independentQueryObjects.addAll(this.relatedQueryResultObjects);
		return independentQueryObjects;
	}

	/**
	 * @return Returns the relatedQueryResultObjects.
	 */
	public Vector getRelatedQueryResultObjects()
	{
		return relatedQueryResultObjects;
	}

	/**
	 * @param relatedQueryResultObjects The relatedQueryResultObjects to set.
	 */
	public void setRelatedQueryResultObjects(Vector relatedQueryResultObjects)
	{
		this.relatedQueryResultObjects = relatedQueryResultObjects;
	}

	/**
	 * @return Returns the identifiedDataColumnIds.
	 */
	public Vector getIdentifiedDataColumnIds()
	{
		return this.identifiedDataColumnIds;
	}

	/**
	 * @param identifiedDataColumnIds The identifiedDataColumnIds to set.
	 */
	public void setIdentifiedDataColumnIds(Vector identifiedDataColumnIds)
	{
		this.identifiedDataColumnIds = identifiedDataColumnIds;
	}

	/**
	 * This method add Identified Data Column Id.
	 * @param columnId column Id to add.
	 */
	public void addIdentifiedDataColumnId(Integer columnId)
	{
		this.identifiedDataColumnIds.add(columnId);
	}
}
