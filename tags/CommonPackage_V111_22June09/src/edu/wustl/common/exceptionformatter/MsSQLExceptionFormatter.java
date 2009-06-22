
package edu.wustl.common.exceptionformatter;

import java.sql.ResultSet;
import java.util.HashMap;

import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;

/**
 *
 * @author prashant_bandal
 *
 */
public class MsSQLExceptionFormatter implements IDBExceptionFormatter
{

	/**
	* LOGGER Logger - Generic LOGGER.
	*/
	private static final Logger LOGGER = Logger.getCommonLogger(MsSQLExceptionFormatter.class);

	/**
	 * This method gets Formated Message.
	 * @param objExp Exception
	 * @param jdbcDAO JDBCDAO
	 * @return formattedErrMsg
	 */
	public String getFormatedMessage(Exception objExp, JDBCDAO jdbcDAO)
	{
		String formattedErrMsg = null; // Formatted Error Message return by this method

		try
		{
			// Get table name from DB exception message.
			String tableName = Utility.parseException(objExp);

			// Generate Error Message by appending all messages of previous cause Exceptions
			String sqlMessage = Utility.generateErrorMessage(objExp);

			// From the MsSQL server error message, extract the key ID
			/* The unique key violatin message is ->
			  could not insert: [edu.wustl.catissuecore.domain.Participant]
			   Violation of UNIQUE KEY constraint 'UQ__CATISSUE_PARTICI__2D27B809'.
			  		Cannot insert duplicate key in object 'dbo.CATISSUE_PARTICIPANT'.
			*/
			int startIndex = 0;
			startIndex = sqlMessage.indexOf(Constants.MSSQLSERVER_DUPL_KEY_MSG_START);
			startIndex += Constants.MSSQLSERVER_DUPL_KEY_MSG_START.length();
			String constraintVoilated = "";
			constraintVoilated = sqlMessage.substring(startIndex, sqlMessage
					.indexOf(Constants.MSSQLSERVER_DUPL_KEY_MSG_END));

			// For the key extracted from the string, get the column name on
			//which the constraint has failed
			boolean found = false;
			// get connection from arguments
			/*if(args[1]!=null) {
				connection =(Connection)args[1];
			} else {
				LOGGER.debug("Error Message: Connection object not given");
			}
			// Get database metadata object for the connection
			DatabaseMetaData dbmd = connection.getMetaData();*/

			// Get a description of the given table's indices and statistics
			ResultSet rs = jdbcDAO.getDBMetaDataResultSet(tableName);
			/*dbmd.getIndexInfo(connection.getCatalog(), null,
					tableName, true, false);*/

			HashMap indexDetails = new HashMap();

			while (rs.next())
			{
				// In this loop, all the indexes are stored as key of the HashMap
				// and the column names are stored as value.
				if (rs.getString("INDEX_NAME") != null)
				{
					if (constraintVoilated.equals(rs.getString("INDEX_NAME")))
					{
						LOGGER.debug("Constraint: " + constraintVoilated);
						found = true; // column name for given key index found
					}

					StringBuffer temp = (StringBuffer)
					indexDetails.get(rs.getString("INDEX_NAME"));
					if (temp != null)
					{
						temp.append(rs.getString("COLUMN_NAME"));
						temp.append(",");
						indexDetails.remove(rs.getString("INDEX_NAME"));
						indexDetails.put(rs.getString("INDEX_NAME"), temp);
						LOGGER.debug("Column :" + temp.toString());
					}
					else
					{
						temp = new StringBuffer(rs.getString("COLUMN_NAME"));
						//temp.append(",");
						indexDetails.put(rs.getString("INDEX_NAME"), temp);
					}
				}
			}
			LOGGER.debug("out of loop");
			rs.close();
			StringBuffer columnNames = new StringBuffer("");
			if (found)
			{
				columnNames = (StringBuffer) indexDetails.get(constraintVoilated);
				LOGGER.debug("Column Name: " + columnNames.toString());
				LOGGER.debug("Constraint: " + constraintVoilated);
			}
			formattedErrMsg = Utility.prepareMessage(columnNames, tableName, jdbcDAO);
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;
		}
		return formattedErrMsg;
	}
}
