package edu.wustl.common.util.global;


public class SqlConstants
{
	public static final String SQL_REPORT_DATA = "SELECT catissue_report_content.IDENTIFIER,REPORT_DATA FROM catissue_report_content,catissue_report_textcontent "
		+ " WHERE catissue_report_content.IDENTIFIER = catissue_report_textcontent.REPORT_ID "
		+ " AND catissue_report_textcontent.REPORT_ID IN ( ";
}
