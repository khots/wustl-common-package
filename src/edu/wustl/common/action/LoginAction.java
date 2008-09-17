
package edu.wustl.common.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.LoginForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.User;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.PasswordManager;
import edu.wustl.common.util.logger.Logger;

/**
 *
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */
public class LoginAction extends Action
{

	private org.apache.log4j.Logger logger= Logger.getLogger(LoginAction.class);

    /**
     * Overrides the execute method of Action class.
     * Initializes the various drop down fields in Institute.jsp Page.
     * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @throws IOException generic exception
	 * @throws ServletException generic exception
     * */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
    	logger.info("in execute method");
    	ActionForward actionForward =(mapping.findForward(Constants.FAILURE));

    	if (form == null)
        {
    		Logger.out.debug("Form is Null");
            return mapping.findForward(Constants.FAILURE);
        }
        HttpSession prevSession = request.getSession();
        if(prevSession!=null)
        {
        	prevSession.invalidate();
        }
        
        LoginForm loginForm = (LoginForm) form;
        String loginName = loginForm.getLoginName();
        String password = PasswordManager.encode(loginForm.getPassword());

        try
        {
        	User validUser = getUser(loginName);

        	if (validUser != null)
        	{
	            boolean loginOK = SecurityManager.getInstance(LoginAction.class).login(loginName, password);
	            if (loginOK)
	            {
	                logger.info("SUCESSFUL LOGIN BY:"+validUser.getCsmUserId());
	                setSessionDataBean(request, loginName, validUser);
	                actionForward=mapping.findForward(Constants.SUCCESS);
	            }
	            else
	            {
	                handleError(request, "errors.incorrectLoginNamePassword");
	            }
        	} // if valid user
        	else
        	{
        		handleError(request, "errors.incorrectLoginNamePassword");
        	} // invalid user
       	}
        catch (Exception e)
        {
            logger.info("Exception: " + e.getMessage(), e);
            handleError(request, "errors.incorrectLoginNamePassword");
        }
        return actionForward;
    }

    /**
     *
     * @param request HttpServletRequest
     * @param loginName String loginName
     * @param validUser User validUser
     */
	private void setSessionDataBean(HttpServletRequest request,
		String loginName, User validUser)
	{
		HttpSession session = request.getSession(true);
		Long userId = validUser.getId();
		String ipAddress = request.getRemoteAddr();
		SessionDataBean sessionData = new SessionDataBean();
		sessionData.setUserName(loginName);
		sessionData.setIpAddress(ipAddress);
		sessionData.setUserId(userId);
		sessionData.setFirstName(validUser.getFirstName());
		sessionData.setLastName(validUser.getLastName());
		sessionData.setCsmUserId(validUser.getCsmUserId().toString());
		session.setAttribute(Constants.SESSION_DATA,sessionData);
	}

	/**
	 *
	 * @param request HttpServletRequest
	 * @param errorKey String
	 */
    private void handleError(HttpServletRequest request, String errorKey)
    {
        ActionErrors errors = new ActionErrors();
        errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errorKey));
        saveErrors(request, errors);
    }

    /**
     *
     * @param loginName String
     * @return User
     * @throws DAOException Database exception
     * @throws BizLogicException bizlogic exception
     */

    private User getUser(String loginName) throws DAOException, BizLogicException
    {
    	User validUser=null;
    	IBizLogic userBizLogic = AbstractBizLogicFactory.getBizLogic(
 				ApplicationProperties.getValue("app.bizLogicFactory"),
 					"getBizLogic", Constants.USER_FORM_ID);
    	String[] whereColumnName = {"activityStatus","loginName"};
    	String[] whereColumnCondition = {"=","="};
    	String[] whereColumnValue = {Constants.ACTIVITY_STATUS_ACTIVE, loginName};

    	List<User> users = userBizLogic.retrieve(User.class.getName(), whereColumnName,
    			whereColumnCondition, whereColumnValue,Constants.AND_JOIN_CONDITION);

    	if (!users.isEmpty())
    	{
    	    validUser = (User)users.get(0);
    	}
        return validUser;
    }
}