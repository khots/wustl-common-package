
package edu.wustl.common.querysuite.queryobject;

/**
 * Represents a logical connector (AND / OR). The nesting represents the number
 * of parentheses (depth of parentheses) around the logic portion (AND or OR) of
 * the connector.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:02 PM
 */
public interface ILogicalConnector extends IBaseQueryObject
{

	public LogicalOperator getLogicalOperator();

	/**
	 * @param logicOperatorCode
	 */
	public void setLogicalOperator(LogicalOperator logicalOperator);

	/**
	 * denotes no. of parantheses around this operator
	 */
	public int getNestingNumber();

	/**
	 * @param nestingNumber
	 */
	public void setNestingNumber(int nestingNumber);
	
	public void incrementNestingNumber();
}
