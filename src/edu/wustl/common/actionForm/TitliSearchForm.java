/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

/**
 * 
 */
package edu.wustl.common.actionForm;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import titli.controller.interfaces.ResultGroupInterface;
import titli.controller.interfaces.SortedResultMapInterface;
import edu.wustl.common.util.TitliResultGroup;
import edu.wustl.common.util.TitliTableMapper;

/**
 * This Class encapsulates all the request parameters passed from Tilti Search
 * Form.
 * 
 * @author Juber Patel
 * 
 */
public class TitliSearchForm extends ActionForm
{
	private String searchString;

	private double timeTaken;

	private long numberOfMatches;

	private SortedResultMapInterface sortedResultMap;

	private Map<String, TitliResultGroup> titliResultMap;

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

		titliResultMap = new LinkedHashMap<String, TitliResultGroup>();

		for (ResultGroupInterface i : sortedResultMap.values())
		{
			titliResultMap.put(i.getTableName(), new TitliResultGroup(i));
		}

	}

	/**
	 * @return the numberOfMatches
	 */
	public long getNumberOfMatches()
	{
		return numberOfMatches;
	}

	/**
	 * @param numberOfMatches
	 *            the numberOfMatches to set
	 */
	public void setNumberOfMatches(long numberOfMatches)
	{
		this.numberOfMatches = numberOfMatches;
	}

	/**
	 * @return the timeTaken
	 */
	public double getTimeTaken()
	{
		return timeTaken;
	}

	/**
	 * @param timeTaken
	 *            the timeTaken to set
	 */
	public void setTimeTaken(double timeTaken)
	{
		this.timeTaken = timeTaken;
	}

	/**
	 * 
	 * @return the result group corresponding to the selected label
	 */
	public TitliResultGroup getSelectedGroup()
	{
		ResultGroupInterface i = sortedResultMap.get(TitliTableMapper
				.getInstance().getTable(selectedLabel));

		return new TitliResultGroup(i);

	}

	/**
	 * @return the titliResultMap
	 */
	public Map<String, TitliResultGroup> getTitliResultMap()
	{
		return titliResultMap;
	}

	
	/**
	 * validate the input
	 * @param mapping the cation mapping
	 * @param request the request
	 * @return acttion errors
	 */
	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();

		String requestSearchString = request.getParameter("searchString");

		// searchString is null or empty
		if (requestSearchString == null
				|| requestSearchString.trim().equals(""))
		{
			errors.add("empty search string", new ActionError("  "));
		}

		if (requestSearchString.startsWith("*")
				|| requestSearchString.startsWith("?"))
		{
			errors.add("search string starts with * or ? ", new ActionError(
					"  "));
		}

		return errors;

	}

}
