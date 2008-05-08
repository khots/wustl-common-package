
package edu.wustl.common.querysuite.queryengine.impl;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
 * @author lalit_chand
 *
 */
public class CommonPathFinder implements IPathFinder
{

	private static PathFinder pathFinder = null;

	public CommonPathFinder()
	{
		if (pathFinder == null)
		{
			Connection connection = null;
			try
			{
				InitialContext context = new InitialContext();
				DataSource dataSource = (DataSource) context.lookup(getDataSource());
				connection = dataSource.getConnection();
				pathFinder = PathFinder.getInstance(connection);

			}
			catch (NamingException e)
			{
				Logger.out.error("CommonPathFinder:", e);
				//TODO need to see how to handle exception
			}
			catch (SQLException e)
			{
				Logger.out.error("CommonPathFinder:", e);
				//TODO need to see how to handle exception
			}
			finally
			{
				try
				{
					if (connection != null)
						connection.close();
				}
				catch (SQLException e)
				{
					Logger.out.error("CommonPathFinder:", e);
				}
			}
		}
	}
	
	/*
	 * This method is for testing only. Its is solely meant to be used in CommonPathFinderTest class
	 */
	String getDS() {
		return getDataSource();
	}

	//  Name of the property file
	private String getDataSource()
	{
		String propertyfile = "commonpackage.properties";
		ClassLoader classLoader = CommonPathFinder.class.getClassLoader();
		URL url = classLoader.getResource(propertyfile);
		Properties properties = new Properties();
		try
		{
			properties.load(url.openStream());
		}
		catch (IOException e)
		{
			Logger.out.error((new StringBuilder()).append("Unable to load properties from : ")
					.append(propertyfile).toString());
			e.printStackTrace();
		}
		return (new StringBuilder()).append("java:/").append(
				properties.getProperty("datasource.name")).toString();
	}

	/**
	 * This method gets all the possible paths between two entities.
	 */
	public List<IPath> getAllPossiblePaths(EntityInterface srcEntity, EntityInterface destEntity)
	{
		//PathFinder pathFinder= getPathFinderInstance();
		return pathFinder.getAllPossiblePaths(srcEntity, destEntity);
	}

	public IPath getPathForAssociations(List<IIntraModelAssociation> intraModelAssociationList)
	{
		//PathFinder pathFinder = getPathFinderInstance();
		return pathFinder.getPathForAssociations(intraModelAssociationList);
	}

	public Set<ICuratedPath> autoConnect(Set<EntityInterface> entitySet)
	{
		return pathFinder.autoConnect(entitySet);
	}

	public Set<ICuratedPath> getCuratedPaths(EntityInterface srcEntity, EntityInterface destEntity)
	{
		return pathFinder.getCuratedPaths(srcEntity, destEntity);
	}

	//
	//	public Collection<AssociationInterface> getIncomingIntramodelAssociations(Long arg0)
	//	{
	//		return  pathFinder.getIncomingIntramodelAssociations(arg0);
	//	}

	public List<IInterModelAssociation> getInterModelAssociations(Long arg0)
	{
		return pathFinder.getInterModelAssociations(arg0);
	}
}
