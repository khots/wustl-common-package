/**
 * <p>Title: AbstractJDBCDAOImpl Class>
 * <p>Description:	JDBCDAO is default implementation of DAO and JDBCDAO through JDBC.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

public abstract class AbstractJDBCDAOImpl implements JDBCDAO
{

	private Connection connection = null;
	protected AuditManager auditManager;
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractJDBCDAOImpl.class);
	private IConnectionManager connectionManager = null ;
	
	/**
	 * This method will be used to establish the session with the database.
	 * Declared in DAO class.
	 * @throws DAOException
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{
		try
		{
			initializeAuditManager(sessionDataBean);
			connection = connectionManager.getConnection();
			connection.setAutoCommit(false);
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(DAOConstants.OPEN_SESSION_ERROR,sqlExp);
		}
	}
	
	/**
	 * This method will be used to close the session with the database.
	 * Declared in DAO class.
	 * @throws DAOException
	 */
	public void closeSession() throws DAOException
	{
		auditManager = null;
		getConnectionManager().closeConnection();
	}

	/**
	 * Commit the database level changes.
	 * Declared in DAO class.
	 * @throws DAOException
	 * @throws SMException
	 */
	public void commit() throws DAOException
	{
		try
		{
			auditManager.insert(this);
		
			if(connection == null)
			{
				logger.fatal(DAOConstants.NO_CONNECTION_TO_DB);
			}	

			connection.commit();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw new DAOException(DAOConstants.COMMIT_DATA_ERROR, dbex);
		}
	}

	/**
	 * RollBack all the changes after last commit. 
	 * Declared in DAO class. 
	 * @throws DAOException 
	 * @throws DAOException
	 */
	public void rollback() throws DAOException
	{
		try
		{
			if(connection == null)
			{
				logger.fatal(DAOConstants.NO_CONNECTION_TO_DB);
			}	

			connection.rollback();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw new DAOException(DAOConstants.ROLLBACK_ERROR, dbex);
			
		}
	}
	
	
	/**
	 * This will be called to initialized the Audit Manager
	 * @param sessionDataBean
	 */
	private void initializeAuditManager(SessionDataBean sessionDataBean)
	{
		auditManager = new AuditManager();
		if (sessionDataBean == null) 
		{
		
			auditManager.setUserId(null);
		} 
		else 
		{
			auditManager.setUserId(sessionDataBean.getUserId());
			auditManager.setIpAddress(sessionDataBean.getIpAddress());
		}
	}
	
	/** 
	 * This method will be called for executing a static SQL statement.
	 * @see edu.wustl.common.dao.JDBCDAO#executeUpdate(java.lang.String)
	 * @param query
	 * 
	 */
	public void executeUpdate(String query) throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		databaseConnectionParams.setConnection(connection);
		databaseConnectionParams.executeUpdate(query);
	}
	
	/**
	 * This method will be called to return Database Statement Object
	 * @return : Database Statement Object
	 * @throws SQLException
	 */
	protected Statement getConnectionStmt() throws SQLException
	{
		return (Statement)connection.createStatement();
	}
	
	/**
	 * This method will be called to return Database PreparedStatement Object
	 * @param query
	 * @return Database PreparedStatement Object
	 * @throws SQLException
	 */
	protected PreparedStatement getPreparedStatement(String query) throws SQLException
	{
		return connection.prepareStatement(query);
	}	

	/* @see edu.wustl.common.dao.JDBCDAO#createTable(java.lang.String, java.lang.String[])
	 * This method will Create and execute a table with the name and columns specified 
	 * */
	
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
		executeUpdate(query);
	}
	
	/**
	 * Generates the Create Table Query
	 * @param tableName Name of the table to create.
	 * @param columnNames Columns in the table.
	 * @return Create Table Query
	 * @throws DAOException
	 */
	private final String createTableQuery(String tableName, String[] columnNames) throws DAOException
	{
		StringBuffer query = new StringBuffer("CREATE TABLE ").append(tableName).append(" (");
		int index;

		for ( index=0; index < (columnNames.length - 1); index++)
		{
			
			query = query.append(columnNames[index]).append(" VARCHAR(50),");
		}
		query.append(columnNames[index]).append(" VARCHAR(50));");
		
		return  query.toString();
	}
	/**
	 * Returns the ResultSet containing all the rows in the table represented in sourceObjectName. 
	 * @param sourceObjectName The table name.
	 * @return The ResultSet containing all the rows in the table represented in sourceObjectName.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<Object> retrieve(String sourceObjectName) throws DAOException
	{
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(null, null,
				null,null);
		
		return retrieve(sourceObjectName, null, queryWhereClauseImpl,false);
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
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName) throws DAOException
	{
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(null, null,
				null,null);
		
		return retrieve(sourceObjectName, selectColumnName,queryWhereClauseImpl,false);
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
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			boolean onlyDistinctRows) throws DAOException
	{
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(null, null,
				null,null);
		
		return retrieve(sourceObjectName, selectColumnName,queryWhereClauseImpl,
				onlyDistinctRows);
	}
		
	/**
	 * Returns the ResultSet containing all the rows according to the columns specified 
	 * from the table represented in sourceObjectName as per the where clause. 
	 * @param sourceObjectName The table name.
	 * @param selectColumnName The column names in select clause.
	 * @param queryWhereClauseImpl The where condition clause which holds the where column name, 
	 * value and conditions applied 
	 * @return The ResultSet containing all the rows according to the columns specified 
	 * from the table represented in sourceObjectName which satisfies the where condition 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<Object> retrieve(String sourceObjectName,
			String[] selectColumnName, QueryWhereClauseImpl queryWhereClauseImpl)
			throws DAOException {
	
		return retrieve(sourceObjectName, selectColumnName,queryWhereClauseImpl,false);
	}

	/**
	 * Returns the ResultSet containing all the rows from the table represented in sourceObjectName
	 * according to the where clause.It will create the where condition clause which holds where column name, 
	 * value and conditions applied 
	 * @param sourceObjectName The table name.
	 * @param whereColumnName The column names in where clause.
	 * @param whereColumnValue The column values in where clause.
	 * @return The ResultSet containing all the rows from the table represented
	 * in sourceObjectName which satisfies the where condition 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<Object> retrieve(String sourceObjectName, String whereColumnName, Object whereColumnValue)
			throws DAOException
	{
		String whereColumnNames[] = {whereColumnName};
		String whereColumnConditions[] = {"="};
		Object whereColumnValues[] = {whereColumnValue};
		String[] selectColumnName = null;
		
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(whereColumnNames, whereColumnConditions,
				whereColumnValues,  Constants.AND_JOIN_CONDITION);
		

		return retrieve(sourceObjectName, selectColumnName,queryWhereClauseImpl,false);
	}


	/**
	 * Retrieves the records for class name in sourceObjectName according to field values passed in the passed session.
	 * @param selectColumnName An array of field names in select clause.
	 * @param queryWhereClauseImpl This will hold the where clause.It holds following 
	 * whereColumnName : An array of field names in where clause.
	 * whereColumnCondition : The comparison condition for the field values. 
	 * whereColumnValue : An array of field values.
	 * joinCondition : The join condition.
	 * @param onlyDistictRows true if only distinct rows should be selected
	 */
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,QueryWhereClauseImpl queryWhereClauseImpl,
			 boolean onlyDistinctRows) throws DAOException
	{
		List<Object> list = null;
		
		QueryWhereClauseJDBCImpl queryWhereClauseJDBCImpl = (QueryWhereClauseJDBCImpl)queryWhereClauseImpl;

		try
		{
			StringBuffer queryStrBuff = getSelectPartOfQuery(selectColumnName, onlyDistinctRows);
			getFromPartOfQuery(sourceObjectName, queryStrBuff);
			
			if(queryWhereClauseJDBCImpl.isConditionSatisfied())
			{
				queryStrBuff.append(queryWhereClauseJDBCImpl.jdbcQueryWhereClause(sourceObjectName));
			}
			
			logger.debug("JDBC Query " + queryStrBuff);
			list = executeQuery(queryStrBuff.toString(), null, false, null);
		}
		catch (ClassNotFoundException classExp)
		{
			logger.error(classExp.getMessage(), classExp);
		}

		return list;
	}

	/**
	 * This method will return the select clause of Query 
	 * @param selectColumnName An array of field names in select clause.
	 * @param onlyDistinctRows true if only distinct rows should be selected
	 * @return It will return the select clause of Query.
	 */
	private StringBuffer getSelectPartOfQuery(String[] selectColumnName,
			boolean onlyDistinctRows)
	{
		StringBuffer query = new StringBuffer("SELECT ");
		if ((selectColumnName != null) && (selectColumnName.length > 0))
		{
			if (onlyDistinctRows)
			{
				query.append(" DISTINCT ");
			}
			int index;
			for (index = 0; index < (selectColumnName.length - 1); index++)
			{
				query.append(selectColumnName[index]).append("  ,");
			}
			query.append(selectColumnName[index]).append("  ");
		} 
		else
		{
			query.append("* ");
		}
		return query;
	}

	/**
	 * This will generate the from clause of Query
	 * @param sourceObjectName The table name.
	 * @param queryStrBuff Query buffer
	 */
	private void getFromPartOfQuery(String sourceObjectName, StringBuffer queryStrBuff) {
		queryStrBuff.append("FROM ").append(sourceObjectName);
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
	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map queryResultObjectDataMap) throws ClassNotFoundException,
			DAOException
	{
		
		QueryParams queryParams = new QueryParams();
		queryParams.setQuery(query);
		queryParams.setSessionDataBean(sessionDataBean);
		queryParams.setSecureToExecute(isSecureExecute);
		queryParams.setHasConditionOnIdentifiedField(false);
		queryParams.setQueryResultObjectDataMap(queryResultObjectDataMap);
		queryParams.setStartIndex(-1);
		queryParams.setNoOfRecords(-1);
		
		return getQueryResultList(queryParams).getResult();
	}

	
	/**
	 *Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
	 * object of class QuerySessionData will be saved session, which will contain the required information for query execution while navigating through query result pages.
	 * */
	public PagenatedResultData executeQuery(QueryParams  queryParams)
			throws ClassNotFoundException, DAOException
	{
		PagenatedResultData pagenatedResultData = null;
		if (!(Constants.SWITCH_SECURITY && queryParams.isSecureToExecute() && queryParams.getSessionDataBean() == null))
		{
		  pagenatedResultData = (PagenatedResultData)getQueryResultList(queryParams);
		}
		return pagenatedResultData;
	}

	/**
	 * This method executed query, parses the result and returns List of rows after doing security checks
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
	public abstract PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException;
	
	

	
	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieveAttribute(java.lang.Class, java.lang.Long, java.lang.String)
	 */
	public Object retrieveAttribute(Class<AbstractDomainObject> objClass, Long identifier,
			String attributeName) throws DAOException
	{
		return retrieveAttribute(objClass.getName(), identifier, attributeName);
	}

	/**
	 * @param metaData ResultSetMetaData 
	 * @param dateColumns 
	 * @param numberColumns
	 * @param tinyIntColumns
	 * @throws SQLException
	 */
	protected void updateColumns(ResultSetMetaData metaData,List<Integer> dateColumns,
			List<Integer> numberColumns,List<Integer> tinyIntColumns) throws SQLException
	{
		for (int i = 1; i <= metaData.getColumnCount(); i++)
		{
			String type = metaData.getColumnTypeName(i);
			if (("DATE").equals(type))
			{
				dateColumns.add(Integer.valueOf(i));
			}	
			if (("NUMBER").equals(type))
			{
				numberColumns.add(Integer.valueOf(i));
			}	
			if (("TINYINT").equals(type))
			{
				tinyIntColumns.add(Integer.valueOf(i));
			}	

		}
		
	}
	
	/**
	 * This method returns the metaData associated to the table specified in tableName. 
	 * @param tableName Name of the table whose metaData is requested
	 * @param columnNames Table columns
	 * @return It will return the metaData associated to the table.
	 * @throws DAOException
	 */
	protected final ResultSetMetaData getMetaData(String tableName,List<String> columnNames)throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
	
		ResultSetMetaData metaData;
		StringBuffer sqlBuff = new StringBuffer("Select ");
		
		try
		{
			
			databaseConnectionParams.setConnection(connection);
			for (int i = 0; i < columnNames.size(); i++)
			{
				sqlBuff.append(columnNames.get(i));
				if (i != columnNames.size() - 1)
				{
					sqlBuff.append("  ,");
				}
			}
			sqlBuff.append(" from " + tableName + " where 1!=1");
			metaData = databaseConnectionParams.getMetaData(sqlBuff.toString());		
		
		}
		finally 
		{
			
			databaseConnectionParams.closeConnectionParams();
		}
		
		return metaData;
		
	}
	
	/**
	 * This method will returns the metaData associated to the table specified in tableName
	 * and update the list columnNames. 
	 * @param tableName Name of the table whose metaData is requested
	 * @param columnNames Table columns
	 * @return It will return the metaData associated to the table.
	 * @throws DAOException
	 */
	protected final ResultSetMetaData getMetaDataAndUpdateColumns(String tableName,List<String> columnNames)throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		ResultSetMetaData metaData;
		
		try
		{
			
			databaseConnectionParams.setConnection(connection);
			StringBuffer sqlBuff = new StringBuffer();
			sqlBuff.append("Select * from " ).append(tableName).append(" where 1!=1");
			metaData = databaseConnectionParams.getMetaData(sqlBuff.toString());
			
			for (int i = 1; i <= metaData.getColumnCount(); i++)
			{
				columnNames.add(metaData.getColumnName(i));
			}
			
		} 
		catch (SQLException sqlExp)
		{
			
			logger.fatal(sqlExp.getMessage(), sqlExp);
			throw new DAOException(sqlExp);
			
		} 
		finally 
		{
			databaseConnectionParams.closeConnectionParams();
		}
		return metaData;
	}
	
	/**
	 * This method generates the Insert query 
	 * @param tableName
	 * @param columnNames_t
	 * @param columnValues
	 * @return
	 * Have to refractor this !!!!!!! columnValues might not needed
	 */
	protected String createInsertQuery(String tableName,List<String> columnNames_t,List<Object> columnValues)
	{
		StringBuffer query = new StringBuffer("INSERT INTO " + tableName + "(");

		Iterator<String> columnIterator = columnNames_t.iterator();
		while (columnIterator.hasNext())
		{
			query.append(columnIterator.next());
			if (columnIterator.hasNext())
			{
				query.append(", ");
			}
			else
			{
				query.append(") values(");
			}
		}
		Iterator<Object> iterator = columnValues.iterator();
		while (iterator.hasNext())
		{
			iterator.next();
			query.append("? ");

			if (iterator.hasNext())
			{
				query.append(", ");
			}
			else 
			{
				query.append(") ");
			}	
		}
		return query.toString();
	}
	
	
	/**
	 * This method called to set Number value to PreparedStatement
	 * @param numberColumns
	 * @param stmt
	 * @param index
	 * @param obj
	 * @throws SQLException
	 */
	protected void setNumberColumns(List<Integer> numberColumns, PreparedStatement stmt, int index, Object obj) throws SQLException
	{
		if (obj != null && numberColumns.contains(Integer.valueOf(index + 1)) && obj.toString().equals("##"))
		{
			stmt.setObject(index + 1, Integer.valueOf(-1));
		}
		else
		{
			stmt.setObject(index + 1, obj);
		}
	}

	/**
	 * This method called to set TimeStamp value to PreparedStatement
	 * @param stmt :PreparedStatement
	 * @param i :
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
	protected void setTinyIntColumns(PreparedStatement stmt, int index, Object obj,List<Integer> tinyIntColumns) throws SQLException
	{
		if (tinyIntColumns.contains(Integer.valueOf(index + 1)))
		{	
			setTinyIntColumns(stmt, index, obj);
		}
	}

	/**
	 * This method is called to set TinyInt value
	 * to prepared statement.
	 * @param stmt
	 * @param index
	 * @param obj
	 * @throws SQLException
	 */
	private void setTinyIntColumns(PreparedStatement stmt, int index, Object obj)
			throws SQLException {
		if (obj != null && (obj.equals("true") || obj.equals("TRUE") || obj.equals("1")))
		{
			stmt.setObject(index + 1, 1);
		}
		else
		{
			stmt.setObject(index + 1, 0);
		}
	}

	/**
	 * This method used to set Date values
	 * to prepared statement
	 * @param stmt
	 * @param index
	 * @param obj
	 * @param dateColumns
	 * @throws SQLException
	 * @throws DAOException 
	 */
	protected void setDateColumns(PreparedStatement stmt, int index,Object obj,List<Integer> dateColumns) throws SQLException, DAOException
	{
		if (obj != null && dateColumns.contains(Integer.valueOf(index + 1)) && obj.toString().equals("##"))
		{
			java.util.Date date = null;
			try
			{
				date = Utility.parseDate("1-1-9999", "mm-dd-yyyy");
			}
			catch (ParseException e)
			{
				throw new DAOException(e);
			}
			Date sqlDate = new Date(date.getTime());
			stmt.setDate(index + 1, sqlDate);
		}
	}
		
	
	/**
	 * This method checks the TimeStamp value.
	 * @param value
	 * @return It returns the TimeStamp value
	 * */
	private final Timestamp isColumnValueDate(Object value)
	{
		Timestamp timestamp = null;
		try	
		{
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy",Locale.getDefault());
			formatter.setLenient(false);
			java.util.Date date;
			date = formatter.parse((String) value);
			/*
			 * Recheck if some issues occurs.
			 */
			Timestamp timestampInner = new Timestamp(date.getTime());
			if (value != null && !value.toString().equals(""))
			{
				timestamp = timestampInner;
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
	
	protected Connection getConnection()
	{
		return connection;
	}
	

}
