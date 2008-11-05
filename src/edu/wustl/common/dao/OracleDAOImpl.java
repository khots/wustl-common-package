package edu.wustl.common.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.dbmanager.DBUtil;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;


public class OracleDAOImpl extends JDBCDAOImpl
{

	private Connection connection = null;
	protected AuditManager auditManager;
	private static org.apache.log4j.Logger logger = Logger.getLogger(OracleDAOImpl.class);

	/**
	 * This method will be used to establish the session with the database.
	 * Declared in AbstractDAO class.
	 * 
	 * @throws DAOException
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{
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

		try
		{
			//Creates a connection.
			connection = DBUtil.getConnection();// getConnection(database, loginName, password);
			connection.setAutoCommit(false);
		}
		catch (Exception sqlExp)
		{
			//throw new DAOException(sqlExp.getMessage(),sqlExp);
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
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
			auditManager = null;
			DBUtil.closeConnection();
			//        	if (connection != null && !connection.isClosed())
			//        	    connection.close();
		}
		catch (Exception sqlExp)
		{
			//            new DAOException(sqlExp.getMessage(),sqlExp);
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);

		}
	}

	/**
	 * Commit the database level changes.
	 * Declared in AbstractDAO class.
	 * @throws DAOException
	 * @throws SMException
	 */
	public void commit() throws DAOException
	{
		try
		{
			auditManager.insert(this);

			if (connection != null)
				connection.commit();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			//throw new DAOException("Error in commit", dbex);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, dbex);
		}
	}

	/**
	 * Rollback all the changes after last commit. 
	 * Declared in AbstractDAO class. 
	 * @throws DAOException
	 */
	public void rollback()
	{
		try
		{
			if (connection != null)
				connection.rollback();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
		}
	}
	
	
	/**
	 * Deletes the specified table
	 * @param tableName
	 * @throws DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("select tname from tab where tname='" + tableName + "'");
		
		try
		{
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query.toString());
			boolean isTableExists = rs.next();
			logger.debug("ORACLE****" + query.toString() + isTableExists);
			if (isTableExists)
			{
				logger.debug("Drop Table");
				executeUpdate("DROP TABLE " + tableName + " cascade constraints");
			}
			rs.close();
			statement.close();
		}
		catch (Exception sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
		}
		
	}

	
	
}
