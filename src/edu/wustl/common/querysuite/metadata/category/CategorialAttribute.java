/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.metadata.category;

import java.io.Serializable;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 * @hibernate.class table="CATEGORIAL_ATTRIBUTE"
 */
public class CategorialAttribute implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3284285745433302355L;
	private Long id;
	private Long deCategoryAttributeId;
	private Long deSourceClassAttributeId;
	private CategorialClass categorialClass;
    private AttributeInterface categoryAttribute;
    private AttributeInterface sourceClassAttribute;
	/**
	 * @return Returns the categorialClass.
	 * @hibernate.many-to-one column="CATEGORIAL_CLASS_ID"
	 * class="edu.wustl.common.querysuite.metadata.category.CategorialClass" constrained="true"
	 */
	public CategorialClass getCategorialClass()
	{
		return categorialClass;
	}

	/**
	 * @param categorialClass The categorialClass to set.
	 */
	public void setCategorialClass(CategorialClass categorialEntity)
	{
		this.categorialClass = categorialEntity;
	}

	/**
	 * @return Returns the deCategoryAttribute.
	 * @hibernate.property name="deCategoryAttributeId" type="long" column="DE_CATEGORY_ATTRIBUTE_ID"
	 */
	public Long getDeCategoryAttributeId()
	{
		return deCategoryAttributeId;
	}

	/**
	 * @param deCategoryAttributeId The deCategoryAttribute to set.
	 */
	public void setDeCategoryAttributeId(Long deCategoryAttributeId)
	{
		this.deCategoryAttributeId = deCategoryAttributeId;
	}

	/**
	 * @return Returns the deSourceClassAttribute.
	 * @hibernate.property name="deSourceClassAttributeId" type="long" column="DE_SOURCE_CLASS_ATTRIBUTE_ID"
	 */
	public Long getDeSourceClassAttributeId()
	{
		return deSourceClassAttributeId;
	}

	/**
	 * @param deSourceClassAttributeId The deSourceClassAttribute to set.
	 */
	public void setDeSourceClassAttributeId(Long deSourceClassAttributeId)
	{
		this.deSourceClassAttributeId = deSourceClassAttributeId;
	}

	/**
	 * @return Returns the id.
	 * @hibernate.id name="id" column="ID" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATEGORIAL_ATTRIBUTE_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Default Constructor.
	 * Required for hibernate.
	 */
	public CategorialAttribute()
	{

	}

    /**
     * @return Returns the categoryAttribute.
     */
    public AttributeInterface getCategoryAttribute() {
        return categoryAttribute;
    }

    /**
     * @return Returns the sourceClassAttribute.
     */
    public AttributeInterface getSourceClassAttribute() {
        return sourceClassAttribute;
    }

    /**
     * @param categoryAttribute The categoryAttribute to set.
     */
    public void setCategoryAttribute(AttributeInterface categoryAttribute) {
        this.categoryAttribute = categoryAttribute;
    }

    /**
     * @param sourceClassAttribute The sourceClassAttribute to set.
     */
    public void setSourceClassAttribute(AttributeInterface sourceClassAttribute) {
        this.sourceClassAttribute = sourceClassAttribute;
    }

}