
package edu.wustl.common.datahandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ravindra_jain
 * @version 1.0
 * @created 21-Apr-2009 6:57:51 PM
 */
public class TextDataHandler extends AbstractDataHandler
{

	private BufferedWriter bufferedWriter;
	private int bufferSize;

	/**
	 *
	 * @param fileName
	 */
	TextDataHandler(String fileName)
	{

	}

	/**
	 *
	 * @param fileName
	 * @param bufferSize
	 */
	TextDataHandler(String fileName, int bufferSize)
	{

	}

	@Override
	public void openFile() throws IOException
	{
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param values
	 */
	public void appendData(List<Object> values)
	{

	}

	/**
	 *
	 * @param values
	 */
	public void appendData(String values)
	{

	}

	/**
	 *
	 */
	public void closeFile()
	{

	}

	@Override
	public void flush() throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void openSheet() throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void saveSheet(String sheetName) throws IOException
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void addHeader(List<Object> headers) throws IOException
	{
		// TODO Auto-generated method stub

	}


}