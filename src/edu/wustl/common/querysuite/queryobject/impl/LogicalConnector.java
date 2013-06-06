/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.04.04 AM
 */

public class LogicalConnector implements ILogicalConnector
{

	private static final long serialVersionUID = 3065606993455242889L;
	private LogicalOperator logicalOperator;
	private int nestingNumber = 0;

	/**
	 * The constructor to instantiate the Logical connector object with the given logical operator.
	 * @param logicalOperator The reference to the Logical operator.
	 */
	public LogicalConnector(LogicalOperator logicalOperator)
	{
		this.logicalOperator = logicalOperator;
	}

	/**
	 * The constructor to instantiate the Logical connector object with the given logical operator & nesting number.
	 * @param logicalOperator The reference to the Logical operator.
	 * @param nestingNumber The integer value which will decide no. of parenthesis surrounded by this operator.
	 */
	public LogicalConnector(LogicalOperator logicalOperator, int nestingNumber)
	{
		this.logicalOperator = logicalOperator;
		this.nestingNumber = nestingNumber;
	}

	/**
	 * @return the reference to the Logical operator.
	 * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#getLogicalOperator()
	 */
	public LogicalOperator getLogicalOperator()
	{
		return logicalOperator;
	}

	/**
	 * @return integer value, that represents no. of parantheses sorrounding this connector.
	 * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#getNestingNumber()
	 */
	public int getNestingNumber()
	{
		return nestingNumber;
	}

	/**
	 * @param logicOperatorCode The logical operator to set.
	 * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#setLogicalOperator(edu.wustl.common.querysuite.queryobject.LogicalOperator)
	 */
	public void setLogicalOperator(LogicalOperator logicOperatorCode)
	{
		logicalOperator = logicOperatorCode;
	}

	/**
	 * To set the nesting numbet for this Logical connector.
	 * @param nestingNumber the nesting number.
	 */
	public void setNestingNumber(int nestingNumber)
	{
		this.nestingNumber = nestingNumber;

	}

	/**
	 * to increment nesting numeber by 1.
	 *
	 */
	public void incrementNestingNumber()
	{
		nestingNumber++;
	}

	/**
	 * 
	 * @return hash code value for this object.
	 * It uses logicalOperator & nestingNumber for the hash code calculation.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;

		if (logicalOperator != null)
		{
			hash = hash * Constants.HASH_PRIME + logicalOperator.hashCode();
		}
		hash = hash * Constants.HASH_PRIME + nestingNumber;

		return hash;
	}

	/**
	 * @param obj The reference of object to be compared.
	 * @return true if object specified is of same class, and locial operator & nesting number of two classes are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj != null && this.getClass() == obj.getClass())
		{
			LogicalConnector logicalConnector = (LogicalConnector) obj;
			if (this.logicalOperator != null
					&& this.logicalOperator.equals(logicalConnector.logicalOperator)
					&& this.nestingNumber == logicalConnector.nestingNumber)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return String representation for this object as Logicaloperator:nesting number.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{

		return "[" + logicalOperator + ":" + nestingNumber + "]";
	}

}