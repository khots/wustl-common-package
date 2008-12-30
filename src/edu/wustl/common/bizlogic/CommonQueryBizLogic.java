package edu.wustl.common.bizlogic;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * This class contains the methods required in Simple Query and Advanced Query.
 * @author deepti_shelar
 *
 */
public class CommonQueryBizLogic
{
	/**
	 * Method to execute the given SQL to get the query result.
	 * @param sessionDataBean reference to SessionDataBean object
	 * @param querySessionData query Session Data.
	 * @param startIndex The Starting index of the result set.
	 * @return The reference to PagenatedResultData, which contains the Query result information.
	 * @throws DAOException generic DAOException.
	 */
	public PagenatedResultData execute(SessionDataBean sessionDataBean,
			QuerySessionData querySessionData, int startIndex) throws DAOException
	{
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		JDBCDAO dao = daofactory.getJDBCDAO();

		try
		{
			dao.openSession(null);
			QueryParams queryParams = new QueryParams();
			queryParams.setQuery(querySessionData.getSql());
			queryParams.setSessionDataBean(sessionDataBean);
			queryParams.setSecureToExecute(querySessionData.isSecureExecute());
			queryParams.setHasConditionOnIdentifiedField(
					querySessionData.isHasConditionOnIdentifiedField());
			queryParams.setQueryResultObjectDataMap(querySessionData.getQueryResultObjectDataMap());
			queryParams.setStartIndex(startIndex);
			queryParams.setNoOfRecords(querySessionData.getRecordsPerPage());
			edu.wustl.common.util.PagenatedResultData pagenatedResultData = dao
					.executeQuery(queryParams);

			return pagenatedResultData;
		}
		catch (DAOException daoExp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("biz.exequery.error");
			throw new DAOException(errorKey, daoExp,"CommonQueryBizLogic");
		}
		catch (ClassNotFoundException exception)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("biz.exequery.error");
			throw new DAOException(errorKey, exception,"CommonQueryBizLogic");
		}
		finally
		{
			dao.closeSession();
		}
	}
	 /**
	 * This method fires a query to insert auditing details into audit tables.
	 * @param sqlQuery SQL query
	 * @param sessionData sessiondata to get userId and ip address
	 * @param comment Comment to be inserted in table
	 * @throws DAOException Daoexception
	 */
	public void auditQuery(String sqlQuery, SessionDataBean sessionData, String comment) throws DAOException
	{
		JDBCDAO jdbcDAO = null;
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		jdbcDAO = daofactory.getJDBCDAO();
		try
		{
			String sqlQuery1 = sqlQuery.replaceAll("'", "''");
			jdbcDAO.openSession(null);
			String comments = "QueryLog";
			jdbcDAO.executeAuditSql(sqlQuery1,sessionData,comment);
		}
		finally
		{
			jdbcDAO.closeSession();
		}
	}
}
