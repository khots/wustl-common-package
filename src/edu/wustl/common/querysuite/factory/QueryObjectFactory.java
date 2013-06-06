/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.factory;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.client.ui.util.ClientPropertyLoader;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.IntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraintEntity;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTreeNode;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Condition;
import edu.wustl.common.querysuite.queryobject.impl.ConstraintEntity;
import edu.wustl.common.querysuite.queryobject.impl.Constraints;
import edu.wustl.common.querysuite.queryobject.impl.ExpressionId;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;
import edu.wustl.common.querysuite.queryobject.impl.OutputEntity;
import edu.wustl.common.querysuite.queryobject.impl.OutputTreeNode;
import edu.wustl.common.querysuite.queryobject.impl.Query;
import edu.wustl.common.querysuite.queryobject.impl.Rule;

/**
 * factory to create the query objects, query engine etc...
 * @version 1.0
 * @updated 11-Oct-2006 02:57:23 PM
 */
public abstract class QueryObjectFactory
{

	/**
	 * To instanciate Logical connector.
	 * @param logicalOperator The Logical operator that connector will hold.  
	 * @return The instance of LogicalConnector class.
	 */
	public static ILogicalConnector createLogicalConnector(LogicalOperator logicalOperator)
	{
		return new LogicalConnector(logicalOperator);
	}

	/**
	 * 
	 * To instanciate Logical connector.
	 * @param logicalOperator The Logical operator that connector will hold.  
	 * @param nestingNumber The nesting number, that represents no. of parantheses sorrounding this connector.
	 * @return The instance of LogicalConnector class.
	 */
	public static ILogicalConnector createLogicalConnector(LogicalOperator logicalOperator,
			int nestingNumber)
	{
		return new LogicalConnector(logicalOperator, nestingNumber);
	}

	/**
	 * To instanciate Condition object.
	 * @param attribute The reference to Dynamic Extension attribute on which condition to be created.
	 * @param relationalOperator The relational operator between attribute & values.
	 * @param values The List of String representing values of the condition.
	 * @return The instance of the Condition class.
	 */
	public static ICondition createCondition(AttributeInterface attribute,
			RelationalOperator relationalOperator, List<String> values)
	{
		return new Condition(attribute, relationalOperator, values);
	}

	/**
	 * To create an empty Condition.
	 * @return The instance of the Condition class.
	 */
	public static ICondition createCondition()
	{
		return new Condition(null, null, null);
	}

	//	public static IExpression createExpression(IFunctionalClass outputEntity,
	//			List<IExpressionOperand> expressionOperands, List<ILogicalConnector> logicalConnectors,
	//			IExpressionId expressionId)
	//	{
	//		return new Expression(outputEntity, expressionOperands, logicalConnectors, expressionId);
	//	}
	//
	//	public static IExpression createExpression(IFunctionalClass outputEntity)
	//	{
	//		return new Expression(outputEntity, null, null, null);
	//	}

	/**
	 * To instanciate Expression Id object.
	 * @param id The id to set.
	 * @return The reference to the ExpressionId object.
	 */
	public static IExpressionId createExpressionId(int id)
	{
		return new ExpressionId(id);
	}

	//	public static IExpressionList createExpressionList(List<IExpression> expressions)
	//	{
	////		return new ExpressionList(expressions);
	//        return null;
	//	}

	/**
	 * To instanciate Constraints class object.
	 * @return The object of Constraints Class.
	 */
	public static IConstraints createConstraints()
	{
		return new Constraints();
	}

	/**
	 * To instanciate object of Rule class, with the given condition list.
	 * @param conditions The list of Conditions to set.
	 * @return The object of class Rule.
	 */
	public static IRule createRule(List<ICondition> conditions)
	{
		return new Rule(conditions);
	}

	/**
	 * To instanciate object of Rule class, with no conditions. 
	 * @return The object of class Rule.
	 */
	public static IRule createRule()
	{
		return new Rule(new ArrayList<ICondition>());
	}

	/**
	 * To instanciate object of IntraModelAssociation class.
	 * @param association The reference to the dynamic Extension associated with this object.
	 * @return The object of class IntraModelAssociation.
	 */
	public static IIntraModelAssociation createIntraModelAssociation(
			AssociationInterface association)
	{
		return new IntraModelAssociation(association);
	}

	/**
	 * To create empty Query object.
	 * @return the reference to the Query object.
	 */
	public static IQuery createQuery()
	{
		return new Query();
	}

	/**
	 * Method to instantiate object of a class implementing IOutputTreeNode interface.
	 * This method will be called only once to instanciate the Root node object. Further to instantiate child objects call addChild method present in IOutputTreeNode.
	 * @param outputEntity The reference to output Entity, that this tree node will represent.
	 * @return The reference to OutputTreeNode treenode object.
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#addChild(edu.wustl.common.querysuite.queryobject.IAssociation, edu.wustl.common.querysuite.queryobject.IOutputEntity)
	 */
	public static IOutputTreeNode createOutputTreeNode(IOutputEntity outputEntity)
	{
		return new OutputTreeNode(outputEntity);
	}

	/**
	 * To instanciate object of class implementing IConstraintEntity interface.
	 * @param entityInterface The Dynamic Extension entity reference associated with this object. 
	 * @return The reference to the ConstraintEntity object.
	 * @deprecated Do not use this method, use method createConstraintEntity(EntityInterface)
	 */
	public static IConstraintEntity createConstrainedEntity(EntityInterface entityInterface)
	{
		return new ConstraintEntity(entityInterface);
	}

	/**
	 * To instanciate object of class implementing  IOutputEntity interface.
	 * @param entityInterface The Dynamic Extension entity reference associated with this object.
	 * @return The reference to the OutputEntity object.
	 */
	public static IOutputEntity createOutputEntity(EntityInterface entityInterface)
	{
		return new OutputEntity(entityInterface);
	}

	/**
	 * To instanciate object of class implementing  IConstraintEntity interface.
	 * @param entityInterface The Dynamic Extension entity reference associated with this object.
	 * @return The reference to the ConstraintEntity object.
	 */
	public static IConstraintEntity createConstraintEntity(EntityInterface entityInterface)
	{
		return new ConstraintEntity(entityInterface);
	}

	/**
	 * This returns an instance of the path finder class mentioned in Client.properties
	 * @return IPathFinder 
	 */
	public static IPathFinder getPathFinder()
	{
		String pathFinderClassName = ClientPropertyLoader.getPathFinderClassName();
		IPathFinder pathFinder = (IPathFinder) CommonObjectFactory.getInstance().getObjectofClass(
				pathFinderClassName);
		return pathFinder;
	}
}