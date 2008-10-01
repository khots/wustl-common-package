/*
 * Created on Oct 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.common.security.dao;

import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.security.authorization.ObjectPrivilegeMap;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import gov.nih.nci.security.util.StringUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @author aarti_sharma
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AuthorizationDAOImpl extends gov.nih.nci.security.dao.AuthorizationDAOImpl
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AuthorizationDAOImpl.class);

	private SessionFactory sf = null;

	/**
	 * @param sf
	 * @param applicationContextName
	 * @throws CSConfigurationException
	 */
	public AuthorizationDAOImpl(SessionFactory sf, String applicationContextName)
			throws CSConfigurationException
	{
		super(sf, applicationContextName);
		this.sf = sf;

	}

	public Collection getPrivilegeMap(String userName, Collection pEs) throws CSException
	{
		ArrayList result = new ArrayList();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		Session s = null;
		Connection cn = null;

		if (StringUtilities.isBlank(userName))
		{
			throw new CSException("userName can't be null!");
		}
		if (pEs == null)
		{
			throw new CSException("protection elements collection can't be null!");
		}
		if (pEs.size() == 0)
		{
			return result;
		}

		try
		{
			s = sf.openSession();
			cn = s.connection();
			StringBuffer stbr = new StringBuffer();
			String attributeVal = "=?";
			generateQuery(stbr,attributeVal);
			
			StringBuffer stbr2 = new StringBuffer();
			attributeVal = " IS NULL";
			generateQuery(stbr2,attributeVal);
			
			String sql = stbr.toString();
			logger.debug("SQL:" + sql);
			pstmt = cn.prepareStatement(sql);

			String sql2 = stbr2.toString();
			pstmt2 = cn.prepareStatement(sql2);
			Iterator it = pEs.iterator();
			while (it.hasNext())
			{
				ProtectionElement pe = (ProtectionElement) it.next();
				ArrayList privs = new ArrayList();
				if (pe.getObjectId() != null)
				{
					if (pe.getAttribute() != null)
					{
						pstmt.setString(1, pe.getObjectId());
						pstmt.setString(2, pe.getAttribute());
						pstmt.setString(3, userName);
						rs = pstmt.executeQuery();
					}
					else
					{
						pstmt2.setString(1, pe.getObjectId());
						pstmt2.setString(2, userName);
						rs = pstmt2.executeQuery();
					}
				}
				while (rs.next())
				{
					String priv = rs.getString(1);
					Privilege p = new Privilege();
					p.setName(priv);
					privs.add(p);
				}
				rs.close();
				ObjectPrivilegeMap opm = new ObjectPrivilegeMap(pe, privs);
				result.add(opm);
			}
			pstmt.close();
		}
		catch (Exception ex)
		{
			if (logger.isDebugEnabled())
				logger.debug("Failed to get privileges for " + userName + "|" + ex.getMessage());
			throw new CSException("Failed to get privileges for " + userName + "|"
					+ ex.getMessage(), ex);
		}
		finally
		{
			try
			{

				s.close();
				rs.close();
				pstmt.close();
			}
			catch (Exception ex2)
			{
				if (logger.isDebugEnabled())
					logger.debug("Authorization|||getPrivilegeMap|Failure|Error in Closing Session |"
							+ ex2.getMessage());
			}
		}
		return result;
	}

	//changes to load the object and then delete it
	//else it throws exception
	public void removeProtectionElementsFromProtectionGroup(String protectionGroupId,
			String[] protectionElementIds) throws CSTransactionException
	{
		Session session = null;
		Transaction tx = null;

		try
		{
			session = sf.openSession();
			tx = session.beginTransaction();

			ProtectionGroup protectionGroup = (ProtectionGroup) this.getObjectByPrimaryKey(session,
					ProtectionGroup.class, Long.valueOf(protectionGroupId));

			for (int i = 0; i < protectionElementIds.length; i++)
			{
				StringBuffer query = new StringBuffer();
				query.append("from gov.nih.nci.security.dao.hibernate.ProtectionGroupProtectionElement protectionGroupProtectionElement");
				query.append(" where protectionGroupProtectionElement.protectionElement.protectionElementId=");
				query.append(protectionElementIds[i]);
				query.append(" and protectionGroupProtectionElement.protectionGroup.protectionGroupId=");
				query.append(protectionGroupId);
				Query queryObj = session.createQuery(query.toString());
				List list = queryObj.list();
				if (list != null && list.size() > 0)
				{
					this.removeObject(list.get(0));
				}
			}
			tx.commit();
		}
		catch (Exception ex)
		{
			tx.rollback();
			StringBuffer mess= new StringBuffer("Error Occured in deassigning Protection Elements ")
					.append(StringUtilities.stringArrayToString(protectionElementIds))
					.append(" to Protection Group").append( protectionGroupId);
			logger.error(mess + ex.getMessage(),ex);
			throw new CSTransactionException(mess+ ex.getMessage(), ex);
		}
		finally
		{
				session.close();
		}
	}

	public Set getGroups(String userId) throws CSObjectNotFoundException
	{
		Session session = null;
		Set groups = new HashSet();
		try
		{
			session = HibernateSessionFactoryHelper.getAuditSession(sf);

			User user = (User) this.getObjectByPrimaryKey(session, User.class, Long.valueOf(userId));
			groups = user.getGroups();
			List list = new ArrayList();
			Iterator toSortIterator = groups.iterator();
			while (toSortIterator.hasNext())
			{
				list.add(toSortIterator.next());
			}
			Collections.sort(list);
			groups.clear();
			groups.addAll(list);
		}
		catch (Exception ex)
		{
			String mess="An error occurred while obtaining Associated Groups for the User:"+userId;
			logger.error(mess,ex);
			throw new CSObjectNotFoundException(mess+ ex.getMessage(), ex);
		}
		finally
		{
			try
			{
				session.close();
			}
			catch (Exception ex2)
			{
				logger.debug("Error in Closing Session"	+ ex2.getMessage());
			}
		}
		return groups;

	}

	private Object getObjectByPrimaryKey(Session s, Class objectType, Long primaryKey)
			throws HibernateException, CSObjectNotFoundException
	{

		if (primaryKey == null)
		{
			throw new CSObjectNotFoundException("The primary key can't be null");
		}
		Object obj = s.load(objectType, primaryKey);

		if (obj == null)
		{
			logger.debug("Authorization|||getObjectByPrimaryKey|Failure|Not found object of type "
					+ objectType.getName() + "|");
			throw new CSObjectNotFoundException(objectType.getName() + " not found");
		}
		logger
				.debug("Authorization|||getObjectByPrimaryKey|Success|Success in retrieving object of type "
						+ objectType.getName() + "|");
		return obj;
	}
	
	private void generateQuery(StringBuffer stbr,String attributeVal)
	{
		stbr.append("select distinct(p.privilege_name)");
		stbr.append(" from csm_protection_group pg,");
		stbr.append(" csm_protection_element pe,");
		stbr.append(" csm_pg_pe pgpe,");
		stbr.append(" csm_user_group_role_pg ugrpg,");
		stbr.append(" csm_user u,");
		stbr.append(" csm_group g,");
		stbr.append(" csm_user_group ug,");
		stbr.append(" csm_role_privilege rp,");
		stbr.append(" csm_privilege p ");
		stbr.append(" where pgpe.protection_group_id = pg.protection_group_id");
		stbr.append(" and pgpe.protection_element_id = pe.protection_element_id");
		stbr.append(" and pe.object_id= ?");

		stbr.append(" and pe.attribute "+attributeVal);
		stbr.append(" and pg.protection_group_id = ugrpg.protection_group_id ");
		stbr.append(" and (( ugrpg.group_id = g.group_id");
		stbr.append(" and ug.group_id= g.group_id");
		stbr.append("       and ug.user_id = u.user_id)");
		stbr.append("       or ");
		stbr.append("     (ugrpg.user_id = u.user_id))");
		stbr.append(" and u.login_name=?");
		stbr.append(" and ugrpg.role_id = rp.role_id ");
		stbr.append(" and rp.privilege_id = p.privilege_id");
	}

}