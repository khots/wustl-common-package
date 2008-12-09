package edu.wustl.common.bizlogic;

import edu.wustl.common.beans.SessionDataBean;
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
			queryParams.setHasConditionOnIdentifiedField(querySessionData.isHasConditionOnIdentifiedField());
			queryParams.setQueryResultObjectDataMap(querySessionData.getQueryResultObjectDataMap());
			queryParams.setStartIndex(startIndex);
			queryParams.setNoOfRecords(querySessionData.getRecordsPerPage());
			
			
			edu.wustl.common.dao.queryExecutor.PagenatedResultData pagenatedResultData = dao
					.executeQuery(queryParams);

			return pagenatedResultData;
		}
		catch (DAOException daoExp)
		{
			throw new DAOException(daoExp.getMessage(), daoExp);
		}
		catch (ClassNotFoundException classExp)
		{
			throw new DAOException(classExp.getMessage(), classExp);
		}
		finally
		{
			dao.closeSession();
		}
	}
}
