/**
 * <p>Title: AbstractActionFormFactory Class>
 * <p>Description:	This is an abstract class for the DomainObjectFactory class.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on April 19, 2006
 */

package edu.wustl.common.factory;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * This is an abstract class for the DomainObjectFactory class.
 * @author gautam_shetty
 */
public abstract class AbstractDomainObjectFactory
{

	/**
	 * This method returns an AbstractDomain object copy of the form bean object.
	 * @param formType Form bean Id.
	 * @param form Form bean object.
	 * @return an AbstractDomain object copy of the form bean object.
	 * @throws AssignDataException Assign Data Exception.
	 */
	public abstract AbstractDomainObject getDomainObject(int formType, AbstractActionForm form)
			throws AssignDataException;

	/**
	 * Returns the fully qualified name of the class according to the form bean type.
	 * @param formType Form bean Id.
	 * @return the fully qualified name of the class according to the form bean type.
	 */
	public abstract String getDomainObjectName(int formType);
}
