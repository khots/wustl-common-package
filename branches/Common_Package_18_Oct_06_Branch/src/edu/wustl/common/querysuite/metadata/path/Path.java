package edu.wustl.common.querysuite.metadata.path;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;

/**
 * @author Chandrakant Talele
 */
public class Path implements IPath {
    EntityInterface sourceEntity;
    EntityInterface targetEntity;
    List<IAssociation> intermediateAssociations;
    
    /**
     * @param sourceEntity
     * @param targetEntity
     * @param intermediateAssociations
     */
    public Path(EntityInterface sourceEntity, EntityInterface targetEntity, List<IAssociation> intermediateAssociations) {
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.intermediateAssociations = intermediateAssociations;
    }

    public EntityInterface getSourceEntity() {
        return sourceEntity;
    }

    public EntityInterface getTargetEntity() {
        return targetEntity;
    }

    public List<IAssociation> getIntermediateAssociations() {
        return intermediateAssociations;
    }

    /**
     * @see edu.wustl.common.querysuite.metadata.path.IPath#isBidirectional()
     */
    public boolean isBidirectional() {
        for(IAssociation association : intermediateAssociations) {
            if(!association.isBidirectional()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see edu.wustl.common.querysuite.metadata.path.IPath#reverse()
     */
    public IPath reverse() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param intermediateAssociations The intermediateAssociations to set.
     */
    public void setIntermediateAssociations(List<IAssociation> intermediateAssociations) {
        this.intermediateAssociations = intermediateAssociations;
    }

    /**
     * @param sourceEntity The sourceEntity to set.
     */
    public void setSourceEntity(EntityInterface sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    /**
     * @param targetEntity The targetEntity to set.
     */
    public void setTargetEntity(EntityInterface targetEntity) {
        this.targetEntity = targetEntity;
    }
    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("Start Entity : " + sourceEntity.getName());
        buff.append("End Entity : " + targetEntity.getName());
        for(IAssociation association : intermediateAssociations) {
            buff.append("\t" + association.toString());
        }
        return buff.toString();
    }
}
