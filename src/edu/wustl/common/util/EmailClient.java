package edu.wustl.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.EmailDetails;
import edu.wustl.common.util.global.SendEmail;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.common.velocity.VelocityManager;
import edu.wustl.common.util.XMLPropertyHandler;

public class EmailClient {

    private static EmailClient instance = null;
    private static final Logger LOGGER = LoggerConfig.getConfiguredLogger(EmailClient.class);
    
    private static Properties emailTemplates = null;
    private static Properties subjectProperties = null;
    private static String footerTemplate = null; 
    private static String subjectPrefix = "";
    private static String mailServer = null;
    private static String mailServerPort = null;
    private static String isSMTPAuthEnabled = null;
    private static String isStartTLSEnabled = null;
    private static String fromAddress = null;
    private static String fromPassword = null;
    private static boolean localMailSend;
	
    private EmailClient() {
    	initEmailclient();
    }
    
    public static EmailClient getInstance(){
    	if(instance == null) {
    		instance = new EmailClient();
    	}
    	return instance;
    }
    
    public boolean sendEmail(String tmplKey, String[] to, Map<String, Object> ctxt) {
    	return sendEmail(tmplKey, to, null, null, ctxt, null);
    }
    
    public boolean sendEmailWithAttachment(String tmplKey, String[] to, String[] cc, String[] bcc, List<File> attachments, Map<String, Object> ctxt, String... subStr){
    	if(footerTemplate == null){
    		initFooter();
    	}
  
    	ctxt.put("footer", footerTemplate);
    	final EmailDetails emailDetails= new EmailDetails();
    	SendEmail email;
    	boolean emailStatus = false;
    	try
    	{   
    		if(subStr == null){
    			emailDetails.setSubject(subjectPrefix+""+subjectProperties.getProperty(tmplKey));
    		} else {
    			emailDetails.setSubject(subjectPrefix+""+String.format(subjectProperties.getProperty(tmplKey),subStr));
    		}
			
    		String emailTemplate = emailTemplates.getProperty(tmplKey);
    		if (emailTemplate == null) {
    			LOGGER.error("could not find e-mail template"+ tmplKey);
    			return emailStatus;
    		}
    	
    		String emailBody = VelocityManager.getInstance().evaluate(ctxt, emailTemplate);
    		
    		emailDetails.setHtmlBody(true);
    		emailDetails.setBody(emailBody);
    		
    		emailDetails.setToAddress(to);
    		if(cc != null){
    			emailDetails.setCcAddress(cc);
    		}
        
    		if(bcc != null){
    			emailDetails.setBccAddress(bcc);
    		}
    		if(attachments!=null && !attachments.isEmpty()){
    			emailDetails.setAttachments(attachments);
    		}
    		
    		if (!localMailSend){
    			email = new SendEmail(mailServer,fromAddress,fromPassword,mailServerPort,isSMTPAuthEnabled,isStartTLSEnabled);
    		} else {
    			email = new SendEmail(mailServer,fromAddress);
    		}  		
    		emailStatus = email.sendMail(emailDetails);
    	} catch (final MessagingException messExcp){
    		emailStatus=false;
    		LOGGER.error(messExcp.getMessage(),messExcp);
    	} catch (Exception e) {
    		LOGGER.error("Error while sending mail", e);
    	}
    	
    	return emailStatus;
    }

    public boolean sendEmail(String tmplKey, String[] to, String[] cc, String[] bcc, Map<String, Object> ctxt, String... subStr) 
    { 
    	return sendEmailWithAttachment(tmplKey, to, cc, bcc, null, ctxt, subStr);
    }
	
    private void initFooter()
    {		
    	try 
    	{
    		Map<String, Object> contextMap = new HashMap<String, Object>();
    		contextMap.put("appUrl", CommonServiceLocator.getInstance().getAppURL());
    		contextMap.put("helpDeskPhone", XMLPropertyHandler.getValue("contact.number"));
    		footerTemplate = VelocityManager.getInstance().evaluate(contextMap, emailTemplates.getProperty("footerTemplate"));
    	} catch (Exception e) {
    		LOGGER.error("Error while getting footer", e);
    	}		
    }
   
    private static void initEmailclient() {
    	fromAddress = XMLPropertyHandler.getValue("email.sendEmailFrom.emailAddress");
    	fromPassword = XMLPropertyHandler.getValue("email.sendEmailFrom.emailPassword");
    	isSMTPAuthEnabled = XMLPropertyHandler.getValue("email.smtp.auth.enabled");
    	mailServer = XMLPropertyHandler.getValue("email.mailServer");
    	mailServerPort = XMLPropertyHandler.getValue("email.mailServer.port");
    	isStartTLSEnabled = XMLPropertyHandler.getValue("email.smtp.starttls.enabled");
    	
    	String propPath = CommonServiceLocator.getInstance().getPropDirPath();
    	String subPropFile = propPath+"/email-templates/emailSubjects.properties";
    	String tmplPropFile = propPath+"/email-templates/emailTemplates.properties";
		
    	try {
    		InputStream subInputStream = new FileInputStream(new File(subPropFile));
    		subjectProperties = new Properties();
    		subjectProperties.load(subInputStream);
    		subInputStream.close();
    		
    		InputStream tmplInputStream = new FileInputStream(new File(tmplPropFile));
    		emailTemplates = new Properties();
    		emailTemplates.load(tmplInputStream);
    		tmplInputStream.close();
    		
    		if (subjectProperties.getProperty("subject.property") != null){
    			subjectPrefix = subjectProperties.getProperty("subject.property");
    		}
    		
    		if (isSMTPAuthEnabled != null && isSMTPAuthEnabled.equals("true") &&
    			isStartTLSEnabled != null && isStartTLSEnabled.equals("true") &&
    			fromPassword != null && ! fromPassword.isEmpty()) 
    		{
    			localMailSend = false;
    		} else {
    			localMailSend = true;
    		}
    		
    	} catch (Exception e) {
    		new RuntimeException("Error while initializing Email Client", e);
    	}
    }
}
