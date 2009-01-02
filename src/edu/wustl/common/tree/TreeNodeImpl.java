/*
 * Created on Apr 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gautam_shetty
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeNodeImpl implements Serializable, TreeNode
{

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -6991311769537160227L;

	/**
	 * identifier for the node.
	 */
	private Long identifier;

	/**
	 * Name of the node.
	 */
	private String value;

	/**
	 * Parent node of this node.
	 */
	private TreeNode parentNode;

	/**
	 * List of child nodes.
	 */
	private List childNodes = new ArrayList();

	/**
	 * Default Constructor.
	 */
	public TreeNodeImpl()
	{
		super();
	}

	/**
	 * Default Constructor.
	 * @param identifier identifier for the node.
	 * @param value Name of the node..
	 */
	public TreeNodeImpl(Long identifier, String value)
	{
		this.identifier = identifier;
		this.value = value;
	}

	/**
	 * @return Returns the identifier.
	 */
	public Long getIdentifier()
	{
		return identifier;
	}

	/**
	 * @param identifier The identifier to set.
	 */
	public void setIdentifier(Long identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return Returns the parentNode.
	 */
	public TreeNode getParentNode()
	{
		return parentNode;
	}

	/**
	 * @param parentNode The parentNode to set.
	 */
	public void setParentNode(TreeNode parentNode)
	{
		this.parentNode = parentNode;
	}

	/**
	 * @return Returns the childNodes.
	 */
	public List getChildNodes()
	{
		return childNodes;
	}

	/**
	 * @param childNodes The childNodes to set.
	 */
	public void setChildNodes(List childNodes)
	{
		this.childNodes = childNodes;
	}

	/**
	 * overrides java.lang.Object.equals.
	 * @param obj Object.
	 * @return true if equal else false.
	 */
	public boolean equals(Object obj)
	{
		boolean flag = false;
		if (obj instanceof TreeNodeImpl)
		{
			TreeNodeImpl treeNodeImpl = (TreeNodeImpl) obj;
			if (this.getIdentifier().equals(treeNodeImpl.getIdentifier()))
			{
				flag = true;
			}
		}

		return flag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	/**
	 * overrides java.lang.Object.hashCode.
	 * @return hashCode.
	 */
	public int hashCode()
	{
		int hashCode = 0;
		if (getIdentifier() != null)
		{
			hashCode += getIdentifier().hashCode();
		}
		return hashCode;
	}

	/**
	 * To display Tooltip for the Tree node.
	 * By default it will return value, override this method if need different tool tip.
	 * @return The tooltip to display.
	 */
	String getToolTip()
	{
		return this.value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/**
	 *  overrides java.lang.Object.toString.
	 *  @return value
	 */
	public String toString()
	{
		String value = this.value;
		if (this.identifier.longValue() != 0)
		{
			value = this.value + " : " + this.identifier;
		}

		return value;
	}
}