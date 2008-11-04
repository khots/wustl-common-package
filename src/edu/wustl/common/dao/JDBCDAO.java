
package edu.wustl.common.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.util.dbmanager.DAOException;

/** This interface defines methods which are specific to JDBC operations.*/
public interface JDBCDAO extends DAO
{

	/**
	 * Creates a table with the query specified.
	 * @param query Query create table.
	 * @throws DAOException generic DAOException.
	 */
	void createTable(String query) throws DAOException;

	/**
	* Returns the ResultSet containing all the rows according to the columns specified
	* from the table represented in sourceObjectName.
	* @param sourceObjectName The table name.
	* @param selectColumnName The column names in select clause.
	* @param onlyDistinctRows true if only distict rows should be selected
	* @return The ResultSet containing all the rows according to the columns specified
	* from the table represented in sourceObjectName.
	* @throws DAOException generic DAOException.
	*/
	List retrieve(String sourceObjectName, String[] selectColumnName, boolean onlyDistinctRows)
			throws DAOException;

	/**
	   * Retrieves the records for class name in sourceObjectName
	   * according to field values passed in the passed session.
	   * @param sourceObjectName The table name.
	   * @param selectColumnName An array of field names in select clause.
	   * @param whereColumnName An array of field names in where clause.
	   * @param whereColumnCondition The comparision condition for the field values.
	   * @param whereColumnValue An array of field values.
	   * @param joinCondition The join condition.
	   * @param onlyDistinctRows true if only distict rows should be selected.
	   * @return The ResultSet containing all the rows according to the columns specified
	   * from the table represented in sourceObjectName.
	   * @throws DAOException generic DAOException.
	   */
	List retrieve(String sourceObjectName, String[] selectColumnName, String[] whereColumnName,
			String[] whereColumnCondition, Object[] whereColumnValue, String joinCondition,
			boolean onlyDistinctRows) throws DAOException;

	/**
	   * Executes the query.
	   * @param query query to be execute.
	   * @param sessionDataBean session specific Data.
	   * @param isSecureExecute is Secure Execute.
	   * @param queryResultObjectDataMap query Result Object Data Map.
	   * @return list.
	   * @throws ClassNotFoundException Class Not Found Exception.
	   * @throws DAOException generic DAOException.
	   */
	List executeQuery(String query, SessionDataBean sessionDataBean, boolean isSecureExecute,
			Map queryResultObjectDataMap) throws ClassNotFoundException, DAOException;

	/**
	   * Executes the query.
	   * @param query query  to be execute.
	   * @param sessionDataBean session specific Data.
	   * @param isSecureExecute is Secure Execute.
	   * @param hasConditionOnIdentifiedField hasConditionOnIdentifiedField.
	   * @param queryResultObjectDataMap query Result Object Data Map.
	   * @return list.
	   * @throws ClassNotFoundException Class Not Found Exception.
	   * @throws DAOException generic DAOException.
	   */
	List executeQuery(String query, SessionDataBean sessionDataBean, boolean isSecureExecute,
			boolean hasConditionOnIdentifiedField, Map queryResultObjectDataMap)
			throws ClassNotFoundException, DAOException;

	/**
	 * Executes the query & returns the results specified by the offset values i.e. startIndex & noOfRecords.
	 * @param query The SQL String
	 * @param sessionDataBean Session databeab reference
	 * @param isSecureExecute is Secure Execute.
	 * @param hasConditionOnIdentifiedField has Condition On Identified Field.
	 * @param queryResultObjectDataMap query Result Object Data Map.
	 * @param startIndex start Index.
	 * @param noOfRecords no Of Records.
	 * @return PagenatedResultData.
	 * @throws ClassNotFoundException Class Not Found Exception
	 * @throws DAOException generic DAOException.
	 */
	PagenatedResultData executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap, int startIndex, int noOfRecords)
			throws ClassNotFoundException, DAOException;

	/**
	 * Inserts records in given table.
	 * @param tableName Name of the table in which record to be inserted
	 * @param columnValues column data
	 * @throws DAOException generic DAOException
	 * @throws SQLException SQL Exception.
	 */
	void insert(String tableName, List columnValues) throws DAOException, SQLException;

	/**
	* Inserts records in given table.
	* @param tableName Name of the table in which record to be inserted
	* @param columnValues column data
	* @param columnNames (optional)column names- if not not provided,
	* all coumn names of the table are added to the list of column names
	* @throws DAOException generic DAOException
	* @throws SQLException SQL Exception.
	*/
	void insert(String tableName, List columnValues, List<String>... columnNames)
			throws DAOException, SQLException;

	/**
	   * Creates a table with the name and columns specified.
	   * @param tableName Name of the table to create.
	   * @param columnNames Columns in the table.
	   * @throws DAOException generic DAOException
	   */
	void create(String tableName, String[] columnNames) throws DAOException;

	/**
	   * Deletes the specified table.
	   * @param tableName Name of the table to delete.
	   * @throws DAOException generic DAOException.
	   */
	void delete(String tableName) throws DAOException;

	/**
	 * This method gets Activity Status.
	 * @param sourceObjectName The table name.
	 * @param indetifier indetifier
	 * @return Activity Status.
	 * @throws DAOException generic DAOException.
	 */
	String getActivityStatus(String sourceObjectName, Long indetifier) throws DAOException;

	/**
	 * Update the database.
	 * @param sql sql statement.
	 * @throws DAOException generic DAOException.
	 */
	void executeUpdate(String sql) throws DAOException;

}
