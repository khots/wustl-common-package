
package edu.wustl.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import oracle.sql.CLOB;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.SqlConstants;
import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * This class for creating a file with a given list of data.
 * It creates the file according to delimeter specified.
 * For eg: if comma is the delimter specified then a CSV file is created. 
 * @author Poornima Govindrao
 * @author deepti_shelar
 * @author Supriya Dankh
 *  
 */

public class ExportReport
{
	private BufferedWriter temp;
	private String zipFileName;
	private String path;
	private BufferedWriter cvsFileWriter;
	private String fileName;
	private static org.apache.log4j.Logger logger = Logger.getLogger(ExportReport.class);

	/**
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public ExportReport(String fileName) throws IOException
	{
		temp = new BufferedWriter(new FileWriter(fileName));
	}

	/**
	 * This method creates the file according to delimeter specified, without any indentation
	 * for the any row and no blank lines are inserted before start of values.
	 * 
	 * @param values
	 * @param delimiter
	 * @throws IOException
	 */
	public void writeData(List values, String delimiter) throws IOException
	{
		writeData(values, delimiter, 0, 0);
	}

	/**
	 * This constructor is called when the exported list contains data for 'file' attributes.
	 * 
	 * @param path
	 * @param csvFileName
	 * @param zipFileName
	 * @throws IOException
	 */
	public ExportReport(String path, String csvFileName, String zipFileName) throws IOException
	{
		this.path = path;
		this.zipFileName = zipFileName;
		this.fileName = csvFileName;
		this.cvsFileWriter = new BufferedWriter(new FileWriter(csvFileName));
	}

	/**
	 * 
	 * @param values
	 * @param delimiter
	 * @param mainEntityIdsList
	 * @throws IOException
	 */
	public void writeDataToZip(List values, String delimiter, List mainEntityIdsList)
			throws IOException
	{
		writeDataToZip(values, delimiter, 0, 0, mainEntityIdsList);
	}

	/**
	 * This method creates a zip file which contains a csv file and other data files.
	 * The file is according to delimeter specified.
	 * 
	 * @param values values list. It is List of List. 
	 * @param delimiter delimiter used for separting individaul fields.
	 * @param noblankLines No of blank lines added before values
	 * @param columnIndent No columns that will be left blank for values
	 * @param mainEntityIdsList list of main entity ids : required in case of file exports.
	 * @throws IOException
	 */
	public void writeDataToZip(List values, String delimiter, int noblankLines, int columnIndent,
			List mainEntityIdsList) throws IOException
	{
		List<String> files = new ArrayList<String>();
		Map<String, String> idFileNameMap = new HashMap<String, String>();
		if (mainEntityIdsList != null && !mainEntityIdsList.isEmpty())
		{
			StringBuffer sql=new StringBuffer(SqlConstants.SQL_REPORT_DATA);
			for (Iterator iterator = mainEntityIdsList.iterator(); iterator.hasNext();)
			{
				String mainEntityId = (String) iterator.next();
				sql.append(mainEntityId).append(',');
				String file = Constants.EXPORT_FILE_NAME_START + mainEntityId + ".txt";
				files.add(path + file);
				idFileNameMap.put(mainEntityId + ".txt", file);
			}
			sql = new StringBuffer(sql.substring(0, sql.lastIndexOf(",")));
			sql.append(')');
			createDataFiles(sql.toString());
		}
		createCSVFile(values, delimiter, noblankLines, columnIndent, idFileNameMap);
		closeFile();
		files.add(fileName);
		createZip(files);
	}

	/**
	 * Creates text files for reports.
	 * @param sql to get the reports 
	 */
	private void createDataFiles(String sql)
	{
		try
		{
			List list = executeQuery(sql);
			if (!list.isEmpty())
			{
				Iterator iterator = list.iterator();
				while (iterator.hasNext())
				{
					List columnList = (List) iterator.next();
					createFile(columnList);
				}
			}
		}
		catch (Exception excp)
		{
			logger.error(excp.getMessage(), excp);
		}
	}

	/**
	 * @param columnList
	 * @throws SQLException
	 * @throws IOException
	 */
	private void createFile(List columnList) throws SQLException, IOException
	{
		CLOB clob;
		BufferedReader bufferReader;
		if (!columnList.isEmpty())
		{
			if (columnList.get(1) instanceof CLOB)
			{
				clob = (CLOB) columnList.get(1);
				bufferReader = new BufferedReader(clob.getCharacterStream());
			}
			else
			{
				String data = (String) columnList.get(1);
				bufferReader = new BufferedReader(new StringReader(data));
			}
			String mainEntityId = (String) columnList.get(0);
			String dataFileName = path + Constants.EXPORT_FILE_NAME_START + mainEntityId + ".txt";
			File outFile = new File(dataFileName);
			FileWriter out = new FileWriter(outFile);
			StringBuffer strOut = new StringBuffer();
			String aux;
			while ((aux = bufferReader.readLine()) != null)
			{
				strOut.append(aux);
				strOut.append(TextConstants.LINE_SEPARATOR);
			}
			out.write(strOut.toString());
			out.close();
		}
	}

	/**
	 * Creates a csv file
	 * @param values list data
	 * @param delimiter comma
	 * @param noblankLines no of blank lines
	 * @param columnIndent 
	 * @param idFileNameMap map which stores id and file name 
	 * @throws IOException 
	 */
	private void createCSVFile(List values, String delimiter, int noblankLines, int columnIndent,
			Map<String, String> idFileNameMap) throws IOException
	{
		for (int i = 0; i < noblankLines; i++)
		{
			cvsFileWriter.write(TextConstants.LINE_SEPARATOR);
		}
		if (values != null)
		{
			Iterator itr = values.iterator();
			while (itr.hasNext())
			{
				List rowValues = (List) itr.next();
				Iterator rowItr = rowValues.iterator();

				for (int i = 0; i < columnIndent; i++)
				{
					cvsFileWriter.write(delimiter);
				}
				while (rowItr.hasNext())
				{
					String tempStr = (String) rowItr.next();
					if (tempStr.indexOf(Constants.EXPORT_FILE_NAME_START) != -1)
					{
						String[] split = tempStr.split("_");
						String entityId = split[2];
						String fName = idFileNameMap.get(entityId);
						tempStr = fName;
					}
					if (tempStr == null)
					{
						tempStr = TextConstants.EMPTY_STRING;
					}
					tempStr = tempStr.replaceAll("\"", "'");
					tempStr = "\"" + tempStr + "\"";
					cvsFileWriter.write(tempStr + delimiter);
				}
				cvsFileWriter.write(TextConstants.LINE_SEPARATOR);
			}
		}
	}

	/**
	 * Closes file stream
	 * @throws IOException
	 */
	public void closeFile() throws IOException
	{
		if (temp != null)
		{
			temp.close();
		}
		if (cvsFileWriter != null)
		{
			cvsFileWriter.close();
		}
	}

	/**
	 * This method creates the file according to delimeter specified.
	 * 
	 * @param values values list. It is List of List. 
	 * @param delimiter delimiter used for separting individaul fields.
	 * @param noblankLines No of blank lines added before values
	 * @param columnIndent No columns that will be left blank for values
	 * @throws IOException
	 */
	public void writeData(List values, String delimiter, int noblankLines, int columnIndent)
			throws IOException
	{
		//Writes the list of data into file 
		for (int i = 0; i < noblankLines; i++)
		{
			temp.write(TextConstants.LINE_SEPARATOR);
		}

		if (values != null)
		{
			Iterator itr = values.iterator();
			while (itr.hasNext())
			{
				List rowValues = (List) itr.next();
				Iterator rowItr = rowValues.iterator();

				for (int i = 0; i < columnIndent; i++)
				{
					temp.write(delimiter);
				}

				while (rowItr.hasNext())
				{
					String tempStr = (String) rowItr.next();
					if (tempStr == null)
					{
						tempStr = TextConstants.EMPTY_STRING;
					}
					tempStr = tempStr.replaceAll("\"", "'");
					tempStr = "\"" + tempStr + "\"";
					temp.write(tempStr + delimiter);
				}
				temp.write(TextConstants.LINE_SEPARATOR);
			}
		}
	}

	/**
	 * Creates a zip file , contains a csv and other txt files
	 * @param files files to be compressed
	 */
	public void createZip(List<String> files)
	{
		try
		{
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(this.zipFileName));
			File file;	
			for (Iterator<String> iterator = files.iterator(); iterator.hasNext();)
			{
				String fileName = (String) iterator.next();
				file = new File(fileName);
				if (fileName.indexOf("csv") == -1)
				{
					putFileToZip(out, fileName);
				}
				else
				{
					putCSVFileToZip(out, fileName);
				}
				file.delete();
			}
			out.close();
		}
		catch (IOException excp)
		{
			logger.error(excp.getMessage(), excp);
		}
	}

	/**
	 * @param buf
	 * @param out
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void putFileToZip(ZipOutputStream out, String fileName) throws IOException
	{
		byte[] buf = new byte[Constants.ONE_KILO_BYTES];
		FileInputStream inputReader = new FileInputStream(fileName);
		out.putNextEntry(new ZipEntry(fileName));
		int len;
		while ((len = inputReader.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
		out.closeEntry();
		inputReader.close();
	}

	/**
	 * @param out
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void putCSVFileToZip(ZipOutputStream out, String fileName) throws IOException
	{
		BufferedReader bufRdr = new BufferedReader(new FileReader(fileName));
		String line = null;
		StringTokenizer tokenizer;
		out.putNextEntry(new ZipEntry(fileName));
		while ((line = bufRdr.readLine()) != null)
		{
			tokenizer = new StringTokenizer(line, ",");
			while (tokenizer.hasMoreTokens())
			{
				String token = tokenizer.nextToken();
				out.write(token.getBytes(), 0, token.length());
				token = ",";
				out.write(token.getBytes(), 0, token.length());

			}
			out.write(TextConstants.LINE_SEPARATOR.getBytes(), 0, TextConstants.LINE_SEPARATOR.length());
		}
		out.closeEntry();
		bufRdr.close();
	}

	/**
	 * Executes sql and returns data list. 
	 * @param sql
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	private List<List<String>> executeQuery(String sql) throws DAOException, ClassNotFoundException
	{
		JDBCDAO dao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		dao.openSession(null);
		return (List<List<String>>) dao.executeQuery(sql, null, false, null);

	}
}