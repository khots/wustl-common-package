
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

/**
 * @version 1.0
 * @updated 11-Oct-2006 02:55:32 PM
 */
public interface IClass extends IFunctionalClass
{

	public String getFullyQualifiedName();

	/**
	 * @param fullyQualifiedName
	 */
	public void setFullyQualifiedName(String fullyQualifiedName);

	/**
	 * The class may be needed for associations only, and need not be shown on
	 * UI. The default value is TRUE, i.e. a class will be visible. It will be
	 * false for classes in a category that are needed for joining purposes
	 * only. It could also be false when a unique path between two classes
	 * (chosen by user) is determined which requires an intermediate third
	 * class; in this case, the third class would not be "visible".
	 */
	public boolean isVisible();

	/**
	 * @param visible
	 */
	public void setVisible(boolean visible);
	
	/**
	 * @return all the urls containing this class.
	 */
	public List<String> allUrls();

}
