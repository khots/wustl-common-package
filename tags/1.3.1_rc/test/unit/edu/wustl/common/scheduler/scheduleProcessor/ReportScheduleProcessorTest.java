
package edu.wustl.common.scheduler.scheduleProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import com.mchange.util.AssertException;

import static org.junit.Assert.assertEquals;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.report.CustomReportGenerator;
import edu.wustl.common.report.ReportGenerator;
import edu.wustl.common.report.reportBizLogic.ReportBizLogic;
import edu.wustl.common.scheduler.bizLogic.ReportAuditDataBizLogic;
import edu.wustl.common.scheduler.domain.BaseSchedule;
import edu.wustl.common.scheduler.domain.ReportAuditData;
import edu.wustl.common.scheduler.domain.ReportSchedule;
import edu.wustl.common.scheduler.exception.ScheduleExpiredException;
import edu.wustl.common.scheduler.processorScheduler.Scheduler;
import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;
import edu.wustl.common.scheduler.scheduleProcessor.ReportScheduleProcessor;
import edu.wustl.common.scheduler.util.ReportSchedulerUtil;
import edu.wustl.common.scheduler.util.SchedulerDataUtility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.OracleDAOImpl;
import edu.wustl.dao.exception.DAOException;

@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)

public class ReportScheduleProcessorTest
{

	@Test
	@PrepareForTest({ReportScheduleProcessor.class, ReportAuditDataBizLogic.class,
			ReportBizLogic.class, ReportSchedulerUtil.class})
	public void testMail() throws Exception
	{ //test data definitions
		List<Long> idList = Arrays.asList(2L);

		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setJobStatus("Completed");

		ReportAuditData repoAudit1 = new ReportAuditData();
		repoAudit1.setCsId(541L);
		repoAudit1.setId(2L);
		repoAudit1.setUserId(2L);
		repoAudit1.setReportId(2L);
		repoAudit1.setFileName("mockFile");
		repoAudit1.setJobStatus("Error");

		List<ReportAuditData> repoAuditList = new ArrayList<ReportAuditData>();

		repoAuditList.add(repoAudit);
		repoAuditList.add(repoAudit1);

		ReportSchedule repo = new ReportSchedule();
		repo.setName("mockReportSchedule");
		repo.setIncludeMe(true);
		repo.setOwnerId(2L);

		// mock definitions
		ReportScheduleProcessor mockObj = PowerMock.createPartialMock(
				ReportScheduleProcessor.class, "getTicketIdList", "populateMailHeader",
				"populateMailBodyEnding", "sendMail", "populateDownloadLinkInMail");

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();

		EasyMock.expect(mockObj.getTicketIdList()).andReturn(idList).anyTimes();

		PowerMock
				.expectPrivate(mockObj, "populateDownloadLinkInMail",
						(ReportBizLogic) EasyMock.anyObject(),
						(StringBuilder) EasyMock.anyObject(),
						(ReportAuditData) EasyMock.anyObject()).andReturn(true).anyTimes();

		PowerMock
				.expectPrivate(mockObj, "populateMailHeader", (Long) EasyMock.anyObject(),
						(StringBuilder) EasyMock.anyObject()).andReturn(true).anyTimes();
		PowerMock
				.expectPrivate(mockObj, "populateMailBodyEnding",
						(StringBuilder) EasyMock.anyObject()).andReturn(true).anyTimes();
		PowerMock
				.expectPrivate(mockObj, "sendMail", (String) EasyMock.anyObject(),
						(StringBuilder) EasyMock.anyObject()).andReturn(true).anyTimes();

		mockObj.setScheduleObject(repo);

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				repoAuditBiz.getReportAuditDataListbyUser((Long) EasyMock.anyObject(),
						(List<Long>) EasyMock.anyObject())).andReturn(repoAuditList)
				.andThrow(new NullPointerException("mock"));
		repoAuditBiz.update(EasyMock.anyObject());
		EasyMock.expectLastCall().anyTimes();

		PowerMock.mockStatic(ReportSchedulerUtil.class);
		EasyMock.expect(ReportSchedulerUtil.getFileDownloadLink((String) EasyMock.anyObject()))
				.andReturn("mockLink").anyTimes();
		EasyMock.expect(ReportSchedulerUtil.getEmail((Long) EasyMock.anyObject()))
				.andReturn("mock@mail").anyTimes();
		// replay state called
		PowerMock.replay(mockObj);
		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);
		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);
		PowerMock.replay(ReportSchedulerUtil.class);
		//actual call
		assertEquals(mockObj.mail(), true);
		PowerMock.verify(mockObj);

	}

	@Test
	@PrepareForTest({ReportScheduleProcessor.class, ReportAuditDataBizLogic.class,
			ReportBizLogic.class, ReportGenerator.class, ReportSchedulerUtil.class,
			CustomReportGenerator.class})
	public void testExecuteSchedule() throws Exception
	{
		//test data definitions
		List<Long> idList = Arrays.asList(2L);
		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setJobStatus("Completed");
		List<ReportAuditData> repoAuditList = new ArrayList<ReportAuditData>();
		repoAuditList.add(repoAudit);

		ReportSchedule repo = new ReportSchedule();
		repo.setName("mockReportSchedule");
		repo.setIncludeMe(true);
		repo.setOwnerId(2L);

		//mock definitions
		ReportScheduleProcessor mockObj = PowerMock.createPartialMock(
				ReportScheduleProcessor.class, "getTicketIdList");
		EasyMock.expect(mockObj.getTicketIdList()).andReturn(idList).anyTimes();
		mockObj.setScheduleObject(repo);

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				(repoAuditBiz.retrieve((String) EasyMock.anyObject(), (Long) EasyMock.anyObject())))
				.andReturn(repoAudit).anyTimes();
		repoAuditBiz.update((ReportAuditData) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();

		CustomReportGenerator custRepoGen = PowerMock.createMock(CustomReportGenerator.class);
		custRepoGen.generateReport((Long) EasyMock.anyObject());
		EasyMock.expectLastCall().anyTimes();

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();

		PowerMock.mockStatic(ReportGenerator.class);
		EasyMock.expect(ReportGenerator.getImplObj((String) EasyMock.anyObject()))
				.andReturn(custRepoGen).anyTimes();

		PowerMock.mockStatic(ReportSchedulerUtil.class);
		EasyMock.expect(ReportSchedulerUtil.generateReport(repoAudit)).andReturn(true).anyTimes();

		//replay state called
		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);
		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);
		PowerMock.replay(custRepoGen);
		PowerMock.replay(CustomReportGenerator.class);
		PowerMock.replay(ReportGenerator.class);
		PowerMock.replay(mockObj);

		//actual call
		mockObj.executeSchedule();
		PowerMock.verify(mockObj);
	}

	@Test
	@PrepareForTest({ReportScheduleProcessor.class, ReportAuditDataBizLogic.class,
			ReportBizLogic.class, ReportSchedulerUtil.class,})
	public void testGenerateTicket() throws Exception
	{

		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setJobStatus("Completed");

		ReportSchedule repo = new ReportSchedule();
		repo.setName("mockReportSchedule");
		repo.setIncludeMe(true);
		repo.setOwnerId(2L);
		repo.setInterval("monthly");
		repo.setScheduleItemIdCollection(Arrays.asList(1L, 2L));

		PowerMock.suppress(PowerMock.method(ReportScheduleProcessor.class,
				"populateAuditDataWithCsId", ReportAuditData.class, ReportBizLogic.class,
				String.class));

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				(repoAuditBiz.retrieve((String) EasyMock.anyObject(), (Long) EasyMock.anyObject())))
				.andReturn(repoAudit).anyTimes();
		repoAuditBiz.insert((ReportAuditData) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();

		ReportScheduleProcessor repoSched = new ReportScheduleProcessor();
		repoSched.setScheduleObject(repo);

		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);
		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		Whitebox.invokeMethod(repoSched, "generateTicket");

	}

	@Test
	@PrepareForTest({ReportScheduleProcessor.class})
	public void testPostScheduleExecution() throws Exception
	{
		PowerMock.suppress(PowerMock.method(ReportScheduleProcessor.class, "mail"));
		ReportScheduleProcessor repoSchedProc = new ReportScheduleProcessor();
		repoSchedProc.postScheduleExecution();
	}

	@Test
	@PrepareForTest({ReportScheduleProcessor.class, SchedulerDataUtility.class,
			SchedulerConfigurationPropertiesHandler.class})
	public void testSendMail() throws Exception
	{
		ReportSchedule repo = new ReportSchedule();
		repo.setName("mockReportSchedule");
		repo.setIncludeMe(true);
		repo.setOwnerId(2L);

		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject())).andReturn("dummy")
				.anyTimes();

		PowerMock.suppress(PowerMock.method(SchedulerDataUtility.class, "sendScheduleMail",
				String.class, String.class, String.class));

		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);
		ReportScheduleProcessor repoSchedProc = new ReportScheduleProcessor();
		repoSchedProc.setScheduleObject(repo);
		Object obj = Whitebox.invokeMethod(repoSchedProc, "sendMail", "email", new StringBuilder(
				"body"));

		assertEquals(obj, new Boolean(true));

	}

	@Test
	@PrepareForTest({ReportBizLogic.class})
	public void testPopulateErrorMessageInMail() throws Exception
	{
		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setJobStatus("Completed");

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();
		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);

		ReportScheduleProcessor repoSchedProc = new ReportScheduleProcessor();
		Whitebox.invokeMethod(repoSchedProc, "populateErrorMessageInMail", repoBiz,
				new StringBuilder("body"), repoAudit);
	}

	@Test
	@PrepareForTest({ReportBizLogic.class, ReportSchedulerUtil.class})
	public void testpopulateDownloadLinkInMail() throws Exception
	{
		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setJobStatus("Completed");

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();

		PowerMock.mockStatic(ReportSchedulerUtil.class);
		EasyMock.expect(ReportSchedulerUtil.getFileDownloadLink((String) EasyMock.anyObject()))
				.andReturn("dummyLink").anyTimes();

		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);
		PowerMock.replay(ReportSchedulerUtil.class);

		ReportScheduleProcessor repoSchedProc = new ReportScheduleProcessor();
		Object obj = Whitebox.invokeMethod(repoSchedProc, "populateDownloadLinkInMail", repoBiz,
				new StringBuilder("body"), repoAudit);

		assertEquals(obj, new Boolean(true));

	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, SchedulerConfigurationPropertiesHandler.class})
	public void tetsPopulateMailHeader() throws Exception
	{

		PowerMock.mockStatic(SchedulerDataUtility.class);
		EasyMock.expect(SchedulerDataUtility.getUserNamesList((List<Long>) EasyMock.anyObject()))
				.andReturn(Arrays.asList("dummy")).anyTimes();
		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject())).andReturn("dummy")
				.anyTimes();

		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.replay(SchedulerDataUtility.class);

		ReportScheduleProcessor repoSchedProc = new ReportScheduleProcessor();
		assertEquals(repoSchedProc.populateMailHeader(12L, new StringBuilder("body")), true);

	}

	@Test
	@PrepareForTest({CommonServiceLocator.class, SchedulerConfigurationPropertiesHandler.class})
	public void testPopulateMailBodyEnding() throws Exception
	{
		CommonServiceLocator commonConfig = PowerMock.createMock(CommonServiceLocator.class);
		PowerMock.expectNew(CommonServiceLocator.class).andReturn(commonConfig).anyTimes();
		EasyMock.expect(commonConfig.getDatePattern()).andReturn("mm/dd/yyyy").anyTimes();

		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject())).andReturn("12")
				.anyTimes();

		PowerMock.replay(commonConfig);
		PowerMock.replay(CommonServiceLocator.class);
		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);

		ReportScheduleProcessor repoSchedProc = new ReportScheduleProcessor();
		Object obj = Whitebox.invokeMethod(repoSchedProc, "populateMailBodyEnding",
				new StringBuilder("body"));

		assertEquals(obj, new Boolean(true));

	}

	@Test
	@PrepareForTest({ReportSchedulerUtil.class, CommonServiceLocator.class,
			SchedulerDataUtility.class, JDBCDAO.class, OracleDAOImpl.class, ResultSet.class,
			ReportAuditDataBizLogic.class, ReportBizLogic.class})
	public void testPopulateAuditDataWithCsId() throws Exception
	{
		JDBCDAO dao = PowerMock.createMock(OracleDAOImpl.class);
		dao.openSession((SessionDataBean) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();
		dao.closeSession();
		PowerMock.expectLastCall().anyTimes();

		PowerMock.mockStatic(SchedulerDataUtility.class);
		EasyMock.expect(SchedulerDataUtility.getJDBCDAO()).andReturn(dao).anyTimes();

		ResultSet mockResultSet = PowerMock.createMock(ResultSet.class);
		EasyMock.expect(mockResultSet.getObject((String) EasyMock.anyObject())).andReturn(12L)
				.anyTimes();
		mockResultSet.close();
		PowerMock.expectLastCall().anyTimes();
		EasyMock.expect(mockResultSet.next()).andReturn(true).anyTimes();

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(
				repoBiz.getReportDetailsResult((JDBCDAO) EasyMock.anyObject(),
						(String) EasyMock.anyObject())).andReturn(mockResultSet).anyTimes();

		PowerMock.replay(dao);
		PowerMock.replay(OracleDAOImpl.class);
		PowerMock.replay(SchedulerDataUtility.class);
		PowerMock.replay(mockResultSet);
		PowerMock.replay(ResultSet.class);
		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);

		ReportScheduleProcessor repoSchedProc = new ReportScheduleProcessor();

		Whitebox.invokeMethod(repoSchedProc, "populateAuditDataWithCsId", new ReportAuditData(),
				repoBiz, "dummyReport");

	}

	@Test
	@PrepareForTest({ReportScheduleProcessor.class, ReportAuditDataBizLogic.class,
			ReportBizLogic.class, ReportSchedulerUtil.class})
	public void testRedundantMethods() throws Exception
	{
		PowerMock.suppress(PowerMock.methods(ReportScheduleProcessor.class, "shutDownIfExpired"));
		PowerMock.suppress(PowerMock.methods(ReportScheduleProcessor.class, "generateTicket"));
		PowerMock.suppress(PowerMock.methods(ReportScheduleProcessor.class, "executeSchedule"));
		PowerMock.suppress(PowerMock
				.methods(ReportScheduleProcessor.class, "postScheduleExecution"));
		PowerMock
				.suppress(PowerMock.methods(ReportScheduleProcessor.class, "populateEmailAddress"));

		ReportScheduleProcessor repoProc = new ReportScheduleProcessor();
		repoProc.run();
		assertEquals(true, repoProc.getTicketIdList().isEmpty());
		assertEquals(null, repoProc.getEmailAndIdList());
		repoProc.setEmailAndIdList();
		repoProc.setScheduledItemFileDetails(null);
		assertEquals(null, repoProc.getScheduleObject());

	}

	@Test
	@PrepareForTest({ReportScheduleProcessor.class, SchedulerDataUtility.class})
	public void testPopulateEmailAddress() throws Exception
	{
		List<Long> list = new ArrayList<Long>();
		list.add(12L);
		ReportScheduleProcessor repoProc = new ReportScheduleProcessor();
		BaseSchedule base = new BaseSchedule();
		base.setIncludeMe(true);
		base.setRecepientUserIdCollection(list);
		base.setOwnerId(12L);
		repoProc.setScheduleObject(base);
		PowerMock.mockStatic(SchedulerDataUtility.class);
		EasyMock.expect(
				SchedulerDataUtility.getUsersIdAndEmailAddressList((List<Long>) EasyMock
						.anyObject())).andReturn(null).anyTimes();

		PowerMock.replay(SchedulerDataUtility.class);

		assertEquals(null, Whitebox.invokeMethod(repoProc, "populateEmailAddress"));

	}

	@Test
	@PrepareForTest({ReportScheduleProcessor.class, Scheduler.class})
	public void testShutDownIfExpired() throws Exception
	{
		ReportScheduleProcessor repoProc = new ReportScheduleProcessor();
		BaseSchedule base = new BaseSchedule();
		base.setId(12L);
		base.setIncludeMe(true);
		base.setRecepientUserIdCollection(Arrays.asList(1L, 2L));
		base.setOwnerId(12L);
		base.setEndDate(new Date(new Date().getTime()-(86400000*2)));
		repoProc.setScheduleObject(base);

		Scheduler scheduler = PowerMock.createMock(Scheduler.class);
		PowerMock.expectNew(Scheduler.class).andReturn(scheduler).anyTimes();
		scheduler.unSchedule(EasyMock.anyLong());
		PowerMock.expectLastCall().anyTimes();
		
		PowerMock.replay(scheduler);
		PowerMock.replay(Scheduler.class);
		

		try
		{
			Whitebox.invokeMethod(repoProc, "shutDownIfExpired");
		}
		catch(ScheduleExpiredException e)
		{
			
		}

	}

}
