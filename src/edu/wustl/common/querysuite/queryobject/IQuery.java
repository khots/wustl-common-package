/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryobject;

/**
 * The query object... this is the unit built from UI, persisted, and from which
 * queries will be built.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:13 PM
 */
public interface IQuery extends IBaseQueryObject
{

	/**
	 * @return the reference to constraints 
	 * @see IConstraints
	 */
	IConstraints getConstraints();

	/**
	 * To set the constraints object.
	 * @param constraints the constraints to set.
	 */
	void setConstraints(IConstraints constraints);

	/**
	 * All the attributes of all the classes are selected. Note that the "class"
	 * here is IFunctionalClass, a cab2b specific user-desired subset of
	 * attributes will be present in the IFunctionalClass. The output classes
	 * form a tree where the edges are associations.
	 * @return the reference to the root noe of the output tree.
	 * @see IOutputTreeNode
	 * @deprecated This method is not required any more for output tree.
	 */
	IOutputTreeNode getRootOutputClass();

	/**
	 * To set the output tree for the Query result view.
	 * @param root The reference to the root noe of the output tree.
	 * @deprecated This method is not required any more for output tree.
	 */
	void setRootOutputClass(IOutputTreeNode root);
}
