/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.labelSQLApp.bizlogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

import edu.wustl.common.hibernate.HibernateUtil;
import edu.wustl.common.labelSQLApp.domain.LabelSQLAssociation;
import edu.wustl.common.util.logger.Logger;

public class CommonBizlogic
{

	private static final Logger LOGGER = Logger.getCommonLogger(CommonBizlogic.class);
	/**
	 * This method executes the HQL in LabelSQL_HQL.hbm.xml
	 * @param queryName
	 * @param values
	 * @return
	 * @throws HibernateException
	 */
	public static List<?> executeHQL(String queryName, List<Object> values)
			throws HibernateException
	{
		Session session = HibernateUtil.newSession();
		List<?> records = null;
		try
		{
			Query q = session.getNamedQuery(queryName);
			if (values != null)
			{
				for (int counter = 0; counter < values.size(); counter++)
				{
					Object value = values.get(counter);
					String objectType = value.getClass().getSimpleName();

					if ("Long".equals(objectType))
					{
						//sets the value for parameter of Long type
						q.setLong(counter, Long.parseLong(value.toString()));
					}
					else if ("String".equals(objectType))
					{
						//sets the value for parameter of String type
						q.setString(counter, (String) value);
					}
					else
					{
						q.setEntity(counter, value);
					}
				}
			}
			records = q.list();
		}
		finally
		{
			session.close();
		}

		return records;

	}

	/**
	 * Retrieves the label for dashboard item can be predefined label or display name
	 * @param labelSQLAssocId
	 * @return
	 * @throws Exception
	 */
	public String getLabelByLabelSQLAssocId(Long labelSQLAssocId) throws Exception
	{

		List<Object> values = new ArrayList<Object>();
		values.add(labelSQLAssocId);
		values.add(labelSQLAssocId);

		List<?> result = executeHQL("getLabelByLabelSQLAssocId", values);

		if (result.size() != 0)
		{
			return (String) result.get(0);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns the query associated with label id
	 * @param labelSQLId
	 * @return
	 * @throws Exception
	 */
	public List<Clob> getQueryByLabelSQLAssocId(Long labelSQLId) throws Exception
	{
		List<Object> values = new ArrayList<Object>();
		values.add(labelSQLId);

		List<?> result = executeHQL("getQueryByLabelSQLAssocId", values);

		return (List<Clob>) result;
	}

	/**
	 * Returns map with the label/display name and query count
	 * @param CPId
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<String, Integer> getLabelQueryResultMapByCPId(Long CPId) throws Exception
	{
		LinkedHashMap<String, Integer> labelQueryResultMap = new LinkedHashMap<String, Integer>();
		LabelSQLAssociationBizlogic labelSQLAssociationBizlogic = new LabelSQLAssociationBizlogic();

		/*Retrieve all the LabelSQL association associated with the CP/CS*/
		List<LabelSQLAssociation> labelSQLAssociations = labelSQLAssociationBizlogic
				.getLabelSQLAssocCollection(CPId);

		for (LabelSQLAssociation labelSQLAssociation : labelSQLAssociations)
		{

			Long labelAssocId = labelSQLAssociation.getId();

			/*insert label and query count in map*/
			String label = getLabelByLabelSQLAssocId(labelAssocId);
			int result = executeQuery(
					clobToStringConversion((getQueryByLabelSQLAssocId(labelAssocId)).get(0)), CPId);

			if (!"".equals(label) && label != null)
			{
				labelQueryResultMap.put(label, result);
			}

		}

		return labelQueryResultMap;
	}

	
	/** This method gets the label sql query results and returns the string with labelsqlid and result.
	 * @param labelSqlId
	 * @param cpId
	 * @return resultsting
	 * @throws Exception
	 */
	public String getQueryResults(Long labelSqlId,Long cpId) throws Exception
	{
		
		LabelSQLBizlogic bizlogic = new LabelSQLBizlogic();
		String labelsqlquery =  clobToStringConversion( bizlogic.getLabelSqlQueryById(labelSqlId));
		
		int result = executeQuery(
				labelsqlquery, cpId);
		String resultString = labelSqlId.toString() + "," + result;
		return resultString;
	}

	/**
	* Executes the stored SQL to give the count
	* @param sql
	* @param CPId
	* @return
	* @throws Exception
	*/
	public int executeQuery(String sql, Long CPId) throws Exception
	{
		int count = 0;
		Session session = HibernateUtil.newSession();
		sql = sql.trim();
		if (sql.endsWith(";"))
		{
			sql = sql.substring(0, sql.length() - 1);
		}
		PreparedStatement stmt = session.doReturningWork(new ReturningWork<Connection>() 
		{
		    @Override
		    public Connection execute(Connection conn) throws SQLException {
		    return conn;
		    }
		}).prepareStatement(sql);
		ResultSet rs = null;
		try
		{
			if (CPId != null)
			{
				stmt.setLong(1, CPId);
			}
			rs = stmt.executeQuery();
			if (rs.next())
			{
				count = rs.getInt(1);
			}

			return count;
		}
		catch (Exception e)
		{
			LOGGER.error("Error executing query -> " + sql);
			LOGGER.error(e);
			return -1;
		}
		finally
		{
			session.close();

		}
	}

	private static String clobToStringConversion(Clob clb) throws IOException, SQLException
	{
		if (clb == null)
			return "";

		StringBuffer str = new StringBuffer();
		String strng;

		BufferedReader bufferRead = new BufferedReader(clb.getCharacterStream());

		while ((strng = bufferRead.readLine()) != null)
			str.append(strng);

		return str.toString();
	}

}
