
package edu.wustl.common.labelSQLApp.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import edu.wustl.common.hibernate.HibernateDatabaseOperations;
import edu.wustl.common.hibernate.HibernateUtil;
import edu.wustl.common.labelSQLApp.bizlogic.LabelSQLBizlogic;
import edu.wustl.common.labelSQLApp.domain.LabelSQL;

@RunWith(PowerMockRunner.class)
public class LabelSQLBizlogicTest
{

	@Test
	@PrepareForTest({LabelSQLBizlogic.class, edu.wustl.common.hibernate.HibernateUtil.class,
			HibernateDatabaseOperations.class, Session.class})
	public void testGetLabelSQLById() throws Exception
	{
		Session session = EasyMock.createMock(Session.class);
		PowerMock.mockStatic(edu.wustl.common.hibernate.HibernateUtil.class);
		HibernateDatabaseOperations<LabelSQL> dbHandler = PowerMock
				.createMock(HibernateDatabaseOperations.class);

		EasyMock.expect(edu.wustl.common.hibernate.HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		Long id = 1L;
		LabelSQL labelSQL = new LabelSQL();

		EasyMock.expect(dbHandler.retrieveById(LabelSQL.class.getName(), id)).andReturn(labelSQL);

		PowerMock.expectLastCall();

		PowerMock.replayAll(HibernateUtil.class);
		LabelSQL actualResult = new LabelSQLBizlogic().getLabelSQLById(id);

		assertEquals(labelSQL, actualResult);

	}

	@Test
	@PrepareForTest({LabelSQLBizlogic.class, edu.wustl.common.hibernate.HibernateUtil.class,
			HibernateDatabaseOperations.class, Session.class})
	public void testGetLabelSQLByLabel() throws Exception
	{
		Session session = EasyMock.createMock(Session.class);
		PowerMock.mockStatic(edu.wustl.common.hibernate.HibernateUtil.class);
		HibernateDatabaseOperations<LabelSQL> dbHandler = PowerMock
				.createMock(HibernateDatabaseOperations.class);

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
	@PrepareForTest({LabelSQLBizlogic.class, edu.wustl.common.hibernate.HibernateUtil.class,
			HibernateDatabaseOperations.class, Session.class})
	public void testInsertLabelSQL() throws Exception
	{
		Session session = EasyMock.createMock(Session.class);
		PowerMock.mockStatic(edu.wustl.common.hibernate.HibernateUtil.class);
		HibernateDatabaseOperations<LabelSQL> dbHandler = PowerMock
				.createMock(HibernateDatabaseOperations.class);

		EasyMock.expect(edu.wustl.common.hibernate.HibernateUtil.newSession()).andReturn(session);
		PowerMock.expectNew(HibernateDatabaseOperations.class, session).andReturn(dbHandler);

		final LabelSQL labelSQL = new LabelSQL();
		labelSQL.setLabel("Male_Participants");
		labelSQL.setQuery("SQL");

		dbHandler.insert((LabelSQL) EasyMock.anyObject());
		EasyMock.expectLastCall().times(0, 1);

		PowerMock.replayAll(HibernateDatabaseOperations.class);

		long actualResult = new LabelSQLBizlogic().insertLabelSQL("Male_Participants", "SQL");

		EasyMock.verify(dbHandler);

		assertEquals(0L, actualResult);

	}

	@Test
	@PrepareForTest({LabelSQLBizlogic.class, edu.wustl.common.hibernate.HibernateUtil.class,
			HibernateDatabaseOperations.class, Session.class})
	public void testGetAllLabelSQL() throws Exception
	{
		Session session = EasyMock.createMock(Session.class);
		PowerMock.mockStatic(edu.wustl.common.hibernate.HibernateUtil.class);
		HibernateDatabaseOperations<LabelSQL> dbHandler = PowerMock
				.createMock(HibernateDatabaseOperations.class);

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
	@PrepareForTest({LabelSQLBizlogic.class})
	public void testGetLabelSQLIdByLabelOrDisplayName() throws Exception
	{
		LabelSQLBizlogic labelSQLBizlogic = PowerMock.createMock(LabelSQLBizlogic.class);

		EasyMock.expect(labelSQLBizlogic.getLabelSQLIdByLabelOrDisplayName(1L, "")).andReturn(1L);
		PowerMock.replayAll(LabelSQLBizlogic.class);
		Long actualResult = new LabelSQLBizlogic().getLabelSQLIdByLabelOrDisplayName(1L, "");

		assertEquals(null, actualResult);

	}

}
