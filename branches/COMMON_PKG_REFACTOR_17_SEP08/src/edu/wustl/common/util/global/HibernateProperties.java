/*
 * Created on Jul 19, 2004
 *
 */

package edu.wustl.common.util.global;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import edu.wustl.common.util.logger.Logger;

/**
 * This class is used to retrieve values of keys from the ApplicationResources.properties file.
 * @author kapil_kaveeshwar
 */
public class HibernateProperties
{

	private static Properties p;

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(HibernateProperties.class);

	public static void initBundle(String baseName) throws Exception
	{
		try
		{
			File file = new File(baseName);
			BufferedInputStream stram = new BufferedInputStream(new FileInputStream(file));
			p = new Properties();
			p.load(stram);
			stram.close();
		}
		catch (Exception exe)
		{
			logger.error("Error:Application may not run properly",exe);
			throw new Exception("Error:Application may not run properly: "+ exe.getMessage(), exe);
		}

		//ResourceBundle.
	}

	public static String getValue(String theKey)
	{
		return p.getProperty(theKey);
	}
}