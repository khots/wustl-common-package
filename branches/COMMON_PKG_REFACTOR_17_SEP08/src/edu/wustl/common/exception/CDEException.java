package edu.wustl.common.exception;


public class CDEException extends Exception
{
	private Exception wrapException;

	/**
	 * No argument constructor
	 */
	public CDEException()
	{
		super();
	}

	/**
	 * One argument constructor
	 * @param message Message with exception
	 */
	public CDEException(String message)
	{
		this(message, null);
	}

	/**
	 * One argument constructor
	 * @param exception Exception
	 */
	public CDEException(Exception exception)
	{
		this("", exception);
	}

	/**
	 * @param message Message with exception
	 * @param wrapException The wrapException to set.
	 */
	public CDEException(String message, Exception wrapException)
	{
		super(message, wrapException);
		this.wrapException = wrapException;
	}

	/**
	 * @return Returns the wrapException.
	 */
	public Exception getWrapException()
	{
		return wrapException;
	}

	/**
	 * @param wrapException The wrapException to set.
	 */
	public void setWrapException(Exception wrapException)
	{
		this.wrapException = wrapException;
	}
}
