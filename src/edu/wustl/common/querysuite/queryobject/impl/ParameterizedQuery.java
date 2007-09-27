/**
 * 
 */
package edu.wustl.common.querysuite.queryobject.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IParameterizedCondition;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;

/**
 * @author chetan_patil
 * @created Aug 31, 2007, 4:22:00 PM
 * 
 * @hibernate.joined-subclass table="QUERY_PARAMETERIZED_QUERY"
 * @hibernate.joined-subclass-key column="IDENTIFIER" 
 */
public class ParameterizedQuery extends Query implements IParameterizedQuery {
    private static final long serialVersionUID = 1L;

    private List<IParameterizedCondition> parameterizedConditionList = new ArrayList<IParameterizedCondition>();

    //private Map<IExpressionId, List<Long>> attributesInView = new HashMap<IExpressionId, List<Long>>();

    private String name;

    private String description;

    /**
     * Default Constructor
     */
    public ParameterizedQuery() {

    }

    /**
     * Parameterized Constructor. This constructor will be used by Hibernate internally.
     * @param id
     * @param name
     * @param description
     */
    public ParameterizedQuery(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Parameterized Constructor
     * @param query
     */
    public ParameterizedQuery(IQuery query) {
        this.setConstraints(query.getConstraints());
    }

    /**
     * Parameterized Constructor
     * @param name
     * @param description
     * @param query
     */
    public ParameterizedQuery(IQuery query, String name, String description) {
        this.setConstraints(query.getConstraints());
        this.name = name;
        this.description = description;
    }

    /**
     * @see edu.wustl.common.querysuite.queryobject.IParameterizedQuery#addParameterizedCondition(edu.wustl.common.querysuite.queryobject.IParameterizedCondition)
     */
    public void addParameterizedCondition(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @see edu.wustl.common.querysuite.queryobject.IParameterizedQuery#getParameterizedConditions()
     */
    public List<IParameterizedCondition> getParameterizedConditions() {
        return parameterizedConditionList;
    }

    public void setParameterizedConditions(List<IParameterizedCondition> parameterizedConditionList) {
        this.parameterizedConditionList = parameterizedConditionList;
    }

    /**
     * @see edu.wustl.common.querysuite.queryobject.IDescribable#getName()
     * 
     * @hibernate.property name="name" column="QUERY_NAME" type="string" length="255" unique="true" 
     */
    public String getName() {
        return name;
    }

    /**
     * @see edu.wustl.common.querysuite.queryobject.IDescribable#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see edu.wustl.common.querysuite.queryobject.IDescribable#getDescription()
     * 
     * @hibernate.property name="description" column="DESCRIPTION" type="string" length="1024"
     */
    public String getDescription() {
        return description;
    }

    /**
     * @see edu.wustl.common.querysuite.queryobject.IDescribable#setDescription()
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     */
    public void addParameterizedCondition(IParameterizedCondition parameterizedCondition) {
        if (parameterizedCondition != null && parameterizedConditionList != null) {
            parameterizedConditionList.add(parameterizedCondition);
        }
    }

    /**
     * 
     * @param parameterizedCondition
     * @return
     */
    public boolean removeParameterizedCondition(IParameterizedCondition parameterizedCondition) {
        boolean isRemoved = false;
        if (parameterizedConditionList.contains(parameterizedCondition)) {
            parameterizedConditionList.remove(parameterizedCondition);
            isRemoved = true;
        }
        return isRemoved;
    }

    /**
     * 
     * @param index
     * @return
     */
    public boolean removeParameterizedCondition(int index) {
        boolean isRemoved = false;
        if (parameterizedConditionList.get(index) != null) {
            parameterizedConditionList.remove(index);
            isRemoved = true;
        }
        return isRemoved;
    }

/*    *//**
     * @return the attributesInView
     *//*
    public Map<IExpressionId, List<Long>> getAttributesInView() {
        return attributesInView;
    }

    *//**
     * @param attributesInView the attributesInView to set
     * 
     * @hibernate.map name="attributesInView" cascade="all-delete-orphan" inverse="false" lazy="false"
     * @hibernate.collection-key column="PARAMETERIZED_QUERY_ID"
     * @hibernate.collection-index column="EXPRESSION_ID" type="edu.wustl.common.querysuite.queryobject.impl.ExpressionId"
     * @hibernate.collection-element column="ATTRIBUTE_ID" type="long" length="30"
     * @hibernate.cache usage="read-write"
     *//*
    public void setAttributesInView(Map<IExpressionId, List<Long>> attributesInView) {
        this.attributesInView = attributesInView;
    }

    public void addAttributeInView(IExpressionId expressionId, AttributeInterface attribute) {
        Set<IExpressionId> keySet = attributesInView.keySet();
        if (keySet.contains(expressionId)) {
            List<Long> attributeIdList = attributesInView.get(expressionId);
            attributeIdList.add(attribute.getId());
        } else {
            List<Long> attributeIdList = new ArrayList<Long>();
            attributeIdList.add(attribute.getId());

            attributesInView.put(expressionId, attributeIdList);
        }
    }

    public void removeAttributeFromView(IExpressionId expressionId, AttributeInterface attribute) {
        Set<IExpressionId> keySet = attributesInView.keySet();
        if (keySet.contains(expressionId)) {
            List<Long> attributeIdList = attributesInView.get(expressionId);
            if (attributeIdList.contains(attribute.getId())) {
                attributeIdList.remove(attribute.getId());
            }
        }
    }*/

}
