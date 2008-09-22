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

import edu.wustl.common.domain.User;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.common.util.global.SendEmail;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * EmailHandler is used to send emails from the application.
 * @author gautam_shetty
 */
public class EmailHandler
{
	private static org.apache.log4j.Logger logger = Logger.getLogger(EmailHandler.class);
	/**
	 * Sends the email to the administrator regarding the status of the CDE downloading.
	 * i.e. the CDEs successfully downloaded and the error messages in case of errors in downloading the CDEs. 
	 */
	public void sendCDEDownloadStatusEmail(List<String> errorList)
	{
		// Send the status of the CDE downloading to the administrator.
		StringBuffer body = new StringBuffer(TextConstants.TWO_LINE_BREAK).append("Dear Administrator,").append(TextConstants.TWO_LINE_BREAK)
			.append(ApplicationProperties.getValue("email.cdeDownload.body.start")).append(TextConstants.TWO_LINE_BREAK);

		Iterator<String> iterator = errorList.iterator();
		while (iterator.hasNext())
		{
			body.append(iterator.next())
					.append(TextConstants.TWO_LINE_BREAK);
		}

		body.append(ApplicationProperties.getValue("email.catissuecore.team"));
		String subject = ApplicationProperties.getValue("email.cdeDownload.subject");
		boolean emailStatus = sendEmailToAdministrator(subject, body.toString());
		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("cdeDownload.email.success"));
		}
		else
		{
			logger.warn(ApplicationProperties.getValue("cdeDownload.email.failure"));
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
		String sendFrom = XMLPropertyHandler
				.getValue("email.sendEmailFrom.emailAddress");
		String mailServer = XMLPropertyHandler.getValue("email.mailServer");

		StringBuffer emailbody = new StringBuffer(body).append(TextConstants.TWO_LINE_BREAK)
								.append(ApplicationProperties.getValue("loginDetails.catissue.url.message"))
								.append(Variables.catissueURL);
		SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(adminEmailAddress, sendFrom, mailServer,
				subject, emailbody.toString());

		return emailStatus;
	}

	/**
	 * Creates and sends the user registration approval emails to user and the administrator.
	 * @param user The user whose registration is approved.
	 * @param roleOfUser Role of the user.
	 */
	public void sendApprovalEmail(User user) throws DAOException
	{
		String subject = ApplicationProperties.getValue("userRegistration.approve.subject");

		StringBuffer body = new StringBuffer("Dear ").append(user.getLastName()).append(",")
					.append(user.getFirstName()).append(TextConstants.TWO_LINE_BREAK)
					.append(ApplicationProperties.getValue("userRegistration.approved.body.start"))
					.append(getUserDetailsEmailBody(user)); // Get the user details in the body of the email.

		//Send login details email to the user.
		sendLoginDetailsEmail(user, body.toString());

		body.append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("userRegistration.thank.body.end"))
				.append(TextConstants.TWO_LINE_BREAK).append(ApplicationProperties.getValue("email.catissuecore.team"));

		//Send the user registration details email to the administrator.
		boolean emailStatus = sendEmailToAdministrator(subject, body.toString());

		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("user.approve.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			logger.warn(ApplicationProperties.getValue("user.approve.email.failure")
					+ user.getLastName() + " " + user.getFirstName());
		}
	}

	/**
	 * Returns the users details to be incorporated in the email.
	 * @param user The user object.
	 * @return the users details to be incorporated in the email.
	 */
	private String getUserDetailsEmailBody(User user)
	{
		StringBuffer userDetailsBody = new StringBuffer(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.loginName"))
				.append(Constants.SEPARATOR).append(user.getLoginName()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.lastName")).append(Constants.SEPARATOR)
				.append(user.getLastName()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.firstName"))
				.append(Constants.SEPARATOR).append(user.getFirstName()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.street")).append(Constants.SEPARATOR)
				.append(user.getAddress().getStreet()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.city")).append(Constants.SEPARATOR)
				.append(user.getAddress().getCity()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.zipCode")).append(Constants.SEPARATOR)
				.append(user.getAddress().getZipCode()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.state")).append(Constants.SEPARATOR)
				.append(user.getAddress().getState()).append( TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.country")).append( Constants.SEPARATOR)
				.append(user.getAddress().getCountry()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.phoneNumber")).append(Constants.SEPARATOR)
				.append(user.getAddress().getPhoneNumber()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.faxNumber")).append(Constants.SEPARATOR)
				.append(user.getAddress().getFaxNumber()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.emailAddress")).append(Constants.SEPARATOR)
				.append(user.getEmailAddress()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.institution")).append(Constants.SEPARATOR)
				.append(user.getInstitution().getName()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.department")).append(Constants.SEPARATOR)
				.append(user.getDepartment().getName()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("user.cancerResearchGroup")).append(Constants.SEPARATOR)
				.append(user.getCancerResearchGroup().getName());

		return userDetailsBody.toString();
	}

	/**
	 * Creates and sends the user signup request received email to the user and the administrator.
	 * @param user The user registered for the membership.
	 */
	public void sendUserSignUpEmail(User user)
	{
		String subject = ApplicationProperties.getValue("userRegistration.request.subject");

		StringBuffer body = new StringBuffer("Dear ").append(user.getLastName())
				.append(",").append(user.getFirstName()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("userRegistration.request.body.start"))
				.append(TextConstants.LINE_BREAK).append(getUserDetailsEmailBody(user))
				.append(TextConstants.TWO_LINE_BREAK).append(TextConstants.TAB)
				.append(ApplicationProperties.getValue("userRegistration.request.body.end"))
				.append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("email.catissuecore.team"));

		boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject, body.toString());

		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("userRegistration.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			logger.warn(ApplicationProperties.getValue("userRegistration.email.failure")
					+ user.getLastName() + " " + user.getFirstName());
		}
	}

	/**
	 * Creates and sends the login details email to the user.
	 * Returns true if the email is successfully sent else returns false.
	 * @param user The user whose login details are to be sent. 
	 * @param userDetailsBody User registration details.
	 * @return true if the email is successfully sent else returns false.
	 * @throws DAOException
	 */
	public boolean sendLoginDetailsEmail(User user, String userDetailsBody) throws DAOException
	{
		boolean emailStatus = false;

		try
		{
			String subject = ApplicationProperties.getValue("loginDetails.email.subject");

			StringBuffer body= new StringBuffer("Dear ").append(user.getFirstName())
									.append(' ').append(user.getLastName());

			if (userDetailsBody != null)
			{
				body = new StringBuffer(userDetailsBody);
			}

			String roleOfUser = SecurityManager.getInstance(EmailHandler.class).getUserRole(
					user.getCsmUserId().longValue()).getName();
			body.append(TextConstants.TWO_LINE_BREAK)
					.append(ApplicationProperties.getValue("forgotPassword.email.body.start"))
					.append(TextConstants.LINE_BREAK).append(TextConstants.TAB).append(' ')
					.append(ApplicationProperties.getValue("user.loginName")).append(Constants.SEPARATOR)
					.append(user.getLoginName())
					.append(TextConstants.LINE_BREAK).append(TextConstants.TAB).append(' ')
					.append(ApplicationProperties.getValue("user.password")).append(Constants.SEPARATOR)
					.append(PasswordManager.decode(user.getPassword()))
					.append(TextConstants.LINE_BREAK).append(TextConstants.TAB).append(' ')
					.append(ApplicationProperties.getValue("user.role")).append(Constants.SEPARATOR)
					.append(roleOfUser).append(TextConstants.TWO_LINE_BREAK)
					.append(ApplicationProperties.getValue("email.catissuecore.team"));

			emailStatus = sendEmailToUser(user.getEmailAddress(), subject, body.toString());

			if (emailStatus)
			{
				logger.info(ApplicationProperties.getValue("user.loginDetails.email.success")
						+ user.getLastName() + " " + user.getFirstName());
			}
			else
			{
				logger.warn(ApplicationProperties.getValue("user.loginDetails.email.failure")
						+ user.getLastName() + " " + user.getFirstName());
			}
		}
		catch (SMException smExp)
		{
			throw new DAOException(smExp.getMessage(), smExp);
		}

		return emailStatus;
	}

	/**
	 * Sends email to the user with the email address passed.
	 * Returns true if the email is successfully sent else returns false.
	 * @param userEmailAddress Email address of the user.
	 * @param subject The subject of the email. 
	 * @param body The body of the email.
	 * @return true if the email is successfully sent else returns false.
	 */
	private boolean sendEmailToUser(String userEmailAddress, String subject, String body)
	{
		String mailServer = ApplicationProperties.getValue("email.mailServer");
		String sendFrom = ApplicationProperties.getValue("email.sendEmailFrom.emailAddress");

		StringBuffer emailBody = new StringBuffer(body).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("loginDetails.catissue.url.message"))
				.append(Variables.catissueURL);

		SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(userEmailAddress, sendFrom, mailServer,
				subject, emailBody.toString());
		return emailStatus;
	}

	/**
	 * Sends email to the administrator and user with the email address passed.
	 * Returns true if the email is successfully sent else returns false.
	 * @param userEmailAddress Email address of the user.
	 * @param subject The subject of the email. 
	 * @param body The body of the email.
	 * @return true if the email is successfully sent else returns false.
	 */
	private boolean sendEmailToUserAndAdministrator(String userEmailAddress, String subject,
			String body)
	{
		String adminEmailAddress = ApplicationProperties.getValue("email.administrative.emailAddress");
		String sendFrom = ApplicationProperties.getValue("email.sendEmailFrom.emailAddress");
		String mailServer = ApplicationProperties.getValue("email.mailServer");

		StringBuffer emailBody = new StringBuffer(body)
						.append(TextConstants.TWO_LINE_BREAK)
						.append(ApplicationProperties.getValue("loginDetails.catissue.url.message"))
						.append(Variables.catissueURL);

		SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(userEmailAddress, adminEmailAddress, null,
				sendFrom, mailServer, subject, emailBody.toString());
		return emailStatus;
	}

	/**
	 * Creates and sends the user registration rejection emails to user and the administrator.
	 * @param user The user whose registration is rejected.
	 */
	public void sendRejectionEmail(User user)
	{
		String subject = ApplicationProperties.getValue("userRegistration.reject.subject");

		StringBuffer body= new StringBuffer("Dear ").append(user.getLastName()).append(",")
				.append(user.getFirstName()).append(TextConstants.TWO_LINE_BREAK)
				.append(ApplicationProperties.getValue("userRegistration.reject.body.start"));

		//Append the comments given by the administrator, if any.
		if ((user.getComments() != null) && (!"".equals(user.getComments())))
		{
			body.append(TextConstants.TWO_LINE_BREAK)
					.append(ApplicationProperties.getValue("userRegistration.reject.comments"))
					.append(user.getComments());
		}

		body.append(TextConstants.TWO_LINE_BREAK)
			.append(ApplicationProperties.getValue("userRegistration.thank.body.end"))
			.append(TextConstants.TWO_LINE_BREAK)
			.append(ApplicationProperties.getValue("email.catissuecore.team"));

		boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject, body.toString());

		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("user.reject.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			logger.warn(ApplicationProperties.getValue("user.reject.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
	}
}
