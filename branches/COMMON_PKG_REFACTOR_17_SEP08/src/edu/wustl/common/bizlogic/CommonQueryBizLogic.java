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
			queryParams.setHasConditionOnIdentifiedField(querySessionData.isHasConditionOnIdentifiedField());
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
	 * @param sql sql to be fired
	 * @param sessionData sessiondata to get userId and ip address
	 * @throws DAOException Daoexception
	 */
	public void insertQuery(String sqlQuery, SessionDataBean sessionData) throws DAOException
	{
		JDBCDAO jdbcDAO = null;
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
		jdbcDAO = daofactory.getJDBCDAO();
		try
		{
			String sqlQuery1 = sqlQuery.replaceAll("'", "''");
			jdbcDAO.openSession(null);
			String comments = "QueryLog";
			// commenting temporary to avoid compilation errors
			
		//	jdbcDAO.executeAuditSql(sqlQuery1,sessionData,comments);
		}
		finally
		{
			jdbcDAO.closeSession();
		}
		//	long no = 1;
		/*SimpleDateFormat fSDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeStamp = fSDateFormat.format(new Date());

			String ipAddr = sessionData.getIpAddress();

			String userId = sessionData.getUserId().toString();
		 */		

		/*if (Variables.databaseName.equals(Constants.MYSQL_DATABASE))
			{
				String sqlForAudiEvent = "insert into catissue_audit_event(IP_ADDRESS,EVENT_TIMESTAMP,USER_ID ,COMMENTS) values ('"
						+ ipAddr + "','" + timeStamp + "','" + userId + "','" + comments + "')";
				jdbcDAO.executeUpdate(sqlForAudiEvent);

				String sql = "select max(identifier) from catissue_audit_event where USER_ID='"
						+ userId + "'";

				List list = jdbcDAO.executeQuery(sql, null, false, null);

				if (!list.isEmpty())
				{

					List columnList = (List) list.get(0);
					if (!columnList.isEmpty())
					{
						String str = (String) columnList.get(0);
						if (!str.equals(""))
						{
							no = Long.parseLong(str);

						}
					}
				}

				String sqlForQueryLog = "insert into catissue_audit_event_query_log(QUERY_DETAILS,AUDIT_EVENT_ID) values ('"
						+ sqlQuery1 + "','" + no + "')";
				Logger.out.debug("sqlForQueryLog:" + sqlForQueryLog);
				jdbcDAO.executeUpdate(sqlForQueryLog);
			}
			else
			{
				String sql = "select CATISSUE_AUDIT_EVENT_PARAM_SEQ.nextVal from dual";

				List list = jdbcDAO.executeQuery(sql, null, false, null);

				if (!list.isEmpty())
				{

					List columnList = (List) list.get(0);
					if (!columnList.isEmpty())
					{
						String str = (String) columnList.get(0);
						if (!str.equals(""))
						{
							no = Long.parseLong(str);

						}
					}
				}
				String sqlForAudiEvent = "insert into catissue_audit_event(IDENTIFIER,IP_ADDRESS,EVENT_TIMESTAMP,USER_ID ,COMMENTS) values ('"
						+ no
						+ "','"
						+ ipAddr
						+ "',to_date('"
						+ timeStamp
						+ "','yyyy-mm-dd HH24:MI:SS'),'" + userId + "','" + comments + "')";
				Logger.out.info("sqlForAuditLog:" + sqlForAudiEvent);
				jdbcDAO.executeUpdate(sqlForAudiEvent);

				long queryNo = 1;
				sql = "select CATISSUE_AUDIT_EVENT_QUERY_SEQ.nextVal from dual";

				list = jdbcDAO.executeQuery(sql, null, false, null);

				if (!list.isEmpty())
				{

					List columnList = (List) list.get(0);
					if (!columnList.isEmpty())
					{
						String str = (String) columnList.get(0);
						if (!str.equals(""))
						{
							queryNo = Long.parseLong(str);

						}
					}
				}
				String sqlForQueryLog = "insert into catissue_audit_event_query_log(IDENTIFIER,QUERY_DETAILS,AUDIT_EVENT_ID) "
						+ "values (" + queryNo + ",EMPTY_CLOB(),'" + no + "')";
				jdbcDAO.executeUpdate(sqlForQueryLog);
				String sql1 = "select QUERY_DETAILS from catissue_audit_event_query_log where IDENTIFIER="+queryNo+" for update";
				list = jdbcDAO.executeQuery(sql1, null, false, null);

				CLOB clob=null;

				if (!list.isEmpty())
				{

					List columnList = (List) list.get(0);
					if (!columnList.isEmpty())
					{
						clob = (CLOB)columnList.get(0);
					}
				}
//				get output stream from the CLOB object
				OutputStream os = clob.getAsciiOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);

//			use that output stream to write character data to the Oracle data store
				osw.write(sqlQuery1.toCharArray());
				//write data and commit
				osw.flush();
				osw.close();
				os.close();
				jdbcDAO.commit();
				Logger.out.info("sqlForQueryLog:" + sqlForQueryLog);


			}
		}
		catch(IOException e)
		{
			throw new DAOException(e.getMessage());
		}
		catch(SQLException e)
		{
			throw new DAOException(e.getMessage());
		}*/
		/*}catch (DAOException e)
		{
			throw (e);
		}
		finally
		{
			jdbcDAO.closeSession();
		}*/
		/*String sqlForQueryLog = "insert into catissue_audit_event_query_log(IDENTIFIER,QUERY_DETAILS) values ('"
		 + no + "','" + sqlQuery1 + "')";
		 jdbcDAO.executeUpdate(sqlForQueryLog);*/

	}
}
