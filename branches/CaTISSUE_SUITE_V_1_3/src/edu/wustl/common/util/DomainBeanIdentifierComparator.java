/**
 * 
 */
package edu.wustl.common.util;

import java.util.Comparator;

import edu.wustl.common.domain.IDomainObject;



/**
 * This class compares the two domain objects  by their identifiers
 * @author chetan_patil
 *
 */
public class DomainBeanIdentifierComparator implements Comparator
{
	public int compare(Object object1, Object object2) 
	{
		IDomainObject domainObject1 = (IDomainObject)object1;
		IDomainObject domainObject2 = (IDomainObject)object2;
		
		Long value = domainObject1.getId() - domainObject2.getId();
		return value.intValue();
	}
}
