
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
	Equals(1),
	NotEquals(1),
	Between(2),
	IsNull(0),
	IsNotNull(0),
	LessThan(1),
	LessThanOrEquals(1),
	GreaterThan(1),
	GreaterThanOrEquals(1),
	In(Integer.MAX_VALUE),
	Contains(1),
	StartsWith(1),
	EndsWith(1);

	private int numberOfValuesRequired;

	RelationalOperator(int numberOfValuesRequired)
	{
		this.numberOfValuesRequired = numberOfValuesRequired;
	}

	public int numberOfValuesRequired()
	{
		return numberOfValuesRequired;
	}
	
	/**
	 * To get the String representation of the operator.
	 * @param relationalOperator The reference to RelationalOperator
	 * @return The SQL String representation of the Operator.
	 */
	public static String getSQL(RelationalOperator relationalOperator)
	{
		String sql = null;
		if (relationalOperator.equals(RelationalOperator.Equals))
			sql = "=";
		else if (relationalOperator.equals(RelationalOperator.NotEquals))
			sql = "!=";
		else if (relationalOperator.equals(RelationalOperator.IsNull))
			sql = "is NULL";
		else if (relationalOperator.equals(RelationalOperator.IsNotNull))
			sql = "is NOT NULL";
		else if (relationalOperator.equals(RelationalOperator.LessThan))
			sql = "<";
		else if (relationalOperator.equals(RelationalOperator.LessThanOrEquals))
			sql = "<=";
		else if (relationalOperator.equals(RelationalOperator.GreaterThan))
			sql = ">";
		else if (relationalOperator.equals(RelationalOperator.GreaterThanOrEquals))
			sql = ">=";
		else if (relationalOperator.equals(RelationalOperator.In))
			sql = "in";
		return sql;
	}
}