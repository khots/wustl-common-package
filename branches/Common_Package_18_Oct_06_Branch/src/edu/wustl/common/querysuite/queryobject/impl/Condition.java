
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
import edu.wustl.common.util.global.Constants;

public class Condition implements ICondition
{

	private static final long serialVersionUID = -307627971270099316L;
	private IAttribute attribute;
	private RelationalOperator relationalOperator;
	private List<String> values = new ArrayList<String>();

	public Condition(IAttribute attribute, RelationalOperator relationalOperator,
			List<String> values)
	{
		this.attribute = attribute;
		this.relationalOperator = relationalOperator;
		if (values != null)
			this.values = values;
	}

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
		if (values.isEmpty())
			values.add(value);
		else
			values.set(0, value);
	}

	/**
	 * @param values
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setValues(java.util.List)
	 */
	public void setValues(List<String> values)
	{
		if (values==null)
			values = new ArrayList<String>();
		this.values = values;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (attribute!=null)
			hash = hash*Constants.HASH_PRIME + attribute.hashCode();
		if (relationalOperator!=null)
			hash = hash*Constants.HASH_PRIME + relationalOperator.hashCode();
		
		return hash;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj != null && this.getClass() == obj.getClass())
		{
			Condition condition = (Condition) obj;
			if (attribute != null && attribute.equals(condition.attribute)
					&& relationalOperator != null
					&& relationalOperator.equals(condition.relationalOperator)
					&& values.equals(condition.values))
			{
				return true;
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
		return "[" + attribute.getAttributeName() + "." + relationalOperator + " = " + values + "]";
	}

}
