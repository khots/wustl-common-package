
package edu.wustl.common.datahandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.wustl.common.util.global.Constants;

/**
 * 
 * @author ravindra_jain
 * @version 1.0
 * @created 21-Apr-2009 6:57:50 PM
 */
public class CSVDataHandler extends AbstractDataHandler
{

	private BufferedWriter bufferedWriter;
	private Integer bufferSize;
	private static int rowCount;
	private String delimiter;
	
	/**
	 * PRIVATE CONSTRUCTOR
	 * @param fileName
	 * @param bufferSize
	 * @throws IOException 
	 */
	CSVDataHandler(String fileName, Integer bufferSize, String delimiter)
	{
		if(bufferSize == null)
		{
			// Default value
			bufferSize = 100;
		}
		if (bufferSize <= 0)
		{
			throw new IllegalArgumentException("BufferSize (Row Count) should be > 0");
		}
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		if(delimiter == null)
		{
			delimiter = Constants.DELIMETER;
		}
		this.delimiter = delimiter;
	}

	@Override
	public void openFile() throws IOException
	{
		bufferedWriter = new BufferedWriter(new FileWriter(fileName));
	}

	/**
	 * 
	 * @param values
	 */
	public void appendData(String values)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param values
	 * @throws IOException 
	 */
	public void appendData(List<Object> values) throws IOException
	{
		//Writes the list of data into file 
		String newLine = System.getProperty(("line.separator"));

		if (values != null)
		{
			for (Object obj : values)
			{
				if(obj == null)
				{
					obj = new String("");
				}
				String value = obj.toString();
				value = value.replaceAll("\"", "'");
				value = "\"" + value + "\"";
				String data = value + delimiter;
				bufferedWriter.write(data);
			}
			rowCount++;
			bufferedWriter.write(newLine);
		}

		if (rowCount == bufferSize)
		{
			flush();
			rowCount = 0;
		}
	}

	/**
	 * 
	 * @throws IOException 
	 */
	public void flush() throws IOException
	{
		bufferedWriter.flush();
	}

	/**
	 * 
	 * @throws IOException 
	 * 
	 */
	public void closeFile() throws IOException
	{
		if (bufferedWriter != null)
		{
			bufferedWriter.flush();
			bufferedWriter.close();
		}
	}
}