/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.metadata.path;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.InterModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.IntraModelAssociation;

/**
 * @author Chandrakant Talele
 */
public class Path implements IPath
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2375912190167946239L;
	private long pathId;
	private EntityInterface sourceEntity;
	private EntityInterface targetEntity;
	private List<IAssociation> intermediateAssociations;

	public Path(EntityInterface sourceEntity, EntityInterface targetEntity,
			List<IAssociation> intermediateAssociations)
	{
		this(-1, sourceEntity, targetEntity, intermediateAssociations);
	}

	/**
	 * @param pathId
	 * @param sourceEntity
	 * @param targetEntity
	 * @param intermediateAssociations
	 */
	public Path(long pathId, EntityInterface sourceEntity, EntityInterface targetEntity,
			List<IAssociation> intermediateAssociations)
	{
		this.pathId = pathId;
		this.sourceEntity = sourceEntity;
		this.targetEntity = targetEntity;
		this.intermediateAssociations = intermediateAssociations;
	}

	public EntityInterface getSourceEntity()
	{
		return sourceEntity;
	}

	public EntityInterface getTargetEntity()
	{
		return targetEntity;
	}

	public List<IAssociation> getIntermediateAssociations()
	{
		return intermediateAssociations;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.path.IPath#isBidirectional()
	 */
	public boolean isBidirectional()
	{
		for (IAssociation association : intermediateAssociations)
		{
			if (!association.isBidirectional())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.path.IPath#reverse()
	 */

	// BEGIN REVERSING
	public IPath reverse()
	{
		List<IAssociation> origAssociations = getIntermediateAssociations();
		List<IAssociation> newAssociations = new ArrayList<IAssociation>(origAssociations.size());
		for (IAssociation association : origAssociations)
		{
			if (!association.isBidirectional())
			{
				throw new IllegalArgumentException("Path ain't bidirectional... cannot reverse.");
			}
			if (association instanceof IInterModelAssociation)
			{
				newAssociations.add(((InterModelAssociation) association).reverse());
			}
			else
			{
				newAssociations.add(((IntraModelAssociation) association).reverse());
			}
		}
		return new Path(getTargetEntity(), getSourceEntity(), newAssociations);
	}

	/**
	 * @param intermediateAssociations The intermediateAssociations to set.
	 */
	public void setIntermediateAssociations(List<IAssociation> intermediateAssociations)
	{
		this.intermediateAssociations = intermediateAssociations;
	}

	/**
	 * @param sourceEntity The sourceEntity to set.
	 */
	public void setSourceEntity(EntityInterface sourceEntity)
	{
		this.sourceEntity = sourceEntity;
	}

	/**
	 * @param targetEntity The targetEntity to set.
	 */
	public void setTargetEntity(EntityInterface targetEntity)
	{
		this.targetEntity = targetEntity;
	}

	@Override
	public String toString()
	{
		StringBuffer buff = new StringBuffer();
		buff.append("Start Entity : " + sourceEntity.getName());
		buff.append("End Entity : " + targetEntity.getName());
		for (IAssociation association : intermediateAssociations)
		{
			buff.append("\t" + association.toString());
		}
		return buff.toString();
	}

	
	/**
	 * @return the pathId.
	 */
	public long getPathId()
	{
		return pathId;
	}

	
	/**
	 * @param pathId the pathId to set.
	 */
	public void setPathId(long pathId)
	{
		this.pathId = pathId;
	}
}
