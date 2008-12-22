/**
 * <p>Title: AppLogger Class>
 * <p>Description:  Application Logger class</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Kapil Kaveeshwar
 * @version 1.00
 *
 * FIXME: Java doc.
 */

package edu.wustl.common.util.logger;

import java.io.File;

import javax.servlet.ServletContext;

import org.apache.log4j.PropertyConfigurator;


/**
 * This is an utility class which provides functions to get logger objects.
 * This class get neither instantiated nor extended.
 *
 */
public final class Logger
{

	/**
	 * @deprecated
	 */
	public static org.apache.log4j.Logger out;
	/**
	 * Indicates whether Logger is configured or not.
	 */
	private static boolean isConfigured = false;

	/**
	 * Configures the logger with the properties of the specified category name in the log4j.xml file.
	 * The category should be present in the log4j.xml file in the JBOSS_HOME/server/default/conf folder.
	 * @param categoryName The category name.
	 */
	public static void configure(String categoryName)
	{
		if (out == null)
		{
			out = getLogger(Logger.class);
		}
	}

	/**
	 * Creates new logger object for a given class.
	 * @param className java class for which logger need to be created.
	 * @return new logger object.
	 */
	public static org.apache.log4j.Logger getLogger(Class className)
	{
		if (!isConfigured )
		{
			out=org.apache.log4j.Logger.getLogger(className);
			out.warn
			("Application specific logger configuration is not done. Please use Logger.configureLogger(path) before using getLogger()");
		}
		return org.apache.log4j.Logger.getLogger(className);
	}

	/**
	 * This method configure Logger.
	 * @param propDirPath Path of directory containing properties file.
	 */
	public static void configureLogger(String propDirPath)
	{
		if(!isConfigured)
		{
			isConfigured = true;
			StringBuffer log4jFilePath = new StringBuffer(propDirPath);
			try
			{
				log4jFilePath.append(File.separator).append("log4j.properties");
				PropertyConfigurator.configure(log4jFilePath.toString());
				out = org.apache.log4j.Logger.getLogger(Logger.class);
			}
			catch (Exception malformedURLEx)
			{
				configure(Logger.class.getName());
				out.fatal
				("Logger not configured. Invalid config file " + log4jFilePath.toString());
			}
		}
	}

	/**
	 * Configures the logger properties from the log4j.properties file.
	 * The log4j.properties file should be present in the classpath for configuring.
	 */
	@Deprecated
	public static void configure()
	{
		if (out == null)
		{
			out = org.apache.log4j.Logger.getLogger("Catissue");
		}
	}

	/**
	 * Logger class should not get instantiated from outside. Hence the constructor is private.
	 */
	private Logger()
	{
	}
	/**
	 * This method is used to configure logger with default values.
	 * @param servletContext generic ServletContext object.
	 */
	public static void configDefaultLogger(ServletContext servletContext)
	{
		String applicationResourcesPath = servletContext.getRealPath("WEB-INF")
				+ System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator")
				+ servletContext.getInitParameter("applicationproperties");

		configure(applicationResourcesPath);

	}
}