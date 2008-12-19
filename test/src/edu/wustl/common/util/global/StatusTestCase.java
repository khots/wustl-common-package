package edu.wustl.common.util.global;

import edu.wustl.common.CommonBaseTestCase;


public class StatusTestCase extends CommonBaseTestCase
{
	public void testStatus()
	{
		assertEquals("Disabled", Status.ACTIVITY_STATUS_DISABLED.toString());
		assertEquals("activityStatus", Status.ACTIVITY_STATUS.toString());
	}
}
