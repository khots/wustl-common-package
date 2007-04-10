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

import titli.controller.interfaces.SortedResultMapInterface;
import titli.model.fetch.TitliFetchException;
import edu.wustl.common.actionForm.TitliSearchForm;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * show the records from the selected entity
 * @author Juber Patel
 * 
 */
public class TitliFetchAction extends Action
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
		// set the request and session attributes required by DataView.jsp and
		// forward
		// for that we need to fetch the selected group of records

		TitliSearchForm titliSearchForm = (TitliSearchForm) form;

		titliSearchForm.setSortedResultMap((SortedResultMapInterface) (request
				.getSession().getAttribute(Constants.SORTED_RESULT_MAP)));

		try
		{
			request.setAttribute(Constants.PAGEOF, titliSearchForm
					.getSelectedGroup().getPageOf());
		}
		catch (Exception e)
		{
			Logger.out.error("Exception in TitliFetchAction : "
					+ e.getMessage(), e);
		}

		try
		{
			request.setAttribute(Constants.SPREADSHEET_DATA_LIST, titliSearchForm
					.getSelectedGroup().getDataList());
			request.setAttribute(Constants.SPREADSHEET_COLUMN_LIST, titliSearchForm
					.getSelectedGroup().getColumnList());
			request.setAttribute(Constants.IDENTIFIER_FIELD_INDEX, titliSearchForm
					.getSelectedGroup().getColumnList().indexOf(
							Constants.IDENTIFIER));
		}
		catch (TitliFetchException e)
		{
			Logger.out.error("Exception in TitliFetchAction : "
					+ e.getMessage(), e);
		}
		
		request.setAttribute(Constants.PAGE_NUMBER, 1);

		request.getSession().setAttribute(
				Constants.TOTAL_RESULTS,
				titliSearchForm.getSelectedGroup().getNativeGroup()
						.getNumberOfMatches());
		request.getSession().setAttribute(Constants.RESULTS_PER_PAGE, 10);

		return mapping.findForward(Constants.SUCCESS);
	}

}
