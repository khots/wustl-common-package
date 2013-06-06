/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.metadata.associations.impl;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation;

/**
 * This stores all the inter model connections present between given pair of entities.
 * @see edu.wustl.common.querysuite.metadata.associations.IAssociation
 * @author Chandrakant Talele
 */
public class InterModelAssociation implements IInterModelAssociation
{

	private static final long serialVersionUID = -3230037755747481080L;

	private String sourceServiceUrl;

	private String targetServiceUrl;

	AttributeInterface sourceAttribute;

	AttributeInterface targetAttribute;

	/**
	 * @param sourceAttribute
	 * @param targetAttribute
	 */
	public InterModelAssociation(AttributeInterface sourceAttribute,
			AttributeInterface targetAttribute)
	{
		this.sourceAttribute = sourceAttribute;
		this.targetAttribute = targetAttribute;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#getSourceEntity()
	 */
	public EntityInterface getSourceEntity()
	{
		return sourceAttribute.getEntity();
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation#getSourceAttribute()
	 */
	public AttributeInterface getSourceAttribute()
	{
		return sourceAttribute;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#getTargetEntity()
	 */
	public EntityInterface getTargetEntity()
	{
		return targetAttribute.getEntity();
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation#getTargetAttribute()
	 */
	public AttributeInterface getTargetAttribute()
	{
		return targetAttribute;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#isBidirectional()
	 */
	public boolean isBidirectional()
	{
		return true; //intermodel association is always bidirectional
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation#getSourceServiceUrl()
	 */
	public String getSourceServiceUrl()
	{
		return sourceServiceUrl;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation#getTargetServiceUrl()
	 */
	public String getTargetServiceUrl()
	{
		return targetServiceUrl;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation#setSourceServiceUrl(java.lang.String)
	 */
	public void setSourceServiceUrl(String sourceServiceUrl)
	{
		this.sourceServiceUrl = sourceServiceUrl;
	}

	/**
	 * @see edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation#setTargetServiceUrl(java.lang.String)
	 */
	public void setTargetServiceUrl(String targetServiceUrl)
	{
		this.targetServiceUrl = targetServiceUrl;
	}

	/**
	 * @return association with swapped source and target attributes, urls.
	 * @author Srinath K.
	 */
	public InterModelAssociation reverse()
	{
		InterModelAssociation interModelAssociation = new InterModelAssociation(
				getTargetAttribute(), getSourceAttribute());

		interModelAssociation.setSourceServiceUrl(getTargetServiceUrl());
		interModelAssociation.setTargetServiceUrl(getSourceServiceUrl());
		return interModelAssociation;
	}

	@Override
	public String toString()
	{
		StringBuffer buff = new StringBuffer();
		buff.append("Source Attribute : " + sourceAttribute.getName());
		buff.append("\tSource Entity : " + getSourceEntity().getName());
		buff.append("\tTarget Attribute : " + targetAttribute.getName());
		buff.append("\tTarget Entity : " + getTargetEntity().getName());
		return buff.toString();
	}

	@Override
	public InterModelAssociation clone()
	{
		InterModelAssociation clone = new InterModelAssociation(sourceAttribute, targetAttribute);
		clone.setSourceServiceUrl(sourceServiceUrl);
		clone.setTargetServiceUrl(targetServiceUrl);
		return clone;
	}
}