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
		
		Object[] whereColumnValue = getWhereColumnValue();
		String[] whereColumnName = getWhereColumnName();
		
		return whereColumnValue != null && whereColumnName.length == whereColumnValue.length;
	}
	
	
	public String jdbcQueryWhereClause(String sourceObjectName) {
		
		String[] whereColumnName = getWhereColumnName();
		String[] whereColumnCondition = getWhereColumnCondition();
		Object[] whereColumnValue = getWhereColumnValue();
		
		StringBuffer queryStrBuff = new StringBuffer();
		
		if (getJoinCondition() == null) {
	            setJoinCondition(Constants.AND_JOIN_CONDITION);
		}
		

		queryStrBuff.append(" WHERE ");
		int index;
		for (index = 0; index < (whereColumnName.length - 1); index++)
		{
			queryStrBuff.append(sourceObjectName + "." + whereColumnName[index] + " "
					+ whereColumnCondition[index] + " " + whereColumnValue[index]);
			queryStrBuff.append(" " + getJoinCondition() + " ");
		}
		queryStrBuff.append(sourceObjectName + "." + whereColumnName[index] + " "
				+ whereColumnCondition[index] + " " + whereColumnValue[index]);
	
	        
		return queryStrBuff.toString();
	}
}
