
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

/**
 * A condition containing an attribute, relational operator and value(s).
 * e.g. participant.sex = 'Male'
 * 
 * @version 1.0
 * @updated 11-Oct-2006 02:56:07 PM
 */
public interface ICondition
{

	public IAttribute getAttribute();

	public RelationalOperator getRelationalOperator();

	public List<String> getValues();

	/**
	 * basically calls getValues(0)
	 */
	public String getValue();

	/**
	 * @param attribute
	 * 
	 */
	public void setAttribute(IAttribute attribute);

	/**
	 * @param relationalOperator
	 * 
	 */
	public void setRelationalOperator(RelationalOperator relationalOperator);

	/**
	 * Basically calls setValue(0, value). Use this for unary operators.
	 * @param value
	 * 
	 */
	public void setValue(String value);

	/**
	 * @param values
	 * 
	 */
	public void setValues(List<String> values);

	/**
	 * returns the value added
	 * @param value
	 * 
	 */
	public void addValue(String value);

}
