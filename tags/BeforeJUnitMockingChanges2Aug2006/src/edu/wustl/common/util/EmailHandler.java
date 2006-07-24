/**
 * <p>Title: EmailHandler Class>
 * <p>Description:	EmailHandler is used to send emails from the application.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.util;

import java.util.Iterator;
import java.util.List;

import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.SendEmail;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;


/**
 * EmailHandler is used to send emails from the application.
 * @author gautam_shetty
 */
public class EmailHandler
{
    /**
     * Sends the email to the administrator regarding the status of the CDE downloading.
     * i.e. the CDEs successfully downloaded and the error messages in case of errors in downloading the CDEs. 
     */
    public void sendCDEDownloadStatusEmail(List errorList)
    {
        // Send the status of the CDE downloading to the administrator.
        String body = "Dear Administrator," + "\n\n" + ApplicationProperties.getValue("email.cdeDownload.body.start") + "\n\n";
        
        Iterator iterator = errorList.iterator();
        while (iterator.hasNext())
        {
            body = body + iterator.next() + "\n\n";
        }
        
        body = "\n\n" + body + ApplicationProperties.getValue("email.catissuecore.team");
        
        String subject = ApplicationProperties.getValue("email.cdeDownload.subject");
        
        boolean emailStatus = sendEmailToAdministrator(subject, body);
        
        if (emailStatus)
		{
			Logger.out.info(ApplicationProperties
			    .getValue("cdeDownload.email.success"));
		}
		else
		{
			Logger.out.info(ApplicationProperties
			    .getValue("cdeDownload.email.failure"));
		}
    }
    
    
    /**
     * Sends email to the adminstrator.
     * Returns true if the email is successfully sent else returns false.
     * @param subject The subject of the email. 
     * @param body The body of the email.
     * @return true if the email is successfully sent else returns false.
     */
    private boolean sendEmailToAdministrator(String subject, String body)
    {
        String adminEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
        String sendFromEmailAddress = XMLPropertyHandler.getValue("email.sendEmailFrom.emailAddress");
        String mailServer = XMLPropertyHandler.getValue("email.mailServer");
        
        body = body + "\n\n" + ApplicationProperties.getValue("loginDetails.catissue.url.message") + Variables.catissueURL;
        
        SendEmail email = new SendEmail();
        boolean emailStatus = email.sendmail(adminEmailAddress, 
                								sendFromEmailAddress, mailServer, subject, body);
        
        return emailStatus;
    }
}
