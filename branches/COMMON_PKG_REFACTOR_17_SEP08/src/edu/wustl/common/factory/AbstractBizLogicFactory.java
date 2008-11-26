/**
 * <p>Title: AbstractBizLogicFactory Class>
 * <p>Description:	Abstract bizlogic factory.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * Abstract bizlogic factory.
 * @author gautam_shetty
 */
public class AbstractBizLogicFactory
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractBizLogicFactory.class);

	/**
	 * This method gets IBizLogic object.
	 * @param bizLogicFactoryName bizLogic Factory Name.
	 * @param methodName method Name
	 * @param formId form Id
	 * @return bizLogic.
	 * @throws BizLogicException BizLogic Exception
	 */
	public static final IBizLogic getBizLogic(String bizLogicFactoryName, String methodName,
			int formId) throws BizLogicException
	{
		try
		{
			//Invokes the singleton method.
			Class bizLogicFactoryClass = getBizLogicFactoryClass(bizLogicFactoryName);
			Method getInstanceMethod = getInstanceMethod(bizLogicFactoryClass, (Class[]) null,
					bizLogicFactoryName, "getInstance");
			Object bizLogicFactory = getInstanceMethod.invoke((Object) null, (Object) null);

			//Invokes getBizLogic method using reflection API.
			Class[] parameterTypes = new Class[]{int.class};
			Object[] parameterValues = new Object[]{Integer.valueOf(formId)};

			Method getBizLogicMethod = getInstanceMethod(bizLogicFactoryClass, parameterTypes,
					bizLogicFactoryName, methodName);
			return (IBizLogic) getBizLogicMethod.invoke(bizLogicFactory, parameterValues);
		}
		catch (InvocationTargetException invTrgtExp)
		{
			logger.debug("AbstractBizLogicFactory : No such method " + methodName
					+ " in bizLogic class " + bizLogicFactoryName);
			throw new BizLogicException("Server Error #3: " + TextConstants.ERROR_MESSAGE,
					invTrgtExp);
		}
		catch (IllegalAccessException illAccEcp)
		{
			logger.debug("AbstractBizLogicFactory : No access to method " + methodName
					+ " in bizLogic class " + bizLogicFactoryName);
			throw new BizLogicException("Server Error #4: " + TextConstants.ERROR_MESSAGE,
					illAccEcp);
		}
	}

	/**
	 * @param bizLogicFactoryClass bizLogic Factory Class.
	 * @param parameterTypes parameter Types.
	 * @param methodName method Name.
	 * @param bizLogicFactoryName bizLogic Factory Name.
	 * @return Method.
	 * @throws BizLogicException BizLogic Exception.
	 */
	private static Method getInstanceMethod(Class bizLogicFactoryClass, Class[] parameterTypes,
			String bizLogicFactoryName, String methodName) throws BizLogicException
	{
		try
		{
			return bizLogicFactoryClass.getMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException noMethodExp)
		{
			logger.debug("AbstractBizLogicFactory : No such method " + methodName
					+ " in bizLogic class " + bizLogicFactoryName);
			throw new BizLogicException("Server Error #2: " + TextConstants.ERROR_MESSAGE,
					noMethodExp);
		}
	}

	/**
	 * @param bizLogicFactoryName bizLogic Factory Name.
	 * @return Class.
	 * @throws BizLogicException BizLogic Exception.
	 */
	private static Class getBizLogicFactoryClass(String bizLogicFactoryName)
			throws BizLogicException
	{
		try
		{
			return Class.forName(bizLogicFactoryName);
		}
		catch (ClassNotFoundException classNotFndExp)
		{
			logger.debug("AbstractBizLogicFactory : BizLogic with class name "
					+ bizLogicFactoryName + " not present", classNotFndExp);

			throw new BizLogicException("Server Error #1: " + TextConstants.ERROR_MESSAGE,
					classNotFndExp);
		}
	}
}