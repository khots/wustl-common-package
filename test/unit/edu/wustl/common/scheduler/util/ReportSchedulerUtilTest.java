
package edu.wustl.common.scheduler.util;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import edu.wustl.common.actionForm.ReportForm;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.report.CustomReportGenerator;
import edu.wustl.common.report.ReportGenerator;
import edu.wustl.common.report.bean.ReportBean;
import edu.wustl.common.report.reportBizLogic.ReportBizLogic;
import edu.wustl.common.scheduler.bizLogic.ReportAuditDataBizLogic;
import edu.wustl.common.scheduler.domain.ReportAuditData;
import edu.wustl.common.scheduler.exception.FileCleanedException;
import edu.wustl.common.scheduler.exception.NotAuthorizedToDownloadException;
import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;
import edu.wustl.common.scheduler.util.ReportSchedulerUtil;
import edu.wustl.common.scheduler.util.SchedulerDataUtility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.OracleDAOImpl;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)
public class ReportSchedulerUtilTest
{

	@Test
	@PrepareForTest({ZipOutputStream.class})
	public void testCreateZipFile() throws Exception
	{

		ZipOutputStream zip = PowerMock.createMock(ZipOutputStream.class);
		PowerMock.expectNew(ZipOutputStream.class, (FileOutputStream) EasyMock.anyObject())
				.andReturn(zip).anyTimes();

		zip.setLevel(EasyMock.anyInt());
		PowerMock.expectLastCall().anyTimes();
		zip.putNextEntry((ZipEntry) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();
		zip.write((byte[]) EasyMock.anyObject(), EasyMock.anyInt(), EasyMock.anyInt());
		PowerMock.expectLastCall().anyTimes();
		zip.closeEntry();
		PowerMock.expectLastCall().anyTimes();
		zip.close();
		PowerMock.expectLastCall().anyTimes();

		/*PowerMock.replay(fileOp);
		PowerMock.replay(FileOutputStream.class);*/
		PowerMock.replay(zip);
		PowerMock.replay(ZipOutputStream.class);
		try
		{
			ReportSchedulerUtil.createZipFile("demoName", Arrays.asList("dummy.txt"));
		}
		catch (Exception e)
		{

		}

	}

	@Test
	@PrepareForTest({ReportSchedulerUtil.class, ReportAuditDataBizLogic.class,
			ReportBizLogic.class, ReportGenerator.class, CustomReportGenerator.class})
	public void testGenerateReport() throws Exception
	{

		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setReportDurationStart(new Date());
		repoAudit.setReportDurationEnd(new Date(new Date().getTime() * 3));
		repoAudit.setJobStatus("Completed");

		ReportAuditData repoAudit1 = new ReportAuditData();
		repoAudit1.setCsId(541L);
		repoAudit1.setId(2L);
		repoAudit1.setUserId(2L);
		repoAudit1.setReportId(2L);
		repoAudit1.setFileName(null);
		repoAudit1.setReportDurationStart(new Date());
		repoAudit1.setReportDurationEnd(null);
		repoAudit1.setJobStatus("Completed");

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		repoAuditBiz.update((ReportAuditData) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();

		CustomReportGenerator custRepoGen = PowerMock.createMock(CustomReportGenerator.class);
		custRepoGen.generateCSV((ReportBean) EasyMock.anyObject());
		EasyMock.expectLastCall().anyTimes();

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();

		PowerMock.mockStatic(ReportGenerator.class);
		EasyMock.expect(ReportGenerator.getImplObj((String) EasyMock.anyObject()))
				.andReturn(custRepoGen).anyTimes();

		PowerMock.mockStaticPartial(ReportSchedulerUtil.class, "createZipFile");
		EasyMock.expect(
				ReportSchedulerUtil.createZipFile((String) EasyMock.anyObject(),
						(List<String>) EasyMock.anyObject())).andReturn(true).anyTimes();

		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);
		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);
		PowerMock.replay(ReportSchedulerUtil.class);
		PowerMock.replay(custRepoGen);
		PowerMock.replay(CustomReportGenerator.class);
		PowerMock.replay(ReportGenerator.class);

		assertEquals(ReportSchedulerUtil.generateReport(repoAudit), true);
		//assertEquals(ReportSchedulerUtil.generateReport(repoAudit1), false);

	}

	@Test
	@PrepareForTest({ReportSchedulerUtil.class, ReportAuditDataBizLogic.class,
			ReportBizLogic.class, ReportGenerator.class, CustomReportGenerator.class})
	public void testGenerateReportNegative() throws Exception
	{

		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setReportDurationStart(new Date());
		repoAudit.setReportDurationEnd(new Date(new Date().getTime() * 3));
		repoAudit.setJobStatus("Completed");

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		repoAuditBiz.update((ReportAuditData) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();

		CustomReportGenerator custRepoGen = PowerMock.createMock(CustomReportGenerator.class);
		custRepoGen.generateCSV((ReportBean) EasyMock.anyObject());
		EasyMock.expectLastCall().anyTimes();

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();

		PowerMock.mockStatic(ReportGenerator.class);
		EasyMock.expect(ReportGenerator.getImplObj((String) EasyMock.anyObject()))
				.andThrow(new NullPointerException()).anyTimes();

		PowerMock.mockStaticPartial(ReportSchedulerUtil.class, "createZipFile");
		EasyMock.expect(
				ReportSchedulerUtil.createZipFile((String) EasyMock.anyObject(),
						(List<String>) EasyMock.anyObject())).andReturn(true).anyTimes();

		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);
		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);
		PowerMock.replay(ReportSchedulerUtil.class);
		PowerMock.replay(custRepoGen);
		PowerMock.replay(CustomReportGenerator.class);
		PowerMock.replay(ReportGenerator.class);

		try
		{
			assertEquals(ReportSchedulerUtil.generateReport(repoAudit), false);
		}
		catch (NullPointerException e)
		{

		}

	}

	@Test
	public void testReplaceSpCharForFile()
	{
		String expected = "dummy_";
		String fileName = "dummy*";
		String replacement = "_";
		assertEquals(ReportSchedulerUtil.replaceSpCharForFile(fileName, replacement), expected);
	}

	@Test
	@PrepareForTest({CommonServiceLocator.class, SchedulerConfigurationPropertiesHandler.class})
	public void testGetFileDownloadLink() throws Exception
	{

		String appURL = "dummyURL";
		String ticketId = "12";
		String partURL = "/RedirectHome.do?pageOf=pageOfDownload&file=";
		String expectedLink = appURL.concat(partURL).concat(ticketId);

		CommonServiceLocator commonConfig = PowerMock.createMock(CommonServiceLocator.class);
		PowerMock.expectNew(CommonServiceLocator.class).andReturn(commonConfig).anyTimes();
		EasyMock.expect(commonConfig.getAppURL()).andReturn(appURL).anyTimes();

		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject())).andReturn(appURL)
				.anyTimes();

		PowerMock.replay(commonConfig);
		PowerMock.replay(CommonServiceLocator.class);
		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);

		assertEquals(ReportSchedulerUtil.getFileDownloadLink(ticketId), expectedLink);

	}

	@Test
	@PrepareForTest({ReportSchedulerUtil.class, ReportAuditDataBizLogic.class})
	public void testGetDownloadFilePath() throws Exception
	{
		String filePath = "dummyFilePath";

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				(repoAuditBiz.executeQuery((String) EasyMock.anyObject(),
						(List<ColumnValueBean>) EasyMock.anyObject())))
				.andReturn(Arrays.asList(filePath)).anyTimes();

		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		assertEquals(ReportSchedulerUtil.getDownloadFilePath("dummy"), filePath);
	}

	@Test
	@PrepareForTest({ReportAuditDataBizLogic.class, ReportSchedulerUtil.class})
	public void testValidateFile() throws Exception
	{
		String filePath = "dummyFilePath";
		boolean success = true;

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				(repoAuditBiz.executeQuery((String) EasyMock.anyObject(),
						(List<ColumnValueBean>) EasyMock.anyObject())))
				.andReturn(Arrays.asList(filePath)).anyTimes();

		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		try
		{
			ReportSchedulerUtil.validateFile(filePath, 12L);
		}
		catch (FileCleanedException e)
		{

		}

	}

	@Test
	@PrepareForTest({ReportAuditDataBizLogic.class, ReportSchedulerUtil.class})
	public void testValidateFileForNotAuthorized() throws Exception
	{
		String filePath = "dummyFilePath";
		boolean success = true;

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				(repoAuditBiz.executeQuery((String) EasyMock.anyObject(),
						(List<ColumnValueBean>) EasyMock.anyObject()))).andReturn(null).anyTimes();

		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		try
		{
			ReportSchedulerUtil.validateFile(filePath, 12L);
		}
		catch (NotAuthorizedToDownloadException e)
		{

		}

	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class})
	public void testGetDurationListJSON() throws Exception
	{
		JSONObject json = new JSONObject();
		json.append("mock", "mock");

		PowerMock.mockStatic(SchedulerDataUtility.class);
		EasyMock.expect(
				SchedulerDataUtility.createExtJsJson((HttpServletRequest) EasyMock.anyObject(),
						(List<NameValueBean>) EasyMock.anyObject())).andReturn(json).anyTimes();
		PowerMock.replay(SchedulerDataUtility.class);

		assertEquals(json.get("mock"),
				ReportSchedulerUtil.getDurationListJSON((HttpServletRequest) EasyMock.anyObject())
						.get("mock"));

	}

	@Test
	@PrepareForTest({CommonServiceLocator.class, SchedulerDataUtility.class, JDBCDAO.class,
			OracleDAOImpl.class})
	public void testIsSysReport() throws Exception
	{

		JDBCDAO dao = PowerMock.createMock(OracleDAOImpl.class);
		dao.openSession((SessionDataBean) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();
		dao.closeSession();
		PowerMock.expectLastCall().anyTimes();
		EasyMock.expect(
				dao.executeQuery((String) EasyMock.anyObject(),
						(List<ColumnValueBean>) EasyMock.anyObject()))
				.andReturn(Arrays.asList(Arrays.asList(""))).anyTimes();

		PowerMock.mockStatic(SchedulerDataUtility.class);
		EasyMock.expect(SchedulerDataUtility.getJDBCDAO()).andReturn(dao).anyTimes();

		PowerMock.replay(dao);
		PowerMock.replay(OracleDAOImpl.class);
		PowerMock.replay(SchedulerDataUtility.class);

		PowerMock.verify(dao);

		assertEquals(true, ReportSchedulerUtil.isSysReport(12L));

	}

	@Test
	@PrepareForTest({CommonServiceLocator.class, SchedulerDataUtility.class, JDBCDAO.class,
			OracleDAOImpl.class})
	public void testIsStudyBasedSchedule() throws DAOException
	{

		JDBCDAO dao = PowerMock.createMock(OracleDAOImpl.class);
		dao.openSession((SessionDataBean) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();
		dao.closeSession();
		PowerMock.expectLastCall().anyTimes();
		EasyMock.expect(
				dao.executeQuery((String) EasyMock.anyObject(),
						(List<ColumnValueBean>) EasyMock.anyObject()))
				.andReturn(Arrays.asList(Arrays.asList(""))).anyTimes();

		PowerMock.mockStatic(SchedulerDataUtility.class);
		EasyMock.expect(SchedulerDataUtility.getJDBCDAO()).andReturn(dao).anyTimes();

		PowerMock.replay(dao);
		PowerMock.replay(OracleDAOImpl.class);
		PowerMock.replay(SchedulerDataUtility.class);

		assertEquals(true, ReportSchedulerUtil.isStudyBasedSchedule(12L, 12L));
	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, SchedulerConfigurationPropertiesHandler.class})
	public void testGetEmail() throws Exception
	{
		String mockedRetriever = "edu.wustl.common.mockedObjectImplementations.MockedHostAppUserDataRetriever";

		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject()))
				.andReturn(mockedRetriever).anyTimes();

		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);

		assertEquals("mock12@mail.com", ReportSchedulerUtil.getEmail(12L));
	}

	@Test
	@PrepareForTest({CommonServiceLocator.class, SchedulerDataUtility.class, JDBCDAO.class,
			OracleDAOImpl.class, ResultSet.class, ReportAuditDataBizLogic.class,
			ReportBizLogic.class, ReportSchedulerUtil.class})
	public void testGenerateTicket() throws Exception
	{
		SessionDataBean sessionDataBean = new SessionDataBean();
		sessionDataBean.setUserId(12L);
		ReportForm repoForm = new ReportForm();
		repoForm.setReportName("dummyReportName");
		repoForm.setToDate("06-11-2012");
		repoForm.setFromDate("06-11-2012");

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

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		repoAuditBiz.insert((ReportAuditData) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();

		PowerMock.replay(dao);
		PowerMock.replay(OracleDAOImpl.class);
		PowerMock.replay(SchedulerDataUtility.class);
		PowerMock.replay(mockResultSet);
		PowerMock.replay(ResultSet.class);
		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);
		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		assertEquals(12L, ReportSchedulerUtil.generateTicket(repoForm, sessionDataBean).getCsId());

	}

	@Test
	public void testGetFileNameFromPath()
	{
		String fileName = "/tmp/demo.txt";
		assertEquals("demo.txt", ReportSchedulerUtil.getFileNameFromPath(fileName));
	}
}
