
package edu.wustl.common.security.exceptions;

/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */
public class SMTransactionException extends SMException
{

	/**
	 * serial version id.
	 */
	private static final long serialVersionUID = 3799704644728617089L;

	/**
	 * No argument constructor.
	 */
	public SMTransactionException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public SMTransactionException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SMTransactionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SMTransactionException(Throwable cause)
	{
		super(cause);
	}
}
