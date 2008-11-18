package edu.wustl.common.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

import edu.wustl.common.util.logger.Logger;

public class DatabaseConnectionParams
{

	private Statement statement;
	private ResultSet resultSet;
	private Connection connection;
	private PreparedStatement preparedStatement;
		
	private static org.apache.log4j.Logger logger = Logger.getLogger(DatabaseConnectionParams.class);
	
	public Statement getDatabaseStatement()
	{
		try
		{
			
			statement = connection.createStatement();
			
		}
		catch (SQLException sqlExp)
		{
			
			logger.fatal("Problem Occured while creating database connection parameters", sqlExp);
		}
		return statement;
		
	}
	
	public ResultSet getResultSet(String query)
	{
		try
		{
			resultSet = statement.executeQuery(query);
			
		}
		catch (SQLException sqlExp)
		{
			
			logger.fatal("Problem Occured while creating database connection parameters", sqlExp);
		}
		return resultSet;
	}
	
	public ResultSetMetaData getMetaData(String query)
	{
		ResultSetMetaData metaData = null;
		try
		{
			
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			metaData = resultSet.getMetaData();
			
		} 
		catch (SQLException sqlExp)
		{
			
			logger.fatal("Problem Occured while creating database connection parameters", sqlExp);
		}
		
		return metaData;
	}
	
	public void closeConnectionParams()
	{
		try
		{
			
			if(connection != null)
			{
				connection.close();
			}
			if(resultSet != null )
			{
				resultSet.close();
			}
			if (statement != null)
			{
				statement.close();
			}
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}
			
		}
		catch(SQLException sqlExp)
		{
			
			logger.fatal(DAOConstants.CONNECTIONS_CLOSING_ISSUE, sqlExp);
		}
	}
	
	public PreparedStatement getPreparedStatement(String query)
	{
		try 
		{
			preparedStatement = (PreparedStatement) connection.prepareStatement(query);
		}
		catch (SQLException sqlExp)
		{
			
			logger.fatal("Problem Occured while creating database connection parameters", sqlExp);
		}
		return preparedStatement;
	}
	
	public void executeUpdate(String query)
	{	
		PreparedStatement stmt = null;
		try
		{
			stmt = getPreparedStatement(query);
			stmt.executeUpdate();
		} 
		catch (SQLException sqlExp)
		{
			logger.error(DAOConstants.EXECUTE_UPDATE_ERROR, sqlExp);
		}
		finally 
		{
			closeConnectionParams();
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
			this.connection = connection;
	}
	
}
