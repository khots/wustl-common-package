package edu.wustl.common.bizlogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * This is an interface for QueryBizLogic.
 * @author ravi_kumar
 *
 */
public interface IQueryBizLogic
{
	Map getQueryObjectNameTableNameMap();
	Map getPivilegeTypeMap();
	Map getRelationData();
	void initializeQueryData();
	String getAliasName(String columnName, Object columnValue) throws DAOException;
	List getColumnNames(String value) throws DAOException, ClassNotFoundException;
	Set getNextTableNames(String prevValue) throws DAOException, ClassNotFoundException;
	String getDisplayName(String aliasName) throws DAOException, ClassNotFoundException;
	String getDisplayNamebyTableName(String tableName) throws DAOException,ClassNotFoundException;
	Set getAllTableNames(String aliasName, int forQI) throws DAOException,ClassNotFoundException;
	List getMainObjectsOfQuery() throws DAOException;
	List getRelatedTableAliases(String aliasName) throws DAOException;
	Set setTablesInPath(Long parentTableId, Long childTableId) throws DAOException,ClassNotFoundException;
	String getAttributeType(String columnName, String aliasName) throws DAOException,ClassNotFoundException;
	String getTableIdFromAliasName(String aliasName) throws DAOException;
	List setColumnNames(String aliasName) throws DAOException, ClassNotFoundException;
	List getColumnNames(String aliasName, boolean defaultViewAttributesOnly)
			throws DAOException, ClassNotFoundException;
	String getSpecimenTypeCount(String specimanType, JDBCDAO jdbcDAO) throws DAOException,
	ClassNotFoundException;
	Map<String, Object> getTotalSummaryDetails() throws DAOException;
	void insertQueryForMySQL(String sqlQuery, SessionDataBean sessionData, JDBCDAO jdbcDAO)
			throws DAOException, ClassNotFoundException;
	void insertQueryForOracle(String sqlQuery, SessionDataBean sessionData, JDBCDAO jdbcDAO)
	throws DAOException, ClassNotFoundException, SQLException, IOException;

	void insertQuery(String sqlQuery, SessionDataBean sessionData) throws DAOException,
	ClassNotFoundException;

	PagenatedResultData execute(SessionDataBean sessionDataBean,
			QuerySessionData querySessionData, int startIndex) throws DAOException;

	List executeSQL(String sql) throws DAOException, ClassNotFoundException;

}
