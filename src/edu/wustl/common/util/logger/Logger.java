/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

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


public abstract class Logger
{
	public static org.apache.log4j.Logger out;
	
    /**
     * Configures the logger with the properties of the specified category name in the log4j.xml file.
     * The category should be present in the log4j.xml file in the JBOSS_HOME/server/default/conf folder.
     * @param categoryName The category name. 
     */
    public static void configure(String categoryName)
	{
		if(out==null)
		{
		    out=org.apache.log4j.Logger.getLogger(categoryName);
		}
	}
    
    /**
     * Configures the logger properties from the log4j.properties file.
     * The log4j.properties file should be present in the classpath for configuring.
     */
    public static void configure()
    {
        if (out == null)
        {
            out=org.apache.log4j.Logger.getLogger("");
        }
    }
    
	private Logger()
	{
	}
}