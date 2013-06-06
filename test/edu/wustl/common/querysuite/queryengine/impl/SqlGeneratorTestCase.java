/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

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
import edu.wustl.common.querysuite.queryobject.util.InheritanceUtils;
import edu.wustl.common.util.InheritanceUtilMock;
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
		InheritanceUtils.setInstance(new InheritanceUtilMock());
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
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
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
					"(Participant_0.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y'))",
					generator.getSQL(condition1, expression));

			setDataBaseType(Constants.ORACLE_DATABASE);
			condition1 = QueryGeneratorMock.createParticipantCondition3(participantEntity);
			assertEquals(
					"Incorrect SQL generated for condition on Oracle database!!!",
					"(Participant_0.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy'))",
					generator.getSQL(condition1, expression));
			condition1 = QueryGeneratorMock.createParticipantCondition5(participantEntity);
			assertEquals("Participant_0.ACTIVITY_STATUS like '%Active%'", generator.getSQL(
					condition1, expression));
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			fail("Unexpected Expection!!!");
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
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantConstraintEntity);
		
			IRule rule = QueryGeneratorMock.createParticipantRule1(participantEntity);
			expression.addOperand(rule);
		
			assertEquals(
					"Incorrect SQL generated for Rule on Mysql database!!!",
					"Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y'))",
					generator.getSQL(rule));

			setDataBaseType(Constants.ORACLE_DATABASE);
			assertEquals(
					"Incorrect SQL generated for Rule on Oracle database!!!",
					"Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy'))",
					generator.getSQL(rule));
}
		catch (Exception e)
		{
			fail("Unexpected Expection!!!");
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
			IConstraintEntity participantConstraintEntity = QueryObjectFactory.createConstraintEntity(participantEntity);
			IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantConstraintEntity);
			generator.setJoinGraph(new JoinGraph());
			String SQL = generator.getWherePartSQL(expression, null, false);
			//			System.out.println("testParticpiantExpression:"+ SQL);
			assertEquals(
					"Incorrect where part SQL formed for Mysql database!!!",
					"(Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y'))) Or(Participant_0.ACTIVITY_STATUS='Active')",
					SQL);
			
			// Testing for Oracle Database.
			setDataBaseType(Constants.ORACLE_DATABASE);
			SQL = generator.getWherePartSQL(expression, null, false);
			//			System.out.println("testParticpiantExpression:"+ SQL);
			assertEquals(
					"Incorrect where part SQL formed for Oracle database!!!",
					"(Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy'))) Or(Participant_0.ACTIVITY_STATUS='Active')",
					SQL);
			
		}
		catch (Exception e)
		{
			fail("Unexpected Expection!!!");
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
					"(Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_1.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					SQL);

			
			// Testing for Oracle Database.
			setDataBaseType(Constants.ORACLE_DATABASE);
			generator.buildQuery(query);
			SQL = generator.getWherePartSQL(rootExpression, null, false);
			//			System.out.println("*********"+SQL);
			assertEquals(
					"Incorrect SQL formed for the Root Expression for Oracle database !!!",
					"(Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_1.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					SQL);
			
			String selectPart = generator.getSelectPart();
			//			System.out.println(selectPart);
			assertEquals(
					"Incorrect SQL formed for Select clause of the Expression !!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column12 ,ParticipantMedicalIdentif_2.IDENTIFIER Column13",
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
			fail("Unexpected Expection!!!");
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column12 ,ParticipantMedicalIdentif_2.IDENTIFIER Column13 From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_1.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					sql);
			

			// Testing same Query for Oracle database.
			setDataBaseType(Constants.ORACLE_DATABASE);
			sql = generator.generateSQL(query);
			//			System.out.println("testParticipantQuery1:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query on Oracle !!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column12 ,ParticipantMedicalIdentif_2.IDENTIFIER Column13 From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_1.ACTIVITY_STATUS='Active') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_1.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 From catissue_participant Participant_1 Where Participant_1.ACTIVITY_STATUS='Active'",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
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
					"Select SpecimenCollectionGroup_1.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_1.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_1.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_1.NAME Column3 ,SpecimenCollectionGroup_1.IDENTIFIER Column4 ,Specimen_2.TYPE Column5 ,Specimen_2.POSITION_DIMENSION_TWO Column6 ,Specimen_2.POSITION_DIMENSION_ONE Column7 ,Specimen_2.PATHOLOGICAL_STATUS Column8 ,Specimen_2.LINEAGE Column9 ,Specimen_2.LABEL Column10 ,Specimen_2.IDENTIFIER Column11 ,Specimen_2.COMMENTS Column12 ,Specimen_2.BARCODE Column13 ,Specimen_2.AVAILABLE Column14 ,Specimen_2.ACTIVITY_STATUS Column15 ,Specimen_3.TYPE Column16 ,Specimen_3.POSITION_DIMENSION_TWO Column17 ,Specimen_3.POSITION_DIMENSION_ONE Column18 ,Specimen_3.PATHOLOGICAL_STATUS Column19 ,Specimen_3.LINEAGE Column20 ,Specimen_3.LABEL Column21 ,Specimen_3.IDENTIFIER Column22 ,Specimen_3.COMMENTS Column23 ,Specimen_3.BARCODE Column24 ,Specimen_3.AVAILABLE Column25 ,Specimen_3.ACTIVITY_STATUS Column26 From catissue_specimen_coll_group SpecimenCollectionGroup_1 left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_1.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) Where (SpecimenCollectionGroup_1.NAME like 'X%') Or(SpecimenCollectionGroup_1.IDENTIFIER = ANY(Select Specimen_2.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_2 left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) where (Specimen_2.TYPE='RNA') Or(Specimen_3.TYPE='DNA'))) And(SpecimenCollectionGroup_1.IDENTIFIER = ANY(Select Specimen_2.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_2 where Specimen_2.AVAILABLE=1)) Or(Specimen_2.TYPE!='DNA')",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}

	/**
	 * 
	 * To Test the SQL for sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc".
	 * <pre>
	 *  P: LastNameStarts with 'S'
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Class equals "Tissue" AND Type equals "Fixed Tissue"
	 *  					OR
	 *  			S: Class equals "Tissue" AND Type equals "Fresh Tissue" 
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_4.TYPE Column21 ,Specimen_4.POSITION_DIMENSION_TWO Column22 ,Specimen_4.POSITION_DIMENSION_ONE Column23 ,Specimen_4.PATHOLOGICAL_STATUS Column24 ,Specimen_4.LINEAGE Column25 ,Specimen_4.LABEL Column26 ,Specimen_4.IDENTIFIER Column27 ,Specimen_4.COMMENTS Column28 ,Specimen_4.BARCODE Column29 ,Specimen_4.AVAILABLE Column30 ,Specimen_4.ACTIVITY_STATUS Column31 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') Where (Participant_1.LAST_NAME like 's%') And((Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue'))",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}

	/**
	 * To test the SQL for the sample query no. 2 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
	 *  P: ANY
	 *  	C: ANY
	 *  		G: Collection Site name equals "Site1" OR Collection Site name equals "Site2"
	 *  			S: Class equals "Tissue" AND(Type equals "Fixed Tissue" OR Type equals "Fresh Tissue")
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Site_4.ACTIVITY_STATUS Column21 ,Site_4.EMAIL_ADDRESS Column22 ,Site_4.TYPE Column23 ,Site_4.NAME Column24 ,Site_4.IDENTIFIER Column25 ,Specimen_5.TYPE Column26 ,Specimen_5.POSITION_DIMENSION_TWO Column27 ,Specimen_5.POSITION_DIMENSION_ONE Column28 ,Specimen_5.PATHOLOGICAL_STATUS Column29 ,Specimen_5.LINEAGE Column30 ,Specimen_5.LABEL Column31 ,Specimen_5.IDENTIFIER Column32 ,Specimen_5.COMMENTS Column33 ,Specimen_5.BARCODE Column34 ,Specimen_5.AVAILABLE Column35 ,Specimen_5.ACTIVITY_STATUS Column36 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_site Site_4 on (SpecimenCollectionGroup_3.SITE_ID=Site_4.IDENTIFIER) left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID And Specimen_5.SPECIMEN_CLASS='Tissue') Where ((Site_4.NAME='Site1') Or(Site_4.NAME='Site1')) And((Specimen_5.TYPE='Fixed Tissue') Or(Specimen_5.TYPE='Fresh Tissue'))",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}

	/**
	 * 
	 * To test the SQL for sample query no. 3 in the "SampleQueriesWithMultipleSubQueryApproach.doc"
	 * <pre>
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_4.TYPE Column21 ,Specimen_4.POSITION_DIMENSION_TWO Column22 ,Specimen_4.POSITION_DIMENSION_ONE Column23 ,Specimen_4.PATHOLOGICAL_STATUS Column24 ,Specimen_4.LINEAGE Column25 ,Specimen_4.LABEL Column26 ,Specimen_4.IDENTIFIER Column27 ,Specimen_4.COMMENTS Column28 ,Specimen_4.BARCODE Column29 ,Specimen_4.AVAILABLE Column30 ,Specimen_4.ACTIVITY_STATUS Column31 ,Specimen_4.CONCENTRATION Column32 ,Specimen_5.TYPE Column33 ,Specimen_5.POSITION_DIMENSION_TWO Column34 ,Specimen_5.POSITION_DIMENSION_ONE Column35 ,Specimen_5.PATHOLOGICAL_STATUS Column36 ,Specimen_5.LINEAGE Column37 ,Specimen_5.LABEL Column38 ,Specimen_5.IDENTIFIER Column39 ,Specimen_5.COMMENTS Column40 ,Specimen_5.BARCODE Column41 ,Specimen_5.AVAILABLE Column42 ,Specimen_5.ACTIVITY_STATUS Column43 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Molecular') left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID And Specimen_5.SPECIMEN_CLASS='Tissue') Where (CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Molecular') left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID And Specimen_5.SPECIMEN_CLASS='Tissue') where (SpecimenCollectionGroup_3.CLINICAL_STATUS='New Diagnosis') And(Specimen_4.TYPE='DNA') Or(Specimen_5.TYPE='Fresh Tissue'))) And(CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID And Specimen_5.SPECIMEN_CLASS='Tissue') where (SpecimenCollectionGroup_3.CLINICAL_STATUS='Post-Operative') And((Specimen_5.TYPE='Fixed Tissue') Or(Specimen_5.TYPE='Fixed Tissue'))))",
					sql);
		}
		catch (Exception e)
		{
			//			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_4.TYPE Column21 ,Specimen_4.POSITION_DIMENSION_TWO Column22 ,Specimen_4.POSITION_DIMENSION_ONE Column23 ,Specimen_4.PATHOLOGICAL_STATUS Column24 ,Specimen_4.LINEAGE Column25 ,Specimen_4.LABEL Column26 ,Specimen_4.IDENTIFIER Column27 ,Specimen_4.COMMENTS Column28 ,Specimen_4.BARCODE Column29 ,Specimen_4.AVAILABLE Column30 ,Specimen_4.ACTIVITY_STATUS Column31 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) Where (CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup_3.CLINICAL_STATUS='New Diagnosis') And(SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 where Specimen_4.TYPE='DNA')) And(SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 where Specimen_4.TYPE='Milk')))) And(CollectionProtocolRegistr_2.IDENTIFIER = ANY(Select SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID From catissue_specimen_coll_group  SpecimenCollectionGroup_3 left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) where (SpecimenCollectionGroup_3.CLINICAL_STATUS='Post Operative') And(Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue')))",
					sql);
		}
		catch (Exception e)
		{
			//			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
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
	 *  			S: Class equals "Tissue" Type equals "Fixed Tissue"
	 *	  				S: Class equals "fluid" Type equals "Milk" 
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_4.TYPE Column21 ,Specimen_4.POSITION_DIMENSION_TWO Column22 ,Specimen_4.POSITION_DIMENSION_ONE Column23 ,Specimen_4.PATHOLOGICAL_STATUS Column24 ,Specimen_4.LINEAGE Column25 ,Specimen_4.LABEL Column26 ,Specimen_4.IDENTIFIER Column27 ,Specimen_4.COMMENTS Column28 ,Specimen_4.BARCODE Column29 ,Specimen_4.AVAILABLE Column30 ,Specimen_4.ACTIVITY_STATUS Column31 ,Specimen_5.TYPE Column32 ,Specimen_5.POSITION_DIMENSION_TWO Column33 ,Specimen_5.POSITION_DIMENSION_ONE Column34 ,Specimen_5.PATHOLOGICAL_STATUS Column35 ,Specimen_5.LINEAGE Column36 ,Specimen_5.LABEL Column37 ,Specimen_5.IDENTIFIER Column38 ,Specimen_5.COMMENTS Column39 ,Specimen_5.BARCODE Column40 ,Specimen_5.AVAILABLE Column41 ,Specimen_5.ACTIVITY_STATUS Column42 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') left join catissue_specimen Specimen_5 on (Specimen_4.IDENTIFIER=Specimen_5.PARENT_SPECIMEN_ID And Specimen_5.SPECIMEN_CLASS='Fluid') Where (Specimen_4.TYPE='Fixed Tissue') And(Specimen_5.TYPE='Milk')",
					sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
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
	 *  			S: Class = "Tissue" and Type equals "Fixed Tissue" and Frozen Event Parameter.Method Starts with ='cry'
	 *  				S: Class = "Fluid" and Type equals "Amniotic Fluid" and Frozen Event Parameter.Method Starts with ='dry'
	 * </pre> 	
	 */
	public void testSampleQuery6()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery6();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
						//System.out.println("testSampleQuery6:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_4.TYPE Column21 ,Specimen_4.POSITION_DIMENSION_TWO Column22 ,Specimen_4.POSITION_DIMENSION_ONE Column23 ,Specimen_4.PATHOLOGICAL_STATUS Column24 ,Specimen_4.LINEAGE Column25 ,Specimen_4.LABEL Column26 ,Specimen_4.IDENTIFIER Column27 ,Specimen_4.COMMENTS Column28 ,Specimen_4.BARCODE Column29 ,Specimen_4.AVAILABLE Column30 ,Specimen_4.ACTIVITY_STATUS Column31 ,SpecimenEventParameters_5.COMMENTS Column32 ,SpecimenEventParameters_5.EVENT_TIMESTAMP Column33 ,SpecimenEventParameters_5.IDENTIFIER Column34 ,FrozenEventParameters_5.METHOD Column35 ,Specimen_6.TYPE Column36 ,Specimen_6.POSITION_DIMENSION_TWO Column37 ,Specimen_6.POSITION_DIMENSION_ONE Column38 ,Specimen_6.PATHOLOGICAL_STATUS Column39 ,Specimen_6.LINEAGE Column40 ,Specimen_6.LABEL Column41 ,Specimen_6.IDENTIFIER Column42 ,Specimen_6.COMMENTS Column43 ,Specimen_6.BARCODE Column44 ,Specimen_6.AVAILABLE Column45 ,Specimen_6.ACTIVITY_STATUS Column46 ,SpecimenEventParameters_7.COMMENTS Column47 ,SpecimenEventParameters_7.EVENT_TIMESTAMP Column48 ,SpecimenEventParameters_7.IDENTIFIER Column49 ,FrozenEventParameters_7.METHOD Column50 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') left join catissue_specimen_event_param SpecimenEventParameters_5 on (Specimen_4.IDENTIFIER=SpecimenEventParameters_5.SPECIMEN_ID) left join catissue_frozen_event_param FrozenEventParameters_5 on (SpecimenEventParameters_5.IDENTIFIER=FrozenEventParameters_5.IDENTIFIER) left join catissue_specimen Specimen_6 on (Specimen_4.IDENTIFIER=Specimen_6.PARENT_SPECIMEN_ID And Specimen_6.SPECIMEN_CLASS='Fluid') left join catissue_specimen_event_param SpecimenEventParameters_7 on (Specimen_6.IDENTIFIER=SpecimenEventParameters_7.SPECIMEN_ID) left join catissue_frozen_event_param FrozenEventParameters_7 on (SpecimenEventParameters_7.IDENTIFIER=FrozenEventParameters_7.IDENTIFIER) Where (Specimen_4.TYPE='Fixed Tissue') And(FrozenEventParameters_5.METHOD like 'cry%') And((Specimen_6.TYPE='Amniotic Fluid') And(FrozenEventParameters_7.METHOD like 'dry%'))",
					sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
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
	 * Note:quantity condition not added.
	 * The implemented Query is as follows:
	 *  P: ANY
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Class Equals "Molecular" AND Type equals "DNA" AND Pathological Status Equals "Malignant" AND Tissue Site Equals "PROSTATE GLAND"
	 *  			Pseudo AND
	 *  			S: Class Equals "Molecular" AND Type equals "DNA" AND Pathological Status Equals "Non-Malignant" AND Tissue Site Equals "PROSTATE GLAND"
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
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_4.TYPE Column21 ,Specimen_4.POSITION_DIMENSION_TWO Column22 ,Specimen_4.POSITION_DIMENSION_ONE Column23 ,Specimen_4.PATHOLOGICAL_STATUS Column24 ,Specimen_4.LINEAGE Column25 ,Specimen_4.LABEL Column26 ,Specimen_4.IDENTIFIER Column27 ,Specimen_4.COMMENTS Column28 ,Specimen_4.BARCODE Column29 ,Specimen_4.AVAILABLE Column30 ,Specimen_4.ACTIVITY_STATUS Column31 ,Specimen_4.CONCENTRATION Column32 ,SpecimenCharacteristics_5.TISSUE_SIDE Column33 ,SpecimenCharacteristics_5.TISSUE_SITE Column34 ,SpecimenCharacteristics_5.IDENTIFIER Column35 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Molecular') left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_4.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER) Where (SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_4.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER And Specimen_4.SPECIMEN_CLASS='Molecular') where (Specimen_4.TYPE='DNA' And Specimen_4.PATHOLOGICAL_STATUS='Malignant') And(SpecimenCharacteristics_5.TISSUE_SITE='Prostate Gland'))) And(SpecimenCollectionGroup_3.IDENTIFIER = ANY(Select Specimen_4.SPECIMEN_COLLECTION_GROUP_ID From catissue_specimen  Specimen_4 left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_4.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER And Specimen_4.SPECIMEN_CLASS='Molecular') where (Specimen_4.TYPE='DNA' And Specimen_4.PATHOLOGICAL_STATUS='Non-Malignant') And(SpecimenCharacteristics_5.TISSUE_SITE='Prostate Gland')))",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
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
			
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
			
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
			fail("Unexpected Expection!!!");
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
	
			IConstraintEntity cprConstraintEntity = QueryObjectFactory.createConstraintEntity(cprEntity);
	
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
			cprConstraintEntity = QueryObjectFactory.createConstraintEntity(entityThree);
			
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
			fail("Unexpected Expection!!!");
		}
	}
	
	/**
	 * To test Query for Multiple parent case type.
	 * <pre>
	 * 			/-- S: type equals 'DNA'--\
	 *    SCG:ANY						   |-- S: type equals 'Amniotic Fluid'
	 *    		\-- S: type equals 'RNA'--/
	 * </pre>
	 */
	public void testCreateMultipleParentQuery1()
	{
		IQuery query = QueryGeneratorMock.createMultipleParentQuery1();
		int multipleParentExpressions = ((JoinGraph)query.getConstraints().getJoinGraph()).getNodesWithMultipleParents().size();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testCreateMultipleParentQuery1:"+sql);
			multipleParentExpressions = ((JoinGraph)generator.constraints.getJoinGraph()).getNodesWithMultipleParents().size();
			assertEquals("Multiple Parent Expression not resolved succesfuly!!!",
					0,
					multipleParentExpressions);
			assertEquals(
					"Incorrect SQL formed for Query !!!",
					"Select SpecimenCollectionGroup_1.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_1.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_1.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_1.NAME Column3 ,SpecimenCollectionGroup_1.IDENTIFIER Column4 ,Specimen_2.TYPE Column5 ,Specimen_2.POSITION_DIMENSION_TWO Column6 ,Specimen_2.POSITION_DIMENSION_ONE Column7 ,Specimen_2.PATHOLOGICAL_STATUS Column8 ,Specimen_2.LINEAGE Column9 ,Specimen_2.LABEL Column10 ,Specimen_2.IDENTIFIER Column11 ,Specimen_2.COMMENTS Column12 ,Specimen_2.BARCODE Column13 ,Specimen_2.AVAILABLE Column14 ,Specimen_2.ACTIVITY_STATUS Column15 ,Specimen_3.TYPE Column16 ,Specimen_3.POSITION_DIMENSION_TWO Column17 ,Specimen_3.POSITION_DIMENSION_ONE Column18 ,Specimen_3.PATHOLOGICAL_STATUS Column19 ,Specimen_3.LINEAGE Column20 ,Specimen_3.LABEL Column21 ,Specimen_3.IDENTIFIER Column22 ,Specimen_3.COMMENTS Column23 ,Specimen_3.BARCODE Column24 ,Specimen_3.AVAILABLE Column25 ,Specimen_3.ACTIVITY_STATUS Column26 From catissue_specimen_coll_group SpecimenCollectionGroup_1 left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_1.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) Where ((Specimen_2.TYPE='DNA') Or(Specimen_3.TYPE='Amniotic Fluid')) Or((Specimen_2.TYPE='RNA') Or(Specimen_3.TYPE='Amniotic Fluid'))",
					sql);
			
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	
	/**
	 * To test Query for Multiple parent case type.
	 * <pre>
	 * 			/-- S: type equals 'DNA'--\
	 *    SCG:ANY						   |-- S: type equals 'Amniotic Fluid'
	 *    		\-- S: type equals 'RNA'--/            \
	 *    							\                   \
	 *    							 \---------------- S: type equals 'milk'
	 * </pre>
	 */
	public void testCreateMultipleParentQuery2()
	{
		IQuery query = QueryGeneratorMock.createMultipleParentQuery2();
		int multipleParentExpressions = ((JoinGraph)query.getConstraints().getJoinGraph()).getNodesWithMultipleParents().size();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testCreateMultipleParentQuery2:"+sql);
			multipleParentExpressions = ((JoinGraph)generator.constraints.getJoinGraph()).getNodesWithMultipleParents().size();
			assertEquals("Multiple Parent Expression not resolved succesfuly!!!",
					0,
					multipleParentExpressions);
			assertEquals(
					"Incorrect SQL formed for Query !!!",
					"Select SpecimenCollectionGroup_1.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_1.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_1.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_1.NAME Column3 ,SpecimenCollectionGroup_1.IDENTIFIER Column4 ,Specimen_2.TYPE Column5 ,Specimen_2.POSITION_DIMENSION_TWO Column6 ,Specimen_2.POSITION_DIMENSION_ONE Column7 ,Specimen_2.PATHOLOGICAL_STATUS Column8 ,Specimen_2.LINEAGE Column9 ,Specimen_2.LABEL Column10 ,Specimen_2.IDENTIFIER Column11 ,Specimen_2.COMMENTS Column12 ,Specimen_2.BARCODE Column13 ,Specimen_2.AVAILABLE Column14 ,Specimen_2.ACTIVITY_STATUS Column15 ,Specimen_3.TYPE Column16 ,Specimen_3.POSITION_DIMENSION_TWO Column17 ,Specimen_3.POSITION_DIMENSION_ONE Column18 ,Specimen_3.PATHOLOGICAL_STATUS Column19 ,Specimen_3.LINEAGE Column20 ,Specimen_3.LABEL Column21 ,Specimen_3.IDENTIFIER Column22 ,Specimen_3.COMMENTS Column23 ,Specimen_3.BARCODE Column24 ,Specimen_3.AVAILABLE Column25 ,Specimen_3.ACTIVITY_STATUS Column26 ,Specimen_4.TYPE Column27 ,Specimen_4.POSITION_DIMENSION_TWO Column28 ,Specimen_4.POSITION_DIMENSION_ONE Column29 ,Specimen_4.PATHOLOGICAL_STATUS Column30 ,Specimen_4.LINEAGE Column31 ,Specimen_4.LABEL Column32 ,Specimen_4.IDENTIFIER Column33 ,Specimen_4.COMMENTS Column34 ,Specimen_4.BARCODE Column35 ,Specimen_4.AVAILABLE Column36 ,Specimen_4.ACTIVITY_STATUS Column37 From catissue_specimen_coll_group SpecimenCollectionGroup_1 left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_1.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_4 on (Specimen_3.IDENTIFIER=Specimen_4.PARENT_SPECIMEN_ID) Where ((Specimen_2.TYPE='DNA') Or((Specimen_3.TYPE='Amniotic Fluid') Or(Specimen_4.TYPE='Milk')) Or(Specimen_3.TYPE='Milk')) Or((Specimen_2.TYPE='RNA') Or((Specimen_3.TYPE='Amniotic Fluid') Or(Specimen_4.TYPE='Milk')))",
					sql);
			
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	
	/**
	 * To test Query for Multiple parent case type with the nesting numbers.
	 * <pre>
	 * 			/-- S: type equals ('DNA' Or 'cDNA')------------\
	 *    SCG:ANY						  						 |-- S: type equals 'Amniotic Fluid' ---> S: type equals 'Milk'
	 *    		\-- S: type equals 'RNA' Or (type equals 'RNA, cytoplasmic' or ChildExp)--/
	 * </pre>
	 * @return reference to the query object.
	 */
	public void testCreateMultipleParentQuery3()
	{
		IQuery query = QueryGeneratorMock.createMultipleParentQuery3();
		int multipleParentExpressions = ((JoinGraph)query.getConstraints().getJoinGraph()).getNodesWithMultipleParents().size();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testCreateMultipleParentQuery3:"+sql);
			multipleParentExpressions = ((JoinGraph)generator.constraints.getJoinGraph()).getNodesWithMultipleParents().size();
			assertEquals("Multiple Parent Expression not resolved succesfuly!!!",
					0,
					multipleParentExpressions);
			assertEquals(
					"Incorrect SQL formed for Query !!!",
					"Select SpecimenCollectionGroup_1.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_1.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_1.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_1.NAME Column3 ,SpecimenCollectionGroup_1.IDENTIFIER Column4 ,Specimen_2.TYPE Column5 ,Specimen_2.POSITION_DIMENSION_TWO Column6 ,Specimen_2.POSITION_DIMENSION_ONE Column7 ,Specimen_2.PATHOLOGICAL_STATUS Column8 ,Specimen_2.LINEAGE Column9 ,Specimen_2.LABEL Column10 ,Specimen_2.IDENTIFIER Column11 ,Specimen_2.COMMENTS Column12 ,Specimen_2.BARCODE Column13 ,Specimen_2.AVAILABLE Column14 ,Specimen_2.ACTIVITY_STATUS Column15 ,Specimen_3.TYPE Column16 ,Specimen_3.POSITION_DIMENSION_TWO Column17 ,Specimen_3.POSITION_DIMENSION_ONE Column18 ,Specimen_3.PATHOLOGICAL_STATUS Column19 ,Specimen_3.LINEAGE Column20 ,Specimen_3.LABEL Column21 ,Specimen_3.IDENTIFIER Column22 ,Specimen_3.COMMENTS Column23 ,Specimen_3.BARCODE Column24 ,Specimen_3.AVAILABLE Column25 ,Specimen_3.ACTIVITY_STATUS Column26 ,Specimen_4.TYPE Column27 ,Specimen_4.POSITION_DIMENSION_TWO Column28 ,Specimen_4.POSITION_DIMENSION_ONE Column29 ,Specimen_4.PATHOLOGICAL_STATUS Column30 ,Specimen_4.LINEAGE Column31 ,Specimen_4.LABEL Column32 ,Specimen_4.IDENTIFIER Column33 ,Specimen_4.COMMENTS Column34 ,Specimen_4.BARCODE Column35 ,Specimen_4.AVAILABLE Column36 ,Specimen_4.ACTIVITY_STATUS Column37 From catissue_specimen_coll_group SpecimenCollectionGroup_1 left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_1.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_4 on (Specimen_3.IDENTIFIER=Specimen_4.PARENT_SPECIMEN_ID) Where (((Specimen_2.TYPE='DNA') Or(Specimen_2.TYPE='cDNA')) Or((Specimen_3.TYPE='Amniotic Fluid') And(Specimen_4.TYPE='Milk'))) Or((Specimen_2.TYPE='RNA') Or((Specimen_2.TYPE='RNA, cytoplasmic') Or((Specimen_3.TYPE='Amniotic Fluid') And(Specimen_4.TYPE='Milk'))))",
					sql);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	

	/**
	 * TO test query for the TABLE_PER_SUB_CLASS inheritance strategy. 
	 * Query for Collection protocol class as: cp.aliquotInSameContainer = 'True' and cp.activityStatus = 'Active' 
	 * Here,
	 * 1. aliquotInSameContainer attribute is in the derived class i.e. Collection Protocol.
	 * 2. activityStatus attribute is in the base class of Collection Protocol i.e. SpecimenProtocol. 
	 */
	public void testCreateInheritanceQuery1()
	{
		IQuery query = QueryGeneratorMock.createInheritanceQuery1();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
//			System.out.println("testCreateInheritanceQuery1:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query !!!",
					"Select SpecimenProtocol_1.TITLE Column0 ,SpecimenProtocol_1.START_DATE Column1 ,SpecimenProtocol_1.SHORT_TITLE Column2 ,SpecimenProtocol_1.IRB_IDENTIFIER Column3 ,SpecimenProtocol_1.IDENTIFIER Column4 ,SpecimenProtocol_1.ENROLLMENT Column5 ,SpecimenProtocol_1.END_DATE Column6 ,SpecimenProtocol_1.DESCRIPTION_URL Column7 ,SpecimenProtocol_1.ACTIVITY_STATUS Column8 ,CollectionProtocol_1.ALIQUOT_IN_SAME_CONTAINER Column9 From catissue_collection_protocol CollectionProtocol_1 left join catissue_specimen_protocol SpecimenProtocol_1 on (CollectionProtocol_1.IDENTIFIER=SpecimenProtocol_1.IDENTIFIER) Where CollectionProtocol_1.ALIQUOT_IN_SAME_CONTAINER=1 And SpecimenProtocol_1.ACTIVITY_STATUS='Active'",
					sql);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	/**
	 * TO test query for the TABLE_PER_HEIRARCHY inheritance strategy. 
	 * Query for Molecular Specimen class as: sp.label contains "1.2" 
	 */
	public void testCreateInheritanceQuery2()
	{
		IQuery query = QueryGeneratorMock.createInheritanceQuery2();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
//			System.out.println("testCreateInheritanceQuery2:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query !!!",
					"Select Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Specimen_1.CONCENTRATION Column11 From catissue_specimen Specimen_1 Where (Specimen_1.LABEL like '%1.2%') And Specimen_1.SPECIMEN_CLASS='Molecular'",
					sql);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	/**
	 * TO create query for the TABLE_PER_HEIRARCHY inheritance strategy with multilevel inheritance.
	 * Query for Cell Specimen Review event parameter class as: srp.viableCellPercentage > 50 and srp.comments contains 'xyz'
	 * Here,
	 * viableCellPercentage: belongs to the base class
	 * comments: belongs to the super class. 
	 */
	public void testCreateInheritanceQuery3()
	{
		IQuery query = QueryGeneratorMock.createInheritanceQuery3();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
//			System.out.println("testCreateInheritanceQuery3:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query !!!",
					"Select SpecimenEventParameters_1.COMMENTS Column0 ,SpecimenEventParameters_1.EVENT_TIMESTAMP Column1 ,SpecimenEventParameters_1.IDENTIFIER Column2 ,CellSpecimenReviewParamet_1.VIABLE_CELL_PERCENTAGE Column3 ,CellSpecimenReviewParamet_1.NEOPLASTIC_CELLULARITY_PER Column4 From CATISSUE_CELL_SPE_REVIEW_PARAM CellSpecimenReviewParamet_1 left join catissue_event_param ReviewEventParameters_1 on (CellSpecimenReviewParamet_1.IDENTIFIER=ReviewEventParameters_1.IDENTIFIER) left join catissue_specimen_event_param SpecimenEventParameters_1 on (ReviewEventParameters_1.IDENTIFIER=SpecimenEventParameters_1.IDENTIFIER) Where CellSpecimenReviewParamet_1.VIABLE_CELL_PERCENTAGE>50 And SpecimenEventParameters_1.COMMENTS like '%xyz%'",
					sql);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	
	/**
	 * To test the Query having empty expressions.
	 *
	 */
	public void testSampleQuery1WithEmptyExp()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery1WithEmptyExp();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testSampleQuery1WithEmptyExp:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_4.TYPE Column21 ,Specimen_4.POSITION_DIMENSION_TWO Column22 ,Specimen_4.POSITION_DIMENSION_ONE Column23 ,Specimen_4.PATHOLOGICAL_STATUS Column24 ,Specimen_4.LINEAGE Column25 ,Specimen_4.LABEL Column26 ,Specimen_4.IDENTIFIER Column27 ,Specimen_4.COMMENTS Column28 ,Specimen_4.BARCODE Column29 ,Specimen_4.AVAILABLE Column30 ,Specimen_4.ACTIVITY_STATUS Column31 ,ParticipantMedicalIdentif_5.MEDICAL_RECORD_NUMBER Column32 ,ParticipantMedicalIdentif_5.IDENTIFIER Column33 ,Site_6.ACTIVITY_STATUS Column34 ,Site_6.EMAIL_ADDRESS Column35 ,Site_6.TYPE Column36 ,Site_6.NAME Column37 ,Site_6.IDENTIFIER Column38 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') left join catissue_part_medical_id ParticipantMedicalIdentif_5 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_5.PARTICIPANT_ID) left join catissue_site Site_6 on (ParticipantMedicalIdentif_5.SITE_ID=Site_6.IDENTIFIER) Where (Participant_1.LAST_NAME like 's%') And((Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue'))",
					sql);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	
	/**
	 * To test the Query having empty expressions.
	 * Expression:
	 * P AND CPR1 AND PM1 AND PM2 AND CPR2 AND PM3
	 * The where part should have conditions as: P AND CPR1 AND CPR2 
	 */

	public void testQueryWithEmptyExp()
	{
		IQuery query = QueryGeneratorMock.createQueryWithEmptyExp();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testQueryWithEmptyExp:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression !!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) Where (Participant_1.FIRST_NAME='Prafull') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Active') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Disabled')",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	
	/**
	 * To test the Query having empty expressions & parenthesis.
	 * Expression: (P AND CPR1 AND PM1) AND PM2 AND CPR2 AND PM3
	 * The where part should have conditions as: (P AND CPR1) AND CPR2 
	 */
	public void testQueryWithEmptyExpWithParenthesis1()
	{
		IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis1();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testQueryWithEmptyExpWithParenthesis1:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) Where ((Participant_1.FIRST_NAME='Prafull') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Active')) And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Disabled')",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
		}
	}
	
	/**
	 * To test the Query having empty expressions & parenthesis.
	 * Expression: ((P AND CPR1) AND PM1 AND PM2 AND CPR2) AND PM3
	 * The where part should have conditions as: ((P AND CPR1) AND CPR2) 
	 */
	public void testQueryWithEmptyExpWithParenthesis2()
	{
		IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis2();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testQueryWithEmptyExpWithParenthesis2:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) Where (((Participant_1.FIRST_NAME='Prafull') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Active')) And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Disabled'))",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
		}
	}
	
	/**
	 * To test the Query having empty expressions & parenthesis.
	 * Expression:  (((P AND CPR1 AND PM1 AND PM2) AND CPR2) AND PM3)
	 * The where part should have conditions as: (((P AND CPR1) AND CPR2)) 
	 */
	public void testQueryWithEmptyExpWithParenthesis3()
	{
		IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis3();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testQueryWithEmptyExpWithParenthesis3:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) Where ((((Participant_1.FIRST_NAME='Prafull') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Active')) And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Disabled')))",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
		}
	}
	
	/**
	 * To test the Query having empty expressions & parenthesis.
	 * Expression: P AND (CPR1 AND PM1 AND PM2 AND CPR2) AND PM3
	 * The where part should have conditions as: P AND (CPR1 AND CPR2) 
	 */
	public void testQueryWithEmptyExpWithParenthesis4()
	{
		IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis4();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testQueryWithEmptyExpWithParenthesis4:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) Where (Participant_1.FIRST_NAME='Prafull') And((CollectionProtocolRegistr_2.ACTIVITY_STATUS='Active') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Disabled'))",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
		}
	}
	
	/**
	 * To test the Query having empty expressions & parenthesis.
	 * Expression: P AND CPR1 AND (PM1 AND PM2) AND CPR2 AND PM3
	 * The where part should have conditions as: P AND CPR1 AND CPR2 
	 */
	public void testQueryWithEmptyExpWithParenthesis5()
	{
		IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis5();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testQueryWithEmptyExpWithParenthesis5:"+sql);
			assertEquals(
					"Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_2.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_2.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_2.IDENTIFIER Column14 ,CollectionProtocolRegistr_2.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) Where (Participant_1.FIRST_NAME='Prafull') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Active') And(CollectionProtocolRegistr_2.ACTIVITY_STATUS='Disabled')",
					sql);
		}
		catch (Exception e)
		{
			fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
		}
	}
	
	/**
	 * 
	 * To Test the SQL for sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc".
	 * <pre>
	 *  P: LastNameStarts with 'S'
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Class equals "Tissue" AND Type equals "Fixed Tissue"
	 *  					OR
	 *  			S: Class equals "Tissue" AND Type equals "Fresh Tissue" 
	 *  
	 * Defining Result view as:
	 * 	P:
	 * 		G:
	 * </pre>  	
	 */
	public void testSampleQuery1WithSelectView1()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery1WithSelectView1();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//			System.out.println("testSampleQuery1WithSelectView1:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query with output view!!!",
					"Select Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column12 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column13 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column14 ,SpecimenCollectionGroup_3.NAME Column15 ,SpecimenCollectionGroup_3.IDENTIFIER Column16 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') Where (Participant_1.LAST_NAME like 's%') And((Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue'))",
					sql);
			
			assertEquals(
					"Incorrect number of output trees Formed while generating SQL !!!",
					1,
					generator.getRootOutputTreeNodeList().size());
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	/**
	 * 
	 * To Test the SQL for sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc".
	 * <pre>
	 *  P: LastNameStarts with 'S'
	 *  	C: ANY
	 *  		G: ANY
	 *  			S: Class equals "Tissue" AND Type equals "Fixed Tissue"
	 *  					OR
	 *  			S: Class equals "Tissue" AND Type equals "Fresh Tissue" 
	 *  
	 * Defining Result view as:
	 * 	S:
	 * </pre>  	
	 */
	public void testSampleQuery1WithSelectView2()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery1WithSelectView2();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//			System.out.println("testSampleQuery1WithSelectView2:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query with output view !!!",
					"Select Specimen_4.TYPE Column0 ,Specimen_4.POSITION_DIMENSION_TWO Column1 ,Specimen_4.POSITION_DIMENSION_ONE Column2 ,Specimen_4.PATHOLOGICAL_STATUS Column3 ,Specimen_4.LINEAGE Column4 ,Specimen_4.LABEL Column5 ,Specimen_4.IDENTIFIER Column6 ,Specimen_4.COMMENTS Column7 ,Specimen_4.BARCODE Column8 ,Specimen_4.AVAILABLE Column9 ,Specimen_4.ACTIVITY_STATUS Column10 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') Where (Participant_1.LAST_NAME like 's%') And((Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue'))",
					sql);

			assertEquals(
					"Incorrect number of output trees Formed while generating SQL !!!",
					1,
					generator.getRootOutputTreeNodeList().size());
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	
	/**
	 * 
	 * To Test the SQL for sample query no. 1 in the "SampleQueriesWithMultipleSubQueryApproach.doc".
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
	 */
	public void testSampleQuery1WithSelectView3()
	{
		IQuery query = QueryGeneratorMock.createSampleQuery1WithSelectView3();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//			System.out.println("testSampleQuery1WithSelectView3:"+sql);
			assertEquals(
					"Incorrect SQL formed for Query with output view !!!",
					"Select SpecimenCollectionGroup_3.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_3.NAME Column3 ,SpecimenCollectionGroup_3.IDENTIFIER Column4 ,Specimen_4.TYPE Column5 ,Specimen_4.POSITION_DIMENSION_TWO Column6 ,Specimen_4.POSITION_DIMENSION_ONE Column7 ,Specimen_4.PATHOLOGICAL_STATUS Column8 ,Specimen_4.LINEAGE Column9 ,Specimen_4.LABEL Column10 ,Specimen_4.IDENTIFIER Column11 ,Specimen_4.COMMENTS Column12 ,Specimen_4.BARCODE Column13 ,Specimen_4.AVAILABLE Column14 ,Specimen_4.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_5.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_5.IDENTIFIER Column17 From catissue_participant Participant_1 left join catissue_coll_prot_reg CollectionProtocolRegistr_2 on (Participant_1.IDENTIFIER=CollectionProtocolRegistr_2.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_2.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') left join catissue_part_medical_id ParticipantMedicalIdentif_5 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_5.PARTICIPANT_ID) Where (Participant_1.LAST_NAME like 's%') And((Specimen_4.TYPE='Fixed Tissue') Or(Specimen_4.TYPE='Fresh Tissue')) And(ParticipantMedicalIdentif_5.MEDICAL_RECORD_NUMBER='M001')",
					sql);

//			Map<OutputTreeDataNode, Map<Long, Map<AttributeInterface, String>>> outputTreeMap = generator.getOutputTreeMap();
//			
//			Set<OutputTreeDataNode> keySet = outputTreeMap.keySet();
			assertEquals(
					"Incorrect number of output trees Formed while generating SQL !!!",
					2,
					generator.getRootOutputTreeNodeList().size());
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query!!!");
		}
	}
	//TODO Add Few More Positive & Negative testcases for Select View.
	
	/**
	 * To test queries having many to many associations.
	 * <pre>
	 *  S: Type equals "DNA"
	 *  	Biohazard: type equals "Toxic"
	 * </pre>
	 *
	 */
	public void testManyToManyQuery1()
	{
		IQuery query = QueryGeneratorMock.createSpecimenBioHazardQuery1();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testManyToManyQuery1:"+sql);
			assertEquals(
					"Incorrect SQL formed for the Query having Many to Many Associations!!!",
					"Select Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Biohazard_2.TYPE Column11 ,Biohazard_2.COMMENTS Column12 ,Biohazard_2.NAME Column13 ,Biohazard_2.IDENTIFIER Column14 From catissue_specimen Specimen_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUE_SPECIMEN_BIOHZ_R_2 on (Specimen_1.IDENTIFIER=CATISSUE_SPECIMEN_BIOHZ_R_2.SPECIMEN_ID And Specimen_1.SPECIMEN_CLASS='Fluid') left join CATISSUE_BIOHAZARD Biohazard_2 on (CATISSUE_SPECIMEN_BIOHZ_R_2.BIOHAZARD_ID=Biohazard_2.IDENTIFIER) Where (Specimen_1.TYPE='DNA') And(Biohazard_2.TYPE='Toxic')",
					sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query having Many to Many Associations!!!");
		}
	}
	/**
	 * To test queries having many to many associations & Pseudo And.
	 * <pre>
	 *  S: Type equals "DNA"
	 *  	Biohazard: type equals "Toxic"
	 *  	Pseudo AND
	 *  	Biohazard: type equals "Radioactive"
	 * </pre>
	 */	
	public void testManyToManyQuery2()
	{
		IQuery query = QueryGeneratorMock.createSpecimenBioHazardQuery2();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testManyToManyQuery2:"+sql);
			assertEquals(
					"Incorrect SQL formed for the Query having Many to Many Associations & Pseudo And!!!",
					"Select Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Biohazard_2.TYPE Column11 ,Biohazard_2.COMMENTS Column12 ,Biohazard_2.NAME Column13 ,Biohazard_2.IDENTIFIER Column14 From catissue_specimen Specimen_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUE_SPECIMEN_BIOHZ_R_2 on (Specimen_1.IDENTIFIER=CATISSUE_SPECIMEN_BIOHZ_R_2.SPECIMEN_ID And Specimen_1.SPECIMEN_CLASS='Fluid') left join CATISSUE_BIOHAZARD Biohazard_2 on (CATISSUE_SPECIMEN_BIOHZ_R_2.BIOHAZARD_ID=Biohazard_2.IDENTIFIER) Where (Specimen_1.TYPE='DNA') And(Specimen_1.IDENTIFIER = ANY(Select Specimen_1.IDENTIFIER From catissue_specimen Specimen_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUE_SPECIMEN_BIOHZ_R_2 on (Specimen_1.IDENTIFIER=CATISSUE_SPECIMEN_BIOHZ_R_2.SPECIMEN_ID And Specimen_1.SPECIMEN_CLASS='Fluid') left join CATISSUE_BIOHAZARD Biohazard_2 on (CATISSUE_SPECIMEN_BIOHZ_R_2.BIOHAZARD_ID=Biohazard_2.IDENTIFIER) where Biohazard_2.TYPE='Toxic')) And(Specimen_1.IDENTIFIER = ANY(Select Specimen_1.IDENTIFIER From catissue_specimen Specimen_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUE_SPECIMEN_BIOHZ_R_2 on (Specimen_1.IDENTIFIER=CATISSUE_SPECIMEN_BIOHZ_R_2.SPECIMEN_ID And Specimen_1.SPECIMEN_CLASS='Fluid') left join CATISSUE_BIOHAZARD Biohazard_2 on (CATISSUE_SPECIMEN_BIOHZ_R_2.BIOHAZARD_ID=Biohazard_2.IDENTIFIER) where Biohazard_2.TYPE='Radioactive'))",
					sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query having Many to Many Associations & Pseudo And!!!");
		}
	}
	
	/**
	 * To test queries having many to many associations & Pseudo And.
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
	 */	
	public void testManyToManyQuery3()
	{
		IQuery query = QueryGeneratorMock.createSpecimenBioHazardQuery3();
		String sql;
		try
		{
			sql = generator.generateSQL(query);
			//System.out.println("testManyToManyQuery3:"+sql);
			assertEquals(
					"Incorrect SQL formed for the Query having Many to Many Associations & Pseudo And!!!",
					"Select Biohazard_1.TYPE Column0 ,Biohazard_1.COMMENTS Column1 ,Biohazard_1.NAME Column2 ,Biohazard_1.IDENTIFIER Column3 ,Specimen_2.TYPE Column4 ,Specimen_2.POSITION_DIMENSION_TWO Column5 ,Specimen_2.POSITION_DIMENSION_ONE Column6 ,Specimen_2.PATHOLOGICAL_STATUS Column7 ,Specimen_2.LINEAGE Column8 ,Specimen_2.LABEL Column9 ,Specimen_2.IDENTIFIER Column10 ,Specimen_2.COMMENTS Column11 ,Specimen_2.BARCODE Column12 ,Specimen_2.AVAILABLE Column13 ,Specimen_2.ACTIVITY_STATUS Column14 ,SpecimenCharacteristics_3.TISSUE_SIDE Column15 ,SpecimenCharacteristics_3.TISSUE_SITE Column16 ,SpecimenCharacteristics_3.IDENTIFIER Column17 From CATISSUE_BIOHAZARD Biohazard_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUE_SPECIMEN_BIOHZ_R_2 on (Biohazard_1.IDENTIFIER=CATISSUE_SPECIMEN_BIOHZ_R_2.BIOHAZARD_ID) left join catissue_specimen Specimen_2 on (CATISSUE_SPECIMEN_BIOHZ_R_2.SPECIMEN_ID=Specimen_2.IDENTIFIER) left join catissue_specimen_char SpecimenCharacteristics_3 on (Specimen_2.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_3.IDENTIFIER) Where (Biohazard_1.TYPE='Toxic') And(Biohazard_1.IDENTIFIER = ANY(Select Biohazard_1.IDENTIFIER From CATISSUE_BIOHAZARD Biohazard_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUE_SPECIMEN_BIOHZ_R_2 on (Biohazard_1.IDENTIFIER=CATISSUE_SPECIMEN_BIOHZ_R_2.BIOHAZARD_ID) left join catissue_specimen Specimen_2 on (CATISSUE_SPECIMEN_BIOHZ_R_2.SPECIMEN_ID=Specimen_2.IDENTIFIER) left join catissue_specimen_char SpecimenCharacteristics_3 on (Specimen_2.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_3.IDENTIFIER) where (Specimen_2.TYPE='DNA') And(SpecimenCharacteristics_3.TISSUE_SITE='skin'))) And(Biohazard_1.IDENTIFIER = ANY(Select Biohazard_1.IDENTIFIER From CATISSUE_BIOHAZARD Biohazard_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUE_SPECIMEN_BIOHZ_R_2 on (Biohazard_1.IDENTIFIER=CATISSUE_SPECIMEN_BIOHZ_R_2.BIOHAZARD_ID) left join catissue_specimen Specimen_2 on (CATISSUE_SPECIMEN_BIOHZ_R_2.SPECIMEN_ID=Specimen_2.IDENTIFIER) left join catissue_specimen_char SpecimenCharacteristics_3 on (Specimen_2.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_3.IDENTIFIER) where (Specimen_2.TYPE='RNA') And(SpecimenCharacteristics_3.TISSUE_SITE='Spinal cord'))) Or(Specimen_2.TYPE='cDNA')",
					sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected Expection, While Generating SQL for the Query having Many to Many Associations & Pseudo And!!!");
		}
	}
}
