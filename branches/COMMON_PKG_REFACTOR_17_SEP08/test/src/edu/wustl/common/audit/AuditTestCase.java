package edu.wustl.common.audit;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import edu.wustl.common.CommonBaseTestCase;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.testdomain.Address;
import edu.wustl.common.testdomain.Order;
import edu.wustl.common.testdomain.Person;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * Testing the audit Manager class.
 * @author shrishail_kalshetty
 *
 */
public class AuditTestCase extends CommonBaseTestCase
{
	/**
	 * DAO configFactory instance.
	 */
	protected DAOConfigFactory daoConfigFactory;
	/**
	 * Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AuditTestCase.class);
	/**
	 * Constant Numeric value 1.
	 */
	private static final long ID_ONE = 1;
	/**
	 * DAO instance.
	 */
	private DAO dao;
	{
		setDAO();
	}
	/**
	 * This method will be called to set the Default DAO.
	 */
	public void setDAO()
	{
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("commonpackagetest");
		try
		{
			dao = daoFactory.getDAO();
		}
		catch (DAOException e)
		{
			logger.fatal("Problem while retrieving DAO object", e);
		}
	}
	/**
	 * This method inserts the Person object.
	 */
	@Test
	public void testInsertPerson()
	{
		try
		{
			AuditManager.init();
			setDAO() ;
			final AuditManager auditManager = getAuditManager("commonpackagetest") ;
			dao.openSession(null);
			Person person = getPerson("Kalpana");
			Address address = getAddress("Street unknown");
			dao.insert(address);
			person.setAddress(address);
			Order order1 = getOrder(person);
			Order order2 = getOrder(person);
			person.setOrderCollection(getOrderCollection(order1, order2));
			dao.insert(person);
			auditManager.insertAudit(dao, person);
			dao.commit();

		}
		catch(Exception exp)
		{
			ApplicationException appExp = (ApplicationException)exp;
			logger.fatal(appExp.getLogMessage());
			assertFalse("Failed while Insering object :", true);
		}
		finally
		{
			closeSession(dao) ;
		}
	}

	@Test
	public void testAuditUpdate()
	{

		try
		{
			AuditManager.init();
			setDAO() ;
			final AuditManager auditManager = getAuditManager("commonpackagetest") ;
			dao.openSession(null);
			/**
			 * Update the Person object.
			 */
			Person person = getPerson("Shrishail");
			person.setIdentifier(Long.valueOf(ID_ONE));
			Address address = getAddress("Sinhgad Road");
			dao.insert(address);
			person.setAddress(address);
			Order order1 = getOrder(person);
			order1.setIdentifier(Long.valueOf(ID_ONE));
			Order order2 = getOrder(person);
			person.setOrderCollection(getOrderCollection(order1, order2));
			//update the dao object
			dao.update(person);
			//Insert the audit information.
			auditManager.updateAudit(dao, person,person);
			dao.commit();
		}
		catch(Exception exception)
		{
			ApplicationException appExp = (ApplicationException)exception;
			logger.fatal(appExp.getLogMessage());
			assertFalse("Failed while Updating object :", true);
		}
		finally
		{
			closeSession(dao);
		}


	}

	/**
	 * This method updates the object and inserts the update details into audit.
	 * @param oldPerson The person object to update.
	 * @param oldAddress The Address object to update.
	 *//*
	@Test
	public void testUpdateAudit(Person oldPerson, Address oldAddress)
	{
		try
		{
			setDAO() ;
			final AuditManager auditManager = getAuditManager("commonpackagetest") ;
			dao.openSession(null);
			*//**
			 * Update the Person object.
			 *//*
			Person person = getPerson("Shrishail");
			person.setIdentifier(Long.valueOf(ID_ONE));
			Address address = getAddress("Sinhgad Road");
			dao.insert(address);
			person.setAddress(address);
			Order order1 = getOrder(person);
			order1.setIdentifier(Long.valueOf(ID_ONE));
			Order order2 = getOrder(person);
			person.setOrderCollection(getOrderCollection(order1, order2));
			//update the dao object
			dao.update(person);
			//Insert the audit information.
			auditManager.updateAudit(dao, person,oldPerson);
			dao.commit();
		}
		catch(Exception exception)
		{
			ApplicationException appExp = (ApplicationException)exception;
			logger.fatal(appExp.getLogMessage());
			assertFalse("Failed while Updating object :", true);
		}
		finally
		{
			closeSession(dao);
		}
	}*/
	/**
	 * Set the values of the Person Object.
	 * @param name Name of the Person.
	 * @return Person object.
	 */
	public Person getPerson(String name)
	{
		Person person = new Person();
		person.setName(name);
		return person ;
	}
	/**
	 * Set the values of the Address Object.
	 * @param street Street name.
	 * @return Address object.
	 */
	public Address getAddress(String street)
	{
		Address address = new Address();
		address.setStreet(street);
		return address;
	}
	/**
	 * Set the values of the Order Object.
	 * @param person set the order to this person object.
	 * @return Order object.
	 */
	public Order getOrder(Person person)
	{
		Order order = new Order() ;
		order.setPerson(person);
		return order ;
	}
	/**
	 * Set the order collection to Person object.
	 * @param order1 Order number one.
	 * @param order2 Order number two.
	 * @return Collection of order objects.
	 */
	public Collection<Object> getOrderCollection(Order order1,Order order2)
	{
		Collection<Object> orderColl = new HashSet<Object>();
		orderColl.add(order1);
		if(order2 != null)
		{
			orderColl.add(order2);
		}
		return orderColl ;
	}
	/**
	 * Get the AuditManager object.
	 * @param appName Application name.
	 * @return auditManager object.
	 */
	public AuditManager getAuditManager(String appName)
	{
		DefaultBizLogic logic = new DefaultBizLogic() ;
		final AuditManager auditManager = logic.getAuditManager(null) ;
		auditManager.setApplicationName(appName) ;
		return auditManager;
	}
	/**
	 * Close the dao Session.
	 * @param dao DAO object.
	 */
	public void closeSession(DAO dao)
	{
		try
		{
			dao.closeSession();
		}
		catch (DAOException exception)
		{
			ApplicationException appExp = (ApplicationException)exception;
			logger.fatal(appExp.getLogMessage());
		}
	}
}
