
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.DataType;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 11.27.04 AM
 */

public class Attribute implements IAttribute
{

	private static final long serialVersionUID = -7803229839713253088L;
	private DataType dataType;
	private IClass umlCLass;
	private String attributeName;

	public Attribute(DataType dataType, IClass umlCLass, String attributeName)
	{
		this.dataType = dataType;
		this.umlCLass = umlCLass;
		this.attributeName = attributeName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAttribute#getAttributeName()
	 */
	public String getAttributeName()
	{
		return attributeName;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAttribute#getDataType()
	 */
	public DataType getDataType()
	{
		return dataType;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IAttribute#getUMLClass()
	 */
	public IClass getUMLClass()
	{
		return umlCLass;
	}

	/**
	 * @param name
	 * @see edu.wustl.common.querysuite.queryobject.IAttribute#setAttributeName(java.lang.String)
	 */
	public void setAttributeName(String name)
	{
		attributeName = name;
	}

	/**
	 * @param dataType
	 * @see edu.wustl.common.querysuite.queryobject.IAttribute#setDataType(edu.wustl.common.querysuite.queryobject.DataType)
	 */
	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * @param klass
	 * @see edu.wustl.common.querysuite.queryobject.IAttribute#setUMLClass(edu.wustl.common.querysuite.queryobject.IClass)
	 */
	public void setUMLClass(IClass klass)
	{
		umlCLass = klass;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj != null && this.getClass() == obj.getClass())
		{
			Attribute attribute = (Attribute) obj;
			if (umlCLass != null && umlCLass.equals(attribute.umlCLass) && attributeName != null
					&& attributeName.equals(attribute.attributeName))
				return true;
		}
		return false;
	}

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (umlCLass!=null)
			hash = hash*Constants.HASH_PRIME + umlCLass.hashCode();
		if (attributeName!=null)
			hash = hash*Constants.HASH_PRIME + attributeName.hashCode();
		return hash;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		
		return "[" + umlCLass.getFullyQualifiedName() + "." + attributeName + "]";
	}

}