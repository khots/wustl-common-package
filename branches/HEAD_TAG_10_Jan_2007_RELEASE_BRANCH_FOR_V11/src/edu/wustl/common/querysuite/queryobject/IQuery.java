package edu.wustl.common.querysuite.queryobject;

/**
 * The query object... this is the unit built from UI, persisted, and from which
 * queries will be built.
 * @version 1.0
 * @updated 11-Oct-2006 02:57:13 PM
 */
public interface IQuery extends IBaseQueryObject {
    /**
     * @return the reference to constraints 
     * @see IConstraints
     */
    IConstraints getConstraints();

    /**
     * To set the constraints object.
     * @param constraints the constraints to set.
     */
    void setConstraints(IConstraints constraints);

    /**
     * This method returns the user defined name of the query. 
     * @return name of the query
     * 
     * @hibernate.property name="queryName" column="QUERY_NAME" type="stirng" length="256" unique="true" 
     */
    public String getQueryName();

    /**
     * This method sets the name of the query. This name must be unique as it will be used as an distinguisher by the user. 
     * @param queryName the user defined name of the query
     */
    public void setQueryName(String queryName);

    /**
     * @return the description
     */
    public String getDescription();

    /**
     * @param description the description to set
     */
    public void setDescription(String description);
}
