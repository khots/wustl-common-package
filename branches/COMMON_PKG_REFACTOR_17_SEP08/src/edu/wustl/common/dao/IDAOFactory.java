package edu.wustl.common.dao;



public interface IDAOFactory
{	
	void buildSessionFactory ();
	void setDefaultDAOClassName(String defaultDAOClassName);
	void setJDBCDAOClassName(String jdbcDAOClassName);
	void setConnectionManagerName(String connectionManagerName);
	void setApplicationName(String applicationName);
	void setConfigurationFile(String configurationFile);
	
	String getDefaultDAOClassName();
	String getJDBCDAOClassName();
	String getConnectionManagerName();
	String getApplicationName();
	String getconfigurationFile();
	DAO getDAO();
	JDBCDAO getJDBCDAO();

}
