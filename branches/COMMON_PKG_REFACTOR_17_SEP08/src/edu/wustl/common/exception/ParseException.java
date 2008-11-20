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
	 * @param errorKey
	 * @param t
	 * @param msgValues
	 */
	public ParseException(ErrorKey errorKey, Throwable t, String msgValues)
	{
		super(errorKey, t, msgValues);
		// TODO Auto-generated constructor stub
	}

}
