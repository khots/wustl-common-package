/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.factory;

import edu.wustl.common.querysuite.queryengine.ISqlGenerator;
import edu.wustl.common.querysuite.queryengine.impl.SqlGenerator;

/**
 * Factory to return the SqlGenerator's instance. 
 * @author deepti_shelar
 *
 */
public abstract class SqlGeneratorFactory
{

	/**
	 * Method to create instance of class SqlGenerator. 
	 * @return The reference of SqlGenerator. 
	 */
	public static ISqlGenerator getInstance()
	{
		return new SqlGenerator();
	}
}
