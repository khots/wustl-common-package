
/**
 * <p>Title: SpecimenTreeBizLogic Class>
 * <p>Description:	SpecimenTreeBizLogic contains the bizlogic required to display Specimen hierarchy in tree form for ordering system module.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Ramya Nagraj
 * @version 1.00
 */

package edu.wustl.common.bizlogic;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.tree.SpecimenTreeNode;
import edu.wustl.common.tree.TreeDataInterface;
import edu.wustl.common.util.dbManager.DAOException;

/**
 * SpecimenTreeBizLogic contains the bizlogic required to display Specimen hierarchy in tree form for ordering system module.
 * @author ramya_nagraj
 */

public class SpecimenTreeBizLogic extends DefaultBizLogic implements TreeDataInterface
{

	public Vector getTreeViewData() throws DAOException 
	{
		//Retrieve the list from DB later.
		
		//Set the 1st Parent Node.
		 SpecimenTreeNode specimenRootTreeNode1 = new SpecimenTreeNode();
		 specimenRootTreeNode1.setValue("ABC DNA");
		 specimenRootTreeNode1.setType("DNA");
		 
		 //Set the 2nd Parent node.
		 SpecimenTreeNode specimenRootTreeNode2 = new SpecimenTreeNode();
		 specimenRootTreeNode2.setValue("PQR DNA");
		 specimenRootTreeNode2.setType("DNA");
		 
		 //Set the child of 1st root node.
		 SpecimenTreeNode child_rootTreeNode1 = new SpecimenTreeNode();
		 child_rootTreeNode1.setParentNode(specimenRootTreeNode1);
		 child_rootTreeNode1.setValue("XYZ RNA");
		 child_rootTreeNode1.setType("RNA");
		 
		 Vector childNodes = new Vector();
		 childNodes.add(child_rootTreeNode1);
		 
		 specimenRootTreeNode1.setChildNodes(childNodes);
		 
		 //Set all the root nodes in the vector.
		 Vector allRootNodes = new Vector();
		 allRootNodes.add(specimenRootTreeNode1);
		 allRootNodes.add(specimenRootTreeNode2);
		 
		return allRootNodes;
	}

	public Vector getTreeViewData(SessionDataBean sessionData, Map map, List list) throws DAOException, ClassNotFoundException {
	
		return null;
	}

}
