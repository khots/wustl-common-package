/**
 * 
 */

package edu.wustl.common.querysuite.metadata.associations;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;

/**
 * @author prafull_kadam
 * Interface to represent Intra model Association.
 */
public interface IIntraModelAssociation extends IAssociation
{

	/**
	 * To get the Dynamic Extenstion Association correpsonding to this class
	 * @return the reference to the Dynamic Extension Attribute.
	 */
	public AssociationInterface getDynamicExtensionsAssociation();
}
