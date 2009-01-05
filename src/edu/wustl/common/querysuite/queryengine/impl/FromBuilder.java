package edu.wustl.common.querysuite.queryengine.impl;

import static edu.wustl.common.querysuite.queryengine.impl.SqlKeyWords.FROM;
import static edu.wustl.common.querysuite.queryengine.impl.SqlKeyWords.INNER_JOIN;
import static edu.wustl.common.querysuite.queryengine.impl.SqlKeyWords.LEFT_JOIN;
import static edu.wustl.common.querysuite.queryengine.impl.SqlKeyWords.ON;
import static edu.wustl.common.querysuite.queryengine.impl.SqlKeyWords.SELECT;
import static edu.wustl.common.querysuite.queryengine.impl.SqlKeyWords.WHERE;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.util.global.Constants.InheritanceStrategy;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.util.InheritanceUtils;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;

/*
 * Note to human debugger: If an error occurs similar to "column foo_bar is
 * ambiguous" while firing a generated SQL, this is because the column "foo_bar"
 * is present in both the child and parent tables in a TABLE_PER_SUBCLASS
 * hierarchy. See the TODO on the class TblPerSubClass below for the cause and
 * fix.
 */

public class FromBuilder {

    private IJoinGraph joinGraph;

    private IExpression root;

    private static final int ALIAS_NAME_LENGTH = 25;

    private final String fromClause;

    public FromBuilder(IJoinGraph joinGraph) {
        if (joinGraph == null) {
            this.fromClause = "";
            return;
        }
        this.joinGraph = joinGraph;
        try {
            root = joinGraph.getRoot();
        } catch (MultipleRootsException e) {
            throw new IllegalArgumentException(e);
        }
        this.fromClause = buildFrom();
    }

    /**
     * To get the Alias Name for the given IExpression. It will return alias
     * name for the DE entity associated with constraint entity.
     * 
     * @param expression The reference to IExpression.
     * @return The Alias Name for the given Entity.
     */
    String aliasOf(IExpression expr) {
        String entName = entity(expr).getName();
        String className = entName.substring(entName.lastIndexOf('.') + 1, entName.length());
        return alias(className, expr);
    }

    private String alias(String s, IExpression expr) {
        s = Utility.removeSpecialCharactersFromString(s);
        if (s.length() > ALIAS_NAME_LENGTH) {
            s = s.substring(0, ALIAS_NAME_LENGTH);
        }
        return s + "_" + expr.getExpressionId();
    }

    String aliasOf(AttributeInterface attr, IExpression expr) {
        return aliasOf(expr) + "." + columnName(origAttr(attr));
    }

    String fromClause() {
        return fromClause;
    }

    private String buildFrom() {
        StringBuilder res = new StringBuilder();
        res.append(FROM);
        res.append(getExprSrc(root));

        Set<IExpression> currExprs = new HashSet<IExpression>();
        currExprs.addAll(children(root));

        while (!currExprs.isEmpty()) {
            Set<IExpression> nextExprs = new HashSet<IExpression>();
            for (IExpression currExpr : currExprs) {
                nextExprs.addAll(children(currExpr));

                String src = getExprSrc(currExpr);
                String joinConds = getJoinConds(currExpr);
                res.append(oneExprStr(src, joinConds));
            }
            currExprs = nextExprs;
        }

        return res.toString();
    }

    private String oneExprStr(String src, String joinConds) {
        return LEFT_JOIN + src + ON + "(" + joinConds + ")";
    }

    private String getJoinConds(IExpression expr) {
        StringBuilder res = new StringBuilder();
        String and = " and ";
        for (IExpression parent : parents(expr)) {
            res.append(joinCond(parent, expr));
            res.append(and);
        }

        return removeLastOccur(res.toString(), and);
    }

    private String joinCond(IExpression leftExpr, IExpression rightExpr) {
        AssociationInterface assoc = getAssociation(leftExpr, rightExpr);
        EntityInterface src = assoc.getEntity();
        EntityInterface tgt = assoc.getTargetEntity();

        ConstraintPropertiesInterface assocProps = assoc.getConstraintProperties();
        String leftAttr = assocProps.getSourceEntityKey();
        String rightAttr = assocProps.getTargetEntityKey();

        if (leftAttr != null && rightAttr != null) {
            // tricky choice of PK.
            return equate(middleTabAlias(assoc, rightExpr), assocProps.getTargetEntityKey(), aliasOf(rightExpr),
                    primaryKey(entity(rightExpr)));
        }
        if (leftAttr == null) {
            leftAttr = primaryKey(src);
        }
        if (rightAttr == null) {
            rightAttr = primaryKey(tgt);
        }

        return equate(aliasOf(leftExpr), leftAttr, aliasOf(rightExpr), rightAttr);
    }

    private String equate(String leftTab, String leftCol, String rightTab, String rightCol) {
        return leftTab + "." + leftCol + "=" + rightTab + "." + rightCol;
    }

    private AssociationInterface getAssociation(IExpression leftExpr, IExpression rightExpr) {
        IIntraModelAssociation iAssociation = (IIntraModelAssociation) joinGraph.getAssociation(leftExpr, rightExpr);
        AssociationInterface assoc = iAssociation.getDynamicExtensionsAssociation();
        return InheritanceUtils.getInstance().getActualAassociation(assoc);
    }

    private List<IExpression> children(IExpression expr) {
        return joinGraph.getChildrenList(expr);
    }

    private List<IExpression> parents(IExpression expr) {
        return joinGraph.getParentList(expr);
    }

    private String middleTabAlias(AssociationInterface assoc, IExpression rightExpr) {
        return alias(middleTabName(assoc), rightExpr);
    }

    private String getExprSrc(IExpression expr) {
        EntityInterface entity = entity(expr);

        SrcStringProvider srcStringProvider;
        if (isDerived(entity)) {
            srcStringProvider = srcStringProvider(inheritanceStrategy(entity));
        } else {
            srcStringProvider = new DefaultSrcProvider();
        }
        String src = srcStringProvider.srcString(expr) + " " + aliasOf(expr);
        if (expr == root) {
            return src;
        }
        // many-many ??
        String res = "";
        for (IExpression parent : parents(expr)) {
            AssociationInterface assoc = getAssociation(parent, expr);
            if (manyToMany(assoc)) {
                ConstraintPropertiesInterface c = assoc.getConstraintProperties();
                String middleTabAlias = middleTabAlias(assoc, expr);
                String joinCond = equate(aliasOf(parent), primaryKey(entity(parent)), middleTabAlias, c
                        .getSourceEntityKey());

                res += c.getName() + " " + middleTabAlias + ON + joinCond;
            }
        }
        if (res.equals("")) {
            return src;
        }
        return res + LEFT_JOIN + src;
    }

    private boolean manyToMany(AssociationInterface assoc) {
        ConstraintPropertiesInterface c = assoc.getConstraintProperties();
        return c.getSourceEntityKey() != null && c.getTargetEntityKey() != null;
    }

    private SrcStringProvider srcStringProvider(InheritanceStrategy strategy) {
        switch (strategy) {
            case TABLE_PER_CONCRETE_CLASS :
                return new TblPerConcreteClass();
            case TABLE_PER_HEIRARCHY :
                return new TblPerHier();
            case TABLE_PER_SUB_CLASS :
                return new TblPerSubClass();
            default :
                throw new RuntimeException("Unknown inheritance strategy.");
        }
    }

    private interface SrcStringProvider {
        String srcString(IExpression expression);
    }

    private static class DefaultSrcProvider implements SrcStringProvider {

        public String srcString(IExpression expression) {
            EntityInterface entity = entity(expression);
            AttributeInterface actAttr = activityStatus(entity);
            if (actAttr == null) {
                return tableName(entity);
            }
            return "(" + SELECT + "*" + FROM + tableName(entity) + WHERE + activeCond(columnName(actAttr)) + ")";
        }

    }

    private static class TblPerConcreteClass extends DefaultSrcProvider {
    }

    private static class TblPerHier implements SrcStringProvider {

        public String srcString(IExpression expression) {
            EntityInterface entity = entity(expression);

            String table = tableName(root(entity));
            return "(" + SELECT + "*" + FROM + table + WHERE + conds(entity) + ")";
        }

        private String conds(EntityInterface entity) {
            String res = discriminator(entity);
            AttributeInterface actAttr = activityStatus(entity);
            if (actAttr == null) {
                return res;
            }
            res += " and " + activeCond(columnName(actAttr));
            return res;
        }

        private static String discriminator(EntityInterface entity) {
            String columnName = entity.getDiscriminatorColumn();
            String columnValue = entity.getDiscriminatorValue();
            // Assuming Discrimanator is of type String.
            String condition = columnName + "='" + columnValue + "'";
            return condition;
        }
    }

    private class TblPerSubClass implements SrcStringProvider {

        /*
         * TODO assumes a rosy practical database design where (1). SAME KEY for
         * whole hier. this is theoretically FLAWED; a subclass may have a
         * different name for the key column than the superclass. But this isn't
         * supported by DE. (2). No clashes of column names across attributes in
         * the hier; this makes the code easy. If/when a bug occurs when a
         * parent and child tables have same column name (for different
         * attributes), this is the place to fix the bug. FIX: generate unique
         * aliases for the columns in this SELECT clause.
         */
        public String srcString(IExpression expression) {
            EntityInterface child = entity(expression);
            return "(" + selectClause(expression) + fromClause(child) + whereClause(child) + ")";
        }

        private String selectClause(IExpression expr) {
            EntityInterface entity = entity(expr);
            StringBuilder res = new StringBuilder();
            res.append(SELECT);

            final String comma = ", ";
            for (AttributeInterface attr : attributes(entity)) {
                res.append(qualifiedColName(attr));
                res.append(comma);
            }
            for (IExpression parent : parents(expr)) {
                AssociationInterface assoc = getAssociation(parent, expr);
                String fk = assoc.getConstraintProperties().getTargetEntityKey();
                if (fk != null && !manyToMany(assoc)) {
                    fk = tableName(assoc.getTargetEntity()) + "." + fk;
                    res.append(fk);
                    res.append(comma);
                }
            }
            for (IExpression child : children(expr)) {
                AssociationInterface assoc = getAssociation(expr, child);
                String fk = assoc.getConstraintProperties().getSourceEntityKey();
                if (fk != null && !manyToMany(assoc)) {
                    fk = tableName(assoc.getEntity()) + "." + fk;
                    res.append(fk);
                    res.append(comma);
                }
            }
            return removeLastOccur(res.toString(), comma);
        }

        private String fromClause(EntityInterface child) {
            StringBuilder res = new StringBuilder();
            res.append(FROM);
            res.append(tableName(child));
            EntityInterface parent = child.getParentEntity();
            while (parent != null) {
                res.append(INNER_JOIN);
                res.append(tableName(parent));
                res.append(" on ");
                res.append(joinWithParent(child));

                child = parent;
                parent = parent.getParentEntity();
            }

            return res.toString();
        }

        private String joinWithParent(EntityInterface entity) {
            return key(entity) + "=" + key(entity.getParentEntity());
        }

        private String key(EntityInterface entity) {
            return tableName(entity) + "." + primaryKey(entity);
        }

        private String qualifiedColName(AttributeInterface attr) {
            return tableName(origAttr(attr).getEntity()) + "." + columnName(attr);
        }

        private String whereClause(EntityInterface child) {
            AttributeInterface actAttr = activityStatus(child);
            if (actAttr == null) {
                return "";
            }
            return WHERE + activeCond(qualifiedColName(actAttr));
        }
    }

    // entity properties
    private static EntityInterface root(EntityInterface entity) {
        // old code also checked inheritance strategy here; not needed if
        // strategies aren't mixed.
        while (entity.getParentEntity() != null) {
            entity = entity.getParentEntity();
        }
        return entity;
    }

    private static EntityInterface entity(IExpression expr) {
        return expr.getQueryEntity().getDynamicExtensionsEntity();
    }

    private static Collection<AttributeInterface> attributes(EntityInterface entity) {
        return entity.getEntityAttributesForQuery();
    }

    private static InheritanceStrategy inheritanceStrategy(EntityInterface entity) {
        return entity.getInheritanceStrategy();
    }

    private static boolean isDerived(EntityInterface entity) {
        return entity.getParentEntity() != null;
    }

    private static String tableName(EntityInterface entity) {
        return entity.getTableProperties().getName();
    }

    private static String columnName(AttributeInterface attr) {
        return origAttr(attr).getColumnProperties().getName();
    }

    private static String middleTabName(AssociationInterface assoc) {
        return assoc.getConstraintProperties().getName();
    }

    private static AttributeInterface origAttr(AttributeInterface attr) {
        return InheritanceUtils.getInstance().getActualAttribute(attr);
    }

    /**
     * To get the primary key attribute of the given entity.
     * 
     * @param entity the DE entity.
     * @return The Primary key attribute of the given entity.
     * @throws SqlException If there is no such attribute present in the
     *             attribute list of the entity.
     */
    private static String primaryKey(EntityInterface entity) {
        Collection<AttributeInterface> attributes = attributes(entity);
        for (AttributeInterface attribute : attributes) {
            if (attribute.getIsPrimaryKey() || attribute.getName().equals("id")) {
                return columnName(attribute);
            }
        }
        EntityInterface parentEntity = entity.getParentEntity();
        if (parentEntity != null) {
            return primaryKey(parentEntity);
        }

        throw new RuntimeException("No Primary key attribute found for Entity:" + entity.getName());
    }

    /**
     * Check for activity status present in entity.
     * 
     * @param entityInterfaceObj The Entity for which we required to check if
     *            activity status present.
     * @return Reference to the AttributeInterface if activityStatus attribute
     *         present in the entity, else null.
     */
    private static AttributeInterface activityStatus(EntityInterface entity) {
        for (AttributeInterface attribute : attributes(entity)) {
            if (attribute.getName().equals(Constants.ACTIVITY_STATUS)) {
                return attribute;
            }
        }
        return null;
    }

    private static String activeCond(String attr) {
        return attr + " != '" + Constants.ACTIVITY_STATUS_DISABLED + "'";
    }

    private static String removeLastOccur(String src, String toRemove) {
        if (src.endsWith(toRemove)) {
            src = src.substring(0, src.length() - toRemove.length());
        }
        return src;
    }
}
