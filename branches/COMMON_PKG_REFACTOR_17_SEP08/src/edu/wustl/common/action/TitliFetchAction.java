/**
 *
 */

package edu.wustl.common.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import titli.controller.Name;
import titli.controller.interfaces.ColumnInterface;
import titli.controller.interfaces.MatchInterface;
import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.SortedResultMapInterface;
import titli.controller.interfaces.TableInterface;
import titli.model.Titli;
import titli.model.TitliException;
import titli.model.fetch.TitliFetchException;
import edu.wustl.common.actionForm.TitliSearchForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.SimpleQueryBizLogic;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.dao.QuerySessionData;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.query.Condition;
import edu.wustl.common.query.DataElement;
import edu.wustl.common.query.Operator;
import edu.wustl.common.query.Query;
import edu.wustl.common.query.QueryFactory;
import edu.wustl.common.query.SimpleConditionsNode;
import edu.wustl.common.query.SimpleQuery;
import edu.wustl.common.query.Table;
import edu.wustl.common.util.TitliResultGroup;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * show the records from the selected entity.
 * @author Juber Patel
 *
 */
public class TitliFetchAction extends Action
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(TitliFetchAction.class);
	/**
	 * alias String alias name of column.
	 */
	private String alias;
	/**
	 * dataList List contains list of data.
	 */
	private List dataList;
	/**
	 * columnNames List of type String contains name of columns.
	 */
	private List<String> columnNames;
	/**
	 * id int.
	 */
	private int id;
	/**
	 * identifierIndex int - identify index of columns.
	 */
	private int identifierIndex;

	/**
	 * get the data from the form for searching and set it in request.
	 * @param mapping the mapping
	 * @param form the action form
	 * @param request the request
	 * @param response the response
	 * @return details of the next action to be taken.
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		// set the request and session attributes required by DataView.jsp and forward
		// for that we need to fetch the selected group of records
		logger.info("in execute method");
		ActionForward actionForward = mapping.findForward(Constants.SUCCESS);
		TitliSearchForm titliSearchForm = (TitliSearchForm) form;
		titliSearchForm.setSortedResultMap((SortedResultMapInterface) (request.getSession()
				.getAttribute(Constants.TITLI_SORTED_RESULT_MAP)));
		TitliResultGroup resultGroup = titliSearchForm.getSelectedGroup();
		Collection<SimpleConditionsNode> simpleConditionsNodeCollection = getSimpleConditionsNodeCollecton(
				resultGroup, request);
		setDataAndColumnLists(simpleConditionsNodeCollection, request);
		try
		{
			//if there is only one record in the selected group, go directly to the edit page
			if (dataList.size() == Constants.ONE)
			{
				String pageOf = resultGroup.getPageOf();
				List row = (List) (dataList.get(0));
				String path = new StringBuffer().append(Constants.SEARCH_OBJECT_ACTION).append("?").
				append(Constants.PAGEOF).append("=").append(pageOf).append("&").append(
				Constants.OPERATION).append("=").append(Constants.SEARCH).append("&").
				append(Constants.SYSTEM_IDENTIFIER).append("=").append((String) (row.get(id))).toString();
				actionForward = getActionForward(Constants.TITLI_SINGLE_RESULT, path);
			}
			else
			{
				request.setAttribute(Constants.PAGEOF, resultGroup.getPageOf());
				request.setAttribute(Constants.SPREADSHEET_DATA_LIST, dataList);
				request.setAttribute(Constants.SPREADSHEET_COLUMN_LIST, columnNames);
				request.setAttribute(Constants.IDENTIFIER_FIELD_INDEX, identifierIndex);
				request.setAttribute(Constants.PAGE_NUMBER, Constants.ONE);
				request.getSession().setAttribute(Constants.TOTAL_RESULTS, dataList.size());
			}
		}
		catch (TitliFetchException e)
		{
			logger.error("Exception in TitliFetchAction : " + e.getMessage(), e);
		}
		catch (Exception e)
		{
			logger.error("Exception in TitliFetchAction : " + e.getMessage(), e);
		}
		return actionForward;
	}

	/**
	 *	set the name of the action and path to be performed.
	 * @param name String
	 * @param path String
	 * @return ActionForward
	 */
	private ActionForward getActionForward(String name, String path)
	{
		ActionForward actionForward = new ActionForward();
		actionForward.setName(name);
		actionForward.setPath(path);
		return actionForward;
	}

	/**
	 * Create a collection of SimpleConditionsNode as needed to create SimpleQuery.
	 * @param resultGroup TitliResultGroup the result group for the selected table
	 * @param request HttpServletRequest
	 * @return Collection of type SimpleConditionsNode, a collection of SimpleConditionsNode
	 * as needed to create SimpleQuery.
	 */
	Collection<SimpleConditionsNode> getSimpleConditionsNodeCollecton(TitliResultGroup resultGroup,
			HttpServletRequest request)
	{
		Collection<SimpleConditionsNode> simpleConditionsNodeCollection = new ArrayList<SimpleConditionsNode>();
		MatchListInterface matchList = resultGroup.getNativeGroup().getMatchList();
		try
		{
			Name dbName = Titli.getInstance().getDatabases().keySet().toArray(new Name[0])[0];
			Name tableName = matchList.get(0).getTableName();
			setAliasFor(tableName.toString(), request);
			TableInterface table = Titli.getInstance().getDatabase(dbName).getTable(tableName);
			Name identifier;
			DataElement dataElement;
			Condition condition;
			SimpleConditionsNode simpleConditionsNode;
			//for each match form a SimpleConditionsNode and add it to the collection
			for (MatchInterface match : matchList)
			{
				identifier = new Name(Constants.IDENTIFIER);
				ColumnInterface column = table.getColumn(identifier);
				String value = match.getUniqueKeys().get(identifier);
				dataElement = new DataElement(alias, Constants.IDENTIFIER, column
						.getType());
				condition = new Condition(dataElement, new Operator(Operator.EQUAL),
						value);
				simpleConditionsNode = new SimpleConditionsNode(condition,
						new Operator(Operator.OR));
				simpleConditionsNodeCollection.add(simpleConditionsNode);
			}
		}
		catch (TitliException e)
		{
			logger.error("Exception in TitliFetchAction : " + e.getMessage(), e);
		}
		catch (DAOException e)
		{
			logger.error("DAOException in TitliFetchAction : " + e.getMessage(), e);
		}
		catch (ClassNotFoundException e)
		{
			logger.error("ClassNotFoundException in TitliFetchAction : " + e.getMessage(), e);
		}
		return simpleConditionsNodeCollection;

	}

	/**
	 * Create SimpleQuery from the given collection of SimpleConditionsNode,
	 * execute it and get the resultant data list.
	 * @param simpleConditionsNodeCollection Collection of type SimpleConditionsNode.
	 * @param request the servlet request.
	 *
	 */
	private void setDataAndColumnLists(
			Collection<SimpleConditionsNode> simpleConditionsNodeCollection,
			HttpServletRequest request)
	{
		dataList = new ArrayList();
		columnNames = new ArrayList<String>();
		HttpSession session = request.getSession();
		Query query = QueryFactory.getInstance().newQuery(Query.SIMPLE_QUERY, alias);
		//Sets the condition objects from user in the query object.
		((SimpleQuery) query).addConditions(simpleConditionsNodeCollection);
		Map queryResultObjectDataMap = new HashMap();
		SimpleQueryBizLogic simpleQueryBizLogic = new SimpleQueryBizLogic();
		try
		{
			List<String> fieldList = new ArrayList<String>();
			List<String> aliasList = new ArrayList<String>();
			aliasList.add(alias);
			fieldList = getFieldList(simpleConditionsNodeCollection);
			List selectDataElements = simpleQueryBizLogic.getSelectDataElements(null, aliasList,
					columnNames, true, fieldList);
			query.setResultView(selectDataElements);
			setColumnNames(query, queryResultObjectDataMap, simpleQueryBizLogic);
			setIdentifierIndex(query);
			int recordsPerPage = Utility.getRecordsPerPage(session);
			PagenatedResultData pagenatedResultData = query.execute(getSessionData(request), true,
					queryResultObjectDataMap, query.hasConditionOnIdentifiedField(), 0,
					recordsPerPage);
			QuerySessionData querySessionData = new QuerySessionData();
			querySessionData.setSql(query.getString());
			querySessionData.setQueryResultObjectDataMap(queryResultObjectDataMap);
			querySessionData.setSecureExecute(true);
			querySessionData
					.setHasConditionOnIdentifiedField(query.hasConditionOnIdentifiedField());
			querySessionData.setRecordsPerPage(recordsPerPage);
			querySessionData.setTotalNumberOfRecords(pagenatedResultData.getTotalRecords());
			session.setAttribute(Constants.QUERY_SESSION_DATA, querySessionData);
			dataList = pagenatedResultData.getResult();
			id = (Integer) (query.getColumnIdsMap().get(alias + "." + Constants.IDENTIFIER))
					- Constants.ONE;
		}
		catch (DAOException e)
		{
			logger.error("DAOException in TitliFetchAction : " + e.getMessage(), e);
		}
		catch (SQLException e)
		{
			logger.error("SQLException in TitliFetchAction : " + e.getMessage(), e);
		}
	}

	/**
	 * set the columnId as identifier in  a map.
	 * @param query Query
	 */
	private void setIdentifierIndex(Query query)
	{
		List<String> tableAliasNames = new ArrayList<String>();
		tableAliasNames.add(alias);
		Map<String,Integer> tableMap = query.getIdentifierColumnIds(tableAliasNames);
		if (tableMap != null)
		{
			identifierIndex = Integer.parseInt(tableMap.get(alias).toString()) - Constants.ONE;
		}
	}

	/**
	 * set the column details required for query,in a list.
	 * @param query Query
	 * @param queryResultObjectDataMap Map
	 * @param simpleQueryBizLogic SimpleQueryBizLogic
	 * @throws DAOException database exception
	 */
	private void setColumnNames(Query query, Map queryResultObjectDataMap,
			SimpleQueryBizLogic simpleQueryBizLogic) throws DAOException
	{
		Set<String> fromTables = query.getTableNamesSet();
		query.setTableSet(fromTables);
		simpleQueryBizLogic
				.createQueryResultObjectData(fromTables, queryResultObjectDataMap, query);
		List<String> identifierColumnNames = new ArrayList<String>();
		identifierColumnNames = simpleQueryBizLogic.addObjectIdentifierColumnsToQuery(
				queryResultObjectDataMap, query);
		simpleQueryBizLogic.setDependentIdentifiedColumnIds(queryResultObjectDataMap, query);
		for (int i = 0; i < identifierColumnNames.size(); i++)
		{
			columnNames.add((String) identifierColumnNames.get(i));
		}
	}

	/**
	 * @param simpleConditionsNodeCollection Collection of type SimpleConditionsNode.
	 * @return List of type String field List containing all column details.
	 */
	private List<String> getFieldList(
			Collection<SimpleConditionsNode> simpleConditionsNodeCollection)
	{
		List<String> fieldList = new ArrayList<String>();
		if (simpleConditionsNodeCollection != null && !simpleConditionsNodeCollection.isEmpty())
		{
			Iterator<SimpleConditionsNode> itr = simpleConditionsNodeCollection.iterator();
			while (itr.hasNext())
			{
				SimpleConditionsNode simpleConditionsNode = (SimpleConditionsNode) itr.next();
				Table table = simpleConditionsNode.getCondition().getDataElement().getTable();
				DataElement dataElement = simpleConditionsNode.getCondition().getDataElement();
				String field = table.getTableName() + "." + table.getTableName() + "."
						+ dataElement.getField() + "." + dataElement.getFieldType();
				fieldList.add(field);
			}
		}
		return fieldList;
	}
	/**
	 * get the alias for the give table name from appropriate database table.
	 * @param tableName the table name for which to get the alias
	 * @param request HttpServletRequest
	 * @throws DAOException database exception
	 * @throws ClassNotFoundException Generic exception
	 */
	private void setAliasFor(String tableName, HttpServletRequest request) throws DAOException,
			ClassNotFoundException
	{
		String query = "select " + Constants.TABLE_ALIAS_NAME_COLUMN + " from "
				+ Constants.TABLE_DATA_TABLE_NAME + " where " + Constants.TABLE_TABLE_NAME_COLUMN
				+ "='" + tableName + "'";
		JDBCDAO dao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		dao.openSession(getSessionData(request));
		List list = dao.executeQuery(query, getSessionData(request), false, null);
		List subList = (List) (list.get(0));
		alias = (String) (subList.get(0));
		dao.closeSession();
	}

	/**
	 *
	 * @param request HttpServletRequest
	 * @return session data.
	 */
	protected SessionDataBean getSessionData(HttpServletRequest request)
	{
		return (SessionDataBean) request.getSession().getAttribute(Constants.SESSION_DATA);
	}

}
