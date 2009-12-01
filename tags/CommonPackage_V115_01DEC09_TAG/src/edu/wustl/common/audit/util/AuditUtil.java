package edu.wustl.common.audit.util;


/**
 *
 * @author.
 *
 */
public final class AuditUtil
{
	/**
	 * Constructor.
	 */
	private AuditUtil()
	{
		//Empty constructor.
	}
	/**
	 * This function returns the function name of passed argument.
	 * @param name name of the attribute whose function will returned.
	 * @return function name of the attribute.
	 */
	public static String getGetterFunctionName(String name)
	{
		String functionName = null;
		if (name != null && name.length() > 0)
		{
			String firstAlphabet = name.substring(0, 1);
			String upperCaseFirstAlphabet = firstAlphabet.toUpperCase();
			String remainingString = name.substring(1);
			functionName = "get" + upperCaseFirstAlphabet + remainingString;
		}
		return functionName;
	}

}
