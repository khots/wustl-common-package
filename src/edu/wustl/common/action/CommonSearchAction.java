/**
 * <p>Title: CommonSearchAction Class>
 * <p>Description:	This class is used to retrieve the information whose record
 * is to be modified.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 20, 2005
 */

package edu.wustl.common.action;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.factory.MasterFactory;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * This class is used to retrieve the information whose record is to be modified.
 * @author gautam_shetty
 */
public class CommonSearchAction extends Action
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(CommonSearchAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Retrieves and populates the information to be edited.
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @exception IOException Generic exception
	 * */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		logger.info("in execute method");
		//long startTime = System.currentTimeMillis();
		String target = null;
		String obj = request.getParameter(Constants.SYSTEM_IDENTIFIER);
		Long identifier = Long.valueOf(Utility.toLong(obj));
		if (identifier.longValue() == 0)
		{
			identifier = (Long) request.getAttribute(Constants.SYSTEM_IDENTIFIER);
			if (identifier == null)
			{
				target = getPageOf(request);
				request.setAttribute("forwardToHashMap", getForwordTohashMap(request));
				return mapping.findForward(target);
			}
		}
		target = openPageInEdit(form, identifier, request);
		//long endTime = System.currentTimeMillis();
		//logger.info("EXECUTE TIME FOR ACTION - " + this.getClass().getSimpleName() + " : " + (endTime - startTime));
		return mapping.findForward(target);
	}

	/**
	 * @param request HttpServletRequest
	 * @return HashMap<String, Long>
	 */
	private HashMap<String, Long> getForwordTohashMap(HttpServletRequest request)
	{
		HashMap<String, Long> forwardToHashMap = new HashMap<String, Long>();
		forwardToHashMap.put("collectionProtocolId", Long.valueOf(request
				.getParameter("cpSearchCpId")));
		forwardToHashMap.put("participantId", Long.valueOf(request
				.getParameter("cpSearchParticipantId")));
		forwardToHashMap.put("COLLECTION_PROTOCOL_EVENT_ID", Long.valueOf(request
				.getParameter("COLLECTION_PROTOCOL_EVENT_ID")));
		return forwardToHashMap;
	}

	/**
	 *
	 * @param form ActionForm
	 * @param identifier Long
	 * @param request HttpServletRequest
	 * @return String
	 */
	private String openPageInEdit(ActionForm form, Long identifier, HttpServletRequest request)
	{
		AbstractActionForm abstractForm = (AbstractActionForm) form;
		String target = Constants.FAILURE;
		try
		{
			AbstractDomainObjectFactory abstractDomainObjectFactory = (AbstractDomainObjectFactory) MasterFactory
					.getFactory(ApplicationProperties.getValue("app.domainObjectFactory"));
			String objName = abstractDomainObjectFactory.getDomainObjectName(abstractForm
					.getFormId());
			SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
					Constants.SESSION_DATA);
			IBizLogic bizLogic = AbstractBizLogicFactory.getBizLogic(ApplicationProperties
					.getValue("app.bizLogicFactory"), "getBizLogic", abstractForm.getFormId());
			boolean hasPrivilege = true;
			if (bizLogic.isReadDeniedTobeChecked() && sessionDataBean != null)
			{
				hasPrivilege = bizLogic.hasPrivilegeToView(objName, identifier, sessionDataBean);
			}
			if (!hasPrivilege)
			{
				throw new DAOException(
						"Access denied ! User does not have privilege to view this information.");
			}

			boolean isSuccess = bizLogic.populateUIBean(objName, identifier, abstractForm);
			if (isSuccess)
			{
				target = getTarget(request, abstractForm);
			}
			else
			{
				saveErrors(request, "errors.item.unknown", AbstractDomainObject
						.parseClassName(objName));
			}
		}
		catch (BizLogicException excp)
		{
			logger.error(excp.getMessage(), excp);
		}
		catch (DAOException excp)
		{
			saveErrors(request, "access.view.action.denied", "");
			target = Constants.ACCESS_DENIED;
			logger.error(excp.getMessage(), excp);
		}
		return target;
	}

	/**
	 * @param request HttpServletRequest
	 * @param key String
	 * @param obj Object
	 */
	private void saveErrors(HttpServletRequest request, String key, Object obj)
	{
		ActionErrors errors = new ActionErrors();
		ActionError error = new ActionError(key, obj);
		errors.add(ActionErrors.GLOBAL_ERROR, error);
		saveErrors(request, errors);
	}

	/**
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm
	 * @return String target
	 */
	private String getTarget(HttpServletRequest request, AbstractActionForm abstractForm)
	{

		String pageOf = getPageOf(request);
		abstractForm.setMutable(false);
		String operation = (String) request.getAttribute(Constants.OPERATION);
		request.setAttribute(Constants.OPERATION, operation);
		return pageOf;
	}

	/**
	 * @param request HttpServletRequest
	 * @return String value of pageOf
	 */
	private String getPageOf(HttpServletRequest request)
	{
		String pageOf = (String) request.getAttribute(Constants.PAGEOF);
		if (pageOf == null)
		{
			pageOf = request.getParameter(Constants.PAGEOF);
		}
		return pageOf;
	}
}