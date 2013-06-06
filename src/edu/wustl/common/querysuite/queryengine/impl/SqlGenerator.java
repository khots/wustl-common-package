/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.querysuite.queryengine.impl;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.IntegerTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.LongTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.StringTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.util.global.Constants.InheritanceStrategy;
import edu.wustl.cab2b.server.category.CategoryOperations;
import edu.wustl.cab2b.server.queryengine.querybuilders.CategoryPreprocessor;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.metadata.category.Category;
import edu.wustl.common.querysuite.queryengine.ISqlGenerator;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraintEntity;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;
import edu.wustl.common.querysuite.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.querysuite.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.querysuite.queryobject.util.InheritanceUtils;
import edu.wustl.common.querysuite.queryobject.util.QueryObjectProcessor;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * To generate SQL from the given Query Object.
 * @author prafull_kadam
 *
 */
public class SqlGenerator implements ISqlGenerator
{

	/**
	 * This map holds integer value that will be appended to each table alias in the sql. 
	 */
	Map<IExpressionId, Integer> aliasAppenderMap = new HashMap<IExpressionId, Integer>();
	
	/**
	 * This map holds the alias name generated for each fully Qualified className, where className id key & value is the aliasName generated for that className.
	 */
	Map<String, String> aliasNameMap = new HashMap<String, String>();
	
	/**
	 * reference to the joingraph object present in the query object.  
	 */
	private JoinGraph joinGraph;

	/**
	 * reference to the constraints object present in the query object.  
	 */
	IConstraints constraints;

	public static final String COLUMN_NAME = "Column";

	/**
	 * This set will contain the expression ids of the empty expression. 
	 * An expression is empty expression when it does not contain any Rule & its sub-expressions (also their subexpressions & so on) also does not contain any Rule
	 */
	private Set<IExpressionId> emptyExpressions;//Set of Empty Expressions.
	
	// Variables required for output tree.
	/**
	 * List of Roots of the output tree node.
	 */
	List<OutputTreeDataNode> rootOutputTreeNodeList;
	
	/**
	 * This map is used in output tree creation logic.
	 * It is map of alias appender verses the output tree node. 
	 * This map is used to ensure that no duplicate output tree node is created for the expressions having same alias appender.
	 */
	Map<Integer, OutputTreeDataNode> outputTreeNodeMap ;
	
	/**
	 * This map contains information about the tree node ids, attributes & their correspoiding column names in the generated SQL.
	 * - Inner most map Map<AttributeInterface, String> contains mapping of attribute interface verses the column name in SQL.
	 * - The outer map  Map<Long, Map<AttributeInterface, String>> contains mapping of treenode Id verses the map in above step. This map contains mapping required for one output tree.
	 * - The List contains the mapping of all output trees that are formed by the query.
	 */
//	List<Map<Long, Map<AttributeInterface, String>>> columnMapList;
	private int treeNo; // this count represents number of output trees formed.

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
		Logger.out.debug("Srarted SqlGenerator.generateSQL().....");
		String sql = buildQuery(query);
		Logger.out.debug("Finished SqlGenerator.generateSQL()...SQL:" + sql);
		return sql;
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
		
		processExpressionsWithCategories(queryClone);

		this.joinGraph = (JoinGraph) constraints.getJoinGraph();
		IExpression rootExpression = constraints.getExpression(constraints.getRootExpressionId());

		// Initializin map variables.
		aliasAppenderMap = new HashMap<IExpressionId, Integer>();
		aliasNameMap = new HashMap<String, String>();
		createAliasAppenderMap(rootExpression, 1, new Integer(1),
				new HashMap<List<IAssociation>, IExpressionId>());

		// Identifying empty Expressions.
		emptyExpressions = new HashSet<IExpressionId>();
		isEmptyExpression(rootExpression.getExpressionId());
		
		//Generating output tree.
		createTree();
		
		//Creating SQL.
		String wherePart = "Where " + getWherePartSQL(rootExpression, null, false);
		String fromPart = getFromPartSQL(rootExpression, null, new HashSet<Integer>());
		String selectPart =  getSelectPart();
		String sql = selectPart + " " + fromPart + " " + wherePart;

		return sql;
	}

	/**
	 * To handle Expressions constrained on Categories. 
	 * If Query contains an Expression having Constraint Entity as Category, 
	 * then that Expression is expanded in such a way that it will look as if it is constrained on Classes without changing Query criteria.
	 * @throws SqlException if there is any error in processing category.
	 */
	private void processExpressionsWithCategories(IQuery query) throws SqlException
	{
		if (containsCategrory(constraints))
		{
			Connection connection=null;
			try
			{
				EntityInterface rootEntity = null;
				EntityInterface rootDEEntity = constraints.getExpression(
						constraints.getRootExpressionId()).getConstraintEntity()
						.getDynamicExtensionsEntity();
				boolean isCategory = edu.wustl.cab2b.common.util.Utility.isCategory(rootDEEntity);
	
				// This is temporary work around, This connection parameter will be reomoved in future.
				InitialContext context = new InitialContext();
				DataSource dataSource = (DataSource) context.lookup("java:/catissuecore");
				connection = dataSource.getConnection();
				
				/**
				 * if the root entity itself is category, then get the root entity of the category & pass it to the processCategory() method.
				 */
				if (isCategory) 
				{
					Category category = new CategoryOperations().getCategoryByEntityId(rootDEEntity
							.getId(), connection);
					rootEntity = EntityManager.getInstance().getEntityByIdentifier(
							category.getRootClass().getDeEntityId());
				}
				else
				{
					rootEntity = rootDEEntity;
				}
				new CategoryPreprocessor().processCategories(query);
			}
			catch (Exception e)
			{
				Logger.out.error(e.getMessage(), e);
				throw new SqlException("Error in preprocessing category!!!!", e);
			}
			finally
			{
				if (connection != null) // Closing connection.
				{
					try
					{
						connection.close();
					}
					catch (SQLException e)
					{
						Logger.out.error(e.getMessage(), e);
						throw new SqlException("Error in closing connection while preprocessing category!!!!", e);
					}
				}
			}
		}
	}

	/**
	 * To check whether there is any Expression having Constraint Entity as category or not.
	 * @param theConstraints reference to IConstraints of the Query object.
	 * @return true if there is any constraint put on category. 
	 */
	private boolean containsCategrory(IConstraints theConstraints)
	{
		Set<IConstraintEntity> constraintEntities = constraints.getConstraintEntities();
		for (IConstraintEntity entity : constraintEntities)
		{
			boolean isCategory = edu.wustl.cab2b.common.util.Utility.isCategory(entity
					.getDynamicExtensionsEntity());
			if (isCategory)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * This method will return map of DE attributes verses & their column names present in the select part of the SQL. 
	 * These DE attributes will be attributes of the each node present in the Output tree.
	 * @return map of DE attributes verses & their column names present in the select part of the SQL. null if no sql is generated.
	 * @see edu.wustl.common.querysuite.queryengine.ISqlGenerator#getColumnMap()
	 * @deprecated This method will not required any more.
	 */
	public Map<Long, Map<AttributeInterface, String>> getColumnMap()
	{
		return null;//columnMapList.get(0);
	}

//	/**
//	 * To get the Map of Output tree formed by SQL Generator for the given query, which contains information of tree & mapping of each treenode attribute to the SQL column name.
//	 * @return Map of output trees & column mapping. 
//	 */
//	public Map<OutputTreeDataNode,Map<Long, Map<AttributeInterface, String>>> getOutputTreeMap()
//	{
//		Map<OutputTreeDataNode,Map<Long, Map<AttributeInterface, String>>> map = new HashMap<OutputTreeDataNode, Map<Long,Map<AttributeInterface,String>>>();
//		
//		for (int i = 0; i < rootOutputTreeNodeList.size(); i++)
//		{
//			OutputTreeDataNode node = rootOutputTreeNodeList.get(i);
//			map.put(node, columnMapList.get(i));
//		}
//		return map;
//	}
	
	/**
	 * To get the select part of the SQL.
	 * @return The SQL for the select part of the query.
	 */
	String getSelectPart()
	{
		selectIndex = 0;
//		columnMapList = new ArrayList<Map<Long,Map<AttributeInterface,String>>>();
		String selectAttribute = "Select ";
		for(OutputTreeDataNode rootOutputTreeNode:rootOutputTreeNodeList)
		{
			selectAttribute += getSelectAttributes(rootOutputTreeNode);
		}
		if (selectAttribute.endsWith(" ,"))
		{
			selectAttribute = selectAttribute.substring(0, selectAttribute.length() - 2); // remove last part " ,"
		}
		return selectAttribute;
	}
	

	/**
	 * It will return the select part attributes for this node along with its child nodes.
	 * @param treeNode the output tree node.
	 * @return  The select part attributes for this node along with its child nodes.
	 */
	private String getSelectAttributes(OutputTreeDataNode treeNode)
	{
		StringBuffer selectPart = new StringBuffer("");
		IExpression expression = constraints.getExpression(treeNode.getExpressionId());

		IOutputEntity outputEntity = treeNode.getOutputEntity();
		List<AttributeInterface> attributes = outputEntity.getSelectedAttributes();
		
		for (AttributeInterface attribute : attributes)
		{
			selectPart.append(getSQL(attribute, expression));
			String columnAliasName = COLUMN_NAME + selectIndex;
			selectPart.append(" " + columnAliasName + " ,");
			
			treeNode.addAttribute(new QueryOutputTreeAttributeMetadata(attribute,columnAliasName));
			
			selectIndex++;
		}
		List<OutputTreeDataNode> children = treeNode.getChildren();
		for (OutputTreeDataNode childTreeNode : children)
		{
			selectPart.append(getSelectAttributes(childTreeNode));
		}
		return selectPart.toString();
	}
	
	private int selectIndex;

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

		EntityInterface leftEntity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		EntityInterface superClassEntity = leftEntity.getParentEntity();
		if (processedAlias.isEmpty()) // this will be true only for root node.
		{
			leftAlias = getAliasName(expression);
			buffer.append("From " + leftEntity.getTableProperties().getName() + " " + leftAlias);

			//processing Parent class heirarchy.
			if (superClassEntity != null)
			{
				EntityInterface theLeftEntity = leftEntity;
				while (superClassEntity != null)
				{
					InheritanceStrategy inheritanceType = theLeftEntity.getInheritanceStrategy();
					if (InheritanceStrategy.TABLE_PER_SUB_CLASS.equals(inheritanceType)) // only need to handle this type of inheritance here.
					{
						AttributeInterface primaryKey = getPrimaryKey(theLeftEntity);
						String primaryKeyColumnName = primaryKey.getColumnProperties().getName();
						String subClassAlias = getAliasFor(expression, theLeftEntity);
						String superClassAlias = getAliasFor(expression, superClassEntity);
						buffer.append(" left join "
								+ superClassEntity.getTableProperties().getName() + " "
								+ superClassAlias + " on ");
						String leftAttribute = subClassAlias + "." + primaryKeyColumnName;
						String rightAttribute = superClassAlias + "." + primaryKeyColumnName;
						buffer.append("(" + leftAttribute + "=" + rightAttribute + ")");
					}
					theLeftEntity = superClassEntity;
					superClassEntity = superClassEntity.getParentEntity();
				}
			}
		}

		Integer parentExpressionAliasAppender = aliasAppenderMap.get(parentExpressionId);
		processedAlias.add(parentExpressionAliasAppender);

		// Processing children
		buffer.append(processChildExpressions(leftAlias, processedAlias, parentExpressionId));
		return buffer.toString();
	}

	/**
	 * To process all child expression of the given Expression & get their SQL representation for where part. 
	 * @param leftAlias left table alias in join.
	 * @param processedAlias The list of precessed alias.
	 * @param parentExpressionId The reference to expression whose children to be processed.
	 * @return the left join sql for children expression.
	 * @throws SqlException when there is error in the passed IQuery object.
	 */
	private String processChildExpressions(String leftAlias, Set<Integer> processedAlias,
			IExpressionId parentExpressionId) throws SqlException
	{
		StringBuffer buffer = new StringBuffer();
		List<IExpressionId> children = joinGraph.getChildrenList(parentExpressionId);
		if (!children.isEmpty())
		{
			// processing all outgoing edges/nodes from the current node in the joingraph.
			for (IExpressionId childExpressionId : children)
			{
				IExpression childExpression = constraints.getExpression(childExpressionId);
				
				IAssociation association = joinGraph.getAssociation(parentExpressionId,
						childExpressionId);

				AssociationInterface actualEavAssociation = ((IIntraModelAssociation) association)
						.getDynamicExtensionsAssociation();
				AssociationInterface eavAssociation = actualEavAssociation;
				EntityInterface rightEntity = eavAssociation.getTargetEntity();
				String rightAlias = getAliasFor(childExpression, rightEntity);
				
				if (!processedAlias.contains(aliasAppenderMap.get(childExpressionId)))
				{
					if (InheritanceUtils.getInstance().isInherited(eavAssociation))
					{
						eavAssociation = InheritanceUtils.getInstance().getActualAassociation(eavAssociation);
						rightEntity = eavAssociation.getTargetEntity();
						
						leftAlias = getAliasFor(constraints.getExpression(parentExpressionId), eavAssociation.getEntity());
						rightAlias = getAliasFor(childExpression, eavAssociation.getTargetEntity());
					}
					
					EntityInterface childEntity = childExpression.getConstraintEntity()
							.getDynamicExtensionsEntity();

					EntityInterface leftEntity = eavAssociation.getEntity();


					ConstraintPropertiesInterface constraintProperties = eavAssociation
							.getConstraintProperties();
					if (constraintProperties.getSourceEntityKey() != null
							&& constraintProperties.getTargetEntityKey() != null)// Many to Many Case
					{

						String leftAttribute = null;
						String rightAttribute = null;

						String middleTableName = constraintProperties.getName();
						String middleTableAlias = getAliasForMiddleTable(childExpression, middleTableName);
						
						AttributeInterface primaryKey = getPrimaryKey(leftEntity);
						leftAttribute = leftAlias + "."
								+ primaryKey.getColumnProperties().getName();
						
						rightAttribute = middleTableAlias + "." + constraintProperties.getSourceEntityKey();
						// Forming joing with middle table.
						buffer.append(" left join " + middleTableName + " "
								+ middleTableAlias + " on ");
						buffer.append("(" + leftAttribute + "=" + rightAttribute);

						/*
						 * Adding descriminator column condition for the 1st parent node while forming FROM part left joins. 
						 * This will be executed only once i.e. when only one node is processed. 
						 */
						if (processedAlias.size()==1)
						{
							buffer.append(getDescriminatorCondition(actualEavAssociation.getEntity(),leftAlias));
						}
						buffer.append(")");
						
						// Forming join with child table.
						leftAttribute = middleTableAlias + "." + constraintProperties.getTargetEntityKey();
						primaryKey = getPrimaryKey(rightEntity);
						rightAttribute = rightAlias + "."
								+ primaryKey.getColumnProperties().getName();
						
						buffer.append(" left join " + rightEntity.getTableProperties().getName() + " "
								+ rightAlias + " on ");
						buffer.append("(" + leftAttribute + "=" + rightAttribute);
						
						/*
						 * Adding descriminator column condition for the child node while forming FROM part left joins. 
						 */
						buffer.append(getDescriminatorCondition(actualEavAssociation.getTargetEntity(),rightAlias)+ ")");
					}
					else
					{
						String leftAttribute = null;
						String rightAttribute = null;
						if (constraintProperties.getSourceEntityKey() != null)// Many Side
						{
							leftAttribute = leftAlias + "."
									+ constraintProperties.getSourceEntityKey();
							AttributeInterface primaryKey = getPrimaryKey(rightEntity);
							rightAttribute = rightAlias + "."
									+ primaryKey.getColumnProperties().getName();
						}
						else // One Side
						{
							AttributeInterface primaryKey = getPrimaryKey(leftEntity);
							leftAttribute = leftAlias + "."
									+ primaryKey.getColumnProperties().getName();
							rightAttribute = rightAlias + "."
									+ constraintProperties.getTargetEntityKey();
						}
						buffer.append(" left join " + rightEntity.getTableProperties().getName() + " "
								+ rightAlias + " on ");
						buffer.append("(" + leftAttribute + "=" + rightAttribute);
						
						/*
						 * Adding descriminator column condition for the 1st parent node while forming FROM part left joins. 
						 * This will be executed only once i.e. when only one node is processed. 
						 */
						if (processedAlias.size()==1)
						{
							buffer.append(getDescriminatorCondition(actualEavAssociation.getEntity(),leftAlias));
						}
						/*
						 * Adding descriminator column condition for the child node while forming FROM part left joins. 
						 */
						buffer.append(getDescriminatorCondition(actualEavAssociation.getTargetEntity(),rightAlias)+ ")");
					}


					buffer.append(getParentHeirarchy(childExpression, childEntity, rightEntity));
				}
				// append from part SQL for the next Expressions.
				buffer.append(getFromPartSQL(childExpression, rightAlias, processedAlias));
			}
		} 
		return buffer.toString();
	}

	/**
	 * To get the SQL for the descriminator column condition for the given entity. 
	 * It will return SQL for condition in format: " AND <DescriminatorColumnName> = '<DescriminatorColumnValue>'"  
	 * @param entity The reference to the entity.
	 * @param aliasName The alias Name assigned to that entity table in the SQL.
	 * @return The String representing SQL for the descriminator column condition for the given entity, if inheritance strategy is TABLE_PER_HEIRARCHY. 
	 * 		Returns empty String if there is no Descriminator column condition present for the Entity.
	 * 		i.e. when either of following is true:
	 * 		1. when entity is not derived entity.(Parent entity is null)
	 * 		2. Inheritance strategy is not TABLE_PER_HEIRARCHY.
	 */
	private String getDescriminatorCondition(EntityInterface entity, String aliasName)
	{
		String sql="";
		EntityInterface parentEntity = entity.getParentEntity();
		// Checking whether the entity is derived or not.
		if (parentEntity != null)
		{
			InheritanceStrategy inheritanceType = entity.getInheritanceStrategy();
			if (inheritanceType.equals(InheritanceStrategy.TABLE_PER_HEIRARCHY))
			{
				String columnName = entity.getDiscriminatorColumn();
				String columnValue = entity.getDiscriminatorValue();
				//Assuming Discrimanator is of type String.
				String condition = aliasName + "." + columnName + "='"
						+ columnValue + "'";
				sql = " " + LogicalOperator.And + " " + condition;
			}
		}
		return sql;
	}
	
	/**
	 * To get the alias name for the Many to Many table.
	 * @param childExpression The child Expression of the association. 
	 * @param middleTableName The Many to Mant table name.
	 * @return The String representing aliasName for the Many to Many table.
	 */
	private String getAliasForMiddleTable(IExpression childExpression, String middleTableName)
	{
		return getAliasForClassName("."+middleTableName) + "_" + aliasAppenderMap.get(childExpression.getExpressionId());
	}

	/**
	 * To add the Parent Heirarchy to the join part.
	 * @param childExpression The Expression to which the entity belongs.
	 * @param childEntity The entity whose parent heirarchy to be joined. 
	 * @param alreadyAddedEntity The entity already added in join part.
	 * @return left join sql for childEntity.  
	 * @throws SqlException when there is error in the passed IQuery object.
	 */
	private String getParentHeirarchy(IExpression childExpression, EntityInterface childEntity,
			EntityInterface alreadyAddedEntity) throws SqlException
	{
		String combinedJoinPart = "";
		if (childEntity.getParentEntity() != null) //Joining Parent & child classes of the entity.
		{
			EntityInterface entity = childEntity;
			EntityInterface parent = childEntity.getParentEntity();
			boolean isReverse = false;
			List<String> joinSqlList = new ArrayList<String>();
			while (parent != null)
			{
				if (entity.equals(alreadyAddedEntity))
				{
					isReverse = true;
				}
				
				if (entity.getInheritanceStrategy().equals(
						InheritanceStrategy.TABLE_PER_SUB_CLASS))
				{ 
					String leftEntityalias = getAliasFor(childExpression, entity);
					String rightEntityalias = getAliasFor(childExpression, parent);
					AttributeInterface primaryKey = getPrimaryKey(entity);
					String primaryKeyColumnName = primaryKey.getColumnProperties().getName();

					String leftAttributeColumn = leftEntityalias + "." + primaryKeyColumnName;
					String rightAttributeColumn = rightEntityalias + "." + primaryKeyColumnName;
					String sql = null;
					if (isReverse)
					{
						sql = " left join " + parent.getTableProperties().getName() + " "
								+ rightEntityalias + " on ";
						sql += "(" + leftAttributeColumn + "=" + rightAttributeColumn + ")";
					}
					else
					{
						sql = " left join " + entity.getTableProperties().getName() + " "
								+ leftEntityalias + " on ";
						sql += "(" + rightAttributeColumn + "=" + leftAttributeColumn + ")";
					}
					joinSqlList.add(0, sql);
				}
				entity = parent;
				parent = parent.getParentEntity();
			}

			for (String joinSql : joinSqlList)
			{
				combinedJoinPart += joinSql;
			}
		}
		return combinedJoinPart;
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

		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();

		String pseudoAndSQL = null;
		
		if (parentExpression != null) // This will be true only for Expression which is not root Expression of the Query.
		{
			IAssociation association = joinGraph.getAssociation(parentExpression.getExpressionId(),
					expression.getExpressionId());
			AssociationInterface eavAssociation = ((IIntraModelAssociation) association)
					.getDynamicExtensionsAssociation();
			
			if (InheritanceUtils.getInstance().isInherited(eavAssociation))
			{
				eavAssociation = InheritanceUtils.getInstance().getActualAassociation(eavAssociation);
			}
			
			if (isPAND) 
			{
				// Adding Pseudo and condition in the where part.
				pseudoAndSQL = createPseudoAndCondition(expression, parentExpression, eavAssociation);
			}
		}

		buffer.append(processOperands(expression));

		/*
		 * If the Query has only one Expression, which referes to an entity having inheritance strategy as TABLE_PER_HEIRARCHY,
		 * then the Descriminator column condition needs to be added in the WHERE part of SQL as it can not be added in the FROM part of the query.
		 * This can be identified by following checks 1. parentExpression is null & 2. expression have no child expression.
		 */
		if (parentExpression == null) // This will be true only for root Expression of the Query.
		{
			List<IExpressionId> childrenList = joinGraph.getChildrenList(expression.getExpressionId());
			if (childrenList==null || childrenList.isEmpty())
			{ 
				/*
				 * No Child Expressions present for the root node, so this is only Expression in the Query.
				 * So check for the Inheritance strategy.
				 * If its derived entity with inheritance strategy as TABLE_PER_HEIRARCHY, then append the descriminator condition SQL in buffer. 
				 */
				if (entity.getParentEntity()!=null && InheritanceStrategy.TABLE_PER_HEIRARCHY.equals(entity.getInheritanceStrategy()))
				{
					String descriminatorCondition = getDescriminatorCondition(entity, getAliasFor(expression, entity));
					buffer.insert(0, "(");
					buffer.append(")");
					buffer.append(descriminatorCondition);
				}
			}
		}

		if (isPAND) // Append Pseudo can sql if the expression is psuedo anded.
		{
			buffer.insert(0, pseudoAndSQL);
		}
		return buffer.toString();
	}

	/**
	 * To form the Pseudo-And condition for the expression.
	 * @param expression The child Expression reference.
	 * @param parentExpression The parent Expression.
	 * @param eavAssociation The association between parent & child expression.
	 * @return The Pseudo-And SQL condition.
	 * @throws SqlException When there is problem in creating from part. problem can be like: no primary key found in entity for join.
	 */
	private String createPseudoAndCondition(IExpression expression, IExpression parentExpression, AssociationInterface eavAssociation) throws SqlException
	{
		String pseudoAndSQL;
		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		String tableName = entity.getTableProperties().getName() + " ";
		String leftAlias = getAliasName(expression);
		String selectAttribute = leftAlias + ".";

		ConstraintPropertiesInterface constraintProperties = eavAssociation.getConstraintProperties();
		if (constraintProperties.getSourceEntityKey() != null
				&& constraintProperties.getTargetEntityKey() != null)// Many to many case.
		{				
			// This will start FROM part of SQL from the parent table.
			selectAttribute = getAliasName(parentExpression) +"."+  getPrimaryKey(parentExpression.getConstraintEntity().getDynamicExtensionsEntity()).getColumnProperties().getName();
			pseudoAndSQL = "Select " + selectAttribute;
			Set<Integer> processedAlias = new HashSet<Integer>();
			String fromPart = getFromPartSQL(parentExpression, leftAlias, processedAlias);
			pseudoAndSQL += " " +fromPart + " where ";
		}
		else
		{
			if (constraintProperties.getTargetEntityKey() == null)
			{
				selectAttribute += getPrimaryKey(entity).getColumnProperties().getName();
			}
			else
			{
				selectAttribute += constraintProperties.getTargetEntityKey();
			}
			pseudoAndSQL = "Select " + selectAttribute;
			Set<Integer> processedAlias = new HashSet<Integer>();
			processedAlias.add(aliasAppenderMap.get(expression.getExpressionId()));
			String fromPart = getFromPartSQL(expression, leftAlias, processedAlias);
			pseudoAndSQL += " From " + tableName + " " + leftAlias + fromPart + " where ";
		}
		return pseudoAndSQL;
	}

	/**
	 * To process all child operands of the expression.
	 * @param expression the reference to Expression.
	 * @return the SQL representation for the child operands.
	 * @throws SqlException When there is error in the passed IQuery object.
	 */
	private String processOperands(IExpression expression) throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");
		int currentNestingCounter = 0;// holds current nesting number count i.e. no of opening Braces that needs to be closed.
		int noOfRules = expression.numberOfOperands();
		
		for (int i = 0; i < noOfRules; i++)
		{
			IExpressionOperand operand = expression.getOperand(i);
			String operandSQL = "";
			boolean emptyExppression = false;
			if (!operand.isSubExpressionOperand())
			{
				operandSQL = getSQL((IRule) operand); // Processing Rule.
			}
			else
			//Processing sub Expression.
			{
				emptyExppression = emptyExpressions.contains(operand);
				if (!emptyExppression)
				{
					operandSQL = processSubExpression(expression, (IExpressionId) operand);
				}
			}
			
			if (!operandSQL.equals("") && noOfRules != 1)
			{
				operandSQL = "(" + operandSQL + ")"; // putting RuleSQL  in Braces so that it will not get mixed with other Rules.
			}
			
			if (i!=noOfRules-1)
			{
				LogicalConnector connector = (LogicalConnector) expression.getLogicalConnector(i,
						i + 1);
				int nestingNumber = connector.getNestingNumber();

				int nextIndex = i+1;
				IExpressionOperand nextOperand = expression.getOperand(nextIndex);
				if (nextOperand.isSubExpressionOperand() && emptyExpressions.contains(nextOperand))
				{
					for (;nextIndex < noOfRules; nextIndex++)
					{
						nextOperand = expression.getOperand(nextIndex);
						if (!(nextOperand.isSubExpressionOperand() && emptyExpressions.contains(nextOperand)))
						{
							break;
						}
					}
					if (nextIndex==noOfRules)// Expression over add closing parenthesis.
					{
						buffer.append(operandSQL);
						buffer.append(getParenthesis(currentNestingCounter, ")"));
						currentNestingCounter=0;
					}
					else
					{
						LogicalConnector newConnector = (LogicalConnector) expression.getLogicalConnector(nextIndex-1,
								nextIndex);
						int newNestingNumber = newConnector.getNestingNumber();
						currentNestingCounter = attachOperandSQL(buffer, currentNestingCounter, operandSQL, newNestingNumber);
						buffer.append(" " + newConnector.getLogicalOperator());
					}
					i = nextIndex-1;
				}
				else
				{
					currentNestingCounter = attachOperandSQL(buffer, currentNestingCounter, operandSQL, nestingNumber);
					buffer.append(" " + connector.getLogicalOperator());
				}
			}
			else
			{
				buffer.append(operandSQL);
				buffer.append(getParenthesis(currentNestingCounter, ")"));// Finishing SQL by adding closing parenthesis if any.
				currentNestingCounter=0;
			}
		}
		return buffer.toString();
	}

	/**
	 * To append the operand SQL to the SQL buffer, with required number of parenthesis.
	 * @param buffer The reference to the String buffer containing SQL for SQL of operands of an expression.
	 * @param currentNestingCounter The current nesting count.
	 * @param operandSQL The SQL of the operand to be appended to buffer
	 * @param nestingNumber The nesting number for the current operand's operator.
	 * @return The updated current nesting count.
	 */
	private int attachOperandSQL(StringBuffer buffer, int currentNestingCounter, String operandSQL, int nestingNumber)
	{
		if (currentNestingCounter < nestingNumber)
		{
			buffer.append(getParenthesis(nestingNumber-currentNestingCounter, "("));
			currentNestingCounter = nestingNumber;
			buffer.append(operandSQL);
		}
		else if (currentNestingCounter > nestingNumber)
		{
			buffer.append(operandSQL);
			buffer.append(getParenthesis(currentNestingCounter-nestingNumber, ")"));
			currentNestingCounter = nestingNumber;
		}
		else 
		{
			buffer.append(operandSQL);
		}
		return currentNestingCounter;
	}

	/**
	 * To get n number of parenthesis.
	 * @param n The positive integer value
	 * @param parenthesis either Opening parenthesis or closing parenthesis. 
	 * @return The n number of parenthesis.
	 */
	String getParenthesis(int n, String parenthesis)
	{
		String string = "";
		for (int i = 0; i < n; i++)
		{
			string+=parenthesis;
		}
		return string;
	}
	
	/**
	 * To Proceess sub Expression.
	 * @param expression The reference to parent Expression.
	 * @param childExpressionId The refrence to child Expression.
	 * @return The SQL representation for the Sub expression.
	 * @throws SqlException When there is error in the passed IQuery object.
	 */
	private String processSubExpression(IExpression expression, IExpressionId childExpressionId)
			throws SqlException
	{
		IExpression childExpression = constraints.getExpression(childExpressionId);

		boolean isPAND = ((Expression) expression).isPseudoAnded(childExpression.getExpressionId(),
				constraints);
		String sql = getWherePartSQL(childExpression, expression, isPAND);
		if (isPAND)
		{
			IAssociation association = joinGraph.getAssociation(expression.getExpressionId(),
					childExpression.getExpressionId());
			AssociationInterface eavAssociation = ((IIntraModelAssociation) association)
					.getDynamicExtensionsAssociation();
			
			if (InheritanceUtils.getInstance().isInherited(eavAssociation))
			{
				eavAssociation = InheritanceUtils.getInstance().getActualAassociation(eavAssociation);
			}
			
			String joinAttribute = getAliasName(expression) + ".";

			ConstraintPropertiesInterface constraintProperties = eavAssociation.getConstraintProperties();
			if (constraintProperties.getSourceEntityKey() != null
					&& constraintProperties.getTargetEntityKey() != null)// Many to Many Case
			{
				joinAttribute += getPrimaryKey(
						expression.getConstraintEntity().getDynamicExtensionsEntity())
						.getColumnProperties().getName();
			}
			else
			{
				if (constraintProperties.getSourceEntityKey() == null)
				{
					joinAttribute += getPrimaryKey(
							expression.getConstraintEntity().getDynamicExtensionsEntity())
							.getColumnProperties().getName();
				}
				else
				{
					joinAttribute += constraintProperties.getSourceEntityKey();
				}
			}
			
			sql = joinAttribute + " = ANY(" + sql + ")";
		}
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
		if (noOfConditions==0)
		{
			throw new SqlException("No conditions defined in the Rule!!!");
		}
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
		if (!(dataType instanceof DateTypeInformationInterface
				|| dataType instanceof IntegerTypeInformationInterface
				|| dataType instanceof LongTypeInformationInterface || dataType instanceof DoubleTypeInformationInterface))
		{
			throw new SqlException(
					"Incorrect Data type of operand for Between oparator in condition:" + condition);
		}

		String firstValue = modifyValueforDataType(values.get(0), dataType);
		String secondValue = modifyValueforDataType(values.get(1), dataType);

		buffer.append("(" + attributeName);
		buffer.append(RelationalOperator.getSQL(RelationalOperator.GreaterThanOrEquals)
				+ firstValue);
		buffer.append(" " + LogicalOperator.And + " " + attributeName
				+ RelationalOperator.getSQL(RelationalOperator.LessThanOrEquals) + secondValue
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
				Logger.out.error(parseExp.getMessage(), parseExp);
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
		
		AttributeInterface actualAttribute = attribute;
		
		if (InheritanceUtils.getInstance().isInherited(attribute))
		{
			actualAttribute = InheritanceUtils.getInstance().getActualAttribute(attribute);
		}
		EntityInterface attributeEntity = actualAttribute.getEntity();
		String aliasName = getAliasFor(expression, attributeEntity);

		String attributeName = aliasName + "." + actualAttribute.getColumnProperties().getName();

		return attributeName;
	}

	private static final int ALIAS_NAME_LENGTH = 25;

	/**
	 * To get the Alias Name for the given IExpression. It will return alias name for the DE entity associated with constraint entity.
	 * @param expression The reference to IExpression.
	 * @return The Alias Name for the given Entity.
	 */
	String getAliasName(IExpression expression)
	{
		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		return getAliasFor(expression, entity);
	}

	/**
	 * To get the aliasName for the given entity present which is associated with Expression.
	 * @param expression The reference to IExpression.
	 * @param attributeEntity The reference to the Entity for which the alias to be searched.
	 * @return The Alias Name for the given Entity.
	 */
	private String getAliasFor(IExpression expression, EntityInterface attributeEntity)
	{
		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		EntityInterface aliasEntity = entity;

		EntityInterface parentEntity = entity.getParentEntity();

		while (parentEntity != null && !attributeEntity.equals(entity))
		{
			InheritanceStrategy type = entity.getInheritanceStrategy();

			if (type.equals(InheritanceStrategy.TABLE_PER_CONCRETE_CLASS))
			{
				aliasEntity = entity;
				break;
			}
			else if (type.equals(InheritanceStrategy.TABLE_PER_SUB_CLASS))
			{
				entity = parentEntity;
				aliasEntity = parentEntity;
				parentEntity = parentEntity.getParentEntity();
			}
			else if (type.equals(InheritanceStrategy.TABLE_PER_HEIRARCHY))
			{
				while (parentEntity != null && type.equals(InheritanceStrategy.TABLE_PER_HEIRARCHY))
				{
					entity = parentEntity;
					if (attributeEntity.equals(entity))
					{
						break;
					}
					type = entity.getInheritanceStrategy();
					parentEntity = parentEntity.getParentEntity();
				}
				aliasEntity = entity;
			}
		}

		// Need an extra check for the TABLE_PER_HEIRARCHY case. 
		// Because even if attribute belongs to this aliasEntity, but if its association with parent is of TABLE_PER_HEIRARCHY type, its alias will be one of its parent heirarchy. 
		parentEntity = aliasEntity.getParentEntity();
		InheritanceStrategy type = aliasEntity.getInheritanceStrategy();
		while (parentEntity != null && type.equals(InheritanceStrategy.TABLE_PER_HEIRARCHY))
		{
			aliasEntity = parentEntity;
			type = aliasEntity.getInheritanceStrategy();
			parentEntity = parentEntity.getParentEntity();
		}

		String aliasName = getAliasForClassName(aliasEntity.getName());
		Integer aliasAppender = aliasAppenderMap.get(expression.getExpressionId());
		if (aliasAppender == null)// for Junits
		{
			aliasAppender = 0;
		}
		aliasName = aliasName + "_" + aliasAppender;
		return aliasName;
	}

	/**
	 * To get the alias for the given Class Name.
	 * @param className The follyQualified class Name.
	 * @return The alias name for the given class Name.
	 */
	private String getAliasForClassName(String className)
	{
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
			if (attribute.getIsPrimaryKey() || attribute.getName().equals("id"))
			{
				return attribute;
			}
		}
		EntityInterface parentEntity = entity.getParentEntity();
		if (parentEntity != null)// && entity.getInheritanceStrategy().equals(InheritanceStrategy.TABLE_PER_SUB_CLASS))
		{
			return getPrimaryKey(parentEntity);
		}

		throw new SqlException("No Primary key attribute found for Entity:" + entity.getName());
	}
	
	/**
	 * To check if the Expression is empty or not. It will simultaneously add such empty expressions in the emptyExpressions set.
	 * 
	 * An expression is said to be empty when:
	 *  - it contains no rule as operand.
	 *  - and all of its children(i.e subExpressions & their subExpressions & so on) contains no rule
	 *  
	 * @param expressionId the reference to the expression id.
	 * @return true if the expression is empty.
	 */
	private boolean isEmptyExpression(IExpressionId expressionId)
	{
		Expression expression = (Expression)constraints.getExpression(expressionId);
		List<IExpressionId> operandList = joinGraph.getChildrenList(expressionId);

		boolean isEmpty = true;
		if (!operandList.isEmpty()) // Check whether any of its children contains rule. 
		{
			for(IExpressionId subExpression: operandList)
			{
				if (!isEmptyExpression(subExpression))
				{
					isEmpty = false;
				}
			}
		}

		isEmpty  = isEmpty && !expression.containsRule();// check if there are rule present as subexpression.
		if (isEmpty)
		{
			emptyExpressions.add(expressionId); // Expression is empty.
		}

		return isEmpty;
	}
	/**
	 * To create output tree for the given expression graph.
	 * @throws MultipleRootsException When there exists multiple roots in joingraph.
	 */
	private void createTree() throws MultipleRootsException
	{
		IExpressionId rootExpID = joinGraph.getRoot();
		IExpression rootExpression = constraints.getExpression(rootExpID); 
		rootOutputTreeNodeList = new ArrayList<OutputTreeDataNode>();
		outputTreeNodeMap = new HashMap<Integer, OutputTreeDataNode>();
		OutputTreeDataNode rootOutputTreeNode = null;
		treeNo = 0;
		if (rootExpression.isInView())
		{
			IOutputEntity rootOutputEntity = getOutputEntity(rootExpression);
			rootOutputTreeNode = new OutputTreeDataNode(rootOutputEntity,rootExpID, treeNo++);
			
			rootOutputTreeNodeList.add(rootOutputTreeNode);
			outputTreeNodeMap.put(aliasAppenderMap.get(rootExpID), rootOutputTreeNode);
		}
		completeTree(rootExpression, rootOutputTreeNode);
	}

	/**
	 * TO create the output tree from the constraints.
	 * @param expression The reference to Expression
	 * @param parentOutputTreeNode The reference to parent output tree node. null if there is no parent.
	 */
	private void completeTree(IExpression expression, OutputTreeDataNode parentOutputTreeNode)
	{
		List<IExpressionId> children = joinGraph.getChildrenList(expression.getExpressionId());
		for(IExpressionId child: children)
		{
			IExpression childExp = constraints.getExpression(child);
			OutputTreeDataNode childNode=parentOutputTreeNode;
			/**
			 * Check whether chid node is in view or not.
			 * if it is in view then create output tree node for it.
			 * else look for their children node & create the output tree heirarchy if required.
			 */
			if (childExp.isInView())
			{
				IOutputEntity childOutputEntity = getOutputEntity(childExp);
				Integer childAliasAppender = aliasAppenderMap.get(child);
			
				/**
				 * Check whether output tree node for expression with the same alias already added or not.
				 * if its not added then need to add it alias in the outputTreeNodeMap 
				 */
				childNode = outputTreeNodeMap.get(childAliasAppender);
				if (childNode==null) 
				{
					if (parentOutputTreeNode==null)
					{
						// New root node for output tree found, so create root node & add it in the rootOutputTreeNodeList.
						childNode =  new OutputTreeDataNode(childOutputEntity,child, treeNo++); 
						rootOutputTreeNodeList.add(childNode);
					}
					else
					{
						childNode = parentOutputTreeNode.addChild(childOutputEntity,child);
					}
					outputTreeNodeMap.put(childAliasAppender, childNode);
				}
			}
			completeTree(childExp, childNode);
		}
	}

	/**
	 * To get the Output Entity for the given Expression.
	 * @param expression The reference to the Expression.
	 * @return The output entity for the Expression.
	 */
	private IOutputEntity getOutputEntity(IExpression expression)
	{
		EntityInterface entity = expression.getConstraintEntity().getDynamicExtensionsEntity();
		IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(entity);
		outputEntity.getSelectedAttributes().addAll(entity.getAttributeCollection());
		return outputEntity;
	}

	
	/**
	 * @return the rootOutputTreeNodeList
	 */
	public List<OutputTreeDataNode> getRootOutputTreeNodeList()
	{
		return rootOutputTreeNodeList;
	}
	
	
}
