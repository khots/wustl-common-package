/**
 * <p>Description:	SimpleSearchAction takes the conditions from the user, prepares,
 * executes the query and shows the result in a spreadsheet view.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.common.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.SimpleQueryInterfaceForm;
import edu.wustl.common.beans.QueryResultObjectData;
import edu.wustl.common.bizlogic.QueryBizLogic;
import edu.wustl.common.bizlogic.SimpleQueryBizLogic;
import edu.wustl.common.dao.QuerySessionData;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.query.DataElement;
import edu.wustl.common.query.Query;
import edu.wustl.common.query.QueryFactory;
import edu.wustl.common.query.SimpleConditionsNode;
import edu.wustl.common.query.SimpleQuery;
import edu.wustl.common.query.Table;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * SimpleSearchAction takes the conditions from the user, prepares,
 * executes the query and shows the result in a spreadsheet view.
 * @author gautam_shetty
 */
public class SimpleSearchAction extends BaseAction
{

	private org.apache.log4j.Logger logger = Logger.getLogger(SimpleQueryInterfaceAction.class);

	/**
	 * @param mapping	ActionMapping
	 * @param form	ActionForm
	 * @param request	HttpServletRequest
	 * @param response	HttpServletResponse
	 * @return ActionForward
	 * @exception Exception Generic exception
	 */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		logger.info("in executeAction method.");
		SimpleQueryInterfaceForm simpleQueryInterfaceForm = (SimpleQueryInterfaceForm) form;
		String strMenu = simpleQueryInterfaceForm.getMenuSelected();
		request.setAttribute(Constants.MENU_SELECTED, strMenu);
		HttpSession session = request.getSession();
		String target = Constants.SUCCESS;
		Map map = simpleQueryInterfaceForm.getValuesMap();
		String counter = simpleQueryInterfaceForm.getCounter();
		Map originalQueryObject = (Map) session
				.getAttribute(Constants.ORIGINAL_SIMPLE_QUERY_OBJECT);
		String originalCounter = (String) session
				.getAttribute(Constants.ORIGINAL_SIMPLE_QUERY_COUNTER);
		//If map from form is null get the map values from session.
		if (map.size() == 0)
		{
			map = (Map) session.getAttribute(Constants.SIMPLE_QUERY_MAP);
			//Get the counter from the simple query map if set during configure action in the session object.
			counter = (String) map.get("counter");
			//After retrieving the value of counter, remove from the map.
			map.remove("counter");
		}
		if (originalQueryObject == null && originalCounter == null)
		{	
			if (map != null && counter != null)
			{
				session.setAttribute(Constants.ORIGINAL_SIMPLE_QUERY_OBJECT, new HashMap(map));
				session.setAttribute(Constants.ORIGINAL_SIMPLE_QUERY_COUNTER, counter);
			}
		}
		session.setAttribute(Constants.SIMPLE_QUERY_MAP, map);
		MapDataParser parser = new MapDataParser("edu.wustl.common.query");
		Collection simpleConditionNodeCollection = parser.generateData(map, true);
		List fieldList = new ArrayList();
		fieldList = getFieldList(simpleConditionNodeCollection);
		Map queryResultObjectDataMap = new HashMap();
		SimpleQueryBizLogic simpleQueryBizLogic = new SimpleQueryBizLogic();
		String viewAliasName = (String) map
				.get("SimpleConditionsNode:1_Condition_DataElement_table");
		Query query = QueryFactory.getInstance().newQuery(Query.SIMPLE_QUERY, viewAliasName);
		// List containing the Alias name of the Table name of the first condition element.
		List aliasList = new ArrayList();
		aliasList.add(viewAliasName);
		List fromTablesList = new ArrayList();
		simpleQueryBizLogic.handleStringAndDateConditions(simpleConditionNodeCollection,
				fromTablesList);

		String[] selectedColumns = simpleQueryInterfaceForm.getSelectedColumnNames();
		if (selectedColumns != null)
		{
			session.setAttribute(Constants.CONFIGURED_SELECT_COLUMN_LIST, selectedColumns);
		}
		List columnNames = new ArrayList();
		List selectDataElements = null;
		if (selectedColumns != null)
		{
			selectDataElements = simpleQueryBizLogic.getSelectDataElements(selectedColumns,
					aliasList, columnNames, true, null);
		}
		else
		{
			selectDataElements = simpleQueryBizLogic.getSelectDataElements(selectedColumns,
					aliasList, columnNames, true, fieldList);
		}
		query.setResultView(selectDataElements);
		Set fromTables = new HashSet();
		Set tableSet = query.getTableSet();
		Iterator itr = tableSet.iterator();
		while (itr.hasNext())
		{
			Table table = (Table) itr.next();
			fromTables.add(table.getTableName());
		}

		fromTables.addAll(fromTablesList);
		fromTables.addAll(aliasList);
		query.setTableSet(fromTables);
		simpleQueryBizLogic.addActivityStatusConditions(simpleConditionNodeCollection, fromTables);
		simpleQueryBizLogic.createOrderByListInQuery(fromTables, query);
		((SimpleQuery) query).addConditions(simpleConditionNodeCollection);

		QueryBizLogic queryBizLogic = (QueryBizLogic) AbstractBizLogicFactory.getBizLogic(
				ApplicationProperties.getValue("app.bizLogicFactory"), "getBizLogic",
				Constants.QUERY_INTERFACE_ID);
		int identifierIndex = 0;
		int recordsPerPage;
		String recordsPerPageSessionValue = (String) session
				.getAttribute(Constants.RESULTS_PER_PAGE);
		if (recordsPerPageSessionValue == null)
		{
			recordsPerPage = Integer.parseInt(XMLPropertyHandler
					.getValue(Constants.RECORDS_PER_PAGE_PROPERTY_NAME));
			session.setAttribute(Constants.RESULTS_PER_PAGE, recordsPerPage + "");
		}
		else
		{
			recordsPerPage = Integer.parseInt(recordsPerPageSessionValue);
		}
		PagenatedResultData pagenatedResultData = null;
		boolean isSecureExecute = getSessionData(request).isSecurityRequired();
		boolean hasConditionOnIdentifiedField;
		simpleQueryBizLogic
				.createQueryResultObjectData(fromTables, queryResultObjectDataMap, query);

		List identifierColumnNames = new ArrayList();
		identifierColumnNames = simpleQueryBizLogic.addObjectIdentifierColumnsToQuery(
				queryResultObjectDataMap, query);
		simpleQueryBizLogic.setDependentIdentifiedColumnIds(queryResultObjectDataMap, query);
		for (int i = 0; i < identifierColumnNames.size(); i++)
		{
			columnNames.add((String) identifierColumnNames.get(i));
		}
		if (isSecureExecute)
		{
			queryBizLogic.insertQuery(query.getString(), getSessionData(request));
			hasConditionOnIdentifiedField = query.hasConditionOnIdentifiedField();
			pagenatedResultData = query.execute(getSessionData(request),isSecureExecute,
					queryResultObjectDataMap, hasConditionOnIdentifiedField, 0, recordsPerPage);
		}	
		else
		{
			isSecureExecute = false;
			hasConditionOnIdentifiedField = false;
			pagenatedResultData = query.execute(getSessionData(request), false, null, false, 0,
					recordsPerPage);
		}
		if (simpleQueryInterfaceForm.getPageOf().equals(Constants.PAGEOF_SIMPLE_QUERY_INTERFACE))
		{
			Map<Integer, QueryResultObjectData> hyperlinkColumnMap = simpleQueryBizLogic
					.getHyperlinkMap(queryResultObjectDataMap, query.getResultView());
			session.setAttribute(Constants.HYPERLINK_COLUMN_MAP, hyperlinkColumnMap);

		}
		else
		{
			Vector tableAliasNames = new Vector();
			tableAliasNames.add(viewAliasName);
			Map tableMap = query.getIdentifierColumnIds(tableAliasNames);
			if (tableMap != null)
			{
				identifierIndex = Integer.parseInt(tableMap.get(viewAliasName).toString())
						- Constants.ONE;
				request.setAttribute(Constants.IDENTIFIER_FIELD_INDEX, Integer
						.valueOf(identifierIndex));
			}
			queryBizLogic.insertQuery(query.getString(), getSessionData(request));
		}

		List list = pagenatedResultData.getResult();
		if (list.isEmpty())
		{
			return getActionForwardForNoResult(request, simpleQueryInterfaceForm, session, map);
		}
		else
		{
			if ((list.size() == Constants.ONE)
					&& (!Constants.PAGEOF_SIMPLE_QUERY_INTERFACE.equals(simpleQueryInterfaceForm.getPageOf())))
			{	
     			return getActionForwardForOneRow(simpleQueryInterfaceForm, identifierIndex, list);
			}
			else
			{
				QuerySessionData querySessionData = new QuerySessionData();
				querySessionData.setSql(query.getString());
				querySessionData.setQueryResultObjectDataMap(queryResultObjectDataMap);
				querySessionData.setSecureExecute(isSecureExecute);
				querySessionData.setHasConditionOnIdentifiedField(hasConditionOnIdentifiedField);
				querySessionData.setRecordsPerPage(recordsPerPage);
				querySessionData.setTotalNumberOfRecords(pagenatedResultData.getTotalRecords());
				session.setAttribute(Constants.QUERY_SESSION_DATA, querySessionData);
				request.setAttribute(Constants.PAGEOF, simpleQueryInterfaceForm.getPageOf());
				request.setAttribute(Constants.SPREADSHEET_DATA_LIST, list);
				request.setAttribute(Constants.SPREADSHEET_COLUMN_LIST, columnNames);
			}
		}
		session.setAttribute(Constants.IS_SIMPLE_SEARCH, Constants.TRUE);
		return mapping.findForward(target);
	}

	/**
	 * @param request HttpServletRequest
	 * @param simpleQueryInterfaceForm SimpleQueryInterfaceForm
	 * @param session HttpSession
	 * @param map Map
	 * @return the next page to be displayed in case no results found.
	 */
	private ActionForward getActionForwardForNoResult(HttpServletRequest request,
			SimpleQueryInterfaceForm simpleQueryInterfaceForm, HttpSession session, Map map)
	{
		ActionErrors errors = new ActionErrors();
		errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("simpleQuery.noRecordsFound"));
		saveErrors(request, errors);
		String alias = (String) session.getAttribute(Constants.SIMPLE_QUERY_ALIAS_NAME);
		if (alias == null)
		{
			alias = simpleQueryInterfaceForm.getAliasName();
		}
		simpleQueryInterfaceForm.setValues(map);
		//remove the original session attributes for the query if the list is empty
		session.setAttribute(Constants.ORIGINAL_SIMPLE_QUERY_OBJECT, null);
		session.setAttribute(Constants.ORIGINAL_SIMPLE_QUERY_COUNTER, null);

		String path = Constants.SIMPLE_QUERY_INTERFACE_ACTION + "?" + Constants.PAGEOF + "="
				+ simpleQueryInterfaceForm.getPageOf() + "&";
		if (alias != null)
		{
			path = path + Constants.TABLE_ALIAS_NAME + "=" + alias;
		}

		return getActionForward(Constants.SIMPLE_QUERY_NO_RESULTS, path);
	}

	/**
	 * @param simpleQueryInterfaceForm SimpleQueryInterfaceForm
	 * @param identifierIndex int
	 * @param list List
	 * @return next action to be taken in case of one row is being selected.
	 */
	private ActionForward getActionForwardForOneRow(
			SimpleQueryInterfaceForm simpleQueryInterfaceForm, int identifierIndex, List list)
	{
		List rowList = (List) list.get(0);

		String path = Constants.SEARCH_OBJECT_ACTION + "?" + Constants.PAGEOF + "="
				+ simpleQueryInterfaceForm.getPageOf() + "&" + Constants.OPERATION + "="
				+ Constants.SEARCH + "&" + Constants.SYSTEM_IDENTIFIER + "="
				+ rowList.get(identifierIndex);

		if (simpleQueryInterfaceForm.getPageOf().equals("pageOfCollectionProtocol"))
		{
			path = "/RetrieveCollectionProtocol.do?" + Constants.SYSTEM_IDENTIFIER + "="
					+ rowList.get(identifierIndex);
		}
		return getActionForward(Constants.SIMPLE_QUERY_SINGLE_RESULT, path);
	}

	/**
	 * @param simpleConditionNodeCollection Collection<SimpleConditionsNode>
	 * @return List<String> containing the details of table,data from the condition specified.
	 */
	private List<String> getFieldList(Collection<SimpleConditionsNode> simpleConditionNodeCollection)
	{
		List<String> fieldList = new ArrayList<String>();
		if (simpleConditionNodeCollection != null && !simpleConditionNodeCollection.isEmpty())
		{
			Iterator<SimpleConditionsNode> itr = simpleConditionNodeCollection.iterator();
			while (itr.hasNext())
			{
				SimpleConditionsNode simpleConditionsNode = (SimpleConditionsNode) itr.next();
				Table table = simpleConditionsNode.getCondition().getDataElement().getTable();
				DataElement dataElement = simpleConditionsNode.getCondition().getDataElement();
				String field = table.getTableName() + "." + dataElement.getField();
				fieldList.add(field);
			}
		}
		return fieldList;
	}

	/**
	 * set the action and path of that action.
	 * @param name String
	 * @param path String
	 * @return the action details.
	 */
	private ActionForward getActionForward(String name, String path)
	{
		ActionForward actionForward = new ActionForward();
		actionForward.setName(name);
		actionForward.setPath(path);

		return actionForward;
	}
}