package edu.wustl.common.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Session;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;


public class CommonEdtAction extends BaseAddEditAction
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(CommonEdtAction.class);


	/**
	 * Overrides the execute method of Action class.
	 * Adds / Updates the data in the database.
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @throws ApplicationException Generic exception
	 * */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException
	{
		String target;
			logger.debug("in method executeEdit()");
			AbstractActionForm abstractForm  = (AbstractActionForm)form;

			String objectName = getObjectName(abstractForm);
			AbstractDomainObject  abstractDomain = getDomainObject(abstractForm, objectName);
			updateDomainObject(request, abstractForm, abstractDomain, objectName);
			if (Constants.ACTIVITY_STATUS_DISABLED.
							equals(abstractForm.getActivityStatus()))
			{
				target = abstractForm.getOnSubmit();
			}
			else if (isStringNotEmpty(abstractForm.getOnSubmit()))
			{
				target = abstractForm.getOnSubmit();
			}
			else
			{
				target = getForwardToTarget(request, abstractForm, abstractDomain, objectName);
			}
			//Sets the domain object value in PrintMap for Label Printing
			request.setAttribute("forwardToPrintMap", generateForwardToPrintMap(abstractForm,
					abstractDomain));
			//Status message key.
			setStatusMsgKey(request, abstractForm);

		return mapping.findForward(target);
	}


	private void setStatusMsgKey(HttpServletRequest request, AbstractActionForm abstractForm)
	{
		StringBuffer statusMsgKey = new StringBuffer();
		statusMsgKey.append(abstractForm.getFormId()).append(".");
		statusMsgKey.append(abstractForm.isAddOperation());
		request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMsgKey.toString());
	}


	private AbstractDomainObject getDomainObject(AbstractActionForm abstractForm, String objectName)
			throws ApplicationException
	{
		try
		{
			AbstractDomainObject abstractDomain;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			abstractDomain = defaultBizLogic.populateDomainObject(objectName, Long.valueOf(abstractForm
					.getId()), abstractForm);
			return abstractDomain;
		}catch(BizLogicException bizLogicException)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),bizLogicException, 
			"Failed while populating domain object in common edit.");			
		}
		catch (DAOException e)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),e, 
			"Failed while populating domain object in common edit.");			
		}
		catch (AssignDataException e)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),e, 
			"Failed while populating domain object in common edit.");			
		}
	}


	private void updateDomainObject(HttpServletRequest request, AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain, String objectName) 
			throws ApplicationException
	{
		if(abstractDomain == null)
		{

			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item.unknown", AbstractDomainObject
					.parseClassName(objectName));
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item.unknown"),null,null);
		}
		persistUsingBizLogic(request, abstractForm, abstractDomain, objectName);
	}


	private void persistUsingBizLogic(HttpServletRequest request,AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain, String objectName)
			throws ApplicationException
	{
		try
		{
			Session sessionClean = getCleanSession();
			AbstractDomainObject abstractDomainOld;
			abstractDomainOld = (AbstractDomainObject) 
			sessionClean.load( objectName, Long.valueOf(abstractForm.getId()));
			IBizLogic bizLogic = getIBizLogic(abstractForm);
			bizLogic.update(abstractDomain, abstractDomainOld,getSessionData(request));
			sessionClean.close();
		}
		catch(BizLogicException bizLogicException)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),bizLogicException, 
					"Failed while updating in common edit.");
		}
		catch(UserNotAuthorizedException excp)
		{
			throw  getErrorForUserNotAuthorized(request, excp);
		}
	}


	private Session getCleanSession() throws ApplicationException
	{
		try
		{
			IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
			DAO dao = daofactory.getDAO();
			IConnectionManager connectionManager = dao.getConnectionManager();
			return connectionManager.getCleanSession();
		}
		catch(DAOException exception)
		{
			logger.error("Failed to get clean session during update ");
			throw new ApplicationException(ErrorKey.getDefaultErrorKey(), exception,
					"Failed in CommonEdit to get clean session" );
		}
	}


	private String getForwardToTarget(HttpServletRequest request, AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain, String objectName) throws ApplicationException
	{
		String target;
		setForwardToHashMap(request, abstractForm, abstractDomain);
		//setting target to ForwardTo attribute of submitted Form
		String forwardTo = abstractForm.getForwardTo();
		String pageOf = (String) request.getParameter(Constants.PAGEOF);

		if(isStringNotEmpty(forwardTo))
		{			
			target = forwardTo;	
		}
		else if (Constants.QUERY.equals(pageOf))
		{
			target = pageOf;
			ActionMessages messages = new ActionMessages();
			addMessage(messages, abstractDomain, "edit", getQueryBizLogic(), objectName);
			saveMessages(request, messages);				
		}
		else
		{
			target = Constants.SUCCESS;
		}
		// The successful edit message. Changes done according to bug# 945, 947
		return target;
	}


	private void setForwardToHashMap(HttpServletRequest request, AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws ApplicationException
	{
		String submittedFor = (String) request.getParameter(Constants.SUBMITTED_FOR);

		//----------ForwardTo Starts----------------
		if ((submittedFor != null) && (submittedFor.equals("ForwardTo")))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, "Default");
			request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm,
					abstractDomain));
		}
		//----------ForwardTo Ends----------------
	}

	private boolean isStringNotEmpty(String str)
	{
		boolean isNotEmpty;
		if (str != null
				&& str.trim().length() > 0)
		{
			isNotEmpty = true;
		}
		else
		{
			isNotEmpty = false;
		}
		return isNotEmpty;
	}
}
