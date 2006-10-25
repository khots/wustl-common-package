
package edu.wustl.common.querysuite.queryobject;

/**
 * Enum
 * p-AND
 * OR
 * @version 1.0
 * @updated 11-Oct-2006 02:57:20 PM
 */
public enum LogicalOperator {

	AND(0), OR(1), PAND(2);

	private int arity;

	LogicalOperator(int arity)
	{
		this.arity = arity;
	}

	public int arity()
	{
		return arity;
	}
}