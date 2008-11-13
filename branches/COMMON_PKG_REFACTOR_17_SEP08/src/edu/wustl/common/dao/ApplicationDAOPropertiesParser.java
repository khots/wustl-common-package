/**
 *
 */

package edu.wustl.common.dao;

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

import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.logger.Logger;

/**
 * @author prashant_bandal
 *
 */
public class ApplicationDAOPropertiesParser
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(ApplicationDAOPropertiesParser.class);

	/**
	 * Specifies dao Factory Map.
	 */
	private transient final Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();

	/**
	 * Specifies Document object.
	 */
	private transient Document dom;

	/**
	 * Specifies application variables.
	 */
	private transient String connectionManager, applicationName, daoFactoryName, defaultDaoName,
			configFile, jdbcDAOName;

	/**
	 * This method gets Dao Factory Map.
	 * @return dao Factory Map.
	 */
	public Map<String, IDAOFactory> getDaoFactoryMap()
	{
		try
		{
			readFile();
			parseDocument();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
		}
		return daoFactoryMap;
	}

	/**
	 * This method parse Xml File.
	 */
	private void readFile()
	{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = documentBuilder.parse("ApplicationDAOProperties.xml");

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
	 * This method parse the document.
	 * @throws InstantiationException Instantiation Exception
	 * @throws IllegalAccessException Illegal Access Exception
	 * @throws ClassNotFoundException Class Not Found Exception
	 * @throws DAOException 
	 */
	private void parseDocument() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, DAOException
	{
		//get the root elememt
		Element root = dom.getDocumentElement();
		NodeList rootChildren = root.getElementsByTagName("Application");
		for (int i = 0; i < rootChildren.getLength(); i++)
		{
			Node applicationChild = rootChildren.item(i);

			if (applicationChild.getNodeName().equals("Application"))
			{
				setApplicationProperties(applicationChild);
			}

			IDAOFactory daoFactory = (IDAOFactory) Class.forName(daoFactoryName).newInstance();
			daoFactory.setConnectionManagerName(connectionManager);
			daoFactory.setDefaultDAOClassName(defaultDaoName);
			daoFactory.setJdbcDAOClassName(jdbcDAOName);
			daoFactory.setApplicationName(applicationName);
			daoFactory.setConfigurationFile(configFile);
			daoFactory.buildSessionFactory();
			daoFactoryMap.put(daoFactory.getApplicationName(), daoFactory);
			resetApplicationProperties();
		}

	}

	/**
	 * This method resets Application Properties.
	 */
	private void resetApplicationProperties()
	{
		daoFactoryName = "";
		connectionManager = "";
		defaultDaoName = "";
		jdbcDAOName = "";
		applicationName = "";
		configFile = "";
	}

	/**
	 * This method sets Application Properties.
	 * @param applicationChild application Children.
	 */
	private void setApplicationProperties(Node applicationChild)
	{
		NamedNodeMap attributeMap = applicationChild.getAttributes();
		applicationName = ((Node) attributeMap.item(0)).getNodeValue();

		NodeList applicationChildList = applicationChild.getChildNodes();

		for (int k = 0; k < applicationChildList.getLength(); k++)
		{
			Node applicationChildNode = applicationChildList.item(k);

			if (applicationChildNode.getNodeName().equals("DAOFactory"))
			{
				setDAOFactoryProperties(applicationChildNode);
			}
			if (applicationChildNode.getNodeName().equals("ConnectionManager"))
			{
				setConnectionManagerProperties(applicationChildNode);
			}
		}
	}

	/**
	 * This method sets Connection Manager Properties.
	 * @param applicationChildNode application Child Node.
	 */
	private void setConnectionManagerProperties(Node applicationChildNode)
	{
		NamedNodeMap attMap = applicationChildNode.getAttributes();
		Node attNode = attMap.item(0);
		connectionManager = attNode.getNodeValue();
	}

	/**
	 * This method sets DAOFactory Properties.
	 * @param childNode DAOFactory child Node.
	 */
	private void setDAOFactoryProperties(Node childNode)
	{
		NamedNodeMap attrMap = childNode.getAttributes();
		daoFactoryName = ((Node) attrMap.item(0)).getNodeValue();
		NodeList childlist = childNode.getChildNodes();
		for (int m = 0; m < childlist.getLength(); m++)
		{
			Node childrenDAOFactory = childlist.item(m);
			if (childrenDAOFactory.getNodeName().equals("DefaultDAO"))
			{
				setDefaultDAOProperties(childrenDAOFactory);
			}
			if (childrenDAOFactory.getNodeName().equals("jdbcDAO"))
			{
				setJDBCDAOProperties(childrenDAOFactory);
			}
		}

	}

	/**
	 * This method sets JDBCDAO Properties.
	 * @param childrenDAOFactory children DAOFactory.
	 */
	private void setJDBCDAOProperties(Node childrenDAOFactory)
	{
		NamedNodeMap attMap = childrenDAOFactory.getAttributes();
		Node attNode = attMap.item(0);
		jdbcDAOName = attNode.getNodeValue();
	}

	/**
	 * This method sets Default DAO Properties.
	 * @param childrenDAOFactory children DAOFactory
	 * @throws DOMException
	 */
	private void setDefaultDAOProperties(Node childrenDAOFactory)
	{
		NodeList childDefaultDAO = childrenDAOFactory.getChildNodes();
		for (int l = 0; l < childDefaultDAO.getLength(); l++)
		{
			Node childnode = childDefaultDAO.item(l);

			if (childnode.getNodeName().equals("Class-name"))
			{
				NamedNodeMap attMap = childnode.getAttributes();
				Node attNode = attMap.item(0);
				defaultDaoName = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("Config-file"))
			{
				NamedNodeMap configFileMap = childnode.getAttributes();
				Node configFileMapNode = configFileMap.item(0);
				configFile = configFileMapNode.getNodeValue();
			}
		}
	}

}
