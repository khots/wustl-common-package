package edu.wustl.common.querysuite.queryobject.impl;

import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IOutputTreeNode;
import edu.wustl.common.querysuite.queryobject.IQuery;

/**
 * The IQuery implementation class.
 * @author Mandar Shidhore
 * @author chetan_patil
 * @version 1.0
 * @created 12-Oct-2006 15.07.04 AM
 * 
 * @hibernate.class table="QUERY"
 * @hibernate.cache usage="read-write"
 */
public class Query extends BaseQueryObject implements IQuery {
    private static final long serialVersionUID = -9105109010866749580L;

    private IConstraints constraints;

    private IOutputTreeNode root;

    private String queryName;

    private String description;

    /**
     * Default Constructor
     */
    public Query() {

    }

    /**
     * Returns the identifier assigned to BaseQueryObject.
     * @return a unique id assigned to the Condition.
     * 
     * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30" unsaved-value="null" generator-class="native"
     * @hibernate.generator-param name="sequence" value="CONSTRAINT_SEQ"
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the reference to constraints.
     * @see edu.wustl.common.querysuite.queryobject.IQuery#getConstraints()
     * 
     * @hibernate.many-to-one column="QUERY_CONSTRAINTS_ID" class="edu.wustl.common.querysuite.queryobject.impl.Constraints" unique="true" cascade="all" lazy="false"
     */
    public IConstraints getConstraints() {
        if (constraints == null) {
            constraints = new Constraints();
        }
        return constraints;
    }

    /**
     * @param constraints the constraints to set.
     * @see edu.wustl.common.querysuite.queryobject.IQuery#setConstraints(edu.wustl.common.querysuite.queryobject.IConstraints)
     */
    public void setConstraints(IConstraints constraints) {
        this.constraints = constraints;
    }

    /**
     * This method returns the user defined name of the query. 
     * @return name of the query
     * 
     * @hibernate.property name="queryName" column="QUERY_NAME" type="string" length="256" unique="true" 
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * This method sets the name of the query. This name must be unique as it will be used as an distinguisher by the user. 
     * @param queryName the user defined name of the query
     */
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /**
     * @return the description
     * 
     * @hibernate.property name="description" column="DESCRIPTION" type="string" length="1024"
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the reference to the root noe of the output tree.
     * @see edu.wustl.common.querysuite.queryobject.IQuery#getRootOutputClass()
     * @deprecated This method is not required any more for output tree.
     */
    public IOutputTreeNode getRootOutputClass() {
        return root;
    }

    /**
     * @param root The reference to the root noe of the output tree.
     * @see edu.wustl.common.querysuite.queryobject.IQuery#setRootOutputClass(edu.wustl.common.querysuite.queryobject.IOutputTreeNode)
     * @deprecated This method is not required any more for output tree.
     */
    public void setRootOutputClass(IOutputTreeNode root) {
        this.root = root;
    }

}
