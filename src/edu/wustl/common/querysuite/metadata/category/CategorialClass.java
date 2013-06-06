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

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.metadata.path.IPath;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 * @hibernate.class table="CATEGORIAL_CLASS"
 */
public class CategorialClass implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -136227480173654340L;
	private Long id;
	private Long deEntityId;
	private Category category;
	private Set<CategorialAttribute> categorialAttributeCollection = new HashSet<CategorialAttribute>();
	private Set<CategorialClass> children = new HashSet<CategorialClass>();
	private CategorialClass parent;
	private Long pathFromParentId;
	private EntityInterface categorialClassEntity;
	private IPath pathFromParent;

	/**
	 * @return the pathFromParent.
	 */
	public IPath getPathFromParent()
	{
		return pathFromParent;
	}

	/**
	 * @param pathFromParent the pathFromParent to set.
	 */
	public void setPathFromParent(IPath pathFromParent)
	{
		this.pathFromParent = pathFromParent;
	}

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

	public AttributeInterface findSourceAttribute(AttributeInterface categoryAttribute)
	{
		for (CategorialAttribute categorialAttribute : getCategorialAttributeCollection())
		{
			if (categoryAttribute.equals(categorialAttribute.getCategoryAttribute()))
			{
				return categorialAttribute.getSourceClassAttribute();
			}
		}
		return null;
	}

	public void addChildCategorialClass(CategorialClass child, IPath pathToChild)
	{
		children.add(child);
		child.setParent(this);
		child.setPathFromParent(pathToChild);
	}

	public void addChildCategorialClass(CategorialClass child)
	{
		children.add(child);
		child.setParent(this);
	}

	// rets true if the child was found; false otherwise.
	public boolean removeChildCategorialClass(CategorialClass child)
	{
		return children.remove(child);
	}

	public void setParent(CategorialClass parent)
	{
		this.parent = parent;
	}

	public void setPathFromParentId(Long pathFromParentId)
	{
		this.pathFromParentId = pathFromParentId;
	}

	public void addCategorialAttribute(CategorialAttribute categorialAttribute)
	{
		if (categorialAttributeCollection == null)
		{
			categorialAttributeCollection = new HashSet<CategorialAttribute>();
		}
		categorialAttributeCollection.add(categorialAttribute);
		categorialAttribute.setCategorialClass(this);
	}

	/**
	 * @return Returns the categorialClassEntity.
	 */
	public EntityInterface getCategorialClassEntity()
	{
		return categorialClassEntity;
	}

	/**
	 * @param categorialClassEntity The categorialClassEntity to set.
	 */
	public void setCategorialClassEntity(EntityInterface categorialClassEntity)
	{
		this.categorialClassEntity = categorialClassEntity;
	}
}