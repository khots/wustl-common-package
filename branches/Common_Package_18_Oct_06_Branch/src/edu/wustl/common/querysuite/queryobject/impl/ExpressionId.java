
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 11.12.04 AM
 */

public class ExpressionId implements IExpressionId
{

	private static final long serialVersionUID = 2012640054952775498L;
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

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash*Constants.HASH_PRIME + id;
		
		return hash;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this==obj)
			return true;
		
		if (obj != null && this.getClass() == obj.getClass())
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
