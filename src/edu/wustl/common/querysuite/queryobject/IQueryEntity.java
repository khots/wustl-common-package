
package edu.wustl.common.querysuite.queryobject;

import edu.common.dynamicextensions.domaininterface.EntityInterface;

/**
 * anything that implements/extends this interface will finally be
 * represented/stored as an Entity in DE
 * @author prafull_kadam
 */
public interface IQueryEntity extends IBaseQueryObject
{

	/**
	 * 
	 * @return The Dynamic Extension Entity reference corresponding to the QueryEntity.
	 */
	EntityInterface getDynamicExtensionsEntity();

	/**
	 * @return true if the entity is a category; false if it is a class
	 */
	boolean isCategory();

}
