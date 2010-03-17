/**
 *
 */
package edu.wustl.common.processor;

import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.UIRepOfDomain;
import edu.wustl.common.exception.ApplicationException;

/**
 * The Class CommonAddRequestProcessor is the common request processor
 * implementation to insert the object into the database. This class works with
 * <code>PersistObjectInputInterface</code> as an input interface and
 * <code>PersistObjectOutputInterface</code> as an output interface.
 *
 * @author vishvesh_mulay
 */
public class CommonAddRequestProcessor extends CommonAddEditRequestProcessor
{

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.wustl.common.processor.RequestProcessorInterface#process(java.lang
     * .Object, java.lang.Object)
     */
    public void process(final PersistObjectInputInterface inputDataCarrier,
            final PersistObjectOutputInterface outputDataCarrier) throws ApplicationException
            {
        final UIRepOfDomain domainObjectInterface = inputDataCarrier.getDomainObjectInputInterface();
        final AbstractDomainObject domainObject = getDomainObject(domainObjectInterface);
        final IBizLogic bizLogic = getBizLogic(inputDataCarrier);
        bizLogic.insert(domainObject, inputDataCarrier.getSessionData());
        outputDataCarrier.setDomainObject(domainObject);
        outputDataCarrier.setDomainObjectName(getObjectName(inputDataCarrier));
            }

}
