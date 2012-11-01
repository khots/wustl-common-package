
package edu.wustl.common.tags.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.tags.factory.TagBizlogicFactory;

import edu.wustl.common.util.global.Constants;
import edu.wustl.common.velocity.VelocityManager;

public class TreeGridInItAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTag = (String) request.getParameter(Constants.ENTITY_TAG);
		List<Tag> tagList = TagBizlogicFactory.getBizLogicInstance(entityTag).getTagList(
				entityTag); 
		 
		String responseString = VelocityManager.getInstance().evaluate(tagList,
				"privilegeTreeTemplate.vm");
		response.getWriter().write(responseString);
		response.setContentType("text/xml");
		return null;
	}

}
