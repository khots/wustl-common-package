/**
 * 
 */
package edu.wustl.common.querysuite.queryobject.impl;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.util.global.Constants;

/**
 * @author chetan_patil
 * @created Sep 27, 2007, 3:44:18 PM
 * 
 * @hibernate.class table="QUERY_OUTPUT_ATTRIBUTE"
 * @hibernate.cache usage="read-write"
 */
public class OutputAttribute extends BaseQueryObject {
    private static final long serialVersionUID = 1L;

    private IExpressionId expressionId;

    private AttributeInterface attribute;

    private Long attributeId;

    /** Default Constructor */
    public OutputAttribute() {

    }

    /**
     * Parameterized Constructor
     * @param expressionId
     * @param attribute
     */
    public OutputAttribute(IExpressionId expressionId, AttributeInterface attribute) {
        this.expressionId = expressionId;
        this.attribute = attribute;
    }

    /**
     * @return the expressionId
     * 
     * @hibernate.many-to-one column="EXPRESSIONID_ID" class="edu.wustl.common.querysuite.queryobject.impl.ExpressionId" unique="true" cascade="all" lazy="false"
     */
    public IExpressionId getExpressionId() {
        return expressionId;
    }

    /**
     * @param expressionId the expressionId to set
     */
    public void setExpressionId(IExpressionId expressionId) {
        this.expressionId = expressionId;
    }

    /**
     * @return the attribute
     */
    public AttributeInterface getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(AttributeInterface attribute) {
        this.attribute = attribute;
    }

    /**
     * @return the attributeId
     * 
     * @hibernate.property name="attributeId" column="ATTRIBUTE_ID" type="long" length="30" not-null="true"
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
     * Returns the identifier assigned to BaseQueryObject.
     * @return a unique id assigned to the Condition.
     * 
     * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30" unsaved-value="null" generator-class="native"
     * @hibernate.generator-param name="sequence" value="OUTPUT_ATTRIBUTE_SEQ"
     */
    public Long getId() {
        return id;
    }

    /**
     * To check equality of the two object.
     * @param obj to be check for equality.
     * @return true if objects are equals.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;

        if (this == object) {
            isEqual = true;
        } else if (object != null && this.getClass() == object.getClass()) {
            OutputAttribute outputAtrribute = (OutputAttribute) object;
            IExpressionId expressionId = outputAtrribute.getExpressionId();
            Long attributeId = outputAtrribute.getAttribute().getId();

            if (this.getExpressionId().equals(expressionId) && this.getAttribute().getId().equals(attributeId)) {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     * To get the HashCode for the object.
     * @return The hash code value for the object.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if (expressionId != null && attribute != null && attributeId != null) {
            hash *= Constants.HASH_PRIME + expressionId.hashCode();
            hash *= Constants.HASH_PRIME + attribute.hashCode();
            hash *= Constants.HASH_PRIME + attributeId.hashCode();
        }
        return hash;
    }

}
