package edu.wustl.common.dao;

import org.hibernate.Query;

import edu.wustl.common.util.global.Constants;

public class QueryWhereClauseImpl {
	
	private String[] whereColumnName;
	private String[] whereColumnCondition;
	private Object[] whereColumnValue;
	private String joinCondition;
		
	
	public void setWhereClause(String[] whereColumnName,String[] whereColumnCondition,
			Object[] whereColumnValue,String joinCondition)
	{
		setJoinCondition(joinCondition);
		setWhereColumnCondition(whereColumnCondition);
		setWhereColumnName(whereColumnName);
		setWhereColumnValue(whereColumnValue);
	}
	
	protected String queryWhereClause(String className)
	{
		String[] whereColumnName = getWhereColumnName();
		StringBuffer sqlBuff = new StringBuffer();
		
		 if (getJoinCondition() == null) {
            setJoinCondition(Constants.AND_JOIN_CONDITION);
		 }
        
		 sqlBuff.append(" where ");
         //Adds the column name and search condition in where clause. 
        for (int i = 0; i < whereColumnName.length; i++)
        {
        	setClausesOfWherePart(className,sqlBuff, i);	
        	
        	if (i < (whereColumnName.length - 1)) {
    			sqlBuff.append(" " + getJoinCondition() + " ");
    		}
        }
		
		return sqlBuff.toString();
	}




	protected void setClausesOfWherePart(String className,
			StringBuffer sqlBuff, int index) {
		
		String[] whereColumnCondition = getWhereColumnCondition();
		Object[] whereColumnValue = getWhereColumnValue();
		
		sqlBuff.append(className + "." + whereColumnName[index] + " ");
		//TODO check this twice with original
		if(whereColumnCondition[index].contains("in"))
		{
			inClauseOfWhereQuery(whereColumnCondition, whereColumnValue,
					sqlBuff, index);
		}
		else if(whereColumnCondition[index].contains("is not null"))
		{
			sqlBuff.append(whereColumnCondition[index]);
		}
		else if(whereColumnCondition[index].contains("is null"))
		{
			sqlBuff.append(whereColumnCondition[index]);
		}
		else
		{
			sqlBuff.append(whereColumnCondition[index]).append("  ? ");
		}
		
		
	}

	protected void inClauseOfWhereQuery(String[] whereColumnCondition,
			Object[] whereColumnValue, StringBuffer sqlBuff, int index) {
		
		sqlBuff.append(whereColumnCondition[index]).append("(  ");
		Object valArr[] = (Object [])whereColumnValue[index];
		for (int j = 0; j < valArr.length; j++)
		{
			//Logger.out.debug(sqlBuff);
			sqlBuff.append("? ");
			if((j+1)<valArr.length) {
				sqlBuff.append(", ");
			}	
		}
		sqlBuff.append(") ");
	}
	
	public String toString(String className) {
		
		return queryWhereClause(className);
	}
	
	public boolean isConditionSatisfied()
	{
		Object[] whereColumnValue = getWhereColumnValue();
				
		boolean isConditionSatisfied = false;
		if ((isWhereColumnName())
                && (isWhereColumnCondition())
                && (whereColumnValue != null))
        {
			isConditionSatisfied = true;
        }
		
		return isConditionSatisfied;
	}

	protected boolean isWhereColumnCondition() {
		String[] whereColumnCondition = getWhereColumnCondition();
		String[] whereColumnName = getWhereColumnName();
		return whereColumnCondition != null && whereColumnCondition.length == whereColumnName.length;
	}

	protected boolean isWhereColumnName() {
		String[] whereColumnName = getWhereColumnName();
		return whereColumnName != null && whereColumnName.length > 0;
	}
	
	public void setParametersToQuery(Query query)
	{
		
		String[] whereColumnCondition = getWhereColumnCondition();
		Object[] whereColumnValue = getWhereColumnValue();
		
		int index = 0;
		//Adds the column values in where clause
		for (int i = 0; i < whereColumnValue.length; i++)
		{
		    //Logger.out.debug("whereColumnValue[i]. " + whereColumnValue[i]);
			if (!(whereColumnCondition[i].equals("is null") || whereColumnCondition[i].equals("is not null")))
			{
			  Object obj = whereColumnValue[i];
		        if(obj instanceof Object[])
		        {
		        	index = setParametersForArray(query, index, obj);
		        }
		        else
		        {
		        	query.setParameter(index, obj);
		        	index++;
		        }
		    }
		}
	}

	protected int setParametersForArray(Query query, int indexVal, Object obj) {
		int index = indexVal; 
		Object[] valArr = (Object[])obj;
		for (int j = 0; j < valArr.length; j++)
		{
			query.setParameter(index, valArr[j]);
			index++;
		}
		return index;
	}
	
	
	protected String[] getWhereColumnName() {
		return whereColumnName;
	}
	
	protected void setWhereColumnName(String[] whereColumnName) {
		this.whereColumnName = whereColumnName;
	}

	protected String[] getWhereColumnCondition() {
		return whereColumnCondition;
	}

	protected void setWhereColumnCondition(String[] whereColumnCondition) {
		this.whereColumnCondition = whereColumnCondition;
	}

	protected Object[] getWhereColumnValue() {
		return whereColumnValue;
	}

	protected void setWhereColumnValue(Object[] whereColumnValue) {
		this.whereColumnValue = whereColumnValue;
	}

	protected String getJoinCondition() {
		return joinCondition;
	}

	protected void setJoinCondition(String joinCondition) {
		this.joinCondition = joinCondition;
	}

	
	
	

}
