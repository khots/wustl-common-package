/**
 * 
 */

package edu.wustl.common.querysuite.security.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
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
//		if(entity.getName().equals("edu.wustl.catissuecore.domain.Participant"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.Specimen"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.SpecimenCollectionGroup"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.CollectionProtocolRegistration"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.SpecimenCollectionGroup.IdentifiedSurgicalPathologyReport"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.pathology.DeidentifiedSurgicalPathologyReport"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.StorageContainer"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.CollectionProtocol"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.Site"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.DistributionProtocol"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.User"))
//			return PrivilegeType.ObjectLevel;
//		else if(entity.getName().equals("edu.wustl.catissuecore.domain.Distribution"))
//			return PrivilegeType.ObjectLevel;
//		
//		else
//			return PrivilegeType.ClassLevel; 
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
		Map<IExpressionId, Collection<ICondition>> allSelectedConditions = QueryUtility
				.getAllSelectedConditions(query);
		Collection<Collection<ICondition>> values = allSelectedConditions.values();
		Boolean trueValue = new Boolean(true);
		for (Collection<ICondition> conditions : values)
		{
			for (ICondition condition : conditions)
			{
				Boolean isConditionOnIdentifiedAttribute = condition.getAttribute().getIsIdentified();

				if (trueValue.equals(isConditionOnIdentifiedAttribute))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isIdentified(AttributeInterface attribute)
	{  
		if(attribute.getEntity().getName().equals("edu.wustl.catissuecore.domain.Participant"))
		{
			if(attribute.getName().equals("firstName") || attribute.getName().equals("lastName") ||attribute.getName().equals("middleName")||attribute.getName().equals("birthDate")||attribute.getName().equals("socialSecurityNumber"))
			 return true;
		}
		else if(attribute.getEntity().getName().equals("edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier"))
		{
			if(attribute.getName().equals("medicalRecordNumber"))
				return true;
		}
		else if (attribute.getEntity().getName().equals("edu.wustl.catissuecore.domain.CollectionProtocolRegistration"))
		{
			if(attribute.getName().equals("registrationDate"))
				return true;
		}
		else if(attribute.getEntity().getName().equals("edu.wustl.catissuecore.domain.SpecimenCollectionGroup"))
		{ 
			if(attribute.getName().equals("surgicalPathologyNumber"))
				return true;
		}
			
		return false;
	}
	
	public static List<AssociationInterface> getContainmentAssociations(EntityInterface entity)
	{
		List<AssociationInterface> associationList = new ArrayList<AssociationInterface>();
		if(entity.getName().equals("edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier"))
		{
			for(AssociationInterface ass : entity.getAssociationCollection())
			{
				if(ass.getTargetEntity().getName().equals("edu.wustl.catissuecore.domain.Participant"))
				  associationList.add(ass);
			}
		}
		if(entity.getName().equals("edu.wustl.catissuecore.domain.pathology.TextContent"))
		{
			for(AssociationInterface ass : entity.getAssociationCollection())
			{
				if(ass.getTargetEntity().getName().equals("edu.wustl.catissuecore.domain.pathology.SurgicalPathologyReport"))
				  associationList.add(ass);
			}
		}
		return associationList;
	}
}
