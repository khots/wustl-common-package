
package edu.wustl.common.querysuite.metadata.category;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.common.dynamicextensions.domain.Entity;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 * @hibernate.class table="CATEGORY"
 */
public class Category
{

    private Long id;
    private Entity deEntity;
    private CategorialClass rootClass;
    private Collection<Category> subCategories = new HashSet<Category>();
    private Category parentCategory;

    /**
     * @hibernate.many-to-one column="DE_ENTITY_ID" class="edu.common.dynamicextensions.domain.Entity"
     * constrained="true"
     * @return Returns the deEntity.
     */
    public Entity getDeEntity()
    {
        return deEntity;
    }

    /**
     * @param deEntity The deEntity to set.
     */
    public void setDeEntity(Entity deEntity)
    {
        this.deEntity = deEntity;
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
     * cascade="none" inverse="false" lazy="false"
     * @hibernate.collection-key column="PARENT_CATEGORY_ID"
     * @hibernate.collection-one-to-many class="edu.wustl.common.querysuite.metadata.category.Category"
     */
    public Collection<Category> getSubCategories()
    {
        return subCategories;
    }

    public Category clone()
    {
        // TODO by Chandu
        return null;
    }

}