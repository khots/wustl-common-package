/**
 *
 */

package edu.wustl.common.exception;

/**
 * @author prashant_bandal
 *
 */
public class ParseException extends ApplicationException
{

	/**
	 * Specifies serial Version UID.
	 */
	private static final long serialVersionUID = 6683027539524802184L;

	/**
	 * Constructor.
	 * @param errorKey error Key
	 * @param throwable Throwable
	 * @param msgValues mssage Values.
	 */
	public ParseException(ErrorKey errorKey, Throwable throwable, String msgValues)
	{
		super(errorKey, throwable, msgValues);
	}

}
