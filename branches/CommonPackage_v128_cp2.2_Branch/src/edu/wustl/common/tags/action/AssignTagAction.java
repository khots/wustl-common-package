
package edu.wustl.common.tags.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.tags.factory.TagBizlogicFactory;
import edu.wustl.common.util.global.Constants;

public class AssignTagAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTag = (String) request.getParameter(Constants.ENTITY_TAG);
		String entityTagItem = (String) request.getParameter(Constants.ENTITY_TAGITEM);
		String objChecks = request.getParameter(Constants.OBJCHECKBOX_STRING);
		final String isCheckAllAcrossAllChecked = request
		.getParameter("isCheckAllAcrossAllChecked");
		HttpSession session = request.getSession();
		if (isCheckAllAcrossAllChecked != null && "true".equals(isCheckAllAcrossAllChecked) &&
				session.getAttribute("specIds") != null)
		{
			
			objChecks = session.getAttribute("specIds").toString();
		}
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				edu.wustl.common.util.global.Constants.SESSION_DATA);
		Long userId = sessionDataBean.getUserId();
		List<Long> objCheckList = getIdList(objChecks);
		addTag(request, objCheckList, entityTag, entityTagItem, userId);
		assignTag(request, objCheckList, entityTagItem, entityTag, userId);
		session.removeAttribute("specIds");
		return null;
	}

	private void assignTag(HttpServletRequest request, List<Long> objCheckList, String entityTagItem, 
			String entityTag, Long userId) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, ApplicationException
	{

		String tagChecks = request.getParameter(Constants.TAGCHECKBOX_STRING);
		List<Long> tagCheckList = getIdList(tagChecks);
		Iterator<Long> tagIterate = tagCheckList.iterator();
		while (tagIterate.hasNext())
		{
			Long tagId = (Long) tagIterate.next();
			Tag tag = TagBizlogicFactory.getBizLogicInstance(entityTag).getTagById(tagId);
			if (tag.getUserId() == userId){
				assignTag(objCheckList, tagId, entityTagItem);
			}
		}
	}

	private void addTag(HttpServletRequest request, List<Long> objCheckList, String entityTag,
			String entityTagItem, Long userId) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, ApplicationException
	{
		String newTagName = request.getParameter(Constants.NEWTAGNAME_STRING);
		if (newTagName != null && !newTagName.isEmpty())
		{
			long tagId = TagBizlogicFactory.getBizLogicInstance(entityTag).createNewTag(newTagName, userId);
			assignTag(objCheckList, tagId, entityTagItem);
		}
	}

	private void assignTag(List<Long> objCheckList, long tagId, String entityTagItem)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, ApplicationException
	{
		Iterator<Long> objIterate = objCheckList.iterator();
		while (objIterate.hasNext())
		{
			Long objId = (Long) objIterate.next();
			TagBizlogicFactory.getBizLogicInstance(entityTagItem).assignTag(tagId,
					objId);
		}
	}

	private List<Long> getIdList(String idStr)
	{
		List<Long> objCheckList = new ArrayList<Long>();

		String[] str = idStr.split("\\,");
		for (String str1 : str)
		{
			if(! str1.trim().isEmpty())
			{
				objCheckList.add(Long.parseLong(str1));
			}
		}

		return objCheckList;
	}

}
