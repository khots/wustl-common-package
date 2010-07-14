/**
 *
 */

package edu.wustl.common.tokenprocessor;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.global.Constants;

/**
 * @author suhas_khot
 *
 */
public class TokenManager
{

	/**
	 * Given a object and format returns value.
	 * @param object
	 * @param format
	 * @throws ApplicationException
	 */
	public static String getLabelValue(final Object object, final String format)
			throws ApplicationException
	{
		StringBuffer tokenLabel = new StringBuffer();
		parseFormat(object, format, tokenLabel);
		return tokenLabel.toString();
	}

	/**
	 * Parse format and generate value
	 * @param object
	 * @param format
	 * @param tokenValue
	 * @param delimiter
	 * @param fileName
	 * @throws ApplicationException
	 */
	public static void parseFormat(final Object object, final String format,
			final StringBuffer tokenValue) throws ApplicationException
	{
		final int tokenDelimiterIndex = format.indexOf(Constants.TOKEN_DELIMITER);
		if (tokenDelimiterIndex > 0)
		{
			tokenValue.append(format.substring(0, tokenDelimiterIndex));
			parseFormat(object, format.substring(tokenDelimiterIndex), tokenValue);
		}
		else if (tokenDelimiterIndex == 0)
		{
			String token;
			try
			{
				token = format.substring(tokenDelimiterIndex + 1, format.indexOf(
						Constants.TOKEN_DELIMITER, tokenDelimiterIndex + 1));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				throw new ApplicationException(null, e, e.getMessage(), "Please enclose tokens between '%' delimiter.");
			}
			final String tokenLabel = TokenFactory.getInstance(token).getTokenValue(object);
			tokenValue.append(tokenLabel);
			final int nextDelimiterIndex = token.length() + 2;
			parseFormat(object, format.substring(nextDelimiterIndex), tokenValue);
		}
		else if (tokenDelimiterIndex == -1)
		{
			tokenValue.append(format);
		}
	}

}
