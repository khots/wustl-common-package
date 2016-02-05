/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 *
 */

package edu.wustl.common.tokenprocessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;


import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.KeySequenceGeneratorUtil;
import edu.wustl.common.util.logger.Logger;

/**
 * This class will generate unique id generically based on object provided with getId method.
 * @author suhas_khot
 *
 */
public class DefaultUniqueIdGenerator implements ILabelTokens
{

	/**
	 * Logger for TokenFactory
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(DefaultUniqueIdGenerator.class);

	/**
	 * This method returns next available unique id for the object provided.
	 * @param object
	 * @return nextAvailableId
	 * @throws ApplicationException
	 */
	public String getTokenValue(final Object object) throws ApplicationException
	{
		String nextAvailableId = "";
		final String className = getClassNameIfHibernateProxy(object);
		try
		{
			final Class klass = object.getClass();
			final Object idValue = invokeGetterMethod(klass, "Id", object);
			final String uniqueId = KeySequenceGeneratorUtil.getNextUniqeId(idValue.toString(),
					className);
			if (uniqueId != null)
			{
				nextAvailableId = uniqueId.toString();
			}
		}
		catch (Exception exception)
		{
			LOGGER.error(exception.getMessage(), exception);
			final ErrorKey errorKey = ErrorKey.getErrorKey("error.generating.auto.PPI");
			throw new ApplicationException(errorKey, exception,
					exception.getMessage());
		}
		return nextAvailableId;
	}

	/**
	 * This class invokes and return value of getter property on object passed to it.
	 *
	 * @param klass to get property by reflection
	 * @param property method on which getter has to be invoked.
	 * @param invokeOnObject object on which getter method is invoked
	 * @return value of getId method invoked on object
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object invokeGetterMethod(final Class klass, final String property,
			final Object invokeOnObject) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException
	{
		final Method getter = klass.getMethod("get" + property);
		return getter.invoke(invokeOnObject);
	}
	
	public static String getClassNameIfHibernateProxy(Object object)
	{
		String className = object.getClass().getName();
		if(object instanceof HibernateProxy)
		{
			Class klass = Hibernate.getClass(object);
			className = klass.getName();
		}
		return className;
	}
	

}
