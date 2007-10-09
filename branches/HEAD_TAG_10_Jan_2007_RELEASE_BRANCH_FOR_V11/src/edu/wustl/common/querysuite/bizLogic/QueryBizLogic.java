/**
 * 
 */
package edu.wustl.common.querysuite.bizLogic;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.common.cache.AbstractEntityCache;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.dao.DAO;
import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.InterModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.IntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Condition;
import edu.wustl.common.querysuite.queryobject.impl.Constraints;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.GraphEntry;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;
import edu.wustl.common.querysuite.queryobject.impl.OutputAttribute;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.impl.QueryEntity;
import edu.wustl.common.querysuite.queryobject.impl.Rule;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;

/**
 * This class processes the Query object before persisting and after retreival.
 * @author chetan_patil
 * @created Aug 30, 2007, 11:31:45 AM
 * @param <Q>
 */
public class QueryBizLogic<Q extends IParameterizedQuery> extends DefaultBizLogic {

    /**
     * Default Constructor
     */
    public QueryBizLogic() {

    }

    /* (non-Javadoc)
     * @see edu.wustl.common.bizlogic.DefaultBizLogic#preInsert(java.lang.Object, edu.wustl.common.dao.DAO, edu.wustl.common.beans.SessionDataBean)
     */
    @Override
    protected void preInsert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException,
            UserNotAuthorizedException {
        Q query = (Q) obj;
        Constraints constraints = (Constraints) query.getConstraints();
        constraints.contemporizeExpressionListWithExpressions();

        Enumeration<IExpressionId> enumeration = constraints.getExpressionIds();
        while (enumeration.hasMoreElements()) {
            IExpressionId expressionId = enumeration.nextElement();
            Expression expression = (Expression) constraints.getExpression(expressionId);
            preProcessExpression(expression);
        }

        JoinGraph joinGraph = (JoinGraph) constraints.getJoinGraph();
        joinGraph.contemporizeGraphEntryCollectionWithJoinGraph();

        Collection<GraphEntry> graphEntryCollection = joinGraph.getGraphEntryCollection();
        for (GraphEntry graphEntry : graphEntryCollection) {
            IAssociation association = graphEntry.getAssociation();
            preProcessAssociation(association);
        }

        if (query instanceof ParameterizedQuery) {
            preProcessParameterizedQuery((ParameterizedQuery) query);
        }
    }

    /**
     * This method returns the name of the name of the class.
     * @return
     */
    protected String getQueryClassName() {
        return ParameterizedQuery.class.getName();
    }

    /**
     * This method persists the Query object
     * @param query
     * @throws RemoteException
     */
    public final void saveQuery(Q query) throws RemoteException {
        try {
            insert(query, Constants.HIBERNATE_DAO);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save object, Exception:" + e.getMessage());
        }
    }

    /* 
     * @see edu.wustl.common.bizlogic.DefaultBizLogic#retrieve(java.lang.String, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.Object[], java.lang.String)
     */
    @Override
    public List<Q> retrieve(String sourceObjectName, String[] selectColumnName, String[] whereColumnName,
                            String[] whereColumnCondition, Object[] whereColumnValue, String joinCondition)
            throws DAOException {
        List<Q> queryList = super.retrieve(sourceObjectName, selectColumnName, whereColumnName,
                                           whereColumnCondition, whereColumnValue, joinCondition);
        try {
            for (Q query : queryList) {
                postProcessQuery(query);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Unable to process object, Exception:" + exception.getMessage());
        }

        return queryList;
    }

    /**
     * This method retrieves the query object given its identifier.
     * @param categoryId Id of the category
     * @return The Category for given id.
     * @throws RemoteException EBJ specific Exception
     */
    public Q getQueryById(Long queryId) throws RemoteException {
        List<Q> queryList = null;
        try {
            queryList = retrieve(getQueryClassName(), "id", queryId);
        } catch (DAOException e) {
            throw new RuntimeException("Unable to retreive object, Exception:" + e.getMessage());
        }

        Q query = null;
        if (queryList != null && !queryList.isEmpty()) {
            if (queryList.size() > 1) {
                throw new RuntimeException("Problem in code; probably database schema");
            } else {
                query = (Q) queryList.get(0);
            }
        }

        return query;
    }

    /**
     * This method retrieves all the query objects in the system.
     * Returns all the categories available in the system.
     * @return List of all categories.
     */
    public List<Q> getAllQueries() throws RemoteException {
        List<Q> queryList = null;
        try {
            queryList = (List<Q>) retrieve(getQueryClassName());
        } catch (DAOException e) {
            throw new RuntimeException("Unable to retreive object, Exception:" + e.getMessage());
        }

        return queryList;
    }

    /**
     * This method processes the query object after retreival.
     * @param query
     */
    public void postProcessQuery(Q query) {
        Constraints constraints = (Constraints) query.getConstraints();
        constraints.contemporizeExpressionsWithExpressionList();

        Enumeration<IExpressionId> enumeration = constraints.getExpressionIds();
        while (enumeration.hasMoreElements()) {
            IExpressionId expressionId = enumeration.nextElement();
            Expression expression = (Expression) constraints.getExpression(expressionId);
            postProcessExpression(expression);
        }

        JoinGraph joinGraph = (JoinGraph) constraints.getJoinGraph();
        postProcessJoinGraph(joinGraph, constraints.getExpressionIds());
        if (query instanceof ParameterizedQuery) {
            postProcessParameterizedQuery((ParameterizedQuery) query);
        }
    }

    /**
     * This method processes the parameterized query object after retreival.
     * @param query
     */
    private void postProcessParameterizedQuery(ParameterizedQuery parameterizedQuery) {
        List<IOutputAttribute> outputAttributeList = parameterizedQuery.getOutputAttributeList();
        for (IOutputAttribute outputAttribute : outputAttributeList) {
            OutputAttribute opAttribute = (OutputAttribute) outputAttribute;
            AbstractEntityCache abstractEntityCache = EntityCache.getCache();

            Long attributeId = opAttribute.getAttributeId();
            AttributeInterface attribute = abstractEntityCache.getAttributeById(attributeId);
            opAttribute.setAttribute(attribute);
        }

    }

    /**
     * This method processes the expression object after retreival.
     * @param expression
     */
    private void postProcessExpression(Expression expression) {
        QueryEntity queryEntity = (QueryEntity) expression.getQueryEntity();
        postProcessQueryEntity(queryEntity);

        List<IExpressionOperand> expressionOperands = expression.getExpressionOperands();
        for (IExpressionOperand expressionOperand : expressionOperands) {
            if (!expressionOperand.isSubExpressionOperand()) {
                Rule rule = (Rule) expressionOperand;
                postProcessRule(rule);
            }
        }

        Collection<ILogicalConnector> logicalConnectorCollection = expression.getLogicalConnectors();
        for (ILogicalConnector logicalConnector : logicalConnectorCollection) {
            LogicalConnector logicalConnectorImpl = (LogicalConnector) logicalConnector;
            String operatorString = logicalConnectorImpl.getOperatorString();
            logicalConnectorImpl.setLogicalOperator(LogicalOperator.getLogicalOperator(operatorString));
        }
    }

    /**
     * This method processes the JoinGraph object after retreival.
     * @param joinGraph
     * @param enumeration
     */
    private void postProcessJoinGraph(JoinGraph joinGraph, Enumeration<IExpressionId> enumeration) {
        Collection<GraphEntry> graphEntryList = joinGraph.getGraphEntryCollection();

        if (graphEntryList.isEmpty()) {
            while (enumeration.hasMoreElements()) {
                IExpressionId expressionId = enumeration.nextElement();
                joinGraph.addIExpressionId(expressionId);
            }
        } else {
            for (GraphEntry graphEntry : graphEntryList) {
                IAssociation association = graphEntry.getAssociation();
                postProcessAssociation(association);

                IExpressionId sourceExpressionId = graphEntry.getSourceExpressionId();
                IExpressionId targetExpressionId = graphEntry.getTargetExpressionId();
                try {
                    joinGraph.putAssociation(association, sourceExpressionId, targetExpressionId);
                } catch (CyclicException e) {
                    throw new RuntimeException("Unable to Process object, Exception:" + e.getMessage());
                }
            }
        }
    }

    /**
     * This method processes the QueryEntity object after retreival.
     * @param queryEntity
     */
    private void postProcessQueryEntity(QueryEntity queryEntity) {
        Long entityId = queryEntity.getEntityId();
        AbstractEntityCache abstractEntityCache = EntityCache.getCache();
        EntityInterface entity = abstractEntityCache.getEntityById(entityId);
        queryEntity.setDynamicExtensionsEntity(entity);
    }

    /**
     *  This method processes the Rule object after retreival.
     * @param rule
     */
    private void postProcessRule(IRule rule) {
        List<ICondition> conditions = rule.getConditions();
        if (!conditions.isEmpty()) {
            AbstractEntityCache abstractEntityCache = EntityCache.getCache();

            for (ICondition condition : conditions) {
                Condition conditionImpl = (Condition) condition;

                Long attributeId = conditionImpl.getAttributeId();
                AttributeInterface attribute = abstractEntityCache.getAttributeById(attributeId);
                conditionImpl.setAttribute(attribute);

                String relationalOperatorString = conditionImpl.getRelationalOperatorString();
                if (relationalOperatorString != null) {
                    RelationalOperator relationalOperator = RelationalOperator.getOperatorForStringRepresentation(relationalOperatorString);
                    conditionImpl.setRelationalOperator(relationalOperator);
                }
            }
        }
    }

    /**
     *  This method processes the Association object after retrieval.
     * @param association
     */
    private void postProcessAssociation(IAssociation association) {
        AbstractEntityCache abstractEntityCache = EntityCache.getCache();

        if (association instanceof InterModelAssociation) {
            InterModelAssociation interModelAssociation = (InterModelAssociation) association;

            Long sourceAttributeId = interModelAssociation.getSourceAttributeId();
            AttributeInterface sourceAttribute = abstractEntityCache.getAttributeById(sourceAttributeId);
            interModelAssociation.setSourceAttribute(sourceAttribute);

            Long targetAttributeId = interModelAssociation.getTargetAttributeId();
            AttributeInterface targetAttribute = abstractEntityCache.getAttributeById(targetAttributeId);
            interModelAssociation.setTargetAttribute(targetAttribute);

        } else if (association instanceof IntraModelAssociation) {
            IntraModelAssociation intraModelAssociation = (IntraModelAssociation) association;

            Long deAssociationId = intraModelAssociation.getDynamicExtensionsAssociationId();
            AssociationInterface deAssociation = abstractEntityCache.getAssociationById(deAssociationId);
            intraModelAssociation.setDynamicExtensionsAssociation(deAssociation);
        }
    }

    /**
     * This method processes the parameterized query object before saving.
     * @param query
     */
    private void preProcessParameterizedQuery(ParameterizedQuery parameterizedQuery) {
        List<IOutputAttribute> outputAttributeList = parameterizedQuery.getOutputAttributeList();
        for (IOutputAttribute outputAttribute : outputAttributeList) {
            AttributeInterface attribute = outputAttribute.getAttribute();
            ((OutputAttribute) outputAttribute).setAttributeId(attribute.getId());
        }
    }

    /**
     * This method processes the Expression object before persisting it.
     * @param expression
     */
    private void preProcessExpression(Expression expression) {
        QueryEntity queryEntity = (QueryEntity) expression.getQueryEntity();
        preProcessQueryEntity(queryEntity);

        List<IExpressionOperand> expressionOperands = expression.getExpressionOperands();
        for (IExpressionOperand expressionOperand : expressionOperands) {
            if (!expressionOperand.isSubExpressionOperand()) {
                Rule rule = (Rule) expressionOperand;
                preProcessRule(rule);
            }
        }

        Collection<ILogicalConnector> logicalConnectorCollection = expression.getLogicalConnectors();
        for (ILogicalConnector logicalConnector : logicalConnectorCollection) {
            LogicalOperator logicalOperator = logicalConnector.getLogicalOperator();
            ((LogicalConnector) logicalConnector).setOperatorString(logicalOperator.getOperatorString());
        }
    }

    /**
     * This method processes the QueryEntity object before persisting it.
     * @param queryEntity
     */
    private void preProcessQueryEntity(QueryEntity queryEntity) {
        EntityInterface entity = queryEntity.getEntityInterface();
        queryEntity.setEntityId(entity.getId());
    }

    /**
     * This method processes the Rule object before persisting it.
     * @param rule
     */
    private void preProcessRule(IRule rule) {
        List<ICondition> conditions = rule.getConditions();
        if (!conditions.isEmpty()) {
            for (ICondition condition : conditions) {
                Condition conditionImpl = (Condition) condition;

                AttributeInterface attribute = condition.getAttribute();
                conditionImpl.setAttributeId(attribute.getId());

                RelationalOperator relationalOperator = conditionImpl.getRelationalOperator();
                conditionImpl.setRelationalOperatorString(relationalOperator.getStringRepresentation());
            }
        }
    }

    /**
     * This method processes the Association object before persisting it.
     * @param association
     */
    private void preProcessAssociation(IAssociation association) {
        if (association instanceof IntraModelAssociation) {
            IntraModelAssociation intraModelAssociation = (IntraModelAssociation) association;
            AssociationInterface dynamicExtensionsAssociation = intraModelAssociation.getDynamicExtensionsAssociation();
            intraModelAssociation.setDynamicExtensionsAssociationId(dynamicExtensionsAssociation.getId());
        } else if (association instanceof InterModelAssociation) {
            InterModelAssociation interModelAssociation = (InterModelAssociation) association;

            AttributeInterface sourceAttribute = interModelAssociation.getSourceAttribute();
            interModelAssociation.setSourceAttributeId(sourceAttribute.getId());

            AttributeInterface targetAttribute = interModelAssociation.getTargetAttribute();
            interModelAssociation.setTargetAttributeId(targetAttribute.getId());
        }
    }

}
