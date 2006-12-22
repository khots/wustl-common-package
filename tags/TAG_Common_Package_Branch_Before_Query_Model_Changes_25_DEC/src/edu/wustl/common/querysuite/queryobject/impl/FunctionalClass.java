
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 14.42.04 AM
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.wustl.common.querysuite.category.ICategory;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;
import edu.wustl.common.util.global.Constants;

public class FunctionalClass implements IFunctionalClass
{

	private static final long serialVersionUID = 2251317784460798583L;
	protected List<IAttribute> attributes = new ArrayList<IAttribute>();
	protected ICategory category;

	public FunctionalClass(List<IAttribute> attributes, ICategory category)
	{
		if (attributes != null)
			this.attributes = attributes;
		this.category = category;
	}

	public FunctionalClass()
	{

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
	public boolean addAttribute(IAttribute attribute)
	{
		return (attributes.add(attribute));
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[FunctionalClass:" + attributes.size() + "]";
		//		return "[" + category + ":" + attributes + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (category != null)
			hash = hash * Constants.HASH_PRIME + category.hashCode();

		hash = hash * Constants.HASH_PRIME + new HashSet<IAttribute>(attributes).hashCode();

		return hash;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj != null && this.getClass() == obj.getClass())
		{
			FunctionalClass theClass = (FunctionalClass) obj;
			if (category == null ? theClass.category == null : category.equals(theClass.category))
				//					&& new HashSet<IAttribute>(attributes).equals(new HashSet<IAttribute>(
				//							theClass.attributes)))
				return true;
		}
		return false;
	}

}