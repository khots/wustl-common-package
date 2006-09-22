/*
 * Created on Jul 19, 2004
 *
 */

package edu.wustl.common.util.global;

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
 * This method should be used when you want to customize error message with replacement parameters
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

}