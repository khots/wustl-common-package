
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 11.12.04 AM
 */

import java.util.List;

import edu.wustl.common.querysuite.category.ICategory;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IClass;

public class Class implements IClass
{

	private String fullQualifiedName;
	private boolean visible;
	private List<IAttribute> attributes;
	private ICategory category;

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IClass#getFullyQualifiedName()
	 */
	public String getFullyQualifiedName()
	{
		return fullQualifiedName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IClass#isVisible()
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * @param fullyQualifiedName
	 * @see edu.wustl.common.querysuite.queryobject.IClass#setFullyQualifiedName(java.lang.String)
	 */
	public void setFullyQualifiedName(String fullyQualifiedName)
	{
		fullQualifiedName = fullyQualifiedName;
	}

	/**
	 * @param visible
	 * @see edu.wustl.common.querysuite.queryobject.IClass#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IFunctionalClass#addAttribute()
	 */
	public IAttribute addAttribute()
	{
		IAttribute iAttribute = new Attribute();
		attributes.add(iAttribute);
		return iAttribute;
	}

	/**
	 * @param attribute
	 * @see edu.wustl.common.querysuite.queryobject.IFunctionalClass#addAttribute(edu.wustl.common.querysuite.queryobject.IAttribute)
	 */
	public void addAttribute(IAttribute attribute)
	{
		attributes.add(attribute);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IFunctionalClass#getAttributes()
	 */
	public List<IAttribute> getAttributes()
	{
		return attributes;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IFunctionalClass#getCategory()
	 */
	public ICategory getCategory()
	{
		return category;
	}

	/**
	 * @param attributes
	 * @see edu.wustl.common.querysuite.queryobject.IFunctionalClass#setAttributes(java.util.List)
	 */
	public void setAttributes(List<IAttribute> attributes)
	{
		this.attributes = attributes;
	}

	/**
	 * @param category
	 * @see edu.wustl.common.querysuite.queryobject.IFunctionalClass#setCategory(edu.wustl.common.querysuite.category.ICategory)
	 */
	public void setCategory(ICategory category)
	{
		this.category = category;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Class)
		{
			Class theClass = (Class) obj;
			if (fullQualifiedName != null && fullQualifiedName.equals(theClass.fullQualifiedName))
				return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + fullQualifiedName + "]";
	}

}
