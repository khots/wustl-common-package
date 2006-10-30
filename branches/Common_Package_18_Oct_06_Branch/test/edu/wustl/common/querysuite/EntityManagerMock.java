
package edu.wustl.common.querysuite;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 17-Oct-2006 16:32:04 PM
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.BooleanAttribute;
import edu.common.dynamicextensions.domain.DateAttribute;
import edu.common.dynamicextensions.domain.DoubleAttribute;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.IntegerAttribute;
import edu.common.dynamicextensions.domain.LongAttribute;
import edu.common.dynamicextensions.domain.Role;
import edu.common.dynamicextensions.domain.StringAttribute;
import edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties;
import edu.common.dynamicextensions.domain.databaseproperties.TableProperties;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public class EntityManagerMock extends EntityManager
{

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#createEntity(edu.common.dynamicextensions.domaininterface.EntityInterface)
	 */
	@Override
	public EntityInterface createEntity(EntityInterface arg0) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.createEntity(arg0);
	}

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
		// TODO Auto-generated method stub
		return super.getAllEntities();
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getAssociation(java.lang.String, java.lang.String)
	 */
	@Override
	public AssociationInterface getAssociation(String arg0, String arg1) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return super.getAssociation(arg0, arg1);
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getAssociations(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection getAssociations(String entityName1, String entityName2) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		if (entityName1.equalsIgnoreCase("Participant") && entityName2.equalsIgnoreCase("ParticipantMedicalIdentifier"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Bi-Directional");

			Role sourceRole = new Role();
			sourceRole.setName("participant");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("containment");
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("participantMedicalIdentifierCollection");
			targetRole.setId(2L);
			targetRole.setAssociationType("containment");
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (entityName1.equalsIgnoreCase("Participant") && entityName2.equalsIgnoreCase("CollectionProtocolRegistration"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Bi-Directional");

			Role sourceRole = new Role();
			sourceRole.setName("participant");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("linking");
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("collectionProtocolRegistrationCollection");
			targetRole.setId(2L);
			targetRole.setAssociationType("linking");
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (entityName1.equalsIgnoreCase("CollectionProtocolRegistration") && entityName2.equalsIgnoreCase("CollectionProtocol"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Destination -> Source");

			Role sourceRole = new Role();
			sourceRole.setName("collectionProtocol");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("linking");
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("collectionProtocolRegistrationCollection");
			targetRole.setId(2L);
			targetRole.setAssociationType("linking");
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (entityName1.equalsIgnoreCase("CollectionProtocolRegistration") && entityName2.equalsIgnoreCase("SpecimenCollectionGroup"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Bi-Directional");

			Role sourceRole = new Role();
			sourceRole.setName("collectionProtocolRegistration");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("linking");
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("SpecimenCollectionGroupCollection");
			targetRole.setId(2L);
			targetRole.setAssociationType("linking");
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (entityName1.equalsIgnoreCase("SpecimenCollectionGroup") && entityName2.equalsIgnoreCase("CollectionProtocolEvent"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Source -> Destination");

			Role sourceRole = new Role();
			sourceRole.setName("specimenCollectionGroup");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("linking");
			sourceRole.setMaxCardinality(10);
			sourceRole.setMinCardinality(0);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("collectionProtocolEvent");
			targetRole.setId(2L);
			targetRole.setAssociationType("linking");
			targetRole.setMaxCardinality(1);
			targetRole.setMinCardinality(1);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (entityName1.equalsIgnoreCase("CollectionProtocol") && entityName2.equalsIgnoreCase("CollectionProtocolEvent"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Bi-Directional");

			Role sourceRole = new Role();
			sourceRole.setName("collectionProtocol");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("linking");
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("collectionProtocolEventCollection");
			targetRole.setId(2L);
			targetRole.setAssociationType("linking");
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(1);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (entityName1.equalsIgnoreCase("SpecimenCollectionGroup") && entityName2.equalsIgnoreCase("Specimen"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Bi-Directional");

			Role sourceRole = new Role();
			sourceRole.setName("specimenCollectionGroup");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("linking");
			sourceRole.setMaxCardinality(1);
			sourceRole.setMinCardinality(1);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("specimenCollection");
			targetRole.setId(2L);
			targetRole.setAssociationType("linking");
			targetRole.setMaxCardinality(10);
			targetRole.setMinCardinality(1);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else if (entityName1.equalsIgnoreCase("Specimen") && entityName2.equalsIgnoreCase("Specimen"))
		{
			Association currentAssociation = new Association();

			EntityInterface sourceEntity = getEntityByName(entityName1);
			EntityInterface targetEntity = getEntityByName(entityName2);

			currentAssociation.setSourceEntity(sourceEntity);
			currentAssociation.setTargetEntity(targetEntity);
			currentAssociation.setDirection("Bi-Directional");

			Role sourceRole = new Role();
			sourceRole.setName("childrenSpecimen");
			sourceRole.setId(1L);
			sourceRole.setAssociationType("linking");
			sourceRole.setMaxCardinality(10);
			sourceRole.setMinCardinality(0);
			currentAssociation.setSourceRole(sourceRole);

			Role targetRole = new Role();
			targetRole.setName("parentSpecimen");
			targetRole.setId(2L);
			targetRole.setAssociationType("linking");
			targetRole.setMaxCardinality(1);
			targetRole.setMinCardinality(0);
			currentAssociation.setTargetRole(targetRole);

			Collection<Association> associationsCollection = new ArrayList<Association>();
			associationsCollection.add(currentAssociation);
			return associationsCollection;
		}
		else
		{
			System.out.println("There is no association between these two entities");
			return null;
		}
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#getAttribute(java.lang.String, java.lang.String)
	 */
	@Override
	public AttributeInterface getAttribute(String entityName, String attributeName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		if (entityName.equalsIgnoreCase("Participant"))
		{
			ArrayList list = getParticipantAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase("ParticipantMedicalIdentifier"))
		{
			ArrayList list = getParticipantMedicalIdentifierAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase("CollectionProtocolRegistration"))
		{
			ArrayList list = getCollectionProtocolRegistrationAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase("CollectionProtocol"))
		{
			ArrayList list = getCollectionProtocolAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase("SpecimenProtocol"))
		{
			ArrayList list = getSpecimenProtocolAttributes();
			//System.out.println(list.get(3).getClass().getName());
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase("CollectionProtocolEvent"))
		{
			ArrayList list = getCollectionProtocolEventAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase("SpecimenCollectionGroup"))
		{
			ArrayList list = getSpecimenCollectionGroupAttributes();
			return getSpecificAttribute(list, attributeName);
		}
		else if (entityName.equalsIgnoreCase("Specimen"))
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
	public EntityInterface getEntityByName(String name) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		if (name.equalsIgnoreCase("Participant"))
		{
			return createParticipantEntity(name);
		}
		else if (name.equalsIgnoreCase("ParticipantMedicalIdentifier"))
		{
			return createParticipantMedicalIdentifierEntity(name);
		}
		else if (name.equalsIgnoreCase("CollectionProtocolRegistration"))
		{
			return createCollectionProtocolRegistrationEntity(name);
		}
		else if (name.equalsIgnoreCase("CollectionProtocol"))
		{
			return createCollectionProtocolEntity(name);
		}
		else if (name.equalsIgnoreCase("SpecimenProtocol"))
		{
			return createSpecimenProtocolEntity(name);
		}
		else if (name.equalsIgnoreCase("CollectionProtocolEvent"))
		{
			return createCollectionProtocolEventEntity(name);
		}
		else if (name.equalsIgnoreCase("SpecimenCollectionGroup"))
		{
			return createSpecimenCollectionGroupEntity(name);
		}
		else if (name.equalsIgnoreCase("Specimen"))
		{
			return createSpecimenEntity(name);
		}
		return null;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityManager#setInstance(edu.common.dynamicextensions.entitymanager.EntityManager)
	 */
	@Override
	public void setInstance(EntityManager arg0)
	{
		// TODO Auto-generated method stub
		super.setInstance(arg0);
	}

	/*
	 * @param name
	 * Creates a participant entity, sets the attributes collection and
	 * table properties for the entity.
	 */
	private Entity createParticipantEntity(String name)
	{
		Entity e = new Entity();
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a participant entity");
		e.setId(1L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getParticipantAttributes());

		TableProperties participantTableProperties = new TableProperties();
		participantTableProperties.setName("catissue_participant");
		participantTableProperties.setId(1L);
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
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a participant medical identifier entity");
		e.setId(2L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getParticipantMedicalIdentifierAttributes());

		TableProperties participantMedicalIdentifierTableProperties = new TableProperties();
		participantMedicalIdentifierTableProperties.setName("catissue_part_medical_id");
		participantMedicalIdentifierTableProperties.setId(2L);
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
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a collection protocol registration entity");
		e.setId(3L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getCollectionProtocolRegistrationAttributes());

		TableProperties collectionProtocolRegistrationTableProperties = new TableProperties();
		collectionProtocolRegistrationTableProperties.setName("catissue_coll_prot_reg");
		collectionProtocolRegistrationTableProperties.setId(3L);
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
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a collection protocol entity");
		e.setId(4L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getCollectionProtocolAttributes());

		TableProperties collectionProtocolTableProperties = new TableProperties();
		collectionProtocolTableProperties.setName("catissue_collection_protocol");
		collectionProtocolTableProperties.setId(4L);
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
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a specimen protocol entity");
		e.setId(5L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getSpecimenProtocolAttributes());

		TableProperties specimenProtocolTableProperties = new TableProperties();
		specimenProtocolTableProperties.setName("catissue_specimen_protocol");
		specimenProtocolTableProperties.setId(5L);
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
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a collection protocol event entity");
		e.setId(6L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getCollectionProtocolEventAttributes());

		TableProperties collectionProtocolEventTableProperties = new TableProperties();
		collectionProtocolEventTableProperties.setName("catissue_coll_prot_event");
		collectionProtocolEventTableProperties.setId(6L);
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
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a specimen collection group entity");
		e.setId(7L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getSpecimenCollectionGroupAttributes());

		TableProperties specimenCollectionGroupTableProperties = new TableProperties();
		specimenCollectionGroupTableProperties.setName("catissue_coll_prot_event");
		specimenCollectionGroupTableProperties.setId(7L);
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
		e.setName(name);
		e.setCreatedDate(new Date());
		e.setDescription("This is a specimen entity");
		e.setId(8L);
		e.setLastUpdated(new Date());

		e.setAbstractAttributeCollection(getSpecimenAttributes());

		TableProperties specimenTableProperties = new TableProperties();
		specimenTableProperties.setName("catissue_specimen");
		specimenTableProperties.setId(8L);
		e.setTableProperties(specimenTableProperties);
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

		StringAttribute att1 = new StringAttribute();
		//att1.setDefaultValue("activityStatus");
		att1.setName("activityStatus");
		att1.setSize(20);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		DateAttribute att2 = new DateAttribute();
		//att2.setDefaultValue(new Date(12 - 03 - 1995));
		att2.setName("birthDate");
		att2.setFormat("DD-MM-YYYY");
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("BIRTH_DATE");
		att2.setColumnProperties(c2);

		DateAttribute att3 = new DateAttribute();
		//att3.setDefaultValue(new Date(12 - 03 - 2005));
		att3.setName("deathDate");
		att3.setFormat("DD-MM-YYYY");
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("DEATH_DATE");
		att3.setColumnProperties(c3);

		StringAttribute att4 = new StringAttribute();
		//att4.setDefaultValue("ethnicity");
		att4.setName("ethnicity");
		att4.setSize(50);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("ETHNICITY");
		att4.setColumnProperties(c4);

		StringAttribute att5 = new StringAttribute();
		//att5.setDefaultValue("firstName");
		att5.setName("firstName");
		att5.setSize(50);
		ColumnProperties c5 = new ColumnProperties();
		c5.setName("FIRST_NAME");
		att5.setColumnProperties(c5);

		StringAttribute att6 = new StringAttribute();
		//att6.setDefaultValue("gender");
		att6.setName("gender");
		att6.setSize(20);
		ColumnProperties c6 = new ColumnProperties();
		c6.setName("GENDER");
		att6.setColumnProperties(c6);

		LongAttribute att7 = new LongAttribute();
		//att7.setDefaultValue(20L);
		att7.setName("id");
		att7.setMeasurementUnits("Long");
		ColumnProperties c7 = new ColumnProperties();
		c7.setName("IDENTIFIER");
		att7.setColumnProperties(c7);

		StringAttribute att8 = new StringAttribute();
		//att8.setDefaultValue("lastName");
		att8.setName("lastName");
		att8.setSize(50);
		ColumnProperties c8 = new ColumnProperties();
		c8.setName("LAST_NAME");
		att8.setColumnProperties(c8);

		StringAttribute att9 = new StringAttribute();
		//att9.setDefaultValue("middleName");
		att9.setName("middleName");
		att9.setSize(50);
		ColumnProperties c9 = new ColumnProperties();
		c9.setName("MIDDLE_NAME");
		att9.setColumnProperties(c9);

		StringAttribute att10 = new StringAttribute();
		//att10.setDefaultValue("sexGenotype");
		att10.setName("sexGenotype");
		att10.setSize(50);
		ColumnProperties c10 = new ColumnProperties();
		c10.setName("GENOTYPE");
		att10.setColumnProperties(c10);

		StringAttribute att11 = new StringAttribute();
		//att11.setDefaultValue("socialSecurityNumber");
		att11.setName("socialSecurityNumber");
		att11.setSize(50);
		ColumnProperties c11 = new ColumnProperties();
		c11.setName("SOCIAL_SECURITY_NUMBER");
		att11.setColumnProperties(c11);

		StringAttribute att12 = new StringAttribute();
		//att12.setDefaultValue("vitalStatus");
		att12.setName("vitalStatus");
		att12.setSize(50);
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

		LongAttribute att1 = new LongAttribute();
		att1.setName("id");
		att1.setMeasurementUnits("Long");
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("IDENTIFIER");
		att1.setColumnProperties(c1);

		StringAttribute att2 = new StringAttribute();
		att2.setName("medicalRecordNumber");
		att2.setSize(50);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("MEDICAL_RECORD_NUMBER");
		att2.setColumnProperties(c2);

		participantMedicalIdentifierAttributes.add(0, att1);
		participantMedicalIdentifierAttributes.add(1, att2);

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

		StringAttribute att1 = new StringAttribute();
		att1.setName("activityStatus");
		att1.setSize(20);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		LongAttribute att2 = new LongAttribute();
		att2.setName("id");
		att2.setMeasurementUnits("Long");
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("IDENTIFIER");
		att2.setColumnProperties(c2);

		StringAttribute att3 = new StringAttribute();
		att3.setName("protocolParticipantIdentifier");
		att3.setSize(50);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("PROTOCOL_PARTICIPANT_ID");
		att3.setColumnProperties(c3);

		DateAttribute att4 = new DateAttribute();
		att4.setName("registrationDate");
		att4.setFormat("DD-MM-YYYY");
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

		BooleanAttribute att1 = new BooleanAttribute();
		att1.setName("aliquotInSameContainer");
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

		StringAttribute att1 = new StringAttribute();
		att1.setName("activityStatus");
		att1.setSize(50);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		StringAttribute att2 = new StringAttribute();
		att2.setName("descriptionURL");
		att2.setSize(200);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("DESCRIPTION_URL");
		att2.setColumnProperties(c2);

		DateAttribute att3 = new DateAttribute();
		att3.setName("endDate");
		att3.setFormat("DD-MM-YYYY");
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("END_DATE");
		att3.setColumnProperties(c3);

		IntegerAttribute att4 = new IntegerAttribute();
		att4.setName("enrollment");
		att4.setMeasurementUnits("Integer");
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("ENROLLMENT");
		att4.setColumnProperties(c4);

		LongAttribute att5 = new LongAttribute();
		att5.setName("id");
		att5.setMeasurementUnits("Long");
		ColumnProperties c5 = new ColumnProperties();
		c5.setName("IDENTIFIER");
		att5.setColumnProperties(c5);

		StringAttribute att6 = new StringAttribute();
		att6.setName("irbIdentifier");
		att6.setSize(50);
		ColumnProperties c6 = new ColumnProperties();
		c6.setName("IRB_IDENTIFIER");
		att6.setColumnProperties(c6);

		StringAttribute att7 = new StringAttribute();
		att7.setName("shortTitle");
		att7.setSize(50);
		ColumnProperties c7 = new ColumnProperties();
		c7.setName("SHORT_TITLE");
		att7.setColumnProperties(c7);

		DateAttribute att8 = new DateAttribute();
		att8.setName("startDate");
		att8.setFormat("DD-MM-YYYY");
		ColumnProperties c8 = new ColumnProperties();
		c8.setName("START_DATE");
		att8.setColumnProperties(c8);

		StringAttribute att9 = new StringAttribute();
		att9.setName("title");
		att9.setSize(150);
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

		StringAttribute att1 = new StringAttribute();
		att1.setName("clinicalStatus");
		att1.setSize(50);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("CLINICAL_STATUS");
		att1.setColumnProperties(c1);

		LongAttribute att2 = new LongAttribute();
		att2.setName("id");
		att2.setMeasurementUnits("Long");
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("IDENTIFIER");
		att2.setColumnProperties(c2);

		DoubleAttribute att3 = new DoubleAttribute();
		att3.setName("studyCalendarEventPoint");
		//att3.setSize(50);
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

		LongAttribute att1 = new LongAttribute();
		att1.setName("id");
		att1.setMeasurementUnits("Long");
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("IDENTIFIER");
		att1.setColumnProperties(c1);

		StringAttribute att2 = new StringAttribute();
		att2.setName("name");
		att2.setSize(55);
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("NAME");
		att2.setColumnProperties(c2);

		StringAttribute att3 = new StringAttribute();
		att3.setName("clinicalDiagnosis");
		att3.setSize(150);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("CLINICAL_DIAGNOSIS");
		att3.setColumnProperties(c3);

		StringAttribute att4 = new StringAttribute();
		att4.setName("clinicalStatus");
		att4.setSize(50);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("CLINICAL_STATUS");
		att4.setColumnProperties(c4);

		StringAttribute att5 = new StringAttribute();
		att5.setName("activityStatus");
		att5.setSize(50);
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

		StringAttribute att1 = new StringAttribute();
		att1.setName("activityStatus");
		att1.setSize(50);
		ColumnProperties c1 = new ColumnProperties();
		c1.setName("ACTIVITY_STATUS");
		att1.setColumnProperties(c1);

		BooleanAttribute att2 = new BooleanAttribute();
		att2.setName("available");
		ColumnProperties c2 = new ColumnProperties();
		c2.setName("AVAILABLE");
		att2.setColumnProperties(c2);

		StringAttribute att3 = new StringAttribute();
		att3.setName("barcode");
		att3.setSize(50);
		ColumnProperties c3 = new ColumnProperties();
		c3.setName("BARCODE");
		att3.setColumnProperties(c3);

		StringAttribute att4 = new StringAttribute();
		att4.setName("comment");
		att4.setSize(200);
		ColumnProperties c4 = new ColumnProperties();
		c4.setName("COMMENTS");
		att4.setColumnProperties(c4);

		LongAttribute att5 = new LongAttribute();
		att5.setName("id");
		att5.setMeasurementUnits("Long");
		ColumnProperties c5 = new ColumnProperties();
		c5.setName("IDENTIFIER");
		att5.setColumnProperties(c5);

		StringAttribute att6 = new StringAttribute();
		att6.setName("label");
		att6.setSize(50);
		ColumnProperties c6 = new ColumnProperties();
		c6.setName("LABEL");
		att6.setColumnProperties(c6);

		StringAttribute att7 = new StringAttribute();
		att7.setName("lineage");
		att7.setSize(50);
		ColumnProperties c7 = new ColumnProperties();
		c7.setName("LINEAGE");
		att7.setColumnProperties(c7);

		StringAttribute att8 = new StringAttribute();
		att8.setName("pathologicalStatus");
		att8.setSize(50);
		ColumnProperties c8 = new ColumnProperties();
		c8.setName("PATHOLOGICAL_STATUS");
		att8.setColumnProperties(c8);

		IntegerAttribute att9 = new IntegerAttribute();
		att9.setName("positionDimensionOne");
		att9.setMeasurementUnits("Integer");
		ColumnProperties c9 = new ColumnProperties();
		c9.setName("POSITION_DIMENSION_ONE");
		att9.setColumnProperties(c9);

		IntegerAttribute att10 = new IntegerAttribute();
		att10.setName("positionDimensionTwo");
		att10.setMeasurementUnits("Integer");
		ColumnProperties c10 = new ColumnProperties();
		c10.setName("POSITION_DIMENSION_TWO");
		att10.setColumnProperties(c10);

		StringAttribute att11 = new StringAttribute();
		att11.setName("type");
		att11.setSize(50);
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
			System.out.println(testMock.getEntityByName("specimenprotocol").getAbstractAttributeCollection().size());
			System.out.println(testMock.getEntityByName("participant").getName());
			System.out.println(testMock.getEntityByName("collectionprotocolregistration").getDescription());
			System.out.println(testMock.getEntityByName("participantMeDicAlidentifier").getId());
			System.out.println("getAttribute(String, String) METHOD returns--> " + testMock.getAttribute("specimen", "lineage").getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}