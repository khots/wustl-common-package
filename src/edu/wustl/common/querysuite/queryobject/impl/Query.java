/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.07.04 AM
 * The IQuery implementation class.
 */

import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IOutputTreeNode;
import edu.wustl.common.querysuite.queryobject.IQuery;

public class Query implements IQuery
{

	private static final long serialVersionUID = -9105109010866749580L;

	private IConstraints constraints;

	private IOutputTreeNode root;

	/**
	 * @return the reference to the root noe of the output tree.
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#getRootOutputClass()
	 * @deprecated This method is not required any more for output tree.
	 */
	public IOutputTreeNode getRootOutputClass()
	{
		return root;
	}

	/**
	 * @return the reference to constraints.
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#getConstraints()
	 */
	public IConstraints getConstraints()
	{
		if (constraints == null)
		{
			constraints = new Constraints();
		}
		return constraints;
	}

	/**
	 * @param constraints the constraints to set.
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#setConstraints(edu.wustl.common.querysuite.queryobject.IConstraints)
	 */
	public void setConstraints(IConstraints constraints)
	{
		this.constraints = constraints;

	}

	/**
	 * @param root The reference to the root noe of the output tree.
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#setRootOutputClass(edu.wustl.common.querysuite.queryobject.IOutputTreeNode)
	 * @deprecated This method is not required any more for output tree.
	 */
	public void setRootOutputClass(IOutputTreeNode root)
	{
		this.root = root;
	}

}
