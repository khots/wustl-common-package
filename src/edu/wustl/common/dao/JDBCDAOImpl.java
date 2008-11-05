/**
 * <p>Title: JDBCDAO Class>
 * <p>Description:	JDBCDAO is default implementation of AbstractDAO through JDBC.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.queryExecutor.IQueryExecutor;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.locator.InterfaceLocator;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * Default implementation of AbstractDAO through JDBC.
 * @author gautam_shetty
 */
public abstract class JDBCDAOImpl implements JDBCDAO
{

	private Connection connection = null;
	protected AuditManager auditManager;
	private static org.apache.log4j.Logger logger = Logger.getLogger(JDBCDAOImpl.class);

	public abstract void closeSession() throws DAOException;
	

	public abstract void commit() throws DAOException;
	

	public abstract void openSession(SessionDataBean sessionDataBean) throws DAOException;
	

	public abstract void rollback() throws DAOException;
	
	
	
	public abstract void delete(String tableName) throws DAOException;
	

	/**
	 * Creates a table with the query specified.
	 * @param query Query create table.
	 * @throws DAOException
	 */
	public void createTable(String query) throws DAOException
	{
		logger.debug("Create Table Query " + query.toString());
		executeUpdate(query.toString());
	}

	/**
	 * Returns the ResultSet containing all the rows in the table represented in sourceObjectName. 
	 * @param sourceObjectName The table name.
	 * @return The ResultSet containing all the rows in the table represented in sourceObjectName.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List retrieve(String sourceObjectName) throws DAOException
	{
		return retrieve(sourceObjectName, null, null, null, null, null);
	}

	/**
	 * Returns the ResultSet containing all the rows according to the columns specified 
	 * from the table represented in sourceObjectName.
	 * @param sourceObjectName The table name.
	 * @param selectColumnName The column names in select clause.
	 * @return The ResultSet containing all the rows according to the columns specified 
	 * from the table represented in sourceObjectName.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName) throws DAOException
	{
		return retrieve(sourceObjectName, selectColumnName, null, null, null, null);
	}

	/**
	 * Returns the ResultSet containing all the rows according to the columns specified 
	 * from the table represented in sourceObjectName.
	 * @param sourceObjectName The table name.
	 * @param selectColumnName The column names in select clause.
	 * @param onlyDistictRows true if only distict rows should be selected
	 * @return The ResultSet containing all the rows according to the columns specified 
	 * from the table represented in sourceObjectName.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			boolean onlyDistinctRows) throws DAOException
	{
		//logger.out.debug(" Only distinct rows:" + onlyDistinctRows);
		return retrieve(sourceObjectName, selectColumnName, null, null, null, null,
				onlyDistinctRows);
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed in the passed session.
	 * @param selectColumnName An array of field names in select clause.
	 * @param whereColumnName An array of field names in where clause.
	 * @param whereColumnCondition The comparision condition for the field values. 
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @param onlyDistictRows true if only distict rows should be selected
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition, boolean onlyDistinctRows) throws DAOException
	{
		//logger.out.debug(" Only distinct rows:" + onlyDistinctRows);
		List list = null;
		String condition;

		try
		{
			StringBuffer query = new StringBuffer("SELECT ");
			
			if (joinCondition == null)
				condition = Constants.AND_JOIN_CONDITION;
			else 
				condition = joinCondition;

			//Prepares the select clause of the query.
			if ((selectColumnName != null) && (selectColumnName.length > 0))
			{
				//Bug# 2003: Limiting the define view does not remove duplicates
				if (onlyDistinctRows)
				{
					//logger.out.debug(" Adding distinct to query ");
					query.append(" DISTINCT ");
				}
				//END Bug# 2003
				int i;
				for (i = 0; i < (selectColumnName.length - 1); i++)
				{
					query.append(selectColumnName[i] + " ");
					query.append(",");
				}
				query.append(selectColumnName[i] + " ");
			}
			else
			{
				query.append("* ");
			}

			//Prepares the from clause of the query.
			query.append("FROM " + sourceObjectName);

			//Prepares the where clause of the query.
			if ((whereColumnName != null && whereColumnName.length > 0)
					&& (whereColumnCondition != null && whereColumnCondition.length == whereColumnName.length)
					&& (whereColumnValue != null && whereColumnName.length == whereColumnValue.length))
			{
				query.append(" WHERE ");
				int i;
				for (i = 0; i < (whereColumnName.length - 1); i++)
				{
					query.append(sourceObjectName + "." + whereColumnName[i] + " "
							+ whereColumnCondition[i] + " " + whereColumnValue[i]);
					query.append(" " + condition + " ");
				}
				query.append(sourceObjectName + "." + whereColumnName[i] + " "
						+ whereColumnCondition[i] + " " + whereColumnValue[i]);
			}
			logger.debug("JDBC Query " + query);
			list = executeQuery(query.toString(), null, false, null);
		}
		catch (ClassNotFoundException classExp)
		{
			logger.error(classExp.getMessage(), classExp);
		}

		return list;
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed in the passed session.
	 * @param selectColumnName An array of field names in select clause.
	 * @param whereColumnName An array of field names in where clause.
	 * @param whereColumnCondition The comparision condition for the field values. 
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @param The session object.
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition) throws DAOException
	{
		return retrieve(sourceObjectName, selectColumnName, whereColumnName, whereColumnCondition,
				whereColumnValue, joinCondition, false);
	}

	/**
	 * Executes the query.
	 * @param query
	 * @param sessionDataBean TODO
	 * @param isSecureExecute TODO
	 * @param columnIdsMap
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map queryResultObjectDataMap) throws ClassNotFoundException,
			DAOException
	{
		return executeQuery(query, sessionDataBean, isSecureExecute, false,
				queryResultObjectDataMap, -1, -1).getResult();
	}

	/**
	 * Executes the query.
	 * @param query
	 * @param sessionDataBean TODO
	 * @param isSecureExecute TODO
	 * @param columnIdsMap
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap) throws ClassNotFoundException, DAOException
	{
		/**
		 * Name: Prafull
		 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
		 *
		 * Calling executeQuery method with StartIndex parameter as -1, so that it will return all records from result.
		 */

		return executeQuery(query, sessionDataBean, isSecureExecute, hasConditionOnIdentifiedField,
				queryResultObjectDataMap, -1, -1).getResult();
	}

	/**
	 * @see edu.wustl.common.dao.JDBCDAO#executeQuery(java.lang.String, edu.wustl.common.beans.SessionDataBean, boolean, boolean, java.util.Map, int, int)
	 */
	public PagenatedResultData executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap, int startIndex, int noOfRecords)
			throws ClassNotFoundException, DAOException
	{
		//Aarti: Security checks
		if (Constants.SWITCH_SECURITY && isSecureExecute)
		{
			if (sessionDataBean == null)
			{
				//logger.out.debug("Session data is null");
				return null;
			}
		}
		/**
		 * Name: Prafull
		 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
		 */

		return getQueryResultList(query, sessionDataBean, isSecureExecute,
				hasConditionOnIdentifiedField, queryResultObjectDataMap, startIndex, noOfRecords);
	}

	/**
	 * This method exeuted query, parses the result and returns List of rows after doing security checks
	 * for user's right to view a record/field
	 * @author aarti_sharma
	 * @param query
	 * @param sessionDataBean
	 * @param isSecureExecute
	 * @param hasConditionOnIdentifiedField
	 * @param queryResultObjectDataMap
	 * @param startIndex The offset value, from which the result will be returned. 
	 * 		This will be used for pagination purpose, 
	 * @param noOfRecords
	 * @return
	 * @throws DAOException
	 */
	private PagenatedResultData getQueryResultList(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap, int startIndex, int noOfRecords) throws DAOException
	{
		/**
		 * Name: Prafull
		 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
		 *
		 * Calling QueryExecutor method.
		 */
		InterfaceLocator iLocator = InterfaceLocator.getInstance();
		String interfaceName = "IQueryExecutor";
		String queryExecutorClassName = iLocator.getClassNameForInterface(interfaceName);
		PagenatedResultData pagenatedResultData = null;
		//
		
		try {
			IQueryExecutor qe = (IQueryExecutor)Class.forName(queryExecutorClassName).newInstance();
			pagenatedResultData = qe.getQueryResultList(query, connection,
					sessionDataBean, isSecureExecute, hasConditionOnIdentifiedField,
					queryResultObjectDataMap, startIndex, noOfRecords);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return pagenatedResultData;

	}

	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieve(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public List retrieve(String sourceObjectName, String whereColumnName, Object whereColumnValue)
			throws DAOException
	{
		String whereColumnNames[] = {whereColumnName};
		String whereColumnConditions[] = {"="};
		Object whereColumnValues[] = {whereColumnValue};

		return retrieve(sourceObjectName, null, whereColumnNames, whereColumnConditions,
				whereColumnValues, Constants.AND_JOIN_CONDITION);
	}

	private Timestamp isColumnValueDate(Object value)
	{
		//logger.out.debug("Column value: " + value);
		try
		{
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			formatter.setLenient(false);
			java.util.Date date = formatter.parse((String) value);
			Timestamp t = new Timestamp(date.getTime());
			// Date sqlDate = new Date(date.getTime());

			//logger.out.debug("Column date value: " + date);
			if (value.toString().equals("") == false)
			{
				//logger.out.debug("Return true: " + value);
				return t;
			}
		}
		catch (Exception e)
		{

		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.JDBCDAO#insert(java.lang.String, java.util.List)
	 */
	public void insert(String tableName, List columnValues) throws DAOException, SQLException
	{
		insert(tableName, columnValues, null);
	}

	/**
	 * @param tableName
	 * @param columnValues
	 * @param columnNames
	 * @throws DAOException
	 * @throws SQLException
	 */
	public void insert(String tableName, List columnValues, List<String>... columnNames)
			throws DAOException, SQLException
	{
		//Get metadate for temp table to set default values in date fields
		String sql = "Select * from " + tableName + " where 1!=1";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql.toString());
		ResultSetMetaData metaData = resultSet.getMetaData();
		
		List dateColumns = new ArrayList();
		List numberColumns = new ArrayList();

		List<String> columnNames_t;
		if (columnNames != null && columnNames.length > 0)
		{
			columnNames_t = columnNames[0];
		}
		else
		{
			columnNames_t = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++)
			{
				columnNames_t.add(metaData.getColumnName(i));
			}
		}

		/* Desciption: Make a list of tinyint columns.
		* Tinyint datatype is used as a replacement for boolean in MySQL.
		*/
		List tinyIntColumns = new ArrayList();
		updateColumns(metaData,dateColumns,tinyIntColumns,numberColumns);

		resultSet.close();
		statement.close();
		StringBuffer query = new StringBuffer("INSERT INTO " + tableName + "(");

		Iterator<String> columnIterator = columnNames_t.iterator();
		while (columnIterator.hasNext())
		{
			query.append(columnIterator.next());
			if (columnIterator.hasNext())
			{
				query.append(",");
			}
			else
			{
				query.append(") values(");
			}
		}

		//StringBuffer query = new StringBuffer("INSERT INTO " + tableName + " values(");
		//Changed implementation with column names
	
		Iterator it = columnValues.iterator();
		while (it.hasNext())
		{
			it.next();
			query.append("?");

			if (it.hasNext())
				query.append(",");
			else
				query.append(")");
		}

		PreparedStatement stmt = null;
		try
		{
			stmt = connection.prepareStatement(query.toString());
			getPreparedStatement(stmt,dateColumns,tinyIntColumns,numberColumns,columnValues);
			stmt.executeUpdate();
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
			throw new DAOException(sqlExp.getMessage(), sqlExp);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException ex)
			{
				throw new DAOException(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.AbstractDAO#update(java.lang.Object, SessionDataBean, boolean, boolean, boolean)
	 */
	public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException,
			UserNotAuthorizedException
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.AbstractDAO#delete(java.lang.Object)
	 */
	public void delete(Object obj) throws DAOException
	{
		// TODO Auto-generated method stub
		//return false;
	}

	/**
	 * Creates a table with the name and columns specified.
	 * @param tableName Name of the table to create.
	 * @param columnNames Columns in the table.
	 * @throws DAOException
	 */
	public void create(String tableName, String[] columnNames) throws DAOException
	{
		StringBuffer query = new StringBuffer("CREATE TABLE " + tableName + " (");
		int i = 0;

		for (; i < (columnNames.length - 1); i++)
		{
			query = query.append(columnNames[i] + " VARCHAR(50),");
		}

		query.append(columnNames[i] + " VARCHAR(50));");

		logger.debug("Create Table*************************" + query.toString());

		executeUpdate(query.toString());
	}
	
	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieve(java.lang.String, java.lang.Long)
	 */
	public Object retrieve(String sourceObjectName, Long id) throws DAOException
	{
		try
		{
			return null;
		}
		catch (Exception hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, hibExp);
		}
	}

	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#insert(java.lang.Object, boolean)
	 */
	public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureInsert) throws DAOException, UserNotAuthorizedException
	{
		// TODO Auto-generated method stub
		//		if(isAuditable)
		//		auditManager.compare((AbstractDomainObject)obj,null,"INSERT");
	}

	public void executeUpdate(String query) throws DAOException
	{
		PreparedStatement stmt = null;
		try
		{
			stmt = connection.prepareStatement(query.toString());

			stmt.executeUpdate();
		}
		catch (SQLException sqlExp)
		{

			throw new DAOException(sqlExp.getMessage(), sqlExp);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException ex)
			{
				throw new DAOException(ex.getMessage(), ex);
			}
		}
	}

	public void disableRelatedObjects(String tableName, String whereColumnName,
			Long whereColumnValues[]) throws DAOException
	{
	}

	public String getActivityStatus(String sourceObjectName, Long indetifier) throws DAOException
	{
		return null;
	}

	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException
	{
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieveAttribute(java.lang.Class, java.lang.Long, java.lang.String)
	 */
	public Object retrieveAttribute(Class<AbstractDomainObject> objClass, Long id,
			String attributeName) throws DAOException
	{
		return retrieveAttribute(objClass.getName(), id, attributeName);
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieveAttribute(java.lang.String, java.lang.Long, java.lang.String)
	 */
	public Object retrieveAttribute(String sourceObjectName, Long id, String attributeName)
			throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private void getPreparedStatement(PreparedStatement stmt,List dateColumns,List tinyIntColumns,List numberColumns,List columnValues) throws SQLException
	{
		for (int i = 0; i < columnValues.size(); i++)
		{
			Object obj = columnValues.get(i);
			/**
			 * For Number -1 is used as MarkUp data For Date 1-1-9999 is used as markUp data.
			 * Please refer bug 3576
			 */

			if (dateColumns.contains(Integer.valueOf((i + 1))) && obj.toString().equals("##"))
			{
				java.util.Date date = null;
				try
				{
					date = Utility.parseDate("1-1-9999", "mm-dd-yyyy");
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				Date sqlDate = new Date(date.getTime());
				stmt.setDate(i + 1, sqlDate);
			}
			
			/**
			* Desciption: If the value of the column is true set 1 in the statement else set 0.
			* This is necessary for MySQL since all boolean values in MySQL are stored in tinyint.
			* If this is not done then all values will be set as 0 
			* irrespective of whether the value is true or false.
			*/
			else if (tinyIntColumns.contains(Integer.valueOf((i + 1))))
			{
				if (obj.equals("true") || obj.equals("TRUE"))
				{
					stmt.setObject(i + 1, 1);
				}
				else
				{
					stmt.setObject(i + 1, 0);
				}
			}
			else
			{
				Timestamp date = isColumnValueDate(obj);
				if (date != null)
				{
					stmt.setObject(i + 1, date);
					//logger.out.debug("t.toString(): " + "---" + date);
				}
				else
				{
					if (numberColumns.contains(Integer.valueOf(i + 1))
							&& obj.toString().equals("##"))
					{
						stmt.setObject(i + 1,Integer.valueOf(-1));
					}
					else
					{
						stmt.setObject(i + 1, obj);
					}
				}
			}
		}
	}
	
	private void updateColumns(ResultSetMetaData metaData,List dateColumns,List tinyIntColumns,List numberColumns) throws SQLException
	{
		
		for (int i = 1; i <= metaData.getColumnCount(); i++)
		{
			String type = metaData.getColumnTypeName(i);
			if ("DATE".equals(type))
			{
				dateColumns.add(Integer.valueOf(i));
			}	
			if ("NUMBER".equals(type))
			{
				numberColumns.add(Integer.valueOf(i));
			}
			if ("TINYINT".equals(type))
			{	
				tinyIntColumns.add(Integer.valueOf(i));
			}

		}
		
	}
	

}
