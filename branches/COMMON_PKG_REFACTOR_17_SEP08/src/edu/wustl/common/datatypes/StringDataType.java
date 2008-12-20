/**
 *
 */

package edu.wustl.common.datatypes;

import java.io.IOException;
import java.text.ParseException;

import org.apache.struts.action.ActionErrors;

import edu.wustl.common.util.global.TextConstants;

/**
 * @author prashant_bandal
 *
 */
public class StringDataType implements IDBDataType
{

	/* (non-Javadoc)
	 * @see edu.wustl.common.datatypes.IDBDataType
	 * #validate(java.lang.String, org.apache.struts.action.ActionErrors)
	 */
	/**
	 * This method validate entered values.
	 * @param enteredValue entered Value.
	 * @param errors errors.
	 * @return conditionError boolean value.
	 */
	public boolean validate(String enteredValue, ActionErrors errors)
	{
		return false;
	}

	/**
	 * get Object Value.
	 * @param str string value
	 * @return Object.
	 * @throws ParseException Parse Exception
	 * @throws IOException IOException
	 */
	public Object getObjectValue(String str) throws ParseException, IOException
	{
		Object obj = null;
		if (!TextConstants.EMPTY_STRING.equals(str))
		{
			obj = str;
		}
		return obj;
	}

}
