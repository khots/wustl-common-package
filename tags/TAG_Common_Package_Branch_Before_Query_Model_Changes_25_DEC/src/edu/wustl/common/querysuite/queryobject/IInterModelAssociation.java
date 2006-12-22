
package edu.wustl.common.querysuite.queryobject;

import java.util.Set;

/**
 * Wrapper around a {@link java.util.LinkedHashMap} with key=leftServiceUrl, and
 * value = {@link java.util.LinkedHashSet}<rightServiceUrl>. These associations are
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

	/**
	 * Use the set for read-only purposes. For adding/removing use the other methods (removeSourceServiceUrl, addSourceServiceUrl).
	 * @return a set view of the source service urls.
	 */
	public Set<String> getSourceServiceUrls();

	/**
	 * returns list of urls for the class on target side of association, for the
	 * given source side url
	 * @param leftServiceUrl
	 */
	public Set<String> getTargetServiceUrls(String sourceServiceUrl);

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

	//
	//	/**
	//	 * Usage example: On ui, all possible source and target service urls will be
	//	 * shown. The user will finally chose some of these. So we could clone the
	//	 * original association object, and use the new clone for the user
	//	 * operations.
	//	 */
	//	public IInterModelAssociation cloneToInterModelAssociation();

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
	 * @param targetServiceUrl
	 */
	public void addTargetServiceUrl(String sourceServiceUrl, String targetServiceUrl);

}
