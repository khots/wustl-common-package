package edu.wustl.common.util.global;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.common.util.ParseXMLFile;
import edu.wustl.common.util.logger.Logger;

/**
 * This class has utility methods to parse xml file.
 * @author ravi_kumar
 *
 */
public class XMLParserUtility
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(XMLParserUtility.class);

	/**
	 * This method return role name from xml file.
	 * @param element String- privilege Element
	 * @param elementName -Element name for which value has to be return
	 * @return String Role name
	 */
	public static String getElementValue(Element element,String elementName)
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
	public static Document getDocument(String fileName)
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
	/**
	 * This method parses the nodes of XML file and returns the Document object.
	 * @param fileName String file name.
	 * @return Document contents of file in document format.
	 * @exception CheckedException checked exception.
	 */
	public static Document parseFile(String fileName) throws CheckedException
	{
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try
		{
			docBuilderFactory.setIgnoringComments(true);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new CheckedException(e.getMessage(), e, ErrorCodeConstants.UN_XXXX);
		}
		File sourceFile = new File(fileName);
		try
		{
			InputStream inputStream = ParseXMLFile.class.getClassLoader().getResourceAsStream(
					fileName);
			if (inputStream == null)
			{
				doc = docBuilder.parse(sourceFile);
			}
			else
			{
				doc = docBuilder.parse(inputStream);
			}
		}
		catch (SAXException e)
		{
			throw new CheckedException(e.getMessage(), e, ErrorCodeConstants.IO_0003);
		}
		catch (IOException e)
		{
			throw new CheckedException(e.getMessage(), e, ErrorCodeConstants.IO_0001);
		}
		return doc;
	}
}
