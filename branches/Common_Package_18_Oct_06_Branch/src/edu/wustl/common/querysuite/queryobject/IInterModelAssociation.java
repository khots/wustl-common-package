
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

/**
 * urls are conceptually a {@link java.util.Map} with key=leftServiceUrl, and
 * value = {@link java.util.List}<rightServiceUrl>. These associations are
 * always bidirectional.
 * @version 1.0
 * @updated 11-Oct-2006 02:56:58 PM
 */
public interface IInterModelAssociation extends IAssociation
{

	/**
	 * removes the given right service url for given left service url
	 */
	public IAttribute getSourceAttribute();

	public IAttribute getTargetAttribute();

	public List<String> getSourceServiceUrls();

	/**
	 * returns list of urls for the class on target side of association, for the
	 * given source side url
	 * @param leftServiceUrl
	 */
	public List<String> getTargetServiceUrls(String sourceServiceUrl);

	/**
	 * boolean return: true if source url existed previously, false otherwise
	 * @param leftServiceUrl
	 */
	public boolean removeSourceServiceUrl(String sourceServiceUrl);

	/**
	 * @param leftServiceUrl
	 * @param rightServiceUrl
	 */
	public boolean removeTargetServiceUrl(String sourceServiceUrl, String targetServiceUrl);

	/**
	 * Usage example: On ui, all possible source and target service urls will be
	 * shown. The user will finally chose some of these. So we could clone the
	 * original association object, and use the new clone for the user
	 * operations.
	 */
	public IInterModelAssociation clone();

	/**
	 * @param attribute
	 */
	public void setSourceAttribute(IAttribute attribute);

	/**
	 * @param attribute
	 */
	public void setTargetAttribute(IAttribute attribute);

	/**
	 * @param url
	 */
	public void addSourceServiceUrl(String url);

	/**
	 * @param leftServiceUrl
	 * @param rightServiceUrl
	 */
	public void addTargetServiceUrl(String sourceServiceUrl, String rightServiceUrl);

}
