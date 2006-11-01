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
		IQuery query = QueryGeneratorMock.creatParticipantQuery();
		IConstraints constraints = query.getConstraints();
		IExpression rootExpression;
		try
		{
			rootExpression = constraints.getExpression(constraints.getRootExpressionId());
			String SQL = generator.getSQL(rootExpression, query.getConstraints().getJoinGraph(),
					null);
			assertEquals(
					SQL,
					"Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.ACTIVITY_STATUS='Active') Or(Participant0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001'))");
			//			System.out.println("*********"+SQL);
		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection!!!", false);
		}

	}
}
