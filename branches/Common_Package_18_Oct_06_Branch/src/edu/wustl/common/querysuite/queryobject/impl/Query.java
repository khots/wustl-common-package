
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.07.04 AM
 */

import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IOutputTreeNode;
import edu.wustl.common.querysuite.queryobject.IQuery;

public class Query implements IQuery
{

	private IConstraints constraints;

	private IOutputTreeNode root;

	public IOutputTreeNode getRootOutputClass()
	{
		return root;
	}

	public IConstraints getConstraints()
	{
		return constraints;
	}

	public void setConstraints(IConstraints constraints)
	{
		this.constraints = constraints;

	}

	public void setRootOutputClass(IOutputTreeNode root)
	{
		this.root = root;
	}

}
