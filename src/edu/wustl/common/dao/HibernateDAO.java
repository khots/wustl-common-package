package edu.wustl.common.dao;

import java.util.Collection;



public interface HibernateDAO extends AbstractDAO
{
/*	//no need , it's present in DAO.java
	public void disableRelatedObjects(String tableName, String whereColumnName, Long whereColumnValues[]) throws DAOException;
	
	//no need , it's present in DAO.java
	public void insert(Object obj, SessionDataBean sessionDataBean,
            boolean isAuditable, boolean isSecureInsert) throws DAOException, UserNotAuthorizedException;
	
	//	no need , it's present in DAO.java
    public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException, UserNotAuthorizedException;

    //  no need , it's present in DAO.java
    public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean, boolean isAuditable) throws DAOException;
    
   
    
//  no need , it's present in DAO.java
    public void delete(Object obj) throws DAOException;
    
//  no need , it's present in DAO.java
    public List retrieve(String sourceObjectName) throws DAOException;
    
//  no need , it's present in DAO.java
    public List retrieve(String sourceObjectName, String whereColumnName,
            Object whereColumnValue) throws DAOException;
    
    //no need , it's present in DAO.java
    public List retrieve(String sourceObjectName, String[] selectColumnName)
            throws DAOException;
    
    //no need , it's present in DAO.java
    public List retrieve(String sourceObjectName, String[] selectColumnName,
            String[] whereColumnName, String[] whereColumnCondition,
            Object[] whereColumnValue, String joinCondition)
            throws DAOException;
   
    //no need , it's present in DAO.java
    public Object retrieve(String sourceObjectName, Serializable systemIdentifier)
            throws DAOException;
 */   
	public Object loadCleanObj(String sourceObjectName, Long systemIdentifier) throws Exception;
	
	public void addAuditEventLogs(Collection auditEventDetailsCollection);

}
