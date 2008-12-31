
package edu.wustl.common.exception;

import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * Base class for all custom exception classes which defines behavior of
 * exception classes and puts some constraints.
 * @author abhijit_naik
 *
 */
public class ApplicationException extends Exception
{

	/**
	 * Wrapped Exception.
	 */
	private Exception wrapException;
	/**
	 * The unique serial version UID.
	 */
	private static final long serialVersionUID = 6277184346384055451L;

	/**
	 * Logger object used to log messages.
	 */
	private static final org.apache.log4j.Logger logger = Logger.getLogger(ApplicationException.class);

	/**
	 *The errorKey object for the exception occurred.
	 */
	private ErrorKey errorKey;

	/**
	 * Custom message, if any, other than errorKey message.
	 */
	private String errorMsg;

	/**
	 * Parameters for error message.
	 */
	private String msgValues;

	/**
	 * The String of error message values to be send to ApplicationException should
	 * use this constant separator to separate values.
	 */
	public static final String ERR_MSG_VALUES_SEPARATOR = ":";


	/**
	 * The Only public constructor to restrict creating object without
	 * initializing mandatory members.
	 * @param errorKey The object which will represent the root cause of the error.
	 * @param throwable root exception, if any, which caused this error.
	 * @param msgValues custom message, additional information.
	 */
	public ApplicationException(ErrorKey errorKey, Throwable throwable, String msgValues)
	{
		super(throwable);
		this.wrapException = (Exception)throwable;
		if (errorKey == null)
		{
			logger.fatal("While constructing application exception errorKey object must not be null");
			throw new AppRunTimeException();
		}

		this.errorKey = errorKey;
		setErrorMsg(msgValues);
	}

	/**
	 * Protected constructor which only child classes can use to reuse
	 * properties of another exception object.
	 * @param applicationException an exception whose properties will be reused.
	 */
	protected ApplicationException(ApplicationException applicationException)
	{
		this(applicationException.errorKey,applicationException,applicationException.msgValues);
	}
	/**
	 * Formats error message to be send to end user.
	 * @return formatted message.
	 */
	public String getFormattedMessage()
	{
		StringBuffer formattedMsg = new StringBuffer();
		formattedMsg.append(errorKey.getErrorNumber()).append(":-");
		String errMsg = errorKey.getMessageWithValues();
		formattedMsg.append(errMsg);
		return formattedMsg.toString();
	}

	/**
	 * This function formats a complete message with all details about error which caused the
	 * exsception. This function intended to use for logging error message.
	 *
	 * Usage: logger.error(ex.getLogMessage,ex);
	 *
	 * @return formatted detailed error message.
	 */
	public String getLogMessage()
	{
		StringBuffer logMsg = new StringBuffer(getFormattedMessage());
		if (!TextConstants.EMPTY_STRING.equals(getErrorMsg()))
		{
			logMsg.append(" Error caused at- ").append(getErrorMsg());
		}
		if(getMessage()!=null)
		{
			logMsg.append("\n Root cause: ").append(getMessage());
		}
		if(wrapException!=null)
		{
			logMsg.append("; ").append(wrapException.getMessage());
		}
		return logMsg.toString();
	}

	/**
	 * Returns errorKey object. This function accessible only by child classes
	 * @return errorKey object reference.
	 */
	protected ErrorKey getErrorKey()
	{
		return errorKey;
	}

	/**
	 * Sets errorKey object. This function accessible only by child classes
	 * @param errorKey erroKey object to be set.
	 */
	protected void setErrorKey(ErrorKey errorKey)
	{
		this.errorKey = errorKey;
	}

	/**
	 * Returns the custom error message set when the error is occurred.
	 * @return The custom error message.
	 */
	protected String getErrorMsg()
	{
		return this.errorMsg;
	}

	/**
	 * This function checks the custom error parameter. If it has values for
	 * errorKey message text then sets custom error blank and sets those values to
	 * errorKey member object.
	 * @param errorValParam custom error message or values for erroKey text message.
	 */
	protected final void setErrorMsg(String errorValParam)
	{
		this.errorMsg = errorValParam;
		if (errorValParam.contains(ApplicationException.ERR_MSG_VALUES_SEPARATOR))
		{
			this.errorMsg = TextConstants.EMPTY_STRING;
			setMsgValues(errorValParam);
			String[] errorValues = errorValParam.split(ERR_MSG_VALUES_SEPARATOR);
			errorKey.setMessageValues(errorValues);
		}
	}

	/**
	 * Inner runtime exception class used to throw exception if
	 * ApplicationException object not get created properly.
	 */
	private class AppRunTimeException extends RuntimeException
	{

		/**
		 * A unique serial versionUID.
		 */
		private static final long serialVersionUID = 9019490724476120927L;

		/**
		 * default constructor.
		 */
		public AppRunTimeException()
		{
			super("Failed to construct ApplicationException object."
					+ " Please see the comple error log above");
		}
	}


	/**
	 * returns error message parameters.
	 * @return message parameters
	 */
	public String getMsgValues()
	{
		return msgValues;
	}
	/**
	 * @return String[].
	 */
	public String[] toMsgValuesArray()
	{
		String [] valueArr;
		if (msgValues == null)
		{
			valueArr =new String[1];
			valueArr[0]=errorMsg;
		}
		else
		{
			valueArr =msgValues.split(ERR_MSG_VALUES_SEPARATOR);
		}
		return valueArr;
	}

	/**
	 * sets error message parameters.
	 * @param msgValues parameters.
	 */
	public final void setMsgValues(String msgValues)
	{
		this.msgValues = msgValues;
	}

	/**
	 * This function is used to get key value set for the Error Key.
	 * @return Unique key value of ErrorKey.
	 */
	public String getErrorKeyAsString()
	{
		return errorKey.getErrorKey();
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
}