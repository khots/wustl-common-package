
package edu.wustl.common.querysuite.queryobject;

import edu.wustl.common.querysuite.category.ICategory;

import java.util.List;

/**
 * a set of attributes that may belong to different classes.
 * @version 1.0
 * @updated 11-Oct-2006 02:56:22 PM
 */
public interface IFunctionalClass
{

	public List<IAttribute> getAttributes();

	/**
	 * @param attributes
	 * 
	 */
	public void setAttributes(List<IAttribute> attributes);

	public IAttribute addAttribute();

	/**
	 * @param attribute
	 * 
	 */
	public void addAttribute(IAttribute attribute);

	/**
	 * can be null if the class does not belong to a category
	 */
	public ICategory getCategory();

	/**
	 * @param category
	 * 
	 */
	public void setCategory(ICategory category);

}
