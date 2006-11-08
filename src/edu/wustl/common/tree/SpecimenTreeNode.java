package edu.wustl.common.tree;

import java.io.Serializable;

/**
 * @author ramya_nagraj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SpecimenTreeNode extends TreeNodeImpl implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * String containing the type of the specimen node.
	 */
	private String type;
	
	/**
	 * @return String containing the type of specimen node.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/*
	 * No args Constructor.
	 */
	public SpecimenTreeNode()
	{
		
	}
	
	public SpecimenTreeNode(Long identifier,String value)
	{
	    super(identifier, value);
	}
	    
	/* (non-Javadoc)
	 * @see edu.wustl.common.tree.TreeNodeImpl#toString()
	 */
	public String toString()
	{
	    return this.value;
	}

}
