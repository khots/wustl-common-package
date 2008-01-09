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
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querysuite.security.utility.CsmCache;
import edu.wustl.common.querysuite.security.utility.CsmCacheManager;
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
			Logger.out.debug("Query Execution on MySQL Completed...");
		}
		catch (SQLException sqlExp)
		{
			Logger.out.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
		}
		
		finally
		{			
			try
			{
				
				if (resultSet != null)
					resultSet.close();

				if (stmt != null)
					stmt.close();

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
		
		boolean isLongKeyOfMap = false;
		if(queryResultObjectDataMap!=null && !queryResultObjectDataMap.isEmpty())
		{
			Iterator mapIterator = queryResultObjectDataMap.keySet().iterator();
			while(mapIterator.hasNext())
			{
				if (mapIterator.next() instanceof Long)
				{
					isLongKeyOfMap = true;	
					break;
				}
			}
		}

		int columnCount = metaData.getColumnCount();
		
		/**
		 * Name: Prafull
		 * Reviewer: Aarti
		 * Bug: 4857,4865
		 * Description: Changed Query modification logic for Oracle.
		 * 
		 * For oracle queries extra rownum is added in SELECT clause as last attribute in SELECT clause of query for paginated results, 
		 * so no need to process that extra rownum column.
		 * @see edu.wustl.common.dao.queryExecutor.AbstractQueryExecutor#putPageNumInSQL(java.lang.String,int,int)
		 */
		if (Variables.databaseName.equals(Constants.ORACLE_DATABASE) && getSublistOfResult)
		{
			columnCount--;
		}
		
		int recordCount = 0;
		List list = new ArrayList();
		 
		CsmCacheManager cacheManager = new CsmCacheManager(connection);
		CsmCache cache = cacheManager.getNewCsmCacheObject();
		
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
					
					//Abhijit: Why used instanceof? 
					// why not metaData.getColumnType(i) == Types.CLOB ??
					
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
			if(!isLongKeyOfMap && queryResultObjectDataMap!=null)
			{
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
			}else
			{
				if (Constants.switchSecurity && hasConditionOnIdentifiedField && isSecureExecute)
				{
					boolean hasPrivilegeOnIdentifiedData = SecurityManager.getInstance(this.getClass())
							.hasPrivilegeOnIdentifiedDataNew(sessionDataBean, queryResultObjectDataMap, aList);
					if (!hasPrivilegeOnIdentifiedData)
						continue;
				}

				//Aarti: Checking object level privileges on each record
				if (Constants.switchSecurity && isSecureExecute)
				{ 
					if (sessionDataBean != null & sessionDataBean.isSecurityRequired())
					{ 
						//SecurityManager.getInstance(this.getClass()).filterReasultRow1(sessionDataBean, queryResultObjectDataMap, aList,cache);
						//Supriya :call filterRow of method of csm cache manager changed for csm-query performance issue. 
						cacheManager.filterRow(sessionDataBean, queryResultObjectDataMap, aList,cache );
					}
				}
			}

			list.add(aList);

			recordCount++;
		}
		return list;
	}

	/**
	 * To modify the SQL, to get the required no. of records with the given offset from the query.
	 * For query like "Select id, first_name from catissue_participant where id > 0 order by id" will be modifed as follows:
	 * For Oracle: "Select * from (Select qry.*, rownum rn From (Select rownum rn, id, first_name from catissue_participant where id > 0 order by id) qry where rownum <= lastindex) where rn > startIndex"
	 * For MySQL : "Select id, first_name from catissue_participant where id > 0 order by id limit startIndex, noOfRecords"
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
			/**
			 * Name: Prafull
			 * Reviewer: Aarti
			 * Bug: 4857,4865
			 * Description: Changed Query modification logic for Oracle.
			 * 
			 * forming new query, by using original query as inner query & adding rownum conditions in outer query.
			 */
			newSql.append(SELECT_CLAUSE).append(" * ").append(FROM_CLAUSE).append(" (").append(SELECT_CLAUSE)
			.append(" qry.*, ROWNUM rn ").append(FROM_CLAUSE).append(" (").append(sql).append(") qry WHERE ROWNUM <= ")
			.append(startIndex + noOfRecords).append(") WHERE rn > ")
			.append(startIndex);
// Another approach to form simillar query by putting both rownum conditions in the outer query.			
//			newSql.append(SELECT_CLAUSE).append(" * ").append(FROM_CLAUSE).append(" (").append(SELECT_CLAUSE)
//			.append(" qry.*, ROWNUM rn ").append(FROM_CLAUSE).append(" (").append(sql).append(") qry ) WHERE rn BETWEEN ")
//			.append(startIndex+1).append(" AND ").append(startIndex + noOfRecords);;
			
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
