
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.00.04 AM
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;

public class JoinGraph implements IJoinGraph
{

	private List<IExpressionId> expressionIds = new ArrayList<IExpressionId>();
	private Map<IExpressionId, List<Edge>> incommingEdgeMap = new HashMap<IExpressionId, List<Edge>>();
	private Map<IExpressionId, List<Edge>> outgoingEdgeMap = new HashMap<IExpressionId, List<Edge>>();

	public JoinGraph(List<IExpressionId> expressionIds,
			Map<IExpressionId, List<Edge>> incommingEdgeMap,
			Map<IExpressionId, List<Edge>> outgoingEdgeMap)
	{
		if (expressionIds != null)
			this.expressionIds = expressionIds;
		if (incommingEdgeMap != null)
			this.incommingEdgeMap = incommingEdgeMap;
		if (outgoingEdgeMap != null)
			this.outgoingEdgeMap = outgoingEdgeMap;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#containsAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean containsAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex)
	{
		IAssociation theAssociation = getAssociation(parentExpressionIndex, childExpressionIndex);
		if (theAssociation == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IAssociation getAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex)
	{
		List<Edge> edges = outgoingEdgeMap.get(parentExpressionIndex);

		if (edges != null)
		{
			Edge edge = getEdgeFromList(edges, childExpressionIndex, false);

			if (edge != null)
				return edge.getAssociation();
		}

		return null;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#isConnected()
	 */
	public boolean isConnected()
	{
		boolean isConnected = true;

		try
		{
			IExpressionId root = getRoot();
			boolean[] visited = new boolean[expressionIds.size()];
			dfs(expressionIds.indexOf(root), visited);
			for (int i = 0; i < visited.length; i++)
			{
				if (visited[i] == false)
					return false;
			}
		}
		catch (MultipleRootsException e)
		{
			isConnected = false;
		}
		return isConnected;
	}

	/**
	 * Method to traverse Using Depth First algorithm. It marks the entry for the index in visited array as true while visiting each node.
	 * @param index An int representing index of the node to traverse from expressionIds list. 
	 * @param visited array of boolean representing status of whether each node is visited or not. true value at index i represents, that i'th node from expressions list is visited.  
	 */
	private void dfs(int index, boolean[] visited)
	{
		if (visited[index] == true)
			return;

		visited[index] = true;
		IExpressionId node = expressionIds.get(index);
		List<Edge> outgoingEdges = outgoingEdgeMap.get(node);
		if (outgoingEdges != null)
		{
			for (int i = 0; i < outgoingEdges.size(); i++)
			{
				Edge edge = outgoingEdges.get(i);
				dfs(expressionIds.indexOf(edge.getOutGoingExpression()), visited);
			}
		}
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#putAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IAssociation)
	 */
	public IAssociation putAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex, IAssociation association) throws CyclicException
	{
		IAssociation theAssociation = getAssociation(parentExpressionIndex, childExpressionIndex);
		if (theAssociation == null) // The Association does not Exist!!
		{

			// Add parent & child Expressionindexed in the Expression ids if not exists.
			if (!expressionIds.contains(parentExpressionIndex))
				expressionIds.add(parentExpressionIndex);
			if (!expressionIds.contains(childExpressionIndex))
				expressionIds.add(childExpressionIndex);

			Edge edge = new Edge(parentExpressionIndex, childExpressionIndex, association);

			// Add this Edge in outgoing Edges.
			List<Edge> outgoingEdges = outgoingEdgeMap.get(parentExpressionIndex);
			if (outgoingEdges == null)
			{
				outgoingEdges = new ArrayList<Edge>();
				outgoingEdgeMap.put(parentExpressionIndex, outgoingEdges);
			}
			outgoingEdges.add(edge);

			// Add this Edge in incomming Edges.
			List<Edge> incomingEdges = incommingEdgeMap.get(childExpressionIndex);
			if (incomingEdges == null)
			{
				incomingEdges = new ArrayList<Edge>();
				incommingEdgeMap.put(childExpressionIndex, incomingEdges);
			}
			incomingEdges.add(edge);

			if (isCyclic())
			{
				// cleaning already added data.
				outgoingEdges.remove(edge);
				incomingEdges.remove(edge);
				throw new CyclicException("Adding this Edge will form a Cycle in Graph.");
			}
		}
		else
		// The Association already Exists, so return old association & overwrite that association by new one.
		{
			Edge edge = getEdgeFromList(outgoingEdgeMap.get(parentExpressionIndex),
					parentExpressionIndex, true);
			theAssociation = edge.getAssociation();
			edge.setAssociation(association);
		}

		return theAssociation;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#removeAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean removeAssociation(IExpressionId firstExpressionIndex,
			IExpressionId secondExpressionIndex)
	{
		List<Edge> incommingEdges = incommingEdgeMap.get(secondExpressionIndex);
		List<Edge> outgoingEdges = outgoingEdgeMap.get(firstExpressionIndex);

		if (incommingEdges != null && outgoingEdges != null)
		{
			Edge incommingEdge = getEdgeFromList(incommingEdges, firstExpressionIndex, true);
			Edge outgoingEdge = getEdgeFromList(outgoingEdges, secondExpressionIndex, false);

			if (incommingEdge != null && outgoingEdge != null)
			{
				incommingEdges.remove(incommingEdge);
				outgoingEdges.remove(outgoingEdge);
				return true;
			}
		}
		return false;
	}

	/**
	 * This method will return the Root Expression id node of the Expression tree. The node having no incomming Edges will be treated as Root node. 
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getRoot()
	 */
	public IExpressionId getRoot() throws MultipleRootsException
	{
		IExpressionId root = null;

		for (int i = 0; i < expressionIds.size(); i++)
		{
			IExpressionId expression = expressionIds.get(i);
			List<Edge> incommingEdges = incommingEdgeMap.get(expression);

			if (incommingEdges == null || incommingEdges.isEmpty())
			{
				if (root != null) //Multiple root node condition.
				{
					throw new MultipleRootsException(
							"The Expression tree has multiple Root nodes!!!");
				}
				root = expression;
			}

		}

		if (root == null) // No root node Exist in tree.
		{
			throw new MultipleRootsException("The Expression tree has no Root node!!!");
		}
		return root;
	}

	/**
	 * To get the Edge for the given expressionIndex. 
	 * @param edges The List of Edges.
	 * @param expressionIndex The reference to the ExpressionId of which Edge to search.
	 * @param isIncommingEdge <code>true</code> if the expressionIndex is incomming edge, else will search it as outGoingEdge; 
	 * @return reference to Edge if exist, else return <code>null</code>
	 */
	private Edge getEdgeFromList(List<Edge> edges, IExpressionId expressionIndex,
			boolean isIncommingEdge)
	{

		for (int i = 0; i < edges.size(); i++)
		{
			Edge edge = edges.get(i);
			if (isIncommingEdge)
			{
				if (edge.getIncomingExpressionId().equals(expressionIndex))
					return edge;
			}
			else
			{
				if (edge.getOutGoingExpression().equals(expressionIndex))
					return edge;
			}
		}
		return null;
	}

	/**
	 * To check wether the JongGraph is cyclin or not.
	 * @return <code>true</code> if There exist any cycle in the Graph.
	 */
	private boolean isCyclic()
	{
		int[] ordinalNos = new int[expressionIds.size()];// ordinal numbers for graph’s visited nodes.
		boolean[] inProg = new boolean[expressionIds.size()];// Keep track if the search on a given node is currently in progress.
		k = 0;
		boolean isCyclic = true;

		for (int i = 0; i < expressionIds.size(); i++)
		{
			List<Edge> incomingList = incommingEdgeMap.get(expressionIds.get(i));
			if (incomingList != null && !incomingList.isEmpty())
			{
				isCyclic = checkCycleUsingDFS(i, ordinalNos, inProg);
				if (isCyclic)
					return true;
			}
		}
		return isCyclic;
	}

	private int k = 0; // ordinal number for the node to be visited next.

	/**
	 * DFS searching for Cycle.
	 * @param currentIndex
	 * @param ordinalNos
	 * @param inProg
	 * @return
	 */
	private boolean checkCycleUsingDFS(int currentIndex, int[] ordinalNos, boolean[] inProg)
	{
		ordinalNos[currentIndex] = k++;
		inProg[currentIndex] = true;
		List<Edge> edges = outgoingEdgeMap.get(expressionIds.get(currentIndex));
		if (edges != null)
		{
			for (int edgeCounter = 0; edgeCounter < edges.size(); edgeCounter++)
			{
				Edge edge = edges.get(edgeCounter);
				int index = expressionIds.indexOf(edge.getOutGoingExpression());
				if (inProg[index])
				{
					return true;
				}
				else if (ordinalNos[index] == 0)
				{
					boolean isCyclic = checkCycleUsingDFS(index, ordinalNos, inProg);
					if (isCyclic)
						return true;
				}
			}
		}
		return false;
	}
}
