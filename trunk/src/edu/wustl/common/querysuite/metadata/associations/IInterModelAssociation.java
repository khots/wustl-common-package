/**
 * 
 */

package edu.wustl.common.querysuite.metadata.associations;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.metadata.associations.impl.InterModelAssociation;

/**
 * @author prafull_kadam
 * Interface to represent Inter model Association.
 */
public interface IInterModelAssociation extends IAssociation
{

	/**
	 * @return the attribute from source entity.
	 */
	AttributeInterface getSourceAttribute();

	/**
	 * 
	 * @return the attribute from target entity.
	 */
	AttributeInterface getTargetAttribute();

	/**
	 * @return the source service url.
	 */
	String getSourceServiceUrl();

	/**
	 * @param url the source url.
	 * 
	 */
	void setSourceServiceUrl(String url);

	/**
	 * @return the target service url
	 */
	String getTargetServiceUrl();

	/**
	 * @param url the target url
	 * 
	 */
	void setTargetServiceUrl(String url);

	/**
	 * @return association with swapped source and target attributes, urls.
	 */
	public InterModelAssociation reverse();
}
