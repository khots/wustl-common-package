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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.SortedResultMapInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.Titli;
import titli.model.TitliException;
import edu.wustl.common.actionForm.TitliSearchForm;
import edu.wustl.common.util.TitliTableMapper;
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
	public ActionForward execute(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response)
	{

		TitliSearchForm titliSearchForm = (TitliSearchForm) form;
		System.out.println("Search string entered is :"+ titliSearchForm.getSearchString());

		try
		{

			TitliInterface titli = Titli.getInstance();
			MatchListInterface matchList = titli.search(titliSearchForm.getSearchString().trim());
			
			SortedResultMapInterface sortedResultMap = matchList.getSortedResultMap();

			// set the result in the action form
			titliSearchForm.setSortedResultMap(sortedResultMap);
			request.getSession().setAttribute(Constants.TITLI_SORTED_RESULT_MAP,sortedResultMap);

			titliSearchForm.setTimeTaken(matchList.getTimeTaken());
			titliSearchForm.setNumberOfMatches(matchList.getNumberOfMatches());
			
			//if matches are from just one table, go directly to TitliFetchAction, skip TitliResultUpdatable.jsp 
			if(sortedResultMap.size()==1)
			{
				try
				{
					String tableName = sortedResultMap.keySet().toArray(new String[0])[0];
					String label = TitliTableMapper.getInstance().getLabel(tableName);
					
					//set the selectedLabel to the label of the only table
					//setting the selectedLabel is necessary for getSelectedGroup() to work properly
					titliSearchForm.setSelectedLabel(label);
					
				}
				catch (Exception e)
				{
					Logger.out.error("Exception in TitliFetchAction : "	+ e.getMessage(), e);
				}
				
				String path =Constants.TITLI_FETCH_ACTION;
				return getActionForward(Constants.TITLI_SINGLE_RESULT, path);
				
			}
		
		}
		catch (TitliException e)
		{
			Logger.out.error("TitliException in TitliSearchAction : "+ e.getMessage(), e);
		}

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
