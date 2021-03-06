/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

									 /**
 * <p>Title: JDBCDAO Class>
 * <p>Description:	JDBCDAO is default implementation of AbstractDAO through JDBC.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.dao;

import java.io.Serializable;
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
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * Default implementation of AbstractDAO through JDBC.
 * @author gautam_shetty
 */
public class JDBCDAOImpl implements JDBCDAO
{

	private Connection connection = null;
	protected AuditManager auditManager;

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
		}
		catch (Exception sqlExp)
		{
			//throw new DAOException(sqlExp.getMessage(),sqlExp);
			Logger.out.error(sqlExp.getMessage(), sqlExp);
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
			Logger.out.error(sqlExp.getMessage(), sqlExp);
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
			Logger.out.error(dbex.getMessage(), dbex);
			//throw new DAOException("Error in commit", dbex);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, dbex);
		}
	}

	/**
	 * Rollback all the changes after last commit. 
	 * Declared in AbstractDAO class. 
	 * @throws DAOException
	 */
	public void rollback() throws DAOException
	{
		try
		{
			if (connection != null)
				connection.rollback();
		}
		catch (SQLException dbex)
		{
			Logger.out.error(dbex.getMessage(), dbex);
			//new DAOException("Error in rollback", dbex);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, dbex);
		}
	}

	/**
	 * Creates a table with the query specified.
	 * @param query Query create table.
	 * @throws DAOException
	 */
	public void createTable(String query) throws DAOException
	{
		Logger.out.debug("Create Table Query " + query.toString());
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
		//Logger.out.debug(" Only distinct rows:" + onlyDistinctRows);
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
		//Logger.out.debug(" Only distinct rows:" + onlyDistinctRows);
		List list = null;

		try
		{
			StringBuffer query = new StringBuffer("SELECT ");

			if (joinCondition == null)
			{
				joinCondition = Constants.AND_JOIN_CONDITION;
			}

			//Prepares the select clause of the query.
			if ((selectColumnName != null) && (selectColumnName.length > 0))
			{
				//Bug# 2003: Limiting the define view does not remove duplicates
				if (onlyDistinctRows)
				{
					//Logger.out.debug(" Adding distinct to query ");
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
					query.append(" " + joinCondition + " ");
				}
				query.append(sourceObjectName + "." + whereColumnName[i] + " "
						+ whereColumnCondition[i] + " " + whereColumnValue[i]);
			}
			Logger.out.debug("JDBC Query " + query);
			list = executeQuery(query.toString(), null, false, null);
		}
		catch (ClassNotFoundException classExp)
		{
			Logger.out.error(classExp.getMessage(), classExp);
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
		//Aarti: Security checks
		if (Constants.switchSecurity && isSecureExecute)
		{
			if (sessionDataBean == null)
			{
				//Logger.out.debug("Session data is null");
				return null;
			}
		}
		List list = getQueryResultList(query, sessionDataBean, isSecureExecute, false,
				queryResultObjectDataMap);
		return list;
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
		//Aarti: Security checks
		if (Constants.switchSecurity && isSecureExecute)
		{
			if (sessionDataBean == null)
			{
				//Logger.out.debug("Session data is null");
				return null;
			}
		}
		List list = getQueryResultList(query, sessionDataBean, isSecureExecute,
				hasConditionOnIdentifiedField, queryResultObjectDataMap);
		return list;
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
	 * @param list
	 * @return
	 * @throws DAOException
	 */
	private List getQueryResultList(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap) throws DAOException
			{
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		List list;

		try
		{
			stmt = connection.prepareStatement(query);
			resultSet = stmt.executeQuery();

			list = new ArrayList();

			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();

			for (int i = 1; i <= columnCount; i++)
			{
				Logger.out.debug("Column " + i + " : " + metaData.getColumnClassName(i) + " "
						+ metaData.getColumnName(i) + " " + metaData.getTableName(i));
				//;
			}

			while (resultSet.next())
			{
				int i = 1;

				List aList = new ArrayList();
				while (i <= columnCount)
				{

					if (resultSet.getObject(i) != null)
					{

						Object valueObj = resultSet.getObject(i);
						if (valueObj instanceof oracle.sql.CLOB)
						{
							aList.add(valueObj);
						}
						else
						{
							String value;
							// Sri: Added check for date/time/timestamp since the
							// default date format returned by toString was yyyy-dd-mm
							// bug#463 
							if (valueObj instanceof java.util.Date) // since all java.sql time 
								//classes are derived from java.util.Date 
							{
								SimpleDateFormat formatter = new SimpleDateFormat(
										Constants.DATE_PATTERN_MM_DD_YYYY);
								value = formatter.format((java.util.Date) valueObj);
							}
							else
							{
								value = valueObj.toString();
							}
							aList.add(value);
						}
					}
					else
					{
						aList.add("");
					}
					i++;
				}

				//Aarti: If query has condition on identified data then check user's permission
				//on the record's identified data.
				//If user does not have privilege don't add the record to results list
				//bug#1413
				if (Constants.switchSecurity && hasConditionOnIdentifiedField && isSecureExecute)
				{
					boolean hasPrivilegeOnIdentifiedData = SecurityManager.getInstance(
							this.getClass()).hasPrivilegeOnIdentifiedData(sessionDataBean,
									queryResultObjectDataMap, aList);
					if (!hasPrivilegeOnIdentifiedData)
						continue;
				}

				//Aarti: Checking object level privileges on each record
				if (Constants.switchSecurity && isSecureExecute)
				{
					SecurityManager.getInstance(this.getClass()).filterRow(sessionDataBean,
							queryResultObjectDataMap, aList);
				}

				list.add(aList);
			}
		}
		catch (SQLException sqlExp)
		{
			//throw new DAOException(sqlExp.getMessage(), sqlExp);
			Logger.out.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();

				if (resultSet != null)
					resultSet.close();
			}
			catch (SQLException ex)
			{
				//throw new DAOException(ex.getMessage(), ex);
				Logger.out.error(ex.getMessage(), ex);
				throw new DAOException(Constants.GENERIC_DATABASE_ERROR, ex);
			}
		}
		return list;
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
		//Logger.out.debug("Column value: " + value);
		try
		{
			DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
			formatter.setLenient(false);
			java.util.Date date = formatter.parse((String) value);
			Timestamp t = new Timestamp(date.getTime());
			// Date sqlDate = new Date(date.getTime());

			//Logger.out.debug("Column date value: " + date);
			if (value.toString().equals("") == false)
			{
				//Logger.out.debug("Return true: " + value);
				return t;
			}
		}
		catch (Exception e)
		{

		}
		return null;
	}

	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.AbstractDAO#insert(java.lang.Object)
	 */
	public void insert(String tableName, List columnValues) throws DAOException, SQLException
	{
		//Get metadate for temp table to set default values in date fields
		String sql = "Select * from " + tableName;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql.toString());
		ResultSetMetaData metaData = resultSet.getMetaData();
		//Make a list of Date columns
		List dateColumns = new ArrayList();
		List numberColumns = new ArrayList();
				
		for (int i = 1; i <= metaData.getColumnCount(); i++)
		{
			String type = metaData.getColumnTypeName(i);

			if (type.equals("DATE"))
				dateColumns.add(new Integer(i));
			if (type.equals("NUMBER"))
				numberColumns.add(new Integer(i));
		}
		resultSet.close();
		statement.close();
		StringBuffer query = new StringBuffer("INSERT INTO " + tableName + " values(");
		int i;

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
			for (i = 0; i < columnValues.size(); i++)
			{
				Object obj = columnValues.get(i);
				/**
				 * For Number -1 is used as MarkUp data For Date 1-1-9999 is used as markUp data.
				 * Please refer bug 3576
				 */

				if (dateColumns.contains(new Integer(i + 1)) && obj.toString().equals("##"))
				{
					java.util.Date date = null;
					try {
						date = Utility.parseDate("1-1-9999","mm-dd-yyyy");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Date sqlDate = new Date(date.getTime());
					stmt.setDate(i + 1, sqlDate);
				}
				else
				{
					Timestamp date = isColumnValueDate(obj);
					if (date != null)
					{
						stmt.setObject(i + 1, date);
						//Logger.out.debug("t.toString(): " + "---" + date);
					}
					else
					{
						if (numberColumns.contains(new Integer(i + 1)) && obj.toString().equals("##"))
						{
							stmt.setObject(i + 1, new Integer(-1));
						}
						else
						{
						stmt.setObject(i + 1, obj);
					}
				}
			}
			}
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

		Logger.out.debug("Create Table*************************" + query.toString());

		executeUpdate(query.toString());
	}

	/**
	 * Deletes the specified table
	 * @param tableName
	 * @throws DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		StringBuffer query; 
		if (Variables.databaseName.equals(Constants.MYSQL_DATABASE))
		{
			Logger.out.debug("MYSQL*****************************");
			query = new StringBuffer("DROP TABLE IF EXISTS " + tableName);
			executeUpdate(query.toString());
		}
		else
		{
			Logger.out.debug("ORACLE*****************************");
			query = new StringBuffer("select tname from tab where upper(tname)=" + "upper('"+ tableName + "')");
			try
			{
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query.toString());
				boolean isTableExists = rs.next();
				Logger.out.debug("ORACLE****" + query.toString() + isTableExists);
				if (isTableExists)
				{
					Logger.out.debug("Drop Table");
					executeUpdate("DROP TABLE " + tableName + " cascade constraints");
				}
				rs.close();
				statement.close();
			}
			catch (Exception sqlExp)
			{
				Logger.out.error(sqlExp.getMessage(), sqlExp);
				throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
			}
		}

	}

	/**
	 * (non-Javadoc)
	 * @see edu.wustl.common.dao.DAO#retrieve(java.lang.String, java.lang.Long)
	 */
	public Object retrieve(String sourceObjectName, Serializable id) throws DAOException
	{
		try
		{
			return null;
		}
		catch (Exception hibExp)
		{
			Logger.out.error(hibExp.getMessage(), hibExp);
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
	/**
	 * Returns the connection.
	 * @return Connection Connection
	 * @throws DAOException DAOException
	 */
	public Connection getConnection() throws DAOException
	{
		return connection;
	}

}