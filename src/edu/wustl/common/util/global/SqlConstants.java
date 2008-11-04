package edu.wustl.common.util.global;


public class SqlConstants
{
	public static final String SQL_REPORT_DATA
	= "SELECT catissue_report_content.IDENTIFIER,REPORT_DATA FROM catissue_report_content,catissue_report_textcontent "
		+ " WHERE catissue_report_content.IDENTIFIER = catissue_report_textcontent.REPORT_ID "
		+ " AND catissue_report_textcontent.REPORT_ID IN ( ";

	public static final String SQL_ENTITY_NAMES = "select IDENTIFIER, NAME from dyextn_abstract_metadata WHERE CREATED_DATE IS NOT NULL";

	public static final String SQL_CORRUPTED_ASSOCIATION =
		"select ASSOCIATION_ID, DE_ASSOCIATION_ID From intra_model_association where de_association_id not in (select IDENTIFIER from dyextn_association)";

	public static final String SQL_PATH =
		"select PATH_ID,FIRST_ENTITY_ID,INTERMEDIATE_PATH,LAST_ENTITY_ID from PATH where INTERMEDIATE_PATH like ";
}
