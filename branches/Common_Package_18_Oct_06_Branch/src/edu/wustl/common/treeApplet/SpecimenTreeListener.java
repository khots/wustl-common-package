package edu.wustl.common.treeApplet;

import edu.wustl.common.tree.SpecimenTreeNode;
import edu.wustl.common.util.global.Constants;


/**
 * @author ramya_nagraj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SpecimenTreeListener extends AppletTreeListener
{

	 private String type;
	 
	 private String specimenClass;

	/**
	  * public No-Args Constructor.
	  *
	  */
	 public SpecimenTreeListener()
	 {
		 super();
	 }
	 
	 /**
	  * public parametrized Constructor to set the type of specimen node.
	  *
	  */
	 public SpecimenTreeListener(String nodeType,String nodeClass)
	 {
		 type = nodeType;
		 specimenClass = nodeClass;
	 }
	 

	public void displayClickedSpecimenNode() 
	{
		SpecimenTreeNode treeNode = (SpecimenTreeNode) node
        .getUserObject();
    	
		//If Root node selected then do nothing
		if(treeNode.toString().equals(Constants.SPECIMEN_TREE_ROOT_NAME))
		{
			//return;
		}
		//TODO Remove the uncommented code later
		//If selected node is not of given type and class, then do nothing.
		/*if(!treeNode.getType().equalsIgnoreCase(type) && !treeNode.getSpecimenClass().equalsIgnoreCase(specimenClass))
		{
			return;
		}*/
		
		//If selected node is of given type and class, then set its value in dropdown and the dropdown is not disabled.
		if(treeNode.getType().equalsIgnoreCase(type) && treeNode.getSpecimenClass().equalsIgnoreCase(specimenClass))
		{
			setValue="setParentWindowValue('"+propertyName+"','"+treeNode.toString()+"')";
		}
		/*else
		{
			return;
		}*/
		
	}

}