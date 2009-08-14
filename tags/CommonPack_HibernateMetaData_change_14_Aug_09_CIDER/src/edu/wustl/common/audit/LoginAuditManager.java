
package edu.wustl.common.audit;

import edu.wustl.common.beans.LoginDetails;
import edu.wustl.common.domain.LoginEvent;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * This class handles the insertion of Login Events details into the database  
 * @author niharika_sharma
 */
public class LoginAuditManager
{

	public static org.apache.log4j.Logger logger = Logger.getLogger(LoginAuditManager.class);
	
	/**
	 * Constant for Login event type
	 */
	public static final String LOGIN="Login";

	/**
	 * Login Event instance which contains the event details
	 */
	private LoginEvent loginEvent;

	/**
	 * Public constructor to instantiate LoginAuditManager from SecurityManager.java 
	 */
	public LoginAuditManager()
	{
		loginEvent = new LoginEvent();
	}

	/**
	 * Create an instance of LoginAuditManager and initialize the contained loginEvent object with
	 * the details provided by the LoginDetails object
	 * @param loginDetails
	 */
	public LoginAuditManager(LoginDetails loginDetails)
	{
		loginEvent = new LoginEvent();
		loginEvent.setEventType(LOGIN);
		loginEvent.setIpAddress(loginDetails.getIpAddress());
		loginEvent.setSourceId(loginDetails.getSourceId());
		loginEvent.setUserLoginId(loginDetails.getUserLoginId());
	}

	/**
	 * Inserts the loginEvent object in the database using the DAO implementation provided as argument
	 * @param dao
	 * @param loginEvent
	 * @throws DAOException
	 */
	protected void insert(DAO dao, LoginEvent loginEvent) throws DAOException
	{
		try
		{
			//dao.insert(loginEvent, null, false, false);
			dao.insert(loginEvent, false,"");
		}
		catch (DAOException ex)
		{
			Logger.out.debug("Exception:" + ex.getMessage(), ex);
			throw ex;
		}
	}

	/**
	 * To be called by SecurityManager.java to audit this login attempt.
	 * Sets the status of LoginAttempt to loginStatus provided as an argument.
	 * @param loginStatus
	 */
	public void audit(boolean loginStatus)
	{
		HibernateDAO hibernateDao = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		try
		{
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
			.getDAO();
			hibernateDao.openSession(null);
			this.loginEvent.setIsLoginSuccessful(loginStatus);
			insert(hibernateDao, this.loginEvent);
			hibernateDao.commit();
		}
		catch (DAOException daoException)
		{
			logger.debug("Exception while Auditing Login Attempt. " + daoException.getMessage(),
					daoException);
		}
		finally
		{
			try
			{
				//dao.closeSession();
				hibernateDao.closeSession();
			}
			catch (DAOException daoException)
			{
				logger.debug(
						"Exception while Auditing Login Attempt. " + daoException.getMessage(),
						daoException);
			}
		}
	}
}
