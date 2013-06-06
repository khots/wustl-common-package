/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.propertiesHandler;

import java.util.HashMap;
import java.util.Map;


import edu.wustl.common.scheduler.util.SchedulerPropertiesFetcher;

public class SchedulerConfigurationPropertiesHandler
{

	private static SchedulerConfigurationPropertiesHandler schedulerProperties;
	private static Map<String, Object> globalProperties = new HashMap<String, Object>();

	/**
	 * @throws Exception
	 */
	private SchedulerConfigurationPropertiesHandler() throws Exception
	{
		SchedulerPropertiesFetcher propertiesFetcher = new SchedulerPropertiesFetcher();
				
		globalProperties.putAll(propertiesFetcher.getSchedulerPropertiesMap());
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static synchronized SchedulerConfigurationPropertiesHandler getInstance()
			throws Exception
	{
		if (schedulerProperties == null)
		{
			schedulerProperties = new SchedulerConfigurationPropertiesHandler();
		}
		return schedulerProperties;
	}

	/**
	 * @param key
	 * @return
	 */
	public synchronized Object getProperty(String key)
	{
		Object property = null;
		if (globalProperties.containsKey(key))
		{
			property = globalProperties.get(key);
		}
		return property;
	}

}
