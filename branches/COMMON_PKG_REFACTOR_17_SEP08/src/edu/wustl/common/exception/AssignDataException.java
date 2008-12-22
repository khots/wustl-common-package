/*
 * Created on Aug 10, 2005
 */

package edu.wustl.common.exception;

/**
 * @author kapil_kaveeshwar
 */
public class AssignDataException extends Exception
{

	/**
	 * Serial Version Unique Identifier.
	 */
	private static final long serialVersionUID = -3191673172998892548L;

	/**
	 * Constructor.
	 */
	public AssignDataException()
	{

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
		super(message, wrapException);
	}

	/**
	 *
	 * @param args arguments
	 */
	public static void main(String[] args)
	{

	}
}
