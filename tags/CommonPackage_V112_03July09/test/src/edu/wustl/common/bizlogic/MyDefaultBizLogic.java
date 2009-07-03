package edu.wustl.common.bizlogic;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.DAO;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.exception.DAOException;


/**
 * Mock test class for test cases of methods in Bizlogic. 
 * @author ravi_kumar
 *
 */
public class MyDefaultBizLogic extends DefaultBizLogic
{

	/**
	 * This method gets called after populateUIBean method.
	 * Any logic after populating  object uiForm can be included here.
	 * @param domainObj object of type AbstractDomainObject
	 * @param uiForm object of the class which implements IValueObject
	 * @throws BizLogicException BizLogic Exception
	 */
	protected void postPopulateUIBean(AbstractDomainObject domainObj, IValueObject uiForm)
			throws BizLogicException
	{
		try
		{
			MyDAOImpl.throwDaoException();
		}
		catch (DAOException exception)
		{
			throw getBizLogicException(exception, "biz.popdomain.error", "");
		}
	}
	
	protected void setPrivilege(DAO dao, String privilegeName, Class objectType,
			Long[] objectIds, Long userId, String roleId, boolean assignToUser,
			boolean assignOperation) throws BizLogicException
	{
		try
		{
			MyDAOImpl.throwDaoException();
		}
		catch(DAOException exception)
		{
			throw getBizLogicException(exception, "biz.setpriv.error"
					,"Exception in setPrivilege method");
		}
	}
	/**
	 * this method return true if authorized user.
	 * @param dao DAO object.
	 * @param domainObject Domain object.
	 * @param sessionDataBean  SessionDataBean object.
	 * @throws BizLogicException generic BizLogic Exception
	 * @return true if authorized user.
	 */
	public boolean isAuthorized(DAO dao, Object domainObject,
			SessionDataBean sessionDataBean) throws BizLogicException
	{

		return true;
	}
}
