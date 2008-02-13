package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @author chetan_patil
 * @version 1.0
 * @created 12-Oct-2006 11.12.04 AM
 * 
 * @hibernate.joined-subclass table="QUERY_EXPRESSIONID" 
 * @hibernate.joined-subclass-key column="IDENTIFIER" 
 */
public class ExpressionId extends ExpressionOperand implements IExpressionId {
    private static final long serialVersionUID = 2012640054952775498L;

    private int expressionIdentifier;
    
    /**
     * Default Constructor
     */
    public ExpressionId() {
        
    }
    
    /**
     * Parameterized Constructor
     * @param id The id to be assigned.
     */
    public ExpressionId(int expressionId) {
        this.expressionIdentifier = expressionId;
    }

    /**
     * This method returns the expression id.
     * @return The integer value sassigned to this Expression id.
     * @see edu.wustl.common.querysuite.queryobject.IExpressionId#getExpressionIdentifier()
     * 
     * @hibernate.property name="expressionId" column="SUB_EXPRESSION_ID" type="integer" length="30" not-null="true"
     */
    private int getExpressionIdentifier() {
        return expressionIdentifier;
    }
    
    public int getInt() {
        return expressionIdentifier;
    }
    
    /**
     * This method sets the expression id.
     * @param expressionId
     */
    private void setExpressionIdentifier(int expressionIdentifier) {
        this.expressionIdentifier = expressionIdentifier;
    }

    /**
     * This method will return true value always.
     * @return true.
     * @see edu.wustl.common.querysuite.queryobject.IExpressionOperand#isSubExpressionOperand()
     */
    public boolean isSubExpressionOperand() {
        return true;
    }

    /**
     * To get the HashCode for the object. It will be calculated based on expression Id.
     * @return The hash code value for the object.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * Constants.HASH_PRIME + expressionIdentifier;

        return hash;
    }

    /**
     * To check whether two objects are equal.
     * @param obj reference to the object to be checked for equality.
     * @return true if expression id of both objects are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() == obj.getClass()) {
            ExpressionId expressionId = (ExpressionId) obj;
            if (this.expressionIdentifier == expressionId.expressionIdentifier) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return String representation of Expression Id object in the form: [ExpressionId: id]
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ExpressionId:" + expressionIdentifier;
    }

}
