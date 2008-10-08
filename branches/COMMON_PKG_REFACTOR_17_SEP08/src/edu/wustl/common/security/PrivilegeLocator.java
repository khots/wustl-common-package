package edu.wustl.common.security;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
 * This classes load all privileges from xml file.
 * @author ravi_kumar
 */
public class PrivilegeLocator
{
	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(PrivilegeLocator.class);

	/**
	 * File name for privilege configuration.
	 */
	private static final String PRIV_CONF_FILE="PrivilegeConf.xml";

	/**
	 * Element name for 'privilege'.
	 */
	private static final String ELE_PRIVILEGE="privilege";

	/**
	 * Element name for 'bit-number'.
	 */
	private static final String ELE_BIT_NUM="bit-number";

	/**
	 * Element name for 'role-name'.
	 */
	private static final String ELE_ROLE_NAME="role-name";

	/**
	 * Attribute name for 'name'.
	 */
	private static final String ATTR_NAME="name";

	/**
	 * Map of privilege Name and Privilege Object.
	 */
	private Map<String,Privilege> namePrivMap;

	/**
	 * Map of bit-number and Privilege Object.
	 */
	private Map<Integer,Privilege> bitPrivMap;

	/**
	 * This method returns Privilege object by privilege name.
	 * @param name Privilege Name
	 * @return Privilege object.
	 */
	public Privilege getPrivilegeByName(String name)
	{
		return namePrivMap.get(name);
	}

	/**
	 * This method returns Privilege object by bit number.
	 * @param bit bit number associated with privilege.
	 * @return Privilege object.
	 */
	public Privilege getPrivilegeByBit(Integer bit)
	{
		return bitPrivMap.get(bit);
	}

	/**
	 * This method load the Privileges into map.
	 */
	public void init()
	{
		Document doc =getDocument(PRIV_CONF_FILE);
		NodeList privNodeLst = doc.getElementsByTagName(ELE_PRIVILEGE);
		populateMaps(privNodeLst);
	}

	/**
	 * @param privNodeLst this method populate xml data to maps.
	 */
	private void populateMaps(NodeList privNodeLst)
	{
		Node privNode;
		namePrivMap= new HashMap<String, Privilege>();
		bitPrivMap= new HashMap<Integer, Privilege>();
		for (int s = 0; s < privNodeLst.getLength(); s++)
		{
		    privNode = privNodeLst.item(s);
		    if (privNode.getNodeType() == Node.ELEMENT_NODE)
		    {
		    	addNewPrivilegeToMap(privNode);
		    }
		}
	}

	/**
	 * @param privNode Node- xml privilege node
	 */
	private void addNewPrivilegeToMap(Node privNode)
	{
	    String privName;
		int bitNumber;
		String roleName;
		Privilege privilege;
		Element privElmnt = (Element) privNode;
	    privName=privElmnt.getAttribute(ATTR_NAME);
	    bitNumber = Integer.parseInt(getElementValue(privElmnt,ELE_BIT_NUM));
	    roleName = getElementValue(privElmnt,ELE_ROLE_NAME);
	    privilege= new Privilege(privName,bitNumber,roleName);
	    namePrivMap.put(privName, privilege);
	    bitPrivMap.put(bitNumber,privilege);
	}

	/**
	 * This method return role name from xml file.
	 * @param privElmnt String- privilege Element
	 * @param elementName -Element name for which value has to be return
	 * @return String Role name
	 */
	private String getElementValue(Element privElmnt,String elementName)
	{
		String roleName;
		NodeList roleNmElmntLst = privElmnt.getElementsByTagName(elementName);
		Element roleNmElmnt = (Element) roleNmElmntLst.item(0);
		NodeList roleNm = roleNmElmnt.getChildNodes();
		roleName=((Node) roleNm.item(0)).getNodeValue();
		return roleName;
	}

	/**
	 * This method returns the Document object for xml file.
	 * @param fileName File name.
	 * @return Document xml document.
	 */
	private Document getDocument(String fileName)
	{
		Document doc=null;
		try
		{
			File file = new File(fileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			doc=documentBuilder.parse(file);
		}
		catch (ParserConfigurationException exception)
		{
			logger.error("DocumentBuilder cannot be created",exception);
		}
		catch (SAXException exception)
		{
			logger.error("Can not parse the xml file:"+fileName,exception);
		}
		catch (IOException exception)
		{
			logger.error("Can not parse the xml file:"+fileName,exception);
		}
		return doc;
	}
}