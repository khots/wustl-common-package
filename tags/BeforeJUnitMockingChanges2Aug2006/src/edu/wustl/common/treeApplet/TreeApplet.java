/**
 * <p>Title: TreeApplet Class>
 * <p>Description:	TreeApplet builds the applet for the tree representation 
 * of query result view.</p>
 * Copyright: Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.treeApplet;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import edu.wustl.common.util.global.Constants;
import edu.wustl.common.tree.GenerateTree;

/**
 * TreeApplet builds the applet for the tree representation
 * of query result view.
 * @author gautam_shetty
 */
public class TreeApplet extends JApplet
{

    /**
     * Initializes the applet.
     */
    public void init()
    {
        ObjectInputStream in = null;
        try
        {
            URL codeBase = getCodeBase();
            String protocol = codeBase.getProtocol();
            String host = codeBase.getHost();
            int port = codeBase.getPort();
            
            String pageOf = this.getParameter(Constants.PAGEOF);
            String storageContainerType = null,propertyName = null, cdeName = null;
            int treeType = Constants.TISSUE_SITE_TREE_ID;
            
            //Sri: Added for selecting node in the storage tree
            Long selectedNode = new Long(0);
            String position = null;
            if (pageOf.equals(Constants.PAGEOF_STORAGE_LOCATION)) 
            {
                storageContainerType = this.getParameter(Constants.STORAGE_CONTAINER_TYPE);
                treeType = Constants.STORAGE_CONTAINER_TREE_ID;                
            }
            else if(pageOf.equals(Constants.PAGEOF_SPECIMEN))
            	treeType = Constants.STORAGE_CONTAINER_TREE_ID;
            else if (pageOf.equals(Constants.PAGEOF_QUERY_RESULTS))
                treeType = Constants.QUERY_RESULTS_TREE_ID;
            else if (pageOf.equals(Constants.PAGEOF_TISSUE_SITE))
            {
                propertyName = this.getParameter(Constants.PROPERTY_NAME);
                cdeName = this.getParameter(Constants.CDE_NAME);
            }
            
            // If storage container tree, take care of positions and parent container
            // ID edit boxes.
            if(treeType == Constants.STORAGE_CONTAINER_TREE_ID)
            {
	            String selectedNodeStr = this.getParameter(Constants.STORAGE_CONTAINER_TO_BE_SELECTED);
	            
	            if((null != selectedNodeStr) && (false == "".equals(selectedNodeStr)) 
	                    && ("null".equals(selectedNodeStr) == false))
	            {
	            	try
					{
	            		selectedNode = Long.valueOf(selectedNodeStr);
	            	}
	            	catch(Exception ex)
					{
	            		//do nothing since default value of selectedNode is 0
	            		ex.printStackTrace();
					}
	            }
	            position = this.getParameter(Constants.STORAGE_CONTAINER_POSITION);
            }
            
            String applicationPath = codeBase.getPath();
            // modify applicationPath String ...
			
            if(applicationPath.indexOf('/',1)!=-1){ //indexOf returns -1 if no match found
				String newApplicationPath=null;
				newApplicationPath = applicationPath.substring(0,applicationPath.indexOf('/',1)+1);
				applicationPath=newApplicationPath;
            }
            
            String urlSuffix = applicationPath+Constants.TREE_DATA_ACTION+"?"+Constants.PAGEOF+"="+URLEncoder.encode(pageOf, "UTF-8");
            if (pageOf.equals(Constants.PAGEOF_TISSUE_SITE) == true)
            {
                urlSuffix = urlSuffix + "&"+Constants.PROPERTY_NAME+"="+URLEncoder.encode(propertyName, "UTF-8")+"&"+Constants.CDE_NAME+"="+URLEncoder.encode(cdeName, "UTF-8");
            }
            System.out.println("URL......................................"+urlSuffix);
            URL dataURL = new URL(protocol, host, port, urlSuffix);
            
            //Establish connection with the TreeDataAction and get the JTree object.
            URLConnection connection = dataURL.openConnection();
            connection.setUseCaches(false);
            
            in = new ObjectInputStream(connection.getInputStream());
            System.out.println("After Connection......................................");
            JTree tree = new JTree();
            List disableSpecimenIdsList = null;
            Vector treeDataVector = null;
            if (pageOf.equals(Constants.PAGEOF_STORAGE_LOCATION) || pageOf.equals(Constants.PAGEOF_SPECIMEN)
                    || pageOf.equals(Constants.PAGEOF_TISSUE_SITE))
            {
                Vector dataVector = (Vector) in.readObject();
                GenerateTree generateTree = new GenerateTree();
                tree = generateTree.createTree(dataVector, treeType);
            }
            else
            {
                treeDataVector = (Vector) in.readObject();
                disableSpecimenIdsList=(List)in.readObject();
                GenerateTree generateTree = new GenerateTree();
                tree = generateTree.createTree(treeDataVector, treeType);
            }
            
            Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout());
            
            if (pageOf.equals(Constants.PAGEOF_QUERY_RESULTS))
            {
            	//Preparing radio buttons for configuring different views.
                JPanel radioButtonPanel = new JPanel(new GridLayout(2, 1));
                
                JRadioButton spreadsheetViewRadioButton = new JRadioButton(
                        Constants.SPREADSHEET_VIEW);
                spreadsheetViewRadioButton
                        .setActionCommand(Constants.SPREADSHEET_VIEW);
                spreadsheetViewRadioButton.setSelected(true);
                spreadsheetViewRadioButton.setPreferredSize(new Dimension(80, 40));
                
                JRadioButton individualViewRadioButton = new JRadioButton(
                        Constants.OBJECT_VIEW);
                individualViewRadioButton.setActionCommand(Constants.OBJECT_VIEW);
                individualViewRadioButton.setPreferredSize(new Dimension(80, 40));
                
                ButtonGroup radioButtonGroup = new ButtonGroup();
                radioButtonGroup.add(spreadsheetViewRadioButton);
                radioButtonGroup.add(individualViewRadioButton);
                
                radioButtonPanel.add(spreadsheetViewRadioButton);
                radioButtonPanel.add(individualViewRadioButton);
                //Radio buttons finish.
                
                //Put the radioButton panel on the Applet.
                contentPane.add(radioButtonPanel,BorderLayout.PAGE_START);
                
            	// Add listeners for the tree.
                QueryResultsTreeListener nodeSelectionListener = new QueryResultsTreeListener(
                        this.getCodeBase(), this.getAppletContext());
	            tree.addTreeSelectionListener(nodeSelectionListener);
	            nodeSelectionListener.setDisableSpecimenIds(disableSpecimenIdsList);
	            
                //Add listeners for the radio buttons.
	            spreadsheetViewRadioButton.addActionListener(nodeSelectionListener);
                individualViewRadioButton.addActionListener(nodeSelectionListener);
            }
            
            JPanel treePanel = new JPanel(new GridLayout(1, 0));
            JScrollPane scroll = new JScrollPane(tree);
            treePanel.add(scroll);
            treePanel.setOpaque(true);
            treePanel.setVisible(true);
            
            if (pageOf.equals(Constants.PAGEOF_TISSUE_SITE))
            {
                // changed for double click event
            	CDETreeListener tissueSiteListener = new CDETreeListener();
	            tissueSiteListener.setAppletContext(this.getAppletContext());
	            tree.addMouseListener(tissueSiteListener);
            }
            else if (pageOf.equals(Constants.PAGEOF_STORAGE_LOCATION) ||  (pageOf.equals(Constants.PAGEOF_SPECIMEN)) )
            {
                StorageLocationViewListener viewListener 
        			= new StorageLocationViewListener(this.getCodeBase(), this.getAppletContext());
		        viewListener.setStorageContainerType(storageContainerType);
		        viewListener.setPageOf(pageOf);
		        tree.addTreeSelectionListener(viewListener);
            } 
            
            //Put the tree panel on the Applet.
            contentPane.add(treePanel, BorderLayout.CENTER);
            
            //Sri: Pass the position of the container to the next level
            // This is used to auto select the node
            if(false == selectedNode.equals(new Long(0)))
            {
                urlSuffix = applicationPath+Constants.SHOW_STORAGE_CONTAINER_GRID_VIEW_ACTION
	            + "?" + Constants.SYSTEM_IDENTIFIER + "=" + selectedNode.toString()
	            + "&" + Constants.STORAGE_CONTAINER_TYPE + "=" + storageContainerType
	            + "&" + Constants.STORAGE_CONTAINER_POSITION + "=" + position
	            + "&" + Constants.PAGEOF + "=" + pageOf;
                dataURL = new URL(protocol, host, port, urlSuffix);
                
	            this.getAppletContext().showDocument(dataURL,Constants.DATA_VIEW_FRAME);
            }
        }
        catch (MalformedURLException malExp)
        {
            malExp.printStackTrace();
        }
        catch (IOException ioExp)
        {
            ioExp.printStackTrace();
        }
        catch (ClassNotFoundException classNotExp)
        {
            classNotExp.printStackTrace();
        }
        catch (Exception excp)
        {
            excp.printStackTrace();
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ioExp)
            {
                ioExp.printStackTrace();
            }
        }
    }
}