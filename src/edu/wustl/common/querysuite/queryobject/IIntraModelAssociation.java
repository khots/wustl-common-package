
package edu.wustl.common.querysuite.queryobject;

/**
 * @version 1.0
 * @updated 11-Oct-2006 02:56:58 PM
 */
public interface IIntraModelAssociation extends IAssociation
{

	/**
	 * the role name in the object model of the app needed to traverse from source class
	 * to target class
	 */
	public String getTargetRoleName();

	/**
	 * @param roleName
	 * 
	 */
	public void setTargetRoleName(String roleName);

	/**
	 * the role name in the object model of the app needed to traverse from target
	 * class to source class.
	 * will be an empty string if isBidirectional() is false.
	 */
	public String getSourceRoleName();

	/**
	 * see getReverseRoleName()
	 * @param roleName
	 * 
	 */
	public void setSourceRoleName(String roleName);

}
