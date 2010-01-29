package edu.wustl.common.datahandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang.StringEscapeUtils;
import edu.wustl.common.util.global.Constants;


/**
 *
 *
 * This class is to generate large workbooks and avoid OutOfMemory exception.
 *
 * The steps is as follows:
 * 1. create a template workbook, create sheets and global objects such as cell styles, number formats, etc.
 * 2. create an application that streams data in a text file
 * 3. Substitute the sheet in the template with the generated data
 *
 * @author amit_doshi
 * @version 1.0
 * @created 14 JAN 2010
 *
 */
public class ExcelsheetForLargeDataHandler extends AbstractDataHandler
{
	/**
	 * XSSFWorkbook.
	 */
	private XSSFWorkbook book;
	/**
	 * Number of sheets need to be created.
	 */
	private List<String> sheetNames;
	/**
	 * tmpXmlFile to store the data into the tmp file, e.g. sheet.xml file.
	 */
	private File tmpXmlFile;
	/**
	 * tmpXmlFileWriter to write the data into the tmp, e.g. sheet.xml file.
	 */
	private Writer tmpXmlFileWriter;
	/**
	 * cell styles, number formats, etc.
	 */
	Map<String, XSSFCellStyle> styles;
	/**
	 * SpreadsheetWriter to write data into tmp XML file.
	 */
	SpreadsheetWriter sheetWriter;
	/**
	 * Sheet Row Count.
	 */
	int rowCount=0;
	/**
	 * Constant for template file.
	 */
	final private File TEMPLATE_WORKBOOK=getTemplateFile();
	/**
	 * Constant for Excel file extn.
	 */
	final private static  String XLSX_FILE_TYPE=".xlsx";
	/**
	 * Constant for Excel file TEMP String.
	 */
	final private static  String TEMP_STR="-tmp";


	/**
	 * Parameterized constructor.
	 * @param excelFileName excel sheet file name
	 */
	public ExcelsheetForLargeDataHandler(final String excelFileName,final List<String> sheetNames)
	{
		super();
		this.fileName = excelFileName;
		this.sheetNames=sheetNames;
	}
	/**
	 * Returns the template file object
	 * @return File
	 */
	private File getTemplateFile()
	{
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
		Date date=new Date();
		File templateFile= new File(dateFormat.format(date)+"template"+XLSX_FILE_TYPE);
		return templateFile;
	}
	/**
	 * Method  to create Header for the Excel Sheet.
	 */
	@Override
	public void addHeader(final List<Object> headers) throws IOException
	{
		int cellCount=0;
		//insert header row
        sheetWriter.insertRow(rowCount);
        for(Object obj: headers)
		{
        	if(obj == null)
			{
				obj = "";
			}
	       final int styleIndex = styles.get("header").getIndex();

	        sheetWriter.createCell(cellCount++, StringEscapeUtils.escapeXml(obj.toString()), styleIndex);
	 	}
         sheetWriter.endRow();
         rowCount++;
	}
	/**
	 * Method to append data.
	 * @param values data to be appended
	 */
	@Override
	public void appendData(final List<Object> values) throws IOException
	{
        //insert  row
		int cellCount=0;
		sheetWriter.insertRow(rowCount);
        for(Object obj: values)
		{
        	if(obj == null)
			{
				obj = "";
			}
	        sheetWriter.createCell(cellCount++, StringEscapeUtils.escapeXml(obj.toString()));
	 	}
        sheetWriter.endRow();
        rowCount++;
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
	 * delete the tmp file used while creating .xslx file.
	 */
	@Override
	public void closeFile() throws IOException
	{
		File file=new File(getTempFileName(fileName));
		if(file.exists())
		{
			file.delete();
		}

		if(TEMPLATE_WORKBOOK.exists())
		{
			file.delete();
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
		styles = createStyles(book);
		addSheets();
	}
	/**
     * Method to add the sheets in workbook in one go.
     * @throws IOException
     *
     */
	 private synchronized void addSheets() throws IOException
    {
		 for(Object sheetName: sheetNames)
		 {
			 book.createSheet(getValidSheetName(sheetName.toString()));
		 }
    }
	 /**
	  * method is used to open sheet, i.e create tmp file and
	  */
	@Override
	public void openSheet() throws IOException
	{
		final File file=new File(fileName);
		rowCount=0;
		if( file.exists())
		{
			/* step 1: create a copy of existing file as template
			 * because now the existing file will be used as template.
			 */
			copy(file,new File(getTempFileName(fileName)));
		}
		else
		{
			// step 1: create template for workbook and save it.
	 		saveTemplate();
	 	}
		//Step 2. Generate XML file.
		createTempXmlFile();
		//beginSheet to writing data as XML nodes in tmp file.
        sheetWriter.beginSheet();

	}
	/**
	 * Method to create tmp file, i.e. sheet**.xml in system specified java.io.tmpdir property location.
	 * @throws IOException
	 */
    private void createTempXmlFile() throws IOException
	{
        tmpXmlFile = File.createTempFile("sheet", ".xml");
        tmpXmlFileWriter = new FileWriter(tmpXmlFile);
        sheetWriter = new SpreadsheetWriter(tmpXmlFileWriter);
    }
    /**
     * Method to save the sheet data.
     *  ( This method is to substitute the tmp xml(sheet**.xml) file data into the actual .xslx sheet.)
     */
    public void saveSheet(final String sheetName) throws IOException
    {
    	sheetWriter.endSheet();
    	tmpXmlFileWriter.close();
        final File file=new File(fileName);
        FileOutputStream out;
        final  String sheetRef=book.getSheet(getValidSheetName(sheetName))
        	.getPackagePart().getPartName().getName();
		if( file.exists())
		{
			//Step 3. Substitute the template entry with the generated data
	        out = new FileOutputStream(fileName);
			substitute(new File(getTempFileName(fileName)), tmpXmlFile, sheetRef.substring(1), out);
		}
		else
		{
			//Step 3. Substitute the template entry with the generated data
			out = new FileOutputStream(fileName,true);
			substitute(TEMPLATE_WORKBOOK, tmpXmlFile, sheetRef.substring(1), out);
		}
        out.close();
    }
	/**
	 * Method is to save the template of excel file.
	 * @throws IOException
	 */
	private void saveTemplate() throws IOException
	{
    	 // step 1: save the template
		 FileOutputStream outStream = new FileOutputStream(TEMPLATE_WORKBOOK);
         book.write(outStream);
         outStream.close();

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
			//TODO need to check condition for few special char : [ ] * \ / ? excel file not support these char.
			newSheetName=sheetName.substring(0,14)+Constants.UNDERSCORE+sheetName.substring(length-14);
		}
		return replaceSpecialChar(newSheetName);
	}
	/**
    * Method to get temp filename.
    * @param fileName
    * @return
    */
   private String getTempFileName(final String fileName)
	{
		return fileName.replace(XLSX_FILE_TYPE, TEMP_STR+XLSX_FILE_TYPE);
	}
	/**
	 * Method to create cell styles, number formats, etc.
	 * This method has some commented dummy style that can be used as template for cells.
	 * @param woorkbook XSSFWorkbook
	 * @return
	 */
	private static Map<String, XSSFCellStyle> createStyles(final XSSFWorkbook woorkbook)
	{
	        Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
	        XSSFDataFormat fmt = woorkbook.createDataFormat();

	      /*  XSSFCellStyle style1 = woorkbook.createCellStyle();
	        style1.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
	        style1.setDataFormat(fmt.getFormat("0.0%"));
	        styles.put("percent", style1);

	        XSSFCellStyle style2 = woorkbook.createCellStyle();
	        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
	        style2.setDataFormat(fmt.getFormat("0.0X"));
	        styles.put("coeff", style2);

	        XSSFCellStyle style3 = woorkbook.createCellStyle();
	        style3.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
	        style3.setDataFormat(fmt.getFormat("$#,##0.00"));
	        styles.put("currency", style3);

	        XSSFCellStyle style4 = woorkbook.createCellStyle();
	        style4.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
	        style4.setDataFormat(fmt.getFormat("mmm dd"));
	        styles.put("date", style4);*/

	        XSSFCellStyle style5 = woorkbook.createCellStyle();
	        XSSFFont headerFont = woorkbook.createFont();
	        headerFont.setBold(true);
	        style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	        style5.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
	        style5.setFont(headerFont);
	        styles.put("header", style5);

	        return styles;
	}
	 /**
	  *	Substitute the template entry with the generated data.
	  * @param zipfile the template file
	  * @param tmpfile the XML file with the sheet data
	  * @param entry the name of the sheet entry to substitute, e.g. xl/worksheets/sheet1.xml (sheet reference)
	  * @param out the stream to write the result to
	  */
	private static void substitute(File zipfile, File tmpfile, String entry, OutputStream out) throws IOException
	{
	        ZipFile zipFile = new ZipFile(zipfile);
	        ZipOutputStream zipOS = new ZipOutputStream(out);
	        @SuppressWarnings("unchecked")
	        Enumeration<ZipEntry> zipEnum = (Enumeration<ZipEntry>) zipFile.entries();
	        while (zipEnum.hasMoreElements())
	        {
	            ZipEntry zipEntry = zipEnum.nextElement();
	            if(!zipEntry.getName().equals(entry))
	            {
	                zipOS.putNextEntry(new ZipEntry(zipEntry.getName()));
	                InputStream instream = zipFile.getInputStream(zipEntry);
	                copyStream(instream, zipOS);
	                instream.close();
	            }
	        }
	        zipOS.putNextEntry(new ZipEntry(entry));
	        InputStream instream = new FileInputStream(tmpfile);
	        copyStream(instream, zipOS);
	        instream.close();
	        tmpfile.delete();
	        zipFile.close();
	        zipOS.close();
	 }
	/**
	 *  Method is to copy input stream to output stream.
	 * @param inputStream InputStream
	 * @param out OutputStream
	 * @throws IOException
	 */
	 private static void copyStream(InputStream inputStream, OutputStream out) throws IOException
	 {
	        byte[] chunk = new byte[1024];
	        int count;
	        while ((count = inputStream.read(chunk)) >=0 )
	        {
	          out.write(chunk,0,count);
	        }
	  }
	 /**
	  * Method to copy source file to target file.
	  * @param source
	  * @param target
	  * @throws IOException
	  */
	 private static void copy(File source, File target) throws IOException
	 {

	        InputStream inputStream = new FileInputStream(source);
	        OutputStream out = new FileOutputStream(target);
	        // Copy the bits from instream to outstream
	        byte[] buf = new byte[1024];
	        int len;

	        while ((len = inputStream.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }

	        inputStream.close();
	        out.close();
	    }
	 /**
	  * Method to replace Special Char.
	  * @param sheetName
	  * @return sheetName
	  */
	 private String replaceSpecialChar(String sheetName)
	 {
		/* need to check condition for few special char : [ ] * \ / ? excel file not support these char.
		 * because excel sheet name should not contains these char.
		 */
		 String[] splCharList={"*",":","[","]","?"};
		 for (String splChar:splCharList)
		 {
			 if(sheetName.contains(splChar))
			 {
				 sheetName=sheetName.replace(splChar, "_");
			 }
		 }
		return sheetName;
	 }
}
