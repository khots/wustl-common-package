
package edu.wustl.common.tags.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;

import edu.wustl.common.tags.factory.TagBizlogicFactory;
import edu.wustl.common.util.global.Constants;
import edu.wustl.dao.exception.DAOException;

public class AssignTagAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTag = (String) request.getParameter(Constants.ENTITY_TAG);
		String entityTagItem = (String) request.getParameter(Constants.ENTITY_TAGITEM);
		String objChecks = request.getParameter(Constants.OBJCHECKBOX_STRING);
		List<Long> objCheckList = getIdList(objChecks);
		addTag(request, objCheckList, entityTag, entityTagItem);
		assignTag(request, objCheckList, entityTagItem);
		return null;
	}

	private void assignTag(HttpServletRequest request, List<Long> objCheckList, String entityTagItem)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, ApplicationException
	{

		String tagChecks = request.getParameter(Constants.TAGCHECKBOX_STRING);
		List<Long> tagCheckList = getIdList(tagChecks);
		Iterator<Long> tagIterate = tagCheckList.iterator();
		while (tagIterate.hasNext())
		{
			Long tagId = (Long) tagIterate.next();
			assignTag(objCheckList, tagId, entityTagItem);
		}
	}

	private void addTag(HttpServletRequest request, List<Long> objCheckList, String entityTag,
			String entityTagItem) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, ApplicationException
	{
		String newTagName = request.getParameter(Constants.NEWTAGNAME_STRING);
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				edu.wustl.common.util.global.Constants.SESSION_DATA);

		if (newTagName != null && !newTagName.isEmpty())
		{
			long tagId = TagBizlogicFactory.getBizLogicInstance(entityTag).createNewTag(entityTag,
					newTagName, sessionDataBean.getUserId());
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
			TagBizlogicFactory.getBizLogicInstance(entityTagItem).assignTag(entityTagItem, tagId,
					objId);
		}
	}

	private List<Long> getIdList(String idStr)
	{
		List<Long> objCheckList = new ArrayList<Long>();

		String[] str = idStr.split("\\,");
		for (String str1 : str)
		{
			if(!"".equals(str1.trim()))
			{
				objCheckList.add(Long.parseLong(str1));
			}
		}

		return objCheckList;
	}

}
