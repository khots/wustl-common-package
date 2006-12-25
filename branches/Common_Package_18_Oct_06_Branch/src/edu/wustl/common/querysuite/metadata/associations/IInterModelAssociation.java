/**
 * 
 */

package edu.wustl.common.querysuite.metadata.associations;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;

/**
 * @author prafull_kadam
 * Interface to represent Inter model Association.
 */
public interface IInterModelAssociation extends IAssociation
{

	/**
	 * removes the given right service url for given left service url
	 * @return reference to source AttributeInterface.
	 */
	AttributeInterface getSourceAttribute();

	/**
	 * 
	 * @return
	 */
	AttributeInterface getTargetAttribute();

	String getSourceServiceUrl();

	/**
	 * @param url
	 * 
	 */
	void setSourceServiceUrl(String url);

	String getTargetServiceUrl();

	/**
	 * @param url
	 * 
	 */
	void setTargetServiceUrl(String url);

}
