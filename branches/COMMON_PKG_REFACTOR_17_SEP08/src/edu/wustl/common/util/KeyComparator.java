
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
	
	/* 
	 * This method compare between two objects passed as parameters.
	 * @param object1
	 * @param object2
	 * @return integer value based on the output of comparison. 
	 */
	public int compare(Object object1, Object object2)
	{
		int retValue;
		int outer1 = 0, outer2 = 0, inner1 = 0, inner2 = 0;
		String key1 = (String) object1;
		String key2 = (String) object2;

		int index1 = key1.indexOf('_');
		if (index1 != -1)
		{
			outer1 = getKey(key1, index1);
		}

		int index2 = key2.indexOf('_');
		if (index2 != -1)
		{
			outer2 = getKey(key2, index2);
		}

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
			if (index1 != -1)
			{
				inner1=getKey(innerKey1,index1);
			}

			String innerKey2 = key2.substring(index2 + 1);
			index2 = innerKey2.indexOf('_');
			if (index2 != -1)
			{
				inner2=getKey(innerKey2,index2);
			}
			retValue = getretValue(inner1, inner2);
		}
		return retValue;
	}

	/**
	 * This method compare the two integers passed as parameters.
	 * @param inner1
	 * @param inner2
	 * @return
	 */
	private int getretValue(int inner1, int inner2)
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
	 * @param key1
	 * @param index1
	 * @return
	 */
	private int getKey(String key, int index)
	{
		String outerKey1 = key.substring(0, index);
		return Integer.parseInt(outerKey1.substring(outerKey1.indexOf(':') + 1));
	}
}
