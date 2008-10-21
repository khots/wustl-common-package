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

/**
 * To remove the Corrupted association from database.
 * @author prafull_kadam
 *
 */
public class QueryMetadataCleanup
{
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
			throw new RuntimeException("Incorrect no of Parameners !!!");
		}
		try
		{
			createConnection(args);
			writer = new BufferedWriter(new FileWriter("./MetadataCleanupLog.txt"));
			cleanup();
			writer.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (SQLException e)
				{
					throw new RuntimeException(e);
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
		ResultSet resultSet = stmt.executeQuery(SqlConstants.SQL_CORRUPTED_ASSOCIATION);
		Set<Long> intraModelAssociationIds = new HashSet<Long>();
		while (resultSet.next())
		{
			deletedRecords.append("\n"+ resultSet.getLong(1)+","+resultSet.getLong(2));
			intraModelAssociationIds.add(resultSet.getLong(1));
		}
		resultSet.close();
		writer.write("\nTotal Corrupted DE AssociationIds: " + intraModelAssociationIds.size());
		if (intraModelAssociationIds.isEmpty())
		{
			return;
		}
		StringBuffer pathDelSQL = new StringBuffer("delete from path where path_id in (");
		StringBuffer associationDelSQL = new StringBuffer(
				"delete from intra_model_association where ASSOCIATION_ID in (");
		Map<Long, String> entityNameMap = getEntityNameMap();
		int pathCnt = 0;
		writer.write("\n------------------------------------");
		writer.write("\nPaths removed: ");
		deletedRecords.append("\npath:");
		for (Long id : intraModelAssociationIds)
		{
			associationDelSQL.append(id).append(',');
			String pathSql = SqlConstants.SQL_PATH +"'%"
					+ id + "%'";
			resultSet = stmt.executeQuery(pathSql);
			while (resultSet.next())
			{
				String path = resultSet.getString(3);
				boolean isPresentId = isPresentInPath(id, path);
				if (isPresentId)
				{
					pathDelSQL.append(resultSet.getLong(1)+",");
					pathCnt++;
					writer.write("\n" + entityNameMap.get(resultSet.getLong(2)) + "--->"
							+ entityNameMap.get(resultSet.getLong(4)));
					deletedRecords.append('\n').append(resultSet.getLong(1))
					.append(',').append(resultSet.getLong(2))
					.append(',').append(resultSet.getString(3))
					.append(',').append(resultSet.getLong(4));
				}

			}
		}

		writer.write("\n------------------------------------");
		writer.write("\n Total Paths Corrupted:" + pathCnt);
		writer.write("\n------------------------------------");
		writer.write("\nExecuting SQL:");
		String sql = null;
		if (pathCnt != 0)
		{
			sql = pathDelSQL.substring(0, pathDelSQL.length() - 1) + ")";
			writer.write("\n" + sql);
			stmt.executeUpdate(sql);
		}

		sql = associationDelSQL.substring(0, associationDelSQL.length() - 1) + ")";
		writer.write("\n" + sql);
		stmt.executeUpdate(sql);
		writer.write("\nDeleted following records from corresponding table: ");
		writer.write(deletedRecords.toString());
		writer.write("\n------------------------------------");

		stmt.close();

	}

	/**
	 * To check whether given association id is present in the given path or not.
	 * @param associationId The intramodel association id.
	 * @param path The string representing intermediate path.
	 * @return isPresent return whether given association id is present in the given path or not
	 */
	private static boolean isPresentInPath(Long associationId, String path)
	{
		String[] splitArray = path.split("_");
		boolean isPresent = false;
		for (int index = 0; index < splitArray.length; index++)
		{
			if (splitArray[index].equals(associationId.toString()))
			{
				isPresent = true;
			}
		}
		return isPresent;
	}

	/**
	 * Create map of entity id verses entity Name.
	 * @return The map of entity id verses entity Name.
	 * @throws SQLException generic SQLException
	 */
	private static Map<Long, String> getEntityNameMap() throws SQLException
	{
		Map<Long, String> entityNameMap = new HashMap<Long, String>();
		ResultSet resultSet = stmt
				.executeQuery(SqlConstants.SQL_ENTITY_NAMES);
		while (resultSet.next())
		{
			entityNameMap.put(resultSet.getLong(1), resultSet.getString(2));
		}
		resultSet.close();
		return entityNameMap;
	}
}
