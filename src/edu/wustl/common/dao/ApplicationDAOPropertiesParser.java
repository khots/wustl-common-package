/**
 * 
 */
package edu.wustl.common.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author prashant_bandal
 *
 */
public class ApplicationDAOPropertiesParser
{
	private Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();
	private Document dom;
	
	private String connectionManager,applicationName,daoFactoryName,className,configFile,jdbcDAOName;
	public Map<String, IDAOFactory> getDaoFactoryMap()
	{
		try
		{
			readFile();
			parseDocument();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse("ApplicationDAOProperties.xml");

		}
		catch (ParserConfigurationException pce)
		{
			pce.printStackTrace();
		}
		catch (SAXException se)
		{
			se.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	private void parseDocument() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException
	{
		//get the root elememt
		Element root = dom.getDocumentElement();
		NodeList children = root.getElementsByTagName("Application");
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);

			if (child.getNodeName().equals("Application"))
			{
				setApplicationProperties(child);
			}

			IDAOFactory daoFactory = (IDAOFactory) Class.forName(daoFactoryName).newInstance();
			daoFactory.setConnectionManagerName(connectionManager);
			daoFactory.setDefaultDAOClassName(className);
			daoFactory.setJDBCDAOClassName(jdbcDAOName);
			daoFactory.setApplicationName(applicationName);
			daoFactory.setConfigurationFile(configFile);
			daoFactoryMap.put(daoFactory.getApplicationName(), daoFactory);
			resetVariables();
		}

	}

	/**
	 * 
	 */
	private void resetVariables()
	{
		daoFactoryName = "";
		connectionManager = "";
		className = "";
		jdbcDAOName = "";
		applicationName = "";
		configFile = "";
	}

	/**
	 * @param child
	 * @throws DOMException
	 */
	private void setApplicationProperties(Node child) throws DOMException
	{
		NamedNodeMap attributeMap = child.getAttributes();
		applicationName = ((Node)attributeMap.item(0)).getNodeValue();

		NodeList chil = child.getChildNodes();

		for (int k = 0; k < chil.getLength(); k++)
		{
			Node child1 = chil.item(k);

			if (child1.getNodeName().equals("DAOFactory"))
			{
				setDAOFactoryProperties(child1);
			}
			if (child1.getNodeName().equals("ConnectionManager"))
			{
				NamedNodeMap attMap = child1.getAttributes();
				Node attNode = attMap.item(0);
				connectionManager = attNode.getNodeValue();
			}
		}
	}
	
	private void setDAOFactoryProperties(Node childNode)
	{
		NamedNodeMap attrMap = childNode.getAttributes();
		daoFactoryName = ((Node)attrMap.item(0)).getNodeValue();
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
	private void setJDBCDAOProperties(Node childrenDAOFactory)
	{
		NamedNodeMap attMap = childrenDAOFactory.getAttributes();
		Node attNode = attMap.item(0);
		jdbcDAOName = attNode.getNodeValue();
	}

	/**
	 * @param childrenDAOFactory
	 * @throws DOMException
	 */
	private void setDefaultDAOProperties(Node childrenDAOFactory) throws DOMException
	{
		NodeList childDefaultDAO = childrenDAOFactory.getChildNodes();
		for (int l = 0; l < childDefaultDAO.getLength(); l++)
		{
			Node childnode = childDefaultDAO.item(l);

			if (childnode.getNodeName().equals("Class-name"))
			{
				NamedNodeMap attMap = childnode.getAttributes();
				Node attNode = attMap.item(0);
				className = attNode.getNodeValue();
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
