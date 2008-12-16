package edu.wustl.common.querysuite.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @author chetan_patil
 * @created 12-Oct-2006 11.54.04 AM
 *
 * @hibernate.class table="QUERY_CONDITION"
 * @hibernate.cache usage="read-write"
 */
public class Condition extends BaseQueryObject implements ICondition {
    private static final long serialVersionUID = -307627971270099316L;

    /**
     * Stores the identifier of the attribute object. This is required for the internal reference to the attribute.
     * Attribute is retrived using this property.
     */
    private Long attributeId;

    /**
     * Stores the associated attribute
     */
    private AttributeInterface attribute;

    /**
     * String representation of the RelationalOperator
     */
    private String relationalOperatorString;

    /**
     * Stores the associated relational operator
     */
    private RelationalOperator relationalOperator;

    /**
     * Stores the values of the condition
     */
    private List<String> values = new ArrayList<String>();

    /**
     * Default constructor
     */
    public Condition() {

    }

    /**
     * Parameterized Constructor
     * 
     * @param attribute The Dynamic Extension attribute reference, The attribute on which condition to be formed.
     * @param relationalOperator The reference to relational Operator.
     * @param values The list of Strings representing value part of condition.
     */
    public Condition(AttributeInterface attribute, RelationalOperator relationalOperator, List<String> values) {
        this.attribute = attribute;
        this.relationalOperator = relationalOperator;

        if (values != null) {
            this.values = values;
        }
    }

    /**
     * Returns the identifier assigned to BaseQueryObject.
     * @return a unique id assigned to the Condition.
     * 
     * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30" unsaved-value="null" generator-class="native"
     * @hibernate.generator-param name="sequence" value="CONDITION_SEQ"
     */
    public Long getId() {
        return id;
    }

    /**
     * This method returns the attribute id
     * @return identifier of the attribute
     * 
     * @hibernate.property column="ATTRIBUTE_ID" type="long" length="30" not-null="true"
     */
    public Long getAttributeId() {
        return attributeId;
    }

    /**
     * @param attributeId the attributeId to set
     */
    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * This method returns the left operand of the condition.
     * @return The Dynamic extension attribute, the left operand of the condition.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#getAttribute()
     */
    public AttributeInterface getAttribute() {
        return attribute;
    }

    /**
     * @param attribute The reference to the attribute on which condition to be formed.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#setAttribute(edu.common.dynamicextensions.domaininterface.AttributeInterface)
     */
    public void setAttribute(AttributeInterface attribute) {
        this.attribute = attribute;
    }

    /**
     * This method returns the string representation of the RelationalOperator
     * @return the relationalOperatorString
     * 
     * @hibernate.property name="relationalOperator" type="string" column="RELATIONAL_OPERATOR"
     */
    public String getRelationalOperatorString() {
        return relationalOperatorString;
    }

    /**
     * This method sets the string representation of the RelationalOperator
     * @param relationalOperatorString the relationalOperatorString to set
     */
    public void setRelationalOperatorString(String relationalOperatorString) {
        this.relationalOperatorString = relationalOperatorString;
    }

    /**
     * This method returns the RelationalOperator
     * @return The relational operator of the Condition.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#getRelationalOperatorCode()
     */
    public RelationalOperator getRelationalOperator() {
        return relationalOperator;
    }

    /**
     * This method sets the RelationalOperator for the Condition.
     * @param relationalOperator reference to RelationalOperator.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#setRelationalOperatorCode(edu.wustl.common.querysuite.queryobject.RelationalOperator)
     */
    public void setRelationalOperator(RelationalOperator relationalOperator) {
        this.relationalOperator = relationalOperator;
    }

    /**
     * This method sets the list of values for the condtion.
     * @return The list of Strings representing right operand of the the condition.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#getValues()
     * 
     * @hibernate.list name="values" table="QUERY_CONDITION_VALUES" cascade="all-delete-orphan" inverse="false" lazy="false"
     * @hibernate.collection-key column="QUERY_CONDITION_ID"
     * @hibernate.collection-index column="POSITION" type="int"
     * @hibernate.collection-element type="string" column="VALUE_LIST"
     * @hibernate.cache usage="read-write"
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * To set the list of values for the Condtion.
     * @param values The List of String representing values for the condition.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#setValues(java.util.List)
     */
    public void setValues(List<String> values) {
        if (values == null) {
            values = new ArrayList<String>();
        }
        this.values = values;
    }

    /**
     * This method add value in the value list of condition.
     * 
     * @param value The String representing one of the value in value list of condition.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#addValue(java.lang.String)
     */
    public void addValue(String value) {
        values.add(value);
    }

    /**
     * @return The String representing first value in the value list of the condition.
     * @see edu.wustl.common.querysuite.queryobject.ICondition#getValue()
     */
    public String getValue() {
        if (!this.values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    /**
     * To set the value for condition. It will overrides older value if exists.
     * @param value The String representing value part of the condition. 
     * @see edu.wustl.common.querysuite.queryobject.ICondition#setValue(java.lang.String)
     */
    public void setValue(String value) {
        if (values.isEmpty()) {
            values.add(value);
        } else {
            values.set(0, value);
        }
    }

    /**
     * To get the HashCode for the object. It will be calculated based on attribute, relational operator.
     * @return The hash code value for the object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        if (attribute != null) {
            hash = hash * Constants.HASH_PRIME + attribute.hashCode();
        }
        if (relationalOperator != null) {
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() == obj.getClass()) {
            Condition condition = (Condition) obj;
            if (attribute != null && attribute.equals(condition.attribute) && relationalOperator != null
                    && relationalOperator.equals(condition.relationalOperator) && values.equals(condition.values)) {
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
    public String toString() {
        return "[" + attribute.getName() + relationalOperator + values + "]";
    }
}