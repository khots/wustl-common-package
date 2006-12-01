
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.util.global.Constants;

public abstract class AbstractAssociation implements IAssociation
{

	private IClass sourceClass;

	private IClass targetClass;

	private boolean bidirectional;

	public AbstractAssociation(IClass sourceClass, IClass targetClass, boolean bidirectional)
	{
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.bidirectional = bidirectional;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#isBidirectional()
	 */
	public boolean isBidirectional()
	{
		return bidirectional;
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

	public abstract boolean isIntraModel();

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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * Constants.HASH_PRIME + (bidirectional ? 1 : 0);
		if (sourceClass != null)
			hash = hash * Constants.HASH_PRIME + sourceClass.hashCode();
		if (targetClass != null)
			hash = hash * Constants.HASH_PRIME + targetClass.hashCode();

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
			AbstractAssociation association = (AbstractAssociation) obj;
			if (bidirectional == association.bidirectional
					&& (this.sourceClass == null
							? association.getSourceClass() == null
							: sourceClass.equals(association.getSourceClass()))
					&& (this.targetClass == null
							? association.getTargetClass() == null
							: targetClass.equals(association.getTargetClass())))
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
		return "[" + getSourceClass() + ":" + getTargetClass() + "]";
	}

}
