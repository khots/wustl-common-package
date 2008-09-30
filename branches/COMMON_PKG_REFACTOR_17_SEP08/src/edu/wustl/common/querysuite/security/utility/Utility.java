/**
 * 
 */

package edu.wustl.common.querysuite.security.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.security.PrivilegeType;
import edu.wustl.common.querysuite.utils.QueryUtility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * This class will have utility methods required fot CSM integration with Query.
 * @author prafull_kadam
 *
 */
public class Utility
{

	/**
	 * TO get the PrivilegeType of an Entity.
	 * @param entity The reference to Entity.
	 * @return appropriate PrivilegeType of the given Entity.
	 */
	public static PrivilegeType getPrivilegeType(EntityInterface entity)
	{
		Collection<TaggedValueInterface> taggedValueCollection = entity.getTaggedValueCollection();
		for (TaggedValueInterface tag : taggedValueCollection)
		{
			if (Constants.PRIVILEGE_TAG_NAME.equals(tag.getKey()))
			{
				return PrivilegeType.getPrivilegeType(Integer.parseInt(tag.getValue()));
			}
		}
		return PrivilegeType.ClassLevel;
	}

	/**
	 * To check whether there is condition on identifier field or not.
	 * @param query the reference to the Query Object.
	 * @return true if there is any condition on the Identified attribute, else returns false.
	 */
	public static boolean isConditionOnIdentifiedField(IQuery query)
	{
		Map<IExpression, Collection<ICondition>> allSelectedConditions = QueryUtility
				.getAllSelectedConditions(query);
		Collection<Collection<ICondition>> values = allSelectedConditions.values();
		Boolean trueValue = Boolean.TRUE;
		for (Collection<ICondition> conditions : values)
		{
			for (ICondition condition : conditions)
			{
				Boolean isConditionOnIdentifiedAttribute = condition.getAttribute()
						.getIsIdentified();

				if (trueValue.equals(isConditionOnIdentifiedAttribute))
				{
					return true;
				}
			}
		}
		return false;
	}

	/* Added By Rukhsana
	 * Added list of objects on which read denied has to be checked while filtration of result for csm-query performance.
	 * A map that contains entity name as key and sql to get Main_Protocol_Object (Collection protocol, Clinical Study) Ids for that entity id as value for csm-query performance.
	 * Reading the above values from a properties file to make query module application independent
	 */
	public static void setReadDeniedAndEntitySqlMap()
	{
		List<String> queryReadDeniedObjectsList = new ArrayList<String>();
		Map<String, String> entityCSSqlMap = new HashMap<String, String>();
		String mainProtocolClassName = "";
		String validatorClassname = "";
		File file = new File(Variables.applicationHome + System.getProperty("file.separator")
				+ "WEB-INF" + System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator") + Constants.CSM_PROPERTY_FILE);
		if (file.exists())
		{
			Properties csmPropertyFile = new Properties();
			try
			{

				csmPropertyFile.load(new FileInputStream(file));
				mainProtocolClassName = csmPropertyFile.getProperty(Constants.MAIN_PROTOCOL_OBJECT);
				validatorClassname = csmPropertyFile.getProperty(Constants.VALIDATOR_CLASSNAME);
				String readdenied = csmPropertyFile.getProperty(Constants.READ_DENIED_OBJECTS);
				String[] readDeniedObjects = readdenied.split(",");
				for (int i = 0; i < readDeniedObjects.length; i++)
				{
					queryReadDeniedObjectsList.add(readDeniedObjects[i]);
					if (csmPropertyFile.getProperty(readDeniedObjects[i]) != null)
						entityCSSqlMap.put(readDeniedObjects[i], csmPropertyFile
								.getProperty(readDeniedObjects[i]));
				}
			}
			catch (FileNotFoundException e)
			{
				Logger.out.debug("csm.properties not found");
				e.printStackTrace();
			}
			catch (IOException e)
			{
				Logger.out.debug("Exception occured while reading csm.properties");
				e.printStackTrace();
			}
			Variables.mainProtocolObject = mainProtocolClassName;
			Variables.queryReadDeniedObjectList.addAll(queryReadDeniedObjectsList);
			Variables.entityCPSqlMap.putAll(entityCSSqlMap);
			Variables.validatorClassname = validatorClassname;
		}

	}
}
