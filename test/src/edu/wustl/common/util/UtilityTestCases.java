package edu.wustl.common.util;


import java.util.Date;

import edu.wustl.common.CommonBaseTestCase;

public class UtilityTestCases extends CommonBaseTestCase
{

	public void testDatePattern()
	{
		try
		{
		String dateStr="01/30/2008";
		String expectedPattern="MM/dd/yyyy";
		String datPattern=Utility.datePattern(dateStr);
		assertEquals("Date is not in formate:"+expectedPattern, expectedPattern, datPattern);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to return correct date format.", true);
		}
	}
	public void testParseDate()
	{
		try
		{
		String dateStr="01/30/2008";
		String expectedPattern="MM/dd/yyyy";
		Date date=Utility.parseDate(dateStr,expectedPattern);
		assertNotNull("Can parse date:",date);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to parse date.", true);
		}
	}
	public void testCreateAccessorMethodName()
	{
		try
		{
			String attr="identifier";
			boolean isSetter = true;
			String methodName = Utility.createAccessorMethodName(attr,isSetter);
			assertEquals("Method name SetIdentifier", "setIdentifier", methodName);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to return correct method name.", true);
		}
	}
	public void testGetArrayString()
	{
		try
		{
		Long []objeIds= {Long.valueOf(1),Long.valueOf(2)};
		String str = Utility.getArrayString(objeIds);
		assertNotNull("Can get array string:",str);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to get array string.", true);
		}
	}
	public void testGetClassObject()
	{
		try
		{
			String className = "edu.wustl.common.datatypes.NumericDataType";
			Object obj = Utility.getObject(className);
			assertNotNull("Can get class object:",obj);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to get class object.", true);
		}
	}
	public void testIsNull()
	{
		try
		{
			Object obj =null;
			assertEquals(true, Utility.isNull(obj));
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to check is null object.", true);
		}
	}
	public void testIsNullObject()
	{
		try
		{
			Object obj =new Object();
			assertEquals(false, Utility.isNull(obj));
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to check is null object.", true);
		}
	}
}
