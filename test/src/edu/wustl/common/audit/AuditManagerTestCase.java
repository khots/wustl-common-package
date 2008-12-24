package edu.wustl.common.audit;

import org.hibernate.cfg.Configuration;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.util.HibernateMetaData;


public class AuditManagerTestCase extends CommonBaseTestCase
{
	/**
	 * Generic Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AuditManagerTestCase.class);

	static {
        Configuration config = new Configuration().
            setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect").
            setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver").
            setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:baseball").
            setProperty("hibernate.connection.username", "sa").
            setProperty("hibernate.connection.password", "").
            setProperty("hibernate.connection.pool_size", "1").
            setProperty("hibernate.connection.autocommit", "true").
            setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider").
            setProperty("hibernate.hbm2ddl.auto", "create-drop").
            setProperty("hibernate.show_sql", "true").
            addClass(edu.wustl.common.audit.AuditableImpl.class);
            /*addClass(BattingStint.class).
            addClass(FieldingStint.class).
            addClass(PitchingStint.class);*/
        	HibernateMetaData.initHibernateMetaData(config);
    }
	public void testCompare()
	{
		Auditable auditable1=new AuditableImpl(Long.valueOf(1));
		Auditable auditable2=new AuditableImpl(Long.valueOf(2));
		AuditManager auditManager= new AuditManager();
		auditManager.setIpAddress("127.0.0.1");
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.compare(auditable1, auditable2,"eventType");
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
		auditManager.setIpAddress("127.0.0.1");
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.compare(currentObj, previousObj,"eventType");
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
		auditManager.setIpAddress("127.0.0.1");
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.compare(currentObj, previousObj,"eventType");
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
		auditManager.setIpAddress("127.0.0.1");
		auditManager.setUserId(Long.valueOf(100));
		try
		{
			auditManager.compare(null, previousObj,"eventType");
			assertTrue("Auditable compared successfully.", true);
		}
		catch(Exception exception)
		{
			fail("Not able to compare.");
			logger.debug("Not able to compare.", exception);
		}
	}
}
