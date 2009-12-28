package edu.wustl.common.audit;

import java.util.ArrayList;
import java.util.Collection;

import edu.wustl.common.audit.util.AuditUtil;
import edu.wustl.common.exception.AuditException;

/**
 * This class is the POJO for the details of the objects read from the XML.
 * When auditing the domain object, an instance of this class will provide information
 * as to which attributes of the class would be audited and how will they be treated
 * (as references or containments).
 * @author niharika_sharma
 */
public class AuditableClass {
	
	/**
	 * Class name of the domain object being referred to.
	 */
	String className;
	
	/**
	 * Specifies is this the main class or a contained class.
	 */
	String relationShipType;
	
	/**
	 * In case this class is a contained class, then this attribute refers to the 
	 * property name of this class instance in the containing class. 
	 */
	String roleName;
	
	/**
	 * Class object containing the instance of the domain object.
	 */
	Class klass;
	
	/**
	 * Collection of attributes of the objects that are reference associations.
	 */
	Collection<AuditableClass> referenceAssociationCollection = new ArrayList<AuditableClass>();
	
	/**
	 * Collection of attributes of the objects that are containment associations.
	 */
	Collection<AuditableClass> containmentAssociationCollection = new ArrayList<AuditableClass>();
	
	/**
	 * Collection of simple attributes of the objects.
	 */
	Collection<Attribute> attributeCollection = new ArrayList<Attribute>();
	
	/**
	 * Returns the collection of simple attributes of the object.
	 * @return Collection<Attribute>
	 */
	public Collection<Attribute> getAttributeCollection()
	{
		return attributeCollection;
	}
	
	/**
	 * Sets the collection of simple attributes of the object.
	 * @param attributeCollection
	 */
	public void setAttributeCollection(Collection<Attribute> attributeCollection)
	{
		this.attributeCollection = attributeCollection;
	}

	/**
	 * Returns the collection of reference associations present in the object.
	 * @return Collection<AuditableClass>
	 */
	public Collection<AuditableClass> getReferenceAssociationCollection()
	{
		return referenceAssociationCollection;
	}

	/**
	 * Sets the collection of reference associations present in the object.
	 * @param referenceAssociationCollection
	 */
	public void setReferenceAssociationCollection(
			Collection<AuditableClass> referenceAssociationCollection)
	{
		this.referenceAssociationCollection = referenceAssociationCollection;
	}

	/**
	 * Returns the collection of containment associations present in the object.
	 * @return Collection<AuditableClass>
	 */
	public Collection<AuditableClass> getContainmentAssociationCollection()
	{
		return containmentAssociationCollection;
	}
	
	/**
	 * Sets the collection of containment associations present in the object.
	 * @param containmentAssociationCollection
	 */
	public void setContainmentAssociationCollection(
			Collection<AuditableClass> containmentAssociationCollection)
	{
		this.containmentAssociationCollection = containmentAssociationCollection;
	}
	

	/**
	 * Returns the class name of the object instance.
	 * @return className
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Sets the class name of the object instance.
	 * @param className
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	/**
	 * Returns the relation ShipType of the class.
	 * @return
	 */
	public String getRelationShipType()
	{
		return relationShipType;
	}
	
	/**
	 * Sets the relation ShipType of the class.
	 * @param relationShipType
	 */
	public void setRelationShipType(String relationShipType)
	{
		this.relationShipType = relationShipType;
	}

	
	public String getRoleName() 
	{
		return roleName;
	}
	public void setRoleName(String roleName) 
	{
		this.roleName = roleName;
	}
	
	/**
	 * This method loads the class mentioned in the XML.
	 * @return
	 * @throws AuditException
	 */
	public Class getClassObject() throws AuditException
	{
		
		try
		{
			if(klass==null)
			{
				klass = Class.forName(className);
			}	
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			throw new AuditException(e,null);
		}  
		return  klass;
	}
	
	/**
	 * This method creates an instance of the class mentioned by className in the XML. 
	 * @return
	 * @throws AuditException
	 */
	public Object getNewInstance() throws AuditException
	{
		Object returnObject = null;
		try
		{
			returnObject = Class.forName(className).newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new AuditException(e,null);
		}  
		return  returnObject;
	}
	
	/**
	 * Given the role name, this method invokes the getter method on the object being passed as the parameter.
	 * @param roleName
	 * @param objectOnWhichMethodToInvoke
	 * @return
	 * @throws AuditException
	 */
	public Object invokeGetterMethod(String roleName,Object objectOnWhichMethodToInvoke) throws AuditException
	{
		Object returnObject = null;
		try
		{
			if(objectOnWhichMethodToInvoke!=null)
			{
			String functionName = AuditUtil.getGetterFunctionName(roleName); 
			returnObject = Class.forName(className).getMethod(functionName, null).invoke(objectOnWhichMethodToInvoke, null);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new AuditException(e,null);
		}
		return returnObject;
	}

//	public void invokeSetterMethod(String roleName, Class[]parameterTypes,Object objectOnWhichMethodToInvoke, Object...args) throws AuditException
//	{
//		Object returnObject = null;
//		String functionName = MigrationUtility.getSetterFunctionName(roleName); 
//		try
//		{
//			Class.forName(className).getMethod(functionName, parameterTypes).invoke(objectOnWhichMethodToInvoke, args);
//		}
//		catch(NoSuchMethodException e)
//		{
//			try
//			{
//				Class.forName(className).getMethod(functionName, parameterTypes[0].getSuperclass()).invoke(objectOnWhichMethodToInvoke, args);
//			}
//			catch (NoSuchMethodException e1)
//			{
//				try
//				{
//				Class.forName(className).getMethod(functionName, parameterTypes[0].getSuperclass().getSuperclass()).invoke(objectOnWhichMethodToInvoke, args);
//				}
//				catch(Exception e2)
//				{
//					e2.printStackTrace();
//				}
//				
//			}
//			catch(Exception e4)
//			{
//				e4.printStackTrace();
//			}
//			
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new AuditException(e,null);
//		}
//	}
	
//	public Long invokeGetIdMethod(Object objectOnWhichMethodToInvoke) throws AuditException
//	{
//		Long id  = null;
//		try
//		{
//			id = (Long)Class.forName(className).getMethod("getId", null).invoke(objectOnWhichMethodToInvoke, null);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new AuditException(e,null);
//		}
//		return id;
//	}

//	public void invokeSetIdMethod(Object objectOnWhichMethodToInvoke, Long id) throws AuditException
//	{
//		try
//		{
//			Class.forName(className).getMethod("setId", Long.class).invoke(objectOnWhichMethodToInvoke, id);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new AuditException(e,null);
//		}
//	}
}
