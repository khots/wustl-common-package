
package edu.wustl.common.labelSQLApp.bizlogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.labelSQLApp.domain.LabelSQL;
import edu.wustl.labelSQLApp.domain.LabelSQLAssociation;

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

		Session session = HibernateUtil.newSession();
		try
		{

			HibernateDatabaseOperations<LabelSQLAssociation> dbHandler = new HibernateDatabaseOperations<LabelSQLAssociation>(
					session);
			labelSQLAssociation = dbHandler.retrieveById(LabelSQLAssociation.class.getName(),
					labelSQLAssocId);
		}
		finally
		{
			session.close();
		}

		return labelSQLAssociation;
	}

	/**
	 * Get all the LabelSQL associations for a CP/CS 
	 * @param CPId
	 * @return
	 * @throws Exception
	 */
	public List<LabelSQLAssociation> getLabelSQLAssocCollectionByCPId(Long CPId) throws Exception
	{
		List<Object> values = new ArrayList<Object>();
		values.add(CPId);

		List<?> result = CommonBizlogic.executeHQL("getLabelSQLAssocByCPId", values);//runs the HQL

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

		Session session = HibernateUtil.newSession();
		try
		{
			HibernateDatabaseOperations<LabelSQLAssociation> dbHandler = new HibernateDatabaseOperations<LabelSQLAssociation>(
					session);
			dbHandler.insert(labelSQLAssociation);
		}
		finally
		{
			session.close();
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

		Session session = HibernateUtil.newSession();
		try
		{
			HibernateDatabaseOperations<LabelSQLAssociation> dbHandler = new HibernateDatabaseOperations<LabelSQLAssociation>(
					session);

			dbHandler.delete(labelSQLAssociation);
		}
		finally
		{
			session.close();
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
		List<LabelSQLAssociation> labelSQLAssociations = getLabelSQLAssocCollectionByCPId(CPId);

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
		values.add(cpId);
		values.add(labelSQLId);

		List<?> result = CommonBizlogic.executeHQL("getLabelSQLAssocByCPIdAndLabelSQLId", values);

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
