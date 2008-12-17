/**
 * <p>Title: HibernateDAO Class>
 * <p>Description:	HibernateDAO is default implemention of AbstractDAO through Hibernate ORM tool.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Mar 16, 2005
 */

package edu.wustl.common.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.audit.Auditable;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Permissions;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * Default implemention of AbstractDAO through Hibernate ORM tool.
 * @author kapil_kaveeshwar
 */
public class HibernateDAOImpl implements HibernateDAO
{
    protected Session session = null;
    protected Transaction transaction = null;
    protected AuditManager auditManager;
    
    /**
     * This method will be used to establish the session with the database.
     * Declared in AbstractDAO class.
     * 
     * @throws DAOException
     */
    public void openSession(SessionDataBean sessionDataBean) throws DAOException
    {
        try
        {
            session = DBUtil.currentSession();
            
            transaction = session.beginTransaction();
            
            auditManager = new AuditManager();
            
            if(sessionDataBean!=null)
            {
            	auditManager.setUserId(sessionDataBean.getUserId());
            	auditManager.setIpAddress(sessionDataBean.getIpAddress());
            }
            else
            {
                auditManager.setUserId(null);
            }
        }
        catch (HibernateException dbex)
        {
            Logger.out.error(dbex.getMessage(), dbex);
            throw handleError(Constants.GENERIC_DATABASE_ERROR, dbex);
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
            DBUtil.closeSession();
        }
        catch (HibernateException dx)
        {
            Logger.out.error(dx.getMessage(), dx);
            throw handleError(Constants.GENERIC_DATABASE_ERROR, dx);
        }
        session = null;
        transaction = null;
        auditManager = null;
    }
    
    /**
     * Commit the database level changes.
     * Declared in AbstractDAO class.
     * @throws DAOException
     */
    public void commit() throws DAOException
    {
        try
        {
            auditManager.insert(this);
            
            if (transaction != null)
                transaction.commit();
        }
        catch (HibernateException dbex)
        {
            Logger.out.error(dbex.getMessage(), dbex);
            throw handleError("Error in commit: ", dbex);
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
            if (transaction != null)
                transaction.rollback();
        }
        catch (HibernateException dbex)
        {
            Logger.out.error(dbex.getMessage(), dbex);
            throw handleError("Error in rollback: ", dbex);
        }
    }
    
    public void disableRelatedObjects(String tableName, String whereColumnName, Long whereColumnValues[]) throws DAOException
	{
    	try
        {
    		Statement st = session.connection().createStatement();
    		
    		StringBuffer buff = new StringBuffer();
    		for (int i = 0; i < whereColumnValues.length; i++)
			{
    			buff.append(whereColumnValues[i].longValue());
    			if((i+1)<whereColumnValues.length)
    				buff.append(",");
			}
    		
    		String sql = "UPDATE "+tableName+" SET ACTIVITY_STATUS = '"+Constants.ACTIVITY_STATUS_DISABLED+ "' WHERE "+whereColumnName+" IN ( "+buff.toString()+")";
    		Logger.out.debug("sql "+sql);
    		int count = st.executeUpdate(sql);
    		st.close();
    		Logger.out.debug("Update count "+count);
        }
        catch (HibernateException dbex)
        {
        	Logger.out.error(dbex.getMessage(),dbex);
        	throw handleError("Error in JDBC connection: ",dbex);
        }
        catch (SQLException sqlEx)
        {
        	Logger.out.error(sqlEx.getMessage(),sqlEx);
        	throw handleError("Error in disabling Related Objects: ",sqlEx);
        }
	}
    
    /**
     * Saves the persistent object in the database.
     * @param obj The object to be saved.
     * @param session The session in which the object is saved.
     * @throws DAOException
     * @throws HibernateException Exception thrown during hibernate operations.
     */
    public void insert(Object obj, SessionDataBean sessionDataBean,
            boolean isAuditable, boolean isSecureInsert) throws DAOException, UserNotAuthorizedException
    {
        boolean isAuthorized = true;
        try
        {
            if (isSecureInsert)
            {
                if (null != sessionDataBean)
                {
                    String userName = sessionDataBean.getUserName();
                    if(userName != null)
                    {
	                    isAuthorized = SecurityManager.getInstance(this.getClass())
	                            .isAuthorized(userName,
	                                    obj.getClass().getName(),
	                                    Permissions.CREATE);
                    }
                    else
                    {
                        isAuthorized = false;
                    }
                }
                else
                {
                    isAuthorized = false;
                }
            }
            Logger.out.debug(" User's Authorization to insert "+obj.getClass().getName()+" "+isAuthorized);
            if(isAuthorized)
            {
                session.save(obj);
                if (obj instanceof Auditable && isAuditable)
                    auditManager.compare((Auditable) obj, null, "INSERT");
            }
            else
            {
                throw new UserNotAuthorizedException("Not Authorized to insert");
            }
        }
        catch (HibernateException hibExp)
        {
            throw handleError("", hibExp);
        }
        catch (AuditException hibExp)
        {
            throw handleError("", hibExp);
        }
        catch( SMException smex)
        {
            throw handleError("", smex);
        }
    }
    
    private DAOException handleError(String message, Exception hibExp)
    {
        Logger.out.error(hibExp.getMessage(), hibExp);
        String msg = generateErrorMessage(message, hibExp);
        return new DAOException(msg, hibExp);
    }
    
    private String generateErrorMessage(String messageToAdd, Exception ex)
    {
        if (ex instanceof HibernateException)
        {
            HibernateException hibernateException = (HibernateException) ex;
            StringBuffer message = new StringBuffer(messageToAdd);
            String str[] = hibernateException.getMessages();
            if (message != null)
            {
                for (int i = 0; i < str.length; i++)
                {
                	Logger.out.debug("str:" + str[i]);
                    message.append(str[i] + " ");
                }
            }
            else
            {
                return "Unknown Error";
            }
            return message.toString();
        }
        else
        {
            return ex.getMessage();
        }
    }
    
    /**
     * Updates the persistent object in the database.
     * @param obj The object to be updated.
     * @param session The session in which the object is saved.
     * @throws DAOException 
     * @throws HibernateException Exception thrown during hibernate operations.
     */
    public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException, UserNotAuthorizedException
    {
        boolean isAuthorized = true;
        try
        {
            if (isSecureUpdate)
            {
                if (null != sessionDataBean)
                {
                    if(!(obj instanceof AbstractDomainObject)||!hasObjectLevelPrivilege)
                    {
                    isAuthorized = SecurityManager.getInstance(this.getClass())
                            .isAuthorized(sessionDataBean.getUserName(),
                                    obj.getClass().getName(),
                                    Permissions.UPDATE);
                    Logger.out.debug(" User's Authorization to update "+obj.getClass().getName()+" "+isAuthorized);
                    }
                    else
                    {
                        isAuthorized = SecurityManager.getInstance(this.getClass())
                        .isAuthorized(sessionDataBean.getUserName(),
                                obj.getClass().getName()+"_"+((AbstractDomainObject)obj).getSystemIdentifier(),
                                Permissions.UPDATE);
                        Logger.out.debug(" User's Authorization to update "+obj.getClass().getName()+" "+isAuthorized);
                    }
                }
                else
                {
                    isAuthorized = false;
                    Logger.out.debug(" User's Authorization to update "+obj.getClass().getName()+"_"+((AbstractDomainObject)obj).getSystemIdentifier()+" "+isAuthorized);
                }
            }
            
            if(isAuthorized)
            {
                session.update(obj);
                
//                Object oldObj = retrieve(obj.getClass().getName(), ((Auditable)obj).getSystemIdentifier());
//                if (obj instanceof Auditable && isAuditable)
//                    auditManager.compare((Auditable) obj, (Auditable)oldObj, "UPDATE");
            }
            else
            {
                throw new UserNotAuthorizedException("Not Authorized to update");
            }
        }
        catch (HibernateException hibExp)
        {
            //Logger.out.error(hibExp.getMessage(), hibExp);
            //throw new DAOException("Error in update", hibExp);
        	throw handleError("", hibExp);
        }
//        catch (AuditException hibExp)
//        {
//            throw handleError("", hibExp);
//        }
        catch (SMException smex)
        {
            //Logger.out.error(smex.getMessage(), smex);
            //throw new DAOException("Error in update", smex);
        	throw handleError("", smex);
        }
    }
    
    public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean, boolean isAuditable) throws DAOException
    {
        try
        {
            if (obj instanceof Auditable && isAuditable)
            {
                Logger.out.debug("In update audit...................................");
                auditManager.compare((Auditable) obj, (Auditable)oldObj, "UPDATE");
            }
        }
        catch (AuditException hibExp)
        {
            throw handleError("", hibExp);
        }
    }
    
    public void addAuditEventLogs(Collection auditEventDetailsCollection)
    {
        auditManager.addAuditEventLogs(auditEventDetailsCollection);
    }
    
    /**
     * Deletes the persistent object from the database.
     * @param obj The object to be deleted.
     */
    public void delete(Object obj) throws DAOException
    {
        try
        {
            session.delete(obj);
            
            //            if(isAuditable)
            //        		auditManager.compare((AbstractDomainObject)obj,null,"INSERT");
        }
        catch (HibernateException hibExp)
        {
            Logger.out.error(hibExp.getMessage(), hibExp);
            throw new DAOException("Error in delete", hibExp);
        }
    }
    
    /**
     * Retrieves all the records for class name in sourceObjectName.
     * @param sourceObjectName Contains the classname whose records are to be retrieved.
     */
    public List retrieve(String sourceObjectName) throws DAOException
    {
        return retrieve(sourceObjectName, null, null, null, null, null);
    }
    
    public List retrieve(String sourceObjectName, String whereColumnName,
            Object whereColumnValue) throws DAOException
    {
        String whereColumnNames[] = {whereColumnName};
        String colConditions[] = {"="};
        Object whereColumnValues[] = {whereColumnValue};

        return retrieve(sourceObjectName, null, whereColumnNames,
                colConditions, whereColumnValues, Constants.AND_JOIN_CONDITION);
    }
    
    /**
     * (non-Javadoc)
     * @see edu.wustl.common.dao.AbstractDAO#retrieve(java.lang.String, java.lang.String[])
     */
    public List retrieve(String sourceObjectName, String[] selectColumnName)
            throws DAOException
    {
        String[] whereColumnName = null;
        String[] whereColumnCondition = null;
        Object[] whereColumnValue = null;
        String joinCondition = null;
        return retrieve(sourceObjectName, selectColumnName, whereColumnName,
                whereColumnCondition, whereColumnValue, joinCondition);
    }
    
    /**
     * Retrieves the records for class name in sourceObjectName according to field values passed in the passed session.
     * @param whereColumnName An array of field names.
     * @param whereColumnCondition The comparision condition for the field values. 
     * @param whereColumnValue An array of field values.
     * @param joinCondition The join condition.
     * @param The session object.
     */
    public List retrieve(String sourceObjectName, String[] selectColumnName,
            String[] whereColumnName, String[] whereColumnCondition,
            Object[] whereColumnValue, String joinCondition)
            throws DAOException
    {
        List list = null;
        try
        {
            StringBuffer sqlBuff = new StringBuffer();
            
            String className = Utility.parseClassName(sourceObjectName);
            
            Logger.out.debug("***********className:"+className);
            if (selectColumnName != null && selectColumnName.length > 0)
            {
                sqlBuff.append("Select ");
                
                //Added by Aarti 
                //--------------------------------
//                if(sourceObjectName.equals(Site.class.getName()))
//                {
//                    sqlBuff.append(className + "." + Constants.SYSTEM_IDENTIFIER);
//                    sqlBuff.append(", ");
//                }
                //---------------------------------
                for (int i = 0; i < selectColumnName.length; i++)
                {
                    sqlBuff.append(className + "." + selectColumnName[i]);
                    if (i != selectColumnName.length - 1)
                    {
                        sqlBuff.append(", ");
                    }
                }
                sqlBuff.append(" ");
            }
            //Logger.out.debug(" String : "+sqlBuff.toString());
            
            Query query = null;
            sqlBuff.append("from " + sourceObjectName
                    + " " + className);
            //Logger.out.debug(" String : "+sqlBuff.toString());
            
            if ((whereColumnName != null && whereColumnName.length > 0)
                    && (whereColumnCondition != null && whereColumnCondition.length == whereColumnName.length)
                    && (whereColumnValue != null))
            {
                if (joinCondition == null)
                    joinCondition = Constants.AND_JOIN_CONDITION;
                
                sqlBuff.append(" where ");
                
                //Adds the column name and search condition in where clause. 
                for (int i = 0; i < whereColumnName.length; i++)
                {
                    sqlBuff.append(className + "." + whereColumnName[i] + " ");
                    if(whereColumnCondition[i].indexOf("in")!=-1)
                    {
                    	sqlBuff.append(whereColumnCondition[i] + "(  ");
                    	Object valArr[] = (Object [])whereColumnValue[i];
                    	for (int j = 0; j < valArr.length; j++)
						{
                    		Logger.out.debug(sqlBuff);
                    		sqlBuff.append("? ");
                    		if((j+1)<valArr.length)
                    			sqlBuff.append(", ");
						}
                    	sqlBuff.append(") ");
                    }
                    else if(whereColumnCondition[i].indexOf("is not null")!=-1)
                    {
                    	sqlBuff.append(whereColumnCondition[i]);
                    }
                    else
                    {
                    	sqlBuff.append(whereColumnCondition[i] + " ? ");
                    }
                    
                    if (i < (whereColumnName.length - 1))
                        sqlBuff.append(" " + joinCondition + " ");
                }
                
                Logger.out.debug(sqlBuff.toString());
                
                query = session.createQuery(sqlBuff.toString());
                
                int index = 0;
                //Adds the column values in where clause
                for (int i = 0; i < whereColumnValue.length; i++)
                {
                    //Logger.out.debug("whereColumnValue[i]. " + whereColumnValue[i]);
                    Object obj = whereColumnValue[i];
                    if(obj instanceof Object[])
                    {
                    	Object[] valArr = (Object[])obj;
                    	for (int j = 0; j < valArr.length; j++)
						{
                    		query.setParameter(index, valArr[j]);
                    		index++;
						}
                    }
                    else
                    {
                    	query.setParameter(index, obj);
                    	index++;
                    }
                }
            }
            else
            {
                query = session.createQuery(sqlBuff.toString());
            }
            
            list = query.list();
            
//          Added by Aarti 
            //--------------------------------
            
//            if(sourceObjectName.equals(Site.class.getName()))
//            {
//                boolean isAuthorized;
//                Object[] objects = null;
//                Object[] newObjects = null;
//                Long systemIdentifier;
//                Site site;
//                
//                if (selectColumnName != null && selectColumnName.length > 0)
//                {
//                    if(list != null)
//                    {
//                        for(int i=0; i<list.size();)
//                        {
//                            objects = (Object[]) list.get(i);
//                            
//                            if(objects != null)
//                            {
//                                newObjects = new Object[objects.length-1];
//                                systemIdentifier = (Long) objects[0];
//                                isAuthorized = SecurityManager.getInstance(this.getClass())
//                                .isAuthorized("sharma.aarti@gmail.com",
//                                        sourceObjectName+"_"+systemIdentifier,
//                                        Permissions.USE);
//                                Logger.out.debug(" User's Authorization to update "+sourceObjectName+"_"+systemIdentifier+" "+isAuthorized);
//                                list.remove(i);
//                                if(isAuthorized)
//                                {
//                                    for(int x = 1;x<objects.length;x++ )
//                                    {
//                                        newObjects[x-1] = objects[x];
//                                    }
//                                    list.add(i,newObjects);
//                                    i++;
//                                }
//                            }
//                        }
//                    }
//                }
//                else
//                {
//                    if(list != null)
//                    {
//                        for(int i=0; i<list.size();)
//                        {
//                            site = (Site) list.get(i);
//                            if(site !=null)
//                            {
//                                isAuthorized = SecurityManager.getInstance(this.getClass())
//                                .isAuthorized("sharma.aarti@gmail.com",
//                                        sourceObjectName+"_"+site.getSystemIdentifier(),
//                                        Permissions.USE);
//                                Logger.out.debug(" User's Authorization to update "+sourceObjectName+"_"+site.getSystemIdentifier()+" "+isAuthorized);
//                                
//                                if(isAuthorized)
//                                {
//                                    i++;
//                                }
//                                else
//                                {
//                                    list.remove(i);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
            //---------------------------------
            
            Logger.out.debug(" String : " + sqlBuff.toString());
        }
        catch (HibernateException hibExp)
        {
            Logger.out.error(hibExp.getMessage(), hibExp);
            throw new DAOException("Error in retrieve " + hibExp.getMessage(),
                    hibExp);
        }
        catch (Exception exp)
        {
            Logger.out.error(exp.getMessage(), exp);
            throw new DAOException("Logical Erroe in retrieve method "
                    + exp.getMessage(), exp);
        }
        return list;
    }
    
    public Object retrieve(String sourceObjectName, Serializable systemIdentifier)
            throws DAOException
    {
        try
        {
            return session.load(Class.forName(sourceObjectName), 
                    systemIdentifier);
        }
        catch (ClassNotFoundException cnFoundExp)
        {
            Logger.out.error(cnFoundExp.getMessage(), cnFoundExp);
            throw new DAOException("Error in retrieve "
                    + cnFoundExp.getMessage(), cnFoundExp);
        }
        catch (HibernateException hibExp)
        {
            Logger.out.error(hibExp.getMessage(), hibExp);
            throw new DAOException("Error in retrieve " + hibExp.getMessage(),
                    hibExp);
        }
    }
    
	public Object loadCleanObj(String sourceObjectName, Long systemIdentifier) throws Exception
	{
		Object obj = retrieve(sourceObjectName, systemIdentifier);
		session.evict(obj);
		return obj;
	}
}