/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.wustl.common.scheduler.util.ReportSchedulerUtil;

public class CustomReportGenerator extends ReportGenerator
{

	String reportCommand = "";

	public CustomReportGenerator(String reportCommand)
	{
		this.reportCommand = reportCommand;
	}

	/*protected void getReportData(AbstractDataHandler handler, ReportBean repoBean)
			throws IOException, DAOException, BizLogicException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		super.getReportData(handler, repoBean);

		String line;

		StringTokenizer strTokenizer = new StringTokenizer(reportCommand);
		List<String> cmmdList = new ArrayList<String>();
		while (strTokenizer.hasMoreTokens())
		{
			cmmdList.add(strTokenizer.nextToken());
		}
		cmmdList.add(repoBean.getToDate());
		cmmdList.add(repoBean.getFromDate());
		cmmdList.add(repoBean.getFileDetails().getFilePath());
		cmmdList.add(repoBean.getReportName());

		String[] cmmdArray = new String[cmmdList.size()];
		cmmdList.toArray(cmmdArray);

		Process p = Runtime.getRuntime().exec(cmmdArray, null,
				new File(System.getProperty("app.customReportsDirPath")));
		BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = bri.readLine()) != null)
		{
			System.out.println(line);
		}
		bri.close();
		while ((line = bre.readLine()) != null)
		{
			System.out.println(line);
		}
		bre.close();

		//        p.waitFor();
		//        int exitVal = p.exitValue();
		//        System.out.println("Process exitValue: " + exitVal);
	}*/

	/* (non-Javadoc)
	 * @see edu.wustl.common.report.ReportGenerator#generateReportData()
	 */
	@Override
	public void generateReport(Long ticketId, List<String> dbDetails) throws Exception
	{
		StringTokenizer strTokenizer = new StringTokenizer(reportCommand);
		List<String> cmmdList = new ArrayList<String>();
		while (strTokenizer.hasMoreTokens())
		{
			cmmdList.add(strTokenizer.nextToken());
		}
		cmmdList.add(ticketId.toString());
		cmmdList.addAll(dbDetails);
		cmmdList.add(ReportSchedulerUtil.getReportDirPath());
		
		String[] cmmdArray = new String[cmmdList.size()];
		cmmdList.toArray(cmmdArray);
		
		Process process= Runtime.getRuntime().exec(cmmdArray, null,
				new File(System.getProperty("app.customReportScriptDir")));
		process.waitFor();
		
		process.exitValue();
	}

}
