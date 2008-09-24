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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wustl.common.util.logger.Logger;

/**
 * This class gives the properties value by giving properties name.
 * 
 * @author tapan_sahoo
 */
public class XMLPropertyHandler
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(XMLPropertyHandler.class);
	private static Document document = null;
	
	public static void init(String path) throws SAXException,IOException,ParserConfigurationException
	{
		logger.info("path" + path);
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
			if (path != null)
			{
				document = dbuilder.parse(path);
			}
		}
		catch (SAXException e)
		{
			logger.error(e.getMessage(), e);
			throw e;
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			throw e;
		}
		catch (ParserConfigurationException e)
		{
			logger.error("Could not locate a JAXP parser: " + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * <p>
	 * Description:This method takes the property name as String argument and
	 * returns the properties value as String. Put the xml file in the path as
	 * you will provide the path
	 * </p>
	 */

	public static String getValue(String propertyName)
	{
		String value=null;
		Element root = document.getDocumentElement();
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child instanceof Element)
			{
				value = extractValue(propertyName, child);
			}
		}
		return value;
	}

	/**
	 * This method extract value from a child.
	 * @param propertyName
	 * @param value
	 * @param child
	 * @return
	 */
	private static String extractValue(String propertyName, Node child)
	{
		NodeList subChildNodes = child.getChildNodes();
		boolean isNameFound = false;
		String value=null;
		for (int j = 0; j < subChildNodes.getLength(); j++)
		{
			Node subchildNode = subChildNodes.item(j);
			String subNodeName = subchildNode.getNodeName();
			if ("name".equals(subNodeName))
			{
				String pName = (String) subchildNode.getFirstChild().getNodeValue();
				if (propertyName.equals(pName))
				{
					isNameFound = true;
				}
			}

			if (isNameFound && "value".equals(subNodeName))
			{
				String pValue = "";
				if (subchildNode != null && subchildNode.getFirstChild() != null)
				{
					pValue = (String) subchildNode.getFirstChild().getNodeValue();
				}
				value=pValue;
			}
		}
		return value;
	}
}