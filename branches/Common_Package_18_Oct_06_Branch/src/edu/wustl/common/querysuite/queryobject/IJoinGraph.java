
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;

/**
 * A rooted, directed acyclic graph with expressions as vertices, and
 * associations as edges.<br>
 * The graph will always contain all the expressions' ids (obtained from
 * {@link edu.wustl.common.querysuite.queryobject.IConstraints}) as vertices. The vertices
 * will be added to/removed from the joingraph as and when expressions are added
 * to/removed from
 * {@link edu.wustl.common.querysuite.queryobject.IConstraints}. The methods
 * in joingraph can only add/remove associations among the vertices. <br>
 * If v1 and v2 are two vertices, the direction will be v1->v2 if v2 is a
 * subexpression of v1. This user of the API will generally build the joingraph
 * based on metadata, and in cases of ambiguity, the user will help forming this
 * graph. <br>
 * This graph basically determines the join conditions in the query. e.g. for
 * each edge (v1, v2) between two vertices v1 and v2, there will be a join
 * between the functional classes of the expressions denoted by v1 and v2; and
 * the join condition is determined by the info in the
 * {@link edu.wustl.common.querysuite.queryobject.IAssociation}.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:02 PM
 */
public interface IJoinGraph extends IBaseQueryObject
{

	/**
	 * @param parentExpressionId
	 * @param childExpressionId
	 */
	public IAssociation getAssociation(IExpressionId parentExpressionId,
			IExpressionId childExpressionId);

	/**
	 * @param parentExpressionId
	 * @param childExpressionId
	 * @param association
	 * @return previous association for the given expressionId's which was
	 *         overwritten by this association; null if no association existed
	 *         previously.
	 * @throws {@link edu.wustl.common.querysuite.exceptions.CyclicException}
	 *             if adding this edge will cause a cycle in the graph
	 */
	public IAssociation putAssociation(IExpressionId parentExpressionId,
			IExpressionId childExpressionId, IAssociation association) throws CyclicException;

	/**
	 * Removes the association from the graph.
	 * @return true if the association between the specified expressions
	 *         existed.
	 * @param parentExpressionId
	 * @param childExpressionId
	 */
	public boolean removeAssociation(IExpressionId parentExpressionId,
			IExpressionId childExpressionId);

	/**
	 * indicates if the graph is a connected graph. for building queries
	 * (DCQL/CQL) the graph should be connected. So this can be used by UI and
	 * querybuilding code for validation purposes.
	 * @throws MultipleRootsException 
	 */
	public boolean isConnected() throws MultipleRootsException;

	/**
	 * True if the graph contains an association between the specified
	 * expressionIds.
	 * @param parentExpressionId
	 * @param childExpressionId
	 */
	public boolean containsAssociation(IExpressionId parentExpressionId,
			IExpressionId childExpressionId);

	public IExpressionId getRoot() throws MultipleRootsException;

	/**
	 * To get the list of Parents of the given ExpressionId.
	 * @param childExpressionId the Child Expression Id reference.
	 * @return The List parent of ExpressionId for th given childExpressionId. 
	 */
	public List<IExpressionId> getParentList(IExpressionId childExpressionId);
	
	/**
	 * To get the list of children of the given ExpressionId.
	 * @param expressionId the expr id whose children are to be found.
	 * @return children of given expressionId. 
	 */
	public List<IExpressionId> getChildrenList(IExpressionId expressionId);
}
