
package edu.wustl.common.querysuite.queryobject;

/**
 * A list of operands, and the logical connectors (AND, OR), that together form
 * a logical expression<br>
 * The connectors are identified by the position of the operands on either side,
 * e.g.,
 * 
 * <pre>
 *         given:  operand connector operand connector operand
 *         index:    0        0,1      1       1,2        2
 * </pre>
 * 
 * An IExpression belongs to a functional class; and constraints on another
 * associated class will be present as a subexpression on the associated
 * functional class. <br>Conversely, if an expression has a subexpression, there
 * must an association in the join graph from the parent expression to the
 * subexpression. <br>Note: "subexpression" refers to an operand that is the
 * IExpressionId of the child expression. The functional class of the
 * subexpression will generally be different from the functional class of this
 * expression (exception is when a class is associated to itself, e.g.
 * Specimen). <br>The expression for an expressionId is found from
 * {@link edu.wustl.common.querysuite.queryobject.IConstraints}.
 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph
 * @see edu.wustl.common.querysuite.queryobject.IConstraints#addExpression(IFunctionalClass)
 * @version 1.0
 * @updated 11-Oct-2006 02:56:12 PM
 */
public interface IExpression extends IExpressionOperand
{

	/**
	 * @param i
	 */
	public IExpressionOperand getOperand(int i);

	/**
	 * the functionalClass of IRule is constructor param to impl class.
	 */
	public IFunctionalClass getFunctionalClass();

	/**
	 * @param i
	 */
	public void setOperand(int i);

	/**
	 * An operator is indexed by {left operand, right operand} i.e. if left
	 * index is "i", value of right index must be "i+1" this is just to make the
	 * method intuitively clearer...
	 * @param leftOperandIndex
	 * @param rightOperandIndex
	 * @throws IllegalArgumentException
	 *             if rightOperandIndex != leftOperandIndex + 1.
	 */
	public ILogicalConnector getLogicalConnector(int leftOperandIndex, int rightOperandIndex);

	/**
	 * @param leftOperandIndex
	 * @param rightOperandIndex
	 * @param logicalConnector
	 * @throws IllegalArgumentException
	 *             if rightOperandIndex != leftOperandIndex + 1.
	 */
	public void setLogicalConnector(int leftOperandIndex, int rightOperandIndex,
			ILogicalConnector logicalConnector);

	/**
	 * Increments nesting num of all the logical connectors in the expression
	 * between the specified operands' indexes, both inclusive.
	 * @param leftOperandIndex
	 * @param rightOperandIndex
	 */
	public void addParantheses(int leftOperandIndex, int rightOperandIndex);

	/**
	 * Just calls addParantheses(0, size-1)
	 */
	public void addParantheses();

	/**
	 * Adds an operand at the end of the operands list. An empty default
	 * connector will be used to connect this operand with the rest of the
	 * expression.
	 * @param operand
	 *            the operand to be added.
	 */
	public IExpressionOperand addOperand(IExpressionOperand operand);

	/**
	 * @param logicalConnector
	 * @param operand
	 */
	public void addOperand(ILogicalConnector logicalConnector, IExpressionOperand operand);

	/**
	 * Inserts an operand with the connector in front of it.
	 * @param logicalConnector
	 * @param operand
	 */
	public void addOperand(int index, ILogicalConnector logicalConnector, IExpressionOperand operand);

	/**
	 * Inserts an operand with the connector behind it.
	 * @param logicalConnector
	 * @param operand
	 */
	public void addOperand(int index, IExpressionOperand operand, ILogicalConnector logicalConnector);

	/**
	 * Removes the operand, and the appropriate connector. The adjacent logical
	 * connector with the greater nesting number will be removed. If both
	 * adjacent connectors are of same nesting number, it is undefined as to
	 * which one will be removed.
	 * @param index
	 *            the index of operand to be removed.
	 * @return the removed operand.
	 */
	public IExpressionOperand removeOperand(int index);

	/**
	 * Removes the operand, and the appropriate connector. The adjacent logical
	 * connector with the greater nesting number will be removed. If both
	 * adjacent connectors are of same nesting number, it is undefined as to
	 * which one will be removed.
	 * @param operand
	 *            the operand to be removed.
	 * @return true if the operand was found; false otherwise.
	 */
	public boolean removeOperand(IExpressionOperand operand);

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOfOperand(IExpressionOperand operand);

	/**
	 * @return the id of this expression.
	 */
	public IExpressionId getExpressionId();

	/**
	 * @return the no. of operands in the expression.
	 */
	public int getSize();

}
