
package edu.wustl.common.querysuite.queryobject;

/**
 * The implementing class will take IFunctionalClass in constructor. There is no
 * setClass method; the factory method creating IExpression or IRule will take
 * IFunctionalClass as input.
 * @version 1.0
 * @updated 11-Oct-2006 02:56:16 PM
 */
public interface IExpressionOperand
{

	public boolean isSubExpressionOperand();

}
