/**
 * <p>Title:XMLPropertyHandler Class>
 * <p>Description:This class parses from caTissue_Properties.xml(includes properties name & value pairs) file using DOM parser.</p>
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

	private static Document document = null;

//	public static void main(String[] args) 
//	{
//		XMLPropertyHandler ph = new XMLPropertyHandler();
//
//		String propertyValue1 = ph.getValue("server.port");
//		System.out.println(""+propertyValue1);
//		String propertyValue2 = ph.getValue("casdr.server");
//		System.out.println(""+propertyValue2);
//
//	}

	public static void init() throws Exception
	{
		String path = System.getProperty("app.propertiesFile");
		Logger.out.debug("path.........................."+path);
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();// throws
			// ParserConfigurationException
			if (path != null)
			{
				document = dbuilder.parse(path);
				// throws SAXException,IOException,IllegalArgumentException(if path is null
			}
		}
		catch (SAXException e)
		{
			Logger.out.error(e.getMessage(),e);
			throw e;
		}
		catch (IOException e)
		{
			Logger.out.error(e.getMessage(),e);
			throw e;
		}
		catch (ParserConfigurationException e)
		{
			Logger.out.error("Could not locate a JAXP parser: "+e.getMessage(),e);
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
		// it gives the rootNode of the xml file
		Element root = document.getDocumentElement();

		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);

			if (child instanceof Element)
			{
				// it gives the subchild nodes in the xml file(name & value)
				NodeList subChildNodes = child.getChildNodes();

				//System.out.println("subchildNodes : "+subChildNodes.getLength()); 
				for (int j = 0; j < subChildNodes.getLength(); j++)
				{
					Node subchildNode = subChildNodes.item(j);
					String subNodeName = subchildNode.getNodeName();
					//System.out.println("subnodeName : "+subnodeName);
					if (subNodeName.equals("name"))
					{
						String pName = (String) subchildNode.getFirstChild().getNodeValue();
						if (propertyName.equals(pName))
						{
							if (subNodeName.equals("value"))
							{
								String pValue = (String) subchildNode.getFirstChild()
										.getNodeValue();
								return pValue;
							}
						}
					}

				}
			}
		}
		return null;
	}
}