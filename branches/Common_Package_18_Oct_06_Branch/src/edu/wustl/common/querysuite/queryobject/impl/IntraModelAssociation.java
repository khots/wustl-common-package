
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.IIntraModelAssociation;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 14.48.04 AM
 */

public class IntraModelAssociation implements IIntraModelAssociation
{

	private String roleName;
	private String revereseRoleName;
	private boolean bidirectional;
	private IClass leftClass;
	private IClass rightClass;

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#getLeftClass()
	 */
	public IClass getLeftClass()
	{
		return leftClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#getRightClass()
	 */
	public IClass getRightClass()
	{
		return rightClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#isBidirectional()
	 */
	public boolean isBidirectional()
	{
		return bidirectional;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#isIntraModel()
	 */
	public boolean isIntraModel()
	{
		return true;
	}

	/**
	 * @param bidirectional
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setBidirectional(boolean)
	 */
	public void setBidirectional(boolean bidirectional)
	{
		this.bidirectional = bidirectional;
	}

	/**
	 * @param leftClass
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setLeftClass(edu.wustl.common.querysuite.queryobject.IClass)
	 */
	public void setLeftClass(IClass leftClass)
	{
		this.leftClass = leftClass;
	}

	/**
	 * @param rightClass
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setRightClass(edu.wustl.common.querysuite.queryobject.IClass)
	 */
	public void setRightClass(IClass rightClass)
	{
		this.rightClass = rightClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#getReverseRoleName()
	 */
	public String getReverseRoleName()
	{
		return revereseRoleName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#getRoleName()
	 */
	public String getRoleName()
	{
		return roleName;
	}

	/**
	 * @param roleName
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#setReverseRoleName(java.lang.String)
	 */
	public void setReverseRoleName(String roleName)
	{
		revereseRoleName = roleName;
	}

	/**
	 * @param roleName
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#setRoleName(java.lang.String)
	 */
	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

}