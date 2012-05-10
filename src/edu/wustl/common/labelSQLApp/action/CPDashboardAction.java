
package edu.wustl.common.labelSQLApp.action;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLAssociationBizlogic;
import edu.wustl.common.labelSQLApp.form.CPDashboardForm;

/** 
 * @author Ashraf
 *	This is the entry point for the labelSQLApp. CSId or CPId is required.
 */
public class CPDashboardAction extends Action
{

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		CPDashboardForm cpDashboardForm = (CPDashboardForm) actionForm;//Form object

		Long cpId = Long.valueOf(request.getParameter("cpSearchCpId"));//Get CPId/CSId

		LinkedHashMap<String, Long> displayNameMap = new LabelSQLAssociationBizlogic()
				.getAssocAndDisplayNameMapByCPId(cpId);//Retrieving association id and display name map from CPId/CSId

		cpDashboardForm.setDisplayNameAndAssocMap(displayNameMap);//Setting association id and display name map

		return actionMapping.findForward("success");
	}
}
