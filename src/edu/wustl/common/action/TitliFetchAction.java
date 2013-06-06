/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * 
 */
package edu.wustl.common.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import titli.controller.interfaces.SortedResultMapInterface;
import titli.model.fetch.TitliFetchException;
import edu.wustl.common.actionForm.TitliSearchForm;
import edu.wustl.common.util.TitliResultGroup;
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
	public ActionForward execute(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response)
	{
		// set the request and session attributes required by DataView.jsp and
		// forward
		// for that we need to fetch the selected group of records

		TitliSearchForm titliSearchForm = (TitliSearchForm) form;

		titliSearchForm.setSortedResultMap((SortedResultMapInterface) (request.getSession().getAttribute(Constants.TITLI_SORTED_RESULT_MAP)));
		
		TitliResultGroup resultGroup = titliSearchForm.getSelectedGroup();

		try
		{
			//if there is only one record in the selected group, go directly to the edit page
			if(resultGroup.getNativeGroup().getNumberOfMatches()==1)
			{
				String pageOf=resultGroup.getPageOf();
				Map<String, String> uniqueKeys = resultGroup.getNativeGroup().getMatchList().get(0).getUniqueKeys();
				
				String id = uniqueKeys.get(Constants.IDENTIFIER);
				
				String path = Constants.SEARCH_OBJECT_ACTION + "?" + Constants.PAGEOF + "="
									+ pageOf + "&" + Constants.OPERATION + "="
									+ Constants.SEARCH + "&" + Constants.SYSTEM_IDENTIFIER + "="
									+ id;
	
				return getActionForward(Constants.TITLI_SINGLE_RESULT, path);
			}
	
			request.setAttribute(Constants.PAGEOF, titliSearchForm.getSelectedGroup().getPageOf());
			request.setAttribute(Constants.SPREADSHEET_DATA_LIST, titliSearchForm	.getSelectedGroup().getDataList());
			request.setAttribute(Constants.SPREADSHEET_COLUMN_LIST, titliSearchForm.getSelectedGroup().getColumnList());
			request.setAttribute(Constants.IDENTIFIER_FIELD_INDEX, titliSearchForm.getSelectedGroup().getColumnList().indexOf(Constants.IDENTIFIER));
		}
		catch (TitliFetchException e)
		{
			Logger.out.error("Exception in TitliFetchAction : "	+ e.getMessage(), e);
		}
		catch (Exception e)
		{
			Logger.out.error("Exception in TitliFetchAction : "	+ e.getMessage(), e);
		}

		
		request.setAttribute(Constants.PAGE_NUMBER, 1);

		request.getSession().setAttribute(	Constants.TOTAL_RESULTS,titliSearchForm.getSelectedGroup().getNativeGroup().getNumberOfMatches());
		request.getSession().setAttribute(Constants.RESULTS_PER_PAGE, 10);

		return mapping.findForward(Constants.SUCCESS);
	}
	
	
	private ActionForward getActionForward(String name, String path)
	{
		ActionForward actionForward = new ActionForward();
		actionForward.setName(name);
		actionForward.setPath(path);

		return actionForward;
	}



}
