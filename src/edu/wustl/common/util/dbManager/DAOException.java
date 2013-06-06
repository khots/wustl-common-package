/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.util.dbManager;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DAOException extends Exception
{

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

	public DAOException(Exception ex)
	{
		this("", ex);
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
	private void setWrapException(Exception wrapException)
	{
		this.wrapException = wrapException;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace()
	{
		super.printStackTrace();
		if (wrapException != null)
			wrapException.printStackTrace();
	}

	public void printStackTrace(PrintWriter thePrintWriter)
	{
		super.printStackTrace(thePrintWriter);
		if (wrapException != null)
			wrapException.printStackTrace(thePrintWriter);
	}

	public void printStackTrace(PrintStream thePrintStream)
	{
		super.printStackTrace(thePrintStream);
		if (wrapException != null)
			wrapException.printStackTrace(thePrintStream);
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return Returns the supportingMessage.
	 */
	public String getSupportingMessage()
	{
		return supportingMessage;
	}

	/**
	 * @param supportingMessage The supportingMessage to set.
	 */
	public void setSupportingMessage(String supportingMessage)
	{
		this.supportingMessage = supportingMessage;
	}
}