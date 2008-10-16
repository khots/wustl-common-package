/**
 * <p>Title: Validator Class>
 * <p>Description:  This Class contains the methods used for validation of the fields in the userform.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 1, 2005
 */

package edu.wustl.common.util.global;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;

/**
 *  This Class contains the methods used for validation of the fields in the userform.
 *  @author gautam_shetty
 */

public class Validator
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(Validator.class);

	/**
	 * This is regular expression to check for XXS vulnerable characters e.g <, >, (, ) etc.
	 * */
	private static final String REGEX_XSS_VULNERABLE = "[()<>]";
	/**
	 * This is regular expression to validate email id.
	 */
	private static final String REGEX_VALID_EMAIL
		= "^\\w(\\.?[\\w-])*@\\w(\\.?[-\\w])*\\.([a-z]{3,}(\\.[a-z]{2,})?|[a-z]{2,}(\\.[a-z]{2,})?)$";
	/**
	 * This is regular expression to validate phone number.
	 */
	private static final String REGEX_VALID_PHONE = "\\(\\d{3}\\)\\d{3}-\\d{4}|\\d{3}\\-\\d{3}-\\d{4}";
	/**
	 * Checks that the input String is a valid email address.
	 * @param aEmailAddress String containing the email address to be checked.
	 * @return Returns true if its a valid email address, else returns false.
	 * */
	public boolean isValidEmailAddress(String aEmailAddress)
	{
		boolean result = true;
		try
		{
			if (isEmpty(aEmailAddress))
			{
				result = false;
			}
			else
			{
				result = isValidEmailId(aEmailAddress);
			}
		}
		catch (Exception ex)
		{
			result = false;
		}
		return result;
	}

	/**
	 * 
	 * @param ssn Social Security Number to check
	 * @return boolean depending on the value of ssn.
	 */
	public boolean isValidSSN(String ssn)
	{
		boolean result = true;
		try
		{
			Pattern pattern = Pattern.compile("[0-9]{3}-[0-9]{2}-[0-9]{4}", Pattern.CASE_INSENSITIVE);
			Matcher mat = pattern.matcher(ssn);
			result = mat.matches();
		}
		catch (Exception exp)
		{
			result=false;
		}
		return result;
	}

	/**
	 * Checks whether a string is empty and adds an ActionError object in the ActionErrors object.
	 * @param str string to be checked.
	 * @return Returns true if the componentName is empty else returns false.
	 */
	public boolean isEmpty(String str)
	{
		boolean isEmpty=false;
		if (str == null || TextConstants.EMPTY_STRING.equals(str.trim()))
		{
			isEmpty=true;
		}
		return isEmpty;
	}

	/**
	 * Checks that the input String contains only alphabetic characters.
	 * @param alphaString The string whose characters are to be checked.
	 * @return false if the String contains any digit else returns true. 
	 * */
	public boolean isAlpha(String alphaString)
	{
		boolean isAlpha=true;
		int index = 0;
		while (index < alphaString.length())
		{
			if (!Character.isLetter(alphaString.charAt(index)))
			{
				isAlpha= false;
				break;
			}
			index++;
		}
		return isAlpha;
	}

	/**
	 * Checks that the input String contains only numeric digits.
	 * @param numString The string whose characters are to be checked.
	 * @return false if the String contains any alphabet else returns true.
	 * */
	public boolean isNumeric(String numString)
	{
		boolean isNumeric=true;
		try
		{
			long longValue = Long.parseLong(numString);
			if (longValue <= 0)
			{
				isNumeric=false;
			}
		}
		catch (NumberFormatException exp)
		{
			isNumeric=false;
		}
		return isNumeric;
	}

	/**
	 *
	 * Checks that the input String contains only numeric digits.
	 * @param numString The string whose characters are to be checked.
	 * @param positiveCheck Positive integer to check for positive number
	 * @return Returns false if the String contains any alphabet else returns true.
	 * Depending on the value of the positiveCheck will check for positive values
	 *
	 */
	public boolean isNumeric(String numString, int positiveCheck)
	{
		return isPositiveNumeric(numString, positiveCheck);
	}

	/**
	 * 
	 * Checks that the input String contains only positive numeric digits.
	 * @param numString The string whose characters are to be checked.
	 * @param positiveCheck Positive integer to check for positive number
	 * @return Returns false if the String contains any alphabet or negative integer else returns true.
	 * @author aarti_sharma
	 * Depending on the value of the positiveCheck will check for positive values
	 *
	 */
	public boolean isPositiveNumeric(String numString, int positiveCheck)
	{
		boolean isPosNum=true;
		Double value = convertToNumber(numString);
		if (value == null)
		{
			isPosNum=false;
		}
		else
		{
			if (positiveCheck > 0 && value.longValue() <= 0)
			{
				isPosNum=false;
			}
			else if (positiveCheck == 0 && value.longValue() < 0)
			{
				isPosNum=false;
			}
		}
		return isPosNum;
	}

	/**
	 *
	 * Return Long representation of numString if its convertible else returns null.
	 * @param numString The string whose characters are to be checked.
	 * @author aarti_sharma
	 * @return Long representation of numString if its convertible else returns null
	 *
	 */
	public Long convertToLong(String numString)
	{
		Long longValue = null;
		try
		{
			longValue = Long.valueOf(numString);

		}
		catch (NumberFormatException exp)
		{
			logger.debug("NumberFormatException:" + exp.getMessage(),exp);
		}
		return longValue;
	}
	/**
	 * This converts string into a number.
	 * @param numString Number as a string.
	 * @return Double number.
	 */
	public Double convertToNumber(String numString)
	{
		Double value = null;
		try
		{
			value = new Double(numString);
		}
		catch (NumberFormatException exp)
		{
			logger.error("NumberFormatException:" + exp.getMessage(),exp);
		}
		return value;
	}

	/**
	 * Checks the given String for double value.
	 *
	 * @param dblString
	 * @return boolean True if the string contains double number or
	 *  false if any non numeric character is present.
	 */
	public boolean isDouble(String dblString)
	{
		boolean isDouble=true;
		try
		{
			double dblValue = Double.parseDouble(dblString);
			if (dblValue < 0 || Double.isNaN(dblValue))
			{
				isDouble=false;
			}
		}
		catch (NumberFormatException exp)
		{
			isDouble=false;
		}
		return isDouble;
	}

	/**
	 * @param dblString The String value to be verified for Double
	 * @param positiveCheck Boolean value indicating whether to check for positive values only or not.
	 * @return Boolean indicating the given string value is double or not.
	 */
	public boolean isDouble(String dblString, boolean positiveCheck)
	{
		boolean isDouble=true;
		try
		{
			double dblValue = Double.parseDouble(dblString);

			if (Double.isNaN(dblValue))
			{
				isDouble=false;
			}
			if (positiveCheck && dblValue < 0)
			{
				isDouble=false;
			}
		}
		catch (NumberFormatException exp)
		{
			logger.debug("NumberFormatException:" + exp.getMessage(),exp);
			isDouble= false;
		}
		return isDouble;
	}

	public boolean isValidOption(String option)
	{
		boolean isValidOption=false;
		if (option != null && !(option.trim().equals("") || option.trim().equals("-1")
					|| option.equals(Constants.SELECT_OPTION)))
		{
			isValidOption=true;
		}
		return isValidOption;
	}

	private boolean isValidEmailId(String emailAddress)
	{
		boolean result = true;
		try
		{
			Pattern pattern = Pattern
					.compile(REGEX_VALID_EMAIL,Pattern.CASE_INSENSITIVE);
			Matcher mat = pattern.matcher(emailAddress);
			result = mat.matches();
		}
		catch (Exception exp)
		{
			result= false;
		}
		return result;
	}

	//    public boolean containsSpecialCharacters( String mainString, String delimiter)
	//    {
	//    	try
	//		{
	//    		StringTokenizer st = new StringTokenizer(mainString, delimiter);
	//    		int count = st.countTokens();
	//    		if(count>1)
	//    			return true;
	//    		else
	//    			return false;
	//		}
	//    	catch(Exception exp)
	//		{
	//			//System.out.println("error : " + exp);
	//    		return true;
	//		}
	//
	//    }

	public String delimiterExcludingGiven(String ignoreList)
	{
		String spChars = "!@#$%^&*()=+\\|{[ ]}\'\";:/?.>,<`~ -_";
		StringBuffer specialChars = new StringBuffer(spChars);
		StringBuffer retStr = new StringBuffer();

		try
		{
			char []spIgnoreChars = ignoreList.toCharArray();
			for (int spCharCount = 0; spCharCount < spIgnoreChars.length; spCharCount++)
			{
				char chkChar = spIgnoreChars[spCharCount];
				int chInd = specialChars.indexOf("" + chkChar);
				while (chInd != -1)
				{
					specialChars = specialChars.deleteCharAt(chInd);
					chInd = specialChars.indexOf("" + chkChar);
				}
			}
			retStr = specialChars;
		}
		catch (Exception exp)
		{
			logger.debug("", exp);
		}
		return retStr.toString();
	}

	private boolean isValidDatePattern(String checkDate)
	{
		boolean result = true;
		try
		{
			Pattern pattern = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}", Pattern.CASE_INSENSITIVE);
			Matcher mat = pattern.matcher(checkDate);
			result = mat.matches();
			dtCh = Constants.DATE_SEPARATOR;
			// check for  / separator
			if (!result)
			{
				pattern = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}", Pattern.CASE_INSENSITIVE);
				mat = pattern.matcher(checkDate);
				result = mat.matches();
				dtCh = Constants.DATE_SEPARATOR_SLASH;
			}
		}
		catch (Exception exp)
		{
			logger.error("IsValidDatePattern : exp : " + exp);
			return false;
		}
		return result;
	}

	String dtCh = Constants.DATE_SEPARATOR;
	int minYear = Integer.parseInt(Constants.MIN_YEAR);
	int maxYear = Integer.parseInt(Constants.MAX_YEAR);

	private int daysInFebruary(int year)
	{
		// February has 29 days in any year evenly divisible by four,
		// EXCEPT for centurial years which are not also divisible by 400.
		return (((year % 4 == 0) && ((!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28);
	}

	private int[] daysArray(int monthNum)
	{
		int []dayArray = new int[monthNum + 1];
		dayArray[0] = 0;
		for (int i = 1; i <= monthNum; i++)
		{
			dayArray[i] = 31;
			if (i == 4 || i == 6 || i == 9 || i == 11)
			{
				dayArray[i] = 30;
			}
			if (i == 2)
			{
				dayArray[i] = 29;
			}
		}
		return dayArray;
	}

	private boolean isDate(String dtStr)
	{
		try
		{
			logger.debug("In isDate : dtCh : " + dtCh + " | dtStr : " + dtStr);
			int[] daysInMonth = daysArray(12);
			int pos1 = dtStr.indexOf(dtCh);
			int pos2 = dtStr.indexOf(dtCh, pos1 + 1);
			String strMonth = dtStr.substring(0, pos1);
			String strDay = dtStr.substring(pos1 + 1, pos2);
			String strYear = dtStr.substring(pos2 + 1);
			String strYr = strYear;

			if (strDay.charAt(0) == '0' && strDay.length() > 1)
			{
				strDay = strDay.substring(1);
			}

			if (strMonth.charAt(0) == '0' && strMonth.length() > 1)
			{
				strMonth = strMonth.substring(1);
			}
			for (int i = 1; i <= 3; i++)
			{
				if (strYr.charAt(0) == '0' && strYr.length() > 1)
				{
					strYr = strYr.substring(1);
				}
			}
			int month = Integer.parseInt(strMonth);
			int day = Integer.parseInt(strDay);
			int year = Integer.parseInt(strYr);

			if (pos1 == -1 || pos2 == -1)
			{
				logger.debug("The date format should be : mm/dd/yyyy");
				return false;
			}
			if (strMonth.length() < 1 || month < 1 || month > 12)
			{
				logger.debug("Please enter a valid month");
				return false;
			}
			if (strDay.length() < 1 || day < 1 || day > 31
					|| (month == 2 && day > daysInFebruary(year)) || day > daysInMonth[month])
			{
				logger.debug("Please enter a valid day");
				return false;
			}
			if (strYear.length() != 4 || year == 0 || year < minYear || year > maxYear)
			{
				logger.debug("Please enter a valid 4 digit year between " + minYear + " and "
						+ maxYear);
				return false;
			}
			return true;
		}
		catch (Exception exp)
		{
			logger.error("exp in isDate : " + exp);
			exp.printStackTrace();
			return false;
		}

	}

	public boolean checkDate(String checkDate)
	{
		boolean result = true;
		try
		{
			logger.debug("checkDate : " + checkDate);
			if (isEmpty(checkDate))
			{
				result = false;
			}
			else
			{
				if (isValidDatePattern(checkDate))
				{
					result = isDate(checkDate);
				}
				else
				{
					result = false;
				}
			}
		}
		catch (Exception exp)
		{
			logger.error("Check Date : exp : " + exp);
			result = false;
		}
		logger.debug("CheckDate : " + result);
		return result;
	}

	public boolean compareDateWithCurrent(String dateToCheck)
	{
		boolean result = true;
		try
		{
			Date currentDate = Calendar.getInstance().getTime();
			String pattern = "MM" + dtCh + "dd" + dtCh + "yyyy";
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			Date toCheck = dateFormat.parse(dateToCheck);
			int dateCheckResult = currentDate.compareTo(toCheck);
			if (dateCheckResult < 0)
			{
				result = false;
			}
		}
		catch (Exception exp)
		{
			result = false;
		}

		return result;
	}

	public boolean compareDates(String startDate, String endDate)
	{
		boolean result = true;
		try
		{
			isValidDatePattern(startDate);
			String pattern = "MM" + dtCh + "dd" + dtCh + "yyyy";
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			Date toCheck = dateFormat.parse(startDate);
			isValidDatePattern(endDate);
			String pattern1 = "MM" + dtCh + "dd" + dtCh + "yyyy";
			SimpleDateFormat dF1 = new SimpleDateFormat(pattern1);
			Date maxDate = dF1.parse(endDate);
			int dateCheckResult = maxDate.compareTo(toCheck);
			if (dateCheckResult < 0)
			{
				result = false;
			}
		}
		catch (Exception exp)
		{
			result = false;
		}
		return result;

	}

	// --- method to check date and format the errors accordingly.
	// Error messages for empty date, Invalid date, and Future dates are formatted .
	public String validateDate(String strDate, boolean checkFutureDate)
	{
		String returnString = "";
		if (isEmpty(strDate))
		{
			returnString = "errors.item.required";
		}
		else
		{
			if (isValidDatePattern(strDate))
			{
				if (isDate(strDate))
				{
					if (checkFutureDate)
					{
						if (!compareDateWithCurrent(strDate))
						{
							returnString = "errors.invalid.date";
						}
					}
				}
				else
				{
					returnString = "errors.date.format";
				}
			}
			else
			{
				returnString = "errors.date.format";
			}
		}
		logger.debug("validateDate Return : " + returnString);
		return returnString;
	}

	//Returns TRUE if operator is valid else return FALSE
	public boolean isOperator(String value)
	{
		if (value == null)
		{
			return false;
		}
		else if (value.equals(Constants.ANY) || value.equals("-1"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public boolean isValue(String value)
	{
		//Purposefully not checked for value==null.
		if (value == null)
		{
			return true;
		}
		else if (value.trim().length() == 0)
		{
			return false;
		}
		else if (value.equals("-1") || value.equals(Constants.SELECT_OPTION))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	// -------------Date Validation ends---------

	/**
	 * returns true if zip code is valid else returns false.
	 */
	public boolean isValidZipCode(String zipCode)
	{
		boolean result = false;
		try
		{
			// valid format for zip code are '99999-9999' or '99999'
			Pattern pattern = Pattern.compile("\\d{5}\\-\\d{4}|\\d{5}");
			Matcher matcher = pattern.matcher(zipCode);
			result = matcher.matches();
		}
		catch (Exception exp)
		{
			//logger.error("Check Zip Code : exp : "+ exp);
			result = false;
		}
		return result;
	}

	/**
	 * returns true if Phone/Fax number is valid else returns false.
	 */
	public boolean isValidPhoneNumber(String phoneNumber)
	{
		boolean result = false;
		try
		{
			// valid format for phone/fax number ar '(999)999-9999' or '999-999-9999'
			Pattern pattern = Pattern.compile(REGEX_VALID_PHONE); // for fax and phone
			Matcher matcher = pattern.matcher(phoneNumber);
			result = matcher.matches();
		}
		catch (Exception exp)
		{
			//logger.error("Check Phone/Fax Number : exp : "+ exp);
			result = false;
		}
		return result;
	}

	/**
	 * Returns true if the time is in invalid pattern else returns false.
	 * @param time the time to be validated.
	 * @param pattern the pattern in which it is to be validated.
	 * @return true if the time is in invalid pattern else returns false.
	 */
	public boolean isValidTime(String time, String pattern)
	{
		boolean isValid = true;

		try
		{
			Utility.parseDate(time, pattern);
		}
		catch (ParseException parseExp)
		{
			isValid = false;
		}

		return isValid;
	}

	/**
	 * Returns true if enumerated value is valid else false.
	 * @param list List the list of enumerated values.
	 * @param value String the value that is to be checked.
	 * @return true if enumerated value is valid else false.
	 */
	public static boolean isEnumeratedValue(List list, String value)
	{
		boolean isValid = false;

		if (list != null && value != null)
		{
			isValid = isValueInList(list, value);
		}

		return isValid;
	}

	/**
	 * Returns true if enumerated value is valid else false.
	 * @param list List - list of object NameValueBean
	 * @param value String the value that is to be checked.
	 * @return true if enumerated value is valid else false.
	 */
	private static boolean isValueInList(List list, String value)
	{
		boolean isValid = false;
		for (int i = 1; i < list.size(); i++)
		{
			NameValueBean bean = (NameValueBean) list.get(i);

			if (value.equals(bean.getValue()))
			{
				isValid = true;
				break;
			}
		}
		return isValid;
	}

	/**
	 * Returns true if enumerated value is valid else false.
	 * @param list String[] the list of enumerated values.
	 * @param value String the value that is to be checked.
	 * @return true if enumerated value is valid else false.
	 */
	public static boolean isEnumeratedValue(String[] list, String value)
	{
		boolean isValid = false;

		if (list != null && value != null)
		{
			for (int i = 1; i < list.length; i++)
			{
				if (value.equals(list[i]))
				{
					isValid = true;
					break;
				}
			}
		}
		return isValid;
	}

	/**
	 * Returns true if enumerated value is valid or null else false.
	 * @param list List - the list of enumerated values.
	 * @param value String - the value that is to be checked.
	 * @return true if enumerated value is valid or null else false.
	 */
	public static boolean isEnumeratedOrNullValue(List list, String value)
	{
		boolean isValid = false;

		if (value == null)
		{
			return true;
		}
		else if (list != null)
		{
			isValid = isValueInList(list, value);
		}
		return isValid;
	}

	/**
	 * Returns true if enumerated value is valid or null else false.
	 * @param list String[]- the list of enumerated values.
	 * @param value String -the value that is to be checked.
	 * @return true if enumerated value is valid or null else false.
	 */
	public static boolean isEnumeratedOrNullValue(String[] list, String value)
	{
		boolean isValid = false;
		if (value == null)
		{
			isValid=true;
		}
		else if (list != null)
		{
			isValid=isEnumeratedValue(list, value);
		}

		return isValid;
	}

	// Mandar : 22-May-06 : bug 1775 : validation of special characters updated.

	public boolean containsSpecialCharacters(String mainString, String delimiter)
	{
		try
		{
			logger.debug("mainString : " + mainString + " delimiter: " + delimiter);
			char[] specialChars = delimiter.toCharArray();

			for (int spCharCount = 0; spCharCount < specialChars.length; spCharCount++)
			{
				char searchChar = specialChars[spCharCount];
				int pos = mainString.indexOf(searchChar);
				logger.debug("searchChar : " + searchChar + " | pos : " + pos);
				if (pos != -1)
				{
					return true;
				}
			}
		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			return true;
		}

		return false;
	}

	//Method to return difference between to dates in days.
	public long getDateDiff(Date startDate, Date endDate)
	{
		long time1 = startDate.getTime();
		long time2 = endDate.getTime();
		long diff = time2 - time1;

		long days = diff / (1000 * 60 * 60 * 24);
		logger.debug("Difference in dates = " + days);

		return days;
	}

	/**
	 * This method check for xxs vulnerable characters like <, >, (, ) etc.
	 * @param value String for which xss vulnerable character [ (,),< or > ] to be checked.
	 * @return true if the string contains any xss vulnerable character else false.
	 */
	public static boolean isXssVulnerable(String value)
	{
		boolean isXssVulnerable = false;
		if (value != null)
		{
			Pattern pattern = Pattern.compile(REGEX_XSS_VULNERABLE);
			Matcher matcher = pattern.matcher(value);
			isXssVulnerable = matcher.find();
		}
		return isXssVulnerable;
	}
}