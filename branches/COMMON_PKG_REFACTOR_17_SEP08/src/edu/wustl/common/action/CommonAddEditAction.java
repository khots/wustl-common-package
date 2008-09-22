/**
 * <p>Title: CommonAddEditAction Class>
 * <p>Description:	This Class is used to Add/Edit the data in the database.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 21, 2005
 */

package edu.wustl.common.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Session;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.AddNewSessionDataBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.bizlogic.QueryBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.factory.AbstractForwardToFactory;
import edu.wustl.common.factory.MasterFactory;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.dbManager.HibernateMetaData;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to Add/Edit data in the database.
 * @author gautam_shetty
 */
public class CommonAddEditAction extends Action
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(CommonAddEditAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Adds / Updates the data in the database.
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @throws IOException Generic exception
	 * @throws ServletException Generic exception
	 * */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException
	{
		//long startTime = System.currentTimeMillis();
		logger.info("in execute method");
		String target = null;
		try
		{
			AbstractActionForm abstractForm = (AbstractActionForm) form;
			if (abstractForm.isAddOperation())
			{
				return executeAdd(mapping, request, abstractForm);
			}
			else
			{
				return executeEdit(mapping, request, abstractForm);
			}
		}
		catch (BizLogicException excp)
		{
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item", excp.getMessage());
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
			target = new String(Constants.FAILURE);
			logger.error(excp.getMessage(), excp);
		}
		catch (DAOException excp)
		{
			target = Constants.FAILURE;
			logger.error(excp.getMessage(), excp);
		}
		catch (UserNotAuthorizedException excp)
		{
			ActionErrors errors = new ActionErrors();
			ActionError error = getErrorForUserNotAuthorized(request, excp);
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
			target = Constants.FAILURE;
			logger.error(excp.getMessage(), excp);
		}
		catch (AssignDataException excp)
		{
			target = Constants.FAILURE;
			logger.error(excp.getMessage(), excp);
		}
		//long endTime = System.currentTimeMillis();
		//logger.info("EXECUTE TIME FOR ACTION - " + this.getClass().getSimpleName() + " : " + (endTime - startTime));
		return mapping.findForward(target);
	}

	/**
	 * return action error in case the user,in request is not authorized. 
	 * @param request HttpServletRequest
	 * @param excp UserNotAuthorizedException
	 * @return ActionError
	 */
	private ActionError getErrorForUserNotAuthorized(HttpServletRequest request,
			UserNotAuthorizedException excp)
	{
		AbstractDomainObject abstractDomain = null;
		SessionDataBean sessionDataBean = getSessionData(request);
		String userName = "";
		if (sessionDataBean != null)
		{
			userName = sessionDataBean.getUserName();
		}
		String className = getActualClassName(abstractDomain.getClass().getName());
		String decoratedPrivilegeName = Utility.getDisplayLabelForUnderscore(excp
				.getPrivilegeName());
		String baseObject = excp.getBaseObject();
		if ("".equals(baseObject) || baseObject == null)
		{
			baseObject = className;
		}
		else
		{
			baseObject = excp.getBaseObject();
		}
		ActionError error = new ActionError("access.addedit.object.denied", userName, className,
				decoratedPrivilegeName, baseObject);
		return error;
	}

	/**
	 * return the  actual class name.
	 * @param name String
	 * @return String
	 */
	public String getActualClassName(String name)
	{
		if (name != null && name.trim().length() != 0)
		{
			String splitter = "\\.";
			String[] arr = name.split(splitter);
			if (arr != null && arr.length != 0)
			{
				return arr[arr.length - Constants.ONE];
			}
		}
		return name;
	}

	/**
	 * @param abstractDomainObjectFactory AbstractDomainObjectFactory
	 * @param abstractForm AbstractActionForm
	 * @return object name.
	 */
	public String getObjectName(AbstractDomainObjectFactory abstractDomainObjectFactory,
			AbstractActionForm abstractForm)
	{

		return abstractDomainObjectFactory.getDomainObjectName(abstractForm.getFormId());
	}

	/**
	 * @return  the object of AbstractDomainObjectFactory
	 */
	public AbstractDomainObjectFactory getAbstractDomainObjectFactory()
	{
		return (AbstractDomainObjectFactory) MasterFactory.getFactory(ApplicationProperties
				.getValue("app.domainObjectFactory"));
	}

	/**
	 * get object required for all API,for query purpose.
	 * @return QueryBizLogic
	 * @throws BizLogicException biz logic exception
	 */
	public QueryBizLogic getQueryBizLogic() throws BizLogicException
	{
		return (QueryBizLogic) AbstractBizLogicFactory.getBizLogic(ApplicationProperties
				.getValue("app.bizLogicFactory"), "getBizLogic", Constants.QUERY_INTERFACE_ID);
	}

	/**
	 * @param abstractForm AbstractActionForm
	 * @return IBizLogic
	 * @throws BizLogicException biz logic exception
	 */
	public IBizLogic getIBizLogic(AbstractActionForm abstractForm) throws BizLogicException
	{
		return AbstractBizLogicFactory.getBizLogic(ApplicationProperties
				.getValue("app.bizLogicFactory"), "getBizLogic", abstractForm.getFormId());
	}

	/**
	 * This method get data from form of current session and add it for further operation.
	 * @param mapping ActionMapping
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm
	 * @return ActionForward
	 * @throws AssignDataException Generic exception
	 * @throws BizLogicException biz logic exception
	 * @throws UserNotAuthorizedException Generic exception
	 */
	public ActionForward executeAdd(ActionMapping mapping, HttpServletRequest request,
			AbstractActionForm abstractForm) throws AssignDataException, BizLogicException,
			UserNotAuthorizedException
	{
		AbstractDomainObject abstractDomain = null;
		AbstractDomainObjectFactory abstractDomainObjectFactory = getAbstractDomainObjectFactory();
		abstractDomain = abstractDomainObjectFactory.getDomainObject(abstractForm.getFormId(),
				abstractForm);
		IBizLogic bizLogic = getIBizLogic(abstractForm);
		bizLogic.insert(abstractDomain, getSessionData(request), Constants.HIBERNATE_DAO);
		String target = new String(Constants.SUCCESS);
		ActionMessages messages = new ActionMessages();
		QueryBizLogic queryBizLogic = getQueryBizLogic();
		String objectName = getObjectName(abstractDomainObjectFactory, abstractForm);
		addMessage(messages, abstractDomain, "add", queryBizLogic, objectName);
		if (abstractDomain.getId() != null)
		{
			abstractForm.setId(abstractDomain.getId().longValue());
			request.setAttribute(Constants.SYSTEM_IDENTIFIER, abstractDomain.getId());
			abstractForm.setMutable(false);
		}

		String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);
		if ((submittedFor != null) && (submittedFor.equals("AddNew")))
		{

			HttpSession session = request.getSession();
			Stack formBeanStack = (Stack) session.getAttribute(Constants.FORM_BEAN_STACK);

			if (formBeanStack != null)
			{
				//Retrieving AddNewSessionDataBean from Stack
				AddNewSessionDataBean addNewSessionDataBean = (AddNewSessionDataBean) formBeanStack
						.pop();

				if (addNewSessionDataBean != null)
				{
					AbstractActionForm sessionFormBean = addNewSessionDataBean
							.getAbstractActionForm();
					String forwardTo = addNewSessionDataBean.getForwardTo();
					sessionFormBean.setAddNewObjectIdentifier(addNewSessionDataBean.getAddNewFor(),
							abstractDomain.getId());
					sessionFormBean.setMutable(false);
					if (formBeanStack.isEmpty())
					{
						session.removeAttribute(Constants.FORM_BEAN_STACK);
						request.setAttribute(Constants.SUBMITTED_FOR, "Default");
					}
					else
					{
						request.setAttribute(Constants.SUBMITTED_FOR, "AddNew");
					}
					String formBeanName = Utility.getFormBeanName(sessionFormBean);
					request.setAttribute(formBeanName, sessionFormBean);
					if (messages != null)
					{
						saveMessages(request, messages);
					}

					//Status message key.
					String statusMessageKey = String.valueOf(abstractForm.getFormId() + "."
							+ String.valueOf(abstractForm.isAddOperation()));
					request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMessageKey);

					//Changing operation attribute in path specified in ForwardTo mapping, If AddNew activity started from Edit page
					if ((sessionFormBean.getOperation().equals("edit")))
					{
						ActionForward editForward = new ActionForward();
						String addPath = (mapping.findForward(forwardTo)).getPath();
						String editPath = addPath.replaceFirst("operation=add", "operation=edit");
						editForward.setPath(editPath);
						return editForward;
					}

					return (mapping.findForward(forwardTo));
				}
				//Setting target as FAILURE if AddNewSessionDataBean is null
				else
				{
					target = new String(Constants.FAILURE);

					ActionErrors errors = new ActionErrors();
					ActionError error = new ActionError("errors.item.unknown", AbstractDomainObject
							.parseClassName(objectName));
					errors.add(ActionErrors.GLOBAL_ERROR, error);
					saveErrors(request, errors);
				}
			}
		}
		else if ((submittedFor != null) && (submittedFor.equals("ForwardTo")))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, "Default");
			request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm,
					abstractDomain));
		}

		//setting target to ForwardTo attribute of submitted Form
		if (abstractForm.getForwardTo() != null && abstractForm.getForwardTo().trim().length() > 0)
		{
			String forwardTo = abstractForm.getForwardTo();
			target = forwardTo;
		}
		//Sets the domain object value in PrintMap for Label Printing
		request.setAttribute("forwardToPrintMap", generateForwardToPrintMap(abstractForm,
				abstractDomain));
		if (messages != null)
		{
			saveMessages(request, messages);
		}
		//Status message key.
		String statusMessageKey = String.valueOf(abstractForm.getFormId() + "."
				+ String.valueOf(abstractForm.isAddOperation()));
		request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMessageKey);
		return mapping.findForward(target);

	}

	/**
	 * This method edit the current session data as per requirement and submit it to next action.
	 * @param mapping ActionMapping
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm
	 * @return ActionForward
	 * @throws AssignDataException Generic exception
	 * @throws BizLogicException biz logic exception
	 * @throws UserNotAuthorizedException Generic exception
	 * @throws DAOException Generic exception
	 */
	public ActionForward executeEdit(ActionMapping mapping, HttpServletRequest request,
			AbstractActionForm abstractForm) throws AssignDataException, BizLogicException,
			UserNotAuthorizedException, DAOException
	{
		logger.debug("in method executeEdit()");
		AbstractDomainObject abstractDomain = null;
		ActionMessages messages = null;
		String target = new String(Constants.SUCCESS);
		DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
		AbstractDomainObjectFactory abstractDomainObjectFactory = getAbstractDomainObjectFactory();
		String objectName = getObjectName(abstractDomainObjectFactory, abstractForm);
		abstractDomain = defaultBizLogic.populateDomainObject(objectName, Long.valueOf(abstractForm
				.getId()), abstractForm);
		if (abstractDomain != null)
		{
			Session sessionClean = DBUtil.getCleanSession();
			AbstractDomainObject abstractDomainOld = null;
			try
			{
				abstractDomainOld = (AbstractDomainObject) sessionClean.load(Class
						.forName(objectName), Long.valueOf(abstractForm.getId()));
				IBizLogic bizLogic = getIBizLogic(abstractForm);
				bizLogic.update(abstractDomain, abstractDomainOld, Constants.HIBERNATE_DAO,
						getSessionData(request));
				sessionClean.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			// -- Direct to Main Menu if record is disabled
			if ((abstractForm.getActivityStatus() != null)
					&& (Constants.ACTIVITY_STATUS_DISABLED.equals(abstractForm.getActivityStatus())))
			{
				String moveTo = abstractForm.getOnSubmit();
				ActionForward reDirectForward = new ActionForward();
				reDirectForward.setPath(moveTo);
				return reDirectForward;
			}

			// OnSubmit
			if (abstractForm.getOnSubmit() != null
					&& abstractForm.getOnSubmit().trim().length() > 0)
			{
				String forwardTo = abstractForm.getOnSubmit();
				return (mapping.findForward(forwardTo));
			}

			String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);

			//----------ForwardTo Starts----------------
			if ((submittedFor != null) && (submittedFor.equals("ForwardTo")))
			{
				request.setAttribute(Constants.SUBMITTED_FOR, "Default");
				request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm,
						abstractDomain));
			}
			//----------ForwardTo Ends----------------

			//setting target to ForwardTo attribute of submitted Form
			if (abstractForm.getForwardTo() != null
					&& abstractForm.getForwardTo().trim().length() > 0)
			{
				String forwardTo = abstractForm.getForwardTo();
				target = forwardTo;
			}
			// Forward the page to edit success in the Advance query search if the edit is through Object view of Advance Search
			String pageOf = (String) request.getParameter(Constants.PAGEOF);
			if (pageOf != null)
			{
				if (pageOf.equals(Constants.QUERY))
				{
					target = pageOf;
				}
			}
			// The successful edit message. Changes done according to bug# 945, 947
			messages = new ActionMessages();
			addMessage(messages, abstractDomain, "edit", getQueryBizLogic(), objectName);
		}
		else
		{
			target = new String(Constants.FAILURE);
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item.unknown", AbstractDomainObject
					.parseClassName(objectName));
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
		}
		//Sets the domain object value in PrintMap for Label Printing
		request.setAttribute("forwardToPrintMap", generateForwardToPrintMap(abstractForm,
				abstractDomain));
		if (messages != null)
		{
			saveMessages(request, messages);
		}
		//Status message key.
		String statusMessageKey = String.valueOf(abstractForm.getFormId() + "."
				+ String.valueOf(abstractForm.isAddOperation()));
		request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMessageKey);
		return mapping.findForward(target);
	}

	/**
	 * get session data from current session.
	 * @param request HttpServletRequest
	 * @return SessionDataBean
	 */
	public SessionDataBean getSessionData(HttpServletRequest request)
	{
		Object obj = request.getSession().getAttribute(Constants.SESSION_DATA);
		/**
		 *  This if loop is specific to Password Security feature.
		 */
		if (obj == null)
		{
			obj = request.getSession().getAttribute(Constants.TEMP_SESSION_DATA);
		}
		if (obj != null)
		{
			SessionDataBean sessionData = (SessionDataBean) obj;
			return sessionData;
		}
		return null;
		//return (String) request.getSession().getAttribute(Constants.SESSION_DATA);
	}

	/**
	 * This method generates HashMap of data required to be forwarded. 
	 * @param abstractForm	Form submitted
	 * @param abstractDomain	DomainObject Added/Edited
	 * @return	HashMap of data required to be forwarded
	 * @throws BizLogicException Generic exception
	 */
	private HashMap generateForwardToHashMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws BizLogicException
	{
		HashMap forwardToHashMap;
		AbstractForwardToProcessor forwardToProcessor = AbstractForwardToFactory
				.getForwardToProcessor(ApplicationProperties.getValue("app.forwardToFactory"),
						"getForwardToProcessor");
		forwardToHashMap = (HashMap) forwardToProcessor.populateForwardToData(abstractForm,
				abstractDomain);
		return forwardToHashMap;
	}

	/**
	 * This method generates HashMap of data required to be forwarded if Form is submitted for Print request
	 * @param abstractForm	Form submitted
	 * @param abstractDomain	DomainObject Added/Edited
	 * @return	HashMap of data required to be forwarded
	 * @throws BizLogicException biz logic exception
	 */
	private HashMap generateForwardToPrintMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws BizLogicException
	{
		HashMap forwardToPrintMap = null;
		AbstractForwardToProcessor forwardToProcessor = AbstractForwardToFactory
				.getForwardToProcessor(ApplicationProperties.getValue("app.forwardToFactory"),
						"getForwardToPrintProcessor");
		forwardToPrintMap = (HashMap) forwardToProcessor.populateForwardToData(abstractForm,
				abstractDomain);
		return forwardToPrintMap;
	}

	/**
	 * This method will add the success message into ActionMessages object
	 * @param messages ActionMessages
	 * @param abstractDomain AbstractDomainObject
	 * @param addoredit String
	 * @param queryBizLogic QueryBizLogic
	 * @param objectName String
	 */
	private void addMessage(ActionMessages messages, AbstractDomainObject abstractDomain,
			String addoredit, QueryBizLogic queryBizLogic, String objectName)
	{
		String message = abstractDomain.getMessageLabel();
		Validator validator = new Validator();
		boolean isEmpty = validator.isEmpty(message);
		String displayName = null;
		try
		{
			displayName = queryBizLogic.getDisplayNamebyTableName(HibernateMetaData
					.getTableName(abstractDomain.getClass()));
		}
		catch (Exception excp)
		{
			displayName = AbstractDomainObject.parseClassName(objectName);
			logger.error(excp.getMessage(), excp);
		}

		if (!isEmpty)
		{
			messages.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("object." + addoredit
					+ ".success", displayName, message));
		}
		else
		{
			messages.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("object." + addoredit
					+ ".successOnly", displayName));
		}
	}
}