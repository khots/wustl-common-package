
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
import edu.wustl.common.util.global.Constants;

public class Class extends FunctionalClass implements IClass
{

	private static final long serialVersionUID = 4139633557474223876L;
	private String fullyQualifiedName;
	private boolean visible;
	
	/**
	 * 
	 * @param fullQualifiedName
	 * @param attributes
	 * @param category
	 * @param isVisible
	 */
	public Class(String fullQualifiedName, List<IAttribute> attributes, ICategory category,
			boolean isVisible)
	{
		this.fullyQualifiedName = fullQualifiedName;
		this.visible = isVisible;
		if (attributes != null)
			this.attributes= attributes;
		this.category  = category;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IClass#getFullyQualifiedName()
	 */
	public String getFullyQualifiedName()
	{
		return fullyQualifiedName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IClass#isVisible()
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IClass#setFullyQualifiedName(java.lang.String)
	 */
	public void setFullyQualifiedName(String fullyQualifiedName)
	{
		this.fullyQualifiedName = fullyQualifiedName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IClass#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (fullyQualifiedName!=null)
			hash = hash*Constants.HASH_PRIME + fullyQualifiedName.hashCode();
		return hash;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this==obj)
			return true;
		
		if (obj != null && this.getClass()==obj.getClass())
		{
			Class theClass = (Class) obj;
			if (fullyQualifiedName != null && fullyQualifiedName.equals(theClass.fullyQualifiedName))
				return super.equals(theClass);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + fullyQualifiedName+":" + attributes + "]";
	}
}
