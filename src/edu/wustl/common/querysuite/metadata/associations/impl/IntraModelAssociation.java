/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.metadata.associations.impl;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.util.global.Constants.AssociationDirection;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.util.global.Constants;

/**
 * @author Chandrakant Talele
 * @version 1.0
 * @created 25-Dec-2006 1:22:01 PM
 */
public class IntraModelAssociation implements IIntraModelAssociation
{

	private static final long serialVersionUID = 1477526671383920408L;

	private AssociationInterface dynamicExtensionsAssociation;

	/**
	 * The only public constructor.
	 * @param dynamicExtensionsAssociation
	 */
	public IntraModelAssociation(AssociationInterface dynamicExtensionsAssociation)
	{
		this.dynamicExtensionsAssociation = dynamicExtensionsAssociation;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#getSourceEntity()
	 */
	public EntityInterface getSourceEntity()
	{
		return dynamicExtensionsAssociation.getEntity();
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#getTargetEntity()
	 */
	public EntityInterface getTargetEntity()
	{
		return dynamicExtensionsAssociation.getTargetEntity();
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#isBidirectional()
	 */
	public boolean isBidirectional()
	{
		return (dynamicExtensionsAssociation.getAssociationDirection() == AssociationDirection.BI_DIRECTIONAL);
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation#getDynamicExtensionsAssociation()
	 */
	public AssociationInterface getDynamicExtensionsAssociation()
	{
		return dynamicExtensionsAssociation;
	}

	/**
	 * @return intramodel association wrapping de association that is reverse of this association.
	 * @throws java.lang.UnsupportedOperationException if this association is not bidirectional.
	 * @author Srinath K.
	 */
	public IntraModelAssociation reverse()
	{
		if (!isBidirectional())
		{
			throw new UnsupportedOperationException(
					"Association ain't bidirectional... cannot reverse.");
		}
		EntityInterface origTgtEnt = getDynamicExtensionsAssociation().getTargetEntity();

		Collection<AssociationInterface> associationsOfOrigTgtEnt = origTgtEnt
				.getAssociationCollection();

		for (AssociationInterface associationOfOrigTgtEnt : associationsOfOrigTgtEnt)
		{
			if (associationOfOrigTgtEnt.getSourceRole().getName().equals(
					getDynamicExtensionsAssociation().getTargetRole().getName())
					&& associationOfOrigTgtEnt.getTargetRole().getName().equals(
							getDynamicExtensionsAssociation().getSourceRole().getName()))
			{
				// gotcha!!!
				return new IntraModelAssociation(associationOfOrigTgtEnt);
			}
		}
		throw new RuntimeException("Some bigger evil is at work here... probably DE");
	}

	@Override
	public String toString()
	{
		if (dynamicExtensionsAssociation != null)
			return "DE Association Id:" + dynamicExtensionsAssociation.getId();
		return "";
	}

	/**
	 * To check equality of the two object. it will check equality based on dynamicExtensionsAssociation.
	 * @param obj to be check for equality.
	 * @return true if objects are equals.
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
			if (this.dynamicExtensionsAssociation != null
					&& this.dynamicExtensionsAssociation
							.equals(association.dynamicExtensionsAssociation))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on dynamicExtensionsAssociation.
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (dynamicExtensionsAssociation != null)
		{
			hash = hash * Constants.HASH_PRIME + dynamicExtensionsAssociation.hashCode();
		}
		return hash;
	}

}