
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.16.04 AM
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.util.global.Constants;

public class Rule implements IRule
{

	private static final long serialVersionUID = 7408369497435719981L;
	private List<ICondition> conditions = new ArrayList<ICondition>();
	private IExpression containingExpression;

	/**
	 * Constructor to instanciate object of Rule class.
	 * @param conditions The list of Conditions to set.
	 */
	public Rule(List<ICondition> conditions)
	{
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
	 * @see edu.wustl.common.querysuite.queryobject.IRule#size()
	 */
	public int size()
	{
		return conditions.size();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String string = "";
		for (int i = 0; i < conditions.size(); i++)
		{
			string = string + conditions.get(i).toString();
			if (i != conditions.size())
				string = string + " " + LogicalOperator.And + " ";
		}
		return "[" + string + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;

		if (containingExpression != null)
			hash = hash & Constants.HASH_PRIME + containingExpression.hashCode();

		if (conditions != null)
			hash = hash * Constants.HASH_PRIME + new HashSet<ICondition>(conditions).hashCode();

		return hash;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj != null && this.getClass() == obj.getClass())
		{
			Rule rule = (Rule) obj;
			if (this.containingExpression != null
					&& this.containingExpression.equals(rule.containingExpression)
					&& new HashSet<ICondition>(this.conditions).equals(new HashSet<ICondition>(
							rule.conditions)))
				return true;
		}
		return false;
	}

	
	/**
	 * @param containingExpression the containingExpression to set
	 */
	void setContainingExpression(IExpression containingExpression)
	{
		this.containingExpression = containingExpression;
	}
}
