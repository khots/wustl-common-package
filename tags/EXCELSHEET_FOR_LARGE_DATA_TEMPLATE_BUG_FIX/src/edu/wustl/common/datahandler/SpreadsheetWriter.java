package edu.wustl.common.datahandler;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;

/**
 * Writes spreadsheet data in a Writer.
 * @author amit_doshi
 *
 */
public class SpreadsheetWriter
{
	/**
	 * Writer out.
	 */
    private final Writer out;
    /**
     * row counter  for spread sheet.
     */
    private int rownum;

    /**
     * SpreadsheetWriter.
     * @param outStream FileWriter.
     */
    public SpreadsheetWriter(Writer outStream)
    {
        this.out = outStream;
    }
    /**
     * Method to write begin XML in tmp file.
     * @throws IOException IOException
     */
    public void beginSheet() throws IOException
    {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" );
        out.write("<sheetData>\n");
    }
    /**
     *  Method to write end XML.
     * @throws IOException IOException
     */
    public void endSheet() throws IOException
    {
        out.write("</sheetData>");
        out.write("</worksheet>");
    }

    /**
     * Insert a new row.
     * @param rownum 0-based row number
     */
    public void insertRow(int rownum) throws IOException
    {
        out.write("<row r=\""+(rownum+1)+"\">\n");
        this.rownum = rownum;
    }

    /**
     * Insert row end marker.
     */
    public void endRow() throws IOException
    {
        out.write("</row>\n");
    }
    /**
     * Method to append cell xml with styles in to file.
     * @param columnIndex columnIndex
     * @param value value
     * @param styleIndex styleIndex
     * @throws IOException IOException
     */
    public void createCell(int columnIndex, String value, int styleIndex) throws IOException
    {
        String ref = new CellReference(rownum, columnIndex).formatAsString();
        out.write("<c r=\""+ref+"\" t=\"inlineStr\"");
        if(styleIndex != -1)
        	{
        		out.write(" s=\""+styleIndex+"\"");
        	}
        out.write(">");
        out.write("<is><t>"+value+"</t></is>");
        out.write("</c>");
    }
    /**
     * Method to append cell xml in to file.
     * @param columnIndex columnIndex
     * @param value value
     * @throws IOException IOException
     */
    public void createCell(int columnIndex, String value) throws IOException
    {
        createCell(columnIndex, value, -1);
    }

    /**
     * Method to append cell xml with styles in to file.
     * @param columnIndex columnIndex
     * @param value value
     * @param styleIndex styleIndex
     * @throws IOException IOException
     */
    public void createCell(int columnIndex, double value, int styleIndex) throws IOException
    {
        String ref = new CellReference(rownum, columnIndex).formatAsString();
        out.write("<c r=\""+ref+"\" t=\"n\"");
        if(styleIndex != -1)
        	{
        		out.write(" s=\""+styleIndex+"\"");
        	}
        out.write(">");
        out.write("<v>"+value+"</v>");
        out.write("</c>");
    }
    /**
     * Method to append cell xml with styles in to file.
     * @param columnIndex columnIndex
     * @param value value
     * @throws IOException IOException
     */
    public void createCell(int columnIndex, double value) throws IOException
    {
        createCell(columnIndex, value, -1);
    }
    /**
     * Method to append cell xml with styles in to file.
     * @param columnIndex columnIndex
     * @param value value
     * @param styleIndex styleIndex
     * @throws IOException IOException
     */
    public void createCell(int columnIndex, Calendar value, int styleIndex) throws IOException
    {
        createCell(columnIndex, DateUtil.getExcelDate(value, false), styleIndex);
    }
}