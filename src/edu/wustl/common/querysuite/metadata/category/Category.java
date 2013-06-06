/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.metadata.category;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityInterface;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 * @hibernate.class table="CATEGORY"
 */
public class Category implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4899537928159969695L;
	private Long id;
	private Long deEntityId;
	private CategorialClass rootClass;
	private Set<Category> subCategories = new HashSet<Category>();
	private Category parentCategory;
    private EntityInterface categoryEntity;
	/**
	 * @hibernate.property name="deEntityId" type="long" column="DE_ENTITY_ID"
	 * @return Returns the deEntity.
	 */
	public Long getDeEntityId()
	{
		return deEntityId;
	}

	/**
	 * @param deEntityId The deEntity to set.
	 */
	public void setDeEntityId(Long deEntityId)
	{
		this.deEntityId = deEntityId;
	}

	/**
	 * @return Returns the id.
	 * @hibernate.id name="id" column="ID" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATEGORY_SEQ"
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
	 * @return Returns the parentCategory.
	 * @hibernate.many-to-one column="PARENT_CATEGORY_ID" class="edu.wustl.common.querysuite.metadata.category.Category"
	 * constrained="true"
	 */
	public Category getParentCategory()
	{
		return parentCategory;
	}

	/**
	 * @param parentCategory The parentCategory to set.
	 */
	public void setParentCategory(Category parentCategory)
	{
		this.parentCategory = parentCategory;
	}

	/**
	 * @param rootClass The rootClass to set.
	 */
	public void setRootClass(CategorialClass rootClass)
	{
		this.rootClass = rootClass;
	}

	/**
	 * @param subCategories The subCategories to set.
	 */
	public void setSubCategories(Set<Category> subCategories)
	{
		this.subCategories = subCategories;
	}

	/**
	 * Default constructor.
	 * Required for hibernate.
	 */
	public Category()
	{

	}

	/**
	 * @return
	 * @hibernate.many-to-one column="ROOT_CATEGORIAL_CLASS_ID" 
	 * class="edu.wustl.common.querysuite.metadata.category.CategorialClass" constrained="true"
	 */
	public CategorialClass getRootClass()
	{
		return rootClass;
	}

	/**
	 * @return
	 * @hibernate.set name="subCategories" table="CATEGORY"
	 * cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PARENT_CATEGORY_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.common.querysuite.metadata.category.Category"
	 */
	public Set<Category> getSubCategories()
	{
		return subCategories;
	}

    /**
     * @return Returns the categoryEntity.
     */
    public EntityInterface getCategoryEntity() {
        return categoryEntity;
    }

    /**
     * @param categoryEntity The categoryEntity to set.
     */
    public void setCategoryEntity(EntityInterface categoryEntity) {
        this.categoryEntity = categoryEntity;
    }
}