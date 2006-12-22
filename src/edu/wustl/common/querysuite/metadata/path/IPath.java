
package edu.wustl.common.querysuite.metadata.path;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;

/**
 * @version 1.0
 * @created 22-Dec-2006 2:49:27 PM
 */
public interface IPath
{

	public EntityInterface getSourceEntity();

	public EntityInterface getTargetEntity();

	/**
	 * srcEntity of 1st assoc = getSourceEntity()
	 * targetEntity of last assoc = getTargetEntity()
	 */
	public List<IAssociation> getIntermediateAssociations();

}
