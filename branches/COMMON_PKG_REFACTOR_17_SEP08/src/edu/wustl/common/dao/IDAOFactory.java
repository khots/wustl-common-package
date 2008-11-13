package edu.wustl.common.dao;

import edu.wustl.common.util.dbmanager.DAOException;



public interface IDAOFactory
{	
	void buildSessionFactory ()throws DAOException;
	
	void setDefaultDAOClassName(String defaultDAOClassName);
	String getDefaultDAOClassName();
	
	void setJdbcDAOClassName(String jdbcDAOClassName);
	String getJdbcDAOClassName();
	
	void setConnectionManagerName(String connectionManagerName);
	String getConnectionManagerName();
	
	void setApplicationName(String applicationName);
	String getApplicationName();
	
	void setConfigurationFile(String configurationFile);
	String getConfigurationFile();
	
	DAO getDAO();
	
	JDBCDAO getJDBCDAO();

}
