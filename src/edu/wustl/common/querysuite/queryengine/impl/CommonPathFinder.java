
package edu.wustl.common.querysuite.queryengine.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.common.path.PathInterface;
import edu.wustl.cab2b.server.path.PathFinder;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;

/**
 * This class is used to find all the possible paths between two entities.
 * @author deepti_shelar
 *
 */
public class CommonPathFinder implements IPathFinder
{
	/**
	 * This method gets all the possible paths between two entities.
	 */
	public Map getAllPossiblePaths(List<EntityInterface> srcEntity, EntityInterface destEntity)
	{
		Map<EntityInterface, List<? extends PathInterface>> pathsMap = null;
		JDBCDAO dao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		Connection connection = null;
		try
		{
			dao.openSession(null);
			connection = dao.getConnection();
			pathsMap = PathFinder.getAllPossiblePaths(srcEntity, destEntity, connection);
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				e.printStackTrace();
			}
		}
		return pathsMap;
	}
}
