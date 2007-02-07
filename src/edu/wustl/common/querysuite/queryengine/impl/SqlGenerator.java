
package edu.wustl.common.querysuite.queryengine.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.IntegerTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.StringTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.wustl.common.querysuite.exceptions.DuplicateChildException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryengine.ISqlGenerator;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTreeNode;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;
import edu.wustl.common.querysuite.queryobject.util.QueryObjectProcessor;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.global.Variables;

/**
 * To generate SQL from the given Query Object.
 * @author prafull_kadam
 *
 */
public class SqlGenerator implements ISqlGenerator
{

	Map<IExpressionId, Integer> aliasAppenderMap = new HashMap<IExpressionId, Integer>();

	Map<String, String> aliasNameMap = new HashMap<String, String>();
	JoinGraph joinGraph;
	IConstraints constraints;
	Map<Long, Map<AttributeInterface, String>> columnMap;

	public static final String COLUMN_NAME = "Column";
	// This will store mapping of output Tre node with the expression Id.
	private Map<Long, IExpressionId> outPutNodeMap;

	/**
	 * Default Constructor to instantiate SQL generator object.
	 */
	public SqlGenerator()
	{
	}

	/**
	 * Generates SQL for the given Query Object.
	 * @param query The Reference to Query Object.
	 * @return the String representing SQL for the given Query object.
	 * @throws MultipleRootsException When there are multpile roots present in a graph.
	 * @throws SqlException When there is error in the passed IQuery object.
	 * @see edu.wustl.common.querysuite.queryengine.ISqlGenerator#generateSQL(edu.wustl.common.querysuite.queryobject.IQuery)
	 */
	public String generateSQL(IQuery query) throws MultipleRootsException, SqlException
	{
		return buildQuery(query);
	}

	/**
	 * To initialize map the variables. & build the SQL for the Given Query Object.  
	 * @param query the IQuery reference.
	 * @return The Root Expetssion of the IQuery. 
	 * @throws MultipleRootsException When there exists multiple roots in joingraph.
	 * @throws SqlException When there is error in the passed IQuery object.
	 */
	String buildQuery(IQuery query) throws MultipleRootsException, SqlException
	{
		IQuery queryClone = (IQuery) QueryObjectProcessor.getObjectCopy(query);
		//		IQuery queryClone = query;
		constraints = queryClone.getConstraints();
		QueryObjectProcessor.replaceMultipleParents(constraints);
		this.joinGraph = (JoinGraph) constraints.getJoinGraph();
		IExpression rootExpression = constraints.getExpression(constraints.getRootExpressionId());

		// Initializin map variables.
		aliasAppenderMap = new HashMap<IExpressionId, Integer>();
		aliasNameMap = new HashMap<String, String>();
		createAliasAppenderMap(rootExpression, 1, new Integer(1),
				new HashMap<List<IAssociation>, IExpressionId>());

		//Generating output tree.
		outPutNodeMap = new HashMap<Long, IExpressionId>();
		IOutputTreeNode root = createOuputTree();
		query.setRootOutputClass(root);

		columnMap = new HashMap<Long, Map<AttributeInterface, String>>();

		//Creating SQL.
		String wherePart = "Where " + getWherePartSQL(rootExpression, null, false);
		String fromPart = getFromPartSQL(rootExpression, null, new HashSet<Integer>());
		String selectPart = getSelectPart(root);
		String sql = selectPart + " " + fromPart + " " + wherePart;

		return sql;
	}

	/**
	 * This method will return map of DE attributes verses & their column names present in the select part of the SQL. 
	 * These DE attributes will be attributes of the each node present in the Output tree.
	 * @return map of DE attributes verses & their column names present in the select part of the SQL. null if no sql is generated.
	 * @see edu.wustl.common.querysuite.queryengine.ISqlGenerator#getColumnMap()
	 */
	public Map<Long, Map<AttributeInterface, String>> getColumnMap()
	{
		return columnMap;
	}

	/**
	 * This method will create output tree with the same heirarchy as that of Constraints & sets in the constraints.
	 * @return Root node of the output tree.
	 * @throws MultipleRootsException If there are multiplt roots persent in constraints.
	 */
	private IOutputTreeNode createOuputTree() throws MultipleRootsException
	{
		IExpressionId root = constraints.getRootExpressionId();
		IExpression expression = constraints.getExpression(root);

		EntityInterface rootEntity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		IOutputEntity rootOutputEntity = QueryObjectFactory.createOutputEntity(rootEntity);
		rootOutputEntity.getSelectedAttributes().addAll(rootEntity.getAttributeCollection());
		IOutputTreeNode rootNode = QueryObjectFactory.createOutputTreeNode(rootOutputEntity);
		outPutNodeMap.put(rootNode.getId(), root);
		copyChildren(expression, rootNode);
		return rootNode;
	}

	/**
	 * To create all simillar heirarchy of expressions under the given output tree node.
	 * @param parentExpression The reference to expression.
	 * @param parentNode the output tree node reference.
	 */
	private void copyChildren(IExpression parentExpression, IOutputTreeNode parentNode)
	{
		for (int index = 0; index < parentExpression.numberOfOperands(); index++)
		{
			IExpressionOperand operand = parentExpression.getOperand(index);
			if (operand.isSubExpressionOperand())
			{

				IExpressionId childExpressionId = (IExpressionId) operand;
				IExpression childExpression = constraints.getExpression(childExpressionId);
				EntityInterface entity = childExpression.getConstraintEntity()
						.getDynamicExtensionsEntity();
				IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(entity);
				outputEntity.getSelectedAttributes().addAll(entity.getAttributeCollection());
				IAssociation association = joinGraph.getAssociation(parentExpression
						.getExpressionId(), childExpressionId);

				List<IOutputTreeNode> childrenNodes = parentNode.getChildren();
				boolean nodeExist = false;
				for (IOutputTreeNode treeNode : childrenNodes)
				{
					if (treeNode.getOutputEntity().equals(outputEntity)
							&& treeNode.getAssociationWithParent().equals(association))
					{
						nodeExist = true;
						copyChildren(childExpression, treeNode);
					}
				}
				if (!nodeExist)
				{
					try
					{
						IOutputTreeNode childNode = parentNode.addChild(association, outputEntity);
						//						childNode.getOutputEntity().getSelectedAttributes().addAll(entity.getAttributeCollection());
						outPutNodeMap.put(childNode.getId(), childExpressionId);
						copyChildren(childExpression, childNode);
					}
					catch (DuplicateChildException e)
					{
						throw new RuntimeException("Unexpected error!!!", e);
					}
				}
			}
		}
	}

	/**
	 * To get the Select clause of the Query.
	 * @param rootOutputTreeNode The root of the output tree.
	 * @return the Select clause of the Query.
	 */
	String getSelectPart(IOutputTreeNode rootOutputTreeNode)
	{
		selectIndex = 0;
		String selectAttribute = "Select " + getSelectAttributes(rootOutputTreeNode);
		selectAttribute = selectAttribute.substring(0, selectAttribute.length() - 2); // remove last part " ,"
		return selectAttribute;

		//		StringBuffer buffer = new StringBuffer("Select ");
		//		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		//		String aliasName = getAliasName(expression);
		//		
		//		Iterator<AttributeInterface> attributeCollectionItr = entity.getAttributeCollection().iterator();
		//		while (attributeCollectionItr.hasNext())
		//		{
		//			AttributeInterface attribute = attributeCollectionItr.next();
		//			
		//			String columnName = attribute.getColumnProperties().getName();
		//			if (!columnName.startsWith("DE_")) // This check is temporary fix, should be removed when DE data is properly populated.
		//			{
		//				buffer.append(aliasName + "." + columnName);
		//				if (attributeCollectionItr.hasNext())
		//				{
		//					buffer.append(", ");
		//				}
		//			}
		//		}
		//		return buffer.toString();
	}

	private int selectIndex;

	/**
	 * It will return the select part attributes for this node along with its child nodes.
	 * @param treeNode the output tree node.
	 * @return  The select part attributes for this node along with its child nodes.
	 */
	private String getSelectAttributes(IOutputTreeNode treeNode)
	{
		StringBuffer selectPart = new StringBuffer("");
		IOutputEntity outputEntity = treeNode.getOutputEntity();

		IExpression expression = constraints.getExpression(outPutNodeMap.get(treeNode.getId()));
		String aliasName = getAliasName(expression);
		List<AttributeInterface> attributes = outputEntity.getSelectedAttributes();
		Map<AttributeInterface, String> entityColumnMap = new HashMap<AttributeInterface, String>();
		columnMap.put(treeNode.getId(), entityColumnMap);
		for (AttributeInterface attribute : attributes)
		{
			selectPart.append(aliasName + "." + attribute.getColumnProperties().getName());
			String columnAliasName = COLUMN_NAME + selectIndex;
			selectPart.append(" " + columnAliasName + " ,");
			entityColumnMap.put(attribute, columnAliasName);
			//			selectPart += aliasName + "."  +attribute.getColumnProperties().getName() + " " + COLUMN_NAME + selectIndex +" ,";
			selectIndex++;
		}
		List<IOutputTreeNode> children = treeNode.getChildren();
		for (IOutputTreeNode childTreeNode : children)
		{
			selectPart.append(getSelectAttributes(childTreeNode));
			//			selectPart+= getSelectAttributes(childTreeNode);
		}

		return selectPart.toString();
	}

	/**
	 *  To get the From clause of the Query.
	 * @param expression The Root Expression.
	 * @param leftAlias the String representing alias of left table. This will be alias of table represented by Parent Expression. Will be null for the Root Expression.  
	 * @param processedAlias The set of aliases processed.
	 * @return the From clause of the SQL.
	 * @throws SqlException When there is problem in creating from part. problem can be like: no primary key found in entity for join.
	 */
	String getFromPartSQL(IExpression expression, String leftAlias, Set<Integer> processedAlias)
			throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");
		IExpressionId parentExpressionId = expression.getExpressionId();

		if (processedAlias.isEmpty()) // this will be true only for root node.
		{
			EntityInterface leftEntity = expression.getConstraintEntity()
					.getDynamicExtensionsEntity();
			leftAlias = getAliasName(expression);
			buffer.append("From " + leftEntity.getTableProperties().getName() + " " + leftAlias);
		}

		processedAlias.add(aliasAppenderMap.get(parentExpressionId));

		List<IExpressionId> children = joinGraph.getChildrenList(parentExpressionId);
		if (!children.isEmpty())
		{
			// processing all outgoing edges/nodes from the current node in the joingraph.
			for (IExpressionId childExpressionId : children)
			{
				IExpression childExpression = constraints.getExpression(childExpressionId);
				if (!processedAlias.contains(aliasAppenderMap.get(childExpressionId)))
				{
					IAssociation association = joinGraph.getAssociation(parentExpressionId,
							childExpressionId);

					AssociationInterface eavAssociation = ((IIntraModelAssociation) association)
							.getDynamicExtensionsAssociation();

					EntityInterface rightEntity = childExpression.getConstraintEntity()
							.getDynamicExtensionsEntity();
					String rightAlias = getAliasName(childExpression);

					buffer.append(" left join " + rightEntity.getTableProperties().getName() + " "
							+ rightAlias + " on ");

					String leftAttribute = null;
					String rightAttribute = null;

					ConstraintPropertiesInterface constraintProperties = eavAssociation
							.getConstraintProperties();
					if (constraintProperties.getSourceEntityKey() != null
							&& constraintProperties.getTargetEntityKey() != null)
					{
						// Many to many case.
						//TODO handle it seperately
						throw new RuntimeException(
								"Many to many condition is not yet handled in sqlgenerator!!!");
					}
					else
					{
						if (constraintProperties.getSourceEntityKey() != null)
						{
							leftAttribute = leftAlias + "."
									+ constraintProperties.getSourceEntityKey();
							AttributeInterface primaryKey = getPrimaryKey(rightEntity);
							rightAttribute = rightAlias + "."
									+ primaryKey.getColumnProperties().getName();
						}
						else
						{
							AttributeInterface primaryKey = getPrimaryKey(rightEntity);
							leftAttribute = leftAlias + "."
									+ primaryKey.getColumnProperties().getName();
							rightAttribute = rightAlias + "."
									+ constraintProperties.getTargetEntityKey();
						}
					}

					buffer.append("(" + leftAttribute + "=" + rightAttribute + ")");

					// append from part SQL for the next Expressions.
					buffer.append(getFromPartSQL(childExpression, rightAlias, processedAlias));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * To compile the SQL & get the SQL representation of the Expression.
	 * @param expression the Expression whose SQL to be generated.
	 * @param parentExpression The Parent Expression.
	 * @param isPAND true if this Expression is psuedo anded with other Expression.
	 * @return The SQL representation of the Expression.
	 * @throws SqlException When there is error in the passed IQuery object.
	 */
	String getWherePartSQL(IExpression expression, IExpression parentExpression, boolean isPAND)
			throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");
		int prevNesting = 0;
		int openingBraces = 0; // holds number of opening Braces added to SQL.

		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();

		if (parentExpression != null)
		{
			IAssociation association = joinGraph.getAssociation(parentExpression.getExpressionId(),
					expression.getExpressionId());
			AssociationInterface eavAssociation = ((IIntraModelAssociation) association)
					.getDynamicExtensionsAssociation();
			if (isPAND) // Adding Pseudo and condition in the where part.
			{
				String tableName = entity.getTableProperties().getName() + " ";
				String leftAlias = getAliasName(expression);
				String selectAttribute = leftAlias + ".";

				if (eavAssociation.getConstraintProperties().getTargetEntityKey() == null)
				{
					selectAttribute += getPrimaryKey(entity).getColumnProperties().getName();
				}
				else
				{
					if (eavAssociation.getConstraintProperties().getSourceEntityKey() == null)
					{
						selectAttribute += eavAssociation.getConstraintProperties()
								.getTargetEntityKey();
					}
					else
					{
						// Many to many case.
						// TODO write logic for this.
						throw new RuntimeException(
								"Many to many condition is not yet handled in sqlgenerator!!!");
					}
				}
				buffer.append("Select " + selectAttribute);
				Set<Integer> processedAlias = new HashSet<Integer>();
				processedAlias.add(aliasAppenderMap.get(expression.getExpressionId()));
				String fromPart = getFromPartSQL(expression, leftAlias, processedAlias);
				buffer.append(" From " + tableName + " " + leftAlias + fromPart + " where ");
			}

		}

		int noOfRules = expression.numberOfOperands();
		for (int i = 0; i < noOfRules; i++)
		{
			IExpressionOperand operand = expression.getOperand(i);
			String ruleSQL = "";
			if (!operand.isSubExpressionOperand())
			{
				ruleSQL = getSQL((IRule) operand); // Processing Rule.
			}
			else
			//Processing sub Expression.
			{
				IExpression childExpression = constraints.getExpression((IExpressionId) operand);

				isPAND = ((Expression) expression).isPseudoAnded(childExpression.getExpressionId(),
						constraints);
				ruleSQL = getWherePartSQL(childExpression, expression, isPAND);
				if (isPAND)
				{
					IAssociation association = joinGraph.getAssociation(expression
							.getExpressionId(), childExpression.getExpressionId());
					AssociationInterface eavAssociation = ((IIntraModelAssociation) association)
							.getDynamicExtensionsAssociation();
					String joinAttribute = getAliasName(expression) + ".";

					if (eavAssociation.getConstraintProperties().getSourceEntityKey() == null)
					{
						joinAttribute += getPrimaryKey(
								childExpression.getConstraintEntity().getDynamicExtensionsEntity())
								.getColumnProperties().getName();
					}
					else
					{
						if (eavAssociation.getConstraintProperties().getTargetEntityKey() == null)
						{
							joinAttribute += eavAssociation.getConstraintProperties()
									.getSourceEntityKey();
						}
						else
						{
							// Many to Many case.
							//TODO
							throw new RuntimeException(
									"Many to many condition is not yet handled in sqlgenerator!!!");
						}
					}
					ruleSQL = joinAttribute + " = ANY(" + ruleSQL + ")";
				}

			}
			if (!ruleSQL.equals("") && noOfRules != 1)
			{
				ruleSQL = "(" + ruleSQL + ")"; // putting RuleSQL  in Braces so that it will not get mixed with other Rules.
			}
			if (!ruleSQL.trim().equals("") && i != noOfRules - 1) // puts opening or closing brace in SQL depending upon nesting number along with LogicalOperator.
			{
				LogicalConnector connector = (LogicalConnector) expression.getLogicalConnector(i,
						i + 1);
				int nestingNumber = connector.getNestingNumber();

				if (prevNesting < nestingNumber)
				{
					buffer.append("(" + ruleSQL + " " + connector.getLogicalOperator());
					openingBraces++;
				}
				else if (prevNesting > nestingNumber)
				{
					buffer.append(ruleSQL + ") " + connector.getLogicalOperator());
					openingBraces--;
				}
				else
				{
					buffer.append(ruleSQL + " " + connector.getLogicalOperator());
				}
				prevNesting = nestingNumber;
			}
			else
			{ // finishing SQL.
				buffer.append(ruleSQL);
				if (openingBraces != 0)
				{
					buffer.append(")");
				}
			}
		}
		String sql = buffer.toString();

		return sql;
	}

	/**
	 * To get the SQL representation of the Rule.
	 * @param rule The reference to Rule.
	 * @return The SQL representation of the Rule.
	 * @throws SqlException When there is error in the passed IQuery object.
	 */
	String getSQL(IRule rule) throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");
		int noOfConditions = rule.size();

		for (int i = 0; i < noOfConditions; i++) // Processing all conditions in Rule combining them with AND operator.
		{
			String condition = getSQL(rule.getCondition(i), rule.getContainingExpression());

			if (i != noOfConditions - 1) // Intermediate Condition.
			{
				buffer.append(condition + " " + LogicalOperator.And + " ");
			}
			else
			{
				// Last Condition, this will not followed by And logical operator.
				buffer.append(condition);
			}
		}
		return buffer.toString();
	}

	/**
	 * To get the SQL Representation of the Condition.
	 * @param condition The reference to condition.
	 * @param expression The reference to Expression to which this condition belongs.
	 * @return The SQL Representation of the Condition.
	 * @throws SqlException When there is error in the passed IQuery object.
	 */
	String getSQL(ICondition condition, IExpression expression) throws SqlException
	{
		String sql = null;
		AttributeInterface attribute = condition.getAttribute();
		String attributeName = getSQL(attribute, expression);

		RelationalOperator operator = condition.getRelationalOperator();

		if (operator.equals(RelationalOperator.Between))//Processing Between Operator, it will be treated as (op>=val1 and op<=val2)
		{
			sql = processBetweenOperator(condition, attributeName);
		}
		else if (operator.equals(RelationalOperator.In)
				|| operator.equals(RelationalOperator.NotIn)) // Processing In Operator
		{

			sql = processInOperator(condition, attributeName);
		}
		else if (operator.equals(RelationalOperator.IsNotNull)
				|| operator.equals(RelationalOperator.IsNull)) // Processing isNull & isNotNull operator.
		{

			sql = processNullCheckOperators(condition, attributeName);
		}
		else if (operator.equals(RelationalOperator.Contains)
				|| operator.equals(RelationalOperator.StartsWith)
				|| operator.equals(RelationalOperator.EndsWith)) // Processing String related Operators.
		{
			sql = processLikeOperators(condition, attributeName);
		}
		else
		// Processing rest operators like =, !=, <, > , <=, >= etc.
		{
			sql = processComparisionOperator(condition, attributeName);
		}

		return sql;
	}

	/**
	 * Processing operators like =, !=, <, > , <=, >= etc.
	 * @param condition the condition.
	 * @param attributeName  the Name of the attribute to returned in SQL. 
	 * @return SQL representation for given condition.
	 * @throws SqlException when:
	 * 		1. value list contains more/less than 1 value.
	 * 		2. other than = ,!= operator present for String data type.
	 */
	private String processComparisionOperator(ICondition condition, String attributeName)
			throws SqlException
	{
		AttributeTypeInformationInterface dataType = condition.getAttribute()
				.getAttributeTypeInformation();
		RelationalOperator operator = condition.getRelationalOperator();
		List<String> values = condition.getValues();
		if (values.size() != 1)
		{
			throw new SqlException("Incorrect number of values found for Operator '" + operator
					+ "' for condition:" + condition);
		}
		String value = values.get(0);
		if (dataType instanceof StringTypeInformationInterface)
		{
			if (!(operator.equals(RelationalOperator.Equals) || operator
					.equals(RelationalOperator.NotEquals)))
			{
				throw new SqlException(
						"Incorrect operator found for String datatype for condition:" + condition);
			}
		}

		if (dataType instanceof BooleanAttributeTypeInformation)
		{
			if (!(operator.equals(RelationalOperator.Equals) || operator
					.equals(RelationalOperator.NotEquals)))
			{
				throw new SqlException(
						"Incorrect operator found for Boolean datatype for condition:" + condition);
			}
		}

		value = modifyValueforDataType(value, dataType);
		String sql = attributeName + RelationalOperator.getSQL(operator) + value;
		return sql;
	}

	/**
	 * To process String operators. for Ex. starts with, contains etc.
	 * @param condition the condition.
	 * @param attributeName  the Name of the attribute to returned in SQL. 
	 * @return SQL representation for given condition.
	 * @throws SqlException when 
	 * 		1. The datatype of attribute is not String.
	 * 		2. The value list empty or more than 1 value.
	 */
	private String processLikeOperators(ICondition condition, String attributeName)
			throws SqlException
	{
		RelationalOperator operator = condition.getRelationalOperator();

		if (!(condition.getAttribute().getAttributeTypeInformation() instanceof StringTypeInformationInterface))
		{
			throw new SqlException("Incorrect data type found for Operator '" + operator
					+ "' for condition:" + condition);
		}

		List<String> values = condition.getValues();
		if (values.size() != 1)
		{
			throw new SqlException("Incorrect number of values found for Operator '" + operator
					+ "' for condition:" + condition);
		}
		String value = values.get(0);
		if (operator.equals(RelationalOperator.Contains))
		{
			value = "'%" + value + "%'";
		}
		else if (operator.equals(RelationalOperator.StartsWith))
		{
			value = "'" + value + "%'";
		}
		else if (operator.equals(RelationalOperator.EndsWith))
		{
			value = "'%" + value + "'";
		}

		return attributeName + " like " + value;
	}

	/**
	 * To process 'Is Null' & 'Is Not Null' operator.
	 * @param condition the condition.
	 * @param attributeName  the Name of the attribute to returned in SQL. 
	 * @return SQL representation for given condition.
	 * @throws SqlException when the value list is not empty.
	 */
	private String processNullCheckOperators(ICondition condition, String attributeName)
			throws SqlException
	{
		String operatorStr = RelationalOperator.getSQL(condition.getRelationalOperator());
		if (condition.getValues().size() > 0)
		{
			throw new SqlException("No value expected in value part for '" + operatorStr
					+ "' operator !!!");
		}

		return attributeName + " " + operatorStr;

	}

	/**
	 * To process 'In' & 'Not In' operator.
	 * @param condition the condition.
	 * @param attributeName  the Name of the attribute to returned in SQL. 
	 * @return SQL representation for given condition.
	 * @throws SqlException when the value list is empty or problem in parsing any of the value.
	 */
	private String processInOperator(ICondition condition, String attributeName)
			throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");
		buffer.append(attributeName + " "
				+ RelationalOperator.getSQL(condition.getRelationalOperator()) + " (");
		List<String> valueList = condition.getValues();
		AttributeTypeInformationInterface dataType = condition.getAttribute()
				.getAttributeTypeInformation();

		if (valueList.size() == 0)
		{
			throw new SqlException(
					"atleast one value required for 'In' operand list for condition:" + condition);
		}

		if (dataType instanceof BooleanAttributeTypeInformation)
		{
			throw new SqlException("Incorrect operator found for Boolean datatype for condition:"
					+ condition);
		}
		for (int i = 0; i < valueList.size(); i++)
		{

			String value = modifyValueforDataType(valueList.get(i), dataType);

			if (i == valueList.size() - 1)
			{
				buffer.append(value + ")");
			}
			else
			{
				buffer.append(value + ",");
			}
		}
		return buffer.toString();
	}

	/**
	 * To get the SQL for the given condition with Between operator. It will be treated as (op>=val1 and op<=val2)
	 * @param condition The condition.
	 * @param attributeName the Name of the attribute to returned in SQL.
	 * @return SQL representation for given condition.
	 * @throws SqlException when:
	 * 		1. value list does not have 2 values
	 * 		2. Datatype is not date
	 * 		3. problem in parsing date.
	 */
	private String processBetweenOperator(ICondition condition, String attributeName)
			throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");
		List<String> values = condition.getValues();
		if (values.size() != 2)
		{
			throw new SqlException("Incorrect number of operand for Between oparator in condition:"
					+ condition);
		}

		AttributeTypeInformationInterface dataType = condition.getAttribute()
				.getAttributeTypeInformation();
		if (!(dataType instanceof DateTypeInformationInterface))
		{
			throw new SqlException(
					"Incorrect Data type of operand for Between oparator in condition:" + condition);
		}

		String firstValue = modifyValueforDataType(values.get(0), dataType);
		String secondValue = modifyValueforDataType(values.get(1), dataType);

		buffer.append("(" + attributeName);
		buffer.append(RelationalOperator.getSQL(RelationalOperator.LessThanOrEquals) + firstValue);
		buffer.append(" " + LogicalOperator.And + " " + attributeName
				+ RelationalOperator.getSQL(RelationalOperator.GreaterThanOrEquals) + secondValue
				+ ")");

		return buffer.toString();
	}

	/**
	 * To Modify value as per the Data type.
	 * 1. In case of String datatype, replace occurence of single quote by singlequote twice. 
	 * 2. Enclose the Given values by single Quotes for String & Date Data type. 
	 * 3. For Boolean DataType it will change value to 1 if its TRUE, else 0.
	 * @param value the Modified value.
	 * @param dataType The DataType of the passed value.
	 * @return The String representing encoded value for the given value & datatype.
	 * @throws SqlException when there is problem with the values, for Ex. unable to parse date/integer/double etc.
	 */
	String modifyValueforDataType(String value, AttributeTypeInformationInterface dataType)
			throws SqlException
	{

		if (dataType instanceof StringTypeInformationInterface)//for data type String it will be enclosed in single quote.
		{
			value = value.replaceAll("'", "''");
			value = "'" + value + "'";
		}
		else if (dataType instanceof DateTypeInformationInterface) // for data type date it will be enclosed in single quote.
		{
			try
			{
				Date date = new Date();
				date = Utility.parseDate(value);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				value = (calendar.get(Calendar.MONTH) + 1) + "-"
						+ calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);

				String strToDateFunction = Variables.strTodateFunction;
				if (strToDateFunction == null || strToDateFunction.trim().equals(""))
				{
					strToDateFunction = "STR_TO_DATE"; // using MySQL function if the Value is not defined.
				}

				String datePattern = Variables.datePattern;
				if (datePattern == null || datePattern.trim().equals(""))
				{
					datePattern = "%m-%d-%Y"; // using MySQL function if the Value is not defined.
				}
				value = strToDateFunction + "('" + value + "','" + datePattern + "')";
			}
			catch (ParseException parseExp)
			{
				throw new SqlException(parseExp.getMessage(), parseExp);
			}
		}
		else if (dataType instanceof BooleanTypeInformationInterface) // defining value for boolean datatype.
		{
			if (value == null
					|| !(value.equalsIgnoreCase(Constants.TRUE) || value
							.equalsIgnoreCase(Constants.FALSE)))
			{
				throw new SqlException(
						"Incorrect value found in value part for boolean operator!!!");
			}
			if (value.equalsIgnoreCase(Constants.TRUE))
			{
				value = "1";
			}
			else
			{
				value = "0";
			}
		}
		else if (dataType instanceof IntegerTypeInformationInterface)
		{
			if (!new Validator().isNumeric(value))
			{
				throw new SqlException("Non numeric value found in value part!!!");
			}
		}
		else if (dataType instanceof DoubleTypeInformationInterface)
		{
			if (!new Validator().isDouble(value))
			{
				throw new SqlException("Non numeric value found in value part!!!");
			}
		}
		return value;
	}

	/**
	 * Get the SQL representatio for Attribute.
	 * @param attribute The reference to AttributeInterface
	 * @param expression The reference to Expression to which this attribute belongs.
	 * @return The SQL representatio for Attribute.
	 */
	String getSQL(AttributeInterface attribute, IExpression expression)
	{
		String tableName = getAliasName(expression);
		return tableName + "." + attribute.getColumnProperties().getName();
	}

	private static final int ALIAS_NAME_LENGTH = 25;

	/**
	 * To get the Alias Name for the given IExpression.
	 * @param expression The reference to IExpression.
	 * @return The Alias Name for the given Entity.
	 */
	String getAliasName(IExpression expression)
	{
		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		String className = entity.getName();

		String aliasName = aliasNameMap.get(className);

		if (aliasName == null)
		{
			aliasName = className.substring(className.lastIndexOf('.') + 1, className.length());
			if (aliasName.length() > ALIAS_NAME_LENGTH)
			{
				aliasName = aliasName.substring(0, ALIAS_NAME_LENGTH);
			}
			// get unique aliasName for the given class.
			int count = 1;
			String theAliasName = aliasName;
			Collection<String> allAssignedAliases = aliasNameMap.values();
			while (allAssignedAliases.contains(theAliasName))
			{
				theAliasName = aliasName + count++;
			}
			aliasName = theAliasName;
			aliasNameMap.put(className, aliasName);
		}
		Integer alias = aliasAppenderMap.get(expression.getExpressionId());
		if (alias == null)
		{
			alias = new Integer(0);
		}
		aliasName = aliasName + "_" + alias;

		return aliasName;
	}

	/**
	 * To assign alias to each tablename in the Expression. It will generate alias that will be assigned to each entity in Expression. 
	 * @param expression the Root Expression of the Query.
	 * @param currentAliasCount The count from which it will start to assign alias appender.
	 * @param aliasToSet The alias to set for the current expression.
	 * @param pathMap The map of path verses the ExpressionId. entry in this map means, for such path, there is already alias assigned to some Expression.
	 * @return The int representing the modified alias appender count that will be used for further processing.
	 * @throws MultipleRootsException if there are multpile roots present in join graph.
	 */
	int createAliasAppenderMap(IExpression expression, int currentAliasCount, Integer aliasToSet,
			Map<List<IAssociation>, IExpressionId> pathMap) throws MultipleRootsException
	{
		aliasAppenderMap.put(expression.getExpressionId(), aliasToSet);
		//processing all sub Expressions of this expression.
		for (int index = 0; index < expression.numberOfOperands(); index++)
		{
			IExpressionOperand operand = expression.getOperand(index);
			if (operand.isSubExpressionOperand())
			{
				IExpression childExpression = constraints.getExpression((IExpressionId) operand);

				List<IAssociation> path = joinGraph.getEdgePath(childExpression.getExpressionId());

				IExpressionId simillarPathExpressionId = pathMap.get(path);
				if (simillarPathExpressionId != null) // use already existing alias.
				{
					aliasToSet = aliasAppenderMap.get(simillarPathExpressionId);
				}
				else
				// define new alias.
				{

					pathMap.put(path, childExpression.getExpressionId());
					aliasToSet = new Integer(++currentAliasCount);// assigned alias to this class, hence increment currentAliasCount. 
				}
				currentAliasCount = createAliasAppenderMap(childExpression, currentAliasCount,
						aliasToSet, pathMap);
			}
		}
		return currentAliasCount;
	}

	/**
	 * @return the aliasAppenderMap
	 */
	Map<IExpressionId, Integer> getAliasMap()
	{
		return aliasAppenderMap;
	}

	/**
	 * This method will be used by Query Mock to set the join Graph externally. 
	 * @param joinGraph the reference to joinGraph.
	 */
	void setJoinGraph(JoinGraph joinGraph)
	{
		this.joinGraph = joinGraph;
	}

	/**
	 * To get the primary key attribute of the given entity.
	 * @param entity the DE entity.
	 * @return The Primary key attribute of the given entity.
	 * @throws SqlException If there is no such attribute present in the attribute list of the entity.
	 */
	private AttributeInterface getPrimaryKey(EntityInterface entity) throws SqlException
	{
		Collection<AttributeInterface> attributes = entity.getAttributeCollection();
		for (AttributeInterface attribute : attributes)
		{
			if (attribute.getIsPrimaryKey())
			{
				return attribute;
			}
		}
		throw new SqlException("No Primary key attribute found for Entity:" + entity.getName());
	}
}
