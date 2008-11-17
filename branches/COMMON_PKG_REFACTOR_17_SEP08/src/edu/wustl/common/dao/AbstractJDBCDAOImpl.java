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
	 * Declared in AbstractDAO class.
	 * @throws DAOException
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{
		try
		{
			initializeAuditManager(sessionDataBean);
			setConnection(getConnectionManager().getConnection());
			getConnection().setAutoCommit(false);
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
		setAuditManager(null);
		getConnectionManager().closeConnection();
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
			getAuditManager().insert(this);
			
			if(getConnection() == null) {
				throw new DAOException(Constants.GENERIC_DATABASE_ERROR);
			}	

			getConnection().commit();
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
			if(getConnection() == null) {
				throw new DAOException(Constants.GENERIC_DATABASE_ERROR);
			}	

			getConnection().rollback();
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
		setAuditManager(new AuditManager());
		if (sessionDataBean == null) {
		
			getAuditManager().setUserId(null);
		} else {
		
			getAuditManager().setUserId(sessionDataBean.getUserId());
			getAuditManager().setIpAddress(sessionDataBean.getIpAddress());
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
			stmt = getConnection().prepareStatement(query);
			stmt.executeUpdate();
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(sqlExp);
		}
		finally
		{
			try
			{
				if (stmt != null) {
					stmt.close();
				}	
			}
			catch (SQLException sqlExp)
			{
				logger.error(sqlExp.getMessage(), sqlExp);
			}
		}
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	protected Statement getConnectionStmt() throws SQLException
	{
		return (Statement)getConnection().createStatement();
	}
	
	/**
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement getPreparedStatement(String query) throws SQLException
	{
		return getConnection().prepareStatement(query);
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
		logger.debug("Create Table Query " + query);
		executeUpdate(query);
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
		
		String[] whereColumnName = null;
		String[] whereColumnCondition = null;
		Object[] whereColumnValue = null;
		String joinCondition = null;
		
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(whereColumnName, whereColumnCondition,
				whereColumnValue,joinCondition);
		
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
		String[] whereColumnName = null;
		String[] whereColumnCondition = null;
		Object[] whereColumnValue = null;
		String joinCondition = null;
		
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(whereColumnName, whereColumnCondition,
				whereColumnValue,joinCondition);
		
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
		String[] whereColumnName = null;
		String[] whereColumnCondition = null;
		Object[] whereColumnValue = null;
		String joinCondition = null;
	
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(whereColumnName, whereColumnCondition,
				whereColumnValue,joinCondition);
		
		return retrieve(sourceObjectName, selectColumnName,queryWhereClauseImpl,
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
	 * @deprecated : This method holds large number of parameters .Use method List<Object> retrieve(String sourceObjectName, String[] selectColumnName,QueryWhereClauseImpl queryWhereClauseImpl,
			 boolean onlyDistinctRows) throws DAOException
	 *//*
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition) throws DAOException
	{
		
		QueryWhereClauseImpl queryWhereClauseImpl = new QueryWhereClauseImpl();
		queryWhereClauseImpl.setWhereClause(whereColumnName, whereColumnCondition,
				whereColumnValue,joinCondition);
		
		
		return retrieve(sourceObjectName, selectColumnName,queryWhereClauseImpl,false);
		
	}*/
	
	public List<Object> retrieve(String sourceObjectName,
			String[] selectColumnName, QueryWhereClauseImpl queryWhereClauseImpl)
			throws DAOException {
	
		return retrieve(sourceObjectName, selectColumnName,queryWhereClauseImpl,false);
	}

	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieve(java.lang.String, java.lang.String, java.lang.Object)
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
	 * @param whereColumnName An array of field names in where clause.
	 * @param whereColumnCondition The comparision condition for the field values. 
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @param onlyDistictRows true if only distict rows should be selected
	 */
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,QueryWhereClauseImpl queryWhereClauseImpl,
			 boolean onlyDistinctRows) throws DAOException
	{
		List<Object> list = null;
		
		QueryWhereClauseJDBCImpl queryWhereClauseJDBCImpl = (QueryWhereClauseJDBCImpl)queryWhereClauseImpl;

		try
		{
			StringBuffer queryStrBuff = getSelectFromQueryPart(selectColumnName, onlyDistinctRows);
			getFromPartOfQuery(sourceObjectName, queryStrBuff);
			
			if(queryWhereClauseJDBCImpl.isConditionSatisfied()) {
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
	 * @param sourceObjectName
	 * @param selectColumnName
	 * @param onlyDistinctRows
	 * @return
	 */
	private StringBuffer getSelectFromQueryPart(String[] selectColumnName,
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
	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap) throws ClassNotFoundException, DAOException
	{
		List pagenatedResultData = null;
			
		if (!(Constants.SWITCH_SECURITY && isSecureExecute && sessionDataBean == null))
		{
			pagenatedResultData = (List)getQueryResultList(query, sessionDataBean, isSecureExecute,hasConditionOnIdentifiedField, queryResultObjectDataMap, -1, -1).getResult();
			
		}
		return pagenatedResultData;
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
		PagenatedResultData pagenatedResultData = null;
		if (!(Constants.SWITCH_SECURITY && isSecureExecute && sessionDataBean == null))
		{
			pagenatedResultData = (PagenatedResultData)getQueryResultList(query, sessionDataBean, isSecureExecute,
					hasConditionOnIdentifiedField, queryResultObjectDataMap, startIndex, noOfRecords);
			
		}
		return pagenatedResultData;
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
	 * 
	 * 
	 * -- TODO have to look into this 
	 */
	public PagenatedResultData getQueryResultList(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap, int startIndex, int noOfRecords) throws DAOException
	{
		PagenatedResultData pagenatedResultData = null;
		//
		
		/*try {
			IQueryExecutor queryExecutor = (IQueryExecutor)Class.forName(queryExecutorClassName).newInstance();
			pagenatedResultData = queryExecutor.getQueryResultList(query, connection,
					sessionDataBean, isSecureExecute, hasConditionOnIdentifiedField,
					queryResultObjectDataMap, startIndex, noOfRecords);
		} catch (InstantiationException e) {
			throw new DAOException(e);
		} catch (IllegalAccessException e) {
			throw new DAOException(e);
		} catch (ClassNotFoundException e) {
			throw new DAOException(e);
		}*/
		return pagenatedResultData;

	}

	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieve(java.lang.String, java.lang.Long)
	 */
	public Object retrieve(String sourceObjectName, Long identifier) throws DAOException
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
	public Object retrieveAttribute(Class<AbstractDomainObject> objClass, Long identifier,
			String attributeName) throws DAOException
	{
		return retrieveAttribute(objClass.getName(), identifier, attributeName);
	}

	/**
	 * @param metaData
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
			if (("DATE").equals(type)) {
				dateColumns.add(Integer.valueOf(i));
			}	
			if (("NUMBER").equals(type)) {
				numberColumns.add(Integer.valueOf(i));
			}	
			if (("TINYINT").equals(type)) {
				tinyIntColumns.add(Integer.valueOf(i));
			}	

		}
		
	}
	
	/**
	 * @param tableName
	 * @param columnValues
	 * @param columnNames_t
	 * @param columnNames
	 * @return
	 * @throws DAOException 
	 * @throws SQLException 
	 * @throws SQLException
	 */
	protected final ResultSetMetaData getMetadataAndUpdatedColumns(String tableName,
			List<String> columnNameslist,List<String>... columnNames) throws DAOException, SQLException
	{
		List<String> columnNames_t = columnNameslist;
		StringBuffer sqlBuff = new StringBuffer("Select ");
		Statement statement = null;
		ResultSet resultSet = null;
		ResultSetMetaData metaData;
		
		try {
			statement = getConnectionStmt();
			if (columnNames != null && columnNames.length > 0) {
				
				columnNames_t = columnNames[0];
				generateSelectSql(columnNames_t, sqlBuff);
				sqlBuff.append(" from " + tableName + " where 1!=1");
				resultSet = statement.executeQuery(sqlBuff.toString());
				metaData = resultSet.getMetaData();

			} else {
				
				sqlBuff.append("* from " + tableName + " where 1!=1");
				resultSet = statement.executeQuery(sqlBuff.toString());
				metaData = resultSet.getMetaData();
				getColumnNames(metaData,columnNames_t);
			}
		} catch (SQLException sqlExp) {
			
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(sqlExp.getMessage(),sqlExp);
			
		} finally {
			
			closeDatabaseConnections(statement, resultSet);
		}
		return metaData;
	}

	private void closeDatabaseConnections(Statement statement,
			ResultSet resultSet) throws SQLException {
		
		if(resultSet != null ) {
			resultSet.close();
		}
		if (statement != null) {
			statement.close();
		}
	}

	private void getColumnNames(ResultSetMetaData metaData,List<String> columnNames_t) throws SQLException {
			
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			columnNames_t.add(metaData.getColumnName(i));
		}
	}

	private void generateSelectSql(List<String> columnNames_t, StringBuffer sql) {
		for (int i = 0; i < columnNames_t.size(); i++)
		{
			sql.append(columnNames_t.get(i));
			if (i != columnNames_t.size() - 1)
			{
				sql.append("  ,");
			}
		}
	}
	
	
	/**
	 * @param tableName
	 * @param columnNames_t
	 * @param columnValues
	 * @return
	 * Have to refractor this !!!!!!! columnValues might not needed
	 * 
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

			if (iterator.hasNext()) {
				query.append(", ");
			} else {
				query.append(") ");
			}	
		}
		return query.toString();
	}
	
	
	/**
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
	protected void setTinyIntColumns(PreparedStatement stmt, int index, Object obj,List<Integer> tinyIntColumns) throws SQLException
	{
		if (tinyIntColumns.contains(Integer.valueOf(index + 1)))
		{	
			setTinyIntColumns(stmt, index, obj);
		}
	}

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
	 * @param value
	 * @return
	 * 
	 */
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
	private Connection getConnection() {
		return connection;
	}

	private void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	private AuditManager getAuditManager() {
		return auditManager;
	}

	private void setAuditManager(AuditManager auditManager) {
		this.auditManager = auditManager;
	}
	
	

}
