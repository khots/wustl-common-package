
package edu.wustl.common.querysuite.queryobject;

/**
 * basically wrapper around LinkedList<IExpression> indexed by ExpressionIndex
 * @version 1.0
 * @updated 11-Oct-2006 02:56:16 PM
 */
public interface IExpressionList
{

	/**
	 * @param i
	 * 
	 */
	public IExpression getExpression(IExpressionId i);

	/**
	 * @param expression
	 * 
	 */
	public void addExpression(IExpression expression);

	/**
	 * true if the expression existed.
	 * @param expression
	 * 
	 */
	public boolean removeExpression(IExpression expression);

	/**
	 * returns the removed expression
	 * @param i
	 * 
	 */
	public IExpression removeExpression(IExpressionId i);

}
