
package edu.wustl.common.querysuite.queryobject;

/**
 * contains
 * LogicalOperatorCode
 * Nesting number (for parantheses)
 * @version 1.0
 * @updated 11-Oct-2006 02:57:02 PM
 */
public interface ILogicalConnector
{

	public LogicalOperator getLogicalOperator();

	/**
	 * @param logicOperatorCode
	 * 
	 */
	public void setLogicalOperator(LogicalOperator logicOperatorCode);

	/**
	 * denotes no. of parantheses around this operator
	 */
	public int getNestingNumber();

	/**
	 * @param nestingNumber
	 * 
	 */
	//public void setNestingNumber(int nestingNumber);
	//public void incrementNestingNumber();
}
