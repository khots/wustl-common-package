/**
 * 
 */

package edu.wustl.common.querysuite.metadata.associations.impl;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.util.global.Constants;

/**
 * @author prafull_kadam
 * Implementation class for IntraModelAssociation.
 */
public class IntraModelAssociation extends Association implements IIntraModelAssociation
{

	private static final long serialVersionUID = 8723649763990091864L;

	private AssociationInterface associationInterface;

	/**
	 * 
	 * @param associationInterface The reference to the dynamic Extension associated with this object.
	 */
	public IntraModelAssociation(AssociationInterface associationInterface)
	{
		this.associationInterface = associationInterface;
	}

	/**
	 * @return the reference to the Dynamic Extension Attribute.
	 * @see edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation#getDynamicExtensionsAssociation()
	 */
	public AssociationInterface getDynamicExtensionsAssociation()
	{
		return associationInterface;
	}

	/**
	 * @param obj the object to be compared.
	 * @return true if the associationInterface of both the objects are same.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj != null && this.getClass() == obj.getClass())
		{
			IntraModelAssociation association = (IntraModelAssociation) obj;
			if (this.associationInterface != null
					&& this.associationInterface.equals(association.associationInterface))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on associationInterface.
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (associationInterface != null)
		{
			hash = hash * Constants.HASH_PRIME + associationInterface.hashCode();
		}
		return hash;
	}

	/**
	 * @return String representation of this object in the form: [associationInterface]
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return associationInterface + "";
	}

}
