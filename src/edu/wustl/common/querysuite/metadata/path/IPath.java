
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

	/**
	 * @return the source entity of the path.
	 */
	EntityInterface getSourceEntity();

	/**
	 * @return the target entity of the path.
	 */
	EntityInterface getTargetEntity();

	/**
	 * srcEntity of 1st assoc = getSourceEntity()
	 * targetEntity of last assoc = getTargetEntity()
	 */
	List<IAssociation> getIntermediateAssociations();

	/**
	 * @return true - if all intermediate associations are bidirectional; false otherwise.
	 */
	boolean isBidirectional();

	/**
	 * Call iff isBidirectional() = true.
	 * @return if bidirectional, returns a reverse path; otherwise returns null.
	 */
	IPath reverse();
}
