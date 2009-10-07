package edu.wustl.common.audit;

import java.util.Iterator;

import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.DataAuditEventLog;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

/**
 * This class provides the exact implementation to insert the DataAuditEventLog.
 * @author niharika_sharma
 *
 */
public class DataAuditManager
{
	/**
	 *
	 * @param dao DAO objkect
	 * @param auditEventLog DataAuditEventLog object.
	 * @throws DAOException throw DAOException
	 */
	protected void insertAuditEventLogs(DAO dao,
			DataAuditEventLog auditEventLog) throws DAOException
			{

		Iterator<AuditEventDetails> auditEventDetailsIterator = auditEventLog
		.getAuditEventDetailsCollcetion().iterator();
		while (auditEventDetailsIterator.hasNext())
		{
			AuditEventDetails auditEventDetails = (AuditEventDetails) auditEventDetailsIterator
			.next();
			auditEventDetails.setAuditEventLog(auditEventLog);
			//dao.insert(auditEventDetails,false,"");
			dao.insert(auditEventDetails);
		}
	}

}
