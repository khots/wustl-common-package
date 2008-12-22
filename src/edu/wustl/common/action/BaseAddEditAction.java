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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.bizlogic.IQueryBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exception.ParseException;
import edu.wustl.common.factory.AbstractDomainObjectFactory;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.factory.IForwordToFactory;
import edu.wustl.common.factory.MasterFactory;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.util.HibernateMetaData;

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
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException;

	/**
	 * return the  actual class name.
	 * @param name String
	 * @return String
	 */
	public String getActualClassName(String name)
	{
		String className = name;
		if (className != null && !className.trim().equals(TextConstants.EMPTY_STRING))
		{
			String splitter = "\\.";
			String[] arr = className.split(splitter);
			if (arr != null && arr.length != 0)
			{
				className = arr[arr.length - Constants.ONE];
			}
		}
		return className;
	}

	/**
	 * get Object Name.
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
	 * @throws ApplicationException Application Exception
	 */
	public IQueryBizLogic getQueryBizLogic() throws ApplicationException
	{

		try
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory(
					"bizLogicFactory");
			return (IQueryBizLogic) factory.getBizLogic(Constants.QUERY_INTERFACE_ID);
		}
		catch (ParseException parseException)
		{
			logger.error("Failed to get QueryBizLogic object from BizLogic Factory");
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"), parseException,
					"Failed to get QueryBizLogic in base Add/Edit.");
		}
	}

	/**
	 * @param abstractForm AbstractActionForm
	 * @return IBizLogic
	 * @throws ApplicationException Application Exception.
	 */
	public IBizLogic getIBizLogic(AbstractActionForm abstractForm) throws ApplicationException
	{
		try
		{
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory(
					"bizLogicFactory");
			return factory.getBizLogic(abstractForm.getFormId());
		}
		catch (ParseException parseException)
		{
			logger.error("Failed to get BizLogic object from BizLogic Factory");
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"), parseException,
					"Failed to get BizLogic in base Add/Edit.");
		}
	}

	/**
	 * get session data from current session.
	 * @param request HttpServletRequest
	 * @return SessionDataBean
	 */
	public SessionDataBean getSessionData(HttpServletRequest request)
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

	/**
	 * This method generates HashMap of data required to be forwarded.
	 * @param abstractForm	Form submitted
	 * @param abstractDomain	DomainObject Added/Edited
	 * @return	HashMap of data required to be forwarded
	 * @throws ApplicationException Application Exception.
	 */
	protected HashMap generateForwardToHashMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws ApplicationException
	{
		try
		{
			HashMap forwardToHashMap;
			IForwordToFactory factory = AbstractFactoryConfig.getInstance().getForwToFactory(
					"forwardToFactory");
			AbstractForwardToProcessor forwardToProcessor = factory.getForwardToProcessor();
			forwardToHashMap = (HashMap) forwardToProcessor.populateForwardToData(abstractForm,
					abstractDomain);
			return forwardToHashMap;
		}
		catch (ParseException parseException)
		{
			logger.error("Failed to generateforward hash map", parseException);
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"), parseException,
					"Failed at generateForwardToHashMap in base Add/Edit.");

		}
	}

	/**
	 * This method generates HashMap of data required to be forwarded if Form is submitted for Print request.
	 * @param abstractForm	Form submitted
	 * @param abstractDomain	DomainObject Added/Edited
	 * @return	HashMap of data required to be forwarded
	 * @throws ApplicationException Application Exception
	 */
	protected HashMap generateForwardToPrintMap(AbstractActionForm abstractForm,
			AbstractDomainObject abstractDomain) throws ApplicationException
	{
		try
		{
			IForwordToFactory factory = AbstractFactoryConfig.getInstance().getForwToFactory(
					"forwardToFactory");
			AbstractForwardToProcessor forwardToProcessor = factory.getForwardToPrintProcessor();
			HashMap forwardToPrintMap = (HashMap) forwardToProcessor.populateForwardToData(
					abstractForm, abstractDomain);
			return forwardToPrintMap;
		}
		catch (ParseException parseException)
		{
			logger.error("Failed to generateforward print map", parseException);
			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"), parseException,
					"Failed at generateForwardToPrintMap in base Add/Edit.");

		}
	}

	/**
	 * This method will add the success message into ActionMessages object.
	 * @param abstractDomain AbstractDomainObject
	 * @param objectName String
	 * @return displayparams.
	 * @throws ApplicationException Application Exception
	 */
	protected String[] addMessage(AbstractDomainObject abstractDomain, String objectName)
			throws ApplicationException
	{
		String message = abstractDomain.getMessageLabel();
		String displayName;
		String[] displayparams;
		IQueryBizLogic queryBizLogic = getQueryBizLogic();
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
	 * get Display Name Of Domain Object.
	 * @param abstractDomain AbstractDomainObject
	 * @param objectName object Name
	 * @param queryBizLogic IQueryBizLogic
	 * @return display Name.
	 */
	private String getDispNameOfDomainObj(AbstractDomainObject abstractDomain, String objectName,
			IQueryBizLogic queryBizLogic)
	{
		String displayName;
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
		return displayName;
	}
}