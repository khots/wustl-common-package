/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.labelSQLApp.util;

import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import au.com.bytecode.opencsv.CSVReader;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLAssociationBizlogic;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLBizlogic;
import edu.wustl.common.labelSQLApp.domain.LabelSQL;
import edu.wustl.common.labelSQLApp.util.InsertCSDashboardItem;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("edu.wustl.common.hibernate.HibernateUtil")
@PowerMockListener(FieldDefaulter.class)
public class InsertCSDashboardItemTest
{

	private Long cSId;
	private Long labelSQLId;
	private Long labelAssocId;
	private String userDefinedLabel;
	private String label;
	private String sql;
	private Integer order;
	private FileReader fileReader;
	private CSVReader csvReader;

	private LabelSQLBizlogic labelSQLBizlogic;
	private LabelSQLAssociationBizlogic labelSQLAssociationBizlogic;

	@Before
	public void setup() throws Exception
	{
		cSId = 1L;
		labelSQLId = 1L;
		labelAssocId = 1L;
		userDefinedLabel = "userDefinedLabel";
		order = 1;
		label = "label";
		sql = "sql";

		String filename = "filename";
		fileReader = PowerMock.createMockAndExpectNew(FileReader.class, filename);

		csvReader = PowerMock.createMockAndExpectNew(CSVReader.class, fileReader);

		labelSQLBizlogic = PowerMock.createMock(LabelSQLBizlogic.class);
		PowerMock.expectNew(LabelSQLBizlogic.class).andReturn(labelSQLBizlogic).anyTimes();

		labelSQLAssociationBizlogic = PowerMock.createMock(LabelSQLAssociationBizlogic.class);
		PowerMock.expectNew(LabelSQLAssociationBizlogic.class).andReturn(
				labelSQLAssociationBizlogic).anyTimes();

	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testUseExistingLabel() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, null, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> result = new ArrayList<LabelSQL>();
		LabelSQL labelSQL = new LabelSQL();
		labelSQL.setId(labelSQLId);
		result.add(labelSQL);

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel(label)).andReturn(result);

		labelSQLAssociationBizlogic.insertLabelSQLAssociation(cSId, labelSQLId, userDefinedLabel,
				order);
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testUseExistingLabelNegative() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, null, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> result = new ArrayList<LabelSQL>();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel(label)).andReturn(result);

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	//	@Test
	//	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
	//			LabelSQLAssociationBizlogic.class})
	//	public void testUseExistingLabelException() throws Exception
	//	{
	//		String[] heading = {"csid", "label", "sql", "displayname", "order"};
	//		String[] row1 = {cSId.toString(), label, null, userDefinedLabel, order.toString()};
	//
	//		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
	//				.andReturn(null).once();
	//
	//		List<LabelSQL> result = new ArrayList<LabelSQL>();
	//
	//		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel(label)).andThrow(new Exception())
	//				.once();
	//
	//		PowerMock.replayAll();
	//		String args[] = {"filename"};
	//
	//		InsertCSDashboardItem.main(args);
	//	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabel() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, sql, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> emptyResult = new ArrayList<LabelSQL>();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel((String) EasyMock.anyObject()))
				.andReturn(emptyResult);

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andReturn(labelSQLId);

		labelSQLAssociationBizlogic.insertLabelSQLAssociation(cSId, labelSQLId, userDefinedLabel,
				order);
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabelWithExistingLabel() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, sql, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> result = new ArrayList<LabelSQL>();

		result.add(new LabelSQL());

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel((String) EasyMock.anyObject()))
				.andReturn(result);

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabelWithDisplayNameNull() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, sql, null, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabelException() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, sql, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> emptyResult = new ArrayList<LabelSQL>();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel((String) EasyMock.anyObject()))
				.andReturn(emptyResult);

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andThrow(new Exception());

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabelSQLException() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, sql, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> emptyResult = new ArrayList<LabelSQL>();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel((String) EasyMock.anyObject()))
				.andReturn(emptyResult);

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andThrow(new SQLException());

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testUseSQLDirectly() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, sql, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andReturn(labelSQLId);

		labelSQLAssociationBizlogic.insertLabelSQLAssociation(cSId, labelSQLId, userDefinedLabel,
				order);
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testUseSQLDirectlyWithDisplayNameNull() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, sql, null, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testUseSQLDirectlyException() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, sql, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andThrow(new Exception());

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testUseSQLDirectlySQLException() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, sql, userDefinedLabel, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andThrow(new SQLException());

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testDeleteAssociation() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, null, userDefinedLabel, "-1"};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLIdByLabelOrDisplayName(cSId, userDefinedLabel))
				.andReturn(labelSQLId);

		labelSQLAssociationBizlogic.deleteLabelSQLAssociation(cSId, labelSQLId);
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testDeleteAssociationNegative() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, null, userDefinedLabel, "-1"};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLIdByLabelOrDisplayName(cSId, userDefinedLabel))
				.andReturn(null);

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testDeleteAssociationNegative1() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, null, userDefinedLabel, "-1"};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLIdByLabelOrDisplayName(cSId, userDefinedLabel))
				.andThrow(new Exception());

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabelHeading() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, null, null, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> emptyResult = new ArrayList<LabelSQL>();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel((String) EasyMock.anyObject()))
				.andReturn(emptyResult);

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andReturn(labelSQLId);

		labelSQLAssociationBizlogic.insertLabelSQLAssociation(cSId, labelSQLId, null, order);
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabelHeadingNegative() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, null, null, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> emptyResult = new ArrayList<LabelSQL>();

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel((String) EasyMock.anyObject()))
				.andReturn(emptyResult);

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andThrow(new Exception());

		labelSQLAssociationBizlogic.insertLabelSQLAssociation(cSId, null, null, order);
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testAddNewLabelHeadingNegative1() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), label, null, null, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		List<LabelSQL> result = new ArrayList<LabelSQL>();
		LabelSQL labelSQL = new LabelSQL();
		labelSQL.setId(labelSQLId);
		result.add(labelSQL);

		EasyMock.expect(labelSQLBizlogic.getLabelSQLByLabel((String) EasyMock.anyObject()))
				.andReturn(result);

		EasyMock.expect(
				labelSQLBizlogic.insertLabelSQL((String) EasyMock.anyObject(), (String) EasyMock
						.anyObject())).andReturn(labelSQLId);

		labelSQLAssociationBizlogic.insertLabelSQLAssociation(cSId, labelSQLId, null, order);
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

	@Test
	@PrepareForTest({InsertCSDashboardItem.class, LabelSQLBizlogic.class,
			LabelSQLAssociationBizlogic.class})
	public void testInvalidEntry() throws Exception
	{
		String[] heading = {"csid", "label", "sql", "displayname", "order"};
		String[] row1 = {cSId.toString(), null, null, null, order.toString()};

		EasyMock.expect(csvReader.readNext()).andReturn(heading).times(1).andReturn(row1).once()
				.andReturn(null).once();

		PowerMock.replayAll();
		String args[] = {"filename"};

		InsertCSDashboardItem.main(args);
	}

}
