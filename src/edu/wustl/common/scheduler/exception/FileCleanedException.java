/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.exception;

public class FileCleanedException extends Exception

{
	private static final long serialVersionUID = 4645473497741710501L;

	public FileCleanedException(String message)
	{
		super(message);
	}
}
