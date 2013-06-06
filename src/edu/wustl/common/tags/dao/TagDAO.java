/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.tags.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.tags.domain.TagItem;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class TagDAO<T> extends DefaultBizLogic
{
	private static final Logger LOGGER = Logger.getCommonLogger(TagDAO.class);

	private static final String GET_TAGS = "SELECT DISTINCT tag "
										+ "FROM %s tag LEFT JOIN  tag.sharedUserIds sharedIds "
					    				+ "WHERE tag.userId =:userId OR sharedIds =:shareUserId";
	
	private static final String DELETE_TAGITEMS = "DELETE FROM %s tagItem "
			 									+ "WHERE tagItem.tag.identifier = :tagId AND " 
			 									+ "tagItem.objId IN (:objIds)";
			 									 
	
	private static final String IS_TAG_ITEM_ASSIGNED = "SELECT tagItem.objId FROM %s tagItem "
											    + "WHERE tagItem.tag.identifier = :tagId AND " 
											    + "tagItem.objId = :objId";
	
	private String entityName;
	private DAO dao = null;
	
	public TagDAO()
	{
	}

	public TagDAO(String entityName) throws BizLogicException
	{
		try 
		{
		this.entityName = entityName;
			SessionDataBean sessionDataBean = new SessionDataBean();
			dao = getHibernateDao(getAppName(), sessionDataBean);
		} 
		catch (DAOException e) {
			LOGGER.error("Error while creating TagDAO",e);
			throw new BizLogicException(e);
	}
	}
	
	public void commit() throws DAOException
	{
		dao.commit();
	}
	
	public void closeSession() throws BizLogicException{
		closeSession(dao);
	}
	/**
	 * Insert the Tag Object to the database.
	 * @param obj Tag Object to be inserted in database
	 * @throws DAOException,BizLogicException.
	 */
	public void insertTag(Tag<T> tag) throws DAOException, BizLogicException
	{
		try
		{
			dao.insert(entityName, tag);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		}
	/**
	 * Delete the Tag Object from the database.
	 * @param obj Tag Object to be deleted in database
	 * @throws DAOException,BizLogicException.
	 */
	public void deleteTag(Tag<T> tag) throws BizLogicException
	{
		try
		{
			dao.delete(entityName, tag);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		}
	/**
	 * Insert the TagItem Object to the database.
	 * @param obj TagItem Object to be inserted in database.
	 * @throws DAOException,BizLogicException.
	 */
	public void insertTagItem(TagItem<T> tagItem) throws DAOException, BizLogicException
	{
		try
		{
			dao.insert(entityName, tagItem);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		}
	 
	/**
	 * Delete the TagItem Object from the database.
	 * @param obj TagItem Object to be deleted in database.
	 * @throws DAOException,BizLogicException.
	 */
	public void deleteTagItem(TagItem<T> tagItem) throws BizLogicException
	{
		try
		{
			dao.delete(entityName, tagItem);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
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
		Tag tag = null;
		try
		{
			tag = (Tag) dao.retrieveByIdAndEntityName(entityName, tagId);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
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
		try
		{
			Tag tag = (Tag) dao.retrieveByIdAndEntityName(entityName, tagId);
			tagItem = tag.getTagItem();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
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
		TagItem tagItem = null;
		try
		{
			tagItem = (TagItem) dao.retrieveByIdAndEntityName(entityName, itemId);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		return tagItem;
	}
	/**
	 * Get list of tags from the database.
	 * @param entityName from hbm file.
	 * @param obj Object to be inserted in database
	 * @throws DAOException,BizLogicException. 
	 */
	public List<Tag<T>> getTags(SessionDataBean sessionBean) throws DAOException, BizLogicException
	{
		List<Tag<T>> tagList = null;  
		try
		{
			String query = String.format(GET_TAGS,entityName);
			List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>();
			parameters.add(new ColumnValueBean("userId", sessionBean.getUserId()));
			parameters.add(new ColumnValueBean("shareUserId", Long.parseLong(sessionBean.getCsmUserId())));
			tagList = ((HibernateDAO)dao).executeParamHQL(query, parameters); 
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		return tagList;
	}
	
	
	/**
	 * delete tagItems from the database.
	 * @param  List<Long> tagItemIds
	 * @throws DAOException,BizLogicException. 
	 */
	public void deleteTagItemsFromObjIds(List<Long> objIds, Long tagId) throws DAOException, BizLogicException
	{
		try 
		{
			String query = String.format(DELETE_TAGITEMS, entityName); 
			List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>(); 
			parameters.add(new ColumnValueBean("tagId", tagId)); 
			parameters.add(new ColumnValueBean("objIds", objIds));
			((HibernateDAO)dao).executeUpdateHQL(query, parameters);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		}
	
	/**
	 * Update the Tag Object to the database.
	 * @param obj Tag Object to be updated in database
	 * @throws DAOException,BizLogicException.
	 * @throws BizLogicException 
	 * 
	 */
	public void updateTag(Tag<T> tag) throws DAOException, BizLogicException
	{
		try
		{
			dao.update(entityName, tag);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
	}
	
	/**
	 * .
	 * @param tagId.
	 * @return Set<TagItem>.
	 * @throws DAOException,BizLogicException.
	 */
	public boolean isTagItemAlreadyAssigned(Long tagId, Long objId) throws BizLogicException
	{
		boolean isObjPresent = false;
		List resultList = null;  
		try
		{
			String query = String.format(IS_TAG_ITEM_ASSIGNED, entityName); 
			List<ColumnValueBean> parameters = new ArrayList<ColumnValueBean>(); 
			parameters.add(new ColumnValueBean("tagId", tagId)); 
			parameters.add(new ColumnValueBean("objId", objId));
			resultList = ((HibernateDAO)dao).executeParamHQL(query, parameters); 
			if(! resultList.isEmpty()){
				isObjPresent = true;
			}
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		return isObjPresent;
	}
}
