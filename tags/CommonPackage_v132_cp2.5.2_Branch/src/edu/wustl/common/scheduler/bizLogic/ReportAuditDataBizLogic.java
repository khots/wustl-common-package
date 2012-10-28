
package edu.wustl.common.scheduler.bizLogic;

import java.util.Collection;
import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.scheduler.domain.ReportAuditData;
import edu.wustl.common.scheduler.util.SchedulerDataUtility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.DAO;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class ReportAuditDataBizLogic extends DefaultBizLogic
{

	public ReportAuditDataBizLogic()
	{
		super(CommonServiceLocator.getInstance().getAppName());
	}

	/**
	 * @param userId
	 * @return
	 * @throws BizLogicException
	 */
	@SuppressWarnings("unchecked")
	public List<ReportAuditData> getReportAuditDataListbyUser(Long userId,
			Collection<Long> ticketIdCollection) throws BizLogicException
	{
		String query = "from " + ReportAuditData.class.getName()
				+ " auditData where auditData.userId=" + userId + " and auditData.id in "
				+ SchedulerDataUtility.getQueryInClauseStringFromIdList(ticketIdCollection);
		return executeQuery(query, null);
	}

	/**
	 * @return
	 * @throws BizLogicException
	 */
	@SuppressWarnings("unchecked")
	public List<ReportAuditData> getFileDetailsList() throws BizLogicException
	{
		ColumnValueBean colVal = new ColumnValueBean("jobStatus", "Completed");
		return retrieve(ReportAuditData.class.getName(), colVal);
	}

	public boolean isAuthorized(DAO dao, Object domainObject, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		return true;
	}

}
