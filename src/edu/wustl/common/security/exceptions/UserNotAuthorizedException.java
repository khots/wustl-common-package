/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.security.exceptions;




/**
 * 
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */
public class UserNotAuthorizedException extends Exception
{
	
	public UserNotAuthorizedException() {
		super();
	}
	/**
	 * @param message
	 */
	public UserNotAuthorizedException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public UserNotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public UserNotAuthorizedException(Throwable cause) {
		super(cause);
	}
}
