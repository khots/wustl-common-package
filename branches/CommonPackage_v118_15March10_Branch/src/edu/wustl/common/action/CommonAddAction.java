package edu.wustl.common.action;

import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.AddNewSessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.processor.CommonAddRequestProcessor;
import edu.wustl.common.processor.PersistObjectInputInterface;
import edu.wustl.common.processor.PersistObjectOutputInterface;
import edu.wustl.common.processor.RequestProcessorInterface;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to Add data in the database.
 */
public class CommonAddAction extends BaseAddEditAction
{
    /**
     * LOGGER Logger - Generic LOGGER.
     */
    private static final Logger LOGGER = Logger.getCommonLogger(CommonAddAction.class);

    /** The processor. */
    protected final RequestProcessorInterface<PersistObjectInputInterface,
    PersistObjectOutputInterface> processor = new CommonAddRequestProcessor();


    /**
     * This method get data from form of current session and add it for further
     * operation.
     *
     * @param mapping
     *            ActionMapping
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param form
     *            Action Form.
     * @return ActionForward
     * @throws ApplicationException
     *             Application Exceptio.
     */
    @Override
    public ActionForward executeXSS(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws ApplicationException
            {
        final PersistObjectInputInterface inputInterface = getInputDataInterface((AbstractActionForm) form, getSessionData(request));
        final CommonAddOutputDataCarrier outputInterface = getOutputDataInterface();
        preProcess(request, form);
        processor.process(inputInterface, outputInterface);
        postProcess((AbstractActionForm) form, outputInterface, request);
        return getForward(mapping, (AbstractActionForm) form, outputInterface, request);
            }

    /**
     * This method gets Edit Forward.
     *
     * @param mapping
     *            ActionMapping
     * @param forwardTo
     *            forward To.
     * @return ActionForward.
     */
    private ActionForward getEditForward(final ActionMapping mapping, final String forwardTo)
    {

        final ActionForward editForward = new ActionForward();
        final String addPath = mapping.findForward(forwardTo).getPath();
        final String editPath = addPath.replaceFirst("operation=add", "operation=edit");
        editForward.setPath(editPath);
        return editForward;
    }

    /**
     * Gets the forward.
     *
     * @param mapping
     *            the mapping
     * @param abstractForm
     *            the abstract form
     * @param outputInterface
     *            the output interface
     * @param request
     *            the request
     *
     * @return the forward
     *
     * @throws ApplicationException
     *             the application exception
     */
    private ActionForward getForward(final ActionMapping mapping, final AbstractActionForm abstractForm,
            final CommonAddOutputDataCarrier outputInterface, final HttpServletRequest request)
    throws ApplicationException
    {
        final String submittedFor = request.getParameter(Constants.SUBMITTED_FOR);
        ActionForward forward;
        if ("AddNew".equals(submittedFor))
        {
            // Retrieving AddNewSessionDataBean from Stack
            forward = getForwardToFromStack(mapping, request, outputInterface.getDomainObject(), outputInterface
                    .getDomainObjectName());

        }
        else
        {
            final String target = getForwardTo(request, outputInterface.getDomainObject(), abstractForm);
            forward = mapping.findForward(target);
        }
        return forward;
    }

    /**
     * This method gets target.
     *
     * @param request
     *            HttpServletRequest
     * @param abstractDomain
     *            AbstractDomainObject
     * @param abstractForm
     *            AbstractActionForm
     * @return target
     * @throws ApplicationException
     *             Application Exception
     */
    private String getForwardTo(final HttpServletRequest request, final AbstractDomainObject abstractDomain,
            final AbstractActionForm abstractForm) throws ApplicationException
            {
        String target = Constants.SUCCESS;
        final String submittedFor = request.getParameter(Constants.SUBMITTED_FOR);
        if ("ForwardTo".equals(submittedFor))
        {
            request.setAttribute(Constants.SUBMITTED_FOR, "Default");
            request.setAttribute("forwardToHashMap", generateForwardToHashMap(abstractForm, abstractDomain));
        }
        if (abstractForm.getForwardTo() != null && abstractForm.getForwardTo().trim().length() > 0)
        {
            final String forwardTo = abstractForm.getForwardTo();
            target = forwardTo;
        }
        return target;
            }

    /**
     * Retrieving AddNewSessionDataBean from Stack.
     *
     * @param mapping
     *            ActionMapping
     * @param request
     *            HttpServletRequest
     * @param abstractDomain
     *            AbstractDomainObject
     * @param objectName
     *            object Name
     * @return ActionForward
     * @throws ApplicationException
     *             Application Exception.
     */
    private ActionForward getForwardToFromStack(final ActionMapping mapping, final HttpServletRequest request,
            final AbstractDomainObject abstractDomain, final String objectName) throws ApplicationException
            {

        final Stack formBeanStack = (Stack) request.getSession().getAttribute(Constants.FORM_BEAN_STACK);
        ActionForward forward;

        if (formBeanStack == null)
        {
            forward = mapping.findForward(Constants.SUCCESS);
        }
        else
        {
            final AddNewSessionDataBean addNewSessionDataBean = (AddNewSessionDataBean) formBeanStack.pop();

            if (addNewSessionDataBean == null)
            {
                throw new ApplicationException(ErrorKey.getErrorKey("errors.item.unknown"), null,
                        AbstractDomainObject.parseClassName(objectName));

            }
            final String forwardTo = addNewSessionDataBean.getForwardTo();
            final AbstractActionForm sessionFormBean = updateSessionForm(abstractDomain, addNewSessionDataBean,
                    formBeanStack, request);

            if (sessionFormBean.getOperation().equals("edit"))
            {
                forward = getEditForward(mapping, forwardTo);
            }
            else
            {
                forward = mapping.findForward(forwardTo);
            }
        }
        return forward;
            }


    /**
     * Post process.
     *
     * @param abstractForm
     *            the abstract form
     * @param outputInterface
     *            the output interface
     * @param request
     *            the request
     *
     * @throws ApplicationException
     *             the application exception
     */
    private void postProcess(final AbstractActionForm abstractForm,
            final CommonAddOutputDataCarrier outputInterface, final HttpServletRequest request)
    throws ApplicationException
    {
        abstractForm.setId(outputInterface.getDomainObject().getId().longValue());
        abstractForm.setMutable(false);
        setSuccessMsg(request, outputInterface.getDomainObjectName(), outputInterface.getDomainObject());
        setRequestAttributes(abstractForm, outputInterface, request);
    }

    private void preProcess(final HttpServletRequest request, final ActionForm form)
    {
        // TODO Auto-generated method stub

    }

    /**
     * Sets the request attributes.
     *
     * @param abstractForm
     *            the abstract form
     * @param outputInterface
     *            the output interface
     * @param request
     *            the request
     *
     * @throws ApplicationException
     *             the application exception
     */
    private void setRequestAttributes(final AbstractActionForm abstractForm,
            final CommonAddOutputDataCarrier outputInterface, final HttpServletRequest request)
    throws ApplicationException
    {
        request.setAttribute(Constants.SYSTEM_IDENTIFIER, outputInterface.getDomainObject().getId());
        request.setAttribute("forwardToPrintMap", generateForwardToPrintMap(abstractForm, outputInterface
                .getDomainObject()));
        final StringBuffer statusMessageKey = new StringBuffer();
        statusMessageKey.append(abstractForm.getFormId()).append('.').append(abstractForm.isAddOperation());
        request.setAttribute(Constants.STATUS_MESSAGE_KEY, statusMessageKey.toString());
    }

    /**
     * Set Success Message.
     *
     * @param request
     *            HttpServletRequest
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
        messages.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("object.add" + ".successOnly",
                displayNameParams));
        saveMessages(request, messages);
            }

    /**
     * This method updates Session Form.
     *
     * @param abstractDomain
     *            AbstractDomainObject
     * @param addNewSessionDataBean
     *            AddNewSessionDataBean
     * @param formBeanStack
     *            form Bean Stack
     * @param request
     *            HttpServletRequest
     * @return AbstractActionForm.
     */
    private AbstractActionForm updateSessionForm(final AbstractDomainObject abstractDomain,
            final AddNewSessionDataBean addNewSessionDataBean, final Stack formBeanStack,
            final HttpServletRequest request)
    {
        final AbstractActionForm sessionFormBean = addNewSessionDataBean.getAbstractActionForm();
        sessionFormBean.setAddNewObjectIdentifier(addNewSessionDataBean.getAddNewFor(), abstractDomain.getId());
        sessionFormBean.setMutable(false);

        if (formBeanStack.isEmpty())
        {
            final HttpSession session = request.getSession();
            session.removeAttribute(Constants.FORM_BEAN_STACK);
            request.setAttribute(Constants.SUBMITTED_FOR, "Default");
        }
        else
        {
            request.setAttribute(Constants.SUBMITTED_FOR, "AddNew");
        }
        final String formBeanName = Utility.getFormBeanName(sessionFormBean);
        request.setAttribute(formBeanName, sessionFormBean);

        return sessionFormBean;
    }

}
