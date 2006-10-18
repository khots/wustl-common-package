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

public class SpecimenTreeListener implements MouseInputListener{

	 private String type;
	 
	 /**
	  * public No-Args Constructor.
	  *
	  */
	 public SpecimenTreeListener()
	 {
		 
	 }
	 
	 /**
	  * public parametrized Constructor to set the type of specimen node.
	  *
	  */
	 public SpecimenTreeListener(String nodeType)
	 {
		 type = nodeType;
	 }
	 
	 /**
     * Corresponds to an applet environment.
     */
    private AppletContext appletContext = null;
    
    /**
     * @return Returns the appletContext.
     */
    public AppletContext getAppletContext()
    {
        return appletContext;
    }
    
    /**
     * @param appletContext The appletContext to set.
     */
    public void setAppletContext(AppletContext appletContext)
    {
        this.appletContext = appletContext;
    }
    
    public void mouseClicked(MouseEvent e) 
    {
        try
        {
			// TODO Auto-generated method stub
	    	if(e.getClickCount()==2)
	        {
	    		Object object = e.getSource();
	    		JTree tree = null;
	
	    		if (object instanceof JTree)
	    		{
	    			tree = (JTree) object;
	    			
	    			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
	                     .getLastSelectedPathComponent();
	
	    			SpecimenTreeNode  treeNode = (SpecimenTreeNode) node
	                     .getUserObject();
	             
	    			//Set the values in the parent window.
	    			Applet applet = this.appletContext.getApplet(Constants.TREE_APPLET_NAME);
	    			System.out.println("applet name "+applet);
	    			//Kapil: MAC ISSUE JDK 1.3.1	    			
	    			JSObject window = JSObject.getWindow(applet);
	    			//commented as this will set in MouseClick
	    			String propertyName = applet.getParameter(Constants.PROPERTY_NAME);
	    			
	    			// if Root node selected or node is not of given type, then do nothing.
	    			if(treeNode.toString().equals(Constants.REQUEST_DETAILS) || !treeNode.getType().equalsIgnoreCase(type))
	    			{
	    				return;
	    			}
	    			String setValue = new String();
	    			//Poornima:Make the Category nodes non-clickable if child exists. Refer to Bug 1718
	    			if(treeNode.getChildNodes()==null || treeNode.getChildNodes().size()==0)
	    			{
	    				setValue="setParentWindowValue('"+propertyName+"','"+treeNode.toString()+"')";
	    			}
					else
					{
						return;
					}
	             
	    			//Kapil: MAC ISSUE JDK 1.3.1
	    			//commented as this will set in MouseClick
	    			window.eval(setValue);
	    			window.eval("closeWindow()");
	    		}
	        }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
		
	}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
	
		}
	
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
	
		}
	
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
	
		}
	
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
	
		}
	
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
	
		}
	
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
	
		}

	
}
