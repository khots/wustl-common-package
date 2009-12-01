package edu.wustl.common.util.global;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import edu.wustl.common.util.logger.LoggerConfig;


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
	private static org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getLogger(CommonServiceLocator.class);

	/**
	 * object of class CommonServiceLocator.
	 */
	private static CommonServiceLocator commonServLocator= new CommonServiceLocator();
	/**
	 * Application Name.
	 */
	private String appName;

	/**
	 * Application Home directory.
	 */
	private String appHome;
	/**
	 * Directory path of properties file.
	 */
	private String propDirPath;
	/**
	 * Application URL.
	 */
	private String appURL;

	/**
	 * Date separator.
	 */
	private String dateSeparator="-";

	/**
	 * Date separator.
	 */
	private final String dateSeparatorSlash="/";

	/**
	 * Minimum year.
	 * e.g 1900
	 */
	private String minYear;
	/**
	 * Maximum year.
	 * e.g 9999
	 */
	private String maxYear;

	/**
	 * Date pattern.
	 * e.g.MM-dd-yyyy
	 */
	private String datePattern;

	/**
	 * Time pattern. e.g. HH:mm:ss
	 */
	private String timePattern;
	/**
	 * Date time pattern.
	 * e.g. yyyy-MM-dd-HH24.mm.ss.SSS
	 */
	private String timeStampPattern;
	/**
	 * Path the request should be redirected to in case of XSS validation failures.
	 */
	private String xssFailurePath;
	/**
	 *No argument constructor.
	 *Here all the properties are set
	 */
	private CommonServiceLocator()
	{
		initProps();
		LoggerConfig.configureLogger(propDirPath);
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
		final InputStream stream = CommonServiceLocator.class.getClassLoader()
		.getResourceAsStream("ApplicationResources.properties");
	    try
		{
			final Properties props= new Properties();
			props.load(stream);
			setAppName(props);
			setPropDirPath();
			setDateSeparator(props);
			setDatePattern(props);
			setTimePattern(props);
			setTimeStampPattern(props);
			setMinYear(props);
			setMaxYear(props);

			//Added by Niharika
			setXSSFailurePath(props);
			stream.close();
		}
		catch (final IOException exception)
		{
			logger.fatal("Not able to load properties file",exception);
		}
	}
	private void setXSSFailurePath(final Properties props)
    {
	    xssFailurePath = props.getProperty("xss.failure.redirect.path");
    }

	public String getXSSFailurePath()
	{
	    return xssFailurePath;
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
	private void setAppName(final Properties props)
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
	 * @param appHome Object of Properties
	 */
	public void setAppHome(final String appHome)
	{
		this.appHome=appHome;
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
		final String path = System.getProperty("app.propertiesFile");
		if(path==null)
		{
			propDirPath = "";
		}
		else
		{
			final File propetiesDirPath = new File(path);
			propDirPath = propetiesDirPath.getParent();
		}
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
	public void setAppURL(final String requestURL)
	{
		if (appURL == null || TextConstants.EMPTY_STRING.equals(appURL.trim()))
		{
			String tempUrl=TextConstants.EMPTY_STRING;
			try
			{
				final URL url = new URL(requestURL);
				tempUrl = url.getProtocol() + "://" + url.getAuthority() + url.getPath();
				appURL = tempUrl.substring(0, tempUrl.lastIndexOf('/'));
				logger.debug("Application URL set: " + appURL);
			}
			catch (final MalformedURLException urlExp)
			{
				logger.error(urlExp.getMessage(), urlExp);
			}
		}
	}


	/**
	 * @return the dateSeparator
	 */
	public String getDateSeparator()
	{
		return dateSeparator;
	}

	/**
	 * @param props Object of Properties
	 */
	public void setDateSeparator(final Properties props)
	{
		dateSeparator = props.getProperty("date.separator");
	}

	/**
	 * @return the dateSeparatorSlash
	 */
	public String getDateSeparatorSlash()
	{
		return dateSeparatorSlash;
	}

	/**
	 * @return the minYear
	 */
	public String getMinYear()
	{
		return minYear;
	}

	/**
	 * @param props Object of Properties.
	 */
	public void setMinYear(final Properties props)
	{
		minYear = props.getProperty("min.year");
	}

	/**
	 * @return the maxYear
	 */
	public String getMaxYear()
	{
		return maxYear;
	}

	/**
	 * @param props Object of Properties.
	 */
	public void setMaxYear(final Properties props)
	{
		maxYear = props.getProperty("max.year");
	}

	/**
	 * @return the datePattern
	 */
	public String getDatePattern()
	{
		return datePattern;
	}

	/**
	 * @param props Object of Properties
	 */
	public void setDatePattern(final Properties props)
	{
		datePattern = props.getProperty("date.pattern");
	}

	/**
	 * @return the timePattern
	 */
	public String getTimePattern()
	{
		return timePattern;
	}

	/**
	 * @param props Object of Properties
	 */
	public void setTimePattern(final Properties props)
	{
		timePattern = props.getProperty("time.pattern");
	}

	/**
	 * @return the timeStampPattern
	 */
	public String getTimeStampPattern()
	{
		return timeStampPattern;
	}

	/**
	 * @param props Object of Properties
	 */
	public void setTimeStampPattern(final Properties props)
	{
		timeStampPattern = props.getProperty("timestamp.pattern");
	}

	/**
	 * This method gets Default Locale.
	 * @return Default Locale.
	 */
	public Locale getDefaultLocale()
	{
		return Locale.getDefault();
	}
}
