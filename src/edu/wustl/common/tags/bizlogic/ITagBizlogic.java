
package edu.wustl.common.tags.bizlogic;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.tags.domain.TagItem;
import edu.wustl.dao.exception.DAOException;

import java.util.List;
import java.util.Set;

import org.json.JSONObject;

public interface ITagBizlogic
{ 

	/**
	 * Assign the TagItems to existing folder.
	 * @param entityName from hbm file.
	 * @param tagId.
	 * @param objId.
	 * @throws DAOException,BizLogicException.
	 */
	public void assignTag(String entityName, long tagId, long objId) throws DAOException,
			BizLogicException;
	
	
	/**
	 * share existing folder to users List.
	 * @param entityName from hbm file.
	 * @param tagIdList.
	 * @param selectedUserList.
	 * @throws DAOException,BizLogicException.
	 */
	public void shareTags(String entityName, Set<Long> tagIdSet, Set<Long> selectedUsers) throws DAOException,
			BizLogicException;

	/**
	 * Insert New Tag to the database.
	 * @param entityName from hbm file.
	 * @param label for new Tag.
	 * @param userId.
	 * @return tag identifier.
	 * @throws DAOException,BizLogicException.
	 */
	public long createNewTag(String entityName, String newTagName, long userId)
			throws DAOException, BizLogicException;
	
	/**
	 * Get list of Tags from the database.
	 * @param entityName from hbm file.
	 * @param obj Object to be inserted in database
	 * @throws DAOException,BizLogicException.
	 */
	public List<Tag> getTagList(String entityName, long userId) throws DAOException, BizLogicException;
	/**
	 * Get the Set of TagItems.
	 * @param entityName from hbm file.
	 * @param tagId.
	 * @return Set<TagItem>.
	 * @throws DAOException,BizLogicException.
	 */
	public Set<TagItem> getTagItemByTagId(String entityName, long tagId) throws BizLogicException;
	/**
	 * Get Tag object.
	 * @param entityName from hbm file.
	 * @param  tagId.
	 * @return Tag Object.
	 * @throws DAOException,BizLogicException.
	 */
	public Tag getTagById(String entityName, long tagId) throws DAOException, BizLogicException;

	/**
	 * Delete the Tag  from database.
	 * @param entityName from hbm file.
	 * @param tagId to retrieve TagItem Object and delete it from database.
	 * @throws DAOException,BizLogicException.
	 */
	public void deleteTag(String entityName, long tagId) throws DAOException, BizLogicException;

	/**
	 * Delete the Tag Item from database.
	 * @param entityName from hbm file.
	 * @param objId to retrieve TagItem Object and delete it from database.
	 * @throws DAOException,BizLogicException.
	 */
	public void deleteTagItem(String entityName, long tagItemId) throws DAOException,
			BizLogicException;
	/**
	 * Get the Tag Item from database for Tree Grid.
	 * @param entityName from hbm file.
	 * @param  tagId.
	 * @return Json Object.
	 * @throws DAOException,BizLogicException.
	 */
	public JSONObject getJSONObj(String entityTag,long tagId)throws DAOException, BizLogicException;
}
