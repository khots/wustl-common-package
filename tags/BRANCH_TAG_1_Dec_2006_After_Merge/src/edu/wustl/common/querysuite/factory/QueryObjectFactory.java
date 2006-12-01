
package edu.wustl.common.querysuite.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.IntegerAttributeTypeInformation;
import edu.common.dynamicextensions.domain.LongAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.util.global.Constants;
import edu.wustl.common.querysuite.category.ICategory;
import edu.wustl.common.querysuite.queryobject.DataType;
import edu.wustl.common.querysuite.queryobject.IAssociation;
import edu.wustl.common.querysuite.queryobject.IAttribute;
import edu.wustl.common.querysuite.queryobject.IClass;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IFunctionalClass;
import edu.wustl.common.querysuite.queryobject.IInterModelAssociation;
import edu.wustl.common.querysuite.queryobject.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Attribute;
import edu.wustl.common.querysuite.queryobject.impl.Condition;
import edu.wustl.common.querysuite.queryobject.impl.Constraints;
import edu.wustl.common.querysuite.queryobject.impl.ExpressionId;
import edu.wustl.common.querysuite.queryobject.impl.FunctionalClass;
import edu.wustl.common.querysuite.queryobject.impl.InterModelAssociation;
import edu.wustl.common.querysuite.queryobject.impl.IntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;
import edu.wustl.common.querysuite.queryobject.impl.Query;
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

	public static ILogicalConnector createLogicalConnector(LogicalOperator logicalOperator)
	{
		return new LogicalConnector(logicalOperator);
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

	public static IClass createClass()
	{
		return createClass(null, null, null, false);
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
		return new Constraints();
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

	public static IRule createRule(IExpression containingExpression)
	{
		return new Rule(null,containingExpression);
	}

	public static IIntraModelAssociation createIntraModelAssociation(IClass leftClass,
			IClass rightClass, String roleName, String revereseRoleName, boolean bidirectional)
	{
		return new IntraModelAssociation(leftClass, rightClass, roleName, revereseRoleName,
				bidirectional);
	}

	public static IInterModelAssociation createInterModelAssociation(IAttribute sourceAttribute,
			IAttribute targetAttribute)
	{
		return new InterModelAssociation(sourceAttribute, targetAttribute);
	}

	public static IQuery createQuery()
	{
		return new Query();
	}
    
    /**
     * Creates the query Class object from dynamic extension's Entity object.
     * The attributes for the class are set and need not to be set explicitly.
     * @param entity The entity object.
     * @return the Class object from Entity object.
     */
    public static IClass createClass(EntityInterface entity)
    {
        IClass classObj = createClass();
//        classObj.setAttributes(createAttributes((List)entity.getAttributeCollection()));
        classObj.setFullyQualifiedName(entity.getName());
        
        Collection col = entity.getAttributeCollection();
        
        for (Iterator iter = col.iterator(); iter.hasNext();)
		{
        	AttributeInterface element = (AttributeInterface)iter.next();
			IAttribute queryAttribute = createAttributeForClass(element, classObj);
			classObj.addAttribute(queryAttribute);
		}
        return classObj;
    }
    /**
     * Create IAttribute instance for the given Dynamic Extenstion attribute & set its class as QueryClass.
     * @param attribute The dynamic extension Attribute object
     * @param queryClass The reference to IClass in which this attribute is expected to present.
     * @return The reference to IAttribute attribute corresponding to the given dynamic extension Attribute.
     */
    private static IAttribute createAttributeForClass(AttributeInterface attribute, IClass queryClass)
    {
    	IAttribute queryAttribute = createAttribute();
        queryAttribute.setUMLClass(queryClass);

    	// set all expected values for queryAttribute.
        queryAttribute.setAttributeName(attribute.getName());
        queryAttribute.setDataType(getAttributeDataType(attribute.getAttributeTypeInformation()));
        
        return queryAttribute;
    }
    /**
     * To search the Query attribute representing the dynamic extension attribute in the given queryClass.
     * @param attribute The dynamic extension Attribute object
     * @param queryClass The reference to IClass in which this attribute is expected to present.
     * @return The reference to IAttribute attribute corresponding to the given dynamic extension Attribute.
     * @thorws NoSuchElementException if the attribute is does not belongs to the given queryClass. 
     */
    public static IAttribute getAttribute(AttributeInterface attribute, IClass queryClass)
    {
    	IAttribute queryAttribute = null;
    	List<IAttribute> tempQueryAttributes = queryClass.getAttributes();
    	String attributeName =  attribute.getName();
    	DataType dataType = getAttributeDataType(attribute.getAttributeTypeInformation());
    	for (int index = 0; index < tempQueryAttributes.size(); index++)
		{
    		queryAttribute = tempQueryAttributes.get(index);
    		if (queryAttribute.getAttributeName().equals(attributeName) && queryAttribute.getDataType().equals(dataType))
    			return queryAttribute;
		}
    	throw new NoSuchElementException(attribute + " Not Exists in the Class: "+ queryClass.getFullyQualifiedName());
    }
    /**
     * Creates & returns the list of query Attributes from the dynamic extension Attribute objects.  
     * @param attributes The dynamic extension Attribute objects.
     * @return the list of query Attributes from the dynamic extension Attribute objects.
     */
    public static List<IAttribute> createAttributes(List<AttributeInterface> attributes)
    {
        List<IAttribute> queryAttributes = new ArrayList<IAttribute>();
        
        if (!attributes.isEmpty())
        {
        	AttributeInterface attribute = attributes.get(0);
        	IClass iclass = createClass(attribute.getEntity()); //this wil create IClass for the given attributes.
        	for (int index = 0; index < attributes.size(); index++) // search the Query attribute corresponding to the dynamic Extension attribute from the IClass attribute list.
			{
        		attribute = attributes.get(index);
                IAttribute queryAttribute = getAttribute(attribute, iclass);
                queryAttributes.add(queryAttribute);
			}
        }
        return queryAttributes;
    }
    
    /**
     * Creates & returns the query Attribute object from the dynamic extension Attribute object.  
     * @param attribute The dybnamic extension object.
     * @return the query Attribute object from the dynamic extension Attribute object.
     */
    public static IAttribute createAttribute(AttributeInterface attribute)
    {
    	 IClass queryClass = createClass(attribute.getEntity());
    	 return getAttribute(attribute, queryClass);
    }
    
    /**
     * To instanciate object of a class extending IIntraModelAssociation interface.  
     * @param association
     * @return
     */
    public static IAssociation createAssociation(AssociationInterface association)
    {
    	IClass leftClass = createClass(association.getEntity());
    	IClass rightClass = createClass(association.getTargetEntity());
    	String roleName = association.getSourceRole().getName();
    	String revereseRoleName = association.getTargetRole().getName();
    	boolean bidirectional = association.getAssociationDirection().equals(Constants.AssociationDirection.BI_DIRECTIONAL);
    	
    	IAssociation queryAssociation = QueryObjectFactory.createIntraModelAssociation(leftClass, rightClass, roleName, revereseRoleName, bidirectional);
    	return queryAssociation;
    }
    /**
     * Returns the datatype of attribute depending on the AttributeTypeInformation of the attribute. 
     * @param attributeTypeInformation The attribute type information.
     * @return the datatype of attribute depending on the AttributeTypeInformation of the attribute.
     */
    private static DataType getAttributeDataType(AttributeTypeInformationInterface attributeTypeInformation)
    {
        DataType dataType = DataType.String;
        if (attributeTypeInformation instanceof LongAttributeTypeInformation)
        {
            dataType = DataType.Long;
        }
        else if (attributeTypeInformation instanceof DateAttributeTypeInformation)
        {
            dataType = DataType.Date;
        }
        else if (attributeTypeInformation instanceof BooleanAttributeTypeInformation)
        {
            dataType = DataType.Boolean;
        }
        else if (attributeTypeInformation instanceof DoubleAttributeTypeInformation)
        {
            dataType = DataType.Double;
        }
        else if (attributeTypeInformation instanceof IntegerAttributeTypeInformation)
        {
            dataType = DataType.Integer;
        }
        
        return dataType;
    }
}