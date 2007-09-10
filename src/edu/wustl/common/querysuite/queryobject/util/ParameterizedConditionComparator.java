/**
 * 
 */
package edu.wustl.common.querysuite.queryobject.util;

import java.util.Comparator;

import edu.wustl.common.querysuite.queryobject.IParameterizedCondition;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedCondition;

/**
 * @author chetan_patil
 * @created Sep 10, 2007, 12:55:33 PM
 */
public class ParameterizedConditionComparator implements Comparator<IParameterizedCondition> {

    public int compare(IParameterizedCondition parameterizedCondition1,
                       IParameterizedCondition parameterizedCondition2) {
        return ((ParameterizedCondition) parameterizedCondition1).getIndex()
                - ((ParameterizedCondition) parameterizedCondition2).getIndex();
    }

}
