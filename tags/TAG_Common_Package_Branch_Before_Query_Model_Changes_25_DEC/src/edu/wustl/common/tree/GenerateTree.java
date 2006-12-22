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
import java.util.Iterator;
import java.util.Vector;

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
	
	public JTree createTree(Vector dataVector, int treeType)
    {
		JTree tree = createTree(dataVector, treeType, false);
		return tree;
    }
	
    /**
     * Creates and returns the JTree from the vector of data nodes passed.
     * @param dataVector the data vector.
     * @param treeType the type of tree.
     * @return the JTree from the vector of data nodes passed.
     */
    public JTree createTree(Vector dataVector, int treeType,boolean isJXTree)
    {
        TreeNode rootName = null;
        if (dataVector != null && (dataVector.isEmpty()==false))
        {
            rootName = (TreeNode)dataVector.get(0);
        }
        
        //Get the root node.
        TreeNode root1 = TreeNodeFactory.getTreeNode(treeType, rootName);
        TreeNodeImpl rootNode = (TreeNodeImpl) root1;
        rootNode.setChildNodes(dataVector);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);
        
        //Create the hierarchy under the root node.
        createHierarchy(root, dataVector);
        JTree tree;
        if(isJXTree)
        {
        	tree = new JXTree(root);
        }else
        {
        	tree = new JTree(root){
			public String getToolTipText(MouseEvent e) {
					String tip = "";
					TreePath path = getPathForLocation(e.getX(), e.getY());
					if (path != null) 
					{
						Object treeNode = path.getLastPathComponent();
						if (treeNode instanceof DefaultMutableTreeNode)
						{
							TreeNodeImpl userObject = (TreeNodeImpl)((DefaultMutableTreeNode)treeNode).getUserObject();
							tip = userObject.getToolTip();
						}
					}
					return tip;
				}
        	};
        	ToolTipManager.sharedInstance().registerComponent(tree);
        }
        return tree;
    }
    
    /**
     * Creates and returns the JTree from the vector of data nodes passed.
     * @param dataVector the data vector.
     * @param treeType the type of tree.
     * @return the JTree from the vector of data nodes passed.
     */
    public JTree createTree(TreeNodeImpl rootNode, boolean isJXTree)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);
        
        //Create the hierarchy under the root node.
        createHierarchy(root, rootNode.getChildNodes());
        JTree tree;
        if(isJXTree)
        {
            tree = new JXTree(root);
        }else
        {
            tree = new JTree(root){
            public String getToolTipText(MouseEvent e) {
                    String tip = "";
                    TreePath path = getPathForLocation(e.getX(), e.getY());
                    if (path != null) 
                    {
                        Object treeNode = path.getLastPathComponent();
                        if (treeNode instanceof DefaultMutableTreeNode)
                        {
                            TreeNodeImpl userObject = (TreeNodeImpl)((DefaultMutableTreeNode)treeNode).getUserObject();
                            tip = userObject.getToolTip();
                        }
                    }
                    return tip;
                }
            };
            ToolTipManager.sharedInstance().registerComponent(tree);
        }
        return tree;
    }
    
    /**
     * Creates the hierarchy of nodes under the parent node with the child nodes passed.
     * @param parentNode the parent node.
     * @param childNodes the child nodes.
     */
    private void createHierarchy(DefaultMutableTreeNode parentNode, Vector childNodes)
    {
        Iterator iterator = childNodes.iterator();
        while (iterator.hasNext())
        {
            TreeNodeImpl childNode = (TreeNodeImpl) iterator.next();
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode);
            parentNode.add(childTreeNode);
            
            createHierarchy(childTreeNode, childNode.getChildNodes());
        }
    }
}
