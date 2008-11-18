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
	 * @throws SAXException sax related exception.
	 * @throws IOException I/O exception.
	 * @throws ParserConfigurationException configuration exception at the time of parsing.
	 */
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
	 * @param propertyName String name of property.
	 * @return String value of property.
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
	 * @param propertyName String name of property.
	 * @param child Node child node.
	 * @return String name of child node.
	 */
	private static String extractValue(String propertyName, Node child)
	{
		NodeList subChildNodes = child.getChildNodes();
		String value=null;
		for (int j = 0; j < subChildNodes.getLength(); j++)
		{
			Node subchildNode = subChildNodes.item(j);
			String subNodeName = subchildNode.getNodeName();
			boolean isNameFound = isNameFound(propertyName, subchildNode, subNodeName);
			value = getNodeValue(isNameFound,subchildNode, subNodeName);
		}
		return value;
	}

	/**
	 * @param isNameFound if name tag found.
	 * @param subchildNode Child Node object.
	 * @param subNodeName sub node value.
	 * @return node value if name tag already found or null.
	 */
	private static String getNodeValue(boolean isNameFound,Node subchildNode,String subNodeName)
	{
		String value=null;
		if (isNameFound && "value".equals(subNodeName))
		{
			String pValue = "";
			if (subchildNode != null && subchildNode.getFirstChild() != null)
			{
				pValue = (String) subchildNode.getFirstChild().getNodeValue();
			}
			value=pValue;
		}
		return value;
	}

	/**
	 * @param propertyName Property Name.
	 * @param subchildNode child Node object.
	 * @param subNodeName  sub node value.
	 * @return true if name tag found else false.
	 */
	private static boolean isNameFound(String propertyName,Node subchildNode,String subNodeName)
	{
		boolean isNameFound=false;
		if ("name".equals(subNodeName))
		{
			String pName = (String) subchildNode.getFirstChild().getNodeValue();
			if (propertyName.equals(pName))
			{
				isNameFound = true;
			}
		}
		return isNameFound;
	}
}