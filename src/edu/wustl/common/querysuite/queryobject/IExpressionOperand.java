/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryobject;

/**
 * A marker interface for an operand. An operand is either a subexpression (in
 * which case, the corresponding expression id is added), or a rule.
 * @version 1.0
 * @updated 11-Oct-2006 02:56:16 PM
 */
public interface IExpressionOperand extends IBaseQueryObject
{

	/**
	 * Used to determine whether the operand is an expression or a rule.
	 * @return true- if subexpression, false - if this is a rule.
	 */
	boolean isSubExpressionOperand();

}
