
package edu.wustl.common.util.global;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.security.exceptions.PasswordEncryptionException;
import edu.wustl.common.util.logger.Logger;
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
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(PasswordEncrypter.class);

	/**
	 * dbUtility object is used to store the argument values and getting database connection.
	 */
	private static DatabaseUtility dbUtility= new DatabaseUtility();

	/**
	 * Minimum number of arguments required.
	 */
	private static final int MIN_NO_ARGS=7;
	/**
	 * The name of the csm table whose password field is to be encrypted.
	 */
	private static final String CSM_DATABASE_TABLE_NAME = "csm_user";

	/**
	 * The name of the catissue table whose password field is to be encrypted.
	 */
	private static final String CATISSUE_DATABASE_TABLE_NAME = "catissue_password";

	/**
	 * The name of the field whose row values have to be encrypted.
	 */
	private static final String DATABASE_TABLE_FIELD_NAME = "password";
	/**
	 * Main method.
	 * @param args arguments to main methods.
	 * @throws Exception -If number of arguments are less than 7
	 */
	public static void main(String[] args) throws Exception
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
		catch (ClassNotFoundException exception)
		{
			logger.fatal("Not abel to load database driver class", exception);
		}
		catch (SQLException exception)
		{
			logger.fatal("Not able to update the passwords.", exception);
		}
	}

	/**
	 * This method will update password field with new encrypted password in the database.
	 * @param connection Database connection object
	 * @param sql Query String
	 * @throws SQLException generic SQL Exception
	 */
	private static void updatePasswords(Connection connection, String sql) throws SQLException
	{
		Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet resultSet = stmt.executeQuery(sql);

		resultSet.first();
		try
		{
		while (resultSet.next())
		{
			String userPassword = resultSet.getString(DATABASE_TABLE_FIELD_NAME);
			if (!StringUtilities.isBlank(userPassword))
			{
					//Encrypting the password and updating the database.
					String encryptedPasswordWithNewEncryption = PasswordManager
							.encrypt(PasswordManager.decode(userPassword));
					resultSet.updateString(DATABASE_TABLE_FIELD_NAME,
							encryptedPasswordWithNewEncryption);
					resultSet.updateRow();
				}
			}
		}
		catch (PasswordEncryptionException exception)
		{
			logger.fatal("Not able to encrypt the passwords.", exception);
		}
		finally
		{
		resultSet.close();
		stmt.close();
		}
	}

	/**
	 * This method is for configuring database connection.
	 * @param args String[] of configuration info
	 * @throws Exception -If number of arguments are less than 7
	 */
	private static void configureDBConnection(String[] args) throws Exception
	{
		if (args.length == MIN_NO_ARGS)
		{
			dbUtility.setDbParams(args);
		}
		else
		{
			throw new Exception("Incorrect number of parameters!!!!");
		}
	}
}
