
package edu.wustl.common.util;

/**
 * @author Kapil Kaveeshwar
 */
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.hibernate.Hibernate;

import edu.wustl.common.query.Table;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;

public class MapDataParser
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(MapDataParser.class);
	private String packageName = "";
	private Map bigMap = new HashMap();
	private Collection dataList = new LinkedHashSet();

	public MapDataParser(String packageName)
	{
		this.packageName = packageName;
	}

	private Map<String,String> createMap()
	{
		Map<String,String> map = new TreeMap<String,String>();
		map.put("DistributedItem:1_Specimen_id", "1");
		map.put("DistributedItem:1_quantity", "100");
		map.put("DistributedItem:1_Specimen_className", "Tissue");
		map.put("DistributedItem:2_Specimen_id", "2");
		map.put("DistributedItem:2_quantity", "200");
		map.put("DistributedItem:2_Specimen_className", "Molecular");
		return map;
	}

	private Object toObject(String str, Class type) throws Exception
	{
		Object obj;
		if (type.equals(String.class))
		{
			obj=str;
		}
		else
		{
			if (TextConstants.EMPTY_STRING.equals(str))
			{
				obj=null;
			}
			else if (type.equals(Long.class))
			{
				obj=Long.valueOf(str);
			}
			else if (type.equals(Double.class))
			{
				obj=Double.valueOf(str);
			}
			else if (type.equals(Float.class))
			{
				obj=Float.valueOf(str);
			}
			else if (type.equals(Integer.class))
			{
				obj=Integer.valueOf(str);
			}
			else if (type.equals(Byte.class))
			{
				obj=Integer.valueOf(str);
			}
			else if (type.equals(Short.class))
			{
				obj=Integer.valueOf(str);
			}
			else if (type.equals(Table.class))
			{
				obj=new Table(str, str);
			}
			else if (type.equals(Boolean.class))
			{
				obj=Boolean.valueOf(str);
			}
			else if (type.equals(Date.class))
			{
				obj=Utility.parseDate(str, Utility.datePattern(str));
			}
			else if (type.equals(Blob.class))
			{
				File file = new File(str);
				DataInputStream dis = new DataInputStream(new BufferedInputStream(
						new FileInputStream(file)));

				byte[] buff = new byte[(int) file.length()];
				dis.readFully(buff);
				dis.close();
				obj=Hibernate.createBlob(buff);
			}
		}
		obj=str;
		return obj;
	}

	private Method findMethod(Class objClass, String methodName) throws Exception
	{
		Method methdName=null;
		Method[] method = objClass.getMethods();
		for (int i = 0; i < method.length; i++)
		{
			if (method[i].getName().equals(methodName))
			{
				methdName=method[i];
				break;
			}
		}
		return methdName;
	}

	private void parstData(Object parentObj, String str, String value, String parentKey)
			throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(str, "_");

		int tokenCount = tokenizer.countTokens();
		if (tokenCount > 1)
		{
			String className = tokenizer.nextToken();
			String mapKey = new StringBuffer(parentKey).append('-').append(str.substring(0, str.indexOf('_'))).toString();
			Object obj = parseClassAndGetInstance(parentObj, className, mapKey);

			if (tokenCount == 2)
			{
				String attrName = tokenizer.nextToken();
				String methodName = Utility.createAccessorMethodName(attrName, true);
				Class objClass = obj.getClass();
				Method method = findMethod(objClass, methodName);
				Object []objArr = {toObject(value, method.getParameterTypes()[0])};
				method.invoke(obj, objArr);
			}
			else
			{
				int firstIndex = str.indexOf('_');
				className = str.substring(firstIndex + 1);
				parstData(obj, className, value, mapKey);
			}
		}
	}

	private Object parseClassAndGetInstance(Object parentObj, String str, String mapKey)
			throws Exception
	{
		StringTokenizer innerST = new StringTokenizer(str, ":");
		String className = "";
		Object obj;
		int count = innerST.countTokens();
		if (count == 2) //Case obj is a collection
		{
			className = innerST.nextToken();
			String index = innerST.nextToken();
			Collection collection = null;
			if (parentObj == null)
			{
				collection = dataList;
				StringTokenizer tokenizer = new StringTokenizer(className, "#");
				if (tokenizer.countTokens() > 1)
				{
					tokenizer.nextToken();
					className = tokenizer.nextToken();
				}
			}
			else
			{
				String collectionName = className;
				StringTokenizer tokenizer = new StringTokenizer(className, "#");
				if (tokenizer.countTokens() > 1)
				{
					collectionName = tokenizer.nextToken();
					className = tokenizer.nextToken();
				}
				collection = getCollectionObj(parentObj, collectionName);
			}

			obj=getObjFromList(collection, index, className, mapKey);
		}
		else
		{
			className = str;
			StringTokenizer tokenizer = new StringTokenizer(className, "#");
			if (tokenizer.countTokens() > 1)
			{
				className = tokenizer.nextToken();
			}
			Object retObj = Utility.getValueFor(parentObj, className);
			if (retObj == null)
			{
				retObj = Utility.setValueFor(parentObj, className, null);
			}
			obj=retObj;
		}
		return obj;
	}

	private Object getObjFromList(Collection coll, String index, String className, String mapKey)
			throws Exception
	{
		Object obj = bigMap.get(mapKey);
		if (obj == null)
		{
			String qualifiedClassName = packageName + "." + className;
			Class aClass = Class.forName(qualifiedClassName);
			obj = aClass.newInstance();
			coll.add(obj);
			bigMap.put(mapKey, obj);
		}
		return obj;
	}

	private Collection getCollectionObj(Object parentObj, String str) throws Exception
	{
		String attrName = str + "Collection";
		return (Collection) Utility.getValueFor(parentObj, attrName);
	}

	public Collection generateData(Map map, boolean isOrdered) throws Exception
	{
		if (isOrdered)
		{
			dataList = new ArrayList();
		}
		return generateData(map);
	}

	public Collection generateData(Map map) throws Exception
	{
		Iterator it = map.keySet().iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			String value = (String) map.get(key);
			parstData(null, key, value, "KEY");
		}
		return dataList;
	}

	public int parseKeyAndGetRowNo(String key)
	{
		int start = key.indexOf(':');
		int end = key.indexOf('_');
		return Integer.parseInt(key.substring(start + 1, end));
		
	}

	public static void main(String[] args) throws Exception
	{
		MapDataParser aMapDataParser = new MapDataParser("edu.wustl.catissuecore.domain");
		Map map = aMapDataParser.createMap();
		//map = aMapDataParser.fixMap(map);
		Collection dataCollection = aMapDataParser.generateData(map);
		logger.info(dataCollection);
	}

	public static void deleteRow(List list, Map map, String status)
	{
		deleteRow(list, map, status, null);
	}

	/**
	 * Returns boolean used for diabling/enabling checkbox in jsp Page and
	 * rearranging rows
	 */
	public static void deleteRow(List<String> list, Map map, String status, String outer)
	{

		//whether delete button is clicked or not
		boolean isDeleteClicked=true;
		if (status == null)
		{
			isDeleteClicked=Boolean.getBoolean(Constants.FALSE);
		}
		else
		{
			isDeleteClicked=Boolean.getBoolean(status);
		}

		String text;
		for (int k = 0; k < list.size(); k++)
		{
			text = (String) list.get(k);
			String first = text.substring(0, text.indexOf(':'));
			String second = text.substring(text.indexOf('_'));

			//condition for creating ids for innerTable
			boolean condition = false;
			String third = "", fourth = "";

			//checking whether key is inneTable'key or not
			if (second.indexOf(':') != -1)
			{
				condition = true;
				third = second.substring(0, second.indexOf(':'));
				fourth = second.substring(second.lastIndexOf('_'));
			}

			if (isDeleteClicked)
			{
				Map values = map;

				//for outerTable
				int outerCount = 1;

				//for innerTable
				int innerCount = 1;
				for (int i = 1; i <= values.size(); i++)
				{
					String id = "";
					String mapId = "";
					//for innerTable key's rearrangement
					if (condition)
					{
						if (outer == null)
						{
							//for outer key's rearrangement
							for (int j = 1; j <= values.size(); j++)
							{
								id = first + ":" + i + third + ":" + j + fourth;
								mapId = first + ":" + outerCount + third + ":" + j + fourth;

								//checking whether map from form contains keys or not
								if (values.containsKey(id))
								{
									values.put(mapId, map.get(id));
									outerCount++;
								}
							}
						}
						else
						{
							id = first + ":" + outer + third + ":" + i + fourth;
							mapId = first + ":" + outer + third + ":" + innerCount + fourth;
						}

					}
					else
					{
						id = first + ":" + i + second;
						mapId = first + ":" + innerCount + second;
					}

					//rearranging key's
					if (values.containsKey(id))
					{
						values.put(mapId, map.get(id));
						innerCount++;
					}
				}
			}
		}
	}
}