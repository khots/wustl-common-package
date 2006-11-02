
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 13.35.04 AM
 */

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;

public class Constraints implements IConstraints
{

	private Map<IExpressionId, IExpression> expressions = new LinkedHashMap<IExpressionId, IExpression>();

	private IJoinGraph joinGraph;

	private int currentExpressionId = 0;
	/**
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#addExpression(edu.wustl.common.querysuite.queryobject.IFunctionalClass)
	 */
	public IExpression addExpression(IFunctionalClass functionalClass)
	{
		IExpression expression = new Expression(functionalClass, ++currentExpressionId);
		expressions.put(expression.getExpressionId(), expression);
		return expression;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getExpressionIds()
	 */
	public Enumeration<IExpressionId> getExpressionIds()
	{
		Set<IExpressionId> set = expressions.keySet();
		Vector<IExpressionId> vector = new Vector<IExpressionId>(set);
		return vector.elements();
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getJoinGraph()
	 */
	public IJoinGraph getJoinGraph()
	{
		return joinGraph;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getRootExpressionId()
	 */
	public IExpressionId getRootExpressionId() throws MultipleRootsException
	{
		return joinGraph.getRoot();
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#removeExpressionWithId(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IExpression removeExpressionWithId(IExpressionId id)
	{
		joinGraph.removeIExpressionId(id);
		return expressions.remove(id);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getExpression(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IExpression getExpression(IExpressionId id)
	{
		return expressions.get(id);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#setJoinGraph(edu.wustl.common.querysuite.queryobject.IJoinGraph)
	 */
	public void setJoinGraph(IJoinGraph joinGraph)
	{
		this.joinGraph = joinGraph;
	}
}