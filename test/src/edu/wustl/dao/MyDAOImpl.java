package edu.wustl.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.querydatabean.QueryDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;


public class MyDAOImpl implements DAO, HibernateDAO, IDAOFactory, JDBCDAO, IConnectionManager
{

	public static boolean isTestForFail=false;

	/**
	 * List to return from retrieve methods.
	 */
	public static List<Object> list= new ArrayList<Object>();
	
	public static String object="retObject";
	/**
	 * @return returns dummy list.
	 * @throws DAOException  throw DAOException when isTestForFail=true
	 */
	public static List<Object> getList() throws DAOException
	{
		if(isTestForFail)
		{
			try
			{
				ErrorKey.init("-");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,new Exception(""),"MyJDAOImpl.java :"+
					DAOConstants.EXECUTE_QUERY_ERROR);
		}
		else
		{
			List<String> list1= new ArrayList<String>();
			list1.add("1");
			list1.add("John");
			list1.add("Enterprise");
			List<String> list2= new ArrayList<String>();
			list2.add("2");
			list2.add("Raghu");
			list2.add("IBM");
			List<String> list3= new ArrayList<String>();
			list3.add("3");
			list3.add("Bame");
			list3.add("Enterprise");
			list.add(list1);
			list.add(list2);
			list.add(list3);
		}
		return (List<Object>)list;
	}

	public String getObject() throws DAOException
	{
		if(isTestForFail)
		{
			try
			{
				ErrorKey.init("-");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,new Exception(""),"MyJDAOImpl.java :"+
					DAOConstants.EXECUTE_QUERY_ERROR);
		}
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

	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map<Object, QueryDataBean> queryResultObjectDataMap)
			throws ClassNotFoundException, DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IConnectionManager getConnectionManager()
	{
		return this.connectionManager;
	}

	public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureInsert) throws DAOException
	{
		if(isTestForFail)
		{
			try
			{
				ErrorKey.init("-");
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
			}
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,new Exception(""),"MyJDAOImpl.java :"+
					DAOConstants.EXECUTE_QUERY_ERROR);
		}
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
		return "retObject";
	}

	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause) throws DAOException
	{
		return (List<Object>)getList();
	}

	public List<Object> retrieve(String sourceObjectName, String whereColumnName,
			Object whereColumnValue) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object retrieveAttribute(Class objClass, Long identifier, String attributeName,
			String columnName) throws DAOException
	{
		return getObject();
	}

	public void rollback() throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void setConnectionManager(IConnectionManager connectionManager)
	{		
		this.connectionManager=connectionManager;
	}

	public void update(Object obj) throws DAOException
	{
		// TODO Auto-generated method stub

	}

	public void addAuditEventLogs(Collection<Object> auditEventDetailsCollection)
	{
		// TODO Auto-generated method stub

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
			throw new DAOException(errorKey,excp,"DAOFactory.java :"+
					DAOConstants.DEFAULT_DAO_INSTANTIATION_ERROR);
		}
		return dao;
	}

	public String getDefaultDAOClassName()
	{
		return this.defaultDAOClassName;
	}

	public JDBCDAO getJDBCDAO() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
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

	public void executeUpdate(String sql) throws DAOException
	{
		// TODO Auto-generated method stub

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

	///////////////////////////////////
}
