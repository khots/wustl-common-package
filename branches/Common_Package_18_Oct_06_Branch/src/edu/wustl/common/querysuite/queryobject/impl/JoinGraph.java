
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.00.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.util.Graph;

public class JoinGraph implements IJoinGraph
{

	private static final long serialVersionUID = 2671567170682456142L;
	public Graph<IExpressionId, IAssociation> graph;

	public JoinGraph()
	{
		graph = new Graph<IExpressionId, IAssociation>();
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#containsAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean containsAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex)
	{
		return graph.containsEdge(parentExpressionIndex, childExpressionIndex);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IAssociation getAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex)
	{
		return graph.getEdge(parentExpressionIndex, childExpressionIndex);
	}

	/**
	 * Checks if the graph is connected by getting the root IExpressionId
	 * The traversing is done on root and if more than one root found, the graph
	 * is considered to be disjoint and a MultipleRootsException is thrown 
	 * @return true if graph is connected; false if graph is disjoint
	 */
	public boolean isConnected()
	{
		return graph.isConnected();
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#putAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IAssociation)
	 */
	public IAssociation putAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex, IAssociation association) throws CyclicException
	{
		return graph.putEdge(parentExpressionIndex, childExpressionIndex, association);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#removeAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean removeAssociation(IExpressionId firstExpressionIndex,
			IExpressionId secondExpressionIndex)
	{
		return graph.removeEdge(firstExpressionIndex, secondExpressionIndex) != null;
	}

	/**
	 * Removes the specified id from the list of IExpressionId if one exists
	 * @param id
	 * @return true upon removing specified existing id; false otherwise
	 */
	public boolean removeIExpressionId(IExpressionId id)
	{
		return graph.removeVertex(id);
	}

	/**
	 * For each element in IExpressionId list, the root node will be checked 
	 * for incoming edges for that element.The node having no incomming edges 
	 * will be treated as Root node. 
	 * @return root node of the expression, null if no root exists for the expression tree
	 * @throws MultipleRootsException if more than 1 roots exists.
	 */
	public IExpressionId getRoot() throws MultipleRootsException
	{
		List<IExpressionId> unReachableNode = graph.getUnreachableNodeList();

		if (unReachableNode.size() == 0)
		{
			return null;
			//			Prafull: instead of throwing exception it will return null.
			//			throw new MultipleRootsException("No Root Exist for the Joing Graph!!!!");
		}

		if (unReachableNode.size() != 1)
			throw new MultipleRootsException("Multiple Root Exist for the Joing Graph!!!!");

		return unReachableNode.get(0);
	}

	public boolean addIExpressionId(IExpressionId id)
	{
		return graph.addVertex(id);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getParentList(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public List<IExpressionId> getParentList(IExpressionId childExpressionId)
	{
		return graph.getDirectPredecessorOf(childExpressionId);
	}

	/**
	 * To get the path of the  given ExpressionId from the root Expression.
	 * @param expressionId reference to ExpressionId 
	 * @return the List of paths of the given ExpressionId from root. returns empty path list if there is no path.
	 */
	public List<List<IExpressionId>> getPaths(IExpressionId expressionId)
			throws MultipleRootsException
	{
		return graph.getReachablePaths(getRoot(), expressionId);
	}

	/**
	 * To get the first path of the  given ExpressionId from the root Expression.
	 * @param expressionId regerence to ExpressionId 
	 * @return the List of vertices representing path of the given ExpressionId from root. returns empty path list if there is no path.
	 */
	public List<IExpressionId> getPath(IExpressionId expressionId) throws MultipleRootsException
	{
		List<List<IExpressionId>> paths = graph.getReachablePaths(getRoot(), expressionId);

		if (paths.isEmpty())
			return new ArrayList<IExpressionId>();

		return paths.get(0);
	}

	/**
	 * To get the edge path of the  given ExpressionId from the root Expression.
	 * @param expressionId reference to ExpressionId 
	 * @return the List of paths of the given ExpressionId from root. returns empty path list if there is no path.
	 */
	public List<List<IAssociation>> getEdgesPaths(IExpressionId expressionId)
			throws MultipleRootsException
	{
		return graph.getReachableEdgePaths(getRoot(), expressionId);
	}

	/**
	 * To get the first edge path of the  given ExpressionId from the root Expression.
	 * @param expressionId regerence to ExpressionId 
	 * @return the List of Associations representing path of the given ExpressionId from root. returns empty path list if there is no path.
	 * @throws MultipleRootsException if there are multpile roots present in join graph.
	 * @throws IllegalArgumentException when the expressionId is not in the graph.
	 */
	public List<IAssociation> getEdgePath(IExpressionId expressionId) throws MultipleRootsException
	{
		List<List<IAssociation>> paths = graph.getReachableEdgePaths(getRoot(), expressionId);

		if (paths.isEmpty())
			return new ArrayList<IAssociation>();

		return paths.get(0);
	}

	public List<IExpressionId> getChildrenList(IExpressionId expressionId)
	{
		return graph.getDirectSuccessorOf(expressionId);
	}
}