
package edu.wustl.common.tags.action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;

import edu.wustl.common.tags.factory.TagBizlogicFactory;
import edu.wustl.common.util.global.Constants;

public class GetTreeGridChildAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTag = (String) request.getParameter(Constants.ENTITY_TAG);
		String tagIdentifier = (String) request.getParameter(Constants.TAGID_STRING);
		long tagId = Long.parseLong(tagIdentifier);

		JSONObject arrayObj = new JSONObject();
		arrayObj=TagBizlogicFactory.getBizLogicInstance(entityTag).getJSONObj(entityTag, tagId);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(arrayObj.toString());

		return null;

	}
}
