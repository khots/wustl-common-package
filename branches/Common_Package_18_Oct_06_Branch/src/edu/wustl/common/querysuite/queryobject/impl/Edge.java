/**
 * 
 */

package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IExpressionId;

/**
 * @author prafull_kadam
 * @version 1.0
 * @created 19-Oct-2006
 */
public class Edge
{

	private IExpressionId incommingExpressionId, outGoingExpressionId;
	private IAssociation association;

	/**
	 * Constructor to define Edge for the Join Graph.
	 * @param incommingExpression The Incomming Expression id.
	 * @param outGoingExpression The outgoing Expression id.
	 * @param association The Association between thow Expressions.
	 */
	public Edge(IExpressionId incommingExpressionId, IExpressionId outGoingExpressionId,
			IAssociation association)
	{
		this.incommingExpressionId = incommingExpressionId;
		this.outGoingExpressionId = outGoingExpressionId;
		this.association = association;
	}

	/**
	 * @return the association
	 */
	public IAssociation getAssociation()
	{
		return association;
	}

	/**
	 * @param association the association to set
	 */
	public void setAssociation(IAssociation association)
	{
		this.association = association;
	}

	/**
	 * @return the incommingExpression
	 */
	public IExpressionId getIncommingExpression()
	{
		return incommingExpressionId;
	}

	/**
	 * @param incommingExpression the incommingExpression to set
	 */
	public void setIncommingExpression(IExpressionId incommingExpression)
	{
		this.incommingExpressionId = incommingExpression;
	}

	/**
	 * @return the outGoingExpression
	 */
	public IExpressionId getOutGoingExpression()
	{
		return outGoingExpressionId;
	}

	/**
	 * @param outGoingExpression the outGoingExpression to set
	 */
	public void setOutGoingExpression(IExpressionId outGoingExpression)
	{
		this.outGoingExpressionId = outGoingExpression;
	}

}
