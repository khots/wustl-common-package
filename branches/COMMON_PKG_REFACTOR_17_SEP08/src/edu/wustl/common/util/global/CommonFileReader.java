
package edu.wustl.common.util.global;

import java.io.BufferedReader;
import java.io.FileReader;

public class CommonFileReader
{

	/**
	 * @param args
	 */
	public String readData(String fileName)
	{
		StringBuffer buffer = new StringBuffer();
		try
		{
			BufferedReader bufferReader = new BufferedReader(new FileReader(fileName));
			String line = TextConstants.EMPTY_STRING;
			while ((line = bufferReader.readLine()) != null)
			{
				buffer.append(line).append("<br>");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return buffer.toString();
	}

	//	public String getFileName(String key)
	//	{
	//		ResourceBundle myResources = ResourceBundle.getBundle("ApplicationResources");
	//		String fileName = myResources.getString(key);
	//		return fileName;
	//	}

}
