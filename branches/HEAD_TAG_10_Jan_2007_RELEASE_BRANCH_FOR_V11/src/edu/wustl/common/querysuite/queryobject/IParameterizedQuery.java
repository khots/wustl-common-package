/**
 * 
 */
package edu.wustl.common.querysuite.queryobject;

import java.util.List;

/**
 * @author chetan_patil
 * @created Aug 31, 2007, 1:46:07 PM
 */
public interface IParameterizedQuery extends IQuery {
    List<IParameterizedCondition> getParameterizedConditions();

    void addParameterizedCondition(IParameterizedCondition parameterizedCondition);
     
    boolean removeParameterizedCondition(IParameterizedCondition parameterizedCondition);
    
    boolean removeParameterizedCondition(int index);
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    void setDescription(String description);
    
}
