/*
 * Created on Aug 10, 2005
 */

package edu.wustl.common.exception;

/**
 * @author kapil_kaveeshwar
 */
public class AuditException extends ApplicationException
{

	/**
	 * Serial Version Unique Identifier.
	 */
	private static final long serialVersionUID = 5720766957546246226L;

	/**
	 * The Only public constructor to restrict creating object without
	 * initializing mandatory members.
	 * @param exception root exception, if any, which caused this error.
	 * @param msgValues custom message, additional information.
	 */
	public AuditException(Exception exception, String msgValues)
	{
		super(ErrorKey.getErrorKey("error.audit.fail"),exception,msgValues);
	}
}
