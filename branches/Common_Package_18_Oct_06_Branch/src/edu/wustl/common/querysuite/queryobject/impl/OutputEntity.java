/**
 * 
 */

package edu.wustl.common.querysuite.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.util.global.Constants;

/**
 * @author prafull_kadam
 * imeplementation class for IOutputEntity.
 */
public class OutputEntity extends QueryEntity implements IOutputEntity
{

	private static final long serialVersionUID = -823732241107299703L;
	List<AttributeInterface> selectedAttributes = new ArrayList<AttributeInterface>();

	/**
	 * @return List of Dynamic Extenstion attributes that will be shown in the Output tree.
	 * @see edu.wustl.common.querysuite.queryobject.IOutputEntity#getSelectedAttributes()
	 */
	public List<AttributeInterface> getSelectedAttributes()
	{
		return selectedAttributes;
	}

	/**
	 * @param selectedAttributesIndices the attribute list to be set.
	 * @see edu.wustl.common.querysuite.queryobject.IOutputEntity#setSelectedAttributes(java.util.List)
	 */
	public void setSelectedAttributes(List<AttributeInterface> selectedAttributesIndices)
	{
		this.selectedAttributes = selectedAttributesIndices;
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
			OutputEntity theObj = (OutputEntity) obj;
			return selectedAttributes.equals(theObj.selectedAttributes) && super.equals(obj);
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
		hash = hash * Constants.HASH_PRIME + 1;
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
		return "[OutputEntity: " + super.toString() + "]";
	}

}
