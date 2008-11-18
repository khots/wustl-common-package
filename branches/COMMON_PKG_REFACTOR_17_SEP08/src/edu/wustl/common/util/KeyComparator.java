
package edu.wustl.common.util;

import java.util.Comparator;

/**
 * <p>Title: KeyComparator Class</p>
 * <p>Description:  This class is base class for comparing two keys.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author abhishek_mehta
 * @version 1.00
 * Created on July 26, 2007
 */
public class KeyComparator implements Comparator<Object>
{

	/**
	 * This method compare between two objects passed as parameters.
	 * @param object1 Object object to be compared.
	 * @param object2 Object object to be compared.
	 * @return integer value based on the output of comparison.
	 */
	public int compare(Object object1, Object object2)
	{
		int retValue;
		int outer1 = 0, outer2 = 0, inner1 = 0, inner2 = 0;
		String key1 = (String) object1;
		String key2 = (String) object2;

		int index1 = key1.indexOf('_');
		outer1 = getIntValue(outer1, key1, index1);

		int index2 = key2.indexOf('_');
		outer2 = getIntValue(outer2, key2, index2);

		if (outer1 > outer2)
		{
			retValue=1;
		}
		else if (outer1 < outer2)
		{
			retValue= -1;
		}
		else
		{
			String innerKey1 = key1.substring(index1 + 1);
			index1 = innerKey1.indexOf('_');
			inner1 = getIntValue(inner1, innerKey1, index1);

			String innerKey2 = key2.substring(index2 + 1);
			index2 = innerKey2.indexOf('_');
			inner2 = getIntValue(inner2, innerKey2, index2);
			retValue = getRetValue(inner1, inner2);
		}
		return retValue;
	}

	/**
	 * @param outer outer key.
	 * @param key String value to compare.
	 * @param index index of '_' in key object.
	 * @return int integer value after ':'
	 */
	private int getIntValue(int outer, String key, int index)
	{
		int outer1=outer;
		if (index != -1)
		{
			outer1 = getIntValueInKey(key, index);
		}
		return outer1;
	}

	/**
	 * This method compare the two integers passed as parameters.
	 * @param inner1 int comparable value.
	 * @param inner2 int comparable value.
	 * @return int return value after comparison.
	 */
	private int getRetValue(int inner1, int inner2)
	{
		int retValue;
		if (inner1 > inner2)
		{
			retValue=1;
		}
		else if (inner1 == inner2)
		{
			retValue=0;
		}
		else
		{
			retValue=-1;
		}
		return retValue;
	}

	/**
	 * This method return the integer for a substring.
	 * @param key String key.
	 * @param index int index.
	 * @return int integer value after ':' chanracter.
	 */
	private int getIntValueInKey(String key, int index)
	{
		String outerKey1 = key.substring(0, index);
		return Integer.parseInt(outerKey1.substring(outerKey1.indexOf(':') + 1));
	}
}
