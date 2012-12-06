
package edu.wustl.common.tags.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.tags.domain.TagItem;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

public class TagDAO<T> extends DefaultBizLogic
{

	public Session session = null;
	public String entityName;
	public Long userId;

	public TagDAO()
	{
	}

	public TagDAO(String entityName,long userId)
	{
		this.entityName = entityName;
		this.userId = userId;
	}
	/**
	 * Insert the Tag Object to the database.
	 * @param obj Tag Object to be inserted in database
	 * @throws DAOException,BizLogicException.
	 */
	public void insertTag(Tag<T> tag) throws DAOException, BizLogicException
	{
		DAO dao = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
			dao.insert(entityName, tag);
			dao.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}

	}
	/**
	 * Delete the Tag Object from the database.
	 * @param obj Tag Object to be deleted in database
	 * @throws DAOException,BizLogicException.
	 */
	public void deleteTag(Tag<T> tag) throws BizLogicException
	{
		DAO dao = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
			dao.delete(entityName, tag);
			dao.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}
	}
	/**
	 * Insert the TagItem Object to the database.
	 * @param obj TagItem Object to be inserted in database.
	 * @throws DAOException,BizLogicException.
	 */
	public void insertTagItem(TagItem<T> tagItem) throws DAOException, BizLogicException
	{
		DAO dao = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
			dao.insert(entityName, tagItem);
			dao.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}

	}
	/**
	 * Delete the TagItem Object from the database.
	 * @param obj TagItem Object to be deleted in database.
	 * @throws DAOException,BizLogicException.
	 */
	public void deleteTagItem(TagItem<T> tagItem) throws BizLogicException
	{
		DAO dao = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
			dao.delete(entityName, tagItem);
			dao.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}

	}
	public Tag<T> getTagByLabel(String label)
	{
		return null;
	}

	public TagItem<T> getTagItemByTagLabel(String label)
	{
		return null;
	}
	/**
	 * Get Tag object.
	 * @param entityName from hbm file.
	 * @param  tagId.
	 * @return Tag Object.
	 * @throws DAOException,BizLogicException.
	 */
	public Tag<T> getTagById(long tagId) throws DAOException, BizLogicException
	{
		DAO dao = null;
		Tag tag = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
			tag = (Tag) dao.retrieveByIdAndEntityName(entityName, tagId);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}
		return tag;
	}
	/**
	 * Get the Set of TagItems.
	 * @param tagId.
	 * @return Set<TagItem>.
	 * @throws DAOException,BizLogicException.
	 */
	public Set<TagItem> getTagItemBytagId(long tagId) throws BizLogicException
	{
		Set<TagItem> tagItem = null;
		DAO dao = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
			Tag tag = (Tag) dao.retrieveByIdAndEntityName(entityName, tagId);
			tagItem = tag.getTagItem();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}
		return tagItem;
	}
	/**
	 * Get TagItem object.
	 * @param entityName from hbm file.
	 * @param  tagItemId.
	 * @return TagItem Object.
	 * @throws DAOException,BizLogicException.
	 */
	public TagItem getTagItemById(long itemId) throws DAOException, BizLogicException
	{
		DAO dao = null;
		TagItem tagItem = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
		    dao = getHibernateDao(getAppName(), sessionDataBean);
			tagItem = (TagItem) dao.retrieveByIdAndEntityName(entityName, itemId);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}
		return tagItem;
	}
	/**
	 * Get list of tags from the database.
	 * @param entityName from hbm file.
	 * @param obj Object to be inserted in database
	 * @throws DAOException,BizLogicException.
	 */
	public List<Tag<T>> getTags() throws BizLogicException
	{

		List<Tag<T>> tagList = null;
		DAO dao = null;
		try
		{
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
			tagList = dao.executeQuery("from  " + entityName +" where userId = "+userId);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			closeSession(dao);
		}
		return tagList;
	}

}
