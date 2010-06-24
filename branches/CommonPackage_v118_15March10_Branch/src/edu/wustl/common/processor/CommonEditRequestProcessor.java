/**
 *
 */
package edu.wustl.common.processor;

import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.UIRepOfDomain;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;

/**
 * The Class CommonEditRequestProcessor is a common request processor
 * implementation to update any object in the database. This class works with
 * <code>PersistObjectInputInterface</code> as an input interface and
 * <code>PersistObjectOutputInterface</code> as an output interface.
 *
 * @author vishvesh_mulay
 */
public class CommonEditRequestProcessor extends CommonAddEditRequestProcessor
{

    /**
     * Gets the domain object with the given identifier from the business logic.
     *
     * @param inputData
     *            the input data including the UI representation object of
     *            domain object and identifier of the object
     * @param objectName
     *            the object name to fetch
     *
     * @return the domain object
     *
     * @throws ApplicationException
     *             the application exception
     */
    private AbstractDomainObject getDomainObject(final PersistObjectInputInterface inputData,
            final String objectName) throws ApplicationException
            {
        final IBizLogic bizLogic = getBizLogic(inputData);
        final AbstractDomainObject abstractDomain = bizLogic.populateDomainObject(objectName, inputData
                .getObjectId(), inputData.getDomainObjectInputInterface());
        if (abstractDomain == null)
        {
            throw new ApplicationException(ErrorKey.getErrorKey("errors.item.unknown"), null, null);
        }
        return abstractDomain;
            }

    /* (non-Javadoc)
     * @see edu.wustl.common.processor.RequestProcessorInterface#process(java.lang.Object, java.lang.Object)
     */
    public void process(final PersistObjectInputInterface inputDataCarrier,
            final PersistObjectOutputInterface outputDataCarrier) throws ApplicationException
            {
        final UIRepOfDomain domainObjectInterface = inputDataCarrier.getDomainObjectInputInterface();
        final String objectName = getObjectName(inputDataCarrier);
        final AbstractDomainObject domainObject = getDomainObject(inputDataCarrier, objectName);
        final IBizLogic bizLogic = getBizLogic(inputDataCarrier);

    	HibernateDAO hibernateDao = null;
		AbstractDomainObject oldObject;
    	try
    	{
    		String appName =((DefaultBizLogic)bizLogic).getAppName();
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDAO();
			hibernateDao.openSession(null);

			oldObject = (AbstractDomainObject) hibernateDao.retrieveById(objectName,
					 inputDataCarrier.getObjectId());

	        bizLogic.update(domainObject, oldObject, inputDataCarrier.getSessionData());
	        outputDataCarrier.setDomainObject(domainObject);
	        outputDataCarrier.setDomainObjectName(getObjectName(inputDataCarrier));
    	}
	    finally
		{
			try
			{
				hibernateDao.closeSession();
			}
			catch (ApplicationException e)
			{
				throw new ApplicationException(e.getErrorKey(), e,e.getMsgValues());
			}
		}

      }

}
