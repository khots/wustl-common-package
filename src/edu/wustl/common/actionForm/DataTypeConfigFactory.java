/**
 *
 */

package edu.wustl.common.actionForm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wustl.common.util.logger.Logger;

/**
 * 
 * @author prashant_bandal
 *
 */
public final class DataTypeConfigFactory
{

	/**
	 * logger Logger - Generic logger.
	 */
	private final static org.apache.log4j.Logger logger = Logger.getLogger(DataTypeConfigFactory.class);

	/**
	 * Specifies ControlConfigurationsFactory instance.
	 */
	private static DataTypeConfigFactory dataTypeConfig = new DataTypeConfigFactory();

	/**
	 * Specifies dataType Configuration Map.
	 */
	private Map<String, DataTypeConfigObject> dataTypeConfigurationMap;

	/**
	 * Specifies Document object.
	 */
	private Document dom;

	/**
	 * ControlConfigurationsFactory constructor.
	 */
	private DataTypeConfigFactory()
	{
		dataTypeConfigurationMap = new HashMap<String, DataTypeConfigObject>();

		parseXML("DataTypeConfigurations.xml");

	}

	/**
	 * This method gets ControlConfigurationsFactory Instance.
	 * @return ControlConfigurationsFactory instance.
	 */
	public static DataTypeConfigFactory getInstance()
	{
		return dataTypeConfig;
	}

	/**
	 * This method parse xml File.
	 * @param xmlFile xml File
	 */
	private void parseXML(String xmlFile)
	{

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

			dom = documentBuilder.parse(xmlFile);
			parseDocument();
		}
		catch (ParserConfigurationException pce)
		{
			logger.error(pce.getMessage(), pce);
		}
		catch (SAXException se)
		{
			logger.error(se.getMessage(), se);
		}
		catch (IOException ioe)
		{
			logger.error(ioe.getMessage(), ioe);
		}
	}

	/**
	 * This method parse document.
	 */
	private void parseDocument()
	{
		//get the root elememt
		Element root = dom.getDocumentElement();

		NodeList validationDataTypeList = root.getElementsByTagName("DataType");
		//int noOfDataTypes = validationDataTypeList.getLength();
		Node validationDataTypeNode = null;

		//String dataTypeName = null;
		for (int i = 0; i < validationDataTypeList.getLength(); i++)
		{
			validationDataTypeNode = validationDataTypeList.item(i);
			if (validationDataTypeNode != null)
			{
				insertInToMap(validationDataTypeNode);
			}
		}
	}

	/**
	 * 
	 * @param validationDataTypeNode validation Data Type Node.
	 * @throws DOMException
	 */
	private void insertInToMap(Node validationDataTypeNode)
	{
		Node dataTypeNameNode;
		Node dataTypeClassNode;
		NamedNodeMap dataTypeAttributes;
		String dataTypeName = null;
		DataTypeConfigObject dataTypeConfigurationObject = new DataTypeConfigObject();
		dataTypeAttributes = validationDataTypeNode.getAttributes();
		if (dataTypeAttributes != null)
		{
			dataTypeNameNode = dataTypeAttributes.getNamedItem("name");
			dataTypeClassNode = dataTypeAttributes.getNamedItem("className");
			if (dataTypeNameNode != null && dataTypeClassNode != null)
			{
				dataTypeName = dataTypeNameNode.getNodeValue();
				dataTypeConfigurationObject.setDataTypeName(dataTypeName);
				dataTypeConfigurationObject.setDataTypeClassName(dataTypeClassNode.getNodeValue());
			}
		}
		dataTypeConfigurationMap.put(dataTypeName, dataTypeConfigurationObject);
	}

	/**
	 * This method gets Validator DataType object.
	 * @param dataType data Type
	 * @return dataTypeInterface
	 */
	public IDBDataType getValidatorDataType(String dataType)
	{
		try
		{

			DataTypeConfigObject dataTypeConfig = dataTypeConfigurationMap.get(dataType);
			String className = dataTypeConfig.getClassName();
			Class<IDBDataType> dataTypeClass = (Class<IDBDataType>)Class.forName(className);
			return dataTypeClass.newInstance();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
		}
		
	}
}
