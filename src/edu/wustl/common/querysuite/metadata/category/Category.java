package edu.wustl.common.querysuite.metadata.category;

import edu.common.dynamicextensions.domaininterface.EntityInterface;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 */
public class Category {

	private long id;
	private long deEntityId;
	public CategorialClass rootClass;
	public Category subCategories;

	
	public CategorialClass getRootClass()
	{
		return rootClass;
	}

	
	public Category getSubCategories()
	{
		return subCategories;
	}

	public Category(){

	}

	public void finalize() throws Throwable {

	}

	public EntityInterface getCategory(){
		return null;
	}
	
	public Category clone() {
		// TODO by Chandu
		return null;
	}

}