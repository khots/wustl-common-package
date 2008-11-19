/**
 *
 */
package edu.wustl.common.actionForm;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;


/**
 * @author prashant_bandal
 *
 */
public class TimeStampTimeValidator implements IDBDataType
{

	/* (non-Javadoc)
	 * @see edu.wustl.common.actionForm.ValidatorDataTypeInterface#
	 * validate(java.lang.String, java.lang.String, org.apache.struts.action.ActionErrors)
	 */
	/**
	 * This method validate TimeStampTime values.
	 * @param enteredValue entered Value.
	 * @param errors errors.
	 * @return conditionError boolean value.
	 */
	public boolean validate(String enteredValue, ActionErrors errors)
	{
		Validator validator = new Validator();
		boolean conditionError = false;
		if (!(validator.isValidTime(enteredValue, Constants.TIME_PATTERN_HH_MM_SS)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("simpleQuery.time.format"));
			conditionError = true;
		}
		return conditionError;
	}

}
