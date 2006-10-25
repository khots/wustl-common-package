
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 13.35.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionList;

public class ExpressionList
{

	private List<IExpression> expressions = new ArrayList<IExpression>();

	public ExpressionList(List<IExpression> expressions)
	{
		if (expressions != null)
			this.expressions = expressions;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionList#addExpression(edu.wustl.common.querysuite.queryobject.IExpression)
	 */
	public void addExpression(IExpression expression)
	{
		expressions.add(expression);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionList#getExpression(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IExpression getExpression(IExpressionId i)
	{
		return expressions.get(i.getInt());
	}

	/**
	 * removes the first occurence of the specified element from the list if the 
	 * list contains one and returns true, otherwise the list is unchanged and 
	 * returns false.
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionList#removeExpression(edu.wustl.common.querysuite.queryobject.IExpression)
	 */
	public boolean removeExpression(IExpression expression)
	{
		return expressions.remove(expression);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionList#removeExpression(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IExpression removeExpression(IExpressionId i)
	{
		return expressions.remove(i.getInt());
	}

}