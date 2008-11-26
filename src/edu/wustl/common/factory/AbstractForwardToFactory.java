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
import edu.wustl.common.util.global.TextConstants;
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
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(AbstractForwardToFactory.class);

	/**
	 * This method returns AbstractForwardToProcessor object.
	 * @param forwardToFactoryName forward To Factory Name.
	 * @param methodName method Name
	 * @return AbstractForwardToProcessor
	 * @throws BizLogicException BizLogic Exception
	 */
	public static final AbstractForwardToProcessor getForwardToProcessor(
			String forwardToFactoryName, String methodName) throws BizLogicException
	{
		try
		{
			Class forwardToClass = getforwardToClass(forwardToFactoryName);
			Class[] parameterTypes = new Class[]{};
			Method getForwardToMethod = forwardToClass.getMethod(methodName, parameterTypes);
			Object[] parameterValues = new Object[]{};
			return (AbstractForwardToProcessor) getForwardToMethod.invoke(null, parameterValues);
		}
		catch (NoSuchMethodException noMethodExp)
		{
			logger.debug("AbstractForwardToFactory : No such method " + methodName
					+ " in ForwardToFactory class " + forwardToFactoryName);
			throw new BizLogicException("Server Error #2: " + TextConstants.ERROR_MESSAGE,
					noMethodExp);
		}
		catch (InvocationTargetException invTrgtExp)
		{
			logger.debug("AbstractForwardToFactory : No such method " + methodName
					+ " in ForwardToFactory class " + forwardToFactoryName);
			throw new BizLogicException("Server Error #3: " + TextConstants.ERROR_MESSAGE,
					invTrgtExp);
		}
		catch (IllegalAccessException illAccEcp)
		{
			logger.debug("AbstractForwardToFactory : No access to method " + methodName
					+ " in ForwardToFactory class " + forwardToFactoryName);
			throw new BizLogicException("Server Error #4: " + TextConstants.ERROR_MESSAGE,
					illAccEcp);
		}

	}

	/**
	 * @param forwardToFactoryName forward To Factory Name.
	 * @return Class.
	 * @throws BizLogicException BizLogic Exception.
	 */
	private static Class getforwardToClass(String forwardToFactoryName) throws BizLogicException
	{
		try
		{
			return Class.forName(forwardToFactoryName);
		}
		catch (ClassNotFoundException classNotFndExp)
		{
			logger.debug("AbstractForwardToFactory : ForwardToFactory with class name "
					+ forwardToFactoryName + " not present");
			throw new BizLogicException("Server Error #1: " + TextConstants.ERROR_MESSAGE,
					classNotFndExp);
		}
	}
}
