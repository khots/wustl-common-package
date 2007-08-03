/*
 * Created on June 9, 2005
 * Last Modified : July 6, 2005.
 */

package edu.wustl.common.cde;

/**
 * @author mandar_deshmukh
 *
 */
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import edu.wustl.common.cde.xml.XMLCDE;
import edu.wustl.common.cde.xml.impl.XMLCDEImpl;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.cadsr.domain.DataElement;
import gov.nih.nci.cadsr.domain.DataElementConcept;
import gov.nih.nci.cadsr.domain.EnumeratedValueDomain;
import gov.nih.nci.cadsr.domain.ObjectClass;
import gov.nih.nci.cadsr.domain.PermissibleValue;
import gov.nih.nci.cadsr.domain.ValueDomain;
import gov.nih.nci.cadsr.domain.ValueDomainPermissibleValue;
import gov.nih.nci.cadsr.domain.impl.DataElementImpl;
import gov.nih.nci.cadsr.domain.impl.ObjectClassImpl;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.cadsr.umlproject.domain.*;
import gov.nih.nci.cadsr.umlproject.domain.impl.UMLClassMetadataImpl;

public class CDEDownloader
{
	public static int MAX_SERVER_CONNECT_ATTEMPTS = 3;//Integer.parseInt(ApplicationProperties.getValue("max.server.connect.attempts"));
	public static int MAX_CDE_DOWNLOAD_ATTEMPTS = 3;//Integer.parseInt(ApplicationProperties.getValue("max.cde.download.attempts"));
	private ApplicationService appService;
	
	//Mandar : 05-Apr-06 : Bugid:1622 : Removed throws Exception
	public CDEDownloader() 
	{
	} // CDEDownloader constructor
	
	private void init() throws Exception
	{
		// creates the passwordauthenticaton object to be used for establishing 
		// the connection with the databse server.
		CDEConConfig.dbserver = "http://cabio.nci.nih.gov/cacore30/server/HTTPServer";
		//http://cabio.nci.nih.gov/cacore32/http/remoteService
		//if(XMLPropertyHandler.getValue("use.proxy.server").equals("true"))
		//{
			setCDEConConfig();
			createPasswordAuthentication(CDEConConfig.proxyhostip, CDEConConfig.proxyport, CDEConConfig.username, CDEConConfig.password);
		//} // use.proxy.server = true
	} // init
	
	/**
	 * @param proxyhost is used to specify the host to connect 
	 * @param proxyport is used to specify the port to be used for connection
	 * @param username is used to specify the local username
	 * @param password is used to specify the local password
	 * @param dbserver is used to specify the database url to connect 
	 * @return returns true if the connection with the dtabase server is successful.
	 *  sets the ApplicationService object if successful.
	 * This method will be used for establishing the connection with the server.
	 */
	public void connect() throws Exception
	{
		//Mandar : 05-Apr-06 Bugid: 1622 : Added call to init() after removing it from constructor
		init();
		
		int connectAttempts = 0;
		while(connectAttempts < MAX_SERVER_CONNECT_ATTEMPTS)
		{
			try
			{
				appService = ApplicationService.getRemoteInstance(CDEConConfig.dbserver);
				
				if(appService!=null)
					return;
			} //try
			catch (Exception conexp)
			{
				Logger.out.error("Connection Error: "+conexp.getMessage(), conexp);
			} // catch
			connectAttempts++;
		} // while connectAttempts < MAX_SERVER_CONNECT_ATTEMPTS
		throw new Exception("Connection Error: Unable to connect to "+CDEConConfig.dbserver);
	} // connect
	
	/**
	 * @param cdeCon It is the Configuration object. Contains all required
	 *  information  for connection.
	 * @param cdePublicId It is the public id of the cde to download.
	 * @param vocabularyName evs database to connect
	 * @return an CDE with all the PermissibleValues. 
	 * 
	 * @throws Exception
	 */
	public void downloadCDE(UMLClassMetadata umlClassMetaData) throws Exception
	{
	    Logger.out.info("Downloading CDE "+umlClassMetaData.getName());
	    
	    int downloadAttempts = 0;
		while(downloadAttempts < MAX_CDE_DOWNLOAD_ATTEMPTS)
		{
			try
			{
				 retrieveDataElement(umlClassMetaData);

			}
			catch (Exception conexp)
			{
				Logger.out.error("CDE Download Error: "+conexp.getMessage(), conexp);
			}
			downloadAttempts++;
		} // while downloadAttempts < MAX_CDE_DOWNLOAD_ATTEMPTS
		throw new Exception("CDE Download Error: Unable to download CDE "+umlClassMetaData.getName());
	} // downloadCDE
	public CDE downloadCDE(XMLCDE xmlCDE) throws Exception
	{
	    Logger.out.info("Downloading CDE "+xmlCDE.getName());
	    
	    int downloadAttempts = 0;
		while(downloadAttempts < MAX_CDE_DOWNLOAD_ATTEMPTS)
		{
			try
			{
				retrieveDataElement(xmlCDE);
				
		
			}
			catch (Exception conexp)
			{
				Logger.out.error("CDE Download Error: "+conexp.getMessage(), conexp);
			}
			downloadAttempts++;
		} // while downloadAttempts < MAX_CDE_DOWNLOAD_ATTEMPTS
		throw new Exception("CDE Download Error: Unable to download CDE "+xmlCDE.getName());
	} // downloadCDE
	/**
	 * @param CDEPublicID PublicID of the CDE to download
	 * @return the CDE if available or null if cde not available
	 * @throws Exception
	 */
	private CDE retrieveDataElement(XMLCDE xmlCDE) throws Exception
	{
		//Create the dataelement and set the dataEelement properties
		DataElement dataElementQuery = new DataElementImpl();
		
		// flag for whether to load the cde using PublicID or not.
		boolean loadByPublicID = Boolean.getBoolean("FALSE");
		
		if(loadByPublicID)
		{
			dataElementQuery.setPublicID(new Long(xmlCDE.getPublicId()));
		} // loadByPublicID = true
		else
		{
			dataElementQuery.setPreferredName(xmlCDE.getName());
		} // else loadByPublicID = false
		
		dataElementQuery.setLatestVersionIndicator(Constants.BOOLEAN_YES );
		
		List resultList = appService.search(DataElement.class, dataElementQuery);
		
		// check if any cde exists with the given public id.
		if (resultList!=null && !resultList.isEmpty())
		{
			//retreive the Data Element for the given search condition
			DataElement dataElement = (DataElement) resultList.get(0);
			
			//Mandar : bug1622: Use of parameterised constructor 
			// create the cde object and set the values.
			CDEImpl cdeobj = new CDEImpl(dataElement.getPublicID().toString(),dataElement.getPreferredName(),
					dataElement.getLongName(),dataElement.getPreferredDefinition(),
					dataElement.getVersion().toString(),dataElement.getDateModified());
			
			Logger.out.debug("CDE Public Id : "+cdeobj.getPublicId());
			Logger.out.debug("CDE Def : "+cdeobj.getDefination());
			Logger.out.debug("CDE Long Name : "+cdeobj.getLongName());
			Logger.out.debug("CDE Version : "+cdeobj.getVersion());
			Logger.out.debug("CDE Perferred Name : "+cdeobj.getPreferredName());
			Logger.out.debug("CDE Last Modified Date : "+cdeobj.getDateLastModified());
			
			//Access the permissible value.
			ValueDomain valueDomain = dataElement.getValueDomain();
			Logger.out.debug("valueDomain class : " + valueDomain.getClass());
			
			if(valueDomain instanceof EnumeratedValueDomain)
			{
				EnumeratedValueDomain enumValueDomain = (EnumeratedValueDomain)valueDomain;
				
			    Collection permissibleValueCollection = enumValueDomain.getValueDomainPermissibleValueCollection();
				Set permissibleValues = getPermissibleValues(permissibleValueCollection);
				cdeobj.setPermissibleValues(permissibleValues);
			} // valueDomain instanceof EnumeratedValueDomain
			
			return cdeobj;
		} // resultList!=null && !resultList.isEmpty()
		else //no Data Element retreived
		{
			return null;
		}
	} // retrieveDataElement
	/**
	 * @param CDEPublicID PublicID of the CDE to download
	 * @return the CDE if available or null if cde not available
	 * @throws Exception
	 */
	private void retrieveDataElement(UMLClassMetadata umlClassMetadata) throws Exception
	{
		//Create the dataelement and set the dataEelement properties
		//DataElement dataElementQuery = new DataElementImpl();
		List resultList = appService.search(UMLClassMetadata.class, umlClassMetadata);
		UMLClassMetadata resultClass = (UMLClassMetadata)resultList.get(0);
		Collection semanticMetadataCollection = (Collection)resultClass.getSemanticMetadataCollection() ;
		Iterator semanticMetadataCollectionItr = semanticMetadataCollection.iterator();
		// check if any cde exists with the given public id.
		while(semanticMetadataCollectionItr.hasNext())
		{
			//retreive the Data Element for the given search condition
			SemanticMetadata semanticMetadata = (SemanticMetadata)semanticMetadataCollectionItr.next();
			
			//Mandar : bug1622: Use of parameterised constructor 
			// create the cde object and set the values.
//			CDEImpl cdeobj = new CDEImpl(dataElement.getPublicID().toString(),dataElement.getPreferredName(),
//					dataElement.getLongName(),dataElement.getPreferredDefinition(),
//					dataElement.getVersion().toString(),dataElement.getDateModified());
			
			Logger.out.debug("Concept code : "+semanticMetadata.getConceptCode());
			Logger.out.debug("Concept Def : "+semanticMetadata.getConceptDefinition());
			Logger.out.debug("concept Name : "+semanticMetadata.getConceptName());
		}
	}
			//Access the permissible value.
			/*ValueDomain valueDomain = dataElement.getValueDomain();
			Logger.out.debug("valueDomain class : " + valueDomain.getClass());
			
			if(valueDomain instanceof EnumeratedValueDomain)
			{
				EnumeratedValueDomain enumValueDomain = (EnumeratedValueDomain)valueDomain;
				
			    Collection permissibleValueCollection = enumValueDomain.getValueDomainPermissibleValueCollection();
				Set permissibleValues = getPermissibleValues(permissibleValueCollection);
				cdeobj.setPermissibleValues(permissibleValues);
			} // valueDomain instanceof EnumeratedValueDomain
			
			return cdeobj;
		} // resultList!=null && !resultList.isEmpty()
		else //no Data Element retreived
		{
			return null;
		}
	} // retrieveDataElement
	
	
	/**
	 * Returns the Set of Permissible values from the collection of value domains. 
	 * @param valueDomainCollection The value domain collection. 
	 * @param cde The CDE to which this permissible values belong. 
	 * @return the Set of Permissibel values from the collection of value domains.
	 */
	private Set getPermissibleValues(Collection valueDomainCollection)
	{
	    Logger.out.debug("Value Domain Size : "+valueDomainCollection.size());
	    
	    Set permissibleValuesSet = new HashSet();
	    
	    Iterator iterator = valueDomainCollection.iterator();
	    while(iterator.hasNext())
	    {
	        ValueDomainPermissibleValue valueDomainPermissibleValue = (ValueDomainPermissibleValue) iterator.next();
	        
	        //caDSR permissible value object
	        PermissibleValue permissibleValue = (PermissibleValue) valueDomainPermissibleValue.getPermissibleValue();
	        
	        //Create the instance of catissue permissible value
	        edu.wustl.common.cde.PermissibleValueImpl cachedPermissibleValue = new PermissibleValueImpl();
	        
	        cachedPermissibleValue.setConceptid(permissibleValue.getId());
	        cachedPermissibleValue.setValue(permissibleValue.getValue());
	        
	        Logger.out.debug("Concept ID : "+cachedPermissibleValue.getConceptid());
	        Logger.out.debug("Value : "+cachedPermissibleValue.getValue());
	        permissibleValuesSet.add(cachedPermissibleValue);
	    } // while iterator
	    return permissibleValuesSet;
	} // getPermissibleValues
	
	/**
	 * @param proxyhost  url of the database to connect
	 * @param proxyport port to be used for connection
	 * @param username Username of the local system  
	 * @param password Password of the local system
	 * @throws Exception
	 * This method which accepts the local username,password, 
	 * proxy host and proxy port to create a PasswordAuthentication 
	 * that will be used for establishing the connection.
	 */
	private void createPasswordAuthentication(String proxyhost,
				String proxyport,String username,String password) throws Exception
	{
		/**
		 * username is a final variable for the username.  
		 */
		final String localusername = username;
		
		/**
		 * password is a final variable for the password.  
		 */
		final String localpassword = password;
		
		/**
		 * authenticator is an object of Authenticator used for 
		 * authentication of the username and password.   
		 */
		Authenticator authenticator = new Authenticator()
		{

			protected PasswordAuthentication getPasswordAuthentication()
			{
				// sets http authentication 
				return new PasswordAuthentication(localusername, localpassword.toCharArray());
			}	
		};
		// setting the authenticator
		Authenticator.setDefault(authenticator);
		
		/**
		 * Checking the proxy port for validity
		 */
		boolean validnum = CommonUtilities.checknum(proxyport, 0, 0);
		
		if (validnum == false)
		{
			//Logger.out.info("Invalid Proxy Port: " + proxyport);
			throw new Exception("Invalid ProxyPort");
		} // validnum == false
		
		// setting the system settings
		System.setProperty("proxyHost", proxyhost);
		System.setProperty("proxyPort", proxyport);
	} //createPasswordAuthentication
	
	private void setCDEConConfig()
	{
		CDEConConfig.proxyhostip ="ptproxy.persistent.co.in";
		CDEConConfig.proxyport = "8080";
		CDEConConfig.password = "";
		CDEConConfig.username = "";
	} // setCDEConConfig
	
	public static void main(String []args) throws Exception
	{
		ApplicationProperties.initBundle("ApplicationResources");
		
		// to remove the Logger configuration from the file
		Variables.applicationHome = System.getProperty("user.dir");
		Logger.out = org.apache.log4j.Logger.getLogger("");
		PropertyConfigurator.configure(Variables.applicationHome+"\\src\\"+"ApplicationResources.properties");
		
//		XMLCDE xmlCDE = new XMLCDEImpl();
//		xmlCDE.setName("PT_RACE_CAT");
//		xmlCDE.setPublicId("106");
		UMLClassMetadata umlClassMetaData = new UMLClassMetadataImpl();
		umlClassMetaData.setName("Participant");
//		Project project=new Project();
//		project.setLongName("caTissue_Core");
//		project.setVersion("v1.1");
//		umlClassMetaData.setProject(project);
		
		CDEDownloader cdeDownloader = new CDEDownloader();
		cdeDownloader.connect();
		//CDE cde = cdeDownloader.downloadCDE(xmlCDE);
		cdeDownloader.downloadCDE(umlClassMetaData);
	} 	
}