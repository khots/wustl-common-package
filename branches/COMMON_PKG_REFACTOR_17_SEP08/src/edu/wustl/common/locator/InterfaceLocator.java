package edu.wustl.common.locator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wustl.common.exception.ParseException;
import edu.wustl.common.util.global.XMLParserUtility;
import edu.wustl.common.util.logger.Logger;

/**
 * Locator for common interfaces.
 * @author deepti_shelar
 *
 */
public final class InterfaceLocator
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(InterfaceLocator.class);
	/**
	 * File name for interface configuration.
	 */
	private static final String INTERFACE_CONF_FILE="CommonInterfaces.xml";

	/**
	 * Element name for 'interface'.
	 */
	private static final String ELE_INTERFACE="interface";

	/**
	 * Attribute name for interface name.
	 */
	private static final String ATTR_NAME="name";

	/**
	 * Attribute name for className.
	 */
	private static final String ATTR_CLASS_NAME="className";

	/**
	 * Specifies interface Map.
	 */
	private Map<String,String> interfaceMap ;
	/**
	 * private instance will be created when the class is loaded.
	 */
	private static InterfaceLocator locator = new InterfaceLocator();

	/**
	 * Specifies success.
	 */
	private static boolean success = false;
	/**
	 * Making this class singleton.
	 */
	private InterfaceLocator()
	{
		try
		{
			init();
			success = true;
		}
		catch (ParseException exception)
		{
			logger.error(exception.getMessage(), exception);
		}
	}
	/**
	 * returning the same instance every time.
	 * @return InterfaceLocator
	 * @throws ParseException ParseException.
	 */
	public static InterfaceLocator getInstance() throws ParseException
	{
		if (success)
		{
			return locator;
		}
		else
		{
			throw new ParseException(null,null,"");
		}
	}
	/**
	 * get Class Name For Interface.
	 * @param interfaceName interface Name.
	 * @return ClassNameForInterface
	 */
	public String getClassNameForInterface(String interfaceName)
	{
		return interfaceMap.get(interfaceName);
	}
	/**
	 * This method load the interfaces into map.
	 * @throws ParseException Parse Exception.
	 */
	private void init() throws ParseException
	{
		Document doc;
		try
		{
			doc = XMLParserUtility.getDocument(INTERFACE_CONF_FILE);
		}
		catch (ParserConfigurationException exception)
		{
			throw new ParseException(null,exception,"");
		}
		catch (SAXException sException)
		{
			throw new ParseException(null,sException,"");
		}
		catch (IOException iOException)
		{
			throw new ParseException(null,iOException,"");
		}
		NodeList interfacesList = doc.getElementsByTagName(ELE_INTERFACE);
		populateMaps(interfacesList);
	}
	/**
	 * @param interfacesList this method populate xml data to maps.
	 */
	private void populateMaps(NodeList interfacesList)
	{
		Node interfaceNode;
		interfaceMap= new HashMap<String, String>();
		for (int s = 0; s < interfacesList.getLength(); s++)
		{
			interfaceNode = interfacesList.item(s);
			if (interfaceNode.getNodeType() == Node.ELEMENT_NODE)
			{
				addNewInterfaceToMap(interfaceNode);
			}
		}
	}
	/**
	 * @param interfaceNode Node- xml interface node
	 */
	private void addNewInterfaceToMap(Node interfaceNode)
	{
		String name;
		String className;
		Element interfaceElem = (Element) interfaceNode;
		name = interfaceElem.getAttribute(ATTR_NAME);
		className = interfaceElem.getAttribute(ATTR_CLASS_NAME);
		interfaceMap.put(name,className);
	}
}