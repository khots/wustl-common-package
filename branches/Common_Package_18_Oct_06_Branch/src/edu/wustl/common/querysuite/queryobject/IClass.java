
package edu.wustl.common.querysuite.queryobject;

/**
 * @version 1.0
 * @updated 11-Oct-2006 02:55:32 PM
 */
public interface IClass extends IFunctionalClass
{

	public String getFullyQualifiedName();

	/**
	 * @param fullyQualifiedName
	 * 
	 */
	public void setFullyQualifiedName(String fullyQualifiedName);

	/**
	 * the class may be needed for associations only, and need not be shown on UI. The
	 * default value is TRUE, i.e. a class will be visible.
	 * it will be false for classes in a category that are needed for joining purposes
	 * only.
	 */
	public boolean isVisible();

	/**
	 * @param visible
	 * 
	 */
	public void setVisible(boolean visible);

}
