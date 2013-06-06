/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.tags.action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.tags.factory.TagBizlogicFactory;
import edu.wustl.common.util.global.Constants;

public class GetTreeGridChildAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTag = (String) request.getParameter(Constants.ENTITY_TAG);
		String tagIdentifier = (String) request.getParameter(Constants.TAGID_STRING);
		Long tagId = Long.parseLong(tagIdentifier);

		SessionDataBean sessionBean = (SessionDataBean)request.getSession().getAttribute(Constants.SESSION_DATA);
		Long userId = sessionBean.getUserId(); 
		
		JSONObject arrayObj = new JSONObject();
		arrayObj=TagBizlogicFactory.getBizLogicInstance(entityTag).getJSONObj(tagId);
		arrayObj.put("userId", userId);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(arrayObj.toString());

		return null;

	}
}
