/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

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

}
