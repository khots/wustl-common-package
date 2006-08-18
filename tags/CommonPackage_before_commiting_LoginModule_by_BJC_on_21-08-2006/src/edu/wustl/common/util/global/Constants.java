/*
 * Created on Nov 30, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package edu.wustl.common.util.global;

import java.util.HashMap;

/**
 * This classes is specific to common files. And contains all variables used by classes from 
 * common package.
 * @author gautam_shetty  
 * */
public class Constants 
{
	public static final String SELECT_OPTION = "-- Select --";
	public static final String CDE_CONF_FILE = "CDEConfig.xml";
	public static final String ANY = "Any";
	public static final String DELIMETER = ",";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String TAB_DELIMETER = "\t";
	
	public static final  HashMap STATIC_PROTECTION_GROUPS_FOR_OBJECT_TYPES = new HashMap();
	
	// Mandar: Used for Date Validations in Validator Class
	public static final String DATE_SEPARATOR = "-";
	public static final String DATE_SEPARATOR_SLASH = "/";
	public static final String MIN_YEAR = "1900";
	public static final String MAX_YEAR = "9999";
	
	//Activity Status values
	public static final String ACTIVITY_STATUS_ACTIVE = "Active";
	
	public static final String ADD = "add";
	
	public static final String getCollectionProtocolPGName(Long identifier)
	{
	    if(identifier == null)
	    {
	        return "COLLECTION_PROTOCOL_";
	    }
	    return "COLLECTION_PROTOCOL_"+identifier;
	}
	
	public static final String getCollectionProtocolPIGroupName(Long identifier)
	{
	    if(identifier == null)
	    {
	        return "PI_COLLECTION_PROTOCOL_";
	    }
	    return "PI_COLLECTION_PROTOCOL_"+identifier;
	}
	
	public static final String getCollectionProtocolCoordinatorGroupName(Long identifier)
	{
	    if(identifier == null)
	    {
	        return "COORDINATORS_COLLECTION_PROTOCOL_";
	    }
	    return "COORDINATORS_COLLECTION_PROTOCOL_"+identifier;
	}
	
	public static final String getDistributionProtocolPGName(Long identifier)
	{
	    if(identifier == null)
	    {
	        return "DISTRIBUTION_PROTOCOL_";
	    }
	    return "DISTRIBUTION_PROTOCOL_"+identifier;
	}
	 
	public static final String getDistributionProtocolPIGroupName(Long identifier)
	{
	    if(identifier == null)
	    {
	        return "PI_DISTRIBUTION_PROTOCOL_";
	    }
	    return "PI_DISTRIBUTION_PROTOCOL_"+identifier;
	}
	
	public static final String getStorageContainerPGName()
	{
	    return "USER_";
	}
	
	public static final String COLLECTION_PROTOCOL_CLASS_NAME = "edu.wustl.catissuecore.domain.CollectionProtocol";//CollectionProtocol.class.getName();
	public static final String DISTRIBUTION_PROTOCOL_CLASS_NAME = "edu.wustl.catissuecore.domain.DistributionProtocol";//DistributionProtocol.class.getName();
	// Aarti: Constants for security parameter required 
	// while retrieving data from DAOs
	public static final int INSECURE_RETRIEVE = 0;
	public static final int CLASS_LEVEL_SECURE_RETRIEVE = 1; 
	public static final int OBJECT_LEVEL_SECURE_RETRIEVE = 2; 
	
	public static final String CATISSUE_SPECIMEN = "CATISSUE_SPECIMEN";
	
	// Constants used for authentication module.
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    
    public static final String OPERATION = "operation";
    
    // Constants for HTTP-API
    public static final String HTTP_API = "HTTPAPI";
	
    public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";
	
	public static final String SYSTEM_IDENTIFIER = "systemIdentifier";
	
	// User Roles
	public static final String ADMINISTRATOR = "Administrator";
	
	//Assign Privilege Constants.
	public static final boolean PRIVILEGE_ASSIGN = true;
	
	//DAO Constants.
	public static final int HIBERNATE_DAO = 1;
	public static final int JDBC_DAO = 2;
	
	public static final String  ORACLE_DATABASE = "ORACLE";
	public static final String  MYSQL_DATABASE = "MYSQL";
	
	// The unique key voilation message is "Duplicate entry %s for key %d"
	// This string is used for searching " for key " string in the above error message
	public static final String MYSQL_DUPL_KEY_MSG = " for key ";
	
	public static final String GENERIC_DATABASE_ERROR = "An error occured during a database operation. Please report this problem to the adminstrator";
	public static final String CONSTRAINT_VOILATION_ERROR = "Submission failed since a {0} with the same {1} already exists";
	public static final String OBJECT_NOT_FOUND_ERROR = "Submission failed since a {0} with given {1}: \"{2}\" does not exists";
	
	public static final String ACTIVITY_STATUS_DISABLED = "Disabled";
	public static final String ACTIVITY_STATUS_CLOSED = "Closed";
	
	public static final String AND_JOIN_CONDITION = "AND";
	public static final String ACTIVITY_STATUS = "activityStatus";
	
	public static final boolean switchSecurity = true;
	
	public static final String EDIT = "edit";
	
	public static final String DATE_PATTERN_MM_DD_YYYY = "MM-dd-yyyy";
	
	//Constants for audit of disabled objects.
	public static final String UPDATE_OPERATION = "UPDATE";
	public static final String ACTIVITY_STATUS_COLUMN = "ACTIVITY_STATUS";
	
	//Tree View constants.
	public static final String TISSUE_SITE = "Tissue Site";
	public static final int TISSUE_SITE_TREE_ID = 1;
	public static final int STORAGE_CONTAINER_TREE_ID = 2;
	public static final int QUERY_RESULTS_TREE_ID = 3;
	public static final String ROOT = "Root";
	public static final String CATISSUE_CORE = "caTissue Core";
	
	//Mandar : CDE xml package path.
	public static final String CDE_XML_PACKAGE_PATH = "edu.wustl.common.cde.xml";
	public static final String BOOLEAN_YES = "Yes";
	public static final String BOOLEAN_NO = "No";
	
	public static final String SESSION_DATA = "sessionData";
	public static final String PASSWORD_CHANGE_IN_SESSION = "changepassword";
	
	public static final String USER_CLASS_NAME = "edu.wustl.catissuecore.domain.User";
	
	public static final String IDENTIFIER = "IDENTIFIER";
	
	public static final String FIELD_TYPE_BIGINT = "bigint";
	public static final String FIELD_TYPE_VARCHAR = "varchar";
	public static final String FIELD_TYPE_TEXT = "text";
	public static final String FIELD_TYPE_TINY_INT = "tinyint";
	public static final String FIELD_TYPE_DATE = "date";
	public static final String FIELD_TYPE_TIMESTAMP_DATE = "timestampdate";
	
	public static final String TABLE_ALIAS_NAME_COLUMN = "ALIAS_NAME";
	public static final String TABLE_DATA_TABLE_NAME = "CATISSUE_QUERY_TABLE_DATA";
	public static final String TABLE_DISPLAY_NAME_COLUMN = "DISPLAY_NAME";
	
	public static final String TABLE_FOR_SQI_COLUMN = "FOR_SQI";
	
	public static final String TABLE_ID_COLUMN = "TABLE_ID";
	
	public static final String NULL = "NULL";
	
	public static final String CONDITION_VALUE_YES = "yes";
	
	public static final String TINY_INT_VALUE_ONE = "1";
	public static final String TINY_INT_VALUE_ZERO = "0";
	
	public static final String FIELD_TYPE_TIMESTAMP_TIME = "timestamptime";
	
	public static final String CDE_NAME_TISSUE_SITE = "Tissue Site";
	
	public static final String UPPER = "UPPER";
	
	public static final String PARENT_SPECIMEN_ID_COLUMN = "PARENT_SPECIMEN_ID";
	
	// Query results view temporary table name.
	public static final String QUERY_RESULTS_TABLE = "CATISSUE_QUERY_RESULTS";
	
	public static final String TIME_PATTERN_HH_MM_SS = "HH:mm:ss";
	
	public static final int SIMPLE_QUERY_INTERFACE_ID = 40;
	
	// -- menu selection related
	public static final String MENU_SELECTED = "menuSelected";
	
	public static final String SIMPLE_QUERY_MAP = "simpleQueryMap";
	
	public static final String IDENTIFIER_FIELD_INDEX = "identifierFieldIndex";
	public static final String PAGEOF_SIMPLE_QUERY_INTERFACE = "pageOfSimpleQueryInterface";
	public static final String SIMPLE_QUERY_ALIAS_NAME = "simpleQueryAliasName";
	
	public static final String SIMPLE_QUERY_INTERFACE_ACTION = "/SimpleQueryInterface.do";
	
	public static final String PAGEOF = "pageOf";
	public static final String TABLE_ALIAS_NAME = "aliasName";
	public static final String SIMPLE_QUERY_NO_RESULTS = "noResults";
	public static final String SEARCH_OBJECT_ACTION = "/SearchObject.do";
	
	public static final String SEARCH = "search";
	
	// SimpleSearchAction
	public static final String SIMPLE_QUERY_SINGLE_RESULT = "singleResult";
	
	public static final String SPREADSHEET_DATA_LIST = "spreadsheetDataList";
	public static final String SPREADSHEET_COLUMN_LIST = "spreadsheetColumnList";
	
	public static final String ACCESS_DENIED = "access_denied";
	
	public static final String ADVANCED_CONDITION_NODES_MAP = "advancedConditionNodesMap";
	public static final String ADVANCED_CONDITIONS_ROOT = "advancedCondtionsRoot";
	
	public static final String TREE_VECTOR = "treeVector";
	public static final String SELECT_COLUMN_LIST = "selectColumnList";
	public static final String SELECTED_NODE = "selectedNode";
	
	//Individual view Constants in DataViewAction.
	public static final String CONFIGURED_COLUMN_DISPLAY_NAMES = "configuredColumnDisplayNames";
	public static final String CONFIGURED_COLUMN_NAMES = "configuredColumnNames";
	public static final String CONFIGURED_SELECT_COLUMN_LIST = "configuredSelectColumnList";
	
	public static final String COLUMN_DISPLAY_NAMES = "columnDisplayNames";
	
	public static final String COLUMN_ID_MAP = "columnIdsMap";
	public static final String PAGEOF_ADVANCE_QUERY_INTERFACE = "pageOfAdvanceQueryInterface";
	public static final String PAGEOF_QUERY_RESULTS = "pageOfQueryResults";
	
	public static final String COLUMN = "Column";
	
	public static final String ATTRIBUTE_NAME_LIST = "attributeNameList";
	public static final String ATTRIBUTE_CONDITION_LIST = "attributeConditionList";
	
	public static final String[] ATTRIBUTE_NAME_ARRAY = {
	        SELECT_OPTION
	};
	
	public static final String[] ATTRIBUTE_CONDITION_ARRAY = {
	        "=","<",">"
	};
	
	//For Simple Query Interface
	public static final int SIMPLE_QUERY_TABLES = 1;
	public static final String OBJECT_NAME_LIST = "objectNameList";
	
	public static final String ACCESS_DENIED_ADMIN = "access_denied_admin";
	public static final String ACCESS_DENIED_BIOSPECIMEN = "access_denied_biospecimen";
	
	// Constants for type of query results view.
	public static final String SPREADSHEET_VIEW = "Spreadsheet View";
	public static final String OBJECT_VIEW = "Edit View";
	
	public static final String COLLECTION_PROTOCOL ="CollectionProtocol";
	
	// Frame names in Query Results page.
	public static final String DATA_VIEW_FRAME = "myframe1";
	public static final String APPLET_VIEW_FRAME = "appletViewFrame";
	
	// NodeSelectionlistener - Query Results Tree node selection (For spreadsheet or individual view).
	public static final String DATA_VIEW_ACTION = "DataView.do?nodeName=";
	public static final String VIEW_TYPE = "viewType";
	
	// TissueSite Tree View Constants.
	public static final String PROPERTY_NAME = "propertyName";
	
	// For Tree Applet
	public static final String PAGEOF_STORAGE_LOCATION = "pageOfStorageLocation";
	public static final String PAGEOF_SPECIMEN = "pageOfSpecimen";
	public static final String PAGEOF_TISSUE_SITE = "pageOfTissueSite";
	
	// Constants for Storage Container.
	public static final String STORAGE_CONTAINER_TYPE = "storageType";
	public static final String STORAGE_CONTAINER_TO_BE_SELECTED = "storageToBeSelected";
	public static final String STORAGE_CONTAINER_POSITION = "position";
	
	public static final String CDE_NAME = "cdeName";
	
	// Tree Data Action
	public static final String TREE_DATA_ACTION = "Data.do";
	public static final String SHOW_STORAGE_CONTAINER_GRID_VIEW_ACTION = "ShowStorageGridView.do";
	
	public static final String TREE_APPLET_NAME = "treeApplet";
	
	//	 for Add New
	public static final String ADD_NEW_STORAGE_TYPE_ID ="addNewStorageTypeId";
	public static final String ADD_NEW_COLLECTION_PROTOCOL_ID ="addNewCollectionProtocolId";
	public static final String ADD_NEW_SITE_ID ="addNewSiteId";
	public static final String ADD_NEW_USER_ID ="addNewUserId";
	public static final String ADD_NEW_USER_TO ="addNewUserTo";
	public static final String SUBMITTED_FOR = "submittedFor";
	public static final String SUBMITTED_FOR_ADD_NEW = "AddNew";
	public static final String SUBMITTED_FOR_FORWARD_TO = "ForwardTo";
	public static final String SUBMITTED_FOR_DEFAULT = "Default";
	public static final String FORM_BEAN_STACK= "formBeanStack";
	public static final String ADD_NEW_FORWARD_TO ="addNewForwardTo";
	public static final String FORWARD_TO = "forwardTo";
	public static final String ADD_NEW_FOR = "addNewFor";
	
	public static final String ERROR_DETAIL = "Error Detail";
	
	//Identifiers for various Form beans
	public static final int QUERY_INTERFACE_ID = 43;
	
	//Status message key Constants
	public static final String STATUS_MESSAGE_KEY = "statusMessageKey";
	
	//Query Interface Results View Constants
	public static final String QUERY = "query";
	//Constant for redefine operation for Advance and Simple Query
	public static final String REDEFINE = "redefine";	
	public static final String ORIGINAL_SIMPLE_QUERY_OBJECT = "originalSimpleQueryObject";
	public static final String ORIGINAL_SIMPLE_QUERY_COUNTER = "counter";
	public static final String SIMPLE_QUERY_COUNTER = "counter";
}