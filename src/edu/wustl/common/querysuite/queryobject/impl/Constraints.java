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
 * @created 12-Oct-2006 13.35.04 AM
 */

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IConstraintEntity;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;

public class Constraints implements IConstraints
{

	private static final long serialVersionUID = 6169601255945564445L;

	private Map<IExpressionId, IExpression> expressions = new LinkedHashMap<IExpressionId, IExpression>();

	private IJoinGraph joinGraph = new JoinGraph();

	private int currentExpressionId = 0;

	/**
	 * @param constraintEntity the constraint Entity for which the new expr is created.
	 * @return the newly created expression.
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#addExpression(edu.wustl.common.querysuite.queryobject.IConstraintEntity)
	 */
	public IExpression addExpression(IConstraintEntity constraintEntity)
	{
		IExpression expression = new Expression(constraintEntity, ++currentExpressionId);
		expressions.put(expression.getExpressionId(), expression);
		((JoinGraph) joinGraph).addIExpressionId(expression.getExpressionId());
		return expression;
	}

	/**
	 * @return an enumeration of the expressions' ids.
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getExpressionIds()
	 */
	public Enumeration<IExpressionId> getExpressionIds()
	{
		Set<IExpressionId> set = expressions.keySet();
		Vector<IExpressionId> vector = new Vector<IExpressionId>(set);
		return vector.elements();
	}

	/**
	 * @return the reference to joingraph.
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getJoinGraph()
	 */
	public IJoinGraph getJoinGraph()
	{
		return joinGraph;
	}

	/**
	 * @return the root expression of the join graph.
	 * @throws MultipleRootsException When there exists multiple roots in joingraph.
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getRootExpressionId()
	 */
	public IExpressionId getRootExpressionId() throws MultipleRootsException
	{
		return joinGraph.getRoot();
	}

	/**
	 * @param id the id of the expression to be removed.
	 * @return the removed expression.
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#removeExpressionWithId(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IExpression removeExpressionWithId(IExpressionId id)
	{
		JoinGraph theJoinGraph = (JoinGraph) joinGraph;

		List<IExpressionId> parents = theJoinGraph.getParentList(id);
		for (int i = 0; i < parents.size(); i++)
		{
			IExpression parentExpression = expressions.get(parents.get(i));
			parentExpression.removeOperand(id);
		}
		theJoinGraph.removeIExpressionId(id);
		return expressions.remove(id);
	}

	/**
	 * @param id the id (usually obtained from getExpressionIds)
	 * @return the reference to the IExpression associatied with the given IExpressionId.
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getExpression(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IExpression getExpression(IExpressionId id)
	{
		return expressions.get(id);
	}

	/**
	 * TO get the Set of all ConstraintEntites present in the Constraints object.
	 * @return Set of all Constraint Entities.
	 * @see edu.wustl.common.querysuite.queryobject.IConstraints#getConstraintEntities()
	 */
	public Set<IConstraintEntity> getConstraintEntities()
	{
		Set<IConstraintEntity> constraintEntitySet = new HashSet<IConstraintEntity>();
		Collection<IExpression> allExpressions = expressions.values();
		for (IExpression expression : allExpressions)
		{
			constraintEntitySet.add(expression.getConstraintEntity());
		}
		return constraintEntitySet;
	}

}