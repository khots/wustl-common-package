/**
 * 
 */
package edu.wustl.common.querysuite.queryobject.util;

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
    public static Map<IExpressionId, Map<AttributeInterface, ICondition>> getSelectedConditions(IQuery query) {
        HashMap<IExpressionId, Map<AttributeInterface, ICondition>> expressionIdConditionCollectionMap = null;
        if (query != null) {
            expressionIdConditionCollectionMap = new HashMap<IExpressionId, Map<AttributeInterface, ICondition>>();

            IConstraints constraints = query.getConstraints();
            Enumeration<IExpressionId> expressionIds = constraints.getExpressionIds();
            while (expressionIds.hasMoreElements()) {
                IExpressionId expressionId = expressionIds.nextElement();
                IExpression expression = constraints.getExpression(expressionId);

                for (int index = 0; index < expression.numberOfOperands(); index++) {
                    IExpressionOperand expressionOperand = expression.getOperand(index);
                    if (!expressionOperand.isSubExpressionOperand()) {
                        IRule rule = (IRule) expressionOperand;

                        Map<AttributeInterface, ICondition> attributeConditionMap = new HashMap<AttributeInterface, ICondition>();
                        Collection<ICondition> conditionList = rule.getConditions();
                        for (ICondition condition : conditionList) {
                            attributeConditionMap.put(condition.getAttribute(), condition);
                        }

                        expressionIdConditionCollectionMap.put(expression.getExpressionId(), attributeConditionMap);
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
                Collection<AttributeInterface> attributeCollection = deEntity.getAttributeCollection();

                expressionIdAttributeCollectionMap.put(expression.getExpressionId(), attributeCollection);
            }
        }

        return expressionIdAttributeCollectionMap;
    }

}
