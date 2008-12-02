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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.factory.AbstractForwardToFactory;
import edu.wustl.common.factory.MasterFactory;
import edu.wustl.common.querysuite.bizlogic.QueryBizLogic;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to Add/Edit data in the database.
 * @author gautam_shetty
 */
public abstract class BaseAddEditAction extends Action
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(BaseAddEditAction.class);


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
	public abstract ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException;
	/**
	 * return action error in case the user,in request is not authorized.
	 * @param request HttpServletRequest
	 * @param excp UserNotAuthorizedException
	 * @return ActionError
	 */
	protected ApplicationException getErrorForUserNotAuthorized(HttpServletRequest request,
			UserNotAuthorizedException excp)
	{
		AbstractDomainObject abstractDomain = null;
		SessionDataBean sessionDataBean = getSessionData(request);

		StringBuffer msgParams = new StringBuffer();

		if (sessionDataBean != null)
		{

			msgParams.append(sessionDataBean.getUserName())
			.append(ApplicationException.ERR_MSG_VALUES_SEPARATOR);
		}

		String className = getActualClassName(abstractDomain.getClass().getName());
		msgParams.append(className).append(ApplicationException.ERR_MSG_VALUES_SEPARATOR);

		String decoratedPrivName = Utility.getDisplayLabelForUnderscore(excp
				.getPrivilegeName());
		msgParams.append(decoratedPrivName).append(ApplicationException.ERR_MSG_VALUES_SEPARATOR);

		String baseObject = excp.getBaseObject();

		if (TextConstants.EMPTY_STRING.equals(baseObject) || baseObject == null)
		{
			baseObject = className;
		}
		msgParams.append(baseObject);
		ErrorKey errorKey = ErrorKey.getErrorKey("access.addedit.object.denied");
		return new ApplicationException(errorKey,excp,msgParams.toString());
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
	public String getObjectName(AbstractActionForm abstractForm)
	{

		AbstractDomainObjectFactory abstractDomainObjectFactory = getAbstractDomainObjectFactory();
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
	public QueryBizLogic getQueryBizLogic() throws ApplicationException
	{
		try
		{
			return (QueryBizLogic) AbstractBizLogicFactory.getBizLogic(ApplicationProperties
				.getValue("app.bizLogicFactory"), "getBizLogic", Constants.QUERY_INTERFACE_ID);
		}
		catch(BizLogicException bizLogicException)
		{
			logger.error("Failed to get QueryBizLogic object from BizLogic Factory");
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),bizLogicException, 
			"Failed to get QueryBizLogic in base Add/Edit.");
			
		}
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
	protected HashMap generateForwardToHashMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws ApplicationException 
	{
		try
		{
			HashMap forwardToHashMap;
			AbstractForwardToProcessor forwardToProcessor = AbstractForwardToFactory
					.getForwardToProcessor(ApplicationProperties.getValue("app.forwardToFactory"),
							"getForwardToProcessor");
			forwardToHashMap = (HashMap) forwardToProcessor.populateForwardToData(abstractForm,
					abstractDomain);
			return forwardToHashMap;
		}
		catch(BizLogicException bizLogicException)
		{
			logger.error("Failed to generateforward hash map", bizLogicException);
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),bizLogicException, 
			"Failed at generateForwardToHashMap in common edit.");

		}
	}

	/**
	 * This method generates HashMap of data required to be forwarded if Form is submitted for Print request.
	 * @param abstractForm	Form submitted
	 * @param abstractDomain	DomainObject Added/Edited
	 * @return	HashMap of data required to be forwarded
	 * @throws BizLogicException biz logic exception
	 */
	protected HashMap generateForwardToPrintMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws ApplicationException
	{
		try
		{
			HashMap forwardToPrintMap = null;
			AbstractForwardToProcessor forwardToProcessor = AbstractForwardToFactory
					.getForwardToProcessor(ApplicationProperties.getValue("app.forwardToFactory"),
							"getForwardToPrintProcessor");
			forwardToPrintMap = (HashMap) forwardToProcessor.populateForwardToData(abstractForm,
					abstractDomain);
			return forwardToPrintMap;
		}
		catch(BizLogicException bizLogicException)
		{
			logger.error("Failed to generateforward print map", bizLogicException);
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),bizLogicException, 
			"Failed at generateForwardToPrintMap in common edit.");

		}
	}

	/**
	 * This method will add the success message into ActionMessages object.
	 * @param messages ActionMessages
	 * @param abstractDomain AbstractDomainObject
	 * @param addoredit String
	 * @param queryBizLogic QueryBizLogic
	 * @param objectName String
	 */
	protected void addMessage(ActionMessages messages, AbstractDomainObject abstractDomain,
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
					+ ".success",  displayName, message));
		}
		else
		{
			messages.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("object." + addoredit
					+ ".successOnly", displayName));
		}
	}
}