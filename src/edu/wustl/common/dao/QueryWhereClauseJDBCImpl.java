package edu.wustl.common.dao;

import edu.wustl.common.util.global.Constants;

public class QueryWhereClauseJDBCImpl extends QueryWhereClauseImpl
{

	public boolean isConditionSatisfied()
	{
				
		boolean isConditionSatisfied = false;
		if ((isWhereColumnName())
                && (isWhereColumnCondition())
                && (isWhereColumnValue()))
        {
			isConditionSatisfied = true;
        }
		
		return isConditionSatisfied;
	}

	private boolean isWhereColumnValue() {
		
		return whereColumnValue != null && whereColumnName.length == whereColumnValue.length;
	}
	
	
	public String jdbcQueryWhereClause(String sourceObjectName) {
		
		StringBuffer queryStrBuff = new StringBuffer();
		
		if (joinCondition == null) {
			joinCondition = Constants.AND_JOIN_CONDITION;
		}
	
		queryStrBuff.append(" WHERE ");
		int index;
		for (index = 0; index < (whereColumnName.length - 1); index++)
		{
			queryStrBuff.append(sourceObjectName + "." + whereColumnName[index] + " "
					+ whereColumnCondition[index] + " " + whereColumnValue[index]);
			queryStrBuff.append(" " + joinCondition + " ");
		}
		queryStrBuff.append(sourceObjectName + "." + whereColumnName[index] + " "
				+ whereColumnCondition[index] + " " + whereColumnValue[index]);
	
	        
		return queryStrBuff.toString();
	}
}
