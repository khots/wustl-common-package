package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.util.global.Constants;

/**
 * @author Mandar Shidhore
 * @author chetan_patil
 * @version 1.0
 * @created 12-Oct-2006 15.04.04 AM
 * 
 * @hibernate.class table="QUERY_LOGICAL_CONNECTOR"
 * @hibernate.cache usage="read-write"
 */

public class LogicalConnector extends BaseQueryObject implements ILogicalConnector {
    private static final long serialVersionUID = 3065606993455242889L;

    private LogicalOperator logicalOperator;

    private String operatorString;

    private int nestingNumber = 0;

    /**
     * Default Constructor
     */
    public LogicalConnector() {

    }

    /**
     * Parameterized Constructor
     * @param logicalOperator The reference to the Logical operator.
     */
    public LogicalConnector(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    /**
     * The constructor to instantiate the Logical connector object with the given logical operator & nesting number.
     * @param logicalOperator The reference to the Logical operator.
     * @param nestingNumber The integer value which will decide no. of parenthesis surrounded by this operator.
     */
    public LogicalConnector(LogicalOperator logicalOperator, int nestingNumber) {
        this.logicalOperator = logicalOperator;
        this.nestingNumber = nestingNumber;
    }

    /**
     * Returns the identifier assigned to BaseQueryObject.
     * @return a unique id assigned to the Condition.
     * 
     * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30" unsaved-value="null" generator-class="native"
     * @hibernate.generator-param name="sequence" value="LOGICAL_CONNECTOR_SEQ"
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the reference to the Logical operator.
     * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#getLogicalOperator()
     */
    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    /**
     * @param logicOperatorCode The logical operator to set.
     * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#setLogicalOperator(edu.wustl.common.querysuite.queryobject.LogicalOperator)
     */
    public void setLogicalOperator(LogicalOperator logicOperatorCode) {
        logicalOperator = logicOperatorCode;
    }

    /**
     * @return the operatorString
     * 
     * @hibernate.property name="operatorString" column="LOGICAL_OPERATOR" type="string" update="true" Insert="true"
     */
    public String getOperatorString() {
        return operatorString;
    }

    /**
     * @param operatorString the operatorString to set
     */
    public void setOperatorString(String operatorString) {
        this.operatorString = operatorString;
    }

    /**
     * @return integer value, that represents no. of parantheses sorrounding this connector.
     * @see edu.wustl.common.querysuite.queryobject.ILogicalConnector#getNestingNumber()
     * 
     * @hibernate.property name="nestingNumber" column="NESTING_NUMBER" type="integer" update="true" insert="true"
     */
    public int getNestingNumber() {
        return nestingNumber;
    }

    /**
     * To set the nesting numbet for this Logical connector.
     * @param nestingNumber the nesting number.
     */
    public void setNestingNumber(int nestingNumber) {
        this.nestingNumber = nestingNumber;

    }

    /**
     * to increment nesting numeber by 1.
     *
     */
    public void incrementNestingNumber() {
        nestingNumber++;
    }

    /**
     * 
     * @return hash code value for this object.
     * It uses logicalOperator & nestingNumber for the hash code calculation.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;

        if (logicalOperator != null) {
            hash = hash * Constants.HASH_PRIME + logicalOperator.hashCode();
        }
        hash = hash * Constants.HASH_PRIME + nestingNumber;

        return hash;
    }

    /**
     * @param obj The reference of object to be compared.
     * @return true if object specified is of same class, and locial operator & nesting number of two classes are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;

        if (this == obj) {
            isEqual = true;
        }

        if (!isEqual && (obj != null && this.getClass() == obj.getClass())) {
            LogicalConnector logicalConnector = (LogicalConnector) obj;
            if (this.logicalOperator != null && this.logicalOperator.equals(logicalConnector.logicalOperator)
                    && this.nestingNumber == logicalConnector.nestingNumber) {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     * @return String representation for this object as Logicaloperator:nesting number.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + logicalOperator + ":" + nestingNumber + "]";
    }

}