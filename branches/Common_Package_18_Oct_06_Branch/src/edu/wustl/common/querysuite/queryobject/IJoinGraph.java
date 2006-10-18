
package edu.wustl.common.querysuite.queryobject;

import edu.wustl.common.querysuite.exceptions.MultipleRootsException;

/**
 * directed acyclic graph with
 * vertex = IExpression
 * edge = IAssociation ...
 * 
 * the graph will always contain all the IExpressions' expressionIndexes in the
 * IExpressionList as vertices. methods can only add/remove associations among the
 * vertices.
 * 
 * vertices will be added/removed as and when expressions are added/removed in
 * IExpressionList.
 * 
 * if v1 and v2 are two vertices, the direction will be v1->v2 if v2 is a
 * subexpression of v1.
 * 
 * this graph can be determined from metadata, and in cases of ambiguity, the user
 * will help forming this graph.
 * 
 * this graph basically decides the <association> tags in (D)CQL.
 * For each edge E(v1, v2) between two vertices v1 and v2, there will be (in the
 * CQL/DCQL) a tag
 * <association to class in funcClass of v2>
 * within the v1 element. The association may be intra- or inter-model, and this
 * decides whether the tag will be <association> or <foreign-association>,
 * respectively.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:02 PM
 */
public interface IJoinGraph
{

	/**
	 * @param parentExpressionIndex
	 * @param childExpressionIndex
	 * 
	 */
	public IAssociation getAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex);

	/**
	 * returns previous association for the given expressionIndexes which was
	 * overwritten by this association.
	 * null if no association existed previously
	 * 
	 * throws a CyclicException if adding this edge will cause a cycle in the graph
	 * @param parentExpressionIndex
	 * @param childExpressionIndex
	 * @param association
	 * 
	 */
	public IAssociation putAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex, IAssociation association);

	/**
	 * returns true if the association between the specified expressions existed.
	 * return value may not be needed.
	 * 
	 * removes the association from the graph
	 * @param firstExpressionIndex
	 * @param secondExpressionIndex
	 * 
	 */
	public boolean removeAssociation(IExpressionId firstExpressionIndex,
			IExpressionId secondExpressionIndex);

	/**
	 * indicates if the graph is a connected graph.
	 * for building queries (DCQL/CQL) the graph should be connected. So this can be
	 * used by UI and querybuilding code for validation purposes.
	 */
	public boolean isConnected();

	/**
	 * true if the graph contains an association between the specified
	 * expressionIndexes
	 * @param parentExpressionIndex
	 * @param childExpressionIndex
	 * 
	 */
	public boolean containsAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex);

	public IExpressionId getRoot() throws MultipleRootsException;
}
