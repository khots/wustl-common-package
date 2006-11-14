
package edu.wustl.common.querysuite.exceptions;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 17-Oct-2006 16.27.04 PM
 */

public class CyclicException extends Exception
{

	/**
	 * @param message
	 */
	public CyclicException(String message)
	{
		super(message);
	}
}
