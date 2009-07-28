package edu.wustl.common.domain;

/**
 * @hibernate.class table="LOGIN_EVENT"
 **/
public class LoginEvent extends AuditEvent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7085963264585176596L;
		
	/**
     * User's login id
     */
	protected String userLoginId;
	
	/**
     * User's source or domain he belongs to
     */
	protected Long sourceId;
	
	/**
	 * Was login attempt successful
	 */
	protected boolean isLoginSuccessful;
	
	/**
     * Returns user's login Status
     * @return user's login Status
     * @hibernate.property name="isLoginSuccessful" type="boolean" 
     * column="LOGIN_SUCCESS"
     */
	public boolean getIsLoginSuccessful()
	{
		return isLoginSuccessful;
	}
	
	public void setIsLoginSuccessful(boolean loginSuccessful)
	{
		this.isLoginSuccessful = loginSuccessful;
	}
	
	/**
     * Returns user's login id 
     * @return user's login id
     * @hibernate.property name="userLoginId" type="string" 
     * column="USER_LOGIN_ID" length="50"
     */
	public String getUserLoginId()
	{
		return userLoginId;
	}
	
	public void setUserLoginId(String userLoginId)
	{
		this.userLoginId = userLoginId;
	}
	
	/**
     * @hibernate.property name="sourceId" type="long" column="SOURCE_ID"
     * @return Returns the institutionId.
     */
    public Long getSourceId()
    {
        return sourceId;
    }
    
    /**
     * @param userId The userId to set.
     */
    public void setSourceId(Long sourceId)
    {
    	this.sourceId=sourceId;
    }
}
