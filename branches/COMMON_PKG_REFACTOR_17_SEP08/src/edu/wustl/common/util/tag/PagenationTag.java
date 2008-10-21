
package edu.wustl.common.util.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2004</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Divyabhanu Singh
 *@version 1.0
 */

public class PagenationTag extends TagSupport
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(PagenationTag.class);
	/**
	/**
	 * specify serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * specify name.
	 */
	protected String name = "Bhanu";

	/**
	 * specify pageNum.
	 */
	protected int pageNum = 1;

	/**
	 * specify previous Page.
	 */
	protected String prevPage = null;

	/**
	 * specify total Results.
	 */
	protected int totalResults = 1000;

	/**
	 * specify number of Results Per Page.
	 */
	protected int numResultsPerPage = 15;

	/**
	 * specify page Link Start.
	 */
	protected int mpageLinkStart = 1;

	/**
	 * specify page Link End.
	 */
	protected int mpageLinkEnd = 10;

	/**
	 * specify show Next.
	 */
	protected boolean mshowNext;

	/**
	 * specify search term.
	 */
	protected String searchTerm = null;

	/**
	 * specify search Term Values.
	 */
	protected String searchTermValues = null;

	/**
	 * specify selectedOrgs.
	 */
	protected String[] selectedOrgs = null;

	/**
	 * specify numLinks.
	 */
	private int numLinks = 10;

	/**
	 * specify resultLowRange.
	 */
	private int resultLowRange = 1;

	/**
	 * specify resultHighRange.
	 */
	private int resultHighRange = 1;

	/**
	 * specify pageName.
	 */
	private String pageName = null;

	/**
	 * Showing combo for Records/page values.
	 */
	protected boolean showPageSizeCombo = false;

	/**
	 * specify recordPerPageList.
	 */
	protected int[] recordPerPageList = Constants.RESULT_PERPAGE_OPTIONS;

	/**
	 * doStartTag.
	 * @return SKIP_BODY
	 */
	public int doStartTag()
	{
		try
		{
			mshowNext = true;
			JspWriter out = pageContext.getOut();
			//pageName = SpreadsheetView for ViewResults page (SimpleSearchDataView.jsp)
			if (getPageName().equals("SpreadsheetView.do"))
			{
				out.println("<table class=\"black_ar\" border=0 bordercolor=#FFFFFF width=98% >");
			}
			else
			{
				out.println("<table class=\"black_ar\" border=0 bordercolor=#FFFFFF width=38%>");
			}

			if (pageNum > numLinks)
			{
				if (pageNum % numLinks != 0)
				{
					mpageLinkStart = ((pageNum / numLinks) * numLinks + 1);
				}
				else
				{
					mpageLinkStart = (pageNum - numLinks) + 1;
				}
			}
			else
			{
				//For first time or for PageNum < 10.
				mpageLinkStart = 1;
			}

			// If user has opted to view all Records on this page.
			if (numResultsPerPage == Integer.MAX_VALUE)
			{
				mpageLinkStart = 1;
				mpageLinkEnd = 1;
				mshowNext = false;
				resultLowRange = 1;
				resultHighRange = totalResults;
			}
			else
			{
				if ((totalResults - ((mpageLinkStart - 1) * numResultsPerPage)) >= numResultsPerPage
						* numLinks)
				{
					mpageLinkEnd = mpageLinkStart + (numLinks - 1);

				}
				else
				{
					if ((totalResults - (mpageLinkStart * numResultsPerPage)) > 0)
					{
						if (totalResults % numResultsPerPage == 0)
						{
							mpageLinkEnd = (mpageLinkStart + (totalResults - (mpageLinkStart * numResultsPerPage))
									/ numResultsPerPage);
						}
						else
						{
							mpageLinkEnd = (mpageLinkStart + (totalResults - (mpageLinkStart * numResultsPerPage))
									/ numResultsPerPage) + 1;
						}
					}
					else
					{
						mpageLinkEnd = (mpageLinkStart + (totalResults - (mpageLinkStart * numResultsPerPage))
								/ numResultsPerPage);

					}

				}

				if ((mpageLinkEnd * numResultsPerPage >= totalResults))
				{
					mshowNext = false;
				}

				resultLowRange = (pageNum - 1) * numResultsPerPage + 1;
				if (totalResults - ((pageNum - 1) * numResultsPerPage) < numResultsPerPage)
				{
					resultHighRange = resultLowRange + totalResults
							- ((pageNum - 1) * numResultsPerPage) - 1;
				}
				else
				{
					resultHighRange = resultLowRange + numResultsPerPage - 1;
				}
			}

			 //pageName = SpreadsheetView for ViewResults page (SimpleSearchDataView.jsp)
			if (!getPageName().equals("SpreadsheetView.do"))
			{
				out.println("<tr> <td class = \"formtextbg\" align=\"CENTER\">" + name + "</td>");
			}

			if (showPageSizeCombo)
			{
				// Showing combo for Records/page values.

				String options = "";

				int [] possibleResultPerPageValues = putValueIfNotPresent(recordPerPageList,
						numResultsPerPage);

				for (int i = 0; i < possibleResultPerPageValues.length; i++)
				{
					int value = possibleResultPerPageValues[i];
					String name = possibleResultPerPageValues[i] + "";

					if (value == Integer.MAX_VALUE)
					{
						name = "All";
					}

					if (possibleResultPerPageValues[i] == numResultsPerPage)
					{
						options = options + "<option value=\"" + value
								+ "\" selected=\"selected\" >" + name + "</option>";
					}
					else
					{
						options = options + "<option value=\"" + value + "\">" + name + "</option>";
					}
				}

				out
						.println("<td>Records Per Page <select name=\"recordPerPage\" size=\"1\" onChange=\"javascript:changeRecordPerPage("
								+ (mpageLinkStart)
								+ ",this,'"
								+ pageName
								+ "')\""
								+ " >"
								+ options + "</select> <td>");

			}
			//Mandar 19-Apr-06 : 1697 :- Summary shows wrong data. Checking for zero records.
			if (totalResults > 0)
			{
				out.println("<td  align = \"CENTER\" class = \"formtextbg\">" + resultLowRange
						+ " - " + resultHighRange + " of " + totalResults + "</td>");
			}
			else
			{
				out.println("<td  align = \"CENTER\" class = \"formtextbg\">Showing Results " + "0"
						+ " - " + "0" + " of " + "0" + "</td>");
			}
			//Mandar 19-Apr-06 : 1697 :- Summary shows wrong data. end

			if ((mpageLinkEnd) > numLinks)
			{
				out.print("<td align=\"CENTER\"><a href=\"javascript:send(" + (mpageLinkStart - 1)
						+ "," + numResultsPerPage + ",'" + prevPage + "','" + pageName + "')"
						+ "\"> &lt;&lt;  </a></td>");
			}
			else
			{

				out.print("<td align=\"CENTER\">&nbsp;</td>");
			}

			int i = mpageLinkStart;
			for (i = mpageLinkStart; i <= mpageLinkEnd; i++)
			{
				if (i != pageNum)
				{
					out.print("<td align=\"CENTER\"> <a href=\"javascript:send(" + i + ","
							+ numResultsPerPage + ",'" + prevPage + "','" + pageName + "')" + "\">"
							+ i + " </a></td>");
				}
				else
				{
					out.print("<td align=\"CENTER\">" + i + " </td>");
				}
			}
			if (mshowNext)
			{
				out.print("<td align=\"CENTER\"><a href=\"javascript:send(" + i + ","
						+ numResultsPerPage + ",'" + prevPage + "','" + pageName + "')"
						+ "\"> >>  </a> </td>");
			}
			else
			{
				out.print("<td align=\"CENTER\">&nbsp;</td>");
			}
			out.print("</tr></table>");

		}
		catch (IOException ioe)
		{
			logger.debug("Error generating prime: " + ioe, ioe);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage(), e);
		}
		return SKIP_BODY;
	}

	/**
	 * This method put Value If Not Present.
	 * @param originalArray originalArray
	 * @param value value
	 * @return newArray
	 */
	private int[] putValueIfNotPresent(int[] originalArray, int value)
	{
		for (int i = 0; i < originalArray.length; i++)
		{
			//if array contains the value, then return same array.
			if (value == originalArray[i])
			{
				return originalArray;
			}
		}

		 // array doesn't containe value, hence define new array.
		int[] newArray = new int[originalArray.length + 1];
		int i = 0;

		for (; i < originalArray.length && value > originalArray[i]; i++)
		{
			// copying all elements less than value.
			newArray[i] = originalArray[i];
		}

		newArray[i++] = value;

		for (; i < newArray.length; i++) // moving array elements 1 position next.
		{
			newArray[i] = originalArray[i - 1];
		}
		return newArray;

	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param pageNum The pageNum to set.
	 */
	public void setPageNum(int pageNum)
	{
		try
		{
			this.pageNum = pageNum;
		}
		catch (NumberFormatException nfe)
		{
			this.pageNum = 1;
			nfe.printStackTrace();
		}

	}

	/**
	 * @param totalResults The totalResults to set.
	 */
	public void setTotalResults(int totalResults)
	{
		try
		{
			this.totalResults = totalResults;
		}
		catch (NumberFormatException nfe)
		{
			this.totalResults = 1000;
		}
	}

	/**
	 * @param numResultsPerPage The numResultsPerPage to set.
	 */
	public void setNumResultsPerPage(int numResultsPerPage)
	{
		try
		{
			this.numResultsPerPage = numResultsPerPage;
		}
		catch (NumberFormatException nfe)
		{
			this.numResultsPerPage = 10;
		}
	}

	/**
	 * @param searchTerm The searchTerm to set.
	 */
	public void setSearchTerm(String searchTerm)
	{
		this.searchTerm = searchTerm;
	}

	/**
	 * @param searchTermvalues The searchTermvalues to set.
	 */
	public void setSearchTermValues(String searchTermvalues)
	{
		this.searchTermValues = searchTermvalues;
	}

	/**
	 * @param selectedOrgs The selectedOrgs to set.
	 */
	public void setSelectedOrgs(String[] selectedOrgs)
	{
		this.selectedOrgs = selectedOrgs;
	}

	/**
	 * @return Returns the prevPage.
	 */
	public String getPrevPage()
	{
		return prevPage;
	}

	/**
	 * @param prevPage The prevPage to set.
	 */
	public void setPrevPage(String prevPage)
	{
		this.prevPage = prevPage;
	}

	/**
	 * @return Returns the pageName.
	 */
	public String getPageName()
	{
		return pageName;
	}

	/**
	 * @param pageName The pageName to set.
	 */
	public void setPageName(String pageName)
	{
		this.pageName = pageName;
	}

	/**
	 * @param showPageSizeCombo The showPageSizeCombo to set.
	 */
	public void setShowPageSizeCombo(boolean showPageSizeCombo)
	{
		this.showPageSizeCombo = showPageSizeCombo;
	}

	/**
	 * @param recordPerPageList The recordPerPageList to set.
	 */
	public void setRecordPerPageList(int[] recordPerPageList)
	{
		this.recordPerPageList = recordPerPageList;
	}
}