/**
 * 
 */

package edu.wustl.common.querysuite.queryengine.impl;

import java.util.HashMap;
import java.util.HashSet;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.EntityManagerMock;
import edu.wustl.common.querysuite.QueryGeneratorMock;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraintEntity;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * TestCase class for SqlGenerator.
 * @author prafull_kadam
 *
 */
public class SqlGeneratorTestCase extends TestCase
{

	SqlGenerator generator;
	EntityManagerMock entityManager = new EntityManagerMock(); 
	
	static
	{
		Logger.configure();// To avoid null pointer Exception for code calling logger statements.
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		generator = new SqlGenerator();
		super.setUp();
		setDataBaseType(Constants.MYSQL_DATABASE);
	}

	/**
	 * To set the Database Type as MySQL or Oracle.
	 * @param databaseType Constants.ORACLE_DATABASE or Constants.MYSQL_DATABASE.
	 */
	private void setDataBaseType(String databaseType)
	{
		Variables.databaseName = databaseType;
		
		if(Variables.databaseName.equals(Constants.ORACLE_DATABASE))
        {
        	//set string/function for oracle
        	Variables.datePattern = "mm-dd-yyyy";
        	Variables.timePattern = "hh-mi-ss";
        	Variables.dateFormatFunction="TO_CHAR";
        	Variables.timeFormatFunction="TO_CHAR";
        	Variables.dateTostrFunction = "TO_CHAR";
        	Variables.strTodateFunction = "TO_DATE";
        }
        else
        {
        	Variables.datePattern = "%m-%d-%Y";
        	Variables.timePattern = "%H:%i:%s";
        	Variables.dateFormatFunction="DATE_FORMAT";
        	Variables.timeFormatFunction="TIME_FORMAT";
        	Variables.dateTostrFunction = "TO_CHAR";
        	Variables.strTodateFunction = "STR_TO_DATE";
        }
	}
	/**
	 * To test the getSQL(ICondition condition) method.
	 *
	 */
	public void testParticpiantCondition()
	{
		try
		{
			EntityInterface participantEntity = entityManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantConstraintEntity);
			
			ICondition condition1 = QueryGeneratorMock.createParticipantCondition1(participantEntity);
			assertEquals("Participant_0.ACTIVITY_STATUS='Active'", generator.getSQL(condition1,
					expression));

			condition1 = QueryGeneratorMock.createParticipantCondition2(participantEntity);
			assertEquals("Participant_0.IDENTIFIER in (1,2,3,4)", generator.getSQL(condition1,
					expression));

			condition1 = QueryGeneratorMock.createParticipantCondition3(participantEntity);
			assertEquals(
					"Incorrect SQL generated for condition on Mysql database!!!",
					"(Participant_0.BIRTH_DATE<=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE>=STR_TO_DATE('1-2-2000','%m-%d-%Y'))",
					generator.getSQL(condition1, expression));

			setDataBaseType(Constants.ORACLE_DATABASE);
			condition1 = QueryGeneratorMock.createParticipantCondition3(participantEntity);
			assertEquals(
					"Incorrect SQL generated for condition on Oracle database!!!",
					"(Participant_0.BIRTH_DATE<=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE>=TO_DATE('1-2-2000','mm-dd-yyyy'))",
					generator.getSQL(condition1, expression));
			condition1 = QueryGeneratorMock.createParticipantCondition5(participantEntity);
			assertEquals("Participant_0.ACTIVITY_STATUS like '%Active%'", generator.getSQL(
					condition1, expression));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Unexpected Expection!!!", false);
		}
	}

	/**
	 * To test the getSQL(IRule rule) method.
	 *
	 */
	public void testParticpiantRule()
	{
		try
		{
			EntityInterface participantEntity = entityManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantConstraintEntity);
		
			IRule rule = QueryGeneratorMock.createParticipantRule1(participantEntity);
			expression.addOperand(rule);
		
			assertEquals(
					"Incorrect SQL generated for Rule on Mysql database!!!",
					"Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE<=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE>=STR_TO_DATE('1-2-2000','%m-%d-%Y'))",
					generator.getSQL(rule));

			setDataBaseType(Constants.ORACLE_DATABASE);
			assertEquals(
					"Incorrect SQL generated for Rule on Oracle database!!!",
					"Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE<=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE>=TO_DATE('1-2-2000','mm-dd-yyyy'))",
					generator.getSQL(rule));
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
		try
		{
			EntityInterface participantEntity = entityManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstrainedEntity(participantEntity);
			IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantConstraintEntity);
			
			String SQL = generator.getWherePartSQL(expression, null, false);
			//			System.out.println("testParticpiantExpression:"+ SQL);
			assertEquals(
					"Incorrect where part SQL formed for Mysql database!!!",
					"(Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE<=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE>=STR_TO_DATE('1-2-2000','%m-%d-%Y'))) Or(Participant_0.ACTIVITY_STATUS='Active')",
					SQL);
			
			// Testing for Oracle Database.
			setDataBaseType(Constants.ORACLE_DATABASE);
			SQL = generator.getWherePartSQL(expression, null, false);
			//			System.out.println("testParticpiantExpression:"+ SQL);
			assertEquals(
					"Incorrect where part SQL formed for Oracle database!!!",
					"(Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE<=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE>=TO_DATE('1-2-2000','mm-dd-yyyy'))) Or(Participant_0.ACTIVITY_STATUS='Active')",
					SQL);
			
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
		IQuery query = QueryGeneratorMock.createParticipantQuery1();
		IConstraints constraints = query.getConstraints();
		IExpression rootExpression;
		try
		{
			rootExpression = constraints.getExpression(constraints.getRootExpressionId());
			generator.setJoinGraph((JoinGraph) constraints.getJoinGraph());

			generator.buildQuery(query);
			String SQL = generator.getWherePartSQL(rootExpression, null, false);
			//			System.out.println("*********"+SQL);
			assertEquals(
					"Incorrect SQL formed for the Root Expression for MySQL database !!!",
					"(Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE<=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_1.BIRTH_DATE>=STR_TO_DATE('1-2-2000','%m-%d-%Y'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					SQL);

			
			// Testing for Oracle Database.
			setDataBaseType(Constants.ORACLE_DATABASE);
			generator.buildQuery(query);
			SQL = generator.getWherePartSQL(rootExpression, null, false);
			//			System.out.println("*********"+SQL);
			assertEquals(
					"Incorrect SQL formed for the Root Expression for Oracle database !!!",
					"(Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE<=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_1.BIRTH_DATE>=TO_DATE('1-2-2000','mm-dd-yyyy'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					SQL);
			
			String selectPart = generator.getSelectPart(rootExpression);
			//			System.out.println(selectPart);
			assertEquals(
					"Incorrect SQL formed for Select clause of the Expression !!!",
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS",
					selectPart);

			String fromPart = generator
					.getFromPartSQL(rootExpression, null, new HashSet<Integer>());
			//			System.out.println(fromPart);
			//			System.out.println("From catissue_participant Participant0 left join catissue_part_medical_id ParticipantMedicalIdentifier0 on (Participant0.IDENTIFIER=ParticipantMedicalIdentifier0.PARTICIPANT_ID)");
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID)",
					fromPart);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Unexpected Expection!!!", false);
		}
	}

	/**
	 * To test the generateSQL(IQuery) method for a Query: [activityStatus = 'Active'] AND [ id in (1,2,3,4) AND birthDate between ('1-1-2000','1-2-2000')] AND [medicalRecordNumber = 'M001'] 
	 *
	 */
	public void testParticipantQuery1()
	{
		IQuery query = QueryGeneratorMock.createParticipantQuery1();
		try
		{
			String sql = generator.generateSQL(query);
			//			System.out.println("testParticipantQuery1:"+sql);

			assertEquals(
					"Incorrect SQL formed for Query on MySQL !!!",
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE<=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_1.BIRTH_DATE>=STR_TO_DATE('1-2-2000','%m-%d-%Y'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					sql);
			

			// Testing same Query for Oracle database.
			setDataBaseType(Constants.ORACLE_DATABASE);
			sql = generator.generateSQL(query);
			//			System.out.println("testParticipantQuery1:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query on Oracle !!!",
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE<=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_1.BIRTH_DATE>=TO_DATE('1-2-2000','mm-dd-yyyy'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					sql);
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
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 Where Participant_1.ACTIVITY_STATUS='Active'",
					sql);
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
			//			System.out.println("testScgQuery1:" + sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select SpecimenCollectionGroup_1.IDENTIFIER, SpecimenCollectionGroup_1.NAME, SpecimenCollectionGroup_1.CLINICAL_DIAGNOSIS, SpecimenCollectionGroup_1.CLINICAL_STATUS, SpecimenCollectionGroup_1.ACTIVITY_STATUS From catissue_specimen_coll_group SpecimenCollectionGroup_1 left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_1.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) Where (SpecimenCollectionGroup_1.NAME like 'X%') Or(SpecimenCollectionGroup_1.IDENTIFIER = ANY(Select Specimen_2.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_2 left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) where (Specimen_2.TYPE='RNA') Or(Specimen_3.TYPE='DNA'))) And(SpecimenCollectionGroup_1.IDENTIFIER = ANY(Select Specimen_2.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_2 where Specimen_2.AVAILABLE=1)) Or(Specimen_2.TYPE!='DNA')",
					sql);
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
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) Where (Participant_1.LAST_NAME like 's%') And((Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue'))",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}

	/**
	 * To test the SQL for the sample query no. 2 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 *  P: ANY
	 *  	C: ANY
	 *  		G: Collection Site name equals "Site1" OR Collection Site name equals "Site2"
	 *  			S: Type equals "Fixed Tissue" OR Type equals "Fresh Tissue"
	 * </pre>
	 *  	
	 */
	public void testSampleQuery2()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery2();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//			System.out.println("testSampleQuery2:" + sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_site Site_4 on (SpecimenCollectionGroup_3.SITE_ID=Site_4.IDENTIFIER) left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID) Where ((Site_4.NAME='Site1') Or(Site_4.NAME='Site1')) And((Specimen_5.TYPE='Fixed Tissue') Or(Specimen_5.TYPE='Fresh Tissue'))",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
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
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) Where (CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup_3.CLINICAL_STATUS='New Diagnosis') And((Specimen_4.TYPE='DNA') Or(Specimen_4.TYPE='Fresh Tissue')))) And(CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup_3.CLINICAL_STATUS='Post-Operative') And((Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fixed Tissue'))))",
					sql);
		}
		catch (Exception e)
		{
			//			e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}

	/**
	 * 
	 * To test the SQL for the sample query no. 4 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
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
	public void testSampleQuery4()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery4();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//			System.out.println("testSampleQuery4:" + sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) Where (CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup_3.CLINICAL_STATUS='New Diagnosis') And(SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 where Specimen_4.TYPE='DNA')) And(SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 where Specimen_4.TYPE='Milk')))) And(CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup_3.CLINICAL_STATUS='Post Operative') And(Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue')))",
					sql);
		}
		catch (Exception e)
		{
			//			e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}

	/**
	 * 
	 * To test the SQL for the sample query no. 5 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 * The Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Type equals "Fixed Tissue"
	 *	  				S: Type equals "Milk" 
	 *  </pre>	
	 */
	public void testSampleQuery5()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery5();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//			System.out.println("testSampleQuery5:" + sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_5 on (Specimen_4.IDENTIFIER=Specimen_5.PARENT_SPECIMEN_ID) Where (Specimen_4.TYPE='Fixed Tissue') And(Specimen_5.TYPE='Milk')",
					sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}

	/**
	 * 
	 * To test the SQL for the sample query no. 6 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * TODO add Specimen Event parameter part in this Query.
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
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_5 on (Specimen_4.IDENTIFIER=Specimen_5.PARENT_SPECIMEN_ID) Where (Specimen_4.TYPE='Fixed Tissue') And(Specimen_5.TYPE='Amniotic Fluid')",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}

	/**
	 * 
	 * To test query for the sample query no. 2 in the "caTissue Core NBN Query Results.doc"
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
	 * Note:quantity & Class conditions not added.
	 * The implemented Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Type equals "DNA" AND Pathological Status Equals "Malignant" AND Tissue Site Equals "PROSTATE GLAND"
	 *  			Pseudo AND
	 *  			S: Type equals "DNA" AND Pathological Status Equals "Non-Malignant" AND Tissue Site Equals "PROSTATE GLAND"
	 * </pre>
	 */
	public void testNBNSampleQuery2()
	{
		IQuery query = QueryGeneratorMock.createNBNSampleQuery2();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testNBNSampleQuery2:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant_1.ACTIVITY_STATUS, Participant_1.BIRTH_DATE, Participant_1.DEATH_DATE, Participant_1.ETHNICITY, Participant_1.FIRST_NAME, Participant_1.GENDER, Participant_1.IDENTIFIER, Participant_1.LAST_NAME, Participant_1.MIDDLE_NAME, Participant_1.GENOTYPE, Participant_1.SOCIAL_SECURITY_NUMBER, Participant_1.VITAL_STATUS From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_4.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER) Where (SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_4.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER) where (Specimen_4.TYPE='DNA' And Specimen_4.PATHOLOGICAL_STATUS='Malignant') And(SpecimenCharacteristics_5.TISSUE_SITE='Prostate Gland'))) And(SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_4.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER) where (Specimen_4.TYPE='DNA' And Specimen_4.PATHOLOGICAL_STATUS='Non-Malignant') And(SpecimenCharacteristics_5.TISSUE_SITE='Prostate Gland')))",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			assertTrue("Unexpected Expection, While Generating SQL for the Query!!!", false);
		}
	}
	/**
	 * To test the method get alias Name.
	 * - The length of alias returned from this method should be less than 30. 
	 * - The aliases for two different entities in two different Expression with different AliasAppender should not be same. 
	 */
	public void testGetAliasName1()
	{
		generator.aliasNameMap = new HashMap<String, String>();
		generator.aliasAppenderMap = new HashMap<IExpressionId, Integer>();
		IConstraints constraints = QueryObjectFactory.createConstraints();
		try
		{
			// Creating entity with name "edu.wustl.catissuecore.domain.CollectionProtocolRegistration"
			EntityInterface cprEntity = entityManager
					.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
			
			IExpression expressionOne = constraints.addExpression(cprConstraintEntity);
			generator.aliasAppenderMap.put(expressionOne.getExpressionId(), new Integer(1));
			String aliasNameExpected1 = generator.getAliasName(expressionOne);
			//			System.out.println(aliasNameExpected1);
			assertTrue("The length of alias exceeds 30 characters!!!",
					aliasNameExpected1.length() <= 30);
			assertEquals("Incorrect AliasName returned from getAliasName method!!!",
					"CollectionProtocolRegistr_1", aliasNameExpected1);

			// Creating another entity with name "edu.wustl.catissuecore.domain.CollectionProtocolRegistration"
			// because of different ExpressionId the alias will different.
			IExpression expressionTwo = constraints.addExpression(cprConstraintEntity);
			generator.aliasAppenderMap.put(expressionTwo.getExpressionId(), new Integer(2));
			String aliasNameExpected2 = generator.getAliasName(expressionTwo);
			//			System.out.println(aliasNameExpected2);
			assertNotSame(
					"getAliasName method returned same alias name for the classes having same ClassName but are different Alias Appender!!!",
					aliasNameExpected1, aliasNameExpected2);
			assertTrue("The length of alias exceeds 30 characters!!!",
					aliasNameExpected2.length() <= 30);
			assertEquals("Incorrect AliasName returned from getAliasName method!!!",
					"CollectionProtocolRegistr_2", aliasNameExpected2);

		}
		catch (Exception e)
		{
			//			e.printStackTrace();
			assertTrue("Unexpected Expection!!!", false);
		}
	}

	/**
	 * To test the method get alias Name, for case when two classes with same name have different packages. 
	 * - aliasName for two such classes should be different.
	 * - lenght of the two aliasNames should be less than 30.
	 */
	public void testGetAliasName2()
	{
		generator.aliasNameMap = new HashMap<String, String>();
		generator.aliasAppenderMap = new HashMap<IExpressionId, Integer>();
		EntityManagerMock enitytManager = new EntityManagerMock();
		IConstraints constraints = QueryObjectFactory.createConstraints();
		try
		{
			// Creating entity with name "edu.wustl.catissuecore.domain.CollectionProtocolRegistration"
			EntityInterface cprEntity = entityManager.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
	
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(cprEntity);
	
			IExpression expressionOne = constraints.addExpression(cprConstraintEntity);
			generator.aliasAppenderMap.put(expressionOne.getExpressionId(), new Integer(1));
			String aliasNameExpected1 = generator.getAliasName(expressionOne);
			//			System.out.println(aliasNameExpected1);
			assertTrue("The length of alias exceeds 30 characters!!!",
					aliasNameExpected1.length() <= 30);
			assertEquals("Incorrect AliasName returned from getAliasName method!!!",
					"CollectionProtocolRegistr_1", aliasNameExpected1);

			// Creating another entity with name "CAedu.wustl.catissuecore.domain.CollectionProtocolRegistration"
			// in this entity class "CollectionProtocolRegistration" will have different package Name, so the alias will not be same as that of above.
			Entity entityThree = (Entity) enitytManager
					.getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
			entityThree.setName("CA" + entityThree.getName());
			cprConstraintEntity = QueryObjectFactory.createConstrainedEntity(entityThree);
			
			IExpression expressionThree = constraints.addExpression(cprConstraintEntity);
			generator.aliasAppenderMap.put(expressionThree.getExpressionId(), new Integer(1));
			String aliasNameExpected2 = generator.getAliasName(expressionThree);
			//			System.out.println(aliasNameExpected2);
			assertNotSame(
					"getAliasName method returned same alias name for the classes having same ClassName but are different packages!!!",
					aliasNameExpected1, aliasNameExpected2);
			assertTrue("The length of alias exceeds 30 characters!!!",
					aliasNameExpected2.length() <= 30);
			assertEquals("Incorrect AliasName returned from getAliasName method!!!",
					"CollectionProtocolRegistr1_1", aliasNameExpected2);

		}
		catch (Exception e)
		{
			//			e.printStackTrace();
			assertTrue("Unexpected Expection!!!", false);
		}
	}
}
