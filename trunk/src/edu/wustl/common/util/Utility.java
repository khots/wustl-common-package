/*
 * Created on Aug 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.util;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;


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
}
