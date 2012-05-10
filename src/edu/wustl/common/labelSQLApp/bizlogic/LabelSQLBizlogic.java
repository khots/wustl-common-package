
package edu.wustl.common.labelSQLApp.bizlogic;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.labelSQLApp.domain.LabelSQL;

import org.hibernate.Session;

import edu.wustl.common.hibernate.HibernateDatabaseOperations;
import edu.wustl.common.hibernate.HibernateUtil;

import edu.wustl.common.labelSQLApp.domain.LabelSQL;

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

		Session session = HibernateUtil.newSession();
		try
		{
			Long id = labelSQLId;
			HibernateDatabaseOperations<LabelSQL> dbHandler = new HibernateDatabaseOperations<LabelSQL>(
					session);
			labelSQL = dbHandler.retrieveById(LabelSQL.class.getName(), id);
		}
		finally
		{
			session.close();
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

		Session session = HibernateUtil.newSession();
		try
		{
			HibernateDatabaseOperations<LabelSQL> dbHandler = new HibernateDatabaseOperations<LabelSQL>(
					session);

			dbHandler.insert(labelSQL);

			session.flush();
			return labelSQL.getId();
		}
		finally
		{
			session.close();
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

		Session session = HibernateUtil.newSession();
		try
		{

			HibernateDatabaseOperations<LabelSQL> dbHandler = new HibernateDatabaseOperations<LabelSQL>(
					session);
			labelSQLs = dbHandler.retrieve(LabelSQL.class.getName());
		}
		finally
		{
			session.close();
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
		values.add(CPId);
		values.add(label);
		values.add(label);

		List<?> result = CommonBizlogic.executeHQL("getLabelSQLIdByLabelOrDisplayName", values);

		if (result.size() != 0)
		{
			return Long.parseLong(result.get(0).toString());
		}
		else
		{
			return null;
		}

	}

}
