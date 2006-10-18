
package edu.wustl.common.querysuite.queryobject;

/**
 * represents a collection of, possibly paranthesized, operands.
 * takes IFunctionalClass in the constructor
 * 
 * An IExpression belongs to a IFunctionalClass; and constraints on another
 * associated class willl be present as a subexpression on the associated
 * IFunctionalClass.
 * Conversely, if an expression has a subexpression, there must an association in
 * the join graph (see IJoinGraph) from the parent expression to the
 * subexpression
 * 
 * Note: "subexpression" refers to an operand that is an IExpression. The
 * functional class of the subexpression will generally be different from the
 * functional class of this expression (exception is when a class is associated to
 * itself, e.g. Specimen). Subexpression is implied by the IExpressionIndex of the
 * subexpression in the IExpressionList.
 * @version 1.0
 * @updated 11-Oct-2006 02:56:12 PM
 */
public interface IExpression
{

	/**
	 * @param i
	 * 
	 */
	public IExpressionOperand getOperand(int i);

	/**
	 * the functionalClass of IRule is constructor param to impl class.
	 */
	public IFunctionalClass getFunctionalClass();

	/**
	 * @param i
	 * 
	 */
	public void setOperand(int i);

	/**
	 * an operator is indexed by {left operand, right oeprand}
	 * i.e. if left index is "i", value of right index must be "i+1"
	 * 
	 * this is just to make the method intuitively clearer...
	 * @param leftOperandIndex
	 * @param rightOperandIndex
	 * 
	 */
	public ILogicalConnector getLogicalConnector(int leftOperandIndex, int rightOperandIndex)
			throws Exception;

	/**
	 * see getLogicalConnector
	 * @param leftOperandIndex
	 * @param rightOperandIndex
	 * @param logicalConnector
	 * 
	 */
	public void setLogicalConnector(int leftOperandIndex, int rightOperandIndex,
			ILogicalConnector logicalConnector) throws Exception;

	/**
	 * increments nesting num of all the logical connectors in the expression between
	 * the specified operands' indexes, both inclusive.
	 * @param leftOperandIndex
	 * @param rightOperandIndex
	 * 
	 */
	public void addParantheses(int leftOperandIndex, int rightOperandIndex);

	/**
	 * just calls addParantheses(0, size-1)
	 */
	public void addParantheses();

	/**
	 * @param operand
	 * 
	 */
	public IExpressionOperand addOperand(IExpressionOperand operand);

	/**
	 * @param logicalConnector
	 * @param operand
	 * 
	 */
	public void addOperand(ILogicalConnector logicalConnector, IExpressionOperand operand);

	/**
	 * returns true if operand was found;
	 * may need to throw exception if
	 * A and (B or C)
	 * is called with
	 * removeOperandAndPrecedingConnector (B)
	 * @param operand
	 * 
	 */
	//public boolean removeOperandAndPrecedingConnector(IExpressionOperand operand) throws Exception;
	/**
	 * returns true if operand was found;
	 * may need to throw exception if
	 * (A and B) or C
	 * is called with
	 * removeOperandAndFollowingConnector (B)
	 * @param operand
	 * 
	 */
	//public boolean removeOperandAndFollowingConnector(IExpressionOperand operand) throws Exception;
	public boolean removeOperand(IExpressionOperand operand);

	public IExpressionId getExpressionId();

	public int getSize();

}
