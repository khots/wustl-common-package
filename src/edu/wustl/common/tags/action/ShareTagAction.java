package edu.wustl.common.tags.action;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.tags.factory.TagBizlogicFactory;
import edu.wustl.common.util.global.Constants;
import edu.wustl.dao.exception.DAOException;

public class ShareTagAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String entityTag = (String) request.getParameter(Constants.ENTITY_TAG);
		String selectedUsers = request.getParameter("userString");        
		Set<Long> selectedUserSet= getIdList(selectedUsers);
		shareExistingTags(request, selectedUserSet, entityTag);
		return null;
	}
 
	private void shareExistingTags(HttpServletRequest request, Set<Long> selectedUserSet, String entityTag) 
			throws DAOException, BizLogicException, ApplicationException
	{
		String tagChecks = request.getParameter(Constants.TAGCHECKBOX_STRING); 
		Set<Long> tagIdSet = getIdList(tagChecks);
		if(tagIdSet != null || ! tagIdSet.isEmpty())
		{
			TagBizlogicFactory.getBizLogicInstance(entityTag).shareTags(entityTag, tagIdSet,
					selectedUserSet);
		}
	}
 
	private Set<Long> getIdList(String idStr)
	{
		Set<Long> selectedUsersList = new LinkedHashSet<Long>();

		String[] str = idStr.split("\\,");
		for (String tempStr : str)
		{
			if(! tempStr.trim().isEmpty())
			{
				selectedUsersList.add(Long.parseLong(tempStr));
			}
		}

		return selectedUsersList;
	}

}
 
