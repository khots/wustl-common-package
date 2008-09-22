
package edu.wustl.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.util.global.Constants;
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
public class LogoutAction extends BaseAction
{

	private org.apache.log4j.Logger logger = Logger.getLogger(LoginAction.class);

	/**
	 * perform the log out action for the user.
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @exception Exception Generic exception
	 */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		logger.info("in execute method");
		HttpSession session = request.getSession();
		//Delete Advance Query table if exists
		SessionDataBean sessionData = getSessionData(request);
		//Advance Query table name with userID attached
		String tempTableName = Constants.QUERY_RESULTS_TABLE + "_" + sessionData.getUserId();
		JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		jdbcDao.openSession(sessionData);
		jdbcDao.delete(tempTableName);
		jdbcDao.closeSession();
		session.invalidate();
		return (mapping.findForward(Constants.SUCCESS));
	}

}