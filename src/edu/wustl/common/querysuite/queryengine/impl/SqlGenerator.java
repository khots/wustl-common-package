
package edu.wustl.common.querysuite.queryengine.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.StringTypeInformationInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryengine.ISqlGenerator;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;

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

	/**
	 * Default Constructor to instantiate SQL generator object.
	 *
	 */
	public SqlGenerator()
	{
	}

	/**
	 * Generates SQL for the given Query Object.
	 * @param query The Reference to Query Object.
	 * @return the String representing SQL for the given Query object.
	 * @throws MultipleRootsException When there are multpile roots present in a graph.
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.  
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 * @see edu.wustl.common.querysuite.queryengine.ISqlGenerator#generateSQL(edu.wustl.common.querysuite.queryobject.IQuery)
	 */
	public String generateSQL(IQuery query) throws MultipleRootsException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		return buildQuery(query);
	}

	/**
	 * To initialize map the variables. & build the SQL for the Given Query Object.  
	 * @param query the IQuery reference.
	 * @return The Root Expetssion of the IQuery. 
	 * @throws MultipleRootsException When there exists multiple roots in joingraph.
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 */
	String buildQuery(IQuery query) throws MultipleRootsException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		constraints = query.getConstraints();
		this.joinGraph = (JoinGraph) constraints.getJoinGraph();
		IExpression rootExpression = constraints.getExpression(constraints.getRootExpressionId());

		// Initializin map variables.
		aliasAppenderMap = new HashMap<IExpressionId, Integer>();
		aliasNameMap = new HashMap<String, String>();
		createAliasAppenderMap(rootExpression, 1, new Integer(1),
				new HashMap<List<IAssociation>, IExpressionId>());

		//Creating SQL.
		String wherePart = "Where " + getWherePartSQL(rootExpression, null, false);
		String fromPart = getFromPartSQL(rootExpression, null, new HashSet<Integer>());
		String selectPart = getSelectPart(rootExpression);
		String sql = selectPart + " " + fromPart + " " + wherePart;
		return sql;
	}

	/**
	 * To get the Select clause of the Query.
	 * @param expression The Expression which will appear in the Select part.
	 * @return the Select clause of the Query.
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 */
	String getSelectPart(IExpression expression) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		StringBuffer buffer = new StringBuffer("Select ");
		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		String aliasName = getAliasName(expression);
		Iterator attributeCollectionItr = entity.getAbstractAttributeCollection().iterator();
		while (attributeCollectionItr.hasNext())
		{
			Attribute attribute = (Attribute) attributeCollectionItr.next();
			buffer.append(aliasName + "." + attribute.getColumnProperties().getName());
			if (attributeCollectionItr.hasNext())
			{
				buffer.append(", ");
			}
		}
		return buffer.toString();
	}

	/**
	 *  To get the From clause of the Query.
	 * @param expression The Root Expression.
	 * @param leftAlias the String representing alias of left table. This will be alias of table represented by Parent Expression. Will be null for the Root Expression.  
	 * @param processedAlias The set of aliases processed.
	 * @return the From clause of the SQL.
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 */
	String getFromPartSQL(IExpression expression, String leftAlias, Set<Integer> processedAlias)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
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
			for (int index = 0; index < children.size(); index++)
			{
				IExpressionId childExpressionId = children.get(index);
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

					String leftAttribute = leftAlias + "."
							+ eavAssociation.getConstraintProperties().getSourceEntityKey();
					String rightAttribute = rightAlias + "."
							+ eavAssociation.getConstraintProperties().getTargetEntityKey();

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
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 */
	String getWherePartSQL(IExpression expression, IExpression parentExpression, boolean isPAND)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
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
				String selectAttribute = leftAlias + "."
						+ eavAssociation.getConstraintProperties().getTargetEntityKey();

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
					String joinAttribute = getAliasName(expression) + "."
							+ eavAssociation.getConstraintProperties().getSourceEntityKey();
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

	//	/**
	//	 * To get the Association from Dynamic Extentsion for the given Assoctaion.
	//	 * @param association The reference to association
	//	 * @return the reference of Association from Dynamic Extentsion 
	//	 * @throws DynamicExtensionsSystemException
	//	 * @throws DynamicExtensionsApplicationException
	//	 */
	//	private AssociationInterface getAssoication(IIntraModelAssociation association)
	//			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	//	{
	//		if (associationMap.containsKey(association))
	//			return associationMap.get(association);
	//
	//		Collection<AssociationInterface> associations =entityManager.getAssociation(association
	//				.getSourceClass().getFullyQualifiedName(), association.getSourceRoleName());
	//		for (Iterator<AssociationInterface> iter = associations.iterator(); iter.hasNext();)
	//		{
	//			AssociationInterface theAssociation = iter.next();
	//			if (theAssociation.getTargetEntity().getName().equals(association.getTargetClass().getFullyQualifiedName()))
	//			{
	//				associationMap.put(association, theAssociation);
	//				return theAssociation;
	//			}
	//		}
	//		
	//		return null;
	//	}

	/**
	 * To get the SQL representation of the Rule.
	 * @param rule The reference to Rule.
	 * @return The SQL representation of the Rule.
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 */
	String getSQL(IRule rule) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
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
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 */
	String getSQL(ICondition condition, IExpression expression)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		StringBuffer buffer = new StringBuffer("");
		AttributeInterface attribute = condition.getAttribute();
		AttributeTypeInformationInterface dataType = attribute.getAttributeTypeInformation();
		String attributeName = getSQL(attribute, expression);

		RelationalOperator operator = condition.getRelationalOperator();
		String strOperator = RelationalOperator.getSQL(operator);

		if (operator.equals(RelationalOperator.Between))//Processing Between Operator, it will be treated as (op>=val1 and op<=val2)
		{
			List<String> values = condition.getValues();
			String firstValue = modifyValueforDataType(values.get(0), dataType);
			String secondValue = modifyValueforDataType(values.get(1), dataType);

			buffer.append("(" + attributeName);
			buffer.append(RelationalOperator.getSQL(RelationalOperator.LessThanOrEquals)
					+ firstValue);
			buffer.append(" " + LogicalOperator.And + " " + attributeName
					+ RelationalOperator.getSQL(RelationalOperator.GreaterThanOrEquals)
					+ secondValue + ")");
		}
		else if (operator.equals(RelationalOperator.In)) // Processing In Operator
		{
			buffer.append(attributeName + " " + strOperator + " (");
			List<String> valueList = condition.getValues();
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
		}
		else if (operator.equals(RelationalOperator.IsNotNull)
				|| operator.equals(RelationalOperator.IsNull)) // Processing isNull & isNotNull operator.
		{
			buffer.append(attributeName + " " + strOperator);
		}
		else if (operator.equals(RelationalOperator.Contains)
				|| operator.equals(RelationalOperator.StartsWith)
				|| operator.equals(RelationalOperator.EndsWith)) // Processing String related Operators.
		{
			String value = condition.getValue();
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
			buffer.append(attributeName + " like " + value);
		}
		else
		// Processing rest operators like =, !=, <, > , <=, >= etc.
		{
			String value = condition.getValue();
			value = modifyValueforDataType(value, dataType);
			buffer.append(attributeName + strOperator + value);
		}

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
	 */
	String modifyValueforDataType(String value, AttributeTypeInformationInterface dataType)
	{

		if (dataType instanceof StringTypeInformationInterface)//for data type String it will be enclosed in single quote.
		{
			value = value.replaceAll("'", "''");
			value = "'" + value + "'";
		}
		else if(dataType instanceof DateTypeInformationInterface) // for data type date it will be enclosed in single quote.
		{
			value = "'" + value + "'";
		}
		else if (dataType instanceof BooleanTypeInformationInterface) // defining value for boolean datatype.
		{
			if (value != null && value.toUpperCase().equals("TRUE"))
			{
				value = "1";
			}
			else
			{
				value = "0";
			}
		}
		return value;
	}

	/**
	 * Get the SQL representatio for Attribute.
	 * @param attribute The reference to AttributeInterface
	 * @param expression The reference to Expression to which this attribute belongs.
	 * @return The SQL representatio for Attribute.
	 * @throws DynamicExtensionsSystemException when DB connection, Hibernate related problem occures.  
	 * @throws DynamicExtensionsApplicationException when Dynamic Extension data validation fails.
	 */
	String getSQL(AttributeInterface attribute, IExpression expression)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
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

	//	/**
	//	 * To get the Entity Class from Dynamic Extension for the given class. 
	//	 * It caches the Entity in the entityMap. First it checks whether the Entity is cached, if it is cached then it returns it from cache, else get it from Dynamic Extension. 
	//	 * @param iClass The IClass reference.
	//	 * @return The reference to Entity Class from Dynamic Extension for the given class.
	//	 * @throws DynamicExtensionsSystemException
	//	 * @throws DynamicExtensionsApplicationException
	//	 */
	//	private Entity getEntity(IClass iClass) throws DynamicExtensionsSystemException,
	//			DynamicExtensionsApplicationException
	//	{
	//		String fullyQualifiedName = iClass.getFullyQualifiedName();
	//		Entity entity = entityMap.get(fullyQualifiedName);
	//		if (entity == null)
	//		{
	//			entity = (Entity) entityManager.getEntityByName(fullyQualifiedName);
	//			entityMap.put(fullyQualifiedName, entity); // storing entity in the entity map.
	//		}
	//		return entity;
	//	}

	//	/**
	//	 * To get the Attribute from Dynamic Extenstion for the Given Attribute.
	//	 * @param attribute The IAttribute reference.
	//	 * @return The reference to Attribute from Dynamic Extenstion for the Given Attribute.
	//	 * @throws DynamicExtensionsSystemException
	//	 * @throws DynamicExtensionsApplicationException
	//	 */
	//	private Attribute getAttribute(IAttribute attribute) throws DynamicExtensionsSystemException,
	//			DynamicExtensionsApplicationException
	//	{
	//		Entity entity = getEntity(attribute.getUMLClass());
	//
	//		Attribute entityAttribute = (Attribute) entityManager.getAttribute(entity.getName(),
	//				attribute.getAttributeName());
	//
	//		return entityAttribute;
	//	}

	/**
	 * This method will be used by Query Mock to set the join Graph externally. 
	 * @param joinGraph the reference to joinGraph.
	 */
	void setJoinGraph(JoinGraph joinGraph)
	{
		this.joinGraph = joinGraph;
	}
}
