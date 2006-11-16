
package edu.wustl.common.querysuite.queryobject;

/**
 * A list of conditions, on different attributes. We could have checks to ensure
 * that conditions on the same attribute are not added to a rule. e.g. when user
 * says, "male participants of african origin" all the conditions belong to a
 * single rule. Note : The conditions in a rule are implicitly linked by AND
 * condition.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:20 PM
 */
public interface IRule extends IExpressionOperand
{

	public ICondition addCondition();

	/**
	 * returns the condition added
	 * @param condition
	 */
	public ICondition addCondition(ICondition condition);

	/**
	 * @param i
	 */
	public ICondition getCondition(int i);

	/**
	 * @return the expression to which this rule belongs.
	 */
	public IExpression getContainingExpression();

	public int size();

}
