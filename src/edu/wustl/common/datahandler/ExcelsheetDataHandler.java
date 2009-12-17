
package edu.wustl.common.datahandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Data handler to create Excel sheet.
 * @author vijay_pande
 * @version 1.0
 */
public class ExcelsheetDataHandler extends AbstractDataHandler
{
	private OutputStream out;
	private Workbook book;
	private Sheet sheet;
	private int rowCount;

	/**
	 * Parameterized constructor.
	 * @param excelFileName excel sheet file name
	 */
	public ExcelsheetDataHandler(String excelFileName)
	{
		super();
		this.fileName = excelFileName;
	}

	/**
	 * Method to append data.
	 * @param values data to be appended
	 */
	@Override
	public void appendData(List<Object> values) throws IOException
	{
		Row row = sheet.createRow(rowCount++);
		Cell cell;
		short cellCounter=0;
		for(Object obj: values)
		{
			if(obj == null)
			{
				obj = new String("");
			}
			cell = row.createCell(cellCounter++);
			cell.setCellValue(obj.toString());
		}
	}

	/**
	 * Method to append data.
	 * @param values data to be appended
	 */
	@Override
	public void appendData(String values)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Method to close file.
	 */
	@Override
	public void closeFile() throws IOException
	{
		out = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
		if(out!=null)
		{
			book.write(out);
			out.close();
		}
	}

	/**
	 * Method to flush.
	 */
	@Override
	public void flush() throws IOException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Method to open file.
	 */
	@Override
	public void openFile() throws IOException
	{
		book = new XSSFWorkbook();
		sheet = book.createSheet();
		rowCount = 0;
	}

}
