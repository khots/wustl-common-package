/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * 
 */

package edu.wustl.common.querysuite.queryobject.impl;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.util.global.Constants;

/**
 * @author prafull_kadam
 *
 */
abstract class QueryEntity implements IQueryEntity
{

	protected EntityInterface entityInterface;

	/**
	 * TO initialize entityInterface object for this object.
	 * @param entityInterface The Dynamic Extension entity reference associated with this object.
	 */
	protected QueryEntity(EntityInterface entityInterface)
	{
		this.entityInterface = entityInterface;	
	}
	/**
	 * To get the Dynamic Extension Entity reference.
	 * @return The Dynamic Extension Entity reference corresponding to the QueryEntity.
	 * @see edu.wustl.common.querysuite.queryobject.IQueryEntity#getDynamicExtensionsEntity()
	 */
	public EntityInterface getDynamicExtensionsEntity()
	{
		return entityInterface;
	}

	/**
	 * To check whether two objects are equal.
	 * @param obj reference to the object to be checked for equality.
	 * @return true if entityInterface of object is equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj != null && this.getClass() == obj.getClass())
		{
			QueryEntity theObj = (QueryEntity)obj;
			if (entityInterface != null && entityInterface.equals(theObj.entityInterface))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on entityInterface.
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * Constants.HASH_PRIME + entityInterface.hashCode();
		return hash;
	}

	/**
	 * @return String representation of this object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return entityInterface.getName();
	}

}
