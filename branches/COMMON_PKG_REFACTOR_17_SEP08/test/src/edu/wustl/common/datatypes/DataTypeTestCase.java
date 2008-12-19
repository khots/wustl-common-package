/**
 * 
 */

package edu.wustl.common.datatypes;

import org.apache.struts.action.ActionErrors;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.ParseException;

/**
 * @author prashant_bandal
 *
 */
public class DataTypeTestCase extends CommonBaseTestCase
{

	public void testValidateDate()
	{
		IDBDataType dbDataType;
		try
		{
			dbDataType = DataTypeConfigFactory.getInstance().getDataType("date");
			ActionErrors errors = new ActionErrors();
			boolean value = dbDataType.validate("12-12-2008", errors);
			assertEquals(true, value);
		}
		catch (ParseException e)
		{
			fail("data type not valid.");
			e.printStackTrace();
		}
	}
	public void testFailValidateDate()
	{
		IDBDataType dbDataType;
		try
		{
			dbDataType = DataTypeConfigFactory.getInstance().getDataType("date1");
			ActionErrors errors = new ActionErrors();
			boolean value = dbDataType.validate("12-12-2008", errors);
			assertEquals(true, value);
			fail("Data type not valid.");
		}
		catch (ParseException e)
		{
			assertTrue("Data type not valid.", true);
			e.printStackTrace();
		}
	}
	public void testValidateInteger()
	{
		IDBDataType dbDataType;
		try
		{
			dbDataType = DataTypeConfigFactory.getInstance().getDataType("integer");
			ActionErrors errors = new ActionErrors();
			boolean value = dbDataType.validate("10", errors);
			assertEquals(true, value);
		}
		catch (ParseException e)
		{
			fail("data type not valid.");
			e.printStackTrace();
		}
	}
	public void testValidateBigInt()
	{
		IDBDataType dbDataType;
		try
		{
			dbDataType = DataTypeConfigFactory.getInstance().getDataType("bigint");
			ActionErrors errors = new ActionErrors();
			boolean value = dbDataType.validate("1000", errors);
			assertEquals(true, value);
		}
		catch (ParseException e)
		{
			fail("data type not valid.");
			e.printStackTrace();
		}
	}
	public void testValidateDouble()
	{
		IDBDataType dbDataType;
		try
		{
			dbDataType = DataTypeConfigFactory.getInstance().getDataType("double");
			ActionErrors errors = new ActionErrors();
			assertEquals(true,dbDataType.validate("10.10", errors));
		}
		catch (ParseException e)
		{
			fail("data type not valid.");
			e.printStackTrace();
		}
	}
	public void testValidateTinyInt()
	{
		IDBDataType dbDataType;
		boolean value=false; 
		try
		{
			dbDataType = DataTypeConfigFactory.getInstance().getDataType("tinyint");
			ActionErrors errors = new ActionErrors();
			value = dbDataType.validate("1", errors);
			assertEquals(false, value);
		}
		catch (ParseException e)
		{
			fail("data type not valid.");
			e.printStackTrace();
		}
	}
}
