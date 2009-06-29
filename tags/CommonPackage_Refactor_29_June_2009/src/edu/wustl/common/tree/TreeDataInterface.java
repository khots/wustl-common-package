/*
 * Created on Jul 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.tree;

import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.exception.DAOException;

/**
 * @author gautam_shetty
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface TreeDataInterface
{

	/**
	 * gets Tree View Data.
	 * @return List.
	 * @throws DAOException generic DAO Exception.
	 */
	List getTreeViewData() throws DAOException;

	/**
	 * gets Tree View Data.
	 * @param sessionData session Data
	 * @param map map
	 * @param list list
	 * @return list
	 * @throws DAOException generic DAO Exception.
	 * @throws ClassNotFoundException Class Not Found Exception.
	 */
	List getTreeViewData(SessionDataBean sessionData, Map map, List list) throws DAOException,
			ClassNotFoundException;
}
