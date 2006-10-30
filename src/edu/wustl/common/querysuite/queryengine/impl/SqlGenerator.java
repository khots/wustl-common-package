/**
 * 
 */

package edu.wustl.common.querysuite.queryengine.impl;

import java.util.List;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.querysuite.EntityManagerMock;
import edu.wustl.common.querysuite.QueryGeneratorMock;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryengine.ISqlGenerator;
import edu.wustl.common.querysuite.queryobject.DataType;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;

/**
 * To generate SQL from the given Query Object.
 * @author prafull_kadam
 *
 */
public class SqlGenerator implements ISqlGenerator
{

	private String tableSuffix = "0";

	private EntityManager entityManager;

	SqlGenerator(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	/**
	 * @see edu.wustl.common.querysuite.queryengine.ISqlGenerator#generateSQL(edu.wustl.common.querysuite.queryobject.IQuery)
	 */
	public String generateSQL(IQuery query) throws MultipleRootsException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * To get the SQL representation of the Expression.
	 * @param expression
	 * @return The SQL representation of the Expression.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private String getSQL(IExpression expression) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		StringBuffer sql = new StringBuffer("");
		int noOfRules = expression.getSize();
		int prevNesting = 0;
		int openingBraces = 0; // holds number of opening Braces added to SQL.

		for (int i = 0; i < noOfRules; i++)
		{
			IExpressionOperand operand = expression.getOperand(i);
			String ruleSQL = "";
			if (!operand.isSubExpressionOperand())
			{
				ruleSQL = getSQL((IRule) operand); // Processing Rule.
			}
			else
			{
				ruleSQL = getSQL((IExpression) operand); //Processing sub Expression.
			}
			if (!ruleSQL.equals(""))
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
	 * To get the SQL representation of the Rule.
	 * @param rule
	 * @return The SQL representation of the Rule.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private String getSQL(IRule rule) throws DynamicExtensionsSystemException,
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
	private String getSQL(ICondition condition) throws DynamicExtensionsSystemException,
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
	private String encodeValueInQuotesIfNeeded(String value, DataType dataType)
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
	private String getSQL(IAttribute attribute) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Entity entity = (Entity) entityManager.getEntityByName(attribute.getUMLClass()
				.getFullyQualifiedName());
		String tableName = entity.getName();
		Attribute entityAttribute = (Attribute) entityManager.getAttribute(entity.getName(),
				attribute.getAttributeName());
		return tableName + tableSuffix + "." + entityAttribute.getColumnProperties().getName();
	}

	public static void main(String args[]) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		SqlGenerator generator = new SqlGenerator(new EntityManagerMock());
		ParticpiantConditionTest(generator);
		ParticpiantRuleTest(generator);
		ParticpiantExpressionTest(generator);
	}

	private static void ParticpiantConditionTest(SqlGenerator generator)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		System.out.println("ParticpiantConditionTest-------------------------------------------");
		IClass class1 = QueryGeneratorMock.createParticantClass();
		ICondition condition1 = QueryGeneratorMock.createParticipantCondition1(class1);
		System.out.println(generator.getSQL(condition1));

		condition1 = QueryGeneratorMock.createParticipantCondition2(class1);
		System.out.println(generator.getSQL(condition1));

		condition1 = QueryGeneratorMock.createParticipantCondition3(class1);
		System.out.println(generator.getSQL(condition1));

		condition1 = QueryGeneratorMock.createParticipantCondition5(class1);
		System.out.println(generator.getSQL(condition1));

	}

	private static void ParticpiantRuleTest(SqlGenerator generator)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		System.out.println("ParticpiantRuleTest-------------------------------------------");
		IClass class1 = QueryGeneratorMock.createParticantClass();
		IRule rule = QueryGeneratorMock.createParticipantRule1(class1);
		System.out.println(generator.getSQL(rule));
	}

	private static void ParticpiantExpressionTest(SqlGenerator generator)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		System.out.println("ParticpiantExpressionTest-------------------------------------------");
		IClass class1 = QueryGeneratorMock.createParticantClass();
		IExpression expression = QueryGeneratorMock.creatParticipantExpression1(class1);
		System.out.println(generator.getSQL(expression));

		expression = QueryGeneratorMock.creatParticipantExpression2(class1);
		System.out.println(generator.getSQL(expression));
	}
}
