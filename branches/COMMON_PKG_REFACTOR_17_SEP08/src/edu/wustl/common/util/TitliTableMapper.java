/**
 *
 */

package edu.wustl.common.util;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import titli.controller.Name;
import titli.model.TitliException;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.global.XMLParserUtility;

/**
 * This class reads the mapping  xml file and gets the tree into the memory.
 * it's a singleton
 *
 * @author Juber Patel
 *
 */
public final class TitliTableMapper
{
	/**
	 * the only instance of this class.
	 */
	private static TitliTableMapper mapper = new TitliTableMapper();

	/**
	 * the in-memory document constructed from the xml file.
	 */
	private Document document=null;

	/**
	 * the private constructor for singleton behavior.
	 * it reads the xml file and creates the Document.
	 *
	 */
	private TitliTableMapper()
	{
		InputStream inputStream = this.getClass()
		.getClassLoader().getResourceAsStream(TextConstants.TITLI_TABLE_MAPPING_FILE);
		document=XMLParserUtility.getDocument(inputStream);
	}

	/**
	 * get the only instance of this class.
	 * @return TitliTableMapper the only instance of this class
	 * @exception TitliException titli exception.
	 */
	public static TitliTableMapper getInstance()throws TitliException
	{
		if(null==mapper.document)
		{
			throw new TitliException("Can not create instance, error in ");
		}
		return mapper;
	}

	/**
	 * get the the label corresponding to the specified table name.
	 * @param tableName the table name.
	 * @return the table name.
	 * @throws TitliException if problems occur.
	 */
	public String getLabel(Name tableName) throws TitliException
	{
		String label = tableName.toString();
		Element root =document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("mapping");
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element element = (Element) (nodeList.item(i));
			if (element.getAttribute("table").trim().equals(label))
			{
				label = element.getAttribute("label");
				break;
			}
		}
		return label;
	}

	/**
	 * get the string containing page details corresponding to the specified label.
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
	 * get the table name corresponding to the specified label.
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
