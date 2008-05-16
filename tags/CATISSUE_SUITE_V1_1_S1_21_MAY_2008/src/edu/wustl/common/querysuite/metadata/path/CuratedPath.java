
package edu.wustl.common.querysuite.metadata.path;

import java.util.HashSet;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.util.global.Constants;

/**
 * @author Chandrakant Talele
 * @version 1.0
 * @created 20-Mar-2008 2:08:13 PM
 * @hibernate.class table="CURATED_PATH"
 * @hibernate.cache usage="read-write"
 */
public class CuratedPath implements ICuratedPath
{

	private static final long serialVersionUID = -4723486424420197283L;
	private long curatedPathId;
	private Set<IPath> paths;
	private Set<EntityInterface> entitySet;
	private boolean selected;
	private String entityIds;

	/**
	 * @param curatedPathId
	 * @param paths
	 * @param entitySet
	 * @param isSelected
	 */
	public CuratedPath(long curatedPathId, Set<EntityInterface> entitySet, boolean selected)
	{
		this.curatedPathId = curatedPathId;
		this.selected = selected;
		if (entitySet == null)
		{
			this.entitySet = new HashSet<EntityInterface>();
		}
		this.entitySet = entitySet;
		this.paths = new HashSet<IPath>();
	}

	/**
	 * Default constructor for used by hibernate
	 */
	public CuratedPath()
	{

	}

	public void addPath(IPath path)
	{
		paths.add(path);
	}

	/**
	 * @return Returns the curated_path_Id.
	 * 
	 * @hibernate.id name="curatedPathId" column="CURATED_PATH_ID" type="long" length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CURATED_PATH_SEQ"
	 */
	public long getCuratedPathId()
	{
		return curatedPathId;
	}

	/**
	 * @return the entityIds
	 *
	 * @hibernate.property name="entityIds" column="ENTITY_IDS" update="true"  insert="true" length="30"
	 * 
	 */

	public String getEntityIds()
	{
		return entityIds;
	}

	/**
	 * @param entityIds the entityIds to set
	 */
	public void setEntityIds(String entityIds)
	{
		this.entityIds = entityIds;
	}

	public Set<EntityInterface> getEntitySet()
	{
		return entitySet;
	}

	/**
	 * @return Returns the isSelected.
	 * @hibernate.property name="selected" column="SELECTED" type="boolean" unsaved-value="false"  update="true"  insert="true"
	 * 
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * @hibernate.set name="paths" cascade="none" lazy="false" inverse="false" table="CURATED_PATH_TO_PATH"
	 * @hibernate.collection-key column="CURATED_PATH_ID" 
	 * @hibernate.collection-many-to-many class="edu.wustl.common.querysuite.metadata.path.Path" column="PATH_ID"
	 * @hibernate.cache usage="read-write"
	 */
	public Set<IPath> getPaths()
	{
		return paths;
	}

	/**
	 * @param curatedPathId The curatedPathId to set.
	 */
	public void setCuratedPathId(long curatedPathId)
	{
		this.curatedPathId = curatedPathId;
	}

	/**
	 * @param entitySet The entitySet to set.
	 */
	public void setEntitySet(Set<EntityInterface> entitySet)
	{
		this.entitySet = entitySet;
	}

	/**
	 * @param isSelected The isSelected to set.
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	/**
	 * @param paths The paths to set.
	 */
	public void setPaths(Set<IPath> paths)
	{
		this.paths = paths;
	}

	@Override
	public int hashCode()
	{
		int hash = 1;
		if (!paths.isEmpty() && !entitySet.isEmpty())
		{
			hash = hash * Constants.HASH_PRIME;
			for (IPath path : paths)
			{
				hash += path.hashCode();
			}
			for (EntityInterface entity : entitySet)
			{
				hash += entity.hashCode();
			}
		}

		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean equals = false;
		if (this == obj)
		{
			equals = true;
		}

		if (!equals && obj instanceof CuratedPath)
		{
			CuratedPath curatedPath = (CuratedPath) obj;
			if (!paths.isEmpty() && paths.equals(curatedPath.getPaths()) && !entitySet.isEmpty()
					&& entitySet.equals(curatedPath.getEntitySet()))
			{
				equals = true;
			}
		}
		return equals;
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		for (IPath path : paths)
		{
			string.append(path.getPathId() + " ");
		}
		for (EntityInterface entity : entitySet)
		{
			string.append(entity.getId() + " ");
		}
		return "[" + string.toString() + "]";
	}

}
