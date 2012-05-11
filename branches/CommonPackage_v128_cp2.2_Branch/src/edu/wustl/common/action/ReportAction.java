
package edu.wustl.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


import edu.wustl.common.actionForm.ReportForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.report.ReportGenerator;
import edu.wustl.common.report.bean.FileDetails;
import edu.wustl.common.report.bean.ReportBean;
import edu.wustl.common.util.SendFile;
import edu.wustl.common.util.Utility;

public class ReportAction extends SecureAction
{

	@Override
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ReportForm hsForm = (ReportForm) form;

		String reportName = hsForm.getReportName();
		if (reportName != null)
		{
			FileDetails fileDetails = Utility.generateFilePath(reportName);
			fileDetails.setFileName(fileDetails.getFileName()
					+ edu.wustl.common.util.global.Constants.CSV_FILE_EXTENTION);
			fileDetails.setFilePath(fileDetails.getFilePath()
					+ edu.wustl.common.util.global.Constants.CSV_FILE_EXTENTION);
			SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
					"sessionData");
			ReportBean reportBean = new ReportBean();
			reportBean.setSessionDataBean(sessionDataBean);
			reportBean.setAllValues(hsForm.getReportName(), hsForm.getFromDate(),
					hsForm.getToDate());
			reportBean.setFileDetails(fileDetails);
			ReportGenerator reportGenerator = ReportGenerator.getImplObj(reportName);
			reportGenerator.generateCSV(reportBean);
			SendFile.sendFileToClient(response, fileDetails.getFilePath(),
					fileDetails.getFileName(), "application/download");
		}
		return null;
	}

}
