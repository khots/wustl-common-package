/**
 * 
 */
package edu.wustl.common.querysuite.security.utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.wustl.common.beans.QueryResultObjectDataBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querysuite.security.PrivilegeType;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.util.Permissions;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;


/**
 * @author supriya_dankh
 * This class is a cache manager class for CsmCache.This class will add CP and privileges related to it in CsmCache 
 * for Read as well as Identified Data Access.Also it filters results by using checkPermision() method of SecurityManager. 
 */
public class CsmCacheManager
{
	private Connection connection;
	
	public CsmCacheManager(Connection connection)
	{
		this.connection = connection;
	}
	
	
	public CsmCache getNewCsmCacheObject()
	{
		CsmCache csmCache = new CsmCache();

		return csmCache;
	}
	
	/**
	 * This method checks user's permissions (Read, Identified data access) for every entity in query result 
	 * and filters result accordingly.
	 * @param sessionDataBean 
	 * @param queryResultObjectDataMap
	 * @param aList
	 * @param cache
	 * @return isAuthorized user or not.
	 */
	public void filterRow(SessionDataBean sessionDataBean,
			Map<String,QueryResultObjectDataBean> queryResultObjectDataMap, List aList ,CsmCache cache)
	{     
		Boolean isAuthorisedUser = true;
		Boolean hasPrivilegeOnIdentifiedData = true;
		if (queryResultObjectDataMap != null)
		{
			Set keySet = queryResultObjectDataMap.keySet();

			for (Object key : keySet)
			{
				QueryResultObjectDataBean queryResultObjectDataBean = queryResultObjectDataMap
						.get(key);
				String entityName = getEntityName(queryResultObjectDataBean);
				int mainEntityId = -1;
				if(queryResultObjectDataBean
						.getMainEntityIdentifierColumnId()!=-1)
				{
					mainEntityId = Integer.parseInt((String)aList.get(queryResultObjectDataBean
						.getMainEntityIdentifierColumnId()));
				}
				
				List<Boolean> readPrivilegeList = new ArrayList<Boolean>();
				List<Boolean> IdentifiedPrivilegeList = new ArrayList<Boolean>();

				//Check if user has read privilege on perticular object or not.
				if ((mainEntityId != -1) && (queryResultObjectDataBean.isReadDeniedObject()))
				{
					List<List<String>> cpIdsList = getCpIdsListForGivenEntityId(sessionDataBean,
							entityName, mainEntityId);

					for (int i = 0; i < cpIdsList.size(); i++)
					{
						List<String> cpIdList = cpIdsList.get(i);
						Long cpId = cpIdList.get(0) != null ? Long.parseLong(cpIdList.get(0)) : -1;
						entityName = Constants.CP_CLASS_NAME;
						queryResultObjectDataBean.setPrivilegeType(PrivilegeType.ObjectLevel);

						isAuthorisedUser = checkReadDenied(sessionDataBean, cache,
								queryResultObjectDataBean, entityName, cpId);
						
						readPrivilegeList.add(isAuthorisedUser);
 
						//If user is authorized to read data then check for identified data access.
						if (isAuthorisedUser
								&& queryResultObjectDataBean.getIdentifiedDataColumnIds().size()!=0)
						{
							hasPrivilegeOnIdentifiedData = checkIdentifiedDataAccess(
									sessionDataBean, cache, queryResultObjectDataBean, entityName,
									cpId);
							
							IdentifiedPrivilegeList.add(hasPrivilegeOnIdentifiedData);
						}

					}

				}
				isAuthorisedUser = isAuthorizedUser(readPrivilegeList);
				hasPrivilegeOnIdentifiedData = isAuthorizedUser(IdentifiedPrivilegeList);
				
				//If user is not authorized to read the data then remove all data relaeted to this perticular from row.
				removeUnauthorizedData(aList, isAuthorisedUser, hasPrivilegeOnIdentifiedData,
						queryResultObjectDataBean);
			}
		}

	}

	/**
	 * If a object say participant-1 is registered to CP-1, CP-2 this method will check what are privileges of an 
	 * user on both CPs and,will return isAuthoried false if user is not having privilege on any one CP else true.
	 * @param PrivilegeList List of privileges that a object id is having for every CP to which this object is registered. 
	 * @return isAuthorized user or not.
	 */
	private Boolean isAuthorizedUser(List<Boolean> PrivilegeList)
	{
		for (int i = 0; i < PrivilegeList.size(); i++)
		{
			Boolean isAuthorized = PrivilegeList.get(i);
			if(!(isAuthorized))
				return isAuthorized;
		}
		return true;
	}

	/**
	 * This method will internally call removeUnauthorizedFieldsData depending on the value of isAuthorisedUser 
	 * and hasPrivilegeOnIdentifiedData.
	 * @param aList
	 * @param isAuthorisedUser
	 * @param hasPrivilegeOnIdentifiedData
	 * @param queryResultObjectDataBean
	 */
	private void removeUnauthorizedData(List aList, Boolean isAuthorisedUser,
			Boolean hasPrivilegeOnIdentifiedData,
			QueryResultObjectDataBean queryResultObjectDataBean)
	{  
		if (!isAuthorisedUser)
		{
			removeUnauthorizedFieldsData(aList, queryResultObjectDataBean, false);
		}
		else
		{
			//If user is not authorized to see identified data then replace identified column values by ##
			if (!hasPrivilegeOnIdentifiedData)
			{
				removeUnauthorizedFieldsData(aList, queryResultObjectDataBean, true);
			}
		}
	}

	/**
	 * Check if user is having Identified data access on a object id passed to method.And update cache accordingly.
	 * @param sessionDataBean A data bean that contains information related to user logged in. 
	 * @param cache cache object that maintains information related to permissions of user on every CP object.  
	 * @param queryResultObjectDataBean A metadata object required for CSM filtering.
	 * @param entityName Name of entity for which identified data access is to be checked.
	 * @param cpId CP id 
	 * @return
	 */
	private Boolean checkIdentifiedDataAccess(SessionDataBean sessionDataBean, CsmCache cache,
			QueryResultObjectDataBean queryResultObjectDataBean, String entityName,
			Long entityId)
	{ 
		Boolean hasPrivilegeOnIdentifiedData;
		if (cache.isIdentifedDataAccess(entityId) == null)
		{
			hasPrivilegeOnIdentifiedData = checkPermission(sessionDataBean,
					entityName, entityId,
					Permissions.IDENTIFIED_DATA_ACCESS,
					queryResultObjectDataBean.getPrivilegeType());

			cache.addNewObjectInIdentifiedDataAccsessMap(entityId,
					hasPrivilegeOnIdentifiedData);
			System.out.println("Identified");
		}
		else
			hasPrivilegeOnIdentifiedData = cache.isIdentifedDataAccess(entityId);
		return hasPrivilegeOnIdentifiedData;
	}

	/**
	 * Check if user is having Read privilege on a object id passed to method.And update cache accordingly.
	 * @param sessionDataBean
	 * @param cache
	 * @param queryResultObjectDataBean
	 * @param entityName
	 * @param cpId
	 * @return
	 */
	private Boolean checkReadDenied(SessionDataBean sessionDataBean, CsmCache cache,
			QueryResultObjectDataBean queryResultObjectDataBean, String entityName, Long cpId)
	{
		Boolean isAuthorisedUser;
		if (cache.isReadDenied(cpId) == null)
		{

			isAuthorisedUser = checkPermission(sessionDataBean, entityName, cpId,
					Permissions.READ_DENIED, queryResultObjectDataBean
							.getPrivilegeType());
			cache.addNewObjectInReadPrivilegeMap(cpId, isAuthorisedUser);
			System.out.println("Read Denied");
		}
		else
			isAuthorisedUser = cache.isReadDenied(cpId);
		return isAuthorisedUser;
	}

	/**
	 * This method will return entity name depending up on if entity is main entity or dependent entity. 
	 * @param queryResultObjectDataBean
	 * @return
	 */
	private String getEntityName(QueryResultObjectDataBean queryResultObjectDataBean)
	{
		String entityName;
		if (!queryResultObjectDataBean.isMainEntity())
		{
			entityName = queryResultObjectDataBean.getMainEntity().getName();
		}
		else
			entityName = queryResultObjectDataBean.getEntity().getName();
		return entityName;
	}

	/**
	 * This method will fire a query on catissue database to get CP ids related to a entity Id passed to this method.
	 * @param sessionDataBean
	 * @param entityName 
	 * @param entityId
	 * @return
	 */
	private List<List<String>> getCpIdsListForGivenEntityId(SessionDataBean sessionDataBean,
			String entityName, int entityId)
	{
		String sql = Variables.entityCPSqlMap.get(entityName);
		List<List<String>> cpIdsList = new ArrayList<List<String>>();
		if (sql != null)
		{
			sql = sql + entityId;
			try
			{
				cpIdsList = executeQuery(sessionDataBean, sql);
			}
			catch (Exception e)
			{
				Logger.out.error("Error ocured while getting CP ids for entity : "+entityName);
				e.printStackTrace();
			}
		}else if (entityName.equals(Constants.CP_CLASS_NAME))
		{
		    List<String> cpIdList = new ArrayList<String>();
		    cpIdList.add(String.valueOf(entityId));
			cpIdsList.add(cpIdList);
		}
		return cpIdsList;
	} 

	/**
	 * This method will internally call checkPermission of SecurityManager 
	 * and will return if a user is authorized user or not.
	 * @param sessionDataBean
	 * @param entityName
	 * @param entityId
	 * @param permission
	 * @param privilegeType
	 */
	private Boolean checkPermission(SessionDataBean sessionDataBean, String entityName, Long entityId,
			String permission, PrivilegeType privilegeType)
	{
		Boolean isAuthorisedUser = SecurityManager.getInstance(this.getClass()).checkPermission(
				sessionDataBean.getUserName(), entityName, entityId, permission, privilegeType);

		if (permission.equals(Permissions.READ_DENIED))
			isAuthorisedUser = !isAuthorisedUser;
		return isAuthorisedUser;
	}

	/**
	 * Executes Query to get CP ids for given entity id on database.Results are added in List<List<String>> 
	 * and this list is returned.
	 * @param sessionDataBean
	 * @param sql
	 * @throws DAOException
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	private List<List<String>> executeQuery(SessionDataBean sessionDataBean, String sql) throws DAOException, ClassNotFoundException, SQLException
	{  
		List<List<String>> aList = new ArrayList<List<String>>();
		Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = stmt.executeQuery(sql);
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		
		while (resultSet.next())
		{
			int i = 1;
			List<String> entityIdsList = new ArrayList<String>();
			while (i <= columnCount)
			{
				entityIdsList.add( resultSet.getObject(i).toString());
				i++;
			}
			aList.add(entityIdsList);
		}
		return aList;
		
	}
	
	/**
	 * This method removes data from list aList.
	 * It could be all data related to QueryResultObjectDataBean
	 * or only the identified fields depending on 
	 * the value of boolean removeOnlyIdentifiedData
	 * user
	 * @author supriya_dankh
	 * @param aList
	 * @param queryResultObjectData
	 * @param removeOnlyIdentifiedData
	 */
	private void removeUnauthorizedFieldsData(List aList,
			QueryResultObjectDataBean queryResultObjectData, boolean removeOnlyIdentifiedData)
	{

		Vector objectColumnIds;

		if (removeOnlyIdentifiedData)
		{
			objectColumnIds = queryResultObjectData.getIdentifiedDataColumnIds();
		}
		else
		{
			objectColumnIds = queryResultObjectData.getObjectColumnIds();
		}
		Logger.out.debug("objectColumnIds:" + objectColumnIds);
		if (objectColumnIds != null)
		{
			for (int k = 0; k < objectColumnIds.size(); k++)
			{
				aList.set(((Integer) objectColumnIds.get(k)).intValue(), "##");
			}
		}
	}
	
}
