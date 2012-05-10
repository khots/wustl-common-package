
package edu.wustl.labelSQLApp.action;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.wustl.labelSQLApp.bizlogic.CommonBizlogic;
import edu.wustl.labelSQLApp.bizlogic.LabelSQLAssociationBizlogic;
import edu.wustl.labelSQLApp.form.CPDashboardForm;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.action.SecureAction;

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
