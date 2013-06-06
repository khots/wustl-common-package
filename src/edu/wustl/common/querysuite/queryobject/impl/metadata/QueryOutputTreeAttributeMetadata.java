/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * 
 */

package edu.wustl.common.querysuite.queryobject.impl.metadata;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;

/**
 * @author prafull_kadam
 * Class to store metadata of the output tree attributes. 
 * This will store the attribute of an output entity & its corresponding column name in the temporary table created while executing Suite Advance query. 
 */
public class QueryOutputTreeAttributeMetadata
{

	/**
	 * Reference to the attribute for which the metadata is to be stored.
	 */
	private AttributeInterface attribute;
	/**
	 * The name of the column in the temporary table for the given attribute.
	 */
	private String columnName;

	/**
	 * Constructor to instanciate this object.
	 * @param attribute The reference to the DE attribute.
	 * @param columnName The name of the column for the attribute passed in the temporary table.
	 */
	public QueryOutputTreeAttributeMetadata(AttributeInterface attribute, String columnName)
	{
		this.attribute = attribute;
		this.columnName = columnName;
	}

	/**
	 * To get the attribute whose metadata is stored in this class.
	 * @return the attribute reference
	 */
	public AttributeInterface getAttribute()
	{
		return attribute;
	}

	/**
	 * TO get the table column name of the attribute.
	 * @return the columnName
	 */
	public String getColumnName()
	{
		return columnName;
	}
}
