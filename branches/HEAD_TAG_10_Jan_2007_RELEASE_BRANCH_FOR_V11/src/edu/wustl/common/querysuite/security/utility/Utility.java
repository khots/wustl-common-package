/**
 * 
 */

package edu.wustl.common.querysuite.security.utility;

import java.util.Collection;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.util.QueryUtility;
import edu.wustl.common.querysuite.security.PrivilegeType;
import edu.wustl.common.util.global.Constants;

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
		return PrivilegeType.InsecureLevel;
	}

	/**
	 * To check whether there is condition on identifier field or not.
	 * @param query the reference to the Query Object.
	 * @return true if there is any condition on the Identified attribute, else returns false.
	 */
	public static boolean isConditionOnIdentifiedField(IQuery query)
	{
		Map<IExpressionId, Collection<ICondition>> allSelectedConditions = QueryUtility
				.getAllSelectedConditions(query);
		Collection<Collection<ICondition>> values = allSelectedConditions.values();
		Boolean trueValue = new Boolean(true);
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
}
