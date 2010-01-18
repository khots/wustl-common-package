
package edu.wustl.common.action;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * This is the base class for all other Actions. The class provides generic
 * methods that are reusable by all subclasses. In addition, this class ensures
 * that the user is authenticated before calling the executeWorkflow of the
 * subclass. If the User is not authenticated then an
 * UserNotAuthenticatedException is thrown.
 *
 * @author Aarti Sharma
 *
 */
public abstract class BaseAction extends Action
{
	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(BaseAction.class);
	/**
	 * Method ensures that the user is authenticated before calling the
	 * executeAction of the subclass. If the User is not authenticated then an
	 * UserNotAuthenticatedException is thrown.
	 *
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @exception Exception Generic exception
	 */
	public final ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		LOGGER.info("Inside execute method of BaseAction ");
		ActionForward actionForward = null;
		 boolean isToExecuteAction = checkForXSSViolation(mapping,form,
				request, response,actionForward);

		if (isToExecuteAction)
        {
        	actionForward = executeAction(mapping, form, request, response);
        }
		return actionForward;
	}

	/**
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @param actionForward actionForward
	 * @return actionForward
	 * @throws Exception Exception
	 * @throws IOException IOException
	 */
	protected boolean checkForXSSViolation(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response,ActionForward actionForward)
				throws Exception, IOException
	{

		LOGGER.info("Checking for XSS validations");
		boolean isRedirected = false;

        isRedirected = getIsRedirected(request);
        final boolean isToRedirect = isToExecuteXSSSafeAction(request);

        final String ajaxRequest = request
                .getParameter(Constants.IS_AJAX_REQEUST);

        boolean isToExecuteAction = true;
        boolean isAjaxRequest = false;

        isAjaxRequest = getIsAjaxRequest(ajaxRequest);
        if (isToRedirect)
        {
            if (isAjaxRequest)
            {
                isToExecuteAction = processAjaxRequestViolations(request,
                        response);
                addXSSValidationError(request);
            }
            else
            {
                if (!isRedirected)
                {
                    isToExecuteAction = false;
                    actionForward = setActionForward(mapping, request,
							response);
                }
            }
        }
		return isToExecuteAction;
	}

	/**
	 * @param mapping mapping
	 * @param request request
	 * @param response response
	 * @return actionForward
	 * @throws IOException IOException
	 */
	private ActionForward setActionForward(ActionMapping mapping,
			HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		ActionForward actionForward = null;
		request.setAttribute(Constants.PAGE_REDIRECTED,
		        Constants.BOOLEAN_YES);
		if (mapping.getValidate())
		{
		    actionForward = mapping.getInputForward();
		}
		else
		{
		    response.sendRedirect("XssViolation.do");
		}
		return actionForward;
	}

	/**
	 * Check if it's redirected.
	 * @param request request
	 * @return true if redirected.
	 */
	private boolean getIsRedirected(final HttpServletRequest request)
    {
        boolean isRedirected = false;
        final Object redirectedObject = request
                .getAttribute(Constants.PAGE_REDIRECTED);
        if (redirectedObject != null
                && Constants.BOOLEAN_YES.equalsIgnoreCase(redirectedObject
                        .toString()))
        {
            isRedirected = true;
        }

        final Object errorObjects = request
                .getAttribute("org.apache.struts.action.ERROR");
        if (!isRedirected)
        {

            isRedirected = setIsRedirected(isRedirected, errorObjects);
        }
        return isRedirected;
    }

	/**
	 * @param isRedirected isRedirected
	 * @param errorObjects errorObjects
	 * @return redirect
	 */
	private boolean setIsRedirected(boolean isRedirected,
			final Object errorObjects)
	{
		boolean redirect = isRedirected;
		if (errorObjects instanceof ActionErrors
		        && !((ActionErrors) errorObjects).isEmpty())
		{
			redirect = true;
		}
		return redirect;
	}

	/**
	 * Check if it's Ajax Request.
	 * @param ajaxRequest ajaxRequest.
	 * @return true if ajax request.
	 */
	private boolean getIsAjaxRequest(final String ajaxRequest)
    {
        boolean isAjaxRequest = false;
        if (!Validator.isEmpty(ajaxRequest)
                && Constants.BOOLEAN_YES.equalsIgnoreCase(ajaxRequest))
        {
            isAjaxRequest = true;
        }
        return isAjaxRequest;
    }

	/**
	 * @param request request
	 * @param response response
	 * @return isToExecuteAction
	 * @throws IOException IOException
	 */
	private boolean processAjaxRequestViolations(
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException
    {
        boolean isToExecuteAction;
        isToExecuteAction = false;
        final Writer writer = response.getWriter();

        final Object propNameObject = request
                .getAttribute(Constants.VIOLATING_PROPERTY_NAMES);
        if (propNameObject instanceof List)
        {
            writer.append(Constants.XSS_ERROR_FIELDS
                    + Constants.PROPERTY_NAMES_DELIMITER);
            final List<String> propertyNamesList = (List<String>) propNameObject;
            for (final String string : propertyNamesList)
            {
                writer.append(string);
                writer.append(Constants.PROPERTY_NAMES_DELIMITER);
            }
        }
        return isToExecuteAction;
    }

	/**
	 * @param request request
	 * @return isXSSViolation
	 * @throws Exception Exception
	 */
	private boolean isToExecuteXSSSafeAction(final HttpServletRequest request) throws Exception
    {
        boolean isXSSViolation = false;

        if (request.getAttribute(Constants.VIOLATING_PROPERTY_NAMES)
        	instanceof List && ((List<String>)request.getAttribute
        			(Constants.VIOLATING_PROPERTY_NAMES)).size()>0)
        {
            addXSSValidationError(request);
            isXSSViolation = true;
        }
        return isXSSViolation;
    }

	/**
	 * @param request request
	 */
	private void addXSSValidationError(final HttpServletRequest request)
    {
        final Object isToAddError = request.getAttribute("isToAddError");
        if (isToAddError instanceof Boolean
                && ((Boolean) isToAddError).equals(Boolean.TRUE))
        {
            final ActionErrors errors;
            final Object actionErrosObject = request
                    .getAttribute("org.apache.struts.action.ERROR");
            if (actionErrosObject instanceof ActionErrors)
            {
                errors = (ActionErrors) actionErrosObject;
            }
            else
            {
                errors = new ActionErrors();
            }
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
                    "errors.xssvulnerable"));

            saveErrors(request, errors);
            request.setAttribute("isToAddError", false);
        }
    }

	/**
	 * sets the URL of the application in proper format.
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 */
	protected void preExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response)
	{
		/** Added by amit_doshi
		 *  code reviewer abhijit_naik
		 */
		if(request.getRequestURL()!=null)
		{
			CommonServiceLocator.getInstance().setAppURL(request.getRequestURL().toString());
		}
	}

	/**
	 * Returns the current User authenticated by CSM Authentication from current session.
	 * @param request HttpServletRequest
	 * @return userName if SessionDataBean is not null
	 */
	protected String getUserLoginName(HttpServletRequest request)
	{
		String userName = null;
		SessionDataBean sessionData = getSessionData(request);
		if (sessionData != null)
		{
			userName = sessionData.getUserName();
		}
		return userName;
	}
	/**
	 * get data from the current session.
	 * @param request HttpServletRequest
	 * @return SessionDataBean from session
	 */
	protected SessionDataBean getSessionData(HttpServletRequest request)
	{
		return (SessionDataBean) request.getSession().getAttribute(Constants.SESSION_DATA);
	}
	/**
	 * Subclasses should implement the action's business logic in this method
	 * and can be sure that an authenticated user is present.
	 *
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @throws Exception generic exception
	 */
	protected abstract ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception;
	/**
	 * This function checks call to the action and sets/removes required attributes
	 *  if AddNew or ForwardTo activity is executing.
	 * @param request - HTTPServletRequest calling the action
	 */
	protected void checkAddNewOperation(HttpServletRequest request)
	{
		String submittedFor = (String) request.getAttribute(Constants.SUBMITTED_FOR);

		String submittedForParam = (String) request.getParameter(Constants.SUBMITTED_FOR);

		if ((Constants.SUBMITTED_FOR_ADD_NEW.equals(submittedFor)))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, Constants.SUBMITTED_FOR_ADD_NEW);
		}
		else if (Constants.SUBMITTED_FOR_ADD_NEW.equals(submittedForParam))
		{
			addNewOperation(request, submittedFor);
		}
		else if (Constants.SUBMITTED_FOR_FORWARD_TO.equals(submittedFor))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, Constants.SUBMITTED_FOR_FORWARD_TO);
			removeFormBeanStack(request);
		}
		//if AddNew loop is over
		else if (Constants.SUBMITTED_FOR_DEFAULT.equals(submittedFor))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, Constants.SUBMITTED_FOR_DEFAULT);
			removeFormBeanStack(request);
		}
		//if AddNew or ForwardTo loop is broken...
		else
		{
			removeFormBeanStack(request);
		}
	}
	/**
	 * sets required attributes.
	 * @param request HTTPServletRequest calling the action
	 * @param submittedFor submitted For.
	 */
	private void addNewOperation(HttpServletRequest request, String submittedFor)
	{
		if (Constants.SUBMITTED_FOR_DEFAULT.equals(submittedFor))
		{
			request.setAttribute(Constants.SUBMITTED_FOR, Constants.SUBMITTED_FOR_DEFAULT);
		}
		else
		{
			request.setAttribute(Constants.SUBMITTED_FOR, Constants.SUBMITTED_FOR_ADD_NEW);
		}
	}
	/**
	 * remove data from current session.
	 * @param request HttpServletRequest
	 */
	private void removeFormBeanStack(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		if ((session.getAttribute(Constants.FORM_BEAN_STACK)) != null)
		{
			session.removeAttribute(Constants.FORM_BEAN_STACK);
		}
	}
	/**
	 * This method calls the specified method passed as parameter.
	 * This allows us to have different entry points.
	 * to an action class. To use this method,
	 * 1. pass the method name as parameter in some request variable
	 * 2. in your executeAction/executeSecureAction method, get the parameter passed.
	 * and pass it to this method.
	 * 3. the control will directly go to the method name of the class that you specified.
	 * This way you can reuse the same action multiple times.
	 *
	 * @param methodName - name of the method to be called
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return 	ActionForward
	 * @throws Exception generic exception
	 */
	protected ActionForward invokeMethod(String methodName, ActionMapping mapping, ActionForm form, // NOPMD
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ActionForward actionForward = null;
		if (!"".equals(methodName))
		{
			Method method = getMethod(methodName, this.getClass());
			if (method != null)
			{
				Object[] args = {mapping, form, request, response};
				actionForward = (ActionForward) method.invoke(this, args);
			}
		}
		return actionForward;
	}
	/**
	 * This method returns the method with the specified name if the method exists. Return null other wise.
	 * @param name - String name of method
	 * @param className - Class
	 * @return method object
	 */
	protected Method getMethod(String name, Class className)
	{
		Method method = null;
		Class[] types = {ActionMapping.class, ActionForm.class, HttpServletRequest.class,
				HttpServletResponse.class};
		try
		{
			method = className.getDeclaredMethod(name, types);
		}
		catch (NoSuchMethodException excp1)
		{
			LOGGER.error(excp1.getMessage(), excp1);
		}
		catch (SecurityException excp3)
		{
			LOGGER.error(excp3.getMessage(), excp3);
		}
		return method;
	}
}