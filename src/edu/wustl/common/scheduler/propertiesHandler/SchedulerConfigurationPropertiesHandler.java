package edu.wustl.common.scheduler.propertiesHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import edu.wustl.common.scheduler.constants.SchedulerConstants;
import edu.wustl.common.scheduler.tag.SchedulerTag;


public class SchedulerConfigurationPropertiesHandler
{
	private static SchedulerConfigurationPropertiesHandler schedulerProperties;
	private static Properties globalProperties = new Properties();

	
	/**
	 * @throws IOException
	 */
	private  SchedulerConfigurationPropertiesHandler() throws IOException
	{
		InputStream inStream = SchedulerTag.class.getClassLoader().getResourceAsStream(
				SchedulerConstants.PROPERTIES_FILE_NAME);
		globalProperties.load(inStream);
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	public static synchronized SchedulerConfigurationPropertiesHandler getInstance() throws IOException
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
