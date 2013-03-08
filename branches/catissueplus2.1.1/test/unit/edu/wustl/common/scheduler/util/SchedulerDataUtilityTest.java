
package edu.wustl.common.scheduler.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.mockedObjectImplementations.MockedHttpServletRequest;
import edu.wustl.common.mockedObjectImplementations.MockedHttpSession;
import edu.wustl.common.report.reportBizLogic.ReportBizLogic;
import edu.wustl.common.scheduler.bizLogic.ReportAuditDataBizLogic;
import edu.wustl.common.scheduler.constants.SchedulerConstants;
import edu.wustl.common.scheduler.domain.BaseSchedule;
import edu.wustl.common.scheduler.domain.ReportAuditData;
import edu.wustl.common.scheduler.domain.ReportSchedule;
import edu.wustl.common.scheduler.domain.Schedule;
import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;
import edu.wustl.common.scheduler.util.SchedulerDataUtility;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.SendEmail;
import edu.wustl.dao.exception.DAOException;

@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)

public class SchedulerDataUtilityTest
{

	
	
	
	@Test
	public void testGetScheduleFromJsonMap() throws JSONException, SecurityException,
			IllegalArgumentException, BizLogicException, ParseException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException
	{
		JSONObject jsonDataMap = new JSONObject();
		jsonDataMap.putOpt(SchedulerConstants.SCHEDULE_TYPE,
				"edu.wustl.common.scheduler.domain.ReportSchedule");
		jsonDataMap.put(SchedulerConstants.SCHEDULE_ID, 12L);
		jsonDataMap.put(SchedulerConstants.SCHEDULE_NAME, "dummyName");
		jsonDataMap.put(SchedulerConstants.INTERVAL, "daily");
		jsonDataMap.put(SchedulerConstants.COMMENTS, "comments");
		jsonDataMap.put(SchedulerConstants.OWNER_ID, 12L);
		jsonDataMap.put(SchedulerConstants.RECIPIENT_LIST, "1,12,3");
		jsonDataMap.put(SchedulerConstants.RECIPIENT_LIST, "1,12,3");
		SimpleDateFormat formatter = new SimpleDateFormat(SchedulerConstants.DATE_FORMAT);
		jsonDataMap.put(SchedulerConstants.START_DATE, formatter.format(new Date()));
		jsonDataMap.put(SchedulerConstants.END_DATE, formatter.format(new Date()));
		jsonDataMap.put(SchedulerConstants.INCLUDE_ME, true);
		jsonDataMap.put("Duration", "daily");
		jsonDataMap.put("itemIdCollection", "1;2;3;4");
		
		System.out.println("-----------------------------------------Test");		

		assertEquals("dummyName", SchedulerDataUtility.getScheduleFromJsonMap(jsonDataMap, 12L)
				.getName());

	}

	@Test
	public void testGetQueryInClauseStringFromIdList()
	{
		assertEquals("(12,13)",
				SchedulerDataUtility.getQueryInClauseStringFromIdList(Arrays.asList(12L, 13L)));
	}

	@Test
	@PrepareForTest({ReportBizLogic.class, SchedulerDataUtility.class})
	public void testGetJSONFromScheduleList() throws Exception
	{
		Schedule reportSched = new ReportSchedule();
		reportSched.setActivityStatus("Active");
		((BaseSchedule) reportSched).setComments("comments");
		((ReportSchedule) reportSched).setDuration("daily");
		reportSched.setEndDate(new Date());
		reportSched.setId(12L);
		((BaseSchedule) reportSched).setIncludeMe(true);
		reportSched.setInterval("daily");
		reportSched.setName("dummy");
		((BaseSchedule) reportSched).setOwnerId(12L);
		((BaseSchedule) reportSched).setRecepientUserIdCollection(Arrays.asList(1L, 2L));
		((BaseSchedule) reportSched).setScheduleItemIdCollection(Arrays.asList(1L, 2L));
		reportSched.setStartDate(new Date());

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNameById((Long) EasyMock.anyObject()))
				.andReturn("mockReport").anyTimes();

		PowerMock.mockStaticPartial(SchedulerDataUtility.class, "getUserNamesList",
				"getRecipientNamesStringFromIds");
		EasyMock.expect(SchedulerDataUtility.getUserNamesList((Collection) EasyMock.anyObject()))
				.andReturn(Arrays.asList("dummy")).anyTimes();
		PowerMock.expectPrivate(
				SchedulerDataUtility.class,
				PowerMock.method(SchedulerDataUtility.class, "getRecipientNamesStringFromIds",
						Collection.class), (Collection) EasyMock.anyObject()).andReturn("dummy");

		PowerMock.replay(SchedulerDataUtility.class);
		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);

		assertEquals("dummy",
				SchedulerDataUtility.getJSONFromScheduleList(Arrays.asList(reportSched))
						.getJSONArray("rows").getJSONObject(0).getJSONArray("data").get(1));
	}

	@Test
	@PrepareForTest({ReportBizLogic.class, SchedulerDataUtility.class})
	public void testGetReportNameAndIdBeans() throws Exception
	{
		List objList = new ArrayList();
		objList.add("one");
		objList.add("two");
		List list = new ArrayList();
		list.add(objList);

		ReportBizLogic repoBiz = PowerMock.createMock(ReportBizLogic.class);
		PowerMock.expectNew(ReportBizLogic.class).andReturn(repoBiz).anyTimes();
		EasyMock.expect(repoBiz.getReportNames(EasyMock.anyLong())).andReturn(list).anyTimes();
		EasyMock.expect(repoBiz.getReportNamesByUserId(EasyMock.anyLong(), EasyMock.anyLong()))
				.andReturn(list).anyTimes();

		PowerMock.replay(repoBiz);
		PowerMock.replay(ReportBizLogic.class);

		assertEquals(((List) list.get(0)).get(0),
				SchedulerDataUtility.getReportNameAndIdBeans(12L, 12L, true).get(0).getName());
		assertEquals(((List) list.get(0)).get(0),
				SchedulerDataUtility.getReportNameAndIdBeans(12L, 12L, false).get(0).getName());

	}

	@Test
	@PrepareForTest({CommonServiceLocator.class, SchedulerDataUtility.class})
	public void testPopulateQuerySpecificNameValueBeansList() throws Exception
	{
		List<NameValueBean> nmList = new ArrayList<NameValueBean>();
		nmList.add(new NameValueBean("dummy", "dummy"));

		CommonServiceLocator commonConfig = PowerMock.createMock(CommonServiceLocator.class);
		PowerMock.expectNew(CommonServiceLocator.class).andReturn(commonConfig).anyTimes();
		EasyMock.expect(commonConfig.getDefaultLocale()).andReturn(Locale.US).anyTimes();

		PowerMock.replay(commonConfig);
		PowerMock.replay(CommonServiceLocator.class);

		Whitebox.invokeMethod(SchedulerDataUtility.class,
				"populateQuerySpecificNameValueBeansList", nmList, nmList, "Dummy query");
	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, SchedulerConfigurationPropertiesHandler.class})
	public void testGetUsersIdAndEmailAddressList() throws Exception
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

		assertEquals(12L, SchedulerDataUtility.getUsersIdAndEmailAddressList(Arrays.asList(12L))
				.get(0)[0]);
	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, SchedulerConfigurationPropertiesHandler.class})
	public void testGetUserNamesList() throws Exception
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

		assertEquals("name12", SchedulerDataUtility.getUserNamesList(Arrays.asList(12L)).get(0));
	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, SchedulerConfigurationPropertiesHandler.class})
	public void testGetRecipientNamesStringFromIds() throws Exception
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

		assertEquals("name12;name13",
				SchedulerDataUtility.getRecipientNamesStringFromIds(Arrays.asList(12L, 13L)));
	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, SchedulerConfigurationPropertiesHandler.class})
	public void testGetUserNameListJSON() throws Exception
	{
		String mockedRetriever = "edu.wustl.common.mockedObjectImplementations.MockedHostAppUserDataRetriever";

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(SchedulerConstants.SCHEDULE_TYPE, SchedulerConstants.REPORT_SCHEDULE);
		paramMap.put(SchedulerConstants.ID, "12");
		paramMap.put(SchedulerConstants.LIMIT, "10");
		paramMap.put(SchedulerConstants.START, "0");

		MockedHttpServletRequest mockRequest = new MockedHttpServletRequest(null, paramMap, null);
		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject()))
				.andReturn(mockedRetriever).anyTimes();

		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);

		assertEquals(2, SchedulerDataUtility.getUserNameListJSON(mockRequest).get("totalCount"));

	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class})
	public void testGetReportNameListJSON() throws DAOException, BizLogicException, IOException,
			JSONException
	{
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(SchedulerConstants.SCHEDULE_TYPE, SchedulerConstants.REPORT_SCHEDULE);
		paramMap.put(SchedulerConstants.ID, "12");
		paramMap.put(SchedulerConstants.LIMIT, "10");
		paramMap.put(SchedulerConstants.START, "0");

		SessionDataBean dataBean = new SessionDataBean();
		dataBean.setAdmin(true);
		dataBean.setUserId(12L);

		Map<String, Object> attribMap = new HashMap<String, Object>();
		attribMap.put(SchedulerConstants.SESSION_DATA, dataBean);

		MockedHttpSession mockSession = new MockedHttpSession(attribMap);

		MockedHttpServletRequest mockRequest = new MockedHttpServletRequest(null, paramMap,
				mockSession);

		PowerMock.mockStaticPartial(SchedulerDataUtility.class, "getReportNameAndIdBeans");
		EasyMock.expect(
				SchedulerDataUtility.getReportNameAndIdBeans(EasyMock.anyLong(),
						EasyMock.anyLong(), EasyMock.anyBoolean()))
				.andReturn(
						Arrays.asList(new NameValueBean(12L, "mockedName1"), new NameValueBean(13L,
								"mockedName2"))).anyTimes();

		PowerMock.replay(SchedulerDataUtility.class);

		assertEquals(2,
				SchedulerDataUtility.getReportNameListJSON(mockRequest, 12L).get("totalCount"));

	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, FileUtils.class, ReportAuditDataBizLogic.class,
			File.class})
	public void testCleanOldFilesFromDir() throws Exception
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
		repoAudit.setExecuteionEnd(new Timestamp(new Date().getTime() - (86400000 * 5)));

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createMock(ReportAuditDataBizLogic.class);
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		repoAuditBiz.update((ReportAuditData) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();
		EasyMock.expect(repoAuditBiz.getFileDetailsList()).andReturn(Arrays.asList(repoAudit))
				.anyTimes();

		PowerMock.suppress(PowerMock.method(FileUtils.class, "forceDelete", File.class));
		File file = PowerMock.createMock(File.class);
		EasyMock.expect(file.exists()).andReturn(true).anyTimes();

		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);
		PowerMock.replay(file);
		PowerMock.replay(File.class);

		SchedulerDataUtility.cleanOldFilesFromDir(1L);

	}

	@Test
	@PrepareForTest({SchedulerDataUtility.class, XMLPropertyHandler.class, SendEmail.class,
			SchedulerConfigurationPropertiesHandler.class})
	public void testSendScheduleMail() throws Exception
	{
		PowerMock.mockStatic(XMLPropertyHandler.class);
		EasyMock.expect(XMLPropertyHandler.getValue((String) EasyMock.anyObject()))
				.andReturn("testProperties").anyTimes();

		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject()))
				.andReturn("testProperties").anyTimes();
		

		PowerMock.replay(XMLPropertyHandler.class);
		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);

		SchedulerDataUtility.sendScheduleMail("emailAddress", "subject", "body");
	}
}
