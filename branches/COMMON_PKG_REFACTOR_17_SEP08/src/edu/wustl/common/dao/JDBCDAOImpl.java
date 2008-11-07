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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.queryExecutor.IQueryExecutor;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.locator.InterfaceLocator;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.dbmanager.DBUtil;
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

	/**
	 * This method will be used to establish the session with the database.
	 * Declared in AbstractDAO class.
	 *
	 * @throws DAOException
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
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

		try
		{
			//Creates a connection.
			connection = DBUtil.getConnection();// getConnection(database, loginName, password);
			connection.setAutoCommit(false);
		}
		catch (Exception sqlExp)
		{
			//throw new DAOException(sqlExp.getMessage(),sqlExp);
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
		}
	}
	
	/**
	 * This method will be used to close the session with the database.
	 * Declared in AbstractDAO class.
	 * @throws DAOException
	 */
	public void closeSession() throws DAOException
	{
		try
		{
			auditManager = null;
			DBUtil.closeConnection();
			//        	if (connection != null && !connection.isClosed())
			//        	    connection.close();
		}
		catch (Exception sqlExp)
		{
			//            new DAOException(sqlExp.getMessage(),sqlExp);
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);

		}
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

			if (connection != null)
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
	 */
	public void rollback()
	{
		try
		{
			if (connection != null)
				connection.rollback();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
		}
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
	
	public Statement getConnectionStmt() throws SQLException
	{
		Statement statement = connection.createStatement();
		return statement;
	}
	
	public PreparedStatement getPreparedStatement(String query) throws SQLException
	{
		return(connection.prepareStatement(query));
		
	}	

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

	private StringBuffer getSelectFromQueryPart(String sourceObjectName, String[] selectColumnName,
			boolean onlyDistinctRows)
	{
		StringBuffer query = new StringBuffer("SELECT ");

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
		
		//return executeQuery(query, sessionDataBean, isSecureExecute, false,
			//	queryResultObjectDataMap, -1, -1).getResult();
		
		return getQueryResultList(query, sessionDataBean, isSecureExecute,
				false, queryResultObjectDataMap, -1, -1).getResult();
	
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

		/*return executeQuery(query, sessionDataBean, isSecureExecute, hasConditionOnIdentifiedField,
				queryResultObjectDataMap, -1, -1).getResult();*/
		
		if (Constants.SWITCH_SECURITY && isSecureExecute)
		{
			if (sessionDataBean == null)
			{
				//logger.out.debug("Session data is null");
				return null;
			}
		}
		
		
		return getQueryResultList(query, sessionDataBean, isSecureExecute,
				hasConditionOnIdentifiedField, queryResultObjectDataMap, -1, -1).getResult();
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
	public PagenatedResultData getQueryResultList(String query, SessionDataBean sessionDataBean,
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

	
	
	

}
