
package edu.wustl.common.labelSQLApp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.labelSQLApp.bizlogic.CommonBizlogic;
/** 
 * @author Ashraf
 *	AJAX action class for retrieving the query result for dashboard items.
 */
public class QueryResultAjaxAction extends Action
{

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long labelSQLId = Long.valueOf(request.getParameter("labelSQLId"));//Association Id
		Long cpId = null;
		String cpIdParam = request.getParameter("cpId");
		if(cpIdParam!=null && !cpIdParam.isEmpty())
		{
		 cpId = Long.valueOf(cpIdParam);
		}		
		
		String result = new CommonBizlogic().getQueryResults(labelSQLId, cpId);//Retrieving query result and association id
		

		response.setContentType("text/html");
		response.getWriter().write(result);
		return null;
	}

}
