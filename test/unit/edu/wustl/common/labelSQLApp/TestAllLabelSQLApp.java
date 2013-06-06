/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.labelSQLApp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLAssociationBizlogicTest;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLBizlogicTest;
import edu.wustl.common.labelSQLApp.util.InsertCSDashboardItemTest;



@RunWith(Suite.class)
@Suite.SuiteClasses({LabelSQLBizlogicTest.class, LabelSQLAssociationBizlogicTest.class,
		InsertCSDashboardItemTest.class})
public class TestAllLabelSQLApp
{

}
