package edu.wustl.common.util.global;

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
 * It return class PG Name,PI GroupName or Coordinator GroupName.
 * @author ravi_kumar
 *
 */
public class CSMGroupLocator
{
	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(CSMGroupLocator.class);

	/**
	 * File name for CSM group configuration.
	 */
	private static final String CSM_GROUP_CONF_FILE="CSMGroup.xml";

	/**
	 * Map of bit-number and Privilege Object.
	 */
	private Map<Class,CSMGroup> classVsCsmGroupMap;

	/**
	 * Element name for 'csm-group'.
	 */
	private static final String ELE_CSM_GROUP="csm-group";

	/**
	 * Element name for 'pg-name'.
	 */
	private static final String ELE_PG_NAME="pg-name";

	/**
	 * Element name for 'pi-group-name'.
	 */
	private static final String ELE_PI_GROUP_NAME="pi-group-name";

	/**
	 * Element name for 'coordinator-group-name'.
	 */
	private static final String ELE_COORDINATOR_GROUP_NAME="coordinator-group-name";

	/**
	 * Attribute name for 'class-name'.
	 */
	private static final String ATTR_CLASS_NAME="class-name";

	/**
	 * @param identifier identifier
	 * @param className Class Name
	 * @return PG name
	 */
	public String getPGName(Long identifier,Class className)
	{
		return (String)classVsCsmGroupMap.get(className).getPgName();
	}

	/**
	 * @param identifier identifier
	 * @param className Class Name
	 * @return PI group Name
	 */
	public String getPIGroupName(Long identifier,Class className)
	{
		return (String)classVsCsmGroupMap.get(className).getPiGroupName();
	}

	/**
	 * @param identifier identifier
	 * @param className Class Name
	 * @return Coordinator Group Name
	 */
	public String getCoordinatorGroupName(Long identifier,Class className)
	{
		return (String)classVsCsmGroupMap.get(className).getCoGroupName();
	}

	/**
	 * This method load the Privileges into map.
	 * @throws ClassNotFoundException If Class not found
	 */
	public void init() throws ClassNotFoundException
	{
		Document doc =getDocument(CSM_GROUP_CONF_FILE);
		NodeList csmGrNodeLst = doc.getElementsByTagName(ELE_CSM_GROUP);
		populateMaps(csmGrNodeLst);
	}

	/**
	 * @param csmGrNodeLst this method populate xml data to maps.
	 * @throws ClassNotFoundException If Class not found
	 */
	private void populateMaps(NodeList csmGrNodeLst) throws ClassNotFoundException
	{
		Node csmGrNode;
		classVsCsmGroupMap= new HashMap<Class, CSMGroup>();
		for (int s = 0; s < csmGrNodeLst.getLength(); s++)
		{
			csmGrNode = csmGrNodeLst.item(s);
		    if (csmGrNode.getNodeType() == Node.ELEMENT_NODE)
		    {
		    	addNewCsmGroupToMap(csmGrNode);
		    }
		}
	}

	/**
	 * @param csmGrNode Node- xml privilege node
	 * @throws ClassNotFoundException If Class not found
	 */
	private void addNewCsmGroupToMap(Node csmGrNode) throws ClassNotFoundException
	{
	    String csmGrClassName;
		String pgName;
		String piGroupName;
		String coGroupName;
		CSMGroup csmGroup;
		Element csmGrElmnt = (Element) csmGrNode;
		csmGrClassName=csmGrElmnt.getAttribute(ATTR_CLASS_NAME);
		Class clazz=Class.forName(csmGrClassName);
		pgName = getElementValue(csmGrElmnt,ELE_PG_NAME);
		piGroupName = getElementValue(csmGrElmnt,ELE_PI_GROUP_NAME);
		coGroupName = getElementValue(csmGrElmnt,ELE_COORDINATOR_GROUP_NAME);
		csmGroup= new CSMGroup(pgName,piGroupName,coGroupName);
	    classVsCsmGroupMap.put(clazz, csmGroup);
	}

	/**
	 * This method return role name from xml file.
	 * @param element String- privilege Element
	 * @param elementName -Element name for which value has to be return
	 * @return String Role name
	 */
	private String getElementValue(Element element,String elementName)
	{
		String roleName;
		NodeList roleNmElmntLst = element.getElementsByTagName(elementName);
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
