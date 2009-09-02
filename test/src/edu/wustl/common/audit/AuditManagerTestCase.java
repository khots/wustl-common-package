package edu.wustl.common.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.MyDAOImpl;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;

/**
 * This test class is for test cases of methods in AuditManager class.
 * @author ravi_kumar
 *
 */
public class AuditManagerTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AuditManagerTestCase.class);
	private static final String IP_ADDR="127.0.0.1";
	
	public void testCompare()
	{
		Auditable auditable1=new AuditableImpl(Long.valueOf(1));
		Auditable auditable2=new AuditableImpl(Long.valueOf(2));
		AuditManager auditManager= new AuditManager();
		auditManager.setIpAddress(IP_ADDR);
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.audit(auditable1, auditable2,"eventType");
			assertTrue("Auditable compared successfully.", true);
		}
		catch(AuditException exception)
		{
			fail("Not able to compare.");
			logger.debug("Not able to compare.", exception);
		}
	}

	public void testCompareWithPrevIdValueNull()
	{
		Auditable currentObj=new AuditableImpl(Long.valueOf(1));
		Auditable previousObj=new AuditableImpl(Long.valueOf(2));
		((AuditableImpl)previousObj).setId(null);
		AuditManager auditManager= new AuditManager();
		auditManager.setIpAddress(IP_ADDR);
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.audit(currentObj, previousObj,"eventType");
			assertTrue("Auditable compared successfully.", true);
		}
		catch(AuditException exception)
		{
			fail("Not able to compare.");
			logger.debug("Not able to compare.", exception);
		}
	}
	
	public void testCompareWithCurrentIdValueNull()
	{
		Auditable currentObj=new AuditableImpl(Long.valueOf(1));
		((AuditableImpl)currentObj).setId(null);
		Auditable previousObj=new AuditableImpl(Long.valueOf(2));
		AuditManager auditManager= new AuditManager();
		auditManager.setIpAddress(IP_ADDR);
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.audit(currentObj, previousObj,"eventType");
			assertTrue("Auditable compared successfully.", true);
		}
		catch(AuditException exception)
		{
			fail("Not able to compare.");
			logger.debug("Not able to compare.", exception);
		}
	}
	public void testCompareWithCurrentValueNull()
	{
		Auditable previousObj=new AuditableImpl(Long.valueOf(2));
		AuditManager auditManager= new AuditManager();
		auditManager.setIpAddress(IP_ADDR);
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.audit(null, previousObj,"eventType");
			assertTrue("Auditable compared successfully.", true);
		}
		catch(Exception exception)
		{
			fail("Not able to compare.");
			logger.debug("Not able to compare.", exception);
		}
	}

	public void testInsert()
	{
		MyDAOImpl.isTestForFail=false;
		AuditManager auditManager= new AuditManager();
		auditManager.setIpAddress(IP_ADDR);
		auditManager.setUserId(Long.valueOf(100));		
		try
		{
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("commonpackagetest");
			DAO dao = daoFactory.getDAO();
			auditManager.addAuditEventLogs(dao,getAuditEventLogCollection());
			auditManager.insert(dao);
			assertTrue("Event details inserted successfully in database.", true);
		}
		catch(Exception exception)
		{
			fail("Fail to insert Event in database.");
			logger.debug("Fail to insert Event in database.", exception);
		}
	}

	Collection<AuditEventLog> getAuditEventLogCollection()
	{

		AuditEvent auditEvent=getAuditEvent(1);
		AuditEventLog auditEventLog=getAuditEventLog(1);
		AuditEventDetails auditEventDetails=getAuditEventDetails(1);

		auditEventLog.setAuditEvent(auditEvent);
		auditEventDetails.setAuditEventLog(auditEventLog);

		Collection<AuditEventDetails> auditEventDetailsColl= new ArrayList<AuditEventDetails>();
		auditEventDetailsColl.add(auditEventDetails);

		Collection<AuditEventLog> auditEventLogColl= new ArrayList<AuditEventLog>();
		auditEventLogColl.add(auditEventLog);

		auditEvent.setAuditEventLogCollection(auditEventLogColl);
		return auditEventLogColl;
	}
	private AuditEventLog getAuditEventLog(int auditEventLogId)
	{
		AuditEventLog auditEventLog= new AuditEventLog();
		auditEventLog.setEventType("eventType");
		auditEventLog.setObjectIdentifier(Long.valueOf(auditEventLogId));
		auditEventLog.setId(Long.valueOf(auditEventLogId));
		auditEventLog.setObjectName("objectName");
		return auditEventLog;
	}

	private AuditEventDetails getAuditEventDetails(int AuditEventDetailsId)
	{
		AuditEventDetails auditEventDetails= new AuditEventDetails();
		auditEventDetails.setCurrentValue("currentValue");
		auditEventDetails.setElementName("elementName");
		auditEventDetails.setId(Long.valueOf(AuditEventDetailsId));
		auditEventDetails.setPreviousValue("previousValue");
		return auditEventDetails;
	}
	private AuditEvent getAuditEvent(int auditEventId)
	{
		AuditEvent auditEvent= new AuditEvent();
		auditEvent.setComments("Comment");
		auditEvent.setId(Long.valueOf(auditEventId));
		auditEvent.setIpAddress(IP_ADDR);
		auditEvent.setTimestamp(new Date());
		auditEvent.setUserId(Long.valueOf(auditEventId));
		return auditEvent;
	}
}
