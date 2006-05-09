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

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * GenerateTree generates tree for the storage structure.
 * @author gautam_shetty
 */
public class GenerateTree
{
    /**
     * Creates and returns the JTree from the vector of data nodes passed.
     * @param dataVector the data vector.
     * @param treeType the type of tree.
     * @return the JTree from the vector of data nodes passed.
     */
    public JTree createTree(Vector dataVector, int treeType)
    {
        TreeNode rootName = (TreeNode)dataVector.get(0);
        //Get the root node.
        TreeNode root1 = TreeNodeFactory.getTreeNode(treeType, rootName);
        TreeNodeImpl rootNode = (TreeNodeImpl) root1;
        rootNode.setChildNodes(dataVector);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);
        
        //Create the hierarchy under the root node.
        createHierarchy(root, dataVector);
        
        JTree tree = new JTree(root);
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
