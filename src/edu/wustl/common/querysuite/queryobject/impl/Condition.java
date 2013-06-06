/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 12-Oct-2006 11.54.04 AM
 */

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.util.global.Constants;

public class Condition implements ICondition
{

	private static final long serialVersionUID = -307627971270099316L;
	private AttributeInterface attribute;
	private RelationalOperator relationalOperator;
	private List<String> values = new ArrayList<String>();

	/**
	 * Constructor to instanciate condition object.
	 * @param attribute The Dynamic Extension attribute reference, The attribute on which condition to be formed.
	 * @param relationalOperator The reference to relational Operator.
	 * @param values The list of Strings representing value part of condition.
	 */
	public Condition(AttributeInterface attribute, RelationalOperator relationalOperator,
			List<String> values)
	{
		this.attribute = attribute;
		this.relationalOperator = relationalOperator;
		if (values != null)
		{
			this.values = values;
		}
	}

	/**
	 * To add value in the value list of condition.
	 * @param value The String representing one of the value in value list of condition.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#addValue(java.lang.String)
	 */
	public void addValue(String value)
	{
		values.add(value);
	}

	/**
	 * To get the left operand of the condition.
	 * @return The Dynamic extension attribute, the left operand of the condition.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getAttribute()
	 */
	public AttributeInterface getAttribute()
	{
		return attribute;
	}

	/**
	 * @return The relational operator of the Condition.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getRelationalOperatorCode()
	 */
	public RelationalOperator getRelationalOperator()
	{
		return relationalOperator;
	}

	/**
	 * @return The String representing first value in the value list of the condition.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getValue()
	 */
	public String getValue()
	{
		if (!this.values.isEmpty())
		{
			return values.get(0);
		}
		return null;
	}

	/**
	 * To get the list of values for the condtion.
	 * @return The list of Strings representing right operand of the the condition.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#getValues()
	 */
	public List<String> getValues()
	{
		return values;
	}

	/**
	 * @param attribute The reference to the attribute on which condition to be formed.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setAttribute(edu.common.dynamicextensions.domaininterface.AttributeInterface)
	 */
	public void setAttribute(AttributeInterface attribute)
	{
		this.attribute = attribute;
	}

	/**
	 * To set relational operator for the Condition.
	 * @param relationalOperator reference to RelationalOperator.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setRelationalOperatorCode(edu.wustl.common.querysuite.queryobject.RelationalOperator)
	 */
	public void setRelationalOperator(RelationalOperator relationalOperator)
	{
		this.relationalOperator = relationalOperator;
	}

	/**
	 * To set the value for condition. It will overrides older value if exists.
	 * @param value The String representing value part of the condition. 
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setValue(java.lang.String)
	 */
	public void setValue(String value)
	{
		if (values.isEmpty())
		{
			values.add(value);
		}
		else
		{
			values.set(0, value);
		}
	}

	/**
	 * To set the list of values for the Condtion.
	 * @param values The List of String representing values for the condition.
	 * @see edu.wustl.common.querysuite.queryobject.ICondition#setValues(java.util.List)
	 */
	public void setValues(List<String> values)
	{
		if (values == null)
		{
			values = new ArrayList<String>();
		}
		this.values = values;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on attribute, relational operator.
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		if (attribute != null)
		{
			hash = hash * Constants.HASH_PRIME + attribute.hashCode();
		}
		if (relationalOperator != null)
		{
			hash = hash * Constants.HASH_PRIME + relationalOperator.hashCode();
		}

		return hash;
	}

	/**
	 * To check whether two objects are equal.
	 * @param obj reference to the object to be checked for equality.
	 * @return true if attribute, relational operator & values of the condition are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
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
	 * 
	 * @return String representation of the object, in the format [attributeName relationalOperator Values].
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + attribute.getName() + relationalOperator + values + "]";
	}

}
