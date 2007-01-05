
package edu.wustl.common.querysuite.metadata.category;

import java.util.HashSet;
import java.util.Set;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 * @hibernate.class table="CATEGORIAL_CLASS"
 */
public class CategorialClass
{

	private Long id;
	private Long deEntityId;
	private Category category;
	private Set<CategorialAttribute> categorialAttributeCollection;
	private Set<CategorialClass> children;
	private CategorialClass parent;
	private Long pathFromParentId;

	/**
	 * @return Returns the id.
	 * @hibernate.id name="id" column="ID" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATEGORIAL_CLASS_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @return Returns the category.
	 * @hibernate.many-to-one column="CATEGORY_ID" class="edu.wustl.common.querysuite.metadata.category.Category"
	 * constrained="true"
	 */
	public Category getCategory()
	{
		return category;
	}

	/**
	 * @param category The category to set.
	 */
	public void setCategory(Category category)
	{
		this.category = category;
	}

	/**
	 * @return Returns the deEntity.
	 * @hibernate.property name="deEntityId" type="long" column="DE_ENTITY_ID"
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
	 * @param id The id to set.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @param categorialAttributeCollection The categorialAttributeCollection to set.
	 */
	public void setCategorialAttributeCollection(
			Set<CategorialAttribute> categorialAttributeCollection)
	{
		this.categorialAttributeCollection = categorialAttributeCollection;
	}

	/**
	 * @param children The children to set.
	 */
	public void setChildren(Set<CategorialClass> children)
	{
		this.children = children;
	}

	public CategorialClass()
	{

	}

	/**
	 * @return
	 * @hibernate.set name="categorialAttributeCollection" table="CATEGORIAL_ATTRIBUTE"
	 * cascade="none" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORIAL_CLASS_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.common.querysuite.metadata.category.CategorialAttribute"
	 */
	public Set<CategorialAttribute> getCategorialAttributeCollection()
	{
		return categorialAttributeCollection;
	}

	/**
	 * @return
	 * @hibernate.set name="children" table="CATEGORIAL_CLASS"
	 * cascade="none" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PARENT_CATEGORIAL_CLASS_ID"
	 * @hibernate.collection-one-to-many class="edu.wustl.common.querysuite.metadata.category.CategorialClass"
	 */
	public Set<CategorialClass> getChildren()
	{
		return children;
	}

	/**
	 * @return
	 * @hibernate.property name="pathFromParentId" type="long" column="PATH_FROM_PARENT_ID"
	 */
	public Long getPathFromParentId()
	{
		return pathFromParentId;
	}

	/**
	 * @hibernate.many-to-one column="PARENT_CATEGORIAL_CLASS_ID" 
	 * class="edu.wustl.common.querysuite.metadata.category.CategorialClass" constrained="true"
	 */
	public CategorialClass getParent()
	{
		return parent;
	}

	public Long findSourceAttributeId(Long catAttrId)
	{
		for (CategorialAttribute categorialAttribute : getCategorialAttributeCollection())
		{
			if (catAttrId.equals(categorialAttribute.getDeCategoryAttributeId()))
			{
				return categorialAttribute.getDeSourceClassAttributeId();
			}
		}
		return null;
	}

	public void addChildCategorialClass(CategorialClass child, Long pathToChildId)
	{
		children.add(child);
		child.setParent(this);
		child.setPathFromParentId(pathToChildId);
	}

	public void setParent(CategorialClass parent)
	{
		this.parent = parent;
	}

	public void setPathFromParentId(Long pathFromParentId)
	{
		this.pathFromParentId = pathFromParentId;
	}
	
	public void addCategorialAttribute(CategorialAttribute categorialAttribute) {
		if(categorialAttributeCollection == null) {
			categorialAttributeCollection = new HashSet<CategorialAttribute>();
		}
		categorialAttributeCollection.add(categorialAttribute);
		categorialAttribute.setCategorialClass(this);
	}
}