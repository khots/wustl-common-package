/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.util;

import java.util.HashMap;
import java.util.Map;
import edu.wustl.common.scheduler.constants.SchedulerConstants;
import edu.wustl.common.scheduler.util.SchedulerPropertiesFetcher;
import edu.wustl.common.util.XMLPropertyHandler;

public class SchedulerPropertiesFetcher
{

	public Map<String, Object> getSchedulerPropertiesMap()
	{
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		for (String property : SchedulerConstants.SCHEDULER_PROPERTIES_LIST)
		{
			if (XMLPropertyHandler.getValue(property) != null
					&& !XMLPropertyHandler.getValue(property).equals(""))
			{
				propertiesMap.put(property, XMLPropertyHandler.getValue(property));
			}
		}
		for (String property : SchedulerConstants.DB_DETAILS_LIST)
		{
			if (XMLPropertyHandler.getValue(property) != null
					&& !XMLPropertyHandler.getValue(property).equals(""))
			{
				propertiesMap.put(property, XMLPropertyHandler.getValue(property));
			}
		}
		return propertiesMap;
	}

}

