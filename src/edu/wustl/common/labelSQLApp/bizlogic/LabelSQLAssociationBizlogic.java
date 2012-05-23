
package edu.wustl.common.labelSQLApp.bizlogic;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import edu.wustl.common.labelSQLApp.domain.LabelSQLAssociation;
import org.hibernate.Session;
import edu.wustl.common.hibernate.HibernateDatabaseOperations;
import edu.wustl.common.hibernate.HibernateUtil;

public class LabelSQLAssociationBizlogic
{

	/**
	 * Gives the LabelSQLAsoociation from the LabelSQLAssociation id
	 * @param labelSQLAssocId
	 * @return
	 * @throws Exception
	 */
	public LabelSQLAssociation getLabelSQLAssocById(Long labelSQLAssocId) throws Exception
	{
		LabelSQLAssociation labelSQLAssociation = null;

		System.out.println("create session getLabelSQLAssocById...");
		Session session = HibernateUtil.newSession();
		System.out.println("create session getLabelSQLAssocById done...");
		try
		{

			HibernateDatabaseOperations<LabelSQLAssociation> dbHandler = new HibernateDatabaseOperations<LabelSQLAssociation>(
					session);
			System.out.println("retrieveById getLabelSQLAssocById...");
			labelSQLAssociation = dbHandler.retrieveById(LabelSQLAssociation.class.getName(),
					labelSQLAssocId);
			System.out.println("retrieveById getLabelSQLAssocById done...");
		}
		finally
		{
			System.out.println("close session getLabelSQLAssocById...");
			session.close();
			System.out.println("close session getLabelSQLAssocById done...");
		}

		return labelSQLAssociation;
	}

	/**
	 * Get all the LabelSQL associations for a CP/CS 
	 * @param CPId
	 * @return
	 * @throws Exception
	 */
	public List<LabelSQLAssociation> getLabelSQLAssocCollection(Long CPId) throws Exception
	{
		List<?> result = null;
		if (CPId == null)
		{
			result = CommonBizlogic.executeHQL("getSystemLabelSQLAssoc", null);
		}
		else
		{
			List<Object> values = new ArrayList<Object>();
			values.add(Long.valueOf(CPId));
			result = CommonBizlogic.executeHQL("getLabelSQLAssocByCPId", values);//runs the HQL
		}

		return (List<LabelSQLAssociation>) result;
	}

	/**
	 * inserting record into LabelSQLAssociation
	 * @param cpId
	 * @param labelSQLId
	 * @param userDefinedLabel
	 * @param order
	 * @throws Exception
	 */
	public void insertLabelSQLAssociation(Long cpId, Long labelSQLId, String userDefinedLabel,
			int order) throws Exception
	{
		LabelSQLAssociation labelSQLAssociation = new LabelSQLAssociation();
		labelSQLAssociation.setLabelSQL(new LabelSQLBizlogic().getLabelSQLById(labelSQLId));
		labelSQLAssociation.setLabelSQLCollectionProtocol(cpId);
		labelSQLAssociation.setUserDefinedLabel(userDefinedLabel);
		labelSQLAssociation.setSeqOrder(order);

		System.out.println("create session insertLabelSQLAssociation...");
		Session session = HibernateUtil.newSession();
		System.out.println("create session insertLabelSQLAssociation done...");
		try
		{
			HibernateDatabaseOperations<LabelSQLAssociation> dbHandler = new HibernateDatabaseOperations<LabelSQLAssociation>(
					session);
			System.out.println("insert insertLabelSQLAssociation...");
			dbHandler.insert(labelSQLAssociation);
			System.out.println("insert insertLabelSQLAssociation done...");
		}
		finally
		{
			System.out.println("close session insertLabelSQLAssociation...");
			session.close();
			System.out.println("close session insertLabelSQLAssociation done...");
		}

	}

	/**
	 * deleting record from LabelSQLAssociation
	 * @param cpId
	 * @param labelSQLId
	 * @throws Exception
	 */
	public void deleteLabelSQLAssociation(Long cpId, Long labelSQLId) throws Exception
	{
		LabelSQLAssociation labelSQLAssociation = new LabelSQLAssociation();
		labelSQLAssociation = getLabelSQLAssocByCPIdAndLabelSQLId(cpId, labelSQLId);//get the labelSQLAssociation from cpId and labelSQLId

		System.out.println("create session deleteLabelSQLAssociation...");
		Session session = HibernateUtil.newSession();
		System.out.println("create session deleteLabelSQLAssociation done...");
		try
		{
			HibernateDatabaseOperations<LabelSQLAssociation> dbHandler = new HibernateDatabaseOperations<LabelSQLAssociation>(
					session);
			System.out.println("delete deleteLabelSQLAssociation...");
			dbHandler.delete(labelSQLAssociation);
			System.out.println("delete deleteLabelSQLAssociation done...");
		}
		finally
		{
			System.out.println("close session deleteLabelSQLAssociation...");
			session.close();
			System.out.println("close session deleteLabelSQLAssociation done...");
		}

	}

	/**
	 * Get display name and association for a CP/CS 
	 * @param CPId
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<String, Long> getAssocAndDisplayNameMapByCPId(Long CPId) throws Exception
	{
		LinkedHashMap<String, Long> displayNameAssocMap = new LinkedHashMap<String, Long>();

		//Retrieving LabelSQLAssociation list by CPId.  
		List<LabelSQLAssociation> labelSQLAssociations = getLabelSQLAssocCollection(CPId);

		for (LabelSQLAssociation labelSQLAssociation : labelSQLAssociations)
		{
			String query = labelSQLAssociation.getLabelSQL().getQuery();//retrieving query by LabelSQLAssociation
			String displayName = new CommonBizlogic().getLabelByLabelSQLAssocId(labelSQLAssociation
					.getId());//retrieve label by association id

			if (query != null && !"".equals(query))
			{ //Associating the displayname name and association id for dashboard items
				if (!"".equals(displayName) && displayName != null)
				{
					displayNameAssocMap.put(displayName, labelSQLAssociation.getId());
				}
			}
			else
			{
				//For group heading map with display name and dummy association
				displayNameAssocMap.put(displayName, new Long(0));
			}

		}
		return displayNameAssocMap;
	}

	/**
	 * Returns the LabelSQLAssociation based on CPId and LabelSQLId
	 * @param cpId
	 * @param labelSQLId
	 * @return
	 * @throws Exception
	 */
	public LabelSQLAssociation getLabelSQLAssocByCPIdAndLabelSQLId(Long cpId, Long labelSQLId)
			throws Exception
	{
		List<Object> values = new ArrayList<Object>();
		String hql="getSysLabelSQLAssocByCPIdAndLabelSQLId";
		if(cpId!=null)
		{
			hql="getLabelSQLAssocByCPIdAndLabelSQLId";
			values.add(cpId);
		} 
		values.add(labelSQLId);
		List<?> result = CommonBizlogic.executeHQL(hql, values);
		if (result.size() != 0)
		{
			return (LabelSQLAssociation) result.get(0);
		}
		else
		{
			return null;
		}

	}

}
