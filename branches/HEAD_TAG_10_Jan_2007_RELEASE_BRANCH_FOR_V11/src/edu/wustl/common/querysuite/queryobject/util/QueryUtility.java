/**
 * 
 */
package edu.wustl.common.querysuite.queryobject.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IParameterizedCondition;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.IRule;

/**
 * @author chetan_patil
 * @created Sep 20, 2007, 12:07:59 PM
 */
public class QueryUtility {

    /**
     * This method returns all the selected Condition for a given query.  
     * @param query
     * @return Map of ExpressionId -> Collection of Condition
     */
    public static Map<IExpressionId, Collection<ICondition>> getAllSelectedConditions(IQuery query) {
        Map<IExpressionId, Collection<ICondition>> expressionIdConditionCollectionMap = null;
        if (query != null) {
            expressionIdConditionCollectionMap = new HashMap<IExpressionId, Collection<ICondition>>();

            IConstraints constraints = query.getConstraints();
            Enumeration<IExpressionId> expressionIds = constraints.getExpressionIds();
            while (expressionIds.hasMoreElements()) {
                IExpressionId expressionId = expressionIds.nextElement();

                IExpression expression = constraints.getExpression(expressionId);
                for (int index = 0; index < expression.numberOfOperands(); index++) {
                    IExpressionOperand expressionOperand = expression.getOperand(index);
                    if (!expressionOperand.isSubExpressionOperand()) {
                        IRule rule = (IRule) expressionOperand;
                        Collection<ICondition> conditionList = rule.getConditions();

                        expressionIdConditionCollectionMap.put(expression.getExpressionId(), conditionList);
                    }
                }
            }
        }

        return expressionIdConditionCollectionMap;
    }

    /**
     * This method returns all the selected Condition for a given query.  
     * @param query
     * @return Map of ExpressionId -> Collection of Condition
     */
    public static Map<IExpressionId, Collection<IParameterizedCondition>> getAllParameterizedConditions(
                                                                                                        IQuery query) {
        Map<IExpressionId, Collection<IParameterizedCondition>> expressionIdConditionCollectionMap = null;
        if (query != null) {
            expressionIdConditionCollectionMap = new HashMap<IExpressionId, Collection<IParameterizedCondition>>();

            IConstraints constraints = query.getConstraints();
            Enumeration<IExpressionId> expressionIds = constraints.getExpressionIds();
            while (expressionIds.hasMoreElements()) {
                IExpressionId expressionId = expressionIds.nextElement();

                IExpression expression = constraints.getExpression(expressionId);
                for (int index = 0; index < expression.numberOfOperands(); index++) {
                    IExpressionOperand expressionOperand = expression.getOperand(index);
                    if (!expressionOperand.isSubExpressionOperand()) {
                        IRule rule = (IRule) expressionOperand;
                        
                        Collection<IParameterizedCondition> parameterizedConditions = new ArrayList<IParameterizedCondition>();

                        Collection<ICondition> conditionList = rule.getConditions();
                        for (ICondition condition : conditionList) {
                            if (condition instanceof IParameterizedCondition) {
                                parameterizedConditions.add((IParameterizedCondition) condition);
                            }
                        }
                        if (!parameterizedConditions.isEmpty()) {
                            expressionIdConditionCollectionMap.put(expression.getExpressionId(),
                                                                   parameterizedConditions);
                        }
                    }
                }
            }
        }

        return expressionIdConditionCollectionMap;
    }

    /**
     * This method returns all the attributes of the expressions involved in a given query.
     * @param query
     * @return Map of ExpressionId -> Collection of Attribute
     */
    public static Map<IExpressionId, Collection<AttributeInterface>> getAllAttributes(IQuery query) {
        Map<IExpressionId, Collection<AttributeInterface>> expressionIdAttributeCollectionMap = null;
        if (query != null) { 
            expressionIdAttributeCollectionMap = new HashMap<IExpressionId, Collection<AttributeInterface>>();

            IConstraints constraints = query.getConstraints();
            Enumeration<IExpressionId> expressionIds = constraints.getExpressionIds();
            while (expressionIds.hasMoreElements()) {
                IExpressionId expressionId = expressionIds.nextElement();

                IExpression expression = constraints.getExpression(expressionId);
                IQueryEntity queryEntity = expression.getQueryEntity();
                EntityInterface deEntity = queryEntity.getDynamicExtensionsEntity();
                Collection<AttributeInterface> attributeCollection = deEntity.getEntityAttributesForQuery();
                expressionIdAttributeCollectionMap.put(expression.getExpressionId(), attributeCollection);
            }
        }

        return expressionIdAttributeCollectionMap;
    }

}
