
package edu.wustl.common.scheduler.scheduleProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.scheduler.bizLogic.ScheduleBizLogic;
import edu.wustl.common.scheduler.constants.SchedulerConstants;
import edu.wustl.common.scheduler.domain.ReportSchedule;
import edu.wustl.common.scheduler.domain.Schedule;
import edu.wustl.common.scheduler.exception.AlreadyScheduledException;
import edu.wustl.common.scheduler.exception.ScheduleHandlerNotFoundException;
import edu.wustl.common.scheduler.processorScheduler.Scheduler;
import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;


public class StartUpScheduleProcessor
{

	/**
	 * @throws IOException
	 * @throws BizLogicException
	 * @throws AlreadyScheduledException
	 * @throws ScheduleHandlerNotFoundException
	 */
	public void scheduleSavedSchedules() throws IOException, BizLogicException,
			AlreadyScheduledException, ScheduleHandlerNotFoundException
	{

		List<Schedule> scheduleList = new ArrayList<Schedule>();
		populateSchedules(scheduleList);
		scheduleSchedulesList(scheduleList);
		//Schedule Cleanup process to delete old files
		schedule(new FilesCleanupProcessor());
	}

	/**
	 * @param scheduleList
	 * @throws AlreadyScheduledException
	 * @throws ScheduleHandlerNotFoundException
	 * @throws IOException
	 */
	private void scheduleSchedulesList(List<Schedule> scheduleList)
			throws AlreadyScheduledException, ScheduleHandlerNotFoundException, IOException
	{
		if (!scheduleList.isEmpty())
		{
			for (Schedule schedule : scheduleList)
			{
				if (schedule instanceof ReportSchedule)
				{
					ReportScheduleProcessor rSchedProc = new ReportScheduleProcessor();
					rSchedProc.setScheduleObject(schedule);
					schedule(rSchedProc);
				}
			}
		}
	}

	/**
	 * @param processor
	 * @throws AlreadyScheduledException
	 * @throws ScheduleHandlerNotFoundException
	 * @throws IOException
	 */
	private void schedule(AbstractScheduleProcessor processor) throws AlreadyScheduledException,
			ScheduleHandlerNotFoundException, IOException
	{
		Scheduler scheduler = new Scheduler();
		scheduler.schedule(processor, true);
	}

	/**
	 * @param schedulesList
	 * @throws IOException
	 * @throws BizLogicException
	 */
	@SuppressWarnings("unchecked")
	private void populateSchedules(List<Schedule> schedulesList) throws IOException, BizLogicException
	{
		List<String> scheduleTypes = Arrays
				.asList(((String) SchedulerConfigurationPropertiesHandler.getInstance()
						.getProperty(SchedulerConstants.SCHEDULE_TYPES)).split(","));
		ScheduleBizLogic schedBizLogic = new ScheduleBizLogic();

		for (String scheduleName : scheduleTypes)
		{
			schedulesList.addAll(schedBizLogic.retrieve(scheduleName));
		}
	}
	
}
