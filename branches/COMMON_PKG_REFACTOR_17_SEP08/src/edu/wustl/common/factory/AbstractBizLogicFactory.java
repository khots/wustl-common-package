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

import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
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
		IBizLogic bizLogic = new DefaultBizLogic();
		String erroMess = "Please contact the caTissue Core support at catissue_support@mga.wustl.edu";
		try
		{
			//Invokes the singleton method.
			Class bizLogicFactoryClass = Class.forName(bizLogicFactoryName);
			Method getInstanceMethod = bizLogicFactoryClass.getMethod("getInstance", null);
			Object bizLogicFactory = getInstanceMethod.invoke(null, null);

			//Invokes getBizLogic method using reflection API.
			Class[] parameterTypes = new Class[]{int.class};
			Object[] parameterValues = new Object[]{Integer.valueOf(formId)};

			Method getBizLogicMethod = bizLogicFactoryClass.getMethod(methodName, parameterTypes);
			bizLogic = (IBizLogic) getBizLogicMethod.invoke(bizLogicFactory, parameterValues);
		}
		catch (ClassNotFoundException classNotFndExp)
		{
			logger.debug("AbstractBizLogicFactory : BizLogic with class name "
					+ bizLogicFactoryName + " not present", classNotFndExp);

			throw new BizLogicException("Server Error #1: " + erroMess);
		}
		catch (NoSuchMethodException noMethodExp)
		{
			logger.debug("AbstractBizLogicFactory : No such method " + methodName
					+ " in bizLogic class " + bizLogicFactoryName);
			logger.debug(noMethodExp.getMessage(), noMethodExp);
			throw new BizLogicException("Server Error #2: " + erroMess);
		}
		catch (InvocationTargetException invTrgtExp)
		{
			logger.debug("AbstractBizLogicFactory : No such method " + methodName
					+ " in bizLogic class " + bizLogicFactoryName);
			logger.debug(invTrgtExp.getMessage(), invTrgtExp);
			throw new BizLogicException("Server Error #3: " + erroMess);
		}
		catch (IllegalAccessException illAccEcp)
		{
			logger.debug("AbstractBizLogicFactory : No access to method " + methodName
					+ " in bizLogic class " + bizLogicFactoryName);
			logger.debug(illAccEcp.getMessage(), illAccEcp);
			throw new BizLogicException("Server Error #4: " + erroMess);
		}
		return bizLogic;
	}
}