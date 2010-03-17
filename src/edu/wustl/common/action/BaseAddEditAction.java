/**
 * <p>Title: CommonAddEditAction Class>
 * <p>Description:	This is base class used to Add/Edit the data in the database.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 21, 2005
 */

package edu.wustl.common.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IQueryBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.UIRepOfDomain;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.factory.IForwordToFactory;
import edu.wustl.common.processor.PersistObjectInputInterface;
import edu.wustl.common.processor.PersistObjectOutputInterface;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.dao.util.HibernateMetaDataFactory;

/**
 * This Class is used to Add/Edit data in the database.
 * @author gautam_shetty
 */
public abstract class BaseAddEditAction extends XSSSupportedAction
{

    protected class CommonAddInputDataCarrier implements PersistObjectInputInterface
    {
        AbstractActionForm actionForm;

        SessionDataBean sessionData;

        /**
         * @param actionForm
         * @param sessionData TODO
         */
        public CommonAddInputDataCarrier(final AbstractActionForm actionForm, final SessionDataBean sessionData)
        {
            super();
            this.actionForm = actionForm;
            this.sessionData = sessionData;
        }

        public int getDomainClassIdentifier()
        {
            return actionForm.getFormId();
        }

        public UIRepOfDomain getDomainObjectInputInterface()
        {
            // TODO Auto-generated method stub
            return actionForm;
        }

        public long getObjectId()
        {
            return actionForm.getId();
        }

        public SessionDataBean getSessionData()
        {
            return sessionData;
        }

    }
    protected class CommonAddOutputDataCarrier implements PersistObjectOutputInterface
    {
        AbstractDomainObject domainObject;

        String domainObjectName;

        /**
         * @return the domainObject
         */
        public AbstractDomainObject getDomainObject()
        {
            return domainObject;
        }

        /**
         * @return the domainObjectName
         */
        public String getDomainObjectName()
        {
            return domainObjectName;
        }

        public void setDomainObject(final AbstractDomainObject domainObject)
        {
            this.domainObject = domainObject;

        }

        public void setDomainObjectName(final String name)
        {
            domainObjectName = name;

        }

    }

    /**
     * LOGGER Logger - Generic LOGGER.
     */
    private static final Logger LOGGER = Logger.getCommonLogger(BaseAddEditAction.class);
    /**
     * This method will add the success message into ActionMessages object.
     * @param abstractDomain AbstractDomainObject
     * @param objectName String
     * @return display param.
     * @throws ApplicationException Application Exception
     */
    protected String[] addMessage(final AbstractDomainObject abstractDomain, final String objectName)
    throws ApplicationException
    {
        final String message = abstractDomain.getMessageLabel();
        String displayName;
        String[] displayparams;
        final IQueryBizLogic queryBizLogic = getQueryBizLogic();
        displayName = getDispNameOfDomainObj(abstractDomain, objectName, queryBizLogic);
        if (Validator.isEmpty(message))
        {
            displayparams = new String[1];
            displayparams[0] = displayName;
        }
        else
        {
            displayparams = new String[2];
            displayparams[0] = displayName;
            displayparams[1] = message;
        }
        return displayparams;
    }
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
    @Override
    public abstract ActionForward executeXSS(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws ApplicationException;
    /**
     * This method generates HashMap of data required to be forwarded.
     * @param abstractForm	Form submitted
     * @param abstractDomain	DomainObject Added/Edited
     * @return	HashMap of data required to be forwarded
     * @throws ApplicationException Application Exception.
     */
    protected Map generateForwardToHashMap(final AbstractActionForm abstractForm,
            final AbstractDomainObject abstractDomain) throws ApplicationException
            {
        try
        {
            Map forwardToHashMap;
            final IForwordToFactory factory = AbstractFactoryConfig.getInstance().getForwToFactory();
            final AbstractForwardToProcessor forwardToProcessor = factory.getForwardToProcessor();
            forwardToHashMap = forwardToProcessor.populateForwardToData(abstractForm,
                    abstractDomain);
            return forwardToHashMap;
        }
        catch (final BizLogicException exception)
        {
            LOGGER.debug("Error in generating data: "+exception.getMessage());
            throw new ApplicationException(exception.getErrorKey(),
                    exception,exception.getMsgValues());

        }
            }
    /**
     * This method generates HashMap of data required to be forwarded if Form is submitted for Print request.
     * @param abstractForm	Form submitted
     * @param abstractDomain	DomainObject Added/Edited
     * @return	HashMap of data required to be forwarded
     * @throws ApplicationException Application Exception
     */
    protected Map generateForwardToPrintMap(final AbstractActionForm abstractForm,
            final AbstractDomainObject abstractDomain) throws ApplicationException
            {
        try
        {
            final IForwordToFactory factory = AbstractFactoryConfig.getInstance().getForwToFactory();
            final AbstractForwardToProcessor forwardToProcessor = factory.getForwardToPrintProcessor();
            final Map forwardToPrintMap = forwardToProcessor.populateForwardToData(abstractForm,
                    abstractDomain);
            return forwardToPrintMap;
        }
        catch (final BizLogicException exception)
        {
            LOGGER.debug("Error in generating data: "+exception.getMessage());
            throw new ApplicationException(exception.getErrorKey(),
                    exception,exception.getMsgValues());

        }
            }
    /**
     * This method returns ActionForward object. In some cases we get forward url,
     * in that case we don't need to find ActionForward in mapping.
     * @param mapping ActionMapping object.
     * @param target target configured in struts-config file or url to forward.
     * @return ActionForward object
     */
    protected ActionForward getActionForward(final ActionMapping mapping,final String target)
    {
        ActionForward actionForward =mapping.findForward(target);
        if(null==actionForward)
        {
            actionForward = new ActionForward();
            actionForward.setPath(target );
        }
        return actionForward;
    }
    /**
     * get Display Name Of Domain Object.
     * @param abstractDomain AbstractDomainObject
     * @param objectName object Name
     * @param queryBizLogic IQueryBizLogic
     * @return display Name.
     */
    private String getDispNameOfDomainObj(final AbstractDomainObject abstractDomain, final String objectName,
            final IQueryBizLogic queryBizLogic)
    {
        String displayName;
        try
        {
            final HibernateMetaData hibernateMetaData = HibernateMetaDataFactory
            .getHibernateMetaData(CommonServiceLocator.getInstance().getAppName());
            if (hibernateMetaData != null)
            {
                displayName = queryBizLogic.getDisplayNamebyTableName(hibernateMetaData
                        .getTableName(abstractDomain.getClass()));
            }
            else
            {
                displayName = "";
            }
        }
        catch (final Exception excp)
        {
            displayName = AbstractDomainObject.parseClassName(objectName);
            LOGGER.error(excp.getMessage(), excp);
        }
        return displayName;
    }
    /**
     * Gets the input data interface.
     *
     * @param form
     *            the form
     * @param sessionData TODO
     *
     * @return the input data interface
     */
    protected PersistObjectInputInterface getInputDataInterface(final AbstractActionForm form, final SessionDataBean sessionData)
    {
        return new CommonAddInputDataCarrier(form, sessionData);
    }

    /**
     * Gets the output data interface.
     *
     * @return the output data interface
     */
    protected CommonAddOutputDataCarrier getOutputDataInterface()
    {
        return new CommonAddOutputDataCarrier();
    }
    /**
     * get object required for all API,for query purpose.
     * @return QueryBizLogic
     * @throws ApplicationException Application Exception
     */
    public IQueryBizLogic getQueryBizLogic() throws ApplicationException
    {

        try
        {
            final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
            return (IQueryBizLogic) factory.getBizLogic(Constants.QUERY_INTERFACE_ID);
        }
        catch (final BizLogicException exception)
        {
            LOGGER.debug("Failed to get object"+exception.getMessage());
            throw new ApplicationException(exception.getErrorKey(),
                    exception,exception.getMsgValues());
        }
    }

    /**
     * get session data from current session.
     * @param request HttpServletRequest
     * @return SessionDataBean
     */
    public SessionDataBean getSessionData(final HttpServletRequest request)
    {
        Object obj = request.getSession().getAttribute(Constants.SESSION_DATA);
        SessionDataBean sessionData = null;
        /**
         *  This if loop is specific to Password Security feature.
         */
        if (obj == null)
        {
            obj = request.getSession().getAttribute(Constants.TEMP_SESSION_DATA);
        }
        if (obj != null)
        {
            sessionData = (SessionDataBean) obj;
        }
        return sessionData;
    }

}
