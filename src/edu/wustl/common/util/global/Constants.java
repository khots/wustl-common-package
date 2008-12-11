/*
 * Created on Nov 30, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package edu.wustl.common.util.global;

/**
 * This classes is specific to common files. And contains all variables used by classes from
 * common package.
 * @author gautam_shetty
 * */
public final class Constants
{

	/**
	 * Private constructor.
	 */
	private Constants()
	{

	}

	//	constants for TiTLi Search
	//public static final String TITLI_SORTED_RESULT_MAP = "sortedResultMap";
	//public static final String TITLI_INSERT_OPERATION = "insert";
	//public static final String TITLI_UPDATE_OPERATION = "update";
	//public static final String TITLI_DELETE_OPERATION = "delete";
	//public static final String TITLI_SINGLE_RESULT = "singleResult";
	//public static final String TITLI_FETCH_ACTION = "/TitliFetch.do";

	// constants for passwordManager

	//public static final String MINIMUM_PASSWORD_LENGTH = "minimumPasswordLength";

	/**
	 * Specify SELECT_OPTION.
	 */
	public static final String SELECT_OPTION = "-- Select --";
	/**
	 * Specify SELECT_OPTION_VALUE.
	 */
	public static final int SELECT_OPTION_VALUE = -1;
	//public static final String CDE_CONF_FILE = "CDEConfig.xml";
	/**
	 * Specify ANY.
	 */
	public static final String ANY = "Any";
	/**
	 * Specify DELIMETER.
	 */
	public static final String DELIMETER = ",";

	/**
	 * Specify TAB_DELIMETER.
	 */
	public static final String TAB_DELIMETER = "\t";

	//	Misc
	/**
	 * Specify SEPARATOR( : ).
	 */
	public static final String SEPARATOR = " : ";

	public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd-HH24.mm.ss.SSS";

	// Mandar: Used for Date Validations in Validator Class
	public static final String DATE_SEPARATOR = "-";
	public static final String DATE_SEPARATOR_SLASH = "/";
	public static final String MIN_YEAR = "1900";
	public static final String MAX_YEAR = "9999";

	//Activity Status values
	public static final String ACTIVITY_STATUS_ACTIVE = "Active";

	/**
	 * Specify add operation.
	 */
	public static final String ADD = "add";

	//public static final String COLLECTION_PROTOCOL_CLASS_NAME = "edu.wustl.catissuecore.domain.CollectionProtocol";//CollectionProtocol.class.getName();
	//public static final String DISTRIBUTION_PROTOCOL_CLASS_NAME = "edu.wustl.catissuecore.domain.DistributionProtocol";//DistributionProtocol.class.getName();

	// Aarti: Constants for security parameter required
	// while retrieving data from DAOs
	//public static final int INSECURE_RETRIEVE = 0;
	//public static final int CLASS_LEVEL_SECURE_RETRIEVE = 1;
	//public static final int OBJECT_LEVEL_SECURE_RETRIEVE = 2;

	//public static final String CATISSUE_SPECIMEN = "CATISSUE_SPECIMEN";

	// Constants used for authentication module.
	/**
	 * Constant for LOGIN.
	 */
	public static final String LOGIN = "login";
	/**
	 * Constant for LOGOUT.
	 */
	public static final String LOGOUT = "logout";
	/**
	 * Constant for OPERATION.
	 */
	public static final String OPERATION = "operation";

	/**
	 *  Constants for HTTP-API.
	 */
	public static final String HTTP_API = "HTTPAPI";
	/**
	 * Constant for SUCCESS.
	 */
	public static final String SUCCESS = "success";
	/**
	 * Constant for FAILURE.
	 */
	public static final String FAILURE = "failure";
	/**
	 * Constant for SYSTEM_IDENTIFIER.
	 */
	public static final String SYSTEM_IDENTIFIER = "id";

	// User Roles
	//public static final String ADMINISTRATOR = "Administrator";

	// The unique key voilation message is "Duplicate entry %s for key %d"
	// This string is used for searching " for key " string in the above error message

	//ExceptionFormatter
	public static final String MYSQL_DUPL_KEY_MSG = " for key ";

	public static final String GENERIC_DATABASE_ERROR = "An error occurred during a database operation. Please report this problem to the administrator";
	public static final String CONSTRAINT_VOILATION_ERROR = "Submission failed since a {0} with the same {1} already exists";
	public static final String OBJECT_NOT_FOUND_ERROR = "Submission failed since a {0} with given {1}: \"{2}\" does not exists";
	//DefaultBizLogic.
	public static final String ACTIVITY_STATUS_DISABLED = "Disabled";
	public static final String ACTIVITY_STATUS_CLOSED = "Closed";

	/**
	 * Constant for AND_JOIN_CONDITION.
	 */
	public static final String AND_JOIN_CONDITION = "AND";
	/**
	 * Constant for OR_JOIN_CONDITION.
	 */
	public static final String OR_JOIN_CONDITION = "OR";
	//DefaultBizLogic.
	public static final String ACTIVITY_STATUS = "activityStatus";

	/**
	 * Constant for EDIT.
	 */
	public static final String EDIT = "edit";
	//catissue.
	public static final String DATE_PATTERN_MM_DD_YYYY = "MM-dd-yyyy";

	//Constants for audit of disabled objects.
	/**
	 * Constant for UPDATE_OPERATION.
	 */
	public static final String UPDATE_OPERATION = "UPDATE";
	/**
	 * Constant for ACTIVITY_STATUS_COLUMN.
	 */
	public static final String ACTIVITY_STATUS_COLUMN = "ACTIVITY_STATUS";

	/*//Constants for Summary Page
	public static final String TISSUE = "Tissue";
	public static final String MOLECULE = "Molecular";
	public static final String CELL = "Cell";
	public static final String FLUID = "Fluid";*/

	/*//Tree View constants.
	public static final String TISSUE_SITE = "Tissue Site";
	public static final String CLINICAL_DIAGNOSIS = "Clinical Diagnosis";
	public static final int TISSUE_SITE_TREE_ID = 1;
	public static final int STORAGE_CONTAINER_TREE_ID = 2;
	public static final String ROOT = "Root";
	public static final String CATISSUE_CORE = "caTissue Core";*/

	//Mandar : CDE xml package path.
	//public static final String CDE_XML_PACKAGE_PATH = "edu.wustl.common.cde.xml";
	/**
	 * Constant for BOOLEAN_YES.
	 */
	public static final String BOOLEAN_YES = "Yes";
	/**
	 * Constant for BOOLEAN_NO.
	 */
	public static final String BOOLEAN_NO = "No";
	/**
	 * Constant for SESSION_DATA.
	 */
	public static final String SESSION_DATA = "sessionData";
	/**
	 * Constant for TEMP_SESSION_DATA.
	 */
	public static final String TEMP_SESSION_DATA = "tempSessionData";
	/**
	 * Constant for ACCESS.
	 */
	public static final String ACCESS = "access";
	/**
	 * Constant for PASSWORD_CHANGE_IN_SESSION.
	 */
	public static final String PASSWORD_CHANGE_IN_SESSION = "changepassword";

	//public static final String USER_CLASS_NAME = "edu.wustl.common.domain.User";

	/**
	 * Constant for IDENTIFIER.
	 */
	public static final String IDENTIFIER = "IDENTIFIER";
	//SimplQuery and catissue.
	public static final String FIELD_TYPE_DATE = "date";
	public static final String FIELD_TYPE_TIMESTAMP_DATE = "timestampdate";

	//Simple Query
	/*public static final String TABLE_ALIAS_NAME_COLUMN = "ALIAS_NAME";
	public static final String TABLE_DATA_TABLE_NAME = "CATISSUE_QUERY_TABLE_DATA";
	public static final String TABLE_DISPLAY_NAME_COLUMN = "DISPLAY_NAME";

	public static final String TABLE_FOR_SQI_COLUMN = "FOR_SQI";

	public static final String TABLE_ID_COLUMN = "TABLE_ID";*/
	//SimplQuery and catissue.
	public static final String FIELD_TYPE_TIMESTAMP_TIME = "timestamptime";
	//public static final String CDE_NAME_TISSUE_SITE = "Tissue Site";

	//public static final String UPPER = "UPPER";
	//datatypes
	public static final String TIME_PATTERN_HH_MM_SS = "HH:mm:ss";

	/**
	 * menu selection related.
	 */
	public static final String MENU_SELECTED = "menuSelected";

	/**
	 * Constant for PAGEOF.
	 */
	public static final String PAGEOF = "pageOf";

	/**
	 * Constant for SPREADSHEET_COLUMN_LIST.
	 */
	public static final String SPREADSHEET_COLUMN_LIST = "spreadsheetColumnList";
	/**
	 * Constant for QUERY_SESSION_DATA.
	 */
	public static final String QUERY_SESSION_DATA = "querySessionData";

	/**
	 * Constant for ACCESS_DENIED.
	 */
	public static final String ACCESS_DENIED = "access_denied";
	/**
	 *  Constant for COLLECTION_PROTOCOL.
	 */
	public static final String COLLECTION_PROTOCOL = "CollectionProtocol";

	//Added By Ramya
	//Constants to display Specimen Tree in RequestDetails.jsp
	/**
	 * Constant for SPECIMEN_TREE_ID.
	 */
	public static final int SPECIMEN_TREE_ID = 4;
	/**
	 * Constant for SPECIMEN_TYPE.
	 */
	public static final String SPECIMEN_TYPE = "type";
	/**
	 * Constant for SPECIMEN_CLASS.
	 */
	public static final String SPECIMEN_CLASS = "specimenClass";
	/**
	 * Constant for SPECIMEN_TREE_ROOT_NAME.
	 */
	public static final String SPECIMEN_TREE_ROOT_NAME = "Specimens";

	/**
	 * Experiment Module.
	 */
	public static final int EXPERIMETN_TREE_ID = 5;

	/**
	 * Constant for SUBMITTED_FOR.
	 */
	public static final String SUBMITTED_FOR = "submittedFor";
	/**
	 * Constant for SUBMITTED_FOR_ADD_NEW.
	 */
	public static final String SUBMITTED_FOR_ADD_NEW = "AddNew";
	/**
	 * Constant for SUBMITTED_FOR_FORWARD_TO.
	 */
	public static final String SUBMITTED_FOR_FORWARD_TO = "ForwardTo";
	/**
	 * Constant for SUBMITTED_FOR_DEFAULT.
	 */
	public static final String SUBMITTED_FOR_DEFAULT = "Default";
	/**
	 * Constant for FORM_BEAN_STACK.
	 */
	public static final String FORM_BEAN_STACK = "formBeanStack";
	/**
	 * Constant for ADD_NEW_FORWARD_TO.
	 */
	public static final String ADD_NEW_FORWARD_TO = "addNewForwardTo";
	/**
	 * Constant for FORWARD_TO.
	 */
	public static final String FORWARD_TO = "forwardTo";
	/**
	 * Constant for ADD_NEW_FOR.
	 */
	public static final String ADD_NEW_FOR = "addNewFor";

	/**
	 * Constant for ERROR_DETAIL.
	 */
	public static final String ERROR_DETAIL = "Error Detail";

	/**
	 * Identifiers for various Form beans.
	 */
	public static final int QUERY_INTERFACE_ID = 43;

	/**
	 * Status message key Constants.
	 */
	public static final String STATUS_MESSAGE_KEY = "statusMessageKey";

	/***  Added New Constansts.  ***/

	//	Activity Status values
	public static final String ACTIVITY_STATUS_APPROVE = "Approve";
	public static final String ACTIVITY_STATUS_REJECT = "Reject";
	public static final String ACTIVITY_STATUS_NEW = "New";
	public static final String ACTIVITY_STATUS_PENDING = "Pending";

	//Approve User status values.
	public static final String APPROVE_USER_APPROVE_STATUS = "Approve";
	public static final String APPROVE_USER_REJECT_STATUS = "Reject";
	public static final String APPROVE_USER_PENDING_STATUS = "Pending";

	//	Query Interface Results View Constants
	/**
	 * Constant for QUERY.
	 */
	public static final String QUERY = "query";

	//Approve User Constants.
	/**
	 * Constant for ZERO.
	 */
	public static final int ZERO = 0;
	/**
	 * Constant for START_PAGE.
	 */
	public static final int START_PAGE = 1;
	/**
	 * Constant for NUMBER_RESULTS_PER_PAGE.
	 */
	public static final int NUMBER_RESULTS_PER_PAGE = 5;
	/**
	 * Constant for PAGE_NUMBER.
	 */
	public static final String PAGE_NUMBER = "pageNum";
	/**
	 * Constant for RESULTS_PER_PAGE.
	 */
	public static final String RESULTS_PER_PAGE = "numResultsPerPage";
	/**
	 * Constant for RECORDS_PER_PAGE_PROPERTY_NAME.
	 */
	public static final String RECORDS_PER_PAGE_PROPERTY_NAME = "resultView.noOfRecordsPerPage";
	/**
	 * Constant for TOTAL_RESULTS.
	 */
	public static final String TOTAL_RESULTS = "totalResults";
	/**
	 * Constant for ORIGINAL_DOMAIN_OBJECT_LIST.
	 */
	public static final String ORIGINAL_DOMAIN_OBJECT_LIST = "originalDomainObjectList";
	/**
	 * Constant for SHOW_DOMAIN_OBJECT_LIST.
	 */
	public static final String SHOW_DOMAIN_OBJECT_LIST = "showDomainObjectList";

	/**
	 * RESULT_PERPAGE_OPTIONS.
	 */
	public static final int[] RESULT_PERPAGE_OPTIONS = {10, 50, 100, 500, Integer.MAX_VALUE};

	/**
	 * Constant for EXPORT_FILE_NAME_START.
	 */
	public static final String EXPORT_FILE_NAME_START = "Report_Content_";

	/**
	 * Constant for ONE.
	 */
	public static final int ONE = 1;
	/**
	 * Constant for ONE_KILO_BYTES.
	 */
	public static final int ONE_KILO_BYTES = 1024;
	/**
	 * Constant for FOUR_KILO_BYTES.
	 */
	public static final int FOUR_KILO_BYTES = 4096;
	/**
	 * constant for CONST_SPACE_CAHR.
	 */
	public static final char CONST_SPACE_CAHR = ' ';
	/**
	 * constant for ALLOW_OPERATION.
	 */
	public static final String ALLOW_OPERATION = "allowOperation";
	/**
	 * constant for DB2_DATABASE.
	 */
	public static final String DB2_DATABASE = "DB2";
	/**
	 * constant for BIRTH_DATE_TAG_NAME.
	 */
	public static final String BIRTH_DATE_TAG_NAME = "IS_BIRTH_DATE";
	/**
	 * constant for TABLE_ALIAS_NAME.
	 */
	public static final String TABLE_ALIAS_NAME = "aliasName";
}
