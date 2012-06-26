
package edu.wustl.common.labelSQLApp.bizlogic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import edu.wustl.common.hibernate.HibernateDatabaseOperations;
import edu.wustl.common.hibernate.HibernateUtil;
import edu.wustl.common.labelSQLApp.bizlogic.CommonBizlogic;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLAssociationBizlogic;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLBizlogic;
import edu.wustl.common.labelSQLApp.domain.LabelSQL;
import edu.wustl.common.labelSQLApp.domain.LabelSQLAssociation;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("edu.wustl.common.hibernate.HibernateUtil")
@PowerMockListener(FieldDefaulter.class)
public class LabelSQLAssociationBizlogicTest
{

	private Session session;
	private HibernateDatabaseOperations<LabelSQLAssociation> dbHandler;
	private Long cSId;
	private Long labelSQLId;
	private Long labelAssocId;

	@Before
	public void setup()
	{
		session = EasyMock.createMock(Session.class);
		PowerMock.mockStatic(HibernateUtil.class);
		dbHandler = PowerMock.createMock(HibernateDatabaseOperations.class);
		PowerMock.mockStaticPartial(CommonBizlogic.class, "executeHQL");
		cSId = 1L;
		labelSQLId = 1L;
		labelAssocId = 1L;
	}

	@Test
	@PrepareForTest({LabelSQLAssociationBizlogic.class})
	public void testGetLabelSQLAssocById() throws Exception
	{
		EasyMock.expect(HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		LabelSQLAssociation labelSQLAssociation = new LabelSQLAssociation();

		EasyMock.expect(dbHandler.retrieveById(LabelSQLAssociation.class.getName(), labelAssocId))
				.andReturn(labelSQLAssociation);

		PowerMock.expectLastCall();

		PowerMock.replayAll();

		LabelSQLAssociation actualResult = new LabelSQLAssociationBizlogic()
				.getLabelSQLAssocById(labelAssocId);

		assertEquals(labelSQLAssociation, actualResult);

	}

	@Test
	@PrepareForTest({CommonBizlogic.class})
	public void testGetLabelSQLAssocCollection() throws Exception
	{
		List<LabelSQLAssociation> expectedResult = new ArrayList<LabelSQLAssociation>();

		EasyMock.expect(
				(CommonBizlogic.executeHQL((String) EasyMock.anyObject(), (List<Object>) EasyMock
						.anyObject()))).andReturn((List) expectedResult);

		PowerMock.replay(CommonBizlogic.class);

		List<LabelSQLAssociation> actualResult = new LabelSQLAssociationBizlogic()
				.getLabelSQLAssocCollection(cSId);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	@PrepareForTest({LabelSQLAssociationBizlogic.class, LabelSQLBizlogic.class})
	public void testInsertLabelSQLAssociation() throws Exception
	{
		LabelSQLBizlogic labelSQLBizlogic = PowerMock.createPartialMock(LabelSQLBizlogic.class,
				"getLabelSQLById");
		PowerMock.expectNew(LabelSQLBizlogic.class).andReturn(labelSQLBizlogic);
		LabelSQL label = new LabelSQL();
		EasyMock.expect(labelSQLBizlogic.getLabelSQLById(labelSQLId)).andReturn(label);
		EasyMock.expect(HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		dbHandler.insert((LabelSQLAssociation) EasyMock.anyObject());
		EasyMock.expectLastCall();

		PowerMock.replayAll();
		new LabelSQLAssociationBizlogic().insertLabelSQLAssociation(cSId, labelSQLId, null, 0);

	}

	@Test
	@PrepareForTest({LabelSQLAssociationBizlogic.class})
	public void testDeleteLabelSQLAssociation() throws Exception
	{
		LabelSQLAssociationBizlogic labelSQLAssociationBizlogic = PowerMock.createPartialMock(
				LabelSQLAssociationBizlogic.class, "getLabelSQLAssocByCPIdAndLabelSQLId");
		PowerMock.expectNew(LabelSQLAssociationBizlogic.class).andReturn(
				labelSQLAssociationBizlogic);

		EasyMock.expect(
				labelSQLAssociationBizlogic.getLabelSQLAssocByCPIdAndLabelSQLId(cSId, labelSQLId))
				.andReturn(new LabelSQLAssociation());

		EasyMock.expect(HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		dbHandler.delete((LabelSQLAssociation) EasyMock.anyObject());
		EasyMock.expectLastCall();

		PowerMock.replayAll();

		new LabelSQLAssociationBizlogic().deleteLabelSQLAssociation(cSId, labelSQLId);

	}

	@Test
	@PrepareForTest({LabelSQLAssociationBizlogic.class, CommonBizlogic.class, LabelSQL.class,
			LabelSQLAssociation.class})
	public void testGetAssocAndDisplayNameMapByCPId() throws Exception
	{
		LabelSQLAssociationBizlogic labelSQLAssociationBizlogic = PowerMock.createPartialMock(
				LabelSQLAssociationBizlogic.class, "getLabelSQLAssocCollection");
		PowerMock.expectNew(LabelSQLAssociationBizlogic.class).andReturn(
				labelSQLAssociationBizlogic);

		List<LabelSQLAssociation> list = new ArrayList<LabelSQLAssociation>();
		LabelSQLAssociation labelSQLAssociation = PowerMock
				.createMockAndExpectNew(LabelSQLAssociation.class);

		list.add(labelSQLAssociation);

		EasyMock.expect(labelSQLAssociation.getLabelSQL()).andReturn(new LabelSQL());

		EasyMock.expect(labelSQLAssociationBizlogic.getLabelSQLAssocCollection(cSId)).andReturn(
				list);

		CommonBizlogic commonBizlogic = PowerMock.createPartialMock(CommonBizlogic.class,
				"getLabelByLabelSQLAssocId");
		PowerMock.expectNew(CommonBizlogic.class).andReturn(commonBizlogic);

		EasyMock.expect(labelSQLAssociation.getId()).andReturn(EasyMock.anyLong());
		EasyMock.expect(commonBizlogic.getLabelByLabelSQLAssocId(labelAssocId)).andReturn(
				"Male Participants").anyTimes();

		PowerMock.replayAll();

		new LabelSQLAssociationBizlogic().getAssocAndDisplayNameMapByCPId(cSId);
	}

	@Test
	@PrepareForTest({LabelSQLAssociationBizlogic.class, CommonBizlogic.class, LabelSQL.class,
			LabelSQLAssociation.class})
	public void testGetAssocAndDisplayNameMapByCPIdWithAssociation() throws Exception
	{
		LabelSQLAssociationBizlogic labelSQLAssociationBizlogic = PowerMock.createPartialMock(
				LabelSQLAssociationBizlogic.class, "getLabelSQLAssocCollection");
		PowerMock.expectNew(LabelSQLAssociationBizlogic.class).andReturn(
				labelSQLAssociationBizlogic);

		List<LabelSQLAssociation> list = new ArrayList<LabelSQLAssociation>();
		LabelSQLAssociation labelSQLAssociation = PowerMock
				.createMockAndExpectNew(LabelSQLAssociation.class);

		LabelSQL labelSQL = new LabelSQL();
		labelSQL.setQuery("Query");

		list.add(labelSQLAssociation);

		EasyMock.expect(labelSQLAssociation.getLabelSQL()).andReturn(labelSQL);

		EasyMock.expect(labelSQLAssociationBizlogic.getLabelSQLAssocCollection(cSId)).andReturn(
				list);

		CommonBizlogic commonBizlogic = PowerMock.createPartialMock(CommonBizlogic.class,
				"getLabelByLabelSQLAssocId");
		PowerMock.expectNew(CommonBizlogic.class).andReturn(commonBizlogic);

		EasyMock.expect(labelSQLAssociation.getId()).andReturn(EasyMock.anyLong()).anyTimes();
		EasyMock.expect(commonBizlogic.getLabelByLabelSQLAssocId(labelAssocId)).andReturn(
				"Male Participants");

		PowerMock.replayAll();

		new LabelSQLAssociationBizlogic().getAssocAndDisplayNameMapByCPId(cSId);
	}

	@Test
	@PrepareForTest({CommonBizlogic.class})
	public void testGetLabelSQLAssocByCPIdAndLabelSQLId() throws Exception
	{
		LabelSQLAssociation labelSQLAssociation = new LabelSQLAssociation();
		List list = new ArrayList<LabelSQLAssociation>();
		list.add(labelSQLAssociation);

		EasyMock.expect(
				(CommonBizlogic.executeHQL((String) EasyMock.anyObject(), (List<Object>) EasyMock
						.anyObject()))).andReturn(list);

		PowerMock.replay(CommonBizlogic.class);

		LabelSQLAssociation actualResult = new LabelSQLAssociationBizlogic()
				.getLabelSQLAssocByCPIdAndLabelSQLId(cSId, labelSQLId);

		assertEquals(labelSQLAssociation, actualResult);

	}

	@Test
	@PrepareForTest({CommonBizlogic.class})
	public void testGetNullLabelSQLAssocByCPIdAndLabelSQLId() throws Exception
	{
		List list = new ArrayList<LabelSQLAssociation>();
		EasyMock.expect(
				(CommonBizlogic.executeHQL((String) EasyMock.anyObject(), (List<Object>) EasyMock
						.anyObject()))).andReturn(list);

		PowerMock.replay(CommonBizlogic.class);

		LabelSQLAssociation actualResult = new LabelSQLAssociationBizlogic()
				.getLabelSQLAssocByCPIdAndLabelSQLId(cSId, labelSQLId);

		assertEquals(null, actualResult);

	}

}
