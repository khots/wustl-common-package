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

import titli.controller.Name;
import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.SortedResultMapInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.Titli;
import titli.model.TitliException;
import edu.wustl.common.actionForm.TitliSearchForm;
import titli.model.util.TitliTableMapper;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TitliSearchConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * perform Titli Search.
 * @author Juber Patel
 *
 */
public class TitliSearchAction extends Action
{

	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(TitliSearchAction.class);

	/**
	 * perform the search action for the search string obtained from search form.
	 * @param mapping the mapping
	 * @param form the action form
	 * @param request the request
	 * @param response the response
	 * @return the set table name and string to be searched.
	 *
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		LOGGER.info("in execute method");
		ActionForward actionForward = mapping.findForward(Constants.SUCCESS);
		TitliSearchForm titliSearchForm = (TitliSearchForm) form;
		try
		{
			TitliInterface titli = Titli.getInstance();
			String searchString = titliSearchForm.getSearchString().trim();
			MatchListInterface matchList = titli.search(searchString);
			SortedResultMapInterface sortedResultMap = matchList.getSortedResultMap();
			titliSearchForm.setSortedResultMap(sortedResultMap);
			request.getSession().setAttribute(TitliSearchConstants.TITLI_SORTED_RESULT_MAP,
					sortedResultMap);
			titliSearchForm.setDisplaySearchString("TiTLi Search");
			titliSearchForm.setDisplayStats("Found " + matchList.getNumberOfMatches()
					+ " matches in " + matchList.getTimeTaken() + " seconds");
			//if matches are from just one table, go directly to TitliFetchAction,
			//skip TitliResultUpdatable.jsp
			if (sortedResultMap.size() == Constants.ONE)
			{
				actionForward = getActionForward(titliSearchForm, sortedResultMap);
			}
		}
		catch (TitliException e)
		{
			LOGGER.error("TitliException in TitliSearchAction : " + e.getMessage(), e);
		}
		catch (Exception e)
		{
			LOGGER.error("Exception in TitliFetchAction : " + e.getMessage(), e);
		}
		return actionForward;
	}

	/**
	 * set the fetch action to be taken and path.
	 * @param titliSearchForm TitliSearchForm
	 * @param sortedResultMap SortedResultMapInterface
	 * @return the set action and path.
	 * @throws Exception Generic Exception
	 */
	private ActionForward getActionForward(TitliSearchForm titliSearchForm,
			SortedResultMapInterface sortedResultMap) throws Exception
	{
		Name tableName = sortedResultMap.keySet().toArray(new Name[0])[0];
		String label = TitliTableMapper.getInstance().getLabel(tableName);
		//setting the selectedLabel is necessary for getSelectedGroup() to work properly
		titliSearchForm.setSelectedLabel(label);
		ActionForward actionForward = new ActionForward();
		actionForward.setName(TitliSearchConstants.TITLI_SINGLE_RESULT);
		actionForward.setPath(TitliSearchConstants.TITLI_FETCH_ACTION);
		return actionForward;
	}
}
