/**
 * <p>Title: CDEBizLogic Class>
 * <p>Description:	This is biz Logic class for the CDEs.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 26, 2005
 */

package edu.wustl.common.bizlogic;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDE;
import edu.wustl.common.cde.CDEImpl;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.cde.PermissibleValue;
import edu.wustl.common.cde.PermissibleValueImpl;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.tree.CDETreeNode;
import edu.wustl.common.tree.TreeDataInterface;
import edu.wustl.common.tree.TreeNode;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

/**
 * This is biz Logic class for the CDEs.
 * @author gautam_shetty
 */
/**
 * @author poornima_govindrao
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author poornima_govindrao
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CDEBizLogic extends DefaultBizLogic implements TreeDataInterface
{

	/**
	* LOGGER Logger - Generic LOGGER.
	*/
	private static final Logger LOGGER = Logger.getCommonLogger(CDEBizLogic.class);
	/**
	 * Saves the storageType object in the database.
	 * @param obj The storageType object to be saved.
	 * @param dao The dao object.
	 * @param sessionDataBean The session specific data.
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		try
		{
			CDEImpl cde = (CDEImpl) obj;
			//Delete the previous CDE data from the database.
			delete(cde, dao);
			//Insert the new CDE data in teh database.
			dao.insert(cde, false);
			Iterator iterator = cde.getPermissibleValues().iterator();
			while (iterator.hasNext())
			{
				PermissibleValueImpl permissibleValue = (PermissibleValueImpl) iterator.next();
				dao.insert(permissibleValue, false);
			}
		}
		catch(DAOException exception)
		{
			LOGGER.debug(exception.getMessage(), exception);
			throw new BizLogicException(exception);
		}
	}

	/**
	 * Deletes the CDE and the corresponding permissible values from the database.
	 * @param obj the CDE to be deleted.
	 * @param dao the DAO object.
	 * @throws BizLogicException Generic BizLogic Exception
	 */
	protected void delete(Object obj, DAO dao) throws BizLogicException
	{
		try
		{
			CDE cde = (CDE) obj;
			List list = dao.retrieve(CDEImpl.class.getName(), "publicId", cde.getPublicId());
			if (!list.isEmpty())
			{
				CDEImpl cde1 = (CDEImpl) list.get(0);
				dao.delete(cde1);
			}
		}
		catch(DAOException exception)
		{
			LOGGER.debug(exception.getMessage(), exception);
			throw new BizLogicException(exception);
		}
	}

	/**
	 * (non-Javadoc).
	 * @see edu.wustl.catissuecore.bizlogic.TreeDataInterface#getTreeViewData()
	 * @throws DAOException generic DAOException
	 * @return list
	 */
	public List getTreeViewData() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method gets tree view data.
	 * @param cdeName cde Name
	 * @return list
	 * @throws DAOException generic DAOException
	 */
	public List getTreeViewData(String cdeName) throws DAOException
	{
		String cdeDecodedName = URLDecoder.decode(cdeName);
		CDE cde = CDEManager.getCDEManager().getCDE(cdeDecodedName);
		CDETreeNode root = new CDETreeNode();
		root.setCdeName(cdeDecodedName);
		List list = getTreeNodeList(root, cde.getPermissibleValues());

		return list;
	}

	/**
	 * This method gets Tree Node List.
	 * @param parentTreeNode parent Tree Node
	 * @param permissibleValueSet Set of permissible Value
	 * @return treeNodeList.
	 */
	private List getTreeNodeList(TreeNode parentTreeNode, Set permissibleValueSet)
	{
		List treeNodeList = new ArrayList();
		if (permissibleValueSet != null)
		{
			Iterator iterator = permissibleValueSet.iterator();
			while (iterator.hasNext())
			{
				PermissibleValueImpl permissibleValueImpl = (PermissibleValueImpl) iterator.next();
				CDETreeNode treeNode = new CDETreeNode(permissibleValueImpl.getIdentifier(),
						permissibleValueImpl.getValue());
				treeNode.setParentNode(parentTreeNode);
				treeNode.setCdeName(((CDETreeNode) parentTreeNode).getCdeName());
				List subPermissibleValues = getTreeNodeList(treeNode, permissibleValueImpl
						.getSubPermissibleValues());
				if (subPermissibleValues != null && !subPermissibleValues.isEmpty())
				{
					//Bug-2717: For sorting
					Collections.sort(subPermissibleValues);
					treeNode.setChildNodes(subPermissibleValues);
				}

				treeNodeList.add(treeNode);
				//Bug-2717: For sorting
				Collections.sort(treeNodeList);
			}
		}

		return treeNodeList;
	}

	/**
	 * Returns the CDE values without category names and only sub-categories.
	 * Poornima:Refer to bug 1718
	 * @param permissibleValueSet - Set of permissible values
	 * @param permissibleValueList - Filtered CDEs
	 */
	public void getFilteredCDE(Set permissibleValueSet, List permissibleValueList)
	{
		Iterator iterator = permissibleValueSet.iterator();
		while (iterator.hasNext())
		{
			PermissibleValue permissibleValue = (PermissibleValue) iterator.next();
			Set subPermissibleValues = permissibleValue.getSubPermissibleValues();
			//if there are no sub-permissible values, add to the list
			if (subPermissibleValues == null || subPermissibleValues.isEmpty())
			{
				permissibleValueList.add(new NameValueBean(permissibleValue.getValue(),
						permissibleValue.getValue()));
			}
			//else call the method for its children
			else
			{
				getFilteredCDE(subPermissibleValues, permissibleValueList);
			}
		}
		//Bug-2717: For sorting
		Collections.sort(permissibleValueList);
	}

	/**
	 * (non-Javadoc).
	 * @see edu.wustl.catissuecore.bizlogic.TreeDataInterface
	 * #getTreeViewData(edu.wustl.common.beans.SessionDataBean, java.util.Map)
	 * @param sessionData session specific Data
	 * @param map Map data structure.
	 * @param list List data structure.
	 * @throws DAOException generic DAOException.
	 * @return list
	 */
	public List getTreeViewData(SessionDataBean sessionData, Map map, List list)
			throws DAOException
	{

		return null;
	}
}