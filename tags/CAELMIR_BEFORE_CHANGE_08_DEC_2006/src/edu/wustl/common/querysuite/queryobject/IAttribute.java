
package edu.wustl.common.querysuite.queryobject;

/**
 * @version 1.0
 * @updated 11-Oct-2006 02:55:29 PM
 */
public interface IAttribute extends IBaseQueryObject
{

	public IClass getUMLClass();

	public String getAttributeName();

	/**
	 * @param klass
	 * 
	 */
	public void setUMLClass(IClass klass);

	/**
	 * @param name
	 * 
	 */
	public void setAttributeName(String name);

	public DataType getDataType();

	/**
	 * @param dataType
	 * 
	 */
	public void setDataType(DataType dataType);

}
