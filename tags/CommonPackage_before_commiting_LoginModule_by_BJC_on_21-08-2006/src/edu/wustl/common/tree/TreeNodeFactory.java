/*
 * Created on Aug 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.tree;

import edu.wustl.common.util.global.Constants;


/**
 * @author gautam_shetty
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeNodeFactory
{
    public static TreeNode getTreeNode(int treeType,TreeNode root)
    {
        TreeNode treeNode = null;
        switch(treeType)
        {
            case Constants.TISSUE_SITE_TREE_ID:
                CDETreeNode rootNode = (CDETreeNode) root;
            	String cdeName = null;
            	if (rootNode != null)
            	{
            	    cdeName = rootNode.getCdeName();
            	}
            	
                treeNode = new CDETreeNode(null, cdeName);
            	break;
            case Constants.STORAGE_CONTAINER_TREE_ID:
                treeNode = new StorageContainerTreeNode(new Long(0),null,Constants.CATISSUE_CORE);
            	break;
            case Constants.QUERY_RESULTS_TREE_ID:
            	treeNode = new TreeNodeImpl(new Long(0),Constants.ROOT);
                break;
        }
        
        return treeNode;
    }
}
