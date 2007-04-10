/**
 * 
 */
package edu.wustl.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.Titli;
import titli.model.TitliException;
import edu.wustl.common.actionForm.TitliSearchForm;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * perform Titli Search
 * @author Juber Patel
 * 
 */
public class TitliSearchAction extends Action
{

	/**
	 * @param mapping the mapping
	 * @param form the action form
	 * @param request the request
	 * @param response the response
	 * @return action forward
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{

		TitliSearchForm titliSearchForm = (TitliSearchForm) form;
		System.out.println("Search string entered is :"
				+ titliSearchForm.getSearchString());

		try
		{

			TitliInterface titli = Titli.getInstance();
			MatchListInterface matchList = titli.search(titliSearchForm
					.getSearchString().trim());

			// set the result in the action form
			titliSearchForm.setSortedResultMap(matchList.getSortedResultMap());
			request.getSession().setAttribute(Constants.TITLI_SORTED_RESULT_MAP,
					matchList.getSortedResultMap());

			titliSearchForm.setTimeTaken(matchList.getTimeTaken());
			titliSearchForm.setNumberOfMatches(matchList.getNumberOfMatches());
		}
		catch (TitliException e)
		{
			Logger.out.error("TitliException in TitliSearchAction : "
					+ e.getMessage(), e);
		}

		return mapping.findForward(Constants.SUCCESS);
	}

}
