
package edu.wustl.common.querysuite.factory;

import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.wustl.common.querysuite.queryengine.ISqlGenerator;
import edu.wustl.common.querysuite.queryengine.impl.SqlGenerator;

/**
 * Factory to return the SqlGenerator's instance. 
 * @author deepti_shelar
 *
 */
public class SqlGeneratorFactory
{

	/**
	 * Method to create instance of class SqlGenerator. 
	 * @param entityManagerInstance The reference to EntityManager, this will be used by SQL generator to get the data related to entities, attributes, associations.
	 * @return The reference of SqlGenerator. 
	 */
	public static ISqlGenerator getInstance(EntityManager entityManagerInstance)
	{
		return new SqlGenerator(entityManagerInstance);
	}
}
