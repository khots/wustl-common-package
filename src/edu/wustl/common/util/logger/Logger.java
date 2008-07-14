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

import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerFactory;

import edu.wustl.common.util.CVSTagReader;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Variables;


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
	
	static {
		
		URL url  = Logger.class.getClassLoader().getResource("log4j.properties");
		if(url != null)
		{
			PropertyConfigurator.configure(url);			
		}

	}
    /**
     * Configures the logger with the properties of the specified category name in the log4j.xml file.
     * The category should be present in the log4j.xml file in the JBOSS_HOME/server/default/conf folder.
     * @param categoryName The category name. 
     */
    public static void configure(String categoryName)
	{
		if(out==null)
		{
		    out=getLogger(Logger.class);
		}
	}
    /**
     * Creates new logger object for a given class. 
     * @param className java class for which logger need to be created.
     * @return new logger object.
     */
    public static org.apache.log4j.Logger getLogger(Class className)
    {
    	return org.apache.log4j.Logger.getLogger(className); 
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
            out=org.apache.log4j.Logger.getLogger("Catissue");
        }
    }
    
	/**
	 * Logger class should not get instantiated from outside. Hence the constructor is private. 
	 */
	private Logger()
	{
	}
	
	public static void configDefaultLogger(ServletContext servletContext)
	{
        String applicationResourcesPath = servletContext.getRealPath("WEB-INF")
        + System.getProperty("file.separator")
        + "classes" + System.getProperty("file.separator")
        + servletContext.getInitParameter("applicationproperties");

        configure(applicationResourcesPath);
		
	}
}