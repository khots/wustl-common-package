/**
 * <p>Title: AbstractActionFormFactory Class>
 * <p>Description:	This is an abstract class for the ActionFormFactory class.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on April 04, 2006
 */

package edu.wustl.common.factory;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.ApplicationException;

/**
 * This is an abstract class for the ActionFormFactory class.
 * @author gautam_shetty
 */
public abstract class AbstractActionFormFactory
{

	/**
	 * Returns the form bean corresponding to the domain object passed
	 * and the operation to be performed.
	 * @param domainObject The domain object whose form bean is required.
	 * @param operation The operation to be performed.
	 * @return the form bean corresponding to the domain object passed
	 * and the operation to be performed.
	 * @throws ApplicationException Application Exception.
	 */
	public abstract AbstractActionForm getFormBean(Object domainObject, String operation)
			throws ApplicationException;
}
