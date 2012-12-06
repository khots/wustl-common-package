
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
import edu.wustl.dao.exception.DAOException;

/** 
 * @author Ashraf
 *	This is the entry point for the labelSQLApp. CSId or CPId is required.
 */
public class CPDashboardAction extends Action
{

	private static final String PARTICIPANT_OBJECT = "participantObject";
	private static final String CP_ID = "cpId";
	private static final String PARTICIPANT_ID = "participantId";

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				"sessionData");
		String isSystemDashBoard = request.getParameter("isSystemDashboard");
		String forward = "success";
		if (isSystemDashBoard != null && isSystemDashBoard.equals("true"))
		{
			if (!sessionDataBean.isAdmin())
			{
				forward = "redirect";
			}
		}
		if (forward.equals("success"))
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
		String cpId = request.getParameter("cpSearchCpId");
		String participantId = request.getParameter(PARTICIPANT_ID);
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
		request.setAttribute(CP_ID, cpId);
		request.setAttribute(PARTICIPANT_ID, participantId);
		request.setAttribute("reportNameList", reportNameList);
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
		if(participantId != null && !participantId.equals(""))
		{
			reportNameList = ReportGenerator.getReportNames(cpId, participantId);
			Object participantObj = new ReportBizLogic().getParticipant(participantId);
			request.setAttribute(PARTICIPANT_OBJECT, participantObj);
		}
		else
		{
			reportNameList = ReportGenerator.getReportNames(cpId, null);
		}
		return reportNameList;
	}

}
