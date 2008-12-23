/**
 *
 */

package edu.wustl.common.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exception.ParseException;
import edu.wustl.common.util.global.XMLParserUtility;

/**
 * @author prashant_bandal
 *
 */
public final class AbstractFactoryConfig
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractFactoryConfig.class);

	/**
	 * Specifies success.
	 */
	private static boolean success = true;

	/**
	 * Specifies ControlConfigurationsFactory instance.
	 */
	private static AbstractFactoryConfig configFactory = new AbstractFactoryConfig();

	/**
	 * Specifies dataType Configuration Map.
	 */
	private final Map<String, FactoryConfigObject> factoryConfigurationMap;

	/**
	 * Specifies Document object.
	 */
	private Document dom;

	/**
	 * Specifies parse Exception Message.
	 */
	private static String parseExcepMessage;

	/**
	 * ControlConfigurationsFactory constructor.
	 */
	private AbstractFactoryConfig()
	{
		factoryConfigurationMap = new HashMap<String, FactoryConfigObject>();

		try
		{
			parseXML("Factory.xml");
		}
		catch (ParseException exception)
		{
			success = false;
			parseExcepMessage = exception.getMessage();
			logger.error(parseExcepMessage, exception);
		}

	}

	/**
	 * This method gets ControlConfigurationsFactory Instance.
	 * @return ControlConfigurationsFactory instance.
	 * @throws BizLogicException BizLogic Exception.
	 */
	public static AbstractFactoryConfig getInstance() throws BizLogicException
	{
		if (success)
		{
			return configFactory;
		}
		else
		{
			throw new BizLogicException(ErrorKey.getErrorKey("biz.getinstance.error"), null,
					parseExcepMessage);
		}
	}

	/**
	 * This method parse xml File.
	 * @param xmlFile xml File
	 * @throws ParseException Parse Exception.
	 */
	private void parseXML(String xmlFile) throws ParseException
	{

		try
		{
			dom = XMLParserUtility.getDocument(xmlFile);
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

		NodeList factoryList = root.getChildNodes();
		//int noOfDataTypes = validationDataTypeList.getLength();
		Node factoryNode = null;

		//String dataTypeName = null;
		for (int i = 0; i < factoryList.getLength(); i++)
		{
			factoryNode = factoryList.item(i);
			if (factoryNode != null)
			{
				insertInToMap(factoryNode);
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
		Node factoryNameNode;
		Node classNode;
		NamedNodeMap dataTypeAttributes;
		String factoryName = null;
		FactoryConfigObject confObject = new FactoryConfigObject();
		dataTypeAttributes = validationDataTypeNode.getAttributes();
		if (dataTypeAttributes != null)
		{
			factoryNameNode = dataTypeAttributes.getNamedItem("name");
			classNode = dataTypeAttributes.getNamedItem("className");
			if (factoryNameNode != null && classNode != null)
			{
				factoryName = factoryNameNode.getNodeValue();
				confObject.setFactoryName(factoryName);
				confObject.setFactoryClassName(classNode.getNodeValue());
			}
		}
		if (factoryName != null)
		{
			factoryConfigurationMap.put(factoryName, confObject);
		}
	}

	/**
	 * This method gets BizLogic Factory Instance.
	 * @param factoryName factory Name.
	 * @return dataTypeInterface
	 * @throws BizLogicException BizLogic Exception.
	 */
	public IFactory getBizLogicFactory(String factoryName) throws BizLogicException
	{
		try
		{
			FactoryConfigObject config = factoryConfigurationMap.get(factoryName);
			String className = config.getFactoryClassName();
			Class<IFactory> factoryClass = (Class<IFactory>) Class.forName(className);
			return factoryClass.newInstance();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
			throw new BizLogicException(ErrorKey.getErrorKey("biz.getinstance.error"), exception, "");
		}
	}

	/**
	 * get Forward To Factory.
	 * @param factoryName factory Name.
	 * @return IForwordToFactory
	 * @throws BizLogicException BizLogicException.
	 */
	public IForwordToFactory getForwToFactory(String factoryName) throws BizLogicException
	{
		try
		{
			FactoryConfigObject config = factoryConfigurationMap.get(factoryName);
			String className = config.getFactoryClassName();
			Class<IForwordToFactory> factoryClass = (Class<IForwordToFactory>) Class
					.forName(className);
			return factoryClass.newInstance();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
			throw new BizLogicException(ErrorKey.getErrorKey("biz.getinstance.error"), exception, "");
		}

	}

}
