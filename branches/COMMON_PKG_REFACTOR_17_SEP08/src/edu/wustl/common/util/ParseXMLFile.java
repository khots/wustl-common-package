/**
 *<p>Copyright: (c) Washington University, School of Medicine 2006.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: edu.wustl.cab2b.client.ui.main.ParseXMLFile </p>
 */

package edu.wustl.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.global.XMLParserUtility;

/**
 * This class is used for parsing the XML file and put the parsed elements to HashMaps.
 * @author Kaushal Kumar
 * @version 1.0
 */

public class ParseXMLFile
{


	/**
	 * XML file (to be parsed) with complete path.
	 */
	/* private final String xmlFileName = "conf/dynamicUI.xml";*/

	/**
	 * Variables declared by Pratibha
	 */
	/**
	 * non enum data type to condition map.
	 */
	private Map<String, ArrayList<String>> nedtCondMap = new HashMap<String, ArrayList<String>>();
	/**
	 * non enum data type to component map.
	 */
	private Map<String, String> nedtCompMap = new HashMap<String, String>();

	/**
	 * enum data type to condition map.
	 */
	private Map<String, ArrayList<String>> edtCondMap = new HashMap<String, ArrayList<String>>();
	/**
	 * enum data type to component map.
	 */
	private Map<String, String> edtompMap = new HashMap<String, String>();

	/**
	 * Constructor for parsing the XML file.
	 * @param path String path of file.
	 * @exception CheckedException checked exception.
	 */
	protected ParseXMLFile(String path)
	{
		Document doc = XMLParserUtility.getDocument(path);
		Node root = doc.getDocumentElement();
		readDynamicUIComponents(root);
	}

	/**
	 * @param path of the file.
	 * @return instance of ParseXMLFile.
	 * @throws CheckedException checked exception.
	 */
	public static ParseXMLFile getInstance(String path) throws CheckedException
	{
		return new ParseXMLFile(path);
	}

	/**
	 * This method return the child node of any particular node.
	 * @param elem node
	 * @return element value
	 */
	private String getElementValue(Node elem)
	{
		Node child;
		String eleValue=TextConstants.EMPTY_STRING;
		if (elem != null)
		{
			if (elem.hasChildNodes())
			{
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling())
				{
					if (child.getNodeType() == Node.TEXT_NODE)
					{
						eleValue=child.getNodeValue();
					}
				}
			}
		}
		return eleValue;
	}

	/**
	 * Method to read dynamic UI component details from xml and populate UI accordingly.
	 * @param node root node to parse.
	 */
	private void readDynamicUIComponents(Node node)
	{

		// This node list will return two children.
		// 1. non-enumerated
		// 2. enumerated
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			// Check if node is non-enumerated
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				if (child.getNodeName().equalsIgnoreCase("non-enumerated"))
				{
					/* Get all its children nodes and store corresponding details into the
					appropriate data-structures.*/
					NodeList dataTypeNodes = child.getChildNodes();
					readDataTypeDetailsForAllNode(dataTypeNodes, nedtCondMap,nedtCompMap);
				}
				else
				{
					/*Get all its children nodes and store corresponding details into the
					appropriate data-structures.*/
					NodeList dataTypeNodes = child.getChildNodes();
					readDataTypeDetailsForAllNode(dataTypeNodes, edtCondMap,edtompMap);
				}
			}
		}
	}

	/**
	 * Method to get condition list and component associated with it.
	 * @param dataTypeNodes NodeList data type of node.
	 * @param dataTypeToCondMap Map of type ArrayList.
	 * @param dataTypeToCompMap Map of type String.
	 */
	private void readDataTypeDetailsForAllNode(NodeList dataTypeNodes,
			Map<String, ArrayList<String>> dataTypeToCondMap,
			Map<String, String> dataTypeToCompMap)
	{
		for (int i = 0; i < dataTypeNodes.getLength(); i++)
		{
			Node dataTypeNode = dataTypeNodes.item(i);
			if (dataTypeNode.getNodeType() == Node.ELEMENT_NODE)
			{
				// Node name like String, number, date, boolean
				String nodeType = dataTypeNode.getNodeName();
				// Get all the nodes under nodeType
				NodeList childNodes = dataTypeNode.getChildNodes();
				for (int childCnt = 0; childCnt < childNodes.getLength(); childCnt++)
				{
					// Read the condition node and populate the condition list
					Node node = childNodes.item(childCnt);
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						if (node.getNodeName().equalsIgnoreCase("conditions"))
						{
							ArrayList<String> conditionList = new ArrayList<String>();
							NodeList nodeConditions = node.getChildNodes();
							for (int conditionCnt = 0; conditionCnt < nodeConditions.getLength(); conditionCnt++)
							{
								Node conditionNode = nodeConditions.item(conditionCnt);
								if (conditionNode.getNodeType() == Node.ELEMENT_NODE)
								{
									NodeList displayNodes = conditionNode.getChildNodes();
									for (int j = 0; j < displayNodes.getLength(); j++)
									{
										if (displayNodes.item(j).getNodeType() == Node.ELEMENT_NODE)
										{
											conditionList.add(getElementValue(displayNodes.item(j)));
											break;
										}
									}
								}
							}
							dataTypeToCondMap.put(nodeType, conditionList);
						}
						// Read component details
						else if (node.getNodeName().equalsIgnoreCase("components"))
						{
							dataTypeToCompMap.put(nodeType, getElementValue(node));
						}
					}
				}
			}
		}
	}

	/**
	 * @param dataTypeString String data type.
	 * @return non enumerated class name.
	 */
	public String getNonEnumClassName(String dataTypeString)
	{
		return nedtCompMap.get(dataTypeString);
	}

	/**
	 * This method returns the enumerated class name.
	 * @param dataTypeString String data type.
	 * @return String class name.
	 */
	public String getEnumClassName(String dataTypeString)
	{
		return edtompMap.get(dataTypeString);
	}

	/**
	 * @param dataTypeString String data type.
	 * @return list of enumerated conditions.
	 */
	public List<String> getEnumConditionList(String dataTypeString)
	{
		return edtCondMap.get(dataTypeString);
	}

	/**
	 * @param dataTypeString String data type.
	 * @return list of non enumerated conditions.
	 */
	public List<String> getNonEnumConditionList(String dataTypeString)
	{
		return nedtCondMap.get(dataTypeString);
	}

}