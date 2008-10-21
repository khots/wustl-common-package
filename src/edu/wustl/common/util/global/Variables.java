/*
 * Created on Nov 30, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package edu.wustl.common.util.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.NameValueBean;

/**
 * @author ajay_sharma
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * This class is specific to common files. It is used by common package.
 * */

public class Variables
{

	/**
	 * specify CONST_VARIABLES_VALUE.
	 */
	private static final String CONST_VARIABLES_VALUE = "";

	/**
	 * specify applicationName.
	 */
	public static String applicationName = CONST_VARIABLES_VALUE;
	/**
	 * specify applicationVersion.
	 */
	public static String applicationVersion = CONST_VARIABLES_VALUE;

	/**
	 * To hold path of application installation directory.
	 */
	public static String applicationHome = CONST_VARIABLES_VALUE;

	/**
	 * specify database name.
	 */
	public static String databaseName = CONST_VARIABLES_VALUE;

	/**
	 * specify propertiesDirPath.
	 */
	public static String propertiesDirPath = CONST_VARIABLES_VALUE;

	/**
	 * specify date Pattern.
	 */
	public static String datePattern = CONST_VARIABLES_VALUE;

	/**
	 * specify time pattern.
	 */
	public static String timePattern = CONST_VARIABLES_VALUE;

	/**
	 * specify timeFormatFunction.
	 */
	public static String timeFormatFunction = CONST_VARIABLES_VALUE;

	/**
	 * specify dateFormatFunction.
	 */
	public static String dateFormatFunction = CONST_VARIABLES_VALUE;

	/**
	 * specify string To date Function.
	 */
	public static String strTodateFunction = CONST_VARIABLES_VALUE;

	/**
	 * specify dateTostrFunction.
	 */
	public static String dateTostrFunction = CONST_VARIABLES_VALUE;

	/**
	 * specify catissueURL.
	 */
	public static String catissueURL = CONST_VARIABLES_VALUE;

	/**
	 * specify entityCPSqlMap.
	 */
	public static Map<String, String> entityCPSqlMap = new HashMap<String, String>();

	/**
	 * specify mainProtocolObject.
	 */
	public static String mainProtocolObject = CONST_VARIABLES_VALUE;

	/**
	 * specify queryReadDeniedObjectList.
	 */
	public static List<String> queryReadDeniedObjectList = new ArrayList<String>();

	/**
	 *  Ravi : for Multi Site Rep. changes.
	 *  specify privilege Details Map.
	 */
	public static Map<String, String> privilegeDetailsMap = new HashMap<String, String>();

	/**
	 * specify privilegeGroupingMap.
	 */
	public static Map<String, List<NameValueBean>> privilegeGroupingMap = new HashMap<String, List<NameValueBean>>();

	/**
	 * specify validatorClassname.
	 */
	public static String validatorClassname = CONST_VARIABLES_VALUE;
}