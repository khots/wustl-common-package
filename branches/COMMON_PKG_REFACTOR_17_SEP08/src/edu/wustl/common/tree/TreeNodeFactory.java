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

	public static TreeNode getTreeNode(int treeType, TreeNode root)
	{
		TreeNode treeNode = null;
		switch (treeType)
		{
			case Constants.TISSUE_SITE_TREE_ID :
				CDETreeNode rootNode = (CDETreeNode) root;
				String cdeName = null;
				if (rootNode != null)
				{
					cdeName = rootNode.getCdeName();
				}

				treeNode = new CDETreeNode(null, cdeName);
				break;
			case Constants.STORAGE_CONTAINER_TREE_ID :
				treeNode = new StorageContainerTreeNode(Long.valueOf(0), null, Constants.CATISSUE_CORE);
				break;
			case Constants.SPECIMEN_TREE_ID :
				treeNode = new SpecimenTreeNode(null, Constants.SPECIMEN_TREE_ROOT_NAME);
				break;
			case Constants.EXPERIMETN_TREE_ID :
				treeNode = new ExperimentTreeNode(Long.valueOf(0), "My Experiments");
				((ExperimentTreeNode) treeNode).setExperimentGroup(true);
				break;

		}

		return treeNode;
	}
}
