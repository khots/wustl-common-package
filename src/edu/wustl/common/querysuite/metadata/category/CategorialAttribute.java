package edu.wustl.common.querysuite.metadata.category;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;

/**
 * @version 1.0
 * @created 28-Dec-2006 2:01:13 AM
 */
public class CategorialAttribute {

	private long id;
	private long deCategoryAttributeId;
	private long deSourceClassAttributeId;
	public CategorialClass categorialEntity;

	public CategorialAttribute(){

	}

	public void finalize() throws Throwable {

	}

	public AttributeInterface getCategoryAttribute(){
		return null;
	}

	public AttributeInterface getSourceClassAttribute(){
		return null;
	}

}