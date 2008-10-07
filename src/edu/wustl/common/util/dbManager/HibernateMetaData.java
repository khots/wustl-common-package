/*
 * Created on Sep 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.util.dbManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.proxy.HibernateProxy;

import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * @author kapil_kaveeshwar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HibernateMetaData
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(HibernateMetaData.class);
	
	/**
	 * cfg Configuration - Hibernate configuration.
	 */
	private static Configuration cfg;
	/**
	 * mappings HashSet - set to keep relations.
	 */
	private static Set<ClassRelationshipData> mappings = new HashSet<ClassRelationshipData>();

	private static final int DEFAULT_COL_WIDTH=50;
	/**
	 * @param configuration Configuration Hibernate configuration.
	 */
	public static void initHibernateMetaData(Configuration configuration)
	{
		cfg = configuration;
	}

	/**
	 * This method returns the list of subclasses of the className.
	 * @author aarti_sharma
	 * @param className String class name.
	 * @return List of sub class.
	 * @throws ClassNotFoundException exception of class not found.
	 */
	public static List getSubClassList(String className) throws ClassNotFoundException
	{
		List<String> list = new ArrayList<String>();
		Class classObj = Class.forName(className);
		PersistentClass classobj1 = cfg.getClassMapping(classObj.getName());
		Iterator<Subclass> iterator = classobj1.getDirectSubclasses();
		while (iterator.hasNext())
		{
			Subclass subClass = (Subclass) iterator.next();
			list.add(subClass.getClassName());
		}
		return list;
	}

	/**
	 * This method returns the super class of the object passed.
	 * @author aarti_sharma
	 * @param obj Object object of class.
	 * @return Class class
	 */
	public static Class getSuperClass(Object obj)
	{
		Class objClass = obj.getClass();
		PersistentClass persistentClass = cfg.getClassMapping(objClass.getName());
		PersistentClass superClass = persistentClass.getSuperclass();
		return superClass.getClass();
	}

	/**
	 * This method returns the super most class
	 * of the class passed that is in the same package as class.
	 * @author aarti_sharma
	 * @param obj Object.
	 * @return Class class
	 */
	public static Class getSupermostClassInPackage(Object obj)
	{
		Class objClass = obj.getClass();
		PersistentClass persistentClass = cfg.getClassMapping(objClass.getName());
		if (persistentClass != null && persistentClass.getSuperclass() != null)
		{
			do
			{
				persistentClass = persistentClass.getSuperclass();
			}
			while (persistentClass != null);

			objClass=persistentClass.getMappedClass();
		}
		return objClass;
	}

	/**
	 * @param classObj Class object of class.
	 * @return String name of table.
	 */
	public static String getTableName(Class classObj)
	{
		String tableName=TextConstants.EMPTY_STRING;
		Table tbl = cfg.getClassMapping(classObj.getName()).getTable();
		if (tbl != null)
		{
			tableName=tbl.getName();
		}
		return tableName;
	}

	/**
	 * @param classObj Class object of class.
	 * @return String root table name for the object passed.
	 */
	public static String getRootTableName(Class classObj)
	{
		String rootTabName=TextConstants.EMPTY_STRING;
		Table tbl = cfg.getClassMapping(classObj.getName()).getRootTable();
		if (tbl != null)
		{
			rootTabName=tbl.getName();
		}
		return rootTabName;
	}

	/**
	 * Return the class name for the table name passed.
	 * @param tableName String name of table.
	 * @return String name of class.
	 */
	public static String getClassName(String tableName)
	{
		String className=TextConstants.EMPTY_STRING;
		Iterator iterator = cfg.getClassMappings();
		PersistentClass persistentClass;
		while (iterator.hasNext())
		{
			persistentClass = (PersistentClass) iterator.next();
			if (tableName.equalsIgnoreCase(persistentClass.getTable().getName()))
			{
				className=persistentClass.getClassName();
				break;
			}
		}

		return className;
	}

	/**
	 * @param classObj Class object of class.
	 * @param attributeName String name of attribute.
	 * @return String the column name for the object and attribute passed.
	 */
	public static String getColumnName(Class classObj, String attributeName)
	{
		String colName=TextConstants.EMPTY_STRING;
		Iterator<Property> iterator = cfg.getClassMapping(classObj.getName()).getPropertyClosureIterator();
		boolean gotColName=false;
		while (iterator.hasNext())
		{
			Property property = (Property) iterator.next();
			if (property != null && property.getName().equals(attributeName))
			{
				Iterator<Column> colIt = property.getColumnIterator();
				while (colIt.hasNext())
				{
					Column col = (Column) colIt.next();
					colName=col.getName();
					gotColName=true;
					break;
				}
			}
			if(gotColName)
			{
				break;
			}
		}

		Property property = cfg.getClassMapping(classObj.getName()).getIdentifierProperty();
		if (property.getName().equals(attributeName))
		{
			Iterator colIt = property.getColumnIterator();
			while (colIt.hasNext())
			{
				Column col = (Column) colIt.next();
				colName= col.getName();
				break;
			}
		}

		return colName;
	}

	/**
	 * @return String database name.
	 */
	public static String getDataBaseName()
	{
		String dbName = "";
		String dialect = cfg.getProperty("hibernate.dialect");
		if (dialect.toLowerCase().indexOf("oracle") != -1)
		{
			dbName = Constants.ORACLE_DATABASE;
		}
		else if (dialect.toLowerCase().indexOf("mysql") != -1)
		{
			dbName = Constants.MYSQL_DATABASE;
		}
		else if (dialect.toLowerCase().indexOf("postgresql") != -1)
		{
			dbName = Constants.POSTGRESQL_DATABASE;
		}
		return dbName;
	}

	/**
	 * Return data of the object passed.
	 * @param classObj Class object of class.
	 */
	public static void getDATA(Class classObj)
	{
		org.hibernate.mapping.Collection coll = cfg.getCollectionMapping(
		  "edu.wustl.catissuecore.domain.CollectionProtocolEvent.specimenRequirementCollection");
		
		Iterator iterator = coll.getColumnIterator();
		while (iterator.hasNext())
		{
			//org.hibernate.mapping.Set set = (org.hibernate.mapping.Set)it.next();
		
		}
	}

	/**
	 * This Function finds all the relations in i.e Many-To-Many and One-To-Many
	 * All the relations are kept in HashMap where key is formed as table1@table2@table_name@attributeName.
	 * and value is Many-To-Many or One-To-Many
	 */
	private static void findRelations()
	{
		try
		{
			Iterator itr1 = cfg.getCollectionMappings();

			while (itr1.hasNext())
			{
				Collection col = (Collection) itr1.next();

				if (col.getElement().getClass().getName().equals(ManyToOne.class.getName()))
				{
					saveRelations(col, "ManyToMany");
				}
				else
				{
					saveRelations(col, "OneToMany");
				}
			}
		}
		catch (ClassNotFoundException exception)
		{
			logger.debug("Not able to save the relation.",exception);
		}

	}

	/**This function saves the relation data in HashSet.
	 * @param col this is the collection which contains all data
	 * @param relType this is Many-To-Many to Many-To-One
	 * @throws ClassNotFoundException
	 */
	private static void saveRelations(Collection col, String relType) throws ClassNotFoundException
	{

		ClassRelationshipData classRelationshipData = new ClassRelationshipData(col, relType);
		mappings.add(classRelationshipData);
		ClassRelationshipData hmc = classRelationshipData;
		String className;
		List list1 = HibernateMetaData.getSubClassList(col.getOwner().getClassName());
		for (int i = 0; i < list1.size(); i++)
		{
//			hmc = new ClassRelationshipData(list1.get(i).toString(), relatedClassName,
//					roleAttribute, relationType, relationTable, keyId, roleId);
			className = list1.get(i).toString();
			hmc = createNewClassRelShip(hmc, className);

			mappings.add(hmc);
		}

		List list2 = HibernateMetaData.getSubClassList(col.getElement().getType().getName());
		String relatedClassName;
		for (int i = 0; i < list2.size(); i++)
		{
			relatedClassName = list2.get(i).toString();
			className =classRelationshipData.getClassName();
			hmc = createNewClassRelShip(classRelationshipData, className, relatedClassName);
			mappings.add(hmc);
		}
	}

	private static ClassRelationshipData createNewClassRelShip(ClassRelationshipData classRelationshipData,
			String className, String relatedClassName)
	{
		ClassRelationshipData hmc;
		hmc = new ClassRelationshipData(className, relatedClassName, classRelationshipData);
		return hmc;
	}

	private static ClassRelationshipData createNewClassRelShip(ClassRelationshipData hmc, String className)
	{
		return  new ClassRelationshipData(className, hmc);

	}

	/** This function checks weather relation is Many-Many.
	 *
	 * @param classObj1 Class object of class.
	 * @param classObj2 Class object of class.
	 * @param roleAttributeName String name of role attribute.
	 * @return boolean true is relation is Many-Many otherwise false.
	 */
	public static boolean isRelationManyToMany(Class classObj1, Class classObj2,
			String roleAttributeName)
	{
		boolean isManyToMay=false;
		ClassRelationshipData crd = new ClassRelationshipData(classObj1.getName(), classObj2
				.getName(), roleAttributeName);
		Iterator itr = mappings.iterator();

		while (itr.hasNext())
		{
			ClassRelationshipData crd1 = (ClassRelationshipData) itr.next();
			if (crd1.equals(crd) && crd1.getRelationType().equals("ManyToMany"))
			{
				isManyToMay= true;
				break;
			}
		}
		return isManyToMay;
	}
	/** This function returns the relation between the classes passed.
	 *
	 * @param classObj1 Class object of class.
	 * @param classObj2 Class object of class.
	 * @param roleAttributeName String name of role attribute.
	 * @return ClassRelationshipData the relation.
	 */

	public static ClassRelationshipData getClassRelationshipData(Class classObj1, Class classObj2,
			String roleAttributeName)
	{
		ClassRelationshipData crd = new ClassRelationshipData(classObj1.getName(), classObj2
				.getName(), roleAttributeName);
		Iterator<ClassRelationshipData> itr = mappings.iterator();
		ClassRelationshipData crdRetValue=null;
		ClassRelationshipData crd1=null;
		while (itr.hasNext())
		{
			crd1 = (ClassRelationshipData) itr.next();
			if (crd1.equals(crd))
			{
				crdRetValue=crd1;
				break;
			}
		}
		return crdRetValue;
	}

	/**This function returns the Role class for given attribute name.
	 * @param attName String name of attribute.
	 * @return RoleClass class of role.
	 */
	public static Class getRoleClass(String attName)
	{
		Class roleClass=null;
		Iterator<ClassRelationshipData> itr = mappings.iterator();
		while (itr.hasNext())
		{
			ClassRelationshipData crd = (ClassRelationshipData) itr.next();
			if (crd.getRoleAttribute().indexOf(attName) != -1)
			{
				roleClass= Utility.getClassObject(crd.getRelatedClassName());
				break;
			}
		}
		return roleClass;
	}

	/** This function returns the qualified attribute name related to concerned objects passed as parameters.
	 * @param classObj1 Class object of class.
	 * @param classObj2 Class object of class.
	 * @param attName String name of attribute.
	 * @return String name of  role attribute.
	 */
	public static String getFullyQualifiedRoleAttName(Class classObj1, Class classObj2,String attName)
	{
		String roleAttName=null;
		Iterator<ClassRelationshipData> itr = mappings.iterator();
		while (itr.hasNext())
		{
			ClassRelationshipData crd = (ClassRelationshipData) itr.next();
			if (classObj1.getName().equals(crd.getClassName())
					&& classObj2.getName().equals(crd.getRelatedClassName())
					&& crd.getRoleAttribute().indexOf(attName) != -1)
			{
				roleAttName=crd.getRoleAttribute();
				break;
			}
		}
		return roleAttName;
	}

	/**
	 * This function gets the key Id
	 * from hibernate mappings and returns the value.
	 * @param attributeName String name of attribute.
	 * @return String key Id.
	 *
	 */
	public static String getKeyId(String attributeName)
	{
		String keyId=TextConstants.EMPTY_STRING;
		org.hibernate.mapping.Collection col1 = cfg.getCollectionMapping(attributeName);
		Iterator<Column> keyIt = col1.getKey().getColumnIterator();
		while (keyIt.hasNext())
		{
			Column col = (Column) keyIt.next();
			keyId=col.getName();
			break;
		}
		return keyId;
	}

	/** This function returns the key Id of role, for the attribute passed.
	 * from hibernate mapping and returns the value
	 * @param attributeName name of attribute.
	 * @return String role key Id.
	 *
	 */
	public static String getRoleKeyId(String attributeName)
	{
		String roleKeyId=TextConstants.EMPTY_STRING;
		org.hibernate.mapping.Collection col1 = cfg.getCollectionMapping(attributeName);
		Iterator<Column> colIt = col1.getElement().getColumnIterator();
		if(colIt.hasNext())
		{
			Column col = (Column) colIt.next();
			roleKeyId=(col.getName());
		}
		return roleKeyId;
	}

	/**
	 * Return the column width for the parameters class and attribute.
	 * @param classObj Name of the class.
	 * @param attributeName Name of the attribute.
	 * @return The width of the column. Returns width of the column or zero.
	 */
	public static int getColumnWidth(Class classObj, String attributeName)
	{
		int colWidth=DEFAULT_COL_WIDTH;
		Iterator<Property> iterator = cfg.getClassMapping(classObj.getName()).getPropertyClosureIterator();
		while (iterator.hasNext())
		{
			Property property = (Property) iterator.next();

			if (property != null && property.getName().equals(attributeName))
			{
				Iterator<Column> colIt = property.getColumnIterator();
				if(colIt.hasNext())
				{
					Column col = (Column) colIt.next();
					colWidth=col.getLength();
					break;
				}
			}
		}
		// if attribute is not found than the default width will be 50.
		return colWidth;
	}

	/**
	 * This method will return proxy object from domain Object.
	 * @param domainObject Object object of domain.
	 * @return Object domain.
	 */
	public static Object getProxyObjectImpl(Object domainObject)
	{
		Object proxyObj=domainObject;
		if (domainObject instanceof HibernateProxy)
		{
			HibernateProxy hibernatProxy = (HibernateProxy) domainObject;
			Object obj = hibernatProxy.getHibernateLazyInitializer().getImplementation();
			proxyObj=(AbstractDomainObject) obj;
		}
		return proxyObj;
	}

	/**
	 * This function configure the properties file.
	 * @param args String[]
	 * @throws Exception generic exception.
	 */
	public static void main(String[] args) throws Exception
	{
		Variables.applicationHome = System.getProperty("user.dir");
		PropertyConfigurator.configure(Variables.applicationHome + "\\WEB-INF\\src\\"
				+ "ApplicationResources.properties");
		DBUtil.currentSession();
	}
}