/**
 * 
 */

package edu.wustl.common.querysuite.queryengine.impl;

import edu.wustl.common.util.logger.Logger;
import junit.framework.TestCase;

/**
 * @author chetan_patil
 *
 */
public class CommonPathFinderTestCase extends TestCase
{
	private CommonPathFinder commonPathFinder = new CommonPathFinder();;

	static
	{
		Logger.configure();// To avoid null pointer Exception for code calling logger statements.
	}

	/**
	 * 
	 */
	public CommonPathFinderTestCase()
	{
		
	}
	
	public void testGetDataSource() {
		String dsName = commonPathFinder.getDS();
		assertEquals("java:/cab2bDS", dsName);
	}

}
