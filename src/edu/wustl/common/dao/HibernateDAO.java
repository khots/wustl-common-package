
package edu.wustl.common.dao;

import java.util.Collection;

import org.hibernate.HibernateException;

import edu.wustl.common.util.dbmanager.DAOException;

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
	Object loadCleanObj(String sourceObjectName, Long identifier) throws DAOException;

	/**
	 * Add AuditEvent Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	void addAuditEventLogs(Collection<Object> auditEventDetailsCollection);
	
	Object loadCleanObj(Class<Object> objectClass, Long identifier) throws HibernateException;

}
