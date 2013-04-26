
package edu.wustl.common.labelSQLApp.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import edu.wustl.common.util.logger.Logger;
import au.com.bytecode.opencsv.CSVReader;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLAssociationBizlogic;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLBizlogic;
import edu.wustl.common.labelSQLApp.domain.LabelSQL;
import edu.wustl.common.labelSQLApp.exception.LabelSQLAppException;

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
				Long cpId = null;
				Integer order = new Integer(0);
				if (!nextLine[0].equals("") && nextLine[0] != null)
				{
					cpId = Long.valueOf(nextLine[0]);
				}
				if (!nextLine[4].equals("") && nextLine[4] != null)
				{
					order = Integer.parseInt(nextLine[4]);
				}
				associateCSWithDashboardItem(cpId, nextLine[1], nextLine[2], nextLine[3], order);

			}
			catch (LabelSQLAppException e)
			{
				Logger.out.error("Error inserting record " + cnt + " " + e.getMessage());
			}
		}
	}

	private static void associateCSWithDashboardItem(Long CSId, String label, String sql,
			String displayName, int order) throws LabelSQLAppException
	{
		Long labelSQLId = null;

		if ((label == null || "".equals(label)) && !(sql == null || "".equals(sql)) && (order >= 0))
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
			}catch (SQLException e){
				Logger.out.error(e.getCause());
			}			
			catch (Exception e)
			{
				Logger.out.error("Error while inserting SQL -> " + sql);
				e.printStackTrace();
			}
		}
		else if ((sql == null || "".equals(sql)) && !(label == null || "".equals(label))
				&& !(displayName == null || "".equals(displayName)) && (order >= 0))
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
			catch (SQLException e){
				Logger.out.error(e.getCause());
			}
			catch (Exception e)
			{
				Logger.out.error("Error associating label -> " + label);
				e.printStackTrace();
			}

		}
		else if (!(sql == null || "".equals(sql)) && !(label == null || "".equals(label))
				&& (order >= 0))
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
			catch (SQLException e){
				Logger.out.error(e.getCause());
			}
			catch (Exception e)
			{
				Logger.out.error("Error associating label -> " + label);
				e.printStackTrace();
			}
		}
		else if ((sql == null || "".equals(sql))
				&& !(displayName == null || "".equals(displayName)) && (order == -1))
		{
			//this is the case to delete the association of labelSQL and CS

			try
			{
				labelSQLId = new LabelSQLBizlogic().getLabelSQLIdByLabelOrDisplayName(CSId,
						displayName);
			}
			catch (Exception e)
			{
				Logger.out.error("Error retrieving association for CS: " + CSId
						+ " with display name -> " + displayName);
				e.printStackTrace();
			}

			if (labelSQLId == null)
			{
				throw new LabelSQLAppException("Display name: " + displayName
						+ " is not associated with the CS: " + CSId);
			}
			else
			{

				try
				{
					new LabelSQLAssociationBizlogic().deleteLabelSQLAssociation(CSId, labelSQLId);
				}
				catch (Exception e)
				{
					Logger.out.error("Error deleting association " + CSId + " --> " + displayName);
					e.printStackTrace();
				}
			}
		}
		else if ((sql == null || "".equals(sql)) && !(label == null || "".equals(label))
				&& (displayName == null || "".equals(displayName)) && (order >= 0))
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
				Logger.out.error("Error retrieving LabelSQL from label -> " + label);
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
					Logger.out.error("Error inserting label: " + label);
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
			catch (SQLException e){
				Logger.out.error(e.getCause());
			}
			catch (Exception e)
			{
				Logger.out.error("Error associating Label Heading " + label);
				e.printStackTrace();
			}

		}
		else
		{
			throw new LabelSQLAppException("Invalid CSV entries");
		}

	}

}
