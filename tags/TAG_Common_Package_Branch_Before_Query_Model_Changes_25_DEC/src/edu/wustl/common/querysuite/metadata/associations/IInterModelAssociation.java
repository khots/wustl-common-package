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
	 */
	public AttributeInterface getSourceAttribute();

	public AttributeInterface getTargetAttribute();

	public String getSourceServiceUrl();

	/**
	 * @param url
	 * 
	 */
	public void setSourceServiceUrl(String url);

	public String getTargetServiceUrl();

	/**
	 * @param url
	 * 
	 */
	public void setTargetServiceUrl(String url);

}
