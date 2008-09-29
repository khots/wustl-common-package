
package edu.wustl.common.util.dbManager;

import java.io.PrintStream;
import java.io.PrintWriter;

import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.util.logger.Logger;

/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DAOException extends Exception
{
	/**
	 * serialVersionUID for serialization
	 */
	private static final long serialVersionUID = 8370981231082627671L;

	/**
	 * logger - generic logger
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOException.class);
	
	private Exception wrapException;

	/**
	 *  message initialised with super.getMessage()
	 */
	private String message = super.getMessage();

	/**
	 *  message which is used as supporting message to the main message
	 */
	private String supportingMessage;

	public DAOException(String message)
	{
		this(message, null);
	}

	public DAOException(Exception exception)
	{
		this("", exception);
	}

	public DAOException(SMException exception)
	{
		this("", exception);
		logger.error("Exception in Authorization: " + exception.getMessage(), exception);
		String message = "Security Exception: " + exception.getMessage();
		if (exception.getCause() != null)
		{
			message = message + " : " + exception.getCause().getMessage();
		this.setMessage(message);
		}
	}

	/**
	 * @param wrapException The wrapException to set.
	 */
	public DAOException(String message, Exception wrapException)
	{
		super(message);
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
	/**
	 * Return the Stack containing details of the exception.
	 */	
	public void printStackTrace()
	{
		super.printStackTrace();
		if (wrapException != null)
		{	
			wrapException.printStackTrace();
		}	
	}
	/**
	 * Return the Stack containing details of the exception.
	 */	
	public void printStackTrace(PrintWriter thePrintWriter)
	{
		super.printStackTrace(thePrintWriter);
		if (wrapException != null)
		{	
			wrapException.printStackTrace(thePrintWriter);
		}	
	}
	/**
	 * Return the Stack containing details of the exception.
	 */	
	public void printStackTrace(PrintStream thePrintStream)
	{
		super.printStackTrace(thePrintStream);
		if (wrapException != null)
		{	
			wrapException.printStackTrace(thePrintStream);
		}	
	}
	/**
	 * @return the message.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param The message to set.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return the supporting message.
	 */
	public String getSupportingMessage()
	{
		return supportingMessage;
	}

	/**
	 * @param supporting message The supporting message to set.
	 */
	public void setSupportingMessage(String supportingMessage)
	{
		this.supportingMessage = supportingMessage;
	}
}