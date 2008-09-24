/**
 * 
 */

package edu.wustl.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import titli.controller.Name;
import titli.controller.interfaces.ColumnInterface;
import titli.controller.interfaces.MatchInterface;
import titli.controller.interfaces.ResultGroupInterface;
import titli.controller.interfaces.TableInterface;
import titli.model.TitliException;
import titli.model.fetch.TitliFetchException;

/**
 * @author Juber Patel
 *
 */
public class TitliResultGroup
{

	private ResultGroupInterface group;
	private String pageOf;
	private String label;
	private List<String> columnList;
	private List<List<String>> dataList;
	private Iterator<MatchInterface> iterator;

	/**
	 * the constructor
	 * @param group the ResultGroup fro mwhich to make TitliResultGroup
	 */
	public TitliResultGroup(ResultGroupInterface group)
	{
		this.group = group;

	}

	/**
	 * get the "page of" string for the table associated with this group
	 * @return the "page of" string for the table associated with this group
	 * @throws Exception if problems occur
	 */
	public String getPageOf() throws TitliException
	{
		if (pageOf == null)
		{
			pageOf = TitliTableMapper.getInstance().getPageOf(getLabel());
		}

		return pageOf;
	}

	/**
	 * get the label for the table associated with this group
	 * @return the label for the table associated with this group
	 * @throws Exception if problems occur
	 */
	public String getLabel() throws TitliException
	{
		if (label == null)
		{
			label = TitliTableMapper.getInstance().getLabel(getNativeGroup().getTableName());
		}

		return label;
	}

	/**
	 * get the underlying ResultGroupInterface
	 * @return the underlying ResultGroupInterface
	 */
	public ResultGroupInterface getNativeGroup()
	{
		return group;
	}

	/**
	 * the list for DataView
	 * @return List of column names
	 * @throws TitliFetchException if problems occur
	 */
	public List<String> getColumnList() throws TitliFetchException
	{
		if (columnList == null)
		{
			columnList = new ArrayList<String>();
			TableInterface table = null;
			table = group.getMatchList().get(0).fetch().getTable();
			for (Name column : table.getColumns().keySet())
			{
				columnList.add(column.toString());
			}
		}
		return columnList;
	}

	/**
	 * get the data list for DataView
	 * @return the list of data
	 * @throws TitliFetchException if problems occur
	 */
	public List<List<String>> getDataList() throws TitliFetchException
	{
		if (dataList == null)
		{
			dataList = new ArrayList<List<String>>();

			//add data rows to the list
			for (MatchInterface match : group.getMatchList())
			{
				List<String> dataRow = new ArrayList<String>();
				Map<ColumnInterface, String> columns = null;
				columns = match.fetch().getColumnMap();
				//populate the data row
				for (String value : columns.values())
				{
					dataRow.add(value);
				}

				//add the row to the data list
				dataList.add(dataRow);
			}
		}
		return dataList;
	}

	/**
	 * get the data list for next n records
	 * this method was spcifically added for the pagination requirement 
	 * that only the rercords required to display the current page should be fetched  
	 * @param num number of records
	 * @return the data list for next n records
	 * @throws TitliFetchException if problems occur
	 */
	public List<List<String>> getNext(int num) throws TitliFetchException
	{
		List<List<String>> data = new ArrayList<List<String>>();
		if (iterator == null)
		{
			iterator = group.getMatchList().iterator();
		}
		
		List<String> dataRow;
		for (int i = 0; i < num && iterator.hasNext(); i++)
		{
			MatchInterface match = iterator.next();
			dataRow = new ArrayList<String>();
			Map<ColumnInterface, String> columns = null;
			columns = match.fetch().getColumnMap();
			//populate the data row
			for (String value : columns.values())
			{
				dataRow.add(value);
			}
			//add the row to the data list
			dataList.add(dataRow);
		}
		return data;
	}

	/**
	 * tells whether there are any more records to be fetched
	 * used in conjunction with getNext()
	 *  
	 * @return true if there are any more records to be fetched, otherwise false
	 */
	public boolean hasMore()
	{
		if (iterator == null)
		{
			iterator = group.getMatchList().iterator();
		}
		return iterator.hasNext();
	}
}
