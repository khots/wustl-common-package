/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.tags.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
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
		SessionDataBean sessionBean = (SessionDataBean)request.getSession().getAttribute(Constants.SESSION_DATA);
		List<Tag> tagList = TagBizlogicFactory.getBizLogicInstance(entityTag).getTagList(sessionBean); 
		Map<String,Object> gridData = new HashMap<String,Object>();
		gridData.put("tagList",tagList);
		gridData.put("entityName", entityTag);
		gridData.put("userId",sessionBean.getUserId());
		String responseString = VelocityManager.getInstance().evaluate(gridData,
				"privilegeTreeTemplate.vm");
		response.setBufferSize(responseString.length());
		response.getWriter().write(responseString);
		response.setContentType("text/xml");
		return null;
	}

}
