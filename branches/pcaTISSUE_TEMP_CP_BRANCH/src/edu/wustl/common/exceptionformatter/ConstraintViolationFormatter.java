package edu.wustl.common.exceptionformatter;
/**
 * 
 * @author sachin_lale
 * Description: Class implementing Exception_Formatter interface method for Constarint_Violation_Exception  
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;

import edu.wustl.common.util.dbManager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

public class ConstraintViolationFormatter implements ExceptionFormatter 
{
	/**
	 * Logger. 
	 */
	private org.apache.log4j.Logger logger = Logger.getLogger(ConstraintViolationFormatter.class);
	
	/*  
	 * @param objExcp - Exception object 
	 * @param args[] where args[0] must be String 'Table_Name' 
	 * and args[1] must be java.sql.Connection object get from session.getCOnnection().   
	 */
			
	public String formatMessage(Exception objExcp, Object[] args) 
	{
		String errMessage =null;
		
		// Check which database is in used and called appropriate Format_Method
		if(Variables.databaseName.equals(Constants.MYSQL_DATABASE))  {
			errMessage = MySQLformatMessage(objExcp,args); // method for MYSQL Database
		} else if(Variables.databaseName.equals(Constants.MSSQLSERVER_DATABASE)) {
			errMessage = MsSQLServerformatMessage(objExcp,args); // method for MsSQLServer Database.
		} else {
			errMessage = OracleformatMessage(objExcp,args); // method for ORACLE Database
		}
		
		if(errMessage==null)
		{
			errMessage = " Database in used not specified " + Constants.GENERIC_DATABASE_ERROR; 
		}
		return errMessage;
	}
	private String OracleformatMessage(Exception objExcp, Object[] args) 
    {
        String tableName="";
        String columnName="";
        String formattedErrMsg=null; // Formatted Error Message return by this method
		logger.debug(objExcp.getClass().getName());
        if(objExcp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
        {
            
            objExcp = (Exception)objExcp.getCause();
            logger.debug(objExcp);
        }
        try
        {
//        	 Get database metadata object for the connection
        	Connection connection=null;
        	if(args[1]!=null)
            {
                connection =(Connection)args[1];
            }
            else
            {
                logger.debug("Error Message: Connection object not given");
            }
        	// Get Contraint Name from messages         		
        	String sqlMessage = generateErrorMessage(objExcp);
        	int tempstartIndexofMsg = sqlMessage.indexOf("(");
            
            String temp = sqlMessage.substring(tempstartIndexofMsg);
            int startIndexofMsg = temp.indexOf(".");
            int endIndexofMsg = temp.indexOf(")");
            String strKey =temp.substring((startIndexofMsg+1),endIndexofMsg);
            logger.debug("Contraint Name: "+strKey);
           
            String Query = "select COLUMN_NAME,TABLE_NAME from user_cons_columns where constraint_name = '"+strKey+"'";
            logger.debug("ExceptionFormatter Query: "+Query);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(Query);
            while(rs.next())
            {
                	columnName+=rs.getString("COLUMN_NAME")+",";
                	logger.debug("columnName: "+columnName);
                	tableName=rs.getString("TABLE_NAME");
                	logger.debug("tableName: "+tableName);
            }
            if(columnName.length()>0&&tableName.length()>0)
            {
            	columnName=columnName.substring(0,columnName.length()-1);
            	logger.debug("columnName befor formatting: "+columnName);
            	String displayName = ExceptionFormatterFactory.getDisplayName(tableName,connection);
            	
            	Object[] arguments = new Object[]{displayName,columnName};
            	formattedErrMsg = MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR,arguments);
            }	
            rs.close();
            statement.close();
         }
        catch(Exception e)
        {
            logger.error(e.getMessage(),e);
            formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;  
        }
        return formattedErrMsg;
    }
	private String MySQLformatMessage(Exception objExcp, Object[] args) {
		String formattedErrMsg=null; // Formatted Error Message return by this method
		Connection connection=null;
		
		try {
			// Get table name from DB exception message.
			String tableName = parseException(objExcp, args);
			
			// Generate Error Message by appending all messages of previous cause Exceptions
			String sqlMessage = generateErrorMessage(objExcp);
			
			// From the MySQL error msg and extract the key ID 
			// The unique key voilation message is "Duplicate entry %s for key %d"
			
			int key=-1;
			int indexofMsg=0;
			indexofMsg=sqlMessage.indexOf(Constants.MYSQL_DUPL_KEY_MSG);
			indexofMsg+=Constants.MYSQL_DUPL_KEY_MSG.length();
			
			// Get the %d part of the string
			String strKey =sqlMessage.substring(indexofMsg,sqlMessage.length()-1);
			key = Integer.parseInt(strKey);
			logger.debug(String.valueOf(key));
			
		 	// For the key extracted frm the string, get the column name on which the 
			// costraint has failed
			boolean found=false;
			// get connection from arguments
			if(args[1]!=null) {
				connection =(Connection)args[1];
			} else {
				logger.debug("Error Message: Connection object not given");
			}
			
			// Get database metadata object for the connection
			DatabaseMetaData dbmd = connection.getMetaData();
			
			//  Get a description of the given table's indices and statistics
			ResultSet rs = dbmd.getIndexInfo(connection.getCatalog(), null,
					tableName, true, false);
			
        	HashMap indexDetails = new HashMap(); 
			int indexCount = 1;
			String constraintVoilated = "";
        	while(rs.next()) {
				// In this loop, all the indexes are stored as key of the HashMap
				// and the column names are stored as value.
        		logger.debug("Key: " + indexCount);
        		if(key==indexCount) {
        			constraintVoilated=rs.getString("INDEX_NAME");
        			logger.debug("Constraint: "+constraintVoilated);
        			found=true; // column name for given key index found
        			//break;
        		}
        		StringBuffer temp = (StringBuffer)indexDetails.get(rs.getString("INDEX_NAME"));
        		if(temp!=null) {
        			temp.append(rs.getString("COLUMN_NAME"));
        			temp.append(",");
        			indexDetails.remove(rs.getString("INDEX_NAME"));
        			indexDetails.put(rs.getString("INDEX_NAME"),temp);
        			logger.debug("Column :"+temp.toString());
        		} else {
        			temp = new StringBuffer(rs.getString("COLUMN_NAME"));
        			//temp.append(rs.getString("COLUMN_NAME"));
        			temp.append(",");
        			indexDetails.put(rs.getString("INDEX_NAME"),temp);
        		}
        		indexCount++; // increment record count*/
        	}
        	logger.debug("out of loop" );
        	rs.close();
        	StringBuffer columnNames = new StringBuffer("");
    		if(found) {
        		columnNames = (StringBuffer) indexDetails.get(constraintVoilated);
        		logger.debug("Column Name: " + columnNames.toString());
    			logger.debug("Constraint: "+constraintVoilated);
    		}
    		formattedErrMsg = prepareMessage(columnNames, tableName, connection);
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;  
		}
		return formattedErrMsg;
	}
	
	/**
	 * Format constraint violation error message for mssqlserver db.
	 *  
	 * @param objExcp
	 * @param args
	 * @return
	 */
	private String MsSQLServerformatMessage(Exception objExcp, Object[] args) {
		String formattedErrMsg=null; // Formatted Error Message return by this method
		Connection connection=null;
		
		try {
			// Get table name from DB exception message.
			String tableName = parseException(objExcp, args);
			
			// Generate Error Message by appending all messages of previous cause Exceptions
			String sqlMessage = generateErrorMessage(objExcp);
			
			// From the MsSQL server error message, extract the key ID
			/* The unique key violatin message is ->
			  could not insert: [edu.wustl.catissuecore.domain.Participant] Violation of UNIQUE KEY constraint 'UQ__CATISSUE_PARTICI__2D27B809'. 
			  		Cannot insert duplicate key in object 'dbo.CATISSUE_PARTICIPANT'.
			*/
			int startIndex = 0;
			startIndex = sqlMessage.indexOf(Constants.MSSQLSERVER_DUPL_KEY_MSG_START);
			startIndex+=Constants.MSSQLSERVER_DUPL_KEY_MSG_START.length();
			String constraintVoilated = "";
			constraintVoilated = sqlMessage.substring(startIndex, sqlMessage.indexOf(Constants.MSSQLSERVER_DUPL_KEY_MSG_END));
			
		 	// For the key extracted from the string, get the column name on which the constraint has failed
			boolean found=false;
			// get connection from arguments
			if(args[1]!=null) {
				connection =(Connection)args[1];
			} else {
				logger.debug("Error Message: Connection object not given");
			}
			
			// Get database metadata object for the connection
			DatabaseMetaData dbmd = connection.getMetaData();
			
			// Get a description of the given table's indices and statistics
			ResultSet rs = dbmd.getIndexInfo(connection.getCatalog(), null,
					tableName, true, false);
			
        	HashMap indexDetails = new HashMap(); 
			
        	while(rs.next()) {
        		// In this loop, all the indexes are stored as key of the HashMap
				// and the column names are stored as value.
        		if(rs.getString("INDEX_NAME")!= null) {
        			if(constraintVoilated.equals(rs.getString("INDEX_NAME"))) {
	        			logger.debug("Constraint: "+constraintVoilated);
	        			found=true; // column name for given key index found
	        		}
        			
	        		StringBuffer temp = (StringBuffer)indexDetails.get(rs.getString("INDEX_NAME"));
	        		if(temp!=null) {
	        			temp.append(rs.getString("COLUMN_NAME"));
	        			temp.append(",");
	        			indexDetails.remove(rs.getString("INDEX_NAME"));
	        			indexDetails.put(rs.getString("INDEX_NAME"),temp);
	        			logger.debug("Column :"+temp.toString());
	        		} else {
	        			temp = new StringBuffer(rs.getString("COLUMN_NAME"));
	        			//temp.append(",");
	        			indexDetails.put(rs.getString("INDEX_NAME"),temp);
	        		}
        		}
        	}
        	logger.debug("out of loop" );
        	rs.close();
        	StringBuffer columnNames=new StringBuffer("");
    		if(found) {
        		columnNames = (StringBuffer) indexDetails.get(constraintVoilated);
        		logger.debug("Column Name: " + columnNames.toString());
    			logger.debug("Constraint: "+constraintVoilated);
    		}
    		formattedErrMsg = prepareMessage(columnNames, tableName, connection);
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;  
		}
		return formattedErrMsg;
	}
	
	private String generateErrorMessage(Exception ex)
	{
		String messageToAdd = "";
		if (ex instanceof HibernateException)
        {
            HibernateException hibernateException = (HibernateException) ex;
            StringBuffer message = new StringBuffer(messageToAdd);
            String str[] = hibernateException.getMessages();
            if (message != null)
            {
                for (int i = 0; i < str.length; i++)
                {
                	logger.debug("str:" + str[i]);
                	message.append(str[i] + " ");
                }
            }
            else
            {
                return "Unknown Error";
            }
            return message.toString();
        }
        else
        {
            return ex.getMessage();
        }
	}
	
	/**
	 * Parse the exception object and find DB table name.
	 * 
	 * @param objExcp exception object.
	 * @param args arguments.
	 * @throws Exception
	 */
	private String parseException(Exception objExcp, Object[] args) throws Exception {
		logger.debug(objExcp.getClass().getName());
		String tableName = "";
		if(objExcp instanceof gov.nih.nci.security.exceptions.CSTransactionException) {
			objExcp = (Exception)objExcp.getCause();
			logger.debug(objExcp);
		}
		
		if(args[0]!=null) {
			tableName = (String)args[0];
		} else {
			logger.debug("Table Name not specified");
			tableName=new String("Unknown Table");
		}
		logger.debug("Table Name:" + tableName);
		
	    //get Class name from message "could not insert [classname]"
		ConstraintViolationException  cEX = (ConstraintViolationException)objExcp;
	    String message = cEX.getMessage();
	    logger.debug("message :"+message);
	    int startIndex = message.indexOf("[");
	    
		/**
		 * Bug ID: 4926
		 * Description:In case of Edit, get Class name from message "could not insert [classname #id]"
	    */
	    int endIndex = message.indexOf("#");
	    if(endIndex == -1) {
	    	endIndex = message.indexOf("]");
	    }
	    String className = message.substring((startIndex+1),endIndex);
	    logger.debug("ClassName: "+className);
	    Class classObj = Class.forName(className);
	    // get table name from class 
	    tableName = HibernateMetaData.getRootTableName(classObj);
	    
	    /**
		 * Bug ID: 6034
		 * Description:To retrive the appropriate tablename checking the SQL"
	    */
	    if(!(cEX.getSQL().contains(tableName))) 
	    {
	    	 tableName = HibernateMetaData.getTableName(classObj);
        	 Properties prop = new Properties();
        	 prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("tablemapping.properties"));
        	 if(prop.getProperty(tableName)!=null)
        	 {
        		 tableName= prop.getProperty(tableName);	 
        	 }
	    }
		 
		 return tableName;
	}
	
	/**
	 * Format and return message to display.
	 * 
	 * @param found Whether the constraint found or not.
	 * @param constraintVoilated DB constraint.
	 * @param indexDetails All constraints on a table, as a key/value pair.
	 * @param connection DB connection object. 
	 * @return error message to display.
	 * @throws Exception
	 */
	private String prepareMessage(StringBuffer columnNames, String tableName, Connection connection) throws Exception {
		String formattedErrMsg = "";
		String columnName=""; //stores Column_Name of table 
		
		// Create arrays of object containing data to insert in CONSTRAINT_VOILATION_ERROR
    	Object[] arguments = new Object[2];
    	String dispTableName = ExceptionFormatterFactory.getDisplayName(tableName,connection);
		arguments[0]=dispTableName;
		columnName=columnNames.toString(); 
		columnName=columnName.substring(0,columnName.length());
		arguments[1]=columnName; 	
		logger.debug("Column Name: " + columnNames.toString());
		
		// Insert Table_Name and Column_Name in CONSTRAINT_VOILATION_ERROR message   
		formattedErrMsg = MessageFormat.format(Constants.CONSTRAINT_VOILATION_ERROR,arguments);
		
		return formattedErrMsg;
	}
}
