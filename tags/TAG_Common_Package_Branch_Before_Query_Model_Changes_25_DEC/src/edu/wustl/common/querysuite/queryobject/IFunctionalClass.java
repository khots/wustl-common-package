
package edu.wustl.common.querysuite.queryobject;

import edu.wustl.common.querysuite.category.ICategory;

import java.util.List;

/**
 * Represents a set of attributes that may belong to different classes.
 * 
 * @version 1.0
 * @updated 11-Oct-2006 02:56:22 PM
 */
public interface IFunctionalClass extends IBaseQueryObject
{

	List<IAttribute> getAttributes();

	/**
	 * @param attributes
	 * 
	 */
	void setAttributes(List<IAttribute> attributes);

	IAttribute addAttribute();

	/**
	 * @param attribute
	 * 
	 */
	boolean addAttribute(IAttribute attribute);

	/**
	 * can be null if the class does not belong to a category
	 */
	ICategory getCategory();

	/**
	 * @param category
	 * 
	 */
	void setCategory(ICategory category);

}
