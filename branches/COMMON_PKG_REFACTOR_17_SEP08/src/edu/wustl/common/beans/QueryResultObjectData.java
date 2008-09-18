/*
 * Created on Nov 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * @author aarti_sharma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QueryResultObjectData {
	private String aliasName;
	private int identifierColumnId;
	private List dependentColumnIds = new ArrayList();
	private List dependentObjectAliases = new ArrayList();
	private List relatedQueryResultObjects = new ArrayList();
	private List IdentifiedDataColumnIds = new ArrayList();
	
	
	public QueryResultObjectData()
	{
		super();
	}
	
	public QueryResultObjectData(String aliasName)
	{
		this.aliasName = aliasName;
	}
	
	/**
	 * @return Returns the aliasName.
	 */
	public String getAliasName() {
		return aliasName;
	}
	/**
	 * @param aliasName The aliasName to set.
	 */
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	/**
	 * @return Returns the dependentObjectAliases.
	 */
	public List getDependentObjectAliases() {
		return dependentObjectAliases;
	}
	/**
	 * @param dependentObjectAliases The dependentObjectAliases to set.
	 */
	public void setDependentObjectAliases(List dependentObjectAliases) {
		this.dependentObjectAliases = dependentObjectAliases;
	}
	/**
	 * @return Returns the identifierColumnId.
	 */
	public int getIdentifierColumnId() {
		return identifierColumnId;
	}
	/**
	 * @param identifierColumnId The identifierColumnId to set.
	 */
	public void setIdentifierColumnId(int identifierColumnId) {
		this.identifierColumnId = identifierColumnId;
	}
	/**
	 * @return Returns the relatedColumnIds.
	 */
	public List getDependentColumnIds() {
		return dependentColumnIds;
	}
	/**
	 * @param relatedColumnIds The relatedColumnIds to set.
	 */
	public void setDependentColumnIds(List dependentColumnIds) {
		this.dependentColumnIds = dependentColumnIds;
	}
	
	
	public void addRelatedQueryResultObject(QueryResultObjectData queryResultObjectData)
	{
		this.relatedQueryResultObjects.add(queryResultObjectData);
	}
	
	public int getNumberOfIndependentObjects()
	{
		return relatedQueryResultObjects.size()+1;
	}
	
	public List getIndependentObjectAliases()
	{
		List independentObjectAliases = new ArrayList();
		independentObjectAliases.add(this.aliasName);
		for(int i=0; i<relatedQueryResultObjects.size(); i++)
		{
			independentObjectAliases.add(((QueryResultObjectData)relatedQueryResultObjects.get(i)).getAliasName());
		}
		return independentObjectAliases;
	}
	
	public List getIndependentQueryObjects()
	{
		List independentQueryObjects = new ArrayList();
		independentQueryObjects.add(this);
		independentQueryObjects.addAll(this.relatedQueryResultObjects);
		return independentQueryObjects;
	}
	/**
	 * @return Returns the relatedQueryResultObjects.
	 */
	public List getRelatedQueryResultObjects() {
		return relatedQueryResultObjects;
	}
	/**
	 * @param relatedQueryResultObjects The relatedQueryResultObjects to set.
	 */
	public void setRelatedQueryResultObjects(List relatedQueryResultObjects) {
		this.relatedQueryResultObjects = relatedQueryResultObjects;
	}
	/**
	 * @return Returns the identifiedDataColumnIds.
	 */
	public List getIdentifiedDataColumnIds() {
		return IdentifiedDataColumnIds;
	}
	/**
	 * @param identifiedDataColumnIds The identifiedDataColumnIds to set.
	 */
	public void setIdentifiedDataColumnIds(List identifiedDataColumnIds) {
		IdentifiedDataColumnIds = identifiedDataColumnIds;
	}
	
	public void addIdentifiedDataColumnId(Integer columnId)
	{
		this.IdentifiedDataColumnIds.add(columnId);
	}
}
