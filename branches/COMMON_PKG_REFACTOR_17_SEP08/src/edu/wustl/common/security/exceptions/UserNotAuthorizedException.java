package edu.wustl.common.security.exceptions;

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */
public class UserNotAuthorizedException extends SMException
{

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8600518692061076696L;

	private String privilegeName;

	private String baseObject;

	private String baseObjIdentifier;

	/**
	 * @return Returns the baseObject.
	 */
	public String getBaseObject()
	{
		return baseObject;
	}

	/**
	 * @param baseObject The baseObject to set.
	 */
	public void setBaseObject(String baseObject)
	{
		this.baseObject = baseObject;
	}

	/**
	 * @return Returns the baseObjectIdentifier.
	 */
	public String getBaseObjectIdentifier()
	{
		return baseObjIdentifier;
	}

	/**
	 * @param baseObjIdentifier The baseObjectIdentifier to set.
	 */
	public void setBaseObjectIdentifier(String baseObjIdentifier)
	{
		this.baseObjIdentifier = baseObjIdentifier;
	}

	/**
	 * @return Returns the privilegeName.
	 */
	public String getPrivilegeName()
	{
		return privilegeName;
	}

	/**
	 * @param privilegeName The privilegeName to set.
	 */
	public void setPrivilegeName(String privilegeName)
	{
		this.privilegeName = privilegeName;
	}

	public UserNotAuthorizedException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public UserNotAuthorizedException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UserNotAuthorizedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public UserNotAuthorizedException(Throwable cause)
	{
		super(cause);
	}
}
