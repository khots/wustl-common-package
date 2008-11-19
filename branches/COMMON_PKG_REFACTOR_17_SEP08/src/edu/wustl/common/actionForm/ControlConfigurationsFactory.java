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
 * @author prashant_bandal
 *
 */
public final class ControlConfigurationsFactory
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(ControlConfigurationsFactory.class);

	/**
	 * Specifies ControlConfigurationsFactory instance.
	 */
	private static ControlConfigurationsFactory instance = new ControlConfigurationsFactory();

	/**
	 * Specifies dataType Configuration Map.
	 */
	private static Map<String, DataTypeConfigurationObject> dataTypeConfigurationMap;

	/**
	 * Specifies Document object.
	 */
	private static Document dom;

	/**
	 * ControlConfigurationsFactory constructor.
	 */
	private ControlConfigurationsFactory()
	{
		dataTypeConfigurationMap = new HashMap<String, DataTypeConfigurationObject>();

		parseXML("DataTypeConfigurations.xml");
	}

	/**
	 * This method gets ControlConfigurationsFactory Instance.
	 * @return ControlConfigurationsFactory instance.
	 */
	public static ControlConfigurationsFactory getInstance()
	{
		return instance;
	}

	/**
	 * This method parse xml File.
	 * @param xmlFile xml File
	 */
	private static void parseXML(String xmlFile)
	{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
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
	private static void parseDocument()
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
	 * @param validationDataTypeNode validation Data Type Node.
	 * @throws DOMException
	 */
	private static void insertInToMap(Node validationDataTypeNode)
	{
		Node dataTypeNameNode;
		Node dataTypeClassNode;
		NamedNodeMap dataTypeAttributes;
		String dataTypeName = null;
		DataTypeConfigurationObject dataTypeConfigurationObject = new DataTypeConfigurationObject();
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
	public ValidatorDataTypeInterface getValidatorDataType(String dataType)
	{
		DataTypeConfigurationObject dataTypeConfiguration =
			(DataTypeConfigurationObject) dataTypeConfigurationMap
				.get(dataType);
		ValidatorDataTypeInterface dataTypeInterface = null;
		Class dataTypeClass;
		try
		{
			dataTypeClass = Class.forName(dataTypeConfiguration.getDataTypeClassName());
			dataTypeInterface = (ValidatorDataTypeInterface) dataTypeClass.newInstance();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
		}
		return dataTypeInterface;
	}
}
