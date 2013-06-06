/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * 
 */

package edu.wustl.common.querysuite.queryobject.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IOutputTreeNode;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.querysuite.queryobject.impl.Rule;
import edu.wustl.common.util.logger.Logger;

/**
 * @author prafull_kadam
 * This class will contain Utility methods for Query objects.
 */
public class QueryObjectProcessor
{

	// Following attributes will be used by replaceMultipleParent method. So that they need not to pass for each method called by that routine.
	/**
	 * holds constraints object of the Query that is currently being processed.
	 */
	private IConstraints constraints;   
	
	/**
	 * holds joinGraph object of the Query that is currently being processed
	 */
	private JoinGraph joinGraph;
	
	/**
	 * contains the expression ids of the expressions having more than one parent.
	 */
	private List<IExpressionId> idsToProcess;

	/**
	 * Default constructor, made it protected to keep its implemenation as singlton class.
	 *
	 */
	protected QueryObjectProcessor()
	{
	}

	/**
	 * if there are any nodes with multiple parents, then it converts Expression graph to the tree. And returns true if processed any such nodes. 
	 * @param constraints The reference to constraints.
	 * @return true if there are any such node present in constraints.
	 */
	public static boolean replaceMultipleParents(IConstraints constraints)
	{
		return new QueryObjectProcessor().replaceMultipleParent(constraints);
	}


	/**
	 * if there are any nodes with multiple parents, then it converts Expression graph to the tree. 
	 * @param constraints The reference to constraints.
	 * @return true if there are any such node present in constraints.
	 */
	private boolean replaceMultipleParent(IConstraints constraints)
	{
		boolean isAnyNodeProcessed = false;
		this.constraints = constraints;
		this.joinGraph = (JoinGraph) constraints.getJoinGraph();
		idsToProcess = joinGraph.getNodesWithMultipleParents();
		try
		{
			for (IExpressionId expressionId : idsToProcess)
			{
				replaceMultipleParent(expressionId);
				isAnyNodeProcessed = true;
			}
		}
		catch (CyclicException exp)
		{
			throw new RuntimeException("Unable to Process object, Exception:" + exp.getMessage());
		}

		return isAnyNodeProcessed;
	}

	/**
	 * Creates another node for the expression having multiple parent. It will also create heirarcy below that node.
	 * @param expressionId The Expression id having multiple panrent.
	 * @throws CyclicException when adding an Edge in graph causes cycle in the graph.
	 */
	private void replaceMultipleParent(IExpressionId expressionId) throws CyclicException
	{
		IExpression expression = constraints.getExpression(expressionId);
		joinGraph = (JoinGraph) constraints.getJoinGraph();
		List<IExpressionId> parents = joinGraph.getParentList(expressionId);
		for (int index = 1; index < parents.size(); index++) // iterating on all parent expression ids.
		{
			IExpressionId parentExpressionId = parents.get(index);
			IExpression parentExpression = constraints.getExpression(parentExpressionId);
			int childIndex = parentExpression.indexOfOperand(expressionId);

			IExpression newExpression = constraints.addExpression(expression.getConstraintEntity()); // creating new expression which will be copy of the given expression id.
			newExpression.setInView(expression.isInView());
			IExpressionId newExpressionId = newExpression.getExpressionId();
			parentExpression.setOperand(childIndex, newExpressionId); // pointing the parent expression to the new expression.

			// changing associations.
			IAssociation association = joinGraph.getAssociation(parentExpressionId, expressionId);
			joinGraph.removeAssociation(parentExpressionId, expressionId);
			joinGraph.putAssociation(parentExpressionId, newExpressionId, association);

			// copying all expression info to new expression, including child expression heirarchy.
			copy(expression, newExpression);
		}
	}

	/**
	 * To copy the expression data to new expression. It will copy all rules/expressions/Logical connectors to new expression, except the expressions having multiple parents.
	 * @param fromExpression the expression to be copied.
	 * @param toExpression The new empty expression.
	 * @throws CyclicException when adding an Edge in graph causes cycle in the graph.
	 */
	private void copy(IExpression fromExpression, IExpression toExpression) throws CyclicException
	{
		int numberOfOperands = fromExpression.numberOfOperands();
		for (int index = 0; index < numberOfOperands; index++)
		{

			IExpressionOperand operand = fromExpression.getOperand(index);
			if (operand instanceof IExpressionId)
			{
				IExpressionId fromExpressionId = (IExpressionId) operand;
				IExpression oldExpression = constraints.getExpression(fromExpressionId);
				IAssociation association = joinGraph.getAssociation(fromExpression
						.getExpressionId(), fromExpressionId);
				if (idsToProcess.contains(operand))
				{
					// this node also have multiple parent. So just adding this operand in new expression's operand list & updating joingraph.
					toExpression.addOperand(operand);
					joinGraph.putAssociation(toExpression.getExpressionId(), fromExpressionId,
							association);
					// this will be handled seperately in method replaceMultipleParent.
				}
				else
				{
					// Create new Expression
					IExpression newExpression = constraints.addExpression(oldExpression
							.getConstraintEntity());
					newExpression.setInView(oldExpression.isInView());
					IExpressionId newExpressionId = newExpression.getExpressionId();
					toExpression.addOperand(newExpressionId);
					joinGraph.putAssociation(toExpression.getExpressionId(), newExpressionId,
							association);
					copy(oldExpression, newExpression);
				}
			}
			else if (operand instanceof IRule)
			{
				// create copy of rule & adds it to the operand list of the new expression.
				IRule fromRule = (IRule) operand;
				IRule toRule = ((Rule) fromRule).getCopy();
				toExpression.addOperand(toRule);
			}

			// setting logical connector.
			if (index != 0)
			{
				ILogicalConnector logicalConnector = fromExpression.getLogicalConnector(index - 1,
						index);
				toExpression.setLogicalConnector(index - 1, index, logicalConnector);
			}
		}

	}

	/**
	 * Method to create deep copy of the object.
	 * @param obj The object to be copied.
	 * @return The Object reference representing deep copy of the given object.
	 */
	public static Object getObjectCopy(Object obj)
	{
		long startTime = System.currentTimeMillis();
		Object copy = null;
		try
		{
			// Write the object out to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			out.flush();
			out.close();

			// Make an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			copy = in.readObject();
		}
		catch (IOException e)
		{
			Logger.out.error(e.getMessage(), e);
			copy = null;
		}
		catch (ClassNotFoundException cnfe)
		{
			Logger.out.error(cnfe.getMessage(), cnfe);
			copy = null;
		}
		Logger.out.debug("Time taken to copy Object:" + (System.currentTimeMillis() - startTime));
		return copy;
	}

	/**
	 * To get map of all Children nodes along with their ids under given output tree node.
	 * @param root The root noe of the output tree.
	 * @return map of all Children nodes along with their ids under given output tree node.
	 */
	public static Map<Long, IOutputTreeNode> getAllChildrenNodes(IOutputTreeNode root)
	{
		Map<Long, IOutputTreeNode> map = new HashMap<Long, IOutputTreeNode>();
		map.put(root.getId(), root);
		List<IOutputTreeNode> children = root.getChildren();
		for (IOutputTreeNode childNode : children)
		{
			map.putAll(getAllChildrenNodes(childNode));
		}
		return map;
	}

	/**
	 * To get map of all Children nodes along with their ids under given output tree node.
	 * @param root The root noe of the output tree.
	 * @return map of all Children nodes along with their ids under given output tree node.
	 */
	public static Map<Long, OutputTreeDataNode> getAllChildrenNodes(OutputTreeDataNode root)
	{
		Map<Long, OutputTreeDataNode> map = new HashMap<Long, OutputTreeDataNode>();
		map.put(root.getId(), root);
		List<OutputTreeDataNode> children = root.getChildren();
		for (OutputTreeDataNode childNode : children)
		{
			map.putAll(getAllChildrenNodes(childNode));
		}
		return map;
	}
	/**
	 * To get map of all Children nodes along with their ids under given output tree node.
	 * @param root The root noe of the output tree.
	 * @param map of all Children nodes along with their ids under given output tree node.
	 */
	private static void addAllChildrenNodes(OutputTreeDataNode root,Map<String, OutputTreeDataNode> map)
	{
		map.put(root.getUniqueNodeId(), root);
		List<OutputTreeDataNode> children = root.getChildren();
		for (OutputTreeDataNode childNode : children)
		{
			addAllChildrenNodes(childNode,map);
		}
	}	
	/**
	 * It returns all the nodes present all tress in results. 
	 * @param keys set of trees
	 * @return Map of uniqueNodeId and tree node
	 */
	public static Map<String, OutputTreeDataNode> getAllChildrenNodes(Set<OutputTreeDataNode> keys)
	{
		Map<String, OutputTreeDataNode> map = new HashMap<String, OutputTreeDataNode>();
		for(OutputTreeDataNode root:keys)
		{
			addAllChildrenNodes(root,map);
		}
		return map;
	}
	
	/**
	 * It returns all the nodes present all tress in results. 
	 * @param keys set of trees
	 * @return Map of uniqueNodeId and tree node
	 */
	public static Map<String, OutputTreeDataNode> getAllChildrenNodes(List<OutputTreeDataNode> keys)
	{
		Map<String, OutputTreeDataNode> map = new HashMap<String, OutputTreeDataNode>();
		for(OutputTreeDataNode root:keys)
		{
			addAllChildrenNodes(root,map);
		}
		return map;
	}
}