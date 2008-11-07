/*
 * Created on Aug 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.dao;

import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;

/**
 * @author kapil_kaveeshwar
 *
 * This interface defines methods for insertion, updation, deletion and retrival of data.
 */
public interface DAO
{

	/**
	 * Insert the Object in the database.
	 * @param obj Object to be inserted in database
	 * @param sessionDataBean session Data
	 * @param isAuditable is Auditable.
	 * @param isSecureInsert is Secure Insert
	 * @throws DAOException generic DAOException
	 * @throws UserNotAuthorizedException User Not Authorized Exception.
	 */
	void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureInsert) throws DAOException, UserNotAuthorizedException;

	/**
	 * updates the persisted object in the database.
	 * @param obj Object to be updated in database
	 * @param sessionDataBean TODO
	 * @param isAuditable TODO
	 * @param isSecureUpdate TODO
	 * @param hasObjectLevelPrivilege TODO
	 * @throws DAOException generic DAOException.
	 * @throws UserNotAuthorizedException TODO
	 */
	void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException,
			UserNotAuthorizedException;

	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	void delete(Object obj) throws DAOException;

	/**
	 * Audit the object.
	 * @param obj Object to be audited.
	 * @param oldObj old Object.
	 * @param sessionDataBean session Data.
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException.
	 */
	void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException;

	/**
	 * Retrive and returns the list of all source objects that satisfy the
	 * for given conditions on a various columns.
	 * @param sourceObjectName Source object's name to be retrived from database.
	 * @param selectColumnName Column names in SELECT clause of the query.
	 * @param whereColumnName Array of column name to be included in where clause.
	 * @param whereColumnCondition condition to be statify between column and its value.
	 * e.g. "=", "<", ">", "=<", ">=" etc
	 * @param whereColumnValue Value of the column name that included in where clause.
	 * @param joinCondition join condition between two columns. (AND, OR)
	 * @return the list of all source objects that satisfy the seasch conditions.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition) throws DAOException;

	/**
	 * Retrive and returns the list of all source objects for given
	 * condition on a single column. The condition value
	 * @param sourceObjectName Source object's name to be retrived from database.
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValue Value of the Column name that included in where clause.
	 * @return the list of all source objects for given condition on a single column.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName, String whereColumnName,
			Object whereColumnValue) throws DAOException;

	/**
	 * Returns the list of all source objects available in database.
	 * @param sourceObjectName Source object's name to be retrived from database.
	 * @return the list of all source objects available in database.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName) throws DAOException;

	/**
	 * Returns the list of all objects with the select columns specified.
	 * @param sourceObjectName Source object in the Database.
	 * @param selectColumnName column names in the select clause.
	 * @return the list of all objects with the select columns specified.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName, String[] selectColumnName)
			throws DAOException;

	/**
	 * Retrive and returns the source object for given id.
	 * @param sourceObjectName Source object in the Database.
	 * @param identifier identifier of source object.
	 * @return object
	 * @throws DAOException generic DAOException.
	 */
	Object retrieve(String sourceObjectName, Long identifier) throws DAOException;

	/**
	 * Disabled Related Objects.
	 * @param tableName table Name
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValues Value of the Column name that included in where clause.
	 * @throws DAOException generic DAOException.
	 */
	void disableRelatedObjects(String tableName, String whereColumnName,
			Long [] whereColumnValues) throws DAOException;

	/**
	 * Execute Query.
	 * @param query query
	 * @param sessionDataBean session Data
	 * @param isSecureExecute is Secure Execute.
	 * @param queryResultObjectDataMap query Result Object Data Map.
	 * @return List.
	 * @throws ClassNotFoundException Class Not Found Exception.
	 * @throws DAOException generic DAOException.
	 */
	List executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map queryResultObjectDataMap) throws ClassNotFoundException,
			DAOException;

	/**
	 * Execute Query.
	 * @param query query
	 * @param sessionDataBean session Data.
	 * @param isSecureExecute is Secure Execute.
	 * @param hasConditionOnIdentifiedField has Condition On Identified Field.
	 * @param queryResultObjectDataMap query Result Object Data Map.
	 * @return List.
	 * @throws ClassNotFoundException Class Not Found Exception.
	 * @throws DAOException generic DAOException.
	 */
	List executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap) throws ClassNotFoundException, DAOException;

	/**
	 * To retrieve the attribute value for the given source object name & Id.
	 * @param sourceObjectName Source object in the Database.
	 * @param identifier Id of the object.
	 * @param attributeName attribute name to be retrieved.
	 * @return The Attribute value corresponding to the SourceObjectName & id.
	 * @throws DAOException generic DAOException.
	 */
	Object retrieveAttribute(String sourceObjectName, Long identifier, String attributeName)
			throws DAOException;

	/**
	 * Retrieves attribute value for given class name and identifier.
	 * @param objClass source Class object
	 * @param identifier identifer of the source object
	 * @param attributeName attribute to be retrieved
	 * @return Object.
	 * @throws DAOException generic DAOException.
	 */
	Object retrieveAttribute(Class<AbstractDomainObject> objClass, Long identifier,
			String attributeName) throws DAOException;
	
	
	/**
	 * This method will be used to establish the session with the database.
	 * 
	 * @throws DAOException
	 */
	public abstract void openSession(SessionDataBean sessionDataBean) throws DAOException;

	/**
	 * This method will be used to close the session with the database.
	 * @throws DAOException
	 */
	public abstract void closeSession() throws DAOException;

	/**
	 * Commit the database level changes  
	 * @throws DAOException
	 */
	public abstract void commit() throws DAOException;

	/**
	 * Rollback all the changes after last commit.  
	 * @throws DAOException 
	 * @throws DAOException
	 */
	public abstract void rollback() throws DAOException;
	
	
}