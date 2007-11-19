/**
 * 
 */
package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.util.global.Constants;

/**
 * 
 * @author chetan_patil
 * @created Aug 10, 2007, 5:27:53 PM
 * 
 * @hibernate.class table="QUERY_GRAPH_ENTRY"
 * @hibernate.cache usage="read-write"
 */
public class GraphEntry extends BaseQueryObject {
    private static final long serialVersionUID = 1L;

    private IExpressionId sourceExpressionId;

    private IExpressionId targetExpressionId;

    private IAssociation association;

    /**
     * Default Constructor
     */
    public GraphEntry() {

    }

    /**
     * Parameterized Constructor
     * @param sourceExpressionId
     * @param targetExpressionId
     * @param association
     */
    public GraphEntry(IExpressionId sourceExpressionId, IExpressionId targetExpressionId, IAssociation association) {
        super();
        this.sourceExpressionId = sourceExpressionId;
        this.targetExpressionId = targetExpressionId;
        this.association = association;
    }

    /**
     * Returns the identifier assigned to BaseQueryObject.
     * @return a unique id assigned to the Condition.
     * 
     * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30" unsaved-value="null" generator-class="native"
     * @hibernate.generator-param name="sequence" value="GRAPH_ENTRY_SEQ"
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the association
     * 
     * @hibernate.many-to-one column="QUERY_MODEL_ASSOCIATION_ID" class="edu.wustl.common.querysuite.metadata.associations.impl.ModelAssociation" cascade="save-update" lazy="false"
     */
    public IAssociation getAssociation() {
        return association;
    }

    /**
     * @param association the association to set
     */
    public void setAssociation(IAssociation association) {
        this.association = association;
    }

    /**
     * @return the sourceExpressionId
     * 
     * @hibernate.many-to-one column="SOURCE_EXPRESSIONID_ID" class="edu.wustl.common.querysuite.queryobject.impl.ExpressionId" cascade="all" lazy="false"
     */
    public IExpressionId getSourceExpressionId() {
        return sourceExpressionId;
    }

    /**
     * @param sourceExpressionId the sourceExpressionId to set
     */
    public void setSourceExpressionId(IExpressionId sourceExpressionId) {
        this.sourceExpressionId = sourceExpressionId;
    }

    /**
     * @return the targetExpressionId
     * 
     * @hibernate.many-to-one column="TARGET_EXPRESSIONID_ID" class="edu.wustl.common.querysuite.queryobject.impl.ExpressionId" cascade="all" lazy="false"
     */
    public IExpressionId getTargetExpressionId() {
        return targetExpressionId;
    }

    /**
     * @param targetExpressionId the targetExpressionId to set
     */
    public void setTargetExpressionId(IExpressionId targetExpressionId) {
        this.targetExpressionId = targetExpressionId;
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
            GraphEntry graphEntry = (GraphEntry) object;
            IExpressionId sourceExpressionId = graphEntry.getSourceExpressionId();
            IExpressionId targetExpressionId = graphEntry.getTargetExpressionId();
            IAssociation association = graphEntry.getAssociation();
            if (this.getSourceExpressionId().equals(sourceExpressionId)
                    && this.getTargetExpressionId().equals(targetExpressionId)
                    && this.getAssociation().equals(association)) {
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
        if (sourceExpressionId != null && targetExpressionId != null && association != null) {
            hash *= Constants.HASH_PRIME + sourceExpressionId.hashCode();
            hash *= Constants.HASH_PRIME + targetExpressionId.hashCode();
            hash *= Constants.HASH_PRIME + association.hashCode();
        }
        return hash;
    }

}
