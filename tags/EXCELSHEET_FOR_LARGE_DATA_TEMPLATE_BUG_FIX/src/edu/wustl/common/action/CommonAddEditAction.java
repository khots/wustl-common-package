/**
 * <p>Title: CommonAddEditAction Class>
 * <p>Description:	This is common class used to Add/Edit the data in the database.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 21, 2005
 */

package edu.wustl.common.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class is used to Add/Edit data in the database.
 * @author gautam_shetty
 */
public class CommonAddEditAction extends BaseAction
{

	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(CommonAddEditAction.class);

	/**
	 * Overrides the execute method of Action class.
	 * Adds / Updates the data in the database.
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @throws IOException Generic exception
	 * @throws ServletException Generic exception
	 * */
	@Override
    public ActionForward executeAction(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException,
			ServletException
	{

		LOGGER.info("in execute method");
		BaseAddEditAction addEditAction;
		final AbstractActionForm abstractForm = (AbstractActionForm) form;
		ActionForward actionfwd;
		try
		{
			if (abstractForm.isAddOperation())
			{
				addEditAction = new CommonAddAction();
			}
			else
			{
				addEditAction = new CommonEdtAction();

			}
			actionfwd = addEditAction.execute(mapping, abstractForm, request, response);
		}
		catch (final ApplicationException applicationException)
		{
			LOGGER.error("Common Add/Edit failed.." + applicationException.getLogMessage(),
					applicationException);
			final ActionErrors actionErrors = new ActionErrors();
			final ActionError actionError = new ActionError("errors.item",applicationException.toMsgValuesArray());
			actionErrors.add(ActionErrors.GLOBAL_ERROR, actionError);
			saveErrors(request, actionErrors);

			actionfwd = mapping.findForward(Constants.FAILURE);
		}
		return actionfwd;
	}
}