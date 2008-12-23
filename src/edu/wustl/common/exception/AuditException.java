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

	public AuditException(Throwable throwable, String msgValues)
	{
		super(ErrorKey.getErrorKey("error.audit.fail"),throwable,msgValues);
	}
}
