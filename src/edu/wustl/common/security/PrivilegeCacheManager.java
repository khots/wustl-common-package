/**
 * PrivilegeCacheManager will manage all the instances of PrivilegeCache. 
 * This will be a singleton. 
 * Instances of PrivilegeCache can be accessed from the instance of PrivilegeCacheManager
 */

package edu.wustl.common.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.wustl.common.action.LoginAction;
import edu.wustl.common.domain.User;


/**
 * @author ravindra_jain
 * creation date : 14th April, 2008
 * @version 1.0
 */
public class PrivilegeCacheManager
{
	/* Singleton instance of PrivilegeCacheManager
	 */
	private static PrivilegeCacheManager instance;

	/* the map of object id and corresponding PrivilegeCache  
	 */
	private Map<String, PrivilegeCache> privilegeCaches;

	// CONSTRUCTOR
	public PrivilegeCacheManager() 
	{
		privilegeCaches = new HashMap<String, PrivilegeCache>();
	}

	/**
	 * to return the Singleton PrivilegeCacheManager instance
	 */
	public static PrivilegeCacheManager getInstance() 
	{
		if(instance==null)
		{
			instance  = new PrivilegeCacheManager();
		}
		return instance;
	}


	/**
	 * to return the PrivilegeCache object from the Map of PrivilegeCaches
	 * 
	 * @param loginName
	 * @return
	 */
	public PrivilegeCache getPrivilegeCache(String loginName)
	{
		return privilegeCaches.get(loginName);
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

		Set<User> users = SecurityManager.getInstance(LoginAction.class).
		getUserProvisioningManager().getUsers(groupName);

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
}
