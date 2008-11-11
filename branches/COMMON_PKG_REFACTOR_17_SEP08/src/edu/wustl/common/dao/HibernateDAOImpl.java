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

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.audit.Auditable;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.dbmanager.DBUtil;
import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * Default implemention of AbstractDAO through Hibernate ORM tool.
 * @author kapil_kaveeshwar
 */
public class HibernateDAOImpl implements HibernateDAO
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(HibernateDAOImpl.class);

	/**
	 * specify Session instance.
	 */
	protected Session session = null;
	/**
	 * specify Transaction instance.
	 */
	protected Transaction transaction = null;
	/**
	 * specify AuditManager instance.
	 */
	protected AuditManager auditManager;
	/**
	 * specify isUpdated.
	 */
	private boolean isUpdated = false;
	
	private IConnectionManager connectionManager = null ;
	
		
	public HibernateDAOImpl(IConnectionManager connectionManager)
	{
		//System.out.println("---got the constructor connectionManager --" +connectionManager);
		this.connectionManager  = connectionManager;
		
	}
	
	/**
	 * This method will be used to establish the session with the database.
	 * Declared in AbstractDAO class.
	 * @param sessionDataBean session Data.
	 * @throws DAOException generic DAOException.
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{

		try
		{
			session = DBUtil.currentSession();

			transaction = session.beginTransaction();

			auditManager = new AuditManager();

			if (sessionDataBean != null)
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
			logger.error(dbex.getMessage(), dbex);
			throw handleError(Constants.GENERIC_DATABASE_ERROR, dbex);
		}
	}

	/**
	 * This method will be used to close the session with the database.
	 * Declared in AbstractDAO class.
	 * @throws DAOException generic DAOException.
	 */
	public void closeSession() throws DAOException
	{

		try
		{
			DBUtil.closeSession();

		}
		catch (HibernateException dx)
		{
			logger.error(dx.getMessage(), dx);
			throw handleError(Constants.GENERIC_DATABASE_ERROR, dx);
		}
		session = null;
		transaction = null;
		auditManager = null;
	}

	/**
	 * Commit the database level changes.
	 * Declared in AbstractDAO class.
	 * @throws DAOException generic DAOException.
	 */
	public void commit() throws DAOException
	{
		try
		{
			auditManager.insert(this);

			if (transaction != null)
			{
				transaction.commit();
			}
		}
		catch (HibernateException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw handleError("Error in commit: ", dbex);
		}
	}

	/**
	 * Rollback all the changes after last commit.
	 * Declared in AbstractDAO class.
	 * @throws DAOException generic DAOException.
	 */
	public void rollback() throws DAOException
	{
		/**
		 * the isUpdated==true is removed because if there is cascade save-update
		 * and the association collection objects
		 * is violating constaring then in insert() method session.save() is throwing exception
		 * and isUpdated is not gettting set to true.
		 * Because of this roll back is not happining on parent object.
		 *
		 */
		if (isUpdated)
		{
			try
			{
				if (transaction != null)
				{
					transaction.rollback();
				}
			}
			catch (HibernateException dbex)
			{
				logger.error(dbex.getMessage(), dbex);
				throw handleError("Error in rollback: ", dbex);
			}
		}
	}

	/**
	 * Disabled Related Objects.
	 * @param tableName table Name.
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValues Value of the Column name that included in where clause.
	 * @throws DAOException generic DAOException.
	 */
	public void disableRelatedObjects(String tableName, String whereColumnName,
			Long[] whereColumnValues) throws DAOException
	{
		try
		{
			Statement statement = session.connection().createStatement();

			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < whereColumnValues.length; i++)
			{
				buff.append(whereColumnValues[i].longValue());
				if ((i + 1) < whereColumnValues.length)
				{
					buff.append(",");
				}
			}

			String sql = "UPDATE " + tableName + " SET ACTIVITY_STATUS = '"
					+ Constants.ACTIVITY_STATUS_DISABLED + "' WHERE "
					+ whereColumnName + " IN ( "
					+ buff.toString() + ")";
			statement.close();
		}
		catch (HibernateException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw handleError("Error in JDBC connection: ", dbex);
		}
		catch (SQLException sqlEx)
		{
			logger.error(sqlEx.getMessage(), sqlEx);
			throw handleError("Error in disabling Related Objects: ", sqlEx);
		}
	}

	/**
	 * Saves the persistent object in the database.
	 * @param obj The object to be saved.
	 * @param sessionDataBean The session in which the object is saved.
	 * @param isAuditable is Auditable.
	 * @param isSecureInsert is Secure Insert.
	 * @throws DAOException generic DAOException.
	 * @throws UserNotAuthorizedException User Not Authorized Exception.
	 */
	public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureInsert) throws DAOException, UserNotAuthorizedException
	{
		boolean isAuthorized = true;

		try
		{
			/**
			* Now, Authorizations on Objects will be done in corresponding biz logic
			* for the Object through DefaultBizLogic's 'isAuthorized' method
			* For this version, each Project will have to provide its implementation
			* for objects which require secured access
			* By Default :: we return as 'true' i.e. user authorized
			*/
			if (isAuthorized)
			{
				session.save(obj);
				if (obj instanceof Auditable && isAuditable)
				{
					auditManager.compare((Auditable) obj, null, "INSERT");
				}
				isUpdated = true;
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
		catch (SMException smex)
		{
			throw handleError("", smex);
		}

	}

	/**
	 * Handles Error.
	 * @param message message
	 * @param hibExp Exception
	 * @return DAOException
	 */
	private DAOException handleError(String message, Exception hibExp)
	{
		logger.error(hibExp.getMessage(), hibExp);
		String msg = generateErrorMessage(message, hibExp);
		return new DAOException(msg, hibExp);
	}

	/**
	 * Generates Error Message.
	 * @param messageToAdd message To Add.
	 * @param exception Exception
	 * @return message.
	 */
	private String generateErrorMessage(String messageToAdd, Exception exception)
	{
		if (exception instanceof HibernateException)
		{
			HibernateException hibernateException = (HibernateException) exception;
			StringBuffer message = new StringBuffer(messageToAdd);
			String [] str = hibernateException.getMessages();
			if (message != null)
			{
				for (int i = 0; i < str.length; i++)
				{
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
			return exception.getMessage();
		}
	}

	/**
	 * Updates the persistent object in the database.
	 * @param obj The object to be updated.
	 * @param sessionDataBean The sessionData in which the object is saved.
	 * @param isAuditable is Auditable.
	 * @param isSecureUpdate is Secure Update.
	 * @param hasObjectLevelPrivilege has Object Level Privilege.
	 * @throws DAOException generic DAOException.
	 * @throws UserNotAuthorizedException User Not Authorized Exception.
	 */
	public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureUpdate, boolean hasObjectLevelPrivilege) throws DAOException,
			UserNotAuthorizedException
	{
		boolean isAuthorized = true;
		try
		{
			/**
			* Now, Authorizations on Objects will be done in corresponding biz logic
			* for the Object through DefaultBizLogic's 'isAuthorized' method
			* For this version, each Project will have to provide its implementation
			* for objects which require secured access
			* By Default :: we return as 'true' i.e. user authorized
			*/

			if (isAuthorized)
			{
				session.update(obj);
				isUpdated = true;
			}
			else
			{
				throw new UserNotAuthorizedException("Not Authorized to update");
			}
		}
		catch (HibernateException hibExp)
		{
			throw handleError("", hibExp);
		}
		catch (SMException smex)
		{
			throw handleError("", smex);
		}

	}

	/**
	 * Audit.
	 * @param obj The object to be audited.
	 * @param oldObj old Object.
	 * @param sessionDataBean session Data.
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException.
	 */
	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException
	{
		try
		{
			if (obj instanceof Auditable && isAuditable)
			{
				auditManager.compare((Auditable) obj, (Auditable) oldObj, "UPDATE");
			}
		}
		catch (AuditException hibExp)
		{
			throw handleError("", hibExp);
		}
	}

	/**
	 * add Audit Event Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	public void addAuditEventLogs(Collection auditEventDetailsCollection)
	{
		auditManager.addAuditEventLogs(auditEventDetailsCollection);
	}

	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	public void delete(Object obj) throws DAOException
	{
		try
		{
			session.delete(obj);
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw new DAOException("Error in delete", hibExp);
		}
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the classname whose records are to be retrieved.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName) throws DAOException
	{
		return retrieve(sourceObjectName, null, null, null, null, null);
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the classname whose records are to be retrieved.
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValue Value of the Column name that included in where clause.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName, String whereColumnName, Object whereColumnValue)
			throws DAOException
	{
		String [] whereColumnNames = {whereColumnName};
		String [] colConditions = {"="};
		Object [] whereColumnValues = {whereColumnValue};

		return retrieve(sourceObjectName, null, whereColumnNames, colConditions, whereColumnValues,
				Constants.AND_JOIN_CONDITION);
	}

	/**
	 * (non-Javadoc).
	 * @see edu.wustl.common.dao.AbstractDAO#retrieve(java.lang.String, java.lang.String[])
	 * @param sourceObjectName Contains the classname whose records are to be retrieved.
	 * @param selectColumnName select Column Name.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName) throws DAOException
	{
		String[] whereColumnName = null;
		String[] whereColumnCondition = null;
		Object[] whereColumnValue = null;
		String joinCondition = null;
		return retrieve(sourceObjectName, selectColumnName, whereColumnName, whereColumnCondition,
				whereColumnValue, joinCondition);
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according
	 * to field values passed in the passed session.
	 * @param whereColumnName An array of field names.
	 * @param whereColumnCondition The comparision condition for the field values.
	 * @param whereColumnValue An array of field values.
	 * @param joinCondition The join condition.
	 * @param sourceObjectName source Object Name.
	 * @param selectColumnName select Column Name.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition) throws DAOException
	{
		List list = null;
		try
		{
			StringBuffer sqlBuff = new StringBuffer();
			String className = Utility.parseClassName(sourceObjectName);

			generateSelectBlock(selectColumnName, sqlBuff, className);

			Query query = null;
			sqlBuff.append("from " + sourceObjectName + " " + className);

			if ((whereColumnName != null && whereColumnName.length > 0)
					&& (whereColumnCondition != null && whereColumnCondition.length == whereColumnName.length)
					&& (whereColumnValue != null))
			{
				query = generateWhereBlock(sourceObjectName, selectColumnName, whereColumnName,
						whereColumnCondition, whereColumnValue, joinCondition, query, sqlBuff,
						className);
			}
			else
			{
				query = session.createQuery(sqlBuff.toString());
			}

			list = query.list();

		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw new DAOException("Error in retrieve " + hibExp.getMessage(), hibExp);
		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			throw new DAOException("Logical Erroe in retrieve method " + exp.getMessage(), exp);
		}
		return list;
	}

	/**
	 * Retrieve Object.
	 * @param sourceObjectName source Object Name.
	 * @param identifier identifier.
	 * @return object.
	 * @throws DAOException generic DAOException.
	 */
	public Object retrieve(String sourceObjectName, Long identifier) throws DAOException
	{
		try
		{
			Object object = session.load(Class.forName(sourceObjectName), identifier);
			return HibernateMetaData.getProxyObjectImpl(object);
		}
		catch (ClassNotFoundException cnFoundExp)
		{
			logger.error(cnFoundExp.getMessage(), cnFoundExp);
			throw new DAOException("Error in retrieve " + cnFoundExp.getMessage(), cnFoundExp);
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw new DAOException("Error in retrieve " + hibExp.getMessage(), hibExp);
		}
	}

	/**
	 * Loads Clean Object.
	 * @param sourceObjectName source Object Name.
	 * @param identifier identifier.
	 * @return object.
	 * @throws Exception Exception.
	 */
	public Object loadCleanObj(String sourceObjectName, Long identifier) throws Exception
	{
		Object obj = retrieve(sourceObjectName, identifier);
		session.evict(obj);
		return obj;
	}

	/**
	 * Executes the HQL query.
	 * @param query HQL query to execute.
	 * @param sessionDataBean session Data
	 * @param isSecureExecute is Secure Execute.
	 * @param queryResultObjectDataMap query Result Object Data Map.
	 * @return List.
	 * @throws ClassNotFoundException Class Not Found Exception.
	 * @throws DAOException generic DAOException.
	 */
	public List executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map queryResultObjectDataMap) throws ClassNotFoundException,
			DAOException
	{
		List returner = null;

		try
		{
			Query hibernateQuery = session.createQuery(query);
			returner = hibernateQuery.list();

		}
		catch (HibernateException e)
		{
			throw (new DAOException(e));
		}

		return returner;
	}

	/**
	 * Executes the HQL query.
	 * @param query HQL query.
	 * @param sessionDataBean sessionData
	 * @param isSecureExecute is Secure Execute.
	 * @param hasConditionOnIdentifiedField has Condition On Identified Field.
	 * @param queryResultObjectDataMap query Result Object Data Map.
	 * @return List.
	 * @throws ClassNotFoundException Class Not Found Exception.
	 * @throws DAOException generic DAOException
	 */
	public List executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, boolean hasConditionOnIdentifiedField,
			Map queryResultObjectDataMap) throws ClassNotFoundException, DAOException
	{

		return null;
	}

	/**
	 * Retrieve Attribute.
	 * @param objClass object.
	 * @param identifier identifier.
	 * @param attributeName attribute Name.
	 * @return Object.
	 * @throws DAOException generic DAOException.
	 */
	public Object retrieveAttribute(Class<AbstractDomainObject> objClass, Long identifier,
			String attributeName) throws DAOException
	{

		String objClassName = objClass.getName();
		String simpleName = objClass.getSimpleName();
		String nameOfAttribute = Utility.createAttributeNameForHQL(simpleName, attributeName);

		StringBuffer queryStringBuffer = new StringBuffer();
		queryStringBuffer.append("Select ").append(simpleName).append(".").append(nameOfAttribute)
				.append(" from ").append(objClassName).append(" ").append(simpleName).append(
						" where ").append(simpleName).append(".").append(
						Constants.SYSTEM_IDENTIFIER).append("=").append(identifier);
		try
		{
			return session.createQuery(queryStringBuffer.toString()).list();
		}
		catch (HibernateException exception)
		{
			throw new DAOException(exception.getMessage(), exception);
		}
	}

	/**
	 * To retrieve the attribute value for the given source object name & Id.
	 * @param sourceObjectName Source object in the Database.
	 * @param identifier Id of the object.
	 * @param attributeName attribute name to be retrieved.
	 * @return The Attribute value corresponding to the SourceObjectName & id.
	 * @throws DAOException
	 * @see edu.wustl.common.dao.DAO#retrieveAttribute(java.lang.String, java.lang.Long, java.lang.String)
	 * @deprecated This function is deprecated use retrieveAttribute
	 * (Class className,Long id, String attributeName)
	 * @throws DAOException generic DAOException.
	 */
	public Object retrieveAttribute(String sourceObjectName, Long identifier, String attributeName)
			throws DAOException
	{
		String[] selectColumnNames = {attributeName};
		String[] whereColumnName = {Constants.SYSTEM_IDENTIFIER};
		String[] whereColumnCondition = {"="};
		Object[] whereColumnValue = {identifier};

		List result = retrieve(sourceObjectName, selectColumnNames, whereColumnName,
				whereColumnCondition, whereColumnValue, null);
		Object attribute = null;

		/*
		 * if the attribute is of type collection, then it needs to be returned as Collection(HashSet)
		 */
		if (Utility.isColumnNameContainsElements(attributeName))
		{
			Collection collection = new HashSet();
			attribute = collection;
			for (int i = 0; i < result.size(); i++)
			{
				/**
				 * Name: Prafull
				 * Calling HibernateMetaData.getProxyObject() because it could be proxy object.
				 */
				collection.add(HibernateMetaData.getProxyObjectImpl(result.get(i)));
			}
		}
		else
		{
			if (!result.isEmpty())
			{
				/**
				 * Name: Prafull
				 * Calling HibernateMetaData.getProxyObject() because it could be proxy object.
				 */
				attribute = HibernateMetaData.getProxyObjectImpl(result.get(0));
			}
		}

		return attribute;
	}

	/**
	 * Generate Select Block.
	 * @param selectColumnName select Column Name.
	 * @param sqlBuff sql Buff
	 * @param className class Name.
	 */
	private void generateSelectBlock(String[] selectColumnName, StringBuffer sqlBuff,
			String className)
	{
		//		logger.debug("***********className:"+className);
		if (selectColumnName != null && selectColumnName.length > 0)
		{
			sqlBuff.append("Select ");

			for (int i = 0; i < selectColumnName.length; i++)
			{
				sqlBuff.append(Utility.createAttributeNameForHQL(className, selectColumnName[i]));
				if (i != selectColumnName.length - 1)
				{
					sqlBuff.append(", ");
				}
			}
			sqlBuff.append(" ");
		}
	}

	/**
	 * Generate WhereBlock.
	 * @param sourceObjectName source Object Name.
	 * @param selectColumnName source Object Name.
	 * @param whereColumnName where Column Name.
	 * @param whereColumnCondition where Column Condition.
	 * @param whereColumnValue where Column Value.
	 * @param joinCondition join Condition.
	 * @param query query
	 * @param sqlBuff sqlBuff
	 * @param className className
	 * @return Query.
	 */
	private Query generateWhereBlock(String sourceObjectName, String[] selectColumnName,
			String[] whereColumnName, String[] whereColumnCondition, Object[] whereColumnValue,
			String joinCondition, Query query, StringBuffer sqlBuff, String className)
	{

		String condition;
		Query sqlQuery = query;
		if (joinCondition == null)
		{
			condition = Constants.AND_JOIN_CONDITION;
		}
		else
		{
			condition = joinCondition;
		}

		sqlBuff.append(" where ");
		//Adds the column name and search condition in where clause.
		for (int i = 0; i < whereColumnName.length; i++)
		{
			sqlBuff.append(className + "." + whereColumnName[i] + " ");
			if (whereColumnCondition[i].indexOf("in") != -1)
			{
				sqlBuff.append(whereColumnCondition[i] + "(  ");
				Object [] valArr = (Object[]) whereColumnValue[i];
				for (int j = 0; j < valArr.length; j++)
				{
					sqlBuff.append("? ");
					if ((j + 1) < valArr.length)
					{
						sqlBuff.append(", ");
					}
				}
				sqlBuff.append(") ");
			}
			else if (whereColumnCondition[i].indexOf("is not null") != -1)
			{
				sqlBuff.append(whereColumnCondition[i]);
			}
			else if (whereColumnCondition[i].indexOf("is null") != -1)
			{
				sqlBuff.append(whereColumnCondition[i]);
			}
			else
			{
				sqlBuff.append(whereColumnCondition[i] + " ? ");
			}

			if (i < (whereColumnName.length - 1))
			{
				sqlBuff.append(" " + condition + " ");
			}
		}

		sqlQuery = session.createQuery(sqlBuff.toString());

		int index = 0;
		//Adds the column values in where clause
		for (int i = 0; i < whereColumnValue.length; i++)
		{
			if (whereColumnCondition[i].equals("is null")
					|| whereColumnCondition[i].equals("is not null"))
			{
			}
			else
			{

				Object obj = whereColumnValue[i];
				if (obj instanceof Object[])
				{
					Object[] valArr = (Object[]) obj;
					for (int j = 0; j < valArr.length; j++)
					{
						sqlQuery.setParameter(index, valArr[j]);
						index++;
					}
				}
				else
				{
					sqlQuery.setParameter(index, obj);
					index++;
				}
			}
		}

		return sqlQuery;
	}

}