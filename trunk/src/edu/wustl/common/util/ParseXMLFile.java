
package edu.wustl.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is used for parsing the XML file and put the parsed elements to HashMaps 
 * @author Kaushal Kumar
 * @version 1.0
 */
public class ParseXMLFile
{

	/**
	 * XML file (to be parsed) with complete path.
	 */
	/*
	 private final String xmlFileName = "C:/temp/dynamicUI.xml";*/
	/**
	 * HashMap keeping the collection of enumerated data-types.
	 */
	public static HashMap<Object, Object[]> enumMap = new HashMap<Object, Object[]>();
	/**
	 * HashMap keeping the collection of non-enumerated data-types.
	 */
	public static HashMap<Object, Object[]> nonEnumMap = new HashMap<Object, Object[]>();
	/**
	 * Object Array for enumerated string data types.
	 */
	private Object[] enumStr1 = new Object[3];
	/**
	 * Object Array for enumerated number data types.
	 */
	private Object[] enumNum1 = new Object[3];
	/**
	 * Object Array for boolean data types.
	 */
	private Object[] enumBool1 = new Object[3];
	/**
	 * Object Array for non-enumerated string data types.
	 */
	private Object[] nonEnumStr1 = new Object[2];
	/**
	 * Object Array for non-enumerated number data types.
	 */
	private Object[] nonEnumNum1 = new Object[2];
	/**
	 * Object Array for Date type.
	 */
	private Object[] nonEnumDate1 = new Object[2];
	/**
	 * Conditions Array for enumerated string data types.
	 */
	private Object[] enumStr = new Object[9];
	/**
	 * Conditions Array for enumerated number data types.
	 */
	private Object[] enumNum = new Object[9];
	/**
	 * Conditions Array for Boolean data types.
	 */
	private Object[] enumBool = new Object[2];
	/**  
	 * Conditions Array for non-enumerated string data types. 
	 */
	private Object[] nonEnumStr = new Object[7];
	/**
	 * Conditions Array for non-enumerated number data types.
	 */
	private Object[] nonEnumNum = new Object[9];
	/**
	 * Conditions Array for Date type.
	 */
	private Object[] nonEnumDate = new Object[9];

	/**
	 * Constructor for parsing the XML file. 
	 *
	 */
	public ParseXMLFile(String xmlFileName)
	{
		Document doc = parseFile(xmlFileName);
		Node root = doc.getDocumentElement();
		System.out.println("Statement of XML document...");
		writeDocumentToOutput(root);
		System.out.println("... end of statement");
	}

	/**
	 * This method return the Element value of any particular node
	 * @param elem
	 * @return string
	 */
	private String getElementValue(Node elem)
	{
		Node child;
		if (elem != null)
		{
			if (elem.hasChildNodes())
			{
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling())
				{
					if (child.getNodeType() == Node.TEXT_NODE)
					{
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * This method parses the XML file and puts the elements into the enumerated/non-enumerated HashMaps.
	 * @param node
	 */
	public void writeDocumentToOutput(Node node)
	{
		String nodeName = node.getNodeName();
		String nodeValue = getElementValue(node);

		NamedNodeMap attributes = node.getAttributes();
		System.out.println("NodeName: " + nodeName + ", NodeValue: " + nodeValue);
		for (int i = 0; i < attributes.getLength(); i++)
		{
			Node attribute = attributes.item(i);
			System.out.println("AttributeName: " + attribute.getNodeName() + ", attributeValue: " + attribute.getNodeValue());
		}

		NodeList children = node.getChildNodes();
		for (int i_count1 = 0; i_count1 < children.getLength(); i_count1++)
		{
			Node child = children.item(i_count1);
			System.out.println(child.getNodeName());
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				if (child.getNodeName().equalsIgnoreCase("enumerated"))
				{
					NodeList enumChildren = child.getChildNodes();
					for (int i_count2 = 0; i_count2 < enumChildren.getLength(); i_count2++)
					{
						Node enumChild = enumChildren.item(i_count2);
						System.out.println(enumChild.getNodeName());
						if (enumChild.getNodeType() == Node.ELEMENT_NODE)
						{
							if (enumChild.getNodeName().equalsIgnoreCase("string"))
							{
								NodeList strChildren = enumChild.getChildNodes();
								for (int i_count3 = 0; i_count3 < strChildren.getLength(); i_count3++)
								{
									Node strChild = strChildren.item(i_count3);
									System.out.println(strChild.getNodeName());
									if (strChild.getNodeType() == Node.ELEMENT_NODE)
									{
										if (strChild.getNodeName().equalsIgnoreCase("conditions"))
										{
											NodeList condChildren = strChild.getChildNodes();
											System.out.println(condChildren.getLength());
											for (int i_count4 = 0; i_count4 < condChildren.getLength(); i_count4++)
											{
												Node condChild = condChildren.item(i_count4);
												System.out.println("i = " + i_count4 + " : " + condChild.getNodeName());
												if (condChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (condChild.getNodeName().equalsIgnoreCase("in"))
													{
														NodeList dispInChildren = condChild.getChildNodes();
														for (int i_count5 = 0; i_count5 < dispInChildren.getLength(); i_count5++)
														{
															Node dispInChild = dispInChildren.item(i_count5);
															System.out.println(dispInChild.getNodeName());
															if (dispInChild.getNodeName().equalsIgnoreCase("displayname"))
																enumStr[0] = (Object) getElementValue(dispInChild);
														}
													}
													else if (condChild.getNodeName().equalsIgnoreCase("notin"))
													{
														NodeList dispNotInChildren = condChild.getChildNodes();
														for (int i_count6 = 0; i_count6 < dispNotInChildren.getLength(); i_count6++)
														{
															Node dispNotInChild = dispNotInChildren.item(i_count6);
															System.out.println(dispNotInChild.getNodeName());
															if (dispNotInChild.getNodeName().equalsIgnoreCase("displayname"))
																enumStr[1] = (Object) getElementValue(dispNotInChild);
														}
													}
												}

											}
											enumStr1[0] = enumStr;
										}
										if (strChild.getNodeName().equalsIgnoreCase("record-count"))
										{
											NodeList recChildren = strChild.getChildNodes();
											for (int i_count8 = 0; i_count8 < recChildren.getLength(); i_count8++)
											{
												Node recChild = recChildren.item(i_count8);
												System.out.println(recChild.getNodeName());
												if (recChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (recChild.getNodeName().equalsIgnoreCase("components1"))
													{
														enumStr1[1] = (Object) getElementValue(recChild);
														System.out.println(enumStr1[1]);
													}
													if (recChild.getNodeName().equalsIgnoreCase("components2"))
													{
														enumStr1[2] = (Object) getElementValue(recChild);
														System.out.println(enumStr1[2]);
													}
												}

											}
										}
									}
								}
								enumMap.put((Object) enumChild.getNodeName(), enumStr1);
							}
							if (enumChild.getNodeName().equalsIgnoreCase("number"))
							{
								NodeList numChildren = enumChild.getChildNodes();
								for (int i_count7 = 0; i_count7 < numChildren.getLength(); i_count7++)
								{
									Node numChild = numChildren.item(i_count7);
									System.out.println(numChild.getNodeName());
									if (numChild.getNodeType() == Node.ELEMENT_NODE)
									{
										if (numChild.getNodeName().equalsIgnoreCase("conditions"))
										{
											NodeList condChildren = numChild.getChildNodes();
											for (int i_count8 = 0; i_count8 < condChildren.getLength(); i_count8++)
											{
												Node condChild = condChildren.item(i_count8);
												System.out.println(condChild.getNodeName());
												if (condChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (condChild.getNodeName().equalsIgnoreCase("in"))
													{
														NodeList dispInChildren = condChild.getChildNodes();
														for (int i_count9 = 0; i_count9 < dispInChildren.getLength(); i_count9++)
														{
															Node dispInChild = dispInChildren.item(i_count9);
															System.out.println(dispInChild.getNodeName());
															if (dispInChild.getNodeName().equalsIgnoreCase("displayname"))
																enumNum[0] = (Object) getElementValue(dispInChild);
														}
													}
													if (condChild.getNodeName().equalsIgnoreCase("notin"))
													{
														NodeList dispNotInChildren = condChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < dispNotInChildren.getLength(); i_count10++)
														{
															Node dispNotInChild = dispNotInChildren.item(i_count10);
															System.out.println(dispNotInChild.getNodeName());
															if (dispNotInChild.getNodeName().equalsIgnoreCase("displayname"))
																enumNum[1] = (Object) getElementValue(dispNotInChild);
														}
													}
												}
											}
											enumNum1[0] = enumNum;
										}
										if (numChild.getNodeName().equalsIgnoreCase("record-count"))
										{
											NodeList recChildren = numChild.getChildNodes();
											for (int i_count8 = 0; i_count8 < recChildren.getLength(); i_count8++)
											{
												Node recChild = recChildren.item(i_count8);
												System.out.println(recChild.getNodeName());
												if (recChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (recChild.getNodeName().equalsIgnoreCase("components1"))
														enumNum1[1] = (Object) getElementValue(recChild);
													if (recChild.getNodeName().equalsIgnoreCase("components2"))
														enumNum1[2] = (Object) getElementValue(recChild);
												}

											}
										}
									}
								}
								enumMap.put((Object) enumChild.getNodeName(), enumNum1);
							}
							if (enumChild.getNodeName().equalsIgnoreCase("boolean"))
							{
								NodeList boolChildren = enumChild.getChildNodes();
								for (int i_count11 = 0; i_count11 < boolChildren.getLength(); i_count11++)
								{
									Node boolChild = boolChildren.item(i_count11);
									System.out.println(boolChild.getNodeName());
									if (boolChild.getNodeType() == Node.ELEMENT_NODE)
									{
										if (boolChild.getNodeName().equalsIgnoreCase("conditions"))
										{
											NodeList condChildren = boolChild.getChildNodes();
											for (int i_count12 = 0; i_count12 < condChildren.getLength(); i_count12++)
											{
												Node condChild = condChildren.item(i_count12);
												System.out.println(condChild.getNodeName());
												if (condChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (condChild.getNodeName().equalsIgnoreCase("equals"))
													{
														NodeList dispEqualsChildren = condChild.getChildNodes();
														for (int i_count13 = 0; i_count13 < dispEqualsChildren.getLength(); i_count13++)
														{
															Node dispEqualsChild = dispEqualsChildren.item(i_count13);
															System.out.println(dispEqualsChild.getNodeName());
															if (dispEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																enumBool[0] = (Object) getElementValue(dispEqualsChild);
														}
													}
													if (condChild.getNodeName().equalsIgnoreCase("notequals"))
													{
														NodeList dispNotEqualsChildren = condChild.getChildNodes();
														for (int i_count14 = 0; i_count14 < dispNotEqualsChildren.getLength(); i_count14++)
														{
															Node dispNotEqualsChild = dispNotEqualsChildren.item(i_count14);
															System.out.println(dispNotEqualsChild.getNodeName());
															if (dispNotEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																enumBool[1] = (Object) getElementValue(dispNotEqualsChild);
														}
													}
												}
											}
											enumBool1[0] = enumBool;
										}
										if (boolChild.getNodeName().equalsIgnoreCase("components"))
										{
											enumBool1[1] = (Object) getElementValue(boolChild);
										}
										/*if(boolChild.getNodeName().equalsIgnoreCase("values"))
										 {
										 NodeList valChildren = boolChild.getChildNodes();
										 for(int i_count = 0;i_count<valChildren.getLength();i_count++)
										 {
										 Node  valChild = valChildren.item(i_count);
										 System.out.println(valChild.getNodeName());
										 if(valChild.getNodeType() == Node.ELEMENT_NODE)
										 {
										 if(valChild.getNodeName().equalsIgnoreCase("yes"))
										 {

										 }
										 if(valChild.getNodeName().equalsIgnoreCase("no"))
										 {

										 }
										 }
										 }
										 enumBool1[2] = (Object)getElementValue(boolChild);
										 }*/
									}
								}
								enumMap.put((Object) enumChild.getNodeName(), enumBool1);
							}
						}
					}
				}
				if (child.getNodeName().equalsIgnoreCase("non-enumerated"))
				{
					NodeList nonEnumChildren = child.getChildNodes();
					for (int i_count2 = 0; i_count2 < nonEnumChildren.getLength(); i_count2++)
					{
						Node nonEnumChild = nonEnumChildren.item(i_count2);
						System.out.println(nonEnumChild.getNodeName());
						if (nonEnumChild.getNodeType() == Node.ELEMENT_NODE)
						{
							if (nonEnumChild.getNodeName().equalsIgnoreCase("string"))
							{
								NodeList nonEnumStrChildren = nonEnumChild.getChildNodes();
								for (int i_count3 = 0; i_count3 < nonEnumStrChildren.getLength(); i_count3++)
								{
									Node nonEnumStrChild = nonEnumStrChildren.item(i_count3);
									System.out.println(nonEnumStrChild.getNodeName());
									if (nonEnumStrChild.getNodeType() == Node.ELEMENT_NODE)
									{
										if (nonEnumStrChild.getNodeName().equalsIgnoreCase("conditions"))
										{
											NodeList nonEnumCondChildren = nonEnumStrChild.getChildNodes();
											System.out.println(nonEnumCondChildren.getLength());
											for (int i_count4 = 0; i_count4 < nonEnumCondChildren.getLength(); i_count4++)
											{
												Node nonEnumCondChild = nonEnumCondChildren.item(i_count4);
												System.out.println("i = " + i_count4 + " : " + nonEnumCondChild.getNodeName());
												if (nonEnumCondChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (nonEnumCondChild.getNodeName().equalsIgnoreCase("startswith"))
													{
														NodeList nonEnumDispStartsWithChildren = nonEnumCondChild.getChildNodes();
														for (int i_count5 = 0; i_count5 < nonEnumDispStartsWithChildren.getLength(); i_count5++)
														{
															Node nonEnumDispStartsWithChild = nonEnumDispStartsWithChildren.item(i_count5);
															System.out.println(nonEnumDispStartsWithChild.getNodeName());
															if (nonEnumDispStartsWithChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumStr[0] = (Object) getElementValue(nonEnumDispStartsWithChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("endswith"))
													{
														NodeList nonEnumDispEndsWithChildren = nonEnumCondChild.getChildNodes();
														for (int i_count6 = 0; i_count6 < nonEnumDispEndsWithChildren.getLength(); i_count6++)
														{
															Node nonEnumDispEndsWithChild = nonEnumDispEndsWithChildren.item(i_count6);
															System.out.println(nonEnumDispEndsWithChild.getNodeName());
															if (nonEnumDispEndsWithChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumStr[1] = (Object) getElementValue(nonEnumDispEndsWithChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("contains"))
													{
														NodeList nonEnumDispContainsChildren = nonEnumCondChild.getChildNodes();
														for (int i_count6 = 0; i_count6 < nonEnumDispContainsChildren.getLength(); i_count6++)
														{
															Node nonEnumDispContainsChild = nonEnumDispContainsChildren.item(i_count6);
															System.out.println(nonEnumDispContainsChild.getNodeName());
															if (nonEnumDispContainsChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumStr[2] = (Object) getElementValue(nonEnumDispContainsChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("equals"))
													{
														NodeList nonEnumDispEqualsChildren = nonEnumCondChild.getChildNodes();
														for (int i_count6 = 0; i_count6 < nonEnumDispEqualsChildren.getLength(); i_count6++)
														{
															Node nonEnumDispEqualsChild = nonEnumDispEqualsChildren.item(i_count6);
															System.out.println(nonEnumDispEqualsChild.getNodeName());
															if (nonEnumDispEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumStr[3] = (Object) getElementValue(nonEnumDispEqualsChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("notequals"))
													{
														NodeList nonEnumDispNotEqualsChildren = nonEnumCondChild.getChildNodes();
														for (int i_count6 = 0; i_count6 < nonEnumDispNotEqualsChildren.getLength(); i_count6++)
														{
															Node nonEnumDispNotEqualsChild = nonEnumDispNotEqualsChildren.item(i_count6);
															System.out.println(nonEnumDispNotEqualsChild.getNodeName());
															if (nonEnumDispNotEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumStr[4] = (Object) getElementValue(nonEnumDispNotEqualsChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("isnull"))
													{
														NodeList nonEnumDispIsNullChildren = nonEnumCondChild.getChildNodes();
														for (int i_count6 = 0; i_count6 < nonEnumDispIsNullChildren.getLength(); i_count6++)
														{
															Node nonEnumDispIsNullChild = nonEnumDispIsNullChildren.item(i_count6);
															System.out.println(nonEnumDispIsNullChild.getNodeName());
															if (nonEnumDispIsNullChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumStr[5] = (Object) getElementValue(nonEnumDispIsNullChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("isnotnull"))
													{
														NodeList nonEnumDispIsNotNullChildren = nonEnumCondChild.getChildNodes();
														for (int i_count6 = 0; i_count6 < nonEnumDispIsNotNullChildren.getLength(); i_count6++)
														{
															Node nonEnumDispIsNotNullChild = nonEnumDispIsNotNullChildren.item(i_count6);
															System.out.println(nonEnumDispIsNotNullChild.getNodeName());
															if (nonEnumDispIsNotNullChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumStr[6] = (Object) getElementValue(nonEnumDispIsNotNullChild);
														}
													}
												}

											}
											nonEnumStr1[0] = nonEnumStr;
										}
										if (nonEnumStrChild.getNodeName().equalsIgnoreCase("components"))
										{
											nonEnumStr1[1] = (Object) getElementValue(nonEnumStrChild);
										}
									}
								}
								nonEnumMap.put((Object) nonEnumChild.getNodeName(), nonEnumStr1);
							}
							if (nonEnumChild.getNodeName().equalsIgnoreCase("number"))
							{
								NodeList nonEnumNumChildren = nonEnumChild.getChildNodes();
								for (int i_count7 = 0; i_count7 < nonEnumNumChildren.getLength(); i_count7++)
								{
									Node nonEnumNumChild = nonEnumNumChildren.item(i_count7);
									System.out.println(nonEnumNumChild.getNodeName());
									if (nonEnumNumChild.getNodeType() == Node.ELEMENT_NODE)
									{
										if (nonEnumNumChild.getNodeName().equalsIgnoreCase("conditions"))
										{
											NodeList nonEnumCondChildren = nonEnumNumChild.getChildNodes();
											for (int i_count8 = 0; i_count8 < nonEnumCondChildren.getLength(); i_count8++)
											{
												Node nonEnumCondChild = nonEnumCondChildren.item(i_count8);
												System.out.println(nonEnumCondChild.getNodeName());
												if (nonEnumCondChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (nonEnumCondChild.getNodeName().equalsIgnoreCase("equals"))
													{
														NodeList nonEnumDispEqualsChildren = nonEnumCondChild.getChildNodes();
														for (int i_count9 = 0; i_count9 < nonEnumDispEqualsChildren.getLength(); i_count9++)
														{
															Node nonEnumDispEqualsChild = nonEnumDispEqualsChildren.item(i_count9);
															System.out.println(nonEnumDispEqualsChild.getNodeName());
															if (nonEnumDispEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[0] = (Object) getElementValue(nonEnumDispEqualsChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("notequals"))
													{
														NodeList nonEnumDispNotEqualsChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispNotEqualsChildren.getLength(); i_count10++)
														{
															Node nonEnumDispNotEqualsChild = nonEnumDispNotEqualsChildren.item(i_count10);
															System.out.println(nonEnumDispNotEqualsChild.getNodeName());
															if (nonEnumDispNotEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[1] = (Object) getElementValue(nonEnumDispNotEqualsChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("between"))
													{
														NodeList nonEnumDispBetweenChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispBetweenChildren.getLength(); i_count10++)
														{
															Node nonEnumDispBetweenChild = nonEnumDispBetweenChildren.item(i_count10);
															System.out.println(nonEnumDispBetweenChild.getNodeName());
															if (nonEnumDispBetweenChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[2] = (Object) getElementValue(nonEnumDispBetweenChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("lessthan"))
													{
														NodeList nonEnumDispLessThanChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispLessThanChildren.getLength(); i_count10++)
														{
															Node nonEnumDispLessThanChild = nonEnumDispLessThanChildren.item(i_count10);
															System.out.println(nonEnumDispLessThanChild.getNodeName());
															if (nonEnumDispLessThanChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[3] = (Object) getElementValue(nonEnumDispLessThanChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("lessthanorequalto"))
													{
														NodeList nonEnumDispLessThanOrEqualToChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispLessThanOrEqualToChildren.getLength(); i_count10++)
														{
															Node nonEnumDispLessThanOrEqualToChild = nonEnumDispLessThanOrEqualToChildren
																	.item(i_count10);
															System.out.println(nonEnumDispLessThanOrEqualToChild.getNodeName());
															if (nonEnumDispLessThanOrEqualToChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[4] = (Object) getElementValue(nonEnumDispLessThanOrEqualToChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("greaterthan"))
													{
														NodeList nonEnumDispGreaterThanChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispGreaterThanChildren.getLength(); i_count10++)
														{
															Node nonEnumDispGreaterThanChild = nonEnumDispGreaterThanChildren.item(i_count10);
															System.out.println(nonEnumDispGreaterThanChild.getNodeName());
															if (nonEnumDispGreaterThanChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[5] = (Object) getElementValue(nonEnumDispGreaterThanChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("greaterthanorequalto"))
													{
														NodeList nonEnumDispGreaterThanOrEqualToChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispGreaterThanOrEqualToChildren.getLength(); i_count10++)
														{
															Node nonEnumDispGreaterThanOrEqualToChild = nonEnumDispGreaterThanOrEqualToChildren
																	.item(i_count10);
															System.out.println(nonEnumDispGreaterThanOrEqualToChild.getNodeName());
															if (nonEnumDispGreaterThanOrEqualToChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[6] = (Object) getElementValue(nonEnumDispGreaterThanOrEqualToChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("isnull"))
													{
														NodeList nonEnumDispIsNullChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispIsNullChildren.getLength(); i_count10++)
														{
															Node nonEnumDispIsNullChild = nonEnumDispIsNullChildren.item(i_count10);
															System.out.println(nonEnumDispIsNullChild.getNodeName());
															if (nonEnumDispIsNullChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[7] = (Object) getElementValue(nonEnumDispIsNullChild);
														}
													}
													else if (nonEnumCondChild.getNodeName().equalsIgnoreCase("isnotnull"))
													{
														NodeList nonEnumDispNotInChildren = nonEnumCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispNotInChildren.getLength(); i_count10++)
														{
															Node nonEnumDispIsNotNullChild = nonEnumDispNotInChildren.item(i_count10);
															System.out.println(nonEnumDispIsNotNullChild.getNodeName());
															if (nonEnumDispIsNotNullChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumNum[8] = (Object) getElementValue(nonEnumDispIsNotNullChild);
														}
													}
												}
											}
											nonEnumNum1[0] = nonEnumNum;
										}
										if (nonEnumNumChild.getNodeName().equalsIgnoreCase("components"))
										{
											nonEnumNum1[1] = (Object) getElementValue(nonEnumNumChild);
										}
									}
								}
								nonEnumMap.put((Object) nonEnumChild.getNodeName(), nonEnumNum1);
							}
							if (nonEnumChild.getNodeName().equalsIgnoreCase("date"))
							{
								NodeList dateChildren = nonEnumChild.getChildNodes();
								for (int i_count11 = 0; i_count11 < dateChildren.getLength(); i_count11++)
								{
									Node dateChild = dateChildren.item(i_count11);
									System.out.println(dateChild.getNodeName());
									if (dateChild.getNodeType() == Node.ELEMENT_NODE)
									{
										if (dateChild.getNodeName().equalsIgnoreCase("conditions"))
										{
											NodeList dateCondChildren = dateChild.getChildNodes();
											for (int i_count12 = 0; i_count12 < dateCondChildren.getLength(); i_count12++)
											{
												Node dateCondChild = dateCondChildren.item(i_count12);
												System.out.println(dateCondChild.getNodeName());
												if (dateCondChild.getNodeType() == Node.ELEMENT_NODE)
												{
													if (dateCondChild.getNodeName().equalsIgnoreCase("equals"))
													{
														NodeList nonEnumDispEqualsChildren = dateCondChild.getChildNodes();
														for (int i_count9 = 0; i_count9 < nonEnumDispEqualsChildren.getLength(); i_count9++)
														{
															Node nonEnumDispEqualsChild = nonEnumDispEqualsChildren.item(i_count9);
															System.out.println(nonEnumDispEqualsChild.getNodeName());
															if (nonEnumDispEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[0] = (Object) getElementValue(nonEnumDispEqualsChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("notequals"))
													{
														NodeList nonEnumDispNotEqualsChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispNotEqualsChildren.getLength(); i_count10++)
														{
															Node nonEnumDispNotEqualsChild = nonEnumDispNotEqualsChildren.item(i_count10);
															System.out.println(nonEnumDispNotEqualsChild.getNodeName());
															if (nonEnumDispNotEqualsChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[1] = (Object) getElementValue(nonEnumDispNotEqualsChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("between"))
													{
														NodeList nonEnumDispBetweenChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispBetweenChildren.getLength(); i_count10++)
														{
															Node nonEnumDispBetweenChild = nonEnumDispBetweenChildren.item(i_count10);
															System.out.println(nonEnumDispBetweenChild.getNodeName());
															if (nonEnumDispBetweenChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[2] = (Object) getElementValue(nonEnumDispBetweenChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("lessthan"))
													{
														NodeList nonEnumDispLessThanChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispLessThanChildren.getLength(); i_count10++)
														{
															Node nonEnumDispLessThanChild = nonEnumDispLessThanChildren.item(i_count10);
															System.out.println(nonEnumDispLessThanChild.getNodeName());
															if (nonEnumDispLessThanChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[3] = (Object) getElementValue(nonEnumDispLessThanChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("lessthanorequalto"))
													{
														NodeList nonEnumDispLessThanOrEqualToChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispLessThanOrEqualToChildren.getLength(); i_count10++)
														{
															Node nonEnumDispLessThanOrEqualToChild = nonEnumDispLessThanOrEqualToChildren
																	.item(i_count10);
															System.out.println(nonEnumDispLessThanOrEqualToChild.getNodeName());
															if (nonEnumDispLessThanOrEqualToChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[4] = (Object) getElementValue(nonEnumDispLessThanOrEqualToChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("greaterthan"))
													{
														NodeList nonEnumDispGreaterThanChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispGreaterThanChildren.getLength(); i_count10++)
														{
															Node nonEnumDispGreaterThanChild = nonEnumDispGreaterThanChildren.item(i_count10);
															System.out.println(nonEnumDispGreaterThanChild.getNodeName());
															if (nonEnumDispGreaterThanChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[5] = (Object) getElementValue(nonEnumDispGreaterThanChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("greaterthanorequalto"))
													{
														NodeList nonEnumDispGreaterThanOrEqualToChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispGreaterThanOrEqualToChildren.getLength(); i_count10++)
														{
															Node nonEnumDispGreaterThanOrEqualToChild = nonEnumDispGreaterThanOrEqualToChildren
																	.item(i_count10);
															System.out.println(nonEnumDispGreaterThanOrEqualToChild.getNodeName());
															if (nonEnumDispGreaterThanOrEqualToChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[6] = (Object) getElementValue(nonEnumDispGreaterThanOrEqualToChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("isnull"))
													{
														NodeList nonEnumDispIsNullChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispIsNullChildren.getLength(); i_count10++)
														{
															Node nonEnumDispIsNullChild = nonEnumDispIsNullChildren.item(i_count10);
															System.out.println(nonEnumDispIsNullChild.getNodeName());
															if (nonEnumDispIsNullChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[7] = (Object) getElementValue(nonEnumDispIsNullChild);
														}
													}
													else if (dateCondChild.getNodeName().equalsIgnoreCase("isnotnull"))
													{
														NodeList nonEnumDispNotInChildren = dateCondChild.getChildNodes();
														for (int i_count10 = 0; i_count10 < nonEnumDispNotInChildren.getLength(); i_count10++)
														{
															Node nonEnumDispIsNotNullChild = nonEnumDispNotInChildren.item(i_count10);
															System.out.println(nonEnumDispIsNotNullChild.getNodeName());
															if (nonEnumDispIsNotNullChild.getNodeName().equalsIgnoreCase("displayname"))
																nonEnumDate[8] = (Object) getElementValue(nonEnumDispIsNotNullChild);
														}
													}
												}
											}
											nonEnumDate1[0] = nonEnumDate;
										}
										if (dateChild.getNodeName().equalsIgnoreCase("components"))
										{
											nonEnumDate1[1] = (Object) getElementValue(dateChild);
										}
									}
								}
								nonEnumMap.put((Object) nonEnumChild.getNodeName(), nonEnumDate1);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method parses the nodes of XML file and returns the Document object
	 * @param fileName
	 * @return doc
	 */
	public Document parseFile(String fileName)
	{
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try
		{
			docBuilder = docBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			System.out.println("Wrong parser configuration: " + e.getMessage());
			return null;
		}
		File sourceFile = new File(fileName);
		try
		{
			InputStream inputStream = ParseXMLFile.class.getClassLoader().getResourceAsStream(fileName);
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
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		}
		catch (IOException e)
		{
			System.out.println("Could not read source file: " + e.getMessage());
		}
		return doc;
	}

	/**
	 * Getter method for returning conditions array of Boolean data type
	 * @return enumbool
	 */
	public Object[] getEnumBool()
	{
		return enumBool;
	}

	/**
	 * Getter method for returning conditions array of enumerated number data types 
	 * @return enumNum
	 */
	public Object[] getEnumNum()
	{
		return enumNum;
	}

	/**
	 * Getter method for returning conditions array of enumerated string data types
	 * @return enumStr
	 */
	public Object[] getEnumStr()
	{
		return enumStr;
	}

	/**
	 * Getter method for returning conditions array of Date type.
	 * @return nonEnumDate
	 */
	public Object[] getNonEnumDate()
	{
		return nonEnumDate;
	}

	/**
	 * Getter method for returning conditions array of non-enumerated number data types.
	 * @return nonEnumNum
	 */
	public Object[] getNonEnumNum()
	{
		return nonEnumNum;
	}

	/** 
	 * Getter method for returning conditions array of non-enumerated string data types.
	 * @return nonEnumStr
	 */
	public Object[] getNonEnumStr()
	{
		return nonEnumStr;
	}

	public static void main(String[] args)
	{
		ParseXMLFile parseFile = new ParseXMLFile("dynamicUI.xml");
	}
}