
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 15.00.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;

public class JoinGraph implements IJoinGraph
{

	private List<IAssociation> associations = new ArrayList<IAssociation>();

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#containsAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean containsAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public IAssociation getAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#isConnected()
	 */
	public boolean isConnected()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#putAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IAssociation)
	 */
	public IAssociation putAssociation(IExpressionId parentExpressionIndex,
			IExpressionId childExpressionIndex, IAssociation association)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#removeAssociation(edu.wustl.common.querysuite.queryobject.IExpressionId, edu.wustl.common.querysuite.queryobject.IExpressionId)
	 */
	public boolean removeAssociation(IExpressionId firstExpressionIndex,
			IExpressionId secondExpressionIndex)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IJoinGraph#getRoot()
	 */
	public IExpressionId getRoot() throws MultipleRootsException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
