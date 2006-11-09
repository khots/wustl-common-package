/**
 * 
 */

package edu.wustl.common.querysuite.queryengine;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IQuery;

/**
 * To Generate SQL for the given Query Object.
 * @author prafull_kadam
 *
 */
public interface ISqlGenerator
{

	/**
	 * Generates SQL for the given Query Object.
	 * @param query The Reference to Query Object.
	 * @return the String representing SQL for the given Query object.
	 * @throws MultipleRootsException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public String generateSQL(IQuery query) throws MultipleRootsException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException;
}
