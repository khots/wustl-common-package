/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/*
 * Created on Aug 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.util;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.wustl.common.tree.TreeNodeImpl;
import edu.wustl.common.util.dbManager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;


/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Utility
{
    /**
     * Parses the string format of date in the given format and returns the Data object.
     * @param date the string containing date.
     * @param pattern the pattern in which the date is present.
     * @return the string format of date in the given format and returns the Data object.
     * @throws ParseException
     */
	public static Date parseDate(String date,String pattern) throws ParseException
	{
		if(date!=null && !date.trim().equals(""))
		{
			try
			{
			    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			    Date dateObj = dateFormat.parse(date);
			    return dateObj;
			}
			catch(Exception e)
			{
				throw new ParseException("Date '"+date+"' is not in format of "+pattern,0);
			}
		}
		else
		{
			return null;
		}
	}
	/**
     * Parses the string format of date in the given format and returns the Data object.
     * @param date the string containing date.
     * @param pattern the pattern in which the date is present.
     * @return the string format of date in the given format and returns the Data object.
     * @throws ParseException
     */
	public static Date parseDate(String date) throws ParseException
	{
	    String pattern = datePattern(date);
	    
	    return parseDate(date, pattern);
	}
	
	public static String datePattern(String strDate)
	{
		String datePattern = "";
		String dtSep  = "";
		boolean result = true;
    	try
		{
    		Pattern re = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}", Pattern.CASE_INSENSITIVE);
    		Matcher  mat =re.matcher(strDate); 
    		result = mat.matches();
    		
    		if(result)
    			dtSep  = Constants.DATE_SEPARATOR; 
    		
    		// check for  / separator
    		if(!result)
    		{
        		re = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}", Pattern.CASE_INSENSITIVE);
        		mat =re.matcher(strDate); 
        		result = mat.matches();
        		//System.out.println("is Valid Date Pattern : / : "+result);
        		if(result)
        			dtSep  = Constants.DATE_SEPARATOR_SLASH; 
    		}
		}
    	catch(Exception exp)
		{
			Logger.out.error("Utility.datePattern() : exp : " + exp);
		}
    	if(dtSep.trim().length()>0)
    		datePattern = "MM"+dtSep+"dd"+dtSep+"yyyy";
    	
    	Logger.out.debug("datePattern returned : "+ datePattern  );
		return datePattern; 
	}
	
	public static String createAccessorMethodName(String attr,boolean isSetter)
	{
		String firstChar = attr.substring(0,1);
		String str = "get"; 
		if(isSetter)
			str = "set"; 
		return str + firstChar.toUpperCase() + attr.substring(1);
	}
	
	public static Object getValueFor(Object obj, String attrName) throws Exception
	{
		//Create the getter method of attribute
		String methodName =  Utility.createAccessorMethodName(attrName,false);
		Class objClass = obj.getClass();
		Method method = objClass.getMethod(methodName, new Class[0]);

		return method.invoke(obj,new Object[0]);
	}
	
	/**
	 * Start: Change for API Search   --- Jitendra 06/10/2006
	 * In Case of Api Search, previoulsy it was failing since there was default class level initialization 
	 * on domain object. For example in ParticipantMedicalIdentifier object, it was initialized as 
	 * protected Site site= new Site(); So we removed default class level initialization on domain object.
	 * Hence getValueFor() method was returning null. So write new method SetValueFor() which will 
	 * instantiate new Object and set it in parent object.
	 * @param obj
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public static Object SetValueFor(Object obj, String attrName) throws Exception
	{
		Object retObject = null;
		//create the setter method for the attribute.
		String methodName =  Utility.createAccessorMethodName(attrName,true);			
		Class objClass = obj.getClass();			
		Method method = findMethod(objClass,methodName);
		retObject = method.getParameterTypes()[0].newInstance();	
		Object objArr[] = {retObject};
		//set the newInstance to the setter nethod of parent obj
		method.invoke(obj,objArr);			
		return retObject;
	
	}	
	
	private static Method findMethod(Class objClass, String methodName) throws Exception
	{
		Method method[] = objClass.getMethods();
		for (int i = 0; i < method.length; i++) 
		{
			if(method[i].getName().equals(methodName))
				return method[i]; 
		}
		return null;
	}	
	
	
	public static Object getValueFor(Object obj, Method method) throws Exception
	{
		return method.invoke(obj,new Object[0]);
	}

    /**
     * @param objectIds
     * @return
     */
    public static String getArrayString(Object[] objectIds)
    {
        StringBuffer arrayStr = new StringBuffer();
        for(int i=0; i<objectIds.length; i++)
        {
            arrayStr.append(objectIds[i].toString()+",");
        }
        return arrayStr.toString();
    }
    
    public static Class getClassObject(String fullyQualifiedClassName)
    {
        Class className = null;
        try
        {
            className = Class.forName(fullyQualifiedClassName);
        }
        catch (ClassNotFoundException classNotExcp)
		{
			return null;
		}
        
        return className;
    }
    
    /**
     * Changes the format of the string compatible to Grid Format, 
     * removing escape characters and special characters from the string
     * @param obj - Unformatted obj to be printed in Grid Format
     * @return obj - Foratted obj to print in Grid Format
     */
    public static Object toGridFormat(Object obj)
    {
        if(obj instanceof String)
        {
            String objString=(String)obj;
            StringBuffer tokenedString=new StringBuffer();
            
            StringTokenizer tokenString=new StringTokenizer(objString,"\n\r\f"); 
            
            while(tokenString.hasMoreTokens())
            {
               tokenedString.append(tokenString.nextToken()+" ");
            }
            
            String gridFormattedStr=new String(tokenedString);
            
            obj=gridFormattedStr.replaceAll("\"","\\\\\"");
        }
 
        return obj;
    }
    
    public static boolean isNull(Object obj)
    {
    	if(obj == null)
    		return true;
    	else
    		return false;
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
            object = classObject.newInstance();
        }
        catch (InstantiationException instExp)
        {
        }
        catch (IllegalAccessException illAccExp)
        {
        }
        
        return object;
    }
    
    public static Object[] addElement(Object[] array, Object obj)
	{
		Object newObjectArr[] = new Object[array.length+1];
		
		if(array instanceof String[])
			newObjectArr = new String[array.length+1];
		
		for (int i = 0; i < array.length; i++)
		{
			newObjectArr[i] = array[i];
		}
		newObjectArr[newObjectArr.length-1] = obj;
		return newObjectArr;
	}
    public static String parseAttributeName(String methodName) throws Exception
	{
		String attributeName = "";
		int index = methodName.indexOf("get");
		if(index!=-1)
		{
			attributeName = methodName.substring(index+"get".length());
		}
		
		String firstChar = (attributeName.charAt(0)+"").toLowerCase();
		attributeName = firstChar + attributeName.substring(1);
		
		Logger.out.debug("attributeName <"+attributeName+">");
		
		return attributeName;
	}
	
    
    /**
	 * @param list
	 * @return
	 */
	public static List removeNull(List list) {
		List nullFreeList = new ArrayList();
		for(int i=0; i< list.size(); i++)
		{
			if(list.get(i)!= null)
			{
				nullFreeList.add(list.get(i));
			}
		}
		return nullFreeList;
	}
		
	/**
     * Parses the fully qualified classname and returns only the classname.
     * @param fullyQualifiedName The fully qualified classname. 
     * @return The classname.
     */
    public static String parseClassName(String fullyQualifiedName)
    {
        try
        {
            return fullyQualifiedName.substring(fullyQualifiedName
                    .lastIndexOf(".") + 1);
        }
        catch (Exception e)
        {
            return fullyQualifiedName;
        }
    }
    
    /**
     * Returns name of FormBean specified in struts-config.xml for passed Object of FormBean
     * @param obj - FormBean object 
     * @return String - name of FormBean object
     */
    public static String getFormBeanName(Object obj)
    {
        String objClassName=obj.getClass().toString();
        
        objClassName=objClassName.substring((objClassName.lastIndexOf(".")+1),(objClassName.length()));
    
        Logger.out.debug("ClassName in getFormBean()---------->"+objClassName);
        
        String classNameFirstCharacter=objClassName.substring(0,1);
        
        Logger.out.debug("FirstCharacter of ClassName-------------->"+classNameFirstCharacter);
        
        String formBeanName=classNameFirstCharacter.toLowerCase()+objClassName.substring(1,(objClassName.length()));
        
        Logger.out.debug("FormBeanName in getFormBean()--------------->"+formBeanName);
        
        return formBeanName;
    }
    
    /**
	 * Parses the Date in given format and returns the string representation.
	 * @param date the Date to be parsed.
	 * @param pattern the pattern of the date.
	 * @return
	 */
	public static String parseDateToString(Date date, String pattern)
	{
	    String d = "";
	    //TODO Check for null
	    if(date!=null)
	    {
		    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			d = dateFormat.format(date);
	    }
	    return d;
	}
	
	public static String toString(Object obj)
	{
		if(obj == null)
			return "";
		
		return obj.toString();
	}
	
	public static String[] getTime(Date date)
	{
		String []time =new String[2];
		Calendar cal = Calendar.getInstance();
 		cal.setTime(date);
 		time[0]= Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
 		time[1]= Integer.toString(cal.get(Calendar.MINUTE));
 		return time;
	}
	
	public static Long[] toLongArray(Collection collection)
	{
		Logger.out.debug(collection.toArray().getClass().getName());
		
		Long obj[] = new Long[collection.size()];
		
		int index = 0;
		Iterator it = collection.iterator();
		while(it.hasNext())
		{
			obj[index] = (Long)it.next();
			Logger.out.debug("obj[index] "+obj[index].getClass().getName());
			index++;
		}
		return obj;
	}
	
	public static int toInt(Object obj)
	{
		int value=0;
		if(obj == null)
			return value;
		else
		{	Integer intObj = (Integer)obj;
			value=intObj.intValue() ;
			return value;
		}
	}
	
	public static double toDouble(Object obj)
	{
		double value=0;
		if(obj == null)
			return value;
		else
		{	Double dblObj = (Double)obj;
			value=dblObj.doubleValue() ;
			return value;
		}
	}
	
	/**
	 * checking whether key's value is persisted or not
	 *
	 */
	public static boolean isPersistedValue(Map map,String key){
		Object obj = map.get(key);
		String val=null;
		if (obj!=null) 
		{
			val = obj.toString();
		}
		if((val!= null && !(val.equals("0"))) && !(val.equals("")))
			return true;
		else 
			return false; 
			
	}
	
//	Mandar 17-Apr-06 Bugid : 1667 :- URL is incomplete displays /.
	/**
	 * @param requestURL URL generated from the request.
	 * Sets the application URL in the Variables class after generating it in proper format.
	 */
	public static void setApplicationURL(String requestURL)
	{
	    Logger.out.debug("17-Apr-06 : requestURL : "+ requestURL);
	    // Mandar : 17-Apr-06 : 1667 : caTissuecore Application URL is displayed as "/"
	    String ourUrl="";
	    try
		{
			URL aURL = new URL(requestURL);			
			ourUrl = aURL.getProtocol()+"://"+aURL.getAuthority()+aURL.getPath();
			ourUrl = ourUrl.substring(0,ourUrl.lastIndexOf("/"));
			Logger.out.debug("Application URL Generated : "+ourUrl);
		}
	    catch(MalformedURLException urlExp)
		{
	    	Logger.out.error(urlExp.getMessage(),urlExp  );
		}	    
	    if (Variables.catissueURL != null && Variables.catissueURL.trim().length() == 0 )
	    {
	        Variables.catissueURL = ourUrl;
	        Logger.out.debug("Application URL set: "+ Variables.catissueURL );
	    }
	}//setApplicationURL()
	
	/**
     * @param selectedMenuID Menu that is clicked
     * @param currentMenuID Menu that is being checked
     * @param normalMenuClass style class for normal menu
     * @param selectedMenuClass style class for selected menu 
     * @param menuHoverClass  style class for hover effect
     * @return The String generated for the TD tag. Creates the selected menu or normal menu.
     */
    public static String setSelectedMenuItem(int selectedMenuID, int currentMenuID, String normalMenuClass , String selectedMenuClass , String menuHoverClass)
    {
    	String returnStr = "";
    	if(selectedMenuID == currentMenuID)
    	{
    		returnStr ="<td class=\"" + selectedMenuClass + "\" onmouseover=\"changeMenuStyle(this,\'" + selectedMenuClass + "\')\" onmouseout=\"changeMenuStyle(this,\'" + selectedMenuClass + "\')\">";
    	}
    	else
    	{
    		returnStr ="<td class=\"" + normalMenuClass + "\" onmouseover=\"changeMenuStyle(this,\'" + menuHoverClass + "\')\" onmouseout=\"changeMenuStyle(this,\'" + normalMenuClass + "\')\">";
    	}
    	 
    	return returnStr;
    }
    
	/**
	 * @param str String to be converted to Proper case.
	 * @return The String in Proper case.
	 */
	public static String initCap(String str)
	{
		String retStr="";
		if(str!=null && str.trim().length() >0 )
		{
			String firstCharacter = str.substring(0,1 );
			String otherData = str.substring(1 );
			retStr = firstCharacter.toUpperCase()+otherData.toLowerCase();
		}
		else
		{
			Logger.out.debug("Utility.initCap : - String provided is either empty or null" + str );
		}
		
		return retStr;
	}
	
	/**
	 * This method is used in JSP pages to get the width of columns for the html fields. 
	 * It acts as a wrapper for the HibernateMetaData getColumnWidth() method.
	 * @param className Class name of the field
	 * @param attributeName Attribute name of the field.
	 * @return Length of the column. 
	 * @see HibernateMetaData.getColumnWidth()  
	 */
	public static String getColumnWidth(Class className, String attributeName)
	{
		String columnLength = toString(new Integer((HibernateMetaData.getColumnWidth(className,attributeName ))));
		Logger.out.debug(className.getName()+ " : "+ attributeName  + " : " + columnLength ); 
		return columnLength;
	}
	//Mandar 17-Apr-06 Bugid : 1667 : end
	
	/**
	 * To sort the Tree nodes based on the comparators overidden by the TreeNodeImpl object.
	 * @param nodes reference to the Vector containing object of class implementing TreeNodeImpl class.
	 */
	public static void sortTreeVector(Vector nodes)
	{
		Collections.sort(nodes);
		for (int i=0;i<nodes.size();i++)
		{
			TreeNodeImpl child = (TreeNodeImpl)nodes.get(i);
			sortTreeVector(child.getChildNodes());
		}
	}
}