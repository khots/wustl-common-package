package edu.wustl.common.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import edu.wustl.common.beans.EmailServerProperties;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.EmailDetails;
import edu.wustl.common.util.global.SendEmail;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.common.velocity.VelocityManager;

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
	
	    
    public static void initialize(EmailServerProperties emailServerProps, String subPropsFile, String templPropsFile) {
    	instance = new EmailClient(emailServerProps, subPropsFile, templPropsFile);
    }

    private EmailClient (EmailServerProperties emailServerProps, String subPropsFile, String tmplPropsFile) {
    	try 
    	{
    		InputStream subInputStream = EmailClient.class.getResourceAsStream(subPropsFile);
    		subjectProperties = new Properties();
    		subjectProperties.load(subInputStream);
    		subInputStream.close();
    		
    		InputStream tmplInputStream = EmailClient.class.getResourceAsStream(tmplPropsFile);
    		emailTemplates = new Properties();
    		emailTemplates.load(tmplInputStream);
    		tmplInputStream.close();
    		
    		setServerProperties(emailServerProps);
    		initSubjectPrefix();
    	} catch (Exception e) {
    		new RuntimeException("Error while initializing Email Client", e);
    	}
    }

	public static EmailClient getInstance(){
		return instance;
    }
    
    public boolean sendEmail(String tmplKey, String[] to, Map<String, Object> ctxt) {
    	return sendEmail(tmplKey, to, null, null, ctxt, null);
    }

    public boolean sendEmail(String tmplKey, String[] to, String[] cc, String[] bcc, Map<String, Object> ctxt, String... subStr) 
    { 
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
		
    		email = new SendEmail(mailServer,fromAddress,fromPassword,mailServerPort,isSMTPAuthEnabled,isStartTLSEnabled);
    		emailStatus = email.sendMail(emailDetails);
    	} catch (final MessagingException messExcp){
    		emailStatus=false;
    		LOGGER.error(messExcp.getMessage(),messExcp);
    	} catch (Exception e) {
    		LOGGER.error("Error while sending mail", e);
    	}
    	
    	return emailStatus;
    }
	
    private void initFooter()
    {		
    	try 
    	{
    		Map<String, Object> contextMap = new HashMap<String, Object>();
    		contextMap.put("appUrl", CommonServiceLocator.getInstance().getAppURL());
    		contextMap.put("helpDeskPhone", ApplicationProperties.getValue("login.contactnumber"));
    		footerTemplate = VelocityManager.getInstance().evaluate(contextMap, emailTemplates.getProperty("footerTemplate"));
    	} catch (Exception e) {
    		LOGGER.error("Error while getting footer", e);
    	}		
    }
    
    private void setServerProperties(EmailServerProperties emailServerProps) {
    	mailServer = emailServerProps.getServerHost();
    	mailServerPort = emailServerProps.getServerPort();
    	isSMTPAuthEnabled = emailServerProps.getIsSMTPAuthEnabled();
    	isStartTLSEnabled = emailServerProps.getIsStartTLSEnabled();
    	fromAddress = emailServerProps.getFromAddr();
    	fromPassword = emailServerProps.getFromPassword();
    }
    
    private void initSubjectPrefix() {
    	if (subjectProperties.getProperty("subject.property") != null){
    		subjectPrefix = subjectProperties.getProperty("subject.property");
    	}
    }
}
