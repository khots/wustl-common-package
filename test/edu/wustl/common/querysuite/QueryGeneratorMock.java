/**
 * 
 */

package edu.wustl.common.querysuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraintEntity;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Expression;

/**
 * Mock to create The Query Objects.
 * @author prafull_kadam
 *
 */
public class QueryGeneratorMock
{

	static EntityManagerMock enitytManager = new EntityManagerMock();
	
	/**
	 * Create Condition for given Participant Entity: activityStatus = 'Active'
	 * @param participantEntity the Dynamic Extension entity for participant. 
	 * @return The Condition object.
	 */
	public static ICondition createParticipantCondition1(EntityInterface participantEntity)
	{
		List<String> values = new ArrayList<String>();
		values.add("Active");
		AttributeInterface attribute = findAttribute(participantEntity, "activityStatus");
		ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.Equals, values);

		return condition;
	}

	/**
	 * To search attribute in the Entity.
	 * @param entity The Dynamic Extension Entity Paricipant.
	 * @param attributeName The name of the attribute to search. 
	 * @return The corresponding attibute.
	 */
	private static AttributeInterface findAttribute(EntityInterface entity, String attributeName)
	{
		Collection attributes = entity.getAbstractAttributeCollection();
		for (Iterator iter = attributes.iterator(); iter.hasNext();)
		{
			AttributeInterface attribute = (AttributeInterface) iter.next();
			if (attribute.getName().equals(attributeName))
				return attribute;
		}
		return null;
	}

	/**
	 * Cretate Condtionion for given Participant: id in (1,2,3,4)
	 * @param participantEntity The Dynamic Extension Entity Paricipant
	 * @return The Condition object.
	 */
	public static ICondition createParticipantCondition2(EntityInterface participantEntity)
	{
		List<String> values = new ArrayList<String>();
		values.add("1");
		values.add("2");
		values.add("3");
		values.add("4");
		AttributeInterface attribute = findAttribute(participantEntity, "id");
		
		ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.In, values);
		return condition;
	}

	/**
	 * Cretate Condtionion for given Participant: birthDate between ('1-1-2000','1-2-2000')
	 * @param participantEntity The Dynamic Extension Entity Paricipant
	 * @return The Condition object.
	 */
	public static ICondition createParticipantCondition3(EntityInterface participantEntity)
	{
		List<String> values = new ArrayList<String>();
		values.add("1-1-2000");
		values.add("1-2-2000");
		AttributeInterface attribute = findAttribute(participantEntity, "birthDate");
		
		ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.Between, values);
		return condition;
	}

	/**
	 * Cretate Condtionion for given Participant: id < 10 
	 * @param participantEntity The Dynamic Extension Entity Paricipant
	 * @return The Condition object.
	 */
	public static ICondition createParticipantCondition4(EntityInterface participantEntity)
	{
		List<String> values = new ArrayList<String>();
		values.add("10");
		
		AttributeInterface attribute = findAttribute(participantEntity, "id");
		
		ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.LessThan, values);
		return condition;
	}

	/**
	 * Create Condition for given Participant: activityStatus Contains 'Active'
	 * @param participantEntity The Dynamic Extension Entity Paricipant
	 * @return The Condition object.
	 */
	public static ICondition createParticipantCondition5(EntityInterface participantEntity)
	{
		List<String> values = new ArrayList<String>();
		values.add("Active");
		
		AttributeInterface attribute = findAttribute(participantEntity, "activityStatus");
		
		ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.Contains, values);

		return condition;
	}

	/**
	 * Create Rule for given Participant as : id in (1,2,3,4) AND birthDate between ('1-1-2000','1-2-2000')
	 * @param participantEntity The Dynamic Extension Entity Paricipant
	 * @return The Rule Object.
	 */
	public static IRule createParticipantRule1(EntityInterface participantEntity)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantCondition2(participantEntity));
		conditions.add(createParticipantCondition3(participantEntity));
		IRule rule = QueryObjectFactory.createRule(conditions);
		return rule;
	}

	/**
	 * Create Rule for given Participant as : activityStatus = 'Active'
	 * @param participantEntity The Dynamic Extension Entity Paricipant
	 * @return The Rule reference.
	 */
	public static IRule createParticipantRule2(EntityInterface participantEntity)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantCondition1(participantEntity));
		IRule rule = QueryObjectFactory.createRule(conditions);

		return rule;
	}

	/**
	 * Create Rule for given Participant as : id < 10
	 * @param participantEntity The Dynamic Extension Entity Paricipant
	 * @return The Rule reference.
	 */
	public static IRule createParticipantRule3(EntityInterface participantEntity)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantCondition4(participantEntity));
		IRule rule = QueryObjectFactory.createRule(conditions);
		return rule;
	}

	/**
	 * Create Expression for given Participant as: [id in (1,2,3,4) AND birthDate between ('1-1-2000','1-2-2000')] OR [activityStatus = 'Active']
	 * @param participant The Constraint Entity object for Paricipant
	 * @return The Expression Object.
	 */
	public static IExpression creatParticipantExpression1(IConstraintEntity participant)
	{
		IExpression expression = new Expression(participant, 1);
		expression.addOperand(createParticipantRule1(participant.getDynamicExtensionsEntity()));
		ILogicalConnector connector = QueryObjectFactory.createLogicalConnector(LogicalOperator.Or);
		expression.addOperand(connector, createParticipantRule2(participant.getDynamicExtensionsEntity()));

		return expression;
	}

	/**
	 * Create Expression for given Participant as: [Expression1] And [id < 10]
	 * Where Expression1 is : [medicalRecordNumber = 'M001']
	 * @param participant The Constraint Entity object for Paricipant.
	 * @param paricipantMedicalId The Constraint Entity object for Paricipant mdefical Identifier.
	 * @return The Expression Object.
	 */
	public static IExpression creatParticipantExpression2(IConstraintEntity participant, IConstraintEntity paricipantMedicalId)
	{
		IExpression expression = new Expression(participant, 1);
		expression.addOperand(createParticipantRule3(participant.getDynamicExtensionsEntity()));
		ILogicalConnector connector = QueryObjectFactory
				.createLogicalConnector(LogicalOperator.And);
		expression
				.addOperand(
						connector,
						(IExpressionOperand) creatParticipantMedicalIdentifierExpression(paricipantMedicalId));

		return expression;
	}

	/**
	 * To create IQuery for the Participant & Participant Medical Identifier as: [activityStatus = 'Active'] OR [medicalRecordNumber = 'M001']
	 * @param expression The Expression reference created by function creatParticipantExpression2()
	 * @return The corresponding join Graph reference.
	 */
	public static IQuery creatParticipantQuery1()
	{
		IQuery query = null;
		try
		{
			query = QueryObjectFactory.createQuery();
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);
			IJoinGraph joinGraph = constraints.getJoinGraph();
			
			// creating Participant Expression.
			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
			participantExpression.addOperand(createParticipantRule2(participantEntity));
	

			// creating Participant medical Id Expression.
			EntityInterface pmIdEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IConstraintEntity pmIdConstraintEntity = QueryObjectFactory.createConstrainedEntity(pmIdEntity);
			IExpression pmExpression = constraints.addExpression(pmIdConstraintEntity);
			ILogicalConnector connector = QueryObjectFactory.createLogicalConnector(LogicalOperator.And);
			participantExpression.addOperand(connector, pmExpression.getExpressionId());
			pmExpression.addOperand(createParticipantMedicalIdentifierRule1(pmIdEntity, pmExpression));

			// Adding association to joingraph.
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IIntraModelAssociation association = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(participantExpression.getExpressionId(), pmExpression
					.getExpressionId(), association);
		}
		catch (Exception e)
		{
			return null;
		}
		return query;
	}

	/**
	 * Defines Following Query:
	 * <pre>
	 * 	G: name starts with 'X'
	 * 		S: type equals 'RNA'
	 * 			 S: type equals 'DNA'
	 * 		Pseudo AND
	 * 		S: is Available
	 *		OR
	 * 		S: Not equals 'DNA'
	 * </pre>
	 * @return The IQuery object corresponding to above Query.
	 */
	public static IQuery createSCGQuery()
	{
		
		IQuery query = null;
		
		try
		{
			query = QueryObjectFactory.createQuery();
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);
			IJoinGraph joinGraph = constraints.getJoinGraph();

			// Creating SCG Expression.
			EntityInterface scgEntity =  enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);;
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstrainedEntity(scgEntity);
			IExpression scgExpression = constraints.addExpression(scgConstraintEntity);
			IRule SCGRule = QueryObjectFactory.createRule(null);
			List<String> values = new ArrayList<String>();
			values.add("X");
			ICondition SCGCondition = QueryObjectFactory.createCondition(findAttribute(scgEntity, "name"), RelationalOperator.StartsWith, values);
			SCGRule.addCondition(SCGCondition);
			scgExpression.addOperand(SCGRule);

			
			ILogicalConnector orConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.Or);
			
			// creating Specimen EXpression.
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstrainedEntity(specimenEntity);
			IExpression SpecimenExpression1 = constraints.addExpression(specimenConstraintEntity);
			scgExpression.addOperand(orConnector, SpecimenExpression1.getExpressionId());
			
			IRule specimenRule1 = QueryObjectFactory.createRule(null);
			List<String> SpecimenExpression1Values = new ArrayList<String>();
			SpecimenExpression1Values.add("RNA");
			ICondition specimenCondition1 = QueryObjectFactory.createCondition(findAttribute(specimenEntity, "type"), RelationalOperator.Equals, SpecimenExpression1Values);
			specimenRule1.addCondition(specimenCondition1);
			SpecimenExpression1.addOperand(specimenRule1);
	
			// Creating child specimen Expression.
			IExpression childSpecimenExpression = constraints.addExpression(specimenConstraintEntity);
			SpecimenExpression1.addOperand(orConnector, childSpecimenExpression.getExpressionId());

			IRule childSpecimenRule = QueryObjectFactory.createRule(null);
			List<String> childSpecimenExpressionValues = new ArrayList<String>();
			childSpecimenExpressionValues.add("DNA");
			ICondition childSpecimenCondition = QueryObjectFactory.createCondition(findAttribute(specimenEntity, "type"), RelationalOperator.Equals, childSpecimenExpressionValues);
			childSpecimenRule.addCondition(childSpecimenCondition);
			childSpecimenExpression.addOperand(childSpecimenRule);
	
			
			ILogicalConnector pAndConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.And);

			// Creating 2nd specimen Expression.
			IExpression SpecimenExpression2 = constraints.addExpression(specimenConstraintEntity);
			scgExpression.addOperand(pAndConnector, SpecimenExpression2.getExpressionId());
			
			IRule specimenRule2 = QueryObjectFactory.createRule(null);
			List<String> SpecimenExpression2Values = new ArrayList<String>();
			SpecimenExpression2Values.add("true");
			ICondition specimenCondition2 = QueryObjectFactory.createCondition(findAttribute(specimenEntity, "available"), RelationalOperator.Equals, SpecimenExpression2Values);
			specimenRule2.addCondition(specimenCondition2);
			SpecimenExpression2.addOperand(specimenRule2);

			
			//Creating 3rd Specimen Expression
			IExpression SpecimenExpression3 = constraints.addExpression(specimenConstraintEntity);
			scgExpression.addOperand(orConnector, SpecimenExpression3.getExpressionId());
			
			IRule specimenRule3 = QueryObjectFactory.createRule(null);
			List<String> SpecimenExpression3Values = new ArrayList<String>();
			SpecimenExpression3Values.add("DNA");
			ICondition specimenCondition3 = QueryObjectFactory.createCondition(findAttribute(specimenEntity, "type"), RelationalOperator.NotEquals, SpecimenExpression3Values);
			specimenRule3.addCondition(specimenCondition3);
			SpecimenExpression3.addOperand(specimenRule3);
			
			
			// Creating & Adding association to joingraph.
			AssociationInterface scgAndSpecimenAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME, "specimenCollectionGroup"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iScgAndSpecimenAssociation = QueryObjectFactory.createIntraModelAssociation(scgAndSpecimenAssociation);
			
			AssociationInterface specimenAndSpecimenAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimenAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimenAssociation);
			

			joinGraph.putAssociation(scgExpression.getExpressionId(), SpecimenExpression1
					.getExpressionId(), iScgAndSpecimenAssociation);
			joinGraph.putAssociation(scgExpression.getExpressionId(), SpecimenExpression2
					.getExpressionId(), iScgAndSpecimenAssociation);
			joinGraph.putAssociation(SpecimenExpression1.getExpressionId(), childSpecimenExpression
					.getExpressionId(), iSpecimenAndSpecimenAssociation);

			joinGraph.putAssociation(scgExpression.getExpressionId(), SpecimenExpression3
					.getExpressionId(), iScgAndSpecimenAssociation);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		return query;
	}

	/**
	 * To create IQuery for the Participant as: [activityStatus = 'Active']
	 * @param expression The Expression reference created by function creatParticipantExpression2()
	 * @return The corresponding join Graph reference.
	 */
	public static IQuery creatParticipantQuery2()
	{
		IQuery query = null;
		try
		{
			
			query = QueryObjectFactory.createQuery();
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);
	
			//creating Participant Expression.
			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
			participantExpression.addOperand(createParticipantRule2(participantEntity));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * Create Condition for given Participant: medicalRecordNumber = 'M001'
	 * @param participantMedicalId The Dynamic Extension Entity object representing Participant Medical Identifier object
	 * @return The Condition object reference.
	 */
	public static ICondition createParticipantMedicalIdentifierCondition1(
			EntityInterface participantMedicalId)
	{
		List<String> values = new ArrayList<String>();
		values.add("M001");
		
		AttributeInterface attribute = findAttribute(participantMedicalId, "medicalRecordNumber");
		ICondition condition = QueryObjectFactory.createCondition(attribute, RelationalOperator.Equals, values);

		return condition;
	}

	/**
	 * Create Rule for Participant Medical Identifier as: [medicalRecordNumber = 'M001']
	 * @param participantMedicalId The Dynamic Extension Entity object representing Participant Medical Identifier obje 
	 * @return The Rule reference.
	 */
	private static IRule createParticipantMedicalIdentifierRule1(EntityInterface participantMedicalId,
			IExpression containingExpression)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantMedicalIdentifierCondition1(participantMedicalId));
		IRule rule = QueryObjectFactory.createRule(conditions);
		return rule;
	}

	/**
	 * Create Expression for Participant Medical Identifier as: [medicalRecordNumber = 'M001']
	 * @param participantMedicalId The Dynamic Extension Entity object representing Participant Medical Identifier.
	 * @return The Expression reference
	 */
	public static IExpression creatParticipantMedicalIdentifierExpression(
			IConstraintEntity participantMedicalId)
	{
		IExpression expression = new Expression(participantMedicalId, 2);
		IRule rule = createParticipantMedicalIdentifierRule1(participantMedicalId.getDynamicExtensionsEntity(), expression);
		expression.addOperand(rule);
		return expression;
	}

	/**
	 * 
	 * @return The IQuery Object representation for the sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: LastNameStarts with 'S'<P>
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Type equals "Fixed Tissue"
	 *  					OR
	 *  			S: Type equals "Fresh Tissue" 
	 * </pre>
	 *  	
	 */
	public static IQuery createSampleQuery1()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			ILogicalConnector andConnector = QueryObjectFactory
					.createLogicalConnector(LogicalOperator.And);

			
			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			EntityInterface cprEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
			
			IRule participantRule = QueryObjectFactory.createRule(null);
			List<String> participantValues = new ArrayList<String>();
			participantValues.add("s");
			ICondition ParticipantCondition = QueryObjectFactory.createCondition(findAttribute(participantEntity, "lastName"), RelationalOperator.StartsWith, participantValues);
			participantRule.addCondition(ParticipantCondition);
			participantExpression.addOperand(participantRule);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(andConnector, collectionProtocolRegExpression
					.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);
			
			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);

			
			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstrainedEntity(scgEntity);
			IExpression specimenCollectionGroupExpression = constraints.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(specimenCollectionGroupExpression.getExpressionId());
			AssociationInterface cprAndSpgAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,
							"collectionProtocolRegistration"),
					EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			IIntraModelAssociation iCprAndSpgAssociation = QueryObjectFactory.createIntraModelAssociation(cprAndSpgAssociation);
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression.getExpressionId(), iCprAndSpgAssociation);

			//creating SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstrainedEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(specimenExpression1.getExpressionId());
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
							"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			ILogicalConnector orConnetor = QueryObjectFactory
					.createLogicalConnector(LogicalOperator.Or);
			List<String> specimenExpression1Rule2Values = new ArrayList<String>();
			specimenExpression1Rule2Values.add("Fresh Tissue");
			ICondition specimenExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule2Values);
			IRule specimenExpression1Rule2 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule2.addCondition(specimenExpression1Rule2Condition1);
			specimenExpression1.addOperand(orConnetor, specimenExpression1Rule2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * @return The IQuery Object representation for the sample query no. 2 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: Collection Site name equals "Site1" OR Collection Site name equals "Site2"
	 *  			S: Type equals "Fixed Tissue" OR Type equals "Fresh Tissue"
	 * </pre>
	 *  	
	 */
	public static IQuery createSampleQuery2()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			
			ILogicalConnector andConnector = QueryObjectFactory
					.createLogicalConnector(LogicalOperator.And);
			ILogicalConnector orConnetor = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.Or);

			
			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			EntityInterface cprEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			EntityInterface siteEntity = enitytManager.getEntityByName(EntityManagerMock.SITE_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());
			
			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);
			
			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);
			

			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstrainedEntity(scgEntity);
			IExpression specimenCollectionGroupExpression1 = constraints.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(specimenCollectionGroupExpression1
					.getExpressionId());
			
			AssociationInterface cprAndSpgAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,
							"collectionProtocolRegistration"),
					EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			IIntraModelAssociation iCprAndSpgAssociation = QueryObjectFactory.createIntraModelAssociation(cprAndSpgAssociation);
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression1.getExpressionId(), iCprAndSpgAssociation);
			
			//creating Site Expression.
			IConstraintEntity siteConstraintEntity = QueryObjectFactory.createConstrainedEntity(siteEntity);
			IExpression siteExpression1 = constraints.addExpression(siteConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(siteExpression1.getExpressionId());
			AssociationInterface SpgAndSiteAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
							"specimenCollectionGroup"), EntityManagerMock.SITE_NAME);
			IIntraModelAssociation iSpgAndSiteAssociation = QueryObjectFactory.createIntraModelAssociation(SpgAndSiteAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					siteExpression1.getExpressionId(), iSpgAndSiteAssociation);

			List<String> siteExpression1Rule1Values = new ArrayList<String>();
			siteExpression1Rule1Values.add("Site1");

			AttributeInterface siteNameAttribute = findAttribute(siteEntity, "name");
			
			ICondition siteExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					siteNameAttribute, RelationalOperator.Equals, siteExpression1Rule1Values);
			IRule siteExpression1Rule1 = QueryObjectFactory.createRule(null);
			siteExpression1Rule1.addCondition(siteExpression1Rule1Condition1);
			siteExpression1.addOperand(siteExpression1Rule1);

			List<String> siteExpression1Rule2Values = new ArrayList<String>();
			siteExpression1Rule2Values.add("Site1");

			ICondition siteExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					siteNameAttribute, RelationalOperator.Equals, siteExpression1Rule2Values);
			IRule siteExpression1Rule2 = QueryObjectFactory.createRule(null);
			siteExpression1Rule2.addCondition(siteExpression1Rule2Condition1);
			siteExpression1.addOperand(orConnetor, siteExpression1Rule2);

			//creating SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstrainedEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(andConnector, specimenExpression1
					.getExpressionId());
			
			
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
							"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);
			
			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			List<String> specimenExpression1Rule2Values = new ArrayList<String>();
			specimenExpression1Rule2Values.add("Fresh Tissue");
			ICondition specimenExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule2Values);
			IRule specimenExpression1Rule2 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule2.addCondition(specimenExpression1Rule2Condition1);
			specimenExpression1.addOperand(orConnetor, specimenExpression1Rule2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * 
	 * @return The IQuery Object representation for the sample query no. 3 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: Clinical status equals "New Diagnosis"
	 *  			S: Type equals "DNA"
	 *  			OR
	 *  			S: Type equals "Fresh Tissue" 
	 *  		Pseudo AND
	 *  		G: Clinical status equals "Post Operative"
	 *  			S: Type equals "Fixed Tissue"
	 *  			OR
	 *  			S: Type equals "Fresh Tissue" 
	 *  </pre>	
	 */
	public static IQuery createSampleQuery3()
	{
		IQuery query = QueryObjectFactory.createQuery();

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			
			ILogicalConnector andConnector = QueryObjectFactory
					.createLogicalConnector(LogicalOperator.And);
			ILogicalConnector orConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.Or);
			ILogicalConnector pandConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.And);
	

			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			EntityInterface cprEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			
			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());
			
			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);
			
			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);
			
			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstrainedEntity(scgEntity);
			IExpression specimenCollectionGroupExpression1 = constraints.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(specimenCollectionGroupExpression1
					.getExpressionId());
			
			AssociationInterface cprAndSpgAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,
							"collectionProtocolRegistration"),
					EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			IIntraModelAssociation iCprAndSpgAssociation = QueryObjectFactory.createIntraModelAssociation(cprAndSpgAssociation);
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression1.getExpressionId(), iCprAndSpgAssociation);
			
			IRule specimenCollectionGroupExpression1Rule1 = QueryObjectFactory
					.createRule(null);
			List<String> specimenCollectionGroupExpression1Rule1Values = new ArrayList<String>();
			specimenCollectionGroupExpression1Rule1Values.add("New Diagnosis");
			ICondition specimenCollectionGroupExpression1Rule1Condition = QueryObjectFactory
					.createCondition(findAttribute(scgEntity, "clinicalStatus"),
							RelationalOperator.Equals,
							specimenCollectionGroupExpression1Rule1Values);
			specimenCollectionGroupExpression1Rule1
					.addCondition(specimenCollectionGroupExpression1Rule1Condition);
			specimenCollectionGroupExpression1.addOperand(specimenCollectionGroupExpression1Rule1);

			//creating SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstrainedEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(andConnector, specimenExpression1
					.getExpressionId());
			
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
							"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);
			
			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("DNA");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			List<String> specimenExpression1Rule2Values = new ArrayList<String>();
			specimenExpression1Rule2Values.add("Fresh Tissue");
			ICondition specimenExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule2Values);
			IRule specimenExpression1Rule2 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule2.addCondition(specimenExpression1Rule2Condition1);
			specimenExpression1.addOperand(orConnector, specimenExpression1Rule2);

			//creating expression for SpecimenCollectionGroup2.
			IExpression specimenCollectionGroupExpression2 = constraints
					.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(pandConnector,
					specimenCollectionGroupExpression2.getExpressionId());
			//			AssociationInterface cprAndSpgAssociation = enitytManager.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME, "collectionProtocolRegistration");
			
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression2.getExpressionId(), iCprAndSpgAssociation);
			
			IRule specimenCollectionGroupExpression2Rule1 = QueryObjectFactory
					.createRule(null);
			List<String> specimenCollectionGroupExpression2Rule1Values = new ArrayList<String>();
			specimenCollectionGroupExpression2Rule1Values.add("Post-Operative");
			ICondition specimenCollectionGroupExpression2Rule1Condition = QueryObjectFactory
					.createCondition(findAttribute(scgEntity, "clinicalStatus"),
							RelationalOperator.Equals,
							specimenCollectionGroupExpression2Rule1Values);
			specimenCollectionGroupExpression2Rule1
					.addCondition(specimenCollectionGroupExpression2Rule1Condition);
			specimenCollectionGroupExpression2.addOperand(specimenCollectionGroupExpression2Rule1);

			
			// creating SpecimeExpression.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression2.addOperand(andConnector, specimenExpression2
					.getExpressionId());
			
			joinGraph.putAssociation(specimenCollectionGroupExpression2.getExpressionId(),
					specimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);
			
			List<String> specimenExpression2Rule1Values = new ArrayList<String>();
			specimenExpression2Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values);
			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);
			specimenExpression2.addOperand(specimenExpression2Rule1);

			List<String> specimenExpression2Rule2Values = new ArrayList<String>();
			specimenExpression2Rule2Values.add("Fixed Tissue");

			ICondition specimenExpression2Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule2Values);
			IRule specimenExpression2Rule2 = QueryObjectFactory.createRule(null);
			specimenExpression2Rule2.addCondition(specimenExpression2Rule2Condition1);
			specimenExpression2.addOperand(orConnector, specimenExpression2Rule2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * 
	 * @return The IQuery Object representation for the sample query no. 4 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: Clinical status equals "New Diagnosis"
	 *  			S: Type equals "DNA"
	 *  			Pseudo AND
	 *  			S: Type equals "Milk" 
	 *  		Pseudo AND
	 *  		G: Clinical status equals "Post Operative"
	 *  			S: Type equals "Fixed Tissue"
	 *  			OR
	 *  			S: Type equals "Fresh Tissue" 
	 *  </pre>	
	 */
	public static IQuery createSampleQuery4()
	{
		IQuery query = QueryObjectFactory.createQuery();

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			
			ILogicalConnector andConnector = QueryObjectFactory
					.createLogicalConnector(LogicalOperator.And);
			ILogicalConnector orConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.Or);
			ILogicalConnector pandConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.And);
	

			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			EntityInterface cprEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());
			
			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);
			
			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);
			
			// creating expression for SpecimenCollectionGroup1.
//			 creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstrainedEntity(scgEntity);
			IExpression specimenCollectionGroupExpression1 = constraints.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(specimenCollectionGroupExpression1
					.getExpressionId());
			
			AssociationInterface cprAndSpgAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,
							"collectionProtocolRegistration"),
					EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			IIntraModelAssociation iCprAndSpgAssociation = QueryObjectFactory.createIntraModelAssociation(cprAndSpgAssociation);
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression1.getExpressionId(), iCprAndSpgAssociation);
			
			List<String> specimenCollectionGroupExpression1Rule1Values = new ArrayList<String>();
			specimenCollectionGroupExpression1Rule1Values.add("New Diagnosis");

			ICondition specimenCollectionGroupExpression1Rule1Condition1 = QueryObjectFactory
					.createCondition(findAttribute(scgEntity, "clinicalStatus"), RelationalOperator.Equals,
							specimenCollectionGroupExpression1Rule1Values);
			IRule specimenCollectionGroupExpression1Rule1 = QueryObjectFactory
					.createRule(null);
			specimenCollectionGroupExpression1Rule1
					.addCondition(specimenCollectionGroupExpression1Rule1Condition1);
			specimenCollectionGroupExpression1.addOperand(specimenCollectionGroupExpression1Rule1);

			//creating SpecimeExpression 1, under scg expression1.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstrainedEntity(specimenEntity);
			IExpression scg1SpecimenExpression1 = constraints.addExpression(specimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(andConnector, scg1SpecimenExpression1
					.getExpressionId());
			
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
							"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					scg1SpecimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);
			
			List<String> scg1SpecimenExpression1Rule1Values = new ArrayList<String>();
			scg1SpecimenExpression1Rule1Values.add("DNA");

			ICondition scg1SpecimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					scg1SpecimenExpression1Rule1Values);
			IRule scg1SpecimenExpression1Rule1 = QueryObjectFactory
					.createRule(null);
			scg1SpecimenExpression1Rule1.addCondition(scg1SpecimenExpression1Rule1Condition1);
			scg1SpecimenExpression1.addOperand(scg1SpecimenExpression1Rule1);

			//			creating SpecimeExpression 2, under scg expression1.
			IExpression scg1SpecimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression1.addOperand(andConnector, scg1SpecimenExpression2
					.getExpressionId());
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					scg1SpecimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);

			List<String> scg1SpecimenExpression2Rule1Values = new ArrayList<String>();
			scg1SpecimenExpression2Rule1Values.add("Milk");

			ICondition scg1SpecimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					scg1SpecimenExpression2Rule1Values);
			IRule scg1SpecimenExpression2Rule1 = QueryObjectFactory
					.createRule(null);
			scg1SpecimenExpression2Rule1.addCondition(scg1SpecimenExpression2Rule1Condition1);
			scg1SpecimenExpression2.addOperand(scg1SpecimenExpression2Rule1);

			//			 creating expression for SpecimenCollectionGroup2.
			IExpression specimenCollectionGroupExpression2 = constraints
					.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(pandConnector,
					specimenCollectionGroupExpression2.getExpressionId());
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression2.getExpressionId(),iCprAndSpgAssociation);

			List<String> specimenCollectionGroupExpression2Rule1Values = new ArrayList<String>();
			specimenCollectionGroupExpression2Rule1Values.add("Post Operative");

			//			IAttribute clinicalStatusAttribute1 = QueryObjectFactory.getAttribute(enitytManager
			//					.getAttribute(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME, "clinicalStatus"), specimenCollectionGroup);
			ICondition specimenCollectionGroupExpression2Rule1Condition1 = QueryObjectFactory
					.createCondition(findAttribute(scgEntity, "clinicalStatus"), RelationalOperator.Equals,
							specimenCollectionGroupExpression2Rule1Values);
			IRule specimenCollectionGroupExpression2Rule1 = QueryObjectFactory
					.createRule(null);
			specimenCollectionGroupExpression2Rule1
					.addCondition(specimenCollectionGroupExpression2Rule1Condition1);
			specimenCollectionGroupExpression2.addOperand(specimenCollectionGroupExpression2Rule1);

			//creating SpecimeExpression 1, under scg expression1.
			IExpression scg2SpecimenExpression1 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression2.addOperand(andConnector, scg2SpecimenExpression1
					.getExpressionId());
			//			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
			//					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
			//							"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			joinGraph.putAssociation(specimenCollectionGroupExpression2.getExpressionId(),
					scg2SpecimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			List<String> scg2SpecimenExpression1Rule1Values = new ArrayList<String>();
			scg2SpecimenExpression1Rule1Values.add("Fixed Tissue");

			//			IAttribute specimenAttributeType = QueryObjectFactory.getAttribute(enitytManager
			//					.getAttribute(EntityManagerMock.SPECIMEN_NAME, "type"), specimenClass);
			ICondition scg2SpecimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					scg2SpecimenExpression1Rule1Values);
			IRule scg2SpecimenExpression1Rule1 = QueryObjectFactory
					.createRule(null);
			scg2SpecimenExpression1Rule1.addCondition(scg2SpecimenExpression1Rule1Condition1);
			scg2SpecimenExpression1.addOperand(scg2SpecimenExpression1Rule1);

			//			creating SpecimeExpression 2, under scg expression1.
			IExpression scg2SpecimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression2.addOperand(orConnector, scg2SpecimenExpression2
					.getExpressionId());
			joinGraph.putAssociation(specimenCollectionGroupExpression2.getExpressionId(),
					scg2SpecimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);

			List<String> scg2SpecimenExpression2Rule1Values = new ArrayList<String>();
			scg2SpecimenExpression2Rule1Values.add("Fresh Tissue");

			ICondition scg2SpecimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					scg2SpecimenExpression2Rule1Values);
			IRule scg2SpecimenExpression2Rule1 = QueryObjectFactory
					.createRule(null);
			scg2SpecimenExpression2Rule1.addCondition(scg2SpecimenExpression2Rule1Condition1);
			scg2SpecimenExpression2.addOperand(scg2SpecimenExpression2Rule1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * 
	 * @return The IQuery Object representation for the sample query no. 5 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Type equals "Fixed Tissue"
	 *	  				S: Type equals "Milk" 
	 *  </pre>	
	 */
	public static IQuery createSampleQuery5()
	{
		IQuery query = QueryObjectFactory.createQuery();

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			
			ILogicalConnector andConnector = QueryObjectFactory
					.createLogicalConnector(LogicalOperator.And);
			
			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			EntityInterface cprEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			
			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());
			
			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);
			
			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);
			
			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstrainedEntity(scgEntity);
			IExpression specimenCollectionGroupExpression1 = constraints.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(specimenCollectionGroupExpression1
					.getExpressionId());
			
			AssociationInterface cprAndSpgAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,
							"collectionProtocolRegistration"),
					EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			IIntraModelAssociation iCprAndSpgAssociation = QueryObjectFactory.createIntraModelAssociation(cprAndSpgAssociation);
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression1.getExpressionId(), iCprAndSpgAssociation);
			
			//creating SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstrainedEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(specimenExpression1
					.getExpressionId());
			
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
							"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);
			

			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			//			creating child SpecimeExpression.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenExpression1.addOperand(andConnector, specimenExpression2.getExpressionId());
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), specimenExpression2
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> specimenExpression2Rule1Values = new ArrayList<String>();
			specimenExpression2Rule1Values.add("Milk");

			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values);
			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);
			specimenExpression2.addOperand(specimenExpression2Rule1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * 
	 * @return The IQuery Object representation for the sample query no. 6 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Type equals "Fixed Tissue" 
	 *  				S: Type equals "Amniotic Fluid"
	 * </pre> 	
	 */
	public static IQuery createSampleQuery6()
	{
		IQuery query = QueryObjectFactory.createQuery();

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			
			ILogicalConnector andConnector = QueryObjectFactory
					.createLogicalConnector(LogicalOperator.And);
			
			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			EntityInterface cprEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);


			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());
			
			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);
			
			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);
			
			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstrainedEntity(scgEntity);
			IExpression specimenCollectionGroupExpression1 = constraints.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(specimenCollectionGroupExpression1
					.getExpressionId());
			
			AssociationInterface cprAndSpgAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,
							"collectionProtocolRegistration"),
					EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			IIntraModelAssociation iCprAndSpgAssociation = QueryObjectFactory.createIntraModelAssociation(cprAndSpgAssociation);
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression1.getExpressionId(), iCprAndSpgAssociation);
			
			//creating SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstrainedEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(specimenExpression1
					.getExpressionId());
			
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
							"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);
			

			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			//creating child SpecimeExpression.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenExpression1.addOperand(andConnector, specimenExpression2.getExpressionId());
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), specimenExpression2
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> specimenExpression2Rule1Values = new ArrayList<String>();
			specimenExpression2Rule1Values.add("Amniotic Fluid");

			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values);
			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);
			specimenExpression2.addOperand(specimenExpression2Rule1);		
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	private static AssociationInterface getAssociationFrom(
			Collection<AssociationInterface> associations, String targetEntityName)
	{
		for (Iterator<AssociationInterface> iter = associations.iterator(); iter.hasNext();)
		{
			AssociationInterface theAssociation = iter.next();
			if (theAssociation.getTargetEntity().getName().equals(targetEntityName))
			{
				return theAssociation;
			}
		}
		return null;
	}
}
