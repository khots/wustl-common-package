
package edu.wustl.common.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.CommonServiceLocator;

public class UtilityTestCases extends CommonBaseTestCase
{

	public void testDatePattern()
	{
		try
		{
			String dateStr = "01-02-2008";
			String expectedPattern = "dd-MM-yyyy";
			String datPattern = Utility.datePattern(dateStr);
			assertEquals("Date is not in formate:" + expectedPattern, expectedPattern, datPattern);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to return correct date format.", true);
		}
	}

	public void testParseDate()
	{
		try
		{
			String dateStr = "01/30/2008";
			String expectedPattern = "MM/dd/yyyy";
			Date date = Utility.parseDate(dateStr, expectedPattern);
			assertNotNull("Can parse date:", date);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to parse date.", true);
		}
	}

	public void testCreateAccessorMethodName()
	{
		try
		{
			String attr = "identifier";
			boolean isSetter = true;
			String methodName = Utility.createAccessorMethodName(attr, isSetter);
			assertEquals("Method name SetIdentifier", "setIdentifier", methodName);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to return correct method name.", true);
		}
	}

	public void testGetArrayString()
	{
		try
		{
			Long[] objeIds = {Long.valueOf(1), Long.valueOf(2)};
			String str = Utility.getArrayString(objeIds);
			assertNotNull("Can get array string:", str);
		}
		catch (Exception exception)
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
			assertNotNull("Can get class object:", obj);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to get class object.", true);
		}
	}

	public void testIsNull()
	{
		try
		{
			Object obj = null;
			assertEquals(true, Utility.isNull(obj));
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to check is null object.", true);
		}
	}

	public void testIsNullObject()
	{
		try
		{
			Object obj = new Object();
			assertEquals(false, Utility.isNull(obj));
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to check is null object.", true);
		}
	}

	public void testParseAttributeName()
	{
		try
		{
			String methodName = "getName";
			assertEquals("name", Utility.parseAttributeName(methodName));
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to get attribute name.", true);
		}
	}

	public void testAddElement()
	{
		try
		{
			Object[] array = new Object[1];
			array[0] = "asd";
			String obj = "object";
			assertNotNull(Utility.addElement(array, obj));
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to get attribute name.", true);
		}
	}

	public void testRemoveNull()
	{
		try
		{
			List<String> list = new ArrayList<String>();
			list.add("1");
			list.add(null);
			list.add("2");
			List newList = Utility.removeNull(list);
			assertEquals("Removed null object.", list.size() - 1, newList.size());
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			assertFalse("Not able to get attribute name.", true);
		}
	}

	public void testParseClassName()
	{
		String simpleClassName = "";
		try
		{
			String className = "edu.wustl.common.datatypes.NumericDataType";
			simpleClassName = Utility.parseClassName(className);
			assertEquals(simpleClassName, "NumericDataType", simpleClassName);
		}
		catch (Exception exception)
		{
			assertFalse("Not able to get class name." + simpleClassName, true);
			exception.printStackTrace();
		}
	}

	public void testFailParseClassName()
	{
		String simpleClassName = "";
		try
		{
			String className = "NumericDataType";
			simpleClassName = Utility.parseClassName(className);
			assertEquals(simpleClassName, className, simpleClassName);
		}
		catch (Exception exception)
		{
			assertTrue("Not able to parse class name:" + simpleClassName, true);
			exception.printStackTrace();
		}
	}

	public void testGetFormBeanName()
	{
		try
		{
			NameValueBean bean = new NameValueBean();
			String beanName = Utility.getFormBeanName(bean);
			assertEquals("namevaluebean", beanName);
		}
		catch (Exception exception)
		{
			assertTrue("Not able to get FormBeanName", true);
			exception.printStackTrace();
		}
	}

	public void testParseDateToString()
	{
		try
		{
			String date = Utility.parseDateToString(new Date("02/01/2009"),
					CommonServiceLocator.getInstance().getDatePattern());
			assertEquals("02-01-2009", date);
		}
		catch (Exception exception)
		{
			assertTrue("Not able to get FormBeanName", true);
			exception.printStackTrace();
		}
	}
}
