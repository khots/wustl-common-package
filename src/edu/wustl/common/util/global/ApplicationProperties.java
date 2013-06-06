/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/*
 * Created on Jul 19, 2004
 *
 */

package edu.wustl.common.util.global;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class is used to retrieve values of keys from the ApplicationResources.properties file.
 * @author kapil_kaveeshwar
 */
public class ApplicationProperties
{

	private static ResourceBundle bundle;

	public static void initBundle(String baseName)
	{
		bundle = ResourceBundle.getBundle(baseName);
	}

	public static String getValue(String theKey)
	{
		return bundle.getString(theKey);
	}

/**
 * This method should be used when you want to customize error message with multiple replacement parameters
 * 
 * @param theKey - error key
 * @param placeHolders - replacement Strings
 * @return - complete error message
 */	
	public static String getValue(String theKey, List placeHolders)
	{
		String msg = bundle.getString(theKey);
		StringBuffer message = new StringBuffer(msg);

		for (int i = 0; i < placeHolders.size(); i++)
		{
			message.replace(message.indexOf("{"), message.indexOf("}") + 1, (String) placeHolders.get(i));
		}
		return message.toString();
	}
	
	/**
	 * This method should be used when you want to customize error message with single replacement parameter
	 * 
	 * @param theKey - error key
	 * @param placeHolders - replacement Strings
	 * @return - complete error message
	 */	
		public static String getValue(String theKey, String placeHolder)
		{
			List temp = new ArrayList();
			temp.add(placeHolder);
			return getValue(theKey,temp);
		}


}