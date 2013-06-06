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
 * @created 12-Oct-2006 15.00.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.util.Graph;

public class JoinGraph implements IJoinGraph
{

	private static final long serialVersionUID = 2671567170682456142L;
	private Graph<IExpressionId, IAssociation> graph;

	/**
	 * Default constructor to instanciate class of this object. 
	 *
	 */
	public JoinGraph()
	{
		graph = new Graph<IExpressionId, IAssociation>();
	}

	/**
	 * To check wether there is an association between two Expression ids.
	 * @param parentExpressionId The parent Expression id.
	 * @param childExpressionId The child Expression id.
	 * @return  true if the graph contains an association between the specified
	 * expressionIds.
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#containsAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean containsAssociation(IExpressionId parentExpressionId,
			IExpressionId childExpressionId)
	{
		return graph.containsEdge(parentExpressionId, childExpressionId);
	}

	/**
	 * To get the association between two Expression ids.
	 * @param parentExpressionId The parent Expression id.
	 * @param childExpressionId The child Expression id.
	 * @return The association betweent the thwo Expression ids.
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IAssociation getAssociation(IExpressionId parentExpressionId,
			IExpressionId childExpressionId)
	{
		return graph.getEdge(parentExpressionId, childExpressionId);
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
	 * @param parentExpressionId The parent Expression id to be added in joingraph.
	 * @param childExpressionId The child Expression id to be added in joingraph.
	 * @param association The association between two expression ids.
	 * @return previous association for the given expressionId's which was
	 *         overwritten by this association; null if no association existed
	 *         previously.
	 * @throws CyclicException if adding this edge will cause a cycle in the graph
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#putAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IAssociation)
	 */
	public IAssociation putAssociation(IExpressionId parentExpressionId,
			IExpressionId childExpressionId, IAssociation association) throws CyclicException
	{
		return graph.putEdge(parentExpressionId, childExpressionId, association);
	}

	/**
	 * Removes the association from the graph.
	 * @param firstExpressionId The parent Expression id
	 * @param secondExpressionId The child Expression id
	 * @return true if the association between the specified expressions existed.
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#removeAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean removeAssociation(IExpressionId firstExpressionId,
			IExpressionId secondExpressionId)
	{
		return graph.removeEdge(firstExpressionId, secondExpressionId) != null;
	}

	/**
	 * Removes the specified id from the list of IExpressionId if one exists
	 * @param id The Expression id to be removed.
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
		{
			throw new MultipleRootsException("Multiple Root Exist for the Joing Graph");
		}
		return unReachableNode.get(0);
	}

	/**
	 * To add vertex in joingraph.
	 * @param expressionId  the expression to be added in join graph.
	 * @return true upon adding vertex to existing vertex list; false otherwise
	 */
	public boolean addIExpressionId(IExpressionId expressionId)
	{
		return graph.addVertex(expressionId);
	}

	/**
	 * To get the list of Parents of the given ExpressionId.
	 * @param childExpressionId the Child Expression Id reference.
	 * @return The List parent of ExpressionId for th given childExpressionId. 
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
	 * @throws MultipleRootsException if more than 1 roots exists.
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
	 * @throws MultipleRootsException if more than 1 roots exists.
	 */
	public List<IExpressionId> getPath(IExpressionId expressionId) throws MultipleRootsException
	{
		List<List<IExpressionId>> paths = graph.getReachablePaths(getRoot(), expressionId);

		if (paths.isEmpty())
		{
			return new ArrayList<IExpressionId>();
		}
		return paths.get(0);
	}

	/**
	 * To get the edge path of the  given ExpressionId from the root Expression.
	 * @param expressionId reference to ExpressionId 
	 * @return the List of paths of the given ExpressionId from root. returns empty path list if there is no path.
	 * @throws MultipleRootsException if there are multpile roots present in join graph.
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
	 */
	public List<IAssociation> getEdgePath(IExpressionId expressionId) throws MultipleRootsException
	{
		List<List<IAssociation>> paths = graph.getReachableEdgePaths(getRoot(), expressionId);

		if (paths.isEmpty())
		{
			return new ArrayList<IAssociation>();
		}
		return paths.get(0);
	}

	/**
	 * @param expressionId the expr id whose children are to be found.
	 * @return List of Vertices directly reachable from the given vertex. Returns null if vertex is not present in graph, Returns empty list if vertex has no directly reachable node.
	 */
	public List<IExpressionId> getChildrenList(IExpressionId expressionId)
	{
		return graph.getDirectSuccessorOf(expressionId);
	}
	
	/**
	 * To get the expressions having multiple parent nodes.
	 * @return the List of expression ids having multiple parent nodes.
	 */
	public List<IExpressionId> getNodesWithMultipleParents()
	{
		List<IExpressionId> nodes = new ArrayList<IExpressionId>();
		List<IExpressionId> allExpressionIds = graph.getVertices();
		for (IExpressionId expression:allExpressionIds)
		{
			if (graph.getDirectPredecessorOf(expression).size()>1)
			{
				nodes.add(expression);
			}
		}
		return nodes;
	}
	
	public List<IExpressionId> getAllRoots()
	{
		return graph.getUnreachableNodeList();
	}
	
	
}