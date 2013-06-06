/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/*
 * Created on Oct 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wustl.common.security;

import net.sf.hibernate.SessionFactory;
import edu.wustl.common.security.dao.AuthorizationDAOImpl;
import gov.nih.nci.security.system.ApplicationSessionFactory;


/**
 * @author aarti_sharma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UserProvisioningManagerImpl extends
		gov.nih.nci.security.provisioning.UserProvisioningManagerImpl {

	/**
	 * @param arg0
	 * @throws Exception
	 */
	public UserProvisioningManagerImpl(String arg0) throws Exception {
		super(arg0);
		
	}
	
	/**
	 * @param arg0
	 * @throws Exception
	 */
	public UserProvisioningManagerImpl() throws Exception {
		super(SecurityManager.CATISSUE_CORE_CONTEXT_NAME);
		SessionFactory sf = ApplicationSessionFactory.getSessionFactory(SecurityManager.CATISSUE_CORE_CONTEXT_NAME);
		super.setAuthorizationDAO(new AuthorizationDAOImpl(sf,"catissuecore"));
	}
	
	

	

}
