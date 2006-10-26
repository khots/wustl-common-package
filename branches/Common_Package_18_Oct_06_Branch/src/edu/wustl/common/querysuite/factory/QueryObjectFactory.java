
package edu.wustl.common.querysuite.factory;

import java.util.List;
import java.util.Map;

import edu.wustl.common.querysuite.category.ICategory;
import edu.wustl.common.querysuite.queryobject.DataType;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;
import edu.wustl.common.querysuite.queryobject.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Attribute;
import edu.wustl.common.querysuite.queryobject.impl.Condition;
import edu.wustl.common.querysuite.queryobject.impl.Edge;
import edu.wustl.common.querysuite.queryobject.impl.ExpressionId;
import edu.wustl.common.querysuite.queryobject.impl.FunctionalClass;
import edu.wustl.common.querysuite.queryobject.impl.IntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;
import edu.wustl.common.querysuite.queryobject.impl.Rule;

/**
 * factory to create the query objects, query engine etc...
 * @version 1.0
 * @updated 11-Oct-2006 02:57:23 PM
 */
public class QueryObjectFactory
{

	public QueryObjectFactory()
	{

	}

	public void finalize() throws Throwable
	{

	}

	public static ILogicalConnector createLogicalConnector(LogicalOperator logicalOperator,
			int nestingNumber)
	{
		return new LogicalConnector(logicalOperator, nestingNumber);
	}

	public static IAttribute createAttribute(DataType dataType, IClass umlCLass,
			String attributeName)
	{
		return new Attribute(dataType, umlCLass, attributeName);
	}

	public static IAttribute createAttribute()
	{
		return new Attribute(null, null, null);
	}

	public static IClass createClass(String fullQualifiedName, List<IAttribute> attributes,
			ICategory category, boolean isVisible)
	{
		return new edu.wustl.common.querysuite.queryobject.impl.Class(fullQualifiedName,
				attributes, category, isVisible);
	}

	public static ICondition createCondition(IAttribute attribute,
			RelationalOperator relationalOperator, List<String> values)
	{
		return new Condition(attribute, relationalOperator, values);
	}

	public static ICondition createCondition()
	{
		return new Condition(null, null, null);
	}

	//	public static IExpression createExpression(IFunctionalClass functionalClass,
	//			List<IExpressionOperand> expressionOperands, List<ILogicalConnector> logicalConnectors,
	//			IExpressionId expressionId)
	//	{
	//		return new Expression(functionalClass, expressionOperands, logicalConnectors, expressionId);
	//	}
	//
	//	public static IExpression createExpression(IFunctionalClass functionalClass)
	//	{
	//		return new Expression(functionalClass, null, null, null);
	//	}

	public static IExpressionId createExpressionId(int id)
	{
		return new ExpressionId(id);
	}

	//	public static IExpressionList createExpressionList(List<IExpression> expressions)
	//	{
	////		return new ExpressionList(expressions);
	//        return null;
	//	}

	public static IConstraints createConstraints()
	{
		// TODO
		return null;
	}

	public static IFunctionalClass createFunctionalClass()
	{
		return new FunctionalClass();
	}

	public static IFunctionalClass createFunctionalClass(List<IAttribute> attributes,
			ICategory category)
	{
		return new FunctionalClass(attributes, category);
	}

	public static IRule createRule(List<ICondition> conditions, IExpression containingExpression)
	{
		return new Rule(conditions, containingExpression);
	}

	public static IRule createRule()
	{
		return new Rule(null, null);
	}

	public static IJoinGraph createJoinGraph(List<IExpressionId> expressionIds,
			Map<IExpressionId, List<Edge>> incommingEdgeMap,
			Map<IExpressionId, List<Edge>> outgoingEdgeMap)
	{
		return new JoinGraph(expressionIds, incommingEdgeMap, outgoingEdgeMap);
	}

	public static IJoinGraph createJoinGraph()
	{
		return new JoinGraph(null, null, null);
	}

	public static IIntraModelAssociation createIntraModelAssociation(IClass leftClass,
			IClass rightClass, String roleName, String revereseRoleName, boolean bidirectional)
	{
		return new IntraModelAssociation(leftClass, rightClass, roleName, revereseRoleName,
				bidirectional);
	}
}