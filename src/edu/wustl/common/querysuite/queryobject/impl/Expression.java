
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 11.12.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;

public class Expression implements IExpression
{

	private List<IExpressionOperand> expressionOperands = new ArrayList<IExpressionOperand>();

	private List<ILogicalConnector> logicalConnectors = new ArrayList<ILogicalConnector>();

	private IExpressionId expressionId;

	private IFunctionalClass functionalClass;

	/**
	 * @param functionalClass
	 */
	public Expression(IFunctionalClass functionalClass, int expressionId)
	{
		this.functionalClass = functionalClass;
		this.expressionId = new ExpressionId(expressionId);
	}

	Expression()
	{

	}

	public Expression(IFunctionalClass functionalClass,
			List<IExpressionOperand> expressionOperands, List<ILogicalConnector> logicalConnectors,
			IExpressionId expressionId)
	{
		this.functionalClass = functionalClass;
		if (expressionOperands != null)
			this.expressionOperands = expressionOperands;
		if (logicalConnectors != null)
			this.logicalConnectors = logicalConnectors;
		this.expressionId = expressionId;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#removeOperand(edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public boolean removeOperand(IExpressionOperand operand)
	{
		int index = expressionOperands.indexOf(operand);
		return removeOperand(index) != null;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public IExpressionOperand addOperand(IExpressionOperand operand)
	{
		expressionOperands.add(operand);
		if(expressionOperands.size() != 1)
			logicalConnectors.add(QueryObjectFactory.createLogicalConnector(LogicalOperator.And, 0));
		return operand;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(edu.wustl.common.querysuite.queryobject.ILogicalConnector,
	 *      edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public void addOperand(ILogicalConnector logicalConnector, IExpressionOperand operand)
	{
		expressionOperands.add(operand);
		logicalConnectors.add(expressionOperands.size() - 2, logicalConnector);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(int, edu.wustl.common.querysuite.queryobject.ILogicalConnector, edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public void addOperand(int index, ILogicalConnector logicalConnector, IExpressionOperand operand)
	{
		expressionOperands.add(index, operand);
		logicalConnectors.add(index - 1, logicalConnector);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addOperand(int, edu.wustl.common.querysuite.queryobject.IExpressionOperand, edu.wustl.common.querysuite.queryobject.ILogicalConnector)
	 */
	public void addOperand(int index, IExpressionOperand operand, ILogicalConnector logicalConnector)
	{
		expressionOperands.add(index, operand);
		logicalConnectors.add(index, logicalConnector);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#addParantheses()
	 */
	public void addParantheses()
	{
		addParantheses(0, logicalConnectors.size() - 1);
	}

	/**
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
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getExpressionId()
	 */
	public IExpressionId getExpressionId()
	{
		return expressionId;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getFunctionalClass()
	 */
	public IFunctionalClass getFunctionalClass()
	{
		return functionalClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getLogicalConnector(int,
	 *      int)
	 */
	public ILogicalConnector getLogicalConnector(int leftOperandIndex, int rightOperandIndex)

	{
		if (rightOperandIndex == leftOperandIndex + 1)
		{
			return logicalConnectors.get(leftOperandIndex);
		}
		else
		{
			throw new IllegalArgumentException(
					"Incorrect indexes selected; please select adjacent indexes");
		}
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getOperand(int)
	 */
	public IExpressionOperand getOperand(int i)
	{
		return expressionOperands.get(i);
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
	 * @param leftOperandIndex,
	 *            rightOperandIndex, logicalConnector
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
	 * @param i
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#setOperand(int)
	 */
	public void setOperand(int i)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#getSize()
	 */
	public int getSize()
	{
		return expressionOperands.size();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Expression)
		{
			Expression expression = (Expression) obj;
			if (this.expressionId != null & this.expressionId.equals(expression.expressionId))
				return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + functionalClass + ":" + expressionId + "]";
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#removeOperand(int)
	 */
	public IExpressionOperand removeOperand(int index)
	{

		if (index == -1)
			return null;
		// A and (B or C) remove C => A and B
		// A and (B or C) remove B => A and C
		// A and (B or C) remove A => B or C
		int connectorIndex = index; // if removing 1st operand then 1st operator must be removed. 
		if (index == expressionOperands.size() - 1) // if removing last operand then last operator must be removed.
		{
			connectorIndex = index - 1;
		}
		else if (index != 0) // if operand to removed is not 1st & last, then operator to remove will depend upon the immediate bracket surrounding that operand. 
		{
			int preNesting = logicalConnectors.get(index - 1).getNestingNumber();
			int postNesting = logicalConnectors.get(index).getNestingNumber();
			if (postNesting < preNesting)
			{
				connectorIndex--;
			}
		}

		IExpressionOperand operand = expressionOperands.get(index);

		expressionOperands.remove(index);
		if (connectorIndex >= 0)
			logicalConnectors.remove(connectorIndex);

		return operand;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpression#indexOfOperand(edu.wustl.common.querysuite.queryobject.IExpressionOperand)
	 */
	public int indexOfOperand(IExpressionOperand operand)
	{
		return expressionOperands.indexOf(operand);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IExpressionOperand#isSubExpressionOperand()
	 */
	public boolean isSubExpressionOperand()
	{
		return true;
	}

}