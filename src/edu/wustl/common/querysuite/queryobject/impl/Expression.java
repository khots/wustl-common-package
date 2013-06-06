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
 * @created 12-Oct-2006 11.12.04 AM
 * Class implementation for IExpression.
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IConstraintEntity;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.util.global.Constants;

public class Expression implements IExpression
{

	public static final int NO_LOGICAL_CONNECTOR = -1;
	public static final int BOTH_LOGICAL_CONNECTOR = -2;

	private static final long serialVersionUID = 1426555905287966634L;

	private List<IExpressionOperand> expressionOperands = new ArrayList<IExpressionOperand>();

	private List<ILogicalConnector> logicalConnectors = new ArrayList<ILogicalConnector>();

	private IExpressionId expressionId;

	private IConstraintEntity constraintEntity;
	
	private boolean isInView = false;
	
	private boolean isVisible = true;

	/**
	 * 
	 * @param constraintEntity The reference to the Constraint Entity associated with this class.
	 * @param expressionId The Expression Id for this Expression.
	 */
	public Expression(IConstraintEntity constraintEntity, int expressionId)
	{
		this.constraintEntity = constraintEntity;
		this.expressionId = new ExpressionId(expressionId);
	}

	/**
	 * Default Constructor, only required for junit testcases.
	 *
	 */
	Expression()
	{

	}

	/**
	 * 
	 * @param constraintEntity The reference to constraint Entity
	 * @param expressionOperands List of all ExpressionOperand
	 * @param logicalConnectors List of all Logical connectors joining Expression operands.
	 * @param expressionId The expression for this Expression.
	 */
	public Expression(IConstraintEntity constraintEntity,
			List<IExpressionOperand> expressionOperands, List<ILogicalConnector> logicalConnectors,
			IExpressionId expressionId)
	{
		this.constraintEntity = constraintEntity;
		if (expressionOperands != null)
		{
			this.expressionOperands = expressionOperands;
		}
		if (logicalConnectors != null)
		{
			this.logicalConnectors = logicalConnectors;
		}
		this.expressionId = expressionId;
	}

	/**
	 * @param operand the operand to be removed.
	 * @return true if the operand was found; false otherwise.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#removeOperand(edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public boolean removeOperand(IExpressionOperand operand)
	{
		int index = expressionOperands.indexOf(operand);
		return removeOperand(index) != null;
	}

	/**
	 * @param operand The operand to be added in Expression.
	 * index of the added operand.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public int addOperand(IExpressionOperand operand)
	{
		expressionOperands.add(operand);
		setContainingExpressionForRule(operand);
		if (expressionOperands.size() != 1)
		{
			logicalConnectors.add(QueryObjectFactory
					.createLogicalConnector(LogicalOperator.Unknown));
		}
		return expressionOperands.size() - 1;
	}

	/**
	 * To set the containing Expression in case when the operand is Rule.
	 * @param operand The reference to IExpressionOperand.
	 */
	private void setContainingExpressionForRule(IExpressionOperand operand)
	{
		if (operand instanceof Rule)
		{
			Rule rule = (Rule) operand;
			rule.setContainingExpression(this);
		}
	}

	/**
	 * @param logicalConnector the Logical connector by which the operand will be connected to the operand behind it.
	 * @param operand The operand to be added in Expression.
	 * @return index of the added operand.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(edu.wustl.common.querysuite.queryobject.ILogicalConnector,
	 *      edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public int addOperand(ILogicalConnector logicalConnector, IExpressionOperand operand)
	{
		expressionOperands.add(operand);
		setContainingExpressionForRule(operand);
		logicalConnectors.add(expressionOperands.size() - 2, logicalConnector);
		return expressionOperands.size() - 1;
	}

	/**
	 * @param index The index at which the operand to be inserted.
	 * @param logicalConnector the Logical connector by which the operand will be connected to the operand behind it.
	 * @param operand The operand to be added in Expression.
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size()).
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(int, edu.wustl.common.querysuite.queryobject.ILogicalConnector, edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public void addOperand(int index, ILogicalConnector logicalConnector, IExpressionOperand operand)
	{
		expressionOperands.add(index, operand);
		setContainingExpressionForRule(operand);
		logicalConnectors.add(index - 1, logicalConnector);
		if (((LogicalConnector) logicalConnectors.get(index)).getNestingNumber() > ((LogicalConnector) logicalConnectors
				.get(index - 1)).getNestingNumber())
		{
			((LogicalConnector) logicalConnectors.get(index - 1))
					.setNestingNumber(((LogicalConnector) logicalConnectors.get(index))
							.getNestingNumber());
		}
	}

	/**
	 * @param index The index at which the operand to be inserted.
	 * @param operand The operand to be added in Expression.
	 * @param logicalConnector the Logical connector by which the operand will be connected operand in front of it.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(int, edu.wustl.common.querysuite.queryobject.IExpressionOperand, edu.wustl.common.querysuite.queryobject.ILogicalConnector)
	 */
	public void addOperand(int index, IExpressionOperand operand, ILogicalConnector logicalConnector)
	{
		expressionOperands.add(index, operand);
		setContainingExpressionForRule(operand);
		logicalConnectors.add(index, logicalConnector);
		if (((LogicalConnector) logicalConnectors.get(index)).getNestingNumber() < ((LogicalConnector) logicalConnectors
				.get(index - 1)).getNestingNumber())
		{
			((LogicalConnector) logicalConnectors.get(index))
					.setNestingNumber(((LogicalConnector) logicalConnectors.get(index - 1))
							.getNestingNumber());
		}
	}

	/**
	 * calls addParantheses(0, size-1) 
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addParantheses()
	 */
	public void addParantheses()
	{
		addParantheses(0, logicalConnectors.size() - 1);
	}

	/**
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the right operand. 
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addParantheses(int,
	 *      int)
	 */
	public void addParantheses(int leftOperandIndex, int rightOperandIndex)
	{
		for (int i = leftOperandIndex; i < rightOperandIndex; i++)
		{
			((LogicalConnector) logicalConnectors.get(i)).incrementNestingNumber();
		}
	}

	/**
	 * calls removeParantheses(0, size-1) 
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#removeParantheses()
	 */
	public void removeParantheses()
	{
		removeParantheses(0, logicalConnectors.size() - 1);

	}

	/**
	 * Decrements nesting num of all the logical connectors in the expression between the specified operands' indexes, both inclusive. 
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the right operand. 
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#removeParantheses(int, int)
	 */
	public void removeParantheses(int leftOperandIndex, int rightOperandIndex)
	{
		for (int i = leftOperandIndex; i < rightOperandIndex; i++)
		{
			((LogicalConnector) logicalConnectors.get(i))
					.setNestingNumber(((LogicalConnector) logicalConnectors.get(i))
							.getNestingNumber() - 1);
		}
	}

	/**
	 * @return the Expression Id of this Expression.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getExpressionId()
	 */
	public IExpressionId getExpressionId()
	{
		return expressionId;
	}

	/**
	 * @return The Constraint Entity reference associated with this Expression.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getConstraintEntity()
	 */
	public IConstraintEntity getConstraintEntity()
	{
		return constraintEntity;
	}

	/**
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the right operand.
	 * @return The reference to logical connector between who operands.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getLogicalConnector(int,
	 *      int)
	 */
	public ILogicalConnector getLogicalConnector(int leftOperandIndex, int rightOperandIndex)

	{
		if (rightOperandIndex == leftOperandIndex + 1)
		{
			if(leftOperandIndex == -1 || leftOperandIndex == logicalConnectors.size()) 
			{
				return QueryObjectFactory.createLogicalConnector(LogicalOperator.Unknown, -1);
			}
			return logicalConnectors.get(leftOperandIndex);
		}
		else
		{
			throw new IllegalArgumentException(
					"Incorrect indexes selected; please select adjacent indexes");
		}
	}

	/**
	 * @param index the index of operand.
	 * @return The operand identified by the given index.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getOperand(int)
	 */
	public IExpressionOperand getOperand(int index)
	{
		return expressionOperands.get(index);
	}

	/**
	 * @see edu.wustl.common.query.queryobject.IExpression#removeOperandAndFollowingConnector(edu.wustl.common.query.queryobject.IExpressionOperand)
	 */
	/*
	 * public boolean removeOperandAndFollowingConnector(IExpressionOperand
	 * operand) throws Exception { if (iExpressionOperandList.contains(operand)) {
	 * int i = iExpressionOperandList.indexOf(operand);
	 * iExpressionOperandList.remove(i); iLogicalConnector.remove(i + 1); } else {
	 * throw new Exception("The operand does not exist"); } return false; }
	 */

	/**
	 * @see edu.wustl.common.query.queryobject.IExpression#removeOperandAndPrecedingConnector(edu.wustl.common.query.queryobject.IExpressionOperand)
	 */
	/*
	 * public boolean removeOperandAndPrecedingConnector(IExpressionOperand
	 * operand) throws Exception { if (iExpressionOperandList.contains(operand)) {
	 * int i = iExpressionOperandList.indexOf(operand);
	 * iExpressionOperandList.remove(i); iLogicalConnector.remove(i - 1); } else {
	 * throw new Exception("The operand does not exist"); } return false; }
	 */

	/**
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the right operand.
	 * @param logicalConnector the logical connector between let & Right operand.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#setLogicalConnector(int,
	 *      int, edu.wustl.common.querysuite.queryobject.ILogicalConnector)
	 */
	public void setLogicalConnector(int leftOperandIndex, int rightOperandIndex,
			ILogicalConnector logicalConnector)
	{
		if (rightOperandIndex == leftOperandIndex + 1)
		{
			logicalConnectors.set(leftOperandIndex, logicalConnector);
		}
		else
		{
			throw new IllegalArgumentException(
					"Incorrect indexes selected; please select adjacent indexes");
		}
	}

	/**
	 * @param index the expected index of the operand in he Expression
	 * @param operand The operand to be added in the Expression.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#setOperand(int,IExpressionOperand)
	 */
	public void setOperand(int index, IExpressionOperand operand)
	{
		expressionOperands.set(index, operand);
	}

	/**
	 * @return the no. of operands in the expression.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#numberOfOperands()
	 */
	public int numberOfOperands()
	{
		return expressionOperands.size();
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on expression Id.
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (expressionId != null)
		{
			hash = hash * Constants.HASH_PRIME + expressionId.hashCode();
		}
		return hash;
	}

	/**
	 * To check whether two objects are equal.
	 * @param obj reference to the object to be checked for equality.
	 * @return true if attribute, expression id of both Expressions are equal.
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
			Expression expression = (Expression) obj;
			if (this.expressionId != null & this.expressionId.equals(expression.expressionId))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return String representation of Expression object in the form: [ConstraintEntity : expressionId]
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + constraintEntity + ":" + expressionId + "]";
	}

	/**
	 * @param index the index of operand to be removed.
	 * @return the removed operand.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#removeOperand(int)
	 */
	public IExpressionOperand removeOperand(int index)
	{

		if (index == -1)
		{
			return null;
		}
		// A and (B or C) remove C => A and B
		// A and (B or C) remove B => A and C
		// A and (B or C) remove A => B or C
		// A and B or C   remove B => A and C
		int connectorIndex = indexOfConnectorForOperand(expressionOperands.get(index));
		IExpressionOperand operand = expressionOperands.get(index);

		expressionOperands.remove(index);
		if (connectorIndex == Expression.BOTH_LOGICAL_CONNECTOR) // if both adjacent connectors have same nesting no. then remove connector following the operand.
		{
			connectorIndex = index;
		}

		if (connectorIndex != Expression.NO_LOGICAL_CONNECTOR)
		{
			logicalConnectors.remove(connectorIndex);
		}

		return operand;
	}

	/**
	 * @param operand the reference to IExpressionOperand, to be searched in the Expression.
	 * @return The index of the given Expression operand.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#indexOfOperand(edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public int indexOfOperand(IExpressionOperand operand)
	{
		return expressionOperands.indexOf(operand);
	}

	/**
	 * To get the adjacent logical connector with the greater nesting number.
	 * @param operand The reference to IExpressionOperand of which the Logical connector to search.
	 * @return index of adjacent Logical connector with greater nesting number. 
	 * 		Expression.NO_LOGICAL_CONNECTOR if operand not found or there is no logical connector present in the Expression.
	 * 		Expression.BOTH_LOGICAL_CONNECTOR if both adjacent connectors are of same nesting number
	 */
	public int indexOfConnectorForOperand(IExpressionOperand operand)
	{
		int index = expressionOperands.indexOf(operand);

		if (index != -1)
		{
			if (expressionOperands.size() == 1) // if there is only one Expression then there is no logical connector associated with it.
			{
				index = Expression.NO_LOGICAL_CONNECTOR;
			}
			else if (index == expressionOperands.size() - 1) // if expression is last operand then index of last connector will be returned.
			{
				index = index - 1;
			}
			else if (index != 0) // if expression is not 1st & last, then index will depend upon the immediate bracket surrounding that expression. 
			{
				int preNesting = ((LogicalConnector) logicalConnectors.get(index - 1))
						.getNestingNumber();
				int postNesting = ((LogicalConnector) logicalConnectors.get(index))
						.getNestingNumber();
				if (postNesting == preNesting) // if nesting no are same, then there is not direct bracket sorrounding operand.
				{
					index = Expression.BOTH_LOGICAL_CONNECTOR;
				}
				else if (postNesting < preNesting)
				{
					index--;
				}
			}
		}
		else
		{
			index = Expression.NO_LOGICAL_CONNECTOR;
		}

		return index;
	}

	/**
	 * To check whether the child Expression is pseudo anded with the other expression.
	 * The given expression is pseudo Anded if and only if it has atleast one Expression having Equal Constraint Entity as that of given Expression's & logical connector between then should be AND. 
	 * @param expressionId The child Expression Id.
	 * @param constraints The reference to Constraints, which is having reference to this Expression & the passed expressionId.
	 * @return true if the given Expression is pseudoAnded with other Expression, else returns false.
	 * @throws IllegalArgumentException if the given Expression Id is not child of the Expression.
	 */
	public boolean isPseudoAnded(IExpressionId expressionId, IConstraints constraints)
	{
		int index = expressionOperands.indexOf(expressionId);
		IExpression currentExpression = constraints
				.getExpression((IExpressionId) expressionOperands.get(index));

		if (index < -1)
		{
			throw new IllegalArgumentException("The given Expression Id not found!!!");
		}

		int immediateOperandIndex = indexOfConnectorForOperand((IExpressionOperand) expressionId);

		if (immediateOperandIndex == Expression.NO_LOGICAL_CONNECTOR) // there is no operand around this Expression.
		{
			return false;
		}
		else if (immediateOperandIndex == Expression.BOTH_LOGICAL_CONNECTOR) // both logical connector sorrounding this expression have same nesting no. need to check 'And' operator for both.
		{
			int preIndex = index - 1;
			int postindex = index + 1;

			IExpressionOperand operand = expressionOperands.get(preIndex);
			if (operand.isSubExpressionOperand()
					&& LogicalOperator.And.equals(getLogicalConnector(preIndex, index)
							.getLogicalOperator()))
			{
				IExpression expression = constraints.getExpression((IExpressionId) operand);
				return isHavingSameClass(currentExpression, expression);
			}

			operand = expressionOperands.get(postindex);
			if (operand.isSubExpressionOperand()
					&& LogicalOperator.And.equals(getLogicalConnector(index, postindex)
							.getLogicalOperator()))
			{
				IExpression expression = constraints.getExpression((IExpressionId) operand);
				return isHavingSameClass(currentExpression, expression);
			}
		}
		else
		// check logical connector between immediateOperandIndex & index.
		{
			if (LogicalOperator.And.equals(logicalConnectors.get(immediateOperandIndex)
					.getLogicalOperator()))
			{
				int otherOperandIndex = immediateOperandIndex == index ? index + 1 : index - 1; //otherOperandIndex = immediateOperandIndex => other operand  index is (index+1)
				IExpressionOperand operand = expressionOperands.get(otherOperandIndex);
				if (operand.isSubExpressionOperand())
				{
					IExpression expression = constraints.getExpression((IExpressionId) operand);
					return isHavingSameClass(currentExpression, expression);
				}
			}
		}
		return false;
	}

	/**
	 * To check wether the given 2 Expressions having equal Constraint Entity.
	 * @param expression1 1st expression to check.
	 * @param expression2 2nd expression to check.
	 * @return true if both the expression have same ConstraintEntity.
	 */
	private boolean isHavingSameClass(IExpression expression1, IExpression expression2)
	{
		return expression1.getConstraintEntity().equals(expression2.getConstraintEntity());
	}
	
	/**
	 * To check whether there are any rule present in the Expression.
	 * @return true if there is atleast one rule present in the operand list of expression.
	 */
	public boolean containsRule()
	{
		for (int i=0;i<numberOfOperands();i++)
		{
			if (!getOperand(i).isSubExpressionOperand())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This expression is in view or not 
	 * @return true if it is in view
	 */
	public boolean isInView()
	{
		return isInView;
	}

	/**
	 * To set the expression in view.
	 * @param isInView true if this expression should be added in view.
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#setInView(boolean)
	 */
	public void setInView(boolean isInView)
	{
		this.isInView = isInView;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
	}
	
	
}