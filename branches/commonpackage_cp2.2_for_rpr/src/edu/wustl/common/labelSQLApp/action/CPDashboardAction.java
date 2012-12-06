
package edu.wustl.common.labelSQLApp.action;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLAssociationBizlogic;
import edu.wustl.common.labelSQLApp.form.CPDashboardForm;
import edu.wustl.common.report.ReportGenerator;
import edu.wustl.common.report.reportBizLogic.ReportBizLogic;
import edu.wustl.common.util.global.ReportConstants;
import edu.wustl.dao.exception.DAOException;

/** 
 * @author Ashraf
 *	This is the entry point for the labelSQLApp. CSId or CPId is required.
 */
public class CPDashboardAction extends Action
{

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				"sessionData");
		String isSystemDashBoard = request.getParameter(ReportConstants.IS_SYSTEM_DASHBOARD);
		String forward = ReportConstants.SUCCESS;
		if (isSystemDashBoard != null && isSystemDashBoard.equals(ReportConstants.TRUE))
		{
			if (!sessionDataBean.isAdmin())
			{
				forward = ReportConstants.REDIRECT;
			}
		}
		if (forward.equals(ReportConstants.SUCCESS))
		{
			loadDashboard(actionForm, request, sessionDataBean.isAdmin(),
					sessionDataBean.getUserId());
		}

		return actionMapping.findForward(forward);
	}

	/**
	 * @param actionForm
	 * @param request
	 * @throws Exception
	 */
	private void loadDashboard(ActionForm actionForm, HttpServletRequest request, boolean isAdmin,
			Long userId) throws Exception
	{
		CPDashboardForm cpDashboardForm = (CPDashboardForm) actionForm;
		String cpId = request.getParameter(ReportConstants.CP_SEARCH_CP_ID);
		String participantId = request.getParameter(ReportConstants.PARTICIPANT_ID);
		Long cp = null;
		if (cpId != null)
		{
			cp = Long.valueOf(cpId);
		}
		LinkedHashMap<String, Long> displayNameMap = new LabelSQLAssociationBizlogic()
				.getAssocAndDisplayNameMapByCPId(cp);
		cpDashboardForm.setDisplayNameAndAssocMap(displayNameMap);
		List<NameValueBean> reportNameList = new ArrayList<NameValueBean>();

		if (isAdmin)
		{
			if (cpId != null)
			{
				reportNameList = processReports(request, cpId, participantId);
			}
			else
			{
				reportNameList = ReportGenerator.getSystemReportNames();
			}
		}
		else
		{
			reportNameList = processReports(request, cpId, participantId);
		}
		request.setAttribute(ReportConstants.CP_ID, cpId);
		request.setAttribute(ReportConstants.PARTICIPANT_ID, participantId);
		request.setAttribute(ReportConstants.REPORT_NAME_LIST, reportNameList);
	}

	/**
	 * @param request
	 * @param cpId
	 * @param participantId
	 * @return
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private List<NameValueBean> processReports(HttpServletRequest request, String cpId,
			String participantId) throws BizLogicException, DAOException
	{
		List<NameValueBean> reportNameList;
		if(participantId != null && !participantId.equals(ReportConstants.DOUBLE_QUOTE))
		{
			reportNameList = ReportGenerator.getReportNames(cpId, participantId);
			Object participantObj = new ReportBizLogic().getParticipant(participantId);
			request.setAttribute(ReportConstants.PARTICIPANT_OBJECT, participantObj);
		}
		else
		{
			reportNameList = ReportGenerator.getReportNames(cpId, null);
		}
		return reportNameList;
	}

}
