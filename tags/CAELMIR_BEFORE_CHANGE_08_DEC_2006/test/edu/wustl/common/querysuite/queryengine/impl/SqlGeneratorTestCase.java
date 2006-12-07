/**
 * 
 */

package edu.wustl.common.querysuite.queryengine.impl;

import java.util.HashSet;

import junit.framework.TestCase;
import edu.wustl.common.querysuite.EntityManagerMock;
import edu.wustl.common.querysuite.QueryGeneratorMock;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;

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
		IExpression expression = QueryGeneratorMock.creatParticipantExpression1(class1);
		ICondition condition1;
		try
		{
			condition1 = QueryGeneratorMock.createParticipantCondition1(class1);
			assertEquals(generator.getSQL(condition1,expression), "Participant0.ACTIVITY_STATUS='Active'");

			condition1 = QueryGeneratorMock.createParticipantCondition2(class1);
			assertEquals(generator.getSQL(condition1,expression), "Participant0.IDENTIFIER in (1,2,3,4)");

			condition1 = QueryGeneratorMock.createParticipantCondition3(class1);
			assertEquals(generator.getSQL(condition1,expression),
					"(Participant0.BIRTH_DATE<='1-1-2000' And Participant0.BIRTH_DATE>='1-2-2000')");

			condition1 = QueryGeneratorMock.createParticipantCondition5(class1);
			assertEquals(generator.getSQL(condition1,expression),
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
		IExpression expression = QueryGeneratorMock.creatParticipantExpression1(class1);
		IRule rule = QueryGeneratorMock.createParticipantRule1(class1,expression);
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
			String SQL = generator.getSQL(expression, null,false);
//			System.out.println("testParticpiantExpression:"+ SQL);
			assertEquals(
					SQL,
					"Where (Participant0.IDENTIFIER in (1,2,3,4) And (Participant0.BIRTH_DATE<='1-1-2000' And Participant0.BIRTH_DATE>='1-2-2000')) Or(Participant0.ACTIVITY_STATUS='Active')");
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
			generator.setJoinGraph((JoinGraph)constraints.getJoinGraph());
			
			generator.buildQuery(query);
			String SQL = generator.getSQL(rootExpression, null, false);
//			System.out.println("*********"+SQL);
			assertEquals(
					"Incorrect SQL formed for the Root Expression !!!",
					"Where (Participant1.ACTIVITY_STATUS='Active') And(ParticipantMedicalIdentifier2.MEDICAL_RECORD_NUMBER='M001')",
					SQL
					);

//			String wherePart = generator.getWherePartSQL();
//			//			System.out.println(wherePart);
//			//			System.out.println("Where ParticipantMedicalIdentifier0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001') And Participant0.IDENTIFIER = ANY(Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.ACTIVITY_STATUS='Active') Or(Participant0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001'))) ");
//			assertEquals(
//					"Incorrect SQL formed for Where clause of the Expression !!!",
//					"Where Participant1.IDENTIFIER = ANY(Where (Participant1.ACTIVITY_STATUS='Active') And(ParticipantMedicalIdentifier2.MEDICAL_RECORD_NUMBER='M001')) And ParticipantMedicalIdentifier2.IDENTIFIER = ANY(ParticipantMedicalIdentifier2.MEDICAL_RECORD_NUMBER='M001') ",
//					wherePart);

			String selectPart = generator.getSelectPart(rootExpression);
			//			System.out.println(selectPart);
			assertEquals(
					"Incorrect SQL formed for Select clause of the Expression !!!",
					"Select Participant1.ACTIVITY_STATUS, Participant1.BIRTH_DATE, Participant1.DEATH_DATE, Participant1.ETHNICITY, Participant1.FIRST_NAME, Participant1.GENDER, Participant1.IDENTIFIER, Participant1.LAST_NAME, Participant1.MIDDLE_NAME, Participant1.GENOTYPE, Participant1.SOCIAL_SECURITY_NUMBER, Participant1.VITAL_STATUS",
					selectPart
					);

			String fromPart = generator.getFromPartSQL(rootExpression,null, new HashSet<Integer>());
//			System.out.println(fromPart);
//			System.out.println("From catissue_participant Participant0 left join catissue_part_medical_id ParticipantMedicalIdentifier0 on (Participant0.IDENTIFIER=ParticipantMedicalIdentifier0.PARTICIPANT_ID)");
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"From catissue_participant Participant1 left join catissue_part_medical_id ParticipantMedicalIdentifier2 on (Participant1.IDENTIFIER=ParticipantMedicalIdentifier2.PARTICIPANT_ID)",
					fromPart
					);


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
//			System.out.println("testParticipantQuery1:"+sql);

//			System.out.println(sql);
//			System.out.println("Select Participant0.ACTIVITY_STATUS, Participant0.BIRTH_DATE, Participant0.DEATH_DATE, Participant0.ETHNICITY, Participant0.FIRST_NAME, Participant0.GENDER, Participant0.IDENTIFIER, Participant0.LAST_NAME, Participant0.MIDDLE_NAME, Participant0.GENOTYPE, Participant0.SOCIAL_SECURITY_NUMBER, Participant0.VITAL_STATUS From catissue_participant Participant0 left join catissue_part_medical_id ParticipantMedicalIdentifier0 on (Participant0.IDENTIFIER=ParticipantMedicalIdentifier0.PARTICIPANT_ID) Where Participant0.IDENTIFIER = ANY(Select Participant0.IDENTIFIER From catissue_participant Participant0 where (Participant0.ACTIVITY_STATUS='Active') And(Participant0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001'))) And ParticipantMedicalIdentifier0.IDENTIFIER = ANY(Select ParticipantMedicalIdentifier0.PARTICIPANT_ID From catissue_part_medical_id ParticipantMedicalIdentifier0 where ParticipantMedicalIdentifier0.MEDICAL_RECORD_NUMBER='M001') ");
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant1.ACTIVITY_STATUS, Participant1.BIRTH_DATE, Participant1.DEATH_DATE, Participant1.ETHNICITY, Participant1.FIRST_NAME, Participant1.GENDER, Participant1.IDENTIFIER, Participant1.LAST_NAME, Participant1.MIDDLE_NAME, Participant1.GENOTYPE, Participant1.SOCIAL_SECURITY_NUMBER, Participant1.VITAL_STATUS From catissue_participant Participant1 left join catissue_part_medical_id ParticipantMedicalIdentifier2 on (Participant1.IDENTIFIER=ParticipantMedicalIdentifier2.PARTICIPANT_ID) Where (Participant1.ACTIVITY_STATUS='Active') And(ParticipantMedicalIdentifier2.MEDICAL_RECORD_NUMBER='M001')",
					sql
					);
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
//			System.out.println("testParticipantQuery2:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant1.ACTIVITY_STATUS, Participant1.BIRTH_DATE, Participant1.DEATH_DATE, Participant1.ETHNICITY, Participant1.FIRST_NAME, Participant1.GENDER, Participant1.IDENTIFIER, Participant1.LAST_NAME, Participant1.MIDDLE_NAME, Participant1.GENOTYPE, Participant1.SOCIAL_SECURITY_NUMBER, Participant1.VITAL_STATUS From catissue_participant Participant1 Where Participant1.ACTIVITY_STATUS='Active'",
					sql
					);
		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}

	
	public void testScgQuery1()
	{
		IQuery query = QueryGeneratorMock.createSCGQuery();
		try
		{
			String sql = generator.generateSQL(query);
//			System.out.println("---------------"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select SpecimenCollectionGroup1.IDENTIFIER, SpecimenCollectionGroup1.NAME, SpecimenCollectionGroup1.CLINICAL_DIAGNOSIS, SpecimenCollectionGroup1.CLINICAL_STATUS, SpecimenCollectionGroup1.ACTIVITY_STATUS From catissue_specimen_coll_group SpecimenCollectionGroup1 left join catissue_specimen Specimen2 on (SpecimenCollectionGroup1.IDENTIFIER=Specimen2.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen3 on (Specimen2.IDENTIFIER=Specimen3.PARENT_SPECIMEN_ID) Where (SpecimenCollectionGroup1.ACTIVITY_STATUS like 'value1%') Or(SpecimenCollectionGroup1.IDENTIFIER = ANY(Select Specimen2.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen2 left join catissue_specimen Specimen3 on (Specimen2.IDENTIFIER=Specimen3.PARENT_SPECIMEN_ID) where (Specimen2.TYPE like 'value1%') Or(Specimen3.TYPE like 'value1%'))) And(SpecimenCollectionGroup1.IDENTIFIER = ANY(Select Specimen2.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen2 where Specimen2.PATHOLOGICAL_STATUS='value1')) Or(Specimen2.PATHOLOGICAL_STATUS!='value1')",
					sql
					);
		}
		catch (Exception e)
		{
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}
	/**
	 * 
	 * To Test the SQL for sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc".
	 * <pre>
	 *  P: LastNameStarts with 'S'
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Type equals "Fixed Tissue"
	 *  					OR
	 *  			S: Type equals "Fresh Tissue" 
	 * </pre>  	
	 */	
	public void testSampleQuery1()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery1();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
//			System.out.println("testSampleQuery1:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant1.ACTIVITY_STATUS, Participant1.BIRTH_DATE, Participant1.DEATH_DATE, Participant1.ETHNICITY, Participant1.FIRST_NAME, Participant1.GENDER, Participant1.IDENTIFIER, Participant1.LAST_NAME, Participant1.MIDDLE_NAME, Participant1.GENOTYPE, Participant1.SOCIAL_SECURITY_NUMBER, Participant1.VITAL_STATUS From catissue_participant Participant1 left join catissue_coll_prot_reg CollectionProtocolRegistration2 on (Participant1.IDENTIFIER=CollectionProtocolRegistration2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup3 on (CollectionProtocolRegistration2.IDENTIFIER=SpecimenCollectionGroup3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen4 on (SpecimenCollectionGroup3.IDENTIFIER=Specimen4.SPECIMEN_COLLECTION_GROUP_ID) Where (Participant1.FIRST_NAME like 's%') And((Specimen4.TYPE='Fixed Tissue') Or(Specimen4.TYPE='Fresh Tissue'))",
					sql
					);
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}
	
	/**
	 * 
	 * To test the SQL for sample query no. 3 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
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
	public void testSampleQuery3()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery3();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
//			System.out.println("testSampleQuery3:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant1.ACTIVITY_STATUS, Participant1.BIRTH_DATE, Participant1.DEATH_DATE, Participant1.ETHNICITY, Participant1.FIRST_NAME, Participant1.GENDER, Participant1.IDENTIFIER, Participant1.LAST_NAME, Participant1.MIDDLE_NAME, Participant1.GENOTYPE, Participant1.SOCIAL_SECURITY_NUMBER, Participant1.VITAL_STATUS From catissue_participant Participant1 left join catissue_coll_prot_reg CollectionProtocolRegistration2 on (Participant1.IDENTIFIER=CollectionProtocolRegistration2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup3 on (CollectionProtocolRegistration2.IDENTIFIER=SpecimenCollectionGroup3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen4 on (SpecimenCollectionGroup3.IDENTIFIER=Specimen4.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup5 on (CollectionProtocolRegistration2.IDENTIFIER=SpecimenCollectionGroup5.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen6 on (SpecimenCollectionGroup5.IDENTIFIER=Specimen6.SPECIMEN_COLLECTION_GROUP_ID) Where (CollectionProtocolRegistration2.IDENTIFIER = ANY(Select SpecimenCollectionGroup3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup3 left join catissue_specimen Specimen4 on (SpecimenCollectionGroup3.IDENTIFIER=Specimen4.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup3.CLINICAL_DIAGNOSIS='New Diagnosis') And((Specimen4.TYPE='DNA') Or(Specimen4.TYPE='null')))) And(CollectionProtocolRegistration2.IDENTIFIER = ANY(Select SpecimenCollectionGroup5.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup5 left join catissue_specimen Specimen6 on (SpecimenCollectionGroup5.IDENTIFIER=Specimen6.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup5.CLINICAL_DIAGNOSIS='Post-Operative') And((Specimen6.TYPE='Fixed Tissue') Or(Specimen6.TYPE='Fixed Tissue'))))",
					sql
					);
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}
	/**
	 * 
	 * To test the SQL for the sample query no. 6 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Type equals "Fixed Tissue" 
	 *  				S: Type equals "Amniotic Fluid"
	 * </pre> 	
	 */
	public void testSampleQuery6()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery6();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
//			System.out.println("testSampleQuery6:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant1.ACTIVITY_STATUS, Participant1.BIRTH_DATE, Participant1.DEATH_DATE, Participant1.ETHNICITY, Participant1.FIRST_NAME, Participant1.GENDER, Participant1.IDENTIFIER, Participant1.LAST_NAME, Participant1.MIDDLE_NAME, Participant1.GENOTYPE, Participant1.SOCIAL_SECURITY_NUMBER, Participant1.VITAL_STATUS From catissue_participant Participant1 left join catissue_coll_prot_reg CollectionProtocolRegistration2 on (Participant1.IDENTIFIER=CollectionProtocolRegistration2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup3 on (CollectionProtocolRegistration2.IDENTIFIER=SpecimenCollectionGroup3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen4 on (SpecimenCollectionGroup3.IDENTIFIER=Specimen4.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen5 on (Specimen4.IDENTIFIER=Specimen5.PARENT_SPECIMEN_ID) Where (Specimen4.TYPE='Fixed Tissue') And(Specimen5.TYPE='Amniotic Fluid')",
					sql
					);
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}
}
