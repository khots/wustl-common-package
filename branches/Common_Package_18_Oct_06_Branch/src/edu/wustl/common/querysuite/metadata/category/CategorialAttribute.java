
package edu.wustl.common.querysuite.metadata.category;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 * @hibernate.class table="CATEGORIAL_ATTRIBUTE"
 */
public class CategorialAttribute
{

    private Long id;
    private AttributeInterface deCategoryAttribute;
    private AttributeInterface deSourceClassAttribute;
    private CategorialClass categorialClass;

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
     * @hibernate.many-to-one column="DE_CATEGORY_ATTRIBUTE_ID"
     * class="edu.common.dynamicextensions.domain.Attribute" constrained="true"
     */
    public AttributeInterface getDeCategoryAttribute()
    {
        return deCategoryAttribute;
    }

    /**
     * @param deCategoryAttribute The deCategoryAttribute to set.
     */
    public void setDeCategoryAttribute(AttributeInterface deCategoryAttribute)
    {
        this.deCategoryAttribute = deCategoryAttribute;
    }

    /**
     * @return Returns the deSourceClassAttribute.
     * @hibernate.many-to-one column="DE_SOURCE_CLASS_ATTRIBUTE_ID"
     * class="edu.common.dynamicextensions.domain.Attribute" constrained="true"
     */
    public AttributeInterface getDeSourceClassAttribute()
    {
        return deSourceClassAttribute;
    }

    /**
     * @param deSourceClassAttribute The deSourceClassAttribute to set.
     */
    public void setDeSourceClassAttribute(
            AttributeInterface deSourceClassAttribute)
    {
        this.deSourceClassAttribute = deSourceClassAttribute;
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

    public void finalize() throws Throwable
    {

    }

    public AttributeInterface getCategoryAttribute()
    {
        return null;
    }

    public AttributeInterface getSourceClassAttribute()
    {
        return null;
    }

}