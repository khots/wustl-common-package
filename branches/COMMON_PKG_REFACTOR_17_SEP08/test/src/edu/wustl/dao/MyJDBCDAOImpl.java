package edu.wustl.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.querydatabean.QueryDataBean;
import edu.wustl.common.util.QueryParams;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.exception.DAOException;


public class MyJDBCDAOImpl implements JDBCDAO
{

	public void createTable(String query) throws DAOException
	{
	}

	public void createTable(String tableName, String[] columnNames) throws DAOException
	{
	}

	public void delete(String tableName) throws DAOException
	{
	}

	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map<Object, QueryDataBean> queryResultObjectDataMap)
			throws ClassNotFoundException, DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void executeUpdate(String sql) throws DAOException
	{
		// TODO Auto-generated method stub

	}
	PagenatedResultData executeQuery(QueryParams  queryParams)throws ClassNotFoundException, DAOException
	{
		return null;
	}

	public String formatMessage(Exception excp, Object[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDateFormatFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDatePattern()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDateTostrFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getStrTodateFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTimeFormatFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTimePattern()
	{
		// TODO Auto-generated method stu 
		return null;
	}

	public void insertHashedValues(String tableName, List<Object> columnValues,
			List<String> columnNames) throws DAOException, SQLException
	{
		// TODO Auto-generated method stub

	}

	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			boolean onlyDistinctRows) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClauseImpl, boolean onlyDistinctRows) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void closeSession() throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void commit() throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void delete(Object obj) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void disableRelatedObjects(String tableName, String whereColumnName,
			Long[] whereColumnValues) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public IConnectionManager getConnectionManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureInsert) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public List<Object> retrieve(String sourceObjectName) throws DAOException
	{

		return null;
	}

	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName)
			throws DAOException
	{
		return null;
	}

	public Object retrieve(String sourceObjectName, Long identifier) throws DAOException
	{
		return null;
	}

	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause) throws DAOException
	{
		return null;
	}

	public List<Object> retrieve(String sourceObjectName, String whereColumnName,
			Object whereColumnValue) throws DAOException
	{
		return null;
	}

	public Object retrieveAttribute(Class objClass, Long identifier, String attributeName,
			String columnName) throws DAOException
	{
		return null;
	}

	public void rollback() throws DAOException
	{

	}

	public void setConnectionManager(IConnectionManager connectionManager)
	{

	}

	public void update(Object obj) throws DAOException
	{

	}

}
