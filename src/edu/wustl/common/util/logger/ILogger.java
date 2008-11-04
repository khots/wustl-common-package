/**
 * <p>Title: AppLogger Class>
 * <p>Description:  Interface to define all the basic method for any loagger</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Kapil Kaveeshwar
 * @version 1.00
 *
 */

package edu.wustl.common.util.logger;
/**
 * Interface for Logger.
 */
public interface ILogger
{

	/**
	 * @param message Message to log.
	 */
	void info(Object message);

	/**
	 * @param message Message to log.
	 * @param throwable Throwable object.
	 */
	void info(Object message, Throwable throwable);

	/**
	 * @param message Message to log.
	 */
	void warn(Object message);

	/**
	 * @param message Message to log.
	 * @param throwable Throwable object.
	 */
	void warn(Object message, Throwable throwable);

	/**
	 * @param message Message to log.
	 */
	void debug(Object message);

	/**
	 * @param message Message to log.
	 * @param throwable Throwable object.
	 */
	void debug(Object message, Throwable throwable);

	/**
	 * @param message Message to log.
	 */
	void error(Object message);

	/**
	 * @param message Message to log.
	 * @param throwable Throwable object.
	 */
	void error(Object message, Throwable throwable);

	/**
	 * @param message Message to log.
	 */
	void fatal(Object message);

	/**
	 * @param message Message to log.
	 * @param throwable Throwable object.
	 */
	void fatal(Object message, Throwable throwable);
}