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

	private static org.apache.log4j.Logger logger = Logger.getLogger(TitliSearchAction.class);

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
		logger.info("in execute method");
		ActionForward actionForward = mapping.findForward(Constants.SUCCESS);
		TitliSearchForm titliSearchForm = (TitliSearchForm) form;
		try
		{
			TitliInterface titli = Titli.getInstance();
			String searchString = titliSearchForm.getSearchString().trim();
			MatchListInterface matchList = titli.search(searchString);
			SortedResultMapInterface sortedResultMap = matchList.getSortedResultMap();
			titliSearchForm.setSortedResultMap(sortedResultMap);
			request.getSession().setAttribute(Constants.TITLI_SORTED_RESULT_MAP, sortedResultMap);
			titliSearchForm.setDisplaySearchString("TiTLi Search");
			titliSearchForm.setDisplayStats("Found " + matchList.getNumberOfMatches()
					+ " matches in " + matchList.getTimeTaken() + " seconds");

			//if matches are from just one table, go directly to TitliFetchAction, skip TitliResultUpdatable.jsp
			if (sortedResultMap.size() == Constants.ONE)
			{
				actionForward = getActionForward(titliSearchForm, sortedResultMap);
			}
		}
		catch (TitliException e)
		{
			logger.error("TitliException in TitliSearchAction : " + e.getMessage(), e);
		}
		catch (Exception e)
		{
			logger.error("Exception in TitliFetchAction : " + e.getMessage(), e);
		}
		return actionForward;
	}

	/**
	 * @param titliSearchForm TitliSearchForm
	 * @param sortedResultMap SortedResultMapInterface
	 * @return the created ActionForward
	 * @throws Exception geniric exception
	 */
	private ActionForward getActionForward(TitliSearchForm titliSearchForm,
			SortedResultMapInterface sortedResultMap) throws Exception
	{
		Name tableName = sortedResultMap.keySet().toArray(new Name[0])[0];
		String label = TitliTableMapper.getInstance().getLabel(tableName);
		//setting the selectedLabel is necessary for getSelectedGroup() to work properly
		titliSearchForm.setSelectedLabel(label);
		ActionForward actionForward = new ActionForward();
		actionForward.setName(Constants.TITLI_SINGLE_RESULT);
		actionForward.setPath(Constants.TITLI_FETCH_ACTION);
		return actionForward;
	}
}
