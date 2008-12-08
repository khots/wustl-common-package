/**
 * PrivilegeCacheManager will manage all the instances of PrivilegeCache. 
 * This will be a singleton. 
 * Instances of PrivilegeCache can be accessed from the instance of PrivilegeCacheManager
 */

package edu.wustl.common.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.util.Permissions;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.ObjectPrivilegeMap;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.exceptions.CSException;


/**
 * @author ravindra_jain
 * creation date : 14th April, 2008
 * @version 1.0
 */
public class PrivilegeManager
{
	/* Singleton instance of PrivilegeCacheManager
	 */
	private static PrivilegeManager instance;

	/* the map of object id and corresponding PrivilegeCache  
	 */
	private Map<String, PrivilegeCache> privilegeCaches;

	private PrivilegeUtility privilegeUtility;
	
	// CONSTRUCTOR
	public PrivilegeManager() 
	{
		privilegeUtility = new PrivilegeUtility();
		privilegeCaches = new HashMap<String, PrivilegeCache>();
	}

	/**
	 * to return the Singleton PrivilegeCacheManager instance
	 */
	public static PrivilegeManager getInstance() 
	{
		if(instance==null)
		{
			instance  = new PrivilegeManager();
		}
		return instance;
	}


	/**
	 * to return the PrivilegeCache object from the Map of PrivilegeCaches
	 * 
	 * @param loginName
	 * @return
	 * @throws Exception 
	 */
	public PrivilegeCache getPrivilegeCache(String loginName)
	{
		PrivilegeCache privilegeCache = privilegeCaches.get(loginName);
		if(privilegeCache == null)
		{
			try {
				privilegeCache = new PrivilegeCache(loginName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 privilegeCaches.put(loginName, privilegeCache);
		}
		return privilegeCache;
	}


	/**
	 * To get PrivilegeCache objects for all users
	 * belonging to a particular group
	 * 
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public List<PrivilegeCache> getPrivilegeCaches(String groupName) throws Exception
	{
		List<PrivilegeCache> listOfPrivilegeCaches = new Vector<PrivilegeCache>();

		Set<User> users = privilegeUtility.getUserProvisioningManager().getUsers(groupName);

		for(User user : users)
		{
			PrivilegeCache privilegeCache = privilegeCaches.get(user.getLoginName());

			if(privilegeCache!= null)
			{
				listOfPrivilegeCaches.add(privilegeCache);
			}
		}

		return listOfPrivilegeCaches;
	}

	/**
	 * To get all PrivilegeCache objects 
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<PrivilegeCache> getPrivilegeCaches() throws Exception
	{
		return privilegeCaches.values();
	}
	
	
	/**
	 *  to add the PrivilegeCache object to the Map of PrivilegeCaches
	 * 
	 * @param loginName
	 * @param privilegeCache
	 */
	public void addPrivlegeCache(String loginName, PrivilegeCache privilegeCache)
	{
		privilegeCaches.put(loginName, privilegeCache);
	}


	/**
	 * This method will generally be called from CatissueCoreSesssionListener.sessionDestroyed 
	 * in order to remove the corresponding PrivilegeCache from the Session
	 * 
	 * @param loginName
	 */
	public void removePrivilegeCache(String loginName)
	{
		privilegeCaches.remove(loginName);
	}
	
	
	/**
	 * This Utility method is called dynamically as soon as a 
	 * Site or CollectionProtocol object gets created through the UI
	 * & adds detials regarding that object to the PrivilegeCaches of
	 * appropriate users in Session
	 * 
	 * @param objectId
	 */
	private void addObjectToPrivilegeCaches(String objectId)
	{
		PrivilegeManager privilegeCacheManager = PrivilegeManager.getInstance();
		try 
		{
			Collection<PrivilegeCache> listOfPrivilegeCaches = privilegeCacheManager.getPrivilegeCaches();

			ProtectionElement protectionElement = privilegeUtility.getUserProvisioningManager().getProtectionElement(objectId);

			Collection<ProtectionElement> protectionElements = new Vector<ProtectionElement>();
			protectionElements.add(protectionElement);

			for(PrivilegeCache privilegeCache : listOfPrivilegeCaches)
			{
				Collection<ObjectPrivilegeMap> objectPrivilegeMapCollection = privilegeUtility.
				getUserProvisioningManager().getPrivilegeMap(privilegeCache.getLoginName(), protectionElements);

				if(objectPrivilegeMapCollection.size()>0)
				{
					privilegeCache.addObject(objectId, objectPrivilegeMapCollection.iterator().next().getPrivileges());
				}
			}
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public void insertAuthorizationData(Vector authorizationData, Set protectionObjects, String[] dynamicGroups, String objectId)
	{
			PrivilegeUtility utility = new PrivilegeUtility();
			try 
			{
				utility.insertAuthorizationData(authorizationData, protectionObjects, dynamicGroups);
			} 
			catch (SMException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			addObjectToPrivilegeCaches(objectId);
	}
	
	
	/**
	 * Used to Update the privilege of a group 
	 * both in the Cache as well as in the database 
	 * after user (admin) selects Assign Privilege option for group
	 * @param privilegeName
	 * @param objectType
	 * @param objectIds
	 * @param roleId
	 * @param assignOperation
	 * @throws Exception
	 */
	public void updateGroupPrivilege(String privilegeName, Class objectType,
			Long[] objectIds, String roleId, boolean assignOperation) throws Exception
	{
		PrivilegeUtility utility = new PrivilegeUtility();
		Collection<PrivilegeCache> listOfPrivilegeCaches = null;
		String groupId = utility.getGroupIdForRole(roleId);
		
		Set<User> users = utility.getUserProvisioningManager().getUsers(groupId);
		
		for(User user : users)
		{
			listOfPrivilegeCaches = PrivilegeManager.getInstance().getPrivilegeCaches();

			for(PrivilegeCache privilegeCache : listOfPrivilegeCaches)
			{
				if(privilegeCache.getLoginName().equals(user.getLoginName()))
				{
					privilegeCache.updatePrivilege(objectType.getName()+"_"+objectIds[0], privilegeName, assignOperation);
				}
			}
			assignPrivilegeToGroup(privilegeName, objectType, objectIds, roleId, assignOperation);
		}	
	}
	
	
	/**
	 * This method assigns privilege by privilegeName to the user group
	 * identified by role corresponding to roleId on the objects identified by
	 * objectIds
	 * 
	 * @param privilegeName
	 * @param objectIds
	 * @param roleId
	 * @throws SMException
	 */
	private void assignPrivilegeToGroup(String privilegeName, Class objectType,
			Long[] objectIds, String roleId, boolean assignOperation) throws SMException 
			{
		PrivilegeUtility utility = new PrivilegeUtility();

		Logger.out.debug("privilegeName:" + privilegeName + " objectType:"
				+ objectType + " objectIds:"
				+ edu.wustl.common.util.Utility.getArrayString(objectIds) + " roleId:" + roleId);
		if (privilegeName == null || objectType == null || objectIds == null
				|| roleId == null) {
			Logger.out
			.debug("Cannot assign privilege to user. One of the parameters is null.");
		} else {
			String groupId;
			UserProvisioningManager userProvisioningManager;
			String protectionGroupName = null;
			String roleName;
			Role role;
			List list;
			ProtectionGroup protectionGroup;
			try
			{
				//Get user group for the corresponding role
				groupId = utility.getGroupIdForRole(roleId);
				userProvisioningManager = utility.getUserProvisioningManager();

				//Getting Appropriate Role
				//role name is generated as <<privilegeName>>_ONLY
				if (privilegeName.equals(Permissions.READ))
					roleName = Permissions.READ_DENIED;
				else
					roleName = privilegeName + "_ONLY";
				role = utility.getRole(roleName);

				Set roles = new HashSet();
				roles.add(role);

				if (privilegeName.equals("USE"))
				{
					protectionGroupName = "PG_GROUP_" + groupId + "_ROLE_" + role.getId();

					if (assignOperation == Constants.PRIVILEGE_ASSIGN)
					{
						protectionGroup = utility.getProtectionGroup(protectionGroupName);

						Logger.out.debug("Assign Protection elements");

						//Assign Protection elements to Protection Group
						utility.assignProtectionElements(protectionGroup
								.getProtectionGroupName(), objectType, objectIds);

						utility.assignGroupRoleToProtectionGroup(Long.valueOf(groupId), roles,
								protectionGroup, assignOperation);
					}
					else
					{
						Logger.out.debug("De Assign Protection elements");

						utility.deAssignProtectionElements(protectionGroupName, objectType, objectIds);
					}
				}
				else
				{
					Logger.out.debug("Value Before#####################"+assignOperation);

					// In case of assign remove the READ_DENIED privilege of the group
					// and in case of de-assign add the READ_DENIED privilege to the group.
					assignOperation = ! assignOperation;

					Logger.out.debug("Value After#####################"+assignOperation);

					for (int i = 0; i < objectIds.length;i++)
					{

						//Getting Appropriate Group
						// Protection Group Name is generated as
						// PG_<<userID>>_ROLE_<<roleID>>
						//				protectionGroupName = "PG_GROUP_" + groupId + "_ROLE_"
						//						+ role.getId();
						Logger.out.debug("objectType............................"+objectType);

						// Commented by Ashwin --- remove dependency on domainobject package

						if (objectType.getName().equals(Constants.COLLECTION_PROTOCOL_CLASS_NAME))
							protectionGroupName = Constants.getCollectionProtocolPGName(objectIds[i]);
						else if (objectType.getName().equals(Constants.DISTRIBUTION_PROTOCOL_CLASS_NAME))
							protectionGroupName = Constants.getDistributionProtocolPGName(objectIds[i]);


						protectionGroup = utility.getProtectionGroup(protectionGroupName);

						Logger.out.debug("Assign Group Role To Protection Group");

						//Assign User Role To Protection Group
						utility.assignGroupRoleToProtectionGroup(Long.valueOf(groupId), roles,
								protectionGroup, assignOperation);
					}
				}

			} catch (CSException csex) {
				throw new SMException(csex);
			}
		}
			}
	
	
	/**
	 * This is a temporary method written for StorageContainer - special case
	 * Used for StorageContainerBizLogic.isDeAssignable() method
	 * 
	 * @param roleId
	 * @param objectId
	 * @param privilegeName
	 * @return
	 */
	public boolean hasGroupPrivilege(String roleId, String objectId, String privilegeName) throws Exception
	{
		PrivilegeUtility utility = new PrivilegeUtility();
		String groupId = utility.getGroupIdForRole(roleId);
		
		Set<User> users = utility.getUserProvisioningManager().getUsers(groupId);
		
		for(User user : users)
		{
			if(!getPrivilegeCache(user.getLoginName()).hasPrivilege(objectId, privilegeName))
				return false;		
		}
		
		return true;
	}
}