/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.exception;


public class ScheduleValidationException extends Exception	
{
	private static final long serialVersionUID = -4118871816197962195L;
	public ScheduleValidationException(String message)
	{
		super(message);
	}
}
