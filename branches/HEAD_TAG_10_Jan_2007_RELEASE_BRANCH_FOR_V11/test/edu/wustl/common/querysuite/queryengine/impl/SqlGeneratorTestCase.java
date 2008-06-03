/**
 * 
 */

package edu.wustl.common.querysuite.queryengine.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.EntityManagerMock;
import edu.wustl.common.querysuite.QueryGeneratorMock;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.INamedTerm;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.util.InheritanceUtils;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.util.InheritanceUtilMock;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * TestCase class for SqlGenerator.
 * 
 * @author prafull_kadam
 * 
 */
public class SqlGeneratorTestCase extends TestCase {

    SqlGenerator generator;

    EntityManagerMock entityManager = new EntityManagerMock();

    static {
        Logger.configure();// To avoid null pointer Exception for code calling
        // logger statements.
        // HUH???
        SecurityManager.getApplicationContextName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setDataBaseType(Constants.MYSQL_DATABASE);
        generator = new SqlGenerator();
        InheritanceUtils.setInstance(new InheritanceUtilMock());
    }

    /**
     * To set the Database Type as MySQL or Oracle.
     * 
     * @param databaseType Constants.ORACLE_DATABASE or
     *            Constants.MYSQL_DATABASE.
     */
    private void setDataBaseType(String databaseType) {
        Variables.databaseName = databaseType;

        if (Variables.databaseName.equals(Constants.ORACLE_DATABASE)) {
            // set string/function for oracle
            Variables.datePattern = "mm-dd-yyyy";
            Variables.timePattern = "hh-mi-ss";
            Variables.dateFormatFunction = "TO_CHAR";
            Variables.timeFormatFunction = "TO_CHAR";
            Variables.dateTostrFunction = "TO_CHAR";
            Variables.strTodateFunction = "TO_DATE";
        } else {
            Variables.datePattern = "%m-%d-%Y";
            Variables.timePattern = "%H:%i:%s";
            Variables.dateFormatFunction = "DATE_FORMAT";
            Variables.timeFormatFunction = "TIME_FORMAT";
            Variables.dateTostrFunction = "TO_CHAR";
            Variables.strTodateFunction = "STR_TO_DATE";
        }
    }

    /**
     * To test the getSQL(ICondition condition) method.
     * 
     */
    public void testParticpiantCondition() {
        try {
            EntityInterface participantEntity = entityManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
            IQueryEntity participantQueryEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantQueryEntity);

            ICondition condition1 = QueryGeneratorMock.createParticipantCondition1(participantEntity);
            assertEquals("Participant_0.ACTIVITY_STATUS='Active'", generator.getSQL(condition1, expression));

            condition1 = QueryGeneratorMock.createParticipantCondition2(participantEntity);
            assertEquals("Participant_0.IDENTIFIER in (1,2,3,4)", generator.getSQL(condition1, expression));

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
            assertEquals("Participant_0.ACTIVITY_STATUS like '%Active%'", generator.getSQL(condition1, expression));
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection!!!");
        }
    }

    /**
     * To test the getSQL(IRule rule) method.
     * 
     */
    public void testParticpiantRule() {
        try {
            EntityInterface participantEntity = entityManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
            IQueryEntity participantQueryEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantQueryEntity);

            IRule rule = QueryGeneratorMock.createParticipantRule1(participantEntity);
            expression.addOperand(rule);

            assertEquals(
                    "Incorrect SQL generated for Rule on Mysql database!!!",
                    "Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y')) And Participant_0.ACTIVITY_STATUS!='Disabled'",
                    generator.getSQL(rule));

            setDataBaseType(Constants.ORACLE_DATABASE);
            assertEquals(
                    "Incorrect SQL generated for Rule on Oracle database!!!",
                    "Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy')) And Participant_0.ACTIVITY_STATUS!='Disabled' And Participant_0.ACTIVITY_STATUS!='Disabled'",
                    generator.getSQL(rule));
        } catch (Exception e) {
            fail("Unexpected Expection!!!");
        }
    }

    /**
     * To test the getSQL(IExpression, IJoinGraph, IExpression) method.
     * 
     */
    public void testParticpiantExpression() {
        try {
            EntityInterface participantEntity = entityManager.getEntityByName(EntityManagerMock.PARTICIPANT_NAME);
            IQueryEntity participantQueryEntity = QueryObjectFactory.createQueryEntity(participantEntity);
            IExpression expression = QueryGeneratorMock.creatParticipantExpression1(participantQueryEntity);
            generator.setJoinGraph(new JoinGraph());
            String SQL = generator.getWherePartSQL(expression, null, false);
            // System.out.println("testParticpiantExpression:"+ SQL);
            assertEquals(
                    "Incorrect where part SQL formed for Mysql database!!!",
                    "(Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_0.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y')) And Participant_0.ACTIVITY_STATUS!='Disabled') Or(Participant_0.ACTIVITY_STATUS='Active' And Participant_0.ACTIVITY_STATUS!='Disabled')",
                    SQL);

            // Testing for Oracle Database.
            setDataBaseType(Constants.ORACLE_DATABASE);
            SQL = generator.getWherePartSQL(expression, null, false);
            // System.out.println("testParticpiantExpression:"+ SQL);
            assertEquals(
                    "Incorrect where part SQL formed for Oracle database!!!",
                    "(Participant_0.IDENTIFIER in (1,2,3,4) And (Participant_0.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_0.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy')) And Participant_0.ACTIVITY_STATUS!='Disabled' And Participant_0.ACTIVITY_STATUS!='Disabled') Or(Participant_0.ACTIVITY_STATUS='Active' And Participant_0.ACTIVITY_STATUS!='Disabled' And Participant_0.ACTIVITY_STATUS!='Disabled')",
                    SQL);

        } catch (Exception e) {
            fail("Unexpected Expection!!!");
        }
    }

    /**
     * To test the getSQL(IExpression, IJoinGraph, IExpression) method.
     * 
     */
    public void testParticpiantExpression1() {
        IQuery query = QueryGeneratorMock.createParticipantQuery1();
        IConstraints constraints = query.getConstraints();
        IExpression rootExpression;
        try {
            rootExpression = constraints.getExpression(constraints.getRootExpressionId());
            generator.setJoinGraph((JoinGraph) constraints.getJoinGraph());

            generator.buildQuery(query);
            String SQL = generator.getWherePartSQL(rootExpression, null, false);
            // System.out.println("*********"+SQL);
            assertEquals(
                    "Incorrect SQL formed for the Root Expression for MySQL database !!!",
                    "(Participant_1.ACTIVITY_STATUS='Active' And Participant_1.ACTIVITY_STATUS!='Disabled') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_1.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y')) And Participant_1.ACTIVITY_STATUS!='Disabled') And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
                    SQL);

            // Testing for Oracle Database.
            setDataBaseType(Constants.ORACLE_DATABASE);
            generator.buildQuery(query);
            SQL = generator.getWherePartSQL(rootExpression, null, false);
            // System.out.println("*********"+SQL);
            assertEquals(
                    "Incorrect SQL formed for the Root Expression for Oracle database !!!",
                    "(Participant_1.ACTIVITY_STATUS='Active' And Participant_1.ACTIVITY_STATUS!='Disabled' And Participant_1.ACTIVITY_STATUS!='Disabled') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_1.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy')) And Participant_1.ACTIVITY_STATUS!='Disabled' And Participant_1.ACTIVITY_STATUS!='Disabled') And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
                    SQL);

            String selectPart = generator.getSelectPart();
            // System.out.println(selectPart);
            assertEquals(
                    "Incorrect SQL formed for Select clause of the Expression !!!",
                    "Select distinct Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column12 ,ParticipantMedicalIdentif_2.IDENTIFIER Column13",
                    selectPart);

            String fromPart = generator.getFromPartSQL(rootExpression, null, new HashSet<Integer>());
            // System.out.println(fromPart);
            // System.out.println("From catissue_participant Participant0 left
            // join catissue_part_medical_id ParticipantMedicalIdentifier0 on
            // (Participant0.IDENTIFIER=ParticipantMedicalIdentifier0.PARTICIPANT_ID)");
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID)",
                    fromPart);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection!!!");
        }
    }

    /**
     * To test the generateSQL(IQuery) method for a Query: [activityStatus =
     * 'Active'] AND [ id in (1,2,3,4) AND birthDate between
     * ('1-1-2000','1-2-2000')] AND [medicalRecordNumber = 'M001']
     * 
     */
    public void testParticipantQuery1() {
        IQuery query = QueryGeneratorMock.createParticipantQuery1();
        try {
            String sql = generator.generateSQL(query);
            // System.out.println("testParticipantQuery1:"+sql);

            assertEquals(
                    "Incorrect SQL formed for Query on MySQL !!!",
                    "Select distinct Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column12 ,ParticipantMedicalIdentif_2.IDENTIFIER Column13 From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_1.ACTIVITY_STATUS='Active' And Participant_1.ACTIVITY_STATUS!='Disabled') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=STR_TO_DATE('1-1-2000','%m-%d-%Y') And Participant_1.BIRTH_DATE<=STR_TO_DATE('1-2-2000','%m-%d-%Y')) And Participant_1.ACTIVITY_STATUS!='Disabled') And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
                    sql);

            // Testing same Query for Oracle database.
            setDataBaseType(Constants.ORACLE_DATABASE);
            sql = generator.generateSQL(query);
            // System.out.println("testParticipantQuery1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query on Oracle !!!",
                    "Select distinct Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column12 ,ParticipantMedicalIdentif_2.IDENTIFIER Column13 From catissue_participant Participant_1 left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_1.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_1.ACTIVITY_STATUS='Active' And Participant_1.ACTIVITY_STATUS!='Disabled') And(Participant_1.IDENTIFIER in (1,2,3,4) And (Participant_1.BIRTH_DATE>=TO_DATE('1-1-2000','mm-dd-yyyy') And Participant_1.BIRTH_DATE<=TO_DATE('1-2-2000','mm-dd-yyyy')) And Participant_1.ACTIVITY_STATUS!='Disabled') And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test the generateSQL(IQuery) method for a Query:
     * [Participant.activityStatus = 'Active']
     * 
     */
    public void testParticipantQuery2() {
        IQuery query = QueryGeneratorMock.creatParticipantQuery2();
        try {
            String sql = generator.generateSQL(query);
            // System.out.println("testParticipantQuery2:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 From catissue_participant Participant_1 Where Participant_1.ACTIVITY_STATUS='Active' And Participant_1.ACTIVITY_STATUS!='Disabled'",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    public void testScgQuery1() {
        IQuery query = QueryGeneratorMock.createSCGQuery();
        try {
            String sql = generator.generateSQL(query);
            // System.out.println("testScgQuery1:" + sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct SpecimenCollectionGroup_3.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_3.NAME Column3 ,SpecimenCollectionGroup_3.IDENTIFIER Column4 ,Specimen_5.TYPE Column5 ,Specimen_5.POSITION_DIMENSION_TWO Column6 ,Specimen_5.POSITION_DIMENSION_ONE Column7 ,Specimen_5.PATHOLOGICAL_STATUS Column8 ,Specimen_5.LINEAGE Column9 ,Specimen_5.LABEL Column10 ,Specimen_5.IDENTIFIER Column11 ,Specimen_5.COMMENTS Column12 ,Specimen_5.BARCODE Column13 ,Specimen_5.AVAILABLE Column14 ,Specimen_5.ACTIVITY_STATUS Column15 ,Specimen_2.TYPE Column16 ,Specimen_2.POSITION_DIMENSION_TWO Column17 ,Specimen_2.POSITION_DIMENSION_ONE Column18 ,Specimen_2.PATHOLOGICAL_STATUS Column19 ,Specimen_2.LINEAGE Column20 ,Specimen_2.LABEL Column21 ,Specimen_2.IDENTIFIER Column22 ,Specimen_2.COMMENTS Column23 ,Specimen_2.BARCODE Column24 ,Specimen_2.AVAILABLE Column25 ,Specimen_2.ACTIVITY_STATUS Column26 ,Specimen_1.TYPE Column27 ,Specimen_1.POSITION_DIMENSION_TWO Column28 ,Specimen_1.POSITION_DIMENSION_ONE Column29 ,Specimen_1.PATHOLOGICAL_STATUS Column30 ,Specimen_1.LINEAGE Column31 ,Specimen_1.LABEL Column32 ,Specimen_1.IDENTIFIER Column33 ,Specimen_1.COMMENTS Column34 ,Specimen_1.BARCODE Column35 ,Specimen_1.AVAILABLE Column36 ,Specimen_1.ACTIVITY_STATUS Column37 ,Specimen_4.TYPE Column38 ,Specimen_4.POSITION_DIMENSION_TWO Column39 ,Specimen_4.POSITION_DIMENSION_ONE Column40 ,Specimen_4.PATHOLOGICAL_STATUS Column41 ,Specimen_4.LINEAGE Column42 ,Specimen_4.LABEL Column43 ,Specimen_4.IDENTIFIER Column44 ,Specimen_4.COMMENTS Column45 ,Specimen_4.BARCODE Column46 ,Specimen_4.AVAILABLE Column47 ,Specimen_4.ACTIVITY_STATUS Column48 From catissue_specimen_coll_group SpecimenCollectionGroup_3 left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_2 on (Specimen_5.IDENTIFIER=Specimen_2.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) Where (SpecimenCollectionGroup_3.NAME like 'X%' And SpecimenCollectionGroup_3.ACTIVITY_STATUS!='Disabled') Or((Specimen_5.TYPE='RNA' And Specimen_5.ACTIVITY_STATUS!='Disabled') Or(Specimen_2.TYPE='DNA' And Specimen_2.ACTIVITY_STATUS!='Disabled')) And(Specimen_1.AVAILABLE=1 And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_4.TYPE!='DNA' And Specimen_4.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To Test the SQL for sample query no. 1 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc".
     * 
     * <pre>
     *  P: LastNameStarts with 'S'
     *  	C: ANY
     *  		G: ANY
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fixed Tissue&quot;
     *  					OR
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fresh Tissue&quot; 
     * </pre>
     */
    public void testSampleQuery1() {
        IQuery query = QueryGeneratorMock.createSampleQuery1();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_3.VITAL_STATUS Column0 ,Participant_3.SOCIAL_SECURITY_NUMBER Column1 ,Participant_3.GENOTYPE Column2 ,Participant_3.MIDDLE_NAME Column3 ,Participant_3.LAST_NAME Column4 ,Participant_3.IDENTIFIER Column5 ,Participant_3.GENDER Column6 ,Participant_3.FIRST_NAME Column7 ,Participant_3.ETHNICITY Column8 ,Participant_3.DEATH_DATE Column9 ,Participant_3.BIRTH_DATE Column10 ,Participant_3.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_5.IDENTIFIER Column14 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_2.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_2.NAME Column19 ,SpecimenCollectionGroup_2.IDENTIFIER Column20 ,Specimen_1.TYPE Column21 ,Specimen_1.POSITION_DIMENSION_TWO Column22 ,Specimen_1.POSITION_DIMENSION_ONE Column23 ,Specimen_1.PATHOLOGICAL_STATUS Column24 ,Specimen_1.LINEAGE Column25 ,Specimen_1.LABEL Column26 ,Specimen_1.IDENTIFIER Column27 ,Specimen_1.COMMENTS Column28 ,Specimen_1.BARCODE Column29 ,Specimen_1.AVAILABLE Column30 ,Specimen_1.ACTIVITY_STATUS Column31 ,Specimen_4.TYPE Column32 ,Specimen_4.POSITION_DIMENSION_TWO Column33 ,Specimen_4.POSITION_DIMENSION_ONE Column34 ,Specimen_4.PATHOLOGICAL_STATUS Column35 ,Specimen_4.LINEAGE Column36 ,Specimen_4.LABEL Column37 ,Specimen_4.IDENTIFIER Column38 ,Specimen_4.COMMENTS Column39 ,Specimen_4.BARCODE Column40 ,Specimen_4.AVAILABLE Column41 ,Specimen_4.ACTIVITY_STATUS Column42 From catissue_participant Participant_3 left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_3.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_5.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') Where (Participant_3.LAST_NAME like 's%' And Participant_3.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_4.TYPE='Fresh Tissue' And Specimen_4.ACTIVITY_STATUS!='Disabled')))",
                    sql);
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test the SQL for the sample query no. 2 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc"
     * 
     * <pre>
     *  P: ANY
     *  	C: ANY
     *  		G: Collection Site name equals &quot;Site1&quot; OR Collection Site name equals &quot;Site2&quot;
     *  			S: Class equals &quot;Tissue&quot; AND(Type equals &quot;Fixed Tissue&quot; OR Type equals &quot;Fresh Tissue&quot;)
     * </pre>
     * 
     */
    public void testSampleQuery2() {
        IQuery query = QueryGeneratorMock.createSampleQuery2();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery2:" + sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_3.VITAL_STATUS Column0 ,Participant_3.SOCIAL_SECURITY_NUMBER Column1 ,Participant_3.GENOTYPE Column2 ,Participant_3.MIDDLE_NAME Column3 ,Participant_3.LAST_NAME Column4 ,Participant_3.IDENTIFIER Column5 ,Participant_3.GENDER Column6 ,Participant_3.FIRST_NAME Column7 ,Participant_3.ETHNICITY Column8 ,Participant_3.DEATH_DATE Column9 ,Participant_3.BIRTH_DATE Column10 ,Participant_3.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_5.IDENTIFIER Column14 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_2.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_2.NAME Column19 ,SpecimenCollectionGroup_2.IDENTIFIER Column20 ,Site_1.ACTIVITY_STATUS Column21 ,Site_1.EMAIL_ADDRESS Column22 ,Site_1.TYPE Column23 ,Site_1.NAME Column24 ,Site_1.IDENTIFIER Column25 ,Specimen_4.TYPE Column26 ,Specimen_4.POSITION_DIMENSION_TWO Column27 ,Specimen_4.POSITION_DIMENSION_ONE Column28 ,Specimen_4.PATHOLOGICAL_STATUS Column29 ,Specimen_4.LINEAGE Column30 ,Specimen_4.LABEL Column31 ,Specimen_4.IDENTIFIER Column32 ,Specimen_4.COMMENTS Column33 ,Specimen_4.BARCODE Column34 ,Specimen_4.AVAILABLE Column35 ,Specimen_4.ACTIVITY_STATUS Column36 From catissue_participant Participant_3 left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_3.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_5.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_site Site_1 on (SpecimenCollectionGroup_2.SITE_ID=Site_1.IDENTIFIER) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') Where (Participant_3.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And((Site_1.NAME='Site1' And Site_1.ACTIVITY_STATUS!='Disabled') Or(Site_1.NAME='Site2' And Site_1.ACTIVITY_STATUS!='Disabled')) And((Specimen_4.TYPE='Fixed Tissue' And Specimen_4.ACTIVITY_STATUS!='Disabled') Or(Specimen_4.TYPE='Fresh Tissue' And Specimen_4.ACTIVITY_STATUS!='Disabled'))))",
                    sql);
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To test the SQL for sample query no. 3 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc"
     * 
     * <pre>
     *  P: ANY
     *  	C: ANY
     *  		G: Clinical status equals &quot;New Diagnosis&quot;
     *  			S: Class equals &quot;Molecular&quot; AND Type equals &quot;DNA&quot;
     *  			OR
     *  			S: Class equals &quot;Tissue&quot; Type equals &quot;Fresh Tissue&quot; 
     *  		Pseudo AND
     *  		G: Clinical status equals &quot;Post Operative&quot;
     *  			S: Class equals &quot;Tissue&quot; Type equals &quot;Fixed Tissue&quot;
     *  			OR
     *  			S: Class equals &quot;Tissue&quot; Type equals &quot;Fresh Tissue&quot; 
     * </pre>
     */
    public void testSampleQuery3() {
        IQuery query = QueryGeneratorMock.createSampleQuery3();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery3:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_7.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_7.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_7.IDENTIFIER Column14 ,CollectionProtocolRegistr_7.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_1.TYPE Column21 ,Specimen_1.POSITION_DIMENSION_TWO Column22 ,Specimen_1.POSITION_DIMENSION_ONE Column23 ,Specimen_1.PATHOLOGICAL_STATUS Column24 ,Specimen_1.LINEAGE Column25 ,Specimen_1.LABEL Column26 ,Specimen_1.IDENTIFIER Column27 ,Specimen_1.COMMENTS Column28 ,Specimen_1.BARCODE Column29 ,Specimen_1.AVAILABLE Column30 ,Specimen_1.ACTIVITY_STATUS Column31 ,Specimen_1.CONCENTRATION Column32 ,Specimen_5.TYPE Column33 ,Specimen_5.POSITION_DIMENSION_TWO Column34 ,Specimen_5.POSITION_DIMENSION_ONE Column35 ,Specimen_5.PATHOLOGICAL_STATUS Column36 ,Specimen_5.LINEAGE Column37 ,Specimen_5.LABEL Column38 ,Specimen_5.IDENTIFIER Column39 ,Specimen_5.COMMENTS Column40 ,Specimen_5.BARCODE Column41 ,Specimen_5.AVAILABLE Column42 ,Specimen_5.ACTIVITY_STATUS Column43 ,SpecimenCollectionGroup_2.ACTIVITY_STATUS Column44 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column45 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column46 ,SpecimenCollectionGroup_2.NAME Column47 ,SpecimenCollectionGroup_2.IDENTIFIER Column48 ,Specimen_6.TYPE Column49 ,Specimen_6.POSITION_DIMENSION_TWO Column50 ,Specimen_6.POSITION_DIMENSION_ONE Column51 ,Specimen_6.PATHOLOGICAL_STATUS Column52 ,Specimen_6.LINEAGE Column53 ,Specimen_6.LABEL Column54 ,Specimen_6.IDENTIFIER Column55 ,Specimen_6.COMMENTS Column56 ,Specimen_6.BARCODE Column57 ,Specimen_6.AVAILABLE Column58 ,Specimen_6.ACTIVITY_STATUS Column59 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_7 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_7.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_7.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Molecular') left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID And Specimen_5.SPECIMEN_CLASS='Tissue') left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_7.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_6 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_6.SPECIMEN_COLLECTION_GROUP_ID And Specimen_6.SPECIMEN_CLASS='Tissue') Where (Participant_4.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_7.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_3.CLINICAL_STATUS='New Diagnosis' And SpecimenCollectionGroup_3.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='DNA' And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_5.TYPE='Fresh Tissue' And Specimen_5.ACTIVITY_STATUS!='Disabled')) And((SpecimenCollectionGroup_2.CLINICAL_STATUS='Post-Operative' And SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And((Specimen_6.TYPE='Fixed Tissue' And Specimen_6.ACTIVITY_STATUS!='Disabled') Or(Specimen_6.TYPE='Fresh Tissue' And Specimen_6.ACTIVITY_STATUS!='Disabled'))))",
                    sql);
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To test the SQL for the sample query no. 4 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc"
     * 
     * <pre>
     * The Query is as follows:
     *  P: ANY
     *  	C: ANY
     *  		G: Clinical status equals &quot;New Diagnosis&quot;
     *  			S: Type equals &quot;DNA&quot;
     *  			Pseudo AND
     *  			S: Type equals &quot;Milk&quot; 
     *  		Pseudo AND
     *  		G: Clinical status equals &quot;Post Operative&quot;
     *  			S: Type equals &quot;Fixed Tissue&quot;
     *  			OR
     *  			S: Type equals &quot;Fresh Tissue&quot; 
     * </pre>
     */
    public void testSampleQuery4() {
        IQuery query = QueryGeneratorMock.createSampleQuery4();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery4:" + sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_5.VITAL_STATUS Column0 ,Participant_5.SOCIAL_SECURITY_NUMBER Column1 ,Participant_5.GENOTYPE Column2 ,Participant_5.MIDDLE_NAME Column3 ,Participant_5.LAST_NAME Column4 ,Participant_5.IDENTIFIER Column5 ,Participant_5.GENDER Column6 ,Participant_5.FIRST_NAME Column7 ,Participant_5.ETHNICITY Column8 ,Participant_5.DEATH_DATE Column9 ,Participant_5.BIRTH_DATE Column10 ,Participant_5.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_8.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_8.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_8.IDENTIFIER Column14 ,CollectionProtocolRegistr_8.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_4.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_4.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_4.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_4.NAME Column19 ,SpecimenCollectionGroup_4.IDENTIFIER Column20 ,Specimen_1.TYPE Column21 ,Specimen_1.POSITION_DIMENSION_TWO Column22 ,Specimen_1.POSITION_DIMENSION_ONE Column23 ,Specimen_1.PATHOLOGICAL_STATUS Column24 ,Specimen_1.LINEAGE Column25 ,Specimen_1.LABEL Column26 ,Specimen_1.IDENTIFIER Column27 ,Specimen_1.COMMENTS Column28 ,Specimen_1.BARCODE Column29 ,Specimen_1.AVAILABLE Column30 ,Specimen_1.ACTIVITY_STATUS Column31 ,Specimen_6.TYPE Column32 ,Specimen_6.POSITION_DIMENSION_TWO Column33 ,Specimen_6.POSITION_DIMENSION_ONE Column34 ,Specimen_6.PATHOLOGICAL_STATUS Column35 ,Specimen_6.LINEAGE Column36 ,Specimen_6.LABEL Column37 ,Specimen_6.IDENTIFIER Column38 ,Specimen_6.COMMENTS Column39 ,Specimen_6.BARCODE Column40 ,Specimen_6.AVAILABLE Column41 ,Specimen_6.ACTIVITY_STATUS Column42 ,SpecimenCollectionGroup_2.ACTIVITY_STATUS Column43 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column44 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column45 ,SpecimenCollectionGroup_2.NAME Column46 ,SpecimenCollectionGroup_2.IDENTIFIER Column47 ,Specimen_7.TYPE Column48 ,Specimen_7.POSITION_DIMENSION_TWO Column49 ,Specimen_7.POSITION_DIMENSION_ONE Column50 ,Specimen_7.PATHOLOGICAL_STATUS Column51 ,Specimen_7.LINEAGE Column52 ,Specimen_7.LABEL Column53 ,Specimen_7.IDENTIFIER Column54 ,Specimen_7.COMMENTS Column55 ,Specimen_7.BARCODE Column56 ,Specimen_7.AVAILABLE Column57 ,Specimen_7.ACTIVITY_STATUS Column58 ,Specimen_3.TYPE Column59 ,Specimen_3.POSITION_DIMENSION_TWO Column60 ,Specimen_3.POSITION_DIMENSION_ONE Column61 ,Specimen_3.PATHOLOGICAL_STATUS Column62 ,Specimen_3.LINEAGE Column63 ,Specimen_3.LABEL Column64 ,Specimen_3.IDENTIFIER Column65 ,Specimen_3.COMMENTS Column66 ,Specimen_3.BARCODE Column67 ,Specimen_3.AVAILABLE Column68 ,Specimen_3.ACTIVITY_STATUS Column69 From catissue_participant Participant_5 left join catissue_coll_prot_reg CollectionProtocolRegistr_8 on (Participant_5.IDENTIFIER=CollectionProtocolRegistr_8.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_4 on (CollectionProtocolRegistr_8.IDENTIFIER=SpecimenCollectionGroup_4.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_4.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_6 on (SpecimenCollectionGroup_4.IDENTIFIER=Specimen_6.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_8.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_7 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_7.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_3 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_3.SPECIMEN_COLLECTION_GROUP_ID) Where (Participant_5.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_8.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_4.CLINICAL_STATUS='New Diagnosis' And SpecimenCollectionGroup_4.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='DNA' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(Specimen_6.TYPE='Milk' And Specimen_6.ACTIVITY_STATUS!='Disabled')) And((SpecimenCollectionGroup_2.CLINICAL_STATUS='Post Operative' And SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(Specimen_7.TYPE='Fixed Tissue' And Specimen_7.ACTIVITY_STATUS!='Disabled') Or(Specimen_3.TYPE='Fresh Tissue' And Specimen_3.ACTIVITY_STATUS!='Disabled')))",
                    sql);
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To test the SQL for the sample query no. 5 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc"
     * 
     * <pre>
     * The Query is as follows:
     *  P: ANY
     *  	C: ANY
     *  		G: ANY
     *  			S: Class equals &quot;Tissue&quot; Type equals &quot;Fixed Tissue&quot;
     *   				S: Class equals &quot;fluid&quot; Type equals &quot;Milk&quot; 
     * </pre>
     */
    public void testSampleQuery5() {
        IQuery query = QueryGeneratorMock.createSampleQuery5();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery5:" + sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_3.VITAL_STATUS Column0 ,Participant_3.SOCIAL_SECURITY_NUMBER Column1 ,Participant_3.GENOTYPE Column2 ,Participant_3.MIDDLE_NAME Column3 ,Participant_3.LAST_NAME Column4 ,Participant_3.IDENTIFIER Column5 ,Participant_3.GENDER Column6 ,Participant_3.FIRST_NAME Column7 ,Participant_3.ETHNICITY Column8 ,Participant_3.DEATH_DATE Column9 ,Participant_3.BIRTH_DATE Column10 ,Participant_3.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_5.IDENTIFIER Column14 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_2.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_2.NAME Column19 ,SpecimenCollectionGroup_2.IDENTIFIER Column20 ,Specimen_1.TYPE Column21 ,Specimen_1.POSITION_DIMENSION_TWO Column22 ,Specimen_1.POSITION_DIMENSION_ONE Column23 ,Specimen_1.PATHOLOGICAL_STATUS Column24 ,Specimen_1.LINEAGE Column25 ,Specimen_1.LABEL Column26 ,Specimen_1.IDENTIFIER Column27 ,Specimen_1.COMMENTS Column28 ,Specimen_1.BARCODE Column29 ,Specimen_1.AVAILABLE Column30 ,Specimen_1.ACTIVITY_STATUS Column31 ,Specimen_4.TYPE Column32 ,Specimen_4.POSITION_DIMENSION_TWO Column33 ,Specimen_4.POSITION_DIMENSION_ONE Column34 ,Specimen_4.PATHOLOGICAL_STATUS Column35 ,Specimen_4.LINEAGE Column36 ,Specimen_4.LABEL Column37 ,Specimen_4.IDENTIFIER Column38 ,Specimen_4.COMMENTS Column39 ,Specimen_4.BARCODE Column40 ,Specimen_4.AVAILABLE Column41 ,Specimen_4.ACTIVITY_STATUS Column42 From catissue_participant Participant_3 left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_3.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_5.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') left join catissue_specimen Specimen_4 on (Specimen_1.IDENTIFIER=Specimen_4.PARENT_SPECIMEN_ID And Specimen_4.SPECIMEN_CLASS='Fluid') Where (Participant_3.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And((Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(Specimen_4.TYPE='Milk' And Specimen_4.ACTIVITY_STATUS!='Disabled'))))",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To test the SQL for the sample query no. 6 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc" TODO add Specimen Event
     * parameter part in this Query.
     * 
     * <pre>
     *  P: ANY
     *  	C: ANY
     *  		G: ANY
     *  			S: Class = &quot;Tissue&quot; and Type equals &quot;Fixed Tissue&quot; and Frozen Event Parameter.Method Starts with ='cry'
     *  				S: Class = &quot;Fluid&quot; and Type equals &quot;Amniotic Fluid&quot; and Frozen Event Parameter.Method Starts with ='dry'
     * </pre>
     */
    public void testSampleQuery6() {
        IQuery query = QueryGeneratorMock.createSampleQuery6();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery6:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_7.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_7.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_7.IDENTIFIER Column14 ,CollectionProtocolRegistr_7.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_1.TYPE Column21 ,Specimen_1.POSITION_DIMENSION_TWO Column22 ,Specimen_1.POSITION_DIMENSION_ONE Column23 ,Specimen_1.PATHOLOGICAL_STATUS Column24 ,Specimen_1.LINEAGE Column25 ,Specimen_1.LABEL Column26 ,Specimen_1.IDENTIFIER Column27 ,Specimen_1.COMMENTS Column28 ,Specimen_1.BARCODE Column29 ,Specimen_1.AVAILABLE Column30 ,Specimen_1.ACTIVITY_STATUS Column31 ,SpecimenEventParameters_5.COMMENTS Column32 ,SpecimenEventParameters_5.EVENT_TIMESTAMP Column33 ,SpecimenEventParameters_5.IDENTIFIER Column34 ,FrozenEventParameters_5.METHOD Column35 ,Specimen_2.TYPE Column36 ,Specimen_2.POSITION_DIMENSION_TWO Column37 ,Specimen_2.POSITION_DIMENSION_ONE Column38 ,Specimen_2.PATHOLOGICAL_STATUS Column39 ,Specimen_2.LINEAGE Column40 ,Specimen_2.LABEL Column41 ,Specimen_2.IDENTIFIER Column42 ,Specimen_2.COMMENTS Column43 ,Specimen_2.BARCODE Column44 ,Specimen_2.AVAILABLE Column45 ,Specimen_2.ACTIVITY_STATUS Column46 ,SpecimenEventParameters_6.COMMENTS Column47 ,SpecimenEventParameters_6.EVENT_TIMESTAMP Column48 ,SpecimenEventParameters_6.IDENTIFIER Column49 ,FrozenEventParameters_6.METHOD Column50 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_7 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_7.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_7.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') left join catissue_specimen_event_param SpecimenEventParameters_5 on (Specimen_1.IDENTIFIER=SpecimenEventParameters_5.SPECIMEN_ID) inner join catissue_frozen_event_param FrozenEventParameters_5 on (SpecimenEventParameters_5.IDENTIFIER=FrozenEventParameters_5.IDENTIFIER) left join catissue_specimen Specimen_2 on (Specimen_1.IDENTIFIER=Specimen_2.PARENT_SPECIMEN_ID And Specimen_2.SPECIMEN_CLASS='Fluid') left join catissue_specimen_event_param SpecimenEventParameters_6 on (Specimen_2.IDENTIFIER=SpecimenEventParameters_6.SPECIMEN_ID) inner join catissue_frozen_event_param FrozenEventParameters_6 on (SpecimenEventParameters_6.IDENTIFIER=FrozenEventParameters_6.IDENTIFIER) Where (Participant_4.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_7.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_3.ACTIVITY_STATUS!='Disabled') And((Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(FrozenEventParameters_5.METHOD like 'cry%') And((Specimen_2.TYPE='Amniotic Fluid' And Specimen_2.ACTIVITY_STATUS!='Disabled') And(FrozenEventParameters_6.METHOD like 'dry%')))))",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To test query for the sample query no. 2 in the "caTissue Core NBN Query
     * Results.doc"
     * 
     * <pre>
     * The Actual Query is as follows:
     *  P: ANY
     *  	C: ANY
     *  		G: ANY
     *  			S: quantity &gt; 5 AND Class Equals &quot;Molecular&quot; AND Type equals &quot;DNA&quot; AND Pathological Status Equals &quot;Malignant&quot; AND Tissue Site Equals &quot;PROSTATE GLAND&quot;
     *  			Pseudo AND
     *  			S: quantity &gt; 5 AND Class Equals &quot;Molecular&quot; AND Type equals &quot;DNA&quot; AND Pathological Status Equals &quot;Non-Malignant&quot; AND Tissue Site Equals &quot;PROSTATE GLAND&quot;
     * </pre>
     * <pre>
     * Note:quantity condition not added.
     * The implemented Query is as follows:
     *  P: ANY
     *  	C: ANY
     *  		G: ANY
     *  			S: Class Equals &quot;Molecular&quot; AND Type equals &quot;DNA&quot; AND Pathological Status Equals &quot;Malignant&quot; AND Tissue Site Equals &quot;PROSTATE GLAND&quot;
     *  			Pseudo AND
     *  			S: Class Equals &quot;Molecular&quot; AND Type equals &quot;DNA&quot; AND Pathological Status Equals &quot;Non-Malignant&quot; AND Tissue Site Equals &quot;PROSTATE GLAND&quot;
     * </pre>
     */
    public void testNBNSampleQuery2() {
        IQuery query = QueryGeneratorMock.createNBNSampleQuery2();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testNBNSampleQuery2:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_7.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_7.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_7.IDENTIFIER Column14 ,CollectionProtocolRegistr_7.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_1.TYPE Column21 ,Specimen_1.POSITION_DIMENSION_TWO Column22 ,Specimen_1.POSITION_DIMENSION_ONE Column23 ,Specimen_1.PATHOLOGICAL_STATUS Column24 ,Specimen_1.LINEAGE Column25 ,Specimen_1.LABEL Column26 ,Specimen_1.IDENTIFIER Column27 ,Specimen_1.COMMENTS Column28 ,Specimen_1.BARCODE Column29 ,Specimen_1.AVAILABLE Column30 ,Specimen_1.ACTIVITY_STATUS Column31 ,Specimen_1.CONCENTRATION Column32 ,SpecimenCharacteristics_5.TISSUE_SIDE Column33 ,SpecimenCharacteristics_5.TISSUE_SITE Column34 ,SpecimenCharacteristics_5.IDENTIFIER Column35 ,Specimen_2.TYPE Column36 ,Specimen_2.POSITION_DIMENSION_TWO Column37 ,Specimen_2.POSITION_DIMENSION_ONE Column38 ,Specimen_2.PATHOLOGICAL_STATUS Column39 ,Specimen_2.LINEAGE Column40 ,Specimen_2.LABEL Column41 ,Specimen_2.IDENTIFIER Column42 ,Specimen_2.COMMENTS Column43 ,Specimen_2.BARCODE Column44 ,Specimen_2.AVAILABLE Column45 ,Specimen_2.ACTIVITY_STATUS Column46 ,Specimen_2.CONCENTRATION Column47 ,SpecimenCharacteristics_6.TISSUE_SIDE Column48 ,SpecimenCharacteristics_6.TISSUE_SITE Column49 ,SpecimenCharacteristics_6.IDENTIFIER Column50 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_7 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_7.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_7.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Molecular') left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_1.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER) left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID And Specimen_2.SPECIMEN_CLASS='Molecular') left join catissue_specimen_char SpecimenCharacteristics_6 on (Specimen_2.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_6.IDENTIFIER) Where (Participant_4.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_7.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_3.ACTIVITY_STATUS!='Disabled') And((Specimen_1.TYPE='DNA' And Specimen_1.PATHOLOGICAL_STATUS='Malignant' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(SpecimenCharacteristics_5.TISSUE_SITE='Prostate Gland')) And((Specimen_2.TYPE='DNA' And Specimen_2.PATHOLOGICAL_STATUS='Non-Malignant' And Specimen_2.ACTIVITY_STATUS!='Disabled') And(SpecimenCharacteristics_6.TISSUE_SITE='Prostate Gland'))))",
                    sql);
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test the method get alias Name. - The length of alias returned from
     * this method should be less than 30. - The aliases for two different
     * entities in two different Expression with different AliasAppender should
     * not be same.
     */
    public void testGetAliasName1() {
        generator.aliasNameMap = new HashMap<String, String>();
        generator.aliasAppenderMap = new HashMap<IExpressionId, Integer>();
        IConstraints constraints = QueryObjectFactory.createConstraints();
        try {
            // Creating entity with name
            // "edu.wustl.catissuecore.domain.CollectionProtocolRegistration"
            EntityInterface cprEntity = entityManager
                    .getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);

            IQueryEntity cprQueryEntity = QueryObjectFactory.createQueryEntity(cprEntity);

            IExpression expressionOne = constraints.addExpression(cprQueryEntity);
            generator.aliasAppenderMap.put(expressionOne.getExpressionId(), new Integer(1));
            String aliasNameExpected1 = generator.getAliasName(expressionOne);
            // System.out.println(aliasNameExpected1);
            assertTrue("The length of alias exceeds 30 characters!!!", aliasNameExpected1.length() <= 30);
            assertEquals("Incorrect AliasName returned from getAliasName method!!!",
                    "CollectionProtocolRegistr_1", aliasNameExpected1);

            // Creating another entity with name
            // "edu.wustl.catissuecore.domain.CollectionProtocolRegistration"
            // because of different ExpressionId the alias will different.
            IExpression expressionTwo = constraints.addExpression(cprQueryEntity);
            generator.aliasAppenderMap.put(expressionTwo.getExpressionId(), new Integer(2));
            String aliasNameExpected2 = generator.getAliasName(expressionTwo);
            // System.out.println(aliasNameExpected2);
            assertNotSame(
                    "getAliasName method returned same alias name for the classes having same ClassName but are different Alias Appender!!!",
                    aliasNameExpected1, aliasNameExpected2);
            assertTrue("The length of alias exceeds 30 characters!!!", aliasNameExpected2.length() <= 30);
            assertEquals("Incorrect AliasName returned from getAliasName method!!!",
                    "CollectionProtocolRegistr_2", aliasNameExpected2);

        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection!!!");
        }
    }

    /**
     * To test the method get alias Name, for case when two classes with same
     * name have different packages. - aliasName for two such classes should be
     * different. - lenght of the two aliasNames should be less than 30.
     */
    public void testGetAliasName2() {
        generator.aliasNameMap = new HashMap<String, String>();
        generator.aliasAppenderMap = new HashMap<IExpressionId, Integer>();
        EntityManagerMock enitytManager = new EntityManagerMock();
        IConstraints constraints = QueryObjectFactory.createConstraints();
        try {
            // Creating entity with name
            // "edu.wustl.catissuecore.domain.CollectionProtocolRegistration"
            EntityInterface cprEntity = entityManager
                    .getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);

            IQueryEntity cprQueryEntity = QueryObjectFactory.createQueryEntity(cprEntity);

            IExpression expressionOne = constraints.addExpression(cprQueryEntity);
            generator.aliasAppenderMap.put(expressionOne.getExpressionId(), new Integer(1));
            String aliasNameExpected1 = generator.getAliasName(expressionOne);
            // System.out.println(aliasNameExpected1);
            assertTrue("The length of alias exceeds 30 characters!!!", aliasNameExpected1.length() <= 30);
            assertEquals("Incorrect AliasName returned from getAliasName method!!!",
                    "CollectionProtocolRegistr_1", aliasNameExpected1);

            // Creating another entity with name
            // "CAedu.wustl.catissuecore.domain.CollectionProtocolRegistration"
            // in this entity class "CollectionProtocolRegistration" will have
            // different package Name, so the alias will not be same as that of
            // above.
            Entity entityThree = (Entity) enitytManager
                    .getEntityByName(EntityManagerMock.COLLECTION_PROTOCOL_REGISTRATION_NAME);
            entityThree.setName("CA" + entityThree.getName());
            cprQueryEntity = QueryObjectFactory.createQueryEntity(entityThree);

            IExpression expressionThree = constraints.addExpression(cprQueryEntity);
            generator.aliasAppenderMap.put(expressionThree.getExpressionId(), new Integer(1));
            String aliasNameExpected2 = generator.getAliasName(expressionThree);
            // System.out.println(aliasNameExpected2);
            assertNotSame(
                    "getAliasName method returned same alias name for the classes having same ClassName but are different packages!!!",
                    aliasNameExpected1, aliasNameExpected2);
            assertTrue("The length of alias exceeds 30 characters!!!", aliasNameExpected2.length() <= 30);
            assertEquals("Incorrect AliasName returned from getAliasName method!!!",
                    "CollectionProtocolRegistr1_1", aliasNameExpected2);

        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection!!!");
        }
    }

    /**
     * To test Query for Multiple parent case type.
     * 
     * <pre>
     * 			/-- S: type equals 'DNA'--\
     *    SCG:ANY						   |-- S: type equals 'Amniotic Fluid'
     *    		\-- S: type equals 'RNA'--/
     * </pre>
     */
    public void testCreateMultipleParentQuery1() {
        IQuery query = QueryGeneratorMock.createMultipleParentQuery1();
        int multipleParentExpressions = ((JoinGraph) query.getConstraints().getJoinGraph())
                .getNodesWithMultipleParents().size();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateMultipleParentQuery1:"+sql);
            multipleParentExpressions = ((JoinGraph) generator.constraints.getJoinGraph())
                    .getNodesWithMultipleParents().size();

            assertEquals("Multiple Parent Expression not resolved succesfuly!!!", 0, multipleParentExpressions);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct SpecimenCollectionGroup_3.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_3.NAME Column3 ,SpecimenCollectionGroup_3.IDENTIFIER Column4 ,Specimen_5.TYPE Column5 ,Specimen_5.POSITION_DIMENSION_TWO Column6 ,Specimen_5.POSITION_DIMENSION_ONE Column7 ,Specimen_5.PATHOLOGICAL_STATUS Column8 ,Specimen_5.LINEAGE Column9 ,Specimen_5.LABEL Column10 ,Specimen_5.IDENTIFIER Column11 ,Specimen_5.COMMENTS Column12 ,Specimen_5.BARCODE Column13 ,Specimen_5.AVAILABLE Column14 ,Specimen_5.ACTIVITY_STATUS Column15 ,Specimen_1.TYPE Column16 ,Specimen_1.POSITION_DIMENSION_TWO Column17 ,Specimen_1.POSITION_DIMENSION_ONE Column18 ,Specimen_1.PATHOLOGICAL_STATUS Column19 ,Specimen_1.LINEAGE Column20 ,Specimen_1.LABEL Column21 ,Specimen_1.IDENTIFIER Column22 ,Specimen_1.COMMENTS Column23 ,Specimen_1.BARCODE Column24 ,Specimen_1.AVAILABLE Column25 ,Specimen_1.ACTIVITY_STATUS Column26 ,Specimen_2.TYPE Column27 ,Specimen_2.POSITION_DIMENSION_TWO Column28 ,Specimen_2.POSITION_DIMENSION_ONE Column29 ,Specimen_2.PATHOLOGICAL_STATUS Column30 ,Specimen_2.LINEAGE Column31 ,Specimen_2.LABEL Column32 ,Specimen_2.IDENTIFIER Column33 ,Specimen_2.COMMENTS Column34 ,Specimen_2.BARCODE Column35 ,Specimen_2.AVAILABLE Column36 ,Specimen_2.ACTIVITY_STATUS Column37 ,Specimen_4.TYPE Column38 ,Specimen_4.POSITION_DIMENSION_TWO Column39 ,Specimen_4.POSITION_DIMENSION_ONE Column40 ,Specimen_4.PATHOLOGICAL_STATUS Column41 ,Specimen_4.LINEAGE Column42 ,Specimen_4.LABEL Column43 ,Specimen_4.IDENTIFIER Column44 ,Specimen_4.COMMENTS Column45 ,Specimen_4.BARCODE Column46 ,Specimen_4.AVAILABLE Column47 ,Specimen_4.ACTIVITY_STATUS Column48 From catissue_specimen_coll_group SpecimenCollectionGroup_3 left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_1 on (Specimen_5.IDENTIFIER=Specimen_1.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_4 on (Specimen_2.IDENTIFIER=Specimen_4.PARENT_SPECIMEN_ID) Where (SpecimenCollectionGroup_3.ACTIVITY_STATUS!='Disabled') And((Specimen_5.TYPE='DNA' And Specimen_5.ACTIVITY_STATUS!='Disabled') Or(Specimen_1.TYPE='Amniotic Fluid' And Specimen_1.ACTIVITY_STATUS!='Disabled')) Or((Specimen_2.TYPE='RNA' And Specimen_2.ACTIVITY_STATUS!='Disabled') Or(Specimen_4.TYPE='Amniotic Fluid' And Specimen_4.ACTIVITY_STATUS!='Disabled'))",
                    sql);

        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test Query for Multiple parent case type.
     * 
     * <pre>
     * 			/-- S: type equals 'DNA'--\
     *    SCG:ANY						   |-- S: type equals 'Amniotic Fluid'
     *    		\-- S: type equals 'RNA'--/            \
     *    							\                   \
     *    							 \---------------- S: type equals 'milk'
     * </pre>
     */
    public void testCreateMultipleParentQuery2() {
        IQuery query = QueryGeneratorMock.createMultipleParentQuery2();
        int multipleParentExpressions = ((JoinGraph) query.getConstraints().getJoinGraph())
                .getNodesWithMultipleParents().size();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateMultipleParentQuery2:"+sql);
            multipleParentExpressions = ((JoinGraph) generator.constraints.getJoinGraph())
                    .getNodesWithMultipleParents().size();

            assertEquals("Multiple Parent Expression not resolved succesfuly!!!", 0, multipleParentExpressions);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct SpecimenCollectionGroup_5.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_5.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_5.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_5.NAME Column3 ,SpecimenCollectionGroup_5.IDENTIFIER Column4 ,Specimen_8.TYPE Column5 ,Specimen_8.POSITION_DIMENSION_TWO Column6 ,Specimen_8.POSITION_DIMENSION_ONE Column7 ,Specimen_8.PATHOLOGICAL_STATUS Column8 ,Specimen_8.LINEAGE Column9 ,Specimen_8.LABEL Column10 ,Specimen_8.IDENTIFIER Column11 ,Specimen_8.COMMENTS Column12 ,Specimen_8.BARCODE Column13 ,Specimen_8.AVAILABLE Column14 ,Specimen_8.ACTIVITY_STATUS Column15 ,Specimen_1.TYPE Column16 ,Specimen_1.POSITION_DIMENSION_TWO Column17 ,Specimen_1.POSITION_DIMENSION_ONE Column18 ,Specimen_1.PATHOLOGICAL_STATUS Column19 ,Specimen_1.LINEAGE Column20 ,Specimen_1.LABEL Column21 ,Specimen_1.IDENTIFIER Column22 ,Specimen_1.COMMENTS Column23 ,Specimen_1.BARCODE Column24 ,Specimen_1.AVAILABLE Column25 ,Specimen_1.ACTIVITY_STATUS Column26 ,Specimen_7.TYPE Column27 ,Specimen_7.POSITION_DIMENSION_TWO Column28 ,Specimen_7.POSITION_DIMENSION_ONE Column29 ,Specimen_7.PATHOLOGICAL_STATUS Column30 ,Specimen_7.LINEAGE Column31 ,Specimen_7.LABEL Column32 ,Specimen_7.IDENTIFIER Column33 ,Specimen_7.COMMENTS Column34 ,Specimen_7.BARCODE Column35 ,Specimen_7.AVAILABLE Column36 ,Specimen_7.ACTIVITY_STATUS Column37 ,Specimen_6.TYPE Column38 ,Specimen_6.POSITION_DIMENSION_TWO Column39 ,Specimen_6.POSITION_DIMENSION_ONE Column40 ,Specimen_6.PATHOLOGICAL_STATUS Column41 ,Specimen_6.LINEAGE Column42 ,Specimen_6.LABEL Column43 ,Specimen_6.IDENTIFIER Column44 ,Specimen_6.COMMENTS Column45 ,Specimen_6.BARCODE Column46 ,Specimen_6.AVAILABLE Column47 ,Specimen_6.ACTIVITY_STATUS Column48 ,Specimen_4.TYPE Column49 ,Specimen_4.POSITION_DIMENSION_TWO Column50 ,Specimen_4.POSITION_DIMENSION_ONE Column51 ,Specimen_4.PATHOLOGICAL_STATUS Column52 ,Specimen_4.LINEAGE Column53 ,Specimen_4.LABEL Column54 ,Specimen_4.IDENTIFIER Column55 ,Specimen_4.COMMENTS Column56 ,Specimen_4.BARCODE Column57 ,Specimen_4.AVAILABLE Column58 ,Specimen_4.ACTIVITY_STATUS Column59 ,Specimen_2.TYPE Column60 ,Specimen_2.POSITION_DIMENSION_TWO Column61 ,Specimen_2.POSITION_DIMENSION_ONE Column62 ,Specimen_2.PATHOLOGICAL_STATUS Column63 ,Specimen_2.LINEAGE Column64 ,Specimen_2.LABEL Column65 ,Specimen_2.IDENTIFIER Column66 ,Specimen_2.COMMENTS Column67 ,Specimen_2.BARCODE Column68 ,Specimen_2.AVAILABLE Column69 ,Specimen_2.ACTIVITY_STATUS Column70 ,Specimen_3.TYPE Column71 ,Specimen_3.POSITION_DIMENSION_TWO Column72 ,Specimen_3.POSITION_DIMENSION_ONE Column73 ,Specimen_3.PATHOLOGICAL_STATUS Column74 ,Specimen_3.LINEAGE Column75 ,Specimen_3.LABEL Column76 ,Specimen_3.IDENTIFIER Column77 ,Specimen_3.COMMENTS Column78 ,Specimen_3.BARCODE Column79 ,Specimen_3.AVAILABLE Column80 ,Specimen_3.ACTIVITY_STATUS Column81 From catissue_specimen_coll_group SpecimenCollectionGroup_5 left join catissue_specimen Specimen_8 on (SpecimenCollectionGroup_5.IDENTIFIER=Specimen_8.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_1 on (Specimen_8.IDENTIFIER=Specimen_1.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_7 on (Specimen_1.IDENTIFIER=Specimen_7.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_6 on (Specimen_8.IDENTIFIER=Specimen_6.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_5.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_2 on (Specimen_4.IDENTIFIER=Specimen_2.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_3 on (Specimen_2.IDENTIFIER=Specimen_3.PARENT_SPECIMEN_ID) Where (SpecimenCollectionGroup_5.ACTIVITY_STATUS!='Disabled') And((Specimen_8.TYPE='DNA' And Specimen_8.ACTIVITY_STATUS!='Disabled') Or((Specimen_1.TYPE='Amniotic Fluid' And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_7.TYPE='Milk' And Specimen_7.ACTIVITY_STATUS!='Disabled')) Or(Specimen_6.TYPE='Milk' And Specimen_6.ACTIVITY_STATUS!='Disabled')) Or((Specimen_4.TYPE='RNA' And Specimen_4.ACTIVITY_STATUS!='Disabled') Or((Specimen_2.TYPE='Amniotic Fluid' And Specimen_2.ACTIVITY_STATUS!='Disabled') Or(Specimen_3.TYPE='Milk' And Specimen_3.ACTIVITY_STATUS!='Disabled')))",
                    sql);

        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test Query for Multiple parent case type with the nesting numbers.
     * 
     * <pre>
     * 			/-- S: type equals ('DNA' Or 'cDNA')------------\
     *    SCG:ANY						  						 |-- S: type equals 'Amniotic Fluid' ---&gt; S: type equals 'Milk'
     *    		\-- S: type equals 'RNA' Or (type equals 'RNA, cytoplasmic' or ChildExp)--/
     * </pre>
     * 
     * @return reference to the query object.
     */
    public void testCreateMultipleParentQuery3() {
        IQuery query = QueryGeneratorMock.createMultipleParentQuery3();
        int multipleParentExpressions = ((JoinGraph) query.getConstraints().getJoinGraph())
                .getNodesWithMultipleParents().size();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateMultipleParentQuery3:"+sql);

            multipleParentExpressions = ((JoinGraph) generator.constraints.getJoinGraph())
                    .getNodesWithMultipleParents().size();
            assertEquals("Multiple Parent Expression not resolved succesfuly!!!", 0, multipleParentExpressions);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct SpecimenCollectionGroup_4.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_4.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_4.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_4.NAME Column3 ,SpecimenCollectionGroup_4.IDENTIFIER Column4 ,Specimen_7.TYPE Column5 ,Specimen_7.POSITION_DIMENSION_TWO Column6 ,Specimen_7.POSITION_DIMENSION_ONE Column7 ,Specimen_7.PATHOLOGICAL_STATUS Column8 ,Specimen_7.LINEAGE Column9 ,Specimen_7.LABEL Column10 ,Specimen_7.IDENTIFIER Column11 ,Specimen_7.COMMENTS Column12 ,Specimen_7.BARCODE Column13 ,Specimen_7.AVAILABLE Column14 ,Specimen_7.ACTIVITY_STATUS Column15 ,Specimen_1.TYPE Column16 ,Specimen_1.POSITION_DIMENSION_TWO Column17 ,Specimen_1.POSITION_DIMENSION_ONE Column18 ,Specimen_1.PATHOLOGICAL_STATUS Column19 ,Specimen_1.LINEAGE Column20 ,Specimen_1.LABEL Column21 ,Specimen_1.IDENTIFIER Column22 ,Specimen_1.COMMENTS Column23 ,Specimen_1.BARCODE Column24 ,Specimen_1.AVAILABLE Column25 ,Specimen_1.ACTIVITY_STATUS Column26 ,Specimen_5.TYPE Column27 ,Specimen_5.POSITION_DIMENSION_TWO Column28 ,Specimen_5.POSITION_DIMENSION_ONE Column29 ,Specimen_5.PATHOLOGICAL_STATUS Column30 ,Specimen_5.LINEAGE Column31 ,Specimen_5.LABEL Column32 ,Specimen_5.IDENTIFIER Column33 ,Specimen_5.COMMENTS Column34 ,Specimen_5.BARCODE Column35 ,Specimen_5.AVAILABLE Column36 ,Specimen_5.ACTIVITY_STATUS Column37 ,Specimen_3.TYPE Column38 ,Specimen_3.POSITION_DIMENSION_TWO Column39 ,Specimen_3.POSITION_DIMENSION_ONE Column40 ,Specimen_3.PATHOLOGICAL_STATUS Column41 ,Specimen_3.LINEAGE Column42 ,Specimen_3.LABEL Column43 ,Specimen_3.IDENTIFIER Column44 ,Specimen_3.COMMENTS Column45 ,Specimen_3.BARCODE Column46 ,Specimen_3.AVAILABLE Column47 ,Specimen_3.ACTIVITY_STATUS Column48 ,Specimen_2.TYPE Column49 ,Specimen_2.POSITION_DIMENSION_TWO Column50 ,Specimen_2.POSITION_DIMENSION_ONE Column51 ,Specimen_2.PATHOLOGICAL_STATUS Column52 ,Specimen_2.LINEAGE Column53 ,Specimen_2.LABEL Column54 ,Specimen_2.IDENTIFIER Column55 ,Specimen_2.COMMENTS Column56 ,Specimen_2.BARCODE Column57 ,Specimen_2.AVAILABLE Column58 ,Specimen_2.ACTIVITY_STATUS Column59 ,Specimen_6.TYPE Column60 ,Specimen_6.POSITION_DIMENSION_TWO Column61 ,Specimen_6.POSITION_DIMENSION_ONE Column62 ,Specimen_6.PATHOLOGICAL_STATUS Column63 ,Specimen_6.LINEAGE Column64 ,Specimen_6.LABEL Column65 ,Specimen_6.IDENTIFIER Column66 ,Specimen_6.COMMENTS Column67 ,Specimen_6.BARCODE Column68 ,Specimen_6.AVAILABLE Column69 ,Specimen_6.ACTIVITY_STATUS Column70 From catissue_specimen_coll_group SpecimenCollectionGroup_4 left join catissue_specimen Specimen_7 on (SpecimenCollectionGroup_4.IDENTIFIER=Specimen_7.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_1 on (Specimen_7.IDENTIFIER=Specimen_1.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_5 on (Specimen_1.IDENTIFIER=Specimen_5.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_3 on (SpecimenCollectionGroup_4.IDENTIFIER=Specimen_3.SPECIMEN_COLLECTION_GROUP_ID) left join catissue_specimen Specimen_2 on (Specimen_3.IDENTIFIER=Specimen_2.PARENT_SPECIMEN_ID) left join catissue_specimen Specimen_6 on (Specimen_2.IDENTIFIER=Specimen_6.PARENT_SPECIMEN_ID) Where (SpecimenCollectionGroup_4.ACTIVITY_STATUS!='Disabled') And(((Specimen_7.TYPE='DNA' And Specimen_7.ACTIVITY_STATUS!='Disabled') Or(Specimen_7.TYPE='cDNA' And Specimen_7.ACTIVITY_STATUS!='Disabled')) Or((Specimen_1.TYPE='Amniotic Fluid' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(Specimen_5.TYPE='Milk' And Specimen_5.ACTIVITY_STATUS!='Disabled'))) Or((Specimen_3.TYPE='RNA' And Specimen_3.ACTIVITY_STATUS!='Disabled') Or((Specimen_3.TYPE='RNA, cytoplasmic' And Specimen_3.ACTIVITY_STATUS!='Disabled') Or((Specimen_2.TYPE='Amniotic Fluid' And Specimen_2.ACTIVITY_STATUS!='Disabled') And(Specimen_6.TYPE='Milk' And Specimen_6.ACTIVITY_STATUS!='Disabled'))))",
                    sql);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * TO test query for the TABLE_PER_SUB_CLASS inheritance strategy. Query for
     * Collection protocol class as: cp.aliquotInSameContainer = 'True' and
     * cp.activityStatus = 'Active' Here, 1. aliquotInSameContainer attribute is
     * in the derived class i.e. Collection Protocol. 2. activityStatus
     * attribute is in the base class of Collection Protocol i.e.
     * SpecimenProtocol.
     */
    public void testCreateInheritanceQuery1() {
        IQuery query = QueryGeneratorMock.createInheritanceQuery1();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateInheritanceQuery1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct SpecimenProtocol_1.TITLE Column0 ,SpecimenProtocol_1.START_DATE Column1 ,SpecimenProtocol_1.SHORT_TITLE Column2 ,SpecimenProtocol_1.IRB_IDENTIFIER Column3 ,SpecimenProtocol_1.IDENTIFIER Column4 ,SpecimenProtocol_1.ENROLLMENT Column5 ,SpecimenProtocol_1.END_DATE Column6 ,SpecimenProtocol_1.DESCRIPTION_URL Column7 ,SpecimenProtocol_1.ACTIVITY_STATUS Column8 ,CollectionProtocol_1.ALIQUOT_IN_SAME_CONTAINER Column9 From catissue_collection_protocol CollectionProtocol_1 left join catissue_specimen_protocol SpecimenProtocol_1 on (CollectionProtocol_1.IDENTIFIER=SpecimenProtocol_1.IDENTIFIER) Where CollectionProtocol_1.ALIQUOT_IN_SAME_CONTAINER=1 And SpecimenProtocol_1.ACTIVITY_STATUS='Active' And SpecimenProtocol_1.ACTIVITY_STATUS!='Disabled'",
                    sql);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * TO test query for the TABLE_PER_HEIRARCHY inheritance strategy. Query for
     * Molecular Specimen class as: sp.label contains "1.2"
     */
    public void testCreateInheritanceQuery2() {
        IQuery query = QueryGeneratorMock.createInheritanceQuery2();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateInheritanceQuery2:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Specimen_1.CONCENTRATION Column11 From catissue_specimen Specimen_1 Where (Specimen_1.LABEL like '%1.2%' And Specimen_1.ACTIVITY_STATUS!='Disabled') And Specimen_1.SPECIMEN_CLASS='Molecular'",
                    sql);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * TO test query for the TABLE_PER_SUBCLASS with multilevel inheritance.
     */
    public void testCreateInheritanceQueryLevel4() {
        IQuery query = QueryGeneratorMock.createInheritanceQueryLevel4();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateInheritanceQueryLevel4:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct Level1DE_1.LEVEL1 Column0 ,Level1DE_1.IDENTIFIER Column1 ,Level2DE_1.LEVEL2 Column2 ,Level3DE_1.LEVEL3 Column3 ,Level4DE_1.LEVEL4 Column4 From DE_LEVEL4 Level4DE_1 left join DE_LEVEL3 Level3DE_1 on (Level4DE_1.IDENTIFIER=Level3DE_1.IDENTIFIER) left join DE_LEVEL2 Level2DE_1 on (Level3DE_1.IDENTIFIER=Level2DE_1.IDENTIFIER) left join DE_LEVEL1 Level1DE_1 on (Level2DE_1.IDENTIFIER=Level1DE_1.IDENTIFIER) Where Level4DE_1.LEVEL4 like 's%'",
                    sql);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * TO test query for the TABLE_PER_SUBCLASS with multilevel inheritance.
     */
    public void testCreateInheritanceQueryLevel4StaticToDE() {
        IQuery query = QueryGeneratorMock.createInheritanceQueryLevel4StaticToDE();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateInheritanceQueryLevel4StaticToDE:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct Participant_1.VITAL_STATUS Column0 ,Participant_1.SOCIAL_SECURITY_NUMBER Column1 ,Participant_1.GENOTYPE Column2 ,Participant_1.MIDDLE_NAME Column3 ,Participant_1.LAST_NAME Column4 ,Participant_1.IDENTIFIER Column5 ,Participant_1.GENDER Column6 ,Participant_1.FIRST_NAME Column7 ,Participant_1.ETHNICITY Column8 ,Participant_1.DEATH_DATE Column9 ,Participant_1.BIRTH_DATE Column10 ,Participant_1.ACTIVITY_STATUS Column11 ,Level1DE_2.LEVEL1 Column12 ,Level1DE_2.IDENTIFIER Column13 ,Level2DE_2.LEVEL2 Column14 ,Level3DE_2.LEVEL3 Column15 ,Level4DE_2.LEVEL4 Column16 From catissue_participant Participant_1 left join DE_LEVEL4 Level4DE_2 on (Participant_1.IDENTIFIER=Level4DE_2.PARTICIPANT_ID) inner join DE_LEVEL3 Level3DE_2 on (Level4DE_2.IDENTIFIER=Level3DE_2.IDENTIFIER) inner join DE_LEVEL2 Level2DE_2 on (Level3DE_2.IDENTIFIER=Level2DE_2.IDENTIFIER) inner join DE_LEVEL1 Level1DE_2 on (Level2DE_2.IDENTIFIER=Level1DE_2.IDENTIFIER) Where (Participant_1.ACTIVITY_STATUS!='Disabled') And(Level4DE_2.LEVEL4 like 's%')",
                    sql);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * TO create query for the TABLE_PER_HEIRARCHY inheritance strategy with
     * multilevel inheritance. Query for Cell Specimen Review event parameter
     * class as: srp.viableCellPercentage > 50 and srp.comments contains 'xyz'
     * Here, viableCellPercentage: belongs to the base class comments: belongs
     * to the super class.
     */
    public void testCreateInheritanceQuery3() {
        IQuery query = QueryGeneratorMock.createInheritanceQuery3();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateInheritanceQuery3:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query !!!",
                    "Select distinct SpecimenEventParameters_1.COMMENTS Column0 ,SpecimenEventParameters_1.EVENT_TIMESTAMP Column1 ,SpecimenEventParameters_1.IDENTIFIER Column2 ,CellSpecimenReviewParamet_1.VIABLE_CELL_PERCENTAGE Column3 ,CellSpecimenReviewParamet_1.NEOPLASTIC_CELLULARITY_PER Column4 From CATISSUE_CELL_SPE_REVIEW_PARAM CellSpecimenReviewParamet_1 left join catissue_event_param ReviewEventParameters_1 on (CellSpecimenReviewParamet_1.IDENTIFIER=ReviewEventParameters_1.IDENTIFIER) left join catissue_specimen_event_param SpecimenEventParameters_1 on (ReviewEventParameters_1.IDENTIFIER=SpecimenEventParameters_1.IDENTIFIER) Where CellSpecimenReviewParamet_1.VIABLE_CELL_PERCENTAGE>50 And SpecimenEventParameters_1.COMMENTS like '%xyz%'",
                    sql);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test the Query having empty expressions.
     * 
     */
    public void testSampleQuery1WithEmptyExp() {
        IQuery query = QueryGeneratorMock.createSampleQuery1WithEmptyExp();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery1WithEmptyExp:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_7.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_7.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_7.IDENTIFIER Column14 ,CollectionProtocolRegistr_7.ACTIVITY_STATUS Column15 ,SpecimenCollectionGroup_3.ACTIVITY_STATUS Column16 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column17 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column18 ,SpecimenCollectionGroup_3.NAME Column19 ,SpecimenCollectionGroup_3.IDENTIFIER Column20 ,Specimen_1.TYPE Column21 ,Specimen_1.POSITION_DIMENSION_TWO Column22 ,Specimen_1.POSITION_DIMENSION_ONE Column23 ,Specimen_1.PATHOLOGICAL_STATUS Column24 ,Specimen_1.LINEAGE Column25 ,Specimen_1.LABEL Column26 ,Specimen_1.IDENTIFIER Column27 ,Specimen_1.COMMENTS Column28 ,Specimen_1.BARCODE Column29 ,Specimen_1.AVAILABLE Column30 ,Specimen_1.ACTIVITY_STATUS Column31 ,Specimen_5.TYPE Column32 ,Specimen_5.POSITION_DIMENSION_TWO Column33 ,Specimen_5.POSITION_DIMENSION_ONE Column34 ,Specimen_5.PATHOLOGICAL_STATUS Column35 ,Specimen_5.LINEAGE Column36 ,Specimen_5.LABEL Column37 ,Specimen_5.IDENTIFIER Column38 ,Specimen_5.COMMENTS Column39 ,Specimen_5.BARCODE Column40 ,Specimen_5.AVAILABLE Column41 ,Specimen_5.ACTIVITY_STATUS Column42 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column43 ,ParticipantMedicalIdentif_2.IDENTIFIER Column44 ,Site_6.ACTIVITY_STATUS Column45 ,Site_6.EMAIL_ADDRESS Column46 ,Site_6.TYPE Column47 ,Site_6.NAME Column48 ,Site_6.IDENTIFIER Column49 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_7 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_7.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_7.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID And Specimen_5.SPECIMEN_CLASS='Tissue') left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) left join catissue_site Site_6 on (ParticipantMedicalIdentif_2.SITE_ID=Site_6.IDENTIFIER) Where (Participant_4.LAST_NAME like 's%' And Participant_4.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_7.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_3.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_5.TYPE='Fresh Tissue' And Specimen_5.ACTIVITY_STATUS!='Disabled'))) And(Site_6.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test the Query having empty expressions. Expression: P AND CPR1 AND
     * PM1 AND PM2 AND CPR2 AND PM3 The where part should have conditions as: P
     * AND CPR1 AND CPR2
     */

    public void testQueryWithEmptyExp() {
        IQuery query = QueryGeneratorMock.createQueryWithEmptyExp();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testQueryWithEmptyExp:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression !!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_6.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_6.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_6.IDENTIFIER Column14 ,CollectionProtocolRegistr_6.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 ,ParticipantMedicalIdentif_1.MEDICAL_RECORD_NUMBER Column18 ,ParticipantMedicalIdentif_1.IDENTIFIER Column19 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column20 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column21 ,CollectionProtocolRegistr_5.IDENTIFIER Column22 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column23 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column24 ,ParticipantMedicalIdentif_2.IDENTIFIER Column25 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_6 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_6.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_1 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_1.PARTICIPANT_ID) left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_4.FIRST_NAME='Prafull' And Participant_4.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_6.ACTIVITY_STATUS='Active' And CollectionProtocolRegistr_6.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_5.ACTIVITY_STATUS='Disabled' And CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To test the Query having empty expressions & parenthesis. Expression: (P
     * AND CPR1 AND PM1) AND PM2 AND CPR2 AND PM3 The where part should have
     * conditions as: (P AND CPR1) AND CPR2
     */
    public void testQueryWithEmptyExpWithParenthesis1() {
        IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis1();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testQueryWithEmptyExpWithParenthesis1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_6.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_6.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_6.IDENTIFIER Column14 ,CollectionProtocolRegistr_6.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 ,ParticipantMedicalIdentif_1.MEDICAL_RECORD_NUMBER Column18 ,ParticipantMedicalIdentif_1.IDENTIFIER Column19 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column20 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column21 ,CollectionProtocolRegistr_5.IDENTIFIER Column22 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column23 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column24 ,ParticipantMedicalIdentif_2.IDENTIFIER Column25 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_6 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_6.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_1 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_1.PARTICIPANT_ID) left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where ((Participant_4.FIRST_NAME='Prafull' And Participant_4.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_6.ACTIVITY_STATUS='Active' And CollectionProtocolRegistr_6.ACTIVITY_STATUS!='Disabled')) And(CollectionProtocolRegistr_5.ACTIVITY_STATUS='Disabled' And CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
        }
    }

    /**
     * To test the Query having empty expressions & parenthesis. Expression: ((P
     * AND CPR1) AND PM1 AND PM2 AND CPR2) AND PM3 The where part should have
     * conditions as: ((P AND CPR1) AND CPR2)
     */
    public void testQueryWithEmptyExpWithParenthesis2() {
        IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis2();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testQueryWithEmptyExpWithParenthesis2:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_6.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_6.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_6.IDENTIFIER Column14 ,CollectionProtocolRegistr_6.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 ,ParticipantMedicalIdentif_1.MEDICAL_RECORD_NUMBER Column18 ,ParticipantMedicalIdentif_1.IDENTIFIER Column19 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column20 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column21 ,CollectionProtocolRegistr_5.IDENTIFIER Column22 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column23 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column24 ,ParticipantMedicalIdentif_2.IDENTIFIER Column25 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_6 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_6.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_1 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_1.PARTICIPANT_ID) left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (((Participant_4.FIRST_NAME='Prafull' And Participant_4.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_6.ACTIVITY_STATUS='Active' And CollectionProtocolRegistr_6.ACTIVITY_STATUS!='Disabled')) And(CollectionProtocolRegistr_5.ACTIVITY_STATUS='Disabled' And CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled'))",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
        }
    }

    /**
     * To test the Query having empty expressions & parenthesis. Expression:
     * (((P AND CPR1 AND PM1 AND PM2) AND CPR2) AND PM3) The where part should
     * have conditions as: (((P AND CPR1) AND CPR2))
     */
    public void testQueryWithEmptyExpWithParenthesis3() {
        IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis3();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testQueryWithEmptyExpWithParenthesis3:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_6.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_6.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_6.IDENTIFIER Column14 ,CollectionProtocolRegistr_6.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 ,ParticipantMedicalIdentif_1.MEDICAL_RECORD_NUMBER Column18 ,ParticipantMedicalIdentif_1.IDENTIFIER Column19 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column20 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column21 ,CollectionProtocolRegistr_5.IDENTIFIER Column22 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column23 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column24 ,ParticipantMedicalIdentif_2.IDENTIFIER Column25 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_6 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_6.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_1 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_1.PARTICIPANT_ID) left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where ((((Participant_4.FIRST_NAME='Prafull' And Participant_4.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_6.ACTIVITY_STATUS='Active' And CollectionProtocolRegistr_6.ACTIVITY_STATUS!='Disabled')) And(CollectionProtocolRegistr_5.ACTIVITY_STATUS='Disabled' And CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled')))",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
        }
    }

    /**
     * To test the Query having empty expressions & parenthesis. Expression: P
     * AND (CPR1 AND PM1 AND PM2 AND CPR2) AND PM3 The where part should have
     * conditions as: P AND (CPR1 AND CPR2)
     */
    public void testQueryWithEmptyExpWithParenthesis4() {
        IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis4();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testQueryWithEmptyExpWithParenthesis4:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_6.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_6.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_6.IDENTIFIER Column14 ,CollectionProtocolRegistr_6.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 ,ParticipantMedicalIdentif_1.MEDICAL_RECORD_NUMBER Column18 ,ParticipantMedicalIdentif_1.IDENTIFIER Column19 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column20 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column21 ,CollectionProtocolRegistr_5.IDENTIFIER Column22 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column23 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column24 ,ParticipantMedicalIdentif_2.IDENTIFIER Column25 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_6 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_6.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_1 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_1.PARTICIPANT_ID) left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_4.FIRST_NAME='Prafull' And Participant_4.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_6.ACTIVITY_STATUS='Active' And CollectionProtocolRegistr_6.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_5.ACTIVITY_STATUS='Disabled' And CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled'))",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
        }
    }

    /**
     * To test the Query having empty expressions & parenthesis. Expression: P
     * AND CPR1 AND (PM1 AND PM2) AND CPR2 AND PM3 The where part should have
     * conditions as: P AND CPR1 AND CPR2
     */
    public void testQueryWithEmptyExpWithParenthesis5() {
        IQuery query = QueryGeneratorMock.createQueryWithEmptyExpWithParenthesis5();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testQueryWithEmptyExpWithParenthesis5:"+sql);
            assertEquals(
                    "Incorrect SQL formed for From clause of the Expression the Query with Empty Expressions with Parenthesis!!!",
                    "Select distinct Participant_4.VITAL_STATUS Column0 ,Participant_4.SOCIAL_SECURITY_NUMBER Column1 ,Participant_4.GENOTYPE Column2 ,Participant_4.MIDDLE_NAME Column3 ,Participant_4.LAST_NAME Column4 ,Participant_4.IDENTIFIER Column5 ,Participant_4.GENDER Column6 ,Participant_4.FIRST_NAME Column7 ,Participant_4.ETHNICITY Column8 ,Participant_4.DEATH_DATE Column9 ,Participant_4.BIRTH_DATE Column10 ,Participant_4.ACTIVITY_STATUS Column11 ,CollectionProtocolRegistr_6.REGISTRATION_DATE Column12 ,CollectionProtocolRegistr_6.PROTOCOL_PARTICIPANT_ID Column13 ,CollectionProtocolRegistr_6.IDENTIFIER Column14 ,CollectionProtocolRegistr_6.ACTIVITY_STATUS Column15 ,ParticipantMedicalIdentif_3.MEDICAL_RECORD_NUMBER Column16 ,ParticipantMedicalIdentif_3.IDENTIFIER Column17 ,ParticipantMedicalIdentif_1.MEDICAL_RECORD_NUMBER Column18 ,ParticipantMedicalIdentif_1.IDENTIFIER Column19 ,CollectionProtocolRegistr_5.REGISTRATION_DATE Column20 ,CollectionProtocolRegistr_5.PROTOCOL_PARTICIPANT_ID Column21 ,CollectionProtocolRegistr_5.IDENTIFIER Column22 ,CollectionProtocolRegistr_5.ACTIVITY_STATUS Column23 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column24 ,ParticipantMedicalIdentif_2.IDENTIFIER Column25 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_6 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_6.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_3 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_3.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_1 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_1.PARTICIPANT_ID) left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_4.FIRST_NAME='Prafull' And Participant_4.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_6.ACTIVITY_STATUS='Active' And CollectionProtocolRegistr_6.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_5.ACTIVITY_STATUS='Disabled' And CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            fail("Unexpected Expection, While Generating SQL for the Query with Empty Expressions with Parenthesis!!!");
        }
    }

    /**
     * 
     * To Test the SQL for sample query no. 1 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc".
     * 
     * <pre>
     *  P: LastNameStarts with 'S'
     *  	C: ANY
     *  		G: ANY
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fixed Tissue&quot;
     *  					OR
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fresh Tissue&quot; 
     *  
     * Defining Result view as:
     * 	P:
     * 		G:
     * </pre>
     */
    public void testSampleQuery1WithSelectView1() {
        IQuery query = QueryGeneratorMock.createSampleQuery1WithSelectView1();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery1WithSelectView1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query with output view!!!",
                    "Select distinct Participant_3.VITAL_STATUS Column0 ,Participant_3.SOCIAL_SECURITY_NUMBER Column1 ,Participant_3.GENOTYPE Column2 ,Participant_3.MIDDLE_NAME Column3 ,Participant_3.LAST_NAME Column4 ,Participant_3.IDENTIFIER Column5 ,Participant_3.GENDER Column6 ,Participant_3.FIRST_NAME Column7 ,Participant_3.ETHNICITY Column8 ,Participant_3.DEATH_DATE Column9 ,Participant_3.BIRTH_DATE Column10 ,Participant_3.ACTIVITY_STATUS Column11 ,SpecimenCollectionGroup_2.ACTIVITY_STATUS Column12 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column13 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column14 ,SpecimenCollectionGroup_2.NAME Column15 ,SpecimenCollectionGroup_2.IDENTIFIER Column16 From catissue_participant Participant_3 left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_3.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_5.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') Where (Participant_3.LAST_NAME like 's%' And Participant_3.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_4.TYPE='Fresh Tissue' And Specimen_4.ACTIVITY_STATUS!='Disabled')))",
                    sql);

            assertEquals("Incorrect number of output trees Formed while generating SQL !!!", 1, generator
                    .getRootOutputTreeNodeList().size());
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To Test the SQL for sample query no. 1 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc".
     * 
     * <pre>
     *  P: LastNameStarts with 'S'
     *  	C: ANY
     *  		G: ANY
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fixed Tissue&quot;
     *  					OR
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fresh Tissue&quot; 
     *  
     * Defining Result view as:
     * 	S:
     * </pre>
     */
    public void testSampleQuery1WithSelectView2() {
        IQuery query = QueryGeneratorMock.createSampleQuery1WithSelectView2();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery1WithSelectView2:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query with output view !!!",
                    "Select distinct Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Specimen_4.TYPE Column11 ,Specimen_4.POSITION_DIMENSION_TWO Column12 ,Specimen_4.POSITION_DIMENSION_ONE Column13 ,Specimen_4.PATHOLOGICAL_STATUS Column14 ,Specimen_4.LINEAGE Column15 ,Specimen_4.LABEL Column16 ,Specimen_4.IDENTIFIER Column17 ,Specimen_4.COMMENTS Column18 ,Specimen_4.BARCODE Column19 ,Specimen_4.AVAILABLE Column20 ,Specimen_4.ACTIVITY_STATUS Column21 From catissue_participant Participant_3 left join catissue_coll_prot_reg CollectionProtocolRegistr_5 on (Participant_3.IDENTIFIER=CollectionProtocolRegistr_5.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_5.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') left join catissue_specimen Specimen_4 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_4.SPECIMEN_COLLECTION_GROUP_ID And Specimen_4.SPECIMEN_CLASS='Tissue') Where (Participant_3.LAST_NAME like 's%' And Participant_3.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_5.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_4.TYPE='Fresh Tissue' And Specimen_4.ACTIVITY_STATUS!='Disabled')))",
                    sql);

            assertEquals("Incorrect number of output trees Formed while generating SQL !!!", 2, generator
                    .getRootOutputTreeNodeList().size());
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * 
     * To Test the SQL for sample query no. 1 in the
     * "SampleQueriesWithMultipleSubQueryApproach.doc".
     * 
     * <pre>
     * 	P: LastNameStarts with 'S'
     * <P>
     * 		PM: medicalRecordNumber equals 'M001'
     * 		AND
     *  	C: ANY
     *  		G: ANY
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fixed Tissue&quot;
     *  					OR
     *  			S: Class equals &quot;Tissue&quot; AND Type equals &quot;Fresh Tissue&quot; 
     * </pre>
     * 
     * Setting PM, G & S node in output tree, Resulting into 2 output trees
     * which are"
     * 
     * <pre>
     * 1. First tree:
     * 		G:
     * 			S:  	
     * 2. Second tree:
     * 		PM
     * </pre>
     */
    public void testSampleQuery1WithSelectView3() {
        IQuery query = QueryGeneratorMock.createSampleQuery1WithSelectView3();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testSampleQuery1WithSelectView3:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query with output view !!!",
                    "Select distinct SpecimenCollectionGroup_3.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_3.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_3.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_3.NAME Column3 ,SpecimenCollectionGroup_3.IDENTIFIER Column4 ,Specimen_1.TYPE Column5 ,Specimen_1.POSITION_DIMENSION_TWO Column6 ,Specimen_1.POSITION_DIMENSION_ONE Column7 ,Specimen_1.PATHOLOGICAL_STATUS Column8 ,Specimen_1.LINEAGE Column9 ,Specimen_1.LABEL Column10 ,Specimen_1.IDENTIFIER Column11 ,Specimen_1.COMMENTS Column12 ,Specimen_1.BARCODE Column13 ,Specimen_1.AVAILABLE Column14 ,Specimen_1.ACTIVITY_STATUS Column15 ,Specimen_5.TYPE Column16 ,Specimen_5.POSITION_DIMENSION_TWO Column17 ,Specimen_5.POSITION_DIMENSION_ONE Column18 ,Specimen_5.PATHOLOGICAL_STATUS Column19 ,Specimen_5.LINEAGE Column20 ,Specimen_5.LABEL Column21 ,Specimen_5.IDENTIFIER Column22 ,Specimen_5.COMMENTS Column23 ,Specimen_5.BARCODE Column24 ,Specimen_5.AVAILABLE Column25 ,Specimen_5.ACTIVITY_STATUS Column26 ,ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER Column27 ,ParticipantMedicalIdentif_2.IDENTIFIER Column28 From catissue_participant Participant_4 left join catissue_coll_prot_reg CollectionProtocolRegistr_6 on (Participant_4.IDENTIFIER=CollectionProtocolRegistr_6.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_3 on (CollectionProtocolRegistr_6.IDENTIFIER=SpecimenCollectionGroup_3.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') left join catissue_specimen Specimen_5 on (SpecimenCollectionGroup_3.IDENTIFIER=Specimen_5.SPECIMEN_COLLECTION_GROUP_ID And Specimen_5.SPECIMEN_CLASS='Tissue') left join catissue_part_medical_id ParticipantMedicalIdentif_2 on (Participant_4.IDENTIFIER=ParticipantMedicalIdentif_2.PARTICIPANT_ID) Where (Participant_4.LAST_NAME like 's%' And Participant_4.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_6.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_3.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled') Or(Specimen_5.TYPE='Fresh Tissue' And Specimen_5.ACTIVITY_STATUS!='Disabled'))) And(ParticipantMedicalIdentif_2.MEDICAL_RECORD_NUMBER='M001')",
                    sql);

            // Map<OutputTreeDataNode, Map<Long, Map<AttributeInterface,
            // String>>> outputTreeMap = generator.getOutputTreeMap();
            //			
            // Set<OutputTreeDataNode> keySet = outputTreeMap.keySet();
            assertEquals("Incorrect number of output trees Formed while generating SQL !!!", 2, generator
                    .getRootOutputTreeNodeList().size());
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    // TODO Add Few More Positive & Negative testcases for Select View.

    /**
     * To test queries having many to many associations.
     * 
     * <pre>
     *  S: Type equals &quot;DNA&quot;
     *  	Biohazard: type equals &quot;Toxic&quot;
     * </pre>
     * 
     */
    public void testManyToManyQuery1() {
        IQuery query = QueryGeneratorMock.createSpecimenBioHazardQuery1();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testManyToManyQuery1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for the Query having Many to Many Associations!!!",
                    "Select distinct Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Biohazard_2.TYPE Column11 ,Biohazard_2.COMMENTS Column12 ,Biohazard_2.NAME Column13 ,Biohazard_2.IDENTIFIER Column14 From catissue_specimen Specimen_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUESPECIMENBIOHZR_2 on (Specimen_1.IDENTIFIER=CATISSUESPECIMENBIOHZR_2.SPECIMEN_ID And Specimen_1.SPECIMEN_CLASS='Fluid') left join CATISSUE_BIOHAZARD Biohazard_2 on (CATISSUESPECIMENBIOHZR_2.BIOHAZARD_ID=Biohazard_2.IDENTIFIER) Where (Specimen_1.TYPE='DNA' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(Biohazard_2.TYPE='Toxic')",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query having Many to Many Associations!!!");
        }
    }

    /**
     * To test queries having many to many associations & Pseudo And.
     * 
     * <pre>
     *  S: Type equals &quot;DNA&quot;
     *  	Biohazard: type equals &quot;Toxic&quot;
     *  	Pseudo AND
     *  	Biohazard: type equals &quot;Radioactive&quot;
     * </pre>
     */
    public void testManyToManyQuery2() {
        IQuery query = QueryGeneratorMock.createSpecimenBioHazardQuery2();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testManyToManyQuery2:"+sql);
            assertEquals(
                    "Incorrect SQL formed for the Query having Many to Many Associations & Pseudo And!!!",
                    "Select distinct Specimen_2.TYPE Column0 ,Specimen_2.POSITION_DIMENSION_TWO Column1 ,Specimen_2.POSITION_DIMENSION_ONE Column2 ,Specimen_2.PATHOLOGICAL_STATUS Column3 ,Specimen_2.LINEAGE Column4 ,Specimen_2.LABEL Column5 ,Specimen_2.IDENTIFIER Column6 ,Specimen_2.COMMENTS Column7 ,Specimen_2.BARCODE Column8 ,Specimen_2.AVAILABLE Column9 ,Specimen_2.ACTIVITY_STATUS Column10 ,Biohazard_3.TYPE Column11 ,Biohazard_3.COMMENTS Column12 ,Biohazard_3.NAME Column13 ,Biohazard_3.IDENTIFIER Column14 ,Biohazard_1.TYPE Column15 ,Biohazard_1.COMMENTS Column16 ,Biohazard_1.NAME Column17 ,Biohazard_1.IDENTIFIER Column18 From catissue_specimen Specimen_2 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUESPECIMENBIOHZR_3 on (Specimen_2.IDENTIFIER=CATISSUESPECIMENBIOHZR_3.SPECIMEN_ID And Specimen_2.SPECIMEN_CLASS='Fluid') left join CATISSUE_BIOHAZARD Biohazard_3 on (CATISSUESPECIMENBIOHZR_3.BIOHAZARD_ID=Biohazard_3.IDENTIFIER) left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUESPECIMENBIOHZR_1 on (Specimen_2.IDENTIFIER=CATISSUESPECIMENBIOHZR_1.SPECIMEN_ID) left join CATISSUE_BIOHAZARD Biohazard_1 on (CATISSUESPECIMENBIOHZR_1.BIOHAZARD_ID=Biohazard_1.IDENTIFIER) Where (Specimen_2.TYPE='DNA' And Specimen_2.ACTIVITY_STATUS!='Disabled') And(Biohazard_3.TYPE='Toxic') And(Biohazard_1.TYPE='Radioactive')",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query having Many to Many Associations & Pseudo And!!!");
        }
    }

    /**
     * To test queries having many to many associations & Pseudo And.
     * 
     * <pre>
     * Biohazard: type equals &quot;Toxic&quot;
     *  	S: Type equals &quot;DNA&quot;
     *  		SP CHAR: tissueSite equals &quot;skin&quot;
     *  	Pseudo AND
     *  	S: Type equals &quot;RNA&quot;
     *  		SP CHAR: tissueSite equals &quot;Spinal cord&quot;
     *  	OR
     *  	S: Type equals &quot;cDNA&quot;
     * </pre>
     */
    public void testManyToManyQuery3() {
        IQuery query = QueryGeneratorMock.createSpecimenBioHazardQuery3();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testManyToManyQuery3:"+sql);
            assertEquals(
                    "Incorrect SQL formed for the Query having Many to Many Associations & Pseudo And!!!",
                    "Select distinct Biohazard_4.TYPE Column0 ,Biohazard_4.COMMENTS Column1 ,Biohazard_4.NAME Column2 ,Biohazard_4.IDENTIFIER Column3 ,Specimen_6.TYPE Column4 ,Specimen_6.POSITION_DIMENSION_TWO Column5 ,Specimen_6.POSITION_DIMENSION_ONE Column6 ,Specimen_6.PATHOLOGICAL_STATUS Column7 ,Specimen_6.LINEAGE Column8 ,Specimen_6.LABEL Column9 ,Specimen_6.IDENTIFIER Column10 ,Specimen_6.COMMENTS Column11 ,Specimen_6.BARCODE Column12 ,Specimen_6.AVAILABLE Column13 ,Specimen_6.ACTIVITY_STATUS Column14 ,SpecimenCharacteristics_3.TISSUE_SIDE Column15 ,SpecimenCharacteristics_3.TISSUE_SITE Column16 ,SpecimenCharacteristics_3.IDENTIFIER Column17 ,Specimen_1.TYPE Column18 ,Specimen_1.POSITION_DIMENSION_TWO Column19 ,Specimen_1.POSITION_DIMENSION_ONE Column20 ,Specimen_1.PATHOLOGICAL_STATUS Column21 ,Specimen_1.LINEAGE Column22 ,Specimen_1.LABEL Column23 ,Specimen_1.IDENTIFIER Column24 ,Specimen_1.COMMENTS Column25 ,Specimen_1.BARCODE Column26 ,Specimen_1.AVAILABLE Column27 ,Specimen_1.ACTIVITY_STATUS Column28 ,SpecimenCharacteristics_5.TISSUE_SIDE Column29 ,SpecimenCharacteristics_5.TISSUE_SITE Column30 ,SpecimenCharacteristics_5.IDENTIFIER Column31 ,Specimen_2.TYPE Column32 ,Specimen_2.POSITION_DIMENSION_TWO Column33 ,Specimen_2.POSITION_DIMENSION_ONE Column34 ,Specimen_2.PATHOLOGICAL_STATUS Column35 ,Specimen_2.LINEAGE Column36 ,Specimen_2.LABEL Column37 ,Specimen_2.IDENTIFIER Column38 ,Specimen_2.COMMENTS Column39 ,Specimen_2.BARCODE Column40 ,Specimen_2.AVAILABLE Column41 ,Specimen_2.ACTIVITY_STATUS Column42 From CATISSUE_BIOHAZARD Biohazard_4 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUESPECIMENBIOHZR_6 on (Biohazard_4.IDENTIFIER=CATISSUESPECIMENBIOHZR_6.BIOHAZARD_ID) left join catissue_specimen Specimen_6 on (CATISSUESPECIMENBIOHZR_6.SPECIMEN_ID=Specimen_6.IDENTIFIER) left join catissue_specimen_char SpecimenCharacteristics_3 on (Specimen_6.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_3.IDENTIFIER) left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUESPECIMENBIOHZR_1 on (Biohazard_4.IDENTIFIER=CATISSUESPECIMENBIOHZR_1.BIOHAZARD_ID) left join catissue_specimen Specimen_1 on (CATISSUESPECIMENBIOHZR_1.SPECIMEN_ID=Specimen_1.IDENTIFIER) left join catissue_specimen_char SpecimenCharacteristics_5 on (Specimen_1.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_5.IDENTIFIER) left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUESPECIMENBIOHZR_2 on (Biohazard_4.IDENTIFIER=CATISSUESPECIMENBIOHZR_2.BIOHAZARD_ID) left join catissue_specimen Specimen_2 on (CATISSUESPECIMENBIOHZR_2.SPECIMEN_ID=Specimen_2.IDENTIFIER) Where (Biohazard_4.TYPE='Toxic') And((Specimen_6.TYPE='DNA' And Specimen_6.ACTIVITY_STATUS!='Disabled') And(SpecimenCharacteristics_3.TISSUE_SITE='skin')) And((Specimen_1.TYPE='RNA' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(SpecimenCharacteristics_5.TISSUE_SITE='Spinal cord')) Or(Specimen_2.TYPE='cDNA' And Specimen_2.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query having Many to Many Associations & Pseudo And!!!");
        }
    }

    /**
     * TO Test Pseudo-And Query with Inherited Entity.
     * 
     * <pre>
     * SCG: clinicalStatus equals 'New Diagnosis'
     * 		MolecularSpecimen: type equals 'DNA'
     * 		PseudoAnd
     * 		TissueSpecimen: type equals 'Fixed Tissue'
     * </pre>
     */
    public void testCreateInheritanceQueryWithPAND1() {
        IQuery query = QueryGeneratorMock.createInheritanceQueryWithPAND1();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testCreateInheritanceQueryWithPAND1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for the Query having Inherited Entity & Pseudo And!!!",
                    "Select distinct SpecimenCollectionGroup_2.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_2.NAME Column3 ,SpecimenCollectionGroup_2.IDENTIFIER Column4 ,Specimen_3.TYPE Column5 ,Specimen_3.POSITION_DIMENSION_TWO Column6 ,Specimen_3.POSITION_DIMENSION_ONE Column7 ,Specimen_3.PATHOLOGICAL_STATUS Column8 ,Specimen_3.LINEAGE Column9 ,Specimen_3.LABEL Column10 ,Specimen_3.IDENTIFIER Column11 ,Specimen_3.COMMENTS Column12 ,Specimen_3.BARCODE Column13 ,Specimen_3.AVAILABLE Column14 ,Specimen_3.ACTIVITY_STATUS Column15 ,Specimen_3.CONCENTRATION Column16 ,Specimen_1.TYPE Column17 ,Specimen_1.POSITION_DIMENSION_TWO Column18 ,Specimen_1.POSITION_DIMENSION_ONE Column19 ,Specimen_1.PATHOLOGICAL_STATUS Column20 ,Specimen_1.LINEAGE Column21 ,Specimen_1.LABEL Column22 ,Specimen_1.IDENTIFIER Column23 ,Specimen_1.COMMENTS Column24 ,Specimen_1.BARCODE Column25 ,Specimen_1.AVAILABLE Column26 ,Specimen_1.ACTIVITY_STATUS Column27 From catissue_specimen_coll_group SpecimenCollectionGroup_2 left join catissue_specimen Specimen_3 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_3.SPECIMEN_COLLECTION_GROUP_ID And Specimen_3.SPECIMEN_CLASS='Molecular') left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID And Specimen_1.SPECIMEN_CLASS='Tissue') Where (SpecimenCollectionGroup_2.CLINICAL_STATUS='New Diagnosis' And SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(Specimen_3.TYPE='DNA' And Specimen_3.ACTIVITY_STATUS!='Disabled') And(Specimen_1.TYPE='Fixed Tissue' And Specimen_1.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query having Inherited Entity & Pseudo And!!!");
        }
    }

    /**
     * TO Test Query with Inherited Entity, where Parent Expression's Entity is
     * Inherited Entity.
     * 
     * <pre>
     * 	MolecularSpecimen: type equals 'DNA'
     * 		SCHAR: Tissue Site Equals &quot;PROSTATE GLAND&quot;
     * </pre>
     */
    public void testInheritanceQueryWithAssociation1() {
        IQuery query = QueryGeneratorMock.createInheritanceQueryWithAssociation1();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testInheritanceQueryWithAssociation1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for the Query On inherited entity having Many to Many Associations!!!",
                    "Select distinct Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Specimen_1.CONCENTRATION Column11 ,SpecimenCharacteristics_2.TISSUE_SIDE Column12 ,SpecimenCharacteristics_2.TISSUE_SITE Column13 ,SpecimenCharacteristics_2.IDENTIFIER Column14 From catissue_specimen Specimen_1 left join catissue_specimen_char SpecimenCharacteristics_2 on (Specimen_1.SPECIMEN_CHARACTERISTICS_ID=SpecimenCharacteristics_2.IDENTIFIER And Specimen_1.SPECIMEN_CLASS='Molecular') Where (Specimen_1.TYPE='DNA' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(SpecimenCharacteristics_2.TISSUE_SITE='Prostate Gland')",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query On inherited entity having Many to Many Associations!!!");
        }
    }

    /**
     * TO Test Query with Inherited Entity, where child Expression's Entity is
     * Inherited Entity.
     * 
     * <pre>
     * SCG: clinicalStatus equals 'New Diagnosis'
     * 		MolecularSpecimen: type equals 'DNA'
     * </pre>
     */
    public void testInheritanceQueryWithAssociation2() {
        IQuery query = QueryGeneratorMock.createInheritanceQueryWithAssociation2();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testInheritanceQueryWithAssociation2:"+sql);
            assertEquals(
                    "Incorrect SQL formed for the Query On inherited entity having One to Many Associations!!!",
                    "Select distinct SpecimenCollectionGroup_1.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_1.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_1.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_1.NAME Column3 ,SpecimenCollectionGroup_1.IDENTIFIER Column4 ,Specimen_2.TYPE Column5 ,Specimen_2.POSITION_DIMENSION_TWO Column6 ,Specimen_2.POSITION_DIMENSION_ONE Column7 ,Specimen_2.PATHOLOGICAL_STATUS Column8 ,Specimen_2.LINEAGE Column9 ,Specimen_2.LABEL Column10 ,Specimen_2.IDENTIFIER Column11 ,Specimen_2.COMMENTS Column12 ,Specimen_2.BARCODE Column13 ,Specimen_2.AVAILABLE Column14 ,Specimen_2.ACTIVITY_STATUS Column15 ,Specimen_2.CONCENTRATION Column16 From catissue_specimen_coll_group SpecimenCollectionGroup_1 left join catissue_specimen Specimen_2 on (SpecimenCollectionGroup_1.IDENTIFIER=Specimen_2.SPECIMEN_COLLECTION_GROUP_ID And Specimen_2.SPECIMEN_CLASS='Molecular') Where (SpecimenCollectionGroup_1.CLINICAL_STATUS='New Diagnosis' And SpecimenCollectionGroup_1.ACTIVITY_STATUS!='Disabled') And(Specimen_2.TYPE='DNA' And Specimen_2.ACTIVITY_STATUS!='Disabled')",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query On inherited entity having One to Many Associations!!!");
        }
    }

    /**
     * To test queries having many to many associations.
     * 
     * <pre>
     *  MolecularSpecimen: Type equals &quot;DNA&quot;
     *  	Biohazard: type equals &quot;Toxic&quot;
     * </pre>
     * 
     */
    public void testInheritanceQueryWithManyToMany() {
        IQuery query = QueryGeneratorMock.createInheritanceQueryWithManyToMany();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testManyToManyQuery1:"+sql);
            assertEquals(
                    "Incorrect SQL formed for the Query having Many to Many Associations!!!",
                    "Select distinct Specimen_1.TYPE Column0 ,Specimen_1.POSITION_DIMENSION_TWO Column1 ,Specimen_1.POSITION_DIMENSION_ONE Column2 ,Specimen_1.PATHOLOGICAL_STATUS Column3 ,Specimen_1.LINEAGE Column4 ,Specimen_1.LABEL Column5 ,Specimen_1.IDENTIFIER Column6 ,Specimen_1.COMMENTS Column7 ,Specimen_1.BARCODE Column8 ,Specimen_1.AVAILABLE Column9 ,Specimen_1.ACTIVITY_STATUS Column10 ,Specimen_1.CONCENTRATION Column11 ,Biohazard_2.TYPE Column12 ,Biohazard_2.COMMENTS Column13 ,Biohazard_2.NAME Column14 ,Biohazard_2.IDENTIFIER Column15 From catissue_specimen Specimen_1 left join CATISSUE_SPECIMEN_BIOHZ_REL CATISSUESPECIMENBIOHZR_2 on (Specimen_1.IDENTIFIER=CATISSUESPECIMENBIOHZR_2.SPECIMEN_ID And Specimen_1.SPECIMEN_CLASS='Fluid') left join CATISSUE_BIOHAZARD Biohazard_2 on (CATISSUESPECIMENBIOHZR_2.BIOHAZARD_ID=Biohazard_2.IDENTIFIER) Where (Specimen_1.TYPE='DNA' And Specimen_1.ACTIVITY_STATUS!='Disabled') And(Biohazard_2.TYPE='Toxic')",
                    sql);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query having Many to Many Associations!!!");
        }
    }

    /**
     * To test special characters and white spaces are removed properly from the
     * given string.
     * 
     */
    public void testRemoveSpecialCharactersFromString() {
        try {
            String testStr = "test space str";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertEquals("Unable to remove space from string", "testspacestr", testStr);

            testStr = "test.str";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertEquals("Unable to remove dot from string", "teststr", testStr);

            testStr = "test & str ,";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertEquals("Unable to remove & and , from string", "teststr", testStr);

            testStr = "(teststr)";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertEquals("Unable to remove () from string", "teststr", testStr);

            testStr = "teststr,test,str,1";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertFalse("Unable to remove , from string", testStr.contains(","));

            testStr = "teststr1-teststr2-teststr3";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertFalse("Unable to remove - from string", testStr.contains("-"));

            testStr = "1teststr";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertEquals("String starting with a number is valide case.", "1teststr", testStr);

            testStr = "1test_str";
            testStr = Utility.removeSpecialCharactersFromString(testStr);
            assertEquals("String starting with a number is valide case.", "1teststr", testStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception, While removing special characters from the string");
        }
    }

    /**
     * To check query containing empty root expression
     * 
     */
    public void testEmptyRootExpression() {
        IQuery query = QueryGeneratorMock.emptyRootExpression();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testEmptyRootExpression:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query with output view!!!",
                    "Select distinct Participant_3.VITAL_STATUS Column0 ,Participant_3.SOCIAL_SECURITY_NUMBER Column1 ,Participant_3.GENOTYPE Column2 ,Participant_3.MIDDLE_NAME Column3 ,Participant_3.LAST_NAME Column4 ,Participant_3.IDENTIFIER Column5 ,Participant_3.GENDER Column6 ,Participant_3.FIRST_NAME Column7 ,Participant_3.ETHNICITY Column8 ,Participant_3.DEATH_DATE Column9 ,Participant_3.BIRTH_DATE Column10 ,Participant_3.ACTIVITY_STATUS Column11 ,Specimen_1.TYPE Column12 ,Specimen_1.POSITION_DIMENSION_TWO Column13 ,Specimen_1.POSITION_DIMENSION_ONE Column14 ,Specimen_1.PATHOLOGICAL_STATUS Column15 ,Specimen_1.LINEAGE Column16 ,Specimen_1.LABEL Column17 ,Specimen_1.IDENTIFIER Column18 ,Specimen_1.COMMENTS Column19 ,Specimen_1.BARCODE Column20 ,Specimen_1.AVAILABLE Column21 ,Specimen_1.ACTIVITY_STATUS Column22 From catissue_participant Participant_3 left join catissue_coll_prot_reg CollectionProtocolRegistr_4 on (Participant_3.IDENTIFIER=CollectionProtocolRegistr_4.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_4.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID) Where (Participant_3.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_4.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(Specimen_1.IDENTIFIER is NOT NULL And Specimen_1.ACTIVITY_STATUS!='Disabled')))",
                    sql);

            assertEquals("Incorrect number of output trees Formed while generating SQL !!!", 1, generator
                    .getRootOutputTreeNodeList().size());
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    /**
     * To check query containing empty child expression
     * 
     */
    public void testEmptyChildExpression() {
        IQuery query = QueryGeneratorMock.emptyChildExpression();
        String sql;
        try {
            sql = generator.generateSQL(query);
            // System.out.println("testEmptyChildExpression:"+sql);
            assertEquals(
                    "Incorrect SQL formed for Query with output view!!!",
                    "Select distinct Participant_3.VITAL_STATUS Column0 ,Participant_3.SOCIAL_SECURITY_NUMBER Column1 ,Participant_3.GENOTYPE Column2 ,Participant_3.MIDDLE_NAME Column3 ,Participant_3.LAST_NAME Column4 ,Participant_3.IDENTIFIER Column5 ,Participant_3.GENDER Column6 ,Participant_3.FIRST_NAME Column7 ,Participant_3.ETHNICITY Column8 ,Participant_3.DEATH_DATE Column9 ,Participant_3.BIRTH_DATE Column10 ,Participant_3.ACTIVITY_STATUS Column11 ,Specimen_1.TYPE Column12 ,Specimen_1.POSITION_DIMENSION_TWO Column13 ,Specimen_1.POSITION_DIMENSION_ONE Column14 ,Specimen_1.PATHOLOGICAL_STATUS Column15 ,Specimen_1.LINEAGE Column16 ,Specimen_1.LABEL Column17 ,Specimen_1.IDENTIFIER Column18 ,Specimen_1.COMMENTS Column19 ,Specimen_1.BARCODE Column20 ,Specimen_1.AVAILABLE Column21 ,Specimen_1.ACTIVITY_STATUS Column22 From catissue_participant Participant_3 left join catissue_coll_prot_reg CollectionProtocolRegistr_4 on (Participant_3.IDENTIFIER=CollectionProtocolRegistr_4.PARTICIPANT_ID) left join catissue_specimen_coll_group SpecimenCollectionGroup_2 on (CollectionProtocolRegistr_4.IDENTIFIER=SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID) left join catissue_specimen Specimen_1 on (SpecimenCollectionGroup_2.IDENTIFIER=Specimen_1.SPECIMEN_COLLECTION_GROUP_ID) Where (Participant_3.LAST_NAME like 'last%' And Participant_3.ACTIVITY_STATUS!='Disabled') And((CollectionProtocolRegistr_4.ACTIVITY_STATUS!='Disabled') And((SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(Specimen_1.ACTIVITY_STATUS!='Disabled')))",
                    sql);

            assertEquals("Incorrect number of output trees Formed while generating SQL !!!", 1, generator
                    .getRootOutputTreeNodeList().size());
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }

    public void testTemporal1() {
        check(
                QueryGeneratorMock.createTemporalQueryDateDiff(),
                "Select distinct Specimen_2.TYPE Column0 ,Specimen_2.POSITION_DIMENSION_TWO Column1 ,Specimen_2.POSITION_DIMENSION_ONE Column2 ,Specimen_2.PATHOLOGICAL_STATUS Column3 ,Specimen_2.LINEAGE Column4 ,Specimen_2.LABEL Column5 ,Specimen_2.IDENTIFIER Column6 ,Specimen_2.COMMENTS Column7 ,Specimen_2.BARCODE Column8 ,Specimen_2.AVAILABLE Column9 ,Specimen_2.ACTIVITY_STATUS Column10 From catissue_specimen Specimen_2 left join catissue_specimen_event_param SpecimenEventParameters_3 on (Specimen_2.IDENTIFIER=SpecimenEventParameters_3.SPECIMEN_ID) inner join catissue_coll_event_param CollectionEventParameters_3 on (SpecimenEventParameters_3.IDENTIFIER=CollectionEventParameters_3.IDENTIFIER) left join catissue_specimen_event_param SpecimenEventParameters_1 on (Specimen_2.IDENTIFIER=SpecimenEventParameters_1.SPECIMEN_ID) inner join catissue_frozen_event_param FrozenEventParameters_1 on (SpecimenEventParameters_1.IDENTIFIER=FrozenEventParameters_1.IDENTIFIER) Where (Specimen_2.ACTIVITY_STATUS!='Disabled') And(SpecimenEventParameters_1.EVENT_TIMESTAMP - interval 30 Minute <= SpecimenEventParameters_3.EVENT_TIMESTAMP)");
    }

    public void testTemporal2() {
        check(
                QueryGeneratorMock.createTemporalQueryOffsetAttribute(),
                "Select distinct SpecimenCollectionGroup_2.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_2.NAME Column3 ,SpecimenCollectionGroup_2.IDENTIFIER Column4 From catissue_specimen_coll_group SpecimenCollectionGroup_2 left join catissue_coll_prot_reg CollectionProtocolRegistr_3 on (SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID=CollectionProtocolRegistr_3.IDENTIFIER) left join catissue_coll_prot_event CollectionProtocolEvent_1 on (SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_EVT_ID=CollectionProtocolEvent_1.IDENTIFIER) Where (SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_3.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_3.REGISTRATION_DATE + interval CollectionProtocolEvent_1.STUDY_CALENDAR_EVENT_POINT Day = STR_TO_DATE('05-30-2008', '%m-%d-%Y'))");
    }

    public void testTemporal3() {
        check(
                QueryGeneratorMock.createTemporalQueryWithOtherCond(),
                "Select distinct Specimen_2.TYPE Column0 ,Specimen_2.POSITION_DIMENSION_TWO Column1 ,Specimen_2.POSITION_DIMENSION_ONE Column2 ,Specimen_2.PATHOLOGICAL_STATUS Column3 ,Specimen_2.LINEAGE Column4 ,Specimen_2.LABEL Column5 ,Specimen_2.IDENTIFIER Column6 ,Specimen_2.COMMENTS Column7 ,Specimen_2.BARCODE Column8 ,Specimen_2.AVAILABLE Column9 ,Specimen_2.ACTIVITY_STATUS Column10 From catissue_specimen Specimen_2 left join catissue_specimen_event_param SpecimenEventParameters_3 on (Specimen_2.IDENTIFIER=SpecimenEventParameters_3.SPECIMEN_ID) inner join catissue_coll_event_param CollectionEventParameters_3 on (SpecimenEventParameters_3.IDENTIFIER=CollectionEventParameters_3.IDENTIFIER) left join catissue_specimen_event_param SpecimenEventParameters_1 on (Specimen_2.IDENTIFIER=SpecimenEventParameters_1.SPECIMEN_ID) inner join catissue_frozen_event_param FrozenEventParameters_1 on (SpecimenEventParameters_1.IDENTIFIER=FrozenEventParameters_1.IDENTIFIER) Where (Specimen_2.IDENTIFIER is NOT NULL And Specimen_2.ACTIVITY_STATUS!='Disabled') And(SpecimenEventParameters_1.EVENT_TIMESTAMP - interval 30 Minute <= SpecimenEventParameters_3.EVENT_TIMESTAMP)");
    }

    public void testTemporalOutput() {
        IQuery query = QueryGeneratorMock.createTemporalOutputQuery();
        check(
                query,
                "Select distinct SpecimenCollectionGroup_2.ACTIVITY_STATUS Column0 ,SpecimenCollectionGroup_2.CLINICAL_STATUS Column1 ,SpecimenCollectionGroup_2.CLINICAL_DIAGNOSIS Column2 ,SpecimenCollectionGroup_2.NAME Column3 ,SpecimenCollectionGroup_2.IDENTIFIER Column4 ,(CollectionProtocolRegistr_3.REGISTRATION_DATE + interval CollectionProtocolEvent_1.STUDY_CALENDAR_EVENT_POINT Day) Column5 From catissue_specimen_coll_group SpecimenCollectionGroup_2 left join catissue_coll_prot_reg CollectionProtocolRegistr_3 on (SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_REG_ID=CollectionProtocolRegistr_3.IDENTIFIER) left join catissue_coll_prot_event CollectionProtocolEvent_1 on (SpecimenCollectionGroup_2.COLLECTION_PROTOCOL_EVT_ID=CollectionProtocolEvent_1.IDENTIFIER) Where (SpecimenCollectionGroup_2.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_3.ACTIVITY_STATUS!='Disabled') And(CollectionProtocolRegistr_3.REGISTRATION_DATE + interval CollectionProtocolEvent_1.STUDY_CALENDAR_EVENT_POINT Day = STR_TO_DATE('05-30-2008', '%m-%d-%Y'))");
        Map<String, INamedTerm> outCols = generator.getOutputTermsColumns();
        assertEquals(1, outCols.size());
        String expectedColName = "Column5";
        assertEquals(expectedColName, outCols.keySet().iterator().next());
        assertEquals(query.getOutputTerms().get(0).getName(), outCols.get(expectedColName).getName());
        // TODO unable to check complete term.
    }

    private void check(IQuery query, String expectedSql) {
        try {
            String sql = generator.generateSQL(query);
            assertEquals(expectedSql, sql);
        } catch (Exception e) {
            // e.printStackTrace();
            fail("Unexpected Expection, While Generating SQL for the Query!!!");
        }
    }
}
