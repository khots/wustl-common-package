/**
 *
 */
package edu.wustl.common.processor;

import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.processor.datacarrier.OutputDataInterface;

/**
 * This interface is used by the request processors which insert/edit the objects in the database.
 * Typically business logic will return the domain object which was processed along with the
 * domain object name for the processed object.
 * Using this information UI code can then render the proper page accordingly with appropriate
 * messages /errors.
 *
 *
 * @author vishvesh_mulay
 */
public interface PersistObjectOutputInterface extends OutputDataInterface
{

    /**
     * This method sets the domain object in the interface provided.
     *
     * @param domainObject the new domain object
     */
    void setDomainObject(AbstractDomainObject domainObject);

    /**
     * This method sets the domain object name back in the provided interface.
     *
     * @param name the new domain object name
     */
    void setDomainObjectName(String name);

}
