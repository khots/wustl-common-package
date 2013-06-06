/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.scheduler.tag;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import edu.wustl.common.scheduler.constants.SchedulerConstants;
import edu.wustl.common.util.global.ApplicationProperties;


public class SchedulerTag extends TagSupport
{

	private static final long serialVersionUID = -8182080756640680808L;

	private String scheduleType;
	private String dropDownURL;
	private String userDropDownURL;
	private String captionName;

	public String getScheduleType()
	{
		return scheduleType;
	}

	public void setScheduleType(String scheduleType)
	{
		this.scheduleType = scheduleType;
	}

	public String getDropDownURL()
	{
		return dropDownURL;
	}

	public void setDropDownURL(String dropDownURL)
	{
		this.dropDownURL = dropDownURL;
	}

	public String getUserDropDownURL()
	{
		return userDropDownURL;
	}

	public void setUserDropDownURL(String userDropDownURL)
	{
		this.userDropDownURL = userDropDownURL;
	}

	public String getCaptionName()
	{
		return captionName;
	}

	public void setCaptionName(String captionName)
	{
		this.captionName = captionName;
	}

	public int doStartTag() throws JspException
	{

		try
		{
			pageContext.getOut().println(
					generateHTMLDisplay());
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	public String generateHTMLDisplay() throws IOException
	{
		InputStream is = SchedulerTag.class.getClassLoader().getResourceAsStream(
				SchedulerConstants.TAG_FILE_NAME);
		int bytesRead = 0;
		byte[] buffer = new byte[1024];
		String tagString = "";
		StringBuilder tag = new StringBuilder();
		while ((bytesRead = is.read(buffer)) != -1)
		{
			String chunk = new String(buffer, 0, bytesRead);
			tag.append(chunk);
		}
		tagString = tag.toString();
		tagString = tagString.replace(SchedulerConstants.SCHEDULE_TYPE_TOKEN, scheduleType);
		tagString = tagString.replace(SchedulerConstants.CAPTION_TOKEN, captionName);
		tagString = tagString.replace(SchedulerConstants.DROPDOWN_CAPTION_TOKEN, captionName);
		tagString = tagString.replace(SchedulerConstants.DROPDOWN_TOKEN, dropDownURL);
		tagString = tagString.replace(SchedulerConstants.USER_DROPDOWN_TOKEN, userDropDownURL);
		tagString = tagString.replace(SchedulerConstants.DATE_PATTERN_TOKEN, ApplicationProperties.getValue("ui.date.pattern"));
		tagString = tagString.replace(SchedulerConstants.MIN_END_YEAR_TOKEN, ApplicationProperties.getValue("min.year"));
		tagString = tagString.replace(SchedulerConstants.MAX_END_YEAR_TOKEN, ApplicationProperties.getValue("max.year"));
		
		return tagString;
	}

}
