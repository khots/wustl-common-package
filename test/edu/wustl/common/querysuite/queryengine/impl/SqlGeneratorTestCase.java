/**
 * 
 */

package edu.wustl.common.querysuite.queryengine.impl;

import junit.framework.TestCase;
import edu.wustl.common.querysuite.EntityManagerMock;
import edu.wustl.common.querysuite.QueryGeneratorMock;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;

/**
 * TestCase class for SqlGenerator.
 * @author prafull_kadam
 *
 */
public class SqlGeneratorTestCase extends TestCase
{

	SqlGenerator generator;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		generator = new SqlGenerator(new EntityManagerMock());
		super.setUp();
	}

	/**
	 * To test the getSQL(ICondition condition) method.
	 *
	 */
	public void testParticpiantCondition()
	{
		IClass class1 = QueryGeneratorMock.createParticantClass();
		ICondition condition1;
		try
		{
			condition1 = QueryGeneratorMock.createParticipantCondition1(class1);
			assertEquals(generator.getSQL(condition1), "Participant0.ACTIVITY_STATUS='Active'");

			condition1 = QueryGeneratorMock.createParticipantCondition2(class1);
			assertEquals(generator.getSQL(condition1), "Participant0.IDENTIFIER in (1,2,3,4)");

			condition1 = QueryGeneratorMock.createParticipantCondition3(class1);
			assertEquals(generator.getSQL(condition1),
					"(Participant0.BIRTH_DATE<='1-1-2000' And Participant0.BIRTH_DATE>='1-2-2000')");

			condition1 = QueryGeneratorMock.createParticipantCondition5(class1);
			assertEquals(generator.getSQL(condition1),
					"Participant0.ACTIVITY_STATUS like '%Active%'");
		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection!!!", false);
		}
	}

	/**
	 * To test the getSQL(IRule rule) method.
	 *
	 */
	public void testParticpiantRule()
	{
		IClass class1 = QueryGeneratorMock.createParticantClass();
		IRule rule = QueryGeneratorMock.createParticipantRule1(class1);
		try
		{
			assertEquals(
					generator.getSQL(rule),
					"Participant0.IDENTIFIER in (1,2,3,4) And (Participant0.BIRTH_DATE<='1-1-2000' And Participant0.BIRTH_DATE>='1-2-2000')");
		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection!!!", false);
		}
	}

	/**
	 * To test the getSQL(IExpression, IJoinGraph, IExpression) method.
	 *
	 */
	public void testParticpiantExpression()
	{
		IClass class1 = QueryGeneratorMock.createParticantClass();
		IExpression expression = QueryGeneratorMock.creatParticipantExpression1(class1);
		try
		{
			String SQL = generator.getSQL(expression, null, null);
			//			System.out.println(SQL);
			assertEquals(
					SQL,
					"Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.IDENTIFIER in (1,2,3,4) And (Participant0.BIRTH_DATE<='1-1-2000' And Participant0.BIRTH_DATE>='1-2-2000')) Or(Participant0.ACTIVITY_STATUS='Active')");
			//			expression = QueryGeneratorMock.creatParticipantExpression2(class1);
			//			SQL = generator.getSQL(expression,null,null);
			//			System.out.println(SQL);
			//			assertEquals(SQL,"((Participant0.IDENTIFIER in (1,2,3,4) And (Participant0.BIRTH_DATE<='1-1-2000' And Participant0.BIRTH_DATE>='1-2-2000')) Or(Participant0.ACTIVITY_STATUS='Active')) And(Participant0.IDENTIFIER<10)");
		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection!!!", false);
		}
	}

	/**
	 * To test the getSQL(IExpression, IJoinGraph, IExpression) method.
	 *
	 */
	public void testParticpiantExpression1()
	{
		IQuery query = QueryGeneratorMock.creatParticipantQuery1();
		IConstraints constraints = query.getConstraints();
		IExpression rootExpression;
		try
		{
			rootExpression = constraints.getExpression(constraints.getRootExpressionId());

			String SQL = generator.getSQL(rootExpression, query.getConstraints().getJoinGraph(),
					null);
			//			System.out.println("*********"+SQL);
			assertEquals(
					"Incorrect SQL formed for the Root Expression !!!",
					SQL,
					"Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.ACTIVITY_STATUS='Active') And(Participant0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001'))");

			String wherePart = generator.getWherePartSQL();
			//			System.out.println(wherePart);
			//			System.out.println("Where ParticipantMedicalIdentifier0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001') And Participant0.IDENTIFIER = ANY(Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.ACTIVITY_STATUS='Active') Or(Participant0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001'))) ");
			assertEquals(
					"Incorrect SQL formed for Where clause of the Expression !!!",
					wherePart,
					"Where Participant0.IDENTIFIER = ANY(Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.ACTIVITY_STATUS='Active') And(Participant0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001'))) And ParticipantMedicalIdentifier0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001') ");

			String selectPart = generator.getSelectPart(rootExpression);
			//			System.out.println(selectPart);
			assertEquals(
					"Incorrect SQL formed for Select clause of the Expression !!!",
					selectPart,
					"Select Participant0.ACTIVITY_STATUS, Participant0.BIRTH_DATE, Participant0.DEATH_DATE, Participant0.ETHNICITY, Participant0.FIRST_NAME, Participant0.GENDER, Participant0.IDENTIFIER, Participant0.LAST_NAME, Participant0.MIDDLE_NAME, Participant0.GENOTYPE, Participant0.SOCIAL_SECURITY_NUMBER, Participant0.VITAL_STATUS");

			String fromPart = generator.getFromPartSQL(rootExpression);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					fromPart,
					"From catissue_participant Participant0 left join catissue_part_medical_id ParticipantMedicalIdentifier0 on (Participant0.IDENTIFIER=ParticipantMedicalIdentifier0.PARTICIPANT_ID)");

			//			System.out.println(fromPart);

		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection!!!", false);
		}
	}

	/**
	 * To test the generateSQL(IQuery) method for a Query: [Participant.activityStatus = 'Active'] AND [ParticipantMedicalIdentifier.medicalRecordNumber = 'M001'] 
	 *
	 */
	public void testParticipantQuery1()
	{
		IQuery query = QueryGeneratorMock.creatParticipantQuery1();
		try
		{
			String sql = generator.generateSQL(query);
//						System.out.println(sql);

			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					sql,
					"Select Participant0.ACTIVITY_STATUS, Participant0.BIRTH_DATE, Participant0.DEATH_DATE, Participant0.ETHNICITY, Participant0.FIRST_NAME, Participant0.GENDER, Participant0.IDENTIFIER, Participant0.LAST_NAME, Participant0.MIDDLE_NAME, Participant0.GENOTYPE, Participant0.SOCIAL_SECURITY_NUMBER, Participant0.VITAL_STATUS From catissue_participant Participant0 left join catissue_part_medical_id ParticipantMedicalIdentifier0 on (Participant0.IDENTIFIER=ParticipantMedicalIdentifier0.PARTICIPANT_ID) Where Participant0.IDENTIFIER = ANY(Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.ACTIVITY_STATUS='Active') And(Participant0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001'))) And ParticipantMedicalIdentifier0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001') ");
		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}


	/**
	 * To test the generateSQL(IQuery) method for a Query: [Participant.activityStatus = 'Active'] 
	 *
	 */
	public void testParticipantQuery2()
	{
		IQuery query = QueryGeneratorMock.creatParticipantQuery2();
		try
		{
			String sql = generator.generateSQL(query);
			//			System.out.println(sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					sql,
					"Select Participant0.ACTIVITY_STATUS, Participant0.BIRTH_DATE, Participant0.DEATH_DATE, Participant0.ETHNICITY, Participant0.FIRST_NAME, Participant0.GENDER, Participant0.IDENTIFIER, Participant0.LAST_NAME, Participant0.MIDDLE_NAME, Participant0.GENOTYPE, Participant0.SOCIAL_SECURITY_NUMBER, Participant0.VITAL_STATUS From catissue_participant Participant0 Where Participant0.IDENTIFIER = ANY(Select Participant0.IDENTIFIER From catissue_participant Participant0 where Participant0.ACTIVITY_STATUS='Active') ");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}

}
