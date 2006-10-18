
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.07.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.queryobject.IExpressionList;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IQuery;

public class Query implements IQuery
{

	private IExpressionList constraints;
	private IJoinGraph joins;
	private List<IFunctionalClass> outputClasses = new ArrayList<IFunctionalClass>();

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#addOutputClass(edu.wustl.common.querysuite.queryobject.IFunctionalClass)
	 */
	public void addOutputClass(IFunctionalClass Klass)
	{
		outputClasses.add(Klass);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#getConstraints()
	 */
	public IExpressionList getConstraints()
	{
		return constraints;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#getJoins()
	 */
	public IJoinGraph getJoins()
	{
		return joins;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#getOutputClasses()
	 */
	public List<IFunctionalClass> getOutputClasses()
	{
		return outputClasses;
	}

	/**
	 * @param joinGraph
	 * @see edu.wustl.common.querysuite.queryobject.IQuery#setJoins(edu.wustl.common.querysuite.queryobject.IJoinGraph)
	 */
	public void setJoins(IJoinGraph joinGraph)
	{
		joins = joinGraph;
	}

}
