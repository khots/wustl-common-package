package edu.wustl.common.actionForm;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import titli.controller.Name;
import titli.controller.interfaces.ResultGroupInterface;
import titli.controller.interfaces.SortedResultMapInterface;
import titli.model.util.TitliResultGroup;
import titli.model.util.TitliTableMapper;
import edu.wustl.common.util.logger.Logger;

/**
 * This Class encapsulates all the request parameters passed from Tilti Search
 * Form.
 *
 * @author Juber Patel
 *
 */
public class TitliSearchForm extends ActionForm
{

	/**
	 * Serial Version Unique Identifier.
	 */
	private static final long serialVersionUID = 3133201882979460560L;

	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(TitliSearchForm.class);

	/**
	 * Specifies search String.
	 */
	private String searchString;

	/**
	 * Specifies display Search String.
	 */
	private String displaySearchString;

	/**
	 * Specifies displayStats.
	 */
	private String displayStats;

	/**
	 * Specifies sorted Result Map.
	 */
	private SortedResultMapInterface sortedResultMap;

	/**
	 * Specifies titli Result Map.
	 */
	private Map<Name, TitliResultGroup> titliResultMap;

	/**
	 * Specifies selected Label.
	 */
	private String selectedLabel;

	/**
	 * @return the selectedLabel
	 */
	public String getSelectedLabel()
	{
		return selectedLabel;
	}

	/**
	 * @param selectedLabel
	 *            the selectedLabel to set
	 */
	public void setSelectedLabel(String selectedLabel)
	{
		this.selectedLabel = selectedLabel;
	}

	/**
	 * @return the search string
	 */
	public String getSearchString()
	{
		return searchString;
	}

	/**
	 * @param searchString
	 *            the string to be searched
	 */
	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

	/**
	 * @return the sortedResultMap
	 */
	public SortedResultMapInterface getSortedResultMap()
	{
		return sortedResultMap;
	}

	/**
	 * @param sortedResultMap
	 *            the sortedResultMap to set
	 */
	public void setSortedResultMap(SortedResultMapInterface sortedResultMap)
	{
		this.sortedResultMap = sortedResultMap;

		titliResultMap = new LinkedHashMap<Name, TitliResultGroup>();

		for (ResultGroupInterface i : sortedResultMap.values())
		{
			titliResultMap.put(i.getTableName(), new TitliResultGroup(i));
		}

	}

	/**
	 * Return the result group corresponding to the selected label.
	 * @return the result group.
	 */
	public TitliResultGroup getSelectedGroup()
	{
		ResultGroupInterface resultGroupI = null;
		try
		{
			resultGroupI = sortedResultMap.get(TitliTableMapper.getInstance().getTable(
					selectedLabel));

		}
		catch (Exception exception)
		{
			LOGGER.error("Exception in TitliSearchForm : " + exception.getMessage(), exception);

		}

		return new TitliResultGroup(resultGroupI);

	}

	/**
	 * @return the titliResultMap
	 */
	public Map<Name, TitliResultGroup> getTitliResultMap()
	{
		return titliResultMap;
	}

	/**
	 * validate the input.
	 * @param mapping the cation mapping
	 * @param request the request
	 * @return acttion errors
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();

		String requestSearchString = request.getParameter("searchString");

		// searchString is null or empty
		if (requestSearchString == null || requestSearchString.trim().equals(""))
		{
			errors.add("empty search string", new ActionError("  "));
		}
		else if(requestSearchString.charAt(0)=='*' || requestSearchString.charAt(0)=='?')
		{
			errors.add("search string starts with * or ? ", new ActionError("  "));
		}

		return errors;

	}

	/**
	 * @return the displaysearchString
	 */
	public String getDisplaySearchString()
	{
		return displaySearchString;
	}

	/**
	 * @param displaySearchString the displaysearchString to set.
	 */
	public void setDisplaySearchString(String displaySearchString)
	{
		this.displaySearchString = displaySearchString;
	}

	/**
	 * @return the displayStats
	 */
	public String getDisplayStats()
	{
		return displayStats;
	}

	/**
	 * @param displayStats the displayStats to set
	 */
	public void setDisplayStats(String displayStats)
	{
		this.displayStats = displayStats;
	}

}
