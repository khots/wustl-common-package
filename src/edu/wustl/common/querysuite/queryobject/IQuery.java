
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

/**
 * The query object... this is the unit built from UI, persisted, and from which
 * queries will be built.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:13 PM
 */
public interface IQuery
{

	public IExpressionList getConstraints();

	public IJoinGraph getJoins();

	/**
	 * @param joinGraph
	 * 
	 */
	public void setJoins(IJoinGraph joinGraph);

	/**
	 * all the attributes of all the classes are selected. Note that the "class" here
	 * is IFunctionalClass, a cab2b specific user-desired subset of attributes will be
	 * present in the IFunctionalClass.
	 * the functional classes here are a subset of the functional classes that be
	 * found in the IExpressions in IExpressionList
	 */
	public List<IFunctionalClass> getOutputClasses();

	/**
	 * @param class
	 * 
	 */
	public void addOutputClass(IFunctionalClass Klass);

}
