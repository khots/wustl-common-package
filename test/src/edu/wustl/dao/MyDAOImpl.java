package edu.wustl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.common.domain.MyDomainObject;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.querydatabean.QueryDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.query.generator.QueryData;
import edu.wustl.dao.util.NamedQueryParam;
import edu.wustl.dao.util.StatementData;


public class MyDAOImpl implements DAO, HibernateDAO, IDAOFactory, JDBCDAO, IConnectionManager
{

	public static boolean isTestForFail=false;
	public static boolean identifierList=false;
	public static boolean isTestActivityStatus=false;
	public static boolean returnActivityStatusObj=false;
	public static boolean returnUserPassword=false;

	/**
	 * List to return from retrieve methods.
	 */
	public static List<Object> list;

	public static String object="retObject";
	/**
	 * @return returns dummy list.
	 * @throws DAOException  throw DAOException when isTestForFail=true
	 */
	public static List<Object> getList() throws DAOException
	{
		list= new ArrayList<Object>();
		if(isTestForFail)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,new Exception(""),"MyJDAOImpl.java :");
		}
		else if(identifierList)
		{
			list.add(Long.valueOf(1));
			list.add(Long.valueOf(2));
			list.add(Long.valueOf(3));
		}
		else if(isTestActivityStatus)
		{
			list.add("ActivityStatus");
		}
		else
		{
			Object []list1= {"1","John","Enterprise"};
			Object []list2= {"2","Raghu","IBM"};
			Object []list3= {"3","Bame","Enterprise"};
			list.add(list1);
			list.add(list2);
			list.add(list3);
		}
		return (List<Object>)list;
	}
	public static void throwDaoException() throws DAOException
	{
		if(isTestForFail)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,new Exception(""),"MyJDAOImpl.java :");
		}
	}
	public Object getObject() throws DAOException
	{
		throwDaoException();
		MyDomainObject myDomainObject = new MyDomainObject();
		return myDomainObject;
	}
	public String getObjectAttribute() throws DAOException
	{
		throwDaoException();
		return object;
	}
	public String  formatMessage(Exception exception, String message)
	{
		return null;
	}
	public String  formatMessage(Exception excp,Connection connection)
	{
		return null;
	}

	public void setIsDefaultDAOFactory(Boolean isDefaultDAOFactory )
	{

	}

	public Boolean getIsDefaultDAOFactory()
	{
		return true;
	}

	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException
	{
		throwDaoException();

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
		throwDaoException();
		//NOT TO DO ANYTHING.
	}

	public void disableRelatedObjects(String tableName, String whereColumnName,
			Long[] whereColumnValues) throws DAOException
	{
		throwDaoException();
	}

	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map<Object, QueryDataBean> queryResultObjectDataMap)
			throws ClassNotFoundException, DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IConnectionManager getConnectionManager()
	{
		setConnectionManager(new MyDAOImpl());
		return this.connectionManager;
	}


	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{
		//not to do anything
	}

	public List<Object> retrieve(String sourceObjectName) throws DAOException
	{
		return (List<Object>)getList();
	}

	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName)
			throws DAOException
	{
		return (List<Object>)getList();
	}

	public Object retrieve(String sourceObjectName, Long identifier) throws DAOException
	{
		Object obj;
		if(isTestForFail)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,new Exception(""),"MyJDAOImpl.java :");
		}
		else if(returnActivityStatusObj)
		{
			MyDomainObject ado= new MyDomainObject();
			ado.setActivityStatus("Active");
			ado.setId(Long.valueOf(1));
			obj=ado;
		}
		else
		{
			obj=getObject();
		}
		return obj;
	}

	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause) throws DAOException
	{
		if(returnUserPassword)
		{
			List<Object> pass= new ArrayList<Object>();
			pass.add(PasswordManager.encode("Login123"));
			return pass;
		}
		else
		{
			return (List<Object>)getList();
		}
	}

	public List<Object> retrieve(String sourceObjectName, String whereColumnName,
			Object whereColumnValue) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List retrieveAttribute(Class objClass,String columnName, Long identifier, String attributeName
			) throws DAOException
	{
		List attributte = new ArrayList();
		attributte.add(getObjectAttribute());
		return attributte;
	}

	public void rollback() throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void setConnectionManager(IConnectionManager connectionManager)
	{
		if(isTestForFail)
		{
			this.connectionManager=null;
		}
		else
		{
			this.connectionManager=connectionManager;
		}
	}

	public void update(Object obj) throws DAOException
	{
		throwDaoException();
	}

	public Object loadCleanObj(String sourceObjectName, Long identifier) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object loadCleanObj(Class objectClass, Long identifier) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void buildSessionFactory() throws DAOException
	{
		this.setApplicationName("commonpackagetest");
	}

	public String getApplicationName()
	{
		return this.applicationName;
		//return "commonpackagetest";
	}

	public String getConfigurationFile()
	{
		return this.configurationFile;
	}

	public String getConnectionManagerName()
	{
		return this.connectionManagerName;
	}

	public DAO getDAO() throws DAOException
	{
		DAO dao = null;
		try
		{
		   dao = (DAO)Class.forName(defaultDAOClassName).newInstance();
		   dao.setConnectionManager(getConnectionManager());

		}
		catch (Exception excp )
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOFactory.java :");
		}
		return dao;
	}

	public String getDefaultDAOClassName()
	{
		return this.defaultDAOClassName;
	}

	public JDBCDAO getJDBCDAO() throws DAOException
	{
		JDBCDAO dao = null;
		try
		{
		   dao = (JDBCDAO)Class.forName(defaultDAOClassName).newInstance();
		   dao.setConnectionManager(getConnectionManager());

		}
		catch (Exception excp )
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOFactory.java :");
		}
		return dao;
	}

	public String getJdbcDAOClassName()
	{
		return this.jdbcDAOClassName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName=applicationName;

	}

	public void setConfigurationFile(String configurationFile)
	{
		this.configurationFile=configurationFile;

	}

	public void setConnectionManagerName(String connectionManagerName)
	{
		this.connectionManagerName=connectionManagerName;

	}

	public void setDefaultDAOClassName(String defaultDAOClassName)
	{
		this.defaultDAOClassName=defaultDAOClassName;

	}

	public void setJdbcDAOClassName(String jdbcDAOClassName)
	{
		this.jdbcDAOClassName=jdbcDAOClassName;
	}

	public void createTable(String query) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void createTable(String tableName, String[] columnNames) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void delete(String tableName) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public PagenatedResultData executeQuery(QueryParams queryParams) throws ClassNotFoundException,
			DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*public int executeUpdate(String sql) throws DAOException
	{
		return 0;
		// TODO Auto-generated method stub

	}*/

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
		// TODO Auto-generated method stub
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

	public void closeConnection() throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public Session currentSession() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Session getCleanSession() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Configuration getConfiguration()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Connection getConnection() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public SessionFactory getSessionFactory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Session newSession() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setConfiguration(Configuration cfg)
	{
		// TODO Auto-generated method stub

	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		// TODO Auto-generated method stub

	}
	public void closeCleanConnection() throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public void closeCleanSession() throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public Connection getCleanConnection() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void executeAuditSql(String sql, SessionDataBean sessionData, String comments)
			throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public int getColumnCount(int columnCount, boolean getSublistOfResult)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	public Object getPrimitiveOperationProcessor()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getSQLForLikeOperator(String attributeName, String value)
	{
		// TODO Auto-generated method stub
		return null;
	}
	//////////////////////////////////
	/**
	 * This member will store the Connection Manager name.
	 */
	private String connectionManagerName;

	/**
	 * This member will store the Default DAO class name.
	 * TODO
	 */
	private String defaultDAOClassName;
	/**
	 * This member will store the JDBC DAO class name.
	 * TODO
	 */
	private String jdbcDAOClassName;
	/**
	 * This member will store the name of the application.
	 */
	private String applicationName;
	/**
	 * This member will store the configuration file name.
	 */
	private String configurationFile;

	/**
	 * This member will store the connectionManager instance.
	 */
	private IConnectionManager connectionManager;


	/**
	 * This will store the default setting for DAO factory(true / false).
	 */
	private Boolean isDefaultDAOFactory;
	public List executeQuery(String arg0) throws DAOException
	{		
		List<String> innerList= new ArrayList<String>();
		innerList.add("Name");
		innerList.add("Id");
		List<List<String>> list= new ArrayList<List<String>>();
		list.add(innerList);
		return list;
	}
	public void addAuditEventLogs(Collection<AuditEventLog> arg0)
	{
		// TODO Auto-generated method stub
		
	}
	public String getDataSource()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public String getDefaultConnMangrName()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public String getJdbcConnMangrName()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void setDataSource(String arg0)
	{
		// TODO Auto-generated method stub
		
	}
	public void setDefaultConnMangrName(String arg0)
	{
		// TODO Auto-generated method stub
		
	}
	public void setJdbcConnMangrName(String arg0)
	{
		// TODO Auto-generated method stub

	}
	/*public SQLFormatter getSQLFormatter(String arg0) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}*/
	/*public void insert(SQLFormatter arg0, String arg1, String arg2, int arg3) throws DAOException
	{
		// TODO Auto-generated method stub

	}*/
	public void updateClob(String arg0, String arg1) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public String getMaxBarcodeCol()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public String getMaxLabelCol()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void insert(Object obj, boolean isAuditable) throws DAOException
	{
		// TODO Auto-generated method stub
		throwDaoException();
	}
	public Object retrieveById(String sourceObjectName, Long identifier) throws DAOException
	{
		// TODO Auto-generated method stub
		
		return getObject();
	}
	public void update(Object obj, Object oldObj) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public List executeNamedQuery(String queryName, Map<String, NamedQueryParam> namedQueryParams)
			throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	public String getDataBaseType()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void setDatabaseProperties(DatabaseProperties databaseProperties)
	{
		// TODO Auto-generated method stub
		
	}
	public void deleteTable(String tableName) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	
	public ResultSet getDBMetaDataResultSet(String tableName) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	public PreparedStatement getPreparedStatement(String query) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	public ResultSet getQueryResultSet(String sql) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void insert(String sql) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public ResultSet retrieveResultSet(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause, boolean onlyDistinctRows) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void setBatchSize(int batchSize) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public Session getSession() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	/*public List retrieveAttribute(Class objClass, Long identifier, String attributeName,
			String columnName) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}*/
	public  StatementData executeUpdate(String sql) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
		
	}
	public void audit(Object obj, Object oldObj) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public void closeStatement(ResultSet resultSet) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public void insert(QueryData queryData) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public void update(QueryData queryData) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public List executeQuery(String arg0, Integer arg1, Integer arg2, List arg3)
			throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void batchClose() throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public void batchCommit() throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public void batchInitialize(int batchSize, String tableName, SortedSet<String> columnSet)
			throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	public void batchInsert(SortedMap<String, ColumnValueBean> dataMap) throws DAOException
	{
		// TODO Auto-generated method stub
		
	}
	
	public void openTransaction()
	{
		// TODO Auto-generated method stub
		
	}
	public StatementData executeUpdate(String tableName,
			LinkedList<ColumnValueBean> columnValueBeanSet) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}	

}
