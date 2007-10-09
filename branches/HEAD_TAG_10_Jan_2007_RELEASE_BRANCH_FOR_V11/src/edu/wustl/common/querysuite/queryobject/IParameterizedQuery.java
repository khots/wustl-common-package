/**
 * 
 */
package edu.wustl.common.querysuite.queryobject;

/**
 * @author chetan_patil
 * @created Aug 31, 2007, 1:46:07 PM
 */
public interface IParameterizedQuery extends IQuery {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    boolean addOutputAttribute(IOutputAttribute outputAttribute);

    boolean removeOutputAttribute(IOutputAttribute outputAttribute);

}
