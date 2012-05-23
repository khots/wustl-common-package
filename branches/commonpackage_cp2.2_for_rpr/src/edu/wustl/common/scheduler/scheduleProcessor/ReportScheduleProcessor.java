
package edu.wustl.common.scheduler.scheduleProcessor;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.wustl.common.report.CustomReportGenerator;
import edu.wustl.common.report.ReportGenerator;
import edu.wustl.common.report.reportBizLogic.ReportBizLogic;
import edu.wustl.common.scheduler.bizLogic.ReportAuditDataBizLogic;
import edu.wustl.common.scheduler.domain.BaseSchedule;
import edu.wustl.common.scheduler.domain.ReportAuditData;
import edu.wustl.common.scheduler.util.ReportSchedulerUtil;
import edu.wustl.common.scheduler.util.SchedulerDataUtility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;


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
			for (ReportAuditData reportAuditData : dataList)
			{
				if(reportAuditData.getJobStatus()!=null)
				{
					if (reportAuditData.getJobStatus().equalsIgnoreCase("Completed"))
					{
						body.append(
								ReportSchedulerUtil.getFileDownloadLink(reportAuditData.getId()
										.toString())).append("\n");
					}
					else if (reportAuditData.getJobStatus().equalsIgnoreCase("Error"))
					{
						body.append("Error generating report "
								+ repoBiz.getReportNameById(reportAuditData.getReportId()));
						body.append(" Cause: " + reportAuditData.getErrorDescription());
					}
					reportAuditData.setIsEmailed(true);
					reportAuditBiz.update(reportAuditData);
				}
			}
			SchedulerDataUtility.sendScheduleMail(email, "Subject", body.toString());
			System.out.println("Mail: "+body.toString());
		}
	}

	/* (non-Javadoc)
	 * @see main.java.scheduler.scheduleProcessors.AbstractScheduleProcessor#generateTicket()
	 */
	@Override
	protected void generateTicket() throws Exception
	{
		ReportAuditData reportAuditData = new ReportAuditData();
		//populate schedule id
		reportAuditData.setScheduleId(scheduleObject.getId());
		//populate is emailed
		reportAuditData.setIsEmailed(false);
		////populate report generation start and end date
		Map<String, Date> reportDurationMap = ReportSchedulerUtil.durationMap.get((scheduleObject
				.getInterval()));
		reportAuditData.setReportDurationStart(reportDurationMap.get("startDate"));
		reportAuditData.setReportDurationEnd(reportDurationMap.get("endDate"));
		
		ReportAuditDataBizLogic reportAuditBiz = new ReportAuditDataBizLogic();
		ReportBizLogic repoBiz = new ReportBizLogic();
	
		//one schedule can have multiple reports so we will have multiple tickets.
		for (Long reportId : ((BaseSchedule) scheduleObject).getScheduleItemIdCollection())
		{
			reportAuditData.setReportId(reportId);
			String reportName = repoBiz.getReportNameById(reportId);
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

}
