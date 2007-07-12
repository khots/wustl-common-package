/**
 * <p>Title: Query Executor Class </p>
 * <p>Description:  AbstractQueryExecutor class is a base class which contains code to execute the sql query to get the results from database. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author prafull_kadam
 * @version 1.00
 * Created on June 29, 2007
 */

package edu.wustl.common.dao.queryExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * @author prafull_kadam
 * Query Executor class, for executing SQL on batabase & getting corresponding results. 
 * This is abstract class, sql execution implementation for specific type of database should be implemented in the derived class of this class.
 */
public abstract class AbstractQueryExecutor
{

	/**
	 * Holds the reference to coonnection object.
	 */
	protected Connection connection;
	/**
	 * Holds refernce to the statement object.
	 */
	protected PreparedStatement stmt = null;
	/**
	 * Holds refernce to the resultSet object.
	 */
	protected ResultSet resultSet = null;
	/**
	 * The SQL to be executed.
	 */
	protected String query;
	/**
	 * Holds refernce to the SessionDataBean object.
	 */
	protected SessionDataBean sessionDataBean;
	/**
	 * Booleans variables required for query execution like security check etc.
	 */
	protected boolean isSecureExecute, hasConditionOnIdentifiedField;

	/**
	 * Boolean variable, will have value true if call is made to get sublist of the total query result by passing StartIndex & totalNoOfRecords.
	 */
	protected boolean getSublistOfResult;
	/**
	 * Map of QueryResultObjectData, used for security checks & handle identified data.
	 */
	protected Map queryResultObjectDataMap;
	/**
	 * Start index in the Query Resultset. & no of records to fetch from the query result.
	 */
	protected int startIndex, noOfRecords;

	/**
	 * Constants required for forming/changing SQL.
	 */
	private static final String SELECT_CLAUSE = "SELECT";
	private static final String FROM_CLAUSE = "FROM";
	private static final String DISTINCT_CLAUSE = "DISTINCT ";

	/**
	 * Method to get the Query executor instance. 
	 * This will return instance of the query executor object depending upon Variables.databaseName value.
	 * @return The instance of the Query executor object.
	 */
	public static AbstractQueryExecutor getInstance()
	{
		if (Variables.databaseName.equals(Constants.MYSQL_DATABASE))
		{
			return new MysqlQueryExecutor();
		}
		else
		{
			return new OracleQueryExecutor();
		}
	}

	/**
	 * To get the Query execution results.
	 * @param query The SQL to be executed.
	 * @param connection The JDBC connection object.
	 * @param sessionDataBean The refernce to SessionDataBean object.
	 * @param isSecureExecute security check parameter.
	 * @param hasConditionOnIdentifiedField security check parameter.
	 * @param queryResultObjectDataMap Map of QueryResultObjectData.
	 * @param startIndex Start index in the Query Resultset.
	 * @param noOfRecords no of records to fetch from the query result.
	 * @return The Query Execution results.
	 * @throws DAOException if there an error occures while executing query.
	 */
	public final PagenatedResultData getQueryResultList(String query, Connection connection,
			SessionDataBean sessionDataBean, boolean isSecureExecute,
			boolean hasConditionOnIdentifiedField, Map queryResultObjectDataMap, int startIndex,
			int noOfRecords) throws DAOException
	{
		this.query = query;
		this.connection = connection;
		this.sessionDataBean = sessionDataBean;
		this.isSecureExecute = isSecureExecute;
		this.hasConditionOnIdentifiedField = hasConditionOnIdentifiedField;
		this.queryResultObjectDataMap = queryResultObjectDataMap;
		this.startIndex = startIndex;
		this.noOfRecords = noOfRecords;
		this.getSublistOfResult = startIndex != -1; // this will be used, when it required to get sublist of the result set.
		
		/**
		 * setting noOfRecords = Integer.MAX_VALUE, if All records are expected from result. see getListFromResultSet method
		 */
		if (!getSublistOfResult)
		{
			this.noOfRecords = Integer.MAX_VALUE;
		}
		
		PagenatedResultData pagenatedResultData = null;
		try
		{
			pagenatedResultData = createStatemtentAndExecuteQuery();
		}
		catch (SQLException sqlExp)
		{
			Logger.out.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
		}
		finally
		{
			Logger.out.debug("Query Execution on MySQL Completed...");
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
		return pagenatedResultData;
	}

	/**
	 * This method will create Statement object, execute the query & returns the query Results, which will contain the Result list based on start index & page size & total no. of records that query can return.
	 * Subclasses of this class must provide its own implementation for executing the query & getting results.
	 * @return The reference to PagenatedResultData object, which will have pagenated data list & total no. of records that query can return.
	 * @throws SQLException
	 */
	protected abstract PagenatedResultData createStatemtentAndExecuteQuery() throws SQLException;

	/**
	 * To process the ResultSet object & create Results in the format List<List<String>>. 
	 * @return The Result list.
	 * @throws SQLException
	 */
	protected List getListFromResultSet() throws SQLException
	{
		ResultSetMetaData metaData = resultSet.getMetaData();

		int columnCount = metaData.getColumnCount();

		for (int i = 1; i <= columnCount; i++)
		{
			Logger.out.debug("Column " + i + " : " + metaData.getColumnClassName(i) + " "
					+ metaData.getColumnName(i) + " " + metaData.getTableName(i));
			//;
		}
		int recordCount = 0;
		List list = new ArrayList();
		
		/**
		 * noOfRecords will hold value = Integer.MAX_VALUE when All records are expected from result. 
		 */
		while (resultSet.next() && recordCount < noOfRecords)
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
				boolean hasPrivilegeOnIdentifiedData = SecurityManager.getInstance(this.getClass())
						.hasPrivilegeOnIdentifiedData(sessionDataBean, queryResultObjectDataMap,
								aList);
				if (!hasPrivilegeOnIdentifiedData)
					continue;
			}

			//Aarti: Checking object level privileges on each record
			if (Constants.switchSecurity && isSecureExecute)
			{
				if (sessionDataBean != null & sessionDataBean.isSecurityRequired())
				{
					SecurityManager.getInstance(this.getClass()).filterRow(sessionDataBean,
							queryResultObjectDataMap, aList);
				}
			}

			list.add(aList);

			recordCount++;
		}
		return list;
	}

	/**
	 * To modify the SQL, to get the required no. of records with the given offset from the query.
	 * For query like "Select id, first_name from catissue_participant where id > 0" will be modifed as follows:
	 * For Oracle: "Select id, first_name from (Select rownum rn, id, first_name from catissue_participant where id > 0) where rn between startIndex AND lastIndex"
	 * For MySQL : "Select id, first_name from catissue_participant where id > 0 limit startIndex, noOfRecords"
	 * @param sql The SQL to be executed on database
	 * @param startIndex The offset, or the starting index. 
	 * @param noOfRecords The totalnumber of records to fetch from the query.
	 * @return The modified SQL. 
	 */
	protected String putPageNumInSQL(String sql, int startIndex, int noOfRecords)
	{
		StringBuffer newSql = new StringBuffer();
		if (Variables.databaseName.equals(Constants.MYSQL_DATABASE))
		{
			// Add limit clause for the MYSQL case
			newSql.append(sql).append(" Limit ").append(startIndex).append(" , ").append(
					noOfRecords);
		}
		else
		{
			//Add rownum condition to the query, by forming inner query.
			String upperCaseSQL = sql.toUpperCase();
			int index = upperCaseSQL.indexOf(SELECT_CLAUSE) + SELECT_CLAUSE.length();
			int fromIndex = upperCaseSQL.indexOf(FROM_CLAUSE);

			String selectAttributes = sql.substring(index, fromIndex).trim();

			// need to handle distinct clause, this clause will be added in outer query.
			boolean addDistinctClause = false;
			if (selectAttributes.startsWith(DISTINCT_CLAUSE))
			{
				selectAttributes = selectAttributes.substring(DISTINCT_CLAUSE.length());
				selectAttributes.trim();
				addDistinctClause = true;
			}
			String[] selectAttributeArray = selectAttributes.split(",");

			StringBuffer outerQuerySelectAttributes = new StringBuffer(" ");

			for (int i = 0; i < selectAttributeArray.length; i++)
			{
				String attribute[] = selectAttributeArray[i].trim().split(" ");
				String attributeName = attribute[0];
				if (attribute.length > 1)
				{
					attributeName = attribute[attribute.length - 1];
				}
				outerQuerySelectAttributes.append(attributeName).append(" ,");
			}
			int outerQuerySelectAttributesLength = outerQuerySelectAttributes.length();
			outerQuerySelectAttributes.delete(outerQuerySelectAttributesLength - 2,
					outerQuerySelectAttributesLength);
			newSql.append(SELECT_CLAUSE).append(" ");

			if (addDistinctClause)
				newSql.append(DISTINCT_CLAUSE);

			newSql.append(outerQuerySelectAttributes).append(" FROM (").append(SELECT_CLAUSE)
					.append(" rownum rn, ").append(selectAttributes).append(" ").append(
							sql.substring(fromIndex)).append(")").append(" WHERE rn BETWEEN ")
					.append(startIndex).append(" AND ").append(startIndex + noOfRecords - 1);
		}
		return newSql.toString();
	}

	/**
	 * To form the SQL query to get the count of the records for the given query.  
	 * @param originalQuery the SQL string
	 * @return The SQL query to get the count of the records for the given originalQuery.
	 */
	protected String getCountQuery(String originalQuery)
	{
		return "Select count(*) from (" + originalQuery + ") alias";
	}
}
