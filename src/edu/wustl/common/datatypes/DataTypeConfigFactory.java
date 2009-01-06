/**
 *
 */

package edu.wustl.common.datatypes;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exception.ParseException;
import edu.wustl.common.util.global.XMLParserUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.daofactory.ApplicationDAOPropertiesParser;

/**
 * This class configure factory for data types.
 * @author prashant_bandal
 *
 */
public final class DataTypeConfigFactory
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DataTypeConfigFactory.class);

	/**
	 * Specifies parse Exception Message.
	 */
	private static String parseExcepMessage;

	/**
	 * Specifies success.
	 */
	private static boolean success = false;

	/**
	 * Specifies ControlConfigurationsFactory instance.
	 */
	private static DataTypeConfigFactory dataTypeConfig = new DataTypeConfigFactory();

	/**
	 * Specifies dataType Configuration Map.
	 */
	private final transient Map<String, DataTypeConfigObject> dataTypeConfigurationMap;

	/**
	 * Specifies Document object.
	 */
	private transient Document dom;

	/**
	 * ControlConfigurationsFactory constructor.
	 */
	private DataTypeConfigFactory()
	{
		dataTypeConfigurationMap = new HashMap<String, DataTypeConfigObject>();

		try
		{
			parseXML("DataTypeConfigurations.xml");
			success = true;
		}
		catch (ParseException exception)
		{
			parseExcepMessage = exception.getMessage();
			logger.error(parseExcepMessage, exception);
		}

	}

	/**
	 * This method gets ControlConfigurationsFactory Instance.
	 * @return ControlConfigurationsFactory instance.
	 * @throws ApplicationException Application Exception.
	 */
	public static DataTypeConfigFactory getInstance() throws ApplicationException
	{
		if (success)
		{
			return dataTypeConfig;
		}
		else
		{
			throw new ApplicationException(ErrorKey.getErrorKey("datatype.parse.error"), null,
					parseExcepMessage);
		}
	}

	/**
	 * This method parse xml File for data type.
	 * @param xmlFile DataTypeConfigurations.xml File
	 * @throws ParseException ParseException.
	 */
	private void parseXML(String xmlFile) throws ParseException
	{
		try
		{
			InputStream inputStream = DataTypeConfigFactory.class.getClassLoader()
			.getResourceAsStream(xmlFile);
			dom = XMLParserUtility.getDocument(inputStream);
			parseDocument();
		}
		catch (Exception ioe)
		{
			logger.error(ioe.getMessage(), ioe);
			throw new ParseException(ioe);
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
	This method inserts dataType Name and DataTypeConfigObject into Map.
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
				dataTypeConfigurationObject.setClassName(dataTypeClassNode.getNodeValue());
			}
		}
		dataTypeConfigurationMap.put(dataTypeName, dataTypeConfigurationObject);
	}

	/**
	 * This method gets Validator DataType object.
	 * @param dataType data Type
	 * @return dataTypeInterface
	 * @throws ApplicationException Application Exception.
	 */
	public IDBDataType getDataType(String dataType) throws ApplicationException
	{
		try
		{
			DataTypeConfigObject dataTypeConfig = dataTypeConfigurationMap.get(dataType);
			String className = dataTypeConfig.getClassName();
			Class<IDBDataType> dataTypeClass = (Class<IDBDataType>) Class.forName(className);
			return dataTypeClass.newInstance();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
			throw new ApplicationException(ErrorKey.getErrorKey("datatype.parse.error"), exception,
					"");
		}
	}
}
