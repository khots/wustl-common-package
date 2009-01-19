/**
 * <p>Title: GenerateTree Class>
 * <p>Description:	GenerateTree generates tree for the storage structure.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 21, 2005
 */

package edu.wustl.common.tree;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;

/**
 * GenerateTree generates tree for the storage structure.
 * @author gautam_shetty
 */
public class GenerateTree
{

	/**
	 * Specify list.
	 */
	private transient List list;

	/**
	 * Specify container Name.
	 */
	private transient String containerName;

	/**
	 * Default consructor.
	 */
	public GenerateTree()
	{
		super();
	}

	/**
	 * parameterised consructor.
	 * @param containerName container Name to set.
	 */
	public GenerateTree(String containerName)
	{
		this.containerName = containerName;
	}

	/**
	 * Creates and returns the JTree from the vector of data nodes passed.
	 * @param dataVector the data vector.
	 * @param treeType the type of tree.
	 * @param isJXTree is JXTree.
	 * @return the JTree from the vector of data nodes passed.
	 */
	public JTree createTree(List dataVector, int treeType, boolean isJXTree)
	{
		TreeNode rootName = null;
		JTree jtree = null;
		if (dataVector != null && !dataVector.isEmpty())
		{
			rootName = (TreeNode) dataVector.get(0);
			TreeNode root1 = TreeNodeFactory.getTreeNode(treeType, rootName);
			TreeNodeImpl rootNode = (TreeNodeImpl) root1;
			rootNode.setChildNodes(dataVector);
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);
			jtree= getTree(dataVector, isJXTree, root);
		}
		return jtree;
	}

	/**
	 * Creates and returns the JTree.
	 * @param dataVector dataVector the data vector.
	 * @param isJXTree is JXTree.
	 * @param root root node
	 * @return the JTree from the vector of data nodes passed.
	 */
	private JTree getTree(List dataVector, boolean isJXTree, DefaultMutableTreeNode root)
	{
		//Create the hierarchy under the root node.
		createHierarchy(root, dataVector);
		JTree tree;
		if (isJXTree)
		{
			tree = new JXTree(root);
		}
		else
		{
			tree = getToolTipTextUsingAnonymousClass(root);
			ToolTipManager.sharedInstance().registerComponent(tree);
		}
		return tree;
	}

	/**
	 * This method gets Tool Tip Text Using Anonymous Class.
	 * @param root root node.
	 * @return Tool Tip Text.
	 */
	private JTree getToolTipTextUsingAnonymousClass(DefaultMutableTreeNode root)
	{
		JTree tree = new JTree(root)
		{

			/**
			 * serial Version Unique ID.
			 */
			private static final long serialVersionUID = -5117917390858206892L;

			public String getToolTipText(MouseEvent event)
			{
				String tip = "";
				TreePath path = getPathForLocation(event.getX(), event.getY());
				if (path != null)
				{
					Object treeNode = path.getLastPathComponent();
					if (treeNode instanceof DefaultMutableTreeNode)
					{
						TreeNodeImpl userObject = (TreeNodeImpl)
						((DefaultMutableTreeNode) treeNode)
								.getUserObject();
						tip = userObject.getToolTip();
					}
				}
				return tip;
			}
		};
		return tree;
	}

	/**
	 * Creates and returns the JTree from the vector of data nodes passed.
	 * @param dataVector the data vector.
	 * @param treeType the type of tree.
	 * @param tempList List in which id is set
	 * @return the JTree from the vector of data nodes passed.
	 */

	public JTree createTree(List dataVector, int treeType, List tempList)
	{
		TreeNode rootName = null;
		JTree tree = null;
		if (dataVector != null && !dataVector.isEmpty())
		{
			rootName = (TreeNode) dataVector.get(0);
			//Get the root node.
			TreeNode root1 = TreeNodeFactory.getTreeNode(treeType, rootName);
			TreeNodeImpl rootNode = (TreeNodeImpl) root1;
			rootNode.setChildNodes(dataVector);
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);
			createHierarchy(root, dataVector);
			tree = getToolTipTextUsingAnonymousClass(root);
			if (list != null)
			{
				tree.setSelectionPath((TreePath) list.get(0));
				if (tempList != null)
				{
					tempList.add(list.get(1));
				}
			}
			ToolTipManager.sharedInstance().registerComponent(tree);
		}
		return tree;
	}

	/**
	 * Creates and returns the JTree from the data nodes passed.
	 * @param dataVector the data vector.
	 * @param treeType type of tree.
	 * @return Return the JTree from the data nodes passed.
	 */
	public JTree createTree(List dataVector, int treeType)
	{
		return createTree(dataVector, treeType, null);
	}

	/**
	 * Creates and returns the JTree from the list of data nodes passed.
	 * @param rootNode the TreeNodeImpl object.
	 * @param isJXTree is JXTree.
	 * @return the JTree from the data nodes passed.
	 */
	public JTree createTree(TreeNodeImpl rootNode, boolean isJXTree)
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);

		//Create the hierarchy under the root node.
		createHierarchy(root, rootNode.getChildNodes());
		return getTree(rootNode.getChildNodes(), isJXTree, root);
	}

	/**
	 * Creates the hierarchy of nodes under the parent node with the child nodes passed.
	 * @param parentNode the parent node.
	 * @param childNodes the child nodes.
	 */
	private void createHierarchy(DefaultMutableTreeNode parentNode, List childNodes)
	{
		Iterator iterator = childNodes.iterator();
		while (iterator.hasNext())
		{
			TreeNodeImpl childNode = (TreeNodeImpl) iterator.next();
			DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode);
			parentNode.add(childTreeNode);
			if (childNode.getValue() != null && containerName != null
					&& childNode.getValue().equalsIgnoreCase(containerName.trim()))
			{
				TreePath treePath = new TreePath(childTreeNode.getPath());
				list = new ArrayList();
				list.add(treePath);
				list.add(childNode.getIdentifier());

			}

			createHierarchy(childTreeNode, childNode.getChildNodes());
		}
	}

}
