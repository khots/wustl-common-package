
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.IIntraModelAssociation;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 14.48.04 AM
 */

public class IntraModelAssociation extends AbstractAssociation implements IIntraModelAssociation
{

	private static final long serialVersionUID = -9145063667034626269L;
	private String sourceRoleName;
	private String targetRoleName;

	public IntraModelAssociation(IClass leftClass, IClass rightClass, String roleName,
			String revereseRoleName, boolean bidirectional)
	{
		super(leftClass, rightClass, bidirectional);
		this.sourceRoleName = roleName;
		this.targetRoleName = revereseRoleName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#isIntraModel()
	 */
	public boolean isIntraModel()
	{
		return true;
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
			hash = hash * Constants.HASH_PRIME + sourceRoleName.hashCode();
		if (targetRoleName != null)
			hash = hash * Constants.HASH_PRIME + targetRoleName.hashCode();
		hash = hash * Constants.HASH_PRIME + super.hashCode();

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
			if (!super.equals(obj))
			{
				return false;
			}
			IntraModelAssociation association = (IntraModelAssociation) obj;
			if (this.sourceRoleName != null && sourceRoleName.equals(association.sourceRoleName)
					&& this.targetRoleName != null
					&& targetRoleName.equals(association.targetRoleName)
			//					&& bidirectional == association.bidirectional
			//					&& this.sourceClass != null
			//					&& sourceClass.equals(association.sourceClass)
			//					&& this.targetClass != null
			//					&& targetClass.equals(association.targetClass)
			)
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