
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.IIntraModelAssociation;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 14.48.04 AM
 */

public class IntraModelAssociation implements IIntraModelAssociation
{

	private static final long serialVersionUID = -9145063667034626269L;
	private String sourceRoleName;
	private String targetRoleName;
	private boolean bidirectional;
	private IClass sourceClass;
	private IClass targetClass;

	public IntraModelAssociation(IClass leftClass, IClass rightClass, String roleName,
			String revereseRoleName, boolean bidirectional)
	{
		this.sourceClass = leftClass;
		this.targetClass = rightClass;
		this.sourceRoleName = roleName;
		this.targetRoleName = revereseRoleName;
		this.bidirectional = bidirectional;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#getSourceClass()
	 */
	public IClass getSourceClass()
	{
		return sourceClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#getTargetClass()
	 */
	public IClass getTargetClass()
	{
		return targetClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#isBidirectional()
	 */
	public boolean isBidirectional()
	{
		return bidirectional;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#isIntraModel()
	 */
	public boolean isIntraModel()
	{
		return true;
	}

	/**
	 * @param bidirectional
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setBidirectional(boolean)
	 */
	public void setBidirectional(boolean bidirectional)
	{
		this.bidirectional = bidirectional;
	}

	/**
	 * @param leftClass
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setSourceClass(edu.wustl.common.querysuite.queryobject.IClass)
	 */
	public void setSourceClass(IClass leftClass)
	{
		this.sourceClass = leftClass;
	}

	/**
	 * @param rightClass
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setTargetClass(edu.wustl.common.querysuite.queryobject.IClass)
	 */
	public void setTargetClass(IClass rightClass)
	{
		this.targetClass = rightClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#getSourceRoleName()
	 */
	public String getSourceRoleName()
	{
		return sourceRoleName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#getTargetRoleName()
	 */
	public String getTargetRoleName()
	{
		return targetRoleName;
	}

	/**
	 * @param roleName
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#setSourceRoleName(java.lang.String)
	 */
	public void setSourceRoleName(String roleName)
	{
		sourceRoleName = roleName;
	}

	/**
	 * @param roleName
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#setTargetRoleName(java.lang.String)
	 */
	public void setTargetRoleName(String roleName)
	{
		this.targetRoleName = roleName;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (sourceRoleName != null)
			hash = hash*Constants.HASH_PRIME + sourceRoleName.hashCode();
		if (targetRoleName != null)
			hash = hash*Constants.HASH_PRIME + targetRoleName.hashCode();
		hash = hash*Constants.HASH_PRIME + (bidirectional ? 1 : 0);
		if (sourceClass != null)
			hash = hash*Constants.HASH_PRIME + sourceClass.hashCode();
		if (targetClass != null)
			hash = hash*Constants.HASH_PRIME + targetClass.hashCode();

		return hash;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj != null && this.getClass() == obj.getClass())
		{
			IntraModelAssociation association = (IntraModelAssociation) obj;
			if (this.sourceRoleName != null && sourceRoleName.equals(association.sourceRoleName)
					&& this.targetRoleName != null
					&& targetRoleName.equals(association.targetRoleName)
					&& bidirectional == association.bidirectional
					&& this.sourceClass != null
					&& sourceClass.equals(association.sourceClass)
					&& this.targetClass != null
					&& targetClass.equals(association.targetClass))
				return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + sourceRoleName + ":" + targetRoleName + "]";
	}

}