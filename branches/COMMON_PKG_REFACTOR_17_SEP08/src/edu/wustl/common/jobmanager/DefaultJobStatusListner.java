
package edu.wustl.common.jobmanager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultJobStatusListner.
 */
public class DefaultJobStatusListner implements JobStatusListener
{

	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(DefaultJobStatusListner.class);

	/* (non-Javadoc)
	 * @see edu.wustl.common.jobManager.JobStatusListener#jobStatusCreated
	 * (edu.wustl.common.jobManager.JobData)
	 */
	public void jobStatusCreated(final JobData jobData)
	{
		JDBCDAO jdbcdao = null;
		ResultSet resultSet = null;
		try
		{
			LinkedList<ColumnValueBean> columnValueBeanlist = new LinkedList<ColumnValueBean>();

			final String applicationName = CommonServiceLocator.getInstance().getAppName();
			jdbcdao = DAOConfigFactory.getInstance().getDAOFactory(applicationName).getJDBCDAO();
			jdbcdao.openSession(null);
			resultSet = jdbcdao.getQueryResultSet("select count(*) from "
					+ Constants.BULK_OPERATION_LOG_TABLE);
			resultSet.next();
			long count = resultSet.getLong(1);
			long identifier = count + 1;

			Iterator keyItr = jobData.getJobStatusEntry().keySet().iterator();
			StringBuffer query = new StringBuffer();
			StringBuffer valueQuery = new StringBuffer();
			query.append("insert into " + Constants.BULK_OPERATION_LOG_TABLE
					+ " (IDENTIFIER,operation_name,status,USER_ID,started_time");
			valueQuery.append("values(?,?,?,?,?");

			columnValueBeanlist.add(new ColumnValueBean("IDENTIFIER", identifier));
			columnValueBeanlist.add(new ColumnValueBean("operation_name", jobData.getJobName()));
			columnValueBeanlist.add(new ColumnValueBean("status", jobData.getJobStatus()));
			columnValueBeanlist.add(new ColumnValueBean("USER_ID", jobData.getJobStartedBy()));
			columnValueBeanlist.add(new ColumnValueBean("started_time", jobData.getStartedTime()));

			Object key = null;
			while (keyItr.hasNext())
			{
				key = keyItr.next();
				valueQuery.append(",?");
				query.append(',');
				query.append(key);
				//				valueQuery.append("?");
				columnValueBeanlist.add(new ColumnValueBean(key.toString(), jobData
						.getJobStatusEntryValue(key)));
			}
			valueQuery.append(')');
			query.append(')');
			query.append(valueQuery);
			jdbcdao.executeUpdate(query.toString(), columnValueBeanlist);
			jdbcdao.commit();
			jobData.setJobID(identifier);
		}
		catch (final DAOException daoExp)
		{
			LOGGER.debug(daoExp.getMessage(), daoExp);
		}
		catch (SQLException e)
		{
			LOGGER.debug(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (jdbcdao != null)
				{
					jdbcdao.closeSession();
				}
			}
			catch (final DAOException daoExp)
			{
				LOGGER.debug(daoExp.getMessage(), daoExp);
			}
		}

	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.jobManager.JobStatusListener#jobStatusUpdated
	 * (edu.wustl.common.jobManager.JobData)
	 */
	public void jobStatusUpdated(final JobData jobData)
	{
		JDBCDAO jdbcdao = null;
		ResultSet resultSet = null;
		try
		{
			LinkedList<ColumnValueBean> columnValueBeanlist = new LinkedList<ColumnValueBean>();
			final String applicationName = CommonServiceLocator.getInstance().getAppName();
			jdbcdao = DAOConfigFactory.getInstance().getDAOFactory(applicationName).getJDBCDAO();
			jdbcdao.openSession(null);

			Iterator keyItr = jobData.getJobStatusEntry().keySet().iterator();
			StringBuffer query = new StringBuffer();
			query.append("update " + Constants.BULK_OPERATION_LOG_TABLE
					+ " set operation_name=?,status=?");

			columnValueBeanlist.add(new ColumnValueBean("operation_name", jobData.getJobName()));
			columnValueBeanlist.add(new ColumnValueBean("status", jobData.getJobStatus()));

			Object key = null;
			while (keyItr.hasNext())
			{
				key = keyItr.next();
				query.append(',');
				query.append(key);
				query.append("=? ");
				columnValueBeanlist.add(new ColumnValueBean(key.toString(), jobData
						.getJobStatusEntryValue(key)));
			}
			query.append(" where IDENTIFIER=" + jobData.getJobID() + " and USER_ID="
					+ jobData.getJobStartedBy());
			jdbcdao.executeUpdate(query.toString(), columnValueBeanlist);
			jdbcdao.commit();
		}
		catch (final DAOException daoExp)
		{
			LOGGER.debug(daoExp.getMessage(), daoExp);
		}
		finally
		{
			try
			{
				if (jdbcdao != null)
				{
					jdbcdao.closeSession();
				}
			}
			catch (final DAOException daoExp)
			{
				LOGGER.debug(daoExp.getMessage(), daoExp);
			}
		}

	}

}
