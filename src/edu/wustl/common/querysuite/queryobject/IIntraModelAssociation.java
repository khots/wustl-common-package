
package edu.wustl.common.querysuite.queryobject;

/**
 * @version 1.0
 * @updated 11-Oct-2006 02:56:58 PM
 */
public interface IIntraModelAssociation extends IAssociation
{

	/**
	 * the role name in the object model of the app needed to traverse from left class
	 * to right class
	 */
	public String getRoleName();

	/**
	 * @param roleName
	 * 
	 */
	public void setRoleName(String roleName);

	/**
	 * the role name in the object model of the app needed to traverse from right
	 * class to left class.
	 * will be an empty string if isBidirectional() is false.
	 */
	public String getReverseRoleName();

	/**
	 * see getReverseRoleName()
	 * @param roleName
	 * 
	 */
	public void setReverseRoleName(String roleName);

}
