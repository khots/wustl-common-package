/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

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

public class TagItemDeleteAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTagItem = (String) request.getParameter(Constants.ENTITY_TAGITEM);
		String tagItemIdentifier = (String) request.getParameter(Constants.TAGITEMID_STRING);
		SessionDataBean sessionBean = (SessionDataBean)request.getSession().getAttribute(Constants.SESSION_DATA);
		Long userId = sessionBean.getUserId();
		Long tagItemId = Long.parseLong(tagItemIdentifier);

		TagBizlogicFactory.getBizLogicInstance(entityTagItem).deleteTagItem(tagItemId, userId);
		return null;
	}
}
