/**
 * <p>Title:XMLPropertyHandler Class>
 * <p>Description:This class parses from caTissue_Properties.xml(includes properties name & value pairs)
 * file using DOM parser.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Tapan Sahoo
 * @version 1.00
 * Created on May 15, 2006
 */

package edu.wustl.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exception.ParseException;
import edu.wustl.common.util.global.XMLParserUtility;
import edu.wustl.common.util.logger.Logger;

/**
 * This class gives the properties value by giving properties name.
 *
 * @author tapan_sahoo
 */
public class XMLPropertyHandler
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(XMLPropertyHandler.class);
	/**
	 * document Document document containing information about xml.
	 */
	private static Document document = null;

	/**
	 * @param path String path for logger information.
	 * @throws ParseException throws this exception if
	 * specified xml file not found or not able to parse the file.
	 */
	public static void init(String path) throws ParseException
	{
		logger.info("path" + path);
		try
		{
			document=XMLParserUtility.getDocument(path);
		}
		catch (Exception ioe)
		{
			logger.error(ioe.getMessage(), ioe);
			ErrorKey errorKey = null;
			throw new ParseException(errorKey,ioe,"");
		}
	}
	/**
	 * <p>
	 * Description:This method takes the property name as String argument and
	 * returns the properties value as String. Put the xml file in the path as
	 * you will provide the path
	 * </p>
	 * @param propertyName String name of property.
	 * @return String value of property.
	 */
	public static String getValue(String propertyName)
	{
		String value="";
		Element docEle = document.getDocumentElement();
		NodeList propNodeList= docEle.getElementsByTagName("property");
		for (int i = 0; i < propNodeList.getLength(); i++)
		{
			Element propElement = (Element)propNodeList.item(i);
			String name = getTextValue(propElement,"name");
			if(name.equals(propertyName))
			{
				value= getTextValue(propElement,"value");
				break;
			}
		}
		return value;
	}

	/**
	 * This class return the value of Element.
	 * e.g if Element for following property tag is passed
	 * <property>
	 *		<name>server.port</name>
	 *		<value>8080</value>
	 *	</property>
	 * then it will return server.port or 8080 depends on second argument.
	 * @param elements Element object
	 * @param tagName tag name.
	 * @return value of element passed.
	 */
	private static String getTextValue(Element elements, String tagName)
	{
		String textVal = "";
		NodeList nodeList = elements.getElementsByTagName(tagName);
		if(nodeList != null && nodeList.getLength() > 0)
		{
			Element element = (Element)nodeList.item(0);
			if(null!=element.getFirstChild())
			{
				textVal = element.getFirstChild().getNodeValue();
			}
		}

		return textVal;
	}
}