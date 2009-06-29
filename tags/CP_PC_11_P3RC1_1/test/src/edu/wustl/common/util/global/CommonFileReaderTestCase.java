package edu.wustl.common.util.global;

import edu.wustl.common.CommonBaseTestCase;


public class CommonFileReaderTestCase extends CommonBaseTestCase
{
	public void testReadData()
	{
		String fileName= new StringBuffer(System.getProperty("user.dir")).
		append(System.getProperty("file.separator")).
		append("src/edu/wustl/common/util/global/CommonFileReader.java").toString();
		String data=new CommonFileReader().readData(fileName);
		assertTrue(data.length()>0);
	}

	public void testFailReadData()
	{
		String data=new CommonFileReader().readData("fileName");
		assertFalse(data.length()>0);
	}
}
