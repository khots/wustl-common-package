/**
 * 
 */

package edu.wustl.common.querysuite.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.util.global.Constants;

/**
 * @author prafull_kadam
 * Output tree node Class for Advance Query tree. 
 * Only SQLGenerator class will instanciate objects of this class, & It is expected that other classes should use getter methods only.
 */
public class OutputTreeDataNode
{

	/**
	 * This constant will be used in autogeneration of the id for this class. 
	 * Value of this constant indicates the maximum number of children that any parent node can have.
	 */
	private static final int CHILD_FACTOR = 10;

	private long id = 1;
	private OutputTreeDataNode parentNode;

	private List<OutputTreeDataNode> children = new ArrayList<OutputTreeDataNode>();
	IOutputEntity outputEntity;
	private int treeNo;
	private IExpressionId expressionId;
	/**
	 * The Constructor to instantiate the object of this class.
	 * @param outputEntity The reference to the output Entity.
	 * @param expressionId The expression id corresponding to this Output tree.
	 * @param treeNo The integer representing tree no.
	 */
	public OutputTreeDataNode(IOutputEntity outputEntity, IExpressionId expressionId, int treeNo)
	{
		this.outputEntity = outputEntity;
		this.expressionId = expressionId;
		this.treeNo = treeNo;
	}

	/**
	 * To get the auto generated id for the class instance. 
	 * In general the id of the class will be  equal to (parent node id) * CHILD_FACTOR + (index of this node in the parent list)
	 * Apart from that this value may differ if removeChild() method is called on parent node.  
	 * @return The long value representing autogenerated id for the class instance.
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getId()
	 */
	public long getId()
	{
		return  id;
	}

	/**
	 * To set the id
	 * @param id The id to set.
	 */
	private void setId(long id)
	{
		this.id = id ;
	}

	/**
	 * To add the child node. 
	 * @param outputEntity The output entity of the child node to be added.
	 * @param expressionId The expression id corresponding to this Output tree.
	 * @return The reference to the child node.
	 */
	public OutputTreeDataNode addChild(IOutputEntity outputEntity, IExpressionId expressionId)
	{
		OutputTreeDataNode child = new OutputTreeDataNode(outputEntity,expressionId, treeNo);
		long childId = this.getId() * CHILD_FACTOR + children.size();
		child.setId(childId);
		child.parentNode = this;
		children.add(child);

		return child;
	}

	/**
	 * @return The list all children of this node
	 */
	public List<OutputTreeDataNode> getChildren()
	{
		return children;
	}

	/**
	 * @return The reference to the output Entity associated with this node.
	 */
	public IOutputEntity getOutputEntity()
	{
		return outputEntity;
	}

	/**
	 * @return Returns the reference to the parent node, null if its root node.
	 */
	public OutputTreeDataNode getParent()
	{
		return parentNode;
	}
	
	/**
	 * @return the expressionId The expression id corresponding to this expression.
	 */
	public IExpressionId getExpressionId()
	{
		return expressionId;
	}
	
	/**
	 * @return the treeNo
	 */
	public int getTreeNo()
	{
		return treeNo;
	}

	/**
	 * @param obj the object to be compared.
	 * @return true if following attributes of both object matches:
	 * 			- treeNo
	 * 			- id
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj != null && this.getClass() == obj.getClass())
		{
			OutputTreeDataNode node = (OutputTreeDataNode) obj;
			if (this.treeNo == node.treeNo && this.id == node.getId())
			{
				return true;
			}

		}
		return false;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on Following attributes:
	 * 			- treeNo
	 * 	 		- id
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * Constants.HASH_PRIME + (int) treeNo;
		hash = hash * Constants.HASH_PRIME + (int) id;
		return hash;
	}

	/**
	 * @return String representation of object in the form: [outputEntity : parentNode]
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + id + ":" + outputEntity.toString() + "("
				+ (parentNode == null ? "-" : parentNode.getId()+"") + ")" + "]";
	}
	/**
	 * Returns a unique id for each node of each tree
	 * @return String id
	 */
	public String getUniqueNodeId()
	{
		 String uniqueNodeId = this.treeNo+"_"+id;
		 return uniqueNodeId;
	}
	
}