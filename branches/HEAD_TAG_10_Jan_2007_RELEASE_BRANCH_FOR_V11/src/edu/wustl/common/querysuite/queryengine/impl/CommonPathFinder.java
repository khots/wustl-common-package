
package edu.wustl.common.querysuite.queryengine.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.server.path.PathFinder;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.IntraModelAssociation;
import edu.wustl.common.querysuite.metadata.path.ICuratedPath;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;

/**
 * This class is used to find all the possible paths between two entities.
 * @author deepti_shelar
 *
 */
public class CommonPathFinder implements IPathFinder
{
	private PathFinder pathFinder=null;
	/**
	 * This method gets all the possible paths between two entities.
	 */
	public List<IPath> getAllPossiblePaths(EntityInterface srcEntity, EntityInterface destEntity)
	{
		 List<IPath> pathsMap = null;
		JDBCDAO dao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		Connection connection = null;
		try
		{
			dao.openSession(null);
			InitialContext context = new InitialContext();
 			DataSource dataSource =  (DataSource) context.lookup("java:/catissuecore");
 			connection = dataSource.getConnection();
 			pathFinder = (PathFinder) PathFinder.getInstance(connection);
			pathsMap = pathFinder.getAllPossiblePaths(srcEntity, destEntity);
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
		catch (NamingException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				dao.closeSession();
				connection.close();
			}
			catch (DAOException e)
			{
				e.printStackTrace();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return pathsMap;
	}

	public IPath getPathForAssociations(List<IIntraModelAssociation> intraModelAssociationList) {
		IPath path = pathFinder.getPathForAssociations(intraModelAssociationList);
		return path;
	}
	public Set<ICuratedPath> autoConnect(Set<EntityInterface> arg0)
	{
		return new HashSet<ICuratedPath>();
	}


	public Set<ICuratedPath> getCuratedPaths(EntityInterface arg0, EntityInterface arg1)
	{
		return new HashSet<ICuratedPath>();
	}


	public Collection<AssociationInterface> getIncomingIntramodelAssociations(Long arg0)
	{
		return new Vector<AssociationInterface>();
	}


	public List<IInterModelAssociation> getInterModelAssociations(Long arg0)
	{
		return new Vector<IInterModelAssociation>();
	}

	
}
