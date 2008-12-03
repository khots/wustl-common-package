package edu.wustl.common.exception;


/**
 * @author kapil_kaveeshwar
 * All Bizlogic methods should wrap other exception and throws only this exception.
 */
public class BizLogicException extends ApplicationException
{

	/**
	 * Serial Version Unique Identifier.
	 */
	private static final long serialVersionUID = -8514900107659307676L;

	/**
	 * The Only public constructor to restrict creating object without
	 * initializing mandatory members.
	 * @param errorKey The object which will represent the root cause of the error.
	 * @param throwable root exception, if any, which caused this error.
	 * @param msgValues custom message, additional information.
	 */
	public BizLogicException(ErrorKey errorKey, Throwable throwable, String msgValues)
	{
		super(errorKey,throwable,msgValues);
	}
}
