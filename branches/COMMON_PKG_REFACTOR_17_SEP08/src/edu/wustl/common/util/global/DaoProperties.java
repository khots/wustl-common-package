package edu.wustl.common.util.global;

import java.util.ResourceBundle;

import edu.wustl.common.util.logger.Logger;


public class DaoProperties
{

	
	
	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(ApplicationProperties.class);

	static ResourceBundle bundle;
	static
	{
		try
		{
			bundle = ResourceBundle.getBundle("Dao");
			logger.debug("File Loaded !!!");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage() + " " + e);
		}
	}


	/**
	 * @param theKey key in a application property file
	 * @return the value of key.
	 */
	public static String getValue(String theKey)
	{
		String val = TextConstants.EMPTY_STRING;
		if (bundle == null)
		{
			logger.fatal("Resource bundle is null cannot return value for key " + theKey);
		}
		else
		{
			val = bundle.getString(theKey);
		}
		return val;
	}
	
	/*public static void main (String[] argv)
	{
		DaoProperties daoProperties = new DaoProperties();
		String defaultDao = daoProperties.getValue("defaultDao");
		
	}*/
}
