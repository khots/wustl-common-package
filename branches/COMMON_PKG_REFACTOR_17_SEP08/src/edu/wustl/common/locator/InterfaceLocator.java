package edu.wustl.common.locator;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.common.util.global.XMLParserUtility;

/**
 * Locator for common interfaces
 * @author deepti_shelar
 *
 */
public class InterfaceLocator
{

	/**
	 * File name for interface configuration.
	 */
	private static final String INTERFACE_CONF_FILE="CommonInterfaces.xml";

	/**
	 * Element name for 'interface'.
	 */
	private static final String ELE_INTERFACE="interface";

	/**
	 * Attribute name for interface name
	 */
	private static final String ATTR_NAME="name";

	/**
	 * Attribute name for className
	 */
	private static final String ATTR_CLASS_NAME="className";


	private Map<String,String> interfaceMap ;
	/**
	 * private instance will be created when the class is loaded.
	 */
	private static InterfaceLocator locator = new InterfaceLocator();
	/**
	 * Making this class singleton
	 */
	private InterfaceLocator()
	{
		init();
	}
	/**
	 * returning the same instance every time
	 * @return InterfaceLocator
	 */
	public static InterfaceLocator getInstance()
	{
		return locator;
	}
	/**
	 * 
	 * @param interfaceName
	 * @return ClassNameForInterface
	 */
	public String getClassNameForInterface(String interfaceName)
	{
		return interfaceMap.get(interfaceName);
	}
	/**
	 * This method load the interfaces into map.
	 */
	private void init()
	{
		Document doc =XMLParserUtility.getDocument(INTERFACE_CONF_FILE);
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