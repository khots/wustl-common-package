
package edu.wustl.common.datahandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.wustl.common.util.global.Constants;

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
	private String sheetName;

	/**
	 * Parameterized constructor.
	 * @param excelFileName excel sheet file name
	 */
	public ExcelsheetDataHandler(String excelFileName,String sheetName)
	{
		super();
		this.fileName = excelFileName;
		this.sheetName=sheetName;
	}
	/**
	 * 	Method is to get the valid sheet name.
	 *	added by amit_doshi date:25 Dec 2009.
	 * @param sheetName
	 * @return
	 */
	private String getValidSheetName(final String sheetName)
	{
		String newSheetName=sheetName;
		final int length=sheetName.length();
		if(length>30)
		{
			newSheetName=sheetName.substring(0,14)+Constants.UNDERSCORE+sheetName.substring(length-14);
		}
		return newSheetName;
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
		addSheet();
		rowCount = 0;
	}

	/**
	 *  Method to read the existing workBook.
	 *  added by amit_doshi date:25 Dec 2009.
	 */
	 private XSSFWorkbook readWoorkBook(final String filename) throws IOException {
         return new XSSFWorkbook(new FileInputStream(filename));
	 }

     /**
      * Method to add the sheet in workbook.
      * added by amit_doshi date:25 Dec 2009.
      * @throws IOException
      *
      */
	 private synchronized void addSheet() throws IOException
     {
    	final File file=new File(fileName);
    	if( ! file.exists())
 		{
    		 createSheet();
 		}
    	else
    	{
    		book = readWoorkBook(fileName);
    		createSheet();
    	}
     }
	 /**
	  *  Method to create sheet in workbook
	  *  added by amit_doshi date:25 Dec 2009.
	  */
	private void createSheet()
	{
		if(sheetName!=null)
		{
			sheet=book.createSheet(getValidSheetName(sheetName));
		}
		else
		{
			sheet=book.createSheet();
		}


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


