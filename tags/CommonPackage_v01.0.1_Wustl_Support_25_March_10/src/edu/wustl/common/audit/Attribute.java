package edu.wustl.common.audit;

/**
 * This class represents the simple attributes for the AuditableClass.
 * @author niharika_sharma
 *
 */
public class Attribute
{

	/**
	 * Name of the attribute.
	 */
	String name;
	
	/**
	 * Data type of the attribute.
	 */
	String dataType;

	public String getDataType()
	{
		return dataType;
	}

	
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
