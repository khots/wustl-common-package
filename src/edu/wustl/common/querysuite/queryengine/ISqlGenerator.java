/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * 
 */

package edu.wustl.common.querysuite.queryengine;

import java.util.Map;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
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
	 * @throws MultipleRootsException When there are multpile roots present in a graph.
	 * @throws SqlException When there is error in the passed IQuery object.
	 */
	String generateSQL(IQuery query) throws MultipleRootsException, SqlException;
	
	/**
	 * This method will return map of DE attributes verses & their column names present in the select part of the SQL. 
	 * These DE attributes will be attributes of the each node present in the Output tree.
	 * @return map of DE attributes verses & their column names present in the select part of the SQL.
	 * @deprecated This method will not required any more.
	 */
	Map<Long, Map<AttributeInterface,String>> getColumnMap();
}
