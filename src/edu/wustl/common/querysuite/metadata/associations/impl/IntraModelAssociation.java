package edu.wustl.common.querysuite.metadata.associations.impl;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.util.global.Constants.AssociationDirection;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;

/**
 * @author Chandrakant Talele
 * @version 1.0
 * @created 25-Dec-2006 1:22:01 PM
 */
public class IntraModelAssociation implements IIntraModelAssociation {

    private static final long serialVersionUID = 1477526671383920408L;

    private AssociationInterface dynamicExtensionsAssociation;
    /**
     * The only public constructor.
     * @param dynamicExtensionsAssociation
     */
    public IntraModelAssociation(AssociationInterface dynamicExtensionsAssociation) {
        this.dynamicExtensionsAssociation = dynamicExtensionsAssociation;
    }
    /**
     * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#getSourceEntity()
     */
    public EntityInterface getSourceEntity() {
        return dynamicExtensionsAssociation.getEntity();
    }

    /**
     * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#getTargetEntity()
     */
    public EntityInterface getTargetEntity() {
        return dynamicExtensionsAssociation.getTargetEntity();
    }

    /**
     * @see edu.wustl.common.querysuite.metadata.associations.IAssociation#isBidirectional()
     */
    public boolean isBidirectional() {
        return (dynamicExtensionsAssociation.getAssociationDirection() == AssociationDirection.BI_DIRECTIONAL);
    }
    /**
     * @see edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation#getDynamicExtensionsAssociation()
     */
    public AssociationInterface getDynamicExtensionsAssociation() {
        return dynamicExtensionsAssociation;
    }
    @Override
    public String toString() {
        if(dynamicExtensionsAssociation!=null)
        return "Dynamic Extension Association Id : " + dynamicExtensionsAssociation.getId();
        return "";
    }
}