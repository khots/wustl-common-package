
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.16.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IRule;

public class Rule implements IRule
{

	private List<ICondition> conditions = new ArrayList<ICondition>();
	private IExpression containingExpression;

	public Rule(List<ICondition> conditions, IExpression containingExpression)
	{
		this.containingExpression = containingExpression;
		if (conditions != null)
			this.conditions = conditions;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IRule#addCondition()
	 */
	public ICondition addCondition()
	{
		ICondition iCondition = QueryObjectFactory.createCondition();
		conditions.add(iCondition);
		return iCondition;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IRule#addCondition(edu.wustl.common.querysuite.queryobject.ICondition)
	 */
	public ICondition addCondition(ICondition condition)
	{
		conditions.add(condition);
		return condition;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IRule#getCondition(int)
	 */
	public ICondition getCondition(int i)
	{
		return conditions.get(i);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IRule#getContainingExpression()
	 */
	public IExpression getContainingExpression()
	{
		return containingExpression;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionOperand#isSubExpressionOperand()
	 */
	public boolean isSubExpressionOperand()
	{
		return false;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IRule#getSize()
	 */
	public int getSize()
	{
		return conditions.size();
	}

}
