
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

	private String sourceRoleName;
	private String targetRoleName;
	private boolean bidirectional;
	private IClass sourceClass;
	private IClass targetClass;

	public IntraModelAssociation(IClass leftClass, IClass rightClass, String roleName,
			String revereseRoleName, boolean bidirectional)
	{
		this.sourceClass = leftClass;
		this.targetClass = rightClass;
		this.sourceRoleName = roleName;
		this.targetRoleName = revereseRoleName;
		this.bidirectional = bidirectional;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#getSourceClass()
	 */
	public IClass getSourceClass()
	{
		return sourceClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#getTargetClass()
	 */
	public IClass getTargetClass()
	{
		return targetClass;
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
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setSourceClass(edu.wustl.common.querysuite.queryobject.IClass)
	 */
	public void setSourceClass(IClass leftClass)
	{
		this.sourceClass = leftClass;
	}

	/**
	 * @param rightClass
	 * @see edu.wustl.common.querysuite.queryobject.IAssociation#setTargetClass(edu.wustl.common.querysuite.queryobject.IClass)
	 */
	public void setTargetClass(IClass rightClass)
	{
		this.targetClass = rightClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#getSourceRoleName()
	 */
	public String getSourceRoleName()
	{
		return sourceRoleName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#getTargetRoleName()
	 */
	public String getTargetRoleName()
	{
		return targetRoleName;
	}

	/**
	 * @param roleName
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#setSourceRoleName(java.lang.String)
	 */
	public void setSourceRoleName(String roleName)
	{
		sourceRoleName = roleName;
	}

	/**
	 * @param roleName
	 * @see edu.wustl.common.querysuite.queryobject.IIntraModelAssociation#setTargetRoleName(java.lang.String)
	 */
	public void setTargetRoleName(String roleName)
	{
		this.targetRoleName = roleName;
	}

}