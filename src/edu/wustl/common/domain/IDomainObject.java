package edu.wustl.common.domain;

import java.io.Serializable;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.exception.AssignDataException;


public interface IDomainObject extends Serializable
{

	public String getObjectId();
	
    /**
     * Copies all values from the AbstractForm object
     * @param abstractForm The AbstractForm object
     */
    public void setAllValues(IValueObject  valueObject) throws AssignDataException;
    
    /**
	 * Returns the unique system identifier assigned to the domain object.
     * @return returns a unique system identifier assigned to the domain object.
     * @see #setId(Long)
	 * */
    public Long getId();

    /**
	 * Sets an system Identifier for the domain object.
	 * @param system Identifier system Identifier for the domain object.
	 * @see #getId()
	 * */
    public void setId(Long id);    
    
    /**
     * Returns message label to display on success add or edit
     * @return String
     */
    public String getMessageLabel();
}
