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

	private IExpressionId incomingExpressionId, outGoingExpressionId;
	private IAssociation association;

	/**
	 * Constructor to define Edge for the Join Graph.
	 * @param incommingExpression The Incomming Expression id.
	 * @param outGoingExpression The outgoing Expression id.
	 * @param association The Association between thow Expressions.
	 */
	public Edge(IExpressionId incomingExpressionId, IExpressionId outGoingExpressionId,
			IAssociation association)
	{
		this.incomingExpressionId = incomingExpressionId;
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
	public IExpressionId getIncomingExpressionId()
	{
		return incomingExpressionId;
	}

	/**
	 * @param incommingExpression the incommingExpression to set
	 */
	public void setIncomingExpressionId(IExpressionId incommingExpression)
	{
		this.incomingExpressionId = incommingExpression;
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
