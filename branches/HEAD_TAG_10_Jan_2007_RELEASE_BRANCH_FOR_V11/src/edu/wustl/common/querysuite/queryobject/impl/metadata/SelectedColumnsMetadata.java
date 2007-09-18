package edu.wustl.common.querysuite.queryobject.impl.metadata;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.querysuite.queryobject.impl.OutputTreeDataNode;

/**
 * 
 * @author deepti_shelar
 *
 */
public class SelectedColumnsMetadata
{
	/**
	 * Selected object by user at the time of defining columns
	 */
	private OutputTreeDataNode currentSelectedObject;
	/**
	 * List of attributes user has selected 
	 */
	private List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList;
	/**
	 * List of name value bean object for selected columns
	 */
	private List<NameValueBean> selectedColumnNameValueBeanList;
	/**
	 * @return the list of attributes selected
	 */
	private List<AttributeInterface> attributeList;
	/**
	 * Returns true/false Whether view is defined.
	 */
	private boolean isDefinedView;
	
	/**
	 * 
	 * @return
	 */
	public boolean isDefinedView()
	{
		return isDefinedView;
	}

	/**
	 * 
	 * @param isDefinedView
	 */
	public void setDefinedView(boolean isDefinedView)
	{
		this.isDefinedView = isDefinedView;
	}

	public List<AttributeInterface> getAttributeList()
	{
		return attributeList;
	}
	
	public void setAttributeList(List<AttributeInterface> attributeList)
	{
		this.attributeList = attributeList;
	}
	public OutputTreeDataNode getCurrentSelectedObject()
	{
		return currentSelectedObject;
	}
	/**
	 * @param currentSelectedObject the currentSelectedObject to set
	 */
	public void setCurrentSelectedObject(OutputTreeDataNode currentSelectedObject)
	{
		this.currentSelectedObject = currentSelectedObject;
	}
	/**
	 * @return the selectedColumnNameValueBeanList
	 */
	public List<NameValueBean> getSelectedColumnNameValueBeanList()
	{
		return selectedColumnNameValueBeanList;
	}
	/**
	 * @param selectedColumnNameValueBeanList the selectedColumnNameValueBeanList to set
	 */
	public void setSelectedColumnNameValueBeanList(List<NameValueBean> selectedColumnNameValueBeanList)
	{
		this.selectedColumnNameValueBeanList = selectedColumnNameValueBeanList;
	}
	/**
	 * @return the selectedAttributeMetaDataList
	 */
	public List<QueryOutputTreeAttributeMetadata> getSelectedAttributeMetaDataList()
	{
		return selectedAttributeMetaDataList;
	}
	/**
	 * @param selectedAttributeMetaDataList the selectedAttributeMetaDataList to set
	 */
	public void setSelectedAttributeMetaDataList(List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList)
	{
		this.selectedAttributeMetaDataList = selectedAttributeMetaDataList;
	}
}
