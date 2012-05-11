
package edu.wustl.common.scheduler.processorScheduler;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.wustl.common.scheduler.bizLogic.ScheduleBizLogic;
import edu.wustl.common.scheduler.domain.Schedule;
import edu.wustl.common.scheduler.exception.AlreadyScheduledException;
import edu.wustl.common.scheduler.exception.ScheduleHandlerNotFoundException;
import edu.wustl.common.scheduler.factory.SchedulerFactory;
import edu.wustl.common.scheduler.scheduleProcessor.AbstractScheduleProcessor;


public class Scheduler
{

	/**
	 * @throws IOException 
	 * 
	 */
	public Scheduler() throws IOException
	{
	}

	/**
	 * @param scheduleId
	 * @return
	 * @throws IOException 
	 */
	public boolean isAlreadyScheduled(Long scheduleId) throws IOException
	{
		return SchedulerFactory.getInstance().getScheduleHandlersMap().containsKey(scheduleId);
	}

	/**
	 * @param scheduler
	 * @throws AlreadyScheduledException
	 * @throws ScheduleHandlerNotFoundException 
	 * @throws IOException 
	 */
	public void schedule(AbstractScheduleProcessor scheduleProcessor, boolean forceSchedule)
			throws AlreadyScheduledException, ScheduleHandlerNotFoundException, IOException
	{
		Long scheduleId = ((Schedule) scheduleProcessor.getScheduleObject()).getId();
		String scheduleName = ((Schedule) scheduleProcessor.getScheduleObject()).getName();
		if (forceSchedule)
		{
			if (isAlreadyScheduled(scheduleId))
			{
				unSchedule(scheduleId);
			}
			schedule(scheduleProcessor);
		}
		else
		{
			if (isAlreadyScheduled(scheduleId))
			{
				throw new AlreadyScheduledException("The scedule " + scheduleName
						+ " is already scheduled.");
			}
			else
			{
				schedule(scheduleProcessor);
			}
		}
	}

	/**
	 * @param scheduleProcessor
	 * @throws IOException 
	 */
	private void schedule(AbstractScheduleProcessor scheduleProcessor) throws IOException
	{
		String interval = ((Schedule) scheduleProcessor.getScheduleObject()).getInterval();
		Date startDate = ((Schedule) scheduleProcessor.getScheduleObject()).getStartDate();
		ScheduleBizLogic schedBizLogic = new ScheduleBizLogic();
		//getStartDelayFromStartTime
		ScheduledFuture schedHandler = SchedulerFactory
				.getInstance()
				.getSchedulerInstance()
				.scheduleAtFixedRate(scheduleProcessor,
						schedBizLogic.getStartDelayFromStartTime(startDate),
						schedBizLogic.getLiteralInterval(interval), TimeUnit.SECONDS);
		SchedulerFactory.getInstance().addHandler(
				((Schedule) scheduleProcessor.getScheduleObject()).getId(), schedHandler);

	}

	/**
	 * @param scheduleId
	 * @throws ScheduleHandlerNotFoundException 
	 * @throws IOException 
	 */
	public void unSchedule(Long scheduleId) throws ScheduleHandlerNotFoundException, IOException
	{
		if (isAlreadyScheduled(scheduleId))
		{
			SchedulerFactory.getInstance().removeHandler(scheduleId);
		}
		else
		{
			throw new ScheduleHandlerNotFoundException("Schedule with ID " + scheduleId
					+ " has not been scheduled.");
		}
	}

}
