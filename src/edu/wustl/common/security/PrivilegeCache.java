/**
 * PrivilegeCache will cache all privileges for a specific user. 
 * An instance of PrivilegeCache will be created for every user who logs in.
 */

package edu.wustl.common.security;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.Permissions;
import gov.nih.nci.security.authorization.ObjectPrivilegeMap;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author ravindra_jain
 * creation date : 14th April, 2008
 * @version 1.0
 */
public class PrivilegeCache
{
	/* login name of the user who has
	 * logged in
	 */
	private String loginName;

	/* the map of object id and corresponding permissions / privileges  
	 */
	private Map<String, BitSet> privilegeMap;

	/* SecurityManager object to use the various
	 * functions implemented by the SecurityManager
	 */
	private SecurityManager securityManager;


	// CONSTRUCTOR
	/**
	 * After initialization of the variables, 
	 * a call to function 'initialize()' is made 
	 * initialize() uses some ProtectionElementSearchCriterias & gets 
	 * Protection Elements from the database
	 * 
	 * @throws Exception
	 */
	public PrivilegeCache(String loginName) throws Exception
	{
		privilegeMap = new HashMap<String, BitSet>();
		securityManager = SecurityManager.getInstance(this.getClass());
		this.loginName = loginName; 

		initialize();
	}	


	/**
	 * This method gets Protection Elements we are interested in, from the database 
	 * We achieve this by using the ProtectionElementSearchCriteria provided by CSM
	 * Then, a call to getPrivilegeMap is made & we get a Collection of ObjectPrivilegeMap for each call
	 * Here, for every passed ProtectionElement,the method looks for the privileges that User has on the ProtectionElement
	 * such ObjectPrivilegeMaps are then passed to 'populatePrivileges' method
	 * 
	 * @throws Exception
	 */
	public void initialize() throws Exception
	{
		String userId = securityManager.getUser(loginName).getUserId().toString(); 

		try{
			String xmlFileName = "CacheableObjects.xml";
			InputStream inputXmlFile = this.getClass().getClassLoader().getResourceAsStream(xmlFileName);

			if (inputXmlFile != null)
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(inputXmlFile);

				Element root = null;
				root = doc.getDocumentElement();

				if(root==null)
				{
					throw new Exception("file can not be read");
				}

				NodeList nodeList =root.getElementsByTagName("Class");

				int length = nodeList.getLength();

				long startTime = System.currentTimeMillis();

				for(int counter=0; counter<length; counter++)
				{
					Element element = (Element)(nodeList.item(counter));
					String temp = new String(element.getAttribute("name"));
					ProtectionElement protectionElement = new ProtectionElement();
					protectionElement.setObjectId(temp);
					ProtectionElementSearchCriteria protectionElementSearchCriteria = new ProtectionElementSearchCriteria(protectionElement);
					List list = securityManager.getUserProvisioningManager().getObjects(protectionElementSearchCriteria);
					int size = list.size();
					if(list.size()>0)
					{
						Collection objectPrivilegeMap = securityManager.getUserProvisioningManager().getPrivilegeMap(loginName, list);
						populatePrivileges(objectPrivilegeMap);
					}
				}

				NodeList nodeList1 =root.getElementsByTagName("ObjectType");

				int length1 = nodeList1.getLength();

				for(int counter=0; counter<length1; counter++)
				{
					Element element = (Element)(nodeList1.item(counter));
					String temp = new String(element.getAttribute("pattern"));
					ProtectionElement protectionElement = new ProtectionElement();
					protectionElement.setObjectId(temp);
					ProtectionElementSearchCriteria protectionElementSearchCriteria = new ProtectionElementSearchCriteria(protectionElement);
					List list = securityManager.getUserProvisioningManager().getObjects(protectionElementSearchCriteria);
					Collection objectPrivilegeMap = securityManager.getUserProvisioningManager().getPrivilegeMap(loginName, list);
					populatePrivileges(objectPrivilegeMap);
				}

				long endTime = System.currentTimeMillis();
				long time = endTime - startTime;
			}
		}
		catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}  
	}	


	/** 
	 * populatePrivileges does the Mapping (inserts into Map) of the 
	 * Object Id's and corresponding BitSets. 
	 * 
	 * BitSet is used for the Mapping. There are total 10 possible Privileges / Permissions.
	 * So, we use 10 bits of bitset for storing these Permissions
	 * For every objectId in PrivilegeMap, a bit '1' in BitSet indicates user has permission on that Privilege
	 * 									 & a bit '0' indicates otherwise
	 * So, in this method, we examine each Privilege returned by PrivilegeMap & 
	 * set the BitSet corresponding to the objectId accordingly.
	 * 
	 * @param objectPrivilegeMapCollection
	 */
	public void populatePrivileges(Collection<ObjectPrivilegeMap> objectPrivilegeMapCollection)
	{
		// To populate the permissionMap
		for (ObjectPrivilegeMap objectPrivilegeMap : objectPrivilegeMapCollection)
		{
			String objectId = objectPrivilegeMap.getProtectionElement().getObjectId();

			BitSet bitSet = new BitSet(10);

			for (Object privilege : objectPrivilegeMap.getPrivileges())
			{
				bitSet.set(getBitNumber(((Privilege) privilege).getName()));
			}
			privilegeMap.put(objectId, bitSet);
		}
	}


	/**
	 * To check for 'Class' & 'Action' level Privilege 
	 * Here, we take className as the objectId & retrieve its associated 
	 * BitSet from the privilegeMap
	 * Then, we check whether User has Permission over the passed Privilege or no
	 * & return true if user has privilege, false otherwise. 
	 * 
	 * @param classObj
	 * @param privilegeName
	 * @return
	 */
	public boolean hasPrivilege(Class classObj, String privilegeName)
	{
		BitSet bitSet = privilegeMap.get(classObj.getName());

		return bitSet.get(getBitNumber(privilegeName));
	}


	/**
	 * To check for 'Object' level Privilege 
	 * Here, we take objectId from Object & retrieve its associated 
	 * BitSet from the privilegeMap
	 * Then, we check whether User has Permission over the passed Privilege or no
	 * & return true if user has privilege, false otherwise. 
	 * 
	 * @param aDObject
	 * @param privilegeName
	 * @return
	 */
	public boolean hasPrivilege(AbstractDomainObject aDObject, String privilegeName)
	{
		BitSet bitSet = privilegeMap.get(aDObject.getObjectId());

		return bitSet.get(getBitNumber(privilegeName));
	}


	/**
	 * To check for Privilege given the ObjectId 
	 * Here, we take objectId & retrieve its associated 
	 * BitSet from the privilegeMap
	 * Then, we check whether User has Permission over the passed Privilege or no
	 * & return true if user has privilege, false otherwise. 
	 * 
	 * @param objectId
	 * @param privilegeName
	 * @return
	 */	
	public boolean hasPrivilege(String objectId, String privilegeName)
	{
		BitSet bitSet = privilegeMap.get(objectId);

		return bitSet.get(getBitNumber(privilegeName));
	}


	public void updatePrivilege(Class classObj, String privilegeName, boolean value)
	{
		BitSet bitSet = privilegeMap.get(classObj.getName());

		bitSet.set(getBitNumber(privilegeName), value);
	}


	public void updatePrivilege(AbstractDomainObject aDObject, String privilegeName, boolean value)
	{
		BitSet bitSet = privilegeMap.get(aDObject.getObjectId());

		bitSet.set(getBitNumber(privilegeName), value);
	}


	public void updatePrivilege(String objectId, String privilegeName, boolean value)
	{
		BitSet bitSet = privilegeMap.get(objectId);

		bitSet.set(getBitNumber(privilegeName), value);
	}

	/**
	 * This method is used to refresh the Privilege Cache for the user
	 * A call to this method forces CSM to go to the database & get the ProtectionElements
	 * For more, please refer to the 'initialize' method above
	 * 
	 * @throws Exception
	 */	
	public void refresh() throws Exception
	{ 
		initialize();
	}


	public void addObject(String objectId, List<String> privilegeNames)
	{
		BitSet bitSet = new BitSet(10);

		for(String privilegeName : privilegeNames)
		{
			bitSet.set(getBitNumber(privilegeName));
		}
		privilegeMap.put(objectId, bitSet);
	}


	private int getBitNumber(String privilegeName)
	{
		if (Permissions.READ.equals(privilegeName))
		{
			return 0;
		}
		if (Permissions.READ_DENIED.equals(privilegeName))
		{
			return 1;
		}
		if (Permissions.UPDATE.equals(privilegeName))
		{
			return 2;
		}
		if (Permissions.DELETE.equals(privilegeName))
		{
			return 3;
		}
		if (Permissions.CREATE.equals(privilegeName))
		{
			return 4;
		}
		if (Permissions.EXECUTE.equals(privilegeName))
		{
			return 5;
		}
		if (Permissions.USE.equals(privilegeName))
		{
			return 6;
		}
		if (Permissions.ASSIGN_READ.equals(privilegeName))
		{
			return 7;
		}
		if (Permissions.ASSIGN_USE.equals(privilegeName))
		{
			return 8;
		}
		if (Permissions.IDENTIFIED_DATA_ACCESS.equals(privilegeName))
		{
			return 9;
		}
		return 0;
	}
}