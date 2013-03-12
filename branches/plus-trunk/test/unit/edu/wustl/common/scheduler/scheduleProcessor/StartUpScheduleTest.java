
package edu.wustl.common.scheduler.scheduleProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import edu.wustl.common.report.reportBizLogic.ReportBizLogic;
import edu.wustl.common.scheduler.bizLogic.ScheduleBizLogic;
import edu.wustl.common.scheduler.domain.ReportSchedule;
import edu.wustl.common.scheduler.domain.Schedule;
import edu.wustl.common.scheduler.processorScheduler.Scheduler;
import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;
import edu.wustl.common.scheduler.scheduleProcessor.AbstractScheduleProcessor;
import edu.wustl.common.scheduler.scheduleProcessor.FilesCleanupProcessor;
import edu.wustl.common.scheduler.scheduleProcessor.ReportScheduleProcessor;
import edu.wustl.common.scheduler.scheduleProcessor.StartUpScheduleProcessor;

@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)

public class StartUpScheduleTest
{

	@Test
	@PrepareForTest({StartUpScheduleProcessor.class, ScheduleBizLogic.class,
			SchedulerConfigurationPropertiesHandler.class})
	public void testPopulateSchedules() throws Exception
	{

		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject())).andReturn("dummy")
				.anyTimes();

		List list = new ArrayList();
		list.add(new Schedule());

		ScheduleBizLogic schedBizBiz = PowerMock.createMock(ScheduleBizLogic.class);
		PowerMock.expectNew(ScheduleBizLogic.class).andReturn(schedBizBiz).anyTimes();
		EasyMock.expect(schedBizBiz.retrieve((String) EasyMock.anyObject())).andReturn(list)
				.anyTimes();

		PowerMock.replay(schedBizBiz);
		PowerMock.replay(ScheduleBizLogic.class);
		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);

		StartUpScheduleProcessor startUpProc = new StartUpScheduleProcessor();
		startUpProc.populateSchedules(new ArrayList<Schedule>());

	}

	@Test
	@PrepareForTest({StartUpScheduleProcessor.class, Scheduler.class})
	public void testSchedule() throws Exception
	{
		Scheduler scheduler = PowerMock.createMock(Scheduler.class);
		PowerMock.expectNew(Scheduler.class).andReturn(scheduler).anyTimes();
		scheduler.schedule((AbstractScheduleProcessor) EasyMock.anyObject(), EasyMock.anyBoolean());
		PowerMock.expectLastCall().anyTimes();

		PowerMock.replay(scheduler);
		PowerMock.replay(Scheduler.class);

		StartUpScheduleProcessor startUpProc = new StartUpScheduleProcessor();
		startUpProc.schedule(new FilesCleanupProcessor());

	}

	@Test
	@PrepareForTest({StartUpScheduleProcessor.class, Scheduler.class})
	public void testScheduleSchedulesList() throws Exception
	{
		StartUpScheduleProcessor startUpProc = PowerMock.createPartialMock(
				StartUpScheduleProcessor.class, "schedule");
		startUpProc.schedule((AbstractScheduleProcessor) EasyMock.anyObject());
		PowerMock.expectLastCall().anyTimes();

		PowerMock.replay(startUpProc);
		PowerMock.replay(StartUpScheduleProcessor.class);

		Schedule schedule = new ReportSchedule();

		startUpProc.scheduleSchedulesList(Arrays.asList(schedule));
	}

	@Test
	@PrepareForTest({StartUpScheduleProcessor.class})
	public void testScheduleSavedSchedules() throws Exception
	{
		PowerMock.suppress(PowerMock.method(StartUpScheduleProcessor.class, "populateSchedules",
				List.class));
		PowerMock.suppress(PowerMock.method(StartUpScheduleProcessor.class,
				"scheduleSchedulesList", List.class));
		PowerMock.suppress(PowerMock.method(StartUpScheduleProcessor.class, "schedule",
				AbstractScheduleProcessor.class));
		
		StartUpScheduleProcessor startUpProc = new StartUpScheduleProcessor();
		startUpProc.scheduleSavedSchedules();

	}
}
