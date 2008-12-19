package edu.wustl.common.bizlogic;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.DAO;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.exception.DAOException;


/**
 * Extending this class to 
 * @author ravi_kumar
 *
 */
public class MyDefaultBizLogic extends DefaultBizLogic
{

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
