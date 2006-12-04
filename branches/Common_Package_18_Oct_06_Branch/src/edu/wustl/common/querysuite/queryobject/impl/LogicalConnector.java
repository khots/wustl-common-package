
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
	private int nestingNumber=0;

	public LogicalConnector(LogicalOperator logicalOperator)
	{
		this.logicalOperator = logicalOperator;
	}

    public LogicalConnector(LogicalOperator logicalOperator, int nestingNumber)
    {
        this.logicalOperator = logicalOperator;
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		
		if (logicalOperator != null)
			hash = hash*Constants.HASH_PRIME + logicalOperator.hashCode();
		hash = hash*Constants.HASH_PRIME + nestingNumber;
		
		return hash;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj != null && this.getClass()==obj.getClass())
		{
			LogicalConnector logicalConnector = (LogicalConnector) obj;
			if (this.logicalOperator!=null && this.logicalOperator.equals(logicalConnector.logicalOperator)
					&& this.nestingNumber == logicalConnector.nestingNumber)
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
		
		return "[" + logicalOperator + ":" + nestingNumber + "]";
	}

}