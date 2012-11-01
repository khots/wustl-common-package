
package edu.wustl.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLAssociationBizlogicTest;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLBizlogicTest;
import edu.wustl.common.labelSQLApp.util.InsertCSDashboardItemTest;
import edu.wustl.common.scheduler.bizlogic.ReportAuditDataBizLogicTest;
import edu.wustl.common.scheduler.bizlogic.ScheduleBizLogicTest;
import edu.wustl.common.scheduler.scheduleProcessor.FileCleanupProcessorTest;
import edu.wustl.common.scheduler.scheduleProcessor.ReportScheduleProcessorTest;
import edu.wustl.common.scheduler.scheduleProcessor.StartUpScheduleTest;
import edu.wustl.common.scheduler.util.ReportSchedulerUtilTest;
import edu.wustl.common.scheduler.util.SchedulerDataUtilityTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ReportScheduleProcessorTest.class, FileCleanupProcessorTest.class,
		StartUpScheduleTest.class, SchedulerDataUtilityTest.class, ReportSchedulerUtilTest.class,
		ScheduleBizLogicTest.class, ReportAuditDataBizLogicTest.class,
		LabelSQLAssociationBizlogicTest.class, LabelSQLBizlogicTest.class,
		InsertCSDashboardItemTest.class})
public class TestAll
{

}
