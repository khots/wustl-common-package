package edu.wustl.common.bizlogic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DatabaseConnectionParams;

/**
 * @author kalpana_thakur
 * This class will handle insert/update of all the hashed values as per the database type.
 */
public class HashedDataHandler
{
	/**
	 * Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(HashedDataHandler.class);

	/**
	 * This method returns the metaData associated to the table specified in tableName.
	 * @param tableName Name of the table whose metaData is requested
	 * @param columnNames Table columns
	 * @param dbConnParamForMetadata : Database connections to retrieve meta data.
	 * @return It will return the metaData associated to the table.
	 * @throws DAOException : DAOException
	 */
	protected final ResultSetMetaData getMetaData(String tableName,List<String> columnNames,
			DatabaseConnectionParams dbConnParamForMetadata)throws DAOException
	{

		ResultSetMetaData metaData;
		StringBuffer sqlBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
		sqlBuff.append("Select").append(DAOConstants.TAILING_SPACES);

		for (int i = 0; i < columnNames.size(); i++)
		{
			sqlBuff.append(columnNames.get(i));
			if (i != columnNames.size() - 1)
			{
				sqlBuff.append("  ,");
			}
		}
		sqlBuff.append(" from " + tableName + " where 1!=1");
		metaData = dbConnParamForMetadata.getMetaData(sqlBuff.toString());

		return metaData;

	}

	/**
	 * This method will returns the metaData associated to the table specified in tableName
	 * and update the list columnNames.
	 * @param tableName Name of the table whose metaData is requested
	 * @param columnNames Table columns
	 * @param dbConnParamForMetadata : Database connections to retrieve meta data.
	 * @return It will return the metaData associated to the table.
	 * @throws DAOException : DAOException
	 */
	protected final ResultSetMetaData getMetaDataAndUpdateColumns(String tableName,
			List<String> columnNames,DatabaseConnectionParams dbConnParamForMetadata)
	throws DAOException
	{
		ResultSetMetaData metaData;
		try
		{

			StringBuffer sqlBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
			sqlBuff.append("Select * from " ).append(tableName).append(" where 1!=1");
			metaData = dbConnParamForMetadata.getMetaData(sqlBuff.toString());

			for (int i = 1; i <= metaData.getColumnCount(); i++)
			{
				columnNames.add(metaData.getColumnName(i));
			}
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp.getMessage(), sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"HashedDataHandler.java :"+
					DAOConstants.RS_METADATA_ERROR);
		}

		return metaData;
	}

	/**
	 * This method generates the Insert query.
	 * @param tableName : Name of the table given to insert query
	 * @param columnNamesList : List of columns of the table.
	 * @return query String.
	 */
	protected String createInsertQuery(String tableName,List<String> columnNamesList)
	{
		StringBuffer query = new StringBuffer("INSERT INTO " + tableName + "(");
		StringBuffer colValues = new StringBuffer();
		Iterator<String> columnIterator = columnNamesList.iterator();
		while (columnIterator.hasNext())
		{
			query.append(columnIterator.next());
			colValues.append(DAOConstants.INDEX_VALUE_OPERATOR).append(DAOConstants.TAILING_SPACES);
			if (columnIterator.hasNext())
			{
				query.append(DAOConstants.SPLIT_OPERATOR).append(DAOConstants.TAILING_SPACES);
				colValues.append(DAOConstants.SPLIT_OPERATOR);
			}
			else
			{
				query.append(") values(");
				colValues.append(") ");
				query.append(colValues.toString());
			}
		}

		return query.toString();
	}

	/**
	 * @param columnValues :
	 * @param metaData :
	 * @param stmt :
	 * @throws SQLException :
	 * @throws DAOException :
	 */
	private void setStmtIndexValue(List<Object> columnValues,
			ResultSetMetaData metaData, PreparedStatement stmt)
			throws SQLException, DAOException
	{
		for (int i = 0; i < columnValues.size(); i++)
		{
			Object obj = columnValues.get(i);
			int index = i;index++;
			if(isDateColumn(metaData,index))
			{
				setDateColumns(stmt, index,obj);
				continue;
			}
			if(isTinyIntColumn(metaData,index))
			{
				setTinyIntColumns(stmt, index, obj);
				continue;
			}
			/*if(isTimeStampColumn(stmt,i,obj))
			{
				continue;
			}*/
			if(isNumberColumn(metaData,index))
			{
				setNumberColumns(stmt, index, obj);
				continue;
			}
			stmt.setObject(index, obj);
		}
	}

	/**
	 * @param metaData :
	 * @param index :
	 * @return true if column type date.
	 * @throws SQLException :Exception
	 */
	private boolean isDateColumn(ResultSetMetaData metaData,int index) throws SQLException
	{
		boolean isDateType = false;
		String type = metaData.getColumnTypeName(index);
		if ("DATE".equals(type))
		{
			isDateType = true;
		}
		return isDateType;
	}

	/**
	 * @param metaData :
	 * @param index :
	 * @return true if column type TinyInt.
	 * @throws SQLException :Exception
	 */
	private boolean isTinyIntColumn(ResultSetMetaData metaData,int index) throws SQLException
	{
		boolean isTinyIntType = false;
		String type = metaData.getColumnTypeName(index);
		if ("TINYINT".equals(type))
		{
			isTinyIntType = true;
		}
		return isTinyIntType;
	}

	/**
	 * @param metaData :
	 * @param index :
	 * @return true if column type is Number.
	 * @throws SQLException :Exception
	 */
	private boolean isNumberColumn(ResultSetMetaData metaData,int index) throws SQLException
	{
		boolean isNumberType = false;
		String type = metaData.getColumnTypeName(index);
		if ("NUMBER".equals(type))
		{
			isNumberType = true;
		}
		return isNumberType;
	}

	/**
	 * This method called to set Number value to PreparedStatement.
	 * @param stmt : TODO
	 * @param index : TODO
	 * @param obj : Object
	 * @throws SQLException : SQLException
	 */
	protected void setNumberColumns(PreparedStatement stmt,
			int index, Object obj) throws SQLException
	{
			if (obj != null	&& obj.toString().equals("##"))
			{
				stmt.setObject(index , Integer.valueOf(-1));
			}
			else
			{
				stmt.setObject(index , obj);
			}
	}

	/**
	 * This method called to set TimeStamp value to PreparedStatement.
	 * @param stmt :PreparedStatement
	 * @param index :
	 * @param obj :
	 * @return return true if column type is timeStamp.
	 * @throws SQLException SQLException
	 */
	protected boolean isTimeStampColumn(PreparedStatement stmt, int index,Object obj) throws SQLException
	{
		boolean isTimeStampColumn = false;
		Timestamp date = isColumnValueDate(obj);
		if (date != null)
		{
			stmt.setObject(index , date);
			isTimeStampColumn = true;
		}
		return isTimeStampColumn;
	}


	/**
	 * This method is called to set TinyInt value
	 * to prepared statement.
	 * @param stmt : TODO
	 * @param index :
	 * @param obj :
	 * @throws SQLException : SQLException
	 */
	private void setTinyIntColumns(PreparedStatement stmt, int index, Object obj)
			throws SQLException
	{
		if (obj != null && (Boolean.parseBoolean(obj.toString())|| obj.equals("1")))
		{
			stmt.setObject(index , 1);
		}
		else
		{
			stmt.setObject(index, 0);
		}
	}

	/**
	 * This method used to set Date values.
	 * to prepared statement
	 * @param stmt :TODO
	 * @param index :
	 * @param obj :
	 * @throws SQLException : SQLException
	 * @throws DAOException : DAOException
	 */
	protected void setDateColumns(PreparedStatement stmt,
			int index,Object obj)
			throws SQLException, DAOException
	{
		if (obj != null && obj.toString().equals("##"))
		{
			java.util.Date date = null;
			try
			{
				date = Utility.parseDate("1-1-9999", "mm-dd-yyyy");
			}
			catch (ParseException exp)
			{
				//TODO have to replace this by parse key
				ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
				throw new DAOException(errorKey,exp,"HashedDataHandler.java :");
			}
			Date sqlDate = new Date(date.getTime());
			stmt.setDate(index, sqlDate);
		}
	}


	/**
	 * This method checks the TimeStamp value.
	 * @param obj :
	 * @return It returns the TimeStamp value
	 * */
	private Timestamp isColumnValueDate(Object obj)
	{
		Timestamp timestamp = null;
		try
		{
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy",Locale.getDefault());
			formatter.setLenient(false);
			java.util.Date date;
			date = formatter.parse(obj.toString());
			/*
			 * Recheck if some issues occurs.
			 */
			Timestamp timestampInner = new Timestamp(date.getTime());
			if (obj != null && !DAOConstants.TAILING_SPACES.equals(obj.toString()))
			{
				timestamp = timestampInner;
			}
		}
		catch (ParseException parseExp)
		{
			logger.error(parseExp.getMessage(),parseExp);
		}

		return timestamp;
	}


	/**
	 * This method will be called to insert hashed data values.
	 * @param tableName :Name of the table
	 * @param columnValues :List of column values
	 * @param columnNames  :List of column names.
	 * @param connection : Database connection
	 * @throws DAOException  :DAOException
	 * @throws SQLException : SQLException
	 */
	public void insertHashedValues(String tableName, List<Object> columnValues, List<String> columnNames,
			Connection connection)throws DAOException, SQLException
	{

		List<String>columnNamesList = new ArrayList<String>();
		ResultSetMetaData metaData;

		DatabaseConnectionParams dbConnParamForMetadata = new DatabaseConnectionParams();
		dbConnParamForMetadata.setConnection(connection);

		DatabaseConnectionParams dbConnParamForInsertQuery = new DatabaseConnectionParams();
		dbConnParamForInsertQuery.setConnection(connection);

		PreparedStatement stmt = null;
		try
		{
			if(columnNames != null && !columnNames.isEmpty())
			{
				metaData = getMetaData(tableName, columnNames,dbConnParamForMetadata);
				columnNamesList = columnNames;
			}
			else
			{
				metaData = getMetaDataAndUpdateColumns(tableName,columnNamesList,
						dbConnParamForMetadata);
			}

			String insertQuery = createInsertQuery(tableName,columnNamesList);
			stmt = dbConnParamForInsertQuery.getPreparedStatement(insertQuery);
			setStmtIndexValue(columnValues, metaData, stmt);
			stmt.executeUpdate();
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(),sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey, sqlExp,"HashedDataHandler.java :"+
					DAOConstants.INSERT_OBJ_ERROR);
		}
		finally
		{
			dbConnParamForMetadata.closeConnectionParams();
			dbConnParamForInsertQuery.closeConnectionParams();
		}
	}

}
