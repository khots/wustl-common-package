
package edu.wustl.common.querysuite.queryobject;

import java.util.Enumeration;

/**
 * A list of {@link edu.wustl.common.querysuite.queryobject.IExpression}s that
 * is indexed by {@link edu.wustl.common.querysuite.queryobject.IExpressionId}.
 * This is global storage location for all expressions in a query; an
 * {@link edu.wustl.common.querysuite.queryobject.IExpression} can be created
 * only by calling the addExpression method here.
 * @version 1.0
 * @updated 11-Oct-2006 02:56:16 PM
 */
public interface IExpressionList
{

	/**
	 * @param id
	 *            the id (usually obtained from getExpressionIds)
	 */
	public IExpression getExpression(IExpressionId id);

	/**
	 * @param id
	 *            the id of the expression to be removed
	 * @return the removed expression
	 */
	public IExpression removeExpressionWithId(IExpressionId id);

	/**
	 * @return an enumeration of the expressions' ids.
	 * @see Enumeration
	 */
	public Enumeration<IExpressionId> getExpressionIds();

	/**
	 * Creates a new {@link IExpression}, adds it to the list, and returns it.
	 * The expressionId is autoGenerated in this method. This is the only way to
	 * create an expression.
	 * @param functionalClass
	 *            the functional class for which the new expr is created.
	 * @return the newly created expression.
	 * @see IExpressionId
	 */
	public IExpression addExpression(IFunctionalClass functionalClass);

}
