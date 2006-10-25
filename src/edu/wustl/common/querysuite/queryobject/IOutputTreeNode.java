
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

import edu.wustl.common.querysuite.exceptions.DuplicateChildException;

/**
 * A tree node to represent a (functional) class that is a part of the outputs
 * of the query. It is a tree with <br>
 * vertex : {@link edu.wustl.common.querysuite.queryobject.IFunctionalClass}
 * label on edge : {@link edu.wustl.common.querysuite.queryobject.IAssociation}.<br>
 * e.g. if a node with functionalClass Participant has a child node with
 * functionalClass Specimen, it means "select participants, and associated
 * specimens"; the association is as given by the label on the edge. Note that
 * the same functionalClass can be present multiple times in this tree.<br>
 * Let fc(X) denote the functionalClass of the node X; L(X,Y) denote the label
 * (association) on the edge from X to Y; children(X) denote the list of
 * children nodes of the node X. Given a node X, the only restriction is : given
 * any two children nodes of X, say, Y1 and Y2, fc(Y1) = fc(Y2) if and only if
 * L(X, Y1) != L(X, Y2).<br>
 * Thus, we can say "select specimens, and the available and used quantities of
 * the specimens". Here, Specimen is parent, with two children; both children
 * are the class Quantity, but the associations are different (available and
 * used quantities).<br>
 * @author srinath_k
 */
public interface IOutputTreeNode
{

	/**
	 * @param association
	 * @param functionalClass
	 * @return
	 * @throws DuplicateChildException
	 *             if there already exists a child with the given functional
	 *             class and association.
	 */
	IOutputTreeNode addChild(IAssociation association, IFunctionalClass functionalClass)
			throws DuplicateChildException;

	IFunctionalClass getFunctionalClass();

	List<IOutputTreeNode> getChildren();

	IAssociation getAssociationWithChild(IFunctionalClass child);

	IAssociation getAssociationWithParent();

	boolean removeChild(IAssociation association, IFunctionalClass functionalClass);

	/**
	 * @return Returns the parent.
	 */
	IOutputTreeNode getParent();

	boolean isLeaf();

	boolean isRoot();

}