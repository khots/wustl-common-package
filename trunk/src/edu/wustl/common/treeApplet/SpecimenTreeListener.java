package edu.wustl.common.treeApplet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.event.MouseInputListener;
import javax.swing.tree.DefaultMutableTreeNode;

import netscape.javascript.JSObject;
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
    	
		//If Root node selected or node is not of given type, then do nothing.
		if(treeNode.toString().equals(Constants.SPECIMEN_TREE_ROOT_NAME) || !(treeNode.getType().equalsIgnoreCase(type)) && (treeNode.getSpecimenClass().equalsIgnoreCase(specimenClass)))
		{
			return;
		}	
		if(treeNode.getChildNodes()==null || treeNode.getChildNodes().size()==0)
		{
			setValue="setParentWindowValue('"+propertyName+"','"+treeNode.toString()+"')";
		}
		else
		{
			return;
		}
		
	}

}
