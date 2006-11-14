package edu.wustl.common.querysuite;


import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.DataType;
import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.ICondition;
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

	public IQuery createQuery1()
	{
		IQuery query = QueryObjectFactory.createQuery();
		IConstraints constraints = QueryObjectFactory.createConstraints();

		query.setConstraints(constraints);

		return query;
	}

	/**
	 * Create Participant Class: with Attributes as: id, activityStatus, birthDate
	 * @return  Participant Class.
	 */
	public static IClass createParticantClass()
	{
		List<IAttribute> attributes = new ArrayList<IAttribute>();

		IClass class1 = QueryObjectFactory.createClass("edu.wustl.catissuecore.domain.Participant", attributes, null, true);

		IAttribute att1 = QueryObjectFactory.createAttribute(DataType.String, class1,
				"activityStatus");
		attributes.add(att1);

		IAttribute att2 = QueryObjectFactory.createAttribute(DataType.Date, class1, "birthDate");
		attributes.add(att2);

		IAttribute att3 = QueryObjectFactory.createAttribute(DataType.Integer, class1, "id");
		attributes.add(att3);

		return class1;
	}

	/**
	 * Create Condition for given Participant: activityStatus = 'Active'
	 * @param participant
	 * @return The Condition object.
	 */
	public static ICondition createParticipantCondition1(IClass participant)
	{
		List<String> values = new ArrayList<String>();
		values.add("Active");
		ICondition condition = QueryObjectFactory.createCondition(participant.getAttributes()
				.get(0), RelationalOperator.Equals, values);

		return condition;
	}

	/**
	 * Cretate Condtionion for given Participant: id in (1,2,3,4)
	 * @param participant
	 * @return
	 */
	public static ICondition createParticipantCondition2(IClass participant)
	{
		List<String> values = new ArrayList<String>();
		values.add("1");
		values.add("2");
		values.add("3");
		values.add("4");
		ICondition condition = QueryObjectFactory.createCondition(participant.getAttributes()
				.get(2), RelationalOperator.In, values);
		return condition;
	}

	/**
	 * Cretate Condtionion for given Participant: birthDate between ('1-1-2000','1-2-2000')
	 * @param participant
	 * @return
	 */
	public static ICondition createParticipantCondition3(IClass participant)
	{
		List<String> values = new ArrayList<String>();
		values.add("1-1-2000");
		values.add("1-2-2000");
		ICondition condition = QueryObjectFactory.createCondition(participant.getAttributes()
				.get(1), RelationalOperator.Between, values);
		return condition;
	}

	/**
	 * Cretate Condtionion for given Participant: id < 10 
	 * @param participant
	 * @return
	 */
	public static ICondition createParticipantCondition4(IClass participant)
	{
		List<String> values = new ArrayList<String>();
		values.add("10");
		ICondition condition = QueryObjectFactory.createCondition(participant.getAttributes()
				.get(2), RelationalOperator.LessThan, values);
		return condition;
	}

	/**
	 * Create Condition for given Participant: activityStatus Contains 'Active'
	 * @param participant
	 * @return The Condition object.
	 */
	public static ICondition createParticipantCondition5(IClass participant)
	{
		List<String> values = new ArrayList<String>();
		values.add("Active");
		ICondition condition = QueryObjectFactory.createCondition(participant.getAttributes()
				.get(0), RelationalOperator.Contains, values);

		return condition;
	}

	/**
	 * Create Rule for given Participant as : id in (1,2,3,4) AND birthDate between ('1-1-2000','1-2-2000')
	 * @param participant
	 * @return The Rule Created.
	 */
	public static IRule createParticipantRule1(IClass participant)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantCondition2(participant));
		conditions.add(createParticipantCondition3(participant));
		IRule rule = QueryObjectFactory.createRule(conditions, null);
		return rule;
	}

	/**
	 * Create Rule for given Participant as : activityStatus = 'Active'
	 * @param participant
	 * @return The Rule reference.
	 */
	public static IRule createParticipantRule2(IClass participant)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantCondition1(participant));
		IRule rule = QueryObjectFactory.createRule(conditions, null);
		return rule;
	}

	/**
	 * Create Rule for given Participant as : id < 10
	 * @param participant
	 * @return The Rule reference.
	 */
	public static IRule createParticipantRule3(IClass participant)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantCondition4(participant));
		IRule rule = QueryObjectFactory.createRule(conditions, null);
		return rule;
	}

	/**
	 * Create Expression for given Participant as: [id in (1,2,3,4) AND birthDate between ('1-1-2000','1-2-2000')] OR [activityStatus = 'Active']
	 * @param participant
	 * @return The Expression reference.
	 */
	public static IExpression creatParticipantExpression1(IClass participant)
	{
		IExpression expression = new Expression(null, 1);
		expression.addOperand(createParticipantRule1(participant));
		ILogicalConnector connector = QueryObjectFactory.createLogicalConnector(LogicalOperator.Or,
				0);
		expression.addOperand(connector, createParticipantRule2(participant));

		return expression;
	}

	
	/**
	 * Create Expression for given Participant as: [Expression1] And [id < 10]
	 * Where Expression1 is : [medicalRecordNumber = 'M001']
	 * @param participant
	 * @return
	 */
	public static IExpression creatParticipantExpression2(IClass participant)
	{
		IExpression expression = new Expression(null, 1);
		expression.addOperand(createParticipantRule3(participant));
		ILogicalConnector connector = QueryObjectFactory.createLogicalConnector(
				LogicalOperator.And, 0);
		expression.addOperand(connector, (IExpressionOperand) creatParticipantMedicalIdentifierExpression(createParticantMedicalIdentifierClass()));

		return expression;
	}

	/**
	 * To create IQuery for the Participant & Participant Medical Identifier as: [activityStatus = 'Active'] OR [medicalRecordNumber = 'M001']
	 * @param expression The Expression reference created by function creatParticipantExpression2()
	 * @return The corresponding join Graph reference.
	 */
	public static IQuery creatParticipantQuery()
	{
		IClass participantClass = createParticantClass();
		IClass pmClass = createParticantMedicalIdentifierClass();
		
		IQuery query = QueryObjectFactory.createQuery();
		
		IConstraints constraints = QueryObjectFactory.createConstraints();
		query.setConstraints(constraints);
		
		IExpression participantExpression = constraints.addExpression(null);
		participantExpression.addOperand(createParticipantRule2(participantClass));
		
		IExpression pmExpression = constraints.addExpression(null);
		
		ILogicalConnector connector = QueryObjectFactory.createLogicalConnector(LogicalOperator.Or,
				0);
		participantExpression.addOperand(connector,pmExpression);
		pmExpression.addOperand(createParticipantMedicalIdentifierRule1(pmClass));
		
		IJoinGraph joinGraph = QueryObjectFactory.createJoinGraph();
		constraints.setJoinGraph(joinGraph);
		
		IAssociation association = QueryObjectFactory.createIntraModelAssociation(participantClass, pmClass, "participant", "participantMedicalIdentifierCollection", true);
		
		try
		{
			joinGraph.putAssociation(participantExpression.getExpressionId(), pmExpression.getExpressionId(),association);
		}
		catch (CyclicException e)
		{
		}
		return query;
	}

	/**
	 * Create Participant Medical Identifier Class: with Attributes as: id, medicalRecordNumber, ParticipantId
	 * @return  Participant Medical Identifier Class.
	 */
	public static IClass createParticantMedicalIdentifierClass()
	{
		List<IAttribute> attributes = new ArrayList<IAttribute>();

		IClass class1 = QueryObjectFactory.createClass("edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier", attributes, null, true);

		IAttribute att1 = QueryObjectFactory.createAttribute(DataType.Integer, class1, "id");
		attributes.add(att1);

		IAttribute att2 = QueryObjectFactory.createAttribute(DataType.String, class1,"medicalRecordNumber");
		attributes.add(att2);

		IAttribute att3 = QueryObjectFactory.createAttribute(DataType.Integer, class1, "ParticipantId");
		attributes.add(att3);

		return class1;
	}
	
	/**
	 * Create Condition for given Participant: medicalRecordNumber = 'M001'
	 * @param participantMedicalId The Iclass representing Participant Medical Identifier obje
	 * @return The Condition object reference.
	 */
	public static ICondition createParticipantMedicalIdentifierCondition1(IClass participantMedicalId)
	{
		List<String> values = new ArrayList<String>();
		values.add("M001");
		ICondition condition = QueryObjectFactory.createCondition(participantMedicalId.getAttributes()
				.get(1), RelationalOperator.Equals, values);

		return condition;
	}
	
	/**
	 * Create Rule for Participant Medical Identifier as: [medicalRecordNumber = 'M001']
	 * @param participantMedicalId The Iclass representing Participant Medical Identifier obje 
	 * @return The Rule reference.
	 */
	private static IRule createParticipantMedicalIdentifierRule1(IClass participantMedicalId)
	{
		List<ICondition> conditions = new ArrayList<ICondition>();
		conditions.add(createParticipantMedicalIdentifierCondition1(participantMedicalId));
		IRule rule = QueryObjectFactory.createRule(conditions, null);
		return rule;
	}
	
	/**
	 * Create Expression for Participant Medical Identifier as: [medicalRecordNumber = 'M001']
	 * @param participantMedicalId The Iclass representing Participant Medical Identifier object.
	 * @return The Expression reference
	 */
	public static IExpression creatParticipantMedicalIdentifierExpression(IClass participantMedicalId)
	{
		IExpression expression = new Expression(null, 2);
		IRule rule = createParticipantMedicalIdentifierRule1(participantMedicalId);
		expression.addOperand(rule);
		return expression;
	}
	
	
}
