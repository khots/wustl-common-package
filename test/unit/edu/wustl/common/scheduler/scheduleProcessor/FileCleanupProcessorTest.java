/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.scheduleProcessor;


import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import edu.wustl.common.scheduler.propertiesHandler.SchedulerConfigurationPropertiesHandler;
import edu.wustl.common.scheduler.scheduleProcessor.FilesCleanupProcessor;
import edu.wustl.common.scheduler.util.SchedulerDataUtility;
import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)

public class FileCleanupProcessorTest 
{

	@Test
	@PrepareForTest({FilesCleanupProcessor.class, SchedulerDataUtility.class,
			SchedulerConfigurationPropertiesHandler.class})
	public void testExecuteSchedule() throws Exception
	{
		SchedulerConfigurationPropertiesHandler config = PowerMock
				.createMock(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.expectNew(SchedulerConfigurationPropertiesHandler.class).andReturn(config)
				.anyTimes();
		EasyMock.expect(config.getProperty((String) EasyMock.anyObject())).andReturn("1").anyTimes();

		PowerMock.mockStatic(SchedulerDataUtility.class);
		SchedulerDataUtility.cleanOldFilesFromDir(1L);
		PowerMock.expectLastCall().anyTimes();

		PowerMock.replay(config);
		PowerMock.replay(SchedulerConfigurationPropertiesHandler.class);
		PowerMock.replay(SchedulerDataUtility.class);
		
		FilesCleanupProcessor filesCleanUpProc = new FilesCleanupProcessor();
		filesCleanUpProc.executeSchedule();

	}
	
	@Test
	public void testRedundant() throws Exception
	{
		FilesCleanupProcessor filesCleanUpProc = new FilesCleanupProcessor();
		assertEquals(false, filesCleanUpProc.mail());
		Whitebox.invokeMethod(filesCleanUpProc, "postScheduleExecution");
		Whitebox.invokeMethod(filesCleanUpProc, "generateTicket");
	}
}
