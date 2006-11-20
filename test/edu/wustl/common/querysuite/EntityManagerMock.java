
package edu.wustl.common.querysuite;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 17-Oct-2006 16:32:04 PM
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.IntegerAttributeTypeInformation;
import edu.common.dynamicextensions.domain.LongAttributeTypeInformation;
import edu.common.dynamicextensions.domain.Role;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties;
import edu.common.dynamicextensions.domain.databaseproperties.ConstraintProperties;
import edu.common.dynamicextensions.domain.databaseproperties.TableProperties;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.Constants;

public class EntityManagerMock extends EntityManager
{

	public List<Entity> entityList = new ArrayList<Entity>();
	public static String PARTICIPANT_NAME = "edu.wustl.catissuecore.domain.Participant";
	public static String PARTICIPANT_MEDICAL_ID_NAME = "edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier";
	public static String COLLECTION_PROTOCOL_NAME = "edu.wustl.catissuecore.domain.CollectionProtocol";
	public static String COLLECTION_PROTOCOL_REGISTRATION_NAME = "edu.wustl.catissuecore.domain.CollectionProtocolRegistration";
	public static String SPECIMEN_PROTOCOL_NAME = "edu.wustl.catissuecore.domain.SpecimenProtocol";
	public static String SPECIMEN_COLLECTION_GROUP_NAME = "edu.wustl.catissuecore.domain.SpecimenCollectionGroup";
	public static String COLLECTION_PROTOCOL_EVT_NAME = "edu.wustl.catissuecore.domain.CollectionProtocolEvent";
	public static String SPECIMEN_NAME = "edu.wustl.catissuecore.domain.Specimen";
	public static String SPECIMEN_EVT_NAME = "edu.wustl.catissuecore.domain.SpecimenEventParameters";
	public static String CHKIN_CHKOUT_EVT_NAME = "edu.wustl.catissuecore.domain.CheckInCheckOutEventParameter";
	public static String FROZEN_EVT_NAME = "edu.wustl.catissuecore.domain.FrozenEventParameters";
	public static String PROCEDURE_EVT_NAME = "edu.wustl.catissuecore.domain.ProcedureEventParameters";
	public static String RECEIVED_EVT_NAME = "edu.wustl.catissuecore.domain.ReceivedEventParameters";

	public static Long PARTICIPANT_ID = new Long(1);
	public static Long PARTICIPANT_MEDICAL_ID = new Long(2);
	public static Long COLLECTION_PROTOCOL_ID = new Long(4);
	public static Long COLLECTION_PROTOCOL_REGISTRATION_ID = new Long(3);
	public static Long SPECIMEN_PROTOCOL_ID = new Long(5);
	public static Long SPECIMEN_COLLECTION_GROUP_ID = new Long(7);
	public static Long COLLECTION_PROTOCOL_EVT_ID = new Long(6);
	public static Long SPECIMEN_ID = new Long(8);
	public static Long SPECIMEN_EVT_ID = new Long(9);
	public static Long CHKIN_CHKOUT_EVT_ID = new Long(10);
	public static Long FROZEN_EVT_ID = new Long(11);
	public static Long PROCEDURE_EVT_ID = new Long(12);
	public static Long RECEIVED_EVT_ID = new Long(13);

	
	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#findEntity(edu.common.dynamicextensions.domaininterface.EntityInterface)
	 */
	@Override
	public Collection findEntity(EntityInterface arg0)
	{
		// TODO Auto-generated method stub
		return super.findEntity(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getAllEntities()
	 */
	@Override
	public Collection getAllEntities() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		entityList.add((Entity)getEntityByName(PARTICIPANT_NAME));
		entityList.add((Entity)getEntityByName(PARTICIPANT_MEDICAL_ID_NAME));
		entityList.add((Entity)getEntityByName(COLLECTION_PROTOCOL_REGISTRATION_NAME));
		entityList.add((Entity)getEntityByName(COLLECTION_PROTOCOL_NAME));
		entityList.add((Entity)getEntityByName(COLLECTION_PROTOCOL_EVT_NAME));
		entityList.add((Entity)getEntityByName(CHKIN_CHKOUT_EVT_NAME));
		return entityList;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getAssociation(java.lang.String, java.lang.String)
	 */
	@Override
	public AssociationInterface getAssociation(String sourceEntityName, String sourceRoleName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Association association = null;
		if (sourceEntityName.equals(PARTICIPANT_NAME) && sourceRoleName.equals("participant"))
		{
			association = new Association();
	
			EntityInterface sourceEntity = getEntityByName(PARTICIPANT_NAME);
			EntityInterface targetEntity = getEntityByName(PARTICIPANT_MEDICAL_ID_NAME);
	
			association.setEntity(sourceEntity);
			association.setTargetEntity(targetEntity);
			association.setAssociationDirection(Constants.AssociationDirection.BI_DIRECTIONAL);
	
			Role sourceRole = new Role();
			sourceRole.setName("participant");
			sourceRole.setId(1L);
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			association.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("participantMedicalIdentifierCollection");
			targetRole.setId(2L);
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			association.setTargetRole(targetRole);
	
			
			ConstraintProperties constraintProperties = new ConstraintProperties();
			constraintProperties.setSourceEntityKey("IDENTIFIER");
			constraintProperties.setTargetEntityKey("PARTICIPANT_ID");
			association.setConstraintProperties(constraintProperties);
			
		}
	
		return association;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getAssociations(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Collection<AssociationInterface> getAssociations(Long sourceEntityId, Long targetEntityId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Collection<AssociationInterface> associationsCollection = new ArrayList<AssociationInterface>();

		if (sourceEntityId.equals(PARTICIPANT_NAME) && targetEntityId.equals(PARTICIPANT_MEDICAL_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(PARTICIPANT_NAME);
			EntityInterface targetEntity = getEntityByName(PARTICIPANT_MEDICAL_ID_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.BI_DIRECTIONAL);
	
			Role sourceRole = new Role();
			sourceRole.setName("participant");
			sourceRole.setId(1L);
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("participantMedicalIdentifierCollection");
			targetRole.setId(2L);
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);
	
			
			ConstraintProperties constraintProperties = new ConstraintProperties();
			constraintProperties.setSourceEntityKey("IDENTIFIER");
			constraintProperties.setTargetEntityKey("PARTICIPANT_ID");
			currentAssociation.setConstraintProperties(constraintProperties);
			
		}
		else if (sourceEntityId.equals(PARTICIPANT_ID) && targetEntityId.equals(COLLECTION_PROTOCOL_REGISTRATION_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(PARTICIPANT_NAME);
			EntityInterface targetEntity = getEntityByName(COLLECTION_PROTOCOL_REGISTRATION_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.BI_DIRECTIONAL);
	
			Role sourceRole = new Role();
			sourceRole.setName("participant");
			sourceRole.setId(1L);
			//TODO check association Type for linking: sourceRole.setAssociationType("linking");
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("collectionProtocolRegistrationCollection");
			targetRole.setId(2L);
			//TODO check association Type for linking: targetRole.setAssociationsType("linking");
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);
	
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (sourceEntityId.equals(COLLECTION_PROTOCOL_REGISTRATION_ID) && targetEntityId.equals(COLLECTION_PROTOCOL_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface targetEntity = getEntityByName(COLLECTION_PROTOCOL_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			// TODO check Direction for: currentAssociation.setDirection("Destination -> Source");
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.SRC_DESTINATION);
	
			Role sourceRole = new Role();
			sourceRole.setName("collectionProtocol");
			sourceRole.setId(1L);
			//TODO check association Type for linking: sourceRole.setAssociationType("linking");
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("collectionProtocolRegistrationCollection");
			targetRole.setId(2L);
			//TODO check association Type for linking: targetRole.setAssociationType("linking");
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);
	
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (sourceEntityId.equals(COLLECTION_PROTOCOL_REGISTRATION_ID) && targetEntityId.equals(SPECIMEN_COLLECTION_GROUP_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface targetEntity = getEntityByName(SPECIMEN_COLLECTION_GROUP_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.BI_DIRECTIONAL);
	
			Role sourceRole = new Role();
			sourceRole.setName("collectionProtocolRegistration");
			sourceRole.setId(1L);
			//TODO check association Type for linking: sourceRole.setAssociationType("linking");
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("SpecimenCollectionGroupCollection");
			targetRole.setId(2L);
			//TODO check association Type for linking: targetRole.setAssociationType("linking");
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
	
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);
	
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (sourceEntityId.equals(SPECIMEN_COLLECTION_GROUP_ID) && targetEntityId.equals(COLLECTION_PROTOCOL_EVT_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(SPECIMEN_COLLECTION_GROUP_NAME);
			EntityInterface targetEntity = getEntityByName(COLLECTION_PROTOCOL_EVT_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.SRC_DESTINATION);
	//		currentAssociation.setDirection("Source -> Destination");
	
			Role sourceRole = new Role();
			sourceRole.setName("specimenCollectionGroup");
			sourceRole.setId(1L);
			//TODO check association Type for linking: sourceRole.setAssociationType("linking");
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(10);
			sourceRole.setMinCardinality(0);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("collectionProtocolEvent");
			targetRole.setId(2L);
			//TODO check association Type for linking: targetRole.setAssociationType("linking");
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
	
			targetRole.setMaxCardinality(1);
			targetRole.setMinCardinality(1);
			currentAssociation.setTargetRole(targetRole);
	
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (sourceEntityId.equals(COLLECTION_PROTOCOL_ID) && targetEntityId.equals(COLLECTION_PROTOCOL_EVT_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(COLLECTION_PROTOCOL_NAME);
			EntityInterface targetEntity = getEntityByName(COLLECTION_PROTOCOL_EVT_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.BI_DIRECTIONAL);
	
			Role sourceRole = new Role();
			sourceRole.setName("collectionProtocol");
			sourceRole.setId(1L);
			//TODO check association Type for linking: sourceRole.setAssociationType("linking");
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("collectionProtocolEventCollection");
			targetRole.setId(2L);
			//TODO check association Type for linking: targetRole.setAssociationType("linking");
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
	
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(1);
			currentAssociation.setTargetRole(targetRole);
	
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (sourceEntityId.equals(SPECIMEN_COLLECTION_GROUP_ID) && targetEntityId.equals(SPECIMEN_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(SPECIMEN_COLLECTION_GROUP_NAME);
			EntityInterface targetEntity = getEntityByName(SPECIMEN_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.BI_DIRECTIONAL);
	
			Role sourceRole = new Role();
			sourceRole.setName("specimenCollectionGroup");
			sourceRole.setId(1L);
			//TODO check association Type for linking: sourceRole.setAssociationType("linking");
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("specimenCollection");
			targetRole.setId(2L);
			//TODO check association Type for linking: targetRole.setAssociationType("linking");
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
	
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(1);
			currentAssociation.setTargetRole(targetRole);
	
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (sourceEntityId.equals(SPECIMEN_ID) && targetEntityId.equals(SPECIMEN_ID))
		{
			Association currentAssociation = new Association();
	
			EntityInterface sourceEntity = getEntityByName(SPECIMEN_NAME);
			EntityInterface targetEntity = getEntityByName(SPECIMEN_NAME);
	
			currentAssociation.setEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setAssociationDirection(Constants.AssociationDirection.BI_DIRECTIONAL);
	
			Role sourceRole = new Role();
			sourceRole.setName("childrenSpecimen");
			sourceRole.setId(1L);
			//TODO check association Type for linking: sourceRole.setAssociationType("linking");
			sourceRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
			sourceRole.setMaxCardinality(10);
			sourceRole.setMinCardinality(0);
			currentAssociation.setSourceRole(sourceRole);
	
			Role targetRole = new Role();
			targetRole.setName("parentSpecimen");
			targetRole.setId(2L);
			//TODO check association Type for linking: targetRole.setAssociationType("linking");
			targetRole.setAssociationsType(Constants.AssociationType.CONTAINTMENT);
	
			targetRole.setMaxCardinality(1);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);
	
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else
		{
			System.out.println("There is no association between these two entities");
		}
		
		return associationsCollection;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getAttribute(java.lang.String, java.lang.String)
	 */
	@Override
	public AttributeInterface getAttribute(String entityName, String attributeName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		if (entityName.equalsIgnoreCase(PARTICIPANT_NAME))
		{
			ArrayList list = getParticipantAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase(PARTICIPANT_MEDICAL_ID_NAME))
		{
			ArrayList list = getParticipantMedicalIdentifierAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase(COLLECTION_PROTOCOL_REGISTRATION_NAME))
		{
			ArrayList list = getCollectionProtocolRegistrationAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase(COLLECTION_PROTOCOL_NAME))
		{
			ArrayList list = getCollectionProtocolAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase(SPECIMEN_PROTOCOL_NAME))
		{
			ArrayList list = getSpecimenProtocolAttributes();
			//System.out.println(list.get(3).getClass().getName());
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase(COLLECTION_PROTOCOL_EVT_NAME))
		{
			ArrayList list = getCollectionProtocolEventAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase(SPECIMEN_COLLECTION_GROUP_NAME))
		{
			ArrayList list = getSpecimenCollectionGroupAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase(SPECIMEN_NAME))
		{
			ArrayList list = getSpecimenAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		return null;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntitiesByAttributeConceptCode(java.lang.String)
	 */
	@Override
	public Collection getEntitiesByAttributeConceptCode(String arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getEntitiesByAttributeConceptCode(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntitiesByAttributeConceptName(java.lang.String)
	 */
	@Override
	public Collection getEntitiesByAttributeConceptName(String arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getEntitiesByAttributeConceptName(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntitiesByAttributeDescription(java.lang.String)
	 */
	@Override
	public Collection getEntitiesByAttributeDescription(String arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getEntitiesByAttributeDescription(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntitiesByAttributeName(java.lang.String)
	 */
	@Override
	public Collection getEntitiesByAttributeName(String arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getEntitiesByAttributeName(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntitiesByConceptCode(java.lang.String)
	 */
	@Override
	public Collection getEntitiesByConceptCode(String arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getEntitiesByConceptCode(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntitiesByConceptName(java.lang.String)
	 */
	@Override
	public Collection getEntitiesByConceptName(String arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getEntitiesByConceptName(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntityByDescription(java.lang.String)
	 */
	@Override
	public Collection getEntityByDescription(String arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getEntityByDescription(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getEntityByName(java.lang.String)
	 */
	@Override
	public EntityInterface getEntityByName(String name) throws DynamicExtensionsSystemException
	{
		if (name.equalsIgnoreCase(PARTICIPANT_NAME))
		{
			return createParticipantEntity(name);
		}
		else if (name.equalsIgnoreCase(PARTICIPANT_MEDICAL_ID_NAME))
		{
			return createParticipantMedicalIdentifierEntity(name);
		}
		else if (name.equalsIgnoreCase(COLLECTION_PROTOCOL_REGISTRATION_NAME))
		{
			return createCollectionProtocolRegistrationEntity(name);
		}
		else if (name.equalsIgnoreCase(COLLECTION_PROTOCOL_NAME))
		{
			return createCollectionProtocolEntity(name);
		}
		else if (name.equalsIgnoreCase(SPECIMEN_PROTOCOL_NAME))
		{
			return createSpecimenProtocolEntity(name);
		}
		else if (name.equalsIgnoreCase(COLLECTION_PROTOCOL_EVT_NAME))
		{
			return createCollectionProtocolEventEntity(name);
		}
		else if (name.equalsIgnoreCase(SPECIMEN_COLLECTION_GROUP_NAME))
		{
			return createSpecimenCollectionGroupEntity(name);
		}
		else if (name.equalsIgnoreCase(SPECIMEN_NAME))
		{
			return createSpecimenEntity(name);
		}
		else if (name.equalsIgnoreCase(SPECIMEN_EVT_NAME))
		{
			return createSpecimenEventParametersEntity(name);
		}
		else if (name.equalsIgnoreCase(CHKIN_CHKOUT_EVT_NAME))
		{
			return createCheckInCheckOutEventParameterEntity(name);
		}
		else if (name.equalsIgnoreCase(FROZEN_EVT_NAME))
		{
			return createFrozenEventParametersEntity(name);
		}
		else if (name.equalsIgnoreCase(PROCEDURE_EVT_NAME))
		{
			return createProcedureEventParametersEntity(name);
		}
		else if (name.equalsIgnoreCase(RECEIVED_EVT_NAME))
		{
			return createReceivedEventParametersEntity(name);
		}
		return null;
	}


	/*
	 * @param name
	 * Creates a participant entity, sets the attributes collection and
	 * table properties for the entity.
	 */
	private Entity createParticipantEntity(String name)
	{
		Entity e = new Entity();
		e.setName(PARTICIPANT_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a participant entity");
		e.setId(PARTICIPANT_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getParticipantAttributes());

		TableProperties participantTableProperties = new TableProperties();
		participantTableProperties.setName("catissue_participant");
		participantTableProperties.setId(PARTICIPANT_ID);
		e.setTableProperties(participantTableProperties);
		return e;
	}

	/*
	 * @param name
	 * Creates a participant medical identifier entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createParticipantMedicalIdentifierEntity(String name)
	{
		Entity e = new Entity();
		e.setName(PARTICIPANT_MEDICAL_ID_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a participant medical identifier entity");
		e.setId(PARTICIPANT_MEDICAL_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getParticipantMedicalIdentifierAttributes());

		TableProperties participantMedicalIdentifierTableProperties = new TableProperties();
		participantMedicalIdentifierTableProperties.setName("catissue_part_medical_id");
		participantMedicalIdentifierTableProperties.setId(PARTICIPANT_MEDICAL_ID);
		e.setTableProperties(participantMedicalIdentifierTableProperties);
		return e;
	}

	/*
	 * @param name
	 * Creates a collection protocol registration entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createCollectionProtocolRegistrationEntity(String name)
	{
		Entity e = new Entity();
		e.setName(COLLECTION_PROTOCOL_REGISTRATION_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a collection protocol registration entity");
		e.setId(COLLECTION_PROTOCOL_REGISTRATION_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getCollectionProtocolRegistrationAttributes());

		TableProperties collectionProtocolRegistrationTableProperties = new TableProperties();
		collectionProtocolRegistrationTableProperties.setName("catissue_coll_prot_reg");
		collectionProtocolRegistrationTableProperties.setId(COLLECTION_PROTOCOL_REGISTRATION_ID);
		e.setTableProperties(collectionProtocolRegistrationTableProperties);
		return e;
	}

	/*
	 * @param name
	 * Creates a collection protocol entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createCollectionProtocolEntity(String name)
	{
		Entity e = new Entity();
		e.setName(COLLECTION_PROTOCOL_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a collection protocol entity");
		e.setId(COLLECTION_PROTOCOL_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getCollectionProtocolAttributes());

		TableProperties collectionProtocolTableProperties = new TableProperties();
		collectionProtocolTableProperties.setName("catissue_collection_protocol");
		collectionProtocolTableProperties.setId(COLLECTION_PROTOCOL_ID);
		e.setTableProperties(collectionProtocolTableProperties);
		return e;
	}

	/*
	 * @param name
	 * Creates a specimen protocol entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createSpecimenProtocolEntity(String name)
	{
		Entity e = new Entity();
		e.setName(SPECIMEN_PROTOCOL_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a specimen protocol entity");
		e.setId(SPECIMEN_PROTOCOL_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getSpecimenProtocolAttributes());

		TableProperties specimenProtocolTableProperties = new TableProperties();
		specimenProtocolTableProperties.setName("catissue_specimen_protocol");
		specimenProtocolTableProperties.setId(SPECIMEN_PROTOCOL_ID);
		e.setTableProperties(specimenProtocolTableProperties);
		return e;
	}

	/*
	 * @param name
	 * Creates a collection protocol event entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createCollectionProtocolEventEntity(String name)
	{
		Entity e = new Entity();
		e.setName(COLLECTION_PROTOCOL_EVT_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a collection protocol event entity");
		e.setId(COLLECTION_PROTOCOL_EVT_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getCollectionProtocolEventAttributes());

		TableProperties collectionProtocolEventTableProperties = new TableProperties();
		collectionProtocolEventTableProperties.setName("catissue_coll_prot_event");
		collectionProtocolEventTableProperties.setId(COLLECTION_PROTOCOL_EVT_ID);
		e.setTableProperties(collectionProtocolEventTableProperties);
		return e;
	}

	/*
	 * @param name
	 * Creates a specimen collection group entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createSpecimenCollectionGroupEntity(String name)
	{
		Entity e = new Entity();
		e.setName(SPECIMEN_COLLECTION_GROUP_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a specimen collection group entity");
		e.setId(SPECIMEN_COLLECTION_GROUP_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getSpecimenCollectionGroupAttributes());

		TableProperties specimenCollectionGroupTableProperties = new TableProperties();
		specimenCollectionGroupTableProperties.setName("catissue_coll_prot_event");
		specimenCollectionGroupTableProperties.setId(SPECIMEN_COLLECTION_GROUP_ID);
		e.setTableProperties(specimenCollectionGroupTableProperties);
		return e;
	}

	/*
	 * @param name
	 * Creates a specimen entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createSpecimenEntity(String name)
	{
		Entity e = new Entity();
		e.setName(SPECIMEN_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a specimen entity");
		e.setId(SPECIMEN_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getSpecimenAttributes());

		TableProperties specimenTableProperties = new TableProperties();
		specimenTableProperties.setName("catissue_specimen");
		specimenTableProperties.setId(SPECIMEN_ID);
		e.setTableProperties(specimenTableProperties);
		return e;
	}
	
	/*
	 * @param name
	 * Creates a specimen event parameters entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createSpecimenEventParametersEntity(String name)
	{
		Entity e = new Entity();
		e.setName(SPECIMEN_EVT_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a specimen event parameters entity");
		e.setId(SPECIMEN_EVT_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getSpecimenEventParametersAttributes());

		TableProperties specimenEventParametersTableProperties = new TableProperties();
		specimenEventParametersTableProperties.setName("catissue_specimen_event_param");
		specimenEventParametersTableProperties.setId(SPECIMEN_EVT_ID);
		e.setTableProperties(specimenEventParametersTableProperties);
		return e;
	}
	
	/*
	 * @param name
	 * Creates a check in check out event parameters entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createCheckInCheckOutEventParameterEntity(String name)
	{
		Entity e = new Entity();
		e.setName(CHKIN_CHKOUT_EVT_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a check in check out event parameters entity");
		e.setId(CHKIN_CHKOUT_EVT_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getCheckInCheckOutEventParameterAttributes());

		TableProperties checkInCheckOutEventParameterTableProperties = new TableProperties();
		checkInCheckOutEventParameterTableProperties.setName("catissue_in_out_event_param");
		checkInCheckOutEventParameterTableProperties.setId(CHKIN_CHKOUT_EVT_ID);
		e.setTableProperties(checkInCheckOutEventParameterTableProperties);
		return e;
	}
	
	/*
	 * @param name
	 * Creates a frozen event parameters entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createFrozenEventParametersEntity(String name)
	{
		Entity e = new Entity();
		e.setName(FROZEN_EVT_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a frozen event parameters entity");
		e.setId(FROZEN_EVT_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getFrozenEventParameterAttributes());

		TableProperties frozenEventParameterTableProperties = new TableProperties();
		frozenEventParameterTableProperties.setName("catissue_frozen_event_param");
		frozenEventParameterTableProperties.setId(FROZEN_EVT_ID);
		e.setTableProperties(frozenEventParameterTableProperties);
		return e;
	}	
	
	/*
	 * @param name
	 * Creates a procedure event parameters entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createProcedureEventParametersEntity(String name)
	{
		Entity e = new Entity();
		e.setName(PROCEDURE_EVT_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a procedure event parameters entity");
		e.setId(PROCEDURE_EVT_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getProcedureEventParametersAttributes());

		TableProperties procedureEventParametersTableProperties = new TableProperties();
		procedureEventParametersTableProperties.setName("catissue_procedure_event_param");
		procedureEventParametersTableProperties.setId(PROCEDURE_EVT_ID);
		e.setTableProperties(procedureEventParametersTableProperties);
		return e;
	}	
	
	/*
	 * @param name
	 * Creates a received event parameters entity, sets attributes collection 
	 * and table properties for the entity.
	 */
	private Entity createReceivedEventParametersEntity(String name)
	{
		Entity e = new Entity();
		e.setName(RECEIVED_EVT_NAME);
		e.setCreatedDate(new Date());
		e.setDescription("This is a received event parameters entity");
		e.setId(RECEIVED_EVT_ID);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getReceivedEventParametersAttributes());

		TableProperties receivedEventParametersTableProperties = new TableProperties();
		receivedEventParametersTableProperties.setName("catissue_received_event_param");
		receivedEventParametersTableProperties.setId(RECEIVED_EVT_ID);
		e.setTableProperties(receivedEventParametersTableProperties);
		return e;
	}	

	/*
	 * Creates attributes for participant entity, creates and sets a 
	 * column property for each attribute and adds all the attributes to
	 * a collection.
	 */
	private ArrayList getParticipantAttributes()
	{
		ArrayList<Attribute> participantAttributes = new ArrayList<Attribute>();
		
		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		DateAttributeTypeInformation date = new DateAttributeTypeInformation();
		date.setFormat("DD-MM-YYYY");
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");
		
		Attribute att1 = new Attribute();
		//att1.setDefaultValue("activityStatus");
		att1.setName("activityStatus");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		Attribute att2 = new Attribute();
		//att2.setDefaultValue(new Date(12 - 03 - 1995));
		att2.setName("birthDate");
		att2.setAttributeTypeInformation(date);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("BIRTH_DATE");
		att2.setColumnProperties(c2);

		Attribute att3 = new Attribute();
		//att3.setDefaultValue(new Date(12 - 03 - 2005));
		att3.setName("deathDate");
		att3.setAttributeTypeInformation(date);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("DEATH_DATE");
		att3.setColumnProperties(c3);

		Attribute att4 = new Attribute();
		//att4.setDefaultValue("ethnicity");
		att4.setName("ethnicity");
		att4.setAttributeTypeInformation(string);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("ETHNICITY");
		att4.setColumnProperties(c4);

		Attribute att5 = new Attribute();
		//att5.setDefaultValue("firstName");
		att5.setName("firstName");
		att5.setAttributeTypeInformation(string);
		ColumnProperties c5 = new ColumnProperties();
		c5.setName("FIRST_NAME");
		att5.setColumnProperties(c5);

		Attribute att6 = new Attribute();
		//att6.setDefaultValue("gender");
		att6.setName("gender");
		att6.setAttributeTypeInformation(string);
		ColumnProperties c6 = new ColumnProperties();
		c6.setName("GENDER");
		att6.setColumnProperties(c6);

		Attribute att7 = new Attribute();
		//att7.setDefaultValue(20L);
		att7.setName("id");
		att7.setAttributeTypeInformation(longType);
		
		ColumnProperties c7 = new ColumnProperties();
		c7.setName("IDENTIFIER");
		att7.setColumnProperties(c7);
		att7.setIsPrimaryKey(new Boolean(true));

		Attribute att8 = new Attribute();
		//att8.setDefaultValue("lastName");
		att8.setName("lastName");
		att8.setAttributeTypeInformation(string);
		ColumnProperties c8 = new ColumnProperties();
		c8.setName("LAST_NAME");
		att8.setColumnProperties(c8);

		Attribute att9 = new Attribute();
		//att9.setDefaultValue("middleName");
		att9.setName("middleName");
		att9.setAttributeTypeInformation(string);
		ColumnProperties c9 = new ColumnProperties();
		c9.setName("MIDDLE_NAME");
		att9.setColumnProperties(c9);

		Attribute att10 = new Attribute();
		//att10.setDefaultValue("sexGenotype");
		att10.setName("sexGenotype");
		att10.setAttributeTypeInformation(string);
		ColumnProperties c10 = new ColumnProperties();
		c10.setName("GENOTYPE");
		att10.setColumnProperties(c10);

		Attribute att11 = new Attribute();
		//att11.setDefaultValue("socialSecurityNumber");
		att11.setName("socialSecurityNumber");
		att11.setAttributeTypeInformation(string);
		ColumnProperties c11 = new ColumnProperties();
		c11.setName("SOCIAL_SECURITY_NUMBER");
		att11.setColumnProperties(c11);

		Attribute att12 = new Attribute();
		//att12.setDefaultValue("vitalStatus");
		att12.setName("vitalStatus");
		att12.setAttributeTypeInformation(string);
		ColumnProperties c12 = new ColumnProperties();
		c12.setName("VITAL_STATUS");
		att12.setColumnProperties(c12);

		participantAttributes.add(0, att1);
		participantAttributes.add(1, att2);
		participantAttributes.add(2, att3);
		participantAttributes.add(3, att4);
		participantAttributes.add(4, att5);
		participantAttributes.add(5, att6);
		participantAttributes.add(6, att7);
		participantAttributes.add(7, att8);
		participantAttributes.add(8, att9);
		participantAttributes.add(9, att10);
		participantAttributes.add(10, att11);
		participantAttributes.add(11, att12);

		return participantAttributes;
	}

	/*
	 * Creates attributes for participant medical identifier entity, creates 
	 * and sets a column property for each attribute and adds all the 
	 * attributes to a collection.
	 */
	private ArrayList getParticipantMedicalIdentifierAttributes()
	{
		ArrayList<Attribute> participantMedicalIdentifierAttributes = new ArrayList<Attribute>();

		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");
		
		Attribute att1 = new Attribute();
		att1.setName("id");
		att1.setAttributeTypeInformation(longType);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("IDENTIFIER");
		att1.setColumnProperties(c1);
		att1.setIsPrimaryKey(new Boolean(true));

		Attribute att2 = new Attribute();
		att2.setName("medicalRecordNumber");
		att2.setAttributeTypeInformation(string);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("MEDICAL_RECORD_NUMBER");
		att2.setColumnProperties(c2);

//		LongAttribute att3 = new LongAttribute();
//		att3.setName("id");
//		att3.setMeasurementUnits("Long");
//		ColumnProperties c3 = new ColumnProperties();
//		c3.setName("PARTICIPANT_ID");
//		att3.setColumnProperties(c3);
		
		participantMedicalIdentifierAttributes.add(0, att1);
		participantMedicalIdentifierAttributes.add(1, att2);
//		participantMedicalIdentifierAttributes.add(2, att3);
		return participantMedicalIdentifierAttributes;
	}

	/*
	 * Creates attributes for collection protocol registration entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getCollectionProtocolRegistrationAttributes()
	{
		ArrayList<Attribute> collectionProtocolRegistrationAttributes = new ArrayList<Attribute>();

		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		DateAttributeTypeInformation date = new DateAttributeTypeInformation();
		date.setFormat("DD-MM-YYYY");
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");

		
		Attribute att1 = new Attribute();
		att1.setName("activityStatus");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		Attribute att2 = new Attribute();
		att2.setName("id");
		att2.setAttributeTypeInformation(longType);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("IDENTIFIER");
		att2.setColumnProperties(c2);
		att2.setIsPrimaryKey(new Boolean(true));

		Attribute att3 = new Attribute();
		att3.setName("protocolParticipantIdentifier");
		att3.setAttributeTypeInformation(string);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("PROTOCOL_PARTICIPANT_ID");
		att3.setColumnProperties(c3);

		Attribute att4 = new Attribute();
		att4.setName("registrationDate");
		att4.setAttributeTypeInformation(date);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("REGISTRATION_DATE");
		att4.setColumnProperties(c4);

		collectionProtocolRegistrationAttributes.add(0, att1);
		collectionProtocolRegistrationAttributes.add(1, att2);
		collectionProtocolRegistrationAttributes.add(2, att3);
		collectionProtocolRegistrationAttributes.add(3, att4);

		return collectionProtocolRegistrationAttributes;
	}

	/*
	 * Creates attributes for collection protocol entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getCollectionProtocolAttributes()
	{
		ArrayList<Attribute> collectionProtocolAttributes = new ArrayList<Attribute>();

		BooleanAttributeTypeInformation booleanType = new BooleanAttributeTypeInformation();
		
		Attribute att1 = new Attribute();
		att1.setName("aliquotInSameContainer");
		att1.setAttributeTypeInformation(booleanType);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ALIQUOT_IN_SAME_CONTAINER");
		att1.setColumnProperties(c1);

		collectionProtocolAttributes.add(0, att1);

		return collectionProtocolAttributes;
	}

	/*
	 * Creates attributes for specimen protocol entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getSpecimenProtocolAttributes()
	{
		ArrayList<Attribute> specimenProtocolAttributes = new ArrayList<Attribute>();

		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		DateAttributeTypeInformation date = new DateAttributeTypeInformation();
		date.setFormat("DD-MM-YYYY");
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");
		
		IntegerAttributeTypeInformation integer = new IntegerAttributeTypeInformation();
		integer.setMeasurementUnits("Integer");
		
		
		Attribute att1 = new Attribute();
		att1.setName("activityStatus");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		Attribute att2 = new Attribute();
		att2.setName("descriptionURL");
		att2.setAttributeTypeInformation(string);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("DESCRIPTION_URL");
		att2.setColumnProperties(c2);

		Attribute att3 = new Attribute();
		att3.setName("endDate");
		att3.setAttributeTypeInformation(date);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("END_DATE");
		att3.setColumnProperties(c3);

		Attribute att4 = new Attribute();
		att4.setName("enrollment");
		att4.setAttributeTypeInformation(integer);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("ENROLLMENT");
		att4.setColumnProperties(c4);

		Attribute att5 = new Attribute();
		att5.setName("id");
		att5.setAttributeTypeInformation(longType);
		ColumnProperties c5 = new ColumnProperties();
		c5.setName("IDENTIFIER");
		att5.setColumnProperties(c5);
		att5.setIsPrimaryKey(new Boolean(true));

		Attribute att6 = new Attribute();
		att6.setName("irbIdentifier");
		att6.setAttributeTypeInformation(string);
		ColumnProperties c6 = new ColumnProperties();
		c6.setName("IRB_IDENTIFIER");
		att6.setColumnProperties(c6);

		Attribute att7 = new Attribute();
		att7.setName("shortTitle");
		att7.setAttributeTypeInformation(string);
		ColumnProperties c7 = new ColumnProperties();
		c7.setName("SHORT_TITLE");
		att7.setColumnProperties(c7);

		Attribute att8 = new Attribute();
		att8.setName("startDate");
		att8.setAttributeTypeInformation(date);
		ColumnProperties c8 = new ColumnProperties();
		c8.setName("START_DATE");
		att8.setColumnProperties(c8);

		Attribute att9 = new Attribute();
		att9.setName("title");
		att9.setAttributeTypeInformation(string);
		ColumnProperties c9 = new ColumnProperties();
		c9.setName("TITLE");
		att9.setColumnProperties(c9);

		specimenProtocolAttributes.add(0, att1);
		specimenProtocolAttributes.add(1, att2);
		specimenProtocolAttributes.add(2, att3);
		specimenProtocolAttributes.add(3, att4);
		specimenProtocolAttributes.add(4, att5);
		specimenProtocolAttributes.add(5, att6);
		specimenProtocolAttributes.add(6, att7);
		specimenProtocolAttributes.add(7, att8);
		specimenProtocolAttributes.add(8, att9);

		return specimenProtocolAttributes;
	}

	/*
	 * Creates attributes for collection protocol event entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getCollectionProtocolEventAttributes()
	{
		ArrayList<Attribute> collectionProtocolEventAttributes = new ArrayList<Attribute>();

		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		DateAttributeTypeInformation date = new DateAttributeTypeInformation();
		date.setFormat("DD-MM-YYYY");
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");
		
		DoubleAttributeTypeInformation doubleType = new DoubleAttributeTypeInformation();
		
		
		Attribute att1 = new Attribute();
		att1.setName("clinicalStatus");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("CLINICAL_STATUS");
		att1.setColumnProperties(c1);

		Attribute att2 = new Attribute();
		att2.setName("id");
		att2.setAttributeTypeInformation(longType);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("IDENTIFIER");
		att2.setColumnProperties(c2);
		att2.setIsPrimaryKey(new Boolean(true));

		Attribute att3 = new Attribute();
		att3.setName("studyCalendarEventPoint");
		//att3.setSize(50);
		att3.setAttributeTypeInformation(doubleType);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("STUDY_CALENDAR_EVENT_POINT");
		att3.setColumnProperties(c3);

		collectionProtocolEventAttributes.add(0, att1);
		collectionProtocolEventAttributes.add(1, att2);
		collectionProtocolEventAttributes.add(2, att3);

		return collectionProtocolEventAttributes;
	}

	/*
	 * Creates attributes for specimen collection group entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getSpecimenCollectionGroupAttributes()
	{
		ArrayList<Attribute> specimenCollectionGroupAttributes = new ArrayList<Attribute>();

		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");
		
		Attribute att1 = new Attribute();
		att1.setName("id");
		att1.setAttributeTypeInformation(longType);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("IDENTIFIER");
		att1.setColumnProperties(c1);
		att1.setIsPrimaryKey(new Boolean(true));

		Attribute att2 = new Attribute();
		att2.setName("name");
		att2.setAttributeTypeInformation(string);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("NAME");
		att2.setColumnProperties(c2);

		Attribute att3 = new Attribute();
		att3.setName("clinicalDiagnosis");
		att3.setAttributeTypeInformation(string);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("CLINICAL_DIAGNOSIS");
		att3.setColumnProperties(c3);

		Attribute att4 = new Attribute();
		att4.setName("clinicalStatus");
		att4.setAttributeTypeInformation(string);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("CLINICAL_STATUS");
		att4.setColumnProperties(c4);

		Attribute att5 = new Attribute();
		att5.setName("activityStatus");
		att5.setAttributeTypeInformation(string);
		ColumnProperties c5 = new ColumnProperties();
		c5.setName("ACTIVITY_STATUS");
		att5.setColumnProperties(c5);

		specimenCollectionGroupAttributes.add(0, att1);
		specimenCollectionGroupAttributes.add(1, att2);
		specimenCollectionGroupAttributes.add(2, att3);
		specimenCollectionGroupAttributes.add(3, att4);
		specimenCollectionGroupAttributes.add(4, att5);

		return specimenCollectionGroupAttributes;
	}

	/*
	 * Creates attributes for specimen entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getSpecimenAttributes()
	{
		ArrayList<Attribute> specimenAttributes = new ArrayList<Attribute>();
		
		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		DateAttributeTypeInformation date = new DateAttributeTypeInformation();
		date.setFormat("DD-MM-YYYY");
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");
		
		BooleanAttributeTypeInformation booleanType = new BooleanAttributeTypeInformation();
		
		IntegerAttributeTypeInformation integer = new IntegerAttributeTypeInformation();
		integer.setMeasurementUnits("Integer");
		
		Attribute att1 = new Attribute();
		att1.setName("activityStatus");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		Attribute att2 = new Attribute();
		att2.setName("available");
		att2.setAttributeTypeInformation(booleanType);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("AVAILABLE");
		att2.setColumnProperties(c2);

		Attribute att3 = new Attribute();
		att3.setName("barcode");
		att3.setAttributeTypeInformation(string);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("BARCODE");
		att3.setColumnProperties(c3);

		Attribute att4 = new Attribute();
		att4.setName("comment");
		att4.setAttributeTypeInformation(string);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("COMMENTS");
		att4.setColumnProperties(c4);

		Attribute att5 = new Attribute();
		att5.setName("id");
		att5.setAttributeTypeInformation(longType);
		ColumnProperties c5 = new ColumnProperties();
		c5.setName("IDENTIFIER");
		att5.setColumnProperties(c5);
		att5.setIsPrimaryKey(new Boolean(true));

		Attribute att6 = new Attribute();
		att6.setName("label");
		att6.setAttributeTypeInformation(string);
		ColumnProperties c6 = new ColumnProperties();
		c6.setName("LABEL");
		att6.setColumnProperties(c6);

		Attribute att7 = new Attribute();
		att7.setName("lineage");
		att7.setAttributeTypeInformation(string);
		ColumnProperties c7 = new ColumnProperties();
		c7.setName("LINEAGE");
		att7.setColumnProperties(c7);

		Attribute att8 = new Attribute();
		att8.setName("pathologicalStatus");
		att8.setAttributeTypeInformation(string);
		ColumnProperties c8 = new ColumnProperties();
		c8.setName("PATHOLOGICAL_STATUS");
		att8.setColumnProperties(c8);

		Attribute att9 = new Attribute();
		att9.setName("positionDimensionOne");
		att9.setAttributeTypeInformation(integer);
		ColumnProperties c9 = new ColumnProperties();
		c9.setName("POSITION_DIMENSION_ONE");
		att9.setColumnProperties(c9);

		Attribute att10 = new Attribute();
		att10.setName("positionDimensionTwo");
		att10.setAttributeTypeInformation(integer);
		ColumnProperties c10 = new ColumnProperties();
		c10.setName("POSITION_DIMENSION_TWO");
		att10.setColumnProperties(c10);

		Attribute att11 = new Attribute();
		att11.setName("type");
		att11.setAttributeTypeInformation(string);
		ColumnProperties c11 = new ColumnProperties();
		c11.setName("TYPE");
		att11.setColumnProperties(c11);

		specimenAttributes.add(0, att1);
		specimenAttributes.add(1, att2);
		specimenAttributes.add(2, att3);
		specimenAttributes.add(3, att4);
		specimenAttributes.add(4, att5);
		specimenAttributes.add(5, att6);
		specimenAttributes.add(6, att7);
		specimenAttributes.add(7, att8);
		specimenAttributes.add(8, att9);
		specimenAttributes.add(9, att10);
		specimenAttributes.add(10, att11);

		return specimenAttributes;
	}
	
	/*
	 * Creates attributes for specimen event parameters entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getSpecimenEventParametersAttributes()
	{
		ArrayList<Attribute> specimenEventParametersAttributes = new ArrayList<Attribute>();
		
		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		DateAttributeTypeInformation date = new DateAttributeTypeInformation();
		date.setFormat("DD-MM-YYYY");
		
		LongAttributeTypeInformation longType = new LongAttributeTypeInformation();
		longType.setMeasurementUnits("Long");
		
	
		IntegerAttributeTypeInformation integer = new IntegerAttributeTypeInformation();
		integer.setMeasurementUnits("Integer");
		
		Attribute att1 = new Attribute();
		att1.setName("id");
		att1.setAttributeTypeInformation(longType);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("IDENTIFIER");
		att1.setColumnProperties(c1);
		
		Attribute att2 = new Attribute();
		att2.setName("timestamp");
		att2.setAttributeTypeInformation(date);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("EVENT_TIMESTAMP");
		att2.setColumnProperties(c2);
		
		Attribute att3 = new Attribute();
		att3.setName("comments");
		att3.setAttributeTypeInformation(string);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("COMMENTS");
		att3.setColumnProperties(c3);
		
		specimenEventParametersAttributes.add(0, att1);
		specimenEventParametersAttributes.add(1, att2);
		specimenEventParametersAttributes.add(2, att3);
		
		return specimenEventParametersAttributes;
	}
	
	/*
	 * Creates attributes for check in check out event parameters entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getCheckInCheckOutEventParameterAttributes()
	{
		ArrayList<Attribute> checkInCheckOutEventParameterAttributes = new ArrayList<Attribute>();
			
		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		
		
		Attribute att1 = new Attribute();
		att1.setName("storageStatus");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("STORAGE_STATUS");
		att1.setColumnProperties(c1);
		
		checkInCheckOutEventParameterAttributes.add(0, att1);
		
		return checkInCheckOutEventParameterAttributes;
	}
	
	/*
	 * Creates attributes for frozen event parameters entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getFrozenEventParameterAttributes()
	{
		ArrayList<Attribute> frozenEventParameterAttributes = new ArrayList<Attribute>();
			
		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		Attribute att1 = new Attribute();
		att1.setName("method");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("METHOD");
		att1.setColumnProperties(c1);
		
		frozenEventParameterAttributes.add(0, att1);
		
		return frozenEventParameterAttributes;
	}
	
	/*
	 * Creates attributes for procedure event parameters entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getProcedureEventParametersAttributes()
	{
		ArrayList<Attribute> procedureEventParameterAttributes = new ArrayList<Attribute>();
			
		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		
		Attribute att1 = new Attribute();
		att1.setName("url");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("URL");
		att1.setColumnProperties(c1);
		
		Attribute att2 = new Attribute();
		att2.setName("name");
		att2.setAttributeTypeInformation(string);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("NAME");
		att1.setColumnProperties(c2);
		
		procedureEventParameterAttributes.add(0, att1);
		procedureEventParameterAttributes.add(0, att2);
		
		return procedureEventParameterAttributes;
	}
	
	/*
	 * Creates attributes for received event parameters entity,  
	 * creates and sets a column property for each attribute and adds all  
	 * the attributes to a collection.
	 */
	private ArrayList getReceivedEventParametersAttributes()
	{
		ArrayList<Attribute> receivedEventParameterAttributes = new ArrayList<Attribute>();
			
		StringAttributeTypeInformation string = new StringAttributeTypeInformation();
		string.setSize(50);
		
		
		Attribute att1 = new Attribute();
		att1.setName("receivedQuality");
		att1.setAttributeTypeInformation(string);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("RECEIVED_QUALITY");
		att1.setColumnProperties(c1);
		
		receivedEventParameterAttributes.add(0, att1);
		
		return receivedEventParameterAttributes;
	}

	private Attribute getSpecificAttribute(ArrayList list, String aName)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if ((((Attribute) list.get(i)).getName()).equalsIgnoreCase(aName))
			{
				return (Attribute) list.get(i);
			}
		}
		return null;
	}

	public static void main(String[] args)
	{
		EntityManagerMock testMock = new EntityManagerMock();
		try
		{
			System.out.println(testMock.getEntityByName(SPECIMEN_PROTOCOL_NAME).getAbstractAttributeCollection().size());
			System.out.println(testMock.getEntityByName(PARTICIPANT_NAME).getName());
			System.out.println(testMock.getEntityByName(COLLECTION_PROTOCOL_REGISTRATION_NAME).getDescription());
			System.out.println(testMock.getEntityByName(PARTICIPANT_MEDICAL_ID_NAME).getId());
			System.out.println("getAttribute(String, String) METHOD returns--> " + testMock.getAttribute(SPECIMEN_NAME, "lineage").getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}