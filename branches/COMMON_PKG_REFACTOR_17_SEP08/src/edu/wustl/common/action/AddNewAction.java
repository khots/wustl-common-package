/**
 * <p>Title: AddNewAction Class>
 * <p>Description:	This Class is used to maintain FormBean for AddNew operation</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Krunal Thakkar
 * @version 1.00
 * Created on Apr 12, 2006
 */

package edu.wustl.common.action;

import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.AddNewSessionDataBean;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to maintain FormBean for AddNew operation.
 * @author Krunal Thakkar
 */
public class AddNewAction extends Action
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(AddNewAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Maintains FormBean for AddNew operation.
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @exception Exception Generic exception
	 * */
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ActionForward actionForward;
		try
		{
			logger.info("Started Add action");
			AddNewSessionDataBean addNewSessionDataBean = createNewSessionDataBean(form, request);
			Stack<AddNewSessionDataBean> formBeanStack = getStackBeanFromSession(request);
			formBeanStack.push(addNewSessionDataBean);
			request.setAttribute(Constants.SUBMITTED_FOR, "AddNew");
			actionForward = mapping.findForward(request.getParameter(Constants.ADD_NEW_FORWARD_TO));
		}
		catch (Exception e)
		{
			logger.info("Exception: " + e.getMessage(), e);
			actionForward = mapping.findForward(Constants.SUCCESS);
		}
		return actionForward;
	}

	/**
	 * Get session data like formBean,forwardTo etc. from session,create stack of AddNewSessionDataBean and set attribute in it,in case it's null.  
	 * @param request HttpServletRequest
	 * @return Stack<AddNewSessionDataBean>
	 */
	private Stack<AddNewSessionDataBean> getStackBeanFromSession(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		Stack<AddNewSessionDataBean> formBeanStack = (Stack) session
				.getAttribute(Constants.FORM_BEAN_STACK);

		if (formBeanStack == null)
		{
			logger.debug("Creating FormBeanStack in AddNewAction.");
			formBeanStack = new Stack<AddNewSessionDataBean>();
		}
		session.setAttribute(Constants.FORM_BEAN_STACK, formBeanStack);
		return formBeanStack;
	}

	/**
	 * create AddNewSessiondataBean object,set attributes for ActionForm,forwardTo and addNewFor.
	 * @param form ActionForm
	 * @param request HttpServletRequest
	 * @return AddNewSessionDataBean
	 */
	private AddNewSessionDataBean createNewSessionDataBean(ActionForm form,
			HttpServletRequest request)
	{
		AddNewSessionDataBean addNewSessionDataBean = new AddNewSessionDataBean();
		addNewSessionDataBean.setAbstractActionForm((AbstractActionForm) form);
		addNewSessionDataBean.setForwardTo(request.getParameter(Constants.FORWARD_TO));
		addNewSessionDataBean.setAddNewFor(request.getParameter(Constants.ADD_NEW_FOR));
		return addNewSessionDataBean;
	}
}
