
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
import edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.metadata.path.ICuratedPath;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.util.logger.Logger;

/**
 * This class is used to find all the possible paths between two entities.
 * @author deepti_shelar
 *
 */
public class CommonPathFinder implements IPathFinder
{
	
	private PathFinder getPathFinderInstance()
	{
		PathFinder pathFinder=null;
		Connection connection = null;
		try
		{
			InitialContext context = new InitialContext();
 			DataSource dataSource =  (DataSource) context.lookup("java:/catissuecore");
 			connection = dataSource.getConnection();
 			pathFinder = PathFinder.getInstance(connection);
		}
		catch (NamingException e)
		{
			Logger.out.error("CommonPathFinder:",e);
		}
		catch (SQLException e)
		{
			Logger.out.error("CommonPathFinder:",e);
		}
		finally
		{
			try
			{
				if (connection!=null)
					connection.close();
			}
			catch (SQLException e)
			{
				Logger.out.error("CommonPathFinder:",e);
			}
		}
		return pathFinder;
	}
	
	/**
	 * This method gets all the possible paths between two entities.
	 */
	public List<IPath> getAllPossiblePaths(EntityInterface srcEntity, EntityInterface destEntity)
	{
		 PathFinder pathFinder= getPathFinderInstance();
		 return pathFinder.getAllPossiblePaths(srcEntity, destEntity);
	}

	public IPath getPathForAssociations(List<IIntraModelAssociation> intraModelAssociationList) 
	{
		PathFinder pathFinder = getPathFinderInstance();
		return  pathFinder.getPathForAssociations(intraModelAssociationList);
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
