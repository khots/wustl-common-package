
package edu.wustl.common.querysuite.queryobject;

/**
 * An association between two classes...
 * 
 * This is an "abstract interface" i.e. no class will implement this interface,
 * but classes will implement one of the subinterfaces of IAssociation.
 * 
 * @version 1.0
 * @updated 11-Oct-2006 02:55:29 PM
 */
public interface IAssociation
{

	public IClass getSourceClass();

	public IClass getTargetClass();

	/**
	 * identifies which is the "concrete subinterface"
	 */
	public boolean isIntraModel();

	/**
	 * @param leftClass
	 * 
	 */
	public void setSourceClass(IClass leftClass);

	/**
	 * @param rightClass
	 * 
	 */
	public void setTargetClass(IClass rightClass);

	/**
	 * true if we can also navigate from targetClass to sourceClass; false otherwise
	 */
	public boolean isBidirectional();

	/**
	 * @param bidirectional
	 * 
	 */
	public void setBidirectional(boolean bidirectional);

}
