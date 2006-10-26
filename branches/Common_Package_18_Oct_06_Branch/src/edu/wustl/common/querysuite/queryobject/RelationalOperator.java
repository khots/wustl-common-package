
package edu.wustl.common.querysuite.queryobject;

/**
 * Enum:
 * Equals
 * LessThan
 * GreaterThan
 * Like
 * In
 * Between
 * 
 * etc....
 * @version 1.0
 * @updated 11-Oct-2006 02:57:23 PM
 */
public enum RelationalOperator {
	Equals(1), NotEquals(1), Between(2), IsNull(0), IsNotNull(0), LessThan(1), LessThanOrEquals(1), GreaterThan(
			1), GreaterThanOrEquals(1), In(Integer.MAX_VALUE);

	private int numberOfValuesRequired;

	RelationalOperator(int numberOfValuesRequired)
	{
		this.numberOfValuesRequired = numberOfValuesRequired;
	}

	public int numberOfValuesRequired()
	{
		return numberOfValuesRequired;
	}
}