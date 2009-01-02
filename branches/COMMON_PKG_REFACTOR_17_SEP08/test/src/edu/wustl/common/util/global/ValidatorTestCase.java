/**
 * 
 */
package edu.wustl.common.util.global;

import edu.wustl.common.CommonBaseTestCase;


/**
 * @author prashant_bandal
 *
 */
public class ValidatorTestCase extends CommonBaseTestCase
{

	public void testIsValidEmailAddress()
	{
		try
		{
		String aEmailAddress = "admin@admin.com";
		Validator validator =new Validator();
		boolean isValid = validator.isValidEmailAddress(aEmailAddress);
		assertEquals(true, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("Invalid Email Address.", true);
		}
	}
	public void testNegativeIsValidEmailAddress()
	{
		try
		{
		String aEmailAddress = "admin*admin.com";
		Validator validator =new Validator();
		boolean isValid = validator.isValidEmailAddress(aEmailAddress);
		assertEquals(false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("Invalid Email Address.", true);
		}
	}
	public void testEmptyIsValidEmailAddress()
	{
		try
		{
		String aEmailAddress = "";
		Validator validator =new Validator();
		boolean isValid = validator.isValidEmailAddress(aEmailAddress);
		assertEquals(false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("Invalid Email Address.", true);
		}
	}

	public void testIsValidSSN()
	{
		try
		{
		String ssn = "000-00-4444";
		Validator validator =new Validator();
		boolean isValid = validator.isValidSSN(ssn);
		assertEquals(true, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("Invalid Email Address.", true);
		}
	}

	public void testIsEmpty()
	{
		try
		{
		String ssn = "string";
		boolean isValid = Validator.isEmpty(ssn);
		assertEquals(false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("Not able to check is empty string.", true);
		}
	}
	public void testIsAlpha()
	{
		try
		{
		String alphaString = "string";
		Validator validator =new Validator();
		boolean isValid = validator.isAlpha(alphaString);
		assertEquals(true, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
	public void testNegativeIsAlpha()
	{
		try
		{
		String alphaString = "#string";
		Validator validator =new Validator();
		boolean isValid = validator.isAlpha(alphaString);
		assertEquals("The input String not contains only alphabetic characters.",false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
	public void testIsNumeric()
	{
		try
		{
		String numString = "1234";
		Validator validator =new Validator();
		boolean isValid = validator.isNumeric(numString);
		assertEquals(true, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
	public void testNegativeIsNumeric()
	{
		try
		{
		String numString = "1234asd";
		Validator validator =new Validator();
		boolean isValid = validator.isNumeric(numString);
		assertEquals(false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
	public void testNegZeroIsNumeric()
	{
		try
		{
		String numString = "00";
		Validator validator =new Validator();
		boolean isValid = validator.isNumeric(numString);
		assertEquals(false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
	public void testPositiveIsNumeric()
	{
		try
		{
		String numString = "1234";
		Validator validator =new Validator();
		boolean isValid = validator.isNumeric(numString,12);
		assertEquals(true, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
	public void testNegatinePositiveIsNumeric()
	{
		try
		{
		String numString = "null";
		Validator validator =new Validator();
		boolean isValid = validator.isNumeric(numString,12);
		assertEquals(false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
	public void testNegatineIsNumeric()
	{
		try
		{
		String numString = "00";
		Validator validator =new Validator();
		boolean isValid = validator.isNumeric(numString,12);
		assertEquals(false, isValid);
		}
		catch(Exception excep)
		{
			assertFalse("The input String not contains only alphabetic characters.", true);
		}
	}
}
