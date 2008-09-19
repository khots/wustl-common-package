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
		String body = new StringBuffer().append("Dear Administrator,").append("\n\n")
			.append(ApplicationProperties.getValue("email.cdeDownload.body.start")).append("\n\n").toString();

		Iterator<String> iterator = errorList.iterator();
		while (iterator.hasNext())
		{
			body = new StringBuffer().append(body).append(iterator.next())
					.append("\n\n").toString();
		}

		body = new StringBuffer().append("\n\n").append(body)
				.append(ApplicationProperties.getValue("email.catissuecore.team")).toString();
		String subject = ApplicationProperties.getValue("email.cdeDownload.subject");
		boolean emailStatus = sendEmailToAdministrator(subject, body);
		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("cdeDownload.email.success"));
		}
		else
		{
			logger.info(ApplicationProperties.getValue("cdeDownload.email.failure"));
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

		String emailbody = new StringBuffer().append(body).append("\n\n").append(ApplicationProperties.getValue("loginDetails.catissue.url.message")).
				append(Variables.catissueURL).toString();

		SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(adminEmailAddress, sendFrom, mailServer,
				subject, emailbody);

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

		String body = new StringBuffer().append("Dear ").append(user.getLastName()).append(",")
					.append(user.getFirstName()).append("\n\n")
					.append(ApplicationProperties.getValue("userRegistration.approved.body.start"))
					.append(getUserDetailsEmailBody(user)).toString(); // Get the user details in the body of the email.

		//Send login details email to the user.
		sendLoginDetailsEmail(user, body);

		body = new StringBuffer().append(body).append("\n\n")
				.append(ApplicationProperties.getValue("userRegistration.thank.body.end"))
				.append("\n\n").append(ApplicationProperties.getValue("email.catissuecore.team")).toString();

		//Send the user registration details email to the administrator.
		boolean emailStatus = sendEmailToAdministrator(subject, body);

		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("user.approve.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			logger.info(ApplicationProperties.getValue("user.approve.email.failure")
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
		String userDetailsBody = new StringBuffer().append("\n\n")
				.append(ApplicationProperties.getValue("user.loginName"))
				.append(Constants.SEPARATOR).append(user.getLoginName()).append("\n\n")
				.append(ApplicationProperties.getValue("user.lastName")).append(Constants.SEPARATOR)
				.append(user.getLastName()).append("\n\n").append(ApplicationProperties.getValue("user.firstName"))
				.append(Constants.SEPARATOR).append(user.getFirstName()).append("\n\n")
				.append(ApplicationProperties.getValue("user.street")).append(Constants.SEPARATOR)
				.append(user.getAddress().getStreet()).append("\n\n")
				.append(ApplicationProperties.getValue("user.city")).append(Constants.SEPARATOR)
				.append(user.getAddress().getCity() + "\n\n")
				.append(ApplicationProperties.getValue("user.zipCode")).append(Constants.SEPARATOR)
				.append(user.getAddress().getZipCode()).append("\n\n")
				.append(ApplicationProperties.getValue("user.state")).append(Constants.SEPARATOR)
				.append(user.getAddress().getState()).append( "\n\n")
				.append(ApplicationProperties.getValue("user.country")).append( Constants.SEPARATOR)
				.append(user.getAddress().getCountry()).append("\n\n")
				.append(ApplicationProperties.getValue("user.phoneNumber")).append(Constants.SEPARATOR)
				.append(user.getAddress().getPhoneNumber()).append("\n\n")
				.append(ApplicationProperties.getValue("user.faxNumber")).append(Constants.SEPARATOR)
				.append(user.getAddress().getFaxNumber()).append("\n\n")
				.append(ApplicationProperties.getValue("user.emailAddress")).append(Constants.SEPARATOR)
				.append(user.getEmailAddress()).append("\n\n")
				.append(ApplicationProperties.getValue("user.institution")).append(Constants.SEPARATOR)
				.append(user.getInstitution().getName()).append("\n\n")
				.append(ApplicationProperties.getValue("user.department")).append(Constants.SEPARATOR)
				.append(user.getDepartment().getName()).append("\n\n")
				.append(ApplicationProperties.getValue("user.cancerResearchGroup")).append(Constants.SEPARATOR)
				.append(user.getCancerResearchGroup().getName()).toString();

		return userDetailsBody;
	}

	/**
	 * Creates and sends the user signup request received email to the user and the administrator.
	 * @param user The user registered for the membership.
	 */
	public void sendUserSignUpEmail(User user)
	{
		String subject = ApplicationProperties.getValue("userRegistration.request.subject");

		String body = new StringBuffer("Dear ").append(user.getLastName())
				.append(",").append(user.getFirstName()).append("\n\n")
				.append(ApplicationProperties.getValue("userRegistration.request.body.start")).append("\n")
				.append(getUserDetailsEmailBody(user)).append("\n\n\t")
				.append(ApplicationProperties.getValue("userRegistration.request.body.end")).append("\n\n")
				.append(ApplicationProperties.getValue("email.catissuecore.team")).toString();

		boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject, body);

		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("userRegistration.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			logger.info(ApplicationProperties.getValue("userRegistration.email.failure")
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

			String body = new StringBuffer().append("Dear ").append(user.getFirstName()).append(" ").append(user.getLastName()).toString();

			if (userDetailsBody != null)
			{
				body = userDetailsBody;
			}

			String roleOfUser = SecurityManager.getInstance(EmailHandler.class).getUserRole(
					user.getCsmUserId().longValue()).getName();
			body = new StringBuffer(body).append("\n\n")
					.append(ApplicationProperties.getValue("forgotPassword.email.body.start")).append("\n\t ")
					.append(ApplicationProperties.getValue("user.loginName")).append(Constants.SEPARATOR)
					.append(user.getLoginName()).append("\n\t ")
					.append(ApplicationProperties.getValue("user.password")).append(Constants.SEPARATOR)
					.append(PasswordManager.decode(user.getPassword())).append("\n\t ")
					.append(ApplicationProperties.getValue("user.role")).append(Constants.SEPARATOR)
					.append(roleOfUser + "\n\n")
					.append(ApplicationProperties.getValue("email.catissuecore.team")).toString();

			emailStatus = sendEmailToUser(user.getEmailAddress(), subject, body);

			if (emailStatus)
			{
				logger.info(ApplicationProperties.getValue("user.loginDetails.email.success")
						+ user.getLastName() + " " + user.getFirstName());
			}
			else
			{
				logger.info(ApplicationProperties.getValue("user.loginDetails.email.failure")
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
		String sendFromEmailAddress = ApplicationProperties
				.getValue("email.sendEmailFrom.emailAddress");

		String emailBody = new StringBuffer().append(body).append("\n\n").append(ApplicationProperties.getValue("loginDetails.catissue.url.message")).
				append(Variables.catissueURL).toString();

		SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(userEmailAddress, sendFromEmailAddress, mailServer,
				subject, emailBody);
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
		String adminEmailAddress = ApplicationProperties
				.getValue("email.administrative.emailAddress");
		String sendFromEmailAddress = ApplicationProperties
				.getValue("email.sendEmailFrom.emailAddress");
		String mailServer = ApplicationProperties.getValue("email.mailServer");

		String emailBody = new StringBuffer().append(body).append("\n\n").append(ApplicationProperties.getValue("loginDetails.catissue.url.message"))
							.append(Variables.catissueURL).toString();

		SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(userEmailAddress, adminEmailAddress, null,
				sendFromEmailAddress, mailServer, subject, emailBody);
		return emailStatus;
	}

	/**
	 * Creates and sends the user registration rejection emails to user and the administrator.
	 * @param user The user whose registration is rejected.
	 */
	public void sendRejectionEmail(User user)
	{
		String subject = ApplicationProperties.getValue("userRegistration.reject.subject");

		String body = new StringBuffer().append("Dear ").append(user.getLastName()).append(",")
				.append(user.getFirstName()).append("\n\n")
				.append(ApplicationProperties.getValue("userRegistration.reject.body.start")).toString();

		//Append the comments given by the administrator, if any.
		if ((user.getComments() != null) && (!"".equals(user.getComments())))
		{
			body = new StringBuffer().append(body).append("\n\n")
					.append(ApplicationProperties.getValue("userRegistration.reject.comments"))
					.append(user.getComments()).toString();
		}

		body = new StringBuffer().append(body).append("\n\n")
				.append(ApplicationProperties.getValue("userRegistration.thank.body.end"))
				.append("\n\n").append(ApplicationProperties.getValue("email.catissuecore.team")).toString();

		boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject, body);

		if (emailStatus)
		{
			logger.info(ApplicationProperties.getValue("user.reject.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			logger.info(ApplicationProperties.getValue("user.reject.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
	}
}
