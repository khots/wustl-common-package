
package edu.wustl.common.util.databasemanager;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author chetan_patil
 * @created Sep 14, 2007, 3:35:35 PM
 */
public class HibernateUtility
{

	/**
	 * GET_PARAMETERIZED_QUERIES_DETAILS String details of parameterized queries.
	 */
	public static final String GET_PARAMETERIZED_QUERIES_DETAILS = "getParameterizedQueriesDetails";
	/** This method add details of query in a list.
	 * @param queryName String name of query.
	 * @param values List of type Object object list.
	 * @return Collection containing details of query.
	 * @throws HibernateException exception of Hibernate.
	 */

	public static Collection executeHQL(String queryName, List<Object> values)
			throws HibernateException
	{
		try
		{
			Session session = DBUtil.currentSession();
			Query query = session.getNamedQuery(queryName);

			if (values != null)
			{
				for (int counter = 0; counter < values.size(); counter++)
				{
					Object value = values.get(counter);
					String objectType = value.getClass().getName();
					String onlyClassName = objectType.substring(objectType.lastIndexOf('.')
							+ 1,objectType.length());
					if (String.class.getSimpleName().equals(onlyClassName))
					{
						query.setString(counter, (String) value);
					}
					else if (Integer.class.getSimpleName().equals(onlyClassName))
					{
						query.setInteger(counter, Integer.parseInt(value.toString()));
					}
					else if (Long.class.getSimpleName().equals(onlyClassName))
					{
						query.setLong(counter, Long.parseLong(value.toString()));
					}

				}
			}
			return query.list();
		}
		finally
		{
			DBUtil.closeSession();
		}
	}
	/**
	 * Return the output of execution of query.
	 * @param queryName String name of query.
	 * @return Collection containing output of execution of query.
	 * @throws HibernateException exception of Hibernate.
	 */
	public static Collection executeHQL(String queryName) throws HibernateException
	{
		return executeHQL(queryName, null);
	}

}
