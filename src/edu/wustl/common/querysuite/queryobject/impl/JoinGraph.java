
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.00.04 AM
 */

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
	public Graph<IExpressionId, IAssociation> joinGraph;

	public JoinGraph()
	{
		joinGraph = new Graph<IExpressionId, IAssociation>();
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#containsAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean containsAssociation(IExpressionId parentExpressionIndex, IExpressionId childExpressionIndex)
	{
		return joinGraph.containsEdge(parentExpressionIndex, childExpressionIndex);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IAssociation getAssociation(IExpressionId parentExpressionIndex, IExpressionId childExpressionIndex)
	{
		return joinGraph.getEdge(parentExpressionIndex, childExpressionIndex);
	}

	/**
	 * Checks if the graph is connected by getting the root IExpressionId
	 * The traversing is done on root and if more than one root found, the graph
	 * is considered to be disjoint and a MultipleRootsException is thrown 
	 * @return true if graph is connected; false if graph is disjoint
	 * @throws MultipleRootsException if more than one root found
	 */
	public boolean isConnected() throws MultipleRootsException
	{
		return joinGraph.isConnected();
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#putAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IAssociation)
	 */
	public IAssociation putAssociation(IExpressionId parentExpressionIndex, IExpressionId childExpressionIndex, IAssociation association)
			throws CyclicException
	{
		return joinGraph.putEdge(parentExpressionIndex, childExpressionIndex, association);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#removeAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean removeAssociation(IExpressionId firstExpressionIndex, IExpressionId secondExpressionIndex)
	{
		return joinGraph.removeEdge(firstExpressionIndex, secondExpressionIndex) != null;
	}

	/**
	 * Removes the specified id from the list of IExpressionId if one exists
	 * @param id
	 * @return true upon removing specified existing id; false otherwise
	 */
	public boolean removeIExpressionId(IExpressionId id)
	{
		return joinGraph.removeVertex(id);
	}

	/**
	 * For each element in IExpressionId list, the root node will be checked 
	 * for incoming edges for that element.The node having no incomming edges 
	 * will be treated as Root node. 
	 * @return root node of the expression
	 * @throws MultipleRootsException if more than 1 roots exists or no root exists for the expression tree
	 */
	public IExpressionId getRoot() throws MultipleRootsException
	{
		List<IExpressionId> unReachableNode = joinGraph.getUnreachableNodeList();
		
		if (unReachableNode.size()==0)
			throw new MultipleRootsException("No Root Exist for the Joing Graph!!!!");
		
		if (unReachableNode.size()!=1)
			throw new MultipleRootsException("Multiple Root Exist for the Joing Graph!!!!");
		
		return unReachableNode.get(0);
	}

	public boolean addIExpressionId(IExpressionId id)
	{
		return joinGraph.addVertex(id);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getParentList(edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public List<IExpressionId> getParentList(IExpressionId childExpressionId)
	{
		return joinGraph.getDirectPredecessorOf(childExpressionId);
	}

}