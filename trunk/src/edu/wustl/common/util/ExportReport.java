package edu.wustl.common.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
/**
 * This class for creating a file with a given list of data.
 * It creates the file according to delimeter specified.
 * For eg: if comma is the delimter specified then a CSV file is created. 
 * @author Poornima Govindrao
 *  
 */

public class ExportReport 
{
	private BufferedWriter temp;
	public ExportReport(String fileName) throws IOException
	{
		temp =  new BufferedWriter(new FileWriter(fileName));
	}
	public void writeData(List values,String delimiter) throws IOException
	{
		//Writes the list of data into file 
		String newLine = System.getProperty(("line.separator"));
		if (values != null) 
		{
			Iterator itr = values.iterator();
			while (itr.hasNext()) 
			{
				List rowValues = (List) itr.next();
				Iterator rowItr = rowValues.iterator();
				while (rowItr.hasNext()) 
				{
					String tempStr = (String) rowItr.next();
					if (tempStr == null)
						tempStr = "*";
					if (tempStr.indexOf(',') > 0)
						tempStr = "\"" + tempStr + "\"";
					String data = tempStr + delimiter;
					temp.write(data);
				}
				temp.write(newLine);
			}
		}
	}

	/**
	 * @param values 
	 * @param delimiter
	 * @param noblankLines No of blank lines added before values
	 * @param columnIndent No columns that will be left blank for values
	 * @throws IOException
	 */
	public void writeData(List values,String delimiter,int noblankLines,int columnIndent) throws IOException
	{
		//Writes the list of data into file 
		String newLine = System.getProperty(("line.separator"));

		for(int i=0; i < noblankLines;i++) {
			temp.write(newLine); 
		}
		
		if (values != null) 
		{
			Iterator itr = values.iterator();
			while (itr.hasNext()) 
			{
				List rowValues = (List) itr.next();
				Iterator rowItr = rowValues.iterator();
				
				for(int i=0; i < columnIndent;i++) {
					temp.write("" + delimiter); 
				}
				
				
				while (rowItr.hasNext()) 
				{
					String tempStr = (String) rowItr.next();
					if (tempStr == null)
						tempStr = "";
					if (tempStr.indexOf(',') > 0)
						tempStr = "\"" + tempStr + "\"";
					String data = tempStr + delimiter;
					temp.write(data);
				}
				temp.write(newLine);
			}
		}
	}

	public void closeFile() throws IOException
	{
		temp.close();
	}
}