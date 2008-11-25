/*
 * Created on Aug 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.exception;


/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BizLogicException extends Exception
{

	/**
	 * Serial Version Unique Identifier.
	 */
	private static final long serialVersionUID = -8514900107659307676L;

	/**
	 * Constructor.
	 */
	public BizLogicException()
	{

	}

	/**
	 * Parameterised constructor.
	 * @param message exeption message.
	 */
	public BizLogicException(String message)
	{
		this(message, null);
	}

	/**
	 * Parameterised constructor.
	 * @param exception Exception
	 */
	public BizLogicException(Exception exception)
	{
		this("", exception);
	}

	/**
	 * Parameterised constructor.
	 * @param message exeption message.
	 * @param wrapException The wrapException to set.
	 */
	public BizLogicException(String message, Exception wrapException)
	{
		/**Added by amit_doshi to fix the bug related to exception chaining
		 * code reviewer :- abhijit_naik
		 */
		super(message, wrapException);
	}

}
