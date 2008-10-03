
package edu.wustl.common.util;

/**
 * This comparator is used where sorting should be done on relevance counter of NameValueBean.
 */
import edu.wustl.common.beans.NameValueBean;

public class NameValueBeanRelevanceComparator implements java.util.Comparator<Object>
{
	/**
	 * @param arg0 Object object to be compared.
	 * @param arg1 Object object to be compared.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @return int output of comparison.
	 */

	public int compare(Object arg0, Object arg1)
	{
		int retValue=0;
		if (arg0 instanceof NameValueBean && arg1 instanceof NameValueBean)
		{
			NameValueBean nvb1 = (NameValueBean) arg0;
			NameValueBean nvb2 = (NameValueBean) arg1;

			if (nvb1.getRelevanceCounter() != null && nvb2.getRelevanceCounter() != null)
			{
				retValue = getDiffOfNameValueBeans(nvb1, nvb2);
			}
		}
		return retValue;
	}

	/**
	 * This method returns difference of two NameValueBean.
	 * @param retValue
	 * @param nvb1 NameValueBean
	 * @param nvb2 NameValueBean
	 * @return difference of two NameValueBean.
	 */
	private int getDiffOfNameValueBeans(NameValueBean nvb1, NameValueBean nvb2)
	{
		int retValue=0;

		//Compare according to relevance counter
		int cmp = nvb1.getRelevanceCounter().compareTo(nvb2.getRelevanceCounter());
		//If relevance counter are equal then compare on basis of name.
		if (cmp == 0)
		{
			if (nvb1.getName() != null && nvb2.getName() != null)
			{
				retValue= nvb1.getName().toString().toLowerCase().compareTo(
						nvb2.getName().toString().toLowerCase());
			}
		}
		else
		{
			retValue=cmp;
		}
		return retValue;
	}
}
