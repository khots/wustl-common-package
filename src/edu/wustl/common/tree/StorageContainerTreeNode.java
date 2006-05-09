/*
 * Created on Jul 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.tree;

import java.io.Serializable;

/**
 * @author gautam_shetty
 *
 * TODO To change the template for this generated storageContainerType comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StorageContainerTreeNode extends TreeNodeImpl implements Serializable
{
    private static final long serialVersionUID = 1234567890L;
    
    /**
     * Type of storage container.
     */
    private String type;
    
    /**
     * Default constructor. 
     */
    public StorageContainerTreeNode()
    {
    }
    
    /**
     * Parameterized constructor. 
     */
    public StorageContainerTreeNode(Long identifier, String value, String type)
    {
        super(identifier, value);
        this.type = type;
    }
    
    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * @param type The type to set.
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String nodeName = type + " : " + identifier;
        return nodeName;
    }
 }