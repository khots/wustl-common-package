/**
 * <p>Title: AbstractForwardToFactory Class>
 * <p>Description:	Abstract factory to get ForwardTo processor</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Krunal Thakkar
 * @version 1.00
 * Created on May 24, 2006
 */

package edu.wustl.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.AbstractForwardToProcessor;
import edu.wustl.common.util.logger.Logger;

/**
 * Abstract factory to get ForwardTo processor.
 * @author Krunal Thakkar
 */
public class AbstractForwardToFactory
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractForwardToFactory.class);
	
	/**
	 * Returns DAO instance according to the form bean type.
	 * @param FORM_TYPE The form bean type.
	 * @return An AbstractDAO object.
	 */
	public static final AbstractForwardToProcessor getForwardToProcessor(
			String forwardToFactoryName, String methodName) throws BizLogicException
	{
		AbstractForwardToProcessor forwardToProcessor;// = new AbstractForwardToProcessor();

		try
		{
			Class forwardToClass = Class.forName(forwardToFactoryName);
			Class[] parameterTypes = new Class[]{};
			Method getForwardToMethod = forwardToClass.getMethod(methodName, parameterTypes);
			Object[] parameterValues = new Object[]{};
			forwardToProcessor = (AbstractForwardToProcessor) getForwardToMethod.invoke(null,
					parameterValues);
		}
		catch (ClassNotFoundException classNotFndExp)
		{
			logger.debug("AbstractForwardToFactory : ForwardToFactory with class name "
					+ forwardToFactoryName + " not present");
			logger.debug(classNotFndExp.getMessage(), classNotFndExp);
			throw new BizLogicException(
					"Server Error #1: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
		}
		catch (NoSuchMethodException noMethodExp)
		{
			logger.debug("AbstractForwardToFactory : No such method " + methodName
					+ " in ForwardToFactory class " + forwardToFactoryName);
			logger.debug(noMethodExp.getMessage(), noMethodExp);
			throw new BizLogicException(
					"Server Error #2: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
		}
		catch (InvocationTargetException invTrgtExp)
		{
			logger.debug("AbstractForwardToFactory : No such method " + methodName
					+ " in ForwardToFactory class " + forwardToFactoryName);
			logger.debug(invTrgtExp.getMessage(), invTrgtExp);
			throw new BizLogicException(
					"Server Error #3: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
		}
		catch (IllegalAccessException illAccEcp)
		{
			logger.debug("AbstractForwardToFactory : No access to method " + methodName
					+ " in ForwardToFactory class " + forwardToFactoryName);
			logger.debug(illAccEcp.getMessage(), illAccEcp);
			throw new BizLogicException(
					"Server Error #4: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
		}

		return forwardToProcessor;
	}
}
