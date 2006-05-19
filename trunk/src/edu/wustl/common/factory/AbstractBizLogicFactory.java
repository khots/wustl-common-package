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

import edu.wustl.common.bizlogic.AbstractBizLogic;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;


/**
 * Abstract bizlogic factory.
 * @author gautam_shetty
 */
public class AbstractBizLogicFactory
{
    
    /**
     * Returns DAO instance according to the form bean type.
     * @param FORM_TYPE The form bean type.
     * @return An AbstractDAO object.
     */
    public static final AbstractBizLogic getBizLogic(String bizLogicFactoryName, String methodName, int formId) throws BizLogicException
    {
        AbstractBizLogic bizLogic = new DefaultBizLogic();
        
        try
        {
            Class bizLogicClass = Class.forName(bizLogicFactoryName);
            Class[] parameterTypes = new Class[]{int.class};
            Method getBizLogicMethod = bizLogicClass.getMethod(methodName, parameterTypes);
            Object[] parameterValues = new Object[]{new Integer(formId)};
            bizLogic = (AbstractBizLogic)getBizLogicMethod.invoke(null,parameterValues);
        }
        catch (ClassNotFoundException classNotFndExp)
        {
            Logger.out.debug("AbstractBizLogicFactory : BizLogic with class name "+bizLogicFactoryName+" not present");
            Logger.out.debug(classNotFndExp.getMessage(), classNotFndExp);
            throw new BizLogicException("Server Error #1: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
        }
        catch (NoSuchMethodException noMethodExp)
        {
            Logger.out.debug("AbstractBizLogicFactory : No such method "+methodName+" in bizLogic class "+bizLogicFactoryName);
            Logger.out.debug(noMethodExp.getMessage(), noMethodExp);
            throw new BizLogicException("Server Error #2: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
        }
        catch (InvocationTargetException invTrgtExp)
        {
            Logger.out.debug("AbstractBizLogicFactory : No such method "+methodName+" in bizLogic class "+bizLogicFactoryName);
            Logger.out.debug(invTrgtExp.getMessage(), invTrgtExp);
            throw new BizLogicException("Server Error #3: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
        }
        catch (IllegalAccessException illAccEcp)
        {
            Logger.out.debug("AbstractBizLogicFactory : No access to method "+methodName+" in bizLogic class "+bizLogicFactoryName);
            Logger.out.debug(illAccEcp.getMessage(), illAccEcp);
            throw new BizLogicException("Server Error #4: Please contact the caTissue Core support at catissue_support@mga.wustl.edu");
        }
        
        return bizLogic;
    }
}
