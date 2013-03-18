
package edu.wustl.common.tags.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.tags.factory.TagBizlogicFactory;
import edu.wustl.common.util.global.Constants;

public class TagDeleteAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTag = (String) request.getParameter(Constants.ENTITY_TAG);
		String tagIdentifier = (String) request.getParameter(Constants.TAGID_STRING);
		SessionDataBean sessionBean = (SessionDataBean)request.getSession().getAttribute(Constants.SESSION_DATA);
		Long tagId = Long.parseLong(tagIdentifier);
		TagBizlogicFactory.getBizLogicInstance(entityTag).deleteTag(sessionBean, tagId);

		return null;
	}

}
