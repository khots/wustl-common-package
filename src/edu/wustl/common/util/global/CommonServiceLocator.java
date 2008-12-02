package edu.wustl.common.util.global;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;


/**
 * This class is a common service locator. Different parameter like application url,
 * properties file directory etc. are set when this class loads. are set when this class loads.
 * @author ravi_kumar
 */
public final class CommonServiceLocator
{
	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(CommonServiceLocator.class);

	/**
	 * object of class CommonServiceLocator.
	 */
	private static CommonServiceLocator commonServLocator= new CommonServiceLocator();
	/**
	 * Application Name.
	 */
	private static String appName;

	/**
	 * Application Home directory.
	 */
	private static String appHome;
	/**
	 * Directory path of properties file.
	 */
	private static String propDirPath;
	/**
	 * Application URL.
	 */
	private static String appURL;

	/**
	 *No argument constructor.
	 *Here all the properties are set
	 */
	private CommonServiceLocator()
	{	initProps();
	}

	/**
	 * This method return object of the class CommonServiceLocator.
	 * @return object of the class CommonServiceLocator.
	 */
	public static CommonServiceLocator getInstance()
	{
		return commonServLocator;
	}
	
	/**
	 * This method loads properties file.
	 */
	private void initProps()
	{
		URL url = ClassLoader.getSystemResource("ApplicationResources.properties");

	    try
		{
	    	InputStream stream = url.openStream();
			Properties props= new Properties();
			props.load(stream);
			setAppName(props);
			//setAppVersion(props);
			setAppHome(props);
			setPropDirPath();
			stream.close();
		}
		catch (IOException exception)
		{
			logger.fatal("Not able to load properties file",exception);
		}
	}
	/**
	 * @return the application name.
	 */
	public String getAppName()
	{
		return appName;
	}


	/**
	 * Set the application name.
	 * @param props Object of Properties
	 */
	private void setAppName(Properties props)
	{
		appName=props.getProperty("app.name");
	}

	/**
	 * @return the application home directory where application is running.
	 */
	public String getAppHome()
	{
		return appHome;
	}


	/**
	 * Set application home directory where application is running.
	 * @param props Object of Properties
	 */
	private void setAppHome(Properties props)
	{
		appHome=props.getProperty("app.home.dir");
	}


	/**
	 * @return the path of directory of property files.
	 */
	public String getPropDirPath()
	{
		return propDirPath;
	}


	/**
	 * Set path of directory of property files.
	 */
	private void setPropDirPath()
	{
		String path = System.getProperty("app.propertiesFile");
		try
		{
			XMLPropertyHandler.init(path);
		}
		catch(Exception exception)
		{
			logger.error("Not able to initialize directory path of property files.");
		}
    	File propetiesDirPath = new File(path);
    	propDirPath = propetiesDirPath.getParent();
	}


	/**
	 * @return the Application URL.
	 */
	public String getAppURL()
	{
		return appURL;
	}


	/**
	 * This method Set Application URL.
	 * @param requestURL request URL.
	 */
	public void setAppURL(String requestURL)
	{
		if (appURL == null || TextConstants.EMPTY_STRING.equals(appURL.trim()))
		{
			String tempUrl=TextConstants.EMPTY_STRING;
			try
			{
				URL url = new URL(requestURL);
				tempUrl = url.getProtocol() + "://" + url.getAuthority() + url.getPath();
				appURL = tempUrl.substring(0, tempUrl.lastIndexOf('/'));
				logger.debug("Application URL set: " + appURL);
			}
			catch (MalformedURLException urlExp)
			{
				logger.error(urlExp.getMessage(), urlExp);
			}
		}
	}
}
