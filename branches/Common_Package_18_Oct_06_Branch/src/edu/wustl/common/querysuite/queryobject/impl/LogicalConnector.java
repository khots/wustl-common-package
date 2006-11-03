
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.04.04 AM
 */

public class LogicalConnector implements ILogicalConnector
{

	private LogicalOperator logicalOperator;
	private int nestingNumber;

	public LogicalConnector(LogicalOperator logicalOperator, int nestingNumber)
	{
		this.logicalOperator = logicalOperator;
		this.nestingNumber = nestingNumber;
	}

	/**
	 * Constructor to create instance of the LogicalConnector class, with the specified nestingNumber.
	 * @param nestingNumber
	 */
	public LogicalConnector(int nestingNumber)
	{
		this.nestingNumber = nestingNumber;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#getLogicalOperator()
	 */
	public LogicalOperator getLogicalOperator()
	{
		return logicalOperator;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#getNestingNumber()
	 */
	public int getNestingNumber()
	{
		return nestingNumber;
	}

	/**
	 * @param logicOperatorCode
	 * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#setLogicalOperator(edu.wustl.common.querysuite.queryobject.LogicalOperator)
	 */
	public void setLogicalOperator(LogicalOperator logicOperatorCode)
	{
		logicalOperator = logicOperatorCode;
	}

	public void setNestingNumber(int nestingNumber)
	{
		this.nestingNumber = nestingNumber;
		
	}
	
	public void incrementNestingNumber()
	{
		nestingNumber++;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof LogicalConnector)
		{
			LogicalConnector logicalConnector = (LogicalConnector) obj;
			if (this.logicalOperator.equals(logicalConnector.logicalOperator)
					&& this.nestingNumber == nestingNumber)
				return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + logicalOperator + "]";
	}

}