/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.bizlogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import edu.wustl.common.hibernate.HibernateDatabaseOperations;
import edu.wustl.common.hibernate.HibernateUtil;
import edu.wustl.common.labelSQLApp.bizlogic.CommonBizlogic;
import edu.wustl.common.scheduler.bizLogic.ScheduleBizLogic;
import edu.wustl.common.scheduler.constants.SchedulerConstants;
import edu.wustl.common.scheduler.domain.BaseSchedule;
import edu.wustl.common.scheduler.domain.ReportSchedule;
import edu.wustl.common.scheduler.domain.Schedule;
import edu.wustl.common.scheduler.processorScheduler.Scheduler;
import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;
import edu.wustl.common.scheduler.scheduleProcessor.AbstractScheduleProcessor;
import edu.wustl.common.scheduler.scheduleProcessor.ReportScheduleProcessor;
import edu.wustl.common.scheduler.util.ReportSchedulerUtil;
import edu.wustl.dao.OracleDAOImpl;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)
public class ScheduleBizLogicTest
{

	@Mock
	public Scheduler scheduler;

	@Before
	public void setup()
	{
		scheduler = PowerMock.createMock(Scheduler.class);
		PowerMock.mockStatic(ReportSchedulerUtil.class);
	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class})
	public void testGetSchedulesByOwner() throws Exception
	{
		BaseSchedule base = new BaseSchedule();
		base.setOwnerId(12L);
		base.setName("dummySchedule");
		base.setActivityStatus("Active");

		BaseSchedule base1 = new BaseSchedule();
		base1.setRecepientUserIdCollection(Arrays.asList(12L));
		base1.setOwnerId(13L);
		base1.setName("dummySchedule");
		base1.setActivityStatus("Active");

		List<Schedule> list = new ArrayList<Schedule>();
		list.add(base);
		list.add(base1);

		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class, "retrieve");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();
		EasyMock.expect(schedBiz.retrieve((String) EasyMock.anyObject())).andReturn(list)
				.anyTimes();

		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);

		assertEquals("dummySchedule", schedBiz.getSchedulesByOwner("DummySchedule", 12L).get(0)
				.getName());

	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class})
	public void testGetLiteralInterval() throws Exception
	{
		/*PowerMock.suppress(PowerMock.constructor(ScheduleBizLogic.class));
		ScheduleBizLogic schedBiz = new ScheduleBizLogic();*/
		assertEquals((long) 24 * 60 * 60, ScheduleBizLogic.getLiteralInterval("daily"));
		assertEquals((long) 24 * 60 * 60 * 7, ScheduleBizLogic.getLiteralInterval("weekly"));
		assertEquals((long) 24 * 30 * 60 * 60, ScheduleBizLogic.getLiteralInterval("monthly"));
		assertEquals((long) 24 * 30 * 60 * 60 * 3, ScheduleBizLogic.getLiteralInterval("quarterly"));
		assertEquals((long) 24 * 60 * 60 * 365, ScheduleBizLogic.getLiteralInterval("yearly"));

	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class, SchedulerConfigurationPropertiesHandler.class})
	public void testGetStartDelayFromStartTime() throws Exception
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 15);
		Date date = new Date();
		Long delay = (long) 0;
		if (cal.getTimeInMillis() > date.getTime())
		{
			delay = (cal.getTimeInMillis() - date.getTime()) / 1000;
		}
		else
		{
			cal.setTimeInMillis(date.getTime() + 24 * 60 * 60 * 1000);
			delay = (cal.getTimeInMillis() - date.getTime()) / 1000;
		}

		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class, "retrieve");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();

		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty(SchedulerConstants.EXECUTION_TIME_HRS)).andReturn("12")
				.anyTimes();
		EasyMock.expect(config.getProperty(SchedulerConstants.EXECUTION_TIME_MIN)).andReturn("15")
				.anyTimes();

		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);
		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);

		assertEquals(delay, schedBiz.getStartDelayFromStartTime(null));
		assertEquals(delay, schedBiz.getStartDelayFromStartTime(new Date()));

	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class})
	public void testInsertAndSchedule() throws Exception
	{
		Schedule schedule = new ReportSchedule();

		PowerMock.suppress(PowerMock.method(ScheduleBizLogic.class, "insert", Schedule.class));
		PowerMock.suppress(PowerMock.method(ScheduleBizLogic.class, "update", Schedule.class));
		PowerMock.suppress(PowerMock.method(ScheduleBizLogic.class, "schedule",
				AbstractScheduleProcessor.class));
		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class, "retrieve");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();

		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);

		schedBiz.insertAndSchedule(schedule);
		schedule.setId(12L);
		schedBiz.insertAndSchedule(schedule);

	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class, Scheduler.class})
	public void testSchedule() throws Exception
	{
		PowerMock.expectNew(Scheduler.class).andReturn(scheduler).anyTimes();
		scheduler.schedule((AbstractScheduleProcessor) EasyMock.anyObject(), EasyMock.anyBoolean());
		PowerMock.expectLastCall().anyTimes();

		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class, "retrieve");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();

		PowerMock.replay(scheduler);
		PowerMock.replay(Scheduler.class);
		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);

		Whitebox.invokeMethod(schedBiz, "schedule", new ReportScheduleProcessor());

	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class, ReportSchedulerUtil.class})
	public void testFilterSchedules() throws Exception
	{
		Schedule repo = new ReportSchedule();
		repo.setName("mockReportSchedule1");
		((BaseSchedule) repo).setIncludeMe(true);
		((BaseSchedule) repo).setOwnerId(2L);
		((BaseSchedule) repo).setScheduleItemIdCollection(Arrays.asList(1L));

		Schedule repo1 = new ReportSchedule();
		repo1.setName("mockReportSchedule2");
		((BaseSchedule) repo1).setIncludeMe(true);
		((BaseSchedule) repo1).setOwnerId(2L);
		((BaseSchedule) repo1).setScheduleItemIdCollection(Arrays.asList(2L));

		List<Schedule> scheduleList = new ArrayList<Schedule>();
		scheduleList.add(repo);
		scheduleList.add(repo1);

		EasyMock.expect(ReportSchedulerUtil.isSysReport(1L)).andReturn(true);
		EasyMock.expect(ReportSchedulerUtil.isSysReport(2L)).andReturn(false);
		EasyMock.expect(ReportSchedulerUtil.isStudyBasedSchedule(12L, 2L)).andReturn(true);
		EasyMock.expect(ReportSchedulerUtil.isStudyBasedSchedule(12L, 1L)).andReturn(false);

		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class, "retrieve");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();

		PowerMock.replay(ReportSchedulerUtil.class);
		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);

		assertEquals("mockReportSchedule1", schedBiz.filterSchedules(scheduleList, true, 12L)
				.get(0).getName());

		assertEquals("mockReportSchedule2",
				schedBiz.filterSchedules(scheduleList, false, 12L).get(0).getName());

	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class})
	public void testIsAuthorized() throws Exception
	{
		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class, "retrieve");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();

		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);

		assertEquals(true, schedBiz.isAuthorized(null, null, null));
	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class})
	public void testGetSchedulesByTypeAndId() throws Exception
	{
		BaseSchedule base1 = new BaseSchedule();
		base1.setRecepientUserIdCollection(Arrays.asList(12L));
		base1.setOwnerId(13L);
		base1.setName("dummySchedule");
		base1.setActivityStatus("Active");

		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class, "retrieve");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();
		EasyMock.expect(schedBiz.retrieve((String) EasyMock.anyObject(), EasyMock.anyLong()))
				.andReturn(base1).anyTimes();

		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);

		assertEquals("dummySchedule",
				((BaseSchedule) schedBiz.getSchedulesByTypeAndId("dummy", 12L)).getName());

	}

	@Test
	@PrepareForTest({ScheduleBizLogic.class, SchedulerConfigurationPropertiesHandler.class})
	public void testScheduleOnStartUp() throws Exception
	{
		BaseSchedule base1 = new BaseSchedule();
		base1.setRecepientUserIdCollection(Arrays.asList(12L));
		base1.setOwnerId(13L);
		base1.setName("dummySchedule");
		base1.setActivityStatus("Active");

		List<Schedule> list = new ArrayList<Schedule>();
		list.add(base1);
		
		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject())).andReturn("test")
				.anyTimes();

		ScheduleBizLogic schedBiz = PowerMock.createPartialMock(ScheduleBizLogic.class,
				"executeQuery");
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBiz).anyTimes();
		EasyMock.expect(
				(schedBiz.executeQuery((String) EasyMock.anyObject(),
						(List<ColumnValueBean>) EasyMock.anyObject()))).andReturn(list).anyTimes();
		PowerMock.suppress(PowerMock.method(ScheduleBizLogic.class, "schedule",
				AbstractScheduleProcessor.class));

		PowerMock.replay(schedBiz);
		PowerMock.replay(ScheduleBizLogic.class);
		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);
		
		schedBiz.scheduleOnStartUp();
	}

}
