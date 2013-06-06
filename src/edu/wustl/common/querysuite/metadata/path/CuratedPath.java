/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.metadata.path;

import java.util.HashSet;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityInterface;

/**
 * @author Chandrakant Talele
 */
public class CuratedPath implements ICuratedPath
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4723486424420197283L;
	private long curatedPathId;
	private Set<IPath> paths;
	private Set<EntityInterface> entitySet;
	private boolean selected;

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

	public void addPath(Path path)
	{
		paths.add(path);
	}

	/**
	 * @return Returns the curatedPathId.
	 */
	public long getCuratedPathId()
	{
		return curatedPathId;
	}

	/**
	 * @return Returns the entitySet.
	 */
	public Set<EntityInterface> getEntitySet()
	{
		return entitySet;
	}

	/**
	 * @return Returns the isSelected.
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * @return Returns the paths.
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

}
