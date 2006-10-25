
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 14.42.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.category.ICategory;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;

public class FunctionalClass implements IFunctionalClass
{

	private List<IAttribute> attributes = new ArrayList<IAttribute>();
	private ICategory category;

	public FunctionalClass(List<IAttribute> attributes, ICategory category)
	{
		this.category = category;
		if (attributes != null)
			this.attributes = attributes;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IFunctionalClass#addAttribute()
	 */
	public IAttribute addAttribute()
	{
		IAttribute iAttribute = QueryObjectFactory.createAttribute();
		attributes.add(iAttribute);
		return iAttribute;
	}

	/**
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

}
