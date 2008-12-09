/**
 *
 */

package edu.wustl.common.util.global;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.util.logger.Logger;

/**
 * To remove the Corrupted association from database.
 * @author prafull_kadam
 *
 */
public final class QueryMetadataCleanup
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(QueryMetadataCleanup.class);

	/**
	 * private constructor.
	 */
	private QueryMetadataCleanup()
	{

	}
	/**
	 * specify connection object.
	 */
	private static Connection con = null;
	/**
	 * specify statement object.
	 */
	private static Statement stmt = null;
	/**
	 * specify BufferedWriter object.
	 */
	private static BufferedWriter writer = null;

	/**
	 *
	 * @param args The database connecttion related parameters:
	 * 	- User Name
	 * 	- Password
	 * 	- driver
	 * 	- Connection URL
	 *
	 */
	public static void main(String[] args)
	{
		if (args.length != 4)
		{
			logger.error("There are no sufficient arguments.");
		}
		try
		{
			createConnection(args);
			writer = new BufferedWriter(new FileWriter("./MetadataCleanupLog.txt"));
			cleanup();
			writer.close();
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (SQLException sqlException)
				{
					logger.error(sqlException.getMessage(), sqlException);
				}
			}
		}

	}

	/**
	 * TO create Connection object.
	 * @param args The database connecttion related parameters:
	 * 	- User Name
	 * 	- Password
	 * 	- driver
	 * 	- Connection URL
	 * @throws SQLException SQL Exception
	 * @throws InstantiationException InstantiationException
	 * @throws IllegalAccessException IllegalAccessException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	private static void createConnection(String[] args) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		String userName = args[0];
		String password = args[1];

		String driver = args[2];
		String url = args[3];
		Class.forName(driver).newInstance();
		/*System.out.println("Database Parameters Passed:");
		System.out.println("userName:" + userName);
		System.out.println("password:" + password);
		System.out.println("driver:" + userName);
		System.out.println("url:" + url);*/
		con = DriverManager.getConnection(url, userName, password);
		//		con.setAutoCommit(false);
		stmt = con.createStatement();
	}

	/**
	 * Removes entries from path & intramodel association table.
	 * @throws SQLException generic SQLException
	 * @throws IOException generic IOException
	 */
	private static void cleanup() throws SQLException, IOException
	{
		StringBuffer deletedRecords = new StringBuffer("\nintra_model_association:");
		Set<Long> intraModelAssociationIds = getIntraModelAssociations(deletedRecords);

		if (intraModelAssociationIds.isEmpty())
		{
			return;
		}

		writer.write("\nTotal Corrupted DE AssociationIds: " + intraModelAssociationIds.size());
		StringBuffer pathDelSQL = new StringBuffer("delete from path where path_id in (");
		StringBuffer associationDelSQL = new StringBuffer(
				"delete from intra_model_association where ASSOCIATION_ID in (");
		int pathCnt = addToBeDeletedIdsToSQLString(deletedRecords, intraModelAssociationIds,
				pathDelSQL, associationDelSQL);

		writer.write("\n------------------------------------");
		writer.write("\n Total Paths Corrupted:" + pathCnt);
		writer.write("\n------------------------------------");
		writer.write("\nExecuting SQL:");

		if (pathCnt != 0)
		{
			deleteRecords(pathDelSQL);
		}

		deleteRecords(associationDelSQL);
		writer.write("\nDeleted following records from corresponding table: ");
		writer.write(deletedRecords.toString());
		writer.write("\n------------------------------------");

		stmt.close();

	}

	/**
	 * This method delete Records.
	 * @param pathDelSQL path Del SQL.
	 * @throws IOException IO Exception
	 * @throws SQLException SQL Exception
	 */
	private static void deleteRecords(StringBuffer pathDelSQL) throws IOException, SQLException
	{
		String pathDESql = pathDelSQL.substring(0, pathDelSQL.length() - 1) + ")";
		writer.write("\n" + pathDESql);
		stmt.executeUpdate(pathDESql);
	}

	/**
	 * This method add To Be Deleted Ids To SQL String.
	 * @param deletedRecords deleted Records.
	 * @param intraModelAssociationIds intra Model Association Ids.
	 * @param pathDelSQL path Del SQL
	 * @param associationDelSQL association Del SQL
	 * @return path Count.
	 * @throws SQLException SQL Exception
	 * @throws IOException IO Exception.
	 */
	private static int addToBeDeletedIdsToSQLString(StringBuffer deletedRecords,
			Set<Long> intraModelAssociationIds, StringBuffer pathDelSQL,
			StringBuffer associationDelSQL) throws SQLException, IOException
	{
		ResultSet resultSet;
		Map<Long, String> entityNameMap = getEntityNameMap();

		int pathCnt = 0;
		writer.write("\n------------------------------------");
		writer.write("\nPaths removed: ");
		deletedRecords.append("\npath:");

		for (Long id : intraModelAssociationIds)
		{
			associationDelSQL.append(id).append(',');
			String pathSql = SqlConstants.SQL_PATH + "'%" + id + "%'";
			resultSet = stmt.executeQuery(pathSql);
			while (resultSet.next())
			{

				if (isPresentInPath(id, resultSet.getString(3)))
				{
					pathDelSQL.append(resultSet.getLong(1) + ",");
					pathCnt++;
					writer.write("\n" + entityNameMap.get(resultSet.getLong(2)) + "--->"
							+ entityNameMap.get(resultSet.getLong(4)));

					deletedRecords.append('\n').append(resultSet.getLong(1)).append(',').
					append(resultSet.getLong(2)).append(',').append(resultSet.getString(3))
							.append(',').append(resultSet.getLong(4));
				}

			}
		}
		return pathCnt;
	}

	/**
	 * get Intra Model Associations.
	 * @param deletedRecords deleted Records
	 * @return Intra Model Associations.
	 * @throws SQLException SQL Exception.
	 */
	private static Set<Long> getIntraModelAssociations(StringBuffer deletedRecords)
			throws SQLException
	{
		ResultSet resultSet = stmt.executeQuery(SqlConstants.SQL_CORRUPTED_ASSOCIATION);
		Set<Long> intraModelAssociationIds = new HashSet<Long>();
		while (resultSet.next())
		{
			deletedRecords.append('\n').append(resultSet.getLong(1)).append(',').append(
					resultSet.getLong(2));
			intraModelAssociationIds.add(resultSet.getLong(1));
		}
		resultSet.close();
		return intraModelAssociationIds;
	}

	/**
	 * To check whether given association id is present in the given path or not.
	 * @param associationId The intramodel association id
	 * @param path The string representing intermediate path.
	 * @return isPresent return whether given association id is present in the given path or not
	 */
	private static boolean isPresentInPath(Long associationId, String path)
	{
		return path.contains("_" + associationId.toString());
	}

	/**
	 * Create map of entity id verses entity Name.
	 * @return The map of entity id verses entity Name.
	 * @throws SQLException generic SQLException
	 */
	private static Map<Long, String> getEntityNameMap() throws SQLException
	{
		Map<Long, String> entityNameMap = new HashMap<Long, String>();
		ResultSet resultSet = stmt.executeQuery(SqlConstants.SQL_ENTITY_NAMES);
		while (resultSet.next())
		{
			entityNameMap.put(resultSet.getLong(1), resultSet.getString(2));
		}
		resultSet.close();
		return entityNameMap;
	}
}
