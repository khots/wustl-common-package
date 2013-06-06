/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.bizlogic;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import edu.wustl.common.scheduler.bizLogic.ReportAuditDataBizLogic;
import edu.wustl.common.scheduler.domain.ReportAuditData;
import edu.wustl.dao.query.generator.ColumnValueBean;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)
public class ReportAuditDataBizLogicTest
{

	@Test
	@PrepareForTest({ReportAuditDataBizLogic.class})
	public void testGetReportAuditDataListbyUser() throws Exception
	{
		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setReportDurationStart(new Date());
		repoAudit.setReportDurationEnd(new Date(new Date().getTime() * 3));
		repoAudit.setJobStatus("Completed");

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createPartialMock(
				ReportAuditDataBizLogic.class, "executeQuery");
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				(repoAuditBiz.executeQuery((String) EasyMock.anyObject(),
						(List<ColumnValueBean>) EasyMock.anyObject())))
				.andReturn(Arrays.asList(repoAudit)).anyTimes();

		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		assertEquals("mockFile", repoAuditBiz.getReportAuditDataListbyUser(12L, Arrays.asList(12L))
				.get(0).getFileName());

	}

	@Test
	@PrepareForTest({ReportAuditDataBizLogic.class})
	public void testIsAuthorized() throws Exception
	{
		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createPartialMock(
				ReportAuditDataBizLogic.class, "retrieve");
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();

		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		assertEquals(true, repoAuditBiz.isAuthorized(null, null, null));
	}

	@Test
	@PrepareForTest({ReportAuditDataBizLogic.class})
	public void testGetFileDetailsList() throws Exception
	{
		ReportAuditData repoAudit = new ReportAuditData();
		repoAudit.setCsId(541L);
		repoAudit.setId(2L);
		repoAudit.setUserId(2L);
		repoAudit.setReportId(2L);
		repoAudit.setFileName("mockFile");
		repoAudit.setReportDurationStart(new Date());
		repoAudit.setReportDurationEnd(new Date(new Date().getTime() * 3));
		repoAudit.setJobStatus("Completed");

		ReportAuditDataBizLogic repoAuditBiz = PowerMock.createPartialMock(
				ReportAuditDataBizLogic.class, "retrieve");
		PowerMock.expectNew(ReportAuditDataBizLogic.class).andReturn(repoAuditBiz).anyTimes();
		EasyMock.expect(
				(repoAuditBiz.retrieve((String) EasyMock.anyObject(),
						(ColumnValueBean) EasyMock.anyObject())))
				.andReturn(Arrays.asList(repoAudit)).anyTimes();

		PowerMock.replay(repoAuditBiz);
		PowerMock.replay(ReportAuditDataBizLogic.class);

		assertEquals("mockFile", repoAuditBiz.getFileDetailsList().get(0).getFileName());
	}
}
