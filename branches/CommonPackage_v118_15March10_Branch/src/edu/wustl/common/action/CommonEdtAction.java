package edu.wustl.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.processor.CommonEditRequestProcessor;
import edu.wustl.common.processor.PersistObjectInputInterface;
import edu.wustl.common.processor.PersistObjectOutputInterface;
import edu.wustl.common.processor.RequestProcessorInterface;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to Edit data in the database.
 */
public class CommonEdtAction extends BaseAddEditAction
{
    /**
     * LOGGER Logger - Generic LOGGER.
     */
    private static final Logger LOGGER = Logger.getCommonLogger(CommonEdtAction.class);

    protected final RequestProcessorInterface<PersistObjectInputInterface,
    PersistObjectOutputInterface> processor = new CommonEditRequestProcessor();
    /**
     * Overrides the execute method of Action class. Adds / Updates the data in
     * the database.
     *
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     * @throws ApplicationException
     *             Generic exception
     * */
    @Override
    public ActionForward executeXSS(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws ApplicationException
            {
        final PersistObjectInputInterface inputInterface = getInputDataInterface((AbstractActionForm) form,
                getSessionData(request));
        final CommonAddOutputDataCarrier outputInterface = getOutputDataInterface();
        // preProcess(request, form);
        processor.process(inputInterface, outputInterface);
        postProcess((AbstractActionForm) form, outputInterface, request);
        return getForward(mapping, (AbstractActionForm) form, outputInterface, request);
            }


    /**
     * Gets the forward.
     *
     * @param mapping the mapping
     * @param abstractForm the abstract form
     * @param outputInterface the output interface
     * @param request the request
     *
     * @return the forward
     *
     * @throws ApplicationException the application exception
     */
    private ActionForward getForward(final ActionMapping mapping, final AbstractActionForm abstractForm,
            final CommonAddOutputDataCarrier outputInterface, final HttpServletRequest request)
    throws ApplicationException
    {
        String target;
        if (abstractForm.getActivityStatus().equals(Status.ACTIVITY_STATUS_DISABLED.toString()))
        {
            target = abstractForm.getOnSubmit();
        }
        else if (isStringNotEmpty(abstractForm.getOnSubmit()))
        {
            target = abstractForm.getOnSubmit();
        }
        else
        {
            target = getForwardToTarget(request, abstractForm, outputInterface.getDomainObject(), outputInterface
                    .getDomainObjectName());
        }

        return getActionForward(mapping, target);
    }

    /**
     * This method gets ForwardTo Target.
     *
     * @param request
     *            HttpServletRequest
     * @param abstractForm
     *            AbstractActionForm
     * @param abstractDomain
     *            AbstractDomainObject
     * @param objectName
     *            object Name
     * @return target
     * @throws ApplicationException
     *             Application Exception
     */
    private String getForwardToTarget(final HttpServletRequest request, final AbstractActionForm abstractForm,
            final AbstractDomainObject abstractDomain, final String objectName) throws ApplicationException
            {
        String target;
        setForwardToHashMap(request, abstractForm, abstractDomain);
        // setting target to ForwardTo attribute of submitted Form
        final String forwardTo = abstractForm.getForwardTo();
        final String pageOf = request.getParameter(Constants.PAGEOF);

        if (isStringNotEmpty(forwardTo))
        {
            target = forwardTo;
        }
        else if (Constants.QUERY.equals(pageOf))
        {
            target = pageOf;
            final ActionMessages messages = new ActionMessages();
            final String[] displayNameParams = addMessage(abstractDomain, objectName);
            messages.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("object. edit" + ".successOnly",
                    displayNameParams));
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
     * This method checks is String Not Empty.
     *
     * @param str
     *            String
     * @return true if not empty else false.
     */
    private boolean isStringNotEmpty(final String str)
    {
        boolean isNotEmpty = false;
        if (str != null && !str.trim().equals(TextConstants.EMPTY_STRING))
        {
            isNotEmpty = true;
        }
        return isNotEmpty;
    }


    private void postProcess(final AbstractActionForm form, final CommonAddOutputDataCarrier outputInterface,
            final HttpServletRequest request) throws ApplicationException
            {

        final String objectName = outputInterface.getDomainObjectName();
        final AbstractDomainObject abstractDomain = outputInterface.getDomainObject();
        setSuccessMsg(request, objectName, abstractDomain);
        request.setAttribute("forwardToPrintMap", generateForwardToPrintMap(form, abstractDomain));
        // Status message key.
        setStatusMsgKey(request, form);
            }

    /**
     * This method sets ForwardTo HashMap.
     *
     * @param request
     *            HttpServletRequest
     * @param abstractForm
     *            AbstractActionForm
     * @param abstractDomain
     *            AbstractDomainObject
     * @throws ApplicationException
     *             Application Exception
     */
    private void setForwardToHashMap(final HttpServletRequest request, final AbstractActionForm abstractForm,
            final AbstractDomainObject abstractDomain) throws ApplicationException
            {
        final String submittedFor = request.getParameter(Constants.SUBMITTED_FOR);

        // ----------ForwardTo Starts----------------
        if (submittedFor != null && submittedFor.equals("ForwardTo"))
        {
            request.setAttribute(Constants.SUBMITTED_FOR, "Default");
            request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm, abstractDomain));
        }
        // ----------ForwardTo Ends----------------
            }

    /**
     * This method sets Status message key.
     *
     * @param request
     *            HttpServletRequest
     * @param abstractForm
     *            AbstractActionForm.
     */
    private void setStatusMsgKey(final HttpServletRequest request, final AbstractActionForm abstractForm)
    {
        final StringBuffer statusMsgKey = new StringBuffer();
        statusMsgKey.append(abstractForm.getFormId()).append('.');
        statusMsgKey.append(abstractForm.isAddOperation());
        request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMsgKey.toString());
    }

    /**
     * Set Success Message.
     *
     * @param request
     *            HttpServletRequest
     * @param messages
     *            ActionMessages
     * @param objectName
     *            object Name
     * @param abstractDomain
     *            AbstractDomainObject
     * @throws ApplicationException
     *             Application Exception.
     */
    private void setSuccessMsg(final HttpServletRequest request, final String objectName,
            final AbstractDomainObject abstractDomain) throws ApplicationException
            {
        final ActionMessages messages = new ActionMessages();
        final String[] displayNameParams = addMessage(abstractDomain, objectName);
        messages.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("object.edit.successOnly", displayNameParams));
        saveMessages(request, messages);
            }

}
