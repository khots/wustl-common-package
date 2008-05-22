package edu.wustl.common.util.dbManager;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import org.hibernate.CallbackException;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.type.Type;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;




public class InterceptorTest implements Interceptor{
	
	public static final Interceptor INSTANCE = new InterceptorTest();
	
	protected InterceptorTest() {
	
		
		try {
			System.out.println("client instance1");
		
		} catch (Exception e) {
			System.out.println("Client Session Starting Exception");
			e.printStackTrace();
		}

	}

	public void onDelete(
			Object entity, 
			Serializable id, 
			Object[] state, 
			String[] propertyNames, 
			Type[] types) {System.out.println("delete");}

	public boolean onFlushDirty(
			Object entity, 
			Serializable id, 
			Object[] currentState, 
			Object[] previousState, 
			String[] propertyNames, 
			Type[] types) { System.out.println("flush method");
		return false;
	}

	public boolean onLoad(
			Object entity, 
			Serializable id, 
			Object[] state, 
			String[] propertyNames, 
			Type[] types) 
	{
		
		System.out.println("****on Load " + entity.getClass().getName() );

		return true;
	}

	public boolean onSave(Object entity,Serializable id,Object[] state,
			String[] propertyNames, Type[] types) 
	{
	Long enId = ((AbstractDomainObject)entity).getId();
		System.out.println("****on save " + entity.getClass().getName() +" id :" + id +" Entity Id:"+enId );
	
		return true;
	}

	public void postFlush(Iterator entities) {
		System.out.println("postFlush");
	}
	public void preFlush(Iterator entities) {
		System.out.println("preFlush");
	}

	public Boolean isTransient(Object entity) {
		System.out.println("isTransient");
		List<?> resultList1 = null;
		
		Session session=null;
		AbstractDomainObject abstractDomainObject=(AbstractDomainObject)entity;
		try {
			try {
				session = DBUtil.getCleanSession();
			} catch (BizLogicException e) {
				
				e.printStackTrace();
			}
			
			resultList1 = session.createQuery("select id from  "+entity.getClass().getName() +" where id = "+abstractDomainObject.getId()).list();
				session.close();
			
    	
		}
		catch(HibernateException e){
			System.out.println("in read");
			e.printStackTrace();
		}
	
		boolean retVal=true;
		if(resultList1 != null && !resultList1.isEmpty())
		{
			retVal = false;
		
			System.out.println("*****Inside Interceptor: entity name := " + entity.getClass().getName() +", ID :=" 
					+ abstractDomainObject.getId() + " Transient? :=" + String.valueOf(retVal) );
			
			return retVal;
		}
		
		System.out.println("*****Inside Interceptor: entity name := " + entity.getClass().getName() +", ID :=" 
				+ abstractDomainObject.getId() + " Transient? :=" + String.valueOf(retVal) );
		return retVal;
	}

	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) {
		System.out.println("instantiate");
		return null;
	}

	public int[] findDirty(Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) {
		System.out.println("");
		return null;
	}

	public String getEntityName(Object object) {
		System.out.println("getEntityName");
		return null;
	}

	public Object getEntity(String entityName, Serializable id) {
		System.out.println("getEntity");
		return null;
	}

	public void afterTransactionBegin(Transaction tx) {
		System.out.println("");
	}
	public void afterTransactionCompletion(Transaction tx) {
		System.out.println("afterTransactionBegin");
	}
	public void beforeTransactionCompletion(Transaction tx) {
		System.out.println("beforeTransactionCompletion");
	}

	public String onPrepareStatement(String sql) {
		System.out.println("onPrepareStatement " + sql);
		return sql;
	}

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
		System.out.println("onCollectionRemove");
	}

	public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
		System.out.println("onCollectionRecreate");
	}

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		System.out.println("onCollectionUpdate");
	}
	
}