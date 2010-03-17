package edu.wustl.common.processor;

import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.UIRepOfDomain;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IDomainObjectFactory;
import edu.wustl.common.factory.IFactory;

/**
 * This is a super class for request processors which try to insert/update
 * objects in the database. It holds the common methods required by such
 * processors.
 * @author vishvesh_mulay
 *
 */
public abstract class CommonAddEditRequestProcessor extends
BaseRequestProcessor<PersistObjectInputInterface, PersistObjectOutputInterface>
{

    public CommonAddEditRequestProcessor()
    {
        super();
    }

    /**
     * Gets the biz logic.
     *
     * @param domainObjectInterface the domain object interface
     *
     * @return the biz logic
     * @throws ApplicationException
     */
    protected IBizLogic getBizLogic(final PersistObjectInputInterface inputData) throws ApplicationException
    {
        final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
        return factory.getBizLogic(inputData.getDomainClassIdentifier());
    }

    /**
     * Gets the domain object.
     *
     * @param domainObjectInputInterface the domain object interface
     *
     * @return the domain object
     * @throws ApplicationException
     */
    protected AbstractDomainObject getDomainObject(final UIRepOfDomain domainObjectInputInterface) throws ApplicationException
    {
        final IDomainObjectFactory abstractDomainObjectFactory = getIDomainObjectFactory();
        final AbstractDomainObject abstractDomain = abstractDomainObjectFactory.createDomainObject(domainObjectInputInterface);//getDomainObject(abstractForm.getFormId(),
        return abstractDomain;
    }

    /**
     * @return  the object of IDomainObjectFactory
     * @throws ApplicationException Application Exception.
     */
    protected IDomainObjectFactory getIDomainObjectFactory() throws ApplicationException
    {
        try
        {
            final IDomainObjectFactory factory = AbstractFactoryConfig.getInstance()
            .getDomainObjectFactory();
            return factory;
        }
        catch (final BizLogicException exception)
        {
            //            LOGGER.debug("Failed to get object"+exception.getMessage());
            throw new ApplicationException(exception.getErrorKey(),
                    exception,exception.getMsgValues());
        }
    }

    /**
     * get Object Name.
     * @param abstractForm AbstractActionForm
     * @return object name.
     * @throws ApplicationException ApplicationException
     */
    protected String getObjectName(final PersistObjectInputInterface inputData) throws ApplicationException
    {

        final IDomainObjectFactory iDomainObjectFactory = getIDomainObjectFactory();
        return iDomainObjectFactory.getDomainObjectName(inputData.getDomainClassIdentifier());
    }

}