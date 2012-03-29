package edu.wustl.common.transformer;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * 
 * @author vinod_gaikwad
 *
 * @param <T>
 */
public interface Transformer<T> 
{
	 public T transform(AbstractDomainObject abstractdominobject,IValueObject form) throws AssignDataException;
}
