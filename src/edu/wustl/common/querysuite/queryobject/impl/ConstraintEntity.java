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
import edu.wustl.common.querysuite.queryobject.IConstraintEntity;
import edu.wustl.common.util.global.Constants;

/**
 * @author prafull_kadam
 * Class implementation for IConstraintEntity.  
 */
public class ConstraintEntity extends QueryEntity implements IConstraintEntity
{

	private static final long serialVersionUID = 3442788139972273766L;

	/**
	 * To instanciate object of ConstraintEntity.
	 * @param entityInterface The Dynamic Extension entity reference associated with this object. 
	 */
	public ConstraintEntity(EntityInterface entityInterface)
	{
		super(entityInterface);
	}

	/**
	 * To check whether two objects are equal.
	 * @param obj reference to the object to be checked for equality.
	 * @return true if centityInterface of object is equal.
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
			return super.equals(obj);
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
		hash = hash * Constants.HASH_PRIME;
		hash = hash * Constants.HASH_PRIME + super.hashCode();
		return hash;
	}

	/**
	 * @return String representation of this object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[ConstrainedEntity: " + super.toString() + "]";
	}

}
