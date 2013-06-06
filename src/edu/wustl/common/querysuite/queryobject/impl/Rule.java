/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

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
		{
			this.conditions = conditions;
		}
	}

	/**
	 * @return The reference to the newly added condition.
	 * @see edu.wustl.common.querysuite.queryobject.IRule#addCondition()
	 */
	public ICondition addCondition()
	{
		ICondition iCondition = QueryObjectFactory.createCondition();
		conditions.add(iCondition);
		return iCondition;
	}

	/**
	 * @param condition The condition to be added.
	 * @return the reference to the newly added condition.
	 * @see edu.wustl.common.querysuite.queryobject.IRule#addCondition(edu.wustl.common.querysuite.queryobject.ICondition)
	 */
	public ICondition addCondition(ICondition condition)
	{
		conditions.add(condition);
		return condition;
	}

	/**
	 * @param index The index of the Condition.
	 * @return the reference to the Condition indexed by given index in the condition list of Rule.
	 * @see edu.wustl.common.querysuite.queryobject.IRule#getCondition(int)
	 */
	public ICondition getCondition(int index)
	{
		return conditions.get(index);
	}

	/**
	 * @return the expression to which this rule belongs.
	 * @see edu.wustl.common.querysuite.queryobject.IRule#getContainingExpression()
	 */
	public IExpression getContainingExpression()
	{
		return containingExpression;
	}

	/**
	 * @return false.
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionOperand#isSubExpressionOperand()
	 */
	public boolean isSubExpressionOperand()
	{
		return false;
	}

	/**
	 * @return The no. of conditions present in the Rule.
	 * @see edu.wustl.common.querysuite.queryobject.IRule#size()
	 */
	public int size()
	{
		return conditions.size();
	}

	/**
	 * @return String representation of Rule object in the form: [[conditions]]
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String string = "";
		for (int i = 0; i < conditions.size(); i++)
		{
			string = string + conditions.get(i).toString();
			if (i != conditions.size() - 1)
			{
				string = string + " " + LogicalOperator.And + " ";
			}
		}
		return "[" + string + "]";
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on containingExpression, conditions.
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;

		if (containingExpression != null)
		{
			hash = hash & Constants.HASH_PRIME + containingExpression.hashCode();
		}

		if (conditions != null)
		{
			hash = hash * Constants.HASH_PRIME + new HashSet<ICondition>(conditions).hashCode();
		}

		return hash;
	}

	/**
	 * To check whether two objects are equal.
	 * @param obj reference to the object to be checked for equality.
	 * @return true if containingExpression, conditions of both Rules are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj != null && this.getClass() == obj.getClass())
		{
			Rule rule = (Rule) obj;
			if (this.containingExpression != null
					&& this.containingExpression.equals(rule.containingExpression)
					&& new HashSet<ICondition>(this.conditions).equals(new HashSet<ICondition>(
							rule.conditions)))
			{
				return true;
			}
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

	/**
	 * To clear condition List of the Rule.
	 * @see edu.wustl.common.querysuite.queryobject.IRule#removeAllConditions()
	 */
	public void removeAllConditions()
	{
		conditions.clear();
	}

	/**
	 * To get the copy of the Rule. Note that, this is not deep copy.
	 * @return The copy og the Rule.
	 */
	public IRule getCopy()
	{
		List<ICondition> theConditions = new ArrayList<ICondition>();
		theConditions.addAll(this.conditions);
		IRule rule = new Rule(theConditions);
		return rule;
	}

	/**
	 * To get the list of conditions of the rule.
	 * @return List of ICondition objects.
	 */
	public List<ICondition> getConditions()
	{
		return conditions;
	}
}
