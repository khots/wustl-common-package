
package edu.wustl.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 31-Oct-2006 12.46.04 PM
 */

public class Graph<V, E>
{

	private List<V> vertexList = new ArrayList<V>();
	private Map<V, List<Edge>> incommingEdgeMap = new HashMap<V, List<Edge>>();
	private Map<V, List<Edge>> outgoingEdgeMap = new HashMap<V, List<Edge>>();
	private int k = 0; // Ordinal number for the node to be visited next.

	public Graph()
	{
	}

	// Internal Edge class to create the edge object
	class Edge
	{
		private V sourceVertex;
		private V targetVertex;
		private E edgeObj;

		public Edge(V sourceVertex, V targetVertex, E edgeobj)
		{
			this.sourceVertex = sourceVertex;
			this.targetVertex = targetVertex;
			this.edgeObj = edgeobj;
		}

		public V getSourceVertex()
		{
			return sourceVertex;
		}

		public V getTargetVertex()
		{
			return targetVertex;
		}

		public E getEdgeObject()
		{
			return edgeObj;
		}
		
		public void setEdgeObject(E edgeObj)
		{
			this.edgeObj = edgeObj;
		}
	}

	/**
	 * Get the edge from the list of edges, if one exists between source vertex and target vertex.
	 * @param sourceVertex
	 * @param targetVertex
	 * @return edge object if it exists; null otherwise
	 */
	public E getEdge(V sourceVertex, V targetVertex)
	{
		List<Edge> edges = outgoingEdgeMap.get(sourceVertex);

		if (edges != null)
		{
			Edge edge =  getEdgeFromList(edges, targetVertex, false);

			if (edge != null)
				return edge.getEdgeObject();
		}
		return null;
	}

	/**
	 * This method checks whether an edge lies between the source vertex and target vertex.
	 * @param sourceVertex
	 * @param targetVertex
	 * @return true if the edge exists; false otherwise
	 */
	public boolean containsEdge(V sourceVertex, V targetVertex)
	{
		return getEdge(sourceVertex, targetVertex) != null;
	}

	/**
	 * Remove the edge if it exists in the list of edges.
	 * @param sourceVertex
	 * @param targetVertex
	 * @return removed edge if edge object is not null; null otherwise
	 */
	public E removeEdge(V sourceVertex, V targetVertex)
	{
		List<Edge> incommingEdges = incommingEdgeMap.get(targetVertex);
		List<Edge> outgoingEdges = outgoingEdgeMap.get(sourceVertex);

		if (incommingEdges != null && outgoingEdges != null)
		{
			Edge edge = (Edge) getEdgeFromList(incommingEdges, sourceVertex, true);

			if (edge != null)
			{
				incommingEdges.remove(edge);
				outgoingEdges.remove(edge);
				return edge.getEdgeObject();
			}
		}
		return null;
	}

	/**
	 * Put the edge into the list of edges if it does not exist. If the edge exists
	 * return the old edge and replace it with a new edge.
	 * @param sourceVertex
	 * @param targetVertex
	 * @param edge
	 * @return the old edge if it exists; null otherwise
	 * @throws CyclicException if the graph is realized to be cyclic
	 */
	public E putEdge(V sourceVertex, V targetVertex, E edge) throws CyclicException
	{
		Edge theEdge = (Edge) getEdge(sourceVertex, targetVertex);

		if (theEdge == null) // The edge does not exist!!
		{
			// Add source vertex & target vertex in the vertices list.
			if (!vertexList.contains(sourceVertex))
				vertexList.add(sourceVertex);
			if (!vertexList.contains(targetVertex))
				vertexList.add(targetVertex);

			Edge newEdge = new Edge(sourceVertex, targetVertex, edge);

			// Add this Edge in outgoing Edges.
			List<Edge> outgoingEdges = outgoingEdgeMap.get(sourceVertex);
			if (outgoingEdges == null)
			{
				outgoingEdges = new ArrayList<Edge>();
				outgoingEdgeMap.put(sourceVertex, outgoingEdges);
			}
			outgoingEdges.add((Edge) newEdge);

			// Add this Edge in incomming Edges.
			List<Edge> incomingEdges = incommingEdgeMap.get(targetVertex);
			if (incomingEdges == null)
			{
				incomingEdges = new ArrayList<Edge>();
				incommingEdgeMap.put(targetVertex, incomingEdges);
			}
			incomingEdges.add((Edge) newEdge);
			
			if (isCyclic())
			{
				// Clean already added data.
				outgoingEdges.remove(newEdge);
				incomingEdges.remove(newEdge);
				throw new CyclicException("Adding this Edge will form a Cycle in Graph.");
			}
		}
		else
		// The edge already exists, so replace the existing edge and return it.
		{
			E oldEdge = theEdge.getEdgeObject();
			theEdge.setEdgeObject(edge);
			return oldEdge;
		}
		return null;
	}

	/**
	 * Checks if the graph is connected
	 * @return true if graph is connected; false otherwise
	 */
	public boolean isConnected()
	{
		boolean isConnected = true;

		try
		{
			V root = getRoot();
			boolean[] visited = new boolean[vertexList.size()];
			dfs(vertexList.indexOf(root), visited);
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
	 * This method will return the Root node of the tree. 
	 * The node having no incomming edges will be treated as Root node. 
	 * @return root node of the expression
	 * @throws MultipleRootsException if more than 2 roots exists or no root exists for the expression tree
	 */
	public V getRoot() throws MultipleRootsException
	{
		V root = null;

		for (int i = 0; i < vertexList.size(); i++)
		{
			V expression = vertexList.get(i);
			List<Edge> incommingEdges = incommingEdgeMap.get(expression);

			if (incommingEdges == null || incommingEdges.isEmpty())
			{
				if (root != null) //Multiple root node condition.
				{
					throw new MultipleRootsException("The Expression tree has multiple Root nodes!!!");
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
	 * Removes the specified vertex from the list of vertices if one exists
	 * @param vertex
	 * @return true upon removing specified existing vertex; false otherwise
	 */
	public boolean removeVertex(V vertex)
	{
		boolean flag = vertexList.remove(vertex);
		if (flag)
		{
			incommingEdgeMap.remove(vertex);
			outgoingEdgeMap.remove(vertex);
		}
		return flag;
	}
	
	/**
	 * Check if the Graph is cyclic. Visits each node and checks
	 * simultaneously if the node has been visited before. Stores this
	 * information in a boolean array for checking.
	 * @return true if any cycle exists between elements in the Graph
	 */
	private boolean isCyclic()
	{
		int[] ordinalNos = new int[vertexList.size()];// ordinal numbers for graph’s visited nodes.
		boolean[] inProg = new boolean[vertexList.size()];// Keep track if the search on a given node is currently in progress.
		k = 0;
		boolean isCyclic = true;

		for (int i = 0; i < vertexList.size(); i++)
		{
			List<Edge> incomingList = incommingEdgeMap.get(vertexList.get(i));
			if (incomingList != null && !incomingList.isEmpty())
			{
				isCyclic = checkCycleUsingDFS(i, ordinalNos, inProg);
				if (isCyclic)
					return true;
			}
		}
		return isCyclic;
	}

	/**
	 * DFS searching for cycle present in the graph
	 * @param currentIndex
	 * @param ordinalNos
	 * @param inProg
	 * @return true if a node has already been visited; false otherwise
	 */
	private boolean checkCycleUsingDFS(int currentIndex, int[] ordinalNos, boolean[] inProg)
	{
		ordinalNos[currentIndex] = k++;
		inProg[currentIndex] = true;
		List<Edge> edges = outgoingEdgeMap.get(vertexList.get(currentIndex));
		if (edges != null)
		{
			for (int edgeCounter = 0; edgeCounter < edges.size(); edgeCounter++)
			{
				Edge edge = edges.get(edgeCounter);
				int index = vertexList.indexOf(edge.getTargetVertex());
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

	/**
	 * Method to traverse using Depth First algorithm. 
	 * It marks the entry for the index in visited array as true while visiting each node.
	 * @param index
	 * @param visited
	 */
	private void dfs(int index, boolean[] visited)
	{
		if (visited[index] == true)
			return;

		visited[index] = true;
		V node = vertexList.get(index);
		List<Edge> outgoingEdges = outgoingEdgeMap.get(node);
		if (outgoingEdges != null)
		{
			for (int i = 0; i < outgoingEdges.size(); i++)
			{
				Edge edge = outgoingEdges.get(i);
				dfs(vertexList.indexOf(edge.getTargetVertex()), visited);
			}
		}
	}

	/**
	 * Get the edge for the specified target vertex.
	 * @param edges
	 * @param targetVertex
	 * @return the edge if it exists; null otherwise
	 */
	private Edge getEdgeFromList(List<Edge> edges, V targetVertex, boolean isIncommingEdge)
	{
		for (int i = 0; i < edges.size(); i++)
		{
			Edge edge = edges.get(i);
			if (isIncommingEdge)
			{
				if (edge.getSourceVertex().equals(targetVertex))
					return edge;
			}
			else
			{
				if (edge.getTargetVertex().equals(targetVertex))
					return edge;
			}
		}
		return null;
	}

}