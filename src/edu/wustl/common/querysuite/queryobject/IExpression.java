/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryobject;

/**
 * A list of operands, and the logical connectors (AND, OR), that together form a
 * logical expression<br> The connectors are identified by the position of the
 * operands on either side, e.g.,
 * <pre> given:  operand connector operand connector operand index:    0        0,
 * 1      1       1,2        2
 * </pre>  An IExpression belongs to a constraint entity; and constraints on
 * another associated entity will be present as a subexpression on the associated
 * entity . <br>Conversely, if an expression has a subexpression, there must an
 * association in the join graph from the parent expression to the subexpression.
 * <br>Note: "subexpression" refers to an operand that is the IExpressionId of the
 * child expression. The entity  of the subexpression will generally be different
 * from the entity  of this expression (exception is when a class is associated to
 * itself, e.g. Specimen). <br>The expression for an expressionId is found from
 * {@link edu.wustl.common.querysuite.queryobject.IConstraints}.
 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph
 * @see edu.wustl.common.querysuite.queryobject.
 * IConstraints#addExpression(IFunctionalClass)
 * @version 1.0
 * @updated 22-Dec-2006 2:50:17 PM
 */
public interface IExpression extends IBaseQueryObject
{

	/**
	 * To get the operand indexed by index in the operand list of Expression.
	 * @param index index of the operand in the operand list of Expression.
	 * @return The operand identified by the given index.
	 */
	IExpressionOperand getOperand(int index);

	/**
	 * The constraintEntity of IRule is constructor param to impl class.
	 * @return The reference to the constraintEntity associated with this expression.
	 */
	IConstraintEntity getConstraintEntity();

	/**
	 * To set the operand in the Expression at index position
	 * @param index the expected index of the operand in he Expression 
	 * @param operand The operand to be added in the Expression.
	 */
	void setOperand(int index, IExpressionOperand operand);

	/**
	 * An operator is indexed by {left operand, right operand} i.e. if left index is
	 * "i", value of right index must be "i+1" this is just to make the method
	 * intuitively clearer...
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the right operand.
	 * @return The reference to logical connector between who adjacent operands.
	 */
	ILogicalConnector getLogicalConnector(int leftOperandIndex, int rightOperandIndex);

	/**
	 * To set logical connector between two adjescent operands.
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the right operand.
	 * @param logicalConnector The reference to the logical connector to set.
	 * 
	 */
	void setLogicalConnector(int leftOperandIndex, int rightOperandIndex,
			ILogicalConnector logicalConnector);

	/**
	 * To add the Parenthesis around the operands specified by left & right operand index.
	 * Increments nesting num of all the logical connectors in the expression between
	 * the specified operands' indexes, both inclusive.
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the left operand.
	 * 
	 */
	void addParantheses(int leftOperandIndex, int rightOperandIndex);

	/**
	 * Just calls addParantheses(0, size-1)
	 */
	void addParantheses();

	/**
	 * To remove the Parenthesis around the operands specified by left & right operand index.
	 * Decrements nesting num of all the logical connectors in the expression between
	 * the specified operands' indexes, both inclusive.
	 * @param leftOperandIndex The index of the left operand.
	 * @param rightOperandIndex The index of the left operand.
	 */
	void removeParantheses(int leftOperandIndex, int rightOperandIndex);

	/**
	 * Just calls removeParantheses(0, size-1)
	 */
	void removeParantheses();

	/**
	 * Adds an operand to the operands list. A default logical connector AND will be
	 * added to the connectors list provided there are atleast two operands in the
	 * operands list.
	 * @param operand The reference of the operand added.
	 * @return index of the added operand.
	 */
	int addOperand(IExpressionOperand operand);

	/**
	 * To add operand to the Expression with the specified logical connector. This operand will be added as last operand in the operand list. 
	 * @param logicalConnector the Logical connector by which the operand will be connected to the operand behind it.
	 * @param operand The operand to be added in Expression.
	 * @return index of the added operand.
	 */
	int addOperand(ILogicalConnector logicalConnector, IExpressionOperand operand);

	/**
	 * Inserts an operand with the connector in front of it.
	 * @param index  The index at which the operand to be inserted.
	 * @param logicalConnector the Logical connector by which the operand will be connected to the operand behind it.
	 * @param operand The operand to be added in Expression.
	 */
	void addOperand(int index, ILogicalConnector logicalConnector, IExpressionOperand operand);

	/**
	 * Inserts an operand with the connector behind it.
	 * @param index The index at which the operand to be inserted.
	 * @param operand The operand to be added in Expression.
	 * @param logicalConnector the Logical connector by which the operand will be connected operand in front of it.
	 * 
	 */
	void addOperand(int index, IExpressionOperand operand, ILogicalConnector logicalConnector);

	/**
	 * Removes the operand, and the appropriate connector. The adjacent logical
	 * connector with the greater nesting number will be removed. If both
	 * adjacent connectors are of same nesting number, it is undefined as to
	 * which one will be removed.
	 * @param index The index of operand to be removed.
	 * @return reference to removed operand.
	 */
	IExpressionOperand removeOperand(int index);

	/**
	 * Removes the operand, and the appropriate connector. The adjacent logical
	 * connector with the greater nesting number will be removed. If both
	 * adjacent connectors are of same nesting number, it is undefined as to
	 * which one will be removed.
	 * @param operand the operand to be removed.
	 * @return true if the operand was found; false otherwise.
	 */
	boolean removeOperand(IExpressionOperand operand);

	/**
	 * To get the index of the operand in the operand list of Expression. 
	 * @param operand the reference to IExpressionOperand, to be searched in the Expression.
	 * @return The index of the given Expression operand.
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	int indexOfOperand(IExpressionOperand operand);

	/**
	 * To get the Expression id assigned to this Expression.
	 * @return the id of this expression.
	 */
	IExpressionId getExpressionId();

	/**
	 * To the the number of operand in the operand list of Expression.
	 * @return the no. of operands in the expression.
	 */
	int numberOfOperands();
	
	/**
	 * This expression is in view or not 
	 * @return true if it is in view
	 */
	boolean isInView();
	
	/**
	 * This expression is visible or not on DAG
	 * @return
	 */
	boolean isVisible();
	
	void setVisible(boolean isVisible);

	/**
	 * To set the expression in view.
	 * @param isInView true if this expression should be added in view.
	 */
	void setInView(boolean isInView);
	
	/**
	 * To check whether there are any rule present in the Expression.
	 * @return true if there is atleast one rule present in the operand list of expression.
	 */
	public boolean containsRule();
}