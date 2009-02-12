package edu.wustl.common.dao.queryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MsSqlServerQueryExecutor extends AbstractQueryExecutor {

	/**
	 * Default constructor.
	 */
	protected MsSqlServerQueryExecutor(){
		isExtraColAddedForPagination = false;
	}
	
	@Override
	protected PagenatedResultData createStatemtentAndExecuteQuery()
			throws SQLException {
		stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		resultSet = stmt.executeQuery();

		if (getSublistOfResult) {
			if (startIndex > 0) { 
				// move cursor to the start index.
				resultSet.absolute(startIndex);
			}
		}
		List list = getListFromResultSet(); // get the resulset.

		// find the total number of records.
		int totalRecords;
		if (getSublistOfResult) {
			resultSet.last();
			totalRecords = resultSet.getRow();
		} else {
			totalRecords = list.size(); // these are all records returned from query.
		}
		return new PagenatedResultData(list, totalRecords);
	}
}
