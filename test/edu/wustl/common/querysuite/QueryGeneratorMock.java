/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite;




import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
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
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
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
		Collection attributes = entity.getAttributeCollection();
		for (Iterator iter = attributes.iterator(); iter.hasNext();)
		{
			AttributeInterface attribute = (AttributeInterface) iter.next();
			if (attribute.getName().equals(attributeName))
				return attribute;
		}
//		EntityInterface parentEntity = entity.getParentEntity();
//		if (parentEntity!=null)
//		{
//			return findAttribute(parentEntity,attributeName);
//		}
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
		values.add("01-01-2000");
		values.add("01-02-2000");
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
		expression.addOperand(getOrConnector(), createParticipantRule2(participant.getDynamicExtensionsEntity()));

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
	 * To create IQuery for the Participant & Participant Medical Identifier as: [activityStatus = 'Active'] AND [ id in (1,2,3,4) AND birthDate between ('1-1-2000','1-2-2000')] AND [medicalRecordNumber = 'M001']
	 * @param expression The Expression reference created by function creatParticipantExpression2()
	 * @return The corresponding join Graph reference.
	 */
	public static IQuery createParticipantQuery1()
	{
		IQuery query = null;
		try
		{
			query = QueryObjectFactory.createQuery();
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);
			IJoinGraph joinGraph = constraints.getJoinGraph();
			ILogicalConnector andConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.And);

			// creating Participant Expression.
			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
			participantExpression.addOperand(createParticipantRule2(participantEntity));

			participantExpression.addOperand(andConnector,createParticipantRule1(participantEntity));

			// creating Participant medical Id Expression.
			EntityInterface pmIdEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IConstraintEntity pmIdConstraintEntity = QueryObjectFactory.createConstraintEntity(pmIdEntity);
			IExpression pmExpression = constraints.addExpression(pmIdConstraintEntity);
			participantExpression.addOperand(andConnector, pmExpression.getExpressionId());
			pmExpression.addOperand(createParticipantMedicalIdentifierRule1(pmIdEntity, pmExpression));

			// Adding association to joingraph.
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IIntraModelAssociation association = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(participantExpression.getExpressionId(), pmExpression
					.getExpressionId(), association);

//			// creating output tree.
			participantExpression.setInView(true);
			pmExpression.setInView(true);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//			query.setRootOutputClass(participantNode);

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
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
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
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
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

//			// creating output tree.
			scgExpression.setInView(true);
			SpecimenExpression1.setInView(true);
			SpecimenExpression2.setInView(true);
			SpecimenExpression3.setInView(true);
			childSpecimenExpression.setInView(true);
//			IOutputTreeNode scgNode = QueryObjectFactory.createOutputTreeNode(createScgOutputEntity(scgEntity));
//			query.setRootOutputClass(scgNode);
//			scgNode.addChild(iScgAndSpecimenAssociation, createSpecimenOutputEntity(specimenEntity));

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
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
			participantExpression.addOperand(createParticipantRule2(participantEntity));
//
//			// creating output tree.
			participantExpression.setInView(true);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//			query.setRootOutputClass(participantNode);

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
	 * Creates outputEntity for Participant with firstName & Last name as its selected Attributes.
	 * @param participant The participant Entity.
	 * @return The Output entity reference.
	 */
	public static IOutputEntity createParticipantOutputEntity(EntityInterface participant)
	{
		IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(participant);
		List<AttributeInterface> selectedAttributesIndices = new ArrayList<AttributeInterface>();
		selectedAttributesIndices.add(findAttribute(participant, "firstName"));
		selectedAttributesIndices.add(findAttribute(participant, "lastName"));
		outputEntity.setSelectedAttributes(selectedAttributesIndices);
		return outputEntity;
	}

	/**
	 * Creates outputEntity for collectionProtocolRegistrationEntity with registrationDate as its selected Attributes.
	 * @param cprEntity The collectionProtocolRegistrationEntity.
	 * @return The Output entity reference.
	 */
	public static IOutputEntity createCollProtoRegOutputEntity(EntityInterface cprEntity)
	{
		IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(cprEntity);
		List<AttributeInterface> selectedAttributesIndices = new ArrayList<AttributeInterface>();
		selectedAttributesIndices.add(findAttribute(cprEntity, "registrationDate"));
		outputEntity.setSelectedAttributes(selectedAttributesIndices);
		return outputEntity;
	}

	/**
	 * Creates outputEntity for Specimen Collection Group with name & clinicalDiagnosis as its selected Attributes.
	 * @param scgEntity The Specimen Collection Group.
	 * @return The Output entity reference.
	 */
	public static IOutputEntity createScgOutputEntity(EntityInterface scgEntity)
	{
		IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(scgEntity);
		List<AttributeInterface> selectedAttributesIndices = new ArrayList<AttributeInterface>();
		selectedAttributesIndices.add(findAttribute(scgEntity, "name"));
		selectedAttributesIndices.add(findAttribute(scgEntity, "clinicalDiagnosis"));
		outputEntity.setSelectedAttributes(selectedAttributesIndices);
		return outputEntity;
	}

	/**
	 * Creates outputEntity for Specimen Collection Group with name & clinicalDiagnosis as its selected Attributes.
	 * @param specimenEntity The Specimen Collection Group.
	 * @return The Output entity reference.
	 */
	public static IOutputEntity createSpecimenOutputEntity(EntityInterface specimenEntity)
	{
		IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(specimenEntity);
		List<AttributeInterface> selectedAttributesIndices = new ArrayList<AttributeInterface>();
		selectedAttributesIndices.add(findAttribute(specimenEntity, "label"));
		selectedAttributesIndices.add(findAttribute(specimenEntity, "barcode"));
		outputEntity.setSelectedAttributes(selectedAttributesIndices);
		return outputEntity;
	}

	/**
	 * Creates outputEntity for Specimen Collection Group with name & clinicalDiagnosis as its selected Attributes.
	 * @param specimenCharEntity The Specimen Collection Group.
	 * @return The Output entity reference.
	 */
	public static IOutputEntity createSpecimenCharacteristicOutputEntity(EntityInterface specimenCharEntity)
	{
		IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(specimenCharEntity);
		List<AttributeInterface> selectedAttributesIndices = new ArrayList<AttributeInterface>();
		selectedAttributesIndices.add(findAttribute(specimenCharEntity, "tissueSite"));
		selectedAttributesIndices.add(findAttribute(specimenCharEntity, "tissueSide"));
		outputEntity.setSelectedAttributes(selectedAttributesIndices);
		return outputEntity;
	}
	/**
	 * 
	 * @return The IQuery Object representation for the sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: LastNameStarts with 'S'<P>
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Class equals "Tissue" AND Type equals "Fixed Tissue"
	 *  					OR
	 *  			S: Class equals "Tissue" AND Type equals "Fresh Tissue" 
	 * 
	 * The output Tree:
	 * P: firtsName, LastName
	 * 		C: registrationDate
	 * 			G: name, clinicalDiagnosis
	 * 				S: label, barcode
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
			EntityInterface tissueSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.TISSUE_SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			IRule participantRule = QueryObjectFactory.createRule(null);
			List<String> participantValues = new ArrayList<String>();
			participantValues.add("s");
			ICondition ParticipantCondition = QueryObjectFactory.createCondition(findAttribute(participantEntity, "lastName"), RelationalOperator.StartsWith, participantValues);
			participantRule.addCondition(ParticipantCondition);
			participantExpression.addOperand(participantRule);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
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
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
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
			IConstraintEntity tissueSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(tissueSpecimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(tissueSpecimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(specimenExpression1.getExpressionId());
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.TISSUE_SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			
			ILogicalConnector orConnetor = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.Or);
			
			IExpression specimenExpression2 = constraints.addExpression(tissueSpecimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(orConnetor, specimenExpression2.getExpressionId());
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);

			
			List<String> specimenExpression2Rule2Values = new ArrayList<String>();
			specimenExpression2Rule2Values.add("Fresh Tissue");
			ICondition specimenExpression2Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule2Values);
			IRule specimenExpression2Rule2 = QueryObjectFactory.createRule(null);
			specimenExpression2Rule2.addCondition(specimenExpression2Rule2Condition1);
			specimenExpression2.addOperand(specimenExpression2Rule2);

//			// Creating output Tree.
			participantExpression.setInView(true);
			collectionProtocolRegExpression.setInView(true);
			specimenCollectionGroupExpression.setInView(true);
			specimenExpression1.setInView(true);
			specimenExpression2.setInView(true);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//
//			query.setRootOutputClass(participantNode);
//
//			IOutputTreeNode cprNode = participantNode.addChild(iParticipanCPRegAssociation, createCollProtoRegOutputEntity(cprEntity));
//
//			IOutputTreeNode scgNode = cprNode.addChild(iCprAndSpgAssociation, createScgOutputEntity(scgEntity));
//			scgNode.addChild(iSpgAndSpecimeAssociation, createSpecimenOutputEntity(tissueSpecimenEntity));

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
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createSampleQuery1()
	 * Added two empty expressions under participant as:
	 * 		PM: ANY
	 * 			Site: ANY  	
	 * @return The IQuery Object representation for the sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc" with few empty expressions.
	 */
	public static IQuery createSampleQuery1WithEmptyExp()
	{
		IQuery query = null;

		try
		{
			query = createSampleQuery1(); 
			IConstraints constraints = query.getConstraints();
			IJoinGraph joinGraph = constraints.getJoinGraph();
			IExpressionId root = constraints.getRootExpressionId();
			
			EntityInterface pmEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);

			// creating expression for Participant Medical Id.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(pmEntity);
			IExpression pmExpression = constraints.addExpression(participantConstraintEntity);
			constraints.getExpression(root).addOperand(getAndConnector(), pmExpression.getExpressionId());
			
			AssociationInterface participanPMAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IIntraModelAssociation iParticipanPMAssociation = QueryObjectFactory.createIntraModelAssociation(participanPMAssociation);

			joinGraph.putAssociation(root,
					pmExpression.getExpressionId(), iParticipanPMAssociation);

			
			EntityInterface siteEntity = enitytManager.getEntityByName(EntityManagerMock.SITE_NAME);

			// creating expression for Site.
			IConstraintEntity siteConstraintEntity = QueryObjectFactory.createConstraintEntity(siteEntity);
			IExpression siteExpression = constraints.addExpression(siteConstraintEntity);
			pmExpression.addOperand(siteExpression.getExpressionId());
			
			AssociationInterface pmSiteAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME, "ParticipantMedicalIdentifier"),
					EntityManagerMock.SITE_NAME);
			IIntraModelAssociation ipmSiteAssociation = QueryObjectFactory.createIntraModelAssociation(pmSiteAssociation);

			joinGraph.putAssociation(pmExpression.getExpressionId(),siteExpression.getExpressionId(), ipmSiteAssociation);
			
			setAllExpressionInView(constraints);
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
	 *  			S: Class equals "Tissue" AND(Type equals "Fixed Tissue" OR Type equals "Fresh Tissue")
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
			EntityInterface tissueSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.TISSUE_SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			EntityInterface siteEntity = enitytManager.getEntityByName(EntityManagerMock.SITE_NAME);

			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);


			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
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
			IConstraintEntity siteConstraintEntity = QueryObjectFactory.createConstraintEntity(siteEntity);
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
			IConstraintEntity tissueSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(tissueSpecimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(tissueSpecimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(andConnector, specimenExpression1
					.getExpressionId());


			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.TISSUE_SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			List<String> specimenExpression1Rule2Values = new ArrayList<String>();
			specimenExpression1Rule2Values.add("Fresh Tissue");
			ICondition specimenExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule2Values);
			IRule specimenExpression1Rule2 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule2.addCondition(specimenExpression1Rule2Condition1);
			specimenExpression1.addOperand(orConnetor, specimenExpression1Rule2);

//			// Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//
//			query.setRootOutputClass(participantNode);
//
//			IOutputTreeNode cprNode = participantNode.addChild(iParticipanCPRegAssociation, createCollProtoRegOutputEntity(cprEntity));
//
//			IOutputTreeNode scgNode = cprNode.addChild(iCprAndSpgAssociation, createScgOutputEntity(scgEntity));
//			scgNode.addChild(iSpgAndSpecimeAssociation, createSpecimenOutputEntity(tissueSpecimenEntity));

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
	 *  			S: Class equals "Molecular" AND Type equals "DNA"
	 *  			OR
	 *  			S: Class equals "Tissue" Type equals "Fresh Tissue" 
	 *  		Pseudo AND
	 *  		G: Clinical status equals "Post Operative"
	 *  			S: Class equals "Tissue" Type equals "Fixed Tissue"
	 *  			OR
	 *  			S: Class equals "Tissue" Type equals "Fresh Tissue" 
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
			EntityInterface tissueSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.TISSUE_SPECIMEN_NAME);
			EntityInterface molecularSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.MOLECULAR_SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);

			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);

			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
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

			//creating Molecular SpecimeExpression.
			IConstraintEntity moelcularSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(molecularSpecimenEntity);
			IExpression molecularSpecimenExpression1 = constraints.addExpression(moelcularSpecimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(andConnector, molecularSpecimenExpression1
					.getExpressionId());

			AssociationInterface spgAndMolecularSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.MOLECULAR_SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndMolecularSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndMolecularSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					molecularSpecimenExpression1.getExpressionId(), iSpgAndMolecularSpecimeAssociation);

			List<String> molecularSpecimenExpression1Rule1Values = new ArrayList<String>();
			molecularSpecimenExpression1Rule1Values.add("DNA");

			ICondition molecularSpecimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(molecularSpecimenEntity, "type"), RelationalOperator.Equals,
					molecularSpecimenExpression1Rule1Values);
			IRule molecularSpecimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			molecularSpecimenExpression1Rule1.addCondition(molecularSpecimenExpression1Rule1Condition1);
			molecularSpecimenExpression1.addOperand(molecularSpecimenExpression1Rule1);


			// Creating TissueSpecimen exression1.
			IConstraintEntity tissueSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(tissueSpecimenEntity);
			IExpression tissueSpecimenExpression1 = constraints.addExpression(tissueSpecimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(orConnector, tissueSpecimenExpression1
					.getExpressionId());
			
			AssociationInterface spgAndTissueSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.TISSUE_SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndTissueSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndTissueSpecimeAssociation);
			
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					tissueSpecimenExpression1.getExpressionId(), iSpgAndTissueSpecimeAssociation);

			
			List<String> tissueSpecimenExpression1Rule2Values = new ArrayList<String>();
			tissueSpecimenExpression1Rule2Values.add("Fresh Tissue");
			ICondition tissueSpecimenExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					tissueSpecimenExpression1Rule2Values);
			IRule tissueSpecimenExpression1Rule2 = QueryObjectFactory.createRule(null);
			tissueSpecimenExpression1Rule2.addCondition(tissueSpecimenExpression1Rule2Condition1);
			tissueSpecimenExpression1.addOperand(tissueSpecimenExpression1Rule2);

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


			// creating Tissue SpecimeExpression2.
			IExpression tissueSpecimenExpression2 = constraints.addExpression(tissueSpecimenConstraintEntity);

			specimenCollectionGroupExpression2.addOperand(andConnector, tissueSpecimenExpression2
					.getExpressionId());

			joinGraph.putAssociation(specimenCollectionGroupExpression2.getExpressionId(),
					tissueSpecimenExpression2.getExpressionId(), iSpgAndTissueSpecimeAssociation);

			List<String> tissueSpecimenExpression2Rule1Values = new ArrayList<String>();
			tissueSpecimenExpression2Rule1Values.add("Fixed Tissue");

			ICondition tissueSpecimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					tissueSpecimenExpression2Rule1Values);
			IRule tissueSpecimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			tissueSpecimenExpression2Rule1.addCondition(tissueSpecimenExpression2Rule1Condition1);
			tissueSpecimenExpression2.addOperand(tissueSpecimenExpression2Rule1);

			List<String> tissueSpecimenExpression2Rule2Values = new ArrayList<String>();
			tissueSpecimenExpression2Rule2Values.add("Fixed Tissue");

			ICondition tissueSpecimenExpression2Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					tissueSpecimenExpression2Rule2Values);
			IRule tissueSpecimenExpression2Rule2 = QueryObjectFactory.createRule(null);
			tissueSpecimenExpression2Rule2.addCondition(tissueSpecimenExpression2Rule2Condition1);
			tissueSpecimenExpression2.addOperand(orConnector, tissueSpecimenExpression2Rule2);

//			// Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//
//			query.setRootOutputClass(participantNode);
//
//			IOutputTreeNode cprNode = participantNode.addChild(iParticipanCPRegAssociation, createCollProtoRegOutputEntity(cprEntity));
//
//			IOutputTreeNode scgNode = cprNode.addChild(iCprAndSpgAssociation, createScgOutputEntity(scgEntity));
//			scgNode.addChild(iSpgAndMolecularSpecimeAssociation, createSpecimenOutputEntity(tissueSpecimenEntity));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * To set all Expressions in the view.
	 * @param constraints The reference to constraints object.
	 */
	private static void setAllExpressionInView(IConstraints constraints)
	{
		Enumeration<IExpressionId>  expressions = constraints.getExpressionIds();
		
		while (expressions.hasMoreElements())
		{
			IExpressionId expId = expressions.nextElement();
			IExpression expression = constraints.getExpression(expId);
			expression.setInView(true);
		}
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
	 *  TODO: Add derived classes liek Molecular/Tissue/Fluide specimen instead of the specimen class. 
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
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);

			// creating expression for SpecimenCollectionGroup1.
//			creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
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
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
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

//			// Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//
//			query.setRootOutputClass(participantNode);
//
//			IOutputTreeNode cprNode = participantNode.addChild(iParticipanCPRegAssociation, createCollProtoRegOutputEntity(cprEntity));
//
//			IOutputTreeNode scgNode = cprNode.addChild(iCprAndSpgAssociation, createScgOutputEntity(scgEntity));
//			scgNode.addChild(iSpgAndSpecimeAssociation, createSpecimenOutputEntity(specimenEntity));
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
	 *  			S: Class equals "Tissue" Type equals "Fixed Tissue"
	 *	  				S: Class equals "fluid" Type equals "Milk" 
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
			EntityInterface tissueSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.TISSUE_SPECIMEN_NAME);
			EntityInterface fluidSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.FLUID_SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);


			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);

			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
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
			IConstraintEntity tissueSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(tissueSpecimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(tissueSpecimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(specimenExpression1
					.getExpressionId());

			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.TISSUE_SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);


			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			//			creating child SpecimeExpression.
			IConstraintEntity fluideSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(fluidSpecimenEntity);
			IExpression specimenExpression2 = constraints.addExpression(fluideSpecimenConstraintEntity);

			specimenExpression1.addOperand(andConnector, specimenExpression2.getExpressionId());
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.TISSUE_SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.FLUID_SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), specimenExpression2
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> specimenExpression2Rule1Values = new ArrayList<String>();
			specimenExpression2Rule1Values.add("Milk");

			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(fluidSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values);
			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);
			specimenExpression2.addOperand(specimenExpression2Rule1);

//			// Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//
//			query.setRootOutputClass(participantNode);
//
//			IOutputTreeNode cprNode = participantNode.addChild(iParticipanCPRegAssociation, createCollProtoRegOutputEntity(cprEntity));
//
//			IOutputTreeNode scgNode = cprNode.addChild(iCprAndSpgAssociation, createScgOutputEntity(scgEntity));
//			IOutputTreeNode  specimenNode = scgNode.addChild(iSpgAndSpecimeAssociation, createSpecimenOutputEntity(tissueSpecimenEntity));
//			specimenNode.addChild(iSpecimenAndSpecimeAssociation, createSpecimenOutputEntity(tissueSpecimenEntity));


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
	 *  			S: Class = "Tissue" and Type equals "Fixed Tissue" and Frozen Event Parameter.Method Starts with ='cry'
	 *  				S: Class = "Fluid" and Type equals "Amniotic Fluid" and Frozen Event Parameter.Method Starts with ='dry'
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
			EntityInterface fluidSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.FLUID_SPECIMEN_NAME);
			EntityInterface tissueSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.TISSUE_SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			EntityInterface frozenEvtParamEntity = enitytManager.getEntityByName(EntityManagerMock.FROZEN_EVT_NAME);

			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);


			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);

			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
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
			IConstraintEntity tissueSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(tissueSpecimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(tissueSpecimenConstraintEntity);
			specimenCollectionGroupExpression1.addOperand(specimenExpression1
					.getExpressionId());

			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.TISSUE_SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression1.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			List<String> specimenExpression1Rule1Values = new ArrayList<String>();
			specimenExpression1Rule1Values.add("Fixed Tissue");

			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(tissueSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values);
			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			// Creating Frozen event parameter Expression
			IConstraintEntity frozenEvtEntity = QueryObjectFactory.createConstraintEntity(frozenEvtParamEntity);
			IExpression frozenEvtExpression1 = constraints.addExpression(frozenEvtEntity);
			specimenExpression1.addOperand(andConnector, frozenEvtExpression1.getExpressionId());
			AssociationInterface tissueSpecimenAndEventtAssociation =  getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.TISSUE_SPECIMEN_NAME, "specimen"),
					EntityManagerMock.FROZEN_EVT_NAME);
			IIntraModelAssociation iTissueSpecimenAndEventtAssociation = QueryObjectFactory.createIntraModelAssociation(tissueSpecimenAndEventtAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), frozenEvtExpression1
					.getExpressionId(), iTissueSpecimenAndEventtAssociation);
			
			List<String> specimenEvtExpression1Rule1Values = new ArrayList<String>();
			specimenEvtExpression1Rule1Values.add("cry");
			
			ICondition specimenEvtExpression1Rule1Condition = QueryObjectFactory.createCondition(
					findAttribute(frozenEvtParamEntity, "method"), RelationalOperator.StartsWith,
					specimenEvtExpression1Rule1Values);
			IRule specimenEvtExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenEvtExpression1Rule1.addCondition(specimenEvtExpression1Rule1Condition);
			frozenEvtExpression1.addOperand(specimenEvtExpression1Rule1);	
			
			//creating child SpecimeExpression.
			IConstraintEntity fluidSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(fluidSpecimenEntity);
			IExpression specimenExpression2 = constraints.addExpression(fluidSpecimenConstraintEntity);

			specimenExpression1.addOperand(andConnector, specimenExpression2.getExpressionId());
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.TISSUE_SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.FLUID_SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), specimenExpression2
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> specimenExpression2Rule1Values = new ArrayList<String>();
			specimenExpression2Rule1Values.add("Amniotic Fluid");

			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(fluidSpecimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values);
			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);
			specimenExpression2.addOperand(specimenExpression2Rule1);		

			// Creating Frozen event parameter Expression
			IExpression frozenEvtExpression2 = constraints.addExpression(frozenEvtEntity);
			specimenExpression2.addOperand(andConnector, frozenEvtExpression2.getExpressionId());
			AssociationInterface fluideSpecimenAndEventtAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.FLUID_SPECIMEN_NAME, "specimen"),
					EntityManagerMock.FROZEN_EVT_NAME);
			IIntraModelAssociation iFluideSpecimenAndEventtAssociation = QueryObjectFactory.createIntraModelAssociation(fluideSpecimenAndEventtAssociation);
			joinGraph.putAssociation(specimenExpression2.getExpressionId(), frozenEvtExpression2
					.getExpressionId(), iFluideSpecimenAndEventtAssociation);
			
			List<String> specimenEvtExpression2Rule1Values = new ArrayList<String>();
			specimenEvtExpression2Rule1Values.add("dry");
			
			ICondition specimenEvtExpression2Rule1Condition = QueryObjectFactory.createCondition(
					findAttribute(frozenEvtParamEntity, "method"), RelationalOperator.StartsWith,
					specimenEvtExpression2Rule1Values);
			IRule specimenEvtExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenEvtExpression2Rule1.addCondition(specimenEvtExpression2Rule1Condition);
			frozenEvtExpression2.addOperand(specimenEvtExpression2Rule1);	
			
//			// Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//
//			query.setRootOutputClass(participantNode);
//
//			IOutputTreeNode cprNode = participantNode.addChild(iParticipanCPRegAssociation, createCollProtoRegOutputEntity(cprEntity));
//
//			IOutputTreeNode scgNode = cprNode.addChild(iCprAndSpgAssociation, createScgOutputEntity(scgEntity));
//			IOutputTreeNode  specimenNode = scgNode.addChild(iSpgAndSpecimeAssociation, createSpecimenOutputEntity(tissueSpecimenEntity));
//			specimenNode.addChild(iSpecimenAndSpecimeAssociation, createSpecimenOutputEntity(fluidSpecimenEntity));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	public static AssociationInterface getAssociationFrom(
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

	/**
	 * 
	 * @return The IQuery Object representation for the sample query no. 2 in the "caTissue Core NBN Query Results.doc"
	 * <pre>
	 * The Actual Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: quantity > 5 AND Class Equals "Molecular" AND Type equals "DNA" AND Pathological Status Equals "Malignant" AND Tissue Site Equals "PROSTATE GLAND"
	 *  			Pseudo AND
	 *  			S: quantity > 5 AND Class Equals "Molecular" AND Type equals "DNA" AND Pathological Status Equals "Non-Malignant" AND Tissue Site Equals "PROSTATE GLAND"
	 * </pre>
	 * <pre>
	 * Note:quantity condition not added.
	 * The implemented Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Class Equals "Molecular" AND Type equals "DNA" AND Pathological Status Equals "Malignant" 
	 *  				SCHAR: Tissue Site Equals "PROSTATE GLAND"
	 *  			Pseudo AND
	 *  			S: Class Equals "Molecular" AND Type equals "DNA" AND Pathological Status Equals "Non-Malignant" 
	 *  				SCHAR: Tissue Site Equals "PROSTATE GLAND"
	 * </pre>
	 */
	public static IQuery createNBNSampleQuery2()
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
			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.MOLECULAR_SPECIMEN_NAME);
			EntityInterface specimenCharacteristicEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_CHARACTERISTIC_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);

			// creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			IExpression collectionProtocolRegExpression = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(collectionProtocolRegExpression
					.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					collectionProtocolRegExpression.getExpressionId(), iParticipanCPRegAssociation);


			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
			IExpression specimenCollectionGroupExpression = constraints.addExpression(scgConstraintEntity);
			collectionProtocolRegExpression.addOperand(specimenCollectionGroupExpression.getExpressionId());
			AssociationInterface cprAndSpgAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME,
					"collectionProtocolRegistration"),
					EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);
			IIntraModelAssociation iCprAndSpgAssociation = QueryObjectFactory.createIntraModelAssociation(cprAndSpgAssociation);
			joinGraph.putAssociation(collectionProtocolRegExpression.getExpressionId(),
					specimenCollectionGroupExpression.getExpressionId(), iCprAndSpgAssociation);

			//creating first SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(specimenExpression1.getExpressionId());
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.MOLECULAR_SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			List<String> specimenExpression1Rule1Values1 = new ArrayList<String>();
			specimenExpression1Rule1Values1.add("DNA");
			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity.getParentEntity(), "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values1);

			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);

			List<String> specimenExpression1Rule1Values2 = new ArrayList<String>();
			specimenExpression1Rule1Values2.add("Malignant");

			ICondition specimenExpression1Rule1Condition2 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity.getParentEntity(), "pathologicalStatus"), RelationalOperator.Equals,
					specimenExpression1Rule1Values2);
			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition2);

			//creating specimenCharacteristic Expression under first specimen Expression.
			IConstraintEntity specimenCharacteristicConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenCharacteristicEntity);
			IExpression specimenCharacteristicExpression1 = constraints.addExpression(specimenCharacteristicConstraintEntity);

			specimenExpression1.addOperand(andConnector,specimenCharacteristicExpression1.getExpressionId());
			AssociationInterface specimeAndSpecimeCharacteristicAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.MOLECULAR_SPECIMEN_NAME,
					""), EntityManagerMock.SPECIMEN_CHARACTERISTIC_NAME);
			IIntraModelAssociation iSpecimeAndSpecimeCharacteristicAssociation = QueryObjectFactory.createIntraModelAssociation(specimeAndSpecimeCharacteristicAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(),
					specimenCharacteristicExpression1.getExpressionId(), iSpecimeAndSpecimeCharacteristicAssociation);

			IRule specimenCharacteristicExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenCharacteristicExpression1.addOperand(specimenCharacteristicExpression1Rule1);

			List<String> specimenCharacteristicExpression1Rule1Values1 = new ArrayList<String>();
			specimenCharacteristicExpression1Rule1Values1.add("Prostate Gland");
			ICondition specimenCharacteristicExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenCharacteristicEntity, "tissueSite"), RelationalOperator.Equals,
					specimenCharacteristicExpression1Rule1Values1);
			specimenCharacteristicExpression1Rule1.addCondition(specimenCharacteristicExpression1Rule1Condition1);


			// creating second SpecimeExpression.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(andConnector, specimenExpression2.getExpressionId());
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2.addOperand(specimenExpression2Rule1);

			List<String> specimenExpression2Rule1Values1 = new ArrayList<String>();
			specimenExpression2Rule1Values1.add("DNA");
			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity.getParentEntity(), "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values1);

			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);

			List<String> specimenExpression2Rule1Values2 = new ArrayList<String>();
			specimenExpression2Rule1Values2.add("Non-Malignant");

			ICondition specimenExpression2Rule1Condition2 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity.getParentEntity(), "pathologicalStatus"), RelationalOperator.Equals,
					specimenExpression2Rule1Values2);
			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition2);


			// creating specimenCharacteristic Expression under second Specimen Expression.
			IExpression specimenCharacteristicExpression2 = constraints.addExpression(specimenCharacteristicConstraintEntity);

			specimenExpression2.addOperand(andConnector,specimenCharacteristicExpression2.getExpressionId());
			joinGraph.putAssociation(specimenExpression2.getExpressionId(),
					specimenCharacteristicExpression2.getExpressionId(), iSpecimeAndSpecimeCharacteristicAssociation);

			IRule specimenCharacteristicExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenCharacteristicExpression2.addOperand(specimenCharacteristicExpression2Rule1);

			List<String> specimenCharacteristicExpression2Rule1Values1 = new ArrayList<String>();
			specimenCharacteristicExpression2Rule1Values1.add("Prostate Gland");
			ICondition specimenCharacteristicExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenCharacteristicEntity, "tissueSite"), RelationalOperator.Equals,
					specimenCharacteristicExpression2Rule1Values1);
			specimenCharacteristicExpression2Rule1.addCondition(specimenCharacteristicExpression2Rule1Condition1);

//			//Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode participantNode = QueryObjectFactory.createOutputTreeNode(createParticipantOutputEntity(participantEntity));
//
//			query.setRootOutputClass(participantNode);
//
//			IOutputTreeNode cprNode = participantNode.addChild(iParticipanCPRegAssociation, createCollProtoRegOutputEntity(cprEntity));
//
//			IOutputTreeNode scgNode = cprNode.addChild(iCprAndSpgAssociation, createScgOutputEntity(scgEntity));
//			IOutputTreeNode  specimenNode = scgNode.addChild(iSpgAndSpecimeAssociation, createSpecimenOutputEntity(specimenEntity));
//			specimenNode.addChild(iSpecimeAndSpecimeCharacteristicAssociation, createSpecimenCharacteristicOutputEntity(specimenCharacteristicEntity));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * Creates Query for Multiple parent case type.
	 * <pre>
	 * 			/-- S: type equals 'DNA'--\
	 *    SCG:ANY						   |-- S: type equals 'Amniotic Fluid'
	 *    		\-- S: type equals 'RNA'--/
	 * </pre>
	 * @return reference to the query object.
	 */
	public static IQuery createMultipleParentQuery1()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			ILogicalConnector orConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.Or);

			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
			IExpression specimenCollectionGroupExpression = constraints.addExpression(scgConstraintEntity);

			// creating first SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(specimenExpression1.getExpressionId());
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			List<String> specimenExpression1Rule1Values1 = new ArrayList<String>();
			specimenExpression1Rule1Values1.add("DNA");
			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values1);

			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);


			// creating second SpecimeExpression.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(orConnector, specimenExpression2.getExpressionId());
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2.addOperand(specimenExpression2Rule1);

			List<String> specimenExpression2Rule1Values1 = new ArrayList<String>();
			specimenExpression2Rule1Values1.add("RNA");
			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values1);

			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);

			// creating child SpecimeExpression & adding it under specimenExpression1.
			IExpression childSpecimenExpression = constraints.addExpression(specimenConstraintEntity);

			specimenExpression1.addOperand(orConnector, childSpecimenExpression.getExpressionId());
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), childSpecimenExpression
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> childSpecimenExpressionRule1Values = new ArrayList<String>();
			childSpecimenExpressionRule1Values.add("Amniotic Fluid");

			ICondition childSpecimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					childSpecimenExpressionRule1Values);
			IRule childSpecimenExpressionRule1 = QueryObjectFactory.createRule(null);
			childSpecimenExpressionRule1.addCondition(childSpecimenExpressionRule1Condition1);
			childSpecimenExpression.addOperand(childSpecimenExpressionRule1);		


			// Adding same Expression under specimenExpression2.
			specimenExpression2.addOperand(orConnector,childSpecimenExpression.getExpressionId());
			joinGraph.putAssociation(specimenExpression2.getExpressionId(), childSpecimenExpression.getExpressionId(), iSpecimenAndSpecimeAssociation);

//			//Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode specimenNode = QueryObjectFactory.createOutputTreeNode(createSpecimenOutputEntity(specimenEntity));
//
//			query.setRootOutputClass(specimenNode);
//
//			specimenNode.addChild(iSpecimenAndSpecimeAssociation, createSpecimenOutputEntity(specimenEntity));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * Creates Query for Multiple parent case type.
	 * <pre>
	 * 			/-- S: type equals 'DNA'--\
	 *    SCG:ANY						   |-- S: type equals 'Amniotic Fluid'
	 *    		\-- S: type equals 'RNA'--/            \
	 *    							\                   \
	 *    							 \---------------- S: type equals 'milk'
	 * </pre>
	 * @return reference to the query object.
	 */

	public static IQuery createMultipleParentQuery2()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();
			ILogicalConnector orConnector = QueryObjectFactory
			.createLogicalConnector(LogicalOperator.Or);

			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
			IExpression specimenCollectionGroupExpression = constraints.addExpression(scgConstraintEntity);

			// creating first SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(specimenExpression1.getExpressionId());
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			List<String> specimenExpression1Rule1Values1 = new ArrayList<String>();
			specimenExpression1Rule1Values1.add("DNA");
			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values1);

			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);


			// creating second SpecimeExpression.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(orConnector, specimenExpression2.getExpressionId());
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2.addOperand(specimenExpression2Rule1);

			List<String> specimenExpression2Rule1Values1 = new ArrayList<String>();
			specimenExpression2Rule1Values1.add("RNA");
			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values1);

			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);

			// creating child SpecimeExpression & adding it under specimenExpression1.
			IExpression childSpecimenExpression = constraints.addExpression(specimenConstraintEntity);

			specimenExpression1.addOperand(orConnector, childSpecimenExpression.getExpressionId());
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), childSpecimenExpression
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> childSpecimenExpressionRule1Values = new ArrayList<String>();
			childSpecimenExpressionRule1Values.add("Amniotic Fluid");

			ICondition childSpecimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					childSpecimenExpressionRule1Values);
			IRule childSpecimenExpressionRule1 = QueryObjectFactory.createRule(null);
			childSpecimenExpressionRule1.addCondition(childSpecimenExpressionRule1Condition1);
			childSpecimenExpression.addOperand(childSpecimenExpressionRule1);		


			// Adding same Expression under specimenExpression2.
			specimenExpression2.addOperand(orConnector,childSpecimenExpression.getExpressionId());
			joinGraph.putAssociation(specimenExpression2.getExpressionId(), childSpecimenExpression.getExpressionId(), iSpecimenAndSpecimeAssociation);


			// creating Grand child SpecimeExpression.
			IExpression grandChildSpecimenExpression = constraints.addExpression(specimenConstraintEntity);
			specimenExpression1.addOperand(orConnector, grandChildSpecimenExpression.getExpressionId());
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), grandChildSpecimenExpression
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> grandChildSpecimenExpressionRule1Values = new ArrayList<String>();
			grandChildSpecimenExpressionRule1Values.add("Milk");

			ICondition grandChildSpecimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					grandChildSpecimenExpressionRule1Values);
			IRule grandChildSpecimenExpressionRule1 = QueryObjectFactory.createRule(null);
			grandChildSpecimenExpressionRule1.addCondition(grandChildSpecimenExpressionRule1Condition1);
			grandChildSpecimenExpression.addOperand(grandChildSpecimenExpressionRule1);

//			Adding grand child Expression under childSpecimenExpression.
			childSpecimenExpression.addOperand(orConnector,grandChildSpecimenExpression.getExpressionId());
			joinGraph.putAssociation(childSpecimenExpression.getExpressionId(), grandChildSpecimenExpression.getExpressionId(), iSpecimenAndSpecimeAssociation);

//			//Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode specimenNode = QueryObjectFactory.createOutputTreeNode(createSpecimenOutputEntity(specimenEntity));
//
//			query.setRootOutputClass(specimenNode);
//
//			specimenNode.addChild(iSpecimenAndSpecimeAssociation, createSpecimenOutputEntity(specimenEntity));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * Creates Query for Multiple parent case type, with the nesting numbers.
	 * <pre>
	 * 			/-- S: type equals ('DNA' Or 'cDNA')------------\
	 *    SCG:ANY						  						 |-- S: type equals 'Amniotic Fluid' ---> S: type equals 'Milk'
	 *    		\-- S: type equals 'RNA' Or (type equals 'RNA, cytoplasmic' or ChildExp)--/
	 * </pre>
	 * @return reference to the query object.
	 */
	public static IQuery createMultipleParentQuery3()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();

			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface scgEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME);


			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity scgConstraintEntity = QueryObjectFactory.createConstraintEntity(scgEntity);
			IExpression specimenCollectionGroupExpression = constraints.addExpression(scgConstraintEntity);

			// creating first SpecimeExpression.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
			IExpression specimenExpression1 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(specimenExpression1.getExpressionId());
			AssociationInterface spgAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_COLLECTION_GROUP_NAME,
					"specimenCollectionGroup"), EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpgAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(spgAndSpecimeAssociation);
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression1.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression1Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression1.addOperand(specimenExpression1Rule1);

			List<String> specimenExpression1Rule1Values1 = new ArrayList<String>();
			specimenExpression1Rule1Values1.add("DNA");
			ICondition specimenExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule1Values1);

			specimenExpression1Rule1.addCondition(specimenExpression1Rule1Condition1);

			IRule specimenExpression1Rule2 = QueryObjectFactory.createRule(null);

			specimenExpression1.addOperand(getOrConnector(),specimenExpression1Rule2);
			specimenExpression1.addParantheses(0, 1);

			List<String> specimenExpression1Rule2Values1 = new ArrayList<String>();
			specimenExpression1Rule2Values1.add("cDNA");
			ICondition specimenExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression1Rule2Values1);

			specimenExpression1Rule2.addCondition(specimenExpression1Rule2Condition1);

			// creating second SpecimeExpression.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);

			specimenCollectionGroupExpression.addOperand(getOrConnector(), specimenExpression2.getExpressionId());
			joinGraph.putAssociation(specimenCollectionGroupExpression.getExpressionId(),
					specimenExpression2.getExpressionId(), iSpgAndSpecimeAssociation);

			IRule specimenExpression2Rule1 = QueryObjectFactory.createRule(null);
			specimenExpression2.addOperand(specimenExpression2Rule1);

			List<String> specimenExpression2Rule1Values1 = new ArrayList<String>();
			specimenExpression2Rule1Values1.add("RNA");
			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values1);

			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);

			IRule specimenExpression2Rule2 = QueryObjectFactory.createRule(null);
			specimenExpression2.addOperand(getOrConnector(),specimenExpression2Rule2);

			List<String> specimenExpression2Rule2Values1 = new ArrayList<String>();
			specimenExpression2Rule2Values1.add("RNA, cytoplasmic");
			ICondition specimenExpression2Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule2Values1);

			specimenExpression2Rule2.addCondition(specimenExpression2Rule2Condition1);

			// creating child SpecimeExpression & adding it under specimenExpression1.
			IExpression childSpecimenExpression = constraints.addExpression(specimenConstraintEntity);

			specimenExpression1.addOperand(getOrConnector(), childSpecimenExpression.getExpressionId());
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "childrenSpecimen"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation iSpecimenAndSpecimeAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(specimenExpression1.getExpressionId(), childSpecimenExpression
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> childSpecimenExpressionRule1Values = new ArrayList<String>();
			childSpecimenExpressionRule1Values.add("Amniotic Fluid");

			ICondition childSpecimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					childSpecimenExpressionRule1Values);
			IRule childSpecimenExpressionRule1 = QueryObjectFactory.createRule(null);
			childSpecimenExpressionRule1.addCondition(childSpecimenExpressionRule1Condition1);
			childSpecimenExpression.addOperand(childSpecimenExpressionRule1);		


			// creating Grand child SpecimeExpression.
			IExpression grandChildSpecimenExpression = constraints.addExpression(specimenConstraintEntity);
			childSpecimenExpression.addOperand(getAndConnector(), grandChildSpecimenExpression.getExpressionId());
			joinGraph.putAssociation(childSpecimenExpression.getExpressionId(), grandChildSpecimenExpression
					.getExpressionId(), iSpecimenAndSpecimeAssociation);

			List<String> grandChildSpecimenExpressionRule1Values = new ArrayList<String>();
			grandChildSpecimenExpressionRule1Values.add("Milk");

			ICondition grandChildSpecimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					grandChildSpecimenExpressionRule1Values);
			IRule grandChildSpecimenExpressionRule1 = QueryObjectFactory.createRule(null);
			grandChildSpecimenExpressionRule1.addCondition(grandChildSpecimenExpressionRule1Condition1);
			grandChildSpecimenExpression.addOperand(grandChildSpecimenExpressionRule1);

			// Adding same Expression under specimenExpression2.
			specimenExpression2.addOperand(getOrConnector(),childSpecimenExpression.getExpressionId());
			specimenExpression2.addParantheses(1,2);
			joinGraph.putAssociation(specimenExpression2.getExpressionId(), childSpecimenExpression.getExpressionId(), iSpecimenAndSpecimeAssociation);

//			//Creating output Tree.
			setAllExpressionInView(constraints);
//			IOutputTreeNode specimenNode = QueryObjectFactory.createOutputTreeNode(createSpecimenOutputEntity(specimenEntity));
//
//			query.setRootOutputClass(specimenNode);
//
//			specimenNode.addChild(iSpecimenAndSpecimeAssociation, createSpecimenOutputEntity(specimenEntity));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * TO create query for the TABLE_PER_SUB_CLASS inheritance strategy.
	 * It will create Query for Collection protocol class as: cp.aliquotInSameContainer = 'True' and cp.activityStatus = 'Active'
	 * Here,
	 * 1. aliquotInSameContainer attribute is in the derived class i.e. Collection Protocol.
	 * 2. activityStatus attribute is in the base class of Collection Protocol i.e. SpecimenProtocol
	 * @return The reference to Query object.
	 */
	public static IQuery createInheritanceQuery1()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			EntityInterface collectionProtocolEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_NAME);
			
			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity cpConstraintEntity = QueryObjectFactory.createConstraintEntity(collectionProtocolEntity);
			IExpression cpExpression = constraints.addExpression(cpConstraintEntity);

			
			List<String> cpExpressionRule1Values1 = new ArrayList<String>();
			cpExpressionRule1Values1.add("TRUE");

			ICondition cpExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(collectionProtocolEntity, "aliquotInSameContainer"), RelationalOperator.Equals,
					cpExpressionRule1Values1);
			IRule cpExpressionRule1 = QueryObjectFactory.createRule(null);
			cpExpressionRule1.addCondition(cpExpressionRule1Condition1);
			
			List<String> cpExpressionRule1Values2 = new ArrayList<String>();
			cpExpressionRule1Values2.add("Active");
			ICondition cpExpressionRule1Condition2 = QueryObjectFactory.createCondition(
					findAttribute(collectionProtocolEntity.getParentEntity(), "activityStatus"), RelationalOperator.Equals,
					cpExpressionRule1Values2);
			cpExpressionRule1.addCondition(cpExpressionRule1Condition2);
			cpExpression.addOperand(cpExpressionRule1);
			
			setAllExpressionInView(constraints);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	
	/**
	 * TO create query for the TABLE_PER_HEIRARCHY inheritance strategy.
	 * It will create Query for Molecular Specimen class as: sp.label contains "1.2"
	 * @return The reference to Query object.
	 */
	public static IQuery createInheritanceQuery2()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			EntityInterface molecularSpecimenEntity = enitytManager.getEntityByName(EntityManagerMock.MOLECULAR_SPECIMEN_NAME);
			
			// creating expression for SpecimenCollectionGroup.
			IConstraintEntity molecularSpecimenConstraintEntity = QueryObjectFactory.createConstraintEntity(molecularSpecimenEntity);
			IExpression molecularSpecimenExpression = constraints.addExpression(molecularSpecimenConstraintEntity);

			
			List<String> rule1Values1 = new ArrayList<String>();
			rule1Values1.add("1.2");

			ICondition rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(molecularSpecimenEntity.getParentEntity(), "label"), RelationalOperator.Contains,
					rule1Values1);
			IRule rule1 = QueryObjectFactory.createRule(null);
			rule1.addCondition(rule1Condition1);
			molecularSpecimenExpression.addOperand(rule1);
			
			setAllExpressionInView(constraints);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * TO create query for the TABLE_PER_HEIRARCHY inheritance strategy with multilevel inheritance.
	 * It will create Query for Cell Specimen Review event parameter class as: srp.viableCellPercentage > 50 and srp.comments contains 'xyz'
	 * Here
	 * viableCellPercentage: belongs to the base class
	 * comments: belongs to the super class. 
	 * @return The reference to Query object.
	 */
	public static IQuery createInheritanceQuery3()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			EntityInterface cellSpecimenReviewParamEntity = enitytManager.getEntityByName(EntityManagerMock.CELL_SPE_REVIEW_EVT_NAME);
			
			// creating expression for cellSpecimenReviewParam.
			IConstraintEntity cellSpecimenReviewParamConstraintEntity = QueryObjectFactory.createConstraintEntity(cellSpecimenReviewParamEntity);
			IExpression expression = constraints.addExpression(cellSpecimenReviewParamConstraintEntity);

			
			List<String> rule1Values1 = new ArrayList<String>();
			rule1Values1.add("50");

			//Adding condition on derived class.
			ICondition rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(cellSpecimenReviewParamEntity, "viableCellPercentage"), RelationalOperator.GreaterThan,
					rule1Values1);
			IRule rule1 = QueryObjectFactory.createRule(null);
			rule1.addCondition(rule1Condition1);

			// Adding condition on the super class.
			List<String> rule1Values2 = new ArrayList<String>();
			rule1Values2.add("xyz");
			ICondition rule1Condition2 = QueryObjectFactory.createCondition(
					findAttribute(cellSpecimenReviewParamEntity.getParentEntity().getParentEntity(), "comments"), RelationalOperator.Contains,
					rule1Values2);

			rule1.addCondition(rule1Condition2);

			expression.addOperand(rule1);
			
			setAllExpressionInView(constraints);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * To instantiate Logical connector for OR operator.
	 * @return reference to logical connector contining 'OR' logical operator.
	 */
	private static ILogicalConnector getOrConnector()
	{
		return QueryObjectFactory
		.createLogicalConnector(LogicalOperator.Or);
	}

	/**
	 * To instantiate Logical connector for AND operator.
	 * @return reference to logical connector contining 'AND' logical operator.
	 */
	private static ILogicalConnector getAndConnector()
	{
		return QueryObjectFactory
		.createLogicalConnector(LogicalOperator.And);
	}
	
	/**
	 * Create Participan Expression with Empty Expressions as:
	 * <pre>
	 * 	P: firstName equals "Prafull"
	 * 		CPR: activityStatus equals "Active"
	 * 		PM: ANY
	 * 		PM: ANY
	 * 		CPR: activityStatus equals "Disabled"
	 * 		PM: ANY
	 * </pre>
	 * Expression as : (P AND CPR1 AND PM1 AND PM2 AND CPR2 AND PM3)
	 * @return
	 */
	public static IQuery createQueryWithEmptyExp()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);

			IJoinGraph joinGraph = constraints.getJoinGraph();

			EntityInterface participantEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			EntityInterface cprEntity = enitytManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			EntityInterface pmEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);

			// creating expression for Participant.
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression participantExpression = constraints.addExpression(participantConstraintEntity);
			
			List<String> participantExpression1Rule2Values = new ArrayList<String>();
			participantExpression1Rule2Values.add("Prafull");
			ICondition participantExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(participantEntity, "firstName"), RelationalOperator.Equals,
					participantExpression1Rule2Values);
			IRule participantExpression1Rule2 = QueryObjectFactory.createRule(null);
			participantExpression1Rule2.addCondition(participantExpression1Rule2Condition1);
			participantExpression.addOperand(participantExpression1Rule2);
			
			
			//  creating expression for collection Protocol Registration
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			IExpression cprExpression1 = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(getAndConnector(),cprExpression1.getExpressionId());

			AssociationInterface participanCPRegAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			IIntraModelAssociation iParticipanCPRegAssociation = QueryObjectFactory.createIntraModelAssociation(participanCPRegAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					cprExpression1.getExpressionId(), iParticipanCPRegAssociation);
			
			List<String> cprExpression1Rule2Values = new ArrayList<String>();
			cprExpression1Rule2Values.add("Active");
			ICondition cprExpression1Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(cprEntity, "activityStatus"), RelationalOperator.Equals,
					cprExpression1Rule2Values);
			IRule cprExpression1Rule2 = QueryObjectFactory.createRule(null);
			cprExpression1Rule2.addCondition(cprExpression1Rule2Condition1);
			cprExpression1.addOperand(cprExpression1Rule2);
			
			// creating empty expression for Participant Medical Id.
			IConstraintEntity pmConstraintEntity = QueryObjectFactory.createConstraintEntity(pmEntity);
			IExpression pmExpression1 = constraints.addExpression(pmConstraintEntity);
			participantExpression.addOperand(getAndConnector(), pmExpression1.getExpressionId());
			
			AssociationInterface participanPMAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IIntraModelAssociation iParticipanPMAssociation = QueryObjectFactory.createIntraModelAssociation(participanPMAssociation);

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					pmExpression1.getExpressionId(), iParticipanPMAssociation);
			
			// creating empty expression for Participant Medical Id.
			IExpression pmExpression2 = constraints.addExpression(pmConstraintEntity);
			participantExpression.addOperand(getAndConnector(), pmExpression2.getExpressionId());

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					pmExpression2.getExpressionId(), iParticipanPMAssociation);
			
			// creating expression for collection Protocol Registration
			IExpression cprExpression2 = constraints.addExpression(cprConstraintEntity);
			participantExpression.addOperand(getAndConnector(),cprExpression2.getExpressionId());

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					cprExpression2.getExpressionId(), iParticipanCPRegAssociation);
			
			List<String> cprExpression2Rule2Values = new ArrayList<String>();
			cprExpression2Rule2Values.add("Disabled");
			ICondition cprExpression2Rule2Condition1 = QueryObjectFactory.createCondition(
					findAttribute(cprEntity, "activityStatus"), RelationalOperator.Equals,
					cprExpression2Rule2Values);
			IRule cprExpression2Rule2 = QueryObjectFactory.createRule(null);
			cprExpression2Rule2.addCondition(cprExpression2Rule2Condition1);
			cprExpression2.addOperand(cprExpression2Rule2);
			
			// creating empty expression for Participant Medical Id.
			IExpression pmExpression3 = constraints.addExpression(pmConstraintEntity);
			participantExpression.addOperand(getAndConnector(), pmExpression3.getExpressionId());

			joinGraph.putAssociation(participantExpression.getExpressionId(),
					pmExpression3.getExpressionId(), iParticipanPMAssociation);
			
			setAllExpressionInView(constraints);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createQueryWithEmptyExp()
	 * Expression as : (P AND CPR1 AND PM1) AND PM2 AND CPR2 AND PM3
	 * @return reference to the Query object.
	 */
	public static IQuery createQueryWithEmptyExpWithParenthesis1()
	{
		IQuery query = null;

		try
		{
			query = createQueryWithEmptyExp();
			IConstraints constraints = query.getConstraints();
			IExpression participantExpression = constraints.getExpression(constraints.getRootExpressionId());
			participantExpression.addParantheses(0,2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createQueryWithEmptyExp()
	 * Expression as : ((P AND CPR1) AND PM1 AND PM2 AND CPR2) AND PM3
	 * @return reference to the Query object.
	 */
	public static IQuery createQueryWithEmptyExpWithParenthesis2()
	{
		IQuery query = null;

		try
		{
			query = createQueryWithEmptyExp();
			IConstraints constraints = query.getConstraints();
			IExpression participantExpression = constraints.getExpression(constraints.getRootExpressionId());
			participantExpression.addParantheses(0,1);
			participantExpression.addParantheses(0,4);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createQueryWithEmptyExp()
	 * Expression as : (((P AND CPR1 AND PM1 AND PM2) AND CPR2) AND PM3)
	 * @return reference to the Query object.
	 */
	public static IQuery createQueryWithEmptyExpWithParenthesis3()
	{
		IQuery query = null;

		try
		{
			query = createQueryWithEmptyExp();
			IConstraints constraints = query.getConstraints();
			IExpression participantExpression = constraints.getExpression(constraints.getRootExpressionId());
			participantExpression.addParantheses(0,3);
			participantExpression.addParantheses(0,4);
			participantExpression.addParantheses(0,5);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}

	/**
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createQueryWithEmptyExp()
	 * Expression as : P AND (CPR1 AND PM1 AND PM2 AND CPR2) AND PM3
	 * @return reference to the Query object.
	 */
	public static IQuery createQueryWithEmptyExpWithParenthesis4()
	{
		IQuery query = null;

		try
		{
			query = createQueryWithEmptyExp();
			IConstraints constraints = query.getConstraints();
			IExpression participantExpression = constraints.getExpression(constraints.getRootExpressionId());
			participantExpression.addParantheses(1,4);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createQueryWithEmptyExp()
	 * Expression as :  P AND CPR1 AND (PM1 AND PM2) AND CPR2 AND PM3
	 * @return reference to the Query object.
	 */
	public static IQuery createQueryWithEmptyExpWithParenthesis5()
	{
		IQuery query = null;

		try
		{
			query = createQueryWithEmptyExp();
			IConstraints constraints = query.getConstraints();
			IExpression participantExpression = constraints.getExpression(constraints.getRootExpressionId());
			participantExpression.addParantheses(2,3);
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
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createSampleQuery1()
	 * Defining Result view as:
	 * <pre>
	 * 		P:
	 * 			SCG:  	
	 * </pre>
	 * @return The IQuery Object representation for the sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc" with few empty expressions.
	 */
	public static IQuery createSampleQuery1WithSelectView1()
	{
		IQuery query = null;

		try
		{
			query = createSampleQuery1(); 
			IConstraints constraints = query.getConstraints();
			IExpressionId rootExpId = constraints.getRootExpressionId();
			
			IExpression participantExpression = constraints.getExpression(rootExpId);
			IExpression collectionProtocolRegExpression = constraints.getExpression((IExpressionId)participantExpression.getOperand(1));
			IExpression specimenCollectionGroupExpression = constraints.getExpression((IExpressionId)collectionProtocolRegExpression.getOperand(0));
			IExpression specimenExpression1 = constraints.getExpression((IExpressionId)specimenCollectionGroupExpression.getOperand(0));
			IExpression specimenExpression2 = constraints.getExpression((IExpressionId)specimenCollectionGroupExpression.getOperand(1));

			// View Part
			participantExpression.setInView(true);
			collectionProtocolRegExpression.setInView(false);
			specimenCollectionGroupExpression.setInView(true);
			specimenExpression1.setInView(false);
			specimenExpression2.setInView(false);
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
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createSampleQuery1()
	 * Defining Result view as:
	 * <pre>
	 * 		S:  	
	 * </pre>
	 * @return The IQuery Object representation for the sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc" with few empty expressions.
	 */
	public static IQuery createSampleQuery1WithSelectView2()
	{
		IQuery query = null;

		try
		{
			query = createSampleQuery1(); 
			IConstraints constraints = query.getConstraints();
			IExpressionId rootExpId = constraints.getRootExpressionId();
			
			IExpression participantExpression = constraints.getExpression(rootExpId);
			IExpression collectionProtocolRegExpression = constraints.getExpression((IExpressionId)participantExpression.getOperand(1));
			IExpression specimenCollectionGroupExpression = constraints.getExpression((IExpressionId)collectionProtocolRegExpression.getOperand(0));
			IExpression specimenExpression1 = constraints.getExpression((IExpressionId)specimenCollectionGroupExpression.getOperand(0));
			IExpression specimenExpression2 = constraints.getExpression((IExpressionId)specimenCollectionGroupExpression.getOperand(1));

			// View Part
			participantExpression.setInView(false);
			collectionProtocolRegExpression.setInView(false);
			specimenCollectionGroupExpression.setInView(false);
			specimenExpression1.setInView(true);
			specimenExpression2.setInView(true);
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
	 * @see edu.wustl.common.querysuite.QueryGeneratorMock#createSampleQuery1()
	 * Adding Expression participant medical identifier under Participant node. The final Query is:
	 * <pre>
	 * 	P: LastNameStarts with 'S'<P>
	 * 		PM: medicalRecordNumber equals 'M001'
	 * 		AND
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Class equals "Tissue" AND Type equals "Fixed Tissue"
	 *  					OR
	 *  			S: Class equals "Tissue" AND Type equals "Fresh Tissue" 
	 * </pre>
	 * Setting PM, G & S node in output tree, Resulting into 2 output trees which are"
	 * <pre>
	 * 1. First tree:
	 * 		G:
	 * 			S:  	
	 * 2. Second tree:
	 * 		PM
	 * </pre>
	 * @return The IQuery Object representation for the sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc" with few empty expressions.
	 */
	public static IQuery createSampleQuery1WithSelectView3()
	{
		IQuery query = null;

		try
		{
			query = createSampleQuery1(); 
			IConstraints constraints = query.getConstraints();
			IExpressionId rootExpId = constraints.getRootExpressionId();
			IJoinGraph joinGraph = constraints.getJoinGraph();
			
			IExpression participantExpression = constraints.getExpression(rootExpId);
			IExpression collectionProtocolRegExpression = constraints.getExpression((IExpressionId)participantExpression.getOperand(1));
			IExpression specimenCollectionGroupExpression = constraints.getExpression((IExpressionId)collectionProtocolRegExpression.getOperand(0));
			IExpression specimenExpression1 = constraints.getExpression((IExpressionId)specimenCollectionGroupExpression.getOperand(0));
			IExpression specimenExpression2 = constraints.getExpression((IExpressionId)specimenCollectionGroupExpression.getOperand(1));

			
			// creating Participant medical Id Expression.
			EntityInterface pmIdEntity = enitytManager.getEntityByName(EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IConstraintEntity pmIdConstraintEntity = QueryObjectFactory.createConstraintEntity(pmIdEntity);
			IExpression pmExpression = constraints.addExpression(pmIdConstraintEntity);
			participantExpression.addOperand(getAndConnector(), pmExpression.getExpressionId());
			pmExpression.addOperand(createParticipantMedicalIdentifierRule1(pmIdEntity, pmExpression));

			// Adding association to joingraph.
			AssociationInterface specimenAndSpecimeAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.PARTICIPANT_NAME, "participant"),
					EntityManagerMock.PARTICIPANT_MEDICAL_ID_NAME);
			IIntraModelAssociation association = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimeAssociation);
			joinGraph.putAssociation(participantExpression.getExpressionId(), pmExpression
					.getExpressionId(), association);
			
			// View Part
			participantExpression.setInView(false);
			collectionProtocolRegExpression.setInView(false);
			specimenCollectionGroupExpression.setInView(true);
			specimenExpression1.setInView(true);
			specimenExpression2.setInView(true);
			pmExpression.setInView(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * To create Query for Many to many Association case.
	 * <pre>
	 *  S: Type equals "DNA"
	 *  	Biohazard: type equals "Toxic"
	 * </pre>
	 * @return reference to query object.
	 */
	public static IQuery createSpecimenBioHazardQuery1()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);
			IJoinGraph joinGraph = constraints.getJoinGraph();

			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface biohazardEntity = enitytManager.getEntityByName(EntityManagerMock.BIOHAZARD_NAME);


			// creating expression for specimen.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
			IExpression specimenExpression = constraints.addExpression(specimenConstraintEntity);
			
			List<String> specimenExpressionRule1Values = new ArrayList<String>();
			specimenExpressionRule1Values.add("DNA");

			ICondition specimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpressionRule1Values);
			IRule specimenExpressionRule1 = QueryObjectFactory
			.createRule(null);
			specimenExpressionRule1.addCondition(specimenExpressionRule1Condition1);
			specimenExpression.addOperand(specimenExpressionRule1);
			
			// Creating Biohazard Expression.
			IConstraintEntity biohazardConstraintEntity = QueryObjectFactory.createConstraintEntity(biohazardEntity);
			IExpression biohazardExpression = constraints.addExpression(biohazardConstraintEntity);
			specimenExpression.addOperand(getAndConnector(),biohazardExpression.getExpressionId());
			
			// Adding association to joingraph.
			AssociationInterface specimenAndBiohazardAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "specimenCollection"),
					EntityManagerMock.BIOHAZARD_NAME);
			IIntraModelAssociation association = QueryObjectFactory.createIntraModelAssociation(specimenAndBiohazardAssociation);
			joinGraph.putAssociation(specimenExpression.getExpressionId(), biohazardExpression
					.getExpressionId(), association);
			
			
			List<String> biohazardExpressionRule1Values = new ArrayList<String>();
			biohazardExpressionRule1Values.add("Toxic");

			ICondition biohazardExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(biohazardEntity, "type"), RelationalOperator.Equals,
					biohazardExpressionRule1Values);
			IRule biohazardExpressionRule1 = QueryObjectFactory
			.createRule(null);
			biohazardExpressionRule1.addCondition(biohazardExpressionRule1Condition1);
			biohazardExpression.addOperand(biohazardExpressionRule1);
			
			setAllExpressionInView(constraints);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return query;
		
	}
	
	/**
	 * To create Query for Many to many Association & Pseudo And.
	 * <pre>
	 *  S: Type equals "DNA"
	 *  	Biohazard: type equals "Toxic"
	 *  	Pseudo AND
	 *  	Biohazard: type equals "Radioactive"
	 * </pre>
	 * @return reference to query object.
	 */	
	public static IQuery createSpecimenBioHazardQuery2()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);
			IJoinGraph joinGraph = constraints.getJoinGraph();

			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface biohazardEntity = enitytManager.getEntityByName(EntityManagerMock.BIOHAZARD_NAME);


			// creating expression for specimen.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
			IExpression specimenExpression = constraints.addExpression(specimenConstraintEntity);
			
			List<String> specimenExpressionRule1Values = new ArrayList<String>();
			specimenExpressionRule1Values.add("DNA");

			ICondition specimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpressionRule1Values);
			IRule specimenExpressionRule1 = QueryObjectFactory
			.createRule(null);
			specimenExpressionRule1.addCondition(specimenExpressionRule1Condition1);
			specimenExpression.addOperand(specimenExpressionRule1);
			
			// Creating Biohazard Expression.
			IConstraintEntity biohazardConstraintEntity = QueryObjectFactory.createConstraintEntity(biohazardEntity);
			IExpression biohazardExpression1 = constraints.addExpression(biohazardConstraintEntity);
			specimenExpression.addOperand(getAndConnector(),biohazardExpression1.getExpressionId());
			
			// Adding association to joingraph.
			AssociationInterface specimenAndBiohazardAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, "specimenCollection"),
					EntityManagerMock.BIOHAZARD_NAME);
			IIntraModelAssociation association = QueryObjectFactory.createIntraModelAssociation(specimenAndBiohazardAssociation);
			joinGraph.putAssociation(specimenExpression.getExpressionId(), biohazardExpression1
					.getExpressionId(), association);
			
			
			List<String> biohazardExpression1Rule1Values = new ArrayList<String>();
			biohazardExpression1Rule1Values.add("Toxic");

			ICondition biohazardExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(biohazardEntity, "type"), RelationalOperator.Equals,
					biohazardExpression1Rule1Values);
			IRule biohazardExpression1Rule1 = QueryObjectFactory
			.createRule(null);
			biohazardExpression1Rule1.addCondition(biohazardExpression1Rule1Condition1);
			biohazardExpression1.addOperand(biohazardExpression1Rule1);
			
			
			// Creating Biohazard Expression.
			IExpression biohazardExpression2 = constraints.addExpression(biohazardConstraintEntity);
			specimenExpression.addOperand(getAndConnector(),biohazardExpression2.getExpressionId());
			
			// Adding association to joingraph.
			joinGraph.putAssociation(specimenExpression.getExpressionId(), biohazardExpression2
					.getExpressionId(), association);
			
			
			List<String> biohazardExpression2Rule1Values = new ArrayList<String>();
			biohazardExpression2Rule1Values.add("Radioactive");

			ICondition biohazardExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(biohazardEntity, "type"), RelationalOperator.Equals,
					biohazardExpression2Rule1Values);
			IRule biohazardExpression2Rule1 = QueryObjectFactory
			.createRule(null);
			biohazardExpression2Rule1.addCondition(biohazardExpression2Rule1Condition1);
			biohazardExpression2.addOperand(biohazardExpression2Rule1);
			
			setAllExpressionInView(constraints);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return query;
	}
	
	/**
	 * To create Query for Many to many Association & Pseudo And.
	 * <pre>
	 * Biohazard: type equals "Toxic"
	 *  	S: Type equals "DNA"
	 *  		SP CHAR: tissueSite equals "skin"
	 *  	Pseudo AND
	 *  	S: Type equals "RNA"
	 *  		SP CHAR: tissueSite equals "Spinal cord"
	 *  	OR
	 *  	S: Type equals "cDNA"
	 * </pre>
	 * @return reference to query object.
	 */	
	public static IQuery createSpecimenBioHazardQuery3()
	{
		IQuery query = null;

		try
		{
			query = QueryObjectFactory.createQuery();;
			IConstraints constraints = QueryObjectFactory.createConstraints();
			query.setConstraints(constraints);
			IJoinGraph joinGraph = constraints.getJoinGraph();

			EntityInterface specimenEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_NAME);
			EntityInterface biohazardEntity = enitytManager.getEntityByName(EntityManagerMock.BIOHAZARD_NAME);
			EntityInterface specimenCharEntity = enitytManager.getEntityByName(EntityManagerMock.SPECIMEN_CHARACTERISTIC_NAME);
			
			// Creating Biohazard Expression.
			IConstraintEntity biohazardConstraintEntity = QueryObjectFactory.createConstraintEntity(biohazardEntity);
			IExpression biohazardExpression = constraints.addExpression(biohazardConstraintEntity);
			
			List<String> biohazardExpressionRule1Values = new ArrayList<String>();
			biohazardExpressionRule1Values.add("Toxic");

			ICondition biohazardExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(biohazardEntity, "type"), RelationalOperator.Equals,
					biohazardExpressionRule1Values);
			IRule biohazardExpressionRule1 = QueryObjectFactory
			.createRule(null);
			biohazardExpressionRule1.addCondition(biohazardExpressionRule1Condition1);
			biohazardExpression.addOperand(biohazardExpressionRule1);
			
			// creating expression for specimen.
			IConstraintEntity specimenConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenEntity);
			IExpression specimenExpression = constraints.addExpression(specimenConstraintEntity);
			biohazardExpression.addOperand(getAndConnector(),specimenExpression.getExpressionId());
			
			// Adding association to joingraph.
			AssociationInterface biohazardAndSpecimenAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.BIOHAZARD_NAME, "biohazardCollection"),
					EntityManagerMock.SPECIMEN_NAME);
			IIntraModelAssociation IBiohazardAndSpecimenAssociation = QueryObjectFactory.createIntraModelAssociation(biohazardAndSpecimenAssociation);
			joinGraph.putAssociation(biohazardExpression.getExpressionId(),specimenExpression
					.getExpressionId(), IBiohazardAndSpecimenAssociation);
			
			List<String> specimenExpressionRule1Values = new ArrayList<String>();
			specimenExpressionRule1Values.add("DNA");

			ICondition specimenExpressionRule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpressionRule1Values);
			IRule specimenExpressionRule1 = QueryObjectFactory
			.createRule(null);
			specimenExpressionRule1.addCondition(specimenExpressionRule1Condition1);
			specimenExpression.addOperand(specimenExpressionRule1);
			
			IConstraintEntity specimenCharConstraintEntity = QueryObjectFactory.createConstraintEntity(specimenCharEntity);
//			// creating expression for Specimen Characteristics.
			IExpression specimenCharExpression1 = constraints.addExpression(specimenCharConstraintEntity);
			specimenExpression.addOperand(getAndConnector(),specimenCharExpression1.getExpressionId());
			// Adding association to joingraph.
			AssociationInterface specimenAndSpecimenCharAssociation = getAssociationFrom(enitytManager
					.getAssociation(EntityManagerMock.SPECIMEN_NAME, ""),
					EntityManagerMock.SPECIMEN_CHARACTERISTIC_NAME);
			IIntraModelAssociation ISpecimenAndSpecimenCharAssociation = QueryObjectFactory.createIntraModelAssociation(specimenAndSpecimenCharAssociation);
			joinGraph.putAssociation(specimenExpression.getExpressionId(),specimenCharExpression1
					.getExpressionId(), ISpecimenAndSpecimenCharAssociation);
			
			List<String> specimenCharExpression1Rule1Values = new ArrayList<String>();
			specimenCharExpression1Rule1Values.add("skin");

			ICondition specimenCharExpression1Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenCharEntity, "tissueSite"), RelationalOperator.Equals,
					specimenCharExpression1Rule1Values);
			IRule specimenCharExpression1Rule1 = QueryObjectFactory
			.createRule(null);
			specimenCharExpression1Rule1.addCondition(specimenCharExpression1Rule1Condition1);
			specimenCharExpression1.addOperand(specimenCharExpression1Rule1);
			
			
			// creating expression2 for specimen.
			IExpression specimenExpression2 = constraints.addExpression(specimenConstraintEntity);
			biohazardExpression.addOperand(getAndConnector(),specimenExpression2.getExpressionId());
			
			// Adding association to joingraph.
			joinGraph.putAssociation(biohazardExpression.getExpressionId(),specimenExpression2
					.getExpressionId(), IBiohazardAndSpecimenAssociation);
			
			List<String> specimenExpression2Rule1Values = new ArrayList<String>();
			specimenExpression2Rule1Values.add("RNA");

			ICondition specimenExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression2Rule1Values);
			IRule specimenExpression2Rule1 = QueryObjectFactory
			.createRule(null);
			specimenExpression2Rule1.addCondition(specimenExpression2Rule1Condition1);
			specimenExpression2.addOperand(specimenExpression2Rule1);
			
			IExpression specimenCharExpression2 = constraints.addExpression(specimenCharConstraintEntity);
			specimenExpression2.addOperand(getAndConnector(),specimenCharExpression2.getExpressionId());
			// Adding association to joingraph.
			joinGraph.putAssociation(specimenExpression2.getExpressionId(),specimenCharExpression2
					.getExpressionId(), ISpecimenAndSpecimenCharAssociation);
			
			List<String> specimenCharExpression2Rule1Values = new ArrayList<String>();
			specimenCharExpression2Rule1Values.add("Spinal cord");

			ICondition specimenCharExpression2Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenCharEntity, "tissueSite"), RelationalOperator.Equals,
					specimenCharExpression2Rule1Values);
			IRule specimenCharExpression2Rule1 = QueryObjectFactory
			.createRule(null);
			specimenCharExpression2Rule1.addCondition(specimenCharExpression2Rule1Condition1);
			specimenCharExpression2.addOperand(specimenCharExpression2Rule1);
			
			//creating expression for specimen.
			IExpression specimenExpression3 = constraints.addExpression(specimenConstraintEntity);
			biohazardExpression.addOperand(getOrConnector(),specimenExpression3.getExpressionId());
			
			// Adding association to joingraph.
			joinGraph.putAssociation(biohazardExpression.getExpressionId(),specimenExpression3
					.getExpressionId(), IBiohazardAndSpecimenAssociation);
			
			List<String> specimenExpression3Rule1Values = new ArrayList<String>();
			specimenExpression3Rule1Values.add("cDNA");

			ICondition specimenExpression3Rule1Condition1 = QueryObjectFactory.createCondition(
					findAttribute(specimenEntity, "type"), RelationalOperator.Equals,
					specimenExpression3Rule1Values);
			IRule specimenExpression3Rule1 = QueryObjectFactory
			.createRule(null);
			specimenExpression3Rule1.addCondition(specimenExpression3Rule1Condition1);
			specimenExpression3.addOperand(specimenExpression3Rule1);
			
			setAllExpressionInView(constraints);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return query;
		
	}
}
