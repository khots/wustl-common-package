
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 11.54.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;

public class Condition implements ICondition
{

	private IAttribute attribute;
	private RelationalOperator relationalOperator;
	private List<String> values = new ArrayList<String>();

	/**
	 * @param value
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#addValue(java.lang.String)
	 */
	public void addValue(String value)
	{
		values.add(value);
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getAttribute()
	 */
	public IAttribute getAttribute()
	{
		return attribute;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getRelationalOperatorCode()
	 */
	public RelationalOperator getRelationalOperator()
	{
		return relationalOperator;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getValue()
	 */
	public String getValue()
	{
		if (!this.values.isEmpty())
			return values.get(0);
		return null;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getValues()
	 */
	public List<String> getValues()
	{
		return values;
	}

	/**
	 * @param attribute
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setAttribute(edu.wustl.common.querysuite.queryobject.IAttribute)
	 */
	public void setAttribute(IAttribute attribute)
	{
		this.attribute = attribute;
	}

	/**
	 * @param relationalOperator
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setRelationalOperatorCode(edu.wustl.common.querysuite.queryobject.RelationalOperator)
	 */
	public void setRelationalOperator(RelationalOperator relationalOperator)
	{
		this.relationalOperator = relationalOperator;
	}

	/**
	 * @param value
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setValue(java.lang.String)
	 */
	public void setValue(String value)
	{
		values.set(0, value);
	}

	/**
	 * @param values
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setValues(java.util.List)
	 */
	public void setValues(List<String> values)
	{
		this.values = values;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Condition)
		{
			Condition condition = (Condition) obj;
			if (attribute != null && attribute.equals(condition.attribute)
					&& relationalOperator != null
					&& relationalOperator.equals(condition.relationalOperator))
			{
				if (values.size() == condition.values.size())
				{
					for (int i = 0; i < values.size(); i++)
					{
						if (!values.get(i).equals(condition.values.get(i)))
							return false;
					}
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + attribute + "." + relationalOperator + " = " + values + "]";
	}

}
