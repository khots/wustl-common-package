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
}
