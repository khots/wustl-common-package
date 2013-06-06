/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryobject;

/**
 * Represents a logical connector (AND / OR). The nesting represents the number
 * of parentheses (depth of parentheses) around the logic portion (AND or OR) of
 * the connector.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:02 PM
 */
public interface ILogicalConnector extends IBaseQueryObject
{

	/**
	 * To get the Logical operator associated with this object. 
	 * @return the reference to the Logical operator.
	 */
	LogicalOperator getLogicalOperator();

	/**
	 * To set the Logical operator.
	 * @param logicalOperator The logical operator to set. 
	 */
	void setLogicalOperator(LogicalOperator logicalOperator);

	/**
	 * denotes no. of parantheses around this operator
	 * @return integer value, that represents no. of parantheses sorrounding this connector.
	 */
	int getNestingNumber();

}
