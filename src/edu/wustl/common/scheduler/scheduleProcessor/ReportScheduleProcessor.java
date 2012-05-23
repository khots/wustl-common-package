
package edu.wustl.common.scheduler.scheduleProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.report.CustomReportGenerator;
import edu.wustl.common.report.ReportGenerator;
import edu.wustl.common.report.reportBizLogic.ReportBizLogic;
import edu.wustl.common.scheduler.bizLogic.ReportAuditDataBizLogic;
import edu.wustl.common.scheduler.constants.SchedulerConstants;
import edu.wustl.common.scheduler.domain.BaseSchedule;
import edu.wustl.common.scheduler.domain.ReportAuditData;
import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;
import edu.wustl.common.scheduler.util.ReportSchedulerUtil;
import edu.wustl.common.scheduler.util.SchedulerDataUtility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;

public class ReportScheduleProcessor extends AbstractScheduleProcessor
{

	private static final Logger log = Logger.getCommonLogger(ReportScheduleProcessor.class);

	/* (non-Javadoc)
	 * @see main.java.scheduler.scheduleProcessors.AbstractScheduleProcessor#executeSchedule()
	 */
	protected void executeSchedule() throws Exception
	{
		ReportGenerator reportGenerator = null;
		ReportBizLogic repoBiz = new ReportBizLogic();
		ReportAuditDataBizLogic repoAuditBiz = new ReportAuditDataBizLogic();
		for (Long ticketId : getTicketIdList())
		{
			ReportAuditData reportAuditData = (ReportAuditData) repoAuditBiz.retrieve(
					ReportAuditData.class.getName(), ticketId);
			String reportName = repoBiz.getReportNameById(reportAuditData.getReportId());
			reportGenerator = ReportGenerator.getImplObj(reportName);

			if (reportGenerator instanceof CustomReportGenerator)
			{
				reportGenerator.generateReport(ticketId);
			}
			else
			{
				ReportSchedulerUtil.generateReport(reportAuditData);
			}
		}
	}

	@Override
	protected void postScheduleExecution() throws Exception
	{
		mail();
	}

	/* (non-Javadoc)
	 * @see main.java.scheduler.scheduleProcessors.AbstractScheduleProcessor#mail()
	 * Mails download link if report execution is successfull and error details if execution fails.
	 */
	@Override
	protected void mail() throws Exception
	{
		Collection<Long> userCollection = ((BaseSchedule) scheduleObject)
				.getRecepientUserIdCollection();
		if (((BaseSchedule) scheduleObject).getIncludeMe())
		{
			userCollection.add(((BaseSchedule) scheduleObject).getOwnerId());
		}
		ReportAuditDataBizLogic reportAuditBiz = new ReportAuditDataBizLogic();
		for (Long userId : userCollection)
		{
			List<ReportAuditData> dataList = reportAuditBiz.getReportAuditDataListbyUser(userId,
					getTicketIdList());
			String email = ReportSchedulerUtil.getEmail(userId);
			ReportBizLogic repoBiz = new ReportBizLogic();
			StringBuilder body = new StringBuilder("");
			populateMailHeader(userId, body);
			for (ReportAuditData reportAuditData : dataList)
			{
				if (reportAuditData.getJobStatus() != null)
				{
					if (reportAuditData.getJobStatus().equalsIgnoreCase("Completed"))
					{
						populateDownloadLinkInMail(repoBiz, body, reportAuditData);
					}
					else if (reportAuditData.getJobStatus().equalsIgnoreCase("Error"))
					{
						populateErrorMessageInMail(repoBiz, body, reportAuditData);
					}
					reportAuditData.setIsEmailed(true);
					reportAuditBiz.update(reportAuditData);
				}
			}
			populateMailBodyEnding(body);
			sendMail(email, body);
			System.out.println("Mail: " + body.toString());
		}
	}

	/**
	 * @param email
	 * @param body
	 * @throws MessagingException
	 * @throws Exception
	 */
	private void sendMail(String email, StringBuilder body) throws MessagingException, Exception
	{
		SchedulerDataUtility.sendScheduleMail(
				email,
				(String) SchedulerConfigurationPropertiesHandler.getInstance().getProperty(
						"scheduler.mail.subject"), body.toString());
	}

	/**
	 * @param repoBiz
	 * @param body
	 * @param reportAuditData
	 * @throws BizLogicException
	 */
	private void populateErrorMessageInMail(ReportBizLogic repoBiz, StringBuilder body,
			ReportAuditData reportAuditData) throws BizLogicException
	{
		body.append(repoBiz.getReportNameById(reportAuditData.getReportId()) + ": "
				+ "Report could not be generated, please contact the administrator.");
	}

	/**
	 * @param repoBiz
	 * @param body
	 * @param reportAuditData
	 * @throws BizLogicException
	 * @throws Exception
	 */
	private void populateDownloadLinkInMail(ReportBizLogic repoBiz, StringBuilder body,
			ReportAuditData reportAuditData) throws BizLogicException, Exception
	{
		body.append(
				repoBiz.getReportNameById(reportAuditData.getReportId())
						+ ": "
						+ ReportSchedulerUtil.getFileDownloadLink(reportAuditData.getId()
								.toString())).append("\n");
	}

	/**
	 * @param userId
	 * @param body
	 * @throws Exception
	 */
	private void populateMailHeader(Long userId, StringBuilder body) throws Exception
	{
		body.append("Hello "
				+ SchedulerDataUtility.getUserNamesList(new ArrayList<Long>(Arrays.asList(userId)))
				+ ",\n");
		body.append((String) SchedulerConfigurationPropertiesHandler.getInstance().getProperty(
				"scheduler.mail.header"));
	}

	/**
	 * @param body
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void populateMailBodyEnding(StringBuilder body) throws NumberFormatException, Exception
	{
		Calendar deleteDay = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(CommonServiceLocator.getInstance()
				.getDatePattern());
		deleteDay.set(
				Calendar.DATE,
				deleteDay.get(Calendar.DATE)
						+ Integer.valueOf((String) SchedulerConfigurationPropertiesHandler
								.getInstance().getProperty("scheduler.cleanUp.timeInterval.days")));
		body.append(((String) SchedulerConfigurationPropertiesHandler.getInstance().getProperty(
				"scheduler.mail.end")).replace("?",
				formatter.format(new Date(deleteDay.getTimeInMillis()))));
	}

	/* (non-Javadoc)
	 * @see main.java.scheduler.scheduleProcessors.AbstractScheduleProcessor#generateTicket()
	 */
	@Override
	protected void generateTicket() throws Exception
	{
		ReportAuditData reportAuditData = new ReportAuditData();
		populateInitialAuditData(reportAuditData);
		ReportAuditDataBizLogic reportAuditBiz = new ReportAuditDataBizLogic();
		ReportBizLogic repoBiz = new ReportBizLogic();
		//one schedule can have multiple reports so we will have multiple tickets.
		for (Long reportId : ((BaseSchedule) scheduleObject).getScheduleItemIdCollection())
		{
			reportAuditData.setReportId(reportId);
			String reportName = repoBiz.getReportNameById(reportId);
			populateAuditDataWithCsId(reportAuditData, repoBiz, reportName);
			Collection<Long> recepientCollection = ((BaseSchedule) scheduleObject)
					.getRecepientUserIdCollection();
			if (((BaseSchedule) scheduleObject).getIncludeMe())
			{
				recepientCollection.add(((BaseSchedule) scheduleObject).getOwnerId());
			}
			// reports are generated for every recipient separately
			for (Long userId : recepientCollection)
			{
				reportAuditData.setId(null);
				reportAuditData.setUserId(userId);
				reportAuditBiz.insert(reportAuditData);
				addTicketId(reportAuditData.getId());
			}
		}
	}

	/**
	 * @param reportAuditData
	 * @param repoBiz
	 * @param reportName
	 * @throws DAOException
	 * @throws SQLException
	 * @throws NumberFormatException
	 */
	private void populateAuditDataWithCsId(ReportAuditData reportAuditData, ReportBizLogic repoBiz,
			String reportName) throws DAOException, SQLException, NumberFormatException
	{
		JDBCDAO dao = DAOConfigFactory.getInstance()
				.getDAOFactory(CommonServiceLocator.getInstance().getAppName()).getJDBCDAO();
		dao.openSession(null);
		try
		{
			ResultSet reportDetailRS = repoBiz.getReportDetailsResult(dao, reportName);
			if (reportDetailRS != null && reportDetailRS.next()
					&& reportDetailRS.getObject("CS_ID") != null)
			{
				//populate csid if its a study or cp report
				reportAuditData.setCsId(Long.valueOf(reportDetailRS.getObject("CS_ID").toString()));
				System.out.println("");
			}
		}
		finally
		{
			dao.closeSession();
		}
	}

	/**
	 * @param reportAuditData
	 */
	private void populateInitialAuditData(ReportAuditData reportAuditData)
	{
		//populate schedule id
		reportAuditData.setScheduleId(scheduleObject.getId());
		//populate is emailed
		reportAuditData.setIsEmailed(false);
		////populate report generation start and end date
		Map<String, Date> reportDurationMap = ReportSchedulerUtil.durationMap.get((scheduleObject
				.getInterval()));
		reportAuditData.setReportDurationStart(reportDurationMap.get("startDate"));
		reportAuditData.setReportDurationEnd(reportDurationMap.get("endDate"));
	}

}
