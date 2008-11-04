
package edu.wustl.common.dao;

import java.util.Collection;

/** This interface defines methods which are specific to Hibernate operations .*/

public interface HibernateDAO extends DAO
{

	/**
	 * Loaded Clean Object.
	 * @param sourceObjectName source Object Name
	 * @param identifier identifier.
	 * @return Object
	 * @throws Exception Exception.
	 */
	Object loadCleanObj(String sourceObjectName, Long identifier) throws Exception;

	/**
	 * Add AuditEvent Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	void addAuditEventLogs(Collection auditEventDetailsCollection);

}
