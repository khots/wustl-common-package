/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.exception;


public class NotAuthorizedToDownloadException extends Exception
{

	private static final long serialVersionUID = -5148397563358757640L;

	public NotAuthorizedToDownloadException(String message)
	{
		super(message);
	}
	

}
