package edu.wustl.common.action;

import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.AddNewSessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.querysuite.bizlogic.QueryBizLogic;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;


public class CommonAddAction extends BaseAddEditAction
{
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
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException
	{
		AbstractDomainObject abstractDomain = null;
		AbstractActionForm abstractForm = (AbstractActionForm)form;
		ActionForward forward;
		ActionMessages messages = new ActionMessages();

		String target;
		String objectName = getObjectName(abstractForm);
		abstractDomain = insertDomainObject(request, abstractForm, messages,objectName);
		
		
		abstractForm.setId(abstractDomain.getId().longValue());
		request.setAttribute(Constants.SYSTEM_IDENTIFIER, abstractDomain.getId());
		abstractForm.setMutable(false);

		String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);
		HttpSession session = request.getSession();

		if ("AddNew".equals(submittedFor) )
		{
			//Retrieving AddNewSessionDataBean from Stack
			forward = getForwardToFromStack(mapping, request, abstractDomain, objectName);

		}
		else 
		{
			target = getForwardTo(request, abstractDomain, abstractForm);
			forward = mapping.findForward(target);
		}

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
		return forward;
	}

	private ActionForward getForwardToFromStack(ActionMapping mapping, HttpServletRequest request,
			AbstractDomainObject abstractDomain, String objectName)
			throws ApplicationException
	{
		
		Stack formBeanStack = (Stack) request.getSession().getAttribute(Constants.FORM_BEAN_STACK);
		ActionForward forward;
		
		if(formBeanStack == null)
		{
			forward = mapping.findForward(Constants.SUCCESS);
		}
		else
		{
			AddNewSessionDataBean addNewSessionDataBean = (AddNewSessionDataBean) formBeanStack
					.pop();
	
			if (addNewSessionDataBean == null)
			{
				throw new ApplicationException(ErrorKey.getErrorKey("errors.item.unknown"),
						null,AbstractDomainObject.parseClassName(objectName));
	
			}
			String forwardTo = addNewSessionDataBean.getForwardTo();
			AbstractActionForm sessionFormBean = updateSessionForm(
					abstractDomain, addNewSessionDataBean, formBeanStack, request);

			if ((sessionFormBean.getOperation().equals("edit")))
			{
				forward = getEditForward(mapping, forwardTo);
			}
			else
			{
				forward = (mapping.findForward(forwardTo));
			}
		}
		return forward;
	}

	private AbstractDomainObject insertDomainObject(HttpServletRequest request, AbstractActionForm abstractForm,
			ActionMessages messages, String objectName) throws ApplicationException
	{
		try
		{
			AbstractDomainObject abstractDomain;
			AbstractDomainObjectFactory abstractDomainObjectFactory = getAbstractDomainObjectFactory();
			abstractDomain = abstractDomainObjectFactory.getDomainObject(abstractForm.getFormId(),
					abstractForm);
			IBizLogic bizLogic = getIBizLogic(abstractForm);
			bizLogic.insert(abstractDomain, getSessionData(request));
			String target = new String(Constants.SUCCESS);
			QueryBizLogic queryBizLogic = getQueryBizLogic();
			addMessage(messages, abstractDomain, "add", queryBizLogic, objectName);
			return abstractDomain;
		}
		catch(BizLogicException bizLogicException)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),bizLogicException, 
					"Failed while updating in common add.");
		}
		catch(UserNotAuthorizedException excp)
		{
			throw  getErrorForUserNotAuthorized(request, excp);
		}
		catch (AssignDataException e)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),e, 
			"Failed while populating domain object in common add.");			
		}		
	}

	private String getForwardTo(HttpServletRequest request, AbstractDomainObject abstractDomain,
			AbstractActionForm abstractForm) throws ApplicationException
	{
		String target = Constants.SUCCESS;
		String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);
		if ("ForwardTo".equals(submittedFor))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, "Default");
			request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm,
				abstractDomain));
		}
		if (abstractForm.getForwardTo() != null && abstractForm.getForwardTo().trim().length() > 0)
		{
			String forwardTo = abstractForm.getForwardTo();
			target = forwardTo;
		}
		return target;
	}

	private ActionForward getEditForward(ActionMapping mapping, String forwardTo)
	{
		
		ActionForward editForward = new ActionForward();
		String addPath = (mapping.findForward(forwardTo)).getPath();
		String editPath = addPath.replaceFirst("operation=add", "operation=edit");
		editForward.setPath(editPath);
		return editForward;
	}

	private AbstractActionForm updateSessionForm(AbstractDomainObject abstractDomain,
			AddNewSessionDataBean addNewSessionDataBean, Stack formBeanStack, HttpServletRequest request)
	{
		AbstractActionForm sessionFormBean = addNewSessionDataBean
				.getAbstractActionForm();
		sessionFormBean.setAddNewObjectIdentifier(
				addNewSessionDataBean.getAddNewFor(),abstractDomain.getId());
		sessionFormBean.setMutable(false);

		if (formBeanStack.isEmpty())
		{
			HttpSession session = request.getSession();
			session.removeAttribute(Constants.FORM_BEAN_STACK);
			request.setAttribute(Constants.SUBMITTED_FOR, "Default");
		}
		else
		{
			request.setAttribute(Constants.SUBMITTED_FOR, "AddNew");
		}
		String formBeanName = Utility.getFormBeanName(sessionFormBean);
		request.setAttribute(formBeanName, sessionFormBean);
		
		return sessionFormBean;
	}

}
