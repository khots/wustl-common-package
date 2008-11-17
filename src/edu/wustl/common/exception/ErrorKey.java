package edu.wustl.common.exception;

import java.util.HashMap;


public final class ErrorKey
{

	private String errorNumber;
	private String errorKey;
	private String errorMessage;
	private Object []messageValues;
	private static ErrorKey DefaultErrorkey;
	
	private static HashMap<String, ErrorKey> errorKeyMap = new HashMap<String, ErrorKey>();
	
	private ErrorKey(String errNo, String key, String msg)
	{
		this.errorNumber = errNo;
		this.errorKey = key;
		this.errorMessage = msg;
	}
	
	public void setMessageValues(Object [] values)
	{
		messageValues = values;
	}
	
	public static ErrorKey getErrorKey(String errorName)
	{
		return errorKeyMap.get(errorName);
	}

	private static void init()
	{

	}
	/**
	 * Default error key
	 * @return
	 */
	public static ErrorKey getDefaultErrorKey()
	{
		return DefaultErrorkey;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
