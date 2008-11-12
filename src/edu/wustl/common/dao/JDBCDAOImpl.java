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
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.dbmanager.DBUtil;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

public abstract class JDBCDAOImpl implements JDBCDAO
{

	private Connection connection = null;
	protected AuditManager auditManager;
	private static org.apache.log4j.Logger logger = Logger.getLogger(JDBCDAOImpl.class);
	private IConnectionManager connectionManager = null ;
	
	/**
	 * This method will be used to establish the session with the database.
	 * Declared in AbstractDAO class.
	 * @throws DAOException
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{
		try
		{
			initializeAuditManager(sessionDataBean);
			connection = DBUtil.getConnection();
			connection.setAutoCommit(false);
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
		}
	}
	
	/**
	 * This method will be used to close the session with the database.
	 * Declared in AbstractDAO class.
	 * @throws DAOException
	 */
	public void closeSession() throws DAOException
	{
		auditManager = null;
		DBUtil.closeConnection();
	}

	/**
	 * Commit the database level changes.
	 * Declared in AbstractDAO class.
	 * @throws DAOException
	 * @throws SMException
	 */
	public void commit() throws DAOException
	{
		try
		{
			auditManager.insert(this);
			
			if(connection == null)
				throw new DAOException(Constants.GENERIC_DATABASE_ERROR);

			connection.commit();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, dbex);
		}
	}

	/**
	 * Rollback all the changes after last commit. 
	 * Declared in AbstractDAO class. 
	 * @throws DAOException 
	 * @throws DAOException
	 */
	public void rollback() throws DAOException
	{
		try
		{
			if(connection == null)
				throw new DAOException(Constants.GENERIC_DATABASE_ERROR);

			connection.rollback();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, dbex);
			
		}
	}
	
	
	/**
	 * @param sessionDataBean
	 */
	private void initializeAuditManager(SessionDataBean sessionDataBean)
	{
		auditManager = new AuditManager();
		if (sessionDataBean != null)
		{
			auditManager.setUserId(sessionDataBean.getUserId());
			auditManager.setIpAddress(sessionDataBean.getIpAddress());
		}
		else
		{
			auditManager.setUserId(null);
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.JDBCDAO#executeUpdate(java.lang.String)
	 */
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
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException sqlExp)
			{
				sqlExp.printStackTrace();
			}
		}
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	protected Statement getConnectionStmt() throws SQLException
	{
		Statement statement = connection.createStatement();
		return statement;
	}
	
	/**
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement getPreparedStatement(String query) throws SQLException
	{
		return(connection.prepareStatement(query));
		
	}	

	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.JDBCDAO#createTable(java.lang.String, java.lang.String[])
	 */
	public void createTable(String tableName, String[] columnNames) throws DAOException
	{
		String query = createTableQuery(tableName,columnNames);
		executeUpdate(query);
	}
	
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
	 * @param tableName
	 * @param columnNames
	 * @return
	 * @throws DAOException
	 */
	private final String createTableQuery(String tableName, String[] columnNames) throws DAOException
	{
		StringBuffer query = new StringBuffer("CREATE TABLE ").append(tableName).append(" (");
		int i;

		for ( i=0; i < (columnNames.length - 1); i++)
		{
			query = query.append(columnNames[i]).append(" VARCHAR(50),");
		}

		query.append(columnNames[i]).append(" VARCHAR(50));");
		
		return  query.toString();
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
		return retrieve(sourceObjectName, null, null, null, null, null,false);
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
		return retrieve(sourceObjectName, selectColumnName, null, null, null, null,false);
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
				whereColumnValues, Constants.AND_JOIN_CONDITION,false);
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
			StringBuffer query = getSelectFromQueryPart(sourceObjectName, selectColumnName, onlyDistinctRows);
			if (joinCondition == null)
				condition = Constants.AND_JOIN_CONDITION;
			else 
				condition = joinCondition;

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
	 * @param sourceObjectName
	 * @param selectColumnName
	 * @param onlyDistinctRows
	 * @return
	 */
	private StringBuffer getSelectFromQueryPart(String sourceObjectName, String[] selectColumnName,
			boolean onlyDistinctRows)
	{
		StringBuffer query = new StringBuffer("SELECT ");
		if ((selectColumnName != null) && (selectColumnName.length > 0))
		{
			if (onlyDistinctRows)
			{
				query.append(" DISTINCT ");
			}
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
		query.append("FROM " + sourceObjectName);
		return query;
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
		
		return getQueryResultList(query, sessionDataBean, isSecureExecute,
				false, queryResultObjectDataMap, -1, -1).getResult();
	}

	/**
	 * Executes the query.
	 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
	 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
	 * Calling executeQuery method with StartIndex parameter as -1, so that it will return all records from result.
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
			
		if (Constants.SWITCH_SECURITY && isSecureExecute)
		{
			if (sessionDataBean == null)
			{
				return null;
			}
		}
		return getQueryResultList(query, sessionDataBean, isSecureExecute,
				hasConditionOnIdentifiedField, queryResultObjectDataMap, -1, -1).getResult();
	}

	/**
	 *Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
	 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
	 * */
	public PagenatedResultData executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap, int startIndex, int noOfRecords)
			throws ClassNotFoundException, DAOException
	{
		if (Constants.SWITCH_SECURITY && isSecureExecute)
		{
			if (sessionDataBean == null)
			{
				return null;
			}
		}
		return getQueryResultList(query, sessionDataBean, isSecureExecute,
				hasConditionOnIdentifiedField, queryResultObjectDataMap, startIndex, noOfRecords);
	}

	/**
	 * This method exeuted query, parses the result and returns List of rows after doing security checks
	 * for user's right to view a record/field
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
	public PagenatedResultData getQueryResultList(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap, int startIndex, int noOfRecords) throws DAOException
	{
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

	
	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieveAttribute(java.lang.Class, java.lang.Long, java.lang.String)
	 */
	public Object retrieveAttribute(Class<AbstractDomainObject> objClass, Long id,
			String attributeName) throws DAOException
	{
		return retrieveAttribute(objClass.getName(), id, attributeName);
	}

	/**
	 * @param metaData
	 * @param dateColumns
	 * @param numberColumns
	 * @param tinyIntColumns
	 * @throws SQLException
	 */
	protected void updateColumns(ResultSetMetaData metaData,List dateColumns,
			List numberColumns,List tinyIntColumns) throws SQLException
	{
		for (int i = 1; i <= metaData.getColumnCount(); i++)
		{
			String type = metaData.getColumnTypeName(i);
			if (type.equals("DATE"))
				dateColumns.add(new Integer(i));
			if (type.equals("NUMBER"))
				numberColumns.add(new Integer(i));
			if (type.equals("TINYINT"))
				tinyIntColumns.add(new Integer(i));

		}
		
	}
	
	/**
	 * @param tableName
	 * @param columnValues
	 * @param dateColumns
	 * @param numberColumns
	 * @param tinyIntColumns
	 * @param columnNames
	 * @return
	 * @throws SQLException
	 */
	protected List getColumns(String tableName, List columnValues, List dateColumns,
			List numberColumns,List tinyIntColumns,List<String>... columnNames) throws SQLException
	{
		List<String> columnNames_t = new ArrayList<String>();
		
		ResultSetMetaData metaData = getMetadataAndUpdatedColumns(tableName, columnValues,columnNames_t,columnNames);
		updateColumns(metaData, dateColumns,numberColumns, tinyIntColumns);
		
		return columnNames_t;
	}
	
	/**
	 * @param tableName
	 * @param columnValues
	 * @param columnNames_t
	 * @param columnNames
	 * @return
	 * @throws SQLException
	 */
	private final ResultSetMetaData getMetadataAndUpdatedColumns(String tableName,
			List columnValues,List columnNames_t,List<String>... columnNames) throws SQLException
	{
		StringBuffer sql = new StringBuffer("Select ");
		Statement statement = getConnectionStmt();
		ResultSet resultSet = null;
		ResultSetMetaData metaData = null;
		
		if (columnNames != null && columnNames.length > 0)
		{
			columnNames_t = columnNames[0];
			for (int i = 0; i < columnNames_t.size(); i++)
			{
				sql.append(columnNames_t.get(i));
				if (i != columnNames_t.size() - 1)
				{
					sql.append(",");
				}
			}
			sql.append(" from " + tableName + " where 1!=1");
			resultSet = statement.executeQuery(sql.toString());
			metaData = resultSet.getMetaData();

		}
		else
		{
			sql.append("* from " + tableName + " where 1!=1");
			resultSet = statement.executeQuery(sql.toString());
			metaData = resultSet.getMetaData();

			columnNames_t = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++)
			{
				columnNames_t.add(metaData.getColumnName(i));
			}
		}
		
		resultSet.close();
		statement.close();
		return metaData;
	}
	
	
	/**
	 * @param tableName
	 * @param columnNames_t
	 * @param columnValues
	 * @return
	 */
	protected String createInsertQuery(String tableName,List columnNames_t,List columnValues)
	{
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
		return query.toString();
	}
	
	
	/**
	 * @param numberColumns
	 * @param stmt
	 * @param i
	 * @param obj
	 * @throws SQLException
	 */
	protected void setNumberColumns(List numberColumns, PreparedStatement stmt, int i, Object obj) throws SQLException
	{
		if (obj != null && numberColumns.contains(new Integer(i + 1)) && obj.toString().equals("##"))
		{
			stmt.setObject(i + 1, new Integer(-1));
		}
		else
		{
			stmt.setObject(i + 1, obj);
		}
	}




	/**
	 * @param stmt
	 * @param i
	 * @param obj
	 * @throws SQLException
	 */
	protected void setTimeStampColumn(PreparedStatement stmt, int index,Object obj) throws SQLException
	{
		Timestamp date = isColumnValueDate(obj);
		if (date != null)
		{
			stmt.setObject(index + 1, date);
		}	
	}




	/**
	 * @param stmt
	 * @param index
	 * @param obj
	 * @param tinyIntColumns
	 * @throws SQLException
	 */
	protected void setTinyIntColumns(PreparedStatement stmt, int index, Object obj,List tinyIntColumns) throws SQLException
	{
		if (tinyIntColumns.contains(new Integer(index + 1)))
		{	
			if (obj != null && (obj.equals("true") || obj.equals("TRUE") || obj.equals("1")))
			{
				stmt.setObject(index + 1, 1);
			}
			else
			{
				stmt.setObject(index + 1, 0);
			}
		}
	}




	/**
	 * @param stmt
	 * @param index
	 * @param obj
	 * @param dateColumns
	 * @throws SQLException
	 */
	protected void setDateColumns(PreparedStatement stmt, int index,Object obj,List dateColumns) throws SQLException
	{
		if (obj != null && dateColumns.contains(new Integer(index + 1)) && obj.toString().equals("##"))
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
			stmt.setDate(index + 1, sqlDate);
		}
	}
		
	
	/**
	 * @param value
	 * @return
	 */
	private final Timestamp isColumnValueDate(Object value)
	{
		Timestamp timestamp = null;
		try
		{
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			formatter.setLenient(false);
			java.util.Date date;
			date = formatter.parse((String) value);
			timestamp = new Timestamp(date.getTime());
			if (value != null && value.toString().equals("") == false)
			{
				return timestamp;
			}
		}
		catch (ParseException parseExp)
		{
			logger.error(parseExp.getMessage(),parseExp);
			
		}
				
		return timestamp;
	}
	
	public void setConnectionManager(IConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
		
	}
	
	public IConnectionManager getConnectionManager()
	{
		return connectionManager;
	}
	
	
	

}
