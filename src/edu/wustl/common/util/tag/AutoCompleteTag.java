/*
 * Created on Feb 16, 2007
 */

package edu.wustl.common.util.tag;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.TextConstants;

/**
 * @author Santosh Chandak
 * JSP tag for Autocomplete feature. The body of this tag is executed once for every call of the tag
 * when the page is rendered.
 * To use this tag, include AutocompleterCommon.jsp in your page
 */
public class AutoCompleteTag extends TagSupport
{

	/**
	 * serialVersionUID long - version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * property on which Auto completer is to be applied.
	 */
	private String property;

	/**
	 * Object containing values of the dropdown. Supported data types.
	 * 1. String Array 2. List of Name Value Beans
	 *
	 */
	private Object optionsList;

	/**
	 * Default style.
	 */
	private String styleClass = "formFieldSized15";

	/**
	 * Number of results to be shown, set to 11 as for time drop downs showing 11 values was more logical.
	 */
	private String numberOfResults = "11";

	/**
	 * Trigger matching when user enters these number of characters.
	 */
	private String numberOfCharacters = "1";

	/**
	 * set to true if the textbox is readOnly.
	 */
	private Object readOnly = "false";

	/**
	 * set to true if the textbox is disabled.
	 */
	private Object disabled = "false";

	/**
	 * Functions to be called when textbox loses focus.
	 */
	private String onChange = "";

	/**
	 * initial value in the textbox, this is compulsory attribute.
	 */
	private Object initialValue = "";

	/**
	 * if the property is dependent on some other property.
	 * eg. type depends on class
	 *
	 */
	private String dependsOn = "";

	/**
	 * size String.
	 */
	private String size = "300";

	/**
	 *  true - in case of static lists eg. class, type
	 *  false - in case of dynamic lists eg. site, user
	 */
	private String staticField = "true";

	/**
	 * input Image Tag.
	 */
	private final transient String inputImageTag = "<input type=\"text\" class=\"{0}\""
			+ " value=\"{1}\"size=\"{2}\""
			+ " id=\"{3}\" name=\"{4}\"onmouseover=\"showTip(this.id)\""
			+ " onmouseout=\"hideTip(this.id)\"{5}/>"
			+ "<image id='{6}' src='images/autocompleter.gif' alt='Click'"
			+ " width='18' height='19' hspace='0' vspace='0' align='absmiddle' />";
	/**
	 * script.
	 */
	private final transient String script = "<script> var valuesInList = new Array();{0}{1}";

	/**
	 * autocompleter.
	 */
	private final transient String autocompleter = "var AutoC = new Autocompleter"
			+ ".Combobox(\"{0}\",\"divFor{0}\",\"{2}\",valuesInList,"
			+ "  { tokens: new Array(), fullSearch: true, partialSearch:"
			+ " true,defaultArray:valuesInList,choices: {3},"
			+ "autoSelect:true, minChars: {4} });</script>";

	/**
	 * script For Dynamic Property.
	 */
	private final transient String scriptForDynamicProperty = "<script> var valuesInListOf{0} = new Array();"
			+ "var idsInListOf{1} = new Array();";

	/**
	 * input Image Tag For Dynamic Property.
	 */
	private final transient String inImTagForDP = "<input type=\"text\" class=\"{0}\""
			+ " value=\"{1}\"size=\"{2}\" id=\"{3}\" name=\"{4}\""
			+ "onmouseover=\"showTip(this.id)\" onmouseout=\"hideTip(this.id)\"{5}{6}{7}/>"
			+ "<image id=\"{8}\" src='images/autocompleter.gif' alt='Click' width='18'"
			+ " height='19' hspace='0' vspace='0' align='absmiddle'/>"
			+ "<input type=\"hidden\" id=\"{9}\" name=\"{10}\"value=\"{11}\"/>";

	/**
	 * autocompleter For Dynamic Property.
	 */
	private final transient String autocompleterDP = "{0}new Autocompleter.Combobox(\"{1}\",\"{2}\",\"{3}\""
			+ ",valuesInListOf{4},  * tokens: new Array(), fullSearch: true,"
			+ " partialSearch: true,defaultArray:"
			+ "valuesInListOf{5},choices: {6},autoSelect:true, minChars: {7} #);";

	/**
	 * A call back function, which gets executed by JSP runtime when opening tag
	 * for this custom tag is encountered.
	 * @exception JspException jsp exception.
	 * @return integer value to skip body.
	 */
	public int doStartTag() throws JspException
	{

		try
		{
			JspWriter out = pageContext.getOut();
			String autocompleteHTMLStr = null;
			if (staticField.equalsIgnoreCase("true"))
			{
				autocompleteHTMLStr = getAutocompleteHTML();
			}
			else
			{
				autocompleteHTMLStr = getAutocompleteHTMLForDynamicProperty();
			}

			/**
			  *  Clearing the variables
			  */
			onChange = "";
			initialValue = "";
			readOnly = "false";
			dependsOn = "";
			size = "300";
			disabled = "false";

			out.print(autocompleteHTMLStr);

		}
		catch (IOException ioe)
		{
			throw new JspTagException("Error:IOException while writing to the user");
		}

		return SKIP_BODY;
	}

	//	@SuppressWarnings("unchecked")
	/**
	 * @return String auto complete result.
	 */
	private String getAutocompleteHTML()
	{
		StringBuffer autoCompleteResult = new StringBuffer();

		prepareCommonData();
		/**
		 *  Always pass the function with brackets, appending '()' will not be done
		 */
		setOnchangeValue();
		autoCompleteResult.append("<div id=\"divFor").append(property).append(
				"\" style=\"display: none;\" class=\"autocomplete\"></div>");
		StringBuffer readOnly = getReadOnlyValue();
		String nameOfArrow = property + "arrow";
		Object[] arguments = {styleClass, initialValue, size, property, property,
				readOnly.toString(), nameOfArrow};
		autoCompleteResult.append(MessageFormat.format(inputImageTag, arguments));
		StringBuffer valueList = getValueInList();
		String autocomplet = "";
		if (property.equals(Constants.SPECIMEN_TYPE))
		{
			Object[] args = {property, nameOfArrow, numberOfResults, numberOfCharacters};
			autocomplet = MessageFormat.format(autocompleter, args);
		}
		Object[] arg = {valueList, autocomplet};
		autoCompleteResult.append(MessageFormat.format(script, arg));
		return autoCompleteResult.toString();
	}

	/**
	 * @return valueList.
	 */
	private StringBuffer getValueInList()
	{
		StringBuffer valueList = new StringBuffer();
		if (optionsList instanceof List)
		{
			List nvbList = (List) optionsList;
			if (nvbList != null && !nvbList.isEmpty())
			{
				for (int i = 0; i < nvbList.size(); i++)
				{
					NameValueBean nvb = (NameValueBean) nvbList.get(i);
					valueList.append("valuesInList[" + i + "] = \"")
					.append(nvb.getName()).append("\";");
				}
			}
		}
		return valueList;
	}

	/**
	 * @return readOnly.
	 */
	private StringBuffer getReadOnlyValue()
	{
		StringBuffer readOnly = new StringBuffer();
		if (readOnly.toString().equalsIgnoreCase("true"))
		{
			readOnly.append("readonly");
		}
		else
		{
			readOnly.append("onblur=\"").append(onChange).append('\"');
		}
		return readOnly;
	}

	/**
	 * set Onchange Value.
	 */
	private void setOnchangeValue()
	{
		if (TextConstants.EMPTY_STRING.equals(onChange))
		{
			onChange = "trimByAutoTag(this)";
		}
		else
		{
			onChange = "trimByAutoTag(this);" + onChange;
		}
	}

	//@SuppressWarnings("unchecked")
	/**
	 * This method prepare common data.
	 */
	private void prepareCommonData()
	{
		setInitialValue();
		/**
		 *  As Type depends on class, get the optionsList
		 *  as List from optionsList which was passed as a map
		 */
		if (property.equalsIgnoreCase(Constants.SPECIMEN_TYPE) && dependsOn != null
				&& !dependsOn.equals(""))
		{
			String className = dependsOn;
			List specimenTypeList = (List) ((Map) optionsList).get(className);
			optionsList = specimenTypeList;
		}

		/**
		 *  Converting other data types to list of Name Value Beans
		 */
		processStringArrayOptionList();
		processListOptionList();

	}

	/**
	 * set Initial Value.
	 */
	private void setInitialValue()
	{
		if (initialValue == null || initialValue.equals(""))
		{
			initialValue = pageContext.getRequest().getParameter(property);

			assignInitialValue();
		}
	}

	/**
	 * assign Initial Value.
	 */
	private void assignInitialValue()
	{
		if (initialValue == null)
		{
			String[] title = (String[]) pageContext.getRequest().getParameterValues(property);

			if (title != null && title.length > 0)
			{
				initialValue = title[0];
			}
			else
			{
				initialValue = "";
			}

		}
	}

	/**
	 * process OptionList.
	 */
	private void processListOptionList()
	{
		if (optionsList instanceof List)
		{

			List nvbList = (List) optionsList;
			if (nvbList != null && !nvbList.isEmpty())
			{

				// TODO other than NVB
				NameValueBean nvb1 = (NameValueBean) nvbList.get(0);
				if (nvb1.getName().equals(Constants.SELECT_OPTION))
				{
					nvbList.remove(0);
				}

			}
			/* if(nvbList == null || nvbList.size() == 0)
			{
				initialValue = "No Records Present"; // needed?
			}  */
		}
	}

	/**
	 * process String Array OptionList.
	 */
	private void processStringArrayOptionList()
	{
		if (optionsList instanceof String[])
		{
			String[] stringArray = (String[]) optionsList;
			List tempNVBList = new ArrayList();
			if (stringArray != null)
			{
				for (int i = 0; i < stringArray.length; i++)
				{
					tempNVBList.add(new NameValueBean(stringArray[i], stringArray[i]));
				}
			}
			optionsList = tempNVBList;
		}
	}

	/**
	 * This function prepares the HTML to be rendered.
	 * @return String - containing HTML to be rendered
	 *
	 */
	//@SuppressWarnings("unchecked")
	private String getAutocompleteHTMLForDynamicProperty()
	{
		StringBuffer autoCompleteResult = new StringBuffer();
		String displayProperty = "display" + property;
		prepareCommonData();
		/**
		 *  Always pass the function with brackets, appending '()' will not be done
		 */
		setOnChangeValueForDynamicProperty();
		String name = setInitialValueForDynamicProperty(displayProperty);
		String value = "";
		if (optionsList instanceof List)
		{
			List nvbList = (List) optionsList;
			for (int i = 0; i < nvbList.size(); i++)
			{
				NameValueBean nvb1 = (NameValueBean) nvbList.get(i);
				boolean nvbValue = nvb1.getValue().equals(initialValue) || nvb1.getValue() != null
						&& "00".equals(nvb1.getValue()) && "0".equals(initialValue);
				if (nvbValue)
				{
					name = nvb1.getName();
					value = nvb1.getValue();
					break;
				}
			}
		}

		String div = "divFor" + displayProperty;
		autoCompleteResult.append("<div id=\"").append(div).append(
				"\" style=\"display: none;\" class=\"autocomplete\"></div>");
		String nameOfArrow = property + "arrow";
		setInputImageTag(autoCompleteResult, nameOfArrow, name, value);
		Object[] scriptArgs = {displayProperty, displayProperty};
		autoCompleteResult.append(MessageFormat.format(scriptForDynamicProperty, scriptArgs));
		setAutocompleter(autoCompleteResult, displayProperty, div, nameOfArrow);
		autoCompleteResult.append("</script>");

		return autoCompleteResult.toString();
	}

	/**
	 * set Input Image Tag.
	 * @param autoCompleteResult autoCompleteResult
	 * @param nameOfArrow name Of Arrow.
	 * @param name name
	 * @param value value
	 */
	private void setInputImageTag(StringBuffer autoCompleteResult, String nameOfArrow, String name,
			String value)
	{
		String displayProperty = "display" + property;
		String readOnlyValue = "";
		if (readOnly.toString().equalsIgnoreCase("true"))
		{
			readOnlyValue = "readonly";
		}

		String onBlur = " onblur=\"" + onChange + "\"";
		String isDisabled = "";
		if (disabled.toString().equalsIgnoreCase("true"))
		{
			isDisabled = "disabled=\"true\"";
		}

		Object[] inputtImageTagArgs = {styleClass, name, size, displayProperty, displayProperty,
				readOnlyValue, onBlur, isDisabled, nameOfArrow, property, property, value};
		autoCompleteResult.append(MessageFormat.format(inImTagForDP, inputtImageTagArgs));
	}

	/**
	 * @param autoCompleteResult autoComplete Result
	 * @param displayProperty display Property
	 * @param div div
	 * @param nameOfArrow name Of Arrow.
	 */
	private void setAutocompleter(StringBuffer autoCompleteResult, String displayProperty,
			String div, String nameOfArrow)
	{
		StringBuffer valueIds = new StringBuffer();
		if (optionsList instanceof List)
		{
			List nvbList = (List) optionsList;
			if (nvbList != null && !nvbList.isEmpty())
			{

				for (int i = 0; i < nvbList.size(); i++)
				{
					NameValueBean nvb = (NameValueBean) nvbList.get(i);
					valueIds.append("valuesInListOf").append(displayProperty)
					.append('[').append(i)
							.append("] = \"").append(nvb.getName()).append("\";");
					valueIds.append("idsInListOf").append(displayProperty)
					.append('[').append(i)
							.append("] = \"").append(nvb.getValue()).append("\";");
				}
				/**
				 *  Giving call to autocompleter constructor
				 */
				Object[] autocompleterArgs = {valueIds, displayProperty, div, nameOfArrow,
						displayProperty, displayProperty,
						numberOfResults, numberOfCharacters};
				String autoCompleteStr = MessageFormat.format(autocompleterDP, autocompleterArgs);
				Utility.replaceAll(autoCompleteStr, "*", "{");
				Utility.replaceAll(autoCompleteStr, "#", "}");
				autoCompleteResult.append(autoCompleteStr);
			}
		}
	}

	/**
	 * set OnChange Value For Dynamic Property.
	 */
	private void setOnChangeValueForDynamicProperty()
	{
		if ("".equals(onChange))
		{
			onChange = "trimByAutoTagAndSetIdInForm(this)";
		}
		else
		{
			onChange = "trimByAutoTagAndSetIdInForm(this);" + onChange;
		}
	}

	/**
	 * @param displayProperty display Property
	 * @return name
	 */
	private String setInitialValueForDynamicProperty(String displayProperty)
	{
		String name = "";
		if ("0".equals(initialValue) || initialValue.toString().equalsIgnoreCase("undefined")
				|| "".equals(initialValue))
		{
			name = assignInitialValueToName(displayProperty);
		}
		return name;
	}

	/**
	 * @param displayProperty display Property.
	 * @return name
	 */
	private String assignInitialValueToName(String displayProperty)
	{
		String name;
		name = pageContext.getRequest().getParameter(displayProperty);

		if (name == null || name.equals(""))
		{
			String[] title = (String[]) pageContext.getRequest()
					.getParameterValues(displayProperty);

			if (title != null && title.length > 0)
			{
				name = title[0];
			}
			else
			{
				name = "";
			}
		}
		return name;
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
	 * @return String number of characters.
	 */
	public String getNumberOfCharacters()
	{
		return numberOfCharacters;
	}

	/**
	 * @param numberOfCharacters String number of characters.
	 */
	public void setNumberOfCharacters(String numberOfCharacters)
	{
		this.numberOfCharacters = numberOfCharacters;
	}

	/**
	 * @return String number of results.
	 */
	public String getNumberOfResults()
	{
		return numberOfResults;
	}

	/**
	 * @param numberOfResults String number of results.
	 */
	public void setNumberOfResults(String numberOfResults)
	{
		this.numberOfResults = numberOfResults;
	}

	/**
	 * @return Object list of options.
	 */
	public Object getOptionsList()
	{
		return optionsList;
	}

	/**
	 * @param optionsList Object set the list of option.
	 */
	public void setOptionsList(Object optionsList)
	{
		this.optionsList = optionsList;
	}

	/**
	 * @return String name of property.
	 */
	public String getProperty()
	{
		return property;
	}

	/**
	 * @param property String set the property.
	 */
	public void setProperty(String property)
	{
		this.property = property;
	}

	/**
	 * @return String size.
	 */
	public String getSize()
	{
		return size;
	}

	/**
	 * @param size String set the size.
	 */
	public void setSize(String size)
	{
		this.size = size;
	}

	/**
	 * @return String style class.
	 */
	public String getStyleClass()
	{
		return styleClass;
	}

	/**
	 * @param styleClass set the style class.
	 */
	public void setStyleClass(String styleClass)
	{
		this.styleClass = styleClass;
	}

	/**
	 * @return Object initial value.
	 */
	public Object getInitialValue()
	{
		return initialValue;
	}

	/**
	 * @param initialValue Object set initial value.
	 */
	public void setInitialValue(Object initialValue)
	{
		if (initialValue != null)
		{
			this.initialValue = initialValue.toString();
		}
	}

	/**
	 * @return Returns the onChange.
	 */
	public String getOnChange()
	{
		return onChange;
	}

	/**
	 * @param onChange The onChange to set.
	 */
	public void setOnChange(String onChange)
	{
		this.onChange = onChange;
	}

	/**
	 * @return Returns the readOnly.
	 */
	public Object getReadOnly()
	{
		return readOnly;
	}

	/**
	 * @param readOnly The readOnly to set.
	 */
	public void setReadOnly(Object readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * @return Returns the dependsOn.
	 */
	public String getDependsOn()
	{
		return dependsOn;
	}

	/**
	 * @param dependsOn The dependsOn to set.
	 */
	public void setDependsOn(String dependsOn)
	{
		this.dependsOn = dependsOn;
	}

	/**
	 * @return Returns the staticField.
	 */
	public String getStaticField()
	{
		return staticField;
	}

	/**
	 * @param staticField The staticField to set.
	 */
	public void setStaticField(String staticField)
	{
		this.staticField = staticField;
	}

	/**
	 * @return Returns the disabled.
	 */
	public Object getDisabled()
	{
		return disabled;
	}

	/**
	 * @param disabled The disabled to set.
	 */
	public void setDisabled(Object disabled)
	{
		this.disabled = disabled;
	}

}