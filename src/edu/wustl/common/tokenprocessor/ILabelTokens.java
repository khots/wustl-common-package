/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.tokenprocessor;

import edu.wustl.common.exception.ApplicationException;


// TODO: Auto-generated Javadoc
/**
 * The Interface LabelTokens.
 */
/**
 * @author nitesh_marwaha
 *
 */
public interface ILabelTokens
{

	/**
	 * Gets the token value.
	 *
	 * @param object the object
	 * @param token the token
	 *
	 * @return the token value
	 */
	String getTokenValue(Object object)throws ApplicationException;
}
