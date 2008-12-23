/*
 * Created on Aug 10, 2005
 */

package edu.wustl.common.exception;

/**
 * @author kapil_kaveeshwar
 */
public class AssignDataException extends ApplicationException
{

	/**
	 * Serial Version Unique Identifier.
	 */
	private static final long serialVersionUID = -3191673172998892548L;

	/**
	 * The Only public constructor to restrict creating object without
	 * initializing mandatory members.
	 * @param errorKey The object which will represent the root cause of the error.
	 * @param throwable root exception, if any, which caused this error.
	 * @param msgValues custom message, additional information.
	 */
	public AssignDataException(ErrorKey errorKey, Throwable throwable, String msgValues)
	{
		super(errorKey, throwable, msgValues);
	}

	/**
	 * Parameterised constructor.
	 * @param message exeption message.
	 */
	public AssignDataException(String message)
	{
		this(message, null);
	}

	/**
	 * Parameterised constructor.
	 * @param exception Exception
	 */
	public AssignDataException(Exception exception)
	{
		this("", exception);
	}

	/**
	 * Parameterised constructor.
	 * @param message exeption message.
	 * @param wrapException The wrapException to set.
	 */
	public AssignDataException(String message, Exception wrapException)
	{
		/**Added by amit_doshi to fix the bug related to exception chaining
		 * code reviewer :- abhijit_naik
		 */
		super(ErrorKey.getErrorKey("errors.item"), wrapException, message);
	}
}
