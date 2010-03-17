/**
 *
 */
package edu.wustl.common.processor;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.UIRepOfDomain;
import edu.wustl.common.processor.datacarrier.InputDataInterface;

/**
 * This is an interface between the business logic and the UI in cases where
 * business logic is trying to insert/ update the domain objects in the
 * database. This interface typically contains UI representation of domain
 * object using which business logic can build the domain object and process it
 * accordingly.
 *
 * @author vishvesh_mulay
 *
 */
public interface PersistObjectInputInterface extends InputDataInterface
{

    /**
     * This method is supposed to return the identifier which specifies which
     * object is being processed. Each domain object like User, Department,
     * Institution, Collection Protocol has an id through which they are
     * identified.
     *
     * Please not that this is not the same as getId() method of Domain Object.
     * This method does NOT return the instance identifier like particular id
     * of some User class. This method returns an integer which specifies which domain
     * object (class) is being processed.
     *
     * @return the domain identifier
     */
    int getDomainClassIdentifier();

    /**
     * This method returns the interface using which request processor will
     * create the domain object The interface returned by this method will
     * typically be UI representation of the domain object.
     *
     * @return the domain object input interface
     */
    UIRepOfDomain getDomainObjectInputInterface();

    /**
     * This method returns the domain object instance identifier. This is used
     * particularly in the edit mode where based on this id old (database)
     * version of the same domain object instance is fetched.
     *
     * @return the object id
     */
    long getObjectId();

    /**
     * This method returns the Session data for the logged in user. The
     * information returned by this method is used in the business logic to
     * check for the authorization etc.
     *
     * @return the session data instance of SessionData object
     */
    SessionDataBean getSessionData();

}
