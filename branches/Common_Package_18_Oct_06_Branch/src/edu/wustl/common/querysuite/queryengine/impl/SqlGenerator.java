/**
 * 
 */

package edu.wustl.common.querysuite.queryengine.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryengine.ISqlGenerator;
import edu.wustl.common.querysuite.queryobject.DataType;
import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.util.global.Constants;

/**
 * To generate SQL from the given Query Object.
 * @author prafull_kadam
 *
 */
public class SqlGenerator implements ISqlGenerator
{

	private String tableSuffix = "0";

	private EntityManager entityManager;

	private Map<String, Entity> entityMap = new HashMap<String, Entity>();

	SqlGenerator(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	IQuery query;

	/**
	 * @see edu.wustl.common.querysuite.queryengine.ISqlGenerator#generateSQL(edu.wustl.common.querysuite.queryobject.IQuery)
	 */
	public String generateSQL(IQuery query) throws MultipleRootsException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		this.query = query;

		return null;
	}

	/**
	 * To get the SQL representation of the Expression.
	 * @param expression
	 * @return The SQL representation of the Expression.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws NoSuchElementException when The Class in an expression does not have an 'id' attribute.
	 */
	String getSQL(IExpression expression, IJoinGraph joinGraph, IExpression parentExpression)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		StringBuffer sql = new StringBuffer("");
		int noOfRules = expression.getSize();
		int prevNesting = 0;
		int openingBraces = 0; // holds number of opening Braces added to SQL.

		IClass iClass = getClass(expression);
		IAttribute attribute = null;

		Entity entity = getEntity(iClass);
		String tableName = entity.getTableProperties().getName() + " ";
		String selectAttribute = "";

		if (parentExpression == null)
		{
			attribute = getParentKeyAttrinbute(iClass);
			selectAttribute = getSQL(attribute);
		}
		else
		{
			IAssociation association = joinGraph.getAssociation(parentExpression.getExpressionId(),
					expression.getExpressionId());
			AssociationInterface eavAssociation = getAssoication((IIntraModelAssociation) association);
			selectAttribute = getAliasName(entity) + "."
					+ eavAssociation.getConstraintProperties().getTargetEntityKey();
		}

		sql.append("Select " + selectAttribute + " From " + tableName + getAliasName(entity)
				+ " where ");

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
				IExpression childExpression = (IExpression) operand;
				IAssociation association = joinGraph.getAssociation(expression.getExpressionId(),
						childExpression.getExpressionId());
				AssociationInterface eavAssociation = getAssoication((IIntraModelAssociation) association);
				String joinAttribute = getAliasName(entity) + "."
						+ eavAssociation.getConstraintProperties().getSourceEntityKey();

				ruleSQL = getSQL(childExpression, joinGraph, expression);

				ruleSQL = joinAttribute + " = ANY(" + ruleSQL + ")";
			}
			if (!ruleSQL.equals("") && noOfRules != 1)
			{
				ruleSQL = "(" + ruleSQL + ")"; // putting RuleSQL  in Braces so that it will not get mixed with other Rules.
			}
			if (i != noOfRules - 1) // puts opening or closing brace in SQL depending upon nesting number along with LogicalOperator.
			{
				ILogicalConnector connector = expression.getLogicalConnector(i, i + 1);
				int nestingNumber = connector.getNestingNumber();

				if (prevNesting < nestingNumber)
				{
					sql.append("(" + ruleSQL + " " + connector.getLogicalOperator());
					openingBraces++;
				}
				else if (prevNesting > nestingNumber)
				{
					sql.append(ruleSQL + ") " + connector.getLogicalOperator());
					openingBraces--;
				}
				else
					sql.append(ruleSQL + " " + connector.getLogicalOperator());
				prevNesting = nestingNumber;
			}
			else
			{ // finiching SQL.
				sql.append(ruleSQL);
				if (openingBraces != 0)
				{
					sql.append(")");
				}
			}
		}
		return sql.toString();
	}

	/**
	 * To get the Association from Dynamic Extentsion for the given Assoctaion.
	 * @param association The reference to association
	 * @return the reference of Association from Dynamic Extentsion 
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private AssociationInterface getAssoication(IIntraModelAssociation association)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Collection associations = entityManager.getAssociations(association.getSourceClass()
				.getFullyQualifiedName(), association.getTargetClass().getFullyQualifiedName());
		AssociationInterface theAssociation = null;
		Iterator itr = associations.iterator();
		while (itr.hasNext())
		{
			theAssociation = (AssociationInterface) itr.next();
			if (association.getSourceRoleName().equals(theAssociation.getSourceRole().getName())
					&& (association.getTargetRoleName() == null || association.getTargetRoleName()
							.equals(theAssociation.getTargetRole().getName())))
				return theAssociation;
		}
		return null;
	}

	/**
	 * To get the Primary key attribute of the Class, i.e. attribute having name as "id". 
	 * @param iClass the iClass reference.
	 * @return the reference to attribute having name as "id". if not present returns null.
	 */
	private IAttribute getParentKeyAttrinbute(IClass iClass)
	{
		List<IAttribute> list = iClass.getAttributes();
		IAttribute attribute = null;
		for (int i = 0; i < list.size(); i++)
		{
			attribute = list.get(i);
			if (attribute.getAttributeName().equals(Constants.SYSTEM_IDENTIFIER))
				return attribute;
		}

		throw new NoSuchElementException(Constants.SYSTEM_IDENTIFIER
				+ " Attribute not found for class " + iClass.getFullyQualifiedName());
	}

	/**
	 * To get the IClass for the given Expression.
	 * @param expression The IExpression reference.
	 * @return the IClass reference for the given Expression.
	 */
	private IClass getClass(IExpression expression)
	{
		int noOfRules = expression.getSize();
		for (int i = 0; i < noOfRules; i++)
		{
			IExpressionOperand operand = expression.getOperand(i);
			if (!operand.isSubExpressionOperand())
			{
				IRule rule = (IRule) operand;
				for (int j = 0; j < rule.getSize(); j++)
				{
					ICondition condition = rule.getCondition(j);
					return condition.getAttribute().getUMLClass();
				}
			}
		}
		return null;
	}

	/**
	 * To get the SQL representation of the Rule.
	 * @param rule
	 * @return The SQL representation of the Rule.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	String getSQL(IRule rule) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		StringBuffer sql = new StringBuffer("");
		int noOfConditions = rule.getSize();

		for (int i = 0; i < noOfConditions; i++) // Processing all conditions in Rule combining them with AND operator.
		{
			String condition = getSQL(rule.getCondition(i));

			if (i != noOfConditions - 1) // Intermediate Condition.
				sql.append(condition + " " + LogicalOperator.And + " ");
			else
				// Last Condition
				sql.append(condition);
		}
		return sql.toString();
	}

	/**
	 * To get the SQL Representation of the Condition.
	 * @param condition
	 * @return The SQL Representation of the Condition.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	String getSQL(ICondition condition) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		StringBuffer sql = new StringBuffer("");
		IAttribute attribute = condition.getAttribute();
		DataType dataType = attribute.getDataType();
		String attributeName = getSQL(attribute);

		RelationalOperator operator = condition.getRelationalOperator();
		String strOperator = RelationalOperator.getSQL(operator);
		//Processing Between Operator, it will be treated as (op>=val1 and op<=;val2)
		if (operator.equals(RelationalOperator.Between))
		{
			List<String> values = condition.getValues();
			String firstValue = encodeValueInQuotesIfNeeded(values.get(0), dataType);
			String secondValue = encodeValueInQuotesIfNeeded(values.get(1), dataType);

			sql.append("(" + attributeName);
			sql.append(RelationalOperator.getSQL(RelationalOperator.LessThanOrEquals) + firstValue);
			sql.append(" " + LogicalOperator.And + " " + attributeName
					+ RelationalOperator.getSQL(RelationalOperator.GreaterThanOrEquals)
					+ secondValue + ")");
		}
		else if (operator.equals(RelationalOperator.In)) // Processing In Operator
		{
			sql.append(attributeName + " " + strOperator + " (");
			List<String> valueList = condition.getValues();
			for (int i = 0; i < valueList.size(); i++)
			{
				String value = encodeValueInQuotesIfNeeded(valueList.get(i), dataType);

				if (i == valueList.size() - 1)
					sql.append(value + ")");
				else
					sql.append(value + ",");
			}
		}
		else if (operator.equals(RelationalOperator.IsNotNull)
				|| operator.equals(RelationalOperator.IsNull)) // Processing isNull & isNotNull operator.
		{
			sql.append(attributeName + " " + strOperator);
		}
		else if (operator.equals(RelationalOperator.Contains)
				|| operator.equals(RelationalOperator.StartsWith)
				|| operator.equals(RelationalOperator.EndsWith)) // Processing String related Operators.
		{
			String value = condition.getValue();
			if (operator.equals(RelationalOperator.Contains))
				value = "'%" + value + "%'";
			else if (operator.equals(RelationalOperator.StartsWith))
				value = "'" + value + "%'";
			else if (operator.equals(RelationalOperator.EndsWith))
				value = "'%" + value + "'";

			sql.append(attributeName + " like " + value);
		}
		else
		// Processing rest operators like =, !=, <, > , <=, >= etc.
		{
			String value = condition.getValue();
			value = encodeValueInQuotesIfNeeded(value, dataType);
			sql.append(attributeName + strOperator + value);
		}

		return sql.toString();
	}

	/**
	 * To enclose the Given String values by single Quotes if required. 
	 * It will be required for String & Date datatype values.
	 * @param value The String to be encoded.
	 * @param dataType The DataType of the passed value.
	 * @return The encoded String.
	 */
	String encodeValueInQuotesIfNeeded(String value, DataType dataType)
	{

		if (dataType.equals(DataType.String) || dataType.equals(DataType.Date))
			value = "'" + value + "'";

		return value;
	}

	/**
	 * Get the SQL representatio for Attribute.
	 * @param attribute The reference to Attribute
	 * @return The SQL representatio for Attribute.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	String getSQL(IAttribute attribute) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Entity entity = getEntity(attribute.getUMLClass());
		String tableName = getAliasName(entity);
		Attribute entityAttribute = getAttribute(attribute);
		return tableName + "." + entityAttribute.getColumnProperties().getName();
	}

	/**
	 * To get the Alias Name for the given Entity.
	 * @param entity the reference to Entity.
	 * @return The Alias Name for the given Entity.
	 */
	private String getAliasName(Entity entity)
	{
		String tableName = entity.getName();
		tableName = tableName.substring(tableName.lastIndexOf('.') + 1, tableName.length())
				+ tableSuffix;
		return tableName;
	}

	/**
	 * To get the Entity Class from Dynamic Extension for the given class. 
	 * It caches the Entity in the entityMap. First it checks whether the Entity is cached, if it is cached then it returns it from cache, else get it from Dynamic Extension. 
	 * @param iClass The IClass reference.
	 * @return The reference to Entity Class from Dynamic Extension for the given class.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private Entity getEntity(IClass iClass) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		String fullyQualifiedName = iClass.getFullyQualifiedName();
		Entity entity = entityMap.get(fullyQualifiedName);
		if (entity == null)
		{
			entity = (Entity) entityManager.getEntityByName(fullyQualifiedName);
			entityMap.put(fullyQualifiedName, entity);
		}
		return entity;
	}

	/**
	 * To get the Attribute from Dynamic Extenstion for the Given Attribute.
	 * @param attribute The IAttribute reference.
	 * @return The reference to Attribute from Dynamic Extenstion for the Given Attribute.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private Attribute getAttribute(IAttribute attribute) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Entity entity = getEntity(attribute.getUMLClass());

		Attribute entityAttribute = (Attribute) entityManager.getAttribute(entity.getName(),
				attribute.getAttributeName());

		return entityAttribute;
	}
}
