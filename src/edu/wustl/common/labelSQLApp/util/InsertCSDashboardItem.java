
package edu.wustl.common.labelSQLApp.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.labelSQLApp.bizlogic.LabelSQLAssociationBizlogic;
import edu.wustl.labelSQLApp.bizlogic.LabelSQLBizlogic;
import edu.wustl.labelSQLApp.domain.LabelSQL;
import edu.wustl.labelSQLApp.exception.LabelSQLAppException;

public class InsertCSDashboardItem
{

	public static void main(String args[]) throws IOException
	{
		CSVReader reader = new CSVReader(new FileReader(args[0]));
		String[] nextLine;
		reader.readNext();
		int cnt = 0;

		while ((nextLine = reader.readNext()) != null)
		{
			try
			{
				cnt++;
				associateCSWithDashboardItem(Long.valueOf(nextLine[0]), nextLine[1], nextLine[2],
						nextLine[3], Integer.parseInt(nextLine[4]));

			}
			catch (LabelSQLAppException e)
			{
				Logger.out.error("Error inserting record " + cnt);
				e.printStackTrace();
			}
		}

	}

	private static void associateCSWithDashboardItem(Long CSId, String label, String sql,
			String displayName, int order) throws LabelSQLAppException
	{
		Long labelSQLId = null;

		if ((label == null || "".equals(label)) && !(sql == null || "".equals(sql)))
		{
			//this is the case to use a SQL directly, 
			//add the SQL with NULL label and associate with CS
			if (displayName == null || "".equals(displayName))
			{
				throw new LabelSQLAppException("Display name is mandatory");
			}
			try
			{
				labelSQLId = new LabelSQLBizlogic().insertLabelSQL(null, sql);

				new LabelSQLAssociationBizlogic().insertLabelSQLAssociation(CSId, labelSQLId,
						displayName, order);
			}
			catch (Exception e)
			{
				Logger.out.error("Error while inserting SQL -> " + sql);
				e.printStackTrace();
			}
		}
		else if ((sql == null || "".equals(sql)) && !(label == null || "".equals(label))
				&& !(displayName == null || "".equals(displayName)))
		{
			//this is the case to use an existing label, 
			//check if there exists a label with the same name, if not throw error if yes use it
			List<LabelSQL> result = null;
			try
			{
				result = new LabelSQLBizlogic().getLabelSQLByLabel(label);
			}
			catch (Exception e)
			{
				Logger.out.error("Error retrieving LabelSQL from label -> " + label);
				e.printStackTrace();
			}
			if (result.size() == 0)
			{
				throw new LabelSQLAppException("There is no existing Label: " + label);
			}
			labelSQLId = result.get(0).getId();

			try
			{
				new LabelSQLAssociationBizlogic().insertLabelSQLAssociation(CSId, labelSQLId,
						displayName, order);
			}
			catch (Exception e)
			{
				Logger.out.error("Error associating label -> " + label);
				e.printStackTrace();
			}

		}
		else if (!(sql == null || "".equals(sql)) && !(label == null || "".equals(label)))
		{
			//this is the case to add a new label, 
			//check if there exists a label with the same name, if yes throw error
			if (displayName == null || "".equals(displayName))
			{
				throw new LabelSQLAppException("Display name is mandatory");
			}

			List<LabelSQL> result = null;
			try
			{
				result = new LabelSQLBizlogic().getLabelSQLByLabel(label);
			}
			catch (Exception e)
			{
				Logger.out.error("Error retrieving LabelSQL from label -> " + label);
				e.printStackTrace();
			}
			if (result.size() != 0)
			{
				throw new LabelSQLAppException("Label already exists : " + label);
			}
			try
			{
				labelSQLId = new LabelSQLBizlogic().insertLabelSQL(label, sql);

				new LabelSQLAssociationBizlogic().insertLabelSQLAssociation(CSId, labelSQLId,
						displayName, order);
			}
			catch (Exception e)
			{
				Logger.out.error("Error associating label -> " + label);
				e.printStackTrace();
			}
		}
		else if ((sql == null || "".equals(sql)) && !(label == null || "".equals(label))
				&& (displayName == null || "".equals(displayName)) && (order == -1))
		{
			//this is the case to delete the association of labelSQL and CS

			try
			{
				labelSQLId = new LabelSQLBizlogic().getLabelSQLIdByLabelOrDisplayName(CSId, label);
			}
			catch (Exception e)
			{
				Logger.out.error("Error retrieving association for label -> " + label);
				e.printStackTrace();
			}

			if (labelSQLId == null)
			{
				throw new LabelSQLAppException("Label: " + label
						+ " is not associated with the CS: " + CSId);
			}

			try
			{
				new LabelSQLAssociationBizlogic().deleteLabelSQLAssociation(CSId, labelSQLId);
			}
			catch (Exception e)
			{
				Logger.out.error("Error deleting association");
				e.printStackTrace();
			}
		}
		else if ((sql == null || "".equals(sql)) && !(label == null || "".equals(label))
				&& (displayName == null || "".equals(displayName)) && (order != -1))
		{
			//this is the case to add a new label heading, 
			//check if there exists a label heading with the same name, if yes throw error
			List<LabelSQL> result = null;
			try
			{
				result = new LabelSQLBizlogic().getLabelSQLByLabel(label);
			}
			catch (Exception e)
			{
				Logger.out.error("Error retrieving LabelSQL from label -> "+label);
				e.printStackTrace();
			}
			if (result.size() == 0)
			{
				try
				{
					labelSQLId = new LabelSQLBizlogic().insertLabelSQL(label, null);
				}
				catch (Exception e)
				{
					Logger.out.error("Error inserting label: "+label);
					e.printStackTrace();
				}
			}
			else
			{
				labelSQLId = result.get(0).getId();
			}
			try
			{
				new LabelSQLAssociationBizlogic().insertLabelSQLAssociation(CSId, labelSQLId, null,
						order);
			}
			catch (Exception e)
			{
				Logger.out.error("Error associating Label Heading "+label);
				e.printStackTrace();
			}

		}

	}

}
