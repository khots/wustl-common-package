
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

	/**
	 * To add an empty condition to the Rule.
	 * @return The reference to the newly added condition.
	 */
	ICondition addCondition();

	/**
	 * To add condition to the Rule.
	 * @param condition The condition to be added.
	 * @return the reference to the newly added condition.
	 */
	ICondition addCondition(ICondition condition);

	/**
	 * To get the condition indexed by given index in the condition list of Rule.
	 * @param index The index of the Condition.
	 * @return the reference to the Condition indexed by given index in the condition list of Rule.
	 */
	ICondition getCondition(int index);

	/**
	 * @return the expression to which this rule belongs.
	 */
	IExpression getContainingExpression();

	/**
	 * To get the no. of conditions present in the Rule.
	 * @return The no. of conditions present in the Rule.
	 */
	int size();
    
    public void removeAllConditions();

}