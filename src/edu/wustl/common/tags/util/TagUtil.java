package edu.wustl.common.tags.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.util.EmailClient;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import gov.nih.nci.security.authorization.domainobjects.User;

public class TagUtil 
{
	public static void sendSharedTagEmailNotification(Long userId, List<Tag> tags, 
			Set<Long> selectedUserSet, String tmplKey) throws Exception 
	{
		ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
		User currentUser = securityManager.getUserById(userId.toString());
		
		Map<String,Object> contextMap = new HashMap<String,Object>();
		contextMap.put("user", currentUser);
		contextMap.put("sharedTags", tags);
		 
		for (Long sUserId : selectedUserSet)
		{
			User sharedUser =  securityManager.getUserById(sUserId.toString());
			contextMap.put("sharedUser", sharedUser);
			EmailClient.getInstance().sendEmail(
					 tmplKey, 
					 new String[]{ sharedUser.getEmailId() }, 
					 contextMap);
		}
	}

}
