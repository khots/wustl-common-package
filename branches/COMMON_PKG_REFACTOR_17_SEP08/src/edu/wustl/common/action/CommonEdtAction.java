
package edu.wustl.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Session;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * This Class is used to Edit data in the database.
 */
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
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException
	{
		String target;
		logger.debug("in method executeEdit()");
		AbstractActionForm abstractForm = (AbstractActionForm) form;

		String objectName = getObjectName(abstractForm);
		AbstractDomainObject abstractDomain = getDomainObject(abstractForm, objectName);
		updateDomainObject(request, abstractForm, abstractDomain, objectName);
		if (Constants.ACTIVITY_STATUS_DISABLED.equals(abstractForm.getActivityStatus()))
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

	/**
	 * This method sets Status message key.
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm.
	 */
	private void setStatusMsgKey(HttpServletRequest request, AbstractActionForm abstractForm)
	{
		StringBuffer statusMsgKey = new StringBuffer();
		statusMsgKey.append(abstractForm.getFormId()).append('.');
		statusMsgKey.append(abstractForm.isAddOperation());
		request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMsgKey.toString());
	}

	/**
	 * This method gets Domain Object.
	 * @param abstractForm AbstractActionForm
	 * @param objectName object Name
	 * @return AbstractDomainObject
	 * @throws ApplicationException Application Exception.
	 */
	private AbstractDomainObject getDomainObject(AbstractActionForm abstractForm, String objectName)
			throws ApplicationException
	{
		try
		{
			AbstractDomainObject abstractDomain;
			DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
			abstractDomain = defaultBizLogic.populateDomainObject(objectName, Long
					.valueOf(abstractForm.getId()), abstractForm);
			return abstractDomain;
		}
		catch (BizLogicException bizLogicException)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"), bizLogicException,
					"Failed while populating domain object in common edit.");
		}
	}

	/**
	 * This method updates Domain Object.
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm
	 * @param abstractDomain AbstractDomainObject
	 * @param objectName object Name
	 * @throws ApplicationException Application Exception.
	 */
	private void updateDomainObject(HttpServletRequest request, AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain, String objectName) throws ApplicationException
	{
		if (abstractDomain == null)
		{

			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item.unknown", AbstractDomainObject
					.parseClassName(objectName));
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item.unknown"), null, null);
		}
		persistUsingBizLogic(request, abstractForm, abstractDomain, objectName);
	}

	/**
	 * This method updates Domain Object.
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm
	 * @param abstractDomain AbstractDomainObject
	 * @param objectName object Name
	 * @throws ApplicationException Application Exception.
	 */
	private void persistUsingBizLogic(HttpServletRequest request, AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain, String objectName) throws ApplicationException
	{
		try
		{
			Session sessionClean = getCleanSession();
			AbstractDomainObject abstractDomainOld;
			abstractDomainOld = (AbstractDomainObject) sessionClean.load(objectName, Long
					.valueOf(abstractForm.getId()));
			IBizLogic bizLogic = getIBizLogic(abstractForm);
			bizLogic.update(abstractDomain, abstractDomainOld, getSessionData(request));
			sessionClean.close();
		}
		catch (BizLogicException bizLogicException)
		{
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"), bizLogicException,
					"Failed while updating in common edit.");
		}
	}

	/**
	 * This method gets Clean Session.
	 * @return Clean Session.
	 * @throws ApplicationException ApplicationException
	 */
	private Session getCleanSession() throws ApplicationException
	{
		try
		{
			IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory();
			DAO dao = daofactory.getDAO();
			IConnectionManager connectionManager = dao.getConnectionManager();
			return connectionManager.getCleanSession();
		}
		catch (DAOException exception)
		{
			logger.error("Failed to get clean session during update ");
			throw new ApplicationException(ErrorKey.getDefaultErrorKey(), exception,
					"Failed in CommonEdit to get clean session");
		}
	}

	/**
	 * This method gets ForwardTo Target.
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm
	 * @param abstractDomain AbstractDomainObject
	 * @param objectName object Name
	 * @return target
	 * @throws ApplicationException Application Exception
	 */
	private String getForwardToTarget(HttpServletRequest request, AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain, String objectName) throws ApplicationException
	{
		String target;
		setForwardToHashMap(request, abstractForm, abstractDomain);
		//setting target to ForwardTo attribute of submitted Form
		String forwardTo = abstractForm.getForwardTo();
		String pageOf = (String) request.getParameter(Constants.PAGEOF);

		if (isStringNotEmpty(forwardTo))
		{
			target = forwardTo;
		}
		else if (Constants.QUERY.equals(pageOf))
		{
			target = pageOf;
			ActionMessages messages = new ActionMessages();
			String[] displayNameParams = addMessage(abstractDomain, objectName);
			messages.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("object. edit"
					+ ".successOnly", displayNameParams));
			saveMessages(request, messages);
		}
		else
		{
			target = Constants.SUCCESS;
		}
		// The successful edit message. Changes done according to bug# 945, 947
		return target;
	}

	/**
	 * This method sets ForwardTo HashMap.
	 * @param request HttpServletRequest
	 * @param abstractForm AbstractActionForm
	 * @param abstractDomain AbstractDomainObject
	 * @throws ApplicationException Application Exception
	 */
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

	/**
	 * This method checks is String Not Empty.
	 * @param str String
	 * @return true if not empty else false.
	 */
	private boolean isStringNotEmpty(String str)
	{
		boolean isNotEmpty = false;
		if (str != null && !str.trim().equals(TextConstants.EMPTY_STRING))
		{
			isNotEmpty = true;
		}
		return isNotEmpty;
	}
}
