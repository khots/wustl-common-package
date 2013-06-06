/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

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

	/**
	 * 
	 * @param id The id to be assigned.
	 */
	public ExpressionId(int id)
	{
		this.id = id;
	}

	/**
	 * @return The integer value sassigned to this Expression id.
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionId#getInt()
	 */
	public int getInt()
	{
		return id;
	}

	/**
	 * This method will return true value always.
	 * @return true.
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionOperand#isSubExpressionOperand()
	 */
	public boolean isSubExpressionOperand()
	{
		return true;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on expression Id.
	 * @return The hash code value for the object.
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
	 * To check whether two objects are equal.
	 * @param obj reference to the object to be checked for equality.
	 * @return true if expression id of both objects are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this==obj)
		{
			return true;
		}
		if (obj != null && this.getClass() == obj.getClass())
		{
			ExpressionId expressionId = (ExpressionId) obj;
			if (this.id == expressionId.id)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return String representation of Expression Id object in the form: [ExpressionId: id]
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ExpressionId:" + id;
	}

}
