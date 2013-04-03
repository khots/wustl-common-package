
package edu.wustl.common.labelSQLApp.bizlogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.wustl.common.labelSQLApp.domain.LabelSQL;
import edu.wustl.common.util.global.Constants;

import org.hibernate.Session;

import edu.wustl.common.hibernate.HibernateDatabaseOperations;
import edu.wustl.common.hibernate.HibernateUtil;

import java.sql.Clob;

public class LabelSQLBizlogic
{

	/**
	 * Gives the LabelSQL from the LabelSQL id
	 * @param labelSQLId
	 * @return
	 * @throws Exception
	 */
	public LabelSQL getLabelSQLById(Long labelSQLId) throws Exception
	{
		LabelSQL labelSQL = null;

		System.out.println("create session getLabelSQLById...");
		Session session = HibernateUtil.newSession();
		System.out.println("create session getLabelSQLById done...");
		try
		{
			Long id = labelSQLId;
			HibernateDatabaseOperations<LabelSQL> dbHandler = new HibernateDatabaseOperations<LabelSQL>(
					session);
			System.out.println("retrieveById getLabelSQLById...");
			labelSQL = dbHandler.retrieveById(LabelSQL.class.getName(), id);
			System.out.println("retrieveById getLabelSQLById done...");
		}
		finally
		{
			System.out.println("closing session getLabelSQLById...");
			session.close();
			System.out.println("closing session getLabelSQLById done...");
		}

		return labelSQL;
	}

	/**
	 * Retrieves the LabelSQL on the basis of label
	 * @param label
	 * @return
	 * @throws Exception
	 */
	public List<LabelSQL> getLabelSQLByLabel(String label) throws Exception
	{
		List<LabelSQL> labelSQLList = null;
		Session session = HibernateUtil.newSession();
		try
		{
			HibernateDatabaseOperations<LabelSQL> dbHandler = new HibernateDatabaseOperations<LabelSQL>(
					session);
			labelSQLList = dbHandler.retrieve(LabelSQL.class.getName(), "label", label);
		}
		finally
		{
			session.close();
		}

		return labelSQLList;
	}

	/**
	 * Insert LabelSQL
	 * @param label
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public long insertLabelSQL(String label, String sql) throws Exception
	{
		LabelSQL labelSQL = new LabelSQL();
		labelSQL.setLabel(label);
		labelSQL.setQuery(sql);

		System.out.println("create session insertLabelSQL...");
		Session session = HibernateUtil.newSession();
		System.out.println("create session insertLabelSQL done...");
		try
		{
			HibernateDatabaseOperations<LabelSQL> dbHandler = new HibernateDatabaseOperations<LabelSQL>(
					session);
			System.out.println("insert insertLabelSQL...");
			dbHandler.insert(labelSQL);
			System.out.println("insert insertLabelSQL done...");

			return labelSQL.getId();
		}
		finally
		{
			System.out.println("closing session insertLabelSQL...");
			session.close();
			System.out.println("closing session insertLabelSQL done...");
		}

	}

	/**
	 * Gives all LabelSQL
	 * @param labelSQLId
	 * @return
	 * @throws Exception
	 */
	public List<LabelSQL> getAllLabelSQL() throws Exception
	{
		List<LabelSQL> labelSQLs = null;

		System.out.println("create session getAllLabelSQL...");
		Session session = HibernateUtil.newSession();
		System.out.println("create session getAllLabelSQL done...");
		try
		{

			HibernateDatabaseOperations<LabelSQL> dbHandler = new HibernateDatabaseOperations<LabelSQL>(
					session);
			System.out.println("retrieve getAllLabelSQL...");
			labelSQLs = dbHandler.retrieve(LabelSQL.class.getName());
			System.out.println("retrieve getAllLabelSQL done...");
		}
		finally
		{
			System.out.println("closing session getAllLabelSQL...");
			session.close();
			System.out.println("closing session getAllLabelSQL done...");
		}

		return labelSQLs;
	}

	/**
	 * Returns the LabelSQL based on label or display name.
	 * @param labelSQLId
	 * @return
	 * @throws Exception
	 */
	public Long getLabelSQLIdByLabelOrDisplayName(Long CPId, String label) throws Exception
	{
		List<Object> values = new ArrayList<Object>();
		String hql="getSysLabelSQLIdByLabelOrDisplayName";
		if(CPId!=null)
		{
			values.add(CPId);
			hql="getLabelSQLIdByLabelOrDisplayName";
		}
		values.add(label);
		values.add(label);
		
		List<?> result = CommonBizlogic.executeHQL(hql, values);

		if (result.size() != 0)
		{
			return Long.parseLong(result.get(0).toString());
		}
		else
		{
			return null;
		}

	}
	
	/** This method returns the labelsql identifier using labelsql displaylabel.
	 * @param label
	 * @return
	 */
	public Long getLabelSqlIdByLabel(String label)
	{
		List<Object> values = new ArrayList<Object>();
		Long labelsqlid = null;
		String hql = "getLabelSqlIdByLabel";
		values.add(label);
		List<?> result = CommonBizlogic.executeHQL(hql, values);

		if (result.size() != 0)
		{
			labelsqlid = Long.parseLong(result.get(0).toString());
		}
		return labelsqlid;
	}
	
	/** This method returns the sql query for given ladelsql id.
	 * @param id
	 * @return
	 */
	public Clob getLabelSqlQueryById(Long id)
	{
	  
		List<Object> values = new ArrayList<Object>();
		Clob query = null;
		String hql = "getQueryById";
		values.add(id);
		List<Clob> result = (List<Clob>)CommonBizlogic.executeHQL(hql, values);

		if (result.size() != 0)
		{
			query = result.get(0);
		}
		return query;
	}
	/**This method generates the displaynameAndLabelSqlIdmap to display the system/default dashboard.
	 * @param dashboardType
	 * @return
	 */
	public LinkedHashMap<String, Long> loadDasboard(String dashboardType)
	{
		List<String[]> dashbrdItems = new ArrayList<String[]>();
		if (dashboardType.equalsIgnoreCase(Constants.DEFAULT_DASHBOARD)) {
			dashbrdItems = edu.wustl.common.util.global.Constants.DEFAULT_DASHBOARD_ITEMS;
		} else if (dashboardType.equalsIgnoreCase(Constants.SYSTEM_DASHBOARD)) {
			dashbrdItems = edu.wustl.common.util.global.Constants.SYSTEM_DASHBOARD_ITEMS;
		}

		LinkedHashMap<String, Long> displayNameAssocMap = new LinkedHashMap<String, Long>();

		LabelSQLBizlogic bizlogic = new LabelSQLBizlogic();
		for (String[] dashbrdItem : dashbrdItems) {
			String queryLabel = dashbrdItem[0];
			String userdefinedLabel = dashbrdItem[1];
			Long labelSqlId = 0l;
			if (!queryLabel.isEmpty() && queryLabel != null) {
				if (!queryLabel.equalsIgnoreCase(Constants.LABEL_SQL_HEADER)) 
				{
					labelSqlId = bizlogic.getLabelSqlIdByLabel(queryLabel);
				}
			if(labelSqlId!=null)
			{
				displayNameAssocMap.put(userdefinedLabel, labelSqlId);
			}
			}
		}

		return displayNameAssocMap;
	}
	
}
