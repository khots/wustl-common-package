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
	public static ISqlGenerator getInstance(EntityManager entityManagerInstance)
	{
		return new SqlGenerator(entityManagerInstance);
	}
}
