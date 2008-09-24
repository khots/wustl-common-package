/**
 * 
 */

package edu.wustl.common.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;

import titli.controller.Name;
import titli.model.TitliException;

/**
 * This class reads the mapping  xml file and gets the tree into the memory
 * it's a sigleton 
 * 
 * @author Juber Patel
 *
 */
public final class TitliTableMapper
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(TitliTableMapper.class);
	/**
	 * the only instance of this class
	 */
	private static TitliTableMapper mapper;

	/**
	 * the in-memory document constructed from the xml file
	 */
	private Document document;

	/**
	 * the private constructor for singleton behaviour
	 * it reads the xml file and creates the Document
	 *
	 */
	private TitliTableMapper()
	{
		InputStream inputStream = Thread.currentThread().getContextClassLoader()
								.getResourceAsStream(TextConstants.TITLI_TABLE_MAPPING_FILE);
		DocumentBuilder builder = null;
		try
		{
			try
			{
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				document = builder.parse(inputStream);
			}
			catch (ParserConfigurationException e)
			{
				logger.error("Could not locate a JAXP parser: "+TextConstants.TITLI_TABLE_MAPPING_FILE,e);
			}
			catch (SAXException e)
			{
				logger.error("cannot parse the file "+TextConstants.TITLI_TABLE_MAPPING_FILE,e);
			}
			finally
			{
				inputStream.close();
			}
		}
		catch (IOException e)
		{
			logger.error("Exception in TitliTableMapper ",e);
		}
	}

	/**
	 * get the only instance of this class
	 * @return the only instance of this class
	 */
	public static TitliTableMapper getInstance()
	{
		if (mapper == null)
		{
			mapper = new TitliTableMapper();
		}
		return mapper;
	}

	/**
	 * get the the label corresponding to the specified table name
	 * @param tableName the table name
	 * @return the table name
	 * @throws Exception if problems occur
	 */
	public String getLabel(Name tableName) throws TitliException
	{
		String label = tableName.toString();
		Element root =document.getDocumentElement();
		if (root == null)
		{
			throw new TitliException("Tilti table mapping file can not be read:"+root);
		}
		NodeList nodeList = root.getElementsByTagName("mapping");
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element element = (Element) (nodeList.item(i));
			if (new Name(element.getAttribute("table")).equals(tableName))
			{
				label = element.getAttribute("label");
				break;
			}
		}
		return label;
	}

	/**
	 * get the pageOf string corresponding to the specified label
	 * @param label the label
	 * @return the pageOf string
	 */
	public String getPageOf(String label)
	{
		String pageOf = null;
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("mapping");
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element element = (Element) (nodeList.item(i));
			if (element.getAttribute("label").equals(label))
			{
				pageOf = element.getAttribute("pageOf");
				break;
			}
		}
		return pageOf;
	}

	/**
	 * get the table name corrsponding to the specified label
	 * @param label the label
	 * @return the table name
	 */
	public Name getTable(String label)
	{
		Name table = null;
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("mapping");
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element element = (Element) (nodeList.item(i));
			if (element.getAttribute("label").equals(label))
			{
				table = new Name(element.getAttribute("table"));
				break;
			}
		}
		return table;
	}
}
