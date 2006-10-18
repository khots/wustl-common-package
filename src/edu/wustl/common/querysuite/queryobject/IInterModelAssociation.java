
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

/**
 * url note:
 * 
 * conceptually a Map with key=leftServiceUrl, and value = list<rightServiceUrl>
 * 
 * TODO : Foreign associations are probably always bidirectional...
 * @version 1.0
 * @updated 11-Oct-2006 02:56:58 PM
 */
public interface IInterModelAssociation extends IAssociation
{

	/**
	 * removes the given right service url for given left service url
	 */
	public IAttribute getLeftAttribute();

	public IAttribute getRightAttribute();

	public List<String> getLeftServiceUrls();

	/**
	 * returns list of urls for the class on right side of association, for the given
	 * left side url
	 * @param leftServiceUrl
	 * 
	 */
	public List<String> getRightServiceUrls(String leftServiceUrl);

	/**
	 * boolean return: true if left url existed previously, false otherwise
	 * 
	 * the return type may not be necessary, and can be removed.
	 * @param leftServiceUrl
	 * 
	 */
	public boolean removeLeftServiceUrl(String leftServiceUrl);

	/**
	 * @param leftServiceUrl
	 * @param rightServiceUrl
	 * 
	 */
	public boolean removeRightServiceUrl(String leftServiceUrl, String rightServiceUrl);

	/**
	 * usage example:
	 * in ui, all possible left and right service urls will be shown. user will
	 * finally chose some of these. so we could clone the original association object,
	 * and used the new clone for the user operations.
	 * clone will also help to isolate changes in
	 * the UI object,
	 * object gotten from metadata, and
	 * object given as input  to querybuilding code
	 */
	public IInterModelAssociation clone();

	/**
	 * @param attribute
	 * 
	 */
	public void setLeftAttribute(IAttribute attribute);

	/**
	 * @param attribute
	 * 
	 */
	public void setRightAttribute(IAttribute attribute);

	/**
	 * @param url
	 * 
	 */
	public void addLeftServiceUrl(String url);

	/**
	 * @param leftServiceUrl
	 * @param rightServiceUrl
	 * 
	 */
	public void addRightServiceUrl(String leftServiceUrl, String rightServiceUrl);

}
