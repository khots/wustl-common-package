package edu.wustl.common.util;

import javax.servlet.http.HttpServletResponse;

import edu.wustl.common.CommonBaseTestCase;


public class SendFileTestCase extends CommonBaseTestCase
{
	public void testSendFileToClient()
	{
		String filePath= new StringBuffer(System.getProperty("user.dir")).
		append(System.getProperty("file.separator")).
		append("src/edu/wustl/common/util/ExportReport.java").toString();
		String fileName="ExportReport.java";
		try
		{
			HttpServletResponse response = new MockHttpServletResponse();
			SendFile.sendFileToClient(response, filePath, fileName, "txt");
			assertTrue("File send successfully.", true);
		}
		catch(Exception exception)
		{
			fail("File not send successfully.");
		}
			
		
		
	}
}
