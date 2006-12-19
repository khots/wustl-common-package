/**
 * 
 */

package edu.wustl.common.querysuite.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.exceptions.DuplicateChildException;
import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;
import edu.wustl.common.querysuite.queryobject.IOutputTreeNode;

/**
 * @author prafull_kadam
 * Class implementation for IOutputTreeNode.
 */
public class OutputTreeNode implements IOutputTreeNode
{

	private static final long serialVersionUID = -4576110943253229029L;

	private IOutputTreeNode parentNode;
	private IAssociation parentAssociation; // association of this obect with parent.

	private List<IOutputTreeNode> children = new ArrayList<IOutputTreeNode>();

	// List of associations of children with Parent. childrenAssoiations[i] represents Association of children[i] with parent node. 
	private List<IAssociation> childrenAssoiations = new ArrayList<IAssociation>();

	IFunctionalClass functionalClass;

	/**
	 * The Constructor to instantiate the object of this class.
	 * @param iFunctionalClass The reference to the Functional class.
	 */
	public OutputTreeNode(IFunctionalClass iFunctionalClass)
	{
		this.functionalClass = iFunctionalClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#addChild(edu.wustl.common.querysuite.queryobject.IAssociation, edu.wustl.common.querysuite.queryobject.IFunctionalClass)
	 */
	public IOutputTreeNode addChild(IAssociation association, IFunctionalClass functionalClass)
			throws DuplicateChildException
	{

		for (int index = 0; index < children.size(); index++)
		{
			if (functionalClass.equals(children.get(index).getFunctionalClass())
					&& association.equals(childrenAssoiations.get(index)))
			{
				throw new DuplicateChildException();
			}
		}

		//child does not exist with same functionalClass & association, so adding new child for the output tree node.
		OutputTreeNode child = new OutputTreeNode(functionalClass);
		child.parentNode = this;
		child.parentAssociation = association;
		children.add(child);
		childrenAssoiations.add(association);

		return child;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getAssociationWithParent()
	 */
	public IAssociation getAssociationWithParent()
	{
		return parentAssociation;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getAssociationsWithChild(edu.wustl.common.querysuite.queryobject.IFunctionalClass)
	 */
	public List<IAssociation> getAssociationsWithChild(IFunctionalClass child)
	{
		List<IAssociation> associationList = new ArrayList<IAssociation>();

		for (int i = 0; i < children.size(); i++)
		{
			if (children.get(i).getFunctionalClass().equals(functionalClass))
				associationList.add(childrenAssoiations.get(i));
		}
		return associationList;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getChildren()
	 */
	public List<IOutputTreeNode> getChildren()
	{
		return children;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getFunctionalClass()
	 */
	public IFunctionalClass getFunctionalClass()
	{
		return functionalClass;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getParent()
	 */
	public IOutputTreeNode getParent()
	{
		return parentNode;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#isLeaf()
	 */
	public boolean isLeaf()
	{
		return children.isEmpty();
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#isRoot()
	 */
	public boolean isRoot()
	{
		return (this.parentNode == null);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#removeChild(edu.wustl.common.querysuite.queryobject.IAssociation, edu.wustl.common.querysuite.queryobject.IFunctionalClass)
	 */
	public boolean removeChild(IAssociation association, IFunctionalClass functionalClass)
	{
		boolean isRemoved = false;

		for (int index = 0; index < children.size(); index++)
		{
			if (functionalClass.equals(children.get(index).getFunctionalClass())
					&& association.equals(childrenAssoiations.get(index)))
			{
				//corresponding child found, so removing child & childassociation from children list & childrenAssoiations list.
				children.remove(index);
				childrenAssoiations.remove(index);
				isRemoved = true;
				break;
			}
		}
		return isRemoved;
	}

}
