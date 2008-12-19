package edu.wustl.common.bizlogic;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.DAO;


/**
 * Extending this class to 
 * @author ravi_kumar
 *
 */
public class MyDefaultBizLogic extends DefaultBizLogic
{

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
