package edu.wustl.common.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.dbManager.DAOException;


public interface JDBCDAO extends AbstractDAO
{
	  
	  public void createTable(String query) throws DAOException;
	  //public List retrieve(String sourceObjectName) throws DAOException;
	  //public List retrieve(String sourceObjectName, String[] selectColumnName)
	    //        throws DAOException;
	  public List retrieve(String sourceObjectName, String[] selectColumnName, boolean onlyDistinctRows)
	            throws DAOException;
	  public List retrieve(String sourceObjectName, String[] selectColumnName,
	            String[] whereColumnName, String[] whereColumnCondition,
	            Object[] whereColumnValue, String joinCondition, boolean onlyDistinctRows)
	            throws DAOException;
	    /*public List retrieve(String sourceObjectName, String[] selectColumnName,
	            String[] whereColumnName, String[] whereColumnCondition,
	            Object[] whereColumnValue, String joinCondition)
	            throws DAOException;*/
	    
	    public List executeQuery(String query, SessionDataBean sessionDataBean, boolean isSecureExecute, Map queryResultObjectDataMap) throws ClassNotFoundException, DAOException;
	    public List executeQuery(String query, SessionDataBean sessionDataBean, boolean isSecureExecute, boolean hasConditionOnIdentifiedField, Map queryResultObjectDataMap) throws ClassNotFoundException, DAOException;
	    
	    /*public List retrieve(String sourceObjectName, String whereColumnName,
	            Object whereColumnValue) throws DAOException;*/
	    public void insert(String tableName, List columnValues) throws DAOException,SQLException;
	    
	    //public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException, UserNotAuthorizedException;
	    //public void delete(Object obj) throws DAOException;
	    public void create(String tableName, String[] columnNames) throws DAOException;
	    public void delete(String tableName) throws DAOException;
	    //public Object retrieve (String sourceObjectName, Serializable systemIdentifier) throws DAOException;
		//public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureInsert) throws DAOException, UserNotAuthorizedException;
	    
	    
	    //public void disableRelatedObjects(String tableName, String whereColumnName, Long whereColumnValues[]) throws DAOException;
		
	    public String getActivityStatus(String sourceObjectName, Long indetifier) throws DAOException;
		
	    //public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean, boolean isAuditable) throws DAOException;
	    
}
