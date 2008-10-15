
package edu.wustl.common.util.global;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.security.exceptions.PasswordEncryptionException;
import gov.nih.nci.security.util.StringUtilities;

/**
 * PasswordEncrypter: This class encrypts all data of given field from a given table.
 * The database connection parameters have to be provided.
 *
 * The encryption is done using the class PasswordManager.
 *
 * @author abhishek_mehta
 *
 */

public class PasswordEncrypter
{
	/**
	 * dbUtility object is used to store the argument values and getting database connection.
	 */
	private static DatabaseUtility dbUtility= new DatabaseUtility();

	// The name of the csm table whose password field is to be encrypted.
	static String CSM_DATABASE_TABLE_NAME = "csm_user";
	// The name of the catissue table whose password field is to be encrypted.
	static String CATISSUE_DATABASE_TABLE_NAME = "catissue_password";
	// The name of the field whose row values have to be encrypted.
	static String DATABASE_TABLE_FIELD_NAME = "password";

	public static void main(String[] args)
	{

		configureDBConnection(args);

		try
		{
			// Create an updatable result set
			Connection connection = dbUtility.getConnection();

			//Encrypting password for csm_user table

			String sql = "SELECT " + CSM_DATABASE_TABLE_NAME + ".* FROM " + CSM_DATABASE_TABLE_NAME;
			updatePasswords(connection, sql);

			//Encrypting password for catissue_password table

			sql = "SELECT " + CATISSUE_DATABASE_TABLE_NAME + ".* FROM "
					+ CATISSUE_DATABASE_TABLE_NAME;
			updatePasswords(connection, sql);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method will update password field with new encrypted password in the database.
	 * @param connection Database connection object
	 * @param sql Query String
	 * @throws SQLException
	 */
	private static void updatePasswords(Connection connection, String sql) throws SQLException
	{
		Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet resultSet = stmt.executeQuery(sql);

		boolean first = false;
		while (resultSet.next())
		{
			if (!first)
			{
				resultSet.first();
				first = true;
			}
			String userPassword = resultSet.getString(DATABASE_TABLE_FIELD_NAME);
			if (!StringUtilities.isBlank(userPassword))
			{
				try
				{
					//Encrypting the password and updating the database.
					String encryptedPasswordWithNewEncryption = PasswordManager
							.encrypt(PasswordManager.decode(userPassword));
					if (!StringUtilities.isBlank(encryptedPasswordWithNewEncryption))
					{
						resultSet.updateString(DATABASE_TABLE_FIELD_NAME,
								encryptedPasswordWithNewEncryption);
						resultSet.updateRow();
					}
				}
				catch (PasswordEncryptionException e)
				{
					e.printStackTrace();
				}
			}
		}
		resultSet.close();
		stmt.close();
	}

	/**
	 * This method is for configuring database connection.
	 * @param args String[] of configuration info
	 */
	private static void configureDBConnection(String[] args)
	{
		if (args.length == 7)
		{
			dbUtility.setDbServerNname(args[0]);
			dbUtility.setDbServerPortNumber(args[1]);
			dbUtility.setDbType(args[2]);
			dbUtility.setDbName(args[3]);
			dbUtility.setDbUserName(args[4]);
			dbUtility.setDbPassword(args[5]);
			dbUtility.setDbDriver(args[6]);
		}
		else
		{
			throw new RuntimeException("Incorrect number of parameters!!!!");
		}
	}
}
