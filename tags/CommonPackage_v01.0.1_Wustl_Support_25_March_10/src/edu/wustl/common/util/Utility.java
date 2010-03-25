/*
 * Created on Aug 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.tree.TreeNodeImpl;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.global.XMLParserUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.dao.util.HibernateMetaDataFactory;

/**
 * @author kapil_kaveeshwar
 *
 */
public final class Utility
{

	/**
	 * private constructor.
	 */
	private Utility()
	{

	}

	/**
	 * Constant for TWO.
	 */
	private static final int TWO = 2;
	/**
	 * LOGGER -Generic Logger.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(Utility.class);

	/**
	 * Parses the string format of date in the given format and returns the Data object.
	 * @param date the string containing date.
	 * @param pattern the pattern in which the date is present.
	 * @return the string format of date in the given format and returns the Data object.
	 * @throws ParseException throws ParseException if date is not in pattern specified.
	 */
	public static Date parseDate(String date, String pattern) throws ParseException
	{
		Date dateObj = null;
		if (date != null && !TextConstants.EMPTY_STRING.equals(date.trim()))
		{
			try
			{
				SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, CommonServiceLocator
						.getInstance().getDefaultLocale());
				dateObj = dateFormat.parse(date);
			}
			catch (ParseException exception)
			{
				String message = new StringBuffer("Date '").append(date).append(
						"' is not in format : ").append(pattern).toString();
				LOGGER.debug(message, exception);
				throw new ParseException(message, exception.getErrorOffset());
			}
		}
		return dateObj;
	}

	/**
	 * Parses the string format of date in the given format and returns the Data object.
	 * @param date the string containing date.
	 * @return the string format of date in the given format and returns the Data object.
	 * @throws ParseException throws ParseException if date is not valid.
	 */
	public static Date parseDate(String date) throws ParseException
	{
		String pattern = datePattern(date);

		return parseDate(date, pattern);
	}

	/**
	 * Returns matching date pattern.
	 * @param strDate Date as string
	 * @return matched date pattern.
	 */
	public static String datePattern(String strDate)
	{
		String datePattern = null;
		if (strDate != null)
		{
			List<SimpleDateFormat> datePatternList = new ArrayList<SimpleDateFormat>();
			datePatternList.add(new SimpleDateFormat("MM-dd-yyyy", CommonServiceLocator
					.getInstance().getDefaultLocale()));
			datePatternList.add(new SimpleDateFormat("MM/dd/yyyy", CommonServiceLocator
					.getInstance().getDefaultLocale()));
			datePatternList.add(new SimpleDateFormat("yyyy-MM-dd", CommonServiceLocator
					.getInstance().getDefaultLocale()));
			datePatternList.add(new SimpleDateFormat("yyyy/MM/dd", CommonServiceLocator
					.getInstance().getDefaultLocale()));
			datePatternList.add(new SimpleDateFormat("dd-MM-yyyy", CommonServiceLocator
					.getInstance().getDefaultLocale()));
			datePatternList.add(new SimpleDateFormat("dd/MM/yyyy", CommonServiceLocator
					.getInstance().getDefaultLocale()));
			datePatternList.add(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", CommonServiceLocator
					.getInstance().getDefaultLocale()));
			Date date = null;
			String matchingPattern = null;
			for (SimpleDateFormat dtPattern : datePatternList)
			{
				try
				{
					date = dtPattern.parse(strDate);
					if (date != null)
					{
						matchingPattern = dtPattern.toPattern();
						if (strDate.equals(dtPattern.format(date)))
						{
							datePattern = matchingPattern;
							break;
						}
					}
				}
				catch (ParseException exception)
				{
					LOGGER.info(strDate + " date not in formate:" + dtPattern.toPattern());
				}
			}
		}
		return datePattern;
	}

	/**
	 * This method creates Accessor Method Name.
	 * @param attr attribute
	 * @param isSetter specifies is Setter.
	 * @return Method Name.
	 */
	public static String createAccessorMethodName(String attr, boolean isSetter)
	{
		String str = "get";
		if (isSetter)
		{
			str = "set";
		}
		StringBuffer mathodname = new StringBuffer(attr);
		mathodname.setCharAt(0, Character.toUpperCase(attr.charAt(0)));
		mathodname.insert(0, str);
		return mathodname.toString();
	}

	/**
	 * Create the getter method of attribute.
	 * @param obj Object
	 * @param attrName attribute Name.
	 * @return Object.
	 * @throws InvocationTargetException InvocationTargetException
	 * @throws IllegalAccessException IllegalAccessException
	 * @throws NoSuchMethodException NoSuchMethodException
	 */
	public static Object getValueFor(Object obj, String attrName) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{
		//Create the getter method of attribute
		String methodName = Utility.createAccessorMethodName(attrName, false);
		Class objClass = obj.getClass();
		Method method = objClass.getMethod(methodName, new Class[0]);

		return method.invoke(obj, new Object[0]);
	}

	/**
	 * Start: Change for API Search   --- Jitendra 06/10/2006
	 * In Case of Api Search, previoulsy it was failing since there was default
	 * class level initialization
	 * on domain object. For example in ParticipantMedicalIdentifier object, it was initialized as
	 * protected Site site= new Site(); So we removed default class level initialization on domain object.
	 * Hence getValueFor() method was returning null. So write new method SetValueFor() which will
	 * instantiate new Object and set it in parent object.
	 * @param obj Object
	 * @param attrName attribute Name.
	 * @param attrValue attribute Value.
	 * @return Object.
	 * @throws IllegalAccessException Illegal Access Exception
	 * @throws InstantiationException Instantiation Exception
	 * @throws InvocationTargetException Invocation Target Exception
	 */
	public static Object setValueFor(Object obj, String attrName, Object attrValue)
			throws InstantiationException, IllegalAccessException, InvocationTargetException

	{

		//create the setter method for the attribute.
		String methodName = Utility.createAccessorMethodName(attrName, true);
		Method method = findMethod(obj.getClass(), methodName);
		Object object = attrValue;
		if (object == null)
		{
			object = method.getParameterTypes()[0].newInstance();
		}
		Object[] objArr = {object};
		//set the newInstance to the setter nethod of parent obj
		method.invoke(obj, objArr);
		return object;

	}

	/**
	 * Finds method of given method Name.
	 * @param objClass Class.
	 * @param methodName the method Name
	 * @return Method
	 */
	private static Method findMethod(Class objClass, String methodName)
	{
		Method[] methods = objClass.getMethods();
		Method method = null;
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].getName().equals(methodName))
			{
				method = methods[i];
				break;
			}
		}
		return method;
	}

	/**
	 * This method gets Array String of Object Array.
	 * @param objectIds Array of object Ids.
	 * @return Array String.
	 */
	public static String getArrayString(Object[] objectIds)
	{
		StringBuffer arrayStr = new StringBuffer();
		for (int i = 0; i < objectIds.length; i++)
		{
			arrayStr.append(objectIds[i].toString()).append(',');
		}
		return arrayStr.toString();
	}

	/**
	 * This method gets Class Object.
	 * @param fullClassName Full qualified name
	 * @return Class.
	 */
	public static Class getClassObject(String fullClassName)
	{
		Class className = null;
		try
		{
			className = Class.forName(fullClassName);
		}
		catch (ClassNotFoundException classNotExcp)
		{
			LOGGER.warn("Didn't find any class as " + fullClassName, classNotExcp);
		}

		return className;
	}

	/**
	 * Changes the format of the string compatible to Grid Format,
	 * removing escape characters and special characters from the string.
	 * @param obj - Unformatted obj to be printed in Grid Format
	 * @return obj - Foratted obj to print in Grid Format
	 */
	public static Object toGridFormat(Object obj)
	{
		Object retObj = obj;
		if (obj instanceof String)
		{
			StringBuffer tokenedString = new StringBuffer();
			StringTokenizer tokenString = new StringTokenizer((String) obj, "\n\r\f");
			while (tokenString.hasMoreTokens())
			{
				tokenedString.append(tokenString.nextToken()).append(' ');
			}
			String gridFormattedStr = new String(tokenedString);
			retObj = gridFormattedStr.replaceAll("\"", "\\\\\"");
		}

		return retObj;
	}

	/**
	 * This method checks for null value.
	 * @param obj Object to be check.
	 * @return true if null else false.
	 */
	public static boolean isNull(Object obj)
	{
		boolean isNull = false;
		if (obj == null)
		{
			isNull = true;
		}
		return isNull;
	}

	/**
	 * Instantiates and returns the object of the class name passed.
	 * @param className The class name whose object is to be instantiated.
	 * @return the object of the class name passed.
	 */
	public static Object getObject(String className)
	{
		Object object = null;

		try
		{
			Class classObject = Utility.getClassObject(className);
			if (classObject != null)
			{
				object = classObject.newInstance();
			}
		}
		catch (InstantiationException instExp)
		{
			LOGGER.debug("Can not create instance of class:" + className, instExp);
		}
		catch (IllegalAccessException illAccExp)
		{
			LOGGER.debug("Can not create instance of class:" + className, illAccExp);
		}

		return object;
	}

	/**
	 * This method add Element in object array.
	 * @param array object array.
	 * @param obj Object to be add.
	 * @return object array.
	 */
	public static Object[] addElement(Object[] array, Object obj)
	{
		int arraySize = array.length + 1;
		Object[] newObjectArr = new Object[arraySize];

		if (array instanceof String[])
		{
			newObjectArr = new String[arraySize];
		}

		System.arraycopy(array, 0, newObjectArr, 0, array.length);
		newObjectArr[array.length] = obj;
		return newObjectArr;
	}

	/**
	 * This method removes null values from given list.
	 * @param list list.
	 * @return List without null values.
	 */
	public static List removeNull(List list)
	{
		List nullFreeList = new ArrayList();
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) != null)
			{
				nullFreeList.add(list.get(i));
			}
		}
		return nullFreeList;
	}

	/**
	 * Parses the fully qualified class name and returns only the class name.
	 * @param qualifiedName The fully qualified class name.
	 * @return The class name.
	 */
	public static String parseClassName(String qualifiedName)
	{
		String className = qualifiedName;
		try
		{
			Class clazz = Class.forName(qualifiedName);
			className = clazz.getSimpleName();
		}
		catch (Exception e)
		{
			LOGGER.warn("Not able to parse class name:" + qualifiedName);
		}
		return className;
	}

	/**
	 * Returns name of FormBean specified in struts-config.xml for passed Object of FormBean.
	 * @param obj - FormBean object
	 * @return String - name of FormBean object
	 */
	public static String getFormBeanName(Object obj)
	{
		String className = obj.getClass().getSimpleName();
		String classNameFirstCharacter = className.substring(0, 1);

		className = classNameFirstCharacter.toLowerCase(CommonServiceLocator.getInstance()
				.getDefaultLocale())
				+ className.substring(1, (className.length()));
		return className;
	}

	/**
	 * Parses the Date in given format and returns the string representation.
	 * @param date the Date to be parsed.
	 * @param pattern the pattern of the date.
	 * @return returns the string representation of Date.
	 */
	public static String parseDateToString(Date date, String pattern)
	{
		String dateStr = TextConstants.EMPTY_STRING;
		if (date != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, CommonServiceLocator
					.getInstance().getDefaultLocale());
			dateStr = dateFormat.format(date);
		}
		return dateStr;
	}

	/**
	 * This method converts object to string.
	 * @param obj object to be convert.
	 * @return string object.
	 */
	public static String toString(Object obj)
	{
		String retValue;
		if (obj == null)
		{
			retValue = TextConstants.EMPTY_STRING;
		}
		else
		{
			retValue = obj.toString();
		}

		return retValue;
	}

	/**
	 * This method gets time.
	 * @param date Date.
	 * @return time.
	 */
	public static String[] getTime(Date date)
	{
		String[] time = new String[TWO];
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		time[0] = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		time[1] = Integer.toString(cal.get(Calendar.MINUTE));
		return time;
	}

	/**
	 * This method converts collection to Long Array.
	 * @param collection Collection to be convert.
	 * @return long array.
	 */
	public static Long[] toLongArray(Collection<Long> collection)
	{

		Long[] obj = new Long[collection.size()];

		int index = 0;
		Iterator<Long> iterator = collection.iterator();
		while (iterator.hasNext())
		{
			obj[index] = (Long) iterator.next();
			index++;
		}
		return obj;
	}

	/**
	 * This method convert object to int.
	 * @param obj Object to be convert.
	 * @return int value.
	 */
	public static int toInt(Object obj)
	{
		int value = 0;
		if (obj != null)
		{
			String objVal = String.valueOf(obj);
			if (objVal.length() > 0)
			{
				Integer intObj = Integer.parseInt(objVal);
				value = intObj.intValue();
			}
		}
		return value;
	}

	/**
	 * This method convert object to Long.
	 * @param obj Object to be convert.
	 * @return long value.
	 */
	public static long toLong(Object obj)
	{
		long value = 0;
		if (obj != null)
		{
			String objVal = String.valueOf(obj);
			if (objVal.length() > 0)
			{
				Long intObj = Long.parseLong(objVal);
				value = intObj.longValue();
			}
		}
		return value;

	}

	/**
	 *  This method convert object to Double.
	 * @param obj Object to be convert.
	 * @return Double value.
	 */
	public static double toDouble(Object obj)
	{
		double value = 0;
		if (obj != null)
		{
			value = ((Double) obj).doubleValue();
		}
		return value;
	}

	/**
	 * checking whether key's value is persisted or not.
	 * @param map map.
	 * @param key key.
	 * @return Return true if Persisted Value in map else false.
	 */
	public static boolean isPersistedValue(Map map, String key)
	{
		Object obj = map.get(key);
		String val = null;
		boolean isPersistedValue = false;
		if (obj != null)
		{
			val = obj.toString();
		}
		if ((val != null && !(TextConstants.STR_ZERO.equals(val)))
				&& !(TextConstants.EMPTY_STRING.equals(val)))
		{
			isPersistedValue = true;
		}
		return isPersistedValue;
	}

	/**
	 * @param str String to be converted to Proper case.
	 * @return The String in Proper case.
	 */
	public static String initCap(String str)
	{
		StringBuffer retStr;
		if (Validator.isEmpty(str))
		{
			retStr = new StringBuffer();
			LOGGER.debug("Utility.initCap : - String provided is either empty or null" + str);
		}
		else
		{
			retStr = new StringBuffer(str.toLowerCase(CommonServiceLocator.getInstance()
					.getDefaultLocale()));
			retStr.setCharAt(0, Character.toUpperCase(str.charAt(0)));
		}
		return retStr.toString();
	}

	/**
	 * This method is used in JSP pages to get the width of columns for the html fields.
	 * It acts as a wrapper for the HibernateMetaData getColumnWidth() method.
	 * @param className Class name of the field
	 * @param attributeName Attribute name of the field.
	 * @return Length of the column.
	 * @see HibernateMetaData.getColumnWidth()
	 */
	public static String getColumnWidth(Class className, String attributeName, String appName)
	{
		String colWidth;
		HibernateMetaData hibernateMetaData = HibernateMetaDataFactory
				.getHibernateMetaData(appName);
		if (hibernateMetaData != null)
		{
			colWidth = Integer
					.toString((hibernateMetaData.getColumnWidth(className, attributeName)));
		}
		else
		{
			colWidth = "0";
		}
		return colWidth;

	}

	/**
	 * To sort the Tree nodes based on the comparators overidden by the TreeNodeImpl object.
	 * @param nodes reference to the Vector containing object of class implementing TreeNodeImpl class.
	 */
	public static void sortTreeVector(List nodes)
	{
		Collections.sort(nodes);
		for (int i = 0; i < nodes.size(); i++)
		{
			TreeNodeImpl child = (TreeNodeImpl) nodes.get(i);
			sortTreeVector(child.getChildNodes());
		}
	}

	/**
	 * Remove special characters and white space from a string.
	 * @param str string.
	 * @return String after removing special characters.
	 */
	public static String removeSpecialCharactersFromString(String str)
	{
		String regexExpression = "[\\p{Punct}\\s]";
		return str.replaceAll(regexExpression, "");
	}

	/**
	 * Returns the label for objects name. It compares ascii value of each char for lower or upper case and
	 * then forms a capitalized label.
	 * e.g. firstName is converted to First Name
	 * @param objectName name of the attribute
	 * @return capitalized label
	 */
	public static String getDisplayLabel(String objectName)
	{
		StringBuffer formatedStr = new StringBuffer();
		int prevIndex = 0;
		String tempStr;
		for (int i = 0; i < objectName.length(); i++)
		{
			if (Character.isUpperCase(objectName.charAt(i)))
			{
				tempStr = objectName.substring(prevIndex, i);
				if (!TextConstants.EMPTY_STRING.equals(tempStr))
				{
					formatedStr.append(initCap(tempStr));
					formatedStr.append(Constants.CONST_SPACE_CAHR);
				}
				prevIndex = i;
			}
		}
		tempStr = objectName.substring(prevIndex, objectName.length());
		formatedStr.append(initCap(tempStr));
		return formatedStr.toString();
	}

	/**
	 * Specifies date pattern.
	 */
	private static String pattern = "MM-dd-yyyy";

	/**
	 * This method gets month from given date.
	 * @param date date
	 * @return month.
	 */
	public static int getMonth(String date)
	{
		return getCalendar(date, pattern).get(Calendar.MONTH) + 1;
	}

	/**
	 * This method gets day from given date.
	 * @param date date
	 * @return day.
	 */
	public static int getDay(String date)
	{
		return getCalendar(date, pattern).get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * This method gets Year from given date.
	 * @param date date
	 * @return Year.
	 */
	public static int getYear(String date)
	{
		return getCalendar(date, pattern).get(Calendar.YEAR);
	}

	/**
	 * This method gets Calendar.
	 * @param date date
	 * @param pattern pattern.
	 * @return Calendar
	 */
	private static Calendar getCalendar(String date, String pattern)
	{
		Calendar calendar = Calendar.getInstance();
		try
		{

			if (date != null && !date.trim().equals(""))
			{
				SimpleDateFormat dateformat = new SimpleDateFormat(pattern, CommonServiceLocator
						.getInstance().getDefaultLocale());
				Date givenDate = dateformat.parse(date);
				calendar = Calendar.getInstance();
				calendar.setTime(givenDate);
			}
		}
		catch (ParseException exception)
		{
			LOGGER.error("exception in getCalendar: date=" + date, exception);
			//calendar = Calendar.getInstance();
		}
		return calendar;
	}

	/**
	 * For MSR changes.
	 * @throws edu.wustl.common.exception.ParseException throws this exception if
	 * specified xml file not found or not able to parse the file.
	 */

	public static void initializePrivilegesMap() throws edu.wustl.common.exception.ParseException
	{
		Map<String, String> privDetMap = Variables.privilegeDetailsMap;
		Map<String, List<NameValueBean>> privGroupMap = Variables.privilegeGroupingMap;
		InputStream inputXmlFile = getCurrClassLoader().getResourceAsStream(
				TextConstants.PERMSN_MAP_DET_FILE);
		if (inputXmlFile != null)
		{
			Document doc;
			try
			{
				doc = XMLParserUtility.getDocument(inputXmlFile);
			}
			catch (Exception ioe)
			{
				LOGGER.error(ioe.getMessage(), ioe);
				throw new edu.wustl.common.exception.ParseException(ioe);
			}
			finally
			{
				try
				{
					inputXmlFile.close();
				}
				catch (IOException exception)
				{
					LOGGER.error("Not able to close input stream", exception);
				}
			}
			Element root = doc.getDocumentElement();
			NodeList nodeList = root.getElementsByTagName("PrivilegeMapping");
			int length = nodeList.getLength();
			for (int counter = 0; counter < length; counter++)
			{
				Element element = (Element) (nodeList.item(counter));
				String key = element.getAttribute("key");
				String value = element.getAttribute("value");

				privDetMap.put(key, value);
			}
			privGroupMap.put("SITE", getPriviligesList(root, "siteMapping"));
			privGroupMap.put("CP", getPriviligesList(root, "collectionProtocolMapping"));
			privGroupMap.put("SCIENTIST", getPriviligesList(root, "scientistMapping"));
			privGroupMap.put("GLOBAL", getPriviligesList(root, "globalMapping"));
		}
	}

	/**
	 * returns Privilege List.
	 * @param root root Element.
	 * @param tagName tag Name.
	 * @return Priviliges List.
	 */
	private static List<NameValueBean> getPriviligesList(Element root, String tagName)
	{
		NodeList nodeList1 = root.getElementsByTagName(tagName);
		int length1 = nodeList1.getLength();
		List<NameValueBean> sitePrivList = new ArrayList<NameValueBean>();
		NameValueBean nmv;
		for (int counter = 0; counter < length1; counter++)
		{
			Element element = (Element) (nodeList1.item(counter));
			nmv = new NameValueBean(element.getAttribute("name"), element.getAttribute("id"));
			sitePrivList.add(nmv);
		}
		return sitePrivList;
	}

	/**
	 * For MSR changes.
	 * @return All Privileges.
	 */
	public static List getAllPrivileges()
	{
		List<NameValueBean> allPrivileges = new ArrayList<NameValueBean>();

		List<NameValueBean> list1 = Variables.privilegeGroupingMap.get("SITE");
		List<NameValueBean> list2 = Variables.privilegeGroupingMap.get("CP");
		List<NameValueBean> list3 = Variables.privilegeGroupingMap.get("SCIENTIST");
		List<NameValueBean> list4 = Variables.privilegeGroupingMap.get("GLOBAL");

		allPrivileges.addAll(list1);
		allPrivileges.addAll(list2);
		allPrivileges.addAll(list3);
		allPrivileges.addAll(list4);
		return allPrivileges;
	}

	/**
	 * This method returns records per page from session.
	 * @param session HttpSession
	 * @return no of records per page has been extracted.
	 */
	public static int getRecordsPerPage(HttpSession session)
	{
		int recordsPerPage;
		String recPerPageSessVal = (String) session.getAttribute(Constants.RESULTS_PER_PAGE);
		if (recPerPageSessVal == null)
		{
			recordsPerPage = Integer.parseInt(XMLPropertyHandler
					.getValue(Constants.RECORDS_PER_PAGE_PROPERTY_NAME));
			session.setAttribute(Constants.RESULTS_PER_PAGE, String.valueOf(recordsPerPage));
		}
		else
		{
			recordsPerPage = Integer.parseInt(recPerPageSessVal);
		}
		return recordsPerPage;
	}

	/**
	 * This method replace string.
	 * This method is used in AutoCompleteTag.java and SimpleQueryInterfaceForm.java(SimpleQuery)
	 * @param source source String
	 * @param toReplace toReplace String
	 * @param replacement replacement String
	 * @return String.
	 */
	public static String replaceAll(String source, String toReplace, String replacement)
	{
		String sourceString = source;
		if (sourceString.contains(toReplace))
		{
			sourceString = sourceString.replace(toReplace, replacement);
		}
		return sourceString;
	}

	/**
	 * Returns current thread's class loader.
	 * @return current thread's class loader.
	 */
	public static ClassLoader getCurrClassLoader()
	{
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * return the  actual class name.
	 * @param name String
	 * @return String
	 */
	public static String getActualClassName(String name)
	{
		String className = name;
		if (className != null && !className.trim().equals(TextConstants.EMPTY_STRING))
		{
			String splitter = "\\.";
			String[] arr = className.split(splitter);
			if (arr != null && arr.length != 0)
			{
				className = arr[arr.length - Constants.ONE];
			}
		}
		return className;
	}

	/**
	* @param tableName :
	* @param jdbcDAO :
	* @return :
	* @throws DAOException :
	*/
	public static String getDisplayName(String tableName, JDBCDAO jdbcDAO) throws DAOException
	{
		String displayName = "";
		String sql = "select DISPLAY_NAME from CATISSUE_QUERY_TABLE_DATA where TABLE_NAME='"
				+ tableName + "'";
		try
		{
			ResultSet resultSet = jdbcDAO.getQueryResultSet(sql);
			while (resultSet.next())
			{
				displayName = resultSet.getString("DISPLAY_NAME");
				break;
			}
			resultSet.close();
		}
		catch (Exception ex)
		{
			LOGGER.error(ex.getMessage(), ex);
		}
		return displayName;
	}

	/**
	 * This method returns method object.
	 * @param obj Object
	 * @param method method
	 * @return the method object.
	 * @throws IllegalAccessException Illegal Access Exception
	 * @throws InvocationTargetException Invocation Target Exception.
	 */
	public static Object getValueFor(Object obj, Method method) throws IllegalAccessException,
			InvocationTargetException
	{
		return method.invoke(obj, new Object[0]);

	}

	/**
	 * This method parse Attribute Name.
	 * @param methodName method Name to be parse.
	 * @return attribute Name.
	 */
	public static String parseAttributeName(String methodName)
	{
		StringBuffer attributeName = new StringBuffer();
		int index = methodName.indexOf("get");
		if (index != -1)
		{
			attributeName.append(methodName.substring(index + "get".length()));
		}
		attributeName.setCharAt(0, Character.toLowerCase(attributeName.charAt(0)));
		return attributeName.toString();
	}

	/**
	 * Generates error messages.
	 * @param exep :
	 * @return error message.
	 */
	public static String generateErrorMessage(Exception exep)
	{
		String messageToReturn = "";
		if (exep instanceof HibernateException)
		{
			HibernateException hibernateException = (HibernateException) exep;
			StringBuffer message = new StringBuffer(messageToReturn);
			String[] str = hibernateException.getMessages();
			if (str == null)
			{
				messageToReturn = "Unknown Error";
			}
			else
			{
				for (int i = 0; i < str.length; i++)
				{
					message.append(str[i]).append(' ');
				}
				messageToReturn = message.toString();
			}

		}
		else
		{
			messageToReturn = exep.getMessage();
		}
		return messageToReturn;
	}

	/**
	* Constants that will appear in HQL for retreiving Attributes of the Collection data type.
	*/
	private static final String ELEMENTS = "elements";

	/**
	 * Check whether the select Column start with "elements" & ends with ")" or not
	 * @param columnName The columnName
	 * @return true if the select Column start with "elements" & ends with ")" or not
	 */
	public static boolean isColumnNameContainsElements(String columnName)
	{
		columnName = columnName.toLowerCase().trim();
		return columnName.startsWith(ELEMENTS) && columnName.endsWith(")");
	}

}