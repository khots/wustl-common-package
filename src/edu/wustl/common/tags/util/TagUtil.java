package edu.wustl.common.tags.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.util.EmailClient;
import gov.nih.nci.security.authorization.domainobjects.User;

public class TagUtil 
{
	public static void sendSharedTagEmailNotification(User currentUser, List<Tag> tags, 
			Set<User> selectedUsers, String tmplKey) throws Exception 
	{	
		Map<String,Object> contextMap = new HashMap<String,Object>();
		contextMap.put("user", currentUser);
		contextMap.put("sharedTags", tags);
		 
		for (User selectedUser : selectedUsers)
		{
			contextMap.put("sharedUser", selectedUser);
			EmailClient.getInstance().sendEmail(
					 tmplKey, 
					 new String[]{ selectedUser.getEmailId() }, 
					 contextMap);
		}
	}

}
