
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IExpressionId;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 11.12.04 AM
 */

public class ExpressionId implements IExpressionId
{

	private int id;

	public ExpressionId(int id)
	{
		this.id = id;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionId#getInt()
	 */
	public int getInt()
	{
		return id;
	}

	/**
	 * This method will return true value always.
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionOperand#isSubExpressionOperand()
	 */
	public boolean isSubExpressionOperand()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof ExpressionId)
		{
			ExpressionId expressionId = (ExpressionId) obj;
			if (this.id == expressionId.id)
				return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ExpressionId:" + id;
	}

}
