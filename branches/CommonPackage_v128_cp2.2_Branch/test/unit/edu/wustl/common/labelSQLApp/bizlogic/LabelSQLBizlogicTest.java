
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
public class LabelSQLBizlogicTest
{

	private Session session;
	private HibernateDatabaseOperations<LabelSQL> dbHandler;
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
	@PrepareForTest({LabelSQLBizlogic.class})
	public void testGetLabelSQLById() throws Exception
	{
		EasyMock.expect(edu.wustl.common.hibernate.HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		LabelSQL labelSQL = new LabelSQL();

		EasyMock.expect(dbHandler.retrieveById(LabelSQL.class.getName(), labelSQLId)).andReturn(
				labelSQL);

		PowerMock.expectLastCall();

		PowerMock.replayAll(HibernateUtil.class);
		LabelSQL actualResult = new LabelSQLBizlogic().getLabelSQLById(labelSQLId);

		assertEquals(labelSQL, actualResult);

	}

	@Test
	@PrepareForTest({LabelSQLBizlogic.class})
	public void testGetLabelSQLByLabel() throws Exception
	{
		EasyMock.expect(edu.wustl.common.hibernate.HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		List<LabelSQL> labelSQLList = null;
		String label = "Male_Participants";

		EasyMock.expect(dbHandler.retrieve(LabelSQL.class.getName(), "label", label)).andReturn(
				labelSQLList);

		PowerMock.expectLastCall();

		PowerMock.replayAll(HibernateUtil.class);

		List<LabelSQL> actualResult = new LabelSQLBizlogic().getLabelSQLByLabel(label);

		assertEquals(labelSQLList, actualResult);

	}

	@Test
	@PrepareForTest({LabelSQLBizlogic.class})
	public void testInsertLabelSQL() throws Exception
	{
		EasyMock.expect(edu.wustl.common.hibernate.HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		dbHandler.insert((LabelSQL) EasyMock.anyObject());
		EasyMock.expectLastCall();

		PowerMock.replayAll(HibernateDatabaseOperations.class);

		Long actualResult = new LabelSQLBizlogic().insertLabelSQL("Male_Participants", "SQL");

		EasyMock.verify(dbHandler);

		assertEquals(Long.class, actualResult.getClass());
	}

	@Test
	@PrepareForTest({LabelSQLBizlogic.class})
	public void testGetAllLabelSQL() throws Exception
	{
		EasyMock.expect(edu.wustl.common.hibernate.HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		List<LabelSQL> labelSQLList = null;

		EasyMock.expect(dbHandler.retrieve(LabelSQL.class.getName())).andReturn(labelSQLList);

		PowerMock.expectLastCall();

		PowerMock.replayAll(HibernateUtil.class);

		List<LabelSQL> actualResult = new LabelSQLBizlogic().getAllLabelSQL();

		assertEquals(labelSQLList, actualResult);

	}

	@Test
	@PrepareForTest({CommonBizlogic.class})
	public void testGetLabelSQLIdByLabelOrDisplayName() throws Exception
	{
		List list = new ArrayList<Long>();
		list.add(labelSQLId);
		EasyMock.expect(
				(CommonBizlogic.executeHQL((String) EasyMock.anyObject(), (List<Object>) EasyMock
						.anyObject()))).andReturn(list);

		PowerMock.replay(CommonBizlogic.class);

		Long actualResult = new LabelSQLBizlogic().getLabelSQLIdByLabelOrDisplayName(cSId,
				"display name");

		assertEquals(labelSQLId, actualResult);

	}

	@Test
	@PrepareForTest({CommonBizlogic.class})
	public void testGetNullLabelSQLAssocByCPIdAndLabelSQLId() throws Exception
	{
		List list = new ArrayList<Long>();
		EasyMock.expect(
				(CommonBizlogic.executeHQL((String) EasyMock.anyObject(), (List<Object>) EasyMock
						.anyObject()))).andReturn(list);

		PowerMock.replay(CommonBizlogic.class);

		Long actualResult = new LabelSQLBizlogic().getLabelSQLIdByLabelOrDisplayName(cSId,
				"display name");

		assertEquals(null, actualResult);

	}

}
