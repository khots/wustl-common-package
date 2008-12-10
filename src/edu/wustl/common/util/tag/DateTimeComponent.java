/*
 * Created on Aug 9, 2006
 *
 * This class is the tag handler class for the Date Time component.
 */

package edu.wustl.common.util.tag;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

import edu.wustl.common.util.Utility;

/**
 * @author mandar_deshmukh
 *
 * This class is the tag handler class for the Date Time component.
 */
public class DateTimeComponent extends TagSupport
{

	/**
	 * specify serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the text field for the date component.
	 */
	String name;

	/**
	 * Value of the text field for the date component.
	 */
	String value;

	/**
	 * Id of the text field for the date component.
	 */
	String id;

	/**
	 * Style class for the text field for the date component.
	 */
	String styleClass;

	/**
	 * Size of the text field for the date component.
	 */
	Integer size;

	/**
	 * disabled property for the text field of the date component.
	 */
	Boolean disabled;

	/**
	 * Month of year of which the calendar is to be displayed.
	 */
	Integer month;

	/**
	 * Day of month to be selected in the calendar.
	 */
	Integer day;

	/**
	 * Year of date of which the calendar is to be displayed.
	 */
	Integer year;

	/**
	 * Date pattern to be used in displaying date.
	 */
	String pattern;

	/**
	 * Name of the html form which will contain the date component.
	 */
	String formName;

	/**
	 * Start year for the year drop down combo box.
	 */
	Integer startYear;

	/**
	 * End year for the year drop down combo box.
	 */
	Integer endYear;

	/**
	 * Tooltip to be displayed on the calendar icon.
	 */
	String iconComment;

	/**
	 * Boolean to decide whether to display time controls.
	 */
	Boolean displayTime;

	/**
	 * Hours to be displayed as selected in the drop down combo for hours.
	 */
	Integer hour;

	/**
	 * Minutes to be displayed as selected in the drop down combo for minutes.
	 */
	Integer minutes;

	/**
	 * Javascript function to be called on click of calendar image.
	 */
	String onClickImage;

	// ------------ SETTER Methods ----------------------------------

	/**
	 * @param datePattern String - The pattern to set.
	 */
	public void setPattern(String datePattern)
	{
		this.pattern = datePattern;
	}

	/**
	 * @param dayOfMonth Integer - The day to set.
	 */
	public void setDay(Integer dayOfMonth)
	{
		this.day = dayOfMonth;
	}

	/**
	 * @param displayTime The displayTime to set.
	 */
	public void setDisplayTime(Boolean displayTime)
	{
		this.displayTime = displayTime;
	}

	/**
	 * @param endYear The endYear to set.
	 */
	public void setEndYear(Integer endYear)
	{
		this.endYear = endYear;
	}

	/**
	 * @param formName The formName to set.
	 */
	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	/**
	 * @param iconComment The iconComment to set.
	 */
	public void setIconComment(String iconComment)
	{
		this.iconComment = iconComment;
	}

	/**
	 * @param monthOfYear Integer - The month to set.
	 */
	public void setMonth(Integer monthOfYear)
	{
		this.month = monthOfYear;
	}

	/**
	 * @param startYear The startYear to set.
	 */
	public void setStartYear(Integer startYear)
	{
		this.startYear = startYear;
	}

	/**
	 * @param timeInHours Integer - The hour to set.
	 */
	public void setHour(Integer timeInHours)
	{
		this.hour = timeInHours;
	}

	/**
	 * @param timeInMinutes Integer - The minutes to set.
	 */
	public void setMinutes(Integer timeInMinutes)
	{
		this.minutes = timeInMinutes;
	}

	/**
	 * @param txtdate String - The name to set.
	 */
	public void setName(String txtdate)
	{
		this.name = txtdate;
	}

	/**
	 * @param txtdateid String - The id to set.
	 */
	public void setId(String txtdateid)
	{
		this.id = txtdateid;
	}

	/**
	 * @param yearOfDate Integer - The year to set.
	 */
	public void setYear(Integer yearOfDate)
	{
		this.year = yearOfDate;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @param styleClass The styleClass to set.
	 */
	public void setStyleClass(String styleClass)
	{
		this.styleClass = styleClass;
	}

	/**
	 * @param size The size to set.
	 */
	public void setSize(Integer size)
	{
		this.size = size;
	}

	/**
	 * @param disabled The disabled to set.
	 */
	public void setDisabled(Boolean disabled)
	{
		this.disabled = disabled;
	}

	/**
	 * @param onClickImage The onClickImage to set.
	 */
	public void setOnClickImage(String onClickImage)
	{
		this.onClickImage = onClickImage;
	}

	// ------------SETTER Methods end ----------------------------------

	/**
	 * A call back function, which gets executed by JSP runtime when opening tag for this
	 * custom tag is encountered.
	 * @exception JspException jsp exception.
	 * @return integer value as per validation.
	 */
	public int doStartTag() throws JspException
	{
		try
		{
			JspWriter out = pageContext.getOut();

			out.print("");
			if (validate())
			{
				initializeParameters();
				out.print(generateOutput());
			}

		}
		catch (IOException ioe)
		{
			throw new JspTagException("Error:IOException while writing to the user");
		}

		return SKIP_BODY;
	}

	/**
	 * A call back function.
	 * @exception JspException jsp exception.
	 * @return integer value for evaluated page.
	 */
	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}

	/**
	 * method to validate the given values for the attributes.
	 * @return true if all required attributes are in proper valid format. Otherwise returns false.
	 */
	private boolean validate()
	{
		boolean result = true;

		ServletRequest request = pageContext.getRequest();
		ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);

		if (errors == null)
		{
			errors = new ActionErrors();
		}

		// validations for name
		if (Utility.isNull(name))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("Name attribute is null"));
			result = false;
		}
		else if (name.trim().length() == 0)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("Name attribute is empty"));
			result = false;
		}

		//validations for id
		if (Utility.isNull(id))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("ID attribute is null"));
			result = false;
		}
		else if (id.trim().length() == 0)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("ID attribute is empty"));
			result = false;
		}

		//validations for formName
		if (Utility.isNull(formName))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("FormName attribute is null"));
			result = false;
		}
		else if (formName.trim().length() == 0)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("FormName attribute is empty"));
			result = false;
		}

		//setting errors in request
		request.setAttribute(Globals.ERROR_KEY, errors);

		return result;
	}

	//method to generate the required output.
	/**
	 * @return String generated output.
	 * @throws IOException I/O exception.
	 */
	private String generateOutput() throws IOException
	{

		String output = "<INPUT name='" + name + "' id = '" + id + "' value='" + value
				+ "' class=\"" + styleClass + "\" size=\"10\"";
		if (disabled.booleanValue())
		{
			output = output + " disabled=\"disabled\">";
		}
		else
		{
			output = output + ">";
		}
		String onClickFunction = "";

		/**
		 * Changes done by Jitendra to fix the bug if two DateTimeComponent included in the same jsp.
		 * So to fix this bug, now we are passing id attribute to showCalendar, printCalendar and
		 * printTimeCalendar js function.
		 */
		if (onClickImage == null)
		{
			onClickFunction = "showCalendar('" + id + "'," + year + "," + month + "," + day + ",'"
					+ pattern + "','" + formName + "','" + name + "',event," + startYear + ","
					+ endYear + ");";
		}
		else
		{
			onClickFunction = onClickImage;
		}
		output = output + "<A onclick=\"" + onClickFunction + "\" href=\"javascript://\">";
		output = output
				+ "<span valign=middle ><IMG alt=\""
				+ iconComment
				+ "\" src=\"images/calendar.gif\" hspace=0 vspace=0 align=top height=22 width=24 border=0></span>";
		output = output + "</A>";

		output = output
				+ "<DIV id=slcalcod"
				+ id
				+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">";
		output = output + "<SCRIPT>";

		if (displayTime.booleanValue())
		{
			output = output + "printTimeCalendar('" + id + "'," + day + "," + month + "," + year
					+ "," + hour + "," + minutes + ");";
		}
		else
		{
			output = output + "printCalendar('" + id + "'," + day + "," + month + "," + year + ");";
		}
		output = output + "</SCRIPT>";
		output = output + "</DIV>";

		return output;
	}

	/**
	 * Method to initialize the optional parameters.
	 */
	private void initializeParameters()
	{
		if (value == null)
		{
			value = "";
		}
		if (value.trim().length() > 0)
		{
			Integer specimenYear = Integer.valueOf(Utility.getYear(value));
			Integer specimenMonth = Integer.valueOf(Utility.getMonth(value));
			Integer specimenDay = Integer.valueOf(Utility.getDay(value));
			day = specimenDay.intValue();
			month = specimenMonth.intValue();
			year = specimenYear.intValue();

		}

		if (Utility.isNull(month))
		{
			month = Integer.valueOf((Calendar.getInstance().get(Calendar.MONTH)) + 1);
		}
		if (Utility.isNull(day))
		{
			day = Integer.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		}
		if (Utility.isNull(year))
		{
			year = Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		}
		if (Utility.isNull(pattern))
		{
			pattern = "MM-dd-yyyy";
		}
		if (Utility.isNull(displayTime))
		{
			displayTime = Boolean.FALSE;
		}
		if (Utility.isNull(hour))
		{
			hour = Integer.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		}
		if (Utility.isNull(minutes))
		{
			minutes = Integer.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
		}
		if (Utility.isNull(startYear))
		{
			startYear = Integer.valueOf(1900);
		}
		if (Utility.isNull(endYear))
		{
			endYear = Integer.valueOf(2020);
		}
		if (Utility.isNull(iconComment))
		{
			iconComment = "This is a Calendar";
		}
		if (Utility.isNull(value))
		{
			value = "";
		}
		if (Utility.isNull(styleClass))
		{
			styleClass = "";
		}
		if (Utility.isNull(size))
		{
			size = Integer.valueOf(15);
		}
		if (Utility.isNull(disabled))
		{
			disabled = Boolean.FALSE;
		}

	}

	/**
	 * @param args arguments.
	 * @throws Exception generic exception.
	 */
	public static void main(String[] args) throws Exception
	{
		DateTimeComponent obj = new DateTimeComponent();
		obj.name = "mddate";
		obj.id = "mddate";
		obj.formName = "newsForm";
		//	obj.onClickImage ="f1()";
		obj.initializeParameters();
		obj.generateOutput();

	}
}