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
public final class TreeNodeFactory
{

	/**
	 * private constructor.
	 */
	private TreeNodeFactory()
	{

	}
	/**
	 * gets Tree Node.
	 * @param treeType tree Type
	 * @param root root
	 * @return Tree Node.

	 */
	public static TreeNode getTreeNode(int treeType, TreeNode root)
	{
		TreeNode treeNode = null;
		if(Constants.TISSUE_SITE_TREE_ID == treeType)
		{
			CDETreeNode rootNode = (CDETreeNode) root;
			String cdeName = null;
			if (rootNode != null)
			{
				cdeName = rootNode.getCdeName();
			}

			treeNode = new CDETreeNode(null, cdeName);
		}
		else
		{
			treeNode = getNode(treeType);
		}
		return treeNode;
	}
	/**
	 * @param treeType tree Type.
	 * @return treeNode
	 */
	private static TreeNode getNode(int treeType)
	{
		int type = treeType;
		TreeNode treeNode = null;
		if(Constants.STORAGE_CONTAINER_TREE_ID == type)
		{
			treeNode = new StorageContainerTreeNode(Long.valueOf(0), null,
					Constants.CATISSUE_CORE);
		}
		else
		{
			if(Constants.SPECIMEN_TREE_ID == type)
			{
				treeNode = new SpecimenTreeNode(null, Constants.SPECIMEN_TREE_ROOT_NAME);
			}
			else if(Constants.EXPERIMETN_TREE_ID == type)
			{
				treeNode = new ExperimentTreeNode(Long.valueOf(0), "My Experiments");
				((ExperimentTreeNode) treeNode).setExperimentGroup(true);
			}
		}
		return treeNode;
	}
}
